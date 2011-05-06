#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
#include <vector>
#include <list>
#include <map>

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrsimplebase.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"

#include "livedata_rtp_api.h"

class IM_EX_FDRLIVEDATAAPI CtiFDRLiveData : public CtiFDRSimple
{
public:
  // typedefs
  typedef CtiFDRInterface Inherited;
  typedef unsigned long LDAddress;

  struct PointInfo {
    LDAddress pointAddress;
    CtiFDRPoint         *ctiPoint;
    unsigned int         period; // in seconds
    //unsigned int         length;
    LiveDataTypes::Base    *liveDataType;
  };

  typedef multimap<LDAddress, PointInfo> PointMap;

protected:
  // callback classes
  class LiveDataWriteCallback : public WriteCallback
  {
  public:
    LiveDataWriteCallback(CtiFDRLiveData* that);
    ~LiveDataWriteCallback();
    bool write(unsigned long address, unsigned long length, void * buffer);

  protected:
    CtiFDRLiveData  *fdrInterface;
    int count;

  };

  friend LiveDataWriteCallback;


public:

  // constructors and destructors
  CtiFDRLiveData();
  virtual ~CtiFDRLiveData();


protected:

  string    _serverIpAddress;
  unsigned short _serverPort;

  struct PollInfo {
    vector<LDAddress> addressList;
    vector<PointInfo> infoList;
    vector<unsigned long> lengthList;
    time_t nextUpdate;
    unsigned int totalLength;
  };

  typedef map<unsigned int, PollInfo> PollDataList;
  PollDataList _pollData;

  void startup();
  bool connect();
  bool testConnection();
  void shutdown();

  virtual void doUpdates();

  void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);
  virtual void processNewPoint(CtiFDRPointSPtr ctiPoint);
  virtual void removeAllPoints();
  virtual void handleNewPoints();
  virtual void handleNewPoint(CtiFDRPointSPtr ctiPoint){};

  void invalidatePendingRequests();

  virtual void readThisConfig();

  LiveDataApi             *_liveDataConnection;
  LiveDataWriteCallback   *_writeCallback;


private:

  bool        _connected;
  bool        _inited;
  bool        _regFlag;
  long        _linkStatusId;
  bool        _okayToWrite;

  PointMap    _pointMap;

  static const CHAR * KEY_SERVER_IP_ADDRESS;
  static const CHAR * KEY_SERVER_PORT;


};
