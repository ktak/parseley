package ktak.parseley;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

abstract class Item<NT,T> {
    
    protected final long startIndex;
    
    protected Item(long startIndex) {
        this.startIndex = startIndex;
    }
    
    protected abstract <R> R match(
            Function<PredictItem<NT,T>,R> predictCase,
            Function<ScanItem<NT,T>,R> scanCase,
            Function<CompleteItem<NT,T>,R> completeCase);
    
    protected static <NT,T> Item<NT,T> item(NT lhs, List<Either<NT,T>> rhs, long startIndex) {
        return item(lhs, new List.Nil<>(), rhs, startIndex);
    }
    
    protected static <NT,T> Item<NT,T> item(
            NT lhs, List<Either<NT,T>> completedReversed, List<Either<NT,T>> rhs, long startIndex) {
        
        return rhs.match(
                (unit) -> new CompleteItem<>(lhs, completedReversed, startIndex),
                (cons) -> cons.left.match(
                        (nonTerminal) -> new PredictItem<>(
                                lhs, completedReversed, nonTerminal, cons.right, startIndex),
                        (terminal) -> new ScanItem<>(
                                lhs, completedReversed, terminal, cons.right, startIndex)));
        
    }
    
    protected static <NT,T> int compare(Item<NT,T> i1, Item<NT,T> i2) {
        return Long.compare(i1.startIndex, i2.startIndex);
    }
    
}
