package com.cannontech.notif.test;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.notif.outputs.Callable;
import com.cannontech.notif.outputs.ContactPhone;

/**
 * 
 */
public class TestCustomer implements Callable {
    ArrayList _list = new ArrayList();
    String _name;
    
    TestCustomer(String numbers, String name) {
        String numArray[] = numbers.split(", *");
        for (int i = 0; i < numArray.length; i++) {
            String string = numArray[i];
            _list.add(new ContactPhone(string,0));
        }

        this._name = name;
    }
    
    public List getContactPhoneNumberList() {
        return _list;
    }
    
    public String toString() {
        return _name;
    }

}
