package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectUserGroupModal extends BaseModal {

    private WebTable userGroupTable;

    public SelectUserGroupModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        userGroupTable = new WebTable(driverExt, "compact-results-table", "primaryOperatorUserGroupPicker");
    }

    public WebTable getuserGroupTable() {
        return userGroupTable;
    }

    public void selectUserGroup(String userGroupName) {
        getuserGroupTable().searchTable(userGroupName);

        WebTable table = getuserGroupTable();
        WebTableRow row = table.getDataRowByLinkName(userGroupName);

        row.selectCellByLink();
    }
}
