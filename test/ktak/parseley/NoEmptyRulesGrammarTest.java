package ktak.parseley;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NoEmptyRulesGrammarTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    private static final Parser<String,String,String> expressionParser =
            new Parser<String,String,String>(createExpressionGrammar());
    
    private static Grammar<String,String,String> createExpressionGrammar() {
        
        return new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("Sum", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("Product")
                        .prependTerminal("+")
                        .prependNonTerminal("Sum")))
                .addRule("Sum", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("Product")))
                .addRule("Product", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("Factor")
                        .prependTerminal("*")
                        .prependNonTerminal("Product")))
                .addRule("Product", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("Factor")))
                .addRule("Factor", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal(")")
                        .prependNonTerminal("Sum")
                        .prependTerminal("(")))
                .addRule("Factor", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("N")));
        
    }
    
    private final boolean recognizes(List<String> input) {
        
        ParseState<String,String,String> parseState = expressionParser.initialParseState();
        for (String str : input) {
            parseState = parseState.parseNextTerminal(str);
        }
        return parseState.recognized();
        
    }
    
    @Test
    public void test() {
        
        Assert.assertFalse(recognizes(Arrays.asList("*")));
        Assert.assertFalse(recognizes(Arrays.asList("+")));
        Assert.assertFalse(recognizes(Arrays.asList("(")));
        Assert.assertFalse(recognizes(Arrays.asList("(", ")")));
        Assert.assertFalse(recognizes(Arrays.asList("*", "N")));
        Assert.assertFalse(recognizes(Arrays.asList("N", "*", "N", ")")));
        
        Assert.assertTrue(recognizes(Arrays.asList("N")));
        Assert.assertTrue(recognizes(Arrays.asList("N", "+", "N")));
        Assert.assertTrue(recognizes(Arrays.asList("N", "*", "N")));
        Assert.assertTrue(recognizes(Arrays.asList("N", "*", "N", "*", "N", "*", "N")));
        Assert.assertTrue(recognizes(Arrays.asList("N", "*", "N", "+", "N", "*", "N")));
        Assert.assertTrue(recognizes(Arrays.asList("N", "*", "(", "N", "+", "N", ")", "*", "N")));
        Assert.assertTrue(recognizes(Arrays.asList(
                "(", "N", "*", "(", "(", "N", "+", "N", ")", ")", "*", "N", ")")));
        
    }
    
}
