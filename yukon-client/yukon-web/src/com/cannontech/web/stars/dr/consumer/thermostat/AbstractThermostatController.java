package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.web.stars.dr.consumer.AbstractConsumerController;

/**
 * Abstract controller for thermostats
 */
public abstract class AbstractThermostatController extends
        AbstractConsumerController {

    @ModelAttribute("thermostatIds")
    public List<Integer> getThermostatIds(HttpServletRequest request)
            throws ServletRequestBindingException {

        String thermostatIds = ServletRequestUtils.getStringParameter(request,
                                                                      "thermostatIds");

        // Override the toString method to get a comma separated list with no
        // leading or trailing brackets
        List<Integer> idList = new ArrayList<Integer>() {
            @Override
            public String toString() {
                return super.toString().replaceAll("[\\[|\\]]", "");
            }

        };

        // If thermostatIds exists, split and create Integer list
        if (!StringUtils.isBlank(thermostatIds)) {
            String[] ids = thermostatIds.split(",");
            for (String id : ids) {
                try {
                    int idInt = Integer.parseInt(id.trim());
                    idList.add(idInt);
                } catch(NumberFormatException nfe) {
                    CTILogger.error(nfe);
                }
            }
        }

        return idList;
    }
}
