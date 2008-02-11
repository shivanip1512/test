package com.cannontech.cbc.cache.filters;

public interface CacheFilter<E>
{
	public boolean valid(E e);
}
