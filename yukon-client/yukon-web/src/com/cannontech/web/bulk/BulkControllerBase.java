package com.cannontech.web.bulk;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;

/**
 * Spring controller base class for bulk operations
 */
public class BulkControllerBase extends MultiActionController {

    protected DeviceCollectionFactory deviceCollectionFactory = null;

//    private int bulkProcessId = 1;
//    private static final Map<Integer, BulkProcess> inProcessMap = Collections.synchronizedMap(new HashMap<Integer, BulkProcess>());

    @Required
    public void setDeviceCollectionFactory(
            DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }

    /**
     * Method to get a device collection based on the parameters in the request
     * @param request - Current request
     * @return A device collection generated from the request params
     * @throws ServletRequestBindingException
     */
    protected DeviceCollection getDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        return deviceCollection;

    }

    /**
     * Method to add a device collection to the model and view based on the
     * parameters in the request
     * @param mav - ModelAndView to add the collection to
     * @param request - Current request
     * @throws ServletRequestBindingException
     */
    protected void addDeviceCollectionToModel(ModelAndView mav, HttpServletRequest request) throws ServletRequestBindingException {
        
        DeviceCollection deviceCollection = this.getDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
    }
    
    

//    /**
//     * Method to add a bulk process to the in process list
//     * @param process - Bulk Process to add
//     */
//    protected void addProcess(BulkProcess process) {
//        BulkControllerBase.inProcessMap.put(process.getId(), process);
//    }
//
//    /**
//     * Method to get the list of bulk processes that are running
//     * @return List of bulk processes
//     */
//    protected List<BulkProcess> getInProcessList() {
//
//        Collection<BulkProcess> values = BulkControllerBase.inProcessMap.values();
//        List<BulkProcess> processList = new ArrayList<BulkProcess>();
//        processList.addAll(values);
//
//        return processList;
//    }
//
//    /**
//     * Method to remove a bulk process from the in process list
//     * @param processId - Id of process to remove
//     */
//    protected void removeProcess(int processId) {
//        BulkControllerBase.inProcessMap.remove(processId);
//    }
//
//    /**
//     * Method to get the next available bulk process id
//     * @return The next id
//     */
//    protected synchronized int getNextBulkProcessId() {
//        return this.bulkProcessId++;
//    }

}
