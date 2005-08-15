#include "yukon.h"

/**
 *
 * File:   fdrpinotify
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.3 $
 * DATE         :  $Date: 2005/08/15 19:04:08 $
 */

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


/**
 * Constructor.
 */ 
CtiFDRPiNotify::CtiFDRPiNotify()
{
}

/**
 * Destructor.
 */ 
CtiFDRPiNotify::~CtiFDRPiNotify()
{
  unregisterPoints();
}

/**
 * Begin receiving new point notifications.
 */ 
void CtiFDRPiNotify::beginNewPoints()
{
  unregisterPoints();
  _pointMap.clear();
}

/**
 * Stop receiving new point notifications.
 */ 
void CtiFDRPiNotify::endNewPoints()
{
  // register for all points in the pointMap
  for (PiPointMap::const_iterator myIter = _pointMap.begin();
        myIter != _pointMap.end();
        ++myIter)
  {
    const PiPointId &piId = (*myIter).first;
    _registerList.push_back(piId);
  
  }
  int32 count = _registerList.size();
  int32 initial_count = count;
  PiPointId *piIdArray = &_registerList[0];
  int err = pisn_evmestablish(&count, piIdArray);
  if (err != 0 || count != initial_count)
  {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to registered for "
        << (initial_count - count) << " of " << initial_count
        << " point notications from Pi, pisn_evmestablish returned "
        << getPiErrorDescription(err, "pisn_evmestablish") << endl;
  }

  forceUpdate();

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Registered for " << count 
      << " points with Pi " << endl;
  }
}

/**
 * Send message to Pi to unregister for point notifications.
 */ 
void CtiFDRPiNotify::unregisterPoints()
{
  if (isConnected())
  {
    // unregister for currently registered points
    int32 count = _registerList.size();
    if (count > 0)
    {
      int32 initial_count = count;
      PiPointId *piIdArray = &_registerList[0];
      int err = pisn_evmdisestablish(&count, piIdArray);
      if (err != 0 || count != initial_count) {
        if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
        {
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          logNow() << "Unable to unregistered for "
            << (initial_count - count) << " of " << initial_count
            << " point notications from Pi, pisn_evmdisestablish returned "
            << getPiErrorDescription(err, "pisn_evmdisestablish") << endl;
        }
      }
    }
  }
  else
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to unregistered for point notifications from Pi (not connected)" << endl;
    }
  }
  _registerList.clear();
}

/**
 * Notification of new point.
 */ 
void CtiFDRPiNotify::processNewPiPoint(PiPointInfo &info) 
{
  _pointMap.insert(PiPointMap::value_type(info.piPointId, info));
}

/**
 * Check for new updates for the registered points.
 */ 
void CtiFDRPiNotify::doUpdates()
{
  if (_registerList.size() > 0)
  {
    const int32 points_at_a_time = 500;
  
    // After this has been called once, memory won't be reallocated 
    // and the following calls will be "cheap."
    _pointList.reserve(points_at_a_time);
    _rvalList.reserve(points_at_a_time);
    _istatList.reserve(points_at_a_time);
    _timeList.reserve(points_at_a_time);
  
    // vectors are guaranteed to have contiguous memory
    PiPointId *piIdArray = &_pointList[0];                                                                       
    float *rvalArray = &_rvalList[0];
    int32 *istatArray = &_istatList[0];
    int32 *timeArray = &_timeList[0];
    
    int32 pointCount = points_at_a_time;
    // loop until the number of points returned is less than number requested
    while (points_at_a_time == pointCount)
    {
      int err = pisn_evmexceptions(&pointCount, piIdArray, rvalArray, istatArray, timeArray);
      if (err != 0)
      {
        if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
        {
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          logNow() << "Unable to update values from Pi, pisn_evmexceptions returned "
            << getPiErrorDescription(err, "pisn_evmexceptions") << endl;
        }
        throw PiException(err);
      }
      if( (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL && pointCount > 0)
          || (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL))
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Received " << pointCount << " exceptions from Pi" << endl;
      }
      for (int i = 0; i < pointCount; ++i)
      {
        PiPointId thisPoint = piIdArray[i];
  
        // Find all entries that match this Pi Point (probably one, but multiple points could 
        // be linked to a single Pi Point).
        pair<PiPointMap::const_iterator,PiPointMap::const_iterator> result = _pointMap.equal_range(thisPoint);
  
        for (PiPointMap::const_iterator myIter = result.first;
              myIter != result.second;
              ++myIter)
        {
          const PiPointInfo &info = (*myIter).second;
  
          // remove local offset (might not be thread-safe)
          time_t timeStamp = mktime(gmtime(&timeArray[i]));
          // pisn_evmesceptions doesn't return error codes per point, default to 0
          handlePiUpdate(info, rvalArray[i], istatArray[i], timeStamp, 0);
  
        }
      }
    }
  }
  else
  {
    if( getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "No check made, no points registered." << endl;
    }
  }
}

/**
 * Get the current value of all of the points.
 */ 
void CtiFDRPiNotify::forceUpdate()
{
  int32 pointCount = _registerList.size();
  if (pointCount > 0) {
    PiPointId *piIdArray = &_registerList[0];
  
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Forcing update of " << pointCount
        << " points." << endl;
    }
  
    vector<float> rvalList;
    rvalList.reserve(pointCount);
    float *rvalArray = &rvalList[0];

    vector<int32> istatList;
    istatList.reserve(pointCount);
    int32 *istatArray = &istatList[0];

    vector<int32> timeList;
    timeList.reserve(pointCount);
    int32 *timeArray = &timeList[0];

    vector<int32> errorList;
    errorList.reserve(pointCount);
    int32 *errorArray = &errorList[0];
  
    int err = pisn_getsnapshots(piIdArray, rvalArray, istatArray, timeArray, errorArray, pointCount);
    if (err != 0)
    {
      if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Unable to update values from Pi, pisn_getsnapshots returned "
          << getPiErrorDescription(err, "pisn_getsnapshots") << endl;
      }
    }
  
    for (int i = 0; i < pointCount; ++i)
    {
      // remove local offset (might not be thread-safe)
      time_t timeToSend = mktime(gmtime(&timeArray[i]));

      PiPointId thisPoint = piIdArray[i];

      // Find all entries that match this Pi Point (probably one, but multiple points could 
      // be linked to a single Pi Point).
      pair<PiPointMap::const_iterator,PiPointMap::const_iterator> result = _pointMap.equal_range(thisPoint);

      for (PiPointMap::const_iterator myIter = result.first;
            myIter != result.second;
            ++myIter)
      {
        const PiPointInfo &info = (*myIter).second;
        handlePiUpdate(info, rvalArray[i], istatArray[i], timeToSend, errorArray[i]);
      }

      //handlePiUpdate(infoList[i], rvalArray[i], istatArray[i], timeToSend, errorArray[i]);
    }
  }
}










