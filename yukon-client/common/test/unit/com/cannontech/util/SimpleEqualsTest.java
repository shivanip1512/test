package com.cannontech.util;

import static org.junit.Assert.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Instant;
import org.junit.Ignore;
import org.junit.Test;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.Ccu721;
import com.cannontech.common.pao.model.CompleteCapBank;
import com.cannontech.common.pao.model.CompleteCapControlArea;
import com.cannontech.common.pao.model.CompleteCapControlFeeder;
import com.cannontech.common.pao.model.CompleteCapControlSpecialArea;
import com.cannontech.common.pao.model.CompleteCapControlSubstation;
import com.cannontech.common.pao.model.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.model.CompleteCbcBase;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.model.CompleteDigiGateway;
import com.cannontech.common.pao.model.CompleteOneWayCbc;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.model.CompleteTwoWayCbc;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.model.CompleteZbEndpoint;
import com.cannontech.common.pao.model.CompleteZbGateway;

public class SimpleEqualsTest {
    // TODO: support immutable objects
    @Test
    public void test_CompleteYukonPaos() throws Exception {
        testClassEqualsMethod(CompleteYukonPao.class);
    }

    @Test
    public void test_CompleteCapControlArea() throws Exception {
        testClassEqualsMethod(CompleteCapControlArea.class);
    }

    @Test
    public void test_CompleteCapControlFeeder() throws Exception {
        testClassEqualsMethod(CompleteCapControlFeeder.class);
    }

    @Test
    public void test_CompleteCapControlSpecialArea() throws Exception {
        testClassEqualsMethod(CompleteCapControlSpecialArea.class);
    }

    @Test
    public void test_CompleteCapControlSubstation() throws Exception {
        testClassEqualsMethod(CompleteCapControlSubstation.class);
    }

    @Test
    public void test_CompleteCapControlSubstationBus() throws Exception {
        testClassEqualsMethod(CompleteCapControlSubstationBus.class);
    }

    @Test
    public void test_CompleteDevice() throws Exception {
        testClassEqualsMethod(CompleteDevice.class);
    }

    @Test
    public void test_Ccu721() throws Exception {
        testClassEqualsMethod(Ccu721.class);
    }

    @Test
    public void test_CompleteCapBank() throws Exception {
        testClassEqualsMethod(CompleteCapBank.class);
    }

    @Test
    public void test_CompleteCbcBase() throws Exception {
        testClassEqualsMethod(CompleteCbcBase.class);
    }

    @Test
    public void test_CompleteOneWayCbc() throws Exception {
        testClassEqualsMethod(CompleteOneWayCbc.class);
    }

    @Test
    @Ignore("test causes NPE because of the way CompleteTwoWayCbc is setup")
    public void test_CompleteTwoWayCbc() throws Exception {
        testClassEqualsMethod(CompleteTwoWayCbc.class);
    }

    @Test
    public void test_CompleteZbEndpoint() throws Exception {
        testClassEqualsMethod(CompleteZbEndpoint.class);
    }

    @Test
    public void test_CompleteZbGateway() throws Exception {
        testClassEqualsMethod(CompleteZbGateway.class);
    }

    @Test
    public void test_CompleteDigiGateway() throws Exception {
        testClassEqualsMethod(CompleteDigiGateway.class);
    }

    @Test
    public void test_CompleteRegulator() throws Exception {
        testClassEqualsMethod(CompleteRegulator.class);
    }

    /**
     * Tests that two objects with the same properties are equal. Tests changing any one property on the object causes
     * .equals() return false.
     */
    private void testClassEqualsMethod(Class<?> aClass) throws IntrospectionException, IllegalArgumentException,
            ReflectiveOperationException {
        ObjectPermutations objs = getObjectInstancePermutations(aClass);
        assertEquals(aClass.getSimpleName() + ".equals() failed test. ", objs.getObjectA(), objs.getObjectB());

        for (ModifiedObject obj : objs.getInequalPermuations()) {
            assertObjectsNotEqual(obj, objs.getObjectA());
            for (Object obj2 : objs.getInequalPermuations()) {
                if (obj != obj2) {
                    assertObjectsNotEqual(obj, obj2);
                }
            }
        }
    }

    private void assertObjectsNotEqual(ModifiedObject modifiedObjA, Object objB) {
        Object objA = modifiedObjA.getObject();
        if (objA.equals(objB) || objB.equals(objA)) {
            PropertyDescriptor modifiedProperty = modifiedObjA.getModifiedProperty();
            String propertyName = modifiedProperty.getName();
            String propertyType = modifiedProperty.getPropertyType().getSimpleName();
            String objType = objA.getClass().getSimpleName();
            fail(objType + ".equals() returned true but objects are not equal! Property: " + propertyType + " "
                + propertyName + " is different. If this property is an object, it's .equals() might not be correct.");
        }
    }

    /**
     * Generates two objects which have identical property values and should be regarded as equal: ObjectA and
     * ObjectB
     * then generates inequal objects by changing one property at a time: InequalPermuations
     * 
     */
    private ObjectPermutations getObjectInstancePermutations(Class<?> type) throws IntrospectionException,
            IllegalArgumentException, ReflectiveOperationException {
        Object objA = ObjectGenerator.newDefault().getNewPopulatedInstance(type);
        Object objB = ObjectGenerator.newDefault().getNewPopulatedInstance(type);

        ObjectGenerator objectGenerator = new ObjectGenerator(500, false);
        List<SetterMethod> setterMethods = getSetterMethods(type);
        List<ModifiedObject> inequalObjectPermutations = new ArrayList<>();
        for (SetterMethod method : setterMethods) {
            Object obj = ObjectGenerator.newDefault().getNewPopulatedInstance(type);
            method.getSetterMethod().invoke(obj, objectGenerator.getObject(method.getSetterType()));
            inequalObjectPermutations.add(new ModifiedObject(obj, method.getPropertyDescriptor()));
        }

        ObjectPermutations objectPermutations = new ObjectPermutations();
        objectPermutations.setObjectA(objA);
        objectPermutations.setObjectB(objB);
        objectPermutations.setInequalPermuations(inequalObjectPermutations);

        return objectPermutations;
    }

    private static List<SetterMethod> getSetterMethods(Class<?> type) throws IntrospectionException {
        List<SetterMethod> setterMethods = new ArrayList<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            if (property.getWriteMethod() != null) {
                setterMethods.add(new SetterMethod(property));
            }
        }
        return setterMethods;
    }

    public static class SetterMethod {
        private Method setterMethod;
        private Class<?> setterType;
        private PropertyDescriptor propertyDescriptor;

        public SetterMethod(PropertyDescriptor propertyDescriptor) {
            setterMethod = propertyDescriptor.getWriteMethod();
            if (setterMethod == null) {
                throw new IllegalArgumentException("PropertyDescriptor must be a setter method");
            }
            setterType = propertyDescriptor.getPropertyType();
            this.propertyDescriptor = propertyDescriptor;
        }

        public Method getSetterMethod() {
            return setterMethod;
        }

        public Class<?> getSetterType() {
            return setterType;
        }

        public PropertyDescriptor getPropertyDescriptor() {
            return propertyDescriptor;
        }
    }

    public static class ModifiedObject {
        private Object object;
        private PropertyDescriptor modifiedProperty;

        public ModifiedObject(Object object, PropertyDescriptor modifiedProperty) {
            this.object = object;
            this.modifiedProperty = modifiedProperty;
        }

        public Object getObject() {
            return object;
        }

        public PropertyDescriptor getModifiedProperty() {
            return modifiedProperty;
        }

    }

    public static class ObjectPermutations {
        private Object objectA;
        private Object objectB;
        private List<ModifiedObject> inequalPermuations;

        public Object getObjectA() {
            return objectA;
        }

        public void setObjectA(Object objectA) {
            this.objectA = objectA;
        }

        public Object getObjectB() {
            return objectB;
        }

        public void setObjectB(Object objectB) {
            this.objectB = objectB;
        }

        public List<ModifiedObject> getInequalPermuations() {
            return inequalPermuations;
        }

        public void setInequalPermuations(List<ModifiedObject> inequalPermuations) {
            this.inequalPermuations = inequalPermuations;
        }
    }

    public static class ObjectGenerator {
        private int uniqueNumber = 1;
        private boolean toggledBoolean;

        public ObjectGenerator(int startNum, boolean startBoolean) {
            uniqueNumber = startNum;
            toggledBoolean = startBoolean;
        }

        public static ObjectGenerator newDefault() {
            return new ObjectGenerator(1, true);
        }

        public Object getObject(Class<?> objType) throws ReflectiveOperationException, IntrospectionException {
            uniqueNumber++;

            switch (objType.getSimpleName()) {
            //@formatter:off
            case "String" : return "bla_" + uniqueNumber;
            case "int" : case "Integer" : return uniqueNumber;
            case "double" : case "Double" : return uniqueNumber + 0.56649198465465165;
            case "float" : case "Float" : return uniqueNumber + 0.1212647332153453f;
            case "boolean" : case "Boolean" :
                toggledBoolean = !toggledBoolean;
                return toggledBoolean;
            case "char" : case "Character" : return (char) uniqueNumber;
            case "byte" : case "Byte" : return (byte) uniqueNumber;
            case "long" : case "Long" : return (long) uniqueNumber;
            case "short" : case "Short" : return (short) uniqueNumber;
            case "List" : throw new IllegalArgumentException("List is not supported by this test.");
            case "Instant" : return new Instant(uniqueNumber);
            case "Date" : return new Date(uniqueNumber);
            case "PaoIdentifier" :
                return new PaoIdentifier(uniqueNumber, PaoType.values()[uniqueNumber % PaoType.values().length]);
            //@formatter:on
            default:
                if (objType.isArray()) {
                    int numItems = uniqueNumber % 10;
                    Object[] array = (Object[]) Array.newInstance(objType.getComponentType(), numItems);
                    for (int i = 0; i < numItems; i++) {
                        array[i] = getObject(objType.getComponentType());
                    }
                    return array;
                }
                if (objType.isEnum()) {
                    return objType.getEnumConstants()[uniqueNumber % objType.getEnumConstants().length];
                }
                return getNewPopulatedInstance(objType);
            }
        }

        /**
         * Instantiates a new object using the default constructor and populates all setters using the
         * objectGenerator
         */
        private Object getNewPopulatedInstance(Class<?> type) throws IntrospectionException,
                ReflectiveOperationException {
            Object obj = type.newInstance();
            List<SetterMethod> setterMethods = getSetterMethods(type);
            for (SetterMethod method : setterMethods) {
                Object objectToSet = getObject(method.getSetterType());
                try {
                    method.getSetterMethod().invoke(obj, objectToSet);
                } catch (IllegalArgumentException e) {
                    String paramType = method.getSetterType().getSimpleName();
                    String wrongType = objectToSet.getClass().getSimpleName();
                    throw new IllegalArgumentException("Trying to call setter: " + method.getSetterMethod().getName()
                        + " with " + wrongType + " failed because it is the wronge type. Expected: " + paramType, e);
                }
            }
            return obj;
        }
    }
}
