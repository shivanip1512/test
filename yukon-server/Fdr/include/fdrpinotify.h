#pragma once

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
  void processPiEventResults(PiPointId piId, PI_EVENT &piEvent, int32 errors );
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

  PiEventInfo _notifyInfo;

};
