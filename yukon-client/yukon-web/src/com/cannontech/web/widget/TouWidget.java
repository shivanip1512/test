package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.tou.dao.TouDao;
import com.cannontech.amr.tou.model.TouAttributeMapping;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class TouWidget extends WidgetControllerBase {

    private AttributeService attributeService;
    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private TouDao touDao;

    private boolean readable = false;
    /**
     * This method renders the default deviceGroupWidget
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("touWidget/render.jsp");
        Meter meter = getMeter(request);
        
        // Gets all the attributes that are needed to show time of use.
        List<TouAttributeMapping> touRatesList = touDao.getTouMappings();
        Set<Attribute> existingAttributes = getExistingAttributes(meter, touRatesList);
        List<KeyValuePair> touRates = touAttributesToHash(existingAttributes, touRatesList);
        
        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("isRead", false);
        mav.addObject("rateTypes", touRates);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        readable = meterReadService.isReadable(meter, existingAttributes, user);
        mav.addObject("readable", readable);
        
        return mav;
    }

    /**
     * This method gets the attributes that we are using and sends them to 
     * porter to fetch the most recent data of those attributes so the AttributeValue
     * tags can update their values.
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        Meter meter = getMeter(request);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        List<TouAttributeMapping> touRatesList = touDao.getTouMappings();
        Set<Attribute> existingAttributes = getExistingAttributes(meter, touRatesList);

        CommandResultHolder result = meterReadService.readMeter(meter, existingAttributes, user);

        mav.addObject("result", result);
        mav.addObject("readable", readable);
        
        return mav;
    }
    
    /**
     * @param request
     * @return
     * @throws ServletRequestBindingException
     */
    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }

    /**
     * Returns a union of all the attributes int the touList and 
     * the attributes that exist on the actual device
     * 
     * @param device
     * @param touList
     * @return
     */
    private Set<Attribute> getExistingAttributes(YukonDevice device,  List<TouAttributeMapping> touList){
        List<Attribute> neededAttributes = new ArrayList<Attribute>();;
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(device);

        for (TouAttributeMapping touRate : touList) {
            neededAttributes.addAll(touRate.getAllAttributes());
        }
        
        allExistingAttributes.retainAll(neededAttributes);
        
        return allExistingAttributes;
    }
    
    /**
     * Checks against the existing attributes on the device and clears any attributes that does not
     * exist on the device.
     * 
     * @param existingAttributes
     * @param touList
     * @return
     */
    public List<KeyValuePair> touAttributesToHash(
            Set<Attribute> existingAttributes, List<TouAttributeMapping> touList) {

        List<KeyValuePair> resultList = new ArrayList<KeyValuePair>();
        
        for (int i = 0; i < touList.size(); i++) {
            TouAttributeMapping tou = touList.get(i);
            if (existingAttributes.contains(tou.getUsage()) || existingAttributes.contains(tou.getPeak())) {
                List<AttributeValuePair> attributeList = new ArrayList<AttributeValuePair>();
                KeyValuePair keyValuePair = new KeyValuePair(tou.getDisplayName(), attributeList);
                
                resultList.add(keyValuePair);
                
                if (existingAttributes.contains(tou.getUsage())) {
                    AttributeValuePair tempValuePair = new AttributeValuePair("Usage", tou.getUsage());
                    attributeList.add(tempValuePair);
                }
                if (existingAttributes.contains(tou.getPeak())) {
                    AttributeValuePair tempValuePair = new AttributeValuePair("Peak Demand", tou.getPeak());
                    attributeList.add(tempValuePair);
                }
                
            }
        }
        return resultList;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

    @Required
    public void setTouDao(TouDao touDao) {
        this.touDao = touDao;
    }

    public class AttributeValuePair{
        String label;
        Attribute attribute;
        
        public AttributeValuePair(String label, Attribute attribute){
            this.label = label;
            this.attribute = attribute;
        }
        
        public Attribute getAttribute() {
            return attribute;
        }
        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
    }
    
    public class KeyValuePair{
        String key;
        List attributeValuePairList;
        
        public KeyValuePair(String key, List attributeValuePairList){
            this.key = key;
            this.attributeValuePairList = attributeValuePairList;
        }
        
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List getAttributeValuePairList() {
            return attributeValuePairList;
        }

        public void setAttributeValuePairList(List attributeValuePairList) {
            this.attributeValuePairList = attributeValuePairList;
        }
    }
}

