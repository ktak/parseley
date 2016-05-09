package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class ScanItem<NT,T> extends Item<NT,T> {
    
    protected final NT lhs;
    protected final List<Either<NT,T>> completedReversed;
    protected final T nextTerminal;
    protected final List<Either<NT,T>> rest;
    
    protected ScanItem(
            NT lhs,
            List<Either<NT,T>> completedReversed,
            T nextTerminal,
            List<Either<NT,T>> rest,
            long startIndex) {
        super(startIndex);
        this.lhs = lhs;
        this.completedReversed = completedReversed;
        this.nextTerminal = nextTerminal;
        this.rest = rest;
    }
    
    @Override
    protected <R> R match(
            Function<PredictItem<NT, T>, R> predictCase,
            Function<ScanItem<NT, T>, R> scanCase,
            Function<CompleteItem<NT, T>, R> completeCase) {
        return scanCase.apply(this);
    }
    
    protected Item<NT,T> shift() {
        return Item.item(
                lhs, completedReversed.cons(Either.right(nextTerminal)), rest, startIndex);
    }
    
    protected static class ScanItemComparator<NT,T> implements Comparator<ScanItem<NT,T>> {
        
        protected final Comparator<Either<NT,T>> cmp;
        
        protected ScanItemComparator(Comparator<Either<NT,T>> cmp) {
            this.cmp = cmp;
        }
        
        @Override
        public int compare(ScanItem<NT, T> i1, ScanItem<NT, T> i2) {
            
            int superCompare = Item.compare(i1, i2);
            if (superCompare != 0) return superCompare;
            
            int compareResult1 = i1.completedReversed
                    .cons(Either.right(i1.nextTerminal))
                    .cons(Either.left(i1.lhs))
                    .compareTo(
                            i2.completedReversed
                            .cons(Either.right(i2.nextTerminal))
                            .cons(Either.left(i2.lhs)),
                            cmp);
            return compareResult1 == 0 ?
                    i1.rest.compareTo(i2.rest, cmp) :
                    compareResult1;
            
        }
        
    }
    
}
