package com.frank_mitchell.jsonpp.spi;

final class CharWrapper implements CharSequence {
    private final int    len;
    private final char[] cbuf;
    private final int    offset;

    CharWrapper(int len, char[] cbuf, int offset) {
        this.len = len;
        this.cbuf = cbuf;
        this.offset = offset;
    }

    @Override
    public char charAt(int index) {
        return cbuf[offset + index];
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int newlen = len + start - end;
        return new CharWrapper(newlen, cbuf, offset + start);
    }
}