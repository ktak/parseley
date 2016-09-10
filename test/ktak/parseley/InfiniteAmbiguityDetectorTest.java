package ktak.parseley;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class InfiniteAmbiguityDetectorTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    
    @Test
    public void test1() {
        
        // S => S | a
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("S"),
                        null))
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null));
        
        Assert.assertTrue(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test2() {
        
        // S => A | a
        // A => B
        // B => C
        // C => S
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("A"),
                        null))
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("B"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C"),
                        null))
                .addRule("C", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("S"),
                        null));
        
        Assert.assertTrue(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test3() {
        
        // S => SS | a
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("S")
                        .prependNonTerminal("S"),
                        null))
                .addRule("S", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null));
        
        Assert.assertFalse(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test4() {
        
        // A => BAC | a
        // B => C | a
        // C => epsilon
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C")
                        .prependNonTerminal("A")
                        .prependNonTerminal("B"),
                        null))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("C", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class),
                        null));
        
        Assert.assertTrue(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test5() {
        
        // A => BAC | a
        // B => C | a
        // C => B
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C")
                        .prependNonTerminal("A")
                        .prependNonTerminal("B"),
                        null))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("C", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("B"),
                        null));
        
        Assert.assertTrue(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test6() {
        
        // A => BAC | a
        // B => CB | a
        // C => B
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C")
                        .prependNonTerminal("A")
                        .prependNonTerminal("B"),
                        null))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("B")
                        .prependNonTerminal("C"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("C", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("B"),
                        null));
        
        Assert.assertFalse(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
    @Test
    public void test7() {
        
        // A => BC | a
        // B => epsilon | A
        // C => a
        Grammar<String,String,String> grammar =
                new Grammar<String,String,String>("Sum", strCmp, strCmp)
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("C")
                        .prependNonTerminal("B"),
                        null))
                .addRule("A", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class),
                        null))
                .addRule("B", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependNonTerminal("A"),
                        null))
                .addRule("C", Rule.newRule(
                        RuleSymbols.empty(String.class, String.class, String.class)
                        .prependTerminal("a"),
                        null));
        
        Assert.assertFalse(InfiniteAmbiguityDetector.isInifinitelyAmbiguous(grammar));
        
    }
    
}
