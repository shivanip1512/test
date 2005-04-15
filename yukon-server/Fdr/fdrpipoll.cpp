#include "yukon.h"

/**
 * File:   fdrpipoll
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.2 $
 * DATE         :  $Date: 2005/04/15 15:34:41 $
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
#include "fdrpipoll.h"

const CHAR * CtiFDRPiPoll::KEY_ALWAYS_SEND              = "FDR_PI_ALWAYS_SEND";
const CHAR * CtiFDRPiPoll::KEY_DEFAULT_PERIOD           = "FDR_PI_DEFAULT_PERIOD";


/**
 * Constructor.
 */ 
CtiFDRPiPoll::CtiFDRPiPoll()
{
  readThisConfig();
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
void CtiFDRPiPoll::beginNewPoints()
{
  _pollData.clear();
}


/**
 * Notification of new point.
 */ 
void CtiFDRPiPoll::processNewPiPoint(PiPointInfoStruct &info) 
{

  RWCString periodStr = info.ctiPoint->getDestinationList()[0].getTranslationValue("Period (sec)");
  info.period = atoi(periodStr);
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

/**
 * Stop receiving new point notifications.
 */ 
void CtiFDRPiPoll::endNewPoints()
{
  // loop through all the points we were notified of in the processNewPiPoint call
  for (PollDataList::iterator myIter =  _pollData.begin();
       myIter != _pollData.end();
       ++myIter)
  {

    PollInfo &pollInfo = (*myIter).second;
    int pollPeriod = (*myIter).first;

    time_t now;
    time(&now);
    struct tm *now_local = localtime(&now);

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
        " points will update every " << pollPeriod << " seconds" << endl;
    }

  }

}

/**
 * Get values of all points in any over due polling period.
 */ 
void CtiFDRPiPoll::doUpdates()
{
  time_t now;
  time(&now);

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
          << " points for period " << pollPeriod << endl;
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
            << getPiErrorDescription(err, "pisn_getsnapshots") << endl;
        }
        throw PiException(err);
      }
    
      for (int i = 0; i < pointCount; ++i)
      {
        // remove local offset (might not be thread-safe)
        time_t timeStamp = mktime(gmtime(&timeArray[i]));
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

  RWCString   tempStr;

  tempStr = iConfigParameters.getValueAsString( KEY_ALWAYS_SEND, "FALSE" );
  _alwaysSendValues = tempStr.contains("TRUE", RWCString::ignoreCase);

  _defaultPeriod = iConfigParameters.getValueAsInt( KEY_DEFAULT_PERIOD, 60);


  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "---------------FDRPiPoll Configs-------------------------------" << endl;
    dout << KEY_ALWAYS_SEND << ": " << _alwaysSendValues << endl;
    dout << KEY_DEFAULT_PERIOD << ": " << _defaultPeriod << endl;
    dout << endl;
  }
}













