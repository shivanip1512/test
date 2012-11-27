package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

/**
 * Dynamic MenuOptionProducer for trends
 */
public class TrendingMenuOptionProducer extends DynamicMenuOptionProducer {

    private GraphDao graphDao;

    public void setGraphDao(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        // Generate one menu option for each graph definition associated with
        // the user

        int userID = userContext.getYukonUser().getUserID();
        List<LiteGraphDefinition> graphDefinitions = graphDao.getGraphDefinitionsForUser(userID);

        List<MenuOption> optionList = new ArrayList<MenuOption>();
        for (LiteGraphDefinition definition : graphDefinitions) {

            // Get graph name
            String name = definition.getName();
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.trending.name",
                                                                                       name);
            // Get graph id
            int id = definition.getLiteID();
            String optionId = "graph_" + id;

            SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink(optionId, resolvable);

            optionLink.setLinkUrl("/stars/consumer/trending/view?gdefid=" + id);

            optionList.add(optionLink);
        }

        return optionList;
    }

}
