/*-----------------------------------------------------------------------------*
*
* File:   dev_mct3XX
*
* Class:  CtiDeviceMCT3XX
* Date:   4/24/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct31X.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/03/13 19:36:13 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT3XX_H__
#define __DEV_MCT3XX_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT3XX : public CtiDeviceMCT
{
protected:

private:

   static CTICMDSET _commandStore;

public:

   typedef CtiDeviceMCT Inherited;

   CtiDeviceMCT3XX();

   CtiDeviceMCT3XX(const CtiDeviceMCT3XX& aRef);

   virtual ~CtiDeviceMCT3XX();

   CtiDeviceMCT3XX& operator=(const CtiDeviceMCT3XX& aRef);

   virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

   static bool initCommandStore();
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );
};
#endif // #ifndef __DEV_MCT3XX_H__
