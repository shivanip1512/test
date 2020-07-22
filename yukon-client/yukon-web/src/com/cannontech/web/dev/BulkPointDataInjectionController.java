package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.dao.impl.RphSimulatorDaoImpl.RphSimulatorPointType;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.development.model.BulkPointInjectionParameters;
import com.cannontech.development.model.RphSimulatorParameters;
import com.cannontech.development.service.BulkPointDataInjectionService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dev.model.PointInjectionStatus;
import com.cannontech.web.dev.model.RphSimulatorPointStatus;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.type.PeriodType;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.RateLimiter;

@Controller
@RequestMapping("/bulkPointInjection/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class BulkPointDataInjectionController {
    private final Logger log = YukonLogManager.getLogger(BulkPointDataInjectionController.class);

    @Autowired private AttributeService attributeService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private BulkPointDataInjectionService bulkPointDataInjectionService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private DeviceGroupProviderDao deviceGroupProviderDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired @Qualifier("main") Executor executor;

    private BulkFakePointInjectionDto bulkInjection;
    private final static String baseKey = "yukon.web.modules.support.bulkPointInjection";
    private final PointInjectionStatus injectionStatus = new PointInjectionStatus();
    private final RphSimulatorPointStatus rphSimulatorPointStatus = new RphSimulatorPointStatus();
    private Validator bulkValidator = new SimpleValidator<BulkFakePointInjectionDto>(BulkFakePointInjectionDto.class) {
        @Override
        public void doValidation(BulkFakePointInjectionDto bulkInjection, Errors errors) {
            if (bulkInjection.getPeriod().toStandardDuration().equals(Duration.ZERO)) {
                // Will run forever. Not good.
                errors.rejectValue("period", baseKey + ".noPeriod");
            }
            if (bulkInjection.getPointQualities() == null || bulkInjection.getPointQualities().isEmpty()) {
                errors.reject(baseKey + ".noQuality");
            }
        }
    };

    @RequestMapping("supported-attributes")
    @ResponseBody
    public Map<String, Object> supportedAttributes(String deviceGroupName, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        Multimap<PaoType, Attribute> supportedAttributesByPao = paoDefinitionDao.getPaoTypeAttributesMultiMap();

        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(deviceGroupName);
        Collection<PaoType> paoTypes =
            Collections2.transform(deviceGroupProviderDao.getDevices(deviceGroup), SimpleDevice.TO_PAO_TYPE);

        if (paoTypes.isEmpty()) {
            Map<String, Object> returnJson = Maps.newHashMapWithExpectedSize(2);
            returnJson.put("error", true);
            returnJson.put("errorMsg", "No devices in group");
            return returnJson;
        }

        Set<? extends Attribute> fullSupport = Sets.newHashSet(BuiltInAttribute.values());
        Set<Attribute> partialSupport = new HashSet<>();
        for (PaoType paoType : paoTypes) {
            fullSupport.retainAll(supportedAttributesByPao.get(paoType));
            partialSupport.addAll(supportedAttributesByPao.get(paoType));
        }

        List<? extends Attribute> fullSupportSorted =
            objectFormattingService.sortDisplayableValues(fullSupport, null, null, userContext);
        List<? extends Attribute> partialSupportSorted =
            objectFormattingService.sortDisplayableValues(partialSupport, null, null, userContext);

        Map<String, Object> returnJson = Maps.newHashMapWithExpectedSize(4);
        returnJson.put("fullSupport", fullSupportSorted);
        returnJson.put("fullSupportDisplayable", accessor.getMessages(fullSupportSorted));
        returnJson.put("partialSupport", partialSupportSorted);
        returnJson.put("partialSupportDisplayable", accessor.getMessages(partialSupportSorted));
        return returnJson;
    }

    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("stop", Instant.now());
        model.addAttribute("start", Instant.now().minus(Duration.standardDays(30)));
        model.addAttribute("injectionStatus", injectionStatus(userContext));
        return "bulkPointInjection/home.jsp";
    }
    
    @RequestMapping("rphSimulator")
    public String rphSimulator(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("stop", Instant.now());
        model.addAttribute("start", Instant.now().minus(Duration.standardDays(30)));
        return "bulkPointInjection/rphSimulator.jsp";
    }
    
    @RequestMapping("injection-status")
    @ResponseBody
    public Map<String, Object> injectionStatus(YukonUserContext userContext) {
        Map<String, Object> injectionStatusJson = Maps.newHashMapWithExpectedSize(10);
        injectionStatusJson.put("isRunning", injectionStatus.getIsRunning().get());
        injectionStatusJson.put("isCanceled", injectionStatus.getIsCanceled().get());
        injectionStatusJson.put("deviceGroupName", injectionStatus.getDeviceGroupName());
        injectionStatusJson.put("attribute",
            objectFormattingService.formatObjectAsString(injectionStatus.getAttribute(), userContext));
        injectionStatusJson.put("numTotal", injectionStatus.getNumTotal());
        injectionStatusJson.put("numComplete", injectionStatus.getNumComplete().get());
        Instant lastInjection = injectionStatus.getLastFinishedInjection();

        if (injectionStatus.getIsRunning().get()) {
            injectionStatusJson.put("status", "running");
        } else if (lastInjection != null) {
            String lastInjectionStr = dateFormattingService.format(lastInjection, DateFormatEnum.DATEHM, userContext);
            injectionStatusJson.put("status", "notRunning");
            injectionStatusJson.put("lastInjection", lastInjectionStr);
        } else {
            injectionStatusJson.put("status", "neverRan");
        }

        String errorMessage = injectionStatus.getErrorMessage();
        if (!StringUtils.isBlank(errorMessage)) {
            injectionStatusJson.put("hasError", true);
            injectionStatusJson.put("errorMessage", "Error Occurred: " + errorMessage);
        }

        return injectionStatusJson;
    }

    @RequestMapping("stop")
    @ResponseBody
    public String stop() {
        injectionStatus.getIsCanceled().set(true);
        return "success";
    }

    @RequestMapping("run")
    @ResponseBody
    public void run(final BulkPointInjectionParameters inject) {
        final AtomicBoolean isRunning = injectionStatus.getIsRunning();
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }
        final AtomicBoolean isCanceled = injectionStatus.getIsCanceled();
        isCanceled.set(false);
        injectionStatus.getNumComplete().set(0);
        injectionStatus.setErrorMessage(null);
        Thread pointInjectionThread = new Thread() {
            @Override
            public void run() {
                try {
                    String deviceGroupName = inject.getDeviceGroupName();
                    Attribute attribute = inject.getAttribute();
                    double valueHigh = inject.getValueHigh();
                    double valueLow = inject.getValueLow();
                    int throttlePerSecond = Math.max(inject.getThrottlePerSecond(), 1);
                    Duration standardDuration = inject.getPeriod().toStandardDuration();
                    Instant stop = inject.getStop();
                    Instant start = inject.getStart();

                    injectionStatus.setAttribute(attribute);
                    injectionStatus.setDeviceGroupName(deviceGroupName);
                    DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(deviceGroupName);
                    List<SimpleDevice> supportedDevices = attributeService.getDevicesInGroupThatSupportAttribute(deviceGroup, attribute);
                    TimeRangeSplitter timeSplitter = new TimeRangeSplitter(start, stop, standardDuration);
                    RateLimiter throttle = RateLimiter.create(throttlePerSecond);
                    injectionStatus.setNumTotal((long) timeSplitter.getTotal() * supportedDevices.size());
                    int numberOfTimestamps = Math.min(2500, throttlePerSecond);
                    List<Instant> timestamps;
                    for (SimpleDevice device : supportedDevices) {
                        timeSplitter.reset();
                        while ((timestamps = timeSplitter.getNext(numberOfTimestamps)).size() != 0) {
                            throttle.acquire(timestamps.size());
                            if (isCanceled.get()) {
                                log.info("Point Injection worker is quiting because the task was canceled.");
                                isRunning.set(false);
                                return;
                            }
                            List<PointData> pointData =
                                    bulkPointDataInjectionService.createPointData(attribute, valueHigh, valueLow, device,
                                    timestamps);
                            AtomicLong numComplete = injectionStatus.getNumComplete();
                            long numTotal = injectionStatus.getNumTotal();
                            asyncDynamicDataSource.putValues(pointData);
                            numComplete.addAndGet(pointData.size());
                            log.info("Injected " + pointData.size() + " points. Progress: " + numComplete.get() + "/"
                                + numTotal);
                        }
                    }
                } catch (Exception e) {
                    log.error("Point Injection has encountered an unexpected error.", e);
                    injectionStatus.setErrorMessage(e.getMessage());
                } finally {
                    log.info("Point Injection worker has finished.");
                    injectionStatus.setLastFinishedInjection(Instant.now());
                    isRunning.set(false);
                }
            }
        };
        pointInjectionThread.setName("BulkPointInjectionTaskRunner");
        pointInjectionThread.setDaemon(true);
        executor.execute(pointInjectionThread);
    }

    @RequestMapping("stopRphSimulator")
    @ResponseBody
    public String stopRphSimulator() {
        rphSimulatorPointStatus.getIsCanceled().set(true);
        return "success";
    }
    
    
    @RequestMapping("start")
    @ResponseBody
    public void start(final RphSimulatorParameters rphSimulatorParameters) {
        log.info("Start inserting points at : " + new Date());
        final AtomicBoolean isRunning = rphSimulatorPointStatus.getIsRunning();
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }
        final AtomicBoolean isCanceled = rphSimulatorPointStatus.getIsCanceled();
        isCanceled.set(false);
        
        final AtomicBoolean isCompleted = rphSimulatorPointStatus.getIsCompleted();
        isCompleted.set(false);
        rphSimulatorPointStatus.setErrorMessage(null);
        Thread pointInjectionThread = new Thread() {
            @Override
            public void run() {
                try {
                    String deviceGroupName = rphSimulatorParameters.getDeviceGroupName();
                    double valueHigh = rphSimulatorParameters.getValueHigh();
                    double valueLow = rphSimulatorParameters.getValueLow();

                    Duration standardDuration = rphSimulatorParameters.getPeriod().toStandardDuration();
                    Instant stop = rphSimulatorParameters.getStop();
                    Instant start = rphSimulatorParameters.getStart();
                    DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(deviceGroupName);
                    List<Integer> devicesIdList =
                        new ArrayList<Integer>(deviceGroupService.getDeviceIds(Collections.singletonList(deviceGroup)));
                    for (List<Integer> devicesId : Lists.partition(devicesIdList, 1000)) {
                        
                        if (isCanceled.get()) {
                            log.info("High Speed Point Injection worker is quiting because the task was canceled.");
                            isRunning.set(false);
                            isCompleted.set(true);
                            return;
                        }
                        
                        bulkPointDataInjectionService.insertPointData(devicesId, rphSimulatorParameters.getType(),
                            valueLow, valueHigh, start, stop, standardDuration);
                    }
                    isCompleted.set(true);
                } catch (Exception e) {
                    log.error("Point Injection has encountered an unexpected error.", e);
                    rphSimulatorPointStatus.setErrorMessage(e.getMessage());
                } finally {
                    log.info("Point Injection worker has finished at " + new Date());
                    isRunning.set(false);
                }

            }

        };
        pointInjectionThread.setName("HighSpeedBulkPointInjectionTaskRunner");
        pointInjectionThread.setDaemon(true);
        executor.execute(pointInjectionThread);
    }

    @RequestMapping("rph-simulator-injection-status")
    @ResponseBody
    public Map<String, Object> rphSimulatorInjectionStatus(YukonUserContext userContext) {
        Map<String, Object> rphInjectionStatusJson = Maps.newHashMapWithExpectedSize(10);
        rphInjectionStatusJson.put("isCompleted", rphSimulatorPointStatus.getIsCompleted());
        rphInjectionStatusJson.put("isCanceled", rphSimulatorPointStatus.getIsCanceled().get());
        if (rphSimulatorPointStatus.getIsRunning().get()) {
            rphInjectionStatusJson.put("status", "running");
        } else {
            rphInjectionStatusJson.put("status", "neverRan");
        }

        String errorMessage = rphSimulatorPointStatus.getErrorMessage();
        if (!StringUtils.isBlank(errorMessage)) {
            rphInjectionStatusJson.put("hasError", true);
            rphInjectionStatusJson.put("errorMessage", "Error Occurred: " + errorMessage);
        }

        return rphInjectionStatusJson;
    }
    
    static class TimeRangeSplitter {
        private final Instant start;
        private final Instant stop;
        private final Duration duration;
        private final int total;
        private MutableDateTime startMutable;

        public TimeRangeSplitter(Instant start, Instant stop, Duration duration) {
            this.total = (int) ((stop.getMillis() - start.getMillis()) / duration.getMillis());
            this.startMutable = new MutableDateTime(start);
            this.start = start;
            this.stop = stop;
            this.duration = duration;
        }

        public int getTotal() {
            return total;
        }

        public void reset() {
            startMutable = new MutableDateTime(start);
        }

        public List<Instant> getNext(int numberItems) {
            List<Instant> instants = new ArrayList<>();
            int numberReturned = 0;
            while (numberReturned < numberItems && startMutable.isBefore(stop)) {
                numberReturned++;
                instants.add(startMutable.toInstant());
                startMutable.add(duration);
            }
            return instants;
        }
    }

    @RequestMapping("main")
    public void main(ModelMap model, YukonUserContext userContext) {
        setupModelMap(model, userContext);
    }

    @RequestMapping("sendBulkData")
    public String sendBulkData(@ModelAttribute("bulkInjection") BulkFakePointInjectionDto bulkInjection,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope, ModelMap model) {
        bulkValidator.validate(bulkInjection, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            this.bulkInjection = bulkInjection;
            setupModelMap(model, userContext);
            return "bulkPointInjection/main.jsp";
        }

        bulkPointDataInjectionService.excecuteInjectionByDevice(bulkInjection);

        this.bulkInjection = bulkInjection;
        flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Injection of "
            + bulkInjection.getInjectionCount() + " points successful."));
        return "redirect:main";
    }

    private void setupModelMap(ModelMap model, YukonUserContext userContext) {
        if (bulkInjection == null) {
            bulkInjection = new BulkFakePointInjectionDto();
        }
        model.addAttribute("bulkInjection", bulkInjection);

        // attributes
        Map<AttributeGroup, List<Attribute>> groupedAttributes = attributeService.getAllGroupedAttributes(userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);
        model.addAttribute("qualities", PointQuality.values());
    }

    @InitBinder
    public void setupBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        webDataBinder.registerCustomEditor(Attribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                Attribute attribute = attributeService.resolveAttributeName(attr);
                setValue(attribute);
            }
        });
        webDataBinder.registerCustomEditor(Period.class, new PeriodType().getPropertyEditor());
        PropertyEditor instantEditor =
            datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, userContext, BlankMode.ERROR);
        webDataBinder.registerCustomEditor(Instant.class, instantEditor);
        
        webDataBinder.registerCustomEditor(RphSimulatorPointType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String rphSimulatorPointType) throws IllegalArgumentException {
                if (rphSimulatorPointType.isEmpty()) {
                    setValue(null);
                    return;
                }
                setValue(RphSimulatorPointType.valueOf(rphSimulatorPointType.toUpperCase()));
            }
        });
    }
}
