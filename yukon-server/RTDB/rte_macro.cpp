

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   rte_macro
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_macro.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/reader.h>

#include "dsm2.h"
#include "message.h"
#include "msg_pcrequest.h"
#include "dllbase.h"
#include "rte_macro.h"
#include "logger.h"

CtiRouteMacro::CtiRouteMacro() {}

CtiRouteMacro::CtiRouteMacro(const CtiRouteMacro& aRef)
{
    *this = aRef;
}

CtiRouteMacro::~CtiRouteMacro() {}

CtiRouteMacro& CtiRouteMacro::operator=(const CtiRouteMacro& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        RouteList = aRef.getRouteList();
    }
    return *this;
}

void CtiRouteMacro::DumpData()
{
    Inherited::DumpData();

    for(int i = 0; i < RouteList.length(); i++)
        RouteList[i].DumpData();
}

CtiRouteMacro::CtiRouteList_t & CtiRouteMacro::getRouteList()         { return RouteList;}
CtiRouteMacro::CtiRouteList_t   CtiRouteMacro::getRouteList() const   { return RouteList;}

CtiRouteMacro::CtiRoutePtrList_t & CtiRouteMacro::getRoutePtrList()         { return RoutePtrList;}
CtiRouteMacro::CtiRoutePtrList_t   CtiRouteMacro::getRoutePtrList() const   { return RoutePtrList;}


void CtiRouteMacro::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
}

void CtiRouteMacro::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
}



INT CtiRouteMacro::ExecuteRequest(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  RWTPtrSlist< CtiMessage >      &vgList,
                                  RWTPtrSlist< CtiMessage >      &retList,
                                  RWTPtrSlist< OUTMESS >         &outList)
{
    INT nRet = NORMAL;
    int offset = (OutMessage->Request.MacroOffset) > 0 ? OutMessage->Request.MacroOffset : pReq->MacroOffset();

    if( offset > 0 )
    {
        // This is a targeted request..  Offset is ONE based.
        offset = offset - 1;

        if( offset < RoutePtrList.length())
        {
            CtiRoute *&pRoute = RoutePtrList[offset];

            if(pRoute && pRoute != this)  // No jerking around here thank you.
            {
                if(pRoute->getType() != MacroRouteType)
                {
                    OUTMESS *NewOMess = new OUTMESS(*OutMessage); // Construct and copy.

                    if(NewOMess)
                    {
                        // NewOMess->Request.RouteID     = pRoute->getRouteID();
                        // NewOMess->Request.MacroOffset = offset + 1;    // Ask for this next please.

                        if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Execute Macro Route Target \"" << pRoute->getName() << "\" " << pRoute->getRouteID()<< endl;
                        }

                        pRoute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " ERROR!!!! new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  SKIPPING MACRO ROUTE IN MACRO ROUTE " << endl;
                    }
                    nRet = SubRouteIsMacro;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   NO MORE ROUTES IN THIS MACRO." << endl;
            }
            nRet = RouteOffsetOutOfRange;
        }
    }
    else if(RoutePtrList.length() > 0)
    {
        for(int i = 0; i < RoutePtrList.length(); i++)
        {
            CtiRoute *&pRoute = RoutePtrList[i];

            if(pRoute && pRoute != this)  // No jerking around here thank you.
            {
                OUTMESS *NewOMess = new OUTMESS(*OutMessage); // Construct and copy.

                if(NewOMess)
                {
                    NewOMess->Request.RouteID     = pRoute->getRouteID();
                    NewOMess->Request.MacroOffset = i+1;

                    if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Execute Macro Route Target \"" << RoutePtrList[i]->getName() << "\" " << RoutePtrList[i]->getRouteID()<< endl;
                    }

                    pRoute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ERROR!!!! new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    else
    {
        nRet = NoRoutesInMacro;
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ERROR: Macro Route " << getName() << " has not resolved any sub-routes. " << endl;
    }


    return nRet;
}


void CtiRouteMacro::DecodeMacroReader(RWDBReader &rdr)
{
    CtiTableMacroRoute   MacroRoute;

    if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

    MacroRoute.DecodeDatabaseReader(rdr);
    RouteList.insert(MacroRoute);
}

bool CtiRouteMacro::processAdditionalRoutes( INMESS *InMessage ) const
{
    bool bret = false;

    if(InMessage->Return.MacroOffset != 0)
    {
        if(getRoutePtrList().entries() >= InMessage->Return.MacroOffset + 1 )
        {
            bret = true;
        }
    }
    return bret;
}

