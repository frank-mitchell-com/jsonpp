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
package com.frank_mitchell.jsonbb.spi;

import com.frank_mitchell.jsonbb.JsonArrayBuilder;
import com.frank_mitchell.jsonbb.JsonBuilder;
import com.frank_mitchell.jsonbb.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fmitchell
 */
public class DefaultJsonArrayBuilder extends DefaultJsonBaseValue implements JsonArrayBuilder {
    
    private final List<JsonValue> _values = new ArrayList<>();
    private final JsonBuilder _parent;

    DefaultJsonArrayBuilder() {
        _parent = null;
    }

    DefaultJsonArrayBuilder(JsonBuilder p) {
        _parent = p;
    }

    @Override
    public JsonArrayBuilder addString(String value) {
        _values.add(new DefaultJsonString(value));
        return this;
    }

    @Override
    public JsonArrayBuilder addNumber(Number value) {
        _values.add(new DefaultJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder addBoolean(boolean value) {
        _values.add(value ? DefaultJsonLiteral.TRUE : DefaultJsonLiteral.FALSE);
        return this;
    }

    @Override
    public JsonArrayBuilder addTrue() {
        _values.add(DefaultJsonLiteral.TRUE);
        return this;
    }

    @Override
    public JsonArrayBuilder addFalse() {
        _values.add(DefaultJsonLiteral.FALSE);
        return this;
    }

    @Override
    public JsonArrayBuilder addNull() {
        _values.add(DefaultJsonLiteral.NULL);
        return this;
    }

    @Override
    public JsonObjectBuilder addNewObject() {
        DefaultJsonObjectBuilder result = new DefaultJsonObjectBuilder(this);
        _values.add(result);
        return result;
    }

    @Override
    public JsonArrayBuilder addNewArray() {
        DefaultJsonArrayBuilder result = new DefaultJsonArrayBuilder(this);
        _values.add(result);
        return result;
    }

    @Override
    public JsonBuilder getParent() {
        return _parent;
    }

    @Override
    public void writeTo(StringBuilder builder) {
        builder.append("[");
        for (int i = 0; i < _values.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            _values.get(i).writeTo(builder);
        }
        builder.append("]");
    }
    
}
