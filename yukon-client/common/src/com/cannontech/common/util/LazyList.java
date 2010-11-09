package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

public class LazyList<T> extends ForwardingList<T> {
    private final static class SimpleSupplier<T> implements Supplier<T> {
        private final Class<T> classToSupply;
        public SimpleSupplier(Class<T> classToSupply) {
            this.classToSupply = classToSupply;
        }

        @Override
        public T get() {
            try {
                return classToSupply.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

	private List<T> backingList;
	private Supplier<T> supplier;

	public LazyList(List<T> backingList, Supplier<T> supplier) {
		this.backingList = Lists.newArrayList(backingList);
		this.supplier = supplier;
	}

	@Override
	protected List<T> delegate() {
		return this.backingList;
	}

	@Override
	public T get(int index) {
		while (index >= backingList.size()) {
			backingList.add(supplier.get());
		}

		return backingList.get(index);
	}

	public static <U> LazyList<U> ofInstance(Class<U> instanceClass) {
        return new LazyList<U>(new ArrayList<U>(), new SimpleSupplier<U>(instanceClass));
	}
}
