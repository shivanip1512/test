#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   mgr_port
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_port.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/05/02 17:02:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>

#include "mgr_port.h"
#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "port_local_modem.h"
#include "port_tcpip.h"
#include "utility.h"


/* SQL to get every column used */
/* Seems far more efficient that using 4 simpler queries without outer join */
// static const CHAR AllPortSql[] = "SELECT DISTINCT COMMPORT.PORTID,COMMPORT.PORTTYPE,COMMPORT.DISABLEFLAG,COMMPORT.ALARMINHIBIT,COMMPORT.DESCRIPTION,COMMPORT.COMMONPROTOCOL,PORTTIMING.PRETXWAIT,PORTTIMING.RTSTOTXWAIT,PORTTIMING.POSTTXWAIT,PORTTIMING.RECEIVEDATAWAIT,PORTSETTINGS.CDWAIT,PORTSETTINGS.BAUDRATE,PORTLOCALSERIAL.PHYSICALPORT,PORTTERMINALSERVER.IPADDRESS,PORTTERMINALSERVER.SOCKETPORTNUMBER,PORTDIALUPMODEM.INITIALIZATIONSTRING FROM COMMPORT,PORTTIMING,PORTRADIOSETTINGS,PORTLOCALSERIAL,PORTTERMINALSERVER,PORTSETTINGS,PORTDIALUPMODEM WHERE PORTSETTINGS.PORTID(+)=COMMPORT.PORTID AND PORTTIMING.PORTID(+)=COMMPORT.PORTID AND PORTRADIOSETTINGS.PORTID(+)=COMMPORT.PORTID AND PORTLOCALSERIAL.PORTID(+)=COMMPORT.PORTID AND PORTTERMINALSERVER.PORTID(+)=COMMPORT.PORTID AND PORTDIALUPMODEM.PORTID(+)=COMMPORT.PORTID";


BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
    case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);
            break;
        }
    case DLL_THREAD_ATTACH:
        {
            break;
        }
    case DLL_THREAD_DETACH:
        {
            break;
        }
    case DLL_PROCESS_DETACH:
        {
            break;
        }
    }
    return TRUE;
}

DLLEXPORT BOOL isAPort(CtiPort* pSp, void *arg)
{
    BOOL bRet = FALSE;

    switch(pSp->getType())
    {
    default:
        {
            bRet = TRUE;
            break;
        }
    }

    return bRet;
}

inline RWBoolean
isNotUpdated(CtiPort *pPort, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pPort->CtiTablePortBase::getUpdatedFlag()));
}

inline void
ApplyResetUpdated(const CtiHashKey *key, CtiPort *&pPort, void* d)
{
    pPort->resetUpdatedFlag();
    return;
}

void
ApplyInvalidateNotUpdated(const CtiHashKey *key, CtiPort *&pPort, void* d)
{
    if(!pPort->getUpdatedFlag())
    {
        pPort->setValid(FALSE);   //   NOT NOT NOT Valid
    }
    return;
}

void
ApplyHaltLog(const CtiHashKey *key, CtiPort *&pPort, void* d)
{
    pPort->haltLog();
    return;
}

CtiPortManager::CtiPortManager(CTI_THREAD_FUNC_PTR fn) :
_portThreadFunc(fn)
{}

CtiPortManager::~CtiPortManager() {}

void CtiPortManager::RefreshList(CtiPort* (*Factory)(RWDBReader &),
                                 BOOL (*testFunc)(CtiPort*,void*),
                                 void *arg)
{
    CtiPort *pTempPort = NULL;

    try
    {
        {   // Make sure all objects that that store results

            // Reset everyone's Updated flag.
            if(Map.entries() > 0)
            {
                Map.apply(ApplyResetUpdated, NULL);
            }
            resetErrorCode();

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)  cout  << "Looking for Direct and ModemDirect Ports" << endl;
                CtiPortLocalModem().getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)  cout  << "Done looking for Direct and ModemDirect Ports" << endl;
            }

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)  cout  << "Looking for TCPIP Ports" << endl;
                CtiPortTCPIPDirect().getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)  cout  << "Done looking for TCPIP Ports" << endl;
            }

            if(getErrorCode() != RWDBStatus::ok)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " database had a return code of " << getErrorCode() << endl;
                }
            }
            else
            {
                if(getErrorCode() != RWDBStatus::ok)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " database had a return code of " << getErrorCode() << endl;
                    }
                }
                else
                {
                    // Now I need to check for any Port removals based upon the
                    // Updated Flag being NOT set
                    Map.apply(ApplyInvalidateNotUpdated, NULL);
                    do
                    {
                        pTempPort = remove(isNotUpdated, NULL);
                        if(pTempPort != NULL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Evicting " << pTempPort->getName() << " from list" << endl;
                            }
                            delete pTempPort;
                        }

                    } while(pTempPort != NULL);
                }
            }
        }   // Temporary results are destroyed to free the connection
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        cout << "Attempting to clear port list..." << endl;
        Map.clearAndDestroy();
        cout << "getPorts:  " << e.why() << endl;
        RWTHROW(e);
    }
}

void CtiPortManager::DumpList(void)
{
    CtiPort *p = NULL;
    try
    {
        CtiRTDBIterator itr(Map);

        for(;itr();)
        {
            p = itr.value();
            p->Dump();
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        cout << "Attempting to clear port list..." << endl;

        Map.clearAndDestroy();

        cout << "DumpPorts:  " << e.why() << endl;
        RWTHROW(e);

    }
}

CtiPort* CtiPortManager::PortGetEqual(LONG pid)
{
    CtiPort *p = NULL;
    try
    {
        CtiHashKey  Key(pid);
        p = Map.findValue(&Key);
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        cout << "Attempting to clear port list..." << endl;

        Map.clearAndDestroy();

        cout << "PortGetEqual:  " << e.why() << endl;
        RWTHROW(e);
    }
    return p;
}



void CtiPortManager::RefreshEntries(RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg)
{
    LONG     lTemp = 0;
    CtiPort* pTempPort = NULL;

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["paobjectid"] >> lTemp;            // get the RouteID
        CtiHashKey key(lTemp);


        if( Map.entries() > 0 && ((pTempPort = Map.findValue(&key)) != NULL) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new settings!
             */

            pTempPort->DecodeDatabaseReader(rdr);     // Fills himself in from the reader
            pTempPort->setUpdatedFlag();              // Mark it updated
            pTempPort->setValid();
        }
        else
        {
            CtiPort* pSp = (*Factory)(rdr);  // Use the reader to get me an object of the proper type

            if(pSp)
            {
                pSp->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

                if(((*testFunc)(pSp, arg)))            // If I care about this point in the db in question....
                {
                    pSp->setUpdatedFlag();              // Mark it updated
                    pSp->setValid();
                    Map.insert( new CtiHashKey(pSp->getPortID()), pSp ); // Stuff it in the list
                }
                else
                {
                    delete pSp;                         // I don't want it!
                }
            }
        }
    }
}

INT CtiPortManager::writeQueue(INT pid, ULONG Request, ULONG DataSize, PVOID Data, ULONG Priority)
{
    INT status = NORMAL;

    CtiPort *pPort = PortGetEqual(pid);

    if(pPort->isInhibited())
    {
        status = PORTINHIBITED;
    }
    else
    {
        if(pPort != NULL)
        {
            status = pPort->writeQueue(Request, DataSize, Data, Priority, _portThreadFunc);
        }
        else
        {
            status = BADPORT;
        }
    }

    return status;
}


CTI_THREAD_FUNC_PTR CtiPortManager::setPortThreadFunc(CTI_THREAD_FUNC_PTR aFn)
{
    CTI_THREAD_FUNC_PTR oldFn = _portThreadFunc;

    _portThreadFunc = aFn;

    return oldFn;
}

void CtiPortManager::haltLogs()
{
    // Reset everyone's Updated flag.
    if(Map.entries() > 0)
    {
        Map.apply(ApplyHaltLog, NULL);
    }
}

