/*-----------------------------------------------------------------------------*
*
* File:   rte_macro
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_macro.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/03/13 19:36:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


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

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}



INT CtiRouteMacro::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NORMAL;
    int onebasedoffset = (OutMessage->Request.MacroOffset) > 0 ? OutMessage->Request.MacroOffset : pReq->MacroOffset();
    int offset = onebasedoffset - 1; // This is a targeted request..  Incomming offset is ONE based.

    if( onebasedoffset > 0 )
    {
        if( offset < RoutePtrList.length())
        {
            CtiRouteSPtr pRoute = RoutePtrList[offset];

            if(pRoute && pRoute.get() != this)  // No jerking around here thank you.
            {
                if(pRoute->getType() != MacroRouteType)
                {
                    OUTMESS *NewOMess = CTIDBG_new OUTMESS(*OutMessage); // Construct and copy.

                    if(NewOMess)
                    {
                        if(onebasedoffset < RoutePtrList.length())
                        {
                            NewOMess->Request.MacroOffset = onebasedoffset + 1;    // Ask for this next (if needed) please.
                        }
                        else
                        {
                            NewOMess->Request.MacroOffset = 0;    // None left MAKE IT STOP!.
                        }


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
                        dout << RWTime() << " ERROR!!!! CTIDBG_new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            CtiRouteSPtr pRoute = RoutePtrList[i];

            if(pRoute && pRoute.get() != this)  // No jerking around here thank you.
            {
                OUTMESS *NewOMess = CTIDBG_new OUTMESS(*OutMessage); // Construct and copy.

                if(NewOMess)
                {
                    NewOMess->Request.RouteID     = pRoute->getRouteID();
                    NewOMess->Request.MacroOffset = 0; // 20020523 CGP  Oh golly. We say zero since we already will be hitting them on the way by. // i+1;

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
                    dout << RWTime() << " ERROR!!!! CTIDBG_new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    if(InMessage->Return.MacroOffset > 0)
    {
        if(getRoutePtrList().entries() >= InMessage->Return.MacroOffset )
        {
            bret = true;
        }
    }
    return bret;
}

