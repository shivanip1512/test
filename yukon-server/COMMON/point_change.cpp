#include "yukon.h"
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

// Stupid compiler!
