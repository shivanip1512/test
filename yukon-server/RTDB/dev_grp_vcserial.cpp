
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_vcserial
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_grp_vcserial.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:35 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "yukon.h"
#include "pt_base.h"
#include "master.h"

#include "logger.h"
#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_grp_vcserial.h"


LONG CtiDeviceGroupVersacomSerial::getRouteID()      // Must be defined!
{
   return VersacomSerialGroup.getRouteID();
}

INT CtiDeviceGroupVersacomSerial::ExecuteRequest(CtiRequestMsg                  *pReq,
                                                 CtiCommandParser               &parse,
                                                 OUTMESS                        *&OutMessage,
                                                 RWTPtrSlist< CtiMessage >      &vgList,
                                                 RWTPtrSlist< CtiMessage >      &retList,
                                                 RWTPtrSlist< OUTMESS >         &outList)
{
   INT   nRet = NoError;
   CHAR  Temp[80];
   CtiRoute *Route = NULL;
   /*
    *  This method should only be called by the dev_base method
    *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
    *   That method prepares an outmessage for submission to the internals..
    */

   if( (Route = getRoute( getRouteID() )) != NULL )    // This is "this's" route
   {
      OutMessage->TargetID                = getID();
      OutMessage->Retry                   = 2;                      // Default to two tries per route!

      OutMessage->Buffer.VSt.Address      = VersacomSerialGroup.getSerial();
      OutMessage->Buffer.VSt.RelayMask    = VersacomSerialGroup.getRelayMask();

      /*
       *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
       *  control path to take with this OutMessage!
       */
      OutMessage->EventCode    = VERSACOM | NORESULT;

      /*
       * OK, these are the items we are about to set out to perform..  Any additional signals will
       * be added into the list upon completion of the Execute!
       */
      if(parse.getActionItems().entries())
      {
         for( ; parse.getActionItems().entries(); )
         {

            RWCString actn = parse.getActionItems().removeFirst();
            RWCString desc = getDescription(parse);

            vgList.insert(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                            pReq->getSOE(),
                                            desc,
                                            actn,
                                            LoadMgmtLogType,
                                            SignalEvent,
                                            pReq->getUser()));

         }
      }

      /*
       *  Form up the reply here since the ExecuteRequest funciton will consume the
       *  OutMessage.
       */
      sprintf(Temp, "Command submitted on route %ld: ", Route->getRouteID());
      RWCString Reply = RWCString(Temp) + Route->getName();

      CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                            RWCString(OutMessage->Request.CommandStr),
                                            Reply,
                                            nRet,
                                            OutMessage->Request.RouteID,
                                            OutMessage->Request.MacroOffset,
                                            OutMessage->Request.Attempt,
                                            OutMessage->Request.TrxID,
                                            OutMessage->Request.UserID,
                                            OutMessage->Request.SOE,
                                            RWOrdered());

      // Start the control request on its route(s)
      if( !(nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
      {
         pRet->setStatus(NORMAL);
      }
      else     // An error occured in the processing/communication
      {
         sprintf(Temp, "ERROR %3d performing command on route %ld", nRet,  Route->getRouteID());
         pRet->setStatus(nRet);
         pRet->setResultString(Temp);
      }

      retList.insert( pRet );
   }
   else
   {
      nRet = NoRouteGroupDevice;

      sprintf(Temp, " ERROR: Route or Route Transmitter not available for group device %s", getName());
      RWCString Reply = RWCString(Temp);

      CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                            RWCString(OutMessage->Request.CommandStr),
                                            Reply,
                                            nRet,
                                            OutMessage->Request.RouteID,
                                            OutMessage->Request.MacroOffset,
                                            OutMessage->Request.Attempt,
                                            OutMessage->Request.TrxID,
                                            OutMessage->Request.UserID,
                                            OutMessage->Request.SOE,
                                            RWOrdered());

      retList.insert( pRet );

      if(OutMessage)
      {
         delete OutMessage;
         OutMessage = NULL;
      }

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << Temp << endl;
      }
   }

   return nRet;
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceGroupVersacomSerial::getDescription(const CtiCommandParser & parse) const
{
   CHAR  tdesc[80];
   CHAR  temp[80];
   CHAR  op_name[80];
   INT   op;
   INT   mask = 1;
   INT   ser;


   if( INT_MIN != parse.getiValue("proptest") || INT_MIN != parse.getiValue("ovuv"))
   {
      sprintf(tdesc, "Group: %s", getName().data());
   }
   else
   {
      sprintf(tdesc, "Group: %s Relay:", getName().data());

      for(int i = 0; i < 32; i++)
      {
         if(VersacomSerialGroup.getRelayMask() & (mask << i))
         {
            sprintf(temp,"%s r%d", tdesc, i+1);
            strcpy(tdesc, temp);
         }
      }
   }

   return RWCString(tdesc);
}

CtiDeviceGroupVersacomSerial::CtiDeviceGroupVersacomSerial() {}

CtiDeviceGroupVersacomSerial::CtiDeviceGroupVersacomSerial(const CtiDeviceGroupVersacomSerial& aRef)
{
   *this = aRef;
}

CtiDeviceGroupVersacomSerial::~CtiDeviceGroupVersacomSerial() {}

CtiDeviceGroupVersacomSerial& CtiDeviceGroupVersacomSerial::operator=(const CtiDeviceGroupVersacomSerial& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      VersacomSerialGroup = aRef.getVersacomSerialGroup();
   }
   return *this;
}

CtiTableLMGroupVersacomSerial   CtiDeviceGroupVersacomSerial::getVersacomSerialGroup() const      { return VersacomSerialGroup; }
CtiTableLMGroupVersacomSerial&  CtiDeviceGroupVersacomSerial::getVersacomSerialGroup()            { return VersacomSerialGroup; }

CtiDeviceGroupVersacomSerial&     CtiDeviceGroupVersacomSerial::setVersacomSerialGroup(const CtiTableLMGroupVersacomSerial& aRef)
{
   VersacomSerialGroup = aRef;
   return *this;
}

void CtiDeviceGroupVersacomSerial::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableLMGroupVersacomSerial::getSQL(db, keyTable, selector);
}

void CtiDeviceGroupVersacomSerial::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

   if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

   VersacomSerialGroup.DecodeDatabaseReader(rdr);
}












