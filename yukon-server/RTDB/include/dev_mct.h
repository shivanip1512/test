#pragma warning( disable : 4786)
#ifndef __DEV_MCT_H__
#define __DEV_MCT_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct
*
* Class:  CtiDeviceMCT
* Date:   12/21/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/11/01 21:04:06 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_carrier.h"
#include "pt_numeric.h"


#define MCT_DEMANDINTERVAL_DEFAULT     300    //  5 minute default demand, if not specified in the database...

#define MAXPULSECOUNT 10000000
#define MAX_MCTMAGIC 35       // Any number > 32..

class IM_EX_DEVDB CtiDeviceMCT : public CtiDeviceCarrier
{
public:

protected:

    INT _magicNumber;
    INT _retryCount;

    INT _queueStat;
    RWTime _queueTime;
    ULONG _nextLPScanTime;
    ULONG _lpIntervalSent;

    enum
    {
        MCT_ModelAddr        = 0x00,
        MCT_ModelLen         =    8,
        MCT_SspecLen         =    5,

        MCT_FuncWriteOpen    = 0x41,
        MCT_FuncWriteClose   = 0x42,
        MCT_TimeAddr         = 0x46,
        MCT_TimeLen          =    3,
        MCT_TSyncAddr        = 0x49,
        MCT_TSyncLen         =    3, //  5,  <-- !!  don't send the extra 2 bytes - this fools Porter into letting it through unscathed

        MCT_GroupAddrInhibit = 0x53,
        MCT_GroupAddrEnable  = 0x54,

        MCT_Rollover         = 100000,  //  5 digits

        MCT_LPInt_Func       = 0x70
    };

private:

    static CTICMDSET _commandStore;

public:

    typedef CtiDeviceCarrier Inherited;


    CtiDeviceMCT( );
    CtiDeviceMCT( const CtiDeviceMCT &aRef );

    virtual ~CtiDeviceMCT( );

    CtiDeviceMCT& operator=( const CtiDeviceMCT &aRef );

    virtual RWTime adjustNextScanTime( const INT scanType=ScanRateGeneral );
    // RWTime setNextInterval(RWTime &aTime, ULONG scanrate);

    //  to be overridden by the 24x, 310, and 318
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual ULONG calcNextLPScanTime( void );
    ULONG         getNextLPScanTime( void );
    void          sendLPInterval( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int           checkLoadProfileQuality( unsigned long &pulses, PointQuality_t &quality, int &badData );

    RWCString getDescription( const CtiCommandParser &parse ) const;

    virtual LONG getDemandInterval() const;


    static bool initCommandStore( );
    virtual bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

    virtual INT GeneralScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 3 );
    virtual INT IntegrityScan  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT LoadProfileScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 9 );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    INT executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executeScan     ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    virtual INT ErrorDecode ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );
    int         insertPointFail( INMESS *InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType );
    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeLoopback ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValue ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutValue ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutStatus( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutConfig( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfig( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    INT  getSSpec() const;
    bool sspecIsValid( int sspec );
    RWCString sspecIsFrom( int sspec );

    static  DOUBLE translateStatusValue( INT PointOffset, INT PointType, INT DeviceType, PUSHORT DataValueArray );
    static  INT extractStatusData( INMESS *InMessage, INT type, USHORT *StatusData );
    static  INT verifyAlphaBuffer( DSTRUCT *DSt );

    virtual bool clearedForScan( int scantype );
    virtual void resetForScan  ( int scantype );

};
#endif // #ifndef __DEV_MCT_H__
