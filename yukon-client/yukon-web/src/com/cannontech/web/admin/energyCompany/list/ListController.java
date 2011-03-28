package com.cannontech.web.admin.energyCompany.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.google.common.collect.SortedSetMultimap;

@Controller
@RequestMapping("/energyCompany/list/*")
public class ListController {
    private YukonListDao yukonListDao;
    private SelectionListService selectionListService;
    private EnergyCompanyService energyCompanyService;

    @RequestMapping
    public String list(ModelMap model, YukonUserContext context, EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> lists =
            selectionListService.getUserEditableLists(ecInfo.getEnergyCompanyId(), context);
        model.addAttribute("lists", lists.asMap());

        return "list/list.jsp";
    }

    @RequestMapping
    public String view(ModelMap model, int listId, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        YukonSelectionList list = yukonListDao.getYukonSelectionList(listId);
        model.addAttribute("list", list);
        model.addAttribute("isInherited", selectionListService.isListInherited(ecId, list));

        model.addAttribute("mode", PageEditMode.VIEW);
        return "list/view.jsp";
    }

    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }

    @Autowired
    public void setSelectionListService(SelectionListService selectionListService) {
        this.selectionListService = selectionListService;
    }

    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
}
