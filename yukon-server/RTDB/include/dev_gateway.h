
/*-----------------------------------------------------------------------------*
*
* File:   dev_gateway
*
* Class:  CtiDeviceGateway
* Date:   6/17/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/07/21 21:34:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GATEWAY_H__
#define __DEV_GATEWAY_H__

#include <windows.h>
#include <iostream>
#include <map>
#include <vector>
using namespace std;

#include "ctitypes.h"
#include "cmdparse.h"
#include "dlldefs.h"
#include "dev_gwstat.h"
#include "gateway.h"
#include "pending_gwresult.h"
#include "thread.h"

class IM_EX_DEVDB CtiDeviceGateway : public CtiThread
{
public:

    typedef map< ULONG, CtiDeviceGatewayStat* > SMAP_t;
    typedef vector< ULONG > SNVECT_t;

protected:

    enum
    {
        GW_THREAD_TERMINATED = CtiThread::LAST
    };

    void run();

    static void postPorter();
    static HANDLE hPostPorter;

private:

    long _rfId;         // Unique Identifier of the RF module.
    SOCKET _msgsock;    // Socket to this device.

    SMAP_t _statMap;    // This is a map of known stats.
    SNVECT_t _statSN;   // This is a vector of their serial numbers.
    bool _socketConnected;

public:
    CtiDeviceGateway(SOCKET msgsock = INVALID_SOCKET);
    CtiDeviceGateway(const CtiDeviceGateway& aRef);
    virtual ~CtiDeviceGateway();
    CtiDeviceGateway& operator=(const CtiDeviceGateway& aRef);

    SOCKET getSocket() const;

    int processParse(CtiCommandParser &parse, CtiOutMessage *&OutMessage, CtiPendingGatewayResult& pendingOperation);
    int checkPendingOperation( CtiPendingGatewayResult& pendingOperation );

    int sendGet(USHORT Type, LONG dev = 0);
    void sendtm_Clock (void);
    void sendKeepAlive (void);
    void sendSetBindMode (UCHAR BindMode = TRUE);
    void sendSetPingMode (UCHAR PingMode = TRUE);
    void sendSetRSSIConfiguration (UCHAR AllMessages);
    void sendSetNetworkID (USHORT NetworkID);
    static int getMessageLength(GATEWAYRXSTRUCT *GatewayRX);
    static ULONG getDeviceID(GATEWAYRXSTRUCT *GatewayRX);

    const SNVECT_t& getThermostatSerialNumbers() const;
    void processGatewayMessage(GATEWAYRXSTRUCT &GatewayRX);
};
#endif // #ifndef __DEV_GATEWAY_H__
