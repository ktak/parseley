package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.List;
import ktak.immutablejava.Tuple;

public class Grammar<NT,T> {
    
    protected final NT start;
    protected final AATreeMap<NT,List<Tuple<Long,RightHandSide<NT,T>>>> rules;
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
            AATreeMap<NT,List<Tuple<Long,RightHandSide<NT,T>>>> rules,
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
    
    public Grammar<NT,T> addRule(NT lhs, RightHandSide<NT,T> rhs) {
        return new Grammar<>(
                start,
                rules.insert(lhs, rules.get(lhs).match(
                        (unit) -> new List.Nil<Tuple<Long,RightHandSide<NT,T>>>()
                                .cons(Tuple.create(0L, rhs)),
                        (list) -> list.cons(Tuple.create(list.length(), rhs)))),
                nullabilityDeterminer.addRule(lhs, rhs),
                rhsCmp,
                ntCmp,
                tCmp);
    }
    
    protected boolean isNullable(NT nonTerminal) {
        return nullabilityDeterminer.isNullable(nonTerminal);
    }
    
}
