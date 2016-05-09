package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;

class ItemComparator<NT,T> implements Comparator<Item<NT,T>> {
    
    protected final Comparator<PredictItem<NT,T>> predictCmp;
    protected final Comparator<ScanItem<NT,T>> scanCmp;
    protected final Comparator<CompleteItem<NT,T>> completeCmp;
    
    protected ItemComparator(Comparator<NT> ntCmp, Comparator<T> tCmp) {
        
        Comparator<Either<NT,T>> eitherCmp =
                (e1, e2) -> e1.match(
                        (nt1) -> e2.match(
                                (nt2) -> ntCmp.compare(nt1, nt2),
                                (t2) -> -1),
                        (t1) -> e2.match(
                                (nt2) -> 1,
                                (t2) -> tCmp.compare(t1, t2)));
        this.predictCmp = new PredictItem.PredictItemComparator<>(eitherCmp);
        this.scanCmp = new ScanItem.ScanItemComparator<>(eitherCmp);
        this.completeCmp = new CompleteItem.CompleteItemComparator<>(eitherCmp);
        
    }
    
    @Override
    public int compare(Item<NT, T> i1, Item<NT, T> i2) {
        
        return i1.match(
                (predictItem1) -> i2.match(
                        (predictItem2) -> predictCmp.compare(predictItem1, predictItem2),
                        (scanItem2) -> -1,
                        (completeItem2) -> -1),
                (scanItem1) -> i2.match(
                        (predictItem2) -> 1,
                        (scanItem2) -> scanCmp.compare(scanItem1, scanItem2),
                        (completeItem2) -> -1),
                (completeItem1) -> i2.match(
                        (predictItem2) -> 1,
                        (scanItem2) -> 1,
                        (completeItem2) -> completeCmp.compare(completeItem1, completeItem2)));
        
    }
    
}
