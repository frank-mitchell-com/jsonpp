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
package com.frank_mitchell.jsonbb.spi;

import java.util.PrimitiveIterator;

/**
 *
 * @author fmitchell
 */
class DefaultJsonString extends DefaultJsonBaseValue {
    
    final String _value;

    DefaultJsonString(String v) {
        _value = v;
    }

    @Override
    public void writeTo(StringBuilder builder) {
        appendJsonString(builder, _value, "null");
    }
    
    public static void appendJsonString(StringBuilder b, CharSequence str, String alt) {
        if (str == null) {
            b.append(alt);
            return;
        }
        
        b.append("\"");
        PrimitiveIterator.OfInt iter = str.chars().iterator();
        while (iter.hasNext()) {
            final int c = iter.nextInt();
            switch (c) {
                case '\"':
                    b.append("\\\"");
                case '\\':
                    b.append("\\\\");
                    break;
                case '/':
                    b.append("\\/");
                    break;
                case '\b':
                    b.append("\\b");
                    break;
                case '\f':
                    b.append("\\f");
                    break;
                case '\n':
                    b.append("\\n");
                    break;
                case '\r':
                    b.append("\\r");
                    break;
                case '\t':
                    b.append("\\t");
                    break;
                default:
                    if (c <= 0x7F && !Character.isISOControl(c)) {
                        b.append(c);
                    } else {
                        b.append("\\u");
                        appendUnicodeHex(b, c);
                    }
                    break;
            }
        }
    }

    private static void appendUnicodeHex(StringBuilder b, int c) {
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
}
