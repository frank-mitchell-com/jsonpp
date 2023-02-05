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

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * This interface traverses a JSON Value as a stream of events.
 *
 * Each call to {@link #next()} moves to the next event in the stream, and the
 * various "get" methods identify the type of event, the value of a String or
 * Number, and/or the name of a key String.
 *
 * Implementations of this interface aren't guaranteed to be thread safe. In
 * most cases one thread will parse an input stream and then discard this
 * parser. In some cases one thread <strong>might</strong> hand a parser off to
 * another thread, then continue parsing once that thread has finished. (In the
 * latter case a co-routine or cooperative single-threaded framework might be
 * more efficient.)
 *
 * @author Frank Mitchell
 *
 */
public interface JsonPullParser extends Closeable {

    /**
     * Get the event parsed by the most recent call to {@link #next()}.
     *
     * @return most recently parsed event.
     */
    public JsonEvent getEvent();

    /**
     * Indicates if the enclosing value is a JSON Array.
     *
     * If this object is currently processing the contents of a JSON Array, this
     * method will return <code>true</code>.
     *
     * @return <code>true</code> if the enclosing value is a JSON Array.
     *
     * @see #isInObject()
     */
    public boolean isInArray();

    /**
     * Indicates if the enclosing value is a JSON Object.
     *
     * If this parser is currently processing the contents of a JSON Object,
     * this method will return <code>true</code>. If neither this method nor
     * {@link #isInArray()} are true, this parser is either at the start or end
     * of the document, the document contains only an atomic value, or the
     * parser encountered an error.
     *
     * @return <code>true</code> if the enclosing value is a JSON Object.
     */
    public boolean isInObject();

    /**
     * Gets the key associated with the current value.
     *
     * On {@link JsonEvent#KEY_NAME}, the result is the JSON Object key
     * with outer quotes removed and backslash escapes resoved.
     *
     * On {@link JsonEvent#END_OBJECT},
     * {@link JsonEvent#END_ARRAY},
     * {@link JsonEvent#VALUE_STRING},
     * {@link JsonEvent#VALUE_NUMBER},
     * {@link JsonEvent#VALUE_TRUE},
     * {@link JsonEvent#VALUE_FALSE}, or {@link JsonEvent#VALUE_NULL}, the
     * result is the JSON Object key this value should be assigned to, if the
     * enclosing construct is a JSON Object.
     *
     * On {@link JsonEvent#START_STREAM},
     * {@link JsonEvent#START_ARRAY},
     * {@link JsonEvent#START_OBJECT},
     * {@link JsonEvent#END_STREAM}, or {@link JsonEvent#SYNTAX_ERROR} or if
     * there is no immediately enclosing JSON object, this method returns
     * <code>null</code>;
     *
     * @return the value of a String or Number or <code>null</code>
     */
    public String getCurrentKey();

    /**
     * Gets the string value associated with the current event.
     *
     * On {@link JsonEvent#KEY_NAME}, the result is the JSON Object key with all
     * escape sequences converted to their character values.
     *
     * On {@link JsonEvent#VALUE_STRING}, the result is the JSON String with all
     * escape sequences converted to their character values.
     *
     * On a {@link JsonEvent#VALUE_NUMBER}, the result is the number as
     * originally read.
     *
     * Otherwise the method throws an exception
     *
     * @return the value of a String or Number or <code>null</code>
     *
     * @throws IllegalStateException if the current event has no string value.
     */
    public String getString();

    /**
     * Gets the {@link BigDecimal} value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER}, this method
     * returns an unspecified subclass of Number. Otherwise this method throws
     * an exception.
     *
     * @return the value of the current JSON Number
     *
     * @throws IllegalStateException if the current event is not a number.
     */
    public Number getNumber() throws IllegalStateException;

    /**
     * Gets the <code>double</code> value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER}, this method
     * returns an unspecified subclass of Number. Otherwise this method throws
     * an exception.
     *
     * @return the value of the current JSON Number
     *
     * @throws IllegalStateException if the current event is not a number.
     */
    default double getDouble() throws IllegalStateException {
        Number n = getNumber();
        if (n == null) {
            return Double.NaN;
        } else {
            return n.doubleValue();
        }
    }

    /**
     * Gets the <code>int</code> value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER}, this method
     * returns an unspecified subclass of Number. Otherwise this method throws
     * an exception.
     *
     * @return the value of the current JSON Number
     *
     * @throws IllegalStateException if the current event is not a number.
     */
    default int getInt() throws IllegalStateException {
        Number n = getNumber();
        if (n == null) {
            throw new IllegalStateException("!" + JsonEvent.VALUE_NUMBER);
        }
        return n.intValue();
    }

    /**
     * Gets the <code>long</code> value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER}, this method
     * returns an unspecified subclass of Number. Otherwise this method throws
     * an exception.
     *
     * @return the value of the current JSON Number
     *
     * @throws IllegalStateException
     */
    default public long getLong() throws IllegalStateException {
        Number n = getNumber();
        if (n == null) {
            throw new IllegalStateException("!" + JsonEvent.VALUE_NUMBER);
        }
        return n.longValue();
    }

    /**
     * Advances to the next significant JSON element in the underlying stream.
     *
     * @throws IOException if the character source could not be read.
     */
    public void next() throws IOException;

    /**
     * Equivalent to calling {@link #next()} followed by {@link #getEvent()}.
     *
     * @return most recently parsed event.
     *
     * @throws IOException if the character source could not be read
     */
    default public JsonEvent nextEvent() throws IOException {
        next();
        return getEvent();
    }

    /**
     * Close the underlying IO or NIO object.
     *
     * @throws IOException from the underlying object.
     */
    @Override
    void close() throws IOException;
}
