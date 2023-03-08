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

import java.io.IOException;

import com.frank_mitchell.codepoint.CodePointSource;

final class DefaultJsonLexer implements JsonLexer {

    private final StringBuilder _tokenBuf = new StringBuilder();
    private final CodePointSource _source;

    private int _tokenType = TOKEN_ERROR;
    private boolean _pushback = false;

    DefaultJsonLexer(CodePointSource s) {
        _source = s;
    }

    @Override
    public int getTokenType() {
        return _tokenType;
    }

    @Override
    public CharSequence getToken() {
        return _tokenBuf;
    }

    private boolean isCodePointParsed() {
        return !_pushback;
    }

    private void setCodePointUnparsed() {
        this._pushback = true;
    }

    private void setCodePointParsed() {
        this._pushback = false;
    }

    @Override
    public void next() throws IOException {
        _tokenType = TOKEN_ERROR;
        _tokenBuf.setLength(0);

        if (!_source.hasNext()) {
            _tokenType = TOKEN_EOF;
            return;
        }

        if (isCodePointParsed()) {
            _source.next();
        }

        int c = _source.getCodePoint();

        setCodePointParsed();

        while (c >= 0 && isJsonWhitespace(c)) {
            _source.next();
            c = _source.getCodePoint();
        }

        if (c < 0) {
            _tokenType = TOKEN_EOF;
            return;
        }

        switch (c) {
            case '[':
                _tokenType = TOKEN_ARR_OPEN;
                _tokenBuf.append((char) c);
                break;
            case ']':
                _tokenType = TOKEN_ARR_CLOSE;
                _tokenBuf.append((char) c);
                break;
            case '{':
                _tokenType = TOKEN_OBJ_OPEN;
                _tokenBuf.append((char) c);
                break;
            case '}':
                _tokenType = TOKEN_OBJ_CLOSE;
                _tokenBuf.append((char) c);
                break;
            case ',':
                _tokenType = TOKEN_COMMA;
                _tokenBuf.append((char) c);
                break;
            case ':':
                _tokenType = TOKEN_COLON;
                _tokenBuf.append((char) c);
                break;
            case '-':
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
                _tokenType = readNumber();
                break;
            case '"':
                _tokenType = readString();
                break;
            case 'f':
                _tokenType = readLiteral("false", TOKEN_FALSE);
                break;
            case 'n':
                _tokenType = readLiteral("null", TOKEN_NULL);
                break;
            case 't':
                _tokenType = readLiteral("true", TOKEN_TRUE);
                break;
        }
    }

    private static boolean isJsonWhitespace(int c) {
        switch (c) {
            case '\r':
            case '\n':
            case '\t':
            case ' ':
                return true;
            default:
                return false;
        }
    }

    private static boolean isJsonDigit(int c) {
        switch (c) {
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
                return true;
            default:
                return false;
        }
    }

    private static boolean isJsonHexDigit(int c) {
        switch (c) {
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
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return true;
            default:
                return false;
        }
    }

    private int readLiteral(String expected, int type) throws IOException {
        if (_source.getCodePoint() == expected.charAt(0)) {
            _tokenBuf.append((char) _source.getCodePoint());
        } else {
            return TOKEN_ERROR;
        }

        for (int i = 1; i < expected.length(); i++) {
            _source.next();
            if (_source.getCodePoint() != expected.charAt(i)) {
                return TOKEN_ERROR;
            }
            _tokenBuf.append((char) _source.getCodePoint());
        }
        return type;
    }

    private int readNumber() throws IOException {
        int c = _source.getCodePoint();

        if (c == '-') {
            _tokenBuf.append((char) c);
            _source.next();
            c = _source.getCodePoint();
            if (c < 0) {
                // Unexpected end of input
                _tokenBuf.append("<EOF>");
                return TOKEN_ERROR;
            }
        }

        if (!isJsonDigit(c)) {
            // Unexpected character
            return TOKEN_ERROR;
        }

        if (c == '0') {
            // according to spec, only zero or a decimal can start with 0
            _tokenBuf.append('0');
            _source.next();
            c = _source.getCodePoint();
        } else {
            while (isJsonDigit(c)) {
                _tokenBuf.append((char) c);
                _source.next();
                c = _source.getCodePoint();
            }
        }

        // decimal portion
        if (c == '.') {
            _tokenBuf.append('.');
            _source.next();
            c = _source.getCodePoint();

            while (isJsonDigit(c)) {
                _tokenBuf.append((char) c);
                _source.next();
                c = _source.getCodePoint();
            }
        }

        if (c < 0) {
            // EOF therefore end of number
            return TOKEN_NUMBER;
        }

        // exponent
        if (c == 'e' || c == 'E') {
            _tokenBuf.append('e');
            _source.next();
            c = _source.getCodePoint();

            if (c == '-' || c == '+') {
                _tokenBuf.append((char) c);
                _source.next();
                c = _source.getCodePoint();
            }
            while (isJsonDigit(c)) {
                _tokenBuf.append((char) c);
                _source.next();
                c = _source.getCodePoint();
            }
        }
        setCodePointUnparsed();
        return TOKEN_NUMBER;
    }

    private int readString() throws IOException {
        int result = TOKEN_STRING;
        int c = _source.getCodePoint();

        _tokenBuf.append((char) c);

        do {
            _source.next();
            c = _source.getCodePoint();

            if (c < 0) {
                // end of input without closing quote
                _tokenBuf.append("<EOF>");
                return TOKEN_ERROR;
            }
            if (c < 0x20 || c > 0x10FFFF) {
                // illegal character in string
                _tokenBuf.append("\\u{").append(Integer.toHexString(c)).append("}");
                return TOKEN_ERROR;
            }

            if (c == '\\') {
                boolean valid = readEscapeSequence();
                if (!valid) {
                    // string is malformed, but keep looking for a closing quote.
                    result = TOKEN_ERROR;
                }
            } else if (c > 0xFFF) {
                // TODO: handle chars > 0xFFFF correctly
                _tokenBuf.append(Character.highSurrogate(c)).append(Character.lowSurrogate(c));
            } else {
                _tokenBuf.append((char) c);
            }

        } while (c != '"');

        return result;
    }

    private boolean readEscapeSequence() throws IOException {
        int c = _source.getCodePoint();

        _tokenBuf.append((char) c);
        _source.next();

        c = _source.getCodePoint();
        _tokenBuf.append((char) c);

        if (isJsonEscapeChar(c)) {
            if (c == 'u') {
                // also ensure next four chars are hex digits
                for (int i = 0; i < 4; i++) {
                    _source.next();
                    c = _source.getCodePoint();
                    _tokenBuf.append((char) c);
                    if (!isJsonHexDigit(c)) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

    private boolean isJsonEscapeChar(int c) {
        switch (c) {
            case 'b':
            case 'f':
            case 'n':
            case 'r':
            case 't':
            case 'u':
            case '"':
            case '/':
            case '\\':
                return true;
            default:
                return false;
        }
    }

    @Override
    public void close() throws IOException {
        _source.close();
    }
}
