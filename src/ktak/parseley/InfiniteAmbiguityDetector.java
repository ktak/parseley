package ktak.parseley;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;

class InfiniteAmbiguityDetector {
    
    public static <NT,T,R> boolean isInifinitelyAmbiguous(Grammar<NT,T,R> grammar) {
        return axiomsContainLoop(derivationLoopAxioms(grammar));
    }
    
    private static <NT,T,R> boolean axiomsContainLoop(AATreeMap<NT,AATreeSet<NT>> axioms) {
        
        return axioms.sortedKeys().foldRight(
                false,
                (nonTerminal) -> (loopFound) -> loopFound ?
                        loopFound :
                        nonTerminalHasDerivationLoop(
                                nonTerminal, axioms, AATreeSet.emptySet(axioms.getComparator())));
        
    }
    
    private static <NT,T,R> boolean nonTerminalHasDerivationLoop(
            NT nonTerminal, AATreeMap<NT,AATreeSet<NT>> axioms, AATreeSet<NT> visited) {
        
        return axioms.get(nonTerminal).match(
                (none) -> false,
                (set) -> visited.contains(nonTerminal) ?
                        true :
                        set.sortedList().foldRight(
                                false,
                                (child) -> (loopFound) -> loopFound ?
                                        loopFound :
                                        nonTerminalHasDerivationLoop(
                                                child, axioms, visited.insert(nonTerminal))));
        
    }
    
    private static <NT,T,R> AATreeMap<NT,AATreeSet<NT>> derivationLoopAxioms(
            Grammar<NT,T,R> grammar) {
        
        return grammar.rules.mapValues(
                (rules) -> rules.foldRight(
                        AATreeSet.emptySet(grammar.ntCmp),
                        (indexAndRule) -> (axiomSnds) ->
                                axiomSnds.union(
                                        derivationLoopAxiomsForRule(
                                                indexAndRule.right.symbols, grammar))));
        
    }
    
    private static <NT,T,R> AATreeSet<NT> derivationLoopAxiomsForRule(
            RuleSymbols<NT,T,?,?> symbols, Grammar<NT,T,R> grammar) {
        
        return allNonTerminals(symbols).match(
                (none) -> AATreeSet.emptySet(grammar.ntCmp),
                (nonTerminals) -> {
                    int numNTsNotNullable = numberNonTerminalsNotNullable(nonTerminals, grammar);
                    return numNTsNotNullable == 0 ?
                            zeroNonNullableNonTerminals(nonTerminals, grammar) :
                            numNTsNotNullable == 1 ?
                                    oneNonNullableNonTerminal(nonTerminals, grammar) :
                                    AATreeSet.emptySet(grammar.ntCmp);
                });
        
    }
    
    private static <NT,T,R> Option<List<NT>> allNonTerminals(RuleSymbols<NT,T,?,?> symbols) {
        
        return symbols.match(
                (ntSym) -> allNonTerminals(ntSym.next).match(
                        (none) -> none,
                        (nonTerminals) -> Option.some(nonTerminals.cons(ntSym.nonTerminal))),
                (tSym) -> Option.none(),
                (endSym) -> Option.some(new List.Nil<>()));
        
    }
    
    private static <NT,T,R> int numberNonTerminalsNotNullable(
            List<NT> nonTerminals, Grammar<NT,T,R> grammar) {
        
        return nonTerminals.foldRight(
                0,
                (nonTerminal) -> (count) -> grammar.isNullable(nonTerminal) ?
                        count :
                        count+1);
        
    }
    
    private static <NT,T,R> AATreeSet<NT> oneNonNullableNonTerminal(
            List<NT> nonTerminals, Grammar<NT,T,R> grammar) {
        
        return nonTerminals.match(
                (unit) -> AATreeSet.emptySet(grammar.ntCmp),
                (cons) -> grammar.isNullable(cons.left) ?
                        oneNonNullableNonTerminal(cons.right, grammar) :
                        AATreeSet.emptySet(grammar.ntCmp).insert(cons.left));
        
    }
    
    private static <NT,T,R> AATreeSet<NT> zeroNonNullableNonTerminals(
            List<NT> nonTerminals, Grammar<NT,T,R> grammar) {
        
        return nonTerminals.match(
                (unit) -> AATreeSet.emptySet(grammar.ntCmp),
                (cons) -> zeroNonNullableNonTerminals(cons.right, grammar).insert(cons.left));
        
    }
    
}
