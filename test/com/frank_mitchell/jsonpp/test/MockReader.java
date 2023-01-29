/*
 * Copyright 2023 Frank Mitchell
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
package com.frank_mitchell.jsonpp.test;

import java.io.IOException;
import java.io.Reader;

/**
 * A reader from an external source.
 * This allows us to create the reader <strong>first</strong>,
 * then feed it JSON (or anything else).
 * 
 * @author fmitchell
 */
public class MockReader extends Reader {
    
    /*
    The parser likes to read one or two characters ahead, so we'll give
    it some whitespace until we have real contents.
    */
    final StringBuilder _input = new StringBuilder("  ");
    boolean _closed = false;
    int _pos = 0;

    public void pushInput(CharSequence input) {
        _input.append(input);
    }

    @Override
    public int read(char[] chars, int off, int len) throws IOException {
  
        if (_closed) {
            throw new IOException("Hey, we're closed here!");
        }
        if (_pos >= _input.length()) {
            return -1;
        } else {
            /*
             * I *could* figure out the maximum chars I can safely read,
             * but the implementation only reads one at a time anyway,
             * so ...
             */
            chars[off] = _input.charAt(_pos);
            _pos += 1;
            return 1;
        }
    }

    @Override
    public void close() throws IOException {
        _closed = true;
    }
    
}
