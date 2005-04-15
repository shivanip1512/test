
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
 *    REVISION     :  $Revision: 1.2 $
 *    DATE         :  $Date: 2005/04/15 15:34:41 $
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *    History:
 */

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>
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

  void processNewPiPoint(PiPointInfoStruct &info);
  void beginNewPoints();
  void endNewPoints();
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
