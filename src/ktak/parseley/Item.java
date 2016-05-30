package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

abstract class Item<NT,T> {
    
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
    
    protected abstract <R> R match(
            Function<PredictItem<NT,T>,R> predictCase,
            Function<ScanItem<NT,T>,R> scanCase,
            Function<CompleteItem<NT,T>,R> completeCase);
    
    protected static <NT,T> Item<NT,T> initialItem(
            NT lhs, long ruleIndex, List<Either<NT,T>> rhs, long startIndex) {
        
        return item(lhs, ruleIndex, 0, new List.Nil<>(), rhs, startIndex);
        
    }
    
    protected static <NT,T> Item<NT,T> item(
            NT lhs, long ruleIndex, int shifts,
            List<Either<NT,T>> completedReversed, List<Either<NT,T>> rhs,
            long startIndex) {
        
        return rhs.match(
                (unit) -> new CompleteItem<>(lhs, ruleIndex, shifts,
                        completedReversed, startIndex),
                (cons) -> cons.left.match(
                        (nonTerminal) -> new PredictItem<>(
                                lhs, ruleIndex, shifts,
                                completedReversed, nonTerminal, cons.right, startIndex),
                        (terminal) -> new ScanItem<>(
                                lhs, ruleIndex, shifts,
                                completedReversed, terminal, cons.right, startIndex)));
        
    }
    
    protected static <NT,T> int compare(
            Comparator<NT> ntCmp, Item<NT,T> i1, Item<NT,T> i2) {
        
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
