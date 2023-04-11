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
package com.frank_mitchell.jsonbb;

/**
 * Builds a syntactically correct JSON Array.
 *
 * @author Frank Mitchell
 */
public interface JsonArrayBuilder extends JsonBuilder {
    @Override
    default boolean isArray() {
        return true;
    }

    @Override
    default boolean isObject() {
        return false;
    }

    /**
     * Add a JSON String to the end of this Array.
     *
     * @param value the value of the String
     * @return this
     */
    JsonArrayBuilder addString(String value);
    
    /**
     * Add a JSON Number to the end of this Array.
     *
     * @param value the value of the Number
     * @return this
     */
    JsonArrayBuilder addNumber(Number value);
    
    /**
     * Add a JSON Boolean to the end of this Array.
     *
     * @param value the boolean value
     * @return this
     */
    JsonArrayBuilder addBoolean(boolean value);
    
    /**
     * Add a JSON Boolean {@code true} to the end of this Array.
     * 
     * @return this
     */
    default JsonArrayBuilder addTrue() {
        return addBoolean(true);
    }
    
    /**
     * Add a JSON Boolean {@code false} to the end of this Array.
     * 
     * @return this
     */
    default JsonArrayBuilder addFalse() {
        return addBoolean(false);
    }
    
    /**
     * Add a JSON Null to the end of this Array.
     * 
     * @return this
     */
    JsonArrayBuilder addNull();
    
    /**
     * Add a new JSON Object to the end of this Array.
     * 
     * @return the builder for the JSON Object.
     */
    JsonObjectBuilder addNewObject();
    
    /**
     * Add a new JSON Array to the end of this Array.
     * 
     * @return the builder for the JSON Array.
     */
    JsonArrayBuilder addNewArray();
}
