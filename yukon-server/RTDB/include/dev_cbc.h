
#pragma warning( disable : 4786)
#ifndef __DEV_CBC_H__
#define __DEV_CBC_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc
*
* Class:  CtiDeviceCBC
* Date:   8/24/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:49 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_base.h"
#include "tbl_dv_cbc.h"       // TYPEVERSACOMCBC, TYPEFISHERPCBC

class IM_EX_DEVDB CtiDeviceCBC : public CtiDeviceBase
{
protected:

   CtiTableDeviceCBC _cbc;
   static int        _cbcTries;

private:

public:

   typedef CtiDeviceBase Inherited;

   CtiDeviceCBC();
   CtiDeviceCBC(const CtiDeviceCBC& aRef);
   virtual ~CtiDeviceCBC();

   CtiDeviceCBC& operator=(const CtiDeviceCBC& aRef);
   CtiTableDeviceCBC   getCBC() const;
   CtiTableDeviceCBC&  getCBC();
   CtiDeviceCBC&     setCBC(const CtiTableDeviceCBC& aRef);

   int getCBCRetries(void);

   virtual LONG getRouteID();
   virtual RWCString getDescription(const CtiCommandParser & parse) const;
   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);


   INT executeFisherPierceCBC(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   INT executeVersacomCBC(CtiRequestMsg                  *pReq,
                          CtiCommandParser               &parse,
                          OUTMESS                        *&OutMessage,
                          RWTPtrSlist< CtiMessage >      &vgList,
                          RWTPtrSlist< CtiMessage >      &retList,
                          RWTPtrSlist< OUTMESS >         &outList);


};


#endif // #ifndef __DEV_CBC_H__
