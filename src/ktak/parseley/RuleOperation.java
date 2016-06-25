package ktak.parseley;

import ktak.immutablejava.Function;
import ktak.parseley.RuleType.End;
import ktak.parseley.RuleType.NonTerminal;
import ktak.parseley.RuleType.Terminal;

abstract class RuleOperation<NT,T,R,Type extends RuleType<NT,T,R,?>> {
    
    protected abstract <X> X match(
            Function<NonTerminalOperation<NT,T,R,?>,X> nonTerminalCase,
            Function<TerminalOperation<NT,T,R,?>,X> terminalCase,
            Function<EndOperation<NT,T,R>,X> endCase);
    
    protected static class NonTerminalOperation<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleOperation<NT,T,R,NonTerminal<NT,T,R,Next>> {
        
        protected final Function<R,Next> operation;
        
        protected NonTerminalOperation(Function<R,Next> operation) {
            this.operation = operation;
        }
        
        @Override
        protected <X> X match(
                Function<NonTerminalOperation<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalOperation<NT,T,R,?>,X> terminalCase,
                Function<EndOperation<NT,T,R>,X> endCase) {
            return nonTerminalCase.apply(this);
        }
        
    }
    
    protected static class TerminalOperation<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleOperation<NT,T,R,Terminal<NT,T,R,Next>> {
        
        protected final Function<T,Next> operation;
        
        protected TerminalOperation(Function<T,Next> operation) {
            this.operation = operation;
        }
        
        @Override
        protected <X> X match(
                Function<NonTerminalOperation<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalOperation<NT,T,R,?>,X> terminalCase,
                Function<EndOperation<NT,T,R>,X> endCase) {
            return terminalCase.apply(this);
        }
        
    }
    
    protected static class EndOperation<NT,T,R> extends RuleOperation<NT,T,R,End<NT,T,R>> {
        
        protected final R result;
        
        protected EndOperation(R result) {
            this.result = result;
        }
        
        @Override
        protected <X> X match(
                Function<NonTerminalOperation<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalOperation<NT,T,R,?>,X> terminalCase,
                Function<EndOperation<NT,T,R>,X> endCase) {
            return endCase.apply(this);
        }
        
    }
    
}
