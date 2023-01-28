# Introduction #

**JSON Pull Parser** a.k.a. JSONPP parses [JSON](http://json.org) input.

*But we already have [JSONP](https://projects.eclipse.org/projects/ee4j.jsonp)!*

At the time I started this, I didn't know about JSONPP. Even if I had, though
I'd still have written it. Partly it's an exercise to waken my rusty coding skills,
but JSONPP has a different design and different goals from JSONP.


*So what are the differences?*

* JSONPP is most comparable to `javax.json.stream.JsonParser`. However, I'd guess
  that the API in `javax.json` came first, and is probably what most people use.
  JSONPP is meant for all users, and ideally is the only API one needs.

* If I've done my job, JSONPP creates as few temporary objects as possible.
  This is a boon for environments with strict memory constraints.

* Unlike JSONP's `JsonParser`, JSONPP separates the method that advances to the 
  next event from the method that provides the event type. One can pass a 
  `JsonPullParser` instance to other methods by itself. As long as no client 
  calls `next()`, it will preserve all state of the last event.

* Internally JSONP deals directly with code points. JSONP, as far as I can tell,
  sticks with `char`s and surrogate pairs.

* JSONPP flags errors with a `SYNTAX_ERROR` event instead of throwing exceptions.
  This avoids the overhead of exceptions, again for memory-limited and real-time
  applications.  The caller can always throw their own if they want.

* JSONPP runs under Java 8. JSONP is targeted for Java 9+, although I can compile
  the API under Java 8 (sans module definitions).


## BUILDING JSONPP ###

The project includes project files for both Eclipse and NetBeans. Fire up your
favorite IDE (if it's one of those two) and go to town. *Or* just run `ant`.

It's a bunch of `.java` files. It's not that hard.


## INSTALLING JSONPP ##

Simply put jsonpp.jar (or jsonpp-*version*.jar) in your CLASSPATH somewhere.


## USING JSONPP ##

The `com.frank_mitchell.jsonpp` package includes the public API,
while `com.frank_mitchell.jsonpp.spi` contains the implemenation(s).

JSONPP is a "pull parser" because instead of providing input and a callback
interface like SAX parsers, the caller pulls new tokens or events from the input at
every call to `next()`. Thus one method can pass a `JsonPullParser` instance to
another method.

For example, let's say a caller wants to create a `PurchaseOrder` instance containing
one or more `PurchaseOrderItem` instances.  A developer can do something like this:

        JsonEvent event;
        java.beans.PropertyDescriptor prop;
        PurchaseOrder order = new PurchaseOrder();

        parser.next();

        if (parser.getEvent() != OBJECT_START) {
           throw new IOException("invalid input");
        }

        parser.next();
        event = parser.getEvent();

        while (!(event == END_STREAM || event == SYNTAX_ERROR)) {

            switch (event) {
            case KEY_NAME:
                prop = lookupPurchaseOrderPropertyForKey(parser.getCurrentKey());
                break;
            case VALUE_TRUE:
                prop.invoke(order, Boolean.TRUE);
            case VALUE_FALSE:
                prop.invoke(order, Boolean.FALSE);
            case VALUE_NULL:
                prop.invoke(order, null);
            case VALUE_STRING:
                prop.invoke(order, parser.getString());
            case VALUE_NUMBER:
                prop.invoke(order, parser.getNumber());
                break;
            case ARRAY_START:
                readOrderItemArray(parser); // calls readOrderItem(parser) in loop
                assert (getEvent() == ARRAY_END);
                break;
            case OBJECT_END:
                checkOrder(order); // checks all necessary properties set.
                break;
            default:
                throw new IOException("invalid input");
            }

            parser.next();
            event = parser.getEvent();
        }

Obviously this code needs more error checking and better error reporting.
It's also not too different from what one may write using JSONP's `JsonParser`.


