package com.cannontech.amr.deviceread.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.IterableUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MeterReadCommandGeneratorServiceImpl implements MeterReadCommandGeneratorService {

	private PaoDefinitionDao paoDefinitionDao;
	
	private Logger log = YukonLogManager.getLogger(MeterReadCommandGeneratorServiceImpl.class);
	
	private boolean isUpdate = true;
	
	public List<CommandRequestDevice> getCommandRequests(final Iterable<PaoMultiPointIdentifier> pointsToRead, DeviceRequestType type) {

	    List<CommandRequestDevice> result = Lists.newArrayListWithExpectedSize(IterableUtils.guessSize(pointsToRead));
	    
	    for (PaoMultiPointIdentifier pao : pointsToRead) {
	        Set<CommandWrapper> minimalCommands = getMinimalCommandSet(pao.getPao(), pao.getPointIdentifiers());
	        for (CommandWrapper wrapper : minimalCommands) {
	            List<String> commandStringList = wrapper.getCommandDefinition().getCommandStringList();
	            for (String commandStr : commandStringList) {
	                commandStr += (isUpdate ? " update " : "");
	                commandStr += (!type.isScheduled() ? " noqueue " : "");

	                CommandRequestDevice request = new CommandRequestDevice();
	                request.setCommand(commandStr);
	                request.setDevice(new SimpleDevice(pao.getPao()));
	                result.add(request);
	            }
	        }
	    }

	    return result;
	}
	
	public boolean isReadable(final Iterable<PaoMultiPointIdentifier> pointsToRead) {
	    if (Iterables.isEmpty(pointsToRead)) return false;
	    
        // the following is meant to mimic the getCommandRequests, but to short-circuit

        for (PaoMultiPointIdentifier pao : pointsToRead) {
            Set<CommandWrapper> minimalCommands = getMinimalCommandSet(pao.getPao(), pao.getPointIdentifiers());
            if (IterableUtils.isNotEmpty(minimalCommands)) return true;
        }
        
        return false;

	}
	
	private Set<CommandWrapper> getMinimalCommandSet(PaoIdentifier device, Set<PointIdentifier> pointSet) {
        Set<CommandDefinition> allPossibleCommands = paoDefinitionDao.getCommandsThatAffectPoints(device.getPaoType(), pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<CommandWrapper>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        LogHelper.debug(log, "Reduced %s on %s to: %s", pointSet, device, minimalCommands);
        return minimalCommands;
    }
	
	@Autowired
	public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
