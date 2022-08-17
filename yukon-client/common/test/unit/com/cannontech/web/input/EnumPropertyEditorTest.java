package com.cannontech.web.input;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumPropertyEditorTest {
	
	@Test
	public void test_PlainEnum_Pass() {
		EnumPropertyEditor<PlainEnum> editor = new EnumPropertyEditor<>(PlainEnum.class);
		
		editor.setAsText("ENUM1");
		assertSame(editor.getValue(), PlainEnum.ENUM1);
		
		editor.setAsText("ENUM2");
		assertSame(editor.getValue(), PlainEnum.ENUM2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_PlainEnum_fail() {
		EnumPropertyEditor<PlainEnum> editor = new EnumPropertyEditor<>(PlainEnum.class);

		editor.setAsText("NOT_A_REAL_ENUM"); // should throw
		
		fail("NOT_A_REAL_ENUM should have thrown an exception");
	}
	
	public enum PlainEnum {ENUM1, ENUM2;}
}
