package com.cannontech.web.widget;

import java.beans.PropertyEditor;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.PaoSelectionMethod;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/paoNotesSearchWidget")
public class PaoNotesSearchWidget extends AdvancedWidgetControllerBase {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) {
        model.addAttribute("paoNotesFilter", new PaoNotesFilter());
        model.addAttribute("paoSelectionMethods", PaoSelectionMethod.values());
        model.addAttribute("allDevicesEnumValue", PaoSelectionMethod.allDevices);
        model.addAttribute("selectIndividuallyEnumValue", PaoSelectionMethod.selectIndividually);
        model.addAttribute("byDeviceGroupsEnumValue", PaoSelectionMethod.byDeviceGroups);
        return "paoNotesSearchWidget/render.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);
        binder.registerCustomEditor(Date.class, "dateRange.min", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "dateRange.max", dayEndDateEditor);
    }
}
