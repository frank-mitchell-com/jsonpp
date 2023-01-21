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


/**
 * 
 */
import java.io.*;
import java.util.*;

import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;

/**
 * @author frankmitchell
 *
 */
public final class DefaultJsonPullParserFactory implements JsonPullParserFactory {

    private final Map<String, Object> _config;

    public DefaultJsonPullParserFactory() {
        this(null);
    }

    public DefaultJsonPullParserFactory(Map<String, ?> conf) {
        _config = new HashMap<String, Object>();
        if (conf != null) {
            _config.putAll(conf);
        }
    }

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
