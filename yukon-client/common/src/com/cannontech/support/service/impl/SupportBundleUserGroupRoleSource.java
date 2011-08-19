package com.cannontech.support.service.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.RolePropertyValue;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.tools.zip.ZipWriter;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class SupportBundleUserGroupRoleSource extends AbstractSupportBundleSource {
    private RolePropertyEditorDao rolePropertyEditorDao;
    private YukonGroupDao yukonGroupDao;
    private final static String databaseZipDir = "Database";
    private Logger log = YukonLogManager.getLogger(SupportBundleUserGroupRoleSource.class);

    @Override
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {

        Writer pw = zipWriter.getBufferedWriter(databaseZipDir, "YukonGroupRoleProperty.csv");

        CSVWriter csvWriter = new CSVWriter(pw);
        String[] header =
          { "Login Group", "Group Id", "Role Property Name", "Value" };
        csvWriter.writeNext(header);
        Predicate<YukonRoleProperty> predicate = Predicates.alwaysTrue();
        List<LiteYukonGroup> groups = yukonGroupDao.getAllGroups();
        for (LiteYukonGroup group : groups) {
            try {
                GroupRolePropertyValueCollection groupCollection =
                    rolePropertyEditorDao.getForGroupAndPredicate(group, true, predicate);

                List<RolePropertyValue> roles = groupCollection.getRolePropertyValues();
                for (RolePropertyValue role : roles) {
                    List<String> ls = new ArrayList<String>(4);
                    ls.add(group.getGroupName());
                    ls.add(String.valueOf(group.getGroupID()));
                    ls.add(role.getYukonRoleProperty().name());
                    if (role.getValue() == null) {
                        ls.add("");
                    } else {
                        ls.add(role.getValue().toString());
                    }
                    String[] nextLine = ls.toArray(new String[0]);
                    csvWriter.writeNext(nextLine);

                }
            } catch (IllegalArgumentException e) {
                String[] nextLine =
                  { group.getGroupName(), String.valueOf(group.getGroupID()),
                          "ERROR: Unable to retreive Roles " + e.getMessage(), "" };
                csvWriter.writeNext(nextLine);
            }
        }
        try {
            pw.flush();
        } catch (IOException e) {
            log.error("Problem trying to flush the print writer for CSVWriter", e);
        }
    }

    @Autowired
    public void setRolePropertyEditorDao(RolePropertyEditorDao rolePropertyEditorDao) {
        this.rolePropertyEditorDao = rolePropertyEditorDao;
    }

    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

}
