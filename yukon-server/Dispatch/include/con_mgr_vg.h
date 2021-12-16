#pragma once

#include <limits.h>

#include "message.h"
#include "vgexe_factory.h"

#include "exe_cmd.h"
#include "msg_cmd.h"

#include "con_mgr.h"

class CtiVanGoghConnectionManager : public CtiConnectionManager
{
   /*
    *  These are the items a VanGogh connection might wish to know about were he in a list.
    */
   union
   {
      UINT _blank;    // Used to zero the struct elements as a block
      struct {
         UINT  _allPoints  : 1;
         UINT  _event      : 1;        // This client can only handle simple event messages
         UINT  _alarm      : 1;        // This client can only handle simple alarm messages
      };
   };

public:

   CtiVanGoghConnectionManager( const std::string& replyToName, const std::string& serverQueueName, Que_t *MainQueue_ );
   virtual ~CtiVanGoghConnectionManager();

   static unsigned hash(const CtiVanGoghConnectionManager& aRef);

   // Client connection supplied information via a Registration Message

   UINT  getAllPoints() const;
   CtiVanGoghConnectionManager& setAllPoints( const UINT a_allPoints );

   UINT  getEvent() const;
   CtiVanGoghConnectionManager& setEvent( const UINT a_event );

   UINT  getAlarm() const;
   CtiVanGoghConnectionManager& setAlarm( const UINT a_alarm );

   bool  isRegForAll() const;

   void  reportRegistration() const;
};
