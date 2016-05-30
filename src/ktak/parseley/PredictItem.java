package ktak.parseley;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class PredictItem<NT,T> extends Item<NT,T> {
    
    protected final List<Either<NT,T>> completedReversed;
    protected final NT nextNonTerminal;
    protected final List<Either<NT,T>> rest;
    
    protected PredictItem(
            NT lhs,
            long ruleIndex,
            int shifts,
            List<Either<NT,T>> completedReversed,
            NT nextNonTerminal,
            List<Either<NT,T>> rest,
            long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
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
                leftHandSide, ruleIndex, shifts+1,
                completedReversed.cons(Either.left(nextNonTerminal)), rest, startIndex);
    }
    
}
