package com.cannontech.common.bulk.field.impl;

import com.cannontech.common.bulk.field.BulkYukonDeviceFieldService;

public class BulkYukonDeviceFieldServiceImpl implements BulkYukonDeviceFieldService {

//    private BulkFieldService bulkFieldService = null;
//    
//    // the following is a naive implementation and should be changed when more
//    // complete processors are added
//    public BulkYukonDeviceFieldProcessor findYukonDeviceFieldProcessor(BulkField<?, YukonDevice> bulkField) {
//        
//        Set<BulkField<?, YukonDevice>> requiredSet = new HashSet<BulkField<?, YukonDevice>>(1);
//        requiredSet.add(bulkField);
//        
//        // the following is a naive implementation and should be changed when more
//        // complete processors are added
//        List<BulkYukonDeviceFieldProcessor> allBulkFieldProcessors = bulkFieldService.getBulkFieldProcessors();
//        BulkYukonDeviceFieldProcessor bulkFieldProcessor = null;
//        for (BulkYukonDeviceFieldProcessor processor : allBulkFieldProcessors) {
//            if (requiredSet.equals(processor.getUpdatableFields())) {
//                bulkFieldProcessor = processor;
//            }
//        }
//        
//        return bulkFieldProcessor;
//    }
//    
//    public Processor<YukonDevice> getBulkProcessor(final BulkYukonDeviceFieldProcessor bulkFieldProcessor, final YukonDeviceDto yukonDeviceDtoObj) {
//        
//        return new SingleProcessor<YukonDevice>() {
//            
//            @Override
//            public void process(YukonDevice device) throws ProcessingException {
//                bulkFieldProcessor.updateField(device, yukonDeviceDtoObj);
//            }
//        };
//    }
//    
//    @Required
//    public void setBulkFieldService(BulkFieldService bulkFieldService) {
//        this.bulkFieldService = bulkFieldService;
//    }
}
