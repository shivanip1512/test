
#pragma warning( disable : 4786)
#ifndef __FDRTELEGYR_H__
#define __FDRTELEGYR_H__

/*-----------------------------------------------------------------------------*
*
* File:   fdrtelegyr
*
* Class:
* Date:   5/14/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdracs.cpp-arc  $
*    REVISION     :  $Revision: 1.1 $
*    DATE         :  $Date: 2002/07/12 18:31:56 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*    History:
      $Log: fdrtelegyr.h,v $
      Revision 1.1  2002/07/12 18:31:56  eschmit

      Initial rev of the Telegyr stuff

*-----------------------------------------------------------------------------*/

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/db/status.h>
#include <vector>

//telegyr header
#include "apicliinc.h"

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"
#include "device.h"             // get the raw states
#include "telegyrgroup.h"
#include "telegyrcontrolcenter.h"
#include "rtdb.h"                   //not too sure about needing this.................

#define DIGITAL_TYPE    "digital"
#define ANALOG_TYPE     "analog"
#define COUNTER_TYPE    "counter"

//the format of the data we need to request from the telegyr server
typedef struct
{
   int  channel_id;
   int  group_type;
   int  group_number;
   int  persistence;
   int  priority;
   int  object_count;
   char *name_list[];              //correct syntax?
} GROUPS_TO_GET;

class IM_EX_FDRTELEGYRAPI CtiFDRTelegyr : public CtiFDRInterface
{
   typedef CtiFDRInterface Inherited;

public:

   // constructors and destructors
   CtiFDRTelegyr();
   virtual ~CtiFDRTelegyr();
   virtual bool sendMessageToForeignSys( CtiMessage *aMessage );
   virtual int processMessageFromForeignSystem( CHAR *data );
   virtual bool connect( int controlCenterNumber, int &status );
   virtual bool contact( void );
   virtual BOOL init( void );
   virtual BOOL run( void );
   virtual BOOL stop( void );

   void buildAndRegisterGroups( void );

   double getHiReasonabilityFilter() const;
   CtiFDRTelegyr & setHiReasonabilityFilter( const double myValue );

   ULONG getScanRateSeconds() const;
   void  setScanRateSeconds( const ULONG mySeconds );

   bool isConnected( void );
   CtiFDRTelegyr & setConnected( bool conn );

   RWCString getPath( void );
   CtiFDRTelegyr & setPath( RWCString inPath );

//   bool        groupCreated;

   static const CHAR * TBLNAME_TELEGYR_GROUPS;
   static const CHAR * KEY_HI_REASONABILITY_FILTER;
   static const CHAR * KEY_DB_RELOAD_RATE;
   static const CHAR * KEY_QUEUE_FLUSH_RATE;
   static const CHAR * KEY_DEBUG_MODE;
   static const CHAR * KEY_API_PATH;
   static const CHAR * COLNAME_TELEGYR_GROUPID;
   static const CHAR * COLNAME_TELEGYR_NAME;
   static const CHAR * COLNAME_TELEGYR_INTERVAL;
   static const CHAR * COLNAME_TELEGYR_TYPE;

   long getLinkStatusID( void ) const;
   CtiFDRTelegyr &setLinkStatusID( const long aPointID );
   void sendLinkState( int aState );

protected:

   RWThreadFunction                    _threadGetTelegyrData;
   int                                 _numberOfConnections;
   CtiTelegyrControlCenter             _controlCenter;
   double                              _hiReasonabilityFilter;
   long                                _linkStatusID;


//   ULONG calculateNextSendTime();
   void threadFunctionGetDataFromTelegyr( void );
   bool loadTranslationLists( void );
   bool loadLists( CtiFDRPointList &aList );
   bool getGroupLists( void );
   int readConfig( void );
   bool processAnalog( APICLI_GET_MEA aPoint, int groupid, int index );
   bool processDigital( APICLI_GET_IND aPoint, int groupid, int index );
   bool processCounter( APICLI_GET_CNT aPoint, int groupid, int index );

   RWCString decipherReason( int transmissionReason );
   RWCString decipherError( int status );

/*       vector<CtiTelegyrControlCenter>   _controlCenterList;         for the future*/

private:

   bool        _connected;
   RWCString   _path;
};

#endif // #ifndef __FDRTELEGYR_H__
