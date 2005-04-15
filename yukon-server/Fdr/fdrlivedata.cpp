#include "yukon.h"

/**
 *
 * File:   fdrlivedata
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Date:   1/11/2005
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.1 $
 * DATE         :  $Date: 2005/04/15 15:34:40 $
 */

#include <windows.h>
#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>
#include <rw/db/db.h>
#include <rw/db/connect.h>
#include <rw/db/status.h>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "fdr.h"
#include "fdrdebuglevel.h"

#include "livedatatypes.h"

// this class header
#include "fdrlivedata.h"

/** global used to start the interface by c functions **/
CtiFDRLiveData * myInterface;

const CHAR * CtiFDRLiveData::KEY_SERVER_IP_ADDRESS   = "FDR_LIVEDATA_SERVER_IP_ADDRESS";
const CHAR * CtiFDRLiveData::KEY_SERVER_PORT         = "FDR_LIVEDATA_SERVER_SERVER_PORT";




/**
 * Default Constructor.
 */ 
CtiFDRLiveData::CtiFDRLiveData() : CtiFDRSimple( "LIVEDATA" )
{
  readThisConfig();
  _okayToWrite = false;
  _liveDataConnection = new LiveDataApi();
  _writeCallback = new LiveDataWriteCallback(this);
}

/**
 * Destructor.
 */ 
CtiFDRLiveData::~CtiFDRLiveData()
{
  delete _liveDataConnection;
  delete _writeCallback;
}

/**
 * Initialize connection
 */ 
void CtiFDRLiveData::startup()
{

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Starting LiveData API; connecting to " << _serverIpAddress << endl;
  }

  _liveDataConnection->setWriteHandler(_writeCallback);
  _liveDataConnection->initialize(_serverIpAddress, _serverPort);

}

/**
 * Shutdown connection
 */ 
void CtiFDRLiveData::shutdown()
{
  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Shutting down LiveData API"  << endl;
  }

  _okayToWrite = false;
  _liveDataConnection->exit(); 

}

/**
 * Open a connection to the remote system.
 */ 
bool CtiFDRLiveData::connect()
{

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Checking LiveData server connection." << endl;
  }

  testConnection();

  return isConnected();
}

/**
 * Open a connection to the remote system.
 */ 
void CtiFDRLiveData::testConnection()
{
  setConnected( _liveDataConnection->isConnected() );
}


/**
 * Add a new point to the appropriate lists.
 */ 
void CtiFDRLiveData::processNewPoint(CtiFDRPoint *ctiPoint)
{
  PointInfo info;
  info.ctiPoint = ctiPoint;

  LiveDataTypes::Factory *typeFactory = LiveDataTypes::Factory::getInstance();


  // we're interested in the first (and only) destination
  RWCString address = ctiPoint->getDestinationList()[0].getTranslationValue("Address");
  info.pointAddress = atoi(address);

  RWCString dataTypeStr = info.ctiPoint->getDestinationList()[0].getTranslationValue("Data Type");

  try
  {
    info.liveDataType = typeFactory->getDataType(dataTypeStr.data());
  
  
    _pointMap.insert(PointMap::value_type(info.pointAddress, info));
  
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Added point " << address << " of type " << dataTypeStr << endl;
    }
  
  }
  catch (std::exception& e)
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Unkown point type " << dataTypeStr << " for address " << address << endl;
  }


}

/**
 * Clear out data about existing points.
 */ 
void CtiFDRLiveData::beginNewPoints() {
  _okayToWrite = false;
  _pointMap.clear();
}

/**
 * Set next update time for the new points.
 */ 
void CtiFDRLiveData::endNewPoints()
{
  _okayToWrite = true;
}

/**
 * Check if any points need to be updated.
 */ 
void CtiFDRLiveData::doUpdates()
{
  testConnection();
  if ( !_liveDataConnection->isConnected() )
  {
    throw FdrException();
  }
}

CtiFDRLiveData::LiveDataWriteCallback::LiveDataWriteCallback(CtiFDRLiveData* that)
{
  fdrInterface = that;
  count = 1;
}

CtiFDRLiveData::LiveDataWriteCallback::~LiveDataWriteCallback()
{
}

bool CtiFDRLiveData::LiveDataWriteCallback::write(unsigned long address, unsigned long length, void * buffer)
{
  bool success = false;

  CtiFDRPointList &aList = fdrInterface->getReceiveFromList();
  CtiLockGuard<CtiMutex> guard(aList.getMutex());  

  if (fdrInterface->_okayToWrite)
  {
    time_t updateTime;
    time(&updateTime);
  
    
  
    pair<PointMap::const_iterator,PointMap::const_iterator> foundRangePair = fdrInterface->_pointMap.equal_range(address);

    if (foundRangePair.first == foundRangePair.second && fdrInterface->isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      fdrInterface->logNow() << "Got data for unknown address " << address << endl;
    }
    else if ( fdrInterface->isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      fdrInterface->logNow() << "Got write for address " << address << endl;
    }
  
    for (PointMap::const_iterator infoIter = foundRangePair.first;
          infoIter != foundRangePair.second;
          ++infoIter) {
      const PointInfo &info = (*infoIter).second;
  
      if (info.liveDataType->getSize() == length)
      {
        if (info.liveDataType->hasTimestamp())
        {
          updateTime = info.liveDataType->getTimestamp((char*)buffer);
        }
          
        fdrInterface->handleUpdate(info.ctiPoint,
                                   info.liveDataType->getData((char*)buffer),
                                   updateTime,
                                   info.liveDataType->getQuality((char*)buffer));
        success = true;
      }
      else
      {
        if( fdrInterface->isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
        {
          unsigned int expectedLength = info.liveDataType->getSize();
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          fdrInterface->logNow() << "Got bad length for address " << address 
            << ", expected " << expectedLength << " got " << length << endl;
        }
      }
      
    }
  }
  else
  {
    if( fdrInterface->isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      fdrInterface->logNow() << "Throwing out write for address " << address << endl;
    }
  }

  return success;
}


/**
 * Read our configuration file.
 */ 
void CtiFDRLiveData::readThisConfig()
{
  CtiFDRSimple::readThisConfig();


  _serverIpAddress = (const char*)iConfigParameters.getValueAsString( KEY_SERVER_IP_ADDRESS, "127.0.0.1" );
  _serverPort = atoi( iConfigParameters.getValueAsString( KEY_SERVER_PORT, "2000" ) );


  if( isDebugLevel( STARTUP_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "----------------FDRLiveData Configs----------------------------" << endl;
    dout << "  " << KEY_SERVER_IP_ADDRESS << ": " << _serverIpAddress << endl;
    dout << "  " << KEY_SERVER_PORT << ": " << _serverPort << endl;
    dout << endl;
  }

}


/*
 *  Here Starts some C functions that are used to Start the
 *  Interface and Stop it from the Main() of FDR.EXE.
 *
 */

extern "C" {

  /**
   * This is used to Start the Interface from the Main()
   * of FDR.EXE. Each interface it Dynamically loaded and
   * this function creates a global CtiFDRLiveData Object and then
   * calls its run method to cank it up.
   */
  DLLEXPORT int RunInterface( void )
  {
      // make a point to the interface
      myInterface = new CtiFDRLiveData();
  
      // now start it up
      return myInterface->run();
  }
  
  /**
   * This is used to Stop the Interface from the Main()
   * of FDR.EXE. Each interface i2 Dynamically loaded and
   * this function stops a global FDRTelegyr Object and then
   * deletes it.
   */
  DLLEXPORT int StopInterface( void )
  {
      myInterface->stop();
      delete myInterface;
      myInterface = 0;
  
      return 0;
  }
  
}


