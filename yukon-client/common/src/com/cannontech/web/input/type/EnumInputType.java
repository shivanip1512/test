package com.cannontech.web.input.type;


public interface EnumInputType<T extends Enum<T>> extends InputType<T> {
	@Override
	Class<T> getTypeClass();
}
