## Compile and Install

* Create `build.xml` to run tests and create `jsonpp-${VERSION}.jar`.
  (Independent of Eclipse, NetBeans, etc.)

  - read version from VERSION.txt

* Use maven instead?


## API

* Spawn a sub-parser / sub-emitter to hand off to another module.
  E.g. in JSON-RPC one part of the code handles the envelope, another
  processes the procedure and arguments.

* Add AbstractJson...Factory to the public API?


## Internals

* Rewrite the current Lexer to *NOT* call `getCodePoint()` until after it calls
  `next()`, so we don't have to load up a first character at the start.

* Make tracking the current key a/or index optional, to save time and space.

* Create alternate `JsonLexer` that uses regular expressions.

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

* put JSONP API implementation into separate jar?


## Documentation

* add example code, including a general Maps and Lists builder.
