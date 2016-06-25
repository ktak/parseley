package ktak.parseley;

public class Rule<NT,T,R,Type extends RuleType<NT,T,R,?>> {
    
    protected final RuleSymbols<NT,T,R,Type> symbols;
    protected final RuleOperation<NT,T,R,Type> operation;
    
    private Rule(
            RuleSymbols<NT,T,R,Type> symbols,
            RuleOperation<NT,T,R,Type> operation) {
        this.symbols = symbols;
        this.operation = operation;
    }
    
    public static <NT,T,R,Type extends RuleType<NT,T,R,?>> Rule<NT,T,R,Type> newRule(
            RuleSymbols<NT,T,R,Type> symbols,
            RuleOperation<NT,T,R,Type> operation) {
        return new Rule<>(symbols, operation);
    }
    
}
