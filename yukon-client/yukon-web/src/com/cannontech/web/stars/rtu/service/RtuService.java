package com.cannontech.web.stars.rtu.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface RtuService {
    
    /**
     * The points on RTU-DNP and its Logical CBC devices are all on the same physical DNP RTU, and the Type and Offset should be unique across the hierarchy.
     * 
     * Generates duplicate points error message by device id.
     */
    List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId, HttpServletRequest request);

    /**
     * The points on RTU-DNP and its Logical CBC devices are all on the same physical DNP RTU, and the Type and Offset should be unique across the hierarchy.
     * 
     * Generates duplicate points error message by device id and point identifier.
     */
    List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId, PointIdentifier pointIdentifier, HttpServletRequest request);
    
    /**
     * This methods returns the RTUs of the type specified in the paoType list passed as a parameter. 
     */
    List<LiteYukonPAObject> getRtusByType(List<PaoType> rtuTypes);
    /**
     * This method delete RTU and returns status of deletion. 
     */
    boolean deleteRtu(int id);
    /**
     * This method return RTU ID once it create a new RTU object. 
     */
    Integer copyRtu(RtuDnp newRtu);
}
