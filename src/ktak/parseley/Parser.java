package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

public class Parser<NT,T,R> {
    
    private static final Comparator<Long> longCmp = (l1, l2) -> l1.compareTo(l2);
    protected final Grammar<NT,T,R> grammar;
    
    private Parser(Grammar<NT,T,R> grammar) {
        this.grammar = grammar;
    }
    
    public static <NT,T,R> Option<Parser<NT,T,R>> createParser(Grammar<NT,T,R> grammar) {
        return InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar) ?
                Option.none() : Option.some(new Parser<>(grammar));
    }
    
    public ParseState<NT,T,R> initialParseState() {
        
        AATreeMap<Long,StateSet<NT,T,R>> chart = AATreeMap.emptyMap(longCmp);
        Tuple<StateSet<NT,T,R>,ScanCandidates<NT,T,R>> init =
                predictAndComplete(
                        initialStateSet(),
                        0,
                        chart,
                        new ScanCandidates<>(grammar.tCmp));
        
        return new ParseState<>(
                this,
                1,
                init.right,
                chart.insert(0L, init.left));
        
    }
    
    protected ParseState<NT,T,R> parseNextTerminal(
            T nextTerminal,
            long index,
            ScanCandidates<NT,T,R> scanCandidates,
            AATreeMap<Long,StateSet<NT,T,R>> chart,
            AATreeMap<Long,T> input) {
        
        Tuple<StateSet<NT,T,R>,ScanCandidates<NT,T,R>> next =
                predictAndComplete(
                        scan(nextTerminal, scanCandidates, chart),
                        index,
                        chart,
                        new ScanCandidates<>(grammar.tCmp));
        
        return new ParseState<>(
                this,
                index+1,
                next.right,
                chart.insert(index, next.left),
                input.insert(index-1, nextTerminal));
        
    }
    
    private StateSet<NT,T,R> initialStateSet() {
        
        return grammar.rules.get(grammar.start).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (rules) -> rules.foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (rule) -> (stateSet) ->
                                stateSet.add(Item.initialItem(
                                        grammar.start, rule.left, rule.right.symbols, 0L))));
        
    }
    
    private Tuple<StateSet<NT,T,R>,ScanCandidates<NT,T,R>> predictAndComplete(
            StateSet<NT,T,R> stateSet,
            long index,
            AATreeMap<Long,StateSet<NT,T,R>> stateSets,
            ScanCandidates<NT,T,R> scanItems) {
        
        return stateSet.nextItem().match(
                (unit) -> Tuple.create(stateSet, scanItems),
                (tup) -> tup.left.match(
                        (predictItem) -> predictAndComplete(
                                predict(tup.right, predictItem, index),
                                index,
                                stateSets,
                                scanItems),
                        (scanItem) -> predictAndComplete(
                                tup.right,
                                index,
                                stateSets,
                                scanItems.add(scanItem)),
                        (completeItem) -> predictAndComplete(
                                complete(tup.right, completeItem, index, stateSets),
                                index,
                                stateSets,
                                scanItems)));
        
    }
    
    private StateSet<NT,T,R> predict(
            StateSet<NT,T,R> stateSet, PredictItem<NT,T,R> item, long index) {
        
        return grammar.rules.get(item.nextNonTerminal).match(
                (unit) -> grammar.isNullable(item.nextNonTerminal) ?
                        stateSet.add(item.shift()) :
                        stateSet,
                (rules) -> rules.foldRight(
                        grammar.isNullable(item.nextNonTerminal) ?
                        stateSet.add(item.shift()) :
                        stateSet,
                        (rule) -> (states) -> states.add(
                                Item.initialItem(
                                        item.nextNonTerminal,
                                        rule.left,
                                        rule.right.symbols,
                                        index))));
        
    }
    
    private StateSet<NT,T,R> complete(
            StateSet<NT,T,R> stateSet,
            CompleteItem<NT,T,R> item,
            long index,
            AATreeMap<Long,StateSet<NT,T,R>> stateSets) {
        
        return index == item.startIndex ?
                stateSet.predictItems(item.leftHandSide)
                .foldRight(
                        stateSet,
                        (predictItem) -> (states) -> states.add(predictItem.shift())) :
                stateSets.get(item.startIndex).match(
                        (unit) -> { throw new RuntimeException(); },
                        (previousStateSet) -> previousStateSet.predictItems(item.leftHandSide)
                                .foldRight(
                                        stateSet,
                                        (predictItem) -> (states) ->
                                                states.add(predictItem.shift())));
        
    }
    
    private StateSet<NT,T,R> scan(
            T terminal,
            ScanCandidates<NT,T,R> scanItems,
            AATreeMap<Long,StateSet<NT,T,R>> stateSets) {
        
        return scanItems.scanCandidates.get(terminal).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (list) -> list.foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (scanItem) -> (states) -> states.add(scanItem.shift())));
        
    }
    
}
