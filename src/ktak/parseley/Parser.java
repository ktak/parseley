package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.Tuple;

public class Parser<NT,T> {
    
    private static final Comparator<Long> longCmp = (l1, l2) -> l1.compareTo(l2);
    private final ItemComparator<NT,T> itemCmp;
    protected final Grammar<NT,T> grammar;
    
    public Parser(Grammar<NT,T> grammar) {
        this.itemCmp = new ItemComparator<NT,T>(grammar.ntCmp, grammar.tCmp);
        this.grammar = grammar;
    }
    
    public ParseState<NT,T> initialParseState() {
        
        AATreeMap<Long,StateSet<NT,T>> chart = AATreeMap.emptyMap(longCmp);
        Tuple<StateSet<NT,T>,ScanCandidates<NT,T>> init =
                predictAndComplete(
                        initialStateSet(),
                        0,
                        chart,
                        new ScanCandidates<>(grammar.tCmp, itemCmp.scanCmp));
        
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
                        new ScanCandidates<>(grammar.tCmp, itemCmp.scanCmp));
        
        return new ParseState<>(
                this,
                index+1,
                next.right,
                chart.insert(index, next.left));
        
    }
    
    private StateSet<NT,T> initialStateSet() {
        
        return grammar.rules.get(grammar.start).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (rules) -> rules.sortedList().foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (rhs) -> (stateSet) -> stateSet.add(Item.item(grammar.start, rhs.rhs, 0L))));
        
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
                                predict(tup.right, predictItem.nextNonTerminal, index),
                                index,
                                stateSets,
                                scanItems),
                        (scanItem) -> predictAndComplete(
                                tup.right,
                                index,
                                stateSets,
                                scanItems.add(scanItem)),
                        (completeItem) -> predictAndComplete(
                                complete(tup.right, completeItem, stateSets),
                                index,
                                stateSets,
                                scanItems)));
                
    }
    
    private StateSet<NT,T> predict(
            StateSet<NT,T> stateSet, NT nonTerminal, long index) {
        
        return grammar.rules.get(nonTerminal).match(
                (unit) -> stateSet,
                (rules) -> rules.sortedList().foldRight(
                        stateSet,
                        (rhs) -> (states) -> states.add(
                                Item.item(nonTerminal, rhs.rhs, index))));
        
    }
    
    private StateSet<NT,T> complete(
            StateSet<NT,T> stateSet,
            CompleteItem<NT,T> item,
            AATreeMap<Long,StateSet<NT,T>> stateSets) {
        
        return stateSets.get(item.startIndex).match(
                (unit) -> { throw new RuntimeException(); },
                (previousStateSet) -> previousStateSet.predictItems(item.lhs)
                .sortedList().foldRight(
                        stateSet,
                        (predictItem) -> (states) -> states.add(predictItem.shift())));
        
    }
    
    private StateSet<NT,T> scan(
            T terminal,
            ScanCandidates<NT,T> scanItems,
            AATreeMap<Long,StateSet<NT,T>> stateSets) {
        
        return scanItems.scanCandidates.get(terminal).match(
                (unit) -> new StateSet<>(grammar.ntCmp, grammar.tCmp),
                (set) -> set.sortedList().foldRight(
                        new StateSet<>(grammar.ntCmp, grammar.tCmp),
                        (scanItem) -> (states) -> states.add(scanItem.shift())));
        
    }
    
}
