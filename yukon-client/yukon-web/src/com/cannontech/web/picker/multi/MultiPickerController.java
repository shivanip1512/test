package com.cannontech.web.picker.multi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.web.picker.pao.PaoPickerController;

public class MultiPickerController extends PaoPickerController {
    public MultiPickerController() {
        super();
    }
    
    @Override
    public ModelAndView initial(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("multiInner");
        String pickerId = ServletRequestUtils.getStringParameter(request, "pickerId", "");
        mav.addObject("pickerId", pickerId);

        String selectionLinkName = ServletRequestUtils.getStringParameter(request, "selectionLinkName", "Done");
        mav.addObject("selectionLinkName", selectionLinkName);
        
        return mav;
    }
}
