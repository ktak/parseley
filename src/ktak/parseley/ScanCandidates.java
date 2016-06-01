package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.List;

class ScanCandidates<NT,T,R> {
    
    protected final AATreeMap<T,List<ScanItem<NT,T,R>>> scanCandidates;
    
    protected ScanCandidates(Comparator<T> tCmp) {
        this.scanCandidates = AATreeMap.emptyMap(tCmp);
    }
    
    private ScanCandidates(AATreeMap<T,List<ScanItem<NT,T,R>>> scanCandidates) {
        this.scanCandidates = scanCandidates;
    }
    
    protected ScanCandidates<NT,T,R> add(ScanItem<NT,T,R> item) {
        
        return new ScanCandidates<>(
                scanCandidates.insert(
                        item.nextTerminal,
                        scanCandidates.get(item.nextTerminal).match(
                                (unit) -> new List.Nil<ScanItem<NT,T,R>>().cons(item),
                                (list) -> list.cons(item))));
        
    }
    
}
