/*-----------------------------------------------------------------------------*
*
* File:   con_mgr_vg
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/con_mgr_vg.h-arc  $
* REVISION     :  $Revision: 1.8.10.1 $
* DATE         :  $Date: 2008/11/13 17:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CON_MGR_VG_H__
#define __CON_MGR_VG_H__
#pragma warning( disable : 4786)

#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "exchange.h"
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

private:

   // Client connection supplied information via a Registration Message

   int  ClientKnownPort;

   CtiVanGoghConnectionManager() :
      _blank(0),
      ClientKnownPort(-1)
   {
      std::cout << "Default Constructor may break things!" << FO(__FILE__) << " " << __LINE__ << std::endl;
      std::cout << "**** Connection Manager!!! *****" << std::endl;
   }

public:

   CtiVanGoghConnectionManager(CtiExchange *XChg, Que_t *MainQueue_);// :_blank(0),ClientKnownPort(-1),CtiConnectionManager(XChg, MainQueue_)
   virtual ~CtiVanGoghConnectionManager();
   static unsigned hash(const CtiVanGoghConnectionManager& aRef);
   int   getClientKnownPort() const;//          {return ClientKnownPort; }
   void  setClientKnownPort(int p);//           {ClientKnownPort = p; }

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

#endif //#ifndef __CON_MGR_VG_H__


