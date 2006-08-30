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
* REVISION     :  $Revision: 1.47 $
* DATE         :  $Date: 2006/08/30 20:49:51 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_H__
#define __DEV_MCT_H__
#pragma warning( disable : 4786)

#include "dev_carrier.h"
#include "pt_numeric.h"
#include "config_parts.h"
#include "prot_emetcon.h"


class IM_EX_DEVDB CtiDeviceMCT : public CtiDeviceCarrier
{
public:
    enum
    {
        MCTConfig_ChannelCount = 4
    };

private:

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    CtiTime _lastReadDataPurgeTime;

protected:

    virtual bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

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

    string _configName;

    bool          _lpIntervalSent;
    CtiTime        _lastLPRequest;
    unsigned long _nextLPScanTime;

    unsigned long _disconnectAddress;

    int _freeze_counter,
        _expected_freeze;

    static bool getMCTDebugLevel(int mask);

    struct MessageReadData  //  Stores message data so we know what is coming back at us for our generic decode
    {
        USHORT newSequence;
        USHORT oldSequence;
        USHORT ioType;
        int location, length;
        CtiTime insertTime;

        bool MessageReadData::operator<(const MessageReadData &rhs) const
        {
            bool retval = false;

            if( newSequence < rhs.newSequence )
            {
                retval = true;
            }
            return retval;
        }
    };

    typedef set<MessageReadData> MessageReadDataSet_t;

    class DynamicPaoAddressing
    {
    public:
        DynamicPaoAddressing(int addr, int len, CtiTableDynamicPaoInfo::Keys k ) :
            address(addr),
            length(len),
            key(k)
        {
        }

        int address, length;
        CtiTableDynamicPaoInfo::Keys key;

        bool DynamicPaoAddressing::operator<(const DynamicPaoAddressing &rhs) const
        {
            bool retval = false;

            if( address < rhs.address )
            {
                retval = true;
            }
            return retval;
        }
    };

    int _lastSequenceNumber;
    MessageReadDataSet_t _expectedReadData;
    bool recordMessageRead(OUTMESS *OutMessage);
    bool restoreMessageRead(INMESS *InMessage, int &ioType, int &location);
    bool recordMultiMessageRead(list< OUTMESS* > &outList);

    enum SequenceDataNumbers
    {
        SequenceCountBegin = 20000,
        SequenceCountEnd   = 30000
    };

    enum MCTDebug
    {
        MCTDebug_Debug_Info  = 1 << 0,
        MCTDebug_Scanrates   = 1 << 1,
        MCTDebug_LoadProfile = 1 << 2,
        MCTDebug_DynamicInfo = 1 << 3,
    };

    enum Memory
    {
        Memory_ModelPos                  = 0x00,
        Memory_ModelLen                  =    8,
        Memory_SspecPos                  = 0x00,
        Memory_SspecLen                  =    5,

        Memory_TimePos                   = 0x46,
        Memory_TimeLen                   =    3,
        Memory_TSyncPos                  = 0x49,
        Memory_TSyncLen                  =    5,

        Command_Open             = 0x41,
        Command_Close            = 0x42,

        Command_Latch            = 0x48,

        Command_GroupAddrInhibit = 0x53,
        Command_GroupAddrEnable  = 0x54,
        Command_ARML             = 0x60,
        Command_ARMD             = 0x61,
        Command_ARMC             = 0x62,

        Command_LPInt            = 0x70,

        Command_Restore          = 0x00,
        Shed_Base_07m            = 0x00,
        Shed_Base_15m            = 0x10,
        Shed_Base_30m            = 0x20,
        Shed_Base_60m            = 0x30,

    };

    enum Miscellaneous
    {
        DemandInterval_Default      = 300,      //  5 minute default demand, if not specified in the database...

        LoadProfileCollectionWindow = 60,
    };

    enum PointOffsets
    {
        PointOffset_Status_Powerfail      =  10,
        PointOffset_Accumulator_Powerfail =  20,

        PointOffset_LoadProfileOffset     = 100,
    };

public:

    enum
    {
        //  for dev_mctbroadcast...  maybe he should be a friend someday
        //  also, these may need ARMS if they're going to a 200-series MCT
        Command_FreezeOne = 0x51,
        Command_FreezeTwo = 0x52,

        //  for dev_dlcbase...  maybe he should be a friend someday
        TestAddress1  = 0x155555,
        TestAddress2  = 0x2aaaaa,
    };

    typedef CtiDeviceCarrier Inherited;

    CtiDeviceMCT( );
    CtiDeviceMCT( const CtiDeviceMCT &aRef );

    virtual ~CtiDeviceMCT( );

    CtiDeviceMCT& operator=( const CtiDeviceMCT &aRef );

    virtual CtiTime adjustNextScanTime( const INT scanType=ScanRateGeneral );
    // CtiTime setNextInterval(CtiTime &aTime, ULONG scanrate);

    //  scanner-side functions
    //  to be overridden by the 24x, 310, and 318
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual ULONG calcNextLPScanTime( void );
    ULONG         getNextLPScanTime ( void );
    void          sendLPInterval( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    int           checkDemandQuality( unsigned long &pulses, PointQuality_t &quality, bool &badData );

    //  porter-side functions
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    string getDescription( const CtiCommandParser &parse ) const;

    virtual LONG getDemandInterval() const;

    virtual void getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);
    virtual void getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);

    void resetMCTScansPending( void );

    virtual INT GeneralScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3 );
    virtual INT IntegrityScan  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT LoadProfileScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 9 );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeScan     ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    int insertPointFail( INMESS *InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType );

    INT decodeLoopback ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValue ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodePutValue ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodePutStatus( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetStatusDisconnect( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT  getSSpec() const;
    bool sspecIsValid( int sspec );
    string sspecIsFrom( int sspec );
    bool isLoadProfile( int type );
    bool hasVariableDemandRate( int type, int sspec );

    void setConfigData( const string &configName, int configType, const string &configMode, const int mctwire[MCTConfig_ChannelCount], const double mpkh[MCTConfig_ChannelCount] );

    void setExpectedFreeze( int freeze );  //  overrides a do-nothing virtual in dev_base
    int  getNextFreeze( void ) const;

    static  INT extractStatusData( INMESS *InMessage, INT type, USHORT *StatusData );
    static  INT verifyAlphaBuffer( DSTRUCT *DSt );

    typedef set< DynamicPaoAddressing > DynamicPaoAddressing_t;
    typedef map< int, DynamicPaoAddressing_t > DynamicPaoFunctionAddressing_t;
};

#endif // #ifndef __DEV_MCT_H__
