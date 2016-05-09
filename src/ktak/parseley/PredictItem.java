package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class PredictItem<NT,T> extends Item<NT,T> {
    
    protected final NT lhs;
    protected final List<Either<NT,T>> completedReversed;
    protected final NT nextNonTerminal;
    protected final List<Either<NT,T>> rest;
    
    protected PredictItem(
            NT lhs,
            List<Either<NT,T>> completedReversed,
            NT nextNonTerminal,
            List<Either<NT,T>> rest,
            long startIndex) {
        super(startIndex);
        this.lhs = lhs;
        this.completedReversed = completedReversed;
        this.nextNonTerminal = nextNonTerminal;
        this.rest = rest;
    }
    
    @Override
    protected <R> R match(
            Function<PredictItem<NT,T>, R> predictCase,
            Function<ScanItem<NT,T>, R> scanCase,
            Function<CompleteItem<NT,T>, R> completeCase) {
        return predictCase.apply(this);
    }
    
    protected Item<NT,T> shift() {
        return Item.item(
                lhs, completedReversed.cons(Either.left(nextNonTerminal)), rest, startIndex);
    }
    
    protected static class PredictItemComparator<NT,T> implements Comparator<PredictItem<NT,T>> {
        
        protected final Comparator<Either<NT,T>> cmp;
        
        protected PredictItemComparator(Comparator<Either<NT,T>> cmp) {
            this.cmp = cmp;
        }
        
        @Override
        public int compare(PredictItem<NT, T> i1, PredictItem<NT, T> i2) {
            
            int superCompare = Item.compare(i1, i2);
            if (superCompare != 0) return superCompare;
            
            int compareResult1 = i1.completedReversed
                    .cons(Either.left(i1.nextNonTerminal))
                    .cons(Either.left(i1.lhs))
                    .compareTo(
                            i2.completedReversed
                            .cons(Either.left(i2.nextNonTerminal))
                            .cons(Either.left(i2.lhs)),
                            cmp);
            return compareResult1 == 0 ?
                    i1.rest.compareTo(i2.rest, cmp) :
                    compareResult1;
            
        }
        
    }
    
}
