
#pragma warning( disable : 4786)
#ifndef __DEV_MARK_V_H__
#define __DEV_MARK_V_H__

/*---------------------------------------------------------------------------------*
*
* File:   dev_mark_v
*
* Class:  CtiDeviceMarkV
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/06 19:49:52 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_transdata.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "mgr_point.h"
#include "device.h"

class IM_EX_DEVDB CtiDeviceMarkV : public CtiDeviceMeter
{
protected:

private:

   CtiProtocolTransdata    _transdataProtocol;

public:

   typedef CtiDeviceMeter Inherited;

   CtiDeviceMarkV();

   CtiDeviceMarkV( const CtiDeviceMarkV& aRef );

   virtual ~CtiDeviceMarkV();
/*
   CtiDeviceMarkV& operator=(const CtiDeviceMarkV& aRef)
   {
     if(this != &aRef)
     {
     }
     return *this;
   }
*/

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ResultDecode(INMESS                    *InMessage,
                            RWTime                    &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS >    &outList);

   virtual INT ErrorDecode(INMESS                     *InMessage,
                           RWTime                     &TimeNow,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList);

   CtiProtocolTransdata & getProtocol( void );
//   void DecodeDatabaseReader( RWDBReader &rdr );


};

#endif // #ifndef __DEV_MARK_V_H__
