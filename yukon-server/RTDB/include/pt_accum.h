#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "dlldefs.h"
#include "logger.h"
#include "pt_numeric.h"
#include "tbl_pt_accum.h"
#include "tbl_pt_accumhistory.h"

class IM_EX_PNTDB CtiPointAccumulator : public CtiPointNumeric
{
   CtiTablePointAccumulator         _pointAccumulator;
   CtiTablePointAccumulatorHistory  *_pointHistory;

   typedef     CtiPointNumeric    Inherited;

public:

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
         _pointHistory = new CtiTablePointAccumulatorHistory( getPointID() );

         if(!_pointHistory->Restore() )
         {
            _pointHistory->Insert();
         }
      }
   }

};


typedef boost::shared_ptr< CtiPointAccumulator > CtiPointAccumulatorSPtr;
