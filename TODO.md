## Compile and Install

* Create `build.xml` to run tests and create `jsonpp-${VERSION}.jar`.
  (Independent of Eclipse, NetBeans, etc.)

  - read version from VERSION.txt

* Use maven instead?


## API

* Add Source and AbstractJsonPullParserFactory to the public API?

* build `JsonEmitter` that writes events as JSON to Writer, OutputStream, 
  or CharBuffer.

  - complementary "Sink" interface to wrap Writers and OutputStreams.

  - use javax.json.stream.JsonGenerator for inspiration.

  - API to complete one or all open brackets / braces.

  - pretty printing?

  - ByteSink to write UTF-8 directly without Writer?


## Internals

* Make tracking the current key a/or index optional, to save time and space.

* Create alternate `JsonLexer` that uses regular expressions.

* Create `UtfInputStreamSource` to convert UTF-8 bytes directly into code points.

* Define configurable properties on `JsonParserFactory`.

* Option to throw exceptions instead of SYNTAX_ERROR.

* Option for "streaming mode" containing multiple JSON objects, 
  e.g. [chunked transfer encoding](https://en.wikipedia.org/wiki/Chunked_transfer_encoding)
  or streaming asynchronous events through a HTTP Response that never ends.


## Tests

* Make tests more readable and writable. E.g. feed bits of JSON to a MockReader
  just before testing they were parsed correctly.

* Test error conditions.

* Improve error reporting.

* Track position in input stream / char stream (absolute, row, column).

  - units? bytes? chars? code points?

* Hooks to internationalize error messages.

* Performance tests:

  - between alternate implementations.

  - against default JSONP implementations.

  - vs. ANTLR parser (callback-based)


## Compatibility

* create `JsonProvider` and wrap with whole JSONP API.

* put JSONP API into separate jar?

* add JSONP API into one big jar?


## Documentation

* add example code, including a general Maps and Lists builder.

* refer to said example in README.md

