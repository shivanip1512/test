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
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2005/04/22 19:00:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_H__
#define __DEV_MCT_H__
#pragma warning( disable : 4786)

#include "dev_carrier.h"
#include "pt_numeric.h"


class IM_EX_DEVDB CtiDeviceMCT : public CtiDeviceCarrier
{
public:
    enum
    {
        MCTConfig_ChannelCount = 4
    };

private:

    static DLCCommandSet _commandStore;

protected:

    enum WireConfig
    {
        WireConfigInvalid   = 0,
        WireConfigTwoWire   = 2,
        WireConfigThreeWire = 3
    } _wireConfig[MCTConfig_ChannelCount];

    double _mpkh[MCTConfig_ChannelCount];

    enum PeakMode
    {
        PeakModeInvalid       = 0,
        PeakModeOnPeakOffPeak = 1,
        PeakModeMinMax        = 2
    } _peakMode;

    enum ConfigType
    {
        ConfigInvalid = 0,
        Config2XX     = 2,
        Config3XX     = 3
    } _configType;

    RWCString _configName;

    bool          _lpIntervalSent;
    RWTime        _lastLPRequest;
    unsigned long _nextLPScanTime;

    unsigned long _disconnectAddress;

    bool _scanGeneralPending,
         _scanIntegrityPending,
         _scanAccumulatorPending;

    static bool getMCTDebugLevel(int mask);

    enum MCTDebug
    {
        MCTDebug_Debug_Info  = 1 << 0,
        MCTDebug_Scanrates   = 1 << 1,
        MCTDebug_LoadProfile = 1 << 2,
    };

    enum
    {
        MCT_ModelPos                  = 0x00,
        MCT_ModelLen                  =    8,
        MCT_SspecLen                  =    5,

        MCT_TimePos                   = 0x46,
        MCT_TimeLen                   =    3,
        MCT_TSyncPos                  = 0x49,
        MCT_TSyncLen                  =    3, //  5,  <-- !!  don't send the extra 2 bytes - this fools Porter into letting it through unscathed

        MCT_Command_Open             = 0x41,
        MCT_Command_Close            = 0x42,

        MCT_Command_GroupAddrInhibit = 0x53,
        MCT_Command_GroupAddrEnable  = 0x54,
        MCT_Command_ARML             = 0x60,
        MCT_Command_ARMD             = 0x61,
        MCT_Command_ARMC             = 0x62,

        MCT_Command_LPInt            = 0x70,

        MCT_Restore                   = 0x00,
        MCT_Shed_Base_07m             = 0x00,
        MCT_Shed_Base_15m             = 0x10,
        MCT_Shed_Base_30m             = 0x20,
        MCT_Shed_Base_60m             = 0x30,

        MCT_Rollover                  = 100000,   //  5 digits
        MCT_DemandIntervalDefault     = 300,      //  5 minute default demand, if not specified in the database...
        MCT_MaxPulseCount             = 10000000,

        MCT_PeakOffset                = 10,  //  peak demand points are offset by this amount (point offset 11, 12, 13...)

        MCT_LPWindow                  = 60,

        MCT_TestAddr1  = 0x155555,
        MCT_TestAddr2  = 0x2aaaaa
    };

    enum
    {
        MCT_PointOffset_Status_Powerfail      =  10,
        MCT_PointOffset_Accumulator_Powerfail =  20,

        MCT_PointOffset_LoadProfileOffset     = 100
    };

public:

    enum
    {
        //  for dev_mctbroadcast...  maybe he should be a friend someday
        MCT_Command_FreezeOne = 0x51,
        MCT_Command_FreezeTwo = 0x52
    };

    typedef CtiDeviceCarrier Inherited;

    CtiDeviceMCT( );
    CtiDeviceMCT( const CtiDeviceMCT &aRef );

    virtual ~CtiDeviceMCT( );

    CtiDeviceMCT& operator=( const CtiDeviceMCT &aRef );

    virtual RWTime adjustNextScanTime( const INT scanType=ScanRateGeneral );
    // RWTime setNextInterval(RWTime &aTime, ULONG scanrate);

    //  scanner-side functions
    //  to be overridden by the 24x, 310, and 318
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual ULONG calcNextLPScanTime( void );
    ULONG         getNextLPScanTime ( void );
    void          sendLPInterval( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int           checkDemandQuality( unsigned long &pulses, PointQuality_t &quality, bool &badData );

    //  porter-side functions
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    RWCString getDescription( const CtiCommandParser &parse ) const;

    virtual LONG getDemandInterval() const;

    static bool initCommandStore( );
    virtual bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

    void setMCTScanPending( int scantype, bool pending );
    void resetMCTScansPending( void );

    virtual bool clearedForScan( int scantype );
    virtual void resetForScan  ( int scantype );

    virtual INT GeneralScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 3 );
    virtual INT IntegrityScan  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT LoadProfileScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 9 );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    INT         executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executeScan     ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT         executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    virtual INT ErrorDecode ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );
    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );
    int         insertPointFail( INMESS *InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType );

    INT decodeLoopback ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValue ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutValue ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutStatus( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodePutConfig( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfig( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    INT decodeGetStatusDisconnect( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    INT  getSSpec() const;
    bool sspecIsValid( int sspec );
    RWCString sspecIsFrom( int sspec );
    bool isLoadProfile( int type );
    bool hasVariableDemandRate( int type, int sspec );

    void setConfigData( const RWCString &configName, int configType, const RWCString &configMode, const int mctwire[MCTConfig_ChannelCount], const double mpkh[MCTConfig_ChannelCount] );

    static  DOUBLE translateStatusValue( INT PointOffset, INT PointType, INT DeviceType, PUSHORT DataValueArray );
    static  INT extractStatusData( INMESS *InMessage, INT type, USHORT *StatusData );
    static  INT verifyAlphaBuffer( DSTRUCT *DSt );

};

#endif // #ifndef __DEV_MCT_H__
