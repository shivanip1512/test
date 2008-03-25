package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonMessageSourceResolvableImpl;
import com.cannontech.i18n.YukonMessageSourceResovable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

/**
 * Dynamic MenuOptionProducer for trends
 */
public class TrendingMenuOptionProducer extends MenuOptionProducerBase {

    private GraphDao graphDao;

    public void setGraphDao(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {

        // Generate one menu option for each graph definition associated with
        // the user

        int userID = userContext.getYukonUser().getUserID();
        List<LiteGraphDefinition> graphDefinitions = graphDao.getGraphDefinitionsForUser(userID);

        List<MenuOption> optionList = new ArrayList<MenuOption>();
        for (LiteGraphDefinition definition : graphDefinitions) {

            // Get graph name
            String name = definition.getName();
            YukonMessageSourceResovable resolvable = new YukonMessageSourceResolvableImpl("yukon.web.menu.config.consumer.trending.name",
                                                                                          name);

            SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink(resolvable);

            // Get graph id
            int id = definition.getLiteID();
            optionLink.setLinkUrl("/user/ConsumerStat/stat/Metering.jsp?gdefid=" + id);

            optionList.add(optionLink);
        }

        return optionList;
    }

}
