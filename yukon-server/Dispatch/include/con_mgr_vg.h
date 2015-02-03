#pragma once

#include <limits.h>

#include "message.h"
#include "vgexe_factory.h"

#include "exe_cmd.h"
#include "msg_cmd.h"

#include "con_mgr.h"

class IM_EX_CTIVANGOGH CtiVanGoghConnectionManager : public CtiConnectionManager
{
protected:

   /*
    *  These are the items a VanGogh connection might wish to know about were he in a list.
    */
   union
   {
      UINT _blank;    // Used to zero the struct elements as a block
      struct {
         UINT  _status     : 1;
         UINT  _analog     : 1;
         UINT  _accum      : 1;
         UINT  _calc       : 1;
         UINT  _event      : 1;        // This client can only handle simple event messages
         UINT  _alarm      : 1;        // This client can only handle simple alarm messages
         UINT  _loadProfile : 1;       // This client wants load profile data too.
      };
   };

public:

   CtiVanGoghConnectionManager( CtiListenerConnection &listenerConn, Que_t *MainQueue_ );
   virtual ~CtiVanGoghConnectionManager();

   static unsigned hash(const CtiVanGoghConnectionManager& aRef);

   // Client connection supplied information via a Registration Message

   UINT  getStatus() const;
   CtiVanGoghConnectionManager& setStatus( const UINT a_status );

   UINT  getAnalog() const;
   CtiVanGoghConnectionManager& setAnalog( const UINT a_analog );

   UINT  getAccumulator() const;
   CtiVanGoghConnectionManager& setAccumulator( const UINT a_accum );

   UINT  getCalculated() const;
   CtiVanGoghConnectionManager& setCalculated( const UINT a_calc );

   UINT  getEvent() const;
   CtiVanGoghConnectionManager& setEvent( const UINT a_event );

   UINT  getAlarm() const;
   CtiVanGoghConnectionManager& setAlarm( const UINT a_alarm );

   UINT  getLoadProfile() const;
   CtiVanGoghConnectionManager& setLoadProfile( const UINT a_lp );

   bool  isRegForAll() const;
   bool  isRegForAnyType() const;
   BOOL  isRegForChangeType(int type) const;

   void  reportRegistration() const;
};
