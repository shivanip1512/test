package com.cannontech.messaging.serialization.thrift.test;

import java.util.Collection;

import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;
import com.cannontech.messaging.serialization.thrift.test.validator.Validator;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidatorService;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidatorServiceImpl;

public abstract class MessageSerializationTestBase {

    protected ApplicationContext appContext;
    protected ThriftMessageFactory messageFactory;
    protected Collection<Validator> globalValidatorList;
    protected ValidatorService validatorSvc;

    protected MessageSerializationTestBase() {
        setUpTest(getContextUri());
    }

    protected abstract String[] getContextUri();

    protected void setUpTest(String... contextURI) {
        appContext = new ClassPathXmlApplicationContext(contextURI);
        
        messageFactory =
            new ThriftMessageFactory((Collection) appContext.getBeansOfType(ThriftSerializer.class).values());

        validatorSvc = new ValidatorServiceImpl();

        globalValidatorList = appContext.getBeansOfType(Validator.class).values();

        for (Validator v : globalValidatorList) {
            v.setValidatorSvc(validatorSvc);
            validatorSvc.registerValidator(v);
        }
    }

    protected void checkResults(ValidationResult result) {
        if (result.hasError()) {           
            Assert.fail(ValidationHelper.formatErrorString(result));
        }
    }
}
