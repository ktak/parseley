package ktak.parseley;

class RuleSpan<NT,T,R> {
    
    public final Rule<NT,T,R,?> rule;
    public final long startIndex;
    public final long endIndex;
    
    RuleSpan(Rule<NT,T,R,?> rule, long startIndex, long endIndex) {
        this.rule = rule;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
}
