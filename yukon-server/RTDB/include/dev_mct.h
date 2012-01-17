#pragma once

#include "dev_carrier.h"
#include "pt_numeric.h"
#include "prot_emetcon.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB MctDevice : public CarrierDevice
{
private:

    typedef CarrierDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    CtiTime _lastReadDataPurgeTime;

    int _freeze_counter,
        _freeze_expected;

protected:

    enum
    {
        MCTConfig_ChannelCount = 4
    };

    virtual bool getOperation( const UINT &cmdType, BSTRUCT &b ) const;

    static bool getOperationFromStore( const CommandSet &store, const UINT &cmd, BSTRUCT &bst );

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

    bool          _lpIntervalSent;
    CtiTime       _lastLPRequest;
    unsigned long _nextLPScanTime;

    unsigned long _disconnectAddress;

    int     getCurrentFreezeCounter () const;
    int     getExpectedFreezeCounter() const;
    bool    getExpectedFreezeParity() const;
    CtiTime getLastFreezeTimestamp(const CtiTime &TimeNow);
    CtiTime getLastScheduledFreezeTimestamp(const CtiTime &TimeNow);
    static CtiTime findLastScheduledFreeze(const CtiTime &TimeNow, unsigned freeze_day);
    void    updateFreezeInfo(int freeze_counter, unsigned long freeze_timestamp);

    static bool getMCTDebugLevel(int mask);

    struct value_descriptor
    {
        unsigned length;
        CtiTableDynamicPaoInfo::PaoInfoKeys name;
    };

    //  multimap because data for multiple keys could start at the same location
    typedef std::multimap<unsigned, value_descriptor> ValueMapping;
    typedef std::map<unsigned, ValueMapping> FunctionReadValueMappings;

    virtual const ValueMapping *getMemoryMap(void) const;
    virtual const FunctionReadValueMappings *getFunctionReadValueMaps(void) const;

    const ValueMapping *getValueMapForFunctionRead(const unsigned function) const;

    void decodeReadDataForKey(const unsigned char *begin, const unsigned char *end, const CtiTableDynamicPaoInfo::PaoInfoKeys key);

    virtual INT executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeScan     ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList& vgList, CtiMessageList& retList, OutMessageList& outList);

    INT decodeLoopback ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValue ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeControl  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodePutValue ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodePutStatus( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfig( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeGetStatusDisconnect( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual INT ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList& retList);

    int insertPointFail( const INMESS &InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType );

    enum DebugLevels
    {
        DebugLevel_Info        = 1 << 0,
        DebugLevel_Scanrates   = 1 << 1,
        DebugLevel_LoadProfile = 1 << 2,
        DebugLevel_DynamicInfo = 1 << 3,
        DebugLevel_Configs     = 1 << 4,
    };

    enum Memory
    {
        Memory_ModelPos          = 0x00,
        Memory_ModelLen          =    8,
    };

    enum Commands
    {
        Command_Disconnect          = 0x41,
        Command_Connect             = 0x42,

        Command_Latch               = 0x48,

        Command_GroupAddressInhibit = 0x53,
        Command_GroupAddressEnable  = 0x54,
        Command_ARML                = 0x60,
        Command_ARMD                = 0x61,
        Command_ARMC                = 0x62,

        Command_LPInt               = 0x70,

        Command_PhaseDetectClear    = 0x84,

        Command_Restore     = 0x00,
        Command_Shed_07m    = 0x00,
        Command_Shed_15m    = 0x10,
        Command_Shed_30m    = 0x20,
        Command_Shed_60m    = 0x30,
    };

    enum Miscellaneous
    {
        DemandInterval_Default      = 300,      //  5 minute default demand, if not specified in the database...

        LoadProfileCollectionWindow = 60,
    };

    enum PointOffsets
    {
        PointOffset_Status_GeneralAlarm   =   9,
        PointOffset_Status_Powerfail      =  10,

        PointOffset_Accumulator_Powerfail =  20,

        PointOffset_LoadProfileOffset     = 100,
    };

    bool sspecIsValid( int sspec );
    int  checkFreezeLogic(const CtiTime &TimeNow, int incoming_counter, std::string &error_string );
    bool hasVariableDemandRate( int type, int sspec );
    bool isLoadProfile( int type );
    std::string sspecIsFrom( int sspec );
    void resetMCTScansPending( void );

    //  scanner-side functions
    //  to be overridden by the 24x, 310, and 318
    void          sendLPInterval( OUTMESS *&OutMessage, OutMessageList &outList );
    int           checkDemandQuality( unsigned long &pulses, PointQuality_t &quality, bool &badData );

    //  porter-side functions
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

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

        //  this is for porttime.cpp and portentry.cpp
        Memory_TSyncPos = 0x49,
        Memory_TSyncLen =    5,
    };

    MctDevice( );
    MctDevice( const MctDevice &aRef );

    virtual ~MctDevice( );

    MctDevice& operator=( const MctDevice &aRef );

    virtual CtiTime adjustNextScanTime( const INT scanType=ScanRateGeneral );

    static bool isMct410( int type );
    static bool isMct420( int type );

    //  scanner-side functions
    //  to be overridden by the 24x, 310, and 318
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
    virtual ULONG calcNextLPScanTime( void );
    ULONG         getNextLPScanTime ( void );

    std::string getDescription( const CtiCommandParser &parse ) const;

    virtual LONG getDemandInterval();

    virtual INT GeneralScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 3 );
    virtual INT IntegrityScan  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4 );
    virtual INT LoadProfileScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 9 );

    void extractDynamicPaoInfo(const INMESS &InMessage);

    void setConfigData( const std::string &configName, int configType, const std::string &configMode, const int mctwire[MCTConfig_ChannelCount], const double mpkh[MCTConfig_ChannelCount] );

    void setExpectedFreeze( int freeze );  //  overrides a do-nothing virtual in dev_base
    int  getNextFreeze( void ) const;
};

}
}

