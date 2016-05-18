package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

class NullabilityDeterminer<NT,T> {
    
    private final AATreeSet<NT> nullableNonTerminals;
    private final AATreeMap<NT,AATreeSet<AATreeSet<NT>>> dependentToDependencies;
    private final Comparator<NT> cmp;
    private final Comparator<AATreeSet<NT>> setCmp;
    
    protected NullabilityDeterminer(Comparator<NT> cmp) {
        this.nullableNonTerminals = AATreeSet.emptySet(cmp);
        this.dependentToDependencies = AATreeMap.emptyMap(cmp);
        this.cmp = cmp;
        this.setCmp = (s1, s2) -> s1.sortedList().compareTo(s2.sortedList(), cmp);
    }
    
    private NullabilityDeterminer(
            AATreeSet<NT> nullableNonTerminals,
            AATreeMap<NT,AATreeSet<AATreeSet<NT>>> dependentToDependencies,
            Comparator<NT> cmp,
            Comparator<AATreeSet<NT>> setCmp) {
        this.nullableNonTerminals = nullableNonTerminals;
        this.dependentToDependencies = dependentToDependencies;
        this.cmp = cmp;
        this.setCmp = setCmp;
    }
    
    protected boolean isNullable(NT nonTerminal) {
        return nullableNonTerminals.contains(nonTerminal);
    }
    
    protected NullabilityDeterminer<NT,T> addRule(NT lhs, RightHandSide<NT,T> rhs) {
        
        return nullableNonTerminals.contains(lhs) ?
                this :
                rhs.rhs.match(
                        (unit) -> addEmptyRule(lhs),
                        (cons) -> addNonEmptyRule(lhs, rhs));
        
    }
    
    private NullabilityDeterminer<NT,T> addEmptyRule(NT nullableNonTerminal) {
        
        Tuple<AATreeSet<NT>,AATreeMap<NT,AATreeSet<AATreeSet<NT>>>> updated =
                removeFromDependencies(nullableNonTerminal);
        
        return updated.left.sortedList().foldRight(
                new NullabilityDeterminer<NT,T>(
                        nullableNonTerminals.insert(nullableNonTerminal),
                        updated.right,
                        cmp,
                        setCmp),
                (nonTerminal) -> (nullabilityDeterminer) ->
                        nullabilityDeterminer.addEmptyRule(nonTerminal));
        
    }
    
    private Tuple<AATreeSet<NT>,AATreeMap<NT,AATreeSet<AATreeSet<NT>>>> removeFromDependencies(
            NT nullableNonTerminal) {
        
        return dependentToDependencies.sortedKeyValPairs().foldRight(
                Tuple.create(AATreeSet.emptySet(cmp), dependentToDependencies),
                (kv) -> (tup) -> {
                    Tuple<Boolean,AATreeSet<AATreeSet<NT>>> updatedDependencies =
                            removeDependency(nullableNonTerminal, kv.left, kv.right);
                    return updatedDependencies.left ?
                            Tuple.create(
                                    tup.left.insert(kv.left),
                                    dependentToDependencies.remove(kv.left)) :
                            Tuple.create(
                                    tup.left,
                                    dependentToDependencies.insert(
                                            kv.left, updatedDependencies.right));
                });
        
    }
    
    /*
     * removes the nullableNonTerminal from each set in setOfSets, returning
     * the updated setOfSets as well as a boolean that represents whether the
     * dependentNonTerminal was found to be nullable due to at least one set
     * in setOfSets becoming empty
     */
    private Tuple<Boolean,AATreeSet<AATreeSet<NT>>> removeDependency(
            NT nullableNonTerminal,
            NT dependentNonTerminal,
            AATreeSet<AATreeSet<NT>> setOfSets) {
        
        return setOfSets.sortedList().foldRight(
                Tuple.create(false, setOfSets),
                (set) -> (tup) -> {
                    AATreeSet<NT> updatedSet = set.remove(nullableNonTerminal);
                    return updatedSet.size() == 0 ?
                            Tuple.create(true, tup.right) :
                            Tuple.create(tup.left, tup.right.insert(updatedSet));
                });
        
    }
    
    private NullabilityDeterminer<NT,T> addNonEmptyRule(NT lhs, RightHandSide<NT,T> rhs) {
        
        return dependencySet(rhs).match(
                (none) -> this,
                (set) -> set.size() == 0 ?
                        addEmptyRule(lhs) :
                        new NullabilityDeterminer<>(
                                nullableNonTerminals,
                                addToMappedSet(dependentToDependencies, lhs, set, setCmp),
                                cmp,
                                setCmp));
        
    }
    
    /*
     * if the right hand side is only nonterminals, returns a set of these
     * with nonterminals that are already known to be nullable removed.
     * returns none otherwise because if the right hand side contains any
     * terminals, this rule cannot contribute to its left hand side being
     * nullable
     */
    private Option<AATreeSet<NT>> dependencySet(RightHandSide<NT,T> rhs) {
        
        return rhs.rhs.foldRight(
                Option.some(AATreeSet.emptySet(cmp)),
                (either) -> (opt) -> opt.match(
                        (none) -> none,
                        (set) -> either.match(
                                (nonTerminal) -> Option.some(
                                        nullableNonTerminals.contains(nonTerminal) ?
                                                set :
                                                set.insert(nonTerminal)),
                                (terminal) -> Option.none())));
        
    }
    
    private <K,V> AATreeMap<K,AATreeSet<V>> addToMappedSet(
            AATreeMap<K,AATreeSet<V>> map, K key, V setValue, Comparator<V> cmp) {
        
        return map.insert(
                key,
                map.get(key).match(
                        (unit) -> AATreeSet.emptySet(cmp).insert(setValue),
                        (set) -> set.insert(setValue)));
        
    }
    
}
