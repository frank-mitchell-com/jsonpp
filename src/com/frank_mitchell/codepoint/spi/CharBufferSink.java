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
package com.frank_mitchell.codepoint.spi;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.PrimitiveIterator;

/**
 * Writes code points to a Byte Buffer.
 * 
 * @author fmitchell
 */
public class CharBufferSink extends AbstractSink {
    private final CharBuffer _cbuf;
    
    public CharBufferSink(CharBuffer cb) {
        _cbuf = cb;
    }

    @Override
    public void putCodePoint(int cp) throws IOException {
        if (Character.isBmpCodePoint(cp)) {
            _cbuf.put((char)cp);            
        } else {
            int[] iarray = { cp };
            String str = new String(iarray, 0, iarray.length);
            _cbuf.put(str);
        }
    }

    @Override
    public void putChars(final char[] cbuf, final int offset, final int len) throws IOException {
        _cbuf.put(cbuf, offset, len);
    }

    @Override
    public void putCharSequence(CharSequence seq) throws IOException {
        // No point in converting if we'll just convert them back
        PrimitiveIterator.OfInt iter = seq.chars().iterator();
        while (iter.hasNext()) {
            _cbuf.put((char)iter.nextInt());
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
    
}
