package com.cannontech.stars.util.filter;

import java.util.Collection;

import org.springframework.jdbc.core.SqlProvider;

import com.google.common.collect.ImmutableList;


public interface JoinTable extends SqlProvider {
    
    public Collection<JoinTable> EMPTY_JOINTABLES = ImmutableList.of();
    
    public JoinTable getDependency();
    
}
