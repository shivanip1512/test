#pragma warning( disable : 4786)

#ifndef __DEV_GRP_VCSERIAL_H__
#define __DEV_GRP_VCSERIAL_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_vcserial
*
* Class:  CtiDeviceGroupVersacomSerial
* Date:   5/9/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_grp_vcserial.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:51 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpslist.h>

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_lmvcserial.h"


class CtiDeviceGroupVersacomSerial : public CtiDeviceGroupBase
{
protected:

   CtiTableLMGroupVersacomSerial       VersacomSerialGroup;

private:

public:

   typedef CtiDeviceGroupBase Inherited;

   CtiDeviceGroupVersacomSerial();// {}

   CtiDeviceGroupVersacomSerial(const CtiDeviceGroupVersacomSerial& aRef);/*
   {
      *this = aRef;
   }                                                                        */

   virtual ~CtiDeviceGroupVersacomSerial();// {}

   CtiDeviceGroupVersacomSerial& operator=(const CtiDeviceGroupVersacomSerial& aRef);/*
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         VersacomSerialGroup = aRef.getVersacomSerialGroup();
      }
      return *this;                                                                    */
   CtiTableLMGroupVersacomSerial   getVersacomSerialGroup() const;//      { return VersacomSerialGroup; }
   CtiTableLMGroupVersacomSerial&  getVersacomSerialGroup();//            { return VersacomSerialGroup; }

   CtiDeviceGroupVersacomSerial&     setVersacomSerialGroup(const CtiTableLMGroupVersacomSerial& aRef);/*
   {
      VersacomSerialGroup = aRef;
      return *this;
   }                                                                                                     */

   virtual LONG getRouteID();
   virtual RWCString getDescription(const CtiCommandParser & parse) const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);/*
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTableLMGroupVersacomSerial::getSQL(db, keyTable, selector);
   }                                                                                   */

   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);/*
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      VersacomSerialGroup.DecodeDatabaseReader(rdr);
   }                                                    */

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);



};
#endif // #ifndef __DEV_GRP_VCSERIAL_H__
