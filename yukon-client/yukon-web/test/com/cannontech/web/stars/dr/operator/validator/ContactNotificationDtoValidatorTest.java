package com.cannontech.web.stars.dr.operator.validator;

import static com.cannontech.common.model.ContactNotificationType.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.service.impl.PhoneNumberFormattingServiceImpl;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactNotificationDtoValidatorTest {

    private ContactNotificationDtoValidator validator;

    @Before
    public void setup() {
        validator = new ContactNotificationDtoValidator();
        ReflectionTestUtils.setField(validator, "phoneNumberFormattingService", new PhoneNumberFormattingServiceImpl());
    }

    @Test
    public void testPhoneNumber() {
        assertPhoneNum(true, "1");
        assertPhoneNum(true, "1234567890-x() ");
        assertPhoneNum(true, "(123)-456-7890 x1234 ");

        assertPhoneNum(false, "");
        assertPhoneNum(false, " ");
        assertPhoneNum(false, " 605 & 123");
        assertPhoneNum(false, " Bob@email.com");
    }
    
    @Test
    public void testEmailValidation() {
        assertEmail(false, "");
        assertEmail(false, "bad");
        assertEmail(false, "bad@Test");
        assertEmail(false, "bad@localhost");
        assertEmail(false, "bad@127.0.0.1");

        assertEmail(true, "good@Test.com");
        assertEmail(true, "$A12345@Test.com");
        assertEmail(true, "_somename@Test.com");
        assertEmail(true, "!def!xyz%abc@Test.com");
        assertEmail(true, "custom/department=shipping@Test.com");
    }

    public void assertPhoneNum(boolean good, String phoneNum) {
        ContactNotificationDto dto = new ContactNotificationDto();
        dto.setNotificationValue(phoneNum);

        Errors errors = new BindException(dto, "dto");

        List<ContactNotificationType> phoneTypes
            = Arrays.asList(FAX, CALL_BACK_PHONE, CELL_PHONE, HOME_PHONE, PHONE, WORK_PHONE);

        for (ContactNotificationType notifType : phoneTypes) {
            dto.setContactNotificationType(notifType);
            validator.validate(dto, errors);
            assertEquals("Phone Number: " + phoneNum + " validated incorrectly! ",!good, errors.hasErrors());
        }
    }

    private void assertEmail(boolean good, String email) {
        ContactNotificationDto dto = new ContactNotificationDto();
        dto.setNotificationValue(email);

        Errors errors = new BindException(dto, "dto");

        for (ContactNotificationType notifType : Arrays.asList(EMAIL,EMAIL_TO_CELL,EMAIL_TO_PAGER)) {
            dto.setContactNotificationType(notifType);
            validator.validate(dto, errors);
            assertEquals("Email: " + email + " validated incorrectly! ", !good, errors.hasErrors());
        }
    }
}