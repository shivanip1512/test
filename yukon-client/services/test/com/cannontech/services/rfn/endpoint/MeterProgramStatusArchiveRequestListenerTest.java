package com.cannontech.services.rfn.endpoint;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.model.ProgrammingStatus;

public class MeterProgramStatusArchiveRequestListenerTest {

    MeterProgramStatusArchiveRequestListener l;
    
    @Parameter(0)
    public String guid;
    @Parameter(1)
    public ProgrammingStatus status;
    @Parameter(2)
    public DeviceError error;
    
    private static final String remoteGuid = "Nd952fa40-0ace-11ea-8d71-362b9e155667";
    private static final String yukonGuid = "REE8358B0-92B7-4603-A148-A06E5489D4C7";
    
    @Parameters(name="{0} {1} {2}")
    public static Collection<Object[]> input() {
        Comparator<Enum<?>> sortByName = (a, b) -> a.name().compareTo(b.name());
        //  Get all combinations of guid, status, and error
        return Stream.of(remoteGuid, yukonGuid)
                .flatMap(guid -> 
                    Arrays.stream(ProgrammingStatus.values())
                          .sorted(sortByName)
                          .flatMap(status -> 
                              Stream.of(DeviceError.SUCCESS, DeviceError.FILE_EXPIRED)
                                    .sorted(sortByName)
                                    .map(error -> new Object[] { guid, status, error })))
                     .collect(Collectors.toList());
    }

    @Test
    void testNothing() {
        
    }
    
    MeterProgramStatusArchiveRequest m;
    
}
