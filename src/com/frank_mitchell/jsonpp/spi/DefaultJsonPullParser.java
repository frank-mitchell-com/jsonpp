/*
 * Copyright 2019 Frank Mitchell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package com.frank_mitchell.jsonpp.spi;

import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.CodePointSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A default implementation of the JsonPullParser.
 */
final class DefaultJsonPullParser implements JsonPullParser {

    private static final int EXPECT_KEY = 1;
    private static final int EXPECT_COLON = 2;
    private static final int EXPECT_VALUE = 3;
    private static final int EXPECT_COMMA_OR_CLOSE = 4;
    private static final int EXPECT_EOF = 5;

    private int _expectState = EXPECT_VALUE;

    private JsonEvent _currentEvent;
    private String _stringValue;
    private BigDecimal _numberValue;

    private final JsonLexer _lexer;

    private final Deque<ValueFrame> _objectStack = new ArrayDeque<>();

    private static class ValueFrame {

        private final boolean _inObject;
        private String _key;

        ValueFrame(boolean value) {
            _inObject = value;
        }

        boolean isInObject() {
            return _inObject;
        }

        private String getKey() {
            return _key;
        }

        private void setKey(String value) {
            _key = value;
        }
    }

    DefaultJsonPullParser(CodePointSource s) throws IOException {
        this(new DefaultJsonLexer(s));
    }

    DefaultJsonPullParser(JsonLexer x) throws IOException {
        _lexer = x;
        _currentEvent = JsonEvent.START_STREAM;
        _stringValue = null;
        setExpectStart();
    }

    @Override
    public JsonEvent getEvent() {
        return _currentEvent;
    }

    @Override
    public String getString() throws IllegalStateException {
        if (_stringValue == null) {
            throw new IllegalStateException(_currentEvent.toString());
        }
        return _stringValue;
    }

    @Override
    public BigDecimal getBigDecimal() throws IllegalStateException {
        if (_numberValue == null) {
            throw new IllegalStateException(_currentEvent.toString());
        }
        return _numberValue;
    }

    @Override
    public double getDouble() throws IllegalStateException {
        return getBigDecimal().doubleValue();
    }

    @Override
    public int getInt() throws IllegalStateException {
        return getBigDecimal().intValue();
    }

    @Override
    public void next() throws IOException {
        _currentEvent = null;
        _stringValue = null;
        _numberValue = null;

        while (_currentEvent == null) {
            _lexer.next();

            int token = _lexer.getTokenType();

            if (!isExpected(token)) {
                // TODO: error message
                _currentEvent = JsonEvent.SYNTAX_ERROR;
                break;
            }

            switch (token) {
                case JsonLexer.TOKEN_OBJ_OPEN:
                    _currentEvent = JsonEvent.START_OBJECT;
                    increaseDepth(true);
                    setExpectKey();
                    break;
                case JsonLexer.TOKEN_OBJ_CLOSE:
                    if (isInObject()) {
                        _currentEvent = JsonEvent.END_OBJECT;
                        decreaseDepth();
                        setExpectCommaOrClose();
                    } else {
                        _currentEvent = JsonEvent.SYNTAX_ERROR;
                    }
                    break;
                case JsonLexer.TOKEN_ARR_OPEN:
                    _currentEvent = JsonEvent.START_ARRAY;
                    increaseDepth(false);
                    setExpectValue();
                    break;
                case JsonLexer.TOKEN_ARR_CLOSE:
                    if (isInArray()) {
                        _currentEvent = JsonEvent.END_ARRAY;
                        decreaseDepth();
                    } else {
                        _currentEvent = JsonEvent.SYNTAX_ERROR;
                    }
                    setExpectCommaOrClose();
                    break;
                case JsonLexer.TOKEN_STRING:
                    _stringValue = unquote(_lexer.getToken());
                    if (isExpectingKey()) {
                        _currentEvent = JsonEvent.KEY_NAME;
                        setKey(_stringValue);
                        setExpectColon();
                    } else {
                        _currentEvent = JsonEvent.VALUE_STRING;
                        setExpectCommaOrClose();
                    }
                    break;
                case JsonLexer.TOKEN_NUMBER:
                    _currentEvent = JsonEvent.VALUE_NUMBER;
                    _stringValue = _lexer.getToken().toString();
                    _numberValue = new BigDecimal(_stringValue);
                    setExpectCommaOrClose();
                    break;
                case JsonLexer.TOKEN_TRUE:
                    _currentEvent = JsonEvent.VALUE_TRUE;
                    setExpectCommaOrClose();
                    break;
                case JsonLexer.TOKEN_FALSE:
                    _currentEvent = JsonEvent.VALUE_FALSE;
                    setExpectCommaOrClose();
                    break;
                case JsonLexer.TOKEN_NULL:
                    _currentEvent = JsonEvent.VALUE_NULL;
                    setExpectCommaOrClose();
                    break;
                case JsonLexer.TOKEN_EOF:
                    _currentEvent = JsonEvent.END_STREAM;
                    break;
                case JsonLexer.TOKEN_COMMA:
                    if (isRootLevel()) {
                        _currentEvent = JsonEvent.SYNTAX_ERROR;
                    } else if (isInObject()) {
                        setExpectKey();
                    } else {
                        setExpectValue();
                    }
                    break;
                case JsonLexer.TOKEN_COLON:
                    setExpectValue();
                    break;
                default:
                    // TODO: error message
                    _currentEvent = JsonEvent.SYNTAX_ERROR;
                    break;
            }
        }
    }

    @Override
    public boolean isInObject() {
        final ValueFrame frame = _objectStack.peek();
        if (frame != null) {
            return frame.isInObject();
        }
        return false;
    }

    @Override
    public boolean isInArray() {
        final ValueFrame frame = _objectStack.peek();
        if (frame != null) {
            return !frame.isInObject();
        }
        return false;
    }

    @Override
    public String getCurrentKey() {
        final ValueFrame frame = _objectStack.peek();
        if (frame != null && frame.isInObject()) {
            return frame.getKey();
        }
        return null;
    }

    void setKey(String key) {
        final ValueFrame frame = _objectStack.peek();
        if (frame != null && frame.isInObject()) {
            frame.setKey(key);
        }
        // else this is an error, right?
    }

    boolean isRootLevel() {
        return getDepth() == 0;
    }

    private int getDepth() {
        return _objectStack.size();
    }

    void decreaseDepth() {
        _objectStack.pop();
    }

    void increaseDepth(boolean isobject) {
        _objectStack.push(new ValueFrame(isobject));
    }

    private String unquote(CharSequence seq) {
        // trim off quotes
        StringBuilder sb = new StringBuilder(seq.length());
        sb.append(seq, 1, seq.length() - 1);

        // replace escape sequences with real characters
        int pos = sb.indexOf("\\");
        String s;

        while (pos >= 0) {
            char c = sb.charAt(pos + 1);
            switch (c) {
                case '\\':
                case '/':
                case '"':
                    sb.deleteCharAt(pos);
                    break;
                case 'b':
                    sb.replace(pos, pos + 2, "\b");
                    break;
                case 'f':
                    sb.replace(pos, pos + 2, "\f");
                    break;
                case 'n':
                    sb.replace(pos, pos + 2, "\n");
                    break;
                case 'r':
                    sb.replace(pos, pos + 2, "\r");
                    break;
                case 't':
                    sb.replace(pos, pos + 2, "\t");
                    break;
                case 'u':
                    s = sb.substring(pos + 2, pos + 6);
                    c = (char) Integer.parseUnsignedInt(s, 16);
                    sb.replace(pos, pos + 6, String.valueOf(c));
                    break;
            }
            pos = sb.indexOf("\\", pos + 1);
        }
        return sb.toString();
    }

    private void setExpectStart() {
        _expectState = EXPECT_VALUE;
    }

    private void setExpectKey() {
        _expectState = EXPECT_KEY;
    }

    private void setExpectValue() {
        _expectState = EXPECT_VALUE;
    }

    private void setExpectColon() {
        _expectState = EXPECT_COLON;
    }

    private void setExpectCommaOrClose() {
        if (isRootLevel()) {
            _expectState = EXPECT_EOF;
        } else {
            _expectState = EXPECT_COMMA_OR_CLOSE;
        }
    }

    private boolean isExpectingKey() {
        return _expectState == EXPECT_KEY;
    }

    private boolean isExpected(int token) {
        int state = _expectState;
        switch (token) {
            case JsonLexer.TOKEN_STRING:
                return state == EXPECT_KEY || state == EXPECT_VALUE;
            case JsonLexer.TOKEN_NUMBER:
            case JsonLexer.TOKEN_NULL:
            case JsonLexer.TOKEN_TRUE:
            case JsonLexer.TOKEN_FALSE:
            case JsonLexer.TOKEN_ARR_OPEN:
            case JsonLexer.TOKEN_OBJ_OPEN:
                return state == EXPECT_VALUE;
            case JsonLexer.TOKEN_ARR_CLOSE:
                // TODO: check that close token matches open token.
                return state == EXPECT_VALUE || state == EXPECT_COMMA_OR_CLOSE;
            case JsonLexer.TOKEN_OBJ_CLOSE:
                // TODO: check that close token matches open token.
                return state == EXPECT_KEY || state == EXPECT_COMMA_OR_CLOSE;
            case JsonLexer.TOKEN_COMMA:
                // TODO: check that close token matches open token.
                return state == EXPECT_COMMA_OR_CLOSE;
            case JsonLexer.TOKEN_COLON:
                return state == EXPECT_COLON;
            case JsonLexer.TOKEN_EOF:
                return state == EXPECT_EOF;
            default:
                return false;
        }
    }
    
    @Override
    public void close() throws IOException {
        _lexer.close();
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
