/*
 * The MIT License
 *
 * Copyright 2023 fmitchell.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.frank_mitchell.jsonpp.spi;

import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.codepoint.spi.ReaderSource;
import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPullParser;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * A faster, leaner pull parser implementation. (Theoretically.) This
 * implementation is more optimistic than the Default parser, i.e. it seeks the
 * end quote of a string before determining whether the string is well-formed.
 * This might pose problems if, for example, the I/O object hangs in the middle
 * of a string with illegal characters in it; the Default parser would fail
 * fast, but this one would hang until I/O sorted itself out. It also does not
 * support {@link #getCurrentKey()} to avoid maintaining a lot of state.
 *
 * @author Frank Mitchell
 */
public final class FastJsonPullParser implements JsonPullParser {

    private final CodePointSource _source;

    private JsonEvent _event;
    private JsonEvent _lastEvent;
    private String _stringValue;
    private BigDecimal _numberValue;

    private final BitSet _objectsByDepth = new BitSet();
    private int _depth = 0;

    private boolean _streamAsArray = false;
    private boolean _allowAllDoubles = false;

    /**
     * Indicates the parser performed look-ahead. The parser therefore should
     * not advance its Source, but reread the Source's current character.
     *
     * Strings end with a single character (") and literals have a finite
     * length; numbers end on the last digit, which is also part of their value.
     */
    private boolean _lookahead;

    /**
     * A constructor around a source of Unicode characters.
     *
     * @param src a source of Unicode code points
     */
    public FastJsonPullParser(CodePointSource src) {
        _source = src;
        _event = JsonEvent.START_STREAM;
        _lastEvent = _event;
        _stringValue = null;
        _numberValue = null;
    }

    /**
     * A constructor around a stream of UTF-8 bytes.While the JSON spec uses
     * only ASCII characters, the Default implementation allows Unicode inside
     * string constants. While this one does also, any embedded non-ASCII
     * characters will slow down processing.
     *
     * @param in an input stream of (mostly) ASCII
     * @throws java.io.IOException
     */
    public FastJsonPullParser(InputStream in) throws IOException {
        // this(new Utf8Source(in));
        this(new ReaderSource(in, StandardCharsets.UTF_8));
    }

    /* -------------------- PARSER OPTIONS ----------------------- */

    /**
     * Whether to parse NaN and +/-Infinity as JSON Numbers.
     * The spec says no, but some implementations allow it as an option.
     * 
     * @return true if 'NaN' and 'Infinity" values are allowed.
     */
    public boolean isAllowAllDoubles() {
        return _allowAllDoubles;
    }

    /**
     * @param value the value to set
     */
    public void setAllowAllDoubles(boolean value) {
        _allowAllDoubles = value;
    }

    /**
     * Whether to treat the stream as an Array. If true the parser will allow
     * the stream to contain multiple values, not just one as per the
     * specification. May be useful to handle continuous streams of messages,
     * e.g. JSON-RPC.
     *
     * @return true if parsing multiple values on same stream.
     */
    public boolean isStreamAsArray() {
        return _streamAsArray;
    }

    /**
     * @param value the value to set
     */
    public void setStreamAsArray(boolean value) {
        _streamAsArray = value;
    }

    /*  ------------------- PARSER METHODS ----------------------- */
    @Override
    public JsonEvent getEvent() {
        return _event;
    }

    @Override
    public boolean isInArray() {
        return depth() > 0 && !_objectsByDepth.get(_depth);
    }

    @Override
    public boolean isInObject() {
        return depth() > 0 && _objectsByDepth.get(depth());
    }

    @Override
    public boolean isCurrentKeySupported() {
        return false;
    }

    @Override
    public String getCurrentKey() {
        // ON PURPOSE!
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getString() {
        if (_stringValue != null) {
            return _stringValue;
        } else if (_numberValue != null) {
            return _numberValue.toString();
        } else {
            throw new IllegalStateException("Not a JSON key, String, or Number");
        }
    }

    /**
     * Provides the last Number parsed. In this class it's always a BigDecimal.
     *
     * @return last number parsed.
     * @throws IllegalStateException if not a number
     */
    @Override
    public BigDecimal getNumber() {
        if (_numberValue == null) {
            throw new IllegalStateException("Not a JSON Number");
        }
        return _numberValue;
    }

    @Override
    public void next() throws IOException {
        clearEventFields();

        if (isLookahead()) {
            setLookahead(false);
        } else if (_source.hasNext()) {
            _source.next();
        } else {
            _event = JsonEvent.END_STREAM;
            return;
        }

        int c = skipWhitespace();
        if (isInObjectOrArray() && c == ',') {
            if (_source.hasNext()) {
                _source.next();
                c = skipWhitespace();
            } else {
                _event = JsonEvent.SYNTAX_ERROR;
                return;
            }
        }
        switch (c) {
            case '{':
                if (expectValue()) {
                    _event = JsonEvent.START_OBJECT;
                    pushValue(_event);
                }
                break;
            case '}':
                // TODO: Check if in appropriate state
                if (expectEndOfObject()) {
                    _event = JsonEvent.END_OBJECT;
                    popValue();
                }
                break;
            case '[':
                // TODO: Check if in appropriate state
                if (expectValue()) {
                    _event = JsonEvent.START_ARRAY;
                    pushValue(_event);
                } else {
                    _event = JsonEvent.SYNTAX_ERROR;
                }
                break;
            case ']':
                if (expectValue() && isInArray()) {
                    _event = JsonEvent.END_ARRAY;
                    popValue();
                }
                break;
            case 'f':
                if (expectValue()) {
                    readLiteral("false", JsonEvent.VALUE_FALSE); // sets _event
                }
                break;
            case 't':
                if (expectValue()) {
                    readLiteral("true", JsonEvent.VALUE_TRUE); // sets _event
                }
                break;
            case 'n':
                if (expectValue()) {
                    readLiteral("null", JsonEvent.VALUE_NULL); // sets _event
                }
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (expectValue()) {
                    readNumber();
                }
                if (_numberValue != null) {
                    _event = JsonEvent.VALUE_NUMBER;
                }
                break;
            case '\"':
                // TODO: Check if in appropriate state
                if (expectValue()) {
                    readString();
                } else if (expectKey()) {
                    readKey();
                }
                if (_stringValue != null) {
                    if (expectKey()) {
                        _event = JsonEvent.KEY_NAME;
                    } else {
                        _event = JsonEvent.VALUE_STRING;
                    }
                }
                break;
            default:
                if (c < 0 && expectEndOfStream()) {
                    _event = JsonEvent.END_STREAM;
                }
                break;
        }
    }

    private JsonEvent lastEvent() {
        return _lastEvent;
    }

    private void clearEventFields() {
        _lastEvent = _event;
        _event = JsonEvent.SYNTAX_ERROR;
        _stringValue = null;
        _numberValue = null;
    }

    private boolean isInObjectOrArray() {
        return depth() > 0;
    }

    private int depth() {
        return _depth;
    }

    private boolean isLookahead() {
        return _lookahead;
    }

    private void setLookahead(boolean value) {
        _lookahead = value;
    }

    private void pushValue(JsonEvent event) {
        _depth++;
        _objectsByDepth.set(depth(), event == JsonEvent.START_OBJECT);
    }

    private void popValue() {
        _objectsByDepth.set(depth(), false);
        _depth--;
    }

    private boolean expectKey() {
        if (isInObject()) {
            switch (lastEvent()) {
                case END_OBJECT:
                case END_ARRAY:
                case VALUE_STRING:
                case VALUE_NUMBER:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    return true;
            }
        }
        return false;
    }

    private boolean expectValue() {
        return lastEvent() == JsonEvent.START_STREAM
                || isInArray()
                || (isInObject() && lastEvent() == JsonEvent.KEY_NAME);
    }

    private boolean expectEndOfObject() {
        return isInObject();
    }

    private boolean expectEndOfArray() {
        return isInArray();
    }

    private boolean expectEndOfStream() {
        return depth() == 0;
    }

    private int skipWhitespace() throws IOException {
        int c = _source.getCodePoint();
        while (Character.isWhitespace(c)) {
            _source.next();
            c = _source.getCodePoint();
        }
        return c;
    }

    private void readKey() throws IOException {
        readString();
        int c = skipWhitespace();
        if (c == ':') {
            _event = JsonEvent.KEY_NAME;
        } else {
            _event = JsonEvent.SYNTAX_ERROR;
        }
    }

    /*
     * Read the string, verify it's a legal JSON String, and set _stringValue
     */
    private void readString() {

    }

    /**
     *
     */
    private void readNumber() {

    }

    private void readLiteral(String expected, JsonEvent value) throws IOException {
        if (_source.getCodePoint() == expected.codePointAt(0)) {
            for (int i = 1; i < expected.length(); i++) {
                if (!_source.hasNext()) {
                    return;
                }
                _source.next();
                if (expected.codePointAt(i) != _source.getCodePoint()) {
                    return;
                }
            }
            _event = value;
        }
    }

    @Override
    public void close() throws IOException {
        _source.close();
    }
}
