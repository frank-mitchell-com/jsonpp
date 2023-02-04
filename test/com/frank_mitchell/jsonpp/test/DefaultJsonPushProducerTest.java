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
package com.frank_mitchell.jsonpp.test;

import com.frank_mitchell.jsonpp.JsonEvent;
import com.frank_mitchell.jsonpp.JsonPushProducer;
import com.frank_mitchell.jsonpp.JsonPushProducerFactory;
import com.frank_mitchell.jsonpp.spi.DefaultJsonPushProducerFactory;
import java.io.CharArrayWriter;
import java.io.IOException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author fmitchell
 */
public class DefaultJsonPushProducerTest {
    
    JsonPushProducerFactory _factory;
    CharArrayWriter _writer;
    JsonPushProducer _producer;
    
    @Before
    public void setUp() throws Exception {
        _factory = new DefaultJsonPushProducerFactory();
        _writer = new CharArrayWriter();
        _producer = _factory.createProducer(_writer);
    }
    
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimpleArray() throws IOException {
        _producer.setEvent(JsonEvent.START_ARRAY);
        _producer.push();
        _producer.setEvent(JsonEvent.END_ARRAY);
        _producer.push();
        
        assertEquals("[]", _writer.toString());
    }    
}
