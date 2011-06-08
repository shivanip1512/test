#pragma once
#pragma warning( disable : 4786)

#include <limits.h>
#include <float.h>

#include "dlldefs.h"
#include "pt_numeric.h"
#include "tbl_pt_analog.h"
#include "logger.h"
#include "string_utility.h"


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

   static std::string getSQLCoreStatement()
   {
      static const std::string sql =  
         "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
            "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
            "PT.archiveinterval, UNT.uomid, UNT.decimalplaces, UNT.decimaldigits, UM.calctype, "
            "ALG.multiplier, ALG.dataoffset, ALG.deadband "
         "FROM Point PT, PointUnit UNT, UnitMeasure UM, PointAnalog ALG "
         "WHERE PT.pointid = UNT.pointid AND UNT.uomid = UM.uomid AND PT.pointid = ALG.pointid";

      return sql;
   }

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
   {
      if(getDebugLevel() & DEBUGLEVEL_DATABASE)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << "Decoding " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
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


