package com.cannontech.common.bulk.field.impl;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.processor.impl.BulkYukonDeviceFieldProcessor;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.service.impl.UnprocessableHeadersException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.web.input.InputSource;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class BulkFieldServiceImpl implements BulkFieldService, ApplicationContextAware {

    private ApplicationContext context;


    @SuppressWarnings("unchecked")
    public List<String> getBulkFieldBeanNames() {
        
        Map<String, BulkField<?, ?>> bulkFields = unsafeBeansOfType();
        
        return new ArrayList<String>(bulkFields.keySet());
    }


    @SuppressWarnings("rawtypes")
    private Map unsafeBeansOfType() {
        // this is a hack to replicate the return value of beansOfTypeIncludingAncestors 
        // before the Spring 3.0 upgrade
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(context, BulkField.class);
    }
    

    /**
     * GET ALL BULK FIELD PROCESSORS
     */
    public List<BulkYukonDeviceFieldProcessor> getBulkFieldProcessors() {
        
        Map<String, BulkYukonDeviceFieldProcessor> bulkFields = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, BulkYukonDeviceFieldProcessor.class);
        
        return new ArrayList<BulkYukonDeviceFieldProcessor>(bulkFields.values());
    }
    
    /**
     * GET ALL BULK FIELDS
     */
    @SuppressWarnings("unchecked")
    public List<BulkField<?, ?>> getBulkFields() {
        
        Map<String, BulkField<?, ?>> bulkFields = unsafeBeansOfType();
        
        return new ArrayList<BulkField<?, ?>>(bulkFields.values());
    }
    
    /**
     * GET ALL MASS CHANGABLE BULK FIELDS
     */
    public List<BulkField<?, ?>> getMassChangableBulkFields() {
        
        List<BulkField<?, ?>> massChangeableBulkFields = new ArrayList<BulkField<?, ?>>();
        
        List<BulkField<?, ?>> bulkFields = getBulkFields();
        
        for (BulkField<?, ?> bulkField : bulkFields) {
            
            if (bulkField.getMassChangable()) {
                massChangeableBulkFields.add(bulkField);
            }
        }
        
        return massChangeableBulkFields;
    }

    public Set<BulkFieldColumnHeader> getUpdateableBulkFieldColumnHeaders() {
        
        Set<BulkFieldColumnHeader> updateableHeaders = new HashSet<BulkFieldColumnHeader>();
        
        List<BulkYukonDeviceFieldProcessor> processors =  getBulkFieldProcessors();
        for (BulkYukonDeviceFieldProcessor p : processors) {
            
            Set<BulkField<?, SimpleDevice>> fields = p.getUpdatableFields();
            for (BulkField<?, SimpleDevice> field : fields) {
                BulkFieldColumnHeader updateableField = getColumnHeaderForFieldName(field.getInputSource().getField());
                updateableHeaders.add(updateableField);
            }
        }
        
        return updateableHeaders;
    }
    
    public Set<BulkFieldColumnHeader> getUpdateIdentifierBulkFieldColumnHeaders() {
        
        Set<BulkFieldColumnHeader> identifierHeaders = new HashSet<BulkFieldColumnHeader>();
        
        List<BulkField<?, ?>> bulkFields = getBulkFields();
        
        for (BulkField<?, ?> field : bulkFields) {
            
            if (field.getIdentifierMapper() != null) {
                BulkFieldColumnHeader identifierField = getColumnHeaderForFieldName(field.getInputSource().getField());
                identifierHeaders.add(identifierField);
            }
        }
        
        return identifierHeaders;
    }
    
    @SuppressWarnings("unchecked")
    public <T> boolean processorExistsForBulkFieldColumnHeaders(List<BulkFieldColumnHeader> bulkFieldColumnHeaders) {
        
        Set<BulkField<?, T>> matchingBulkFields = Sets.newHashSet();
        Map<String, BulkField<?, T>> allBulkFields = unsafeBeansOfType();
        
        for (BulkFieldColumnHeader bulkFieldColumnHeader : bulkFieldColumnHeaders) {
            
            for (BulkField<?, T> field : allBulkFields.values()) {
                if (field.getInputSource().getField().equals(bulkFieldColumnHeader.getFieldName())) {
                    matchingBulkFields.add(field);
                    break;
                }
            }
        }
        
        List<BulkYukonDeviceFieldProcessor> bulkYukonDeviceFieldProcessors = findProcessorsForFields(matchingBulkFields);
        return bulkYukonDeviceFieldProcessors.size() > 0;
    }
    
    
    public BulkFieldColumnHeader getColumnHeaderForFieldName(String fieldName) {
        
        BulkFieldColumnHeader[] headers = BulkFieldColumnHeader.values();
        
        for (BulkFieldColumnHeader header : headers) {
            if (header.getFieldName().equals(fieldName)) {
                return header;
            }
        }
        
        return null;
    }
    
    public <T, O> O getYukonDeviceForIdentifier(BulkField<T, O> bulkField, String identifier) throws ObjectMappingException, IllegalArgumentException {
        
        String id = StringUtils.strip(identifier);

        InputSource<T> inputSource = bulkField.getInputSource();
        PropertyEditor propertyEditor = inputSource.getType().getPropertyEditor();
        propertyEditor.setAsText(id);
        
        Object valueObject = propertyEditor.getValue();
        T value = inputSource.getType().getTypeClass().cast(valueObject);

        ObjectMapper<T, O> mapper = bulkField.getIdentifierMapper();
        O device = mapper.map(value);
            
        return device;
    }
    
    public <T> List<BulkYukonDeviceFieldProcessor> findProcessorsForFields(Set<BulkField<?, T>> bulkFieldSet) {
        
        /* This is going to be a "greedy" algorithm, so we'll sort the available
            processors based on the number of fields it handles (descending). */ 
        List<BulkYukonDeviceFieldProcessor> allProcessors = getBulkFieldProcessors();
        Ordering<BulkYukonDeviceFieldProcessor> ordering = 
            Ordering.natural().reverse().onResultOf(new Function<BulkYukonDeviceFieldProcessor, Integer>() {
                @Override
                public Integer apply(BulkYukonDeviceFieldProcessor from) {
                    return from.getUpdatableFields().size();
                }
            });
        allProcessors = ordering.sortedCopy(allProcessors);

        List<BulkYukonDeviceFieldProcessor> result = new ArrayList<BulkYukonDeviceFieldProcessor>();
        
        for (BulkYukonDeviceFieldProcessor processor : allProcessors) {
            if (bulkFieldSet.isEmpty()) break;
            if (bulkFieldSet.containsAll(processor.getUpdatableFields())) {
                bulkFieldSet.removeAll(processor.getUpdatableFields());
                result.add(processor);
            }
        }
        
        if(!bulkFieldSet.isEmpty()) {
            Set<BulkFieldColumnHeader> badHeaders = Sets.newHashSet();
            for(BulkField<?, T> field : bulkFieldSet) {
                BulkFieldColumnHeader header = BulkFieldColumnHeader.getForFieldName(field.getInputSource().getField());
                badHeaders.add(header);
            }
            throw new UnprocessableHeadersException(badHeaders);
        }
        
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        this.context = context;
    }
    
}
