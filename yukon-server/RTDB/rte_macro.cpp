/*-----------------------------------------------------------------------------*
*
* File:   rte_macro
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_macro.cpp-arc  $
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "row_reader.h"

#include "dsm2.h"
#include "message.h"
#include "msg_pcrequest.h"
#include "dllbase.h"
#include "rte_macro.h"
#include "logger.h"
#include "guard.h"
#include "porter.h"
#include "utility.h"
#include "msg_pcreturn.h"

using std::endl;
using std::list;

using Cti::MacroOffset;

CtiRouteMacro::CtiRouteMacro()
{
}

void CtiRouteMacro::DumpData()
{
    Inherited::DumpData();

    for(int i = 0; i < RouteList.length(); i++)
        RouteList[i].DumpData();
}

CtiRouteMacro::CtiRouteList_t & CtiRouteMacro::getRouteList()
{
    return RouteList;
}
CtiRouteMacro::CtiRouteList_t   CtiRouteMacro::getRouteList() const
{
    return RouteList;
}

CtiRouteMacro::CtiRoutePtrList_t & CtiRouteMacro::getRoutePtrList()
{
    return RoutePtrList;
}
CtiRouteMacro::CtiRoutePtrList_t   CtiRouteMacro::getRoutePtrList() const
{
    return RoutePtrList;
}

void CtiRouteMacro::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

INT CtiRouteMacro::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT nRet = NoError;
    MacroOffset offset = (OutMessage->Request.RetryMacroOffset) ? OutMessage->Request.RetryMacroOffset : pReq->MacroOffset();

    try
    {
        bool allroutes = false;

        allroutes |= findStringIgnoreCase( parse.getCommandStr(), " allroutes" ) != 0; // If specified, all routes in the macro will get the command.
        allroutes |= (OutMessage->MessageFlags & MessageFlag_BroadcastOnMacroSubroutes) != 0;

        CtiLockGuard< CtiMutex > listguard(getRouteListMux());
        if( !allroutes && (OutMessage->EventCode & RESULT) && offset )       // If this is a two way request we want to walk the routelist.  Otherwise send on all subroutes.
        {
            if( *offset < RoutePtrList.length() )
            {
                CtiRouteSPtr pRoute = RoutePtrList[*offset];

                if(pRoute && pRoute.get() != this)  // No jerking around here thank you.
                {
                    if(pRoute->getType() != RouteTypeMacro)
                    {
                        OUTMESS *NewOMess = CTIDBG_new OUTMESS(*OutMessage); // Construct and copy.

                        if(NewOMess)
                        {
                            if( (*offset + 1) < RoutePtrList.length() )
                            {
                                NewOMess->Request.RetryMacroOffset = *offset + 1;    // Ask for this next (if needed) please.
                            }
                            else
                            {
                                NewOMess->Request.RetryMacroOffset = MacroOffset::none;    // None left MAKE IT STOP!.
                            }

                            if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Execute Macro Route Target \"" << pRoute->getName() << "\" " << pRoute->getRouteID()<< endl;
                            }

                            int status = pRoute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);

                            if(status == DEVICEINHIBITED && NewOMess && NewOMess->Request.RetryMacroOffset )
                            {
                                std::list< CtiMessage* >::iterator iter;
                                for(iter = retList.begin(); iter != retList.end(); iter++)
                                {
                                    if((*iter)->isA() == MSG_PCRETURN)
                                    {
                                        ((CtiReturnMsg*)(*iter))->setExpectMore(true);
                                    }
                                }
                                ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
                            }

                            if(NewOMess)
                            {
                                if(status != DEVICEINHIBITED && isDebugLudicrous())
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  Route " << pRoute->getName() << " did not clean up his mess." << endl;
                                }
                                delete NewOMess;
                                NewOMess = 0;
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ERROR!!!! CTIDBG_new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        NewOMess->Request.RouteID          = pRoute->getRouteID();
                        NewOMess->Request.RetryMacroOffset = MacroOffset::none; // No retry offset necessary, since we are already broadcasting to all routes

                        if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Execute Macro Route Target \"" << RoutePtrList[i]->getName() << "\" " << RoutePtrList[i]->getRouteID()<< endl;
                        }

                        pRoute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " ERROR!!!! CTIDBG_new memory failure " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    if(NewOMess)
                    {
                        delete NewOMess;
                        NewOMess = 0;
                    }
                }
            }
        }
        else
        {
            nRet = NoRoutesInMacro;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ERROR: Macro Route " << getName() << " has not resolved any sub-routes. " << endl;
        }

        /* if(OutMessage)       // 20050217 CGP.  This OM will be deleted elsewhere dev_base.cpp as OutMessageTemplate.
        {
            delete OutMessage;
            OutMessage = 0;
        } */
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return nRet;
}


void CtiRouteMacro::DecodeMacroReader(Cti::RowReader &rdr)
{
    CtiTableMacroRoute   MacroRoute;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    MacroRoute.DecodeDatabaseReader(rdr);
    RouteList.insert(MacroRoute);
}

bool CtiRouteMacro::processAdditionalRoutes( const INMESS &InMessage ) const
{
    if( ! InMessage.Return.RetryMacroOffset )
    {
        return false;
    }

    return (*InMessage.Return.RetryMacroOffset < getRoutePtrList().entries());
}

CtiMutex& CtiRouteMacro::getRouteListMux()
{
    return _routeListMux;
}
