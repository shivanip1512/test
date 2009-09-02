package com.cannontech.dr.program.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ProgramServiceImpl implements ProgramService {
    private ProgramDao programDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;

    @Override
    public ObjectMapper<DisplayablePao, LMProgramBase> getMapper() {
        return this;
    }

    @Override
    public LMProgramBase map(DisplayablePao from) throws ObjectMappingException {
        DatedObject<LMProgramBase> datedProgram =
            loadControlClientConnection.getDatedProgram(from.getPaoIdentifier().getPaoId());
        return datedProgram == null ? null : datedProgram.getObject();
    }

    @Override
    public DatedObject<LMProgramBase> findDatedProgram(int programId) {
        return loadControlClientConnection.getDatedProgram(programId);
    }

    @Override
    public DisplayablePao getProgram(int programId) {
        return programDao.getProgram(programId);
    }

    @Autowired
    public void ProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
}
