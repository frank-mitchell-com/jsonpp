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

import java.io.*;
import java.util.*;

import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;

/**
 * The default JsonPullParserFactory.
 *
 * Only this class is public because extending the others is at best
 * useless and at worst a source of bugs.
 *
 * @author Frank Mitchell
 */
public final class DefaultJsonPullParserFactory implements JsonPullParserFactory {

    @Override
    public JsonPullParser createParser(Reader reader) throws IOException {
        return new DefaultJsonPullParser(new ReaderSource(reader));
    }

    @Override
    public JsonPullParser createParser(InputStream input) throws IOException {
        return createParser(new InputStreamReader(input));
    }

    @Override
    public JsonPullParser createParser(InputStream input, String enc) throws IOException {
        return createParser(new InputStreamReader(input, enc));
    }
}
