package com.cannontech.messaging.serialization.thrift.test.validator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class AutoInitializedClassValidator<T> extends ClassValidator<T> {

    private long seed;
    private boolean isAbstract;

    protected AutoInitializedClassValidator(Class<T> clazz) {
        this(clazz, 0);
    }

    protected AutoInitializedClassValidator(Class<T> clazz, long seed) {
        super(clazz, null);
        this.seed = seed;
        this.isAbstract = Modifier.isAbstract( clazz.getModifiers() );
    }

    public T getDefaultObject() {
        return getDefaultObject(new RandomGenerator(this.seed));
    }

    public T getDefaultObject(RandomGenerator generator) {
        T obj = createObject();
        initControlObject(obj, generator);
        return obj;
    }

    public List<T> getDefaultObjectList(RandomGenerator generator) {
        return getDefaultObjectList(generator, generator.generateInt(2, 5));
    }

    public List<T> getDefaultObjectList(RandomGenerator generator, int size) {
        List<T> list = new ArrayList<T>(size);

        for (int i = 0; i < size; i++) {
            list.add(getDefaultObject(generator));
        }
        return list;
    }

    public ValidationResult validate(T value) {
        return validate(value, getDefaultObject());
    }

    private void initControlObject(T ctrlObj, RandomGenerator generator) {

        ClassValidator<? super T> parentValidator = null;
        try {
            parentValidator = getParentValidator();
        }
        catch (Exception e) {}

        if (parentValidator != null && parentValidator instanceof AutoInitializedClassValidator) {
            ((AutoInitializedClassValidator<? super T>) parentValidator).initControlObject(ctrlObj, generator);
        }

        populateExpectedValue(ctrlObj, generator);
    }

    protected T createObject() {
        try {
            return getValidatedClass().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {

        }
        return null;
    }

    public abstract void populateExpectedValue(T ctrlObj, RandomGenerator generator);

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    protected void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    protected <C> AutoInitializedClassValidator<C> getAutoValidatorFor(Class<C> elementClass) {
        return (AutoInitializedClassValidator<C>) getValidatorSvc().findValidator(elementClass);
    }

    protected <C> C getDefaultObjectFor(Class<C> elementClass, RandomGenerator generator) {
        return getAutoValidatorFor(elementClass).getDefaultObject(generator);
    }

    protected <C> List<C> getDefaultObjectListFor(Class<C> elementClass, RandomGenerator generator) {
        return getAutoValidatorFor(elementClass).getDefaultObjectList(generator);
    }

    protected <C> List<C> getDefaultObjectListFor(Class<C> elementClass, RandomGenerator generator, int size) {
        return getAutoValidatorFor(elementClass).getDefaultObjectList(generator, size);
    }
    
    protected <C> Vector<C> getDefaultObjectVectorFor(Class<C> elementClass, RandomGenerator generator) {
        return new Vector<>(getAutoValidatorFor(elementClass).getDefaultObjectList(generator));
    }

    protected <C> Vector<C> getDefaultObjectVectorFor(Class<C> elementClass, RandomGenerator generator, int size) {
        return new Vector<>(getAutoValidatorFor(elementClass).getDefaultObjectList(generator, size));
    }
}
