package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.framework.DriverExtensions;

public class RecentArchievedRadingsModal extends BaseModal {

	public RecentArchievedRadingsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        if(modalTitle.isPresent()) {
            modalTitle = Optional.of(modalTitle.get());
        }
        
        if(describedBy.isPresent()) {
            describedBy = Optional.of(describedBy.get());
        }
    }

}
