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

import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author fmitchell
 */
public abstract class AbstractJsonPullParserFactory implements JsonPullParserFactory {
    
    protected static final String UTF8 = "UTF-8";

    @Override
    public JsonPullParser createParser(Reader reader) throws IOException {
        final ReaderSource source = new ReaderSource(reader);
        return createParser(source);
    }

    @Override
    public JsonPullParser createParser(InputStream input) throws IOException {
        return createParser(new InputStreamReader(input, UTF8));
    }

    @Override
    public JsonPullParser createParser(InputStream input, String enc) throws IOException {
        return createParser(new InputStreamReader(input, enc));
    }
    
    /**
     * Create a parser from a {@link CodePointSource}.
     * 
     * @param source a source for Unicode code points.
     * @return a new JsonPullParser
     * @throws IOException 
     */
    protected abstract JsonPullParser createParser(CodePointSource source) throws IOException;
}
