
#pragma warning( disable : 4786)
#ifndef __FDRTELEGYR_H__
#define __FDRTELEGYR_H__



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <vector>

//telegyr header
#include "apicliinc.h"

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrdebuglevel.h"
#include "fdrasciiimportbase.h"
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
      bool        _quit;
      bool        _reloadPending;

      int         _inited;
      int         _dbReloadInterval;
      int         _panicNumber;

      string   _path;
      string   _appName;
      string   _apiVersion;

      CtiTime     _reloadTimer;
      CtiTime     _reportTimer;

      vector< CtiTelegyrGroup >  _groupList;

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

      string getPath( void );
      CtiFDRTelegyr & setPath( string inPath );

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
      bool loadTranslationLists( void );
      bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
      bool loadGroupLists( void );
      bool processAnalog( APICLI_GET_MEA aPoint, int groupid, int group_type, int index );
      bool processDigital( APICLI_GET_IND aPoint, int groupid, int group_type, int index );
      bool processCounter( APICLI_GET_CNT aPoint, int groupid, int group_type, int index );
      bool timeToReload( void );
      bool isReloadTime( void );
      bool connect( int controlCenterNumber, int &status );
      bool contact( int &status );

      void deleteGroups( void );
      void threadFunctionGetDataFromTelegyr( void );
      void buildAndRegisterGroups( void );
      void receivedAnalog( int arraySize, int group_num, int group_type, int first, int last, int result[] );
      void receivedDigital( int arraySize, int group_num, int group_type, int first, int last, int result[] );
      void receivedCounter( int arraySize, int group_num, int group_type, int first, int last, int result[] );
      void halt( void );

      int noDataAction( int no_msg_count );
      int readConfig( void );

      string decipherReason( int transmissionReason );

      USHORT getQuality( SYS_DEP_INFO );
};

#endif // #ifndef __FDRTELEGYR_H__
