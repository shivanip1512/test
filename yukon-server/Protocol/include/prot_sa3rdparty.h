
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/29 19:58:50 $
* HISTORY      :
*
* $Log: prot_sa3rdparty.h,v $
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

#define MAX_SA_MSG_SIZE 256
#define MAX_SAERR_MSG_SIZE 256

class IM_EX_PROT CtiProtocolSA3rdParty : public CtiProtocolBase
{
protected:

    BYTE _lbt;
    BYTE _delayToTx;                    // Time in seconds before transmitting codes.
    BYTE _maxTxTime;                    // Maximum Time in seconds to transmit codes.

    int _transmitterAddress;            // The address of the RTC, or RTU.
    int _groupType;                     // This must be one of the supported DCU types in the lib.. ie. SA205, GOLAY...

    bool _shed;                         // NON DCU205 groups use this bool to determine shed or restore.
    int _function;                      // This is the function to execute on the DCU....  Should be directly applied in the switch.

    int _code205;                       // This is the code to transmit iff this is an SA205 DCU type group.
    CHAR _codeSimple[7];                // This is the code to transmit iff this is NOT an SA205 DCU type group.

    CHAR _serial205[33];                // This is a 205 serial number.

    // The parameters below are assigned typically by the parse object
    int _swTimeout;                     // Switch OFF time in seconds.
    int _cycleTime;                     // Switch on + off time in seconds.
    int _repeats;                       // Number of _cycleTimes to repeat the operation (DCU205)

    bool _messageReady;
    BYTE _buffer[MAX_SA_MSG_SIZE];
    int _bufferLen;                     // This is the valid size of the prepared _buffer

    CHAR _errorBuf[MAX_SAERR_MSG_SIZE];
    int _errorLen;

    INT assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    INT loadControl();                 // This is a shed!
    INT restoreLoadControl();

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

public:

    CtiProtocolSA3rdParty();
    CtiProtocolSA3rdParty(const CtiProtocolSA3rdParty& aRef);

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

};

#endif // #ifndef __PROT_SA3RDPARTY_H__
