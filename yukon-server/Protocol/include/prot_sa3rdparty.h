
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/07/30 21:35:07 $
* HISTORY      :
*
* $Log: prot_sa3rdparty.h,v $
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
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"
#include "protocol_sa.h"        // Telvent provided sa library.

#define MAX_SAERR_MSG_SIZE 256

#define GRP_SA_RTM 100              // Must be greater than any REAL grouptype.

class IM_EX_PROT CtiProtocolSA3rdParty : public CtiProtocolBase
{
protected:

    CtiSAData _sa;

    int _sTime;
    int _cTime;

    bool _messageReady;

    CHAR _errorBuf[MAX_SAERR_MSG_SIZE];
    int _errorLen;

    INT assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    INT loadControl();                 // This is a shed!
    INT restoreLoadControl();
    INT formRTMRequest(USHORT command);


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
    int parseCommand(CtiCommandParser &parse, CtiOutMessage &OutMessage);

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
    void appendVariableLengthTimeSlot(BYTE *dest, ULONG &len) const;

    INT getSABufferLen() const;
    CtiSAData getSAData() const;
    CtiProtocolSA3rdParty& setSAData(const CtiSAData &sa);

    RWCString asString() const;
    RWCString decomposeMessage(BYTE *buf) const;
    RWCString strategyAsString() const;
    RWCString functionAsString() const;

};

#endif // #ifndef __PROT_SA3RDPARTY_H__
