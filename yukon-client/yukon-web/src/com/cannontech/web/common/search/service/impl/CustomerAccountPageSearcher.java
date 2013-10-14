package com.cannontech.web.common.search.service.impl;

import java.util.List;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.search.index.CustomerAccountIndexManager;
import com.cannontech.common.search.index.IndexManager;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.web.common.search.result.Page;
import com.google.common.collect.ImmutableList;

@Service
public class CustomerAccountPageSearcher extends AbstractPageSearcher {
    @Autowired private CustomerAccountIndexManager indexManager;

    private final static String[] searchFields = new String[] {"accountNumber"};

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
        String accountNumber = document.get("accountNumber");
        String accountId = document.get("accountId");

        String module = "operator";
        String path = "/stars/operator/account/view?accountId=" + accountId;
        String pageName = "account.VIEW";

        List<String> arguments = ImmutableList.of(accountNumber);
        UserPage userPage = new UserPage(0, path, false, SiteModule.getByName(module), pageName, arguments,
            null, null);

        Page page = new Page(userPage, accountNumber);
        return page;
    }
}
