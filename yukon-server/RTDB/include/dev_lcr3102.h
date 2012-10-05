#pragma once

#include "dev_carrier.h"

#include "dev_lcr3102_commands.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Lcr3102Device : public CarrierDevice
{
private:

    typedef CarrierDevice Inherited;

    static const    CommandSet  _commandStore;
    static          CommandSet  initCommandStore();

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
        DataRead_SoftspecLen       = 13
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

    virtual INT executeGetValue  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeScan      ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executeGetConfig ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT executePutConfig ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual INT IntegrityScan    ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority );

    int executeGetValueHistorical( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, OutMessageList &outList );

    CtiDeviceSingle::point_info getSixBitValueFromBuffer(unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize);

    int parseLoadValue(CtiCommandParser &parse);
    int parsePreviousValue(CtiCommandParser &parse);

    virtual INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeGetValueTransmitPower        ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueTemperature          ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueIntervalLast         ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValuePropCount            ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueHistoricalTime       ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueControlTime          ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueXfmrHistoricalRuntime( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueDutyCycle            ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeGetConfigRaw       ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigSoftspec  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigAddressing( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigTime      ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    void decodeMessageXfmrHistoricalRuntime( DSTRUCT DSt, std::vector<point_info> &runtimeHours);
    void decodeMessageDutyCycle            ( BYTE Message[], int &dutyCycle, int &transformer );
    void decodeMessageSoftspec             ( BYTE Message[], int &sspec, int &rev, int &serial, int &spid, int &geo );
    void decodeMessageAddress              ( BYTE Message[], int &prgAddr1, int &prgAddr2, int &prgAddr3, int &prgAddr4, int &splAddr1, int &splAddr2, int &splAddr3, int &splAddr4 );
    void decodeMessageSubstation           ( BYTE Message[], int &substation, int &feeder, int &zip, int &uda );
    void decodeMessageTemperature          ( BYTE Message[], int &txTemp, int &boxTemp );
    void decodeMessageTime                 ( BYTE Message[], CtiTime &time);
    void decodeMessageTransmitPower        ( BYTE Message[], int &transmitPower );

    virtual INT ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, std::list<CtiMessage*>& retList);

public:

    Lcr3102Device( );
    Lcr3102Device( const Lcr3102Device &aRef );

    Lcr3102Device& operator=( const Lcr3102Device &aRef );

    virtual LONG getAddress() const;
    
    LONG getSerial() const;

};

}       // namespace Devices
}       // namespace Cti

