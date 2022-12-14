package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.google.common.collect.HashMultimap;

@Controller
@RequestMapping("removePoints/*")
public class RemovePointsController extends AddRemovePointsControllerBase {

    private Logger log = YukonLogManager.getLogger(AddPointsController.class);
    @Autowired DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    // HOME
    @Override
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.REMOVE_POINTS);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/removePoints/removePointsHome.jsp");
        return "../collectionActions/collectionActionsHome.jsp";    }
    
    @RequestMapping(value = "removePointsInputs", method = RequestMethod.GET)
    public String removePointsInputs(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        return "removePoints/removePointsHome.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", true);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskMissingPoints", maskMissingPoints);

        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);

        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap =
            getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        model.addAttribute("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        List<PaoTypeMasks> paoTypeMasksList =
            createExistsPointsMap(deviceTypeSet, maskMissingPoints, false, deviceCollection);
        model.addAttribute("paoTypeMasksList", paoTypeMasksList);

        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        model.addAttribute("sharedPaoTypeMasks", sharedPaoTypeMasks);
    }
    
    @Override
    protected void fillInPointTemplateMask(boolean maskIfExistOnAllDevices,
            HashMultimap<PaoType, SimpleDevice> paoTypeToSimpleDeviceMultiMap, PaoType paoType,
            Map<PointTemplate, Boolean> pointTemplateMaskMap) {

        // loop over each possible point for this device type
        for (PointTemplate pointTemplate : pointTemplateMaskMap.keySet()) {

            // check each device of this type and see if it has the point or not
            boolean noneHavePoint = true;
            for (SimpleDevice device : paoTypeToSimpleDeviceMultiMap.get(paoType)) {
                boolean pointExistsForDevice =
                    pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                if (pointExistsForDevice) {
                    noneHavePoint = false;
                    break;
                }
            }

            // after looking at all the device of this type, did we find at least one that had it?
            if (noneHavePoint) {
                pointTemplateMaskMap.put(pointTemplate, true);
            }
        }
    }

    // EXECUTE REMOVE
    @Override
    @RequestMapping(value = "execute", method = RequestMethod.POST)
    public String execute(ModelMap model, HttpServletRequest request, YukonUserContext userContext, HttpServletResponse resp) throws ServletException, Exception {

        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        // options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        boolean maskMissingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskMissingPoints");
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskMissingPoints", maskMissingPoints);

        // maskExisting POINTS REDIRECT
        String maskMissingPointsSubmitButton =
            ServletRequestUtils.getStringParameter(request, "maskMissingPointsSubmitButton");
        if (maskMissingPointsSubmitButton != null) {
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("maskMissingPoints", !maskMissingPoints); // toggle it!
            return "redirect:home";      
        }

        // create processor
        Map<PaoType, Set<PointTemplate>> pointTemplatesMap =
            extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
        SingleProcessor<YukonDevice> addPointsProcessor = getRemovePointsProcessor(pointTemplatesMap);

        if (pointTemplatesMap.isEmpty()) {
            setupModel(model, request);
            String errorMsg = messageSourceAccessor.getMessage("yukon.common.device.bulk.removePointsHome.noPointsSuppliedMsg");
            return redirectWithError(model, errorMsg, deviceCollection, "removePoints/removePointsHome.jsp", resp);
        }

        // start processor
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        for (Map.Entry<PaoType, Set<PointTemplate>> entry : pointTemplatesMap.entrySet()) {
            List<String> points = new ArrayList<>();
            entry.getValue().forEach(template -> {
                points.add(template.getName());
            });
            userInputs.put(entry.getKey().getDbString(), String.join(", ", points));
        }
        int key = startBulkProcessor(CollectionAction.REMOVE_POINTS, deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.REMOVE_POINTS, userContext, userInputs);

        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }

    // remove points processor
    private SingleProcessor<YukonDevice> getRemovePointsProcessor(
            final Map<PaoType, Set<PointTemplate>> pointTemplatesMap) {

        SingleProcessor<YukonDevice> removePointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {

                PaoType paoType = device.getPaoIdentifier().getPaoType();
                if (pointTemplatesMap.containsKey(paoType)) {
                    Set<PointTemplate> pointSet = pointTemplatesMap.get(paoType);
                    for (PointTemplate pointTemplate : pointSet) {

                        boolean pointExistsForDevice =
                            pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                        if (pointExistsForDevice) {

                            LitePoint liteDeletePoint =
                                pointService.getPointForPao(device, pointTemplate.getPointIdentifier());
                            PointBase deletePoint = (PointBase) LiteFactory.convertLiteToDBPers(liteDeletePoint);

                            log.debug("Removing point from device: deletePointId=" + liteDeletePoint.getLiteID()
                                + " point=" + pointTemplate + " deviceId=" + device.getPaoIdentifier().getPaoId());
                            dbPersistentDao.performDBChange(deletePoint, Transaction.DELETE);

                        } else {
                            log.debug("Point does not exist for device, not removing: point=" + pointTemplate
                                + " deviceId=" + device.getPaoIdentifier().getPaoId());
                        }
                    }
                } else {
                    log.debug("Not points selected for device type, none removed: deviceId="
                        + device.getPaoIdentifier().getPaoId());
                }
            }
        };

        return removePointsProcessor;
    }

}