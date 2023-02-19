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

* Internally JSONPP deals directly with code points. JSONP, as far as I can tell,
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

(TODO: Sensible code example.)
