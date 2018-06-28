package com.cannontech.web.widget;

import static com.cannontech.common.pao.notes.service.PaoNotesService.MAX_CHARACTERS_IN_NOTE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.paonote.validator.PaoNoteValidator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

@Controller
@RequestMapping("/paoNotesWidget/*")
public class PaoNotesWidget extends AdvancedWidgetControllerBase {

    @Autowired private PaoNotesService paoNotesService;
    @Autowired private PaoNoteValidator paoNoteValidator;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final String baseKey = "yukon.web.widgets.paoNotesWidget";

    @Autowired
    public PaoNotesWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping(value = "render", method = RequestMethod.GET)
    public String render(ModelMap model, int deviceId, YukonUserContext userContext) {
        setupModel(deviceId, userContext.getYukonUser().getUsername(), model);
        return "paoNotesWidget/render.jsp";
    }

    @RequestMapping(value = "createPaoNote", method = RequestMethod.POST)
    public String createNote(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("createPaoNote") PaoNote paoNote, BindingResult result) {

        paoNoteValidator.validate(paoNote, result);

        if (result.hasErrors()) {
            setupModel(paoNote.getPaoId(), userContext.getYukonUser().getUsername(), model);
            return "paoNotesWidget/render.jsp";
        }
        paoNotesService.create(paoNote, userContext.getYukonUser());
        return "redirect:render?deviceId=" + paoNote.getPaoId();
    }

    @RequestMapping(value = "deletePaoNote", method = RequestMethod.POST)
    public String deletePaoNote(ModelMap model, int noteId, int deviceId, YukonUserContext userContext,
            FlashScope flash) {
        paoNotesService.delete(noteId, userContext.getYukonUser());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.successful"));
        setupModel(deviceId, userContext.getYukonUser().getUsername(), model);
        return "paoNotesWidget/render.jsp";
    }

    @RequestMapping(value = "editPaoNote", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> editPaoNote(ModelMap model, YukonUserContext userContext, PaoNote paoNote) {
        Map<String, Object> jsonResponse = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        if (StringUtils.isBlank(paoNote.getNoteText())) {
            jsonResponse.put("hasError", true);
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.isBlank"));
        } else if (paoNote.getNoteText().length() > MAX_CHARACTERS_IN_NOTE) {
            jsonResponse.put("hasError", true);
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.exceedsMaximumLength", 255));
        } else {
            jsonResponse.put("hasError", false);
            paoNotesService.edit(paoNote, userContext.getYukonUser());
        }

        return jsonResponse;
    }

    private void setupModel(int deviceId, String userName, ModelMap model) {
        PaoNote createPaoNote = null;
        if (model.containsAttribute("createPaoNote")) {
            createPaoNote = (PaoNote) model.get("createPaoNote");
        } else {
            createPaoNote = new PaoNote();
            createPaoNote.setCreateUserName(userName);
            createPaoNote.setPaoId(deviceId);
        }
        model.addAttribute("createPaoNote", createPaoNote);

        List<PaoNotesSearchResult> recentNotes = null;
        if (model.containsAttribute("recentNotes")) {
            recentNotes = (List<PaoNotesSearchResult>) model.get("recentNotes");
        } else {
            recentNotes = paoNotesService.findMostRecentNotes(deviceId, 3);
        }
        model.addAttribute("recentNotes", recentNotes);
    }

}
