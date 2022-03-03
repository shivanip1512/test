package com.cannontech.messaging.serialization.thrift.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;
import com.cannontech.messaging.serialization.thrift.test.validator.Validator;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidatorService;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidatorServiceImpl;

@SuppressWarnings("rawtypes")
public abstract class MessageSerializationTestBase extends ContextAwareTestBase{

    protected ThriftMessageFactory messageFactory;
    protected Collection<Validator> validatorList;
    protected ValidatorService validatorSvc;

    @Override
    protected void setUpTest(String... contextURI) {
        super.setUpTest(contextURI);
        
        messageFactory = createMessageFactory();

        validatorSvc = new ValidatorServiceImpl();

        validatorList = createValidatorList();

        for (Validator v : validatorList) {
            v.setValidatorSvc(validatorSvc);
            validatorSvc.registerValidator(v);
        }
    }

    @SuppressWarnings({ "unchecked" })
    protected ThriftMessageFactory createMessageFactory() {
        return new ThriftMessageFactory((Collection) appContext.getBeansOfType(ThriftSerializer.class).values());
    }

    protected Collection<Validator> createValidatorList() {
        return appContext.getBeansOfType(Validator.class).values();
    }

    protected void checkResults(ValidationResult result) {
        if (result.hasError()) {
            fail(ValidationHelper.formatErrorString(result));
        }
    }
}
