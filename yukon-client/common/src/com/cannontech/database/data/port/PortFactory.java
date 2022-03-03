package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.spring.YukonSpringHook;

public final class PortFactory {

    @SuppressWarnings("deprecation")
    public static DirectPort createPort(PaoType portType) {
        DirectPort port;

        switch (portType) {
        case LOCAL_DIRECT:
            LocalDirectPort p = new LocalDirectPort();
            port = p;
            break;

        case LOCAL_SHARED:
            LocalSharedPort lsp = new LocalSharedPort();
            lsp.getCommPort().setCommonProtocol("IDLC");
            lsp.getPortTiming().setPreTxWait(new Integer(25));
            lsp.getPortTiming().setRtsToTxWait(new Integer(0));
            lsp.getPortTiming().setPostTxWait(new Integer(0));
            lsp.getPortTiming().setReceiveDataWait(new Integer(0));
            lsp.getPortTiming().setExtraTimeOut(new Integer(0));
            lsp.getPortTiming().setPostCommWait(new Integer(0));

            port = lsp;
            break;

        case DIALOUT_POOL:
            PooledPort pp = new PooledPort();
            port = pp;
            break;

        case LOCAL_RADIO:
            LocalRadioPort lrp = new LocalRadioPort();

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

        case LOCAL_DIALUP:
            LocalDialupPort dialupPort = new LocalDialupPort();

            dialupPort.getPortTiming().setPreTxWait(new Integer(25));
            dialupPort.getPortTiming().setRtsToTxWait(new Integer(0));
            dialupPort.getPortTiming().setPostTxWait(new Integer(0));
            dialupPort.getPortTiming().setReceiveDataWait(new Integer(0));
            dialupPort.getPortTiming().setExtraTimeOut(new Integer(0));

            port = dialupPort;
            break;
        case LOCAL_DIALBACK:
            LocalDialbackPort dialbackPort = new LocalDialbackPort();

            dialbackPort.getPortTiming().setPreTxWait(new Integer(25));
            dialbackPort.getPortTiming().setRtsToTxWait(new Integer(0));
            dialbackPort.getPortTiming().setPostTxWait(new Integer(0));
            dialbackPort.getPortTiming().setReceiveDataWait(new Integer(0));
            dialbackPort.getPortTiming().setExtraTimeOut(new Integer(0));

            port = dialbackPort;
            break;
        case TSERVER_DIRECT:
            TerminalServerDirectPort tdp = new TerminalServerDirectPort();
            port = tdp;
            break;

        case TSERVER_SHARED:
            TerminalServerSharedPort tsp = new TerminalServerSharedPort();
            tsp.getCommPort().setCommonProtocol("IDLC");
            tsp.getPortTiming().setPreTxWait(new Integer(25));
            tsp.getPortTiming().setRtsToTxWait(new Integer(0));
            tsp.getPortTiming().setPostTxWait(new Integer(0));
            tsp.getPortTiming().setReceiveDataWait(new Integer(0));
            tsp.getPortTiming().setExtraTimeOut(new Integer(0));
            tsp.getPortTiming().setPostCommWait(new Integer(0));
            port = tsp;
            break;
        case UDPPORT:
            UdpPort udpPort = new UdpPort();
            udpPort.getCommPort().setCommonProtocol("IDLC");
            udpPort.getPortTiming().setPreTxWait(new Integer(25));
            udpPort.getPortTiming().setRtsToTxWait(new Integer(0));
            udpPort.getPortTiming().setPostTxWait(new Integer(0));
            udpPort.getPortTiming().setReceiveDataWait(new Integer(0));
            udpPort.getPortTiming().setExtraTimeOut(new Integer(0));
            udpPort.getPortTiming().setPostCommWait(new Integer(0));
            port = udpPort;
            break;

        case TSERVER_RADIO:
            TerminalServerRadioPort trp = new TerminalServerRadioPort();

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

        case TSERVER_DIALUP:
            TerminalServerDialupPort tp = new TerminalServerDialupPort();

            tp.getPortTiming().setPreTxWait(new Integer(25));
            tp.getPortTiming().setRtsToTxWait(new Integer(0));
            tp.getPortTiming().setPostTxWait(new Integer(0));
            tp.getPortTiming().setReceiveDataWait(new Integer(0));
            tp.getPortTiming().setExtraTimeOut(new Integer(0));
            port = tp;
            break;
        case TCPPORT:
            TcpPort tcp = new TcpPort();

            tcp.getPortTiming().setPreTxWait(new Integer(25));
            tcp.getPortTiming().setRtsToTxWait(new Integer(0));
            tcp.getPortTiming().setPostTxWait(new Integer(0));
            tcp.getPortTiming().setReceiveDataWait(new Integer(0));
            tcp.getPortTiming().setExtraTimeOut(new Integer(0));
            tcp.getPortTiming().setPostCommWait(new Integer(0));
            port = tcp;
            break;
        default:
            return null;
        }

        // Grab a unique id
        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
        port.setPortID(paoDao.getNextPaoId());

        port.setDisableFlag(new Character('N'));
        port.getCommPort().setAlarmInhibit(new Character('N'));

        if (port.getCommPort().getCommonProtocol() == null) {
            port.getCommPort().setCommonProtocol("None");
        }

        port.getCommPort().setPerformThreshold(new Integer(90));
        port.getCommPort().setPerformanceAlarm(new Character('Y'));
        port.getCommPort().setSharedPortType(com.cannontech.common.util.CtiUtilities.STRING_NONE);
        port.getCommPort().setSharedSocketNumber(new Integer(CommPort.DEFAULT_SHARED_SOCKET_NUMBER));

        return port;
    }
}