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


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import org.junit.Test;

import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPull;
import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;

public class JsonPullParserTest {

    private static final BigDecimal BIG_DECIMAL_SIX = new BigDecimal("6");

    public static void assertStartOfStream(JsonPullParser p) {
        assertEquals("event", JsonEvent.START_STREAM, p.getEvent());
    }

    static void assertEndOfStream(JsonPullParser p) throws IOException {
        p.next();
        assertEquals("event", JsonEvent.END_STREAM, p.getEvent());
    }

    /**
     * Common method to create a "default" parser that reads a string. Override this
     * method to test another parser configuration.
     *
     * @param json JSON text to parse
     *
     * @return a parser for all other tests
     * @throws IOException from the parser factory
     */
    protected JsonPullParser createParser(String json) throws IOException {
        StringReader reader = new StringReader(json);
        JsonPullParserFactory factory = JsonPull.createParserFactory();
        return factory.createParser(reader);
    }

    @Test
    public void parseArray() throws IOException {
        JsonPullParser p = createParser("[321, \"a string\", true, false, null]");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", "321", p.getString());
        assertEquals("number", new BigDecimal("321"), p.getBigDecimal());

        p.next();
        assertEquals("event", JsonEvent.VALUE_STRING, p.getEvent());
        assertEquals("string", "a string", p.getString());
        
        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NULL, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_ARRAY, p.getEvent());

        assertEndOfStream(p);
    }

    @Test
    public void parseArrayMissingComma() throws IOException {
        JsonPullParser p = createParser("[ true , false null ]");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());
        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());
        p.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, p.getEvent());
    }

    @Test
    public void parseEmptyArray() throws IOException {
        JsonPullParser p = createParser("[]");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_ARRAY, p.getEvent());

        assertEndOfStream(p);
    }

    @Test
    public void parseEmptyObject() throws IOException {
        JsonPullParser p = createParser("{}");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_OBJECT, p.getEvent());

        assertEndOfStream(p);
    }

    @Test
    public void parseObject() throws IOException {
        JsonPullParser p = createParser("{" + 
                "\"alpha\":   \"a string\", \n" + 
                "\"bravo\":   213, \n" + 
                "\"charlie\": true, \n" + 
                "\"delta\":   false, \n" + 
                "\"echo\":    null \n" + 
                "}");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "alpha", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_STRING, p.getEvent());
        assertEquals("string", "a string", p.getString());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "bravo", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", "213", p.getString());
        assertEquals("number", new BigDecimal("213"), p.getBigDecimal());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "charlie", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "delta", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "echo", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NULL, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_OBJECT, p.getEvent());

        assertEndOfStream(p);
    }

    @Test
    public void parseNestedObjectsAndArrays() throws IOException {
        JsonPullParser p = createParser("{\n" + 
                "\t\"alpha\":   \"a string\", \n" +
                "\t\"bravo\":   { \n" + 
                "\t\t\"charlie\": true, \n" + 
                "\t\t\"delta\":   false, \n" + 
                "\t\t\"echo\":    null \n" + 
                "\t},\n"
                + "\t\"foxtrot\" : [ 6, 1, 6 ] \n" + 
                "}");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());
        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "alpha", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_STRING, p.getEvent());
        assertEquals("string", "a string", p.getString());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "bravo", p.getString());

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "charlie", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "delta", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "echo", p.getString());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NULL, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());
        assertEquals("key", "foxtrot", p.getString());

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", "6", p.getString());
        assertEquals("number", BIG_DECIMAL_SIX, p.getBigDecimal());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", "1", p.getString());
        assertEquals("number", BigDecimal.ONE, p.getBigDecimal());

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", "6", p.getString());
        assertEquals("number", BIG_DECIMAL_SIX, p.getBigDecimal());

        p.next();
        assertEquals("event", JsonEvent.END_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_OBJECT, p.getEvent());
        
        assertEndOfStream(p);
    }

    @Test
    public void parseString() throws IOException {
        testSingleString("a string", "\"a string\"");
    }

    @Test
    public void parseStringEscapes() throws IOException {
        testSingleString("\\/\"\b\f\n\r\t\u05D0", 
                "\"\\\\/\\\"\\b\\f\\n\\r\\t\\u05D0\"");
    }

    /**
     * Common method to test valid JSON input that contains a single string value.
     *
     * @param value the string without quotes and with all escape sequences resolved
     * @param json  JSON input containing a quoted string with zero or more legal
     *              escape sequences
     * @throws IOException
     */
    protected void testSingleString(String value, String json) throws IOException {
        JsonPullParser p = createParser(json);

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.VALUE_STRING, p.getEvent());
        assertEquals("string", value, p.getString());
        
        assertEndOfStream(p);
    }

    @Test
    public void parseNumber() throws IOException {
        testSingleNumber("123", " 123 ");
    }

    @Test
    public void parseNegativeNumber() throws IOException {
        testSingleNumber("-456", "\t-456");
    }

    @Test
    public void parseNumberWithDecimal() throws IOException {
        testSingleNumber("123.45", " 123.45 ");
    }

    @Test
    public void parseNumberWithExponent() throws IOException {
        testSingleNumber("1.23e6", " 1.23e6  ");
    }

    /**
     * Common method to test valid JSON input that contains a single number.
     *
     * @param number the number contained in the JSON input
     * @param json   JSON containing only the number value and whitespace
     *
     * @throws IOException from the parser
     */

    public void testSingleNumber(String number, String json) throws IOException {
        JsonPullParser p = createParser(json);
        BigDecimal decimal = new BigDecimal(number);

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, p.getEvent());
        assertEquals("string", number, p.getString());
        assertEquals("number", decimal, p.getBigDecimal());

        assertEndOfStream(p);
    }

    @Test
    public void parseTrue() throws IOException {
        testSingleValue(JsonEvent.VALUE_TRUE, "true");
    }

    @Test
    public void parseFalse() throws IOException {
        testSingleValue(JsonEvent.VALUE_FALSE, "false");
    }

    @Test
    public void parseNull() throws IOException {
        testSingleValue(JsonEvent.VALUE_NULL, "null");
    }

    @Test
    public void parseWithWhitespace() throws IOException {
        testSingleValue(JsonEvent.VALUE_TRUE, "\t  true\r\n");
    }

    /**
     * Common method to test valid JSON input containing a single value, usually
     * <code>true</code>, <code>false</code>, or <code>null</code>. Won't work for
     * objects or arrays. Would test the <em>type</em> and validity of a string or
     * number, but not the parsed value.
     *
     * @param ev   an event denoting the expected value type
     * @param json valid JSON for a single value
     * 
     * @throws IOException from the parser
     */
    protected void testSingleValue(JsonEvent ev, String json) throws IOException {
        JsonPullParser p = createParser(json);

        assertStartOfStream(p);

        p.next();
        assertEquals("event", ev, p.getEvent());
        assertEndOfStream(p);
    }

    @Test
    public void parseWhitespaceOnly() throws IOException {
        testSingleError("    \r\n\t   \r\n");
    }

    @Test
    public void parseZeroChars() throws IOException {
        testSingleError("");
    }

    /**
     * Common method to test invalid JSON input. The first or only production parsed
     * should cause an error, e.g.
     * <ul>
     * <li>A stream with no JSON content whatsoever.</li>
     * <li>A malformed number or string.</li>
     * <li>A misspelling of <code>true</code>, <code>false</code>, or
     * <code>null</code></li>
     * <li>A character not allowed as the first non-whitespace in JSON input.</li>
     * </ul>
     * This method is not intended for malformed objects or arrays.
     *
     * @param json invalid JSON
     *
     * @throws IOException from the parser.
     */
    protected void testSingleError(String json) throws IOException {
        JsonPullParser p = createParser(json);

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, p.getEvent());
    }
    
    @Test
    public void parseArrayCloseError() throws IOException {
        
        JsonPullParser p = createParser("[[[true]}]");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.START_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_ARRAY, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, p.getEvent());
    }

    @Test
    public void parseObjectCloseError() throws IOException {
        
        JsonPullParser p = createParser("{\"a\":{\"b\":{\"c\":true}]}");

        assertStartOfStream(p);

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.START_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.KEY_NAME, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.END_OBJECT, p.getEvent());

        p.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, p.getEvent());
    }
}
