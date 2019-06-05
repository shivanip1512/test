package com.cannontech.util;

import static org.junit.Assert.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
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
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DeleteSetRequest;
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.DutyCycleDrRequest;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.ListHierarchyRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingSet;

/**
 * Tests simple POJOs, immutable POJOs, and JavaBeans for equality.
 * 
 * For any given class this test will fully populate every property of the object with known data and test two
 * such created objects to make sure they are equal according to their .equals() method. Then each individual
 * property is changed and this object is tested to make sure it's not equal.
 * 
 * This will enforce that .equals() methods are updated properly when new fields are added or existing fields
 * are modified.
 */
public class SimpleEqualsTest {

    @Test
    public void test_EnergyCompanySetting() {
        testEqualsAndHashCode(EnergyCompanySetting.class, false);
    }
    
    @Test
    public void test_RegisterDeviceRequest() {
        testEqualsAndHashCode(RegisterDeviceRequest.class, true);
    }

    @Test
    public void test_AuthenticationRequest() {
        testEqualsAndHashCode(AuthenticationRequest.class, true);
    }

    @Test
    public void test_StandardResponse() {
        testEqualsAndHashCode(StandardResponse.class, true);
    }

    @Test
    public void test_MoveDeviceRequest() {
        testEqualsAndHashCode(MoveDeviceRequest.class, true);
    }

    @Test
    public void test_RuntimeReportJobRequest() {
        testEqualsAndHashCode(RuntimeReportJobRequest.class, true);
    }

    @Test
    public void test_CreateSetRequest() {
        testEqualsAndHashCode(CreateSetRequest.class, true);
    }

    @Test
    public void test_DeleteSetRequest() {
        testEqualsAndHashCode(DeleteSetRequest.class, true);
    }

    @Test
    public void test_MoveSetRequest() {
        testEqualsAndHashCode(MoveSetRequest.class, true);
    }

    @Test
    public void test_DutyCycleDrRequest() {
        testEqualsAndHashCode(DutyCycleDrRequest.class, true);
    }

    @Test
    public void test_DrResponse() {
        testEqualsAndHashCode(DrResponse.class, true);
    }

    @Test
    public void test_DrRestoreRequest() {
        testEqualsAndHashCode(DrRestoreRequest.class, true);
    }

    @Test
    public void test_BaseResponse() {
        testEqualsAndHashCode(BaseResponse.class, true);
    }

    @Test
    public void test_ListHierarchyRequest() {
        testEqualsAndHashCode(ListHierarchyRequest.class, true);
    }

    @Test
    public void test_HierarchyResponse() {
        testEqualsAndHashCode(HierarchyResponse.class, true);
    }

    @Test
    public void test_CompleteYukonPaos() {
        testEqualsAndHashCode(CompleteYukonPao.class, false);
    }

    @Test
    public void test_CompleteCapControlArea() {
        testEqualsAndHashCode(CompleteCapControlArea.class, false);
    }

    @Test
    public void test_CompleteCapControlFeeder() {
        testEqualsAndHashCode(CompleteCapControlFeeder.class, false);
    }

    @Test
    public void test_CompleteCapControlSpecialArea() {
        testEqualsAndHashCode(CompleteCapControlSpecialArea.class, false);
    }

    @Test
    public void test_CompleteCapControlSubstation() {
        testEqualsAndHashCode(CompleteCapControlSubstation.class, false);
    }

    @Test
    public void test_CompleteCapControlSubstationBus() {
        testEqualsAndHashCode(CompleteCapControlSubstationBus.class, false);
    }

    @Test
    public void test_CompleteDevice() {
        testEqualsAndHashCode(CompleteDevice.class, false);
    }

    @Test
    public void test_Ccu721() {
        testEqualsAndHashCode(Ccu721.class, false);
    }

    @Test
    public void test_CompleteCapBank() {
        testEqualsAndHashCode(CompleteCapBank.class, false);
    }

    @Test
    public void test_CompleteCbcBase() {
        testEqualsAndHashCode(CompleteCbcBase.class, false);
    }

    @Test
    public void test_CompleteOneWayCbc() {
        testEqualsAndHashCode(CompleteOneWayCbc.class, false);
    }

    @Test
    @Ignore("test causes NPE because of the way CompleteTwoWayCbc is setup")
    public void test_CompleteTwoWayCbc() {
        testEqualsAndHashCode(CompleteTwoWayCbc.class, false);
    }

    @Test
    public void test_CompleteZbEndpoint() {
        testEqualsAndHashCode(CompleteZbEndpoint.class, false);
    }

    @Test
    public void test_CompleteZbGateway() {
        testEqualsAndHashCode(CompleteZbGateway.class, false);
    }

    @Test
    public void test_CompleteDigiGateway() {
        testEqualsAndHashCode(CompleteDigiGateway.class, false);
    }

    @Test
    public void test_CompleteRegulator() {
        testEqualsAndHashCode(CompleteRegulator.class, false);
    }

    /**
     * We can't create real lists since we have no idea what type of object to place in them.
     * So we always insert a blank list but we implement .equals() so we can fake it out.
     */
    private static class MockList extends ForwardingList<Object> {
        int uniqueNumber;
        List<Object> delegate = Collections.emptyList();

        public MockList(int uniqueNumber) {
            this.uniqueNumber = uniqueNumber;
        }

        @Override
        public boolean equals(Object obj) {
            MockList other = (MockList) obj;
            return other.uniqueNumber == uniqueNumber;
        }

        @Override
        public int hashCode() {
            return uniqueNumber;
        }

        @Override
        protected List<Object> delegate() {
            return delegate;
        }
    }

    private static class MockSet extends ForwardingSet<Object> {
        int uniqueNumber;
        Set<Object> delegate = Collections.emptySet();

        public MockSet(int uniqueNumber) {
            this.uniqueNumber = uniqueNumber;
        }

        @Override
        public boolean equals(Object obj) {
            MockSet other = (MockSet) obj;
            return other.uniqueNumber == uniqueNumber;
        }

        @Override
        public int hashCode() {
            return uniqueNumber;
        }

        @Override
        protected Set<Object> delegate() {
            return delegate;
        }
    }

    private static class MockMap extends ForwardingMap<Object, Object> {
        int uniqueNumber;
        Map<Object, Object> delegate = Collections.emptyMap();

        public MockMap(int uniqueNumber) {
            this.uniqueNumber = uniqueNumber;
        }

        @Override
        public boolean equals(Object obj) {
            MockMap other = (MockMap) obj;
            return other.uniqueNumber == uniqueNumber;
        }

        @Override
        public int hashCode() {
            return uniqueNumber;
        }

        @Override
        protected Map<Object, Object> delegate() {
            return delegate;
        }
    }

    /**
     * Tests that two objects with the same properties are equal. Tests changing any one property on the
     * object causes
     * .equals() return false.
     */
    private void testEqualsAndHashCode(Class<?> aClass, boolean immutable) {
        try {
            ObjectPermutations objs = getPopulatedObjects(aClass, immutable);
            assertEquals(aClass.getSimpleName() + ".equals() failed test. ", objs.getObjectA(), objs.getObjectB());
            assertEquals(aClass.getSimpleName() + ".hashCode() failed test. ", 
                objs.getObjectA().hashCode(), objs.getObjectB().hashCode());

            for (ModifiedObject obj : objs.getInequalPermuations()) {
                String propertyDesc = obj.getPropertyType().getSimpleName() + " " + obj.getPropertyName();
                assertObjectsNotEqual(propertyDesc, obj.getObject(), objs.getObjectA());
                for (ModifiedObject obj2 : objs.getInequalPermuations()) {
                    if (obj != obj2) {
                        assertObjectsNotEqual(propertyDesc, obj.getObject(), obj2.getObject());
                    }
                }
            }
        } catch (IntrospectionException | IllegalArgumentException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertObjectsNotEqual(String propertyDescription, Object objA, Object objB) {
        String objType = objA.getClass().getSimpleName();
        if (objA.equals(objB) || objB.equals(objA)) {
            fail(objType + ".equals() returned true but objects are not equal! Property: " + propertyDescription +
                " is different. If this property is an object, it's .equals() might not be correct.");
        }
        if (objA.hashCode() == objB.hashCode()) {
            fail(objType + ".hashCode() should not match for inequal objects! Property: " + propertyDescription +
                " is different. If this property is an object, it's .hashCode() might not be correct.");
        }
    }

    /**
     * Generates two objects which have identical property values and should be regarded as equal: ObjectA and
     * ObjectB
     * then generates inequal objects by changing one property at a time: InequalPermuations
     * 
     */
    private ObjectPermutations getPopulatedObjects(Class<?> type, boolean immutable) throws IntrospectionException,
            IllegalArgumentException, ReflectiveOperationException {
        Object objA = ObjectGenerator.newDefault().getNewPopulatedInstance(type, immutable);
        Object objB = ObjectGenerator.newDefault().getNewPopulatedInstance(type, immutable);

        ObjectGenerator nonDefaultObjectGenerator = new ObjectGenerator(500, false);
        List<SetterMethod> setterMethods = getSetterMethods(type);
        List<ModifiedObject> inequalObjectPermutations = new ArrayList<>();

        if (immutable) {
            Constructor<?> c = type.getConstructors()[0];
            int numberOfArgs = c.getParameterTypes().length;
            Object[] originalArgValues = new Object[numberOfArgs];

            // Get the original arguments
            for (int i = 0; i < numberOfArgs; i++) {
                originalArgValues[i] = ObjectGenerator.newDefault().getObject(c.getParameterTypes()[i], immutable);
            }
            // for each argument lets create a new object but just change a single value
            for (int i = 0; i < numberOfArgs; i++) {
                Object[] oneDifferentArgValues = new Object[numberOfArgs];
                System.arraycopy(originalArgValues, 0, oneDifferentArgValues, 0, numberOfArgs);
                if (c.getParameterTypes()[i].getSimpleName().toLowerCase().equals("boolean")) {
                    oneDifferentArgValues[i] = !(Boolean)oneDifferentArgValues[i];
                } else {
                    oneDifferentArgValues[i] = nonDefaultObjectGenerator.getObject(c.getParameterTypes()[i], immutable);
                }
                Object obj = c.newInstance(oneDifferentArgValues);
                inequalObjectPermutations.add(new ModifiedObject(obj, c.getParameterTypes()[i], ""));
            }
        } else {
            for (SetterMethod method : setterMethods) {
                Object obj = ObjectGenerator.newDefault().getNewPopulatedInstance(type, immutable);
                method.getSetterMethod().invoke(obj,
                    nonDefaultObjectGenerator.getObject(method.getSetterType(), immutable));
                inequalObjectPermutations.add(new ModifiedObject(obj, method.getPropertyDescriptor().getPropertyType(),
                    method.getPropertyDescriptor().getName()));
            }
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
        private Class<?> propertyType;
        private String propertyName;

        public ModifiedObject(Object object, Class<?> propertyType, String propertyName) {
            this.object = object;
            this.propertyType = propertyType;
            this.propertyName = propertyName;
        }

        public Object getObject() {
            return object;
        }

        public Class<?> getPropertyType() {
            return propertyType;
        }

        public String getPropertyName() {
            return propertyName;
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

        /**
         * Returns an instance of objType with all fields initialized. No two types will be the same for any
         * given
         * ObjectGenerator (e.g. if the object has three Strings each will be initialized to a different
         * value)
         */
        public Object getObject(Class<?> objType, boolean immutable) throws ReflectiveOperationException,
                IntrospectionException {
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
            case "LocalDate" : return new LocalDate().plusDays(uniqueNumber);
            case "Instant" : return new Instant(uniqueNumber);
            case "Date" : return new Date(uniqueNumber);
            case "PaoIdentifier" :
                return new PaoIdentifier(uniqueNumber, PaoType.values()[uniqueNumber % PaoType.values().length]);
            //@formatter:on
            default:
                // It would be nice to initialize collections properly like we do for arrays but there
                // isn't an easy way to determine the type of the list. If we want to properly initialize
                // these collections we will need to determine the generic type.
                if (objType.isAssignableFrom(MockList.class)) {
                    return new MockList(uniqueNumber);
                }
                if (objType.isAssignableFrom(MockSet.class)) {
                    return new MockSet(uniqueNumber);
                }
                if (objType.isAssignableFrom(MockMap.class)) {
                    return new MockMap(uniqueNumber);
                }
                if (objType.isArray()) {
                    int numItems = uniqueNumber % 10;
                    Object[] array = (Object[]) Array.newInstance(objType.getComponentType(), numItems);
                    for (int i = 0; i < numItems; i++) {
                        array[i] = getObject(objType.getComponentType(), immutable);
                    }
                    return array;
                }
                if (objType.isEnum()) {
                    return objType.getEnumConstants()[uniqueNumber % objType.getEnumConstants().length];
                }

                return getNewPopulatedInstance(objType, immutable);
            }
        }

        /**
         * Instantiates a new object using the default constructor and populates all setters using the
         * objectGenerator
         */
        private Object getNewPopulatedInstance(Class<?> type, boolean immutable) throws IntrospectionException,
                ReflectiveOperationException {
            if (type.isInterface()) {
                throw new IllegalArgumentException("Cannot instantiate " + type.getSimpleName()
                    + " because it is an interface.");
            }
            List<SetterMethod> setterMethods = getSetterMethods(type);
            Object obj = null;
            if (immutable) {
                if (!setterMethods.isEmpty()) {
                    throw new IllegalArgumentException(type.getSimpleName() + " has setters and is marked immutable.");
                }
                if (type.getConstructors().length < 1) {
                    throw new IllegalArgumentException(type.getSimpleName()
                        + " has either no public constructors. SimpleEqualsTest needs a public constructor "
                        + "for immutable objects.");
                }
                Constructor<?> c = type.getConstructors()[0];
                Object[] args = new Object[c.getParameterTypes().length];
                for (int i = 0; i < c.getParameterTypes().length; i++) {
                    args[i] = getObject(c.getParameterTypes()[i], immutable);
                }
                obj = c.newInstance(args);
            } else {
                obj = type.newInstance();
                for (SetterMethod method : setterMethods) {
                    Object objectToSet = getObject(method.getSetterType(), immutable);
                    try {
                        method.getSetterMethod().invoke(obj, objectToSet);
                    } catch (IllegalArgumentException e) {
                        String paramType = method.getSetterType().getSimpleName();
                        String wrongType = objectToSet.getClass().getSimpleName();
                        throw new IllegalArgumentException("Trying to call setter: "
                            + method.getSetterMethod().getName() + " with " + wrongType
                            + " failed because it is the wronge type. Expected: " + paramType, e);
                    }
                }
            }
            return obj;
        }
    }

    // Something similar to this could be used to instantiate interfaces
    // private static List<Class<?>> getClassesForInterface(Class<?> aClass) {
    // if (!aClass.isInterface()) {
    // throw new IllegalArgumentException(aClass.getSimpleName() + " is not an interface!");
    // }
    // List<Class<?>> classes = new ArrayList<>();
    // BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
    // ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);
    //
    // s.addIncludeFilter(new AssignableTypeFilter(aClass));
    // s.setIncludeAnnotationConfig(false);
    // s.scan("com.cannontech.");
    // for (String beanDefName : bdr.getBeanDefinitionNames()) {
    // BeanDefinition beanDef = bdr.getBeanDefinition(beanDefName);
    // try {
    // String beanClassName = beanDef.getBeanClassName();
    // classes.add(Class.forName(beanClassName));
    // } catch (ClassNotFoundException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // return classes;
    // }
}
