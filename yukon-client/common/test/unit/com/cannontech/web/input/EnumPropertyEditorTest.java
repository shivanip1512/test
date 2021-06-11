package com.cannontech.web.input;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumPropertyEditorTest {
	
	@Test
	public void test_PlainEnum_Pass() {
		EnumPropertyEditor<PlainEnum> editor = new EnumPropertyEditor<>(PlainEnum.class);
		
		editor.setAsText("ENUM1");
		assertSame(editor.getValue(), PlainEnum.ENUM1);
		
		editor.setAsText("ENUM2");
		assertSame(editor.getValue(), PlainEnum.ENUM2);
	}

    @Test
    public void test_PlainEnum_fail() {
        EnumPropertyEditor<PlainEnum> editor = new EnumPropertyEditor<>(PlainEnum.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            editor.setAsText("NOT_A_REAL_ENUM"); // should throw
        });
    }
	
	public enum PlainEnum {ENUM1, ENUM2;}
}
