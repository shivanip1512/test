/*-----------------------------------------------------------------------------*
*
* File:   dev_mct310
*
* Class:  CtiDeviceMCT310
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct310.h-arc  $
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2006/10/04 19:12:57 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT310_H__
#define __DEV_MCT310_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT310 : public CtiDeviceMCT
{
private:

    typedef CtiDeviceMCT Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum Memory
    {
        Memory_DemandPos = 0x2c,
        Memory_DemandLen =    2,

        Memory_StatusPos = 0x05,
        Memory_StatusLen =    2,

        MCT3XX_PutMRead1Pos            = 0x20,
        MCT3XX_PutMRead2Pos            = 0x32,
        MCT3XX_PutMRead3Pos            = 0x51,
        MCT3XX_PutMReadLen             =    6,

        MCT3XX_Mult1Pos                = 0x26,
        MCT3XX_Mult2Pos                = 0x38,
        MCT3XX_Mult3Pos                = 0x57,
        MCT3XX_MultLen                 =    2,

        MCT3XX_PFCountPos              = 0x07,
        MCT3XX_PFCountLen              =    2,

        MCT3XX_ResetPos                = 0x06,
        MCT3XX_ResetLen                =    1,

        MCT3XX_TimePos                 = 0x7A,
        MCT3XX_TimeLen                 =    5,

        MCT3XX_LPStatusPos             = 0x70,
        MCT3XX_LPStatusLen             =    9,

        MCT3XX_DemandIntervalPos       = 0x1B,
        MCT3XX_DemandIntervalLen       =    1,
        MCT3XX_LPIntervalPos           = 0x76,
        MCT3XX_LPIntervalLen           =    1,

        MCT3XX_OptionPos               = 0x02,
        MCT3XX_OptionLen               =    6,
        MCT3XX_GenStatPos              = 0x05,
        MCT3XX_GenStatLen              =    9,

        MCT3XX_MinMaxPeakConfigPos     = 0x03,
    };

    enum Functions
    {
        FuncRead_MReadPos        = 0x90,
        FuncRead_MReadLen        =    9,
        FuncRead_FrozenPos       = 0x91,
        FuncRead_FrozenLen       =    9,
    };

    enum Commands
    {
        Command_PeakOff          = 0x59,
        Command_PeakOn           = 0x5a,
    };

    enum
    {
        MCT3XX_GroupAddrPos            = 0x10,
        MCT3XX_GroupAddrLen            =    5,

        MCT3XX_UniqAddrPos             = 0x0A,
        MCT3XX_UniqAddrLen             =    6,
        MCT3XX_GroupAddrBronzePos      = 0x10,
        MCT3XX_GroupAddrBronzeLen      =    1,
        MCT3XX_GroupAddrLeadPos        = 0x11,
        MCT3XX_GroupAddrLeadLen        =    3,
        MCT3XX_GroupAddrGoldSilverPos  = 0x14,
        MCT3XX_GroupAddrGoldSilverLen  =    1,

        MCT310_Rollover                = 100000,   //  5 digits
        MCT310_MaxPulseCount           = 10000000,
    };

    static  DOUBLE translateStatusValue( INT PointOffset, INT PointType, INT DeviceType, PUSHORT DataValueArray );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetValueKWH         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePeak        ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigOptions    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeScanLoadProfile     ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodePutConfigPeakMode   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

    enum
    {
        MCT310_StatusConnected         = 0x00,
        MCT310_StatusConnectArmed      = 0x40,
        MCT310_StatusConnectInProgress = 0x80,
        MCT310_StatusDisconnected      = 0xc0
    };

    CtiDeviceMCT310( );
    CtiDeviceMCT310( const CtiDeviceMCT310 &aRef );
    virtual ~CtiDeviceMCT310( );

    CtiDeviceMCT310 &operator=( const CtiDeviceMCT310 &aRef );

    //  virtual so that the MCT318 can override them
    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    void decodeAccumulators( ULONG *result, INT accum_cnt, BYTE *Data );
    void decodeStati( INT &stat, INT which, BYTE *Data );
};
#endif // #ifndef __DEV_MCT310_H__
