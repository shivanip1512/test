package com.cannontech.common.pao.dao.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.pao.dao.PaoPersistenceTypeHelper;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PaoPersistenceTypeHelperImpl implements PaoPersistenceTypeHelper {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceTypeHelperImpl.class);

    private final Set<Class<? extends CompleteYukonPao>> paoClasses;
    private final Set<Class<?>> paoPartClasses;
    private final Set<Class<?>> allPaoClasses;

    private final Map<PaoType, List<CompletePaoMetaData>> paoTypeToTableMapping = new HashMap<>();
    private final Map<Class<?>, Set<PaoType>> paoTypeByClass = new HashMap<>();
    private final Map<PaoType, Class<? extends CompleteYukonPao>> classByPaoType = new HashMap<>();

    @Autowired
    public PaoPersistenceTypeHelperImpl(Set<Class<? extends CompleteYukonPao>> paoClasses, Set<Class<?>> paoPartClasses) {
        log.debug("system has " + paoClasses.size() + " @YukonPao classes and " + paoPartClasses.size()
            + " @YukonPaoPart classes.");
        this.paoClasses = paoClasses;
        this.paoPartClasses = paoPartClasses;

        Builder<Class<?>> builder = ImmutableSet.builder();
        builder.addAll(paoClasses);
        builder.addAll(paoPartClasses);

        allPaoClasses = builder.build();

        Map<Class<?>, CompletePaoMetaData> metaDataByClass = new HashMap<>();

        for (Class<?> klass : allPaoClasses) {
            CompletePaoMetaData paoMetaData = makeMetaData(metaDataByClass, klass);
            metaDataByClass.put(klass, paoMetaData);
        }

        // Build the maps required for later use in the DAO methods.
        buildPaoTypeMaps(metaDataByClass);
       
		for (CompletePaoMetaData mapping : metaDataByClass.values()) {
			Iterable<PaoFieldMetaData> iter = mapping.getFields();
			Iterables.removeIf(iter, new Predicate<PaoFieldMetaData>() {
				@Override
				public boolean apply(PaoFieldMetaData input) {
		            Class<?> propertyType = input.getPropertyDescriptor().getPropertyType();
		            return propertyType.getAnnotation(YukonPaoPart.class) != null;
				}
			});

		}
    }

    /**
     * Given a Class, extract the database information and information about the class' contents and
     * constructs a {@link CompletePaoMetaData} for the class, then stores it in the metaDataByClass map.
     * 
     * @param metaDataByClass the Class-to-DbTableMapping map into which completePaoType's
     *        DbTableMapping information will be stored.
     * @param klass the class whose DbTableMapping information will be created.
     */
    private CompletePaoMetaData makeMetaData(Map<Class<?>, CompletePaoMetaData> metaDataByClass, Class<?> klass) {
        YukonPao paoAnnotation = klass.getAnnotation(YukonPao.class);
        YukonPaoPart partAnnotation = klass.getAnnotation(YukonPaoPart.class);

        // This will be used to detect the table name automatically if one wasn't provided.
        String tableName = klass.getSimpleName().replaceAll("^Complete", "");

        String idColumnName = null;
        if (paoAnnotation != null) {
            if (paoAnnotation.tableBacked()) {
                if (!YukonPao.AUTO_DETECT.equals(paoAnnotation.tableName())) {
                    tableName = paoAnnotation.tableName();
                }

                if (YukonPao.UNSPECIFIED.equals(paoAnnotation.idColumnName())) {
                    // Annotation error, this class cannot be table backed without specifying an id column name!
                    throw new RuntimeException(klass.getName() + " is specified as being table-backed but "
                        + "no idColumnName has been specified!");
                }

                idColumnName = paoAnnotation.idColumnName();
            }
        } else {
            if (!YukonPao.AUTO_DETECT.equals(partAnnotation.tableName())) {
                tableName = partAnnotation.tableName();
            }

            if (YukonPao.UNSPECIFIED.equals(partAnnotation.idColumnName())) {
                // Annotation error, parts cannot have a missing id column name!
                throw new RuntimeException(klass.getName() + " is specified as being table-backed but "
                    + "no idColumnName has been specified!");
            }

            idColumnName = partAnnotation.idColumnName();
        }

        List <PaoFieldMetaData> fields = Lists.newArrayList();
        List<String> propertiesInserted = Lists.newArrayList();
        Method[] methods = klass.getDeclaredMethods();
        for (Method method : methods) {
            YukonPaoField fieldAnnotation = method.getAnnotation(YukonPaoField.class);
            if (fieldAnnotation != null) {
                PropertyDescriptor propertyDescriptor = BeanUtils.findPropertyForMethod(method);
                if (propertiesInserted.contains(propertyDescriptor.getName())) {
                    throw new RuntimeException("More than one method for " + klass.getSimpleName() + "'s "
                        + propertyDescriptor.getName() + " property were annotated.");
                }
                String dbColumnName = null;
                if (YukonPao.AUTO_DETECT.equals(fieldAnnotation.columnName())) {
                    dbColumnName = propertyDescriptor.getName();
                } else {
                    dbColumnName = fieldAnnotation.columnName();
                }
                PaoFieldMetaData paoFieldMetaData = new PaoFieldMetaData(propertyDescriptor, dbColumnName);
                fields.add(paoFieldMetaData);
                propertiesInserted.add(propertyDescriptor.getName());
            }
        }

        CompletePaoMetaData paoMetaData =
            new CompletePaoMetaData(klass, tableName, idColumnName, fields, null);
        return paoMetaData;
    }

    /**
     * Use the information in the metaDataByClass map to generate the two private class maps which will be
     * used for the functionality of the DAO.
     * 
     * @param metaDataByClass A fully populated map of all classes in the com.cannontech.common.pao.pojo
     *        package to their {@link CompletePaoMetaData} representation
     */
    private void buildPaoTypeMaps(Map<Class<?>, CompletePaoMetaData> metaDataByClass) {
        // For each class in the map, we want to build a list of the DbTableMapping objects
        // that are required to build an instance of that object.
        for (Class<?> klass : metaDataByClass.keySet()) {
            YukonPao paoAnnotation = klass.getAnnotation(YukonPao.class);

            // This information is only valid if we have a YukonPao annotation.
            if (paoAnnotation != null) {
                List<CompletePaoMetaData> classTableMappings = Lists.newArrayList();

                // Iterate over klass's hierarchy, adding all inheritance and composition
                // classes to the classTableMappings list.
                Class<?> currentClass = klass;
                while (currentClass != null) {
                    CompletePaoMetaData mapping = metaDataByClass.get(currentClass);
                    if (mapping != null) {
                        // Look to see if currentClass contains any YukonPaoPart members. If it
                        // does, create a new DbTableMapping for that Part, including the getter
                        // for that Part, and add it to the list of mappings klass requires. This
                        // must be done before we add mapping to the list, since this list is
                        // created deletion order as we iterate UPWARDS over the hierarchy.
                        for (PaoFieldMetaData field : mapping.getFields()) {
                            PropertyDescriptor propertyDescriptor = field.getPropertyDescriptor();
                            Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();

                            // We already have a record of all classes in the package, so if the
                            // metaDataByClass map contains the return type of this annotated
                            // member, we know it's a YukonPaoPart and needs to be stored.
                            CompletePaoMetaData partMapping = metaDataByClass.get(returnType);
                            if (partMapping != null) {
                                classTableMappings.add(partMapping.withDescriptor(propertyDescriptor));
                            }
                        }

                        // Only add currentClass to klass' list if it's a table-backed class. For
                        // instance, we wouldn't want to add Ccu721 to the list because it doesn't
                        // actually represent a place in the database where information is entered
                        // to create a Ccu721 device.
                        if (currentClass.getAnnotation(YukonPao.class).tableBacked()) {
                            classTableMappings.add(mapping);
                        }
                    }

                    // Grab the next class up in the hierarchy.
                    currentClass = currentClass.getSuperclass();
                }

                // We want to toss these into the PaoType maps in INSERTION order. This
                // check is only valid on YukonPao annotated classes though.
                classTableMappings = Lists.reverse(classTableMappings);
                if (klass.getAnnotation(YukonPao.class) != null) {
                    Validate.isTrue(classTableMappings.get(0).getKlass() == CompleteYukonPao.class, "Init error: "
                        + klass + "'s structure is invalid!");
                }

                // Map each of the PaoTypes in the annotation to the insertion-ordered list
                // of DbTableMapping objects that define it.
                Set<PaoType> supportedPaoTypes = Sets.newHashSet(paoAnnotation.paoTypes());
                for (PaoType paoType : supportedPaoTypes) {
                    paoTypeToTableMapping.put(paoType, classTableMappings);
                }

                // Map klass itself to the PaoTypes it represents so we don't have to
                // re-acquire that data every time someone calls one of our methods.
                paoTypeByClass.put(klass, supportedPaoTypes);
                for (PaoType paoType : supportedPaoTypes) {
                    @SuppressWarnings("unchecked")
                    Class<? extends CompleteYukonPao> completeYukonPaoClass = (Class<? extends CompleteYukonPao>) klass;
                    classByPaoType.put(paoType, completeYukonPaoClass);
                }
            }
        }
    }

    @Override
    public Set<Class<? extends CompleteYukonPao>> getPaoClasses() {
        return paoClasses;
    }
    @Override
    public Set<Class<?>> getPaoPartClasses() {
        return paoPartClasses;
    }

    @Override
    public Map<PaoType, List<CompletePaoMetaData>> getPaoTypeToTableMapping() {
        return paoTypeToTableMapping;
    }

    @Override
    public Map<Class<?>, Set<PaoType>> getPaoTypeByClass() {
        return paoTypeByClass;
    }

    @Override
    public Map<PaoType, Class<? extends CompleteYukonPao>> getClassByPaoType() {
        return classByPaoType;
    }
}
