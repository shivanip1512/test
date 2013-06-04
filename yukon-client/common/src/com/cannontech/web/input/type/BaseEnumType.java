package com.cannontech.web.input.type;

/**
 * This is a specialization of {@link BaseEnumeratedType} for use only with Java enums.  It helps us get around
 * some issues with generics.
 */
public abstract class BaseEnumType<T extends Enum<T>> extends BaseEnumeratedType<T> implements EnumInputType<T> {

}
