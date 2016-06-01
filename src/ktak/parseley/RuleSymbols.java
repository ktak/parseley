package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.parseley.RuleType.End;
import ktak.parseley.RuleType.NonTerminal;
import ktak.parseley.RuleType.Terminal;

public abstract class RuleSymbols<NT,T,R,Type extends RuleType<NT,T,R,?>> {
    
    public static <NT,T,R> RuleSymbols<NT,T,R,End<NT,T,R>> empty(
            Class<NT> ntClass, Class<T> tClass, Class<R> rClass) {
        return new EndSymbol<>();
    }
    
    public RuleSymbols<NT,T,R,NonTerminal<NT,T,R,Type>> prependNonTerminal(NT nt) {
        return new NonTerminalSymbol<>(nt, this);
    }
    
    public RuleSymbols<NT,T,R,Terminal<NT,T,R,Type>> prependTerminal(T t) {
        return new TerminalSymbol<>(t, this);
    }
    
    protected abstract <X> X match(
            Function<NonTerminalSymbol<NT,T,R,?>,X> nonTerminalCase,
            Function<TerminalSymbol<NT,T,R,?>,X> terminalCase,
            Function<EndSymbol<NT,T,R>,X> endCase);
    
    protected abstract int compareTo(
            Comparator<NT> ntCmp,
            Comparator<T> tCmp,
            RuleSymbols<NT,T,R,?> other);
    
    protected static class NonTerminalSymbol<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleSymbols<NT,T,R,NonTerminal<NT,T,R,Next>> {
        
        protected final NT nonTerminal;
        protected final RuleSymbols<NT,T,R,Next> next;
        
        protected NonTerminalSymbol(
                NT nonTerminal,
                RuleSymbols<NT,T,R,Next> next) {
            this.nonTerminal = nonTerminal;
            this.next = next;
        }
        
        @Override
        public <X> X match(
                Function<NonTerminalSymbol<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalSymbol<NT,T,R,?>,X> terminalCase,
                Function<EndSymbol<NT,T,R>,X> endCase) {
            return nonTerminalCase.apply(this);
        }
        
        @Override
        public int compareTo(
                Comparator<NT> ntCmp,
                Comparator<T> tCmp,
                RuleSymbols<NT,T,R,?> other) {
            
            return other.match(
                    (ntSym) -> {
                        int ntCmpResult = ntCmp.compare(nonTerminal, ntSym.nonTerminal);
                        return ntCmpResult != 0 ?
                                ntCmpResult :
                                next.compareTo(ntCmp, tCmp, ntSym.next);
                    },
                    (tSym) -> -1,
                    (endSym) -> -1);
            
        }
        
    }
    
    protected static class TerminalSymbol<NT,T,R,Next extends RuleType<NT,T,R,?>>
    extends RuleSymbols<NT,T,R,Terminal<NT,T,R,Next>> {
        
        protected final T terminal;
        protected final RuleSymbols<NT,T,R,Next> next;
        
        protected TerminalSymbol(
                T terminal,
                RuleSymbols<NT,T,R,Next> next) {
            this.terminal = terminal;
            this.next = next;
        }
        
        @Override
        public <X> X match(
                Function<NonTerminalSymbol<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalSymbol<NT,T,R,?>,X> terminalCase,
                Function<EndSymbol<NT,T,R>,X> endCase) {
            return terminalCase.apply(this);
        }
        
        @Override
        public int compareTo(
                Comparator<NT> ntCmp,
                Comparator<T> tCmp,
                RuleSymbols<NT,T,R,?> other) {
            
            return other.match(
                    (ntSym) -> 1,
                    (tSym) -> {
                        int tCmpResult = tCmp.compare(terminal, tSym.terminal);
                        return tCmpResult != 0 ?
                                tCmpResult :
                                next.compareTo(ntCmp, tCmp, tSym.next);
                    },
                    (endSym) -> -1);
            
        }
        
    }
    
    protected static class EndSymbol<NT,T,R> extends RuleSymbols<NT,T,R,End<NT,T,R>> {
        
        @Override
        public <X> X match(
                Function<NonTerminalSymbol<NT,T,R,?>,X> nonTerminalCase,
                Function<TerminalSymbol<NT,T,R,?>,X> terminalCase,
                Function<EndSymbol<NT,T,R>,X> endCase) {
            return endCase.apply(this);
        }
        
        @Override
        public int compareTo(
                Comparator<NT> ntCmp,
                Comparator<T> tCmp,
                RuleSymbols<NT,T,R,?> other) {
            
            return other.match(
                    (ntSym) -> 1,
                    (tSym) -> 1,
                    (endSym) -> 0);
            
        }
        
    }
    
}
