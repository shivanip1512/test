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

   for(int i=0; i < ConnectionManagerCollection.entries(); i++)
   {
      try
      {
         LockGuard guard(monitor());

         // Copy constructor...
         CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ChgMsg);

         if(pData != NULL)
         {
            if((ConnMgrMsg = CTIDBG_new CtiReturnMsg()) != NULL)
            {
               ConnMgrMsg->PointData().insert(pData);

               (ConnectionManagerCollection[i])->WriteConnQue(ConnMgrMsg, 5000); // Default priority of 7 is used here!
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " Posting point " << ChgMsg.getId() << " to local connection " << (ConnectionManagerCollection[i])->getClientName() << endl;
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
               dout << RWTime() << " Failed to post change event, the client has shutdown. " << endl;
            }
            RemoveConnectionManager((ConnectionManagerCollection[i]));
         }
         else if(msg.errorNumber() == RWNETECONNABORTED)
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Connection Aborted, Removing from list." << endl;
            }
            RemoveConnectionManager((ConnectionManagerCollection[i]));
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
              dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
          }
         break;
      }
   }

   return 0;
}

CtiPointConnection::CtiPointConnection()
{
}

CtiPointConnection::~CtiPointConnection()
{
   // Blow away everything. // Connection Managers must be deleted by VanGogh
   ConnectionManagerCollection.clear();
}

void CtiPointConnection::AddConnectionManager(CtiConnectionManager *cm)
{
   LockGuard guard(monitor());
   ConnectionManagerCollection.insert(cm);
}
void CtiPointConnection::RemoveConnectionManager(CtiConnectionManager *cm)
{
   LockGuard guard(monitor());

   if(ConnectionManagerCollection.contains(cm))
   {
        ConnectionManagerCollection.removeAll(cm);
    }
}

int CtiPointConnection::PostPointChangeToConnections(const CtiPointDataMsg& Msg);

CtiPointConnection& CtiPointConnection::operator=(const CtiPointConnection &aRef);

RWTPtrSlist<CtiConnectionManager>& CtiPointConnection::getManagerList()
{
    return ConnectionManagerCollection;
}
RWTPtrSlist<CtiConnectionManager>  CtiPointConnection::getManagerList() const
{
    return ConnectionManagerCollection;
}



