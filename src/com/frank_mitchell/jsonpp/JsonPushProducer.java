package com.frank_mitchell.jsonpp;

import java.io.IOException;

/**
 * Instances of this class write JSON to an IO object. The application sets the
 * event they want to write, adds necessary information like a string for a JSON
 * Object key or the string or number value to write. Then they call [@link
 * #push()} to write the event, and repeat until they
 *
 * @author Frank Mitchell
 */
public interface JsonPushProducer {

    /**
     * Set the event to be emitted on the next {@link #push()}
     *
     * @param event the event to write.
     */
    public void setEvent(JsonEvent event);

    /**
     * Get the event set by {@link #setEvent(JsonEvent)} after the previous
     * {@link push()}. If the user set no event, the implementation should
     * either set itself to the next logical event or simply return
     * {@link JsonEvent#SYNTAX_ERROR}.
     *
     * @return the current event
     */
    public JsonEvent getEvent();

    /**
     * The string corresponding to the JSON value to be written out.
     *
     * If {@link #getEvent()} is {@link JsonEvent#START_ARRAY},
     * {@link JsonEvent#END_ARRAY}, {@link JsonEvent#START_OBJECT},
     * or {@link JsonEvent#END_OBJECT},
     * this method returns the appropriate brace or bracket,
     * <strong>not</strong> the whole object. On {@link JsonEvent#SYNTAX_ERROR),
     * {@link JsonEvent#START_STREAM} or
     * {@link JsonEvent#END_STREAM}
     * this method will throw an exception.
     *
     * @return
     */
    public String getJsonValue() throws IllegalStateException;

    /**
     * Sets the string value associated with the current event.
     *
     * If {@link #getEvent()} is not {@link JsonEvent#VALUE_STRING)
     * or {@link JsonEvent#KEY_NAME} this method throws an exception.
     *
     *
     * @param value the value of a JSON String or Object key.
     *
     * @throws IllegalStateException if the current event is not a string.
     */
    public void setString(String value) throws IllegalStateException;

    /**
     * Sets the {@link Number} value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER} this is the
     * number to be written out. If the event is {@link JsonEvent#VALUE_KEY}, or {@link JsonEvent#VALUE_STRING),
     * Otherwise this method throws an exception.
     *
     * @param value the value of a JSON String or Object key.
     *
     * @throws IllegalStateException if the current event is not a number or string.
     */
    public void setNumber(Number value) throws IllegalStateException;

    /**
     * Sets the {@literal double} value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER} this is the
     * number to be written out. If the event is {@link JsonEvent#VALUE_KEY}, or {@link JsonEvent#VALUE_STRING),
     * Otherwise this method throws an exception.
     *
     * @param value the value of a JSON String or Object key.
     *
     * @throws IllegalStateException if the current event is not a number or string.
     */
    default void setDouble(double value) throws IllegalStateException {
        setNumber(value);
    }

    /**
     * Sets the {@literal int} value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER} this is the
     * number to be written out. If the event is {@link JsonEvent#VALUE_KEY}, or {@link JsonEvent#VALUE_STRING),
     * Otherwise this method throws an exception.
     *
     * @param value the value of a JSON String or Object key.
     *
     * @throws IllegalStateException if the current event is not a number or string.
     */
    default void setInt(int value) throws IllegalStateException {
        setNumber(value);
    }

    /**
     * Sets the {@literal long} value associated with the current event.
     *
     * If {@link #getEvent()} is {@link JsonEvent#VALUE_NUMBER} this is the
     * number to be written out. If the event is {@link JsonEvent#VALUE_KEY}, or {@link JsonEvent#VALUE_STRING),
     * Otherwise this method throws an exception.
     *
     * @param value the value of a JSON String or Object key.
     *
     * @throws IllegalStateException if the current event is not a number or string.
     */
    default void setLong(long value) throws IllegalStateException {
        setNumber(value);
    }

    /**
     * Write the current event and associated values.
     *
     * @throws IOException if the I/O object encounters an error.
     * @throws IllegalStateException if this object lacks some information.
     */
    public void push() throws IOException, IllegalStateException;

    /**
     * Flush the underlying I/O stream.
     *
     * @throws IOException if the I/O object encounters an error.
     * @throws IllegalStateException if the JSON text isn't complete.
     * @see Writer#flush()
     */
    public void flush() throws IOException;

    /**
     * Close the underlying I/O stream.
     *
     * @throws IOException if the I/O object encounters an error.
     * @see Writer#close()
     */
    public void close() throws IOException, IllegalStateException;

    /**
     * A convenience method to close all open Arrays and Objects and write the
     * end of the stream.
     * @throws java.io.IOException if the I/O stream encounters an error.
     * @throws IllegalStateException if this object lacks some information.
     */
    public void finishStream() throws IOException, IllegalStateException;

}
