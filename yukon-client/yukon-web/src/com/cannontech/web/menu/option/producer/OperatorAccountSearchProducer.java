package com.cannontech.web.menu.option.producer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class OperatorAccountSearchProducer implements SearchProducer {
    
    private RolePropertyDao rolePropertyDao;
    
    public SearchFormData getSearchProducer(YukonUserContext userContext) {
        if (rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, userContext.getYukonUser())) {
            SearchFormData formData = new SearchFormData("/stars/operator/account/search", "searchValue");
            formData.setTypeName("searchBy");
            OperatorAccountSearchBy[] searchBys = OperatorAccountSearchBy.values();
            List<SearchType> searchTypes = Lists.newArrayListWithExpectedSize(searchBys.length);
            
            for (OperatorAccountSearchBy searchBy : searchBys) {
                SearchType searchType = new SearchType(searchBy.name(), searchBy.getFormatKey());
                searchTypes.add(searchType);
            }
            formData.setTypeOptions(searchTypes);
            return formData;
        } else {
            return null;
        }
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
