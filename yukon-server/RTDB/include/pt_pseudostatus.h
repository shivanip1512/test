/*-----------------------------------------------------------------------------*
*
* File:   pt_pseudostatus
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_pseudostatus.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/09/26 14:18:23 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_PSEUDOSTATUS_H__
#define __PT_PSEUDOSTATUS_H__
#pragma warning( disable : 4786)


#include "pt_base.h"
#include "yukon.h"
#include "dlldefs.h"
#include "tbl_pt_status.h"

class IM_EX_PNTDB CtiPointPseudoStatus : public CtiPointBase
{
protected:

   CtiTablePointStatus  _pointStatus;

public:

   typedef CtiPointBase    Inherited;

   CtiPointPseudoStatus( ) {}

   CtiPointPseudoStatus(const CtiPointPseudoStatus& aRef)
   {
      *this = aRef;
   }

   CtiPointPseudoStatus& operator=(const CtiPointPseudoStatus& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);

         _pointStatus   = aRef.getPointStatus();
      }

      return *this;
   }


   CtiTablePointStatus  getPointStatus() const     { return _pointStatus; }
   CtiTablePointStatus& getPointStatus()           { return _pointStatus; }

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTablePointStatus::getSQL(db, keyTable, selector);
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      string rwsTemp;
      Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!

      if(getDebugLevel() & DEBUGLEVEL_DATABASE)
      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
      _pointStatus.DecodeDatabaseReader(rdr);
   }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      _pointStatus.dump();
   }
};
#endif // #ifndef __PT_PSEUDOSTATUS_H__

