#pragma once

#include "pt_base.h"
#include "yukon.h"
#include "dlldefs.h"
#include "tbl_pt_status.h"
#include "string_utility.h"

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

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
   {
      std::string rwsTemp;
      Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!

      if(getDebugLevel() & DEBUGLEVEL_DATABASE)
      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Decoding " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
      }
      _pointStatus.DecodeDatabaseReader(rdr);
   }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      _pointStatus.dump();
   }
};
