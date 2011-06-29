#include "yukon.h"

#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

#include "piapi.h"
#include "piapix.h"

// this class header
#include "fdrpipoll.h"

const CHAR * CtiFDRPiPoll::KEY_ALWAYS_SEND              = "FDR_PI_ALWAYS_SEND";
const CHAR * CtiFDRPiPoll::KEY_DEFAULT_PERIOD           = "FDR_PI_DEFAULT_PERIOD";


/**
 * Constructor.
 */
CtiFDRPiPoll::CtiFDRPiPoll()
{
}

/**
 * Destructor.
 */
CtiFDRPiPoll::~CtiFDRPiPoll()
{
}

/**
 * Begin receiving new point notifications.
 */
void CtiFDRPiPoll::removeAllPoints()
{
  _pollData.clear();
}


/**
 * Notification of new point.
 */
void CtiFDRPiPoll::processNewPiPoint(PiPointInfoStruct &info)
{

  string periodStr = info.ctiPoint->getDestinationList()[0].getTranslationValue("Period (sec)");
  info.period = atoi(periodStr.c_str());
  if (info.period <= 0)
  {
    info.period = _defaultPeriod;
  }

  unsigned int period = info.period;
  _pollData[period].pointList.push_back(info.piPointId);
  _pollData[period].infoList.push_back(info);
  _pollData[period].rvalList.push_back(0);
  _pollData[period].istatList.push_back(0);
  _pollData[period].timeList.push_back(0);
  _pollData[period].errorList.push_back(0);

}

void CtiFDRPiPoll::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
  string periodStr = translationPoint->getDestinationList()[0].getTranslationValue("Period (sec)");

  unsigned int period = atoi(periodStr.c_str());

  if (period <= 0)
  {
    period = _defaultPeriod;
  }

  string tagName = translationPoint->getDestinationList()[0].getTranslationValue("Tag Name");

  PiPointId piId;
  int err = getPiPointIdFromTag(tagName,piId);
  if (err != 0)
  {
    {
      std::string piError = getPiErrorDescription(err, "pipt_findpoint");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to find PI tag '" << tagName <<
        "' for point " << translationPoint->getPointID() <<
        ", pipt_findpoint returned " << piError << Cti::endl;
    }
    return;
  }

  vector<PiPointId>::iterator pointListItr = _pollData[period].pointList.begin();
  for ( ; pointListItr != _pollData[period].pointList.end(); pointListItr++)
  {
    if (*pointListItr == piId)
    {
      _pollData[period].pointList.erase(pointListItr);
      break;
    }
  }

  vector<PiPointInfo>::iterator infoListItr = _pollData[period].infoList.begin();
  for ( ; infoListItr != _pollData[period].infoList.end(); infoListItr++)
  {
    if ((*infoListItr).piPointId == piId)
    {
      _pollData[period].infoList.erase(infoListItr);
      break;
    }
  }

  _pollData[period].rvalList.pop_back();
  _pollData[period].istatList.pop_back();
  _pollData[period].timeList.pop_back();
  _pollData[period].errorList.pop_back();

}

void CtiFDRPiPoll::handleNewPoint(CtiFDRPointSPtr ctiPoint)
{
  //would do a single version of this, but re updating all points is acceptable
  string periodStr = ctiPoint->getDestinationList()[0].getTranslationValue("Period (sec)");

  unsigned int period = atoi(periodStr.c_str());

  if (period <= 0)
  {
    period = _defaultPeriod;
  }

  //No good way to update the single point, so every point in the period is being updated.
  //Setting next update to one period ago for this single period.

  PollDataList::iterator itr = _pollData.find(period);

  if (itr != _pollData.end())
  {
    PollInfo &pollInfo = (*itr).second;
    int pollPeriod = (*itr).first;

    time_t now;
    ::time(&now);
    struct tm *now_local = NULL;
    now_local = CtiTime::localtime_r(&now);

    int secondsPastHour;
    secondsPastHour = now_local->tm_min * 60;
    secondsPastHour += now_local->tm_sec;

    time_t topOfThisHour = now - secondsPastHour;
    if (pollPeriod < 60 * 60)
    {
      // Set next update to be one period ago, forces all points to get
      // updated immediately.
      int periodsToAdd = secondsPastHour / pollPeriod;
      pollInfo.nextUpdate = topOfThisHour + periodsToAdd * pollPeriod;
    }
    else
    {
      pollInfo.nextUpdate = topOfThisHour;
    }
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << pollInfo.pointList.size() <<
        " points will update every " << pollPeriod << " seconds" << Cti::endl;
    }
  }
  else
  {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Check Point. No points setup for the peroid " << period << ", expected at least 1." << Cti::endl;
    }
  }
}

/**
 * Setup receiving new point notifications.
 */
void CtiFDRPiPoll::handleNewPoints()
{
  // loop through all the points we were notified of in the processNewPiPoint call
  for (PollDataList::iterator myIter =  _pollData.begin();
       myIter != _pollData.end();
       ++myIter)
  {

    PollInfo &pollInfo = (*myIter).second;
    int pollPeriod = (*myIter).first;

    time_t now;
    ::time(&now);
    struct tm *now_local = NULL;
    now_local = CtiTime::localtime_r(&now);

    int secondsPastHour;
    secondsPastHour = now_local->tm_min * 60;
    secondsPastHour += now_local->tm_sec;

    time_t topOfThisHour = now - secondsPastHour;
    if (pollPeriod < 60 * 60)
    {
      // Set next update to be one period ago, forces all points to get
      // updated immediately.
      int periodsToAdd = secondsPastHour / pollPeriod;
      pollInfo.nextUpdate = topOfThisHour + periodsToAdd * pollPeriod;
    }
    else
    {
      pollInfo.nextUpdate = topOfThisHour;
    }
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << pollInfo.pointList.size() <<
        " points will update every " << pollPeriod << " seconds" << Cti::endl;
    }

  }

}

/**
 * Get values of all points in any over due polling period.
 */
void CtiFDRPiPoll::doUpdates()
{
  time_t now;
  ::time(&now);

  for (PollDataList::iterator myIter =  _pollData.begin();
       myIter != _pollData.end();
       ++myIter)
  {
    unsigned int pollPeriod = (*myIter).first;
    PollInfo &pollInfo = (*myIter).second;

    if (pollInfo.nextUpdate < now)
    {

      if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Checking " << pollInfo.pointList.size()
          << " points for period " << pollPeriod << Cti::endl;
      }

      int pointCount = pollInfo.pointList.size(); //all sizes should be identical

      // the following is supposed to be legit
      //    (http://www.parashift.com/c++-faq-lite/containers-and-templates.html#faq-34.3)
      PiPointId *piIdArray = &pollInfo.pointList[0];
      float *rvalArray = &pollInfo.rvalList[0];
      int32 *istatArray = &pollInfo.istatList[0];
      int32 *timeArray = &pollInfo.timeList[0];
      int32 *errorArray = &pollInfo.errorList[0];

      int err = pisn_getsnapshots(piIdArray, rvalArray, istatArray, timeArray, errorArray, pointCount);
      if (err != 0)
      {
        if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
        {
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          logNow() << "Unable to update values from Pi, pisn_getsnapshots returned "
            << getPiErrorDescription(err, "pisn_getsnapshots") << Cti::endl;
        }
        setConnected(false);
        throw PiException(err);
      }

      for (int i = 0; i < pointCount; ++i)
      {
        // remove local offset (might not be thread-safe)
        struct tm *temp = NULL;
        time_t tTime = timeArray[i];
        temp = CtiTime::gmtime_r(&tTime);
        time_t timeStamp = mktime(temp);
        // if 'alwaysSendValues' send the time we were supposed to poll, this has the
        // effect of always sending the values
        time_t timeToSend = _alwaysSendValues ? pollInfo.nextUpdate: timeStamp;
        handlePiUpdate(pollInfo.infoList[i], rvalArray[i], istatArray[i], timeToSend, errorArray[i]);
      }

      // calculate next update time
      // Should only need to go through loop once, but allows
      // for "catch-up" if thread gets stalled.
      do
      {
        pollInfo.nextUpdate += pollPeriod;
      }
      while (pollInfo.nextUpdate < now);

    }
  }
}

/**
 * Read the configuration file.
 */
void CtiFDRPiPoll::readThisConfig()
{
  CtiFDRPiBase::readThisConfig();

  string   tempStr;

  tempStr = gConfigParms.getValueAsString( KEY_ALWAYS_SEND, "FALSE" );
  _alwaysSendValues = findStringIgnoreCase(tempStr,"TRUE");

  _defaultPeriod = gConfigParms.getValueAsInt( KEY_DEFAULT_PERIOD, 60);


  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "---------------FDRPiPoll Configs-------------------------------" << endl;
    dout << KEY_ALWAYS_SEND << ": " << _alwaysSendValues << endl;
    dout << KEY_DEFAULT_PERIOD << ": " << _defaultPeriod << endl;
    dout << endl;
  }
}













