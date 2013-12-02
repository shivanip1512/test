package com.cannontech.web.search.lucene.index.site;

import java.sql.SQLException;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.search.lucene.index.site.PaoPageIndexBuilder.PaoTypeHandler;

public class MeterPaoTypeHandler implements PaoTypeHandler {
    @Autowired private PaoLoadingService paoLoadingService;

    private final Set<PaoType> allTypes = PaoType.getMeterTypes();

    @Override
    public Set<PaoType> getTypesHandled() {
        return allTypes;
    }

    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier)
            throws SQLException {
        int paoId = paoIdentifier.getPaoId();

        builder.module("amr");
        String path = "/meter/home?deviceId=" + paoId;
        String pageName = "meterDetail.electric";
        if (paoIdentifier.getPaoType().isWaterMeter()) {
            path = "/meter/water/home?deviceId=" + paoId;
            pageName = "meterDetail.water";
        }
        builder.pageName(pageName);
        builder.path(path);
        // TODO:  consider baking into main query:
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoIdentifier);
        String deviceName = displayablePao.getName();

        builder.pageArgs(deviceName);

        String meterNumber = rs.getString("meterNumber");
        String description = rs.getString("description");
        builder.summaryArgs(meterNumber, description);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        // TODO
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return true;
    }
}
