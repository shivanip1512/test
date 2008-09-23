
#pragma warning( disable : 4786)
#ifndef __FDRPIPOLL_H__
#define __FDRPIPOLL_H__

/**
 *
 * File:   fdrpipoll
 *
 * Class:
 * Date:   1/11/2005
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive:     $
 *    REVISION     :  $Revision: 1.6 $
 *    DATE         :  $Date: 2008/09/23 15:15:22 $
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *    History:
 */

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include <rw/db/status.h>
#include <vector>
#include <map>

#include "dlldefs.h"
#include "fdrpibase.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"
#include "device.h"             // get the raw states

class IM_EX_FDRPIBASEAPI CtiFDRPiPoll : public CtiFDRPiBase
{
  typedef struct {
    vector<PiPointId> pointList;
    vector<PiPointInfo> infoList;
    vector<float> rvalList;
    vector<int32> istatList;
    vector<int32> timeList;
    vector<int32> errorList;
    time_t nextUpdate;

  } PollInfo;

  typedef map<unsigned int, PollInfo> PollDataList;

public:

  // constructors and destructors
  CtiFDRPiPoll();
  virtual ~CtiFDRPiPoll();


protected:

  virtual void cleanupTranslationPoint(CtiFDRPointSPtr translationPoint, bool recvList);
  void processNewPiPoint(PiPointInfoStruct &info);
  void removeAllPoints();

  void handleNewPoints();
  void handleNewPoint(CtiFDRPointSPtr ctiPoint);

  void doUpdates();

  virtual void readThisConfig();

  PollDataList _pollData;

private:
  bool _alwaysSendValues;
  int  _defaultPeriod;
  static const CHAR * KEY_ALWAYS_SEND;
  static const CHAR * KEY_DEFAULT_PERIOD;
};

#endif // #ifndef __FDRPIPOLL_H__
