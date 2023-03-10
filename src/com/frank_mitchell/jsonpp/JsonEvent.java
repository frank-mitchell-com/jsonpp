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

/**
 * Enumeration of all possible JsonPullParser events.
 */
public enum JsonEvent {
    /**
     * Invalid JSON syntax.
     */
    SYNTAX_ERROR,

    /**
     * Before first JSON element
     */
    START_STREAM,

    /**
     * Start of JSON array ('[')
     */
    START_ARRAY,

    /**
     * End of JSON array (']')
     */
    END_ARRAY,

    /**
     * Start of JSON object ('{')
     */
    START_OBJECT,

    /**
     * End of JSON object ('}')
     */
    END_OBJECT,

    /**
     * Key of JSON object member ('"' ... '"' ':')
     */
    KEY_NAME,

    /**
     * JSON null ('null')
     */
    VALUE_NULL,

    /**
     * JSON boolean true ('true')
     */
    VALUE_TRUE,

    /**
     * JSON boolean false ('false')
     */
    VALUE_FALSE,

    /**
     * JSON number
     */
    VALUE_NUMBER,

    /**
     * JSON string ('"'...'"')
     */
    VALUE_STRING,

    /**
     * After last JSON element
     */
    END_STREAM
};
