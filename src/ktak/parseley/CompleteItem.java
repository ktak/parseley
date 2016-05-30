package ktak.parseley;

import ktak.immutablejava.Either;
import ktak.immutablejava.Function;
import ktak.immutablejava.List;

class CompleteItem<NT,T> extends Item<NT,T> {
    
    protected final List<Either<NT,T>> completedReversed;
    
    protected CompleteItem(
            NT lhs, long ruleIndex, int shifts,
            List<Either<NT,T>> completedReversed, long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
        this.completedReversed = completedReversed;
    }
    
    @Override
    protected <R> R match(
            Function<PredictItem<NT, T>, R> predictCase,
            Function<ScanItem<NT, T>, R> scanCase,
            Function<CompleteItem<NT, T>, R> completeCase) {
        return completeCase.apply(this);
    }
    
}
