#pragma once

#include <rw\thr\mutex.h>
#include "yukon.h"
#include "dlldefs.h"

IM_EX_CTIBASE extern RWMutexLock coutMux;

class IM_EX_CTIBASE CtiPointChange
{
private:
   LONG              PointNumber;
   double            PointValue;
   int               PointType;
   PointQuality_t    PointQuality;
   CtiTime            PointChangeTime;     // Time the point changed

public:

   CtiPointChange();
   CtiPointChange(unsigned ptid, double ptval, PointQuality_t ptq, INT ptype = InvalidPointType, CtiTime now = CtiTime());

   CtiPointChange(const CtiPointChange &aRef);
   CtiPointChange& operator=(const CtiPointChange &aRef)
   {
      if(this != &aRef)
      {
         PointNumber     = aRef.getPointNumber();
         PointValue      = aRef.getPointValue();
         PointQuality    = aRef.getPointQuality();
         PointChangeTime = aRef.getPointChangeTime();
         PointType       = aRef.getPointType();
      }
      return *this;
   }

   RWBoolean operator==(const CtiPointChange& c1)
   {
      return(PointNumber == c1.getPointNumber());
   }


   INT            getPointType() const;                   
   LONG           getPointNumber() const;                 
   double         getPointValue() const;                  
   PointQuality_t getPointQuality() const;                
   const CtiTime&         getPointChangeTime() const;     
   const CtiTime&         UpdatePointChangeTime();
   
   void           setPointNumber(LONG id);                
   void           setPointType(INT  i);                   
   void           setPointValue(double val);              
   void           setPointQuality(PointQuality_t q);      
   void           setPointChangeTime(CtiTime rt);         

   void           Dump();
};
