package com.cannontech.amr.deviceread.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.amr.deviceread.dao.impl.UnreadableException;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MeterReadCommandGeneratorServiceImpl implements MeterReadCommandGeneratorService {

	private AttributeService attributeService;
	private DeviceDao deviceDao;
	private PaoDefinitionDao paoDefinitionDao;
	
	private Logger log = YukonLogManager.getLogger(MeterReadCommandGeneratorServiceImpl.class);
	
	private boolean isUpdate = true;
	
	
	
	public Multimap<YukonDevice, CommandRequestDevice> getCommandRequests(Iterable<? extends YukonDevice> devices, Set<? extends Attribute> attributes, DeviceRequestType type) {
	    
	    ListMultimap<YukonDevice, CommandRequestDevice> list = ArrayListMultimap.create();
	    
	    for (YukonDevice device : devices) {
	        
	        Multimap<PaoIdentifier, LitePoint> pointsToRead = getPointsToRead(device.getPaoIdentifier(), attributes);
	        
	        try {
	            
	            Multimap<PaoIdentifier, CommandWrapper> requiredCommands = getRequiredCommands(pointsToRead);
	            
	            for (PaoIdentifier paoIdentifier : requiredCommands.keySet()) {
	                list.putAll(device, getCommandRequests(paoIdentifier, requiredCommands.get(paoIdentifier), type));
	            }
	            
	        } catch (UnreadableException e) {
	            // device does not support an attribute, it will not be included in result Multimap
	            continue;
	        }
	    }
	    
	    return list;
	}
	
	public boolean isReadable(YukonDevice device, Set<? extends Attribute> attributes) {
	    if(attributes.isEmpty()) return false;
	    Multimap<PaoIdentifier, LitePoint> pointsToRead = getPointsToRead(device.getPaoIdentifier(), attributes);
	    
	    try {
            getRequiredCommands(pointsToRead);
        } catch (UnreadableException e) {
            return false;
        }
        
        return true;
	}
	
	private  Multimap<PaoIdentifier, LitePoint> getPointsToRead(PaoIdentifier device, Set<? extends Attribute> attributes) {
        
		// reduce number of commands
    	Multimap<PaoIdentifier, LitePoint> pointsToRead = HashMultimap.create();
    	for (Attribute attribute : attributes) {
    	    // consider wrapping in try/catch and returning false if this fails
    		LitePoint pointForAttribute = null;
    		try {
    			pointForAttribute = attributeService.getPointForAttribute(new SimpleDevice(device), attribute);
    			
    			PaoIdentifier deviceForPoint = null;
                if (pointForAttribute.getPaobjectID() == device.getPaoId()) {
                    // prevent DAO call for common case
                    deviceForPoint = device;
                } else {
                    deviceForPoint = deviceDao.getYukonDevice(pointForAttribute.getPaobjectID()).getPaoIdentifier();
                }
                pointsToRead.put(deviceForPoint, pointForAttribute);
                
    		} catch (IllegalArgumentException e) {
    			log.debug("Device does not have point for attribute, it will not be read. device = " + device + ", attribute=" + attribute, e);
    		} catch (IllegalUseOfAttribute e) {
    			log.debug("Device does not have point for attribute, it will not be read. device = " + device + ", attribute=" + attribute, e);
    		}
        }
        return pointsToRead;
    }
	
	private Multimap<PaoIdentifier, CommandWrapper> getRequiredCommands(Multimap<PaoIdentifier, LitePoint> pointsToRead) throws UnreadableException {
		
        Multimap<PaoIdentifier, CommandWrapper> requiredCommands = HashMultimap.create();
    	
    	for (PaoIdentifier deviceToRead : pointsToRead.keySet()) {
    	    Set<PointIdentifier> pointSet = convertToDevicePointIdentifiers(pointsToRead.get(deviceToRead));
    	    Set<CommandWrapper> minimalCommands = getMinimalCommandSet(deviceToRead, pointSet);
            if (minimalCommands == null) {
                throw new UnreadableException();
            }
            requiredCommands.putAll(deviceToRead, minimalCommands);
        }
        return requiredCommands;
    }
	
	private List<CommandRequestDevice> getCommandRequests(PaoIdentifier device, Iterable<CommandWrapper> commands, DeviceRequestType type) {
        List<CommandRequestDevice> commandRequests = new ArrayList<CommandRequestDevice>();
        for (CommandWrapper wrapper : commands) {
            List<String> commandStringList = wrapper.getCommandDefinition().getCommandStringList();
            for (String commandStr : commandStringList) {
                CommandRequestDevice request = new CommandRequestDevice();
                request.setDevice(new SimpleDevice(device));
                commandStr += (isUpdate ? " update " : "");
                commandStr += (!type.isScheduled() ? " noqueue " : "");

                request.setCommand(commandStr);
                commandRequests.add(request);
            }
        }
        return commandRequests;
    }
	
	private Set<CommandWrapper> getMinimalCommandSet(PaoIdentifier device, Set<PointIdentifier> pointSet) {
        Set<CommandDefinition> allPossibleCommands = paoDefinitionDao.getCommandsThatAffectPoints(device.getPaoType(), pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<CommandWrapper>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        return minimalCommands;
    }
	
	private Set<PointIdentifier> convertToDevicePointIdentifiers(Iterable<LitePoint> collection) {
        Set<PointIdentifier> result = Sets.newHashSet();
        for (LitePoint point : collection) {
            PointIdentifier pointIdentifier = new PointIdentifier(point.getPointType(), point.getPointOffset());
            result.add(pointIdentifier);
        }
        return result;
    }
	
	@Autowired
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	@Autowired
	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
	
	@Autowired
	public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
