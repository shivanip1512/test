#pragma once

#include "dev_carrier.h"

#include "dev_lcr3102_commands.h"
#include "tbl_dyn_lcrComms.h"


namespace Cti {
namespace Devices {

class IM_EX_DEVDB Lcr3102Device : public CarrierDevice
{
    typedef CarrierDevice Inherited;

    static const    CommandSet  _commandStore;
    static          CommandSet  initCommandStore();

    CtiTableDynamicLcrCommunications    _dynamicComms;

    // Data received from the last get config addressing.  We save these
    //   to save in the database when all messages are received.
    int programAddressRelay[4], splinterAddressRelay[4];
    int substation, feeder, zipcode, uda;
    int sspec, rev, serial, spid, geo;

    // Times from all 3 get config addressing messages.  When we have all 3,
    // we write to the database
    boost::optional<CtiTime> lastAddressingMessage1;
    boost::optional<CtiTime> lastAddressingMessage2;
    boost::optional<CtiTime> lastAddressingMessage3;

    void updateLastCommsTime( const INT sequence_id, const CtiTime & current_time );

    void writeAddress(Database::DatabaseWriter &writer, int id, long devid);
    void writeRelay(Database::DatabaseWriter &writer, int id, int relay);

protected:

    enum Functions
    {
        FuncRead_LastIntervalPos = 0x8C,
        FuncRead_LastIntervalLen = 5,

        FuncRead_RuntimePos      = 0xB0,
        FuncRead_RuntimeLen      = 13,

        FuncRead_ShedtimePos     = 0xC0,
        FuncRead_ShedtimeLen     = 13,

        FuncRead_PropCountPos    = 0x13,
        FuncRead_PropCountLen    = 1,

        FuncRead_ControlTimePos  = 0x92,
        FuncRead_ControlTimeLen  = 5,

        FuncRead_XfmrHistoricalCT1Pos  = 0x88,
        FuncRead_XfmrHistoricalLen     = 6,

        FuncRead_DutyCyclePos    = 0x90,
        FuncRead_DutyCycleLen    = 2
    };

    enum DataReads
    {
        DataRead_TransmitPowerPos  = 0xf9,
        DataRead_TransmitPowerLen  = 1,

        DataRead_DeviceTimePos     = 0x40,
        DataRead_DeviceTimeLen     = 4,

        DataRead_TemperaturePos    = 0x03,
        DataRead_TemperatureLen    = 4,

        DataRead_AddressInfoPos    = 0x02,
        DataRead_AddressInfoLen    = 8,

        DataRead_SubstationDataPos = 0x01,
        DataRead_SubstationDataLen = 9,

        DataRead_SoftspecPos       = 0x00,
        DataRead_SoftspecLen       = 13,

        DataRead_LoopbackPos       = 0x00,
        DataRead_LoopbackLen       = 3
    };

    enum PointOffsets
    {
        PointOffset_LastIntervalBase = 1,   //      PointOffset = Base# + (load/relay# - 1)
        PointOffset_RuntimeBase      = 5,   //      ditto here
        PointOffset_ShedtimeBase     = 9,   //      and here

        PointOffset_PropCount        = 13,
        PointOffset_ControlTimeBase  = 15,  //      AND HERE
        PointOffset_DutyCycleBase    = 19,  //      AND HERE
        PointOffset_XfmrHistorical   = 23,

        PointOffset_TamperInfo       = 30,

        PointOffset_CommStatus       = 2000
    };

    virtual bool getOperation( const UINT &cmdType, BSTRUCT &b ) const;

    YukonError_t executeLoopback  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executeGetValue  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executeScan      ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executeGetConfig ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executePutConfig ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t IntegrityScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority ) override;

    YukonError_t executeGetValueHistorical( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, OutMessageList &outList );

    CtiDeviceSingle::point_info getSixBitValueFromBuffer(const unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize);

    int parseLoadValue(CtiCommandParser &parse);
    int parsePreviousValue(CtiCommandParser &parse);

    YukonError_t ResultDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t decodeGetValueTransmitPower        ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueTemperature          ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueIntervalLast         ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValuePropCount            ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueHistoricalTime       ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueControlTime          ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueXfmrHistoricalRuntime( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueDutyCycle            ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    YukonError_t decodePutConfig( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    YukonError_t decodeGetConfigRaw       ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigSoftspec  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigAddressing( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigTime      ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    YukonError_t decodeLoopback( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    void decodeMessageXfmrHistoricalRuntime( const DSTRUCT DSt, std::vector<point_info> &runtimeHours);
    void decodeMessageDutyCycle            ( const BYTE Message[], int &dutyCycle, int &transformer );
    void decodeMessageSoftspec             ( const BYTE Message[], int &sspec, int &rev, int &serial, int &spid, int &geo );
    void decodeMessageAddress              ( const BYTE Message[], int &prgAddr1, int &prgAddr2, int &prgAddr3, int &prgAddr4, int &splAddr1, int &splAddr2, int &splAddr3, int &splAddr4 );
    void decodeMessageSubstation           ( const BYTE Message[], int &substation, int &feeder, int &zip, int &uda );
    void decodeMessageTemperature          ( const BYTE Message[], int &txTemp, int &boxTemp );
    void decodeMessageTime                 ( const BYTE Message[], CtiTime &time);
    void decodeMessageTransmitPower        ( const BYTE Message[], int &transmitPower );

    YukonError_t ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList& retList) override;

public:

    Lcr3102Device( );

    virtual LONG getAddress() const;

    LONG getSerial() const;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader( Cti::RowReader &rdr );
};

}       // namespace Devices
}       // namespace Cti

