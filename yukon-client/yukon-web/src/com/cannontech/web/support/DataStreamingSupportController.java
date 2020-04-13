package com.cannontech.web.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.web.security.annotation.CheckCparmLicense;

@Controller
@CheckCparmLicense(license = MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED)
public class DataStreamingSupportController {

    @Autowired private DataStreamingAttributeHelper dsHelper;

    @RequestMapping("/dataStreamingSupport")
    public String view(ModelMap model) {
        dsHelper.buildMatrixModel(model);
        return "dataStreaming/dataStreamingSupport.jsp";
    }

    @RequestMapping(value="matrixView")
    public String getMatrixView(ModelMap model){
        dsHelper.buildMatrixModel(model);
        return "dataStreaming/matrix.jsp";
    }
}
