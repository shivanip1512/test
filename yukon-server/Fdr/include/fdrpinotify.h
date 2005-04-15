
#pragma warning( disable : 4786)
#ifndef __FDRPINOTIFY_H__
#define __FDRPINOTIFY_H__

/**
 *
 * File:   fdrpinotify
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

class IM_EX_FDRPIBASEAPI CtiFDRPiNotify : public CtiFDRPiBase
{

  typedef multimap<PiPointId, PiPointInfo> PiPointMap;

public:

  // constructors and destructors
  CtiFDRPiNotify();
  virtual ~CtiFDRPiNotify();


protected:

  void processNewPiPoint(PiPointInfoStruct &info);
  void beginNewPoints();
  void endNewPoints();

  void unregisterPoints();

  void doUpdates();

private:
  PiPointMap _pointMap;
  vector<PiPointId> _registerList;

  vector<PiPointId> _pointList;
  vector<float> _rvalList;
  vector<int32> _istatList;
  vector<int32> _timeList;

};

#endif // #ifndef __FDRPINOTIFY_H__
