#include "precompiled.h"

#include <iostream>
using namespace std;

#include "dbaccess.h"
#include "logger.h"
#include "msg_pcreturn.h"
#include "pt_base.h"
#include "ptconnect.h"

#include "utility.h"

CtiPointConnection& CtiPointConnection::operator=(const CtiPointConnection &aRef)
{
   CtiLockGuard<CtiMutex> guard(_classMutex);

   if(this != &aRef)
   {
      ConnectionManagerCollection = aRef.getManagerList();

   }
   return *this;
}

int CtiPointConnection::PostPointChangeToConnections(const CtiPointDataMsg &ChgMsg)
{
   CtiReturnMsg* ConnMgrMsg = NULL;
   Cti::StreamBuffer logData;
   bool hasLogData = false;

   CollectionType::iterator itr = ConnectionManagerCollection.begin();

   while ( itr != ConnectionManagerCollection.end() )
   {
      CtiServer::ptr_type CtiM = *itr;
      try
      {
         CtiLockGuard<CtiMutex> guard(_classMutex);

         // Copy constructor...
         CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ChgMsg);

         if(pData != NULL)
         {
            if((ConnMgrMsg = CTIDBG_new CtiReturnMsg()) != NULL)
            {
               ConnMgrMsg->PointData().push_back(pData);

               CtiM->WriteConnQue(ConnMgrMsg, 5000); // Default priority of 7 is used here!

               logData <<"\nPosting point "<< ChgMsg.getId() <<" to local connection "<< CtiM->getClientName();

               hasLogData = true;
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
      catch(const RWxmsg& x)
      {
          CTILOG_EXCEPTION_ERROR(dout, x);
          break;
      }
      ++itr;
   }

   if( hasLogData )
   {
       CTILOG_INFO(dout, logData);
   }

   return 0;
}

CtiPointConnection::CtiPointConnection()
{
}

//Copy constructor copies nothing!
CtiPointConnection::CtiPointConnection(const CtiPointConnection& aRef)
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
   CtiLockGuard<CtiMutex> guard(_classMutex);
   ConnectionManagerCollection.insert(cm);
}
void CtiPointConnection::RemoveConnectionManager(CtiServer::ptr_type cm)
{
   CtiLockGuard<CtiMutex> guard(_classMutex);
   bool present = false;
   CollectionType::iterator itr = ConnectionManagerCollection.begin();
   while(itr != ConnectionManagerCollection.end() ){
       if ( *itr == cm) {//Note  this is removing based off the pointer address
           itr = ConnectionManagerCollection.erase(itr);
       }else
           ++itr;
   }
}

bool CtiPointConnection::IsEmpty()
{
   CtiLockGuard<CtiMutex> guard(_classMutex);
   return ConnectionManagerCollection.empty();
}

CtiPointConnection::CollectionType& CtiPointConnection::getManagerList()
{
    return ConnectionManagerCollection;
}
CtiPointConnection::CollectionType  CtiPointConnection::getManagerList() const
{
    return ConnectionManagerCollection;
}

bool CtiPointConnection::HasConnection(const CtiServer::ptr_type cm)
{
   CollectionType::iterator iter = ConnectionManagerCollection.find(cm);
   if(iter != ConnectionManagerCollection.end())
   {
      return true;
   }
   return false;
}

