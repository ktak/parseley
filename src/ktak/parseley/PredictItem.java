package ktak.parseley;

import ktak.immutablejava.Function;

class PredictItem<NT,T,R> extends Item<NT,T,R> {
    
    protected final NT nextNonTerminal;
    protected final RuleSymbols<NT,T,R,?> rest;
    
    protected PredictItem(
            NT lhs,
            long ruleIndex,
            int shifts,
            NT nextNonTerminal,
            RuleSymbols<NT,T,R,?> rest,
            long startIndex) {
        super(lhs, ruleIndex, shifts, startIndex);
        this.nextNonTerminal = nextNonTerminal;
        this.rest = rest;
    }
    
    @Override
    protected <X> X match(
            Function<PredictItem<NT,T,R>,X> predictCase,
            Function<ScanItem<NT,T,R>,X> scanCase,
            Function<CompleteItem<NT,T,R>,X> completeCase) {
        return predictCase.apply(this);
    }
    
    protected Item<NT,T,R> shift() {
        return Item.item(
                leftHandSide, ruleIndex, shifts+1, rest, startIndex);
    }
    
}
