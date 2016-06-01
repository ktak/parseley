package ktak.parseley;

import ktak.immutablejava.Function;

class CompleteItem<NT,T,R> extends Item<NT,T,R> {
    
    protected CompleteItem(
            NT lhs, long ruleIndex, int shifts, long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
    }
    
    @Override
    protected <X> X match(
            Function<PredictItem<NT,T,R>,X> predictCase,
            Function<ScanItem<NT,T,R>,X> scanCase,
            Function<CompleteItem<NT,T,R>,X> completeCase) {
        return completeCase.apply(this);
    }
    
}
