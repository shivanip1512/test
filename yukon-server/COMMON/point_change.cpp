#include "precompiled.h"
#include <iostream>
using namespace std;

#include "point_change.h"

static char *PointQualityDescriptions[] = {
   "Normal Quality",
   "Questionable Quality ",
   "Unreasonable Quality ",
   "Invalid Quality"
};

void CtiPointChange::Dump()
{
   CtiLockGuard<CtiLogger> logger_guard(dout);

   dout << "Point Change for point #" << PointNumber << " PtType " << PointType << endl;
   dout << " " << PointChangeTime << endl;
   dout << " " << PointValue << " " << PointQualityDescriptions[PointQuality] << endl;
}

CtiPointChange() :
  PointNumber(0),
  PointValue(0.0),
  PointType(StatusPointType),
  PointQuality(QuestionableQuality)
{}

CtiPointChange(unsigned ptid, double ptval, PointQuality_t ptq, INT ptype = InvalidPointType, CtiTime now = CtiTime()) :
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

INT CtiPointChange::getPointType() const
{ return PointType; }
LONG CtiPointChange::getPointNumber() const
{ return PointNumber; }
double CtiPointChange::getPointValue() const
{ return PointValue;  }
PointQuality_t CtiPointChange::getPointQuality() const
{ return PointQuality;}

const CtiTime& CtiPointChange::getPointChangeTime() const
{
   return PointChangeTime;
}
const CtiTime& CtiPointChange::UpdatePointChangeTime()
{
  PointChangeTime = PointChangeTime.now();
  return PointChangeTime;
}


void CtiPointChange::setPointNumber(LONG id)
{ PointNumber = id; }
void CtiPointChange::setPointType(INT  i)
{ PointType = i;}
void CtiPointChange::setPointValue(double val)
{ PointValue = val; }
void CtiPointChange::setPointQuality(PointQuality_t q)
{ PointQuality = q; }
void CtiPointChange::setPointChangeTime(CtiTime rt)
{ PointChangeTime = rt; }


// Stupid compiler!
