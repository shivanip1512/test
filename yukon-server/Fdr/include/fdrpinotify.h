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

  typedef std::multimap<PiPointId, PiPointInfo> PiPointMap;

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
  std::vector<PiPointId> _registerList;

  std::vector<PiPointId> _pointList;
  std::vector<float>     _rvalList;
  std::vector<int32>     _istatList;
  std::vector<int32>     _timeList;

};
