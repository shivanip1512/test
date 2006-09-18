/*-----------------------------------------------------------------------------*
*
* File:   dev_repeater
*
* Class:  CtiDeviceRepeater900
* Date:   8/27/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_repeater.h-arc  $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2006/09/18 17:25:09 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_REPEATER_H__
#define __DEV_REPEATER_H__
#pragma warning( disable : 4786)


#include "dev_dlcbase.h"

class IM_EX_DEVDB CtiDeviceRepeater900 : public CtiDeviceDLCBase
{
private:

   static const CommandSet _commandStore;
   static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        StatusPos   = 0x00,
        StatusLen   =    3,
        RoleBasePos = 0x00,
        RoleLen     =    2,
        ModelPos    = 0x7f,
        ModelLen    =    3
    };

    INT executeLoopback (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    INT decodeLoopback      (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodeGetConfigRole (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodePutConfigRole (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

public:

    CtiDeviceRepeater900();

    CtiDeviceRepeater900(const CtiDeviceRepeater900& aRef);

    virtual ~CtiDeviceRepeater900();

    CtiDeviceRepeater900& operator=(const CtiDeviceRepeater900& aRef);

    INT CtiDeviceRepeater900::getSSpec() const;

    INT GeneralScan   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority);

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

};

#endif // #ifndef __DEV_REPEATER_H__
