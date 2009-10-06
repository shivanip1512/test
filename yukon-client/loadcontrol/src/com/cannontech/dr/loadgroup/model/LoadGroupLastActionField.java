package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupLastActionField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LAST_ACTION";
    }
    
    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
        template.addData("date", group.getGroupTime());
        return template;
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(final boolean isDescending,
                                                YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMDirectGroupBase group1 = getGroupFromYukonPao(pao1);
                LMDirectGroupBase group2 = getGroupFromYukonPao(pao2);
                if (group1 == group2) {
                    return 0;
                }
                if (group1 == null || group1.getGroupTime() == null) {
                    return isDescending ? -1 : 1;
                }
                if (group2 == null || group2.getGroupTime() == null) {
                    return isDescending ? 1 : -1;
                }
                int retVal = group1.getGroupTime().compareTo(group2.getGroupTime());
                return isDescending ? (0 - retVal) : retVal;
            }};
    }        

}
