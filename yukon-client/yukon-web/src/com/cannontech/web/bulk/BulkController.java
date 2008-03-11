package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.BulkProcess;
import com.cannontech.common.bulk.action.BulkActionEnum;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private DeviceGroupProviderDao deviceGroupDao = null;
    private PaoDao paoDao = null;

    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public ModelAndView deviceSelection(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("deviceSelection.jsp");

        List<DeviceGroup> groupList = deviceGroupDao.getAllGroups();
        mav.addObject("groupList", groupList);

        return mav;
    }

    public ModelAndView bulkActions(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("bulkActions.jsp");

        // Pass device collection through
        this.addDeviceCollectionToModel(mav, request);

        BulkActionEnum[] actions = BulkActionEnum.values();
        mav.addObject("actions", actions);

        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        mav.addObject("routes", routes);

        mav.addObject("types", this.getDeviceTypeList());

        return mav;
    }

    public ModelAndView currentProcesses(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("currentBulkProcesses.jsp");

        List<BulkProcess> list = this.getInProcessList();
        mav.addObject("inProcessList", list);

        return mav;
    }

    public ModelAndView removeProcess(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("currentBulkProcesses.jsp");

        int processId = ServletRequestUtils.getRequiredIntParameter(request,
                                                                    "id");

        this.removeProcess(processId);

        List<BulkProcess> list = this.getInProcessList();
        mav.addObject("inProcessList", list);

        return mav;
    }

    private List<YukonType> getDeviceTypeList() {
        List<YukonType> typeList = new ArrayList<YukonType>();

        typeList.add(new YukonType(DeviceTypes.STRING_MCT_410CL[0],
                                   DeviceTypes.MCT410CL));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_410FL[0],
                                   DeviceTypes.MCT410FL));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_410GL[0],
                                   DeviceTypes.MCT410GL));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_410IL[0],
                                   DeviceTypes.MCT410IL));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_430A[0],
                                   DeviceTypes.MCT430A));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_430S4[0],
                                   DeviceTypes.MCT430S4));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_430SL[0],
                                   DeviceTypes.MCT430SL));
        typeList.add(new YukonType(DeviceTypes.STRING_MCT_470[0],
                                   DeviceTypes.MCT470));

        return typeList;

    }

    public static class YukonType {
        private String name = null;
        private Integer id = null;

        public YukonType(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }
    }
}
