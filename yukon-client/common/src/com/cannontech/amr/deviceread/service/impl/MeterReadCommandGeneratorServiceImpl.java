package com.cannontech.amr.deviceread.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.IterableUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterReadCommandGeneratorServiceImpl implements MeterReadCommandGeneratorService {
    private Logger log = YukonLogManager.getLogger(MeterReadCommandGeneratorServiceImpl.class);

    @Autowired private PaoDefinitionDao paoDefinitionDao;
	
	@Override
    public List<CommandRequestDevice> getCommandRequests(final Iterable<PaoMultiPointIdentifier> pointsToRead) {

	    List<CommandRequestDevice> result = Lists.newArrayListWithExpectedSize(IterableUtils.guessSize(pointsToRead));
	    
	    Map<String, CommandCallback> callbackCache = Maps.newHashMap();
	    
	    for (PaoMultiPointIdentifier pao : pointsToRead) {
	        Set<CommandWrapper> minimalCommands = getMinimalCommandSet(pao.getPao(), pao.getPointIdentifiers());
	        if(minimalCommands != null){
				for (CommandWrapper wrapper : minimalCommands) {
					List<String> commandStringList = wrapper.getCommandDefinition().getCommandStringList();
					for (String commandStr : commandStringList) {

						CommandCallback commandCallback = callbackCache.get(commandStr);
						if (commandCallback == null) {
							commandCallback = new PorterCommandCallback(commandStr);
							callbackCache.put(commandStr, commandCallback);
						}

						CommandRequestDevice request = new CommandRequestDevice();
						request.setCommandCallback(commandCallback);
						request.setDevice(new SimpleDevice(pao.getPao()));
						result.add(request);
					}
				}
	        }
	    }

	    return result;
	}
	
	@Override
    public boolean isReadable(final Iterable<PaoMultiPointIdentifier> pointsToRead) {
	    if (Iterables.isEmpty(pointsToRead)) return false;
	    
        // the following loop mimics what getCommandRequests does, but because we don't need
	    // to actually build commands, it is much shorter

        for (PaoMultiPointIdentifier pao : pointsToRead) {
            Set<CommandWrapper> minimalCommands = getMinimalCommandSet(pao.getPao(), pao.getPointIdentifiers());
            if (IterableUtils.isNotEmpty(minimalCommands)) return true;
        }
        
        return false;
	}
	
	private Set<CommandWrapper> getMinimalCommandSet(PaoIdentifier device, Set<PointIdentifier> pointSet) {
        Set<CommandDefinition> allPossibleCommands = paoDefinitionDao.getCommandsThatAffectPoints(device.getPaoType(), pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        LogHelper.debug(log, "Reduced %s on %s to: %s", pointSet, device, minimalCommands);
        return minimalCommands;
    }
}
