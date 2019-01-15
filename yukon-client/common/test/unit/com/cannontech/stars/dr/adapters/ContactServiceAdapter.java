package com.cannontech.stars.dr.adapters;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.general.service.ContactService;

public class ContactServiceAdapter implements ContactService {

    @Override
    public LiteContact createAdditionalContact(String firstName, String lastName, LiteCustomer customer, LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId) {
        throw new UnsupportedOperationException("not implemented");
    }

}
