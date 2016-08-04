#include "precompiled.h"

#if !defined(_WINDLL)
#error _WINDLL is required!
#endif

#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

#include "piapi.h"
#include "piapix.h"

// this class header
#include "fdrpipoll.h"

using namespace std;

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
   _pollData[period].pointList.push_back(info);
   _pollData[period].pointIdList.push_back(info.piPointId);
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
      const std::string piError = getPiErrorDescription(err, "pipt_findpoint");

      CTILOG_ERROR(dout, logNow() <<"Unable to find PI tag '"<< tagName <<"' for point "<< translationPoint->getPointID() <<
              ", pipt_findpoint returned " << piError);

      return;
  }


  vector<PiPointId>::iterator pointIdItr = _pollData[period].pointIdList.begin();
  for ( ; pointIdItr != _pollData[period].pointIdList.end(); pointIdItr++)
  {
    if ((*pointIdItr) == piId)
    {
      _pollData[period].pointIdList.erase(pointIdItr);
      break;
    }
  }

  vector<PiPointInfo>::iterator pointListItr = _pollData[period].pointList.begin();
  for ( ; pointListItr != _pollData[period].pointList.end(); pointListItr++)
  {
    if ((*pointListItr).piPointId == piId)
    {
      _pollData[period].pointList.erase(pointListItr);
      break;
    }
  }

  if ( _pollData[period].pointList.size() == 0)
  {
    _pollData.erase(period);
  }


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

  PollDataMap::iterator itr = _pollData.find(period);

  if (itr != _pollData.end())
  {
    PiEventInfo &pollInfo = (*itr).second;
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
        CTILOG_DEBUG(dout, logNow() << pollInfo.pointList.size() <<" points will update every "<< pollPeriod <<" seconds");
    }
  }
  else
  {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
        //FIXME: DEBUG or ERROR?
        CTILOG_DEBUG(dout, logNow() << "No points setup for the period " << period << ", expected at least 1.");
    }
  }
}

/**
 * Setup receiving new point notifications.
 */
void CtiFDRPiPoll::handleNewPoints()
{
  // loop through all the points we were notified of in the processNewPiPoint call
  for (PollDataMap::iterator myIter =  _pollData.begin();
       myIter != _pollData.end();
       ++myIter)
  {

    PiEventInfo &pollInfo = (*myIter).second;
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
        CTILOG_DEBUG(dout, logNow() << pollInfo.pointList.size() <<
                " points will update every "<< pollPeriod <<" seconds");
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

    for (PollDataMap::iterator myIter =  _pollData.begin();
         myIter != _pollData.end();
         ++myIter)
    {
        unsigned int pollPeriod = (*myIter).first;
        PiEventInfo &pollInfo = (*myIter).second;
        int32 pointCount = pollInfo.pointList.size(); //all sizes should be identical

        if (pollInfo.nextUpdate < now && pointCount > 0)
        {
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
                CTILOG_DEBUG(dout, logNow() <<"Checking "<< pointCount <<" points for period "<< pollPeriod);
            }

            pollInfo.pointIdList.resize(pointCount);
            pollInfo.eventList.resize(pointCount);

            // the following is supposed to be legit
            //    (http://www.parashift.com/c++-faq-lite/containers-and-templates.html#faq-34.3)

            PiPointId* piIdArray = &pollInfo.pointIdList[0];
            PI_EVENT piEvent = pollInfo.eventList[0];
            int32 error;

            int err = pisn_getsnapshotsx(piIdArray, &pointCount, &piEvent.drval, &piEvent.ival, &piEvent.bval, &piEvent.bsize,
                                   &piEvent.istat, NULL, &piEvent.timestamp, &error, GETFIRST);

            if (!err)
            {
                int i = 0;
                do
                {
                    // if 'alwaysSendValues' send the time we were supposed to poll, this has the
                    // effect of always sending the values
                    time_t timeToSend = _alwaysSendValues ? pollInfo.nextUpdate: piToYukonTime(piEvent.timestamp);

                   /* handle results */
                    processPiPollResults(pollInfo.pointList[i], piEvent, error, timeToSend);
                    i++;
                    err = pisn_getsnapshotsx(piIdArray, &pointCount, &piEvent.drval, &piEvent.ival, &piEvent.bval, &piEvent.bsize,
                                                    &piEvent.istat, NULL, &piEvent.timestamp, &error, GETNEXT);
                } while (!err);
            }
            else
            {
                if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
                {
                    CTILOG_DEBUG(dout, logNow() <<"Unable to update values from Pi, pisn_getsnapshotsx returned "<<
                            getPiErrorDescription(err, "pisn_getsnapshotsx"));
                }
                setConnected(false);
                throw PiException(err);
            }
            // calculate next update time
            // Should only need to go through loop once, but allows
            // for "catch-up" if thread gets stalled.
            do
            {
              pollInfo.nextUpdate += pollPeriod;
            } while (pollInfo.nextUpdate < now);
        }
    }
}
void CtiFDRPiPoll::processPiPollResults(PiPointInfo info, PI_EVENT &piEvent, int32 error, time_t timeToSend)
{

    handlePiUpdate(info, piEvent.drval, piEvent.ival, piEvent.istat, timeToSend, error);

    piEvent.bsize = sizeof(piEvent.bval);
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
      Cti::FormattedList loglist;
      loglist.add(KEY_ALWAYS_SEND)    << _alwaysSendValues;
      loglist.add(KEY_DEFAULT_PERIOD) << _defaultPeriod;

      CTILOG_DEBUG(dout, "FDRPiPoll Configs:"<<
              loglist);
  }
}

