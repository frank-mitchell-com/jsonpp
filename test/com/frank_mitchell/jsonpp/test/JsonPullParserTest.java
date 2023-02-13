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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPullParser;
import com.frank_mitchell.jsonpp.JsonPullParserFactory;
import com.frank_mitchell.jsonpp.spi.DefaultJsonPullParserFactory;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonPullParserTest {

    private static final BigDecimal BIG_DECIMAL_SIX = new BigDecimal("6");

    private JsonPullParserFactory _factory;
    private JsonPullParser _parser;
    private StringBuilder _builder;
    private FakeSource _source;

    @Before
    public void setUp() throws IOException {
        _factory = getJsonPullParserFactory();
        _builder = new StringBuilder();
        _source = new FakeSource(_builder);
        _parser = _factory.createParser(_source);
    }

    @After
    public void tearDown() {
        _factory = null;
        _parser = null;
        _source = null;
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

    public void push(CharSequence s) {
        _builder.append(s);
    }
    
    @Test
    public void parseArray() throws IOException {
        push("[321, \"a string\", true, false, null]");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", "321", _parser.getString());
        assertEquals("number", new BigDecimal("321"), _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_STRING, _parser.getEvent());
        assertEquals("string", "a string", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NULL, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
    }

    @Test
    public void parseArrayMissingComma() throws IOException {
        push("[ true , false null ]");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());
        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());
        _parser.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, _parser.getEvent());
    }

    @Test
    public void parseEmptyArray() throws IOException {
        push("[]");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
    }

    @Test
    public void parseEmptyObject() throws IOException {
        push("{}");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
    }

    @Test
    public void parseObject() throws IOException {
        push("{"
                + "\"alpha\":   \"a string\", \n"
                + "\"bravo\":   213, \n"
                + "\"charlie\": true, \n"
                + "\"delta\":   false, \n"
                + "\"echo\":    null \n"
                + "}");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "alpha", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_STRING, _parser.getEvent());
        assertEquals("string", "a string", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "bravo", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", "213", _parser.getString());
        assertEquals("number", new BigDecimal("213"), _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "charlie", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "delta", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "echo", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NULL, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
    }

    @Test
    public void parseNestedObjectsAndArrays() throws IOException {
        push("{\n"
                + "\t\"alpha\":   \"a string\", \n"
                + "\t\"bravo\":   { \n"
                + "\t\t\"charlie\": true, \n"
                + "\t\t\"delta\":   false, \n"
                + "\t\t\"echo\":    null \n"
                + "\t},\n"
                + "\t\"foxtrot\" : [ 6, 1, 6 ] \n"
                + "}");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());
        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "alpha", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_STRING, _parser.getEvent());
        assertEquals("string", "a string", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "bravo", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "charlie", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "delta", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_FALSE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "echo", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NULL, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertEquals("key", "foxtrot", _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", "6", _parser.getString());
        assertEquals("number", BIG_DECIMAL_SIX, _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", "1", _parser.getString());
        assertEquals("number", BigDecimal.ONE, _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", "6", _parser.getString());
        assertEquals("number", BIG_DECIMAL_SIX, _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
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
     * Common method to test valid JSON input that contains a single string
     * value.
     *
     * @param value the string without quotes and with all escape sequences
     * resolved
     * @param json JSON input containing a quoted string with zero or more legal
     * escape sequences
     * @throws IOException
     */
    protected void testSingleString(String value, String json) throws IOException {
        push(json);

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_STRING, _parser.getEvent());
        assertEquals("string", value, _parser.getString());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
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
     * @param json JSON containing only the number value and whitespace
     *
     * @throws IOException from the parser
     */
    public void testSingleNumber(String number, String json) throws IOException {
        push(json);
        BigDecimal decimal = new BigDecimal(number);

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertEquals("string", number, _parser.getString());
        assertEquals("number", decimal, _parser.getNumber());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
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
     * <code>true</code>, <code>false</code>, or <code>null</code>. Won't work
     * for objects or arrays. Would test the <em>type</em> and validity of a
     * string or number, but not the parsed value.
     *
     * @param ev an event denoting the expected value type
     * @param json valid JSON for a single value
     *
     * @throws IOException from the parser
     */
    protected void testSingleValue(JsonEvent ev, String json) throws IOException {
        push(json);

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", ev, _parser.getEvent());
        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
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
     * Common method to test invalid JSON input. The first or only production
     * parsed should cause an error, e.g.
     * <ul>
     * <li>A stream with no JSON content whatsoever.</li>
     * <li>A malformed number or string.</li>
     * <li>A misspelling of <code>true</code>, <code>false</code>, or
     * <code>null</code></li>
     * <li>A character not allowed as the first non-whitespace in JSON
     * input.</li>
     * </ul>
     * This method is not intended for malformed objects or arrays.
     *
     * @param json invalid JSON
     *
     * @throws IOException from the parser.
     */
    protected void testSingleError(String json) throws IOException {
        push(json);

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, _parser.getEvent());
    }

    @Test
    public void parseArrayCloseError() throws IOException {
        push("[[[true]}]");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, _parser.getEvent());
    }

    @Test
    public void parseObjectCloseError() throws IOException {
        push("{\"a\":{\"b\":{\"c\":true}]}");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_TRUE, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());

        _parser.next();
        assertEquals("event", JsonEvent.SYNTAX_ERROR, _parser.getEvent());
    }

    @Test

    public void parseGetCurrentKey() throws IOException {
        push("["
                + "{"
                + "\"foo\": 1, \"bar\": 2, \"baz\": "
                + "{"
                + "\t\"foobar\": "
                + "["
                + "4, 5"
                + "]"
                + "}"
                + "}"
                + "]");

        assertEquals("event", JsonEvent.START_STREAM, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());
        assertTrue("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "foo", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "foo", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "bar", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "bar", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "baz", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.START_OBJECT, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.KEY_NAME, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "foobar", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.START_ARRAY, _parser.getEvent());
        assertTrue("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertTrue("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.VALUE_NUMBER, _parser.getEvent());
        assertTrue("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "foobar", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertTrue("inObject", _parser.isInObject());
        assertEquals("currentKey", "baz", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.END_OBJECT, _parser.getEvent());
        assertTrue("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.END_ARRAY, _parser.getEvent());
        assertFalse("inArray", _parser.isInArray());
        assertFalse("inObject", _parser.isInObject());
        assertNull("currentKey", _parser.getCurrentKey());

        _parser.next();
        assertEquals("event", JsonEvent.END_STREAM, _parser.getEvent());
    }
}
