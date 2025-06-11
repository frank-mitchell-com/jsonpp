/*
 * The MIT License
 *
 * Copyright 2025 Frank Mitchell <me@frank-mitchell.com>.
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
package com.frank_mitchell.json;

import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.jsonbb.JsonArrayBuilder;
import com.frank_mitchell.jsonbb.JsonObjectBuilder;
import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;
import com.frank_mitchell.jsonpp.spi.DefaultJsonPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import com.frank_mitchell.jsonbb.JsonBuilderFactory;
import com.frank_mitchell.jsonbb.spi.DefaultJsonBuilderFactory;
import java.util.Optional;

/**
 *
 * @author Frank Mitchell <me@frank-mitchell.com>
 */
public class Json {
    /**
     * Constant for "UTF-32".
     */
    public static final Charset UTF_32 = Charset.forName("UTF-32");
    
    private static JsonBuilderFactory getBuilderFactory() {
        JsonBuilderFactory result = null;
        try {
            ServiceLoader<JsonBuilderFactory> loader
                    = ServiceLoader.load(JsonBuilderFactory.class);
            Optional<JsonBuilderFactory> first = loader.findFirst();
            if (first.isPresent()) {
                result = first.get();
            }
        } catch (ServiceConfigurationError e) {
            System.Logger.Level level = System.Logger.Level.WARNING;

            getLogger().log(level, "Error with ServiceLoader; using default", e);
        } finally {
            if (result == null) {
                System.Logger.Level level = System.Logger.Level.TRACE;

                getLogger().log(level, "Using default builder factory");

                result = new DefaultJsonBuilderFactory();
            }
        }
        return result;
    }

    /**
     * Create a builder for a syntactically correct JSON Array.
     *
     * @return the builder
     */
    public static JsonArrayBuilder getNewArray() {
        JsonBuilderFactory factory = getBuilderFactory();
        return factory.getNewArray();
    }

    /**
     * Create a builder for a syntactically correct JSON Object.
     *
     * @return the builder
     */
    public static JsonObjectBuilder getNewObject() {
        JsonBuilderFactory factory = getBuilderFactory();
        return factory.getNewObject();
    }

    private static JsonPullParserFactory getParserFactory() {
        JsonPullParserFactory result = null;
        try {
            ServiceLoader<JsonPullParserFactory> loader
                    = ServiceLoader.load(JsonPullParserFactory.class);
            Optional<JsonPullParserFactory> first = loader.findFirst();
            if (first.isPresent()) {
                result = first.get();
            }
        } catch (ServiceConfigurationError e) {
            System.Logger.Level level = System.Logger.Level.WARNING;

            getLogger().log(level, "Error with ServiceLoader; using default", e);
        } finally {
            if (result == null) {
                System.Logger.Level level = System.Logger.Level.TRACE;

                getLogger().log(level, "Using default parser factory");

                result = new DefaultJsonPullParserFactory();
            }
        }
        return result;
    }

    private static System.Logger getLogger() {
        Module module = Json.class.getModule();
        System.LoggerFinder loggerFinder = System.LoggerFinder.getLoggerFinder();
        return loggerFinder.getLogger("ELTN", module);
    }

    /**
     * Creates a parser to process UTF-16 characters. In other words, a stream
     * of Java {@code char}s.
     *
     * @param reader a stream of UTF-16 chars.
     *
     * @return a parser for the reader.
     *
     * @throws IOException if the reader throws an exception.
     */
    public static JsonPullParser createPullParser(Reader reader)
            throws IOException {
        JsonPullParserFactory factory = getParserFactory();
        return factory.createParser(reader);
    }

    /**
     * Creates a parser to process bytes in the specified encoding. Because Java
     * translates strings internally to UTF-16, an ELTN parser in Java cannot
     * remain agnostic about the encoding of its bytes.
     *
     * @param stream a stream of bytes.
     * @param cs     a character encoding.
     *
     * @return a parser for the stream.
     *
     * @throws IOException if the stream throws an exception.
     */
    public static JsonPullParser createPullParser(InputStream stream,
                                                  Charset cs) throws IOException {
        JsonPullParserFactory factory = getParserFactory();
        return factory.createParser(stream, cs);
    }

    /**
     * Creates a parser to process bytes in the specified encoding. Because Java
     * translates strings internally to UTF-16, an ELTN parser in Java cannot
     * remain agnostic about the encoding of its bytes.
     *
     * @param cps a stream of Unicode code points.
     *
     * @return a parser for the stream.
     *
     * @throws IOException if the stream throws an exception.
     */
    public static JsonPullParser createPullParser(CodePointSource cps)
            throws IOException {
        JsonPullParserFactory factory = getParserFactory();
        return factory.createParser(cps);
    }

}
