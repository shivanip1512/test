package com.cannontech.web.common.search.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.search.index.IndexManager;
import com.cannontech.common.search.index.InventoryIndexManager;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.web.common.search.result.Page;
import com.google.common.collect.ImmutableList;

@Service
public class InventoryPageSearcher extends AbstractPageSearcher {
    @Autowired private InventoryIndexManager indexManager;

    private final static String[] searchFields = new String[] {"manufacturerSerialNumber", "meterNumber"};

    @Override
    protected IndexManager getIndexManager() {
        return indexManager;
    }

    @Override
    protected String[] getSearchFields() {
        return searchFields;
    }

    @Override
    protected Page buildPage(Document document) {
        int inventoryId = Integer.parseInt(document.get("inventoryId"));

        String accountIdStr = document.get("accountId");
        String module = "operator";
        String path;
        String pageName;
        if (StringUtils.isBlank(accountIdStr) || "0".equals(accountIdStr)) {
            pageName = "inventory.VIEW";
            path = "/stars/operator/inventory/view?inventoryId=" + inventoryId;
        } else {
            int accountId = Integer.parseInt(accountIdStr);
            pageName = "hardware.VIEW";
            path = "/stars/operator/hardware/view?accountId=" + accountId + "&inventoryId=" + inventoryId;
        }

        String displayName = document.get("displayName");

        List<String> arguments = ImmutableList.of(displayName);
        UserPage userPage = new UserPage(0, path, false, SiteModule.getByName(module), pageName, arguments, null, null);
        Page page = new Page(userPage, displayName);

        return page;
    }
}
