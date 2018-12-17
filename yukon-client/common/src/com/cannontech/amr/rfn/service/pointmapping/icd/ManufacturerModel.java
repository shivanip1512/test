package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.Optional;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ManufacturerModel {

    @JsonCreator
    public ManufacturerModel(String manufacturerModel) {
        
        this.original = manufacturerModel;

        // Split the manufacturer+model string on the first space.
        int firstSpace = manufacturerModel.indexOf(' ');
        
        if (firstSpace <= 0) {
            throw new IllegalArgumentException("Argument must have manufacturer and model, separated by a space");
        }
        
        String manufacturer = manufacturerModel.substring(0, firstSpace);
        String model = manufacturerModel.substring(firstSpace + 1);

        RfnIdentifier rfnId = new RfnIdentifier(null, manufacturer, model);

        mm = Optional.ofNullable(RfnManufacturerModel.of(rfnId))
                .orElseThrow(() -> new IllegalArgumentException("Unknown manufacturer/model combination: " + manufacturerModel));
    }
    
    public String getOriginal() {
        return original;
    }
    public RfnManufacturerModel getManufacturerModel() {
        return mm;
    }
    
    private String original;
    private RfnManufacturerModel mm;
}