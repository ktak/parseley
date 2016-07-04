package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

public class Grammar<NT,T,R> {
    
    protected final NT start;
    protected final AATreeMap<NT,List<Tuple<Long,Rule<NT,T,R,?>>>> rules;
    protected final NullabilityDeterminer<NT,T> nullabilityDeterminer;
    protected final Comparator<RightHandSide<NT,T>> rhsCmp;
    protected final Comparator<NT> ntCmp;
    protected final Comparator<T> tCmp;
    
    public Grammar(NT start, Comparator<NT> ntCmp, Comparator<T> tCmp) {
        this.start = start;
        this.rules = AATreeMap.emptyMap(ntCmp);
        this.nullabilityDeterminer = new NullabilityDeterminer<NT,T>(ntCmp);
        this.rhsCmp = new RightHandSide.RHSComparator<>(ntCmp, tCmp);
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
    }
    
    private Grammar(
            NT start,
            AATreeMap<NT,List<Tuple<Long,Rule<NT,T,R,?>>>> rules,
            NullabilityDeterminer<NT,T> nullabilityDeterminer,
            Comparator<RightHandSide<NT,T>> rhsCmp,
            Comparator<NT> ntCmp,
            Comparator<T> tCmp) {
        this.start = start;
        this.rules = rules;
        this.nullabilityDeterminer = nullabilityDeterminer;
        this.rhsCmp = rhsCmp;
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
    }
    
    public Grammar<NT,T,R> addRule(NT lhs, Rule<NT,T,R,?> rule) {
        return new Grammar<>(
                start,
                rules.insert(lhs, rules.get(lhs).match(
                        (unit) -> new List.Nil<Tuple<Long,Rule<NT,T,R,?>>>()
                                .cons(Tuple.create(0L, rule)),
                        (list) -> list.cons(Tuple.create(list.length(), rule)))),
                nullabilityDeterminer.addRule(lhs, rule.symbols),
                rhsCmp,
                ntCmp,
                tCmp);
    }
    
    protected boolean isNullable(NT nonTerminal) {
        return nullabilityDeterminer.isNullable(nonTerminal);
    }
    
    protected Option<Rule<NT,T,R,?>> getRule(NT lhs, long index) {
        
        return rules.get(lhs).match(
                (none) -> new Option.None<>(),
                (lhsRules) -> lhsRules.foldRight(
                        new Option.None<>(),
                        (rule) -> (opt) -> rule.left.equals(index) ?
                                Option.some(rule.right) : opt));
        
    }
    
}
