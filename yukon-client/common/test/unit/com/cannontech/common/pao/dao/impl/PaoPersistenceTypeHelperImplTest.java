package com.cannontech.common.pao.dao.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.pao.dao.PaoPersistenceTypeHelper;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/com/cannontech/common/pao/completePao.xml")
public class PaoPersistenceTypeHelperImplTest {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceTypeHelperImplTest.class);
    private static final Joiner joiner = Joiner.on("\n");

    @Autowired private PaoPersistenceTypeHelper paoPersistenceTypeHelper;

    private final Set<Class<? extends CompleteYukonPao>> scannedPaoClasses = new HashSet<>();
    private final Set<Class<?>> scannedPaoPartClasses = new HashSet<>();

    public PaoPersistenceTypeHelperImplTest() {
        // Scan all classes in the com.cannontech.common.pao.model package.  We will then check the list of found
        // classes against those listed in completePao.xml to be sure they are all listed properly.
        Set<Class<?>> allClassesInPaoModelPackage = scanForClasses();

        for (Class<?> klass : allClassesInPaoModelPackage) {
            YukonPao yukonPaoAnnotation = klass.getAnnotation(YukonPao.class);
            YukonPaoPart yukonPaoPartAnnotation = klass.getAnnotation(YukonPaoPart.class);
            String className = klass.getName();

            if (yukonPaoAnnotation != null && yukonPaoPartAnnotation != null) {
                fail(className + " is annotated with both @YukonPao and @YukonPaoPart which is not valid.");
            } else if (yukonPaoAnnotation != null) {
                if (CompleteYukonPao.class.isAssignableFrom(klass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends CompleteYukonPao> completeYukonPaoClass = (Class<? extends CompleteYukonPao>) klass;
                    scannedPaoClasses.add(completeYukonPaoClass);
                } else {
                    fail(className + " is annotated with @YukonPao but does not extend CompleteYukonPao.");
                }
            } else if (yukonPaoPartAnnotation != null) {
                if (CompleteYukonPao.class.isAssignableFrom(klass)) {
                    fail(className + " is annotated with @YukonPaoPart but extends CompleteYukonPao.");
                } else {
                    scannedPaoPartClasses.add(klass);
                }
            } else {
                log.debug("ignoring class " + className + " because it is not annotated");
            }
        }
    }

    private Set<Class<?>> scanForClasses() {
        Set<Class<?>> retVal = new HashSet<>();

        // scan com.cannontech.common.pao.model package for YukonPao annotation
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        String packageSearchPath = "classpath*:com/cannontech/common/pao/model/**/*.class";
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> klass = Class.forName(className);
                    retVal.add(klass);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Failed to find CompleteYukonPao types", e);
        }

        return retVal;
    }

    @Test
    public void test_getPaoClasses() {
        Set<Class<? extends CompleteYukonPao>> difference =
            Sets.difference(scannedPaoClasses, paoPersistenceTypeHelper.getPaoClasses());
        if (!difference.isEmpty()) {
            System.out.println(joiner.join(difference));
            fail("The following classes were annotated with @YukonPao but not listed in completePao.xml:\n"
                + joiner.join(difference));
        }

        difference = Sets.difference(paoPersistenceTypeHelper.getPaoClasses(), scannedPaoClasses);
        if (!difference.isEmpty()) {
            System.out.println(joiner.join(difference));
            fail("The following classes were listed in completePao.xml but not annotated with @YukonPao:\n"
                + joiner.join(difference));
        }
    }

    @Test
    public void test_getPaoPartClasses() {
        Set<Class<?>> difference = Sets.difference(scannedPaoPartClasses, paoPersistenceTypeHelper.getPaoPartClasses());
        if (!difference.isEmpty()) {
            System.out.println(joiner.join(difference));
            fail("The following classes were annotated with @YukonPaoPart but not listed in completePao.xml:\n"
                + joiner.join(difference));
        }

        difference = Sets.difference(paoPersistenceTypeHelper.getPaoPartClasses(), scannedPaoPartClasses);
        if (!difference.isEmpty()) {
            System.out.println(joiner.join(difference));
            fail("The following classes were listed in completePao.xml but not annotated with @YukonPaoPart:\n"
                + joiner.join(difference));
        }
    }

    @Test
    public void test_checkForNoArgConstructors() {
        // Every class needs to have a no-argument constructor.
        for (Class<?> klass : Sets.union(scannedPaoClasses, scannedPaoPartClasses)) {
            String className = klass.getName();
            System.out.println("*** checking class " + className);
            Constructor<?>[] declaredConstructors = klass.getDeclaredConstructors();
            Constructor<?> noArgConstructor = null;
            for (Constructor<?> constructor : declaredConstructors) {
                if (constructor.getGenericParameterTypes().length == 0) {
                    noArgConstructor = constructor;
                    break;
                }
            }
            assertNotNull(className + " does not have a no-argument constructor.", noArgConstructor);
            assertTrue(className + "'s no-argument constructor is not public.",
                Modifier.isPublic(noArgConstructor.getModifiers()));
        }
    }
}
