package com.cannontech.web.bulk;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.BulkProcessImpl;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.action.BulkActionEnum;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.EditableDevice;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.MetaObject;
import com.cannontech.web.util.TextView;

/**
 * Spring controller class for bulk operations
 */
public class BulkActionController extends BulkControllerBase {

    private BulkProcessor bulkProcessor = null;
    private ProcessorFactory processorFactory = null;
    private ObjectMapperFactory objectMapperFactory = null;

    private PaoGroupsWrapper paoGroupsWrapper = null;
    private PaoDao paoDao = null;

    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }

    public void setProcessorFactory(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    public void setObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    // Controller methods

    /**
     * Method used to bulk change a collection of devices to a new type
     */
    public ModelAndView changeType(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/currentProcesses");

        int type = ServletRequestUtils.getRequiredIntParameter(request, "type");

        DeviceCollection deviceCollection = this.getDeviceCollection(request);

        // Kick off bulk action
        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
        ObjectMapper<YukonDevice, YukonDevice> mapper = objectMapperFactory.createPassThroughMapper();
        Processor<YukonDevice> processor = processorFactory.createChangeTypeProcessor(type);
        bulkProcessor.backgroundBulkProcess(deviceCollection.getDeviceIterator(),
                                            mapper,
                                            processor,
                                            callback);

        // Add process to inProcessList
        String typeString = paoGroupsWrapper.getPAOTypeString(type);
        String actionKey = BulkActionEnum.CHANGE_TYPE.getDescriptionKey();
        this.addBulkProcess(request,
                            callback,
                            deviceCollection,
                            actionKey,
                            typeString);

        return mav;
    }

    /**
     * Method used to bulk delete a collection of devices
     */
    public ModelAndView delete(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/currentProcesses");

        DeviceCollection deviceCollection = this.getDeviceCollection(request);

        // Kick off bulk action
        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
        ObjectMapper<YukonDevice, YukonDevice> mapper = objectMapperFactory.createPassThroughMapper();
        Processor<YukonDevice> processor = processorFactory.createDeleteDeviceProcessor();
        bulkProcessor.backgroundBulkProcess(deviceCollection.getDeviceIterator(),
                                            mapper,
                                            processor,
                                            callback);

        // Add process to inProcessList
        String actionKey = BulkActionEnum.DELETE_DEVICES.getDescriptionKey();
        this.addBulkProcess(request, callback, deviceCollection, actionKey);

        return mav;
    }

    /**
     * Method used to bulk enable or disable a collection of devices
     */
    public ModelAndView enableDisableDevices(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/currentProcesses");

        boolean enable = ServletRequestUtils.getRequiredBooleanParameter(request,
                                                                         "enable");

        DeviceCollection deviceCollection = this.getDeviceCollection(request);

        // Kick off bulk action
        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
        ObjectMapper<YukonDevice, YukonDevice> mapper = objectMapperFactory.createPassThroughMapper();
        Processor<YukonDevice> processor = processorFactory.createEnableDisableProcessor(enable);
        bulkProcessor.backgroundBulkProcess(deviceCollection.getDeviceIterator(),
                                            mapper,
                                            processor,
                                            callback);

        // Add process to inProcessList
        String actionKey = BulkActionEnum.ENABLE_DISABLE_DEVICES.getDescriptionKey();
        this.addBulkProcess(request,
                            callback,
                            deviceCollection,
                            actionKey,
                            ((enable) ? "Enable" : "Disable"));

        return mav;
    }

    /**
     * Method used to bulk update the route for a collection of devices
     */
    public ModelAndView updateRoute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/currentProcesses");

        int route = ServletRequestUtils.getRequiredIntParameter(request,
                                                                "route");

        DeviceCollection deviceCollection = this.getDeviceCollection(request);

        // Kick off bulk action
        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
        ObjectMapper<YukonDevice, YukonDevice> mapper = objectMapperFactory.createPassThroughMapper();
        Processor<YukonDevice> processor = processorFactory.createUpdateRouteProcessor(route);
        bulkProcessor.backgroundBulkProcess(deviceCollection.getDeviceIterator(),
                                            mapper,
                                            processor,
                                            callback);

        // Add process to inProcessList
        LiteYukonPAObject liteRoute = paoDao.getLiteYukonPAO(route);
        String actionKey = BulkActionEnum.UPDATE_ROUTE.getDescriptionKey();
        this.addBulkProcess(request,
                            callback,
                            deviceCollection,
                            actionKey,
                            liteRoute.getPaoName());

        return mav;
    }

    /**
     * Method used to bulk edit a collection of devices in a grid
     */
    public ModelAndView editGrid(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();

        ColumnInfo info = new ColumnInfo();
        info.setColumnName("Id");
        info.setColumnWidth(70);
        columnInfoList.add(info);

        info = new ColumnInfo();
        info.setColumnName("Name");
        info.setColumnWidth(70);
        columnInfoList.add(info);

        MetaObject metaObject = new MetaObject();
        metaObject.setColumns(columnInfoList);
        metaObject.setRoot("rows");
        metaObject.setId("id");

        ModelAndView mav = new ModelAndView("bulkGrid.jsp");
        mav.addObject("metaObject", metaObject);

        DeviceCollection deviceCollection = this.getDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);

        return mav;
    }

    /**
     * Method used to get the grid data for an editor grid
     */
    public ModelAndView gridData(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        DeviceCollection deviceCollection = this.getDeviceCollection(request);
        List<EditableDevice> editableDeviceList = this.convertToEditableDeviceList(deviceCollection);

        JSONArray dataArray = new JSONArray(editableDeviceList);
        OutputStream oStream = response.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(oStream));
        writer.write(dataArray.toString());
        writer.flush();

        return null;
    }

    public ModelAndView saveEdit(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView(new TextView("Save successful."));

        String data = ServletRequestUtils.getStringParameter(request, "data");
        JSONArray array = new JSONArray(data);

        // Convert the JSON request data into editable devices that contain the
        // updated device data
        List<EditableDevice> editableDeviceList = new ArrayList<EditableDevice>();
        for (Object object : array.toArray()) {

            JSONObject jsonObject = (JSONObject) object;
            EditableDevice editableDevice = (EditableDevice) JSONObject.toBean(jsonObject,
                                                                               EditableDevice.class);

            editableDeviceList.add(editableDevice);

        }

        // Kick off bulk action
        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
        Processor<EditableDevice> processor = processorFactory.createEditDeviceProcessor();
        bulkProcessor.backgroundBulkProcess(editableDeviceList.iterator(),
                                            processor,
                                            callback);

        // Add process to inProcessList
        DeviceCollection deviceCollection = this.getDeviceCollection(request);
        String actionKey = BulkActionEnum.EDIT_DEVICES.getDescriptionKey();
        this.addBulkProcess(request, callback, deviceCollection, actionKey);

        return mav;
    }

    // Helper methods

    /**
     * Helper Method to add a bulk process to the currently executing bulk
     * process list
     */
    private void addBulkProcess(HttpServletRequest request,
            CollectingBulkProcessorCallback callback,
            DeviceCollection collection, String actionKey,
            String... actionParams) {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        BulkProcessImpl process = new BulkProcessImpl();
        process.setId(this.getNextBulkProcessId());
        process.setUserContext(userContext);
        process.setStartDate(new Date());
        process.setResultHolder(callback);
        process.setDeviceCollection(collection);
        process.setActionMessage(actionKey, actionParams);

        this.addProcess(process);

    }

    /**
     * Helper method to generate a list of editable devices from a device
     * collection
     * @param collection - Collection to generate data objects for
     * @return - A list of editable devices for the collection
     */
    private List<EditableDevice> convertToEditableDeviceList(
            DeviceCollection collection) {

        List<EditableDevice> editableDeviceList = new ArrayList<EditableDevice>();

        List<YukonDevice> deviceList = collection.getDeviceList();
        for (YukonDevice device : deviceList) {

            EditableDevice editableDevice = new EditableDevice();
            int deviceId = device.getDeviceId();
            editableDevice.setId(deviceId);

            LiteYukonPAObject liteDevice = paoDao.getLiteYukonPAO(deviceId);
            editableDevice.setName(liteDevice.getPaoName());

            // TODO - populate the rest of the editableDevice based on the
            // columns needed in the editor grid

            editableDeviceList.add(editableDevice);
        }

        return editableDeviceList;
    }

}
