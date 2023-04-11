/*
 * The MIT License
 *
 * Copyright 2023 Frank Mitchell.
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
 * Builds syntactically correct JSON Objects.
 *
 * @author Frank Mitchell
 */
public interface JsonObjectBuilder extends JsonBuilder {
    @Override
    default boolean isArray() {
        return false;
    }
    
    @Override
    default boolean isObject() {
        return true;
    }
    
    /**
     * Set a JSON String for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @param value  value of the element
     * @return this instance
     */
    JsonObjectBuilder setString(String key, String value);
    
    /**
     * Set a JSON Number for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @param value  value of the element
     * @return this instance
     */
    JsonObjectBuilder setNumber(String key, Number value);
    
    /**
     * Set a JSON Boolean for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @param value  value of the element
     * @return this instance
     */
    default JsonObjectBuilder setBoolean(String key, boolean value) {
        return value ? setTrue(key) : setFalse(key);
    }
    
    /**
     * Set a JSON Boolean {@code false} for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @return this instance
     */
    JsonObjectBuilder setTrue(String key);

    /**
     * Set a JSON Boolean {@code false} for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @return this instance
     */
    JsonObjectBuilder setFalse(String key);

    /**
     * Set a JSON Null for the named element in the JSON Object.
     *
     * @param key    name of the element
     * @return this instance
     */
    JsonObjectBuilder setNull(String key);
    
    /**
     * Set a new JSON Object for the named element in this Object.
     *
     * @param key    name of the element
     * @return the builder for the new Object
     */
    JsonObjectBuilder setNewObject(String key);

    /**
     * Set a new JSON Array for the named element in this JSON Object.
     *
     * @param key    name of the element
     * @return the builder for the new Array
     */
    JsonArrayBuilder setNewArray(String key);
}
