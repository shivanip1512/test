/*-----------------------------------------------------------------------------*
*
* File:   mgr_port
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_port.cpp-arc  $
* REVISION     :  $Revision: 1.28 $
* DATE         :  $Date: 2005/03/14 01:27:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>

#include "mgr_port.h"
#include "port_direct.h"
#include "port_dialout.h"
#include "port_pool_out.h"
#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "port_tcpip.h"
#include "utility.h"


/* SQL to get every column used */
/* Seems far more efficient that using 4 simpler queries without outer join */
// static const CHAR AllPortSql[] = "SELECT DISTINCT COMMPORT.PORTID,COMMPORT.PORTTYPE,COMMPORT.DISABLEFLAG,COMMPORT.ALARMINHIBIT,COMMPORT.DESCRIPTION,COMMPORT.COMMONPROTOCOL,PORTTIMING.PRETXWAIT,PORTTIMING.RTSTOTXWAIT,PORTTIMING.POSTTXWAIT,PORTTIMING.RECEIVEDATAWAIT,PORTSETTINGS.CDWAIT,PORTSETTINGS.BAUDRATE,PORTLOCALSERIAL.PHYSICALPORT,PORTTERMINALSERVER.IPADDRESS,PORTTERMINALSERVER.SOCKETPORTNUMBER,PORTDIALUPMODEM.INITIALIZATIONSTRING FROM COMMPORT,PORTTIMING,PORTRADIOSETTINGS,PORTLOCALSERIAL,PORTTERMINALSERVER,PORTSETTINGS,PORTDIALUPMODEM WHERE PORTSETTINGS.PORTID(+)=COMMPORT.PORTID AND PORTTIMING.PORTID(+)=COMMPORT.PORTID AND PORTRADIOSETTINGS.PORTID(+)=COMMPORT.PORTID AND PORTLOCALSERIAL.PORTID(+)=COMMPORT.PORTID AND PORTTERMINALSERVER.PORTID(+)=COMMPORT.PORTID AND PORTDIALUPMODEM.PORTID(+)=COMMPORT.PORTID";

bool findExecutingAndExcludedPort(const long key, CtiPortSPtr Port, void* d)
{
    bool bstatus = false;

    CtiPort *pAnxiousPort = (CtiPort *)d;       // This is the port that wishes to execute!

    if(pAnxiousPort->getPortID() != Port->getPortID())      // And it is not me...
    {
        bool portexcluded = pAnxiousPort->isPortExcluded(Port->getPortID());

        if(portexcluded)
        {
            // Ok, now decide if that excluded port is executing....
            bstatus = Port->isExecuting();
        }
    }

    return bstatus;
}

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

inline void applyRemoveProhibit(const long key, CtiPortSPtr Port, void* d)
{
    try
    {
    CtiPort *pAnxiousPort = (CtiPort *)d;       // This is the port that wishes to execute!
    LONG pid = (LONG)pAnxiousPort->getPortID();       // This is the port id which is to be pulled from the prohibition list.

    if(Port->isExecutionProhibited())   // There is at least one entry in the list...
    {
        bool found = Port->removeInfiniteExclusion( pid );

        if(found && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port->getName() << " no longer prohibited because of " << pAnxiousPort->getName() << "." << endl;
        }
    }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

inline void applyClearExclusions(const long key, CtiPortSPtr Port, void* d)
{
    Port->clearExclusions();
    return;
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

CtiPortManager::CtiPortManager(CTI_PORTTHREAD_FUNC_FACTORY_PTR fn) :
_portThreadFuncFactory(fn)
{}

extern void cleanupDB();

CtiPortManager::~CtiPortManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
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

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Direct Ports" << endl;
                }
                CtiPortDirect().getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Direct Ports" << endl;
                }
            }

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for TCPIP Ports" << endl;
                }
                CtiPortTCPIPDirect().getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for TCPIP Ports" << endl;
                }
            }


            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for DialABLE Ports" << endl;
                }
                CtiPortDialable::getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshDialableEntries(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for DialABLE Ports" << endl;
                }
            }

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Pool Ports" << endl;
                }
                CtiPortPoolDialout().getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshEntries(rowFound, rdr, Factory, testFunc, arg);

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Pool Ports" << endl;
                }
            }

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called

                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
                RWDBTable   keyTable;

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Pool Ports Children" << endl;
                }

                CtiPortPoolDialout::getPooledPortsSQL(db,keyTable,selector);
                RWDBReader  rdr = selector.reader( conn );
                if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }

                RefreshPooledPortEntries(rowFound, rdr, Factory, testFunc, arg);

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Pool Ports Children" << endl;
                }
            }

            refreshExclusions();

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
        int trycount = 0;

        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << " CtiPortManager::apply " << endl;
                }
                break;
            }
        }

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

CtiPortManager::ptr_type CtiPortManager::find(bool (*findFun)(const long, ptr_type, void*), void* d)
{
    ptr_type p;

    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            if( findFun( itr->first, itr->second, d ) )
            {
                p = itr->second;
                break;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return p;
}


void CtiPortManager::DumpList(void)
{
    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }

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
             *  update my list entry to the CTIDBG_new settings!
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
                pSp->DecodeDatabaseReader(rdr);         // Fills himself in from the reader

                if(_portThreadFuncFactory)
                {
                    pSp->setPortThreadFunc( (*_portThreadFuncFactory)( pSp->getType() ) );  // Make the thing know who runs it.
                }

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

void CtiPortManager::RefreshDialableEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg)
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
             *  update my list entry to the CTIDBG_new settings!
             */

            pTempPort->DecodeDialableDatabaseReader(rdr);     // Fills himself in from the reader
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << lTemp << " not found in pao, but IS in portdialupmodem table" << endl;
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


CTI_PORTTHREAD_FUNC_FACTORY_PTR CtiPortManager::setPortThreadFunc(CTI_PORTTHREAD_FUNC_FACTORY_PTR aFn)
{
    CTI_PORTTHREAD_FUNC_FACTORY_PTR oldFn = _portThreadFuncFactory;

    _portThreadFuncFactory = aFn;

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

void CtiPortManager::RefreshPooledPortEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg)
{
    LONG     owner = 0;
    LONG     child = 0;
    ptr_type pOwnerPort;
    ptr_type pChildPort;

    try
    {
        while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
        {
            rowFound = true;
            rdr["paobjectid"] >> owner;            // get the Port's paoobject id

            if( !_smartMap.empty() && (pOwnerPort = _smartMap.find(owner)) )
            {
                rdr["childid"] >> child;            // get the Port's paoobject id

                if((pChildPort = _smartMap.find(child)))
                {
                    pOwnerPort->addPort(pChildPort);
                    pChildPort->setParentPort(pOwnerPort);  // Link child back to parent.
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Unable to match child port id " << child << " to add to " << pOwnerPort->getName() << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


/*
 * ptr_type anxiousPort has asked to execute.  We make certain that no other port which is in his exclusion list is executing.
 */
bool CtiPortManager::mayPortExecuteExclusionFree(ptr_type anxiousPort, CtiTablePaoExclusion &portexclusion)
{
    bool bstatus = false;

    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }


        if(anxiousPort)
        {
            // Make sure no other port out there has begun executing and doesn't want us to until they are done.
            // The port may also have logic which prevents it's executing.
            if( !anxiousPort->isExecutionProhibited() && !anxiousPort->isExecutionProhibitedByInternalLogic() )
            {
                ptr_type port;
                spiterator itr;

                if(anxiousPort->hasExclusions())
                {
                    vector< ptr_type > exlist;

                    CtiPort::exclusions exvector = anxiousPort->getExclusions();
                    CtiPort::exclusions::iterator itr;

                    for(itr = exvector.begin(); itr != exvector.end(); itr++)
                    {
                        CtiTablePaoExclusion &paox = *itr;

                        switch(paox.getFunctionId())
                        {
                        case (CtiTablePaoExclusion::ExFunctionIdExclusion):
                            {
                                port = PortGetEqual(paox.getExcludedPaoId());

                                if(port)
                                {
                                    if(port->isExecuting())
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Port " << anxiousPort->getName() << " cannot execute because " << port->getName() << " is executing" << endl;
                                        }
                                        portexclusion = paox;   // Pass this out to the callee!
                                        exlist.clear();         // Cannot use it!
                                        break;                  // we cannot go
                                    }
                                    else
                                    {
                                        exlist.push_back(port);
                                    }
                                }

                                break;
                            }
                        default:
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                break;
                            }
                        }
                    }

                    if(!exlist.empty())     // This tells me that I have noconflicting points!
                    {
                        vector< ptr_type >::iterator xitr;
                        for(xitr = exlist.begin(); xitr != exlist.end(); xitr++)
                        {
                            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Port " << port->getName() << " prohibited because " << anxiousPort->getName() << " is executing" << endl;
                            }
                            port = *xitr;
                            port->setExecutionProhibited(anxiousPort->getPortID());
                        }
                        bstatus = true;
                    }
                }
                else
                {
                    bstatus = true;
                }
            }

            if(bstatus)
            {
                if(anxiousPort->hasExclusions() && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Port " << anxiousPort->getName() << " is clear to execute" << endl;
                }
                anxiousPort->setExecuting(true);                    // Mark ourselves as executing!
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

/*
 * ptr_type anxiousPort has completed an execution.  We must cleanup his mess.
 */
bool CtiPortManager::removePortExclusionBlocks(ptr_type anxiousPort)
{
    bool bstatus = false;

    try
    {
        if(anxiousPort)
        {
            apply( applyRemoveProhibit, (void*)anxiousPort.get());   // Remove prohibit mark from any port.
            anxiousPort->setExecuting(false);                               // Mark ourselves as executing!
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}


void CtiPortManager::refreshExclusions(LONG id)
{
    LONG     lTemp = 0;
    ptr_type pTempPort;

    LockGuard gaurd(getMux(), 30000);

    while(!gaurd.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        gaurd.tryAcquire(30000);
    }

    // Reset everyone's Updated flag.
    if(!_smartMap.empty())
    {
        apply(applyClearExclusions, NULL);
    }


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    // are out of scope when the release is called

    RWDBDatabase db = conn.database();
    RWDBSelector selector = conn.database().selector();
    RWDBTable   keyTable;

    if(DebugLevel & 0x00080000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Port Exclusions" << endl;
    }
    CtiTablePaoExclusion::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["paoid"] == id && selector.where());
    }

    RWDBReader  rdr = selector.reader( conn );
    if(DebugLevel & 0x00080000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["paoid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && (pTempPort = _smartMap.find(lTemp)) )
        {
            CtiTablePaoExclusion paox;

            paox.DecodeDatabaseReader(rdr);
            // Add this exclusion into the list.
            pTempPort->addExclusion(paox);
        }
    }

    if(DebugLevel & 0x00080000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Port Exclusions" << endl;
    }
}

