package ktak.parseley;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.List;

public class ParseState<NT,T,R> {
    
    private final Parser<NT,T,R> parser;
    private final long nextIndex;
    private final ScanCandidates<NT,T,R> scanCandidates;
    private final AATreeMap<Long,StateSet<NT,T,R>> chart;
    private final AATreeMap<Long,T> input;
    
    protected ParseState(
            Parser<NT,T,R> parser,
            long nextIndex,
            ScanCandidates<NT,T,R> scanCandidates,
            AATreeMap<Long,StateSet<NT,T,R>> chart) {
        this.parser = parser;
        this.nextIndex = nextIndex;
        this.scanCandidates = scanCandidates;
        this.chart = chart;
        this.input = AATreeMap.emptyMap((l1, l2) -> l1.compareTo(l2));
    }
    
    protected ParseState(
            Parser<NT,T,R> parser,
            long nextIndex,
            ScanCandidates<NT,T,R> scanCandidates,
            AATreeMap<Long,StateSet<NT,T,R>> chart,
            AATreeMap<Long,T> input) {
        this.parser = parser;
        this.nextIndex = nextIndex;
        this.scanCandidates = scanCandidates;
        this.chart = chart;
        this.input = input;
    }
    
    public ParseState<NT,T,R> parseNextTerminal(T nextTerminal) {
        return parser.parseNextTerminal(
                nextTerminal, nextIndex, scanCandidates, chart, input);
    }
    
    public boolean recognized() {
        
        return chart.get(nextIndex-1).match(
                (unit) -> { throw new RuntimeException(); },
                (states) -> states.completeItems(parser.grammar.start)
                    .foldRight(
                            false,
                            (item) -> (recognized) ->
                                recognized || (item.startIndex == 0)));
        
    }
    
    public List<R> results() {
        
    	return createRuleIndex(parser.grammar, chart, input)
    	        .buildResults(parser.grammar.start, 0, nextIndex-1);
    	
    }
    
    private CompletedRuleIndex<NT,T,R> createRuleIndex(
            Grammar<NT,T,R> grammar,
            AATreeMap<Long,StateSet<NT,T,R>> chart,
            AATreeMap<Long,T> input) {
        
        return chart.sortedKeyValPairs().foldRight(
                new CompletedRuleIndex<>(input, grammar.tCmp),
                (endIndexStateSet) -> (ruleIndex1) ->
                endIndexStateSet.right.completeItems().sortedKeyValPairs().foldRight(
                        ruleIndex1,
                        (lhsCompleteItems) -> (ruleIndex2) ->
                        lhsCompleteItems.right.foldRight(
                                ruleIndex2,
                                (completeItem) -> (ruleIndex3) ->
                                ruleIndex3.addCompletedRule(completeItem, endIndexStateSet.left, grammar))));
        
    }
    
}
