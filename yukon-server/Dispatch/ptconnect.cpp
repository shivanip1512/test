/*-----------------------------------------------------------------------------
    Filename:
         pointconnect.cpp

    Programmer:
         Corey G. Plender

    Description:
        Defines functions related to retrieving point data

    Initial Date:  7/23/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;

#include <rw/db/db.h>
#include <rw/toolpro/neterr.h>

#include "dbaccess.h"
#include "logger.h"
#include "msg_pcreturn.h"
#include "pt_base.h"
#include "ptconnect.h"

#include "utility.h"

CtiPointConnection& CtiPointConnection::operator=(const CtiPointConnection &aRef)
{
   LockGuard guard(monitor());

   if(this != &aRef)
   {
      ConnectionManagerCollection = aRef.getManagerList();

   }
   return *this;
}

int CtiPointConnection::PostPointChangeToConnections(const CtiPointDataMsg &ChgMsg)
{
   CtiReturnMsg* ConnMgrMsg = NULL;
   std::list< CtiServer::ptr_type >::iterator itr = ConnectionManagerCollection.begin();
   while ( itr != ConnectionManagerCollection.end() )
   {
      CtiServer::ptr_type CtiM = *itr;
      try
      {
         LockGuard guard(monitor());

         // Copy constructor...
         CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ChgMsg);

         if(pData != NULL)
         {
            if((ConnMgrMsg = CTIDBG_new CtiReturnMsg()) != NULL)
            {
               ConnMgrMsg->PointData().push_back(pData);

               CtiM->WriteConnQue(ConnMgrMsg, 5000); // Default priority of 7 is used here!
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << CtiTime() << " Posting point " << ChgMsg.getId() << " to local connection " << CtiM->getClientName() << endl;
               }
            }
            else
            {
               delete pData;
            }
         }

         ConnMgrMsg = NULL;
         pData = NULL;

         //(ConnectionManagerCollection[i])->WriteConnQue(CTIDBG_new CtiReturnMsg(Chg)); // Default priority of 7 is used here!

      }
      catch(RWSockErr& msg )
      {
         if(msg.errorNumber() == RWNETECONNRESET)
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " Failed to post change event, the client has shutdown. " << endl;
            }
            RemoveConnectionManager(CtiM);
         }
         else if(msg.errorNumber() == RWNETECONNABORTED)
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " Connection Aborted, Removing from list." << endl;
            }
            RemoveConnectionManager(CtiM);
         }
         else
         {
            cout << "Socket Error :" << msg.errorNumber() << " occurred" << endl;
            cout << "  " << msg.why() << endl;
         }
      }
      catch(const RWxmsg& x)
      {
          {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
          }
         break;
      }
      ++itr;
   }

   return 0;
}

CtiPointConnection::CtiPointConnection()
{
}

CtiPointConnection::~CtiPointConnection()
{
    // Blow away everything. // Connection Managers must be deleted by VanGogh
    // delete_container( ConnectionManagerCollection );  // Release of the reference allows a delete to occur.
    ConnectionManagerCollection.clear();
}

void CtiPointConnection::AddConnectionManager(CtiServer::ptr_type cm)
{
   LockGuard guard(monitor());
   ConnectionManagerCollection.push_back(cm);
}
void CtiPointConnection::RemoveConnectionManager(CtiServer::ptr_type cm)
{
   LockGuard guard(monitor());
   bool present = false;
   std::list< CtiServer::ptr_type >::iterator itr = ConnectionManagerCollection.begin();
   while(itr != ConnectionManagerCollection.end() ){
       if ( *itr == cm) {//Note  this is removing based off the pointer address
           itr = ConnectionManagerCollection.erase(itr);
       }else
           ++itr;
   }
}

int CtiPointConnection::PostPointChangeToConnections(const CtiPointDataMsg& Msg);

CtiPointConnection& CtiPointConnection::operator=(const CtiPointConnection &aRef);

list< CtiServer::ptr_type >& CtiPointConnection::getManagerList()
{
    return ConnectionManagerCollection;
}
list< CtiServer::ptr_type >  CtiPointConnection::getManagerList() const
{
    return ConnectionManagerCollection;
}



