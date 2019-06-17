package com.cannontech.dr.program.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for program backing fields 
 */
public abstract class ProgramBackingFieldBase implements DemandResponseBackingField<LMProgramBase> {

    private final static String baseKey = "yukon.web.modules.dr.program.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    protected final static MessageSourceResolvable blankValueResolvable = 
        new YukonMessageSourceResolvable("yukon.common.dashesTwo");
    
    private ProgramService programService;
    
    /**
     * Method to get the field value from the given program
     * @param program - Program to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given program (Should be one of: String, 
     *                                                  MessageSourceResolvable, ResolvableTemplate)
     */
    public abstract Object getProgramValue(LMProgramBase program, YukonUserContext userContext);
    
    @Override
    public Object getValue(LMProgramBase program, YukonUserContext userContext) {
        if(program != null || handlesNull()) {
            return getProgramValue((LMProgramBase) program, userContext);
        } else {
            return blankFieldResolvable;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }
    
    protected LMProgramBase getProgramFromYukonPao(YukonPao from){
        return programService.getProgramForPao(from);
    }
    
    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }
    
    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }
    
    protected boolean handlesNull() {
        return false;
    }
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
}
