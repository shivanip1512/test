#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "dlldefs.h"
#include "logger.h"
#include "pt_numeric.h"
#include "tbl_pt_accum.h"
#include "tbl_pt_accumhistory.h"
#include "string_utility.h"

class IM_EX_PNTDB CtiPointAccumulator : public CtiPointNumeric
{
private:

   friend class Test_CtiPointAccumulator;

   CtiTablePointAccumulator         _pointAccumulator;
   CtiTablePointAccumulatorHistory  *_pointHistory;

public:

   typedef     CtiPointNumeric    Inherited;

   CtiPointAccumulator() :
   _pointHistory(NULL)
   {}

   virtual ~CtiPointAccumulator()
   {
      if(_pointHistory != NULL && _pointHistory->isDirty())
      {
         if( ! _pointHistory->Update() )
         {
            if( ! _pointHistory->Insert() )     // Maybe it doesn't exist... Try this then bail
            {
                CTILOG_ERROR(dout, "Unable to insert dynamic accumulator data for "<< getName());
            }
         }
      }
      if( _pointHistory )
      {
          delete _pointHistory;
          _pointHistory = 0;
      }

   }

   const CtiTablePointAccumulator &getPointAccum() const  { return _pointAccumulator; }

   CtiTablePointAccumulatorHistory& getPointHistory()
   {
      validatePointHistory();
      return *_pointHistory;
   }

   virtual DOUBLE       getMultiplier() const         { return _pointAccumulator.getMultiplier();}
   virtual DOUBLE       getDataOffset() const         { return _pointAccumulator.getDataOffset();}

   static std::string getSQLCoreStatement()
   {
      static const std::string sql =
         "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
           "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
           "PT.archiveinterval, PTU.uomid, PTU.decimalplaces, PTU.decimaldigits, UTM.calctype, "
           "PAC.multiplier, PAC.dataoffset "
         "FROM Point PT, PointUnit PTU, UnitMeasure UTM, PointAccumulator PAC "
         "WHERE PT.pointid = PTU.pointid AND PTU.uomid = UTM.uomid AND PT.pointid = PAC.pointid";

      return sql;
   }

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
   {
       Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

       if(getDebugLevel() & DEBUGLEVEL_DATABASE)
       {
           CTILOG_DEBUG(dout, "Decoding DB reader");
       }

       _pointAccumulator.DecodeDatabaseReader(rdr);
   }

   void validatePointHistory()
   {
      if(_pointHistory == NULL)
      {
         _pointHistory = CTIDBG_new CtiTablePointAccumulatorHistory( getPointID() );
         if(_pointHistory != NULL)
         {
            if(!_pointHistory->Restore() )
            {
               _pointHistory->Insert();
            }
         }
         else
         {
            CTILOG_ERROR(dout, "Unexpected _pointHistory is NULL");
         }
      }
   }

};


struct IM_EX_PNTDB Test_CtiPointAccumulator : public CtiPointAccumulator
{
    void setPointOffset( int  offset   )     {  _pointBase.setPointOffset(offset);   }
    void setID         ( long id       )     {  _pointBase.setID(id);                }
    void setDeviceID   ( long deviceid )     {  _pointBase.setPAObjectID(deviceid);  }
    void setName       ( std::string name )  {  _pointBase.setName(name);  }
    double computeValueForUOM( double value ) const  {  return value;  }
};


typedef boost::shared_ptr< CtiPointAccumulator > CtiPointAccumulatorSPtr;
