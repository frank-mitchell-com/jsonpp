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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

/**
 * Creates JsonPullParser instances without clients knowing the specific
 * class(es) used.
 *
 * {@link #setConfiguration(Map)} provides a hook to configure a factory 
 * without knowing or caring what specific instance performs the work.
 */
public interface JsonPullParserFactory {
   /**
    * Retrieve the configuration of this factory.
    * A map of keys and values represents the factory's current configuration.
    * Keys and allowed values depend on the factory's implementation.
    *
    * Reimplementers should return either a copy of their internal configuration
    * or an immutable wrapper around it, so that changing the map does
    * not provide a back door to changing configuration parameters. 
    *
    * @return configuration
    */
    default Map<String, ?> getConfiguration() {
        return Collections.emptyMap();
    }

   /**
    * Configure this factory with values from `conf`.
    * The argument need not contain all recognized configuration keys,
    * and it can contain keys an implementation does not recognize
    * without throwing an exception.
    *
    * The default implementation does nothing and sets nothing.
    * Reimplementors should copy the values into instance variables or
    * at least a new Map instance. Otherwise changing the argument will 
    * provide a back door to changing configuration parameters. 
    *
    * @throws IllegalArgumentException if a recognized key contains an invalid value.
    * @see #getConfiguration
    */
   default void setConfiguration(Map<String, ?> conf)
       throws IllegalArgumentException {
   }

   /**
    * Set a single configuration value.
    * This convenience method is equivalent to calling 
    * `setConfiguration` with a Map containing only `key` and `value`.
    *
    * @throws IllegalArgumentException if a recognized key contains an invalid value.
    * @see #getConfiguration
    * @see #setConfiguration
    */
   default void setConfigurationValue(String key, Object value) throws IllegalArgumentException {
       Map<String, Object> c = java.util.Collections.singletonMap(key, value);
       setConfiguration(c);
   }

   /**
    * Create a parser to process `reader`.
    *
    * @return new parser
    */
   JsonPullParser createParser(Reader reader) throws IOException;

   /**
    * Create a parser to process `input` as an ASCII or UTF-8 stream.
    *
    * @return new parser
    */
   JsonPullParser createParser(InputStream input) throws IOException;

   /**
    * Create a parser to process `input` as a stream of `enc` characters.
    *
    * @return new parser
    */
   JsonPullParser createParser(InputStream input, String enc) throws IOException;

}
