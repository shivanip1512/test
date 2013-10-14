package com.cannontech.web.common.search.service.impl;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.index.IndexManager;
import com.cannontech.common.search.index.PaoIndexManager;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.web.common.search.result.Page;
import com.google.common.collect.ImmutableList;

@Service
public class MeterPageSearcher extends AbstractPageSearcher {
    @Autowired private PaoIndexManager indexManager;

    private final static Query isMeterQuery = new TermQuery(new Term("isMeter", "true"));
    private final static String[] searchFields = new String[] {"deviceName", "meterNumber"};

    @Override
    protected IndexManager getIndexManager() {
        return indexManager;
    }

    @Override
    protected Query getRequiredQuery() {
        return isMeterQuery;
    }

    @Override
    protected String[] getSearchFields() {
        return searchFields;
    }

    @Override
    protected Page buildPage(Document document) {
        String deviceName = document.get("deviceName");
        @SuppressWarnings("deprecation")
        PaoType paoType = PaoType.getForDbString(document.get("type"));
        int paoId = Integer.parseInt(document.get("paoid"));
        String meterNumber = document.get("meterNumber");

        String module = "amr";
        String path = "/meter/home?deviceId=" + paoId;
        String pageName = "meterDetail.electric";
        if (paoType.isWaterMeter()) {
            path = "/meter/water/home?deviceId=" + paoId;
            pageName = "meterDetail.water";
        }

        List<String> arguments = ImmutableList.of(deviceName);
        UserPage userPage = new UserPage(0, path, false, SiteModule.getByName(module), pageName, arguments,
            null, null);

        Page page = new Page(userPage, deviceName, meterNumber);
        return page;
    }
}
