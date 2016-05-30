package ktak.parseley;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class ScanItem<NT,T> extends Item<NT,T> {
    
    protected final List<Either<NT,T>> completedReversed;
    protected final T nextTerminal;
    protected final List<Either<NT,T>> rest;
    
    protected ScanItem(
            NT lhs,
            long ruleIndex,
            int shifts,
            List<Either<NT,T>> completedReversed,
            T nextTerminal,
            List<Either<NT,T>> rest,
            long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
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
                leftHandSide, ruleIndex, shifts+1,
                completedReversed.cons(Either.right(nextTerminal)), rest, startIndex);
    }
    
}
