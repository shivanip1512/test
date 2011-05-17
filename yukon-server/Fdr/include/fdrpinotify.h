#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
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
