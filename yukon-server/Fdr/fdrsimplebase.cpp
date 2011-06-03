#include "yukon.h"

#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include "ctitime.h"
#include "ctidate.h"

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
CtiFDRSimple::CtiFDRSimple(string interfaceName) :
    CtiFDRInterface(string(interfaceName.c_str())),
    _connected(false),
    _linkStatusId(0)

{
   CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
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
  const int secondsBeforeReconnect = gConfigParms.getValueAsInt("FDR_" + getInterfaceName() + "_RECONNECT_RATE", 60);
  const int loopsBeforeReconnect = (secondsBeforeReconnect * 1000) / loopPeriod;
  int reconnectLoopCount = loopsBeforeReconnect;
  bool loadLists = true;

  try
  {
    for( ;; )
    {
      pSelf.sleep( loopPeriod );

      pSelf.serviceCancellation();

      CtiFDRPointList &aList = getReceiveFromList();
      CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

      if(needsConnection())
      {
        if (reconnectLoopCount > loopsBeforeReconnect)
        {
          connect();
          reconnectLoopCount = 0;
        } else {
          reconnectLoopCount++;
        }
      }

      if ( isConnected() )
      {
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
        loadLists = true;
        if (reconnectLoopCount == 0)
        {
          if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            logNow() << "Connection down, waiting "
              << secondsBeforeReconnect << " seconds for reconnect" << endl;
          }
        }
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
  CtiFDRPointSPtr point;

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
                                                   string (FDR_INTERFACE_RECEIVE));

    if (pointList->loadPointList())
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
        CtiFDRManager* mgrPtr = aList.getPointList();
        CtiFDRManager::writerLock guard(mgrPtr->getLock());

        CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

        removeAllPoints();

        while ( myIterator != mgrPtr->getMap().end() )
        {
          point = (*myIterator).second;

          processNewPoint(point);
          ++myIterator;

        }
        handleNewPoints();
      }
    }
    else
    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      logNow() << "error in db read code " << endl;
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


bool CtiFDRSimple::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
  processNewPoint(translationPoint);
  handleNewPoint(translationPoint);
  return true;
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

  CtiTime currentTime(timestamp);
  double valueConverted = 0;

  if (ctiPoint->getLastTimeStamp() < currentTime)
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

    // This no longer needs to be saved for the handleNonUpdate() method.
    //ctiPoint->setValue(valueConverted);
    //ctiPoint->setLastTimeStamp(time);

    pData->setTime(currentTime);

    queueMessageToDispatch(pData);

    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "new value " << value << " for "
        << *ctiPoint << " queued" << endl;
    }
  }
}


/**
 * Formats and sends an UpdateFailed message.
 * This will cause the quality of the point to change to a NonUpdatedQuality.
 */
void CtiFDRSimple::handleNonUpdate(CtiFDRPoint *ctiPoint,
                                   const time_t timestamp)
{
  CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

  if (pMsg == NULL)
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Unable to allocate memory for CtiPointDataMsg (CtiFDRSimple::handleUpdate()) " << endl;
    return;
  }

  pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
  pMsg->insert(CtiCommandMsg::OP_POINTID);       // OP_POINTID indicates a point fail situation.
  pMsg->insert(ctiPoint->getPointID());  // The pointid which failed
  pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,
                                  //  ScanRateIntegrity, or if unknown -> ScanRateInvalid
  pMsg->insert(UnknownError);     // The error number from dsm2.h or yukon.h which was reported.

  queueMessageToDispatch(pMsg);

  if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "UpdateFailed command for " <<
      *ctiPoint << " queued" << endl;
  }
}


/**
 * Read our configuration file.
 */
void CtiFDRSimple::readThisConfig()
{
  string  keyDbReloadRate = "FDR_" + getInterfaceName() + "_DB_RELOAD_RATE";
  string  keyDebugMode = "FDR_" + getInterfaceName() + "_DB_DEBUG_MODE";

  setReloadRate( gConfigParms.getValueAsInt( keyDbReloadRate, 86400 ) );

  string   tempStr = gConfigParms.getValueAsString( keyDebugMode );

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












