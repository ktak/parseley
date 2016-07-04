package ktak.parseley;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ktak.immutablejava.AATreeSet;

public class AmbiguousGrammarTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    private static final Parser<String,String,String> expressionParser =
            new Parser<String,String,String>(createExpressionGrammar());
    
    private static Grammar<String,String,String> createExpressionGrammar() {
        
        return new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("Sum", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("Sum")
                        .prependTerminal("+")
                        .prependNonTerminal("Sum"),
                        new RuleOperation.NonTerminalOperation<>((S1) ->
                        new RuleOperation.TerminalOperation<>((plus) ->
                        new RuleOperation.NonTerminalOperation<>((S2) ->
                        new RuleOperation.EndOperation<>("(" + S1 + " + " + S2 + ")"))))))
                .addRule("Sum", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("N"),
                        new RuleOperation.TerminalOperation<>((N) ->
                        new RuleOperation.EndOperation<>(N))));
        
    }
    
    @Test
    public void test() {
        
        Assert.assertEquals(
                (Long)uniqueTrees(1),
                parseResults(Arrays.asList("N")).size());
        Assert.assertEquals(
                (Long)uniqueTrees(2),
                parseResults(Arrays.asList("N", "+", "N")).size());
        Assert.assertEquals(
                (Long)uniqueTrees(3),
                parseResults(Arrays.asList("N", "+", "N", "+", "N")).size());
        Assert.assertEquals(
                (Long)uniqueTrees(4),
                parseResults(Arrays.asList("N", "+", "N", "+", "N", "+", "N")).size());
        Assert.assertEquals(
                (Long)uniqueTrees(5),
                parseResults(Arrays.asList("N", "+", "N", "+", "N", "+", "N", "+", "N")).size());
        Assert.assertEquals(
                (Long)uniqueTrees(6),
                parseResults(Arrays.asList("N", "+", "N", "+", "N", "+", "N", "+", "N", "+", "N")).size());
        
    }
    
    private AATreeSet<String> parseResults(List<String> input) {
        
        ParseState<String,String,String> parseState = expressionParser.initialParseState();
        for (String str : input) {
            parseState = parseState.parseNextTerminal(str);
        }
        return parseState.results().foldRight(
                AATreeSet.emptySet((s1, s2) -> s1.compareTo(s2)),
                (s) -> (set) -> set.insert(s));
        
    }
    
    private long uniqueTrees(int numLeaves) {
        
        if (numLeaves <= 2)
            return 1;
        
        int count = 0;
        for (int leftCount=1; leftCount < numLeaves; leftCount++) {
            count += uniqueTrees(leftCount) * uniqueTrees(numLeaves-leftCount);
        }
        return count;
        
    }
    
}
