package com.cannontech.web.support.development;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Set;

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

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.BulkFakePointInjectionDto;
import com.cannontech.development.BulkPointDataInjectionService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.type.PeriodType;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;

@Controller
@RequestMapping("/development/bulkPointInjection/*")
@CheckDevelopmentMode
public class BulkPointDataInjectionController {
    @Autowired private AttributeService attributeService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private BulkPointDataInjectionService bulkPointDataInjectionSerivce;

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

        bulkPointDataInjectionSerivce.excecuteInjection(bulkInjection);

        this.bulkInjection = bulkInjection;
        flashScope.setConfirm(YukonMessageSourceResolvable
            .createDefaultWithoutCode("Injection of " + bulkInjection.getInjectionCount()
                                                        + " points successful."));
        return "redirect:main";
    }
    
    private void setupModelMap(ModelMap model) {
        if (bulkInjection == null) {
            bulkInjection = new BulkFakePointInjectionDto();
        }
        model.addAttribute("bulkInjection", bulkInjection);

        // attributes
        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);
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
}
