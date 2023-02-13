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

import com.frank_mitchell.codepoint.CodePointSource;
import java.io.IOException;
import static org.junit.Assert.assertTrue;

/**
 * A mock code point source that reads from a StringBuilder.
 * This allows us to create the reader <strong>first</strong>,
 * then feed it JSON (or anything else).
 * 
 * @author fmitchell
 */
public class FakeSource implements CodePointSource {
    
    /*
    The parser likes to read one or two characters ahead, so we'll give
    it some whitespace until we have real contents.
    */
    final CharSequence _input;
    boolean _closed = false;
    boolean _nextCalled = false;
    int _pos = -1;
    
    public FakeSource(CharSequence s) {
        _input = s;
    }

    @Override
    public int getCodePoint() {
        assertTrue("should not be closed!", !_closed);
        assertTrue("next provides next character", _nextCalled);
        assertTrue("advanced beyond the end of input", _pos <= _input.length());

        // If we're testing something outside the Basic Multilingual Plane
        // this may need more code ... unlike a real mock object.
        if (_pos < _input.length()) {
            return _input.charAt(_pos);
        } else {
            return -1;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        assertTrue("should not be closed!", !_closed);
        return (_pos + 1 < _input.length());
    }

    @Override
    public void next() throws IOException {
        assertTrue("should not be closed!", !_closed);
        _nextCalled = true;
        _pos++;
    }
    
    @Override
    public void close() throws IOException {
        assertTrue("should not be closed!", !_closed);
        _closed = true;
    }
}
