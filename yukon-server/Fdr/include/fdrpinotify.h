
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
 *    REVISION     :  $Revision: 1.9.2.2 $
 *    DATE         :  $Date: 2008/11/18 20:11:30 $
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *    History:
 */


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
#include <rw/db/status.h>
#include <vector>
#include <map>

#include "dlldefs.h"
#include "fdrpibase.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"

class IM_EX_FDRPIBASEAPI CtiFDRPiNotify : public CtiFDRPiBase
{

  typedef multimap<PiPointId, PiPointInfo> PiPointMap;

public:

  // constructors and destructors
  CtiFDRPiNotify();
  virtual ~CtiFDRPiNotify();


protected:

  void processNewPiPoint(PiPointInfoStruct &info);
  virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

  void removeAllPoints();
  void handleNewPoints();
  void handleNewPoint(CtiFDRPointSPtr ctiPoint);

  void unregisterAllPoints();
  void unregisterPoint(PiPointId& pid);

  void doUpdates();
  void forceUpdateAllPoints();

private:
  PiPointMap _pointMap;
  vector<PiPointId> _registerList;

  vector<PiPointId> _pointList;
  vector<float> _rvalList;
  vector<int32> _istatList;
  vector<int32> _timeList;

};

#endif // #ifndef __FDRPINOTIFY_H__
