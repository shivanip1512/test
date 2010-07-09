/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LCR3102_H__
#define __DEV_LCR3102_H__
#pragma warning( disable : 4786)

#include "dev_carrier.h"


namespace Cti       {
namespace Devices    {

class IM_EX_DEVDB LCR3102 : public CtiDeviceCarrier
{
private:

    typedef CtiDeviceCarrier Inherited;

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
        FuncRead_XfmrHistoricalCT2Pos  = 0x89,
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

        PointOffset_CommStatus       = 2000
    };

    virtual bool getOperation( const UINT &cmdType, BSTRUCT &b ) const;

    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeScan     ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT IntegrityScan   ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority );

    CtiDeviceSingle::point_info getSixBitValueFromBuffer(unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize);

    int parseLoadValue(CtiCommandParser &parse);
    int parsePreviousValue(CtiCommandParser &parse);

    INT decodeGetValueTransmitPower        ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueTemperature          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueIntervalLast         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePropCount            ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueHistoricalTime       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueControlTime          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueXfmrHistoricalRuntime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDutyCycle     	   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetConfigRaw       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigSoftspec  ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigAddress   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigSubstation( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigTime      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    std::vector<int> decodeXfmrHistoricalRuntimeMessage( BYTE message[] );
    std::vector<int> decodeMessageSoftspec             ( BYTE Message[] );
    std::vector<int> decodeMessageAddress              ( BYTE Message[] );
    std::vector<int> decodeMessageSubstation           ( BYTE Message[] );
    std::vector<int> decodeMessageTemperature          ( BYTE Message[] );
    std::vector<int> decodeMessageDutyCycle            ( BYTE Message[] );
    int              decodeMessageTime                 ( BYTE Message[] );
    int              decodeMessageTransmitPower        ( BYTE Message[] );

public:

    LCR3102( );
    LCR3102( const LCR3102 &aRef );

    virtual ~LCR3102( );

    LCR3102& operator=( const LCR3102 &aRef );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore );

    virtual INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual LONG getAddress() const;

};

}       // namespace Devices
}       // namespace Cti

#endif // #ifndef __DEV_LCR3102_H__
