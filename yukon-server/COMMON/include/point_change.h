#ifndef __POINT_CHANGE_H__
#define __POINT_CHANGE_H__

#include <rw\rwtime.h>
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
   RWTime            PointChangeTime;     // Time the point changed

public:

   CtiPointChange() :
      PointNumber(0),
      PointValue(0.0),
      PointType(StatusPointType),
      PointQuality(QuestionableQuality)
   {}

   CtiPointChange(unsigned ptid, double ptval, PointQuality_t ptq, INT ptype = InvalidPointType, RWTime now = RWTime()) :
      PointNumber(ptid),
      PointValue(ptval),
      PointQuality(ptq),
      PointType(ptype),
      PointChangeTime(now)
   {}

   CtiPointChange(const CtiPointChange &aRef) :
      PointNumber(aRef.getPointNumber()),
      PointValue(aRef.getPointValue()),
      PointQuality(aRef.getPointQuality()),
      PointType(aRef.getPointType()),
      PointChangeTime(aRef.getPointChangeTime())
   {}

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


   INT            getPointType() const                   { return PointType; }
   LONG           getPointNumber() const                 { return PointNumber; }
   double         getPointValue() const                  { return PointValue;  }
   PointQuality_t getPointQuality() const                { return PointQuality;}
   RWTime         getPointChangeTime() const             { return PointChangeTime; }
   RWTime         UpdatePointChangeTime()
   {
      PointChangeTime = PointChangeTime.now();
      return PointChangeTime;
   }

   RWTime&        getPointChangeTime()                   { return PointChangeTime; }

   void           setPointNumber(LONG id)                { PointNumber = id; }
   void           setPointType(INT  i)                   { PointType = i;}
   void           setPointValue(double val)              { PointValue = val; }
   void           setPointQuality(PointQuality_t q)      { PointQuality = q; }
   void           setPointChangeTime(RWTime rt)          { PointChangeTime = rt; }

   void           Dump();
};

#endif //#ifndef __POINT_CHANGE_H__
