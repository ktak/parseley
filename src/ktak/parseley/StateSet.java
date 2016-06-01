package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

class StateSet<NT,T,R> {
    
    private final Comparator<NT> ntCmp;
    private final Comparator<T> tCmp;
    private final Comparator<Item<NT,T,R>> itemCmp;
    protected final AATreeSet<Item<NT,T,R>> itemsInSet;
    private final AATreeMap<NT,List<PredictItem<NT,T,R>>> predictItems;
    private final AATreeMap<NT,List<CompleteItem<NT,T,R>>> completeItems;
    private final List<Item<NT,T,R>> frontQueue;
    private final List<Item<NT,T,R>> backQueue;
    
    protected StateSet(Comparator<NT> ntCmp, Comparator<T> tCmp) {
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
        this.itemCmp = (i1, i2) -> Item.compare(ntCmp, i1, i2);
        this.itemsInSet = AATreeSet.emptySet(itemCmp);
        this.predictItems = AATreeMap.emptyMap(ntCmp);
        this.completeItems = AATreeMap.emptyMap(ntCmp);
        this.frontQueue = new List.Nil<>();
        this.backQueue = new List.Nil<>();
    }
    
    private StateSet(
            Comparator<NT> ntCmp, Comparator<T> tCmp,
            Comparator<Item<NT,T,R>> itemCmp,
            AATreeSet<Item<NT,T,R>> statesInSet,
            AATreeMap<NT,List<PredictItem<NT,T,R>>> predictItems,
            AATreeMap<NT,List<CompleteItem<NT,T,R>>> completeItems,
            List<Item<NT,T,R>> frontQueue, List<Item<NT,T,R>> backQueue) {
        this.ntCmp = ntCmp;
        this.tCmp = tCmp;
        this.itemCmp = itemCmp;
        this.itemsInSet = statesInSet;
        this.predictItems = predictItems;
        this.completeItems = completeItems;
        this.frontQueue = frontQueue;
        this.backQueue = backQueue;
    }
    
    protected StateSet<NT,T,R> add(Item<NT,T,R> item) {
        
        return itemsInSet.contains(item) ?
                this :
                addNewItem(item);
        
    }
    
    private StateSet<NT,T,R> addNewItem(Item<NT,T,R> item) {
        
        AATreeSet<Item<NT,T,R>> newItemsInSet = itemsInSet.insert(item);
        List<Item<NT,T,R>> newBackQueue = backQueue.cons(item);
        return item.match(
                (predictItem) -> newStateSet(
                        newItemsInSet,
                        mapListInsert(
                                predictItems,
                                predictItem.nextNonTerminal,
                                predictItem),
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
                        mapListInsert(
                                completeItems,
                                completeItem.leftHandSide,
                                completeItem),
                        newBackQueue));
        
    }
    
    private StateSet<NT,T,R> newStateSet(
            AATreeSet<Item<NT,T,R>> itemsInSet,
            AATreeMap<NT,List<PredictItem<NT,T,R>>> predictItems,
            AATreeMap<NT,List<CompleteItem<NT,T,R>>> completeItems,
            List<Item<NT,T,R>> backQueue) {
        
        return new StateSet<>(
                ntCmp, tCmp, itemCmp,
                itemsInSet, predictItems, completeItems,
                frontQueue, backQueue);
        
    }
    
    private <Elem> AATreeMap<NT,List<Elem>> mapListInsert(
            AATreeMap<NT,List<Elem>> map,
            NT key,
            Elem element) {
        
        return map.insert(
                key,
                map.get(key).match(
                        (unit) -> new List.Nil<Elem>().cons(element),
                        (list) -> list.cons(element)));
        
    }
    
    protected Option<Tuple<Item<NT,T,R>,StateSet<NT,T,R>>> nextItem() {
        
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
    
    protected List<CompleteItem<NT,T,R>> completeItems(NT lhs) {
        
        return completeItems.get(lhs).match(
                (unit) -> new List.Nil<>(),
                (list) -> list);
        
    }
    
    protected List<PredictItem<NT,T,R>> predictItems(NT nextNonTerminal) {
        
        return predictItems.get(nextNonTerminal).match(
                (unit) -> new List.Nil<>(),
                (list) -> list);
        
    }
    
}
