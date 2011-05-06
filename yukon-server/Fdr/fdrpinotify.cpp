#include "yukon.h"

#include <math.h>
#include <stdlib.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include "ctitime.h"
#include "ctidate.h"

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
  unregisterAllPoints();
}

/**
 * Begin receiving new point notifications.
 */
void CtiFDRPiNotify::removeAllPoints()
{
  unregisterAllPoints();
  _pointMap.clear();
}

/**
 * Setup receiving for all point notifications.
 */
void CtiFDRPiNotify::handleNewPoints()
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
      logNow() << "Unable to register for "
        << (initial_count - count) << " of " << initial_count
        << " point notications from Pi, pisn_evmestablish returned "
        << getPiErrorDescription(err, "pisn_evmestablish") << endl;
  }

  forceUpdateAllPoints();

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Registered for " << count
      << " points with Pi " << endl;
  }
}

/**
 * Setup receiving for all point notifications.
 */
void CtiFDRPiNotify::handleNewPoint(CtiFDRPointSPtr ctiPoint)
{
  // we're interested in the first (and only) destination
  string tagName = ctiPoint->getDestinationList()[0].getTranslationValue("Tag Name");

  PiPointId pid;
  int err = getPiPointIdFromTag(tagName,pid);
  if (err == 0)
  {
    {
      std::string piError = getPiErrorDescription(err, "pipt_findpoint");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to find PI tag '" << tagName <<
        "' for point " << ctiPoint->getPointID() <<
        ", pipt_findpoint returned " << piError << endl;
    }
    return;
  }

  _registerList.push_back(pid);

  int32 count = 1;
  int32 initial_count = count;

  PiPointId *piIdArray = &pid;
  err = pisn_evmestablish(&count, piIdArray);
  if (err != 0 || count != initial_count)
  {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to register for point id "
        << pid
        << "with Pi, pisn_evmestablish returned "
        << getPiErrorDescription(err, "pisn_evmestablish") << endl;
  }

  //excessive since its only one point?
  forceUpdateAllPoints();

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Registered for " << count << " points with Pi " << endl;
  }
}

/**
 * Send message to Pi to unregister for point notifications.
 */
void CtiFDRPiNotify::unregisterAllPoints()
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
 * Send message to Pi to unregister for point notification.
 */
void CtiFDRPiNotify::unregisterPoint(PiPointId& pid)
{
  if (isConnected())
  {
    // unregister for currently registered points
    int32 count = 1;
    int32 initial_count = count;
    PiPointId *piIdArray = &pid;
    int err = pisn_evmdisestablish(&count, piIdArray);
    if (err != 0 || count != initial_count)
    {
      if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Unable to unregister for point notications from Pi point "
                 << pid
                 << ", pisn_evmdisestablish returned "
                 << getPiErrorDescription(err, "pisn_evmdisestablish")
                 << endl;
      }
    }
  }
  else
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to unregister for point notification from Pi (not connected)" << endl;
    }
  }
  //Removing from register list
  for (vector<PiPointId>::iterator itr = _registerList.begin(); itr != _registerList.end(); itr++)
  {
    if(*itr == pid)
    {
      _registerList.erase(itr);
      break;
    }
  }
}

/**
 * Notification of new point.
 */
void CtiFDRPiNotify::processNewPiPoint(PiPointInfo &info)
{
  _pointMap.insert(PiPointMap::value_type(info.piPointId, info));
}

void CtiFDRPiNotify::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
  string tagName = translationPoint->getDestinationList()[0].getTranslationValue("Tag Name");

  PiPointId piId;
  int err = getPiPointIdFromTag(tagName,piId);
  if (err != 0)
  {
    //if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "pipt_findpoint");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to find PI tag '" << tagName <<
        "' for point " << translationPoint->getPointID() <<
        ", pipt_findpoint returned " << piError << endl;
    }
    return;
  }

  _pointMap.erase(piId);

  unregisterPoint(piId);
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
    _pointList.resize(points_at_a_time);
    _rvalList.resize(points_at_a_time);
    _istatList.resize(points_at_a_time);
    _timeList.resize(points_at_a_time);

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
          struct tm *temp = NULL;
          time_t tTime = timeArray[i];
          temp = CtiTime::gmtime_r(&tTime);
          time_t timeStamp = mktime(temp);
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

  // Check to see if we're connected to the right database? 
  // This function call happens very frequently. Seems like a good
  // place to attempt to connect to the right database if we aren't already.
  if(_currentNodeIndex != 0 && _serverNodeNames.size() > 0 )
  {
    attemptPrimaryReconnection();
  }
}

/**
 * Get the current value of all of the points.
 */
void CtiFDRPiNotify::forceUpdateAllPoints()
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
    rvalList.resize(pointCount);
    float *rvalArray = &rvalList[0];

    vector<int32> istatList;
    istatList.resize(pointCount);
    int32 *istatArray = &istatList[0];

    vector<int32> timeList;
    timeList.resize(pointCount);
    int32 *timeArray = &timeList[0];

    vector<int32> errorList;
    errorList.resize(pointCount);
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
      time_t tTime = timeArray[i];
      time_t timeToSend = mktime(std::gmtime(&tTime) );

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
    }
  }
}










