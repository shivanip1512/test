package com.cannontech.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.cannontech.common.util.CtiUtilities;

public class GetAllJarsTest {

    @Test
    public void testGetAllJars() throws IOException {
        File thirdParty = new File("../lib/");
        System.out.println(thirdParty.getAbsolutePath());
        Collection<String> allJars = CtiUtilities.getAllJars(thirdParty, "dbEditor.jar");
        assertTrue(allJars.size() > 0);
    }

}
