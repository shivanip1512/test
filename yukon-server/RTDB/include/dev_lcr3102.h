/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LCR3102_H__
#define __DEV_LCR3102_H__
#pragma warning( disable : 4786)

#include "dev_carrier.h"


namespace Cti       {
namespace Device    {

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
        FuncRead_PropCountLen    = 1
    };

    virtual bool getOperation( const UINT &cmdType, BSTRUCT &b ) const;

    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    int parseLoadValue(CtiCommandParser &parse);
    int parsePreviousValue(CtiCommandParser &parse);

    INT decodeGetValueIntervalLast( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueRuntime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueShedtime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePropCount( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

    LCR3102( );
    LCR3102( const LCR3102 &aRef );

    virtual ~LCR3102( );

    LCR3102& operator=( const LCR3102 &aRef );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore );

    INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

};

}       // namespace Device
}       // namespace Cti

#endif // #ifndef __DEV_LCR3102_H__
