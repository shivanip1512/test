#include "precompiled.h"

#if !defined(_WINDLL)
#error _WINDLL is required!
#endif

#include "ctitime.h"
#include "ctidate.h"

#include "piapi.h"
#include "piapix.h"

// this class header
#include "fdrpinotify.h"

using namespace std;

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
  if (count > 0)
  {
      PiPointId *piIdArray = &_registerList[0];
      int err = pisn_evmestablish(&count, piIdArray);
      if (err != 0 || count != initial_count)
      {
          CTILOG_ERROR(dout, logNow() <<"Unable to register for "<<
                  (initial_count - count) <<" of "<< initial_count <<
                  " point notications from Pi, pisn_evmestablish returned "<<
                  getPiErrorDescription(err, "pisn_evmestablish"));
      }

      forceUpdateAllPoints();
  }

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
      CTILOG_DEBUG(dout, logNow() << "Registered for "<< count <<" points with Pi");
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
  if (err != 0)
  {
      const std::string piError = getPiErrorDescription(err, "pipt_findpoint");

      CTILOG_ERROR(dout, logNow() << "Unable to find PI tag '"<< tagName <<
              "' for point "<< ctiPoint->getPointID() <<
              ", pipt_findpoint returned " << piError);

    return;
  }

  _registerList.push_back(pid);

  int32 count = 1;
  int32 initial_count = count;

  PiPointId *piIdArray = &pid;
  err = pisn_evmestablish(&count, piIdArray);
  if (err != 0 || count != initial_count)
  {
      CTILOG_ERROR(dout, logNow() <<"Unable to register for point id "<< pid <<
              " with Pi, pisn_evmestablish returned "<<
              getPiErrorDescription(err, "pisn_evmestablish"));
  }

  //excessive since its only one point?
  forceUpdateAllPoints();

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
      CTILOG_DEBUG(dout, logNow() <<"Registered for "<< count <<" points with Pi");
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
            CTILOG_DEBUG(dout, logNow() <<"Unable to unregistered for "<<
                    (initial_count - count) <<" of "<< initial_count <<
                    " point notications from Pi, pisn_evmdisestablish returned "<<
                    getPiErrorDescription(err, "pisn_evmdisestablish"));
        }
      }
    }
  }
  else
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
        CTILOG_DEBUG(dout, logNow() <<"Unable to unregistered for point notifications from Pi (not connected)");
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
          CTILOG_DEBUG(dout, logNow() <<"Unable to unregister for point notifications from Pi point "<< pid <<
                  ", pisn_evmdisestablish returned "<<
                  getPiErrorDescription(err, "pisn_evmdisestablish"));
      }
    }
  }
  else
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
        CTILOG_DEBUG(dout, logNow() <<"Unable to unregister for point notification from Pi (not connected)");
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
      const std::string piError = getPiErrorDescription(err, "pipt_findpoint");

      CTILOG_ERROR(dout, logNow() <<"Unable to find PI tag '"<< tagName <<
              "' for point "<< translationPoint->getPointID() <<
              ", pipt_findpoint returned "<< piError);

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
        _notifyInfo.pointList.resize(points_at_a_time);
        _notifyInfo.eventList.resize(points_at_a_time);

        // vectors are guaranteed to have contiguous memory
        PiPointId piId    = _notifyInfo.pointList[0].piPointId;
        PI_EVENT piEvent  = _notifyInfo.eventList[0];

        int32 pointCount = points_at_a_time;
        // loop until the number of points returned is less than number requested
        while (points_at_a_time == pointCount)
        {
            int err =  pisn_evmexceptionsx(&pointCount, &piId, &piEvent, GETFIRST);
            if( (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL && pointCount > 0)
                  || (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL))
            {
                CTILOG_DEBUG(dout, logNow() <<"Received "<< pointCount <<" exceptions from Pi");
            }
            while (err != PI_NOMOREVALUES)
            {
                if (err)
                {
                    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
                    {
                        CTILOG_DEBUG(dout, logNow() <<"Unable to update values from Pi, pisn_evmexceptions returned "<<
                                getPiErrorDescription(err, "pisn_evmexceptions"));
                    }

                    setConnected(false);
                    throw PiException(err);
                }

                processPiEventResults(piId, piEvent, 0);
                err =  pisn_evmexceptionsx(&pointCount, &piId, &piEvent, GETNEXT);

            };
        }
    }
    else
    {
      if( getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL )
      {
          CTILOG_DEBUG(dout, logNow() << "No check made, no points registered.");
      }
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
        CTILOG_DEBUG(dout, logNow() <<"Forcing update of "<< pointCount <<" points.");
    }
    PI_EVENT piEvent;
    int32 error;

    int err = pisn_getsnapshotsx(piIdArray, &pointCount, &piEvent.drval, &piEvent.ival, &piEvent.bval, &piEvent.bsize,
                                 &piEvent.istat, NULL, &piEvent.timestamp, &error, GETFIRST);
    if (!err)
    {
        int i = 0;
        do
        {
            processPiEventResults(piIdArray[i], piEvent, error);
            i++;
            err = pisn_getsnapshotsx(piIdArray, &pointCount, &piEvent.drval, &piEvent.ival, &piEvent.bval, &piEvent.bsize,
                                 &piEvent.istat, NULL, &piEvent.timestamp, &error, GETNEXT);
        } while (!err);
    }
    else
    {
      if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
      {
          CTILOG_DEBUG(dout, logNow() <<"Unable to update values from Pi, pisn_getsnapshotsx returned "<< getPiErrorDescription(err, "pisn_getsnapshotsx"));
      }
    }
  }
}

void CtiFDRPiNotify::processPiEventResults(PiPointId piId, PI_EVENT &piEvent, int32 error )
{
    // remove local offset (might not be thread-safe)
    time_t timeToSend = piToYukonTime(piEvent.timestamp);

    // Find all entries that match this Pi Point (probably one, but multiple points could
    // be linked to a single Pi Point).
    pair<PiPointMap::const_iterator,PiPointMap::const_iterator> result = _pointMap.equal_range(piId);

    for (PiPointMap::const_iterator myIter = result.first;
          myIter != result.second;
          ++myIter)
    {
        const PiPointInfo &info = (*myIter).second;

        // pisn_evmesceptions doesn't return error codes per point, default to 0
        handlePiUpdate(info, piEvent.drval, piEvent.ival, piEvent.istat, timeToSend, error);
    }
    piEvent.bsize = sizeof(piEvent.bval);
}









