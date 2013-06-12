#include "precompiled.h"

#include "mgr_port.h"

#include "dbaccess.h"
#include "resolvers.h"
#include "utility.h"

#include "port_direct.h"
#include "port_dialin.h"
#include "port_dialout.h"
#include "port_pool_out.h"
#include "port_tcpipdirect.h"
#include "port_tcp.h"
#include "port_udp.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;
using std::vector;


bool findExecutingAndExcludedPort(const long key, CtiPortSPtr Port, void* d)
{
    CtiPort *pAnxiousPort = (CtiPort *)d;       // This is the port that wishes to execute!

    if(pAnxiousPort->getPortID() != Port->getPortID())      // And it is not me...
    {
        bool portexcluded = pAnxiousPort->isPortExcluded(Port->getPortID());

        if(portexcluded)
        {
            // Ok, now decide if that excluded port is executing....
            return Port->isExecuting();
        }
    }

    return false;
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
                dout << CtiTime() << " Port " << Port->getName() << " no longer prohibited because of " << pAnxiousPort->getName() << "." << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

inline void applyClearExclusions(const long key, CtiPortSPtr Port, void* d)
{
    Port->clearExclusions();
}

inline void ApplyResetUpdated(const long key, CtiPortSPtr Port, void* d)
{
    Port->resetUpdatedFlag();
}

void ApplyInvalidateNotUpdated(const long key, CtiPortSPtr Port, void* d)
{
    if(!Port->getUpdatedFlag())
    {
        Port->setValid(FALSE);   //   NOT NOT NOT Valid
    }
}

void ApplyHaltLog(const long key, CtiPortSPtr Port, void* d)
{
    Port->haltLog();
}

CtiPortManager::CtiPortManager(CTI_PORTTHREAD_FUNC_FACTORY_PTR fn) :
_portThreadFuncFactory(fn)
{}

CtiPortManager::~CtiPortManager()
{
}

void CtiPortManager::RefreshList()
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
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Direct Ports" << endl;
                }

                static const string sql = CtiPortDirect::getSQLCoreStatement();

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshEntries(rowFound, rdr);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Direct Ports" << endl;
                }
            }

            {
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for TCPIP Terminal Server Ports" << endl;
                }

                static const string sql = CtiPortTCPIPDirect::getSQLCoreStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshEntries(rowFound, rdr);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for TCPIP Terminal Server Ports" << endl;
                }
            }

            {
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for TCP Ports" << endl;
                }

                const string sql = string(Cti::Ports::TcpPort::getSQLCoreStatement() + " AND YP.type = 'TCP'");

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshEntries(rowFound, rdr);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for TCP Ports" << endl;
                }
            }
            {
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for DialABLE Ports" << endl;
                }

                static const string sql = CtiPortDialable::getSQLCoreStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshDialableEntries(rowFound, rdr);
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for DialABLE Ports" << endl;
                }
            }

            {
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Pool Ports" << endl;
                }
                static const string sql = CtiPortPoolDialout::getSQLCoreStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshEntries(rowFound, rdr);

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Pool Ports" << endl;
                }
            }

            {
                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Pool Ports Children" << endl;
                }

                static const string sql = CtiPortPoolDialout::getSQLPooledPortsStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00080000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }

                RefreshPooledPortEntries(rowFound, rdr);

                if(DebugLevel & 0x00080000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Done looking for Pool Ports Children" << endl;
                }
            }

            if( !refreshExclusions() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Evicting " << pTempPort->getName() << " from list" << endl;
                            }
                            pTempPort.reset();      // Free the thing!
                        }

                    } while(pTempPort);
                }
            }
        }   // Temporary results are destroyed to free the connection
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiPortManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        coll_type::reader_lock_guard_t guard(getLock(), 30000);

        while(!guard.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock port mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << static_cast<string>(getLock()) << endl;
            }
            guard.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << " CtiPortManager::apply " << endl;
                }
                break;
            }
        }

        spiterator itr, itr_end = _smartMap.getMap().end();

        for(itr = _smartMap.getMap().begin(); itr != itr_end; itr++)
        {
            applyFun( itr->first, itr->second, d);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


struct PortNotNull
{
    bool operator()(CtiPortSPtr p) const
    {
        return p;
    }
};


std::vector<CtiPortSPtr> CtiPortManager::getPorts() const
{
    return _smartMap.findAll(PortNotNull());
}



CtiPortManager::ptr_type CtiPortManager::getPortById(LONG pid)
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return p;
}

void CtiPortManager::RefreshEntries(bool &rowFound, Cti::RowReader& rdr)
{
    LONG     portID = 0;
    ptr_type tempPort;

    boost::shared_ptr<CtiPortTCPIPDirect> tempPortTCP;
    string oldIP;
    int    oldPort;

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
    {
        rowFound = true;
        rdr["paobjectid"] >> portID;            // get the RouteID

        if( !_smartMap.empty() && (tempPort = _smartMap.find(portID)) )
        {
            /*
             *  The port just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new settings!
             */

            //  Save off the TCP settings in case they changed - we may need to reconnect
            if( tempPort->getType() == PortTypeTServerDirect ||
                tempPort->getType() == PortTypeUdp )
            {
                tempPortTCP = boost::static_pointer_cast<CtiPortTCPIPDirect>(tempPort);

                oldIP   = tempPortTCP->getIPAddress();
                oldPort = tempPortTCP->getIPPort();
            }

            tempPort->DecodeDatabaseReader(rdr);     // Fills himself in from the reader
            tempPort->setUpdatedFlag();              // Mark it updated
            tempPort->setValid();

            if( tempPortTCP )
            {
                //  If the IP or port changed, disconnect
                if( tempPortTCP->getIPAddress().compare(oldIP) || (tempPortTCP->getIPPort() != oldPort) )
                {
                    tempPortTCP->shutdownClose();

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Port " << tempPortTCP->getName() << " reconnecting due to DBChange " << __FILE__ << "(" << __LINE__ << ")" << endl;
                    }
                }

                tempPortTCP.reset();
            }
        }
        else
        {
            CtiPort* pSp = PortFactory(rdr);  // Use the reader to get me an object of the proper type

            if(pSp)
            {
                pSp->DecodeDatabaseReader(rdr);         // Fills himself in from the reader

                if(_portThreadFuncFactory)
                {
                    pSp->setPortThreadFunc( (*_portThreadFuncFactory)( pSp->getType() ) );  // Make the thing know who runs it.
                }

                pSp->setUpdatedFlag();              // Mark it updated
                pSp->setValid();
                _smartMap.insert( pSp->getPortID(), pSp );    // Stuff it in the list
            }
        }
    }
}

void CtiPortManager::RefreshDialableEntries(bool &rowFound, Cti::RowReader& rdr)
{
    LONG     lTemp = 0;
    ptr_type pTempPort;

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
    {
        rowFound = true;
        rdr["paobjectid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && (pTempPort = _smartMap.find(lTemp)) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new settings!
             */

            pTempPort->DecodeDialableDatabaseReader(rdr);     // Fills himself in from the reader
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << lTemp << " not found in pao, but IS in portdialupmodem table" << endl;
            }
        }
    }
}



INT CtiPortManager::writeQueue(OUTMESS *OutMessage)
{
    ptr_type pPort = getPortById(OutMessage->Port);

    if( ! pPort )
    {
        return BADPORT;
    }

    if( pPort->isInhibited() )
    {
        return PORTINHIBITED;
    }

    return pPort->writeQueue(OutMessage);
}


INT CtiPortManager::writeQueueWithPriority(OUTMESS *OutMessage, int priority)
{
    ptr_type pPort = getPortById(OutMessage->Port);

    if( ! pPort )
    {
        return BADPORT;
    }

    if( pPort->isInhibited() )
    {
        return PORTINHIBITED;
    }

    return pPort->writeQueueWithPriority(OutMessage, priority);
}


void CtiPortManager::haltLogs()
{
    // Reset everyone's Updated flag.
    if(!_smartMap.empty())
    {
        apply(ApplyHaltLog, NULL);
    }
}

void CtiPortManager::RefreshPooledPortEntries(bool &rowFound, Cti::RowReader& rdr)
{
    LONG     owner = 0;
    LONG     child = 0;
    ptr_type pOwnerPort;
    ptr_type pChildPort;

    try
    {
        while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
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
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Unable to match child port id " << child << " to add to " << pOwnerPort->getName() << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        coll_type::reader_lock_guard_t guard(getLock(), 30000);

        while(!guard.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            guard.tryAcquire(30000);
        }


        if(anxiousPort)
        {
            // Make sure no other port out there has begun executing and doesn't want us to until they are done.
            // The port may also have logic which prevents it's executing.
            if( !anxiousPort->isExecutionProhibited() && !anxiousPort->isExecutionProhibitedByInternalLogic() )
            {
                ptr_type port;

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
                                port = getPortById(paox.getExcludedPaoId());

                                if(port)
                                {
                                    if(port->isExecuting())
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Port " << anxiousPort->getName() << " cannot execute because " << port->getName() << " is executing" << endl;
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
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                break;
                            }
                        }
                    }

                    if(!exlist.empty())     // This tells me that I have noconflicting points!
                    {
                        std::vector< ptr_type >::iterator xitr;
                        for(xitr = exlist.begin(); xitr != exlist.end(); xitr++)
                        {
                            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Port " << port->getName() << " prohibited because " << anxiousPort->getName() << " is executing" << endl;
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
                    dout << CtiTime() << " Port " << anxiousPort->getName() << " is clear to execute" << endl;
                }
                anxiousPort->setExecuting(true);                    // Mark ourselves as executing!
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

bool CtiPortManager::refreshExclusions(LONG id)
{
    LONG     lTemp = 0;
    ptr_type pTempPort;

    coll_type::writer_lock_guard_t guard(getLock(), 30000);

    while(!guard.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        guard.tryAcquire(30000);
    }

    // Reset everyone's Updated flag.
    if(!_smartMap.empty())
    {
        apply(applyClearExclusions, NULL);
    }

    if(DebugLevel & 0x00080000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout  << "Looking for Port Exclusions" << endl;
    }

    const string sql = CtiTablePaoExclusion::getSQLCoreStatement(id) ;

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    if(id > 0)
    {
        rdr << id;
    }

    rdr.execute();

    if(DebugLevel & 0x00080000 || !rdr.isValid())
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
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

    return rdr.isValid();
}


CtiPort* CtiPortManager::PortFactory(Cti::RowReader &rdr)
{
    string portTypeString;

    rdr["type"]  >> portTypeString;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Creating a Port of type " << portTypeString << endl;
    }

    switch( resolvePortType(portTypeString) )
    {
        case PortTypeLocalDirect:
            return new CtiPortDirect;

        case PortTypeLocalDialup:
            return new CtiPortDirect( new CtiPortDialout );

        case PortTypeLocalDialBack:
            return new CtiPortDirect( new CtiPortDialin );

        case PortTypeTServerDirect:
            return new CtiPortTCPIPDirect;

        case PortTypeTcp:
            return new Cti::Ports::TcpPort;

        case PortTypeUdp:
            return new Cti::Ports::UdpPort;

        case PortTypeTServerDialup:
            return new CtiPortTCPIPDirect( new CtiPortDialout );

        case PortTypeTServerDialBack:
            return new CtiPortTCPIPDirect( new CtiPortDialin );

        case PortTypePoolDialout:
            return new CtiPortPoolDialout;

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port Factory has failed to produce for type " << portTypeString << "!" << endl;

            return 0;
        }
    }
}

