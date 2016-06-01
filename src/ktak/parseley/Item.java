package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Function;

abstract class Item<NT,T,R> {
    
    // these four fields uniquely determine an item
    protected final NT leftHandSide;
    protected final long ruleIndex;
    protected final int shifts;
    protected final long startIndex;
    
    protected Item(NT leftHandSide, long ruleIndex, int shifts, long startIndex) {
        this.leftHandSide = leftHandSide;
        this.ruleIndex = ruleIndex;
        this.shifts = shifts;
        this.startIndex = startIndex;
    }
    
    protected abstract <X> X match(
            Function<PredictItem<NT,T,R>,X> predictCase,
            Function<ScanItem<NT,T,R>,X> scanCase,
            Function<CompleteItem<NT,T,R>,X> completeCase);
    
    protected static <NT,T,R> Item<NT,T,R> initialItem(
            NT lhs, long ruleIndex, RuleSymbols<NT,T,R,?> symbols, long startIndex) {
        
        return item(lhs, ruleIndex, 0, symbols, startIndex);
        
    }
    
    protected static <NT,T,R> Item<NT,T,R> item(
            NT lhs, long ruleIndex, int shifts, RuleSymbols<NT,T,R,?> symbols, long startIndex) {
        
        return symbols.match(
                (ntSym) -> new PredictItem<>(
                        lhs, ruleIndex, shifts, ntSym.nonTerminal, ntSym.next, startIndex),
                (tSym) -> new ScanItem<>(
                        lhs, ruleIndex, shifts, tSym.terminal, tSym.next, startIndex),
                (endSym) -> new CompleteItem<>(
                        lhs, ruleIndex, shifts, startIndex));
        
        
    }
    
    protected static <NT,T,R> int compare(
            Comparator<NT> ntCmp, Item<NT,T,R> i1, Item<NT,T,R> i2) {
        
        int lhsCmpResult = ntCmp.compare(i1.leftHandSide, i2.leftHandSide);
        if (lhsCmpResult != 0)
            return lhsCmpResult;
        
        int ruleIndexCmpResult = Long.compare(i1.ruleIndex, i2.ruleIndex);
        if (ruleIndexCmpResult != 0)
            return ruleIndexCmpResult;
        
        int shiftsCmpResult = Integer.compare(i1.shifts, i2.shifts);
        if (shiftsCmpResult != 0)
            return shiftsCmpResult;
        
        int startIndexCmpResult = Long.compare(i1.startIndex, i2.startIndex);
        if (startIndexCmpResult != 0)
            return startIndexCmpResult;
        
        return 0;
        
    }
    
}
