package com.cannontech.web.bulk;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;


public class MassChangeController extends BulkControllerBase {

    private BulkFieldService bulkFieldService = null;
    private DeviceDao deviceDao = null;
    
    /**
     * SELECT MASS CHANGE TYPE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massChangeSelect(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("mass/massChangeSelect.jsp");
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // available masss change operations
        List<BulkField<?, ?>> massChangableBulkFields = bulkFieldService.getMassChangableBulkFields();
        mav.addObject("massChangableBulkFields", massChangableBulkFields);
        
        // pass previously selected bulk field name through
        mav.addObject("selectedBulkFieldName", ServletRequestUtils.getStringParameter(request, "selectedBulkFieldName", ""));
        
        return mav;
    }
    
    
    /**
     * MASS DELETE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("mass/massDeleteConfirm.jsp");
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        mav.addObject("deviceCount", deviceCount);
        
        
        return mav;
    }
    
    /**
     * DO MASS DELETE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView doMassDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = null;
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancelButton", null);
        String deleteButton = ServletRequestUtils.getStringParameter(request, "deleteButton", null);
        String returnButton = ServletRequestUtils.getStringParameter(request, "returnButton", null);
        
        // CANCEL
        if (cancelButton != null) {
            
            mav = new ModelAndView("collectionActions.jsp");
            
            // pass along deviceCollection
            DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
            mav.addObject("deviceCollection", deviceCollection);
        }
        
        // DO DELETE
        else if (deleteButton != null) {
            
            mav = new ModelAndView("mass/massDeleteResults.jsp");
            
            DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
            long deletedItemsCount = 0;
            
            List<YukonDevice> devices = deviceCollection.getDeviceList();
            for (YukonDevice device : devices) {
                deviceDao.removeDevice(device);
                deletedItemsCount++;
            }
            
            mav.addObject("deletedItemsCount", deletedItemsCount);
        }
        
        // RETURN TO DEVICE SELECTION
        else if (returnButton != null) {
            
            mav = new ModelAndView("redirect:/spring/bulk/deviceSelection");
        }
        
        return mav;
    }
    
    
    
    
    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
}
