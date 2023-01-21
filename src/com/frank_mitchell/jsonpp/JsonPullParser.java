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
import java.math.BigDecimal;

/**
 * This interface traverses a JSON Value as a stream of events.
 * 
 * Each call to {@link #next()} moves to the next event in the
 * stream, and the various "get" methods identify the type of
 * event, the value of a String or Number, the name of a key
 * String.
 * 
 * @author fmitchell
 *
 */
public interface JsonPullParser {

    /**
     * Get the event parsed by the most recent call to {@link #next()}.
     * 
     * @return most recently parsed event.
     */
	public JsonEvent getEvent();

	/**
	 * Gets the string value associated with the current event.
	 * 
	 * On {@link JsonEvent#VALUE_STRING} or {@link JsonEvent#KEY_NAME},
	 * the result is the JSON String with all escape sequences 
	 * converted to their character values.
	 * 
	 * On a {@link JsonEvent#VALUE_NUMBER}, the result is the number
	 * as originally read.
	 * 
	 * Otherwise the method throws an exception
	 * 
	 * @return  the value of a String or Number or <code>null<code> 
	 * 
	 * @throws IllegalStateException if the current event is not a number.
	 */
	public String getString();


    /**
     * Gets the {@link BigDecimal} value associated with the current event.
     * 
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER},
     * this method returns an unspecified subclass of Number.
     * Otherwise this method throws an exception. 
     * 
     * @return the value of the current JSON Number
     * 
     * @throws IllegalStateException if the current event is not a number.
     */
	public BigDecimal getBigDecimal();
	

	/**
     * Gets the <code>double</code> value associated with the current event.
     * 
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER},
     * this method returns an unspecified subclass of Number.
     * Otherwise this method throws an exception. 
     * 
     * @return the value of the current JSON Number
     * 
     * @throws IllegalStateException if the current event is not a number.
	 */
	public double getDouble() throws IllegalStateException;

	
    /**
     * Gets the <code>int</code> value associated with the current event.
     * 
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER},
     * this method returns an unspecified subclass of Number.
     * Otherwise this method throws an exception. 
     * 
     * @return the value of the current JSON Number
     * 
     * @throws IllegalStateException if the current event is not a number.
     */
    public int getInt() throws IllegalStateException;

    /**
     * Gets the <code>long</code> value associated with the current event.
     * 
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER},
     * this method returns an unspecified subclass of Number.
     * Otherwise this method throws an exception. 
     * 
     * @return
     * @throws IllegalStateException
     */
    default public long getLong() throws IllegalStateException {
        Number n = getBigDecimal();
        if (n == null) {
            throw new IllegalStateException("!" + JsonEvent.VALUE_NUMBER);
        }
        return n.longValue();
    }

	/**
	 * Advances to the next significant JSON element in the
	 * underlying stream.
	 * 
	 * @throws IOException if 
	 */
	public void next() throws IOException;
	
	/**
	 * Equivalent to calling {@link #next()} followed by
	 * {@link #getEvent()}.
	 * 
	 * @return
	 * @throws IOException
	 */
	default public JsonEvent nextEvent() throws IOException {
	    next();
	    return getEvent();
	}

}