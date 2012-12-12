package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;

@Configurable("meterNumberTagPrototype")
public class MeterNumberTag extends YukonTagSupport {
    private DeviceDao deviceDao;

    private YukonPao yukonPao = null;

    @Override
    public void doTag() throws JspException, IOException {
        String meterNumber;
        LiteDeviceMeterNumber deviceMeterNumber = deviceDao.getLiteDeviceMeterNumber(yukonPao.getPaoIdentifier().getPaoId());
        if(deviceMeterNumber != null) {
            meterNumber = deviceMeterNumber.getMeterNumber();
        } else {
            meterNumber = "-";
        }
        
        JspWriter out = getJspContext().getOut();
        out.print(String.format("<span>%s</span>", meterNumber));
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void setYukonPao(YukonPao yukonPao) {
        this.yukonPao = yukonPao;
    }
}
