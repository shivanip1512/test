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

class IM_EX_FDRPIBASEAPI CtiFDRPiPoll : public CtiFDRPiBase
{
private:  


  typedef std::map<unsigned int, PiEventInfo> PollDataMap;

public:
  DEBUG_INSTRUMENTATION;

  // constructors and destructors
  CtiFDRPiPoll();
  virtual ~CtiFDRPiPoll();


protected:

  virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);
  void processNewPiPoint(PiPointInfoStruct &info);
  void removeAllPoints();
  void processPiPollResults(PiPointInfo piInfo, PI_EVENT &piEvent, int32 error, time_t timeToSend);

  void handleNewPoints();
  void handleNewPoint(CtiFDRPointSPtr ctiPoint);

  void doUpdates();

  virtual void readThisConfig();

  PollDataMap _pollData;

private:
  bool _alwaysSendValues;
  int  _defaultPeriod;
  static const CHAR * KEY_ALWAYS_SEND;
  static const CHAR * KEY_DEFAULT_PERIOD;
};
