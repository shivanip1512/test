
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/06/30 14:39:00 $
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
#include "thread.h"

class IM_EX_DEVDB CtiDeviceGateway : public CtiThread
{
public:

    typedef map< ULONG, CtiDeviceGatewayStat* > SMAP_t;
    typedef vector< ULONG > SNVECT_t;

protected:

    enum
    {
        GW_THREAD_TERMINATED = CtiThread::LAST,
        GW_CLEAN_ME
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

    unsigned char   _mac[6];
    unsigned long   _ipaddr;
    unsigned short  _spid;
    unsigned short  _geo;
    unsigned short  _feeder;
    unsigned long   _zip;
    unsigned short  _uda;
    unsigned char   _program;
    unsigned char   _splinter;

    unsigned long   _minutesWestOfGreenwich;

public:
    CtiDeviceGateway(SOCKET msgsock = INVALID_SOCKET);
    CtiDeviceGateway(const CtiDeviceGateway& aRef);
    virtual ~CtiDeviceGateway();
    CtiDeviceGateway& operator=(const CtiDeviceGateway& aRef);

    SOCKET getSocket() const;

    RWCString getMACAddress() const;
    RWCString getIPAddress() const;

    int processParse(CtiCommandParser &parse, CtiOutMessage *&OutMessage);
    int checkPendingOperations();

    int sendGet(USHORT Type, LONG dev = 0);
    int sendQueryRuntime(LONG dev, UCHAR Reset);
    void sendtm_Clock (BYTE hour = -1, BYTE minute = -1);
    int sendKeepAlive (void);
    void sendSetBindMode (UCHAR BindMode = TRUE);
    void sendSetPingMode (UCHAR PingMode = TRUE);
    void sendSetRSSIConfiguration (UCHAR AllMessages);
    void sendSetNetworkID (USHORT NetworkID);

    void sendSetAddressing(ULONG DeviceId, USHORT Spid, USHORT Geo, USHORT Feeder, ULONG Zip, USHORT Uda, UCHAR Program, UCHAR Splinter, ULONG ServerIP);
    void sendSetTimezone(ULONG minutesWestOfGreenwich, UCHAR doIt, USHORT DSTMinutesOffset);


    static int getMessageLength(GATEWAYRXSTRUCT *GatewayRX);
    static ULONG getDeviceID(GATEWAYRXSTRUCT *GatewayRX);

    const SNVECT_t& getThermostatSerialNumbers() const;
    void processGatewayMessage(GATEWAYRXSTRUCT &GatewayRX);
    bool getCompletedOperation( CtiPendingStatOperation &op );
    bool isConnected() const { return _msgsock != INVALID_SOCKET; }

    bool shouldClean();

    CtiMessage* rsvpToDispatch();

};
#endif // #ifndef __DEV_GATEWAY_H__
