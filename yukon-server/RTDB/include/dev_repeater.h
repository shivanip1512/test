#ifndef __DEV_REPEATER_H__
#define __DEV_REPEATER_H__

#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/25 20:10:57 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\cstring.h>

#include "dev_dlcbase.h"

class IM_EX_DEVDB CtiDeviceRepeater900 : public CtiDeviceDLCBase
{

protected:

   enum
   {
       Rpt_StatusAddr   = 0x00,
       Rpt_StatusLen    =    3,
       Rpt_RoleBaseAddr = 0x00,
       Rpt_RoleLen      =    2,
       Rpt_ModelAddr    = 0x7f,
       Rpt_ModelLen     =    3
   };

private:

   static CTICMDSET _commandStore;

public:

   CtiDeviceRepeater900();

   CtiDeviceRepeater900(const CtiDeviceRepeater900& aRef);

   virtual ~CtiDeviceRepeater900();

   CtiDeviceRepeater900& operator=(const CtiDeviceRepeater900& aRef);

   INT CtiDeviceRepeater900::getSSpec() const;

   static bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   INT ExecuteRequest(CtiRequestMsg                  *pReq,
                      CtiCommandParser               &parse,
                      OUTMESS                        *&OutMessage,
                      RWTPtrSlist< CtiMessage >      &vgList,
                      RWTPtrSlist< CtiMessage >      &retList,
                      RWTPtrSlist< OUTMESS >         &outList);

   INT GeneralScan(CtiRequestMsg              *pReq,
                   CtiCommandParser           &parse,
                   OUTMESS                   *&OutMessage,
                   RWTPtrSlist< CtiMessage >  &vgList,
                   RWTPtrSlist< CtiMessage >  &retList,
                   RWTPtrSlist< OUTMESS >     &outList,
                   INT                         ScanPriority);

   INT executeLoopback(CtiRequestMsg                  *pReq,
                       CtiCommandParser               &parse,
                       OUTMESS                        *&OutMessage,
                       RWTPtrSlist< CtiMessage >      &vgList,
                       RWTPtrSlist< CtiMessage >      &retList,
                       RWTPtrSlist< OUTMESS >         &outList);

   INT executeGetConfig(CtiRequestMsg                  *pReq,
                        CtiCommandParser               &parse,
                        OUTMESS                        *&OutMessage,
                        RWTPtrSlist< CtiMessage >      &vgList,
                        RWTPtrSlist< CtiMessage >      &retList,
                        RWTPtrSlist< OUTMESS >         &outList);

   INT executePutConfig(CtiRequestMsg                  *pReq,
                        CtiCommandParser               &parse,
                        OUTMESS                        *&OutMessage,
                        RWTPtrSlist< CtiMessage >      &vgList,
                        RWTPtrSlist< CtiMessage >      &retList,
                        RWTPtrSlist< OUTMESS >         &outList);

   INT executeGetValue(CtiRequestMsg                  *pReq,
                       CtiCommandParser               &parse,
                       OUTMESS                        *&OutMessage,
                       RWTPtrSlist< CtiMessage >      &vgList,
                       RWTPtrSlist< CtiMessage >      &retList,
                       RWTPtrSlist< OUTMESS >         &outList);

   INT executePutValue(CtiRequestMsg                  *pReq,
                       CtiCommandParser               &parse,
                       OUTMESS                        *&OutMessage,
                       RWTPtrSlist< CtiMessage >      &vgList,
                       RWTPtrSlist< CtiMessage >      &retList,
                       RWTPtrSlist< OUTMESS >         &outList);

   virtual INT ResultDecode(INMESS*InMessage,
                            RWTime &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist<OUTMESS> &outList);

   INT decodeLoopback(INMESS *InMessage,
                      RWTime &TimeNow,
                      RWTPtrSlist< CtiMessage > &vgList,
                      RWTPtrSlist< CtiMessage > &retList,
                      RWTPtrSlist< OUTMESS > &outList);

   INT decodeGetConfigModel(INMESS *InMessage,
                            RWTime &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS > &outList);

   INT decodeGetConfigRole(INMESS *InMessage,
                           RWTime &TimeNow,
                           RWTPtrSlist< CtiMessage > &vgList,
                           RWTPtrSlist< CtiMessage > &retList,
                           RWTPtrSlist< OUTMESS > &outList);
   INT decodePutConfigRole(INMESS *InMessage,
                           RWTime &TimeNow,
                           RWTPtrSlist< CtiMessage > &vgList,
                           RWTPtrSlist< CtiMessage > &retList,
                           RWTPtrSlist< OUTMESS > &outList);

};

#endif // #ifndef __DEV_REPEATER_H__
