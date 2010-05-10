package com.cannontech.common.util;

import com.google.common.base.Function;

public class IdentityFunction<T> implements Function<T, T> {

	public T apply(T from) {
		return from;
	};
}
