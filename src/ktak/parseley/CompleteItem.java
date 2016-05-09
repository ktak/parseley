package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class CompleteItem<NT,T> extends Item<NT,T> {
    
    protected final NT lhs;
    protected final List<Either<NT,T>> completedReversed;
    
    protected CompleteItem(NT lhs, List<Either<NT,T>> completedReversed, long startIndex) {
        super(startIndex);
        this.lhs = lhs;
        this.completedReversed = completedReversed;
    }
    
    @Override
    protected <R> R match(
            Function<PredictItem<NT, T>, R> predictCase,
            Function<ScanItem<NT, T>, R> scanCase,
            Function<CompleteItem<NT, T>, R> completeCase) {
        return completeCase.apply(this);
    }
    
    protected static class CompleteItemComparator<NT,T> implements Comparator<CompleteItem<NT,T>> {
        
        protected final Comparator<Either<NT,T>> cmp;
        
        protected CompleteItemComparator(Comparator<Either<NT,T>> cmp) {
            this.cmp = cmp;
        }
        
        @Override
        public int compare(CompleteItem<NT, T> i1, CompleteItem<NT, T> i2) {
            
            int superCompare = Item.compare(i1, i2);
            if (superCompare != 0) return superCompare;
            
            return i1.completedReversed
                    .cons(Either.left(i1.lhs))
                    .compareTo(
                            i2.completedReversed
                            .cons(Either.left(i2.lhs)),
                            cmp);
            
        }
        
    }
    
}
