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
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.web.input.InputSource;

public class BulkFieldServiceImpl implements BulkFieldService, ApplicationContextAware {

    private ApplicationContext context;


    @SuppressWarnings("unchecked")
    public List<String> getBulkFieldBeanNames() {
        
        Map<String, BulkField<?, ?>> bulkFields = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, BulkField.class);
        
        return new ArrayList<String>(bulkFields.keySet());
    }
    

    /**
     * GET ALL BULK FIELD PROCESSORS
     */
    @SuppressWarnings("unchecked")
    public List<BulkYukonDeviceFieldProcessor> getBulkFieldProcessors() {
        
        Map<String, BulkYukonDeviceFieldProcessor> bulkFields = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, BulkYukonDeviceFieldProcessor.class);
        
        return new ArrayList<BulkYukonDeviceFieldProcessor>(bulkFields.values());
    }
    
    /**
     * GET ALL BULK FIELDS
     */
    @SuppressWarnings("unchecked")
    public List<BulkField<?, ?>> getBulkFields() {
        
        Map<String, BulkField<?, ?>> bulkFields = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, BulkField.class);
        
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
            
            Set<BulkField<?, YukonDevice>> fields = p.getUpdatableFields();
            for (BulkField<?, YukonDevice> field : fields) {
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
    
    public <T> List<BulkYukonDeviceFieldProcessor> findProcessorsForFields(List<BulkField<?, T>> bulkFields) {
        
        List<BulkYukonDeviceFieldProcessor> processors = new ArrayList<BulkYukonDeviceFieldProcessor>();

        // this guy is kinda dumb, will always be 1-to-1 field-to-processor
        // will be more complicated if multi-field processors ever exist
        for (BulkField<?, T> updateableField : bulkFields) {
            
            // the fields the processor needs to handle
            Set<BulkField<?, T>> requiredSet = new HashSet<BulkField<?, T>>(1);
            requiredSet.add(updateableField);
            
            // find that processor
            for (BulkYukonDeviceFieldProcessor processor : getBulkFieldProcessors()) {
                if (requiredSet.equals(processor.getUpdatableFields())) {
                    processors.add(processor);
                    break;
                }
            } 
        }
        return processors;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        this.context = context;
    }
    
}
