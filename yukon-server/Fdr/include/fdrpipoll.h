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
  typedef struct {
    std::vector<PiPointId> pointList;
    std::vector<PiPointInfo> infoList;
    std::vector<float> rvalList;
    std::vector<int32> istatList;
    std::vector<int32> timeList;
    std::vector<int32> errorList;
    time_t nextUpdate;

  } PollInfo;

  typedef std::map<unsigned int, PollInfo> PollDataList;

public:

  // constructors and destructors
  CtiFDRPiPoll();
  virtual ~CtiFDRPiPoll();


protected:

  virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);
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
