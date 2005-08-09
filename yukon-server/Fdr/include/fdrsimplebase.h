
#pragma warning( disable : 4786)
#ifndef __FDRSIMPLE_H__
#define __FDRSIMPLE_H__

/**
 *
 * File:   fdrsimple
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Class:
 * Date:   1/11/2005
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive:     $
 *    REVISION     :  $Revision: 1.2 $
 *    DATE         :  $Date: 2005/08/09 22:36:02 $
 */

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/db/status.h>
#include <vector>
#include <map>

//osi header
#include "piapi.h"

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"
#include "device.h"             // get the raw states


class IM_EX_FDRBASE CtiFDRSimple : public CtiFDRInterface
{
  typedef CtiFDRInterface Inherited;


public:

  // constructors and destructors
  CtiFDRSimple(string interfaceName);
  virtual ~CtiFDRSimple();

  virtual bool sendMessageToForeignSys( CtiMessage *aMessage );
  virtual int processMessageFromForeignSystem( char *data );

  virtual BOOL run();
  virtual BOOL stop();

  bool loadTranslationLists();


  //long getLinkStatusID( void ) const;
  //void setLinkStatusID( const long aPointID );

protected:

  virtual BOOL init();

  virtual void startup() {};
  virtual bool connect() = 0;
  virtual void setConnected( bool conn );
  virtual bool isConnected() {return _connected;};
  virtual void testConnection() = 0;
  virtual void shutdown() {};

  virtual void beginNewPoints() {};
  virtual void processNewPoint(CtiFDRPoint *ctiPoint) = 0;
  virtual void endNewPoints() {};

  virtual void doUpdates() = 0;

  void handleUpdate(CtiFDRPoint *ctiPoint, 
                    const double value, 
                    const time_t timestamp,
                    const PointQuality_t quality = NormalQuality);

  void handleNonUpdate(CtiFDRPoint *ctiPoint, 
                       const time_t timestamp);

  ostream logNow();
  bool isDebugLevel (long debugLevel) {return getDebugLevel() & debugLevel;};

  virtual void readThisConfig();


public:
  class FdrException : public exception {
  public:
    FdrException(int err) : exception("FdrException") {}
    FdrException() : exception("FdrException") {}
  };


private:

  RWThreadFunction  _threadGetData;
  bool        _connected;
  bool        _inited;
  long        _linkStatusId;

  void sendLinkState(bool state);
  void threadFunctionGetData();

};

#endif // #ifndef __FDRSIMPLE_H__
