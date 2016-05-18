package ktak.parseley;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmptyRulesGrammarTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    private static final Parser<String,String> complexGrammarParser =
            new Parser<String,String>(createComplexGrammar());
    
    /*
     * S => A | a
     * A => epsilon | SS
     */
    private static Grammar<String,String> createComplexGrammar() {
        
        return new Grammar<String,String>("S", strCmp, strCmp)
                .addRule("S", new RightHandSide<String,String>()
                        .thenNonTerminal("A"))
                .addRule("S", new RightHandSide<String,String>()
                        .thenTerminal("a"))
                .addRule("A", new RightHandSide<String,String>())
                .addRule("A", new RightHandSide<String,String>()
                        .thenNonTerminal("S").thenNonTerminal("S"));
        
    }
    
    private final boolean recognizes(List<String> input) {
        
        ParseState<String,String> parseState = complexGrammarParser.initialParseState();
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
