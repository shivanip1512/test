
#pragma warning( disable : 4786)
#ifndef __FDRPIBASE_H__
#define __FDRPIBASE_H__

/**
 *
 * File:   fdrpibase
 *
 * Class:
 * Date:   1/11/2005
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive:     $
 *    REVISION     :  $Revision: 1.8.2.2 $
 *    DATE         :  $Date: 2008/11/18 20:11:30 $
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *    History:
 */


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
#include <vector>
#include <map>

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
  typedef CtiFDRInterface Inherited;

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



public:

  // constructors and destructors
  CtiFDRPiBase();
  virtual ~CtiFDRPiBase();

  static CtiFDRPiBase* createInstance();


  //long getLinkStatusID( void ) const;
  //void setLinkStatusID( const long aPointID );

protected:

  string    _serverNodeName;
  string    _serverUsername;
  string    _serverPassword;

  bool connect();
  void testConnection();

  virtual void processNewPoint(CtiFDRPointSPtr ctiPoint);
  virtual void processNewPiPoint(PiPointInfoStruct &info) = 0;
  virtual void removeAllPoints() {};
  virtual void handleNewPoints() {};

  int getPiPointIdFromTag(const string& tagName, PiPointId& piId);

  void handlePiUpdate(const PiPointInfo info,
                    const float rval,
                    const int32 istat,
                    const time_t timestamp_utc,
                    const int32 errorcode);

  //ULONG getScanRateSeconds() const;
  //void  setScanRateSeconds( const ULONG mySeconds );

  std::string getPiErrorDescription(int errCode, char* functionName = "");

  virtual void readThisConfig();

public:
  class PiException : public exception {
  public:
    PiException(int err) : exception("PiException") {}
  };

private:

  bool        _connected;
  bool        _inited;
  bool        _regFlag;
  long        _linkStatusId;

  string   _appName;

  static const CHAR * KEY_FLAVOR;
  static const CHAR * KEY_DB_RELOAD_RATE;
  static const CHAR * KEY_DEBUG_MODE;
  static const CHAR * KEY_APPLICATION_NAME;
  static const CHAR * KEY_SERVER_NODE_NAME;
  static const CHAR * KEY_SERVER_USERNAME;
  static const CHAR * KEY_SERVER_PASSWORD;

  static const char PI_REAL_POINT;
  static const char PI_INTEGER_POINT;
  static const char PI_DIGITAL_POINT;

};

#endif // #ifndef __FDRPIBASE_H__
