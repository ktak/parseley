package ktak.parseley;

import ktak.immutablejava.AATreeMap;

public class ParseState<NT,T> {
    
    private final Parser<NT,T> parser;
    private final long nextIndex;
    private final ScanCandidates<NT,T> scanCandidates;
    private final AATreeMap<Long,StateSet<NT,T>> chart;
    
    protected ParseState(
            Parser<NT,T> parser,
            long nextIndex,
            ScanCandidates<NT,T> scanCandidates,
            AATreeMap<Long,StateSet<NT,T>> chart) {
        this.parser = parser;
        this.nextIndex = nextIndex;
        this.scanCandidates = scanCandidates;
        this.chart = chart;
    }
    
    public ParseState<NT,T> parseNextTerminal(T nextTerminal) {
        return parser.parseNextTerminal(
                nextTerminal, nextIndex, scanCandidates, chart);
    }
    
    public boolean recognized() {
        
        return chart.get(nextIndex-1).match(
                (unit) -> { throw new RuntimeException(); },
                (states) -> states.completeItems(parser.grammar.start).sortedList()
                    .foldRight(
                            false,
                            (item) -> (recognized) ->
                                recognized || (item.startIndex == 0)));
        
    }
    
}
