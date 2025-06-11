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
import com.frank_mitchell.codepoint.ForCharsets;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A wrapper for a stream of ASCII or UTF-8 bytes.
 * It's fastest if the stream is pure ASCII, but it can handle multi-byte
 * UTF-8 characters correctly, if not efficiently.
 */
public class FastAsciiSource implements CodePointSource {
    private final InputStream _input;
    private int _current = -1;
    private volatile boolean _hasnext = true;

    /**
     * Create a source around a stream of ASCII or UTF-8 bytes (only).
     *
     * @param in the Input Stream.
     */
    @ForCharsets(names={"US-ASCII"})
    public FastAsciiSource(InputStream in) {
        Objects.requireNonNull(in, "No InputStream");
        _input = new BufferedInputStream(in, 1024);
    }

    @Override
    public int getCodePoint() {
        synchronized (this) {
            if (_current < 0) {
                throw new IllegalStateException("have not called next() yet");
            }
            return _current;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        return _hasnext;
    }

    @Override
    public void next() throws IOException {
        int c = _input.read();
        if (c < 0) {
            _hasnext = false;
            c = ' ';
        }
        if (c > 0x7F) {
            throw new IOException("Not an ASCII charcter:" + (char) c);
        }
        synchronized (this) {
            _current = c;
        }
    }

    @Override
    public void close() throws IOException {
        _input.close();
    }
}
