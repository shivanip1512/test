#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   mgr_port
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_port.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2002/09/03 14:33:50 $
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

DLLEXPORT BOOL isAPort(CtiPort *pSp, void *arg)
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

inline bool isNotUpdated(CtiPortSPtr &Port, void* d)
{
    // Return TRUE if it is NOT SET
    return( !Port->getUpdatedFlag() );
}

inline void ApplyResetUpdated(const long key, CtiPortSPtr Port, void* d)
{
    Port->resetUpdatedFlag();
    return;
}

inline void ApplyDump(const long key, CtiPortSPtr Port, void* d)
{
    Port->Dump();
    return;
}

void ApplyInvalidateNotUpdated(const long key, CtiPortSPtr Port, void* d)
{
    if(!Port->getUpdatedFlag())
    {
        Port->setValid(FALSE);   //   NOT NOT NOT Valid
    }
    return;
}

void ApplyHaltLog(const long key, CtiPortSPtr Port, void* d)
{
    Port->haltLog();
    return;
}

CtiPortManager::CtiPortManager(CTI_PORTTHREAD_FUNC_PTR fn) :
_portThreadFunc(fn)
{}

CtiPortManager::~CtiPortManager()
{
}

void CtiPortManager::RefreshList(CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg)
{
    ptr_type pTempPort;

    bool rowFound = false;

    try
    {
        {
            // Reset everyone's Updated flag.
            if(!_smartMap.empty())
            {
                apply(ApplyResetUpdated, NULL);
            }

            _smartMap.resetErrorCode();

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
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rowFound, rdr, Factory, testFunc, arg);
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
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)  cout  << "Done looking for TCPIP Ports" << endl;
            }

            if(_smartMap.getErrorCode() != RWDBStatus::ok)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " database had a return code of " << _smartMap.getErrorCode() << endl;
                }
            }
            else
            {
                if(_smartMap.getErrorCode() != RWDBStatus::ok)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " database had a return code of " << _smartMap.getErrorCode() << endl;
                    }
                }
                else
                {
                    if(rowFound)
                    {
                        // Now I need to check for any Port removals based upon the
                        // Updated Flag being NOT set
                        apply(ApplyInvalidateNotUpdated, NULL);
                        pTempPort.reset();
                        do
                        {
                            pTempPort = _smartMap.remove(isNotUpdated, NULL);
                            if(pTempPort)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  Evicting " << pTempPort->getName() << " from list" << endl;
                                }
                                pTempPort.reset();      // Free the thing!
                            }

                        } while(pTempPort);
                    }
                }
            }
        }   // Temporary results are destroyed to free the connection
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiPortManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        CtiLockGuard<CtiMutex> gaurd(_mux);
        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            applyFun( itr->first, itr->second, d);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void CtiPortManager::DumpList(void)
{
    try
    {
        CtiLockGuard<CtiMutex> gaurd(_mux);
        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            itr->second->Dump();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPortManager::ptr_type CtiPortManager::PortGetEqual(LONG pid)
{
    ptr_type p;
    try
    {
        p = _smartMap.find(pid);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return p;
}

void CtiPortManager::RefreshEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg)
{
    LONG     lTemp = 0;
    ptr_type pTempPort;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;
        rdr["paobjectid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && (pTempPort = _smartMap.find(lTemp)) )
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

            pSp->setPortThreadFunc( _portThreadFunc );  // Make the thing know who runs it.

            if(pSp)
            {
                pSp->DecodeDatabaseReader(rdr);         // Fills himself in from the reader

                if(((*testFunc)(pSp, arg)))             // If I care about this point in the db in question....
                {
                    pSp->setUpdatedFlag();              // Mark it updated
                    pSp->setValid();
                    _smartMap.insert( pSp->getPortID(), pSp );    // Stuff it in the list
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

    ptr_type pPort = PortGetEqual(pid);

    if(pPort->isInhibited())
    {
        status = PORTINHIBITED;
    }
    else
    {
        if(pPort)
        {
            status = pPort->writeQueue(Request, DataSize, Data, Priority);
        }
        else
        {
            status = BADPORT;
        }
    }

    return status;
}


CTI_PORTTHREAD_FUNC_PTR CtiPortManager::setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn)
{
    CTI_PORTTHREAD_FUNC_PTR oldFn = _portThreadFunc;

    _portThreadFunc = aFn;

    return oldFn;
}

void CtiPortManager::haltLogs()
{
    // Reset everyone's Updated flag.
    if(!_smartMap.empty())
    {
        apply(ApplyHaltLog, NULL);
    }
}

CtiPortManager::spiterator CtiPortManager::begin()
{
    return _smartMap.getMap().begin();
}
CtiPortManager::spiterator CtiPortManager::end()
{
    return _smartMap.getMap().end();
}


