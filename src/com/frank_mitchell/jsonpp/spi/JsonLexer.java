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

interface JsonLexer {

    static final int TOKEN_ERROR     = 0x0;
    static final int TOKEN_OBJ_OPEN  = '{';
    static final int TOKEN_OBJ_CLOSE = '}';
    static final int TOKEN_ARR_OPEN  = '[';
    static final int TOKEN_ARR_CLOSE = ']';
    static final int TOKEN_STRING    = '"';
    static final int TOKEN_NUMBER    = '#';
    static final int TOKEN_TRUE      = 't';
    static final int TOKEN_FALSE     = 'f';
    static final int TOKEN_NULL      = 'n';
    static final int TOKEN_COMMA     = ',';
    static final int TOKEN_COLON     = ':';
    static final int TOKEN_EOF       = -1;

    void next() throws IOException;

    CharSequence getToken();

    int getTokenType();
    
    void close() throws IOException;
}