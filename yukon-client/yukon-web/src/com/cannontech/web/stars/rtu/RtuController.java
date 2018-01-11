package com.cannontech.web.stars.rtu;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.HeartbeatConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;

@Controller
public class RtuController {
    
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private IDatabaseCache cache;
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        RtuDnp rtu = getRtuDnp(id, model);
        model.addAttribute("rtu", rtu);
        return "/rtu/rtuDetail.jsp";
    }
    
    private RtuDnp getRtuDnp(int id, ModelMap model) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
        RtuDnp rtu = new RtuDnp();
        rtu.setName(pao.getPaoName());
        rtu.setPaoType(pao.getPaoType());
        rtu.setDisableFlag(Boolean.parseBoolean(pao.getDisableFlag()));
        rtu.setDeviceScanRateMap(new HashMap<String, DeviceScanRate>());
        model.addAttribute("dnpConfig", getDnpConfiguration(pao));
        model.addAttribute("heartbeatConfig", getHeartbeatConfiguration(pao));
        return rtu;
    }
    
    private DNPConfiguration getDnpConfiguration(YukonPao device) {
        LightDeviceConfiguration configuration =
            configurationDao.findConfigurationForDevice(new SimpleDevice(device.getPaoIdentifier()));
        DeviceConfiguration deviceConfiguration =
            configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
        return configurationDao.getDnpConfiguration(deviceConfiguration);
    }

    private HeartbeatConfiguration getHeartbeatConfiguration(YukonPao device) {
        LightDeviceConfiguration configuration =
                configurationDao.findConfigurationForDevice(new SimpleDevice(device.getPaoIdentifier()));
        DeviceConfiguration deviceConfiguration =
            configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
        return configurationDao.getHeartbeatConfiguration(deviceConfiguration);
    }
    
    //get points - CbcController line 240
}