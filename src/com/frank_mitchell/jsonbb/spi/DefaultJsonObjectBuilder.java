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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultJsonObjectBuilder extends DefaultJsonBaseValue implements JsonObjectBuilder {
    
    private final List<String> _keys = new ArrayList<>();
    private final Map<String, JsonValue> _values = new HashMap<>();
    private final JsonBuilder _parent;

    DefaultJsonObjectBuilder(JsonBuilder p) {
        _parent = p;
    }

    DefaultJsonObjectBuilder() {
        _parent = null;
    }

    @Override
    public JsonObjectBuilder setString(String key, String value) {
        setValue(key, new DefaultJsonString(value));
        return this;
    }

    private void setValue(String key, final JsonValue jsonval) {
        if (!_values.containsKey(key)) {
            _keys.add(key);
        }
        _values.put(key, jsonval);
    }

    @Override
    public JsonObjectBuilder setNumber(String key, Number value) {
        setValue(key, new DefaultJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder setBoolean(String key, boolean value) {
        setValue(key, value ? DefaultJsonLiteral.TRUE : DefaultJsonLiteral.FALSE);
        return this;
    }

    @Override
    public JsonObjectBuilder setTrue(String key) {
        setValue(key, DefaultJsonLiteral.TRUE);
        return this;
    }

    @Override
    public JsonObjectBuilder setFalse(String key) {
        setValue(key, DefaultJsonLiteral.FALSE);
        return this;
    }

    @Override
    public JsonObjectBuilder setNull(String key) {
        setValue(key, DefaultJsonLiteral.NULL);
        return this;
    }

    @Override
    public JsonObjectBuilder setNewObject(String key) {
        DefaultJsonObjectBuilder result = new DefaultJsonObjectBuilder(this);
        setValue(key, result);
        return result;
    }

    @Override
    public JsonArrayBuilder setNewArray(String key) {
        DefaultJsonArrayBuilder result = new DefaultJsonArrayBuilder(this);
        setValue(key, result);
        return result;
    }

    @Override
    public JsonBuilder getParent() {
        return _parent;
    }

    @Override
    public void writeTo(StringBuilder builder) {
        builder.append("{");
        for (int i = 0; i < _keys.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            final String key = _keys.get(i);
            DefaultJsonString.appendJsonString(builder, key, "\"\"");
            builder.append(":");
            _values.get(key).writeTo(builder);
        }
        builder.append("}");
    }
    
}
