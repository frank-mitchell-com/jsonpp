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
package com.frank_mitchell.jsonpp.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.frank_mitchell.jsonpp.JsonPushProducer;
import com.frank_mitchell.jsonpp.JsonPushProducerFactory;

/**
 *
 * @author fmitchell
 */
public abstract class AbstractJsonPushProducerFactory implements JsonPushProducerFactory {
    
    @Override
    public JsonPushProducer createProducer(Writer writer) throws IOException {
        return createProducer(new WriterSink(writer));
    }

    @Override
    public JsonPushProducer createProducer(OutputStream out, Charset enc) throws IOException {
        return createProducer(new WriterSink(new OutputStreamWriter(out, enc)));
    }

    @Override
    public JsonPushProducer createUtf8Producer(OutputStream out) throws IOException {
        return createProducer(out, StandardCharsets.UTF_8);
    }
    
}
