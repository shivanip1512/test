
#pragma warning( disable : 4786)
#ifndef __FDRTELEGYR_H__
#define __FDRTELEGYR_H__


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
#define STATUS_TYPE     "status"
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
   char *name_list[];
} GROUPS_TO_GET;

class IM_EX_FDRTELEGYRAPI CtiFDRTelegyr : public CtiFDRInterface 
{
   private:

      bool        _connected;
      bool        _regFlag;
      bool        _quit;
      bool        _reloadPending;

      int         _inited;
      int         _dbReloadInterval;
      int         _panicNumber;

      RWCString   _path;
      RWCString   _appName;
      RWCString   _apiVersion;

      RWTime      _reloadTimer;

      typedef CtiFDRInterface Inherited;

   public:
   
      // constructors and destructors
      CtiFDRTelegyr();
      virtual ~CtiFDRTelegyr();
      virtual bool sendMessageToForeignSys( CtiMessage *aMessage );
      virtual int processMessageFromForeignSystem( CHAR *data );
      virtual BOOL init( void );
      virtual BOOL run( void );
      virtual BOOL stop( void );
   
      double getHiReasonabilityFilter() const;
      CtiFDRTelegyr & setHiReasonabilityFilter( const double myValue );
   
      ULONG getScanRateSeconds() const;
      void  setScanRateSeconds( const ULONG mySeconds );
   
      bool isConnected( void );
      CtiFDRTelegyr & setConnected( bool conn );
   
      RWCString getPath( void );
      CtiFDRTelegyr & setPath( RWCString inPath );
   
      static const CHAR * TBLNAME_TELEGYR_GROUPS;
      static const CHAR * KEY_HI_REASONABILITY_FILTER;
      static const CHAR * KEY_APPLICATION_NAME;
      static const CHAR * KEY_API_VERSION;
      static const CHAR * KEY_DB_RELOAD_RATE;
      static const CHAR * KEY_QUEUE_FLUSH_RATE;
      static const CHAR * KEY_DEBUG_MODE;
      static const CHAR * KEY_API_PATH;
      static const CHAR * KEY_OPERATOR;
      static const CHAR * KEY_PASSWORD;
      static const CHAR * KEY_SYSTEM_NAME;
      static const CHAR * KEY_CHANNEL_ID;
      static const CHAR * KEY_ACCESS;
      static const CHAR * KEY_RELOAD_FREQUENCY;
      static const CHAR * KEY_PANIC_NUMBER;
      static const CHAR * COLNAME_TELEGYR_GROUPID;
      static const CHAR * COLNAME_TELEGYR_NAME;
      static const CHAR * COLNAME_TELEGYR_INTERVAL;
      static const CHAR * COLNAME_TELEGYR_TYPE;
   
      long getLinkStatusID( void ) const;
      CtiFDRTelegyr &setLinkStatusID( const long aPointID );
      void sendLinkState( int aState );
   
   protected:
   
      RWThreadFunction                    _threadGetTelegyrData;
      RWThreadFunction                    _threadMain;
      int                                 _numberOfConnections;
      CtiTelegyrControlCenter             _controlCenter;
      double                              _hiReasonabilityFilter;
      long                                _linkStatusID;
   
      bool processBadPoint( int groupid, int index );
      void deleteGroups( void );
      void threadFunctionGetDataFromTelegyr( void );
      bool loadTranslationLists( void );
      bool loadGroupLists( void );
      int readConfig( void );
      bool processAnalog( APICLI_GET_MEA aPoint, int groupid, int group_type, int index );
      bool processDigital( APICLI_GET_IND aPoint, int groupid, int group_type, int index );
      bool processCounter( APICLI_GET_CNT aPoint, int groupid, int group_type, int index );
      bool timeToReload( void );
      bool isReloadTime( void );
      RWCString decipherReason( int transmissionReason );
      void buildAndRegisterGroups( void );
      bool connect( int controlCenterNumber, int &status );
      bool contact( int &status );
   
};

#endif // #ifndef __FDRTELEGYR_H__
