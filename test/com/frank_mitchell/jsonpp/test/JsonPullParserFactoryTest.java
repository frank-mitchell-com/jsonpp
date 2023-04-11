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
package com.frank_mitchell.jsonpp.test;

import com.frank_mitchell.codepoint.CodePointSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;
import com.frank_mitchell.jsonpp.spi.DefaultJsonPullParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonPullParserFactoryTest {

    private JsonPullParserFactory _factory;

    @Before
    public void setUp() {
        _factory = getJsonPullParserFactory();
    }

    @After
    public void tearDown() {
        _factory = null;
    }

    /**
     * Common method to create a "default" parser factory. Override this method
     * to test another parser configuration.
     *
     * @return a parser factory for all other tests
     */
    protected JsonPullParserFactory getJsonPullParserFactory() {
        return new DefaultJsonPullParserFactory();
    }

    @Test
    public void testReaderParser() throws Exception {
        Reader reader = new StringReader(getData());
        JsonPullParser parser = _factory.createParser(reader);
        assertNotNull("Parser should not be null", parser);
        parseData(parser);
    }

    @Test
    public void testInputStreamParser() throws Exception {
        Charset charset = StandardCharsets.US_ASCII;
        final byte[] data = getData().getBytes(charset);
        InputStream instream = new ByteArrayInputStream(data);
        JsonPullParser parser = _factory.createParser(instream, charset);
        assertNotNull("Parser should not be null", parser);
        parseData(parser);
    }

    @Test
    public void testUtf8InputStreamParser() throws Exception {
        final byte[] data = getData().getBytes(StandardCharsets.UTF_8);
        InputStream instream = new ByteArrayInputStream(data);
        JsonPullParser parser = _factory.createUtf8Parser(instream);
        assertNotNull("Parser should not be null", parser);
        parseData(parser);
    }

    @Test
    public void testCodePointSourceParser() throws Exception {
        CodePointSource in = new FakeSource(getData());
        JsonPullParser parser = _factory.createParser(in);
        assertNotNull("Parser should not be null", parser);
        parseData(parser);
    }

    public String getData() {
        return "{}";
    }

    public void parseData(JsonPullParser parser) throws IOException {
        assertEquals("event", JsonEvent.START_STREAM, parser.getEvent());

        parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, parser.getEvent());

        parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, parser.getEvent());

        parser.next();
        assertEquals("event", JsonEvent.END_STREAM, parser.getEvent());
    }
}
