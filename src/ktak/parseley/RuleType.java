package ktak.parseley;

public abstract class RuleType<NT,T,R,Next extends RuleType<NT,T,R,?>> {
    
    public static abstract class NonTerminal<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleType<NT,T,R,Next> {
        
    }
    
    public static abstract class Terminal<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleType<NT,T,R,Next> {
        
    }
    
    public static abstract class End<NT,T,R> extends RuleType<NT,T,R,End<NT,T,R>> {
        
    }
    
}
