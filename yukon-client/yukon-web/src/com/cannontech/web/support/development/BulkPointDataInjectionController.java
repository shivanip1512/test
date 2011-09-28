package com.cannontech.web.support.development;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.type.PeriodType;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.support.development.database.objects.BulkFakePointInjectionDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/development/bulkPointInjection/*")
@CheckDevelopmentMode
public class BulkPointDataInjectionController {
    private static final Logger log = YukonLogManager.getLogger(BulkPointDataInjectionController.class);
    private DynamicDataSource dynamicDataSource;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private AttributeService attributeService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private PaoDao paoDao;
    private BulkFakePointInjectionDto bulkInjection;
    private final static String baseKey = "yukon.web.modules.support.bulkPointInjection";

    private Validator bulkValidator =
        new SimpleValidator<BulkFakePointInjectionDto>(BulkFakePointInjectionDto.class) {
            @Override
            public void doValidation(BulkFakePointInjectionDto bulkInjection, Errors errors) {
                if (bulkInjection.getPeriod().toStandardDuration().equals(Duration.ZERO)) {
                    // Will run forever. Not good.
                    errors.rejectValue("period", baseKey + ".noPeriod");
                }
                if (bulkInjection.getPointQualities() == null
                    || bulkInjection.getPointQualities().isEmpty()) {
                    errors.reject(baseKey + ".noQuality");
                }
            }
        };

    @RequestMapping("main")
    public void main(ModelMap model) {
        setupModelMap(model);
    }

    @RequestMapping
    public String sendBulkData(@ModelAttribute("bulkInjection") BulkFakePointInjectionDto bulkInjection,
                               BindingResult bindingResult, YukonUserContext userContext,
                               FlashScope flashScope, ModelMap model) {
        bulkValidator.validate(bulkInjection, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            this.bulkInjection = bulkInjection;
            setupModelMap(model);
            return "development/bulkPointInjection/main.jsp";
        }
        List<LitePoint> litePoints = getLitePointListOfDevicesInGroupWithAttribute(bulkInjection);
        int injectionCount = 0;
        Map<Integer, Double> valueMap = Maps.newHashMap();
        Random rand = new Random();
        Instant iterableInstant = new Instant(bulkInjection.getStart());
        while (iterableInstant.isBefore(bulkInjection.getStop())) {
            for (LitePoint litePoint : litePoints) {
                PointData pointData = new PointData();
                pointData.setId(litePoint.getPointID());
                long randWindow = (long) getRandomWithinRange(0, bulkInjection.getPeriodWindow()
                        .toStandardDuration().getMillis());
                pointData.setTime(iterableInstant.plus(randWindow)
                    .toDateTime(userContext.getJodaTimeZone()).toDate());
                Collections.shuffle(bulkInjection.getPointQualities());
                pointData.setPointQuality(bulkInjection.getPointQualities().get(0));
                double value = 0.0;
                if (bulkInjection.getAlgorithm().equalsIgnoreCase("normal")) {
                    do {
                        double nextGaussian = rand.nextGaussian();
                        double standDeviation =
                            (bulkInjection.getValueHigh() - bulkInjection.getValueLow()) / 6; // +-3 standard deviations
                        value = nextGaussian * standDeviation + bulkInjection.getMean();
                    } while (value < bulkInjection.getValueLow()
                             || value > bulkInjection.getValueHigh());
                    if (bulkInjection.isIncremental()) {
                        Double mappedValue = valueMap.get(litePoint.getPointID());
                        if (mappedValue != null) {
                            value = mappedValue + value;
                        }
                        valueMap.put(litePoint.getPointID(), value);
                    }
                } else {
                    value = getRandomWithinRange(bulkInjection.getValueLow(), bulkInjection.getValueHigh());
                }
                
                double roundedVal = getRoundedValue(value, bulkInjection.getDecimalPlaces());
                pointData.setValue(roundedVal);
                pointData.setTagsPointMustArchive(bulkInjection.isArchive());
                pointData.setType(litePoint.getPointTypeEnum().getPointTypeId());
                dynamicDataSource.putValue(pointData);
                log.info("point data sent from bulk injector: " + pointData);
                injectionCount++;
            }
            Duration duration = bulkInjection.getPeriod().toStandardDuration();
            iterableInstant = iterableInstant.plus(duration);
        }
        this.bulkInjection = bulkInjection;
        flashScope.setConfirm(YukonMessageSourceResolvable
            .createDefaultWithoutCode("Injection of " + injectionCount
                                                        + " points successful."));
        return "redirect:main";
    }
    
    private double getRoundedValue(double value, int decimalPlaces) {
        double scaler = Math.pow(10, decimalPlaces);
        double roundedValue = (double)Math.round(value * scaler) / scaler;
        return roundedValue;
    }

    private List<LitePoint> getLitePointListOfDevicesInGroupWithAttribute(BulkFakePointInjectionDto bulkInjection) {
        List<LitePoint> litePoints = Lists.newArrayList();
        DeviceGroup group = deviceGroupService.resolveGroupName(bulkInjection.getGroupName());
        List<SimpleDevice> supportedDevices =
            attributeService.getDevicesInGroupThatSupportAttribute(group,
                                                                   bulkInjection.getAttribute());
        for (SimpleDevice simpleDevice : supportedDevices) {
            YukonPao yukonPao = paoDao.getYukonPao(simpleDevice.getDeviceId());
            LitePoint point = attributeService.getPointForAttribute(yukonPao, bulkInjection.getAttribute());
            litePoints.add(point);
        }
        return litePoints;
    }

    private double getRandomWithinRange(double min, double max) {
        return (min + (int) (Math.random() * ((max - min) + 1)));
    }

    private void setupModelMap(ModelMap model) {
        if (bulkInjection == null) {
            bulkInjection = new BulkFakePointInjectionDto();
        }
        model.addAttribute("bulkInjection", bulkInjection);

        // attributes
        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);

        DeviceGroup group = deviceGroupService.resolveGroupName(SystemGroupEnum.ROOT.getFullPath());
        DeviceCollection deviceCollection =
            deviceGroupCollectionHelper.buildDeviceCollection(group);
        model.addAttribute("deviceCollection", deviceCollection);
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
            datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM,
                                                               userContext,
                                                               BlankMode.ERROR);
        webDataBinder.registerCustomEditor(Instant.class, instantEditor);
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
