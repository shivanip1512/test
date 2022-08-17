#include "precompiled.h"

#include "rte_macro.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "porter.h"  //  for RESULT

using std::endl;
using std::list;

using Cti::MacroOffset;

namespace Cti {
namespace Routes {

MacroRoute::MacroRoute()
{
}

std::string MacroRoute::toString() const
{
    Cti::FormattedList itemList;
    itemList <<"CtiRouteMacro";

    int i = 0;
    for each( const Tables::MacroRouteTable &subroute in RouteList )
    {
        itemList <<"Route "<< ++i <<":";
        itemList << subroute;
    }

    return (Inherited::toString() += itemList.toString());
}

std::vector<long> MacroRoute::getSubrouteIds() const
{
    std::vector<long> ids;

    std::transform(
            RouteList.begin(),
            RouteList.end(),
            std::back_inserter(ids),
            boost::bind(&Tables::MacroRouteTable::getSingleRouteID, _1));

    return ids;
}

void MacroRoute::addSubroute(CtiRouteSPtr rte)
{
    RoutePtrList.push_back(rte);
}

CtiRouteSPtr MacroRoute::getSubroute(const unsigned offset) const
{
    if( offset < RoutePtrList.size() )
    {
        return RoutePtrList[offset];
    }
    return CtiRouteSPtr();
}

void MacroRoute::clearSubroutes()
{
    RouteList.clear();
    RoutePtrList.clear();
}


void MacroRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
}

YukonError_t MacroRoute::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    MacroOffset offset = (OutMessage->Request.RetryMacroOffset) ? OutMessage->Request.RetryMacroOffset : pReq->MacroOffset();

    try
    {
        bool allroutes = false;

        allroutes |= findStringIgnoreCase( parse.getCommandStr(), " allroutes" ) != 0; // If specified, all routes in the macro will get the command.
        allroutes |= (OutMessage->MessageFlags & MessageFlag_BroadcastOnMacroSubroutes) != 0;

        CtiLockGuard< CtiMutex > listguard(getMacroMux());

        //  Iterate over the subroutes if we're not sending on all routes AND this message expects a response
        if( !allroutes && (OutMessage->EventCode & RESULT) && offset )
        {
            if( *offset >= RoutePtrList.size() )
            {
                CTILOG_WARN(dout, "No more routes in this macro");

                return ClientErrors::RouteOffsetOutOfRange;
            }

            CtiRouteSPtr pRoute = RoutePtrList[*offset];

            if( ! pRoute || pRoute.get() == this)  // No jerking around here thank you.
            {
                return ClientErrors::None;
            }

            if(pRoute->getType() == RouteTypeMacro)
            {
                CTILOG_WARN(dout, "Skipping macro route in macro route")

                return ClientErrors::SubRouteIsMacro;
            }

            OUTMESS *NewOMess = new OUTMESS(*OutMessage); // Construct and copy.

            if( (*offset + 1) < RoutePtrList.size() )
            {
                NewOMess->Request.RetryMacroOffset = *offset + 1;    // Ask for this next (if needed) please.
            }
            else
            {
                NewOMess->Request.RetryMacroOffset = MacroOffset::none;    // None left MAKE IT STOP!.
            }

            if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
            {
                CTILOG_DEBUG(dout, "Execute Macro Route Target \""<< pRoute->getName() <<"\" "<< pRoute->getRouteID());
            }

            YukonError_t status = pRoute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);

            //  if the transmitter device was inhibited and we have another route to try...
            if(status == ClientErrors::DeviceInhibited && NewOMess && NewOMess->Request.RetryMacroOffset )
            {
                //  set all return messages to expect more...
                for each( CtiMessage *msg in retList )
                {
                    if( msg->isA() == MSG_PCRETURN)
                    {
                        static_cast<CtiReturnMsg *>(msg)->setExpectMore(true);
                    }
                }
                //  and perform a recursive call to execute on the next subroute
                ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
            }

            if(NewOMess)
            {
                if(status != ClientErrors::DeviceInhibited && isDebugLudicrous())
                {
                    CTILOG_DEBUG(dout, "Route "<< pRoute->getName() <<" did not clean up his mess");
                }

                delete NewOMess;
                NewOMess = 0;
            }

            return ClientErrors::None;
        }

        if( RoutePtrList.empty() )
        {
            CTILOG_ERROR(dout, "Macro Route "<< getName() <<" has not resolved any sub-routes");

            return ClientErrors::NoRoutesInMacro;
        }

        for each( CtiRouteSPtr subroute in RoutePtrList )
        {
            if(subroute && subroute.get() != this)  // No jerking around here thank you.
            {
                OUTMESS *NewOMess = new OUTMESS(*OutMessage); // Construct and copy.

                NewOMess->Request.RouteID          = subroute->getRouteID();
                NewOMess->Request.RetryMacroOffset = MacroOffset::none; // No retry offset necessary, since we are already broadcasting to all routes

                if(getDebugLevel() & DEBUGLEVEL_MGR_ROUTE)
                {
                    CTILOG_DEBUG(dout, "Execute Macro Route Target \""<< subroute->getName() <<"\" "<< subroute->getRouteID());
                }

                subroute->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);

                if(NewOMess)
                {
                    delete NewOMess;
                    NewOMess = 0;
                }
            }
        }

        if(!retList.empty())
        {
            auto itr = retList.rbegin();

            // Set expectMore on the CtiReturnMsgs, but don't touch the last one if there was no OM sent.
            //   That probably means the command is done, but do NOT unset it in case it was manually set.
            ++itr;

            while(itr != retList.rend())
            {
                static_cast<CtiReturnMsg *>(*itr++)->setExpectMore(true);
            }
        }

        /* if(OutMessage)       // 20050217 CGP.  This OM will be deleted elsewhere dev_base.cpp as OutMessageTemplate.
        {
            delete OutMessage;
            OutMessage = 0;
        } */
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}


void MacroRoute::DecodeSubrouteReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    Cti::Tables::MacroRouteTable MacroRoute(rdr);

    RouteList.insert(MacroRoute);
}

bool MacroRoute::processAdditionalRoutes( const INMESS &InMessage ) const
{
    if( ! InMessage.Return.RetryMacroOffset )
    {
        return false;
    }

    return (*InMessage.Return.RetryMacroOffset < RoutePtrList.size());
}

CtiMutex& MacroRoute::getMacroMux()
{
    return _routeListMux;
}

}
}
