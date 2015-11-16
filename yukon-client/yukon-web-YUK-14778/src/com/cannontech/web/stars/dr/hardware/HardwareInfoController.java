package com.cannontech.web.stars.dr.hardware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.util.JsonView;

@Controller
public class HardwareInfoController extends MultiActionController {

	private PaoDao paoDao;
	
	@RequestMapping(value = "/hardware/info/getMeterAddress", method = RequestMethod.GET)
	public ModelAndView getMeterAddress(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	
    	int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
    	LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
    	
    	ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("address", pao.getAddress());
        
        return mav;
    }
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
