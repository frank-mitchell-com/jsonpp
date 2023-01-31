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

import com.frank_mitchell.jsonpp.CodePointSink;
import com.frank_mitchell.jsonpp.JsonEvent;
import static com.frank_mitchell.jsonpp.JsonEvent.VALUE_FALSE;
import java.io.IOException;
import java.util.PrimitiveIterator;
import com.frank_mitchell.jsonpp.JsonPushProducer;

/**
 *
 * @author fmitchell
 */
public class DefaultJsonPushProducer implements JsonPushProducer {

    private final CodePointSink _sink;
    private JsonEvent _event;
    private String _string;
    private Number _number;

    DefaultJsonPushProducer(CodePointSink sink) {
        _sink = sink;
        _event = JsonEvent.START_STREAM;
    }

    @Override
    public void setEvent(JsonEvent event) {
        synchronized (this) {
            if (event != null) {
                _event = event;
            }
        }
    }

    @Override
    public JsonEvent getEvent() {
        synchronized (this) {
            return _event;
        }
    }

    @Override
    public String getJsonValue() throws IllegalStateException {
        synchronized (this) {
            switch (_event) {
                case START_ARRAY:
                    return "[";
                case END_ARRAY:
                    return "]";
                case START_OBJECT:
                    return "{";
                case END_OBJECT:
                    return "}";
                case KEY_NAME:
                    return quoteString(getString(), "\"\"") + ":";
                case VALUE_NULL:
                    return "null";
                case VALUE_TRUE:
                    return "true";
                case VALUE_FALSE:
                    return "false";
                case VALUE_NUMBER:
                    if (_number != null) {
                        return toJsonNumber(_number); // this good enough?
                    } else {
                        throw new IllegalStateException("no JSON Number for " + _event);
                    }
                case VALUE_STRING:
                    return quoteString(getString(), "null");
                default:
                    throw new IllegalStateException("no JSON value for " + _event);
            }
        }
    }

    private String getString() {
        synchronized (this) {
            if (_string != null) {
                return _string;
            } else if (_number != null) {
                return _number.toString();
            } else {
                return null;
            }
        }
    }
    
    private String toJsonNumber(Number n) {
        final double d = n.doubleValue();
        // JSON spec:
        //     number ::= whole frac? exp?
        // where 
        //     whole ::= ( ("-")? ( 0 | [1-9][0-9]* ) )
        //     frac  ::= ( '.' [0-9]* ) 
        //     exp   ::= ( ( "e"|"E" ) ("+"|"-")? [0-9][0-9]* )
        //
        // Meanwhile we have this:
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            // no provisions for +/-Inf or NaN in the spec, so
            return "null";
        } else {
            return n.toString();
        }
    }

    private String quoteString(CharSequence s, String alt) {
        if (s == null) {
            return alt;
        }

        StringBuilder builder = new StringBuilder("\"");
        builder.append("\"");
        PrimitiveIterator.OfInt iter = s.chars().iterator();
        while (iter.hasNext()) {
            final int c = iter.nextInt();
            switch (c) {
                case '\"':
                    builder.append("\\\"");
                case '\\':
                    builder.append("\\\\");
                    break;
                case '/':
                    builder.append("\\/");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    if (c <= 0x7F && !Character.isISOControl(c)) {
                        builder.append(c);
                    } else {
                        builder.append("\\u");
                        appendUnicodeHex(builder, c);
                    }
                    break;
            }
        }
        return builder.toString();
    }
    
    private void appendUnicodeHex(StringBuilder b, int c) {
        String hex = Integer.toHexString(c);
        if (c < 0) {
            // OK, this shouldn't happen, so lets get dangerous ...
            long lc = Integer.toUnsignedLong(c);
            appendUnicodeHex(b, (int) ((lc >> 16) & 0xFFFF));
            appendUnicodeHex(b, (int) (lc & 0xFFFF));
        } else if (c <= 0xFF) {
            if (c <= 0xF) {
                b.append("000").append(hex);
            } else {
                b.append("00").append(hex);
            }
        } else {
            if (c <= 0xFFF) {
                b.append("0").append(hex);
            } else if (c <= 0xFFFF) {
                b.append(hex);
            } else {
                // Json spec doesn't allow more than four digits
                appendUnicodeHex(b, Character.highSurrogate(c));
                appendUnicodeHex(b, Character.lowSurrogate(c));
            }
        } 
    }

    @Override
    public void setString(String value) throws IllegalStateException {
        synchronized (this) {
            _string = value;
        }
    }

    @Override
    public void setNumber(Number value) throws IllegalStateException {
        synchronized (this) {
            _number = value;
        }
    }

    @Override
    public void push() throws IOException, IllegalStateException {
        synchronized (this) {
            // TODO: Verify that this is valid JSON
            // TODO: Pretty-printing options?
            _sink.putCharSequence(getJsonValue());
        }
    }

    @Override
    public void flush() throws IOException {
        _sink.flush();
    }

    @Override
    public void close() throws IOException, IllegalStateException {
        _sink.close();
    }

    @Override
    public void finishStream() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
