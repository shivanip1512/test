package com.cannontech.common.util.predicate;

import java.util.List;

public class AggregateAndPredicate<T> implements Predicate<T> {

    private List<Predicate<T>> predicates;
    
    public AggregateAndPredicate(List<Predicate<T>> predicatesToCheck) {
        
        this.predicates = predicatesToCheck;
    }
    
    @Override
    public boolean evaluate(T object) {

        for (Predicate<T> p : this.predicates) {
            
            if (!p.evaluate(object)) {
                return false;
            }
        }
        
        return true;
    }

}
