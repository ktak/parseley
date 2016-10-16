# parseley
parseley is an Earley parser written in java that works with all context-free
grammars that are not infinitely ambiguous. it also statically enforces that
each operation to build the parse tree matches the structure of the
corresponding grammar rule. the references used are the
[Earley parser](https://en.wikipedia.org/wiki/Earley_parser) wikipedia page,
[this](http://loup-vaillant.fr/tutorials/earley-parsing/) very hepful website,
and [this](http://cs.stackexchange.com/questions/40965/cfgs-detecting-infinitely-many-derivations-of-a-single-string)
description of an algorithm for detecting infinite ambiguity in a grammar. for
an example of how to construct a parser see
[test/ktak/parseley/AmbiguousGrammarTest.java](https://github.com/ktak/parseley/blob/master/test/ktak/parseley/AmbiguousGrammarTest.java).
the only library that this project depends on is
[immutable-java](https://github.com/ktak/immutable-java)
