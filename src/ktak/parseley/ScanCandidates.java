package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;

class ScanCandidates<NT,T> {
    
    private final Comparator<ScanItem<NT,T>> cmp;
    protected final AATreeMap<T,AATreeSet<ScanItem<NT,T>>> scanCandidates;
    
    protected ScanCandidates(Comparator<T> tCmp, Comparator<ScanItem<NT,T>> cmp) {
        this.cmp = cmp;
        this.scanCandidates = AATreeMap.emptyMap(tCmp);
    }
    
    private ScanCandidates(
            Comparator<ScanItem<NT,T>> cmp,
            AATreeMap<T,AATreeSet<ScanItem<NT,T>>> scanCandidates) {
        this.cmp = cmp;
        this.scanCandidates = scanCandidates;
    }
    
    protected ScanCandidates<NT,T> add(ScanItem<NT,T> item) {
        
        return new ScanCandidates<>(
                cmp,
                scanCandidates.insert(
                        item.nextTerminal,
                        scanCandidates.get(item.nextTerminal).match(
                                (unit) -> AATreeSet.emptySet(cmp).insert(item),
                                (set) -> set.insert(item))));
        
    }
    
}
