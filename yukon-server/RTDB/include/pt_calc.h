/*-----------------------------------------------------------------------------*
*
* File:   pt_calc
*
* Date:   20 Dec 2000 15:09:58
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_calc.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/04/15 19:02:51 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_CALC_H__
#define __PT_CALC_H__
#pragma warning( disable : 4786)


#include <rw/tpsrtvec.h>
#include <rw\cstring.h>
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
   RWCString   Operation;     // What is it that I am doing?

public:

   CtiPointCalculation(INT Ord = -1, LONG pid = -1, RWCString str = RWCString("NoOp") ) :
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
   RWCString      getOperation() const             { return Operation; }

   void           setOrder(INT i)                  { Order = i; }
   void           setPointID(LONG pid)             {PointID = pid; }
   void           setOperation(const RWCString s)  { Operation = s; }
};

class IM_EX_PNTDB CtiPointCalculated : public CtiPointNumeric
{
private:

   INT         UpdateFrequency;

   RWTPtrSortedVector<CtiPointCalculation, greater<CtiPointCalculation> > CalcVector;

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

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      INT iTemp;
      RWDBNullIndicator isNull;

      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

      #if 0 // 062800
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["updatefrequency"] >> UpdateFrequency;
      #endif
   }

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
       cout << " AAAAAAGHHHHH " << endl;
   }

   void DecodeCalcElementsDatabaseReader(RWDBReader &rdr)
   {
      INT iTemp;
      RWDBNullIndicator isNull;

      rdr["itemorder"] >> isNull;
      rdr["itemorder"] >> iTemp;

      if(!isNull)
      {
         LONG pid;
         RWCString Op;

         rdr["calcpointid"]   >> pid;
         rdr["operation"]     >> Op;

         // CtiPointCalculation(iTemp, pid, Op );        // FIX FIX FIX
      }
   }

   INT      getUpdateFrequency() const          { return UpdateFrequency; }
   void     setUpdateFrequency(INT i)           { UpdateFrequency = i; }

   // Just have at it ok!
   RWTPtrSortedVector<CtiPointCalculation, greater<CtiPointCalculation> >& getCalcVector() { return CalcVector; }

   virtual void DumpData()
   {
      Inherited::DumpData();       // get the base class handled
      cout << " Update Frequency                         : " << UpdateFrequency << endl;
   }



};


#endif // #ifndef __PT_CALC_H__

