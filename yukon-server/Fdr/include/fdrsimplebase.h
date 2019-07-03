#pragma once

#include <windows.h>
#include <vector>
#include <map>

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"

class IM_EX_FDRBASE CtiFDRSimple : public CtiFDRInterface
{
  typedef CtiFDRInterface Inherited;

public:
  DEBUG_INSTRUMENTATION;

  // constructors and destructors
  CtiFDRSimple(std::string interfaceName);
  virtual ~CtiFDRSimple();

  void sendMessageToForeignSys( CtiMessage *aMessage ) override;
  virtual int processMessageFromForeignSystem( char *data );

  virtual BOOL run();
  virtual BOOL stop();

  bool loadTranslationLists();

protected:

  virtual BOOL init();

  virtual void startup() {};
  virtual bool connect() = 0;
  virtual void setConnected( bool conn );
  virtual bool needsConnection() {return !_connected;};
  virtual bool isConnected() {return _connected;};
  virtual bool testConnection() = 0;
  virtual void shutdown() {};

  virtual void removeAllPoints() {};
  virtual void processNewPoint(CtiFDRPointSPtr ctiPoint)=0;
  virtual void handleNewPoint(CtiFDRPointSPtr ctiPoint)=0;
  virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList);
  virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)=0;
  virtual void handleNewPoints(){};

  virtual void doUpdates() = 0;

  void handleUpdate(CtiFDRPoint *ctiPoint,
                    const double value,
                    const time_t timestamp,
                    const PointQuality_t quality = NormalQuality);

  void handleNonUpdate(CtiFDRPoint *ctiPoint,
                       const time_t timestamp);

  bool isDebugLevel (long debugLevel) {return getDebugLevel() & debugLevel;};

  virtual void readThisConfig();


public:
  class FdrException : public std::exception {
  public:
    FdrException(int err) : std::exception("FdrException") {}
    FdrException() : std::exception("FdrException") {}
  };

private:

  Cti::WorkerThread _threadGetData;

  bool        _connected;
  long        _linkStatusId;

  void sendLinkState(bool state);
  void threadFunctionGetData();

};
