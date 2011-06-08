/*-----------------------------------------------------------------------------*
*
* File:   pt_calc
*
* Date:   20 Dec 2000 15:09:58
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_calc.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_CALC_H__
#define __PT_CALC_H__
#pragma warning( disable : 4786)


#include <rw/tpsrtvec.h>
#include "sorted_vector"
#include "pt_numeric.h"

/*-----------------------------------------------------------------------------*
 * This class is the guy stuffed in the orderd list in a CtiPointCalculated
 *  object
 *-----------------------------------------------------------------------------*/
class IM_EX_PNTDB CtiPointCalculation
{
private:

   INT         Order;         // Which calculation is this??
   LONG        PointID;       // Which _other_ point does this calc operate upon?
   std::string   Operation;     // What is it that I am doing?

public:

   CtiPointCalculation(INT Ord = -1, LONG pid = -1, std::string str = std::string("NoOp") ) :
      Order(Ord),
      PointID(pid),
      Operation(str)
   {}

   // Sorts calculation by their order number!
   BOOL operator>(const CtiPointCalculation& aRef)
   {
      return (Order > aRef.getOrder());
   }

   INT            getOrder() const                 { return Order; }
   LONG           getPointID() const               { return PointID; }
   string      getOperation() const             { return Operation; }

   void           setOrder(INT i)                  { Order = i; }
   void           setPointID(LONG pid)             {PointID = pid; }
   void           setOperation(const std::string s)  { Operation = s; }
};

class IM_EX_PNTDB CtiPointCalculated : public CtiPointNumeric
{
private:

   INT         UpdateFrequency;

   codeproject::sorted_vector<CtiPointCalculation*, false, greater<CtiPointCalculation> > CalcVector;
public:

   typedef     CtiPointNumeric    Inherited;

   CtiPointCalculated()
   {}

   CtiPointCalculated(const CtiPointCalculated& aRef)
   {
      *this = aRef;
   }

   CtiPointCalculated& operator=(const CtiPointCalculated& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);

      }

      return *this;
   }

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
   {
      //if(isA(rdr))
      {
          Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
      }
      /*else
      {
           {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " cannot decode this rdr " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
           }
      }*/
   }

   INT      getUpdateFrequency() const          { return UpdateFrequency; }
   void     setUpdateFrequency(INT i)           { UpdateFrequency = i; }

   // Just have at it ok!
   codeproject::sorted_vector<CtiPointCalculation*, false, greater<CtiPointCalculation> >& getCalcVector() { return CalcVector; }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      cout << " Update Frequency                         : " << UpdateFrequency << std::endl;
   }



};


#endif // #ifndef __PT_CALC_H__

