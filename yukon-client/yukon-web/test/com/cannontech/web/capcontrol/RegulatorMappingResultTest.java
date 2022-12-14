package com.cannontech.web.capcontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.model.RegulatorPointMappingResult;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResultType;

public class RegulatorMappingResultTest {
    private RegulatorMappingResult successResult;
    private RegulatorMappingResult partialSuccessResult;
    private RegulatorMappingResult failResult;
    
    @BeforeEach
    public void init() {
        
        YukonPao regulatorPao = new YukonPao() {
            PaoIdentifier paoId = new PaoIdentifier(1, PaoType.GANG_OPERATED);
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoId;
            }
        };
        
        successResult = new RegulatorMappingResult(regulatorPao);
        
        successResult.addPointDetail(RegulatorPointMapping.VOLTAGE, RegulatorPointMappingResult.SUCCESS);
        successResult.addPointDetail(RegulatorPointMapping.TAP_DOWN, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        
        YukonPao regulatorPao2 = new YukonPao() {
            PaoIdentifier paoId = new PaoIdentifier(1, PaoType.PHASE_OPERATED);
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoId;
            }
        };
        
        partialSuccessResult = new RegulatorMappingResult(regulatorPao2);
        
        partialSuccessResult.addPointDetail(RegulatorPointMapping.FORWARD_BANDWIDTH, RegulatorPointMappingResult.SUCCESS);
        partialSuccessResult.addPointDetail(RegulatorPointMapping.KEEP_ALIVE, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        partialSuccessResult.addPointDetail(RegulatorPointMapping.VOLTAGE, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        partialSuccessResult.addPointDetail(RegulatorPointMapping.TAP_UP, RegulatorPointMappingResult.NO_POINTS_FOUND);
        
        YukonPao regulatorPao3 = new YukonPao() {
            PaoIdentifier paoId = new PaoIdentifier(1, PaoType.LOAD_TAP_CHANGER);
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoId;
            }
        };
        
        failResult = new RegulatorMappingResult(regulatorPao3);
        
        failResult.addPointDetail(RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        failResult.addPointDetail(RegulatorPointMapping.FORWARD_BANDWIDTH, RegulatorPointMappingResult.NO_POINTS_FOUND);
    }
    
    @Test
    public void test_completion() {
        assertFalse(successResult.isComplete(), "Result is complete before complete() method is called.");
        successResult.complete();
        assertTrue(successResult.isComplete(), "Result is incomplete after complete() method is called. ");
    }
    
    @Test
    public void test_deviceResults() {
        assertEquals(RegulatorMappingResultType.INCOMPLETE, 
                            partialSuccessResult.getType(),
                            "Result should be INCOMPLETE before complete() method call.");
        
        successResult.complete();
        partialSuccessResult.complete();
        failResult.complete();
        
        assertEquals(RegulatorMappingResultType.SUCCESSFUL, 
                            successResult.getType(),
                            "Incorrect result when all points are successful.");
        assertEquals(RegulatorMappingResultType.PARTIALLY_SUCCESSFUL, 
                            partialSuccessResult.getType(),
                            "Incorrect result when points are a mix of success and fail.");
        assertEquals(RegulatorMappingResultType.FAILED, 
                            failResult.getType(),
                            "Incorrect result when all points are failed.");
    }
    
    @Test
    public void test_pointResults() {
        Map<RegulatorPointMapping, RegulatorPointMappingResult> successResults = successResult.getPointMappingResults();
        Map<RegulatorPointMapping, RegulatorPointMappingResult> partialSuccessResults = partialSuccessResult.getPointMappingResults();
        Map<RegulatorPointMapping, RegulatorPointMappingResult> failResults = failResult.getPointMappingResults();
        
        RegulatorPointMappingResult successVolt = successResults.get(RegulatorPointMapping.VOLTAGE);
        RegulatorPointMappingResult successTapDown = successResults.get(RegulatorPointMapping.TAP_DOWN);
        assertEquals(RegulatorPointMappingResult.SUCCESS, successVolt, "Incorrect result type for point.");
        assertEquals(RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE, successTapDown, "Incorrect result type for point.");
        
        RegulatorPointMappingResult partSuccessFwdBand = partialSuccessResults.get(RegulatorPointMapping.FORWARD_BANDWIDTH);
        RegulatorPointMappingResult partSuccessKeepAlive = partialSuccessResults.get(RegulatorPointMapping.KEEP_ALIVE);
        RegulatorPointMappingResult partSuccessVolt = partialSuccessResults.get(RegulatorPointMapping.VOLTAGE);
        RegulatorPointMappingResult partSuccessTapUp = partialSuccessResults.get(RegulatorPointMapping.TAP_UP);
        assertEquals(RegulatorPointMappingResult.SUCCESS, partSuccessFwdBand, "Incorrect result type for point.");
        assertEquals(RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE, partSuccessKeepAlive, "Incorrect result type for point.");
        assertEquals(RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND, partSuccessVolt, "Incorrect result type for point.");
        assertEquals(RegulatorPointMappingResult.NO_POINTS_FOUND, partSuccessTapUp, "Incorrect result type for point.");
        
        RegulatorPointMappingResult failAutoRemoteControl = failResults.get(RegulatorPointMapping.AUTO_REMOTE_CONTROL);
        RegulatorPointMappingResult failFwdBand = failResults.get(RegulatorPointMapping.FORWARD_BANDWIDTH);
        assertEquals(RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND, failAutoRemoteControl, "Incorrect result type for point.");
        assertEquals(RegulatorPointMappingResult.NO_POINTS_FOUND, failFwdBand, "Incorrect result type for point.");
    }
}