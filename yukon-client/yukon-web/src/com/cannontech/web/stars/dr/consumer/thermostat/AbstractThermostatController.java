package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.dr.consumer.AbstractConsumerController;

/**
 * Base controller for Consumer-side Thermostat operations
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
        @SuppressWarnings("serial")
        List<Integer> idList = new ArrayList<Integer>() {
            @Override
            public String toString() {
                return super.toString().replaceAll("[\\[|\\]]", "");
            }

        };

        // If thermostatIds exists, split and create Integer list
        if (!StringUtils.isBlank(thermostatIds)) {
        	List<Integer> tempIdList = ServletUtil.getIntegerListFromString(thermostatIds);
        	idList.addAll(tempIdList);
        }

        return idList;
    }
}
