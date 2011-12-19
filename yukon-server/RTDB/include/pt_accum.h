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

   CtiPointAccumulator(const CtiPointAccumulator& aRef) :
   _pointHistory(NULL)
   {
      *this = aRef;
   }

   virtual ~CtiPointAccumulator()
   {
      if(_pointHistory != NULL && _pointHistory->isDirty())
      {
         if( ! _pointHistory->Update() )
         {
            if( ! _pointHistory->Insert() )     // Maybe it doesn't exist... Try this then bail
            {

               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
                  dout << "**** ERROR **** Unable to insert dynamic accumulator data for " << getName() << std::endl;
                  dout << "     ERROR **** " << FO(__FILE__) << " (" << __LINE__ << ")" <<std::endl;
               }
            }
         }
      }
      if( _pointHistory )
      {
          delete _pointHistory;
          _pointHistory = 0;
      }

   }


   CtiPointAccumulator& operator=(const CtiPointAccumulator& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         _pointAccumulator = aRef.getPointAccum();

         if(_pointHistory != NULL)
         {
            delete _pointHistory;
         }
         _pointHistory = NULL;
      }

      return *this;
   }

   CtiTablePointAccumulator  getPointAccum() const       { return _pointAccumulator;}
   CtiTablePointAccumulator& getPointAccum()             { return _pointAccumulator;}

   CtiTablePointAccumulatorHistory& getPointHistory()
   {
      validatePointHistory();
      return *_pointHistory;
   }

   virtual DOUBLE       getMultiplier() const         { return _pointAccumulator.getMultiplier();}
   virtual DOUBLE       getDataOffset() const         { return _pointAccumulator.getDataOffset();}

   virtual void         setMultiplier(DOUBLE d)       { _pointAccumulator.setMultiplier(d);}
   virtual void         setDataOffset(DOUBLE d)       { _pointAccumulator.setDataOffset(d);}

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
       //if(isA(rdr))
       {
          Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
          if(getDebugLevel() & DEBUGLEVEL_DATABASE)
          {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << "Decoding " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
          }

          _pointAccumulator.DecodeDatabaseReader(rdr);
       }
       /*else
       {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << getName() << " cannot decode this rdr " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
            }
       }*/
   }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      _pointAccumulator.dump();
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
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** MEMORY ERROR **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
            }
         }
      }
   }

};


class IM_EX_PNTDB Test_CtiPointAccumulator : public CtiPointAccumulator
{
public:
    void setPointOffset( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setID         ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID   ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }
};


typedef boost::shared_ptr< CtiPointAccumulator > CtiPointAccumulatorSPtr;
