package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.PaoTypeMasks;

@Controller
@RequestMapping("addPoints/*")
public class AddPointsController extends AddRemovePointsControllerBase {
    public static String VARIABLE_PRESELECTED_POINT_ARRAY = "preselectedPointIdentifiers";

    private static final Logger log = YukonLogManager.getLogger(AddPointsController.class);
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    // HOME
    @Override
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.ADD_POINTS);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/addPoints/addPointsHome.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "addPointsInputs", method = RequestMethod.GET)
    public String addPointsInputs(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        return "addPoints/addPointsHome.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        // PointIdentifiers
        final String identifyingPointData = ServletRequestUtils.getStringParameter(request, "pointTypeOffsets");
        final String pointTypeOffsets[] =
            StringUtils.isBlank(identifyingPointData) ? null : identifyingPointData.split(",");
        final List<String> ptos = pointTypeOffsets == null ? new ArrayList<>() : Arrays.asList(pointTypeOffsets);
        model.addAttribute(VARIABLE_PRESELECTED_POINT_ARRAY, ptos);

        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", true);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("updatePoints", updatePoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);

        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        model.addAttribute("deviceTypeEnumSet", deviceTypeSet);

        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap =
            getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        model.addAttribute("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        List<PaoTypeMasks> paoTypeMasksList =
            createExistsPointsMap(deviceTypeSet, maskExistingPoints, true, deviceCollection);
        model.addAttribute("paoTypeMasksList", paoTypeMasksList);

        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        model.addAttribute("sharedPaoTypeMasks", sharedPaoTypeMasks);
    }
    
    // EXECUTE ADD
    @Override
    @RequestMapping(value = "execute", method = RequestMethod.POST)
    public String execute(ModelMap model, HttpServletRequest request, YukonUserContext userContext, HttpServletResponse resp) throws ServletException, Exception {

        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        // options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        final boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskExistingPoints");
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("updatePoints", updatePoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);

        // mask existing points redirect
        String maskExistingPointsSubmitButton =
            ServletRequestUtils.getStringParameter(request, "maskExistingPointsSubmitButton");
        if (maskExistingPointsSubmitButton != null) {
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("maskExistingPoints", !maskExistingPoints); // toggle it!
            return "redirect:home";
        }

        // Check to see if points were supplied and create processor
        Map<PaoType, Set<PointTemplate>> pointTemplatesMap =
            extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
        SingleProcessor<YukonDevice> addPointsProcessor = getAddPointsProcessor(pointTemplatesMap, updatePoints);

        if (pointTemplatesMap.isEmpty()) {
            setupModel(model, request);
            String errorMsg = messageSourceAccessor.getMessage("yukon.common.device.bulk.addPointsHome.noPointsSuppliedMsg");
            return redirectWithError(model, errorMsg, deviceCollection, "addPoints/addPointsHome.jsp", resp);
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
        int key = startBulkProcessor(CollectionAction.ADD_POINTS, deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.ADD_POINTS, userContext, userInputs);

        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }
    
    // add points processor
    private SingleProcessor<YukonDevice> getAddPointsProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap,
            final boolean updatePoints) {

        SingleProcessor<YukonDevice> addPointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {

                PaoType paoType = device.getPaoIdentifier().getPaoType();
                try {
                    if (pointTemplatesMap.containsKey(paoType)) {
                        Set<PointTemplate> pointSet = pointTemplatesMap.get(paoType);
                        for (PointTemplate pointTemplate : pointSet) {

                            boolean pointExistsForDevice =
                                pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());

                            /* Check to see if the point name is in use by a point with a different offset */
                            if (!pointExistsForDevice
                                && pointDao.findPointByName(device, pointTemplate.getName()) != null) {
                                String errorMessage =
                                    "Point with name (" + pointTemplate.getName() + ") already exists for device ("
                                        + paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()) + ")";
                                log.debug(errorMessage);
                                throw new ProcessingException(errorMessage, "pointAlreadyExists",
                                    pointTemplate.getName(),
                                    paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()));
                            }

                            // add new point
                            if (!pointExistsForDevice) {

                                // add new
                                PointBase newPoint =
                                    pointCreationService.createPoint(device.getPaoIdentifier(), pointTemplate);
                                dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);

                                log.debug("Added point to device: deviceId=" + device.getPaoIdentifier().getPaoId()
                                    + " pointTemplate=" + pointTemplate);

                            } else {

                                // update point
                                if (updatePoints) {

                                    log.debug(
                                        "Point already exists for device, updatePoints=true, will attempt update: deviceId="
                                            + device.getPaoIdentifier().getPaoId() + " pointTemplate=" + pointTemplate);

                                    LitePoint litePoint =
                                        pointService.getPointForPao(device, pointTemplate.getPointIdentifier());

                                    PointBase pointBase = (PointBase) dbPersistentDao.retrieveDBPersistent(litePoint);

                                    if (pointBase instanceof AnalogPoint) {

                                        AnalogPoint analogPoint = (AnalogPoint) pointBase;
                                        analogPoint.getPointAnalog().setMultiplier(pointTemplate.getMultiplier());
                                        analogPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
                                        analogPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                                        dbPersistentDao.performDBChange(analogPoint, TransactionType.UPDATE);

                                    } else if (pointBase instanceof StatusPoint) {

                                        StatusPoint statusPoint = (StatusPoint) pointBase;
                                        statusPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                                        dbPersistentDao.performDBChange(statusPoint, TransactionType.UPDATE);

                                    } else if (pointBase instanceof AccumulatorPoint) {

                                        AccumulatorPoint accumulatorPoint = (AccumulatorPoint) pointBase;
                                        accumulatorPoint.getPointAccumulator().setMultiplier(
                                            pointTemplate.getMultiplier());
                                        accumulatorPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
                                        accumulatorPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                                        dbPersistentDao.performDBChange(accumulatorPoint, TransactionType.UPDATE);

                                    } else {

                                        log.debug("Point type not supported, not updating: deviceId="
                                            + device.getPaoIdentifier().getPaoId() + " pointId=" + litePoint.getLiteID()
                                            + " pointType=" + litePoint.getLiteType());
                                    }

                                    log.debug("Updated point for device: deviceId="
                                        + device.getPaoIdentifier().getPaoId() + " point=" + pointTemplate);

                                } else {

                                    log.debug(
                                        "Point already exists for device and updatePoints=false, not updating: deviceId="
                                            + device.getPaoIdentifier().getPaoId() + " point=" + pointTemplate);
                                }
                            }
                        }

                    } else {

                        log.debug("No points selected for device type, none added or updated: deviceId="
                            + device.getPaoIdentifier().getPaoId());
                    }
                } catch (PersistenceException e) {
                    throw new ProcessingException("Add point failed", "addPointFailed", e,
                        paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()),
                        paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()));
                } catch (NotFoundException e) {
                    throw new ProcessingException("Add point failed", "addPointFailed", e,
                        paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()),
                        paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()));
                }
            }
        };

        return addPointsProcessor;
    }
    
}