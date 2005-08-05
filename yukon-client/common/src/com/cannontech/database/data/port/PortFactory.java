package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.db.port.CommPort;
 
public final class PortFactory {
/**
 * This method was created in VisualAge.
 * @return DBPersistentData
 * @param typeOfPort int
 */
public static DirectPort createPort( int typeOfPort ) 
{
	DirectPort port;
	
	switch( typeOfPort )
	{
		case PortTypes.LOCAL_DIRECT:
			LocalDirectPort p = new LocalDirectPort();
			p.setPortType( PortTypes.STRING_LOCAL_DIRECT );
			port = p;
			break;
			
		case PortTypes.LOCAL_SHARED:
			LocalSharedPort lsp = new LocalSharedPort();			
			lsp.setPortType( PortTypes.STRING_LOCAL_SERIAL );
			lsp.getCommPort().setCommonProtocol("IDLC");
			lsp.getPortTiming().setPreTxWait(new Integer(25));
			lsp.getPortTiming().setRtsToTxWait(new Integer(0));
			lsp.getPortTiming().setPostTxWait(new Integer(0));
			lsp.getPortTiming().setReceiveDataWait(new Integer(0));
			lsp.getPortTiming().setExtraTimeOut(new Integer(0));
			
			port = lsp;
			break;

		case PortTypes.DIALOUT_POOL:
			PooledPort pp = new PooledPort();		
			pp.setPortType( PortTypes.STRING_DIALOUT_POOL );

			port = pp;
			break;
			
		case PortTypes.LOCAL_RADIO:
			LocalRadioPort lrp = new LocalRadioPort();

			lrp.setPortType( PortTypes.STRING_LOCAL_RADIO );
			
			lrp.getPortTiming().setPreTxWait(new Integer(25));
			lrp.getPortTiming().setRtsToTxWait(new Integer(0));
			lrp.getPortTiming().setPostTxWait(new Integer(0));
			lrp.getPortTiming().setReceiveDataWait(new Integer(0));
			lrp.getPortTiming().setExtraTimeOut(new Integer(0));

			lrp.getPortRadioSettings().setRtsToTxWaitSameD(new Integer(0));
			lrp.getPortRadioSettings().setRtsToTxWaitDiffD(new Integer(0));
			lrp.getPortRadioSettings().setRadioMasterTail(new Integer(0));
			lrp.getPortRadioSettings().setReverseRTS(new Integer(0));
		
			port = lrp;
			break;
			
		case PortTypes.LOCAL_DIALUP:
		case PortTypes.LOCAL_DIALBACK:
			LocalDialupPort ldp = new LocalDialupPort();

			if( typeOfPort ==  PortTypes.LOCAL_DIALUP )
				ldp.setPortType( PortTypes.STRING_LOCAL_DIALUP );
			else if( typeOfPort == PortTypes.LOCAL_DIALBACK )
				ldp.setPortType( PortTypes.STRING_LOCAL_DIALBACK );
			
			ldp.getPortTiming().setPreTxWait(new Integer(25));
			ldp.getPortTiming().setRtsToTxWait(new Integer(0));
			ldp.getPortTiming().setPostTxWait(new Integer(0));
			ldp.getPortTiming().setReceiveDataWait(new Integer(0));
			ldp.getPortTiming().setExtraTimeOut(new Integer(0));
			
			port = ldp;
			break;

		case PortTypes.TSERVER_DIRECT:
		    TerminalServerDirectPort tdp = new TerminalServerDirectPort();
		    tdp.setPortType( PortTypes.STRING_TERM_SERVER_DIRECT );
		    
			port = tdp;
			break;
			
		case PortTypes.TSERVER_SHARED:
			TerminalServerSharedPort tsp = new TerminalServerSharedPort();
			tsp.setPortType( PortTypes.STRING_TERM_SERVER );
			tsp.getCommPort().setCommonProtocol("IDLC");			
			tsp.getPortTiming().setPreTxWait(new Integer(25));
			tsp.getPortTiming().setRtsToTxWait(new Integer(0));
			tsp.getPortTiming().setPostTxWait(new Integer(0));
			tsp.getPortTiming().setReceiveDataWait(new Integer(0));
			tsp.getPortTiming().setExtraTimeOut(new Integer(0));
			port = tsp;
			break;
			
		case PortTypes.TSERVER_RADIO:
			TerminalServerRadioPort trp = new TerminalServerRadioPort();

			trp.setPortType( PortTypes.STRING_TERM_SERVER_RADIO );

			trp.getPortTiming().setPreTxWait(new Integer(25));
			trp.getPortTiming().setRtsToTxWait(new Integer(0));
			trp.getPortTiming().setPostTxWait(new Integer(0));
			trp.getPortTiming().setReceiveDataWait(new Integer(0));
			trp.getPortTiming().setExtraTimeOut(new Integer(0));

			trp.getPortRadioSettings().setRtsToTxWaitSameD(new Integer(0));
			trp.getPortRadioSettings().setRtsToTxWaitDiffD(new Integer(0));
			trp.getPortRadioSettings().setRadioMasterTail(new Integer(0));
			trp.getPortRadioSettings().setReverseRTS(new Integer(0));
			
			port = trp;
			break;
			
		case PortTypes.TSERVER_DIALUP:
			TerminalServerDialupPort tp = new TerminalServerDialupPort();
			tp.setPortType( PortTypes.STRING_TERM_SERVER_DIALUP );

			tp.getPortTiming().setPreTxWait(new Integer(25));
			tp.getPortTiming().setRtsToTxWait(new Integer(0));
			tp.getPortTiming().setPostTxWait(new Integer(0));
			tp.getPortTiming().setReceiveDataWait(new Integer(0));
			tp.getPortTiming().setExtraTimeOut(new Integer(0));
			port = tp;
			break;
		default:
			return null;
	}

	//Grab a unique id
	port.setPortID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
	port.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_PORT );
	port.setPAOClass( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_PORT );
	
	port.setDisableFlag(new Character('N'));
	port.getCommPort().setAlarmInhibit(new Character('N'));

	if( port.getCommPort().getCommonProtocol() == null )
		port.getCommPort().setCommonProtocol("None");

	port.getCommPort().setPerformThreshold(new Integer(90) );
	port.getCommPort().setPerformanceAlarm(new Character('Y'));
	port.getCommPort().setSharedPortType( com.cannontech.common.util.CtiUtilities.STRING_NONE );
	port.getCommPort().setSharedSocketNumber( new Integer(CommPort.DEFAULT_SHARED_SOCKET_NUMBER) );

	return port;
}
}
