package com.cannontech.web.stars.rtu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.PageEditMode;
import com.cannontech.yukon.IDatabaseCache;

@Controller
public class RtuController {
    
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private IDatabaseCache cache;
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        RtuDnp rtu = createMockedRtuDnp(id);
        model.addAttribute("rtu", rtu);
        return "/rtu/rtuDetail.jsp";
    }
    
    private RtuDnp createMockedRtuDnp(int id) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
        RtuDnp rtu = new RtuDnp();
        rtu.setId(id);
        rtu.setName(pao.getPaoName());
        rtu.setPaoType(pao.getPaoType());
        rtu.setDisableFlag(Boolean.parseBoolean(pao.getDisableFlag()));
        return rtu;
    }
    
    private DNPConfiguration getDnpConfiguration(SimpleDevice device) {
        LightDeviceConfiguration configuration = configurationDao.findConfigurationForDevice(device);
        DeviceConfiguration deviceConfiguration =
            configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
        return configurationDao.getDnpConfiguration(deviceConfiguration);
    }
}