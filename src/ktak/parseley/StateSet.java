package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

class StateSet<NT,T> {
    
    private final Comparator<NT> ntCmp;
    private final Comparator<T> tCmp;
    private final ItemComparator<NT,T> itemCmp;
    protected final AATreeSet<Item<NT,T>> itemsInSet;
    private final AATreeMap<NT,AATreeSet<PredictItem<NT,T>>> predictItems;
    private final AATreeMap<NT,AATreeSet<CompleteItem<NT,T>>> completeItems;
    private final List<Item<NT,T>> frontQueue;
    private final List<Item<NT,T>> backQueue;
    
    protected StateSet(Comparator<NT> ntCmp, Comparator<T> tCmp) {
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
        this.itemCmp = new ItemComparator<NT,T>(ntCmp, tCmp);
        this.itemsInSet = AATreeSet.emptySet(itemCmp);
        this.predictItems = AATreeMap.emptyMap(ntCmp);
        this.completeItems = AATreeMap.emptyMap(ntCmp);
        this.frontQueue = new List.Nil<>();
        this.backQueue = new List.Nil<>();
    }
    
    private StateSet(
            Comparator<NT> ntCmp, Comparator<T> tCmp, ItemComparator<NT,T> itemCmp,
            AATreeSet<Item<NT,T>> statesInSet,
            AATreeMap<NT,AATreeSet<PredictItem<NT,T>>> predictStates,
            AATreeMap<NT,AATreeSet<CompleteItem<NT,T>>> completeStates,
            List<Item<NT,T>> frontQueue, List<Item<NT,T>> backQueue) {
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
        this.itemCmp = itemCmp;
        this.itemsInSet = statesInSet;
        this.predictItems = predictStates;
        this.completeItems = completeStates;
        this.frontQueue = frontQueue;
        this.backQueue = backQueue;
    }
    
    protected StateSet<NT,T> add(Item<NT,T> item) {
        
        return itemsInSet.contains(item) ?
                this :
                addNewItem(item);
        
    }
    
    private StateSet<NT,T> addNewItem(Item<NT,T> item) {
        
        AATreeSet<Item<NT,T>> newItemsInSet = itemsInSet.insert(item);
        List<Item<NT,T>> newBackQueue = backQueue.cons(item);
        return item.match(
                (predictItem) -> newStateSet(
                        newItemsInSet,
                        mapSetInsert(
                                predictItems,
                                predictItem.nextNonTerminal,
                                predictItem,
                                itemCmp.predictCmp),
                        completeItems,
                        newBackQueue),
                (scanItem) -> newStateSet(
                        newItemsInSet,
                        predictItems,
                        completeItems,
                        newBackQueue),
                (completeItem) -> newStateSet(
                        newItemsInSet,
                        predictItems,
                        mapSetInsert(
                                completeItems,
                                completeItem.lhs,
                                completeItem,
                                itemCmp.completeCmp),
                        newBackQueue));
        
    }
    
    private StateSet<NT,T> newStateSet(
            AATreeSet<Item<NT,T>> itemsInSet,
            AATreeMap<NT,AATreeSet<PredictItem<NT,T>>> predictItems,
            AATreeMap<NT,AATreeSet<CompleteItem<NT,T>>> completeItems,
            List<Item<NT,T>> backQueue) {
        
        return new StateSet<>(
                ntCmp, tCmp, itemCmp,
                itemsInSet, predictItems, completeItems,
                frontQueue, backQueue);
        
    }
    
    private <SetElem> AATreeMap<NT,AATreeSet<SetElem>> mapSetInsert(
            AATreeMap<NT,AATreeSet<SetElem>> map,
            NT key,
            SetElem element,
            Comparator<SetElem> elemCmp) {
        
        return map.insert(
                key,
                map.get(key).match(
                        (unit) -> AATreeSet.emptySet(elemCmp).insert(element),
                        (set) -> set.insert(element)));
        
    }
    
    protected Option<Tuple<Item<NT,T>,StateSet<NT,T>>> nextItem() {
        
        return frontQueue.match(
                (unit1) -> backQueue.reverse().match(
                        (unit2) -> Option.none(),
                        (cons) -> Option.some(Tuple.create(
                                cons.left, new StateSet<>(
                                        ntCmp, tCmp, itemCmp,
                                        itemsInSet, predictItems, completeItems,
                                        cons.right, new List.Nil<>())))),
                (cons) -> Option.some(Tuple.create(
                        cons.left, new StateSet<>(
                                ntCmp, tCmp, itemCmp,
                                itemsInSet, predictItems, completeItems,
                                cons.right, backQueue))));
        
    }
    
    protected AATreeSet<CompleteItem<NT,T>> completeItems(NT lhs) {
        
        return completeItems.get(lhs).match(
                (unit) -> AATreeSet.emptySet(itemCmp.completeCmp),
                (set) -> set);
        
    }
    
    protected AATreeSet<PredictItem<NT,T>> predictItems(NT nextNonTerminal) {
        
        return predictItems.get(nextNonTerminal).match(
                (unit) -> AATreeSet.emptySet(itemCmp.predictCmp),
                (set) -> set);
        
    }
    
}
