#include "yukon.h"

/**
 * File:   fdrsimple
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.3 $
 * DATE         :  $Date: 2005/08/09 22:36:01 $
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
#include "fdrinterface.h"

// this class header
#include "fdrsimplebase.h"

/** 
 * Constructor
 */ 
CtiFDRSimple::CtiFDRSimple(string interfaceName) : CtiFDRInterface( RWCString(interfaceName.c_str()) )
{
   CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
   getReceiveFromList().setPointList( recList );

   recList = NULL;

}


/** 
 * Destructor
 */ 
CtiFDRSimple::~CtiFDRSimple()
{
  if (getReceiveFromList().getPointList() != NULL)
  {
    getReceiveFromList().deletePointList();
  }
}


/**
 * Initializes the class by reading in the configuration, 
 * attempting to connect, and starting the threads.
 */ 
BOOL CtiFDRSimple::init()
{
  BOOL        retVal = true;


  // init the base class
  if( !Inherited::init() )
  {
     retVal = false;
  }
  else
  {

    readThisConfig();

    _linkStatusId = getClientLinkStatusID( getInterfaceName() );

    setConnected( false );
    startup();

    _threadGetData = rwMakeThreadFunction( *this, &CtiFDRSimple::threadFunctionGetData );
  }
  return retVal;
}


/**
 * Worker thread loops forever to check for new data on the foreign system.
 */ 
void CtiFDRSimple::threadFunctionGetData()
{
  RWRunnableSelf    pSelf = rwRunnable();

  const int loopPeriod = 1250;
  const int secondsBeforeReconnect = 60;
  const int loopsBeforeReconnect = (secondsBeforeReconnect * 1000) / loopPeriod;
  int reconnectLoopCount = loopsBeforeReconnect;
  bool loadLists = true;

  try
  {
    connect();
    for( ;; )
    {
      pSelf.sleep( loopPeriod ); 

      pSelf.serviceCancellation();

      CtiFDRPointList &aList = getReceiveFromList();
      CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

      //testConnection();

      if (isConnected())
      {
        reconnectLoopCount = 0;
        try
        {
          if (loadLists)
          {
            loadTranslationLists();
            loadLists = false;
          }
          doUpdates();
        }
        catch (exception& e)
        {
          setConnected(false);
        }        
      }
      else
      {
        if (reconnectLoopCount == 0)
        {
          if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            logNow() << "Connection down, waiting "
              << secondsBeforeReconnect << " seconds for reconnect" << endl;
          }
        }
        if (reconnectLoopCount > loopsBeforeReconnect)
        {
          connect();
          reconnectLoopCount = 0;
        } else {
          reconnectLoopCount++;
        }
        loadLists = true;
      }
    }
  }
  catch( RWCancellation &cancellationMsg )
  {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Cancelling thread threadFunctionGetData" << endl;
    }
    return;
  }
  catch (...)
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Caught unknown exception in CtiFDRSimple::threadFunctionGetData()." << endl;
  }
}
 

/**
 * Retreives updated list of points from database.
 */ 
bool CtiFDRSimple::loadTranslationLists()
{
  bool    retCode = true;
  bool                successful = true;
  bool                foundPoint = false;
  CtiFDRPoint *       point = NULL;

  if (!isConnected()) {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Skipping loadTranslationLists(), not connected " << endl;
    }
    // Returning false would cause the caller to keep attempting to reload the
    // translation lists. When we get reconnected, they'll be reloaded anyway.
    return true;
  }

  CtiFDRPointList &aList = getReceiveFromList();

  try
  {
    // make a list with all received points
    CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), 
                                                   RWCString (FDR_INTERFACE_RECEIVE));


    RWDBStatus dbStatus = pointList->loadPointList();

    if (dbStatus.errorCode() == (RWDBStatus::ok))
    {
      const int minExpectedOld = 2; // somewhat arbitrary, see note in fdrsinglesocket.cpp
      if (pointList->entries() == 0 && aList.getPointList()->entries() > minExpectedOld)
      {
        if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
        {
          const int oldSize = aList.getPointList()->entries();
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          logNow() << "Got an unexpected empty list from the database (old size=" 
            << oldSize << ")" << endl;
        }
      }
      else
      {
        CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());  
        if (aList.getPointList() != NULL)
        {
          aList.deletePointList();
        }
        aList.setPointList (pointList);
  
        // get iterator on list
        CtiFDRManager::CTIFdrPointIterator  myIterator(aList.getPointList()->getMap());
  
        beginNewPoints();
  
        while ( myIterator() )
        {
          point = myIterator.value();
  
          processNewPoint(point);
  
        }
        endNewPoints();
      }
    }
    else
    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      logNow() << "error in db read code " << pointList->loadPointList().errorCode() << endl;
      successful = false;
    }
  }   // end try block

  catch (RWExternalErr e )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    logNow() << "loadTranslationList():  " << e.why() << endl;
    RWTHROW(e);
  }
  return( successful );
}


/**
 * Formats and sends an update message.
 * This function will apply the offset and multiplier.
 */ 
void CtiFDRSimple::handleUpdate(CtiFDRPoint *ctiPoint, 
                    const double value, 
                    const time_t timestamp,
                    const PointQuality_t quality)
{

  RWTime rwTime(rwEpoch + timestamp);
  double valueConverted = 0;

  if (ctiPoint->getLastTimeStamp() < rwTime)
  {
    valueConverted = value;
    if ( ctiPoint->getPointType() == AnalogPointType)
    {
      valueConverted *= ctiPoint->getMultiplier();
      valueConverted += ctiPoint->getOffset();
    }

    CtiPointDataMsg   *pData = NULL;
    pData = new CtiPointDataMsg(ctiPoint->getPointID(),
                                valueConverted,
                                quality,
                                ctiPoint->getPointType());


    if (!pData)
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to allocate memory for CtiPointDataMsg (CtiFDRSimple::handleUpdate()) " << endl;
      return;
    }

    ctiPoint->setValue(value);
    ctiPoint->setLastTimeStamp(rwTime);

    pData->setTime(rwTime);

    queueMessageToDispatch(pData);

    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "new value " << value << " for point "
        << ctiPoint->getPointID() << " queued" << endl;
    }
  }
}


/**
 * Formats and sends an update message.
 * This function will apply the offset and multiplier.
 */ 
void CtiFDRSimple::handleNonUpdate(CtiFDRPoint *ctiPoint, 
                                   const time_t timestamp)
{
  RWTime rwTime(rwEpoch + timestamp);
  double valueConverted = ctiPoint->getValue();

  CtiPointDataMsg   *pData = NULL;
  pData = new CtiPointDataMsg(ctiPoint->getPointID(),
                              valueConverted,
                              NonUpdatedQuality,
                              ctiPoint->getPointType());


  if (!pData)
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Unable to allocate memory for CtiPointDataMsg (CtiFDRSimple::handleUpdate()) " << endl;
    return;
  }

  ctiPoint->setLastTimeStamp(rwTime);

  pData->setTime(rwTime);

  queueMessageToDispatch(pData);

  if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "non-updated for point " << 
      ctiPoint->getPointID() << " queued" << endl;
  }
}


/**
 * Read our configuration file.
 */ 
void CtiFDRSimple::readThisConfig()
{
  RWCString  keyDbReloadRate = "FDR_" + getInterfaceName() + "_DB_RELOAD_RATE";
  RWCString  keyDebugMode = "FDR_" + getInterfaceName() + "_DB_DEBUG_MODE";

  setReloadRate( iConfigParameters.getValueAsInt( keyDbReloadRate, 86400 ) );

  RWCString   tempStr = iConfigParameters.getValueAsString( keyDebugMode );

  setInterfaceDebugMode( tempStr.length() > 0 );

  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "----------------FDRSimple Configs------------------------------" << endl;
    dout << "  " << keyDbReloadRate << ": " << getReloadRate() << "         (secs)" << endl;
    dout << "  " << keyDebugMode << ": " << (isInterfaceInDebugMode() ? "TRUE" : "FALSE") << endl;
    dout << endl;
  }
}


/**
 * Send message with current link status.
 */ 
void CtiFDRSimple::sendLinkState( bool state )
{
    CtiPointDataMsg     *pData;
    pData = new CtiPointDataMsg( _linkStatusId , 
                                 state ? FDR_CONNECTED : FDR_NOT_CONNECTED, 
                                 NormalQuality, StatusPointType );
    sendMessageToDispatch( pData );
}

/**
 * Set connected state.
 * Send updated link status when it changes.
 */ 
void CtiFDRSimple::setConnected( bool conn )
{
   if( conn != _connected)
   {
     sendLinkState(conn);
     if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
     {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Setting " << (conn?"":"dis") << "connected" << endl;
     }
   _connected = conn;
   }
}


/**
 * Return the 'dout' logger and prepend the current time and the interface name.
 */ 
ostream CtiFDRSimple::logNow() {
  return dout << RWTime::now() << " FDR-" << getInterfaceName() << ": ";
}


/**
 * Run the interface
 */ 
BOOL CtiFDRSimple::run()
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      logNow() << "Starting Interface -------------------------" << endl;
   }

   init();

   // crank up the base class
   Inherited::run();

   // startup our interfaces
   _threadGetData.start();

   return TRUE;
}


/**
 * Stop the interface
 */ 
BOOL CtiFDRSimple::stop()
{
    _threadGetData.requestCancellation();

    setConnected(false);

    shutdown();

    // stop the base class
    Inherited::stop();

    return(TRUE);
}

bool CtiFDRSimple::sendMessageToForeignSys( CtiMessage *aMessage )
{
  return true;
}

int CtiFDRSimple::processMessageFromForeignSystem( char *data )
{
  return 1;
}












