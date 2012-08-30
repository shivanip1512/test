package com.cannontech.cbc.cyme;

import com.cannontech.cbc.cyme.profile.CymeLoadProfile;

public interface CymeLoadProfileReader {

    public CymeLoadProfile readFromFile(String fileName);
}
