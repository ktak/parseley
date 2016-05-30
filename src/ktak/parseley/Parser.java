package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.Tuple;

public class Parser<NT,T> {
    
    private static final Comparator<Long> longCmp = (l1, l2) -> l1.compareTo(l2);
    protected final Grammar<NT,T> grammar;
    
    public Parser(Grammar<NT,T> grammar) {
        this.grammar = grammar;
    }
    
    public ParseState<NT,T> initialParseState() {
        
        AATreeMap<Long,StateSet<NT,T>> chart = AATreeMap.emptyMap(longCmp);
        Tuple<StateSet<NT,T>,ScanCandidates<NT,T>> init =
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
    
    protected ParseState<NT,T> parseNextTerminal(
            T nextTerminal,
            long index,
            ScanCandidates<NT,T> scanCandidates,
            AATreeMap<Long,StateSet<NT,T>> chart) {
        
        Tuple<StateSet<NT,T>,ScanCandidates<NT,T>> next =
                predictAndComplete(
                        scan(nextTerminal, scanCandidates, chart),
                        index,
                        chart,
                        new ScanCandidates<>(grammar.tCmp));
        
        return new ParseState<>(
                this,
                index+1,
                next.right,
                chart.insert(index, next.left));
        
    }
    
    private StateSet<NT,T> initialStateSet() {
        
        return grammar.rules.get(grammar.start).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (rules) -> rules.foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (rule) -> (stateSet) ->
                                stateSet.add(Item.initialItem(
                                        grammar.start, rule.left, rule.right.rhs, 0L))));
        
    }
    
    private Tuple<StateSet<NT,T>,ScanCandidates<NT,T>> predictAndComplete(
            StateSet<NT,T> stateSet,
            long index,
            AATreeMap<Long,StateSet<NT,T>> stateSets,
            ScanCandidates<NT,T> scanItems) {
        
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
    
    private StateSet<NT,T> predict(
            StateSet<NT,T> stateSet, PredictItem<NT,T> item, long index) {
        
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
                                        rule.right.rhs,
                                        index))));
        
    }
    
    private StateSet<NT,T> complete(
            StateSet<NT,T> stateSet,
            CompleteItem<NT,T> item,
            long index,
            AATreeMap<Long,StateSet<NT,T>> stateSets) {
        
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
    
    private StateSet<NT,T> scan(
            T terminal,
            ScanCandidates<NT,T> scanItems,
            AATreeMap<Long,StateSet<NT,T>> stateSets) {
        
        return scanItems.scanCandidates.get(terminal).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (list) -> list.foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (scanItem) -> (states) -> states.add(scanItem.shift())));
        
    }
    
}
