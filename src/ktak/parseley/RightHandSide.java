package ktak.parseley;

import java.util.Comparator;

import ktak.immutablejava.Either;
import ktak.immutablejava.List;

public class RightHandSide<NT,T> {
    
    protected final List<Either<NT,T>> rhs;
    
    public RightHandSide() { rhs = new List.Nil<>(); }
    
    protected RightHandSide(List<Either<NT,T>> rhs) {
        this.rhs = rhs;
    }
    
    protected static class RHSComparator<NT,T> implements Comparator<RightHandSide<NT,T>> {
        
        private final Comparator<Either<NT,T>> cmp;
        
        protected RHSComparator(Comparator<NT> ntCmp, Comparator<T> tCmp) {
            this.cmp = (e1, e2) -> e1.match(
                    (left1) -> e2.match(
                            (left2) -> ntCmp.compare(left1, left2),
                            (right2) -> -1),
                    (right1) -> e2.match(
                            (left2) -> 1,
                            (right2) -> tCmp.compare(right1, right2)));
        }
        
        @Override
        public int compare(RightHandSide<NT, T> rhs1, RightHandSide<NT, T> rhs2) {
            return rhs1.rhs.compareTo(rhs2.rhs, cmp);
        }
        
    }
    
    public RightHandSide<NT,T> thenNonTerminal(NT nt) {
        return new RightHandSide<NT,T>(
                rhs.append(
                        new List.Nil<Either<NT,T>>()
                        .cons(Either.left(nt))));
    }
    
    public RightHandSide<NT,T> thenTerminal(T t) {
        return new RightHandSide<NT,T>(
                rhs.append(
                        new List.Nil<Either<NT,T>>()
                        .cons(Either.right(t))));
    }
    
}
