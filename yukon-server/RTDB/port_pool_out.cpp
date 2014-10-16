/*-----------------------------------------------------------------------------*
*
* File:   port_pool_out
*
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cparms.h"
#include "port_pool_out.h"

using std::string;
using std::endl;
using std::list;

int CtiPortPoolDialout::_poolDebugLevel = 0;

YukonError_t CtiPortPoolDialout::openPort(INT rate, INT bits, INT parity, INT stopbits)
{
    YukonError_t status = ClientErrors::None;

    return status;
}
YukonError_t CtiPortPoolDialout::inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, list< CtiMessage* > &traceList)
{
    return ClientErrors::None;
}

YukonError_t CtiPortPoolDialout::outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, list< CtiMessage* > &traceList)
{
    return ClientErrors::None;
}

string CtiPortPoolDialout::getSQLCoreStatement()
{
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                   "CP.alarminhibit, CP.commonprotocol, CP.performancealarm, CP.performthreshold, "
                                   "CP.sharedporttype, CP.sharedsocketnumber "
                               "FROM YukonPAObject YP, CommPort CP "
                               "WHERE YP.paobjectid = CP.portid AND upper (YP.type) = 'DIALOUT POOL'";

    return sql;
}

void CtiPortPoolDialout::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    CtiPortPoolDialout::_poolDebugLevel = gConfigParms.getValueAsULong("PORTPOOL_DEBUGLEVEL", 0, 16);
    Inherited::DecodeDatabaseReader(rdr);
}

string CtiPortPoolDialout::getSQLPooledPortsStatement()
{
    static const string sql =  "SELECT YP.paobjectid, PO.ownerid, PO.childid "
                               "FROM YukonPAObject YP, PaoOwner PO "
                               "WHERE PO.ownerid = YP.paobjectid AND upper (YP.category) = 'PORT'";

    return sql;
}

void CtiPortPoolDialout::DecodePooledPortsDatabaseReader(Cti::RowReader &rdr)
{
    long owner;
    long child;

    rdr["ownerid"] >> owner;
    rdr["childid"] >> child;

    if(getPortID() == owner)
    {
        _portids.push_back(child);      // This should build up the list of Id's we will use.
    }
}

size_t CtiPortPoolDialout::addPort(CtiPortSPtr port)
{
    if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDADDITION)
    {
        CTILOG_DEBUG(dout, "Port "<< getName() <<" adding "<< port->getName() <<" to child list");
    }

    _ports.push_back(port);
    return _ports.size();
}


/*
 *  This funciton attempts to locate an existing port which "currently owns" this device.
 *  Failing that, it returns the first port which has no device attached to it currently and has zero queue entries.
 *
 */
CtiPortSPtr CtiPortPoolDialout::getAvailableChildPort(CtiDeviceSPtr  Device)
{
    CtiPortSPtr resultport;

    try
    {
        CtiPortSPtr curport;
        CtiPortSPtr matchport;
        CtiPortSPtr idlependingport;
        CtiPortSPtr vacantport;
        CtiPortSPtr vacantquestionableport;
        CtiPortPoolVector::iterator itr;

        ULONG devguid = Device->getUniqueIdentifier();

        // Is there a port which claims this device as its connected device?
        // Is there a port which claims this device as it GUID device?
        // Is there a port which has no other work to do?

        // Look through all ports for one claiming this device as its own.
        for(itr = _ports.begin(); itr != _ports.end(); itr++)
        {
            curport = *itr;

            if( !(curport->isInhibited()) )
            {
                if(curport->getPoolAssignedGUID() > 0 )
                {
                    if( curport->getPoolAssignedGUID() == devguid )
                    {
                        // This is a MATCH port!!  It is already working for this device!!
                        CTILOG_TRACE(dout, "Child port "<< curport->getName() <<" has a GUID match for paoId "<< Device->getID() <<" / "<< Device->getName());

                        matchport = curport;
                        break;
                    }
                    else
                    {
                        if( !idlependingport && curport->isMinMaxIdle() && !curport->isQuestionable() )
                        {
                            idlependingport = curport; // This port could be used if no other port is found!

                            CTILOG_TRACE(dout, "Child port "<< curport->getName() <<" is busy/idle/pending with paoId "<< curport->getConnectedDevice());
                        }
                        else if((CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDAVAILABILITY) && (curport->queueCount() > 0 || curport->getConnectedDevice() || curport->getPoolAssignedGUID()))
                        {
                            CTILOG_DEBUG(dout, "Child port "<< curport->getName() <<" is busy with paoId/guid "<< curport->getConnectedDevice() <<" / "<< curport->getPoolAssignedGUID());
                        }
                    }
                }
                else if( (CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDAVAILABILITY) && curport->queueCount() > 0 )
                {
                    CTILOG_DEBUG(dout, "Child port "<< curport->getName() <<" is busy with unknown paoId");
                }
                else if( !vacantport )
                {
                    if(curport->isQuestionable())
                    {
                        CTILOG_TRACE(dout, "Child port "<< curport->getName() <<" is idle/questionable");

                        // This port is free for execution! He also represents a last resort because he is questionable!
                        vacantquestionableport = curport;
                    }
                    else
                    {
                        CTILOG_TRACE(dout, "Child port "<< curport->getName() <<" is idle");

                        vacantport = curport;   // This port is free for execution!  Keep looking in case we don't find a match
                    }
                }
            }
        }

        if(matchport)
        {
            resultport = matchport;

            if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDSELECTION)
            {
                CTILOG_DEBUG(dout, "Port "<< resultport->getName() <<" selected due to MATCH");
            }
        }
        else if(vacantport)                                     // Use the vacant port first.
        {
            vacantport->setPoolAssignedGUID(devguid);
            resultport = vacantport;
            if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDSELECTION)
            {
                CTILOG_DEBUG(dout, "Port "<< resultport->getName() <<" selected due to VACANT");
            }
        }
        else if(idlependingport)                                // Next choose a port which would hang up if new work were allocated!.
        {
            idlependingport->setPoolAssignedGUID(devguid);
            resultport = idlependingport;
            if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDSELECTION)
            {
                CTILOG_DEBUG(dout, "Port "<< resultport->getName() <<" selected due to IDLEPENDING");
            }
        }
        else if(vacantquestionableport)                         // Last use the vacant and questionable port.
        {
            vacantquestionableport->setPoolAssignedGUID(devguid);
            resultport = vacantquestionableport;
            if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDSELECTION)
            {
                CTILOG_DEBUG(dout, "Port "<< resultport->getName() <<" selected due to VACANTQUESTIONABLE");
            }
        }
        else
        {
            if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDSELECTION)
            {
                CTILOG_DEBUG(dout, "Port "<< getName() <<" has no child port able to queue up work for "<< Device->getName() <<" id: "<< Device->getID() <<" guid: "<< devguid);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return resultport;
}

/*
 *  This function searchs the pool port queue looking for any OM which match the GUID which is already assigned to the port.
 *
 */
INT CtiPortPoolDialout::allocateQueueEntsToChildPort()
{
    INT status = ClientErrors::None;
    INT qloc;

    CtiPortSPtr childport;
    CtiPortPoolVector::iterator itr;

    try
    {
        if(queueCount() > 0)
        {
            status = PPSC_AllChildrenBusy;  // Assume this and reset on detect!.

            // Look through all ports for one claiming this port as its own.
            for(itr = _ports.begin(); itr != _ports.end(); itr++)
            {
                childport = *itr;

                if(!childport->isInhibited() && !childport->isQuestionable() )
                {
                    if(childport->getPoolAssignedGUID() == 0)
                    {
                        status = PPSC_ChildReady;
                    }
                    else
                    {
                        do
                        {
                            qloc = searchPortQueue( (void*)childport->getPoolAssignedGUID(), searchFuncForOutMessageUniqueID );
                            if(qloc > 0)
                            {
                                OUTMESS        *OutMessage;
                                BYTE           ReadPriority;
                                ULONG          QueEntries;
                                ULONG          ReadLength;

                                setQueueSlot(qloc);
                                // Move the OM from the pool queue to the child queue.
                                if( readQueue( &ReadLength, (PPVOID) &OutMessage, DCWW_WAIT, &ReadPriority, &QueEntries ) == ClientErrors::None )
                                {
                                    childport->writeQueue( OutMessage );

                                    if(CtiPortPoolDialout::_poolDebugLevel & PORTPOOL_DEBUGLEVL_CHILDALLOCATION)
                                    {
                                        CTILOG_DEBUG(dout, "Allocating OutMessage from pool queue to child port "<< childport->getName() <<". Child has "<< childport->queueCount() <<" queue entries");
                                    }
                                }
                            }
                        }
                        while(qloc != 0);
                    }
                }
            }
        }
        else
        {
            status = PPSC_ParentQueueEmpty;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}
