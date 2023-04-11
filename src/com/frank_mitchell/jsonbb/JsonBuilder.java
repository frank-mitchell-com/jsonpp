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

import com.frank_mitchell.codepoint.CodePointSink;
import java.io.IOException;
import java.io.Writer;

/**
 * Common methods for objects to build syntactically correct JSON values.
 *
 * @author Frank Mitchell
 */
public interface JsonBuilder {
    /**
     * Whether this object is a JSON Array.
     *
     * @return whether this object is a JSON Array
     */
    boolean isArray();
    
    /**
     * Whether this object is a JSON Object.
     *
     * @return whether this object is a JSON Object
     */
    boolean isObject();
    
    /**
     * The Builder that contains this instance.
     *
     * @return the parent of this instance.
     */
    JsonBuilder getParent();
    
    /**
     * Write this object's current value to a string.
     *
     * @param builder the StringBuilder to be written to.
     */
    void writeTo(StringBuilder builder);
    
    /**
     * Write this object's current value to a Writer.
     *
     * @param writer the Writer to be written to.
     * @throws IOException if the writer throws an exception
     */
    void writeTo(Writer writer) throws IOException;

    /**
     * Write this object's current value to a CodePointSink.
     *
     * @param sink the CodePointSink to be written to.
     * @throws IOException if the sink throws an exception
     */
    void writeTo(CodePointSink sink) throws IOException;
}
