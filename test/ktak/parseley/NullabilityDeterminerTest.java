package ktak.parseley;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class NullabilityDeterminerTest {
    
    private static final Comparator<String> strCmp = (s1, s2) -> s1.compareTo(s2);
    
    @Test
    public void test() {
        
        NullabilityDeterminer<String,String> determiner =
                new NullabilityDeterminer<>(strCmp);
        
        Assert.assertFalse(determiner.isNullable("A"));
        Assert.assertFalse(determiner.isNullable("B"));
        Assert.assertFalse(determiner.isNullable("C"));
        Assert.assertFalse(determiner.isNullable("D"));
        
        determiner = determiner.addRule("A", new RightHandSide<String,String>());
        
        Assert.assertTrue(determiner.isNullable("A"));
        Assert.assertFalse(determiner.isNullable("B"));
        Assert.assertFalse(determiner.isNullable("C"));
        Assert.assertFalse(determiner.isNullable("D"));
        
        determiner = determiner.addRule("B",
                new RightHandSide<String,String>()
                .thenNonTerminal("C").thenNonTerminal("D"));
        
        Assert.assertTrue(determiner.isNullable("A"));
        Assert.assertFalse(determiner.isNullable("B"));
        Assert.assertFalse(determiner.isNullable("C"));
        Assert.assertFalse(determiner.isNullable("D"));
        
        determiner = determiner.addRule("C",
                new RightHandSide<String,String>().thenTerminal("t"));
        
        Assert.assertTrue(determiner.isNullable("A"));
        Assert.assertFalse(determiner.isNullable("B"));
        Assert.assertFalse(determiner.isNullable("C"));
        Assert.assertFalse(determiner.isNullable("D"));
        
        determiner = determiner.addRule("C", new RightHandSide<String,String>());
        
        Assert.assertTrue(determiner.isNullable("A"));
        Assert.assertFalse(determiner.isNullable("B"));
        Assert.assertTrue(determiner.isNullable("C"));
        Assert.assertFalse(determiner.isNullable("D"));
        
        determiner = determiner.addRule("D",
                new RightHandSide<String,String>().thenNonTerminal("C"));
        
        Assert.assertTrue(determiner.isNullable("A"));
        Assert.assertTrue(determiner.isNullable("B"));
        Assert.assertTrue(determiner.isNullable("C"));
        Assert.assertTrue(determiner.isNullable("D"));
        
    }
    
}
