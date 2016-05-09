package ktak.parseley;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NoEmptyRulesGrammarTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    private static final Parser<String,String> expressionParser =
            new Parser<String,String>(createExpressionGrammar());
    
    private static Grammar<String,String> createExpressionGrammar() {
        
        return new Grammar<String,String>("Sum", strCmp, strCmp)
                .addRule("Sum", new RightHandSide<String,String>()
                        .thenNonTerminal("Sum").thenTerminal("+").thenNonTerminal("Product"))
                .addRule("Sum", new RightHandSide<String,String>().thenNonTerminal("Product"))
                .addRule("Product", new RightHandSide<String,String>()
                        .thenNonTerminal("Product").thenTerminal("*").thenNonTerminal("Factor"))
                .addRule("Product", new RightHandSide<String,String>().thenNonTerminal("Factor"))
                .addRule("Factor", new RightHandSide<String,String>()
                        .thenTerminal("(").thenNonTerminal("Sum").thenTerminal(")"))
                .addRule("Factor", new RightHandSide<String,String>().thenTerminal("N"));
        
    }
    
    private final boolean recognizes(List<String> input) {
        
        ParseState<String,String> parseState = expressionParser.initialParseState();
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
