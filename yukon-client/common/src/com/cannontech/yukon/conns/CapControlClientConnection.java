package com.cannontech.yukon.conns;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * CBC.  Specifically it registers CBC specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import java.util.HashSet;

import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableArea;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableAreas;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableCapBankDevice;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableCapControlCommand;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableCapControlMessage;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableCapControlServerResponse;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableChangeOpState;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableDeleteItem;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableDynamicCommand;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableFeeder;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableItemCommand;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSpecialArea;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSpecialAreas;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableState;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableStateGroupMessage;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSubBus;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSubStation;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSubStations;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSubstationBuses;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableSystemStatus;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableTempMoveCapBank;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableVerifyBanks;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableVerifyInactiveBanks;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableVoltageRegulator;
import com.cannontech.message.capcontrol.defineCollectable.DefineCollectableVoltageRegulatorMessage;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.dispatch.message.DefineCollectablePointData;
import com.cannontech.message.util.ClientConnection;
import com.google.common.collect.Sets;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class CapControlClientConnection extends ClientConnection {
    
	private HashSet<MessageEventListener> messageEventListeners = Sets.newHashSet();

	// this MUST conatin all the DefineCollectibles of the CBC client
	private static DefineCollectable[] mappings = {
		//Data Structures
		new DefineCollectableCapBankDevice(),
		new DefineCollectableSubBus(),
		new DefineCollectableFeeder(),
		new DefineCollectableState(),
		new DefineCollectableArea(),
        new DefineCollectableSpecialAreas(),
        new DefineCollectableSpecialArea(),
        new DefineCollectableVoltageRegulator(),
		
        //Collectable Mappings
		CollectableMappings.OrderedVector,
		CollectableMappings.CollectableString,
		
		//Messages
		new DefineCollectableStateGroupMessage(),
		new DefineCollectableMulti(),
		new DefineCollectableCapControlCommand(),
		new DefineCollectableItemCommand(),
		new DefineCollectableAreas(),
		new DefineCollectableSubstationBuses(),
		new DefineCollectableCapControlMessage(), // not used except as a superclass
		new DefineCollectablePointData(),
		new DefineCollectableTempMoveCapBank(),
		new DefineCollectableVerifyBanks(),
		new DefineCollectableVerifyInactiveBanks(),
		new DefineCollectableSubStation(),
		new DefineCollectableSubStations(),
		new DefineCollectableCapControlServerResponse(),
		new DefineCollectableVoltageRegulatorMessage(),
		new DefineCollectableDynamicCommand(),
		new DefineCollectableChangeOpState(),
		new DefineCollectableSystemStatus(),
		new DefineCollectableDeleteItem()
	};
	
	public CapControlClientConnection() {
		super("CBC");
        setRegistrationMsg(new CapControlCommand(CommandType.REQUEST_ALL_DATA.getCommandId()));
	}
	
	public void addMessageEventListener(MessageEventListener listener) {
		synchronized (messageEventListeners) {
		    messageEventListeners.add(listener);
		}
	}
	
	public void fireMessageEvent(MessageEvent event) {	
		synchronized(messageEventListeners) {
			for (MessageEventListener listener : messageEventListeners) {
				listener.messageEvent(event);
			}
		}
	}
	
	protected void registerMappings(CollectableStreamer streamer) {
		super.registerMappings(streamer);
		
		for(DefineCollectable mapping : mappings) {
			streamer.register(mapping);
		}
	}
	
	public void sendCommand(CapControlCommand command) {
		if (!isValid()) {
			return;
		}

		synchronized (this) {
			write( command );
		}

		MessageEvent messageEvent = new MessageEvent( this, CommandType.getForId(command.getCommandId()).name() + " was executed.");
		messageEvent.setMessageType(MessageEvent.INFORMATION_MESSAGE);
		fireMessageEvent(messageEvent);
	}
	
}