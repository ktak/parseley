package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.List;

class ScanCandidates<NT,T> {
    
    protected final AATreeMap<T,List<ScanItem<NT,T>>> scanCandidates;
    
    protected ScanCandidates(Comparator<T> tCmp) {
        this.scanCandidates = AATreeMap.emptyMap(tCmp);
    }
    
    private ScanCandidates(AATreeMap<T,List<ScanItem<NT,T>>> scanCandidates) {
        this.scanCandidates = scanCandidates;
    }
    
    protected ScanCandidates<NT,T> add(ScanItem<NT,T> item) {
        
        return new ScanCandidates<>(
                scanCandidates.insert(
                        item.nextTerminal,
                        scanCandidates.get(item.nextTerminal).match(
                                (unit) -> new List.Nil<ScanItem<NT,T>>().cons(item),
                                (list) -> list.cons(item))));
        
    }
    
}
