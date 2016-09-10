package ktak.parseley;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmptyRulesGrammarTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    private static final Parser<String,String,String> complexGrammarParser =
            Parser.createParser(createComplexGrammar()).match(
                    (unit) -> { throw new RuntimeException(); },
                    (parser) -> parser);
    
    /*
     * S => epsilon | Aa | a
     * A => epsilon | SS
     */
    private static Grammar<String,String,String> createComplexGrammar() {
        return new Grammar<String,String,String>("S", strCmp, strCmp)
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class),
                        new RuleOperation.EndOperation<>("epsilon")))
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a")
                        .prependNonTerminal("A"),
                        new RuleOperation.NonTerminalOperation<>((A) ->
                        new RuleOperation.TerminalOperation<>((a) ->
                        new RuleOperation.EndOperation<>(A+a)))))
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        new RuleOperation.TerminalOperation<>((a) ->
                        new RuleOperation.EndOperation<>(a))))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class),
                        new RuleOperation.EndOperation<>("epsilon")))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("S")
                        .prependNonTerminal("S"),
                        new RuleOperation.NonTerminalOperation<>((S1) ->
                        new RuleOperation.NonTerminalOperation<>((S2) ->
                        new RuleOperation.EndOperation<>(S1 + " " + S2)))));
        
    }
    
    private final boolean recognizes(List<String> input) {
        
        ParseState<String,String,String> parseState = complexGrammarParser.initialParseState();
        for (String str : input) {
            parseState = parseState.parseNextTerminal(str);
        }
        return parseState.recognized();
        
    }
    
    @Test
    public void test() {
        
        Assert.assertFalse(recognizes(Arrays.asList("b")));
        Assert.assertFalse(recognizes(Arrays.asList("a","b")));
        Assert.assertFalse(recognizes(Arrays.asList("b","a")));
        
        Assert.assertTrue(recognizes(Arrays.asList()));
        Assert.assertTrue(recognizes(Arrays.asList("a")));
        Assert.assertTrue(recognizes(Arrays.asList("a","a")));
        Assert.assertTrue(recognizes(Arrays.asList("a","a","a")));
        Assert.assertTrue(recognizes(Arrays.asList("a","a","a","a")));
        
    }
    
}
