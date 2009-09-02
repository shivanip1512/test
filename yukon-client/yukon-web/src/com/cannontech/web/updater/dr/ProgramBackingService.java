package com.cannontech.web.updater.dr;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.program.model.ProgramDisplayField;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class ProgramBackingService implements UpdateBackingService {
    private ProgramService programService = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;

    @Override
    public String getLatestValue(String identifier, long afterDateLong,
            YukonUserContext userContext) {
        String[] idBits = identifier.split("\\/");
        int programId = Integer.parseInt(idBits[0]);
        ProgramDisplayField displayField =
            ProgramDisplayField.valueOf(idBits[1]);

        DatedObject<LMProgramBase> datedProgram =
            programService.findDatedProgram(programId);

        MessageSource messageSource = messageSourceResolver.getMessageSource(userContext);
        if (datedProgram == null) {
            if (displayField.isCssClass()) {
                return "not_in_control_area";
            }
            return messageSource.getMessage("yukon.web.modules.dr.fieldNotInLoadManagement",
                                            null,
                                            userContext.getLocale());
        }

        Date afterDate = new Date(afterDateLong);
        if (!datedProgram.getDate().before(afterDate)) {
            return messageSource.getMessage(displayField.getValue(datedProgram.getObject()),
                                            userContext.getLocale());
        }

        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String identifier,
            long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
