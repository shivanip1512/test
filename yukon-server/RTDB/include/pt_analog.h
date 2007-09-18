/*-----------------------------------------------------------------------------*
*
* File:   pt_analog
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_analog.h-arc  $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2007/09/18 14:31:32 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_ANALOG_H__
#define __PT_ANALOG_H__
#pragma warning( disable : 4786)

#include <limits.h>
#include <float.h>

#include "dlldefs.h"
#include "pt_numeric.h"
#include "tbl_pt_analog.h"
#include "logger.h"


class IM_EX_PNTDB CtiPointAnalog : public CtiPointNumeric
{
private:

   CtiTablePointAnalog     _pointAnalog;

   friend class Test_CtiPointAnalog;

public:

   typedef CtiPointNumeric Inherited;

   CtiPointAnalog() {}

   CtiPointAnalog(const CtiPointAnalog& aRef)
   {
      *this = aRef;
   }

   CtiPointAnalog& operator=(const CtiPointAnalog& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         _pointAnalog      = aRef.getPointAnalog();
      }

      return *this;
   }

   CtiTablePointAnalog     getPointAnalog() const      { return _pointAnalog; }
   CtiTablePointAnalog&    getPointAnalog()            { return _pointAnalog; }

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTablePointAnalog::getSQL(db, keyTable, selector);
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      if(getDebugLevel() & DEBUGLEVEL_DATABASE)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      Inherited::DecodeDatabaseReader(rdr);
      _pointAnalog.DecodeDatabaseReader(rdr);
   }

   UINT adjustStaticTags(UINT &tag) const
   {
      if(getType() == AnalogOutputPointType) { tag |= TAG_ATTRIB_CONTROL_AVAILABLE; }
      return Inherited::adjustStaticTags(tag);
   }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      _pointAnalog.dump();
   }

   DOUBLE               getDeadband() const           { return _pointAnalog.getDeadband(); }

   virtual DOUBLE       getMultiplier() const         { return _pointAnalog.getMultiplier(); }
   virtual DOUBLE       getDataOffset() const         { return _pointAnalog.getDataOffset(); }

   virtual void         setMultiplier(DOUBLE d)       { _pointAnalog.setMultiplier(d); }
   virtual void         setDataOffset(DOUBLE d)       { _pointAnalog.setDataOffset(d); }

};


class IM_EX_PNTDB Test_CtiPointAnalog : public CtiPointAnalog
{
public:
    void setPointOffset( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setID         ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID   ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }
};

typedef CtiPointAnalog CtiPointAnalogOutput;//Someday CtiPointAnalogOutput may be its own class

typedef shared_ptr< CtiPointAnalog > CtiPointAnalogSPtr;

#endif // #ifndef __PT_ANALOG_H__

