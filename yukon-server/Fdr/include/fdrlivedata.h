#ifndef __FDRLIVEDATA_H__
#define __FDRLIVEDATA_H__

/**
 *
 * File:   fdrlivedata
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 *    REVISION     :  $Revision: 1.1 $
 *    DATE         :  $Date: 2005/04/15 15:34:41 $
 *    History:
 *      $History:$
 */

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/db/status.h>
#include <vector>
#include <list>
#include <map>

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrsimplebase.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"
#include "device.h"             // get the raw states

#include "livedata_rtp_api.h"
//#include "iccp_types.h"


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
  void testConnection();
  void shutdown();

  virtual void doUpdates();

  virtual void processNewPoint(CtiFDRPoint *ctiPoint);
  virtual void beginNewPoints();
  virtual void endNewPoints();

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


#endif // #ifndef __FDRLIVEDATA_H__
