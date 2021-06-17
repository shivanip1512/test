package com.cannontech.services.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import com.cannontech.core.dynamic.AllPointDataListener;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.dynamic.RichPointDataService;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.dynamic.impl.AsyncDynamicDataSourceImpl;
import com.cannontech.core.dynamic.impl.PointServiceImpl;
import com.cannontech.core.dynamic.impl.RichPointDataServiceImpl;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.SepReportedAddressDao;
import com.cannontech.dr.dao.impl.ExpressComReportedAddressDaoImpl;
import com.cannontech.dr.dao.impl.SepReportedAddressDaoImpl;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.messaging.activemq.DigiSmartUpdateListener;
import com.google.common.collect.ImmutableList;

/**
 * The test is intended to ensure all JMS Object messages only use classes within the whitelisted namespaces
 */
public class WhitelistedPackagesTest {
    private final static List <String> serializablePackages;
    private Map<String, List<Class<?>>> excludeClassMap;

    private static List<String> trustedPackages = ImmutableList.of("com.cannontech.amr.rfn.message",
        "com.cannontech.common.rfn.message", "com.cannontech.dr.dao", "com.cannontech.thirdparty.messaging",
        "com.cannontech.core.dynamic");

    static {
        serializablePackages = ImmutableList.of("java.lang","javax.security","java.util","javax.jms","com.thoughtworks.xstream.mapper","org.joda","com.google.common.collect","com.cannontech");
    }

    @BeforeEach
    public void setup() {
        excludeClassMap = new HashMap<String, List<Class<?>>>();
        List<Class<?>> excludeClassList = new ArrayList<Class<?>>();

        excludeClassList.add(ExpressComReportedAddressDao.class);
        excludeClassList.add(SepReportedAddressDao.class);
        excludeClassList.add(ExpressComReportedAddressDaoImpl.class);
        excludeClassList.add(SepReportedAddressDaoImpl.class);
        excludeClassMap.put("com.cannontech.dr.dao", new ArrayList<Class<?>>(excludeClassList));

        excludeClassList.clear();
        excludeClassList.add(SepRestoreMessage.class);
        excludeClassList.add(SepControlMessage.class);
        excludeClassList.add(DigiSmartUpdateListener.class);
        excludeClassMap.put("com.cannontech.thirdparty.messaging", new ArrayList<Class<?>>(excludeClassList));
        excludeClassList.clear();

        excludeClassList.add(AllPointDataListener.class);
        excludeClassList.add(AsyncDynamicDataSource.class);
        excludeClassList.add(DatabaseChangeEventListener.class);
        excludeClassList.add(PointDataListener.class);
        excludeClassList.add(PointService.class);
        excludeClassList.add(PointValueBuilder.class);
        excludeClassList.add(PointValueHolder.class);
        excludeClassList.add(RichPointDataListener.class);
        excludeClassList.add(RichPointDataService.class);
        excludeClassList.add(SignalListener.class);
        excludeClassList.add(DispatchNotConnectedException.class);
        excludeClassList.add(DynamicDataAccessException.class);
        excludeClassList.add(AsyncDynamicDataSourceImpl.class);
        excludeClassList.add(PointServiceImpl.class);
        excludeClassList.add(RichPointDataServiceImpl.class);
        excludeClassList.add(SimplePointValue.class);

        excludeClassMap.put("com.cannontech.core.dynamic", new ArrayList<Class<?>>(excludeClassList));

    }

    @Test
    public void test_trustedPackages() {
        boolean isFound = false;
        Set<Package> packages;
        try {
            packages = getPackages(trustedPackages);
            for (Package pkg : packages) {
                isFound = checkInPackages(pkg);
                assertEquals(true, isFound);
            }
        } catch (ClassNotFoundException | IntrospectionException e) {

            assertEquals(true, isFound);
        }
    }

    private boolean checkInPackages(Package pkg) throws ClassNotFoundException {

        if (pkg != null) {
            boolean found = false;
            for (String packageName : serializablePackages) {
                if (pkg.getName().equals(packageName) || pkg.getName().startsWith(packageName + ".")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private Set<Package> getPackages(List<String> trustedPackages) throws ClassNotFoundException,
            IntrospectionException {
        BeanInfo beanInfo;
        Set<Package> packages = new HashSet<Package>();
        final ClassPathScanningCandidateComponentProvider provider =
            new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        for (String pkg : trustedPackages) {
            // get matching classes defined in the package
            final Set<BeanDefinition> beanLoadedClasses = provider.findCandidateComponents(pkg);

            // this is how you can load the class type from BeanDefinition instance
            for (BeanDefinition bean : beanLoadedClasses) {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                if (excludeClassMap.get(pkg) != null && !excludeClassMap.get(pkg).contains(clazz)) {
                    packages.add(clazz.getPackage());

                    beanInfo = Introspector.getBeanInfo(clazz);
                    for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                        Package propertyPackage =
                            property.getPropertyType() != null ? property.getPropertyType().getPackage() : null;
                        if (propertyPackage != null && !propertyPackage.getClass().isPrimitive()) {
                            packages.add(propertyPackage);
                        }
                    }
                }
            }
        }
        return packages;
    }

}
