## Compile and Install

* Amend `build.xml` to create `jsonpp-${VERSION}.jar`.

  - read version from VERSION.txt?
  - pull in latest codepoint.jar?


## API

* Use `java.util.ServiceLoader` to load a factory.

* Remove API dependency on CodePoint?

* Spawn a sub-parser / sub-emitter to hand off to another module.
  E.g. in JSON-RPC one part of the code handles the envelope, another
  processes the procedure and arguments.


## Internals

* Create alternate `JsonLexer` that uses regular expressions.

* Define configurable properties on `JsonParserFactory`.

* Option to throw exceptions instead of `SYNTAX_ERROR`.

* Option for "streaming mode" containing multiple JSON objects, 
  e.g. [chunked transfer encoding](https://en.wikipedia.org/wiki/Chunked_transfer_encoding)
  or streaming asynchronous events through a HTTP Response that never ends.

* Profile existing implementation(s) (with what?) to minimize object creation
  and other performance bottlenecks.


## Tests

* Make tests more readable and writable. E.g. feed bits of JSON to a MockReader
  just before testing they were parsed correctly.

* Real tests of JsonBuilder, etc.

* Test error conditions.

* Improve error reporting.

* Track position in input stream / char stream (absolute, row, column).

  - units? bytes? chars? code points?

* Hooks to internationalize error messages.

* Performance tests:

  - between alternate implementations.

  - against default JSONP implementations.

  - vs. ANTLR parser (callback-based)


## Documentation

* add example code, including a general Maps and Lists builder.

* refer to said example in README.md

