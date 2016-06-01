package ktak.parseley;

import ktak.immutablejava.Function;

class ScanItem<NT,T,R> extends Item<NT,T,R> {
    
    protected final T nextTerminal;
    protected final RuleSymbols<NT,T,R,?> rest;
    
    protected ScanItem(
            NT lhs,
            long ruleIndex,
            int shifts,
            T nextTerminal,
            RuleSymbols<NT,T,R,?> rest,
            long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
        this.nextTerminal = nextTerminal;
        this.rest = rest;
    }
    
    @Override
    protected <X> X match(
            Function<PredictItem<NT,T,R>,X> predictCase,
            Function<ScanItem<NT,T,R>,X> scanCase,
            Function<CompleteItem<NT,T,R>,X> completeCase) {
        return scanCase.apply(this);
    }
    
    protected Item<NT,T,R> shift() {
        return Item.item(
                leftHandSide, ruleIndex, shifts+1, rest, startIndex);
    }
    
}
