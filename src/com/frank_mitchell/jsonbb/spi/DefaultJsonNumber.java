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

/**
 *
 * @author fmitchell
 */
class DefaultJsonNumber extends DefaultJsonBaseValue {
    
    final Number _value;

    DefaultJsonNumber(Number v) {
        _value = v;
    }

    @Override
    public void writeTo(StringBuilder builder) {
        appendJsonNumber(builder, _value, "null");
    }
    
    public static void appendJsonNumber(StringBuilder buf, Number n, String alt) {
        // JSON spec:
        //     number ::= whole frac? exp?
        // where
        //     whole ::= ( ("-")? ( 0 | [1-9][0-9]* ) )
        //     frac  ::= ( '.' [0-9]* )
        //     exp   ::= ( ( "e"|"E" ) ("+"|"-")? [0-9][0-9]* )
        //
        // Meanwhile we have this:
        if (n == null) {
            buf.append(alt);
        } else if (Double.isInfinite(n.doubleValue()) 
                || Double.isNaN(n.doubleValue())) {
            // no provisions for +/-Inf or NaN in the spec, so
            buf.append(alt);
        } else {
            buf.append(n.toString());
        }
    }

}
