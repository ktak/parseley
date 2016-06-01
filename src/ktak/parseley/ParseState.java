package ktak.parseley;

import ktak.immutablejava.AATreeMap;

public class ParseState<NT,T,R> {
    
    private final Parser<NT,T,R> parser;
    private final long nextIndex;
    private final ScanCandidates<NT,T,R> scanCandidates;
    private final AATreeMap<Long,StateSet<NT,T,R>> chart;
    
    protected ParseState(
            Parser<NT,T,R> parser,
            long nextIndex,
            ScanCandidates<NT,T,R> scanCandidates,
            AATreeMap<Long,StateSet<NT,T,R>> chart) {
        this.parser = parser;
        this.nextIndex = nextIndex;
        this.scanCandidates = scanCandidates;
        this.chart = chart;
    }
    
    public ParseState<NT,T,R> parseNextTerminal(T nextTerminal) {
        return parser.parseNextTerminal(
                nextTerminal, nextIndex, scanCandidates, chart);
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
    
}
