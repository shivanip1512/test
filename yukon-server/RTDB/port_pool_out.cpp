
/*-----------------------------------------------------------------------------*
*
* File:   port_pool_out
*
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/29 13:18:22 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "port_pool_out.h"

INT CtiPortPoolDialout::openPort(INT rate, INT bits, INT parity, INT stopbits)
{
    INT status = NORMAL;

    return status;
}
INT CtiPortPoolDialout::inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = NORMAL;

    return status;
}

INT CtiPortPoolDialout::outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = NORMAL;

    return status;
}

void CtiPortPoolDialout::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    selector.where( selector.where() && keyTable["type"] == "Dialout Pool");
}

void CtiPortPoolDialout::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
}

void CtiPortPoolDialout::getPooledPortsSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable paoTable = db.table( "YukonPAObject" );
    keyTable = db.table( "PaoOwner" );

    selector <<
    paoTable["paobjectid"] <<
    keyTable["ownerid"] <<
    keyTable["childid"];

    selector.from(paoTable);
    selector.from(keyTable);

    selector.where( keyTable["ownerid"] == paoTable["paobjectid"] && paoTable["category"] == "PORT");
}

void CtiPortPoolDialout::DecodePooledPortsDatabaseReader(RWDBReader &rdr)
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

CtiPortPoolDialout& CtiPortPoolDialout::operator=(const CtiPortPoolDialout& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

size_t CtiPortPoolDialout::addPort(CtiPortSPtr port)
{
    _ports.push_back(port);
    return _ports.size();
}


/*
 *  This funciton attempts to locate an existing port which "currently owns" this device.
 *  Failing that, it returns the first port which has no device attached to it currently and has zero queue entries.
 *
 */
CtiPortSPtr CtiPortPoolDialout::getAvailableChildPort(CtiDevice* Device)
{
    CtiPortSPtr curport;
    CtiPortSPtr mtport;
    CtiPortPoolVector::iterator itr;

    // Look through all ports for one claiming this port as its own.
    for(itr = _ports.begin(); itr != _ports.end(); itr++)
    {
        curport = *itr;

        if(curport->getPortID() == 1)
        {
            break;
        }
        else
            if(curport->getConnectedDevice() == Device->getID())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << curport->getName() << " is processing for " << Device->getName() << " adding new work to this port queue" << endl;
            }

            break;
        }
        else if(curport->getConnectedDevice() <= 0)
        {
            mtport = curport;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " This is empty " << curport->getName() <<endl;
            }
        }
        else
        {

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " This is busy " << curport->getName() <<endl;
            }
        }

    }

    return curport;
}
