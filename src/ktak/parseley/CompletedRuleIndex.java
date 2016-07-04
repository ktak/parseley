package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.Either;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

class CompletedRuleIndex<NT,T,R> {
    
    /*
     * maps a start index to a map from the left hand side of a rule to a list
     * of tuples of a rule and an end index, where each rule was completed between
     * the start and end index
     */
    private final AATreeMap<Long,AATreeMap<NT,List<Tuple<Rule<NT,T,R,?>,Long>>>> index;
    private final AATreeMap<Long,T> input;
    private final Comparator<T> tCmp;
    
    protected CompletedRuleIndex(
            AATreeMap<Long,T> input, Comparator<T> tCmp) {
        this.index = AATreeMap.emptyMap((l1, l2) -> l1.compareTo(l2));
        this.input = input;
        this.tCmp = tCmp;
    }
    
    private CompletedRuleIndex(
            AATreeMap<Long,AATreeMap<NT,List<Tuple<Rule<NT,T,R,?>,Long>>>> index,
            AATreeMap<Long,T> input,
            Comparator<T> tCmp) {
        this.index = index;
        this.input = input;
        this.tCmp = tCmp;
    }
    
    protected CompletedRuleIndex<NT,T,R> addCompletedRule(
            CompleteItem<NT,T,R> item, long endIndex, Grammar<NT,T,R> grammar) {
        
        return new CompletedRuleIndex<>(
                index.insert(
                        item.startIndex,
                        index.get(item.startIndex).match(
                                (none1) -> insertLhsRule(
                                        AATreeMap.emptyMap(grammar.ntCmp),
                                        item.leftHandSide,
                                        grammar.getRule(item.leftHandSide, item.ruleIndex).match(
                                                (none2) -> { throw new RuntimeException(); },
                                                (rule) -> rule),
                                        endIndex),
                                (lhsMap) -> insertLhsRule(
                                        lhsMap,
                                        item.leftHandSide,
                                        grammar.getRule(item.leftHandSide, item.ruleIndex).match(
                                                (none2) -> { throw new RuntimeException(); },
                                                (rule) -> rule),
                                        endIndex))),
                input,
                tCmp);
        
    }
    
    private AATreeMap<NT,List<Tuple<Rule<NT,T,R,?>,Long>>> insertLhsRule(
            AATreeMap<NT,List<Tuple<Rule<NT,T,R,?>,Long>>> lhsMap,
            NT lhs, Rule<NT,T,R,?> rule, long endIndex) {
        
        return lhsMap.insert(
                lhs,
                lhsMap.get(lhs).match(
                        (none) -> new List.Nil<Tuple<Rule<NT,T,R,?>,Long>>().cons(
                                Tuple.create(rule, endIndex)),
                        (rules) -> rules.cons(Tuple.create(rule, endIndex))));
        
    }
    
    public List<R> buildResults(NT lhs, long startIndex, long endIndex) {
        
        return rulesSpanning(lhs, startIndex, endIndex).mapcat(
                (rule) -> expandRule(rule.symbols, startIndex, endIndex).mapcat(
                        (expandedRule) -> constructParseResults(
                                rule.symbols,
                                rule.operation,
                                expandedRule)));
        
    }
    
    private List<R> constructParseResults(
            RuleSymbols<NT,T,R,?> ruleSymbols,
            RuleOperation<NT,T,R,?> ruleOperation,
            List<Either<T,RuleSpan<NT,T,R>>> expandedRule) {
        
        return expandedRule.match(
                (nil) -> ruleSymbols.match(
                        (ntSym) -> { throw new RuntimeException(); },
                        (tSym) -> { throw new RuntimeException(); },
                        (endSym) -> ruleOperation.match(
                                (ntOp) -> { throw new RuntimeException(); },
                                (tOp) -> { throw new RuntimeException(); },
                                (endOp) -> new List.Nil<R>().cons(endOp.result))),
                (cons) -> cons.left.match(
                        (terminal) -> ruleSymbols.match(
                                (ntSym) -> { throw new RuntimeException(); },
                                (tSym) -> ruleOperation.match(
                                        (ntOp) -> { throw new RuntimeException(); },
                                        (tOp) -> constructParseResults(
                                                tSym.next,
                                                tOp.operation.apply(terminal),
                                                cons.right),
                                        (endOp) -> { throw new RuntimeException(); }),
                                (endSym) -> { throw new RuntimeException(); }),
                        (ruleSpan) -> ruleSymbols.match(
                                (ntSym) -> ruleOperation.match(
                                        (ntOp) -> expandRule(
                                                ruleSpan.rule.symbols,
                                                ruleSpan.startIndex,
                                                ruleSpan.endIndex).mapcat(
                                                        (expdRule) -> constructParseResults(
                                                                ruleSpan.rule.symbols,
                                                                ruleSpan.rule.operation,
                                                                expdRule).mapcat(
                                                                        (result) -> constructParseResults(
                                                                                ntSym.next,
                                                                                ntOp.operation.apply(result),
                                                                                cons.right))),
                                        (tOp) -> { throw new RuntimeException(); },
                                        (endOp) -> { throw new RuntimeException(); }),
                                (tSym) -> { throw new RuntimeException(); },
                                (endSym) -> { throw new RuntimeException(); })));
        
    }
    
    private List<Rule<NT,T,R,?>> rulesSpanning(NT lhs, long startIndex, long endIndex) {
        
        List<Rule<NT,T,R,?>> nil = new List.Nil<>();
        return index.get(startIndex).match(
                (none) -> nil,
                (lhsMap) -> lhsMap.get(lhs).match(
                        (none) -> nil,
                        (tuples) -> tuples.foldRight(
                                nil,
                                (ruleAndEnd) -> (rules) -> ruleAndEnd.right.equals(endIndex) ?
                                        rules.cons(ruleAndEnd.left) :
                                        rules)));
        
    }
    
    private Option<List<Tuple<Rule<NT,T,R,?>,Long>>> rulesBeginningAt(NT lhs, long startIndex) {
        
        Option<List<Tuple<Rule<NT,T,R,?>,Long>>> none = Option.none();
        return index.get(startIndex).match(
                (nope) -> none,
                (lhsMap) -> lhsMap.get(lhs).match(
                        (nope) -> none,
                        (tuples) -> Option.some(tuples)));
        
    }
    
    private List<List<Either<T,RuleSpan<NT,T,R>>>> expandRule(
            RuleSymbols<NT,T,R,?> symbols, long startIndex, long endIndex) {
        
        List<List<Either<T,RuleSpan<NT,T,R>>>> result = new List.Nil<>();
        return symbols.match(
                (ntSym) -> rulesBeginningAt(ntSym.nonTerminal, startIndex).match(
                        (none) -> result,
                        (ruleAndEndList) -> ruleAndEndList.mapcat(
                                (ruleAndEnd) -> expandRule(ntSym.next, ruleAndEnd.right, endIndex).map(
                                        (expandedRest) -> expandedRest.cons(Either.right(
                                                new RuleSpan<NT,T,R>(
                                                        ruleAndEnd.left, startIndex, ruleAndEnd.right)))))),
                (tSym) -> input.get(startIndex).match(
                        (none) -> result,
                        (inputTerminal) -> tCmp.compare(inputTerminal,  tSym.terminal) != 0 ?
                                result :
                                expandRule(tSym.next, startIndex+1, endIndex).map(
                                        (expandedRest) -> expandedRest.cons(Either.left(inputTerminal)))),
                (endSym) -> Long.compare(startIndex, endIndex) == 0 ?
                        result.cons(new List.Nil<>()) : result);
        
    }
    
}
