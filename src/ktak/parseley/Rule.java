package ktak.parseley;

public class Rule<NT,T,R,Type extends RuleType<NT,T,R,?>> {
    
    protected final RuleSymbols<NT,T,R,Type> symbols;
    
    private Rule(
            RuleSymbols<NT,T,R,Type> symbols) {
        this.symbols = symbols;
    }
    
    public static <NT,T,R,Type extends RuleType<NT,T,R,?>> Rule<NT,T,R,Type> newRule(
            RuleSymbols<NT,T,R,Type> symbols) {
        return new Rule<>(symbols);
    }
    
}
