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
package com.frank_mitchell.jsonpp;

import com.frank_mitchell.codepoint.CodePointSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Creates JsonPullParser instances without clients knowing the specific
 * class(es) used.
 *
 * @author Frank Mitchell
 */
public interface JsonPullParserFactory {

    /**
     * Create a parser to read <code>char</code>s.
     *
     * @param reader a stream of UTF-16 characters to parse
     * @return new parser
     * @throws IOException if source throws an IOException
     */
    JsonPullParser createParser(Reader reader) throws IOException;

    /**
     * Create a parser to process an ASCII or UTF-8 stream.
     *
     * @param input a stream of ASCII or UTF-8 bytes
     * @return new parser
     * @throws IOException if source throws an IOException
     */
    JsonPullParser createUtf8Parser(InputStream input) throws IOException;

    /**
     * Create a parser to process an encoded byte stream.
     *
     * @param input a stream of encoded bytes to parse
     * @param enc   the standard name for the stream's encoding
     * @return new parser
     * @throws IOException if source throws an IOException
     */
    JsonPullParser createParser(InputStream input, Charset enc) throws IOException;

    /**
     * Create a parser to process a stream of Unicode code points.
     *
     * @param source provider of code points
     * @return new parser
     * @throws IOException if source throws an IOException
     */
    JsonPullParser createParser(CodePointSource source) throws IOException;


}
