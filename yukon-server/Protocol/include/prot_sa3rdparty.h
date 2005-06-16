
/*-----------------------------------------------------------------------------*
*
* File:   prot_sa3rdparty
*
* Class:  CtiProtocolSA3rdParty
* Date:   4/9/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/06/16 21:25:14 $
* HISTORY      :
*
* $Log: prot_sa3rdparty.h,v $
* Revision 1.13  2005/06/16 21:25:14  cplender
* Adding the RTC scan command and decode. Must be trested with a device.
*
* Revision 1.12  2005/05/16 20:37:24  cplender
* Altered the terminate syntax to send a 0 count cycle if we are before the last period or nothing if beyond that point.
*
* Revision 1.11  2005/04/27 13:45:01  cplender
* Code change to record the most recent control command to be sent out so that a restore can use the same stime/ctime.
* Removed unused OutMessage argument from several methods.
*
* Revision 1.10  2005/03/10 19:22:50  mfisher
* changed CtiProtocolBase to Cti::Protocol::Interface
*
* Revision 1.9  2005/02/10 23:23:58  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.8  2004/12/14 22:25:16  cplender
* Various to wring out config commands.  Should be pretty good.
*
* Revision 1.7  2004/10/14 20:39:09  cplender
* Added config205 and tamper205 and coldLoad205 to the party.
*
* Revision 1.6  2004/09/20 16:11:51  mfisher
* changed RTM functions to be static - we don't need to keep state
*
* Revision 1.5  2004/07/30 21:35:07  cplender
* RTM stuff
*
* Revision 1.4  2004/06/24 13:16:12  cplender
* Some cleanup on the simulator to make RTC and LMIRTU trx sessions look the same.
* Added PORTER_SA_RTC_MAXCODES the maimum number of codes that can be sent in one block
*
* Revision 1.3  2004/06/03 21:46:17  cplender
* Simulator mods.
*
* Revision 1.2  2004/05/19 14:55:56  cplender
* Supportting new dsm2.h struct CtiSAData
*
* Revision 1.1  2004/04/29 19:58:50  cplender
* Initial sa protocol/load group support
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PROT_SA3RDPARTY_H__
#define __PROT_SA3RDPARTY_H__

#include "cmdparse.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"
using namespace Cti;  //  in preparation for moving devices to their own namespace

#include "protocol_sa.h"        // Telvent provided sa library.

#define MAX_SAERR_MSG_SIZE 256

#define GRP_SA_RTM 100              // Must be greater than any REAL grouptype.

class IM_EX_PROT CtiProtocolSA3rdParty : public Protocol::Interface
{
protected:

    enum
    {
        sac_unspec = 16,
        sac_toos,               // _sTime = hours off
        sac_address_config      // ???
    };

    CtiSAData _sa;

    int _sTime;
    int _cTime;
    RWTime _onePeriodTime;

    bool _messageReady;

    CHAR _errorBuf[MAX_SAERR_MSG_SIZE];
    int _errorLen;

    INT assembleControl(CtiCommandParser &parse);
    INT assemblePutConfig(CtiCommandParser &parse);

    INT loadControl();                 // This is a shed!
    INT addressAssign(INT &len, USHORT slot);
    INT restoreLoadControl();
    //INT formRTMRequest(USHORT command);


    /*
     * This method used the input strategy or the period and percentage to produce a strategy value.
     * The produced value will be equal or larger than the requested parse.  If the parse is unsuccessful, the result will be -1.
     */
    int solveStrategy(CtiCommandParser &parse);

private:

    // The data below is used to identify the strategy in a Versacomesque fashion.  This data is converted to the closest equal or higher control values.
    int _period;          // Cycle Time. 7.5*60, 15*60, 20*60, 30*60, & 60*60 are valid values.
    int _timeout;         // Percentage of the cycle that the loads are to be shed (in seconds = timeout.).

    void computeShedTimes(int shed_time);
    void processResult(INT retCode);
    void computeSnCTime();

public:

    CtiProtocolSA3rdParty();
    CtiProtocolSA3rdParty(const CtiSAData sa);

    virtual ~CtiProtocolSA3rdParty();

    CtiProtocolSA3rdParty& operator=(const CtiProtocolSA3rdParty& aRef);
    int parseCommand(CtiCommandParser &parse);

    bool messageReady() const;

    CtiProtocolSA3rdParty& setTransmitterAddress( int val );
    CtiProtocolSA3rdParty& setGroupType( int val );
    CtiProtocolSA3rdParty& setShed( bool val );
    CtiProtocolSA3rdParty& setFunction( int val );
    CtiProtocolSA3rdParty& setCode205( int val );
    CtiProtocolSA3rdParty& setCodeGolay( RWCString val );
    CtiProtocolSA3rdParty& setCodeSADigital( RWCString val );
    CtiProtocolSA3rdParty& setSwitchTimeout( int val );
    CtiProtocolSA3rdParty& setCycleTime( int val );
    CtiProtocolSA3rdParty& setRepeats( int val );
    CtiProtocolSA3rdParty& setLBTMode( int val );
    CtiProtocolSA3rdParty& setDelayTxTime( int val );
    CtiProtocolSA3rdParty& setMaxTxTime( int val );

    void copyMessage(RWCString &str) const;

    void getBuffer(BYTE *dest, ULONG &len) const;
    void appendVariableLengthTimeSlot(int transmitter,BYTE *dest,ULONG &len, BYTE dlyToTx = 0, BYTE maxTx = 0, BYTE lbtMode = 0);
    void statusScan(int transmitter, BYTE *dest, ULONG &len);

    INT getSABufferLen() const;
    CtiSAData getSAData() const;
    CtiProtocolSA3rdParty& setSAData(const CtiSAData &sa);

    RWCString asString() const;
    RWCString strategyAsString() const;
    RWCString functionAsString() const;
    int getStrategySTime() const;
    int getStrategyCTime() const;
    RWTime getStrategyOnePeriodTime() const;

    static INT formatTMScmd (UCHAR *abuf, INT *buflen, USHORT TMS_cmd_type, USHORT xmitter);
    static INT TMSlen (UCHAR *abuf, INT *len);
    static INT procTMSmsg(UCHAR *abuf, INT len, SA_CODE *scode, X205CMD *x205cmd);
};

#endif // #ifndef __PROT_SA3RDPARTY_H__
