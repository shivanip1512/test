#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   fdrpinotify
*
* Date:   1/11/2005
*
* Author: Tom Mack
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/02/14 16:38:41 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>
#include <rw/db/db.h>
#include <rw/db/connect.h>
#include <rw/db/status.h>

#include "piapi.h"
#include "piapix.h"

// this class header
#include "fdrpinotify.h"


//=================================================================================================================================
// Constructor
//=================================================================================================================================
CtiFDRPiNotify::CtiFDRPiNotify()
{
}

//=================================================================================================================================
// Destructor
//=================================================================================================================================
CtiFDRPiNotify::~CtiFDRPiNotify()
{
  unregisterPoints();
}

void CtiFDRPiNotify::beginNewPoints() {
  unregisterPoints();
  pointMap.clear();
}

void CtiFDRPiNotify::endNewPoints() {
  // register for all points in the pointMap
  for (PiPointMap::const_iterator myIter = pointMap.begin();
        myIter != pointMap.end();
        ++myIter) {
    const PiPointId &piId = (*myIter).first;
    registerList.push_back(piId);
  
  }
  int32 count = registerList.size();
  int32 initial_count = count;
  PiPointId *piIdArray = &registerList[0];
  int err = pisn_evmestablish(&count, piIdArray);
  if (err != 0 || count != initial_count) {
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to registered for " << 
        (initial_count - count) << " of " << initial_count << 
        " point notications from Pi, pisn_evmestablish returned " << 
        getPiErrorDescription(err, "pisn_evmestablish") << endl;
    }
  }
  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL ) {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    dout << RWTime::now() << " FDRPI: Registered for " << count << 
      " points with Pi " << endl;
  }
}

void CtiFDRPiNotify::unregisterPoints() {
  if (isConnected()) {
    // unregister for currently registered points
    int32 count = registerList.size();
    if (count > 0) {
      int32 initial_count = count;
      PiPointId *piIdArray = &registerList[0];
      int err = pisn_evmdisestablish(&count, piIdArray);
      if (err != 0 || count != initial_count) {
        if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ) {
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          dout << RWTime::now() << " FDRPI: Unable to unregistered for " << 
            (initial_count - count) << " of " << initial_count << 
            " point notications from Pi, pisn_evmdisestablish returned " << 
            getPiErrorDescription(err, "pisn_evmdisestablish") << endl;
        }
      }
    }
  } else {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to unregistered for point notifications from Pi (not connected)" << endl;
    }
  }
  registerList.clear();
}

void CtiFDRPiNotify::processNewPoint(PiPointInfo &info) 
{
  pointMap.insert(PiPointMap::value_type(info.piPointId, info));
}

void CtiFDRPiNotify::updatePiValues()
{
  const int32 points_at_a_time = 500;

  // After this has been called once, memory won't be reallocated 
  // and the following calls will be "cheap."
  pointList.reserve(points_at_a_time);
  rvalList.reserve(points_at_a_time);
  istatList.reserve(points_at_a_time);
  timeList.reserve(points_at_a_time);

  // vectors are guaranteed to have contiguous memory
  PiPointId *piIdArray = &pointList[0];                                                                       
  float *rvalArray = &rvalList[0];
  int32 *istatArray = &istatList[0];
  int32 *timeArray = &timeList[0];
  
  int32 pointCount = points_at_a_time;
  // loop until the number of points returned is less than number requested
  while (points_at_a_time == pointCount) {
    int err = pisn_evmexceptions(&pointCount, piIdArray, rvalArray, istatArray, timeArray);
    if (err != 0) {
      if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ) {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        dout << RWTime::now() << " FDRPI: Unable to update values from Pi, pisn_evmexceptions returned " << 
          getPiErrorDescription(err, "pisn_evmexceptions") << endl;
      }
      throw PiException(err);
    }
    if( (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL && pointCount > 0)
        || (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Received " << pointCount << " exceptions from Pi" << endl;
    }
    for (int i = 0; i < pointCount; ++i) {
      PiPointId thisPoint = piIdArray[i];

      // Find all entries that match this Pi Point (probably one, but multiple points could 
      // be linked to a single Pi Point).
      pair<PiPointMap::const_iterator,PiPointMap::const_iterator> result = pointMap.equal_range(thisPoint);

      for (PiPointMap::const_iterator myIter = result.first;
            myIter != result.second;
            ++myIter) {
        const PiPointInfo &info = (*myIter).second;

        // remove local offset (might not be thread-safe)
        time_t timeStamp = mktime(gmtime(&timeArray[i]));
        // pisn_evmesceptions doesn't return error codes per point, default to 0
        handleUpdate(info, rvalArray[i], istatArray[i], timeStamp, 0);

      }
    }
  }

}













