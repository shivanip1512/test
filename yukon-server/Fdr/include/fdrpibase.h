#pragma once

//osi header
#include "piapi.h"

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrsimplebase.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"


class IM_EX_FDRPIBASEAPI CtiFDRPiBase : public CtiFDRSimple
{
public:
  DEBUG_INSTRUMENTATION;

  CtiFDRPiBase();
  virtual ~CtiFDRPiBase();

  static CtiFDRPiBase* createInstance();

  class PiException : public std::exception {
  public:
    PiException(int err) : std::exception("PiException") {}
  };

protected:

  typedef long PiPointId;

  typedef struct PiPointInfoStruct {
    PiPointId piPointId;
    CtiFDRPoint *ctiPoint;
    //int nextUpdate; //need some timestamp type here
    char piType;
    unsigned int period; // in seconds
    int digitalOffset;
    int digitalLastIndex;

  } PiPointInfo;


  typedef struct {
    std::vector<PiPointInfo> pointList;
    std::vector<PiPointId> pointIdList;
    std::vector<PI_EVENT> eventList;

    time_t nextUpdate;

  }PiEventInfo;

  /**
   * The amount of connection failures that can occur before the
   * <code>connect()</code> function attempts to switch and
   * connect to a different node in the node list.
   */
  static const int FailureThreshold = 2;

  // The main loop in FDRSimpleBase occurs every 1.25 seconds, so
  // this will give us 30 seconds per primary node connection retry.
  static const int UpdatesPerRetry = 24;

  std::vector<std::string> _serverNodeNames;
  std::string _serverUsername;
  std::string _serverPassword;

  int _currentNodeIndex;
  int _connectionFailureCount;

  bool connect();
  bool testConnection();
  void setConnected( bool conn );
  bool needsConnection();
  bool isConnected() { return _connected; };

  virtual void processNewPoint(CtiFDRPointSPtr ctiPoint);
  virtual void processNewPiPoint(PiPointInfoStruct &info) = 0;
  virtual void removeAllPoints() {};
  virtual void handleNewPoints() {};

  std::string getCurrentNodeName();
  std::string getPrimaryNodeName();

  bool isCollectiveConnection();

  bool tryPrimaryConnection();
  bool trySecondaryConnection();

  bool serverNodeLogin();

  int getPiPointIdFromTag(const std::string& tagName, PiPointId& piId);

  void handlePiUpdate(const PiPointInfo info,
                    const float64 rval,
                    const int32 ival,
                    const int32 istat,
                    const time_t timestamp_utc,
                    const int32 errorcode);

  std::string getPiErrorDescription(int errCode, char* functionName = "");

  std::time_t piToYukonTime(PITIMESTAMP piTimeStamp);

  virtual void readThisConfig();

private:

  typedef CtiFDRInterface Inherited;

  bool        _connected;

  static const CHAR * KEY_FLAVOR;
  static const CHAR * KEY_DB_RELOAD_RATE;
  static const CHAR * KEY_APPLICATION_NAME;
  static const CHAR * KEY_SERVER_NODE_NAME;
  static const CHAR * KEY_SERVER_USERNAME;
  static const CHAR * KEY_SERVER_PASSWORD;

  static const char PI_REAL_POINT;
  static const char PI_INTEGER_POINT;
  static const char PI_DIGITAL_POINT;

};
