/*-----------------------------------------------------------------------------*
*
* File:   pt_accum
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_accum.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/10/12 20:14:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_ACCUM_H__
#define __PT_ACCUM_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>

#include "dlldefs.h"
#include "logger.h"
#include "pt_numeric.h"
#include "tbl_pt_accum.h"
#include "tbl_pt_accumhistory.h"

class IM_EX_PNTDB CtiPointAccumulator : public CtiPointNumeric
{
private:

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
         if(_pointHistory->Update().errorCode() != RWDBStatus::ok)
         {
            if(_pointHistory->Insert().errorCode() != RWDBStatus::ok)     // Maybe it doesn't exist... Try this then bail
            {

               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                  dout << "**** ERROR **** Unable to insert dynamic accumulator data for " << getName() << endl;
                  dout << "     ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTablePointAccumulator::getSQL(db, keyTable, selector);
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      _pointAccumulator.DecodeDatabaseReader(rdr);
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
            if(_pointHistory->Restore().errorCode() != RWDBStatus::ok )
            {
               _pointHistory->Insert();
            }
         }
         else
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " **** MEMORY ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
         }
      }
   }

};

#endif // #ifndef __PT_ACCUM_H__
