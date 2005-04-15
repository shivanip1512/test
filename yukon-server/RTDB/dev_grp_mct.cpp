/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_mct
*
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/04/15 19:04:10 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_grp_mct.h"
#include "porter.h"
#include "numstr.h"
#include "prot_emetcon.h"

using Cti::Protocol::Emetcon;


CtiDeviceGroupMCT::CtiDeviceGroupMCT()
{
}

CtiDeviceGroupMCT::CtiDeviceGroupMCT( const CtiDeviceGroupMCT &aRef )
{
    *this = aRef;
}

CtiDeviceGroupMCT::~CtiDeviceGroupMCT()
{
}


CtiDeviceGroupMCT &CtiDeviceGroupMCT::operator=( const CtiDeviceGroupMCT &aRef )
{
    if( this != &aRef )
    {

    }

    return *this;
}

LONG CtiDeviceGroupMCT::getRouteID( void )
{
    return _lmGroupMCT.getRouteID();
}


RWCString CtiDeviceGroupMCT::getDescription( const CtiCommandParser &parse ) const
{
    //  ACH:  fix this up with tasty relay and address descriptions sometime
    return getName();
}


void CtiDeviceGroupMCT::getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector )
{
    Inherited::getSQL(db, keyTable, selector);

    _lmGroupMCT.getSQL(db, keyTable, selector);
}


void CtiDeviceGroupMCT::DecodeDatabaseReader( RWDBReader &rdr )
{
    Inherited::DecodeDatabaseReader(rdr);

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _lmGroupMCT.DecodeDatabaseReader(rdr);
}


LONG CtiDeviceGroupMCT::getAddress() const
{
    int address = 0;

    switch( _lmGroupMCT.getAddressLevel() )
    {
        case CtiTableLMGroupMCT::Addr_Bronze:
        {
            address  = MCTGroup_BronzeAddr_Base;
            address += _lmGroupMCT.getAddress();

            break;
        }

        case CtiTableLMGroupMCT::Addr_Lead:
        {
            address  = MCTGroup_LeadLoadAddr_Base;
            address += _lmGroupMCT.getAddress();

            break;
        }

        case CtiTableLMGroupMCT::Addr_Unique:
        {
            CtiDeviceBase *tmpDevice;

            if( _lmGroupMCT.getMCTUniqueAddress() )
            {
                address = _lmGroupMCT.getMCTUniqueAddress();
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "MCT Load Group \"" << getName() << "\" has unique address 0, aborting command" << endl;
                }

                address = 0;
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unknown/bad address level specifier in calcMCTLMGroupAddress() for MCT loadgroup \"" << getName() << "\"" << endl;
            }

            address = 0;
        }
    }

    return address;
}


INT CtiDeviceGroupMCT::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT        nRet  = NoError;
    CtiRouteSPtr Route;
    RWCString  resultString;
    long routeID;
    char Temp[80];

    bool found = false;
    CtiReturnMsg* pRet = 0;

    switch( parse.getCommand( ) )
    {
        case ControlRequest:
        {
            nRet = executeControl( pReq, parse, OutMessage, vgList, retList, outList );

            break;
        }
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command to MCT loadgroup \"" << getName( ) << "\". Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " Couldn't come up with an operation for MCT loadgroup \"" << getName( ) << "\"" << endl;
            dout << RWTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert( CTIDBG_new CtiReturnMsg(getID( ),
                                         RWCString(OutMessage->Request.CommandStr),
                                         resultString,
                                         nRet,
                                         OutMessage->Request.RouteID,
                                         OutMessage->Request.MacroOffset,
                                         OutMessage->Request.Attempt,
                                         OutMessage->Request.TrxID,
                                         OutMessage->Request.UserID,
                                         OutMessage->Request.SOE,
                                         RWOrdered( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            outList.append( OutMessage );
            OutMessage = NULL;
        }
        /*
         ***************************** PASS OFF TO ROUTE BEYOND THIS POINT ****************************************
         */

        for(int i = outList.entries() ; i > 0; i-- )
        {
            OUTMESS *pOut = outList.get();

            if( pReq->RouteId() )
            {
                routeID = pReq->RouteId();
            }
            else
            {
                routeID = getRouteID();
            }

            pOut->Request.RouteID = routeID;

            EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

            if( (Route = CtiDeviceBase::getRoute( pOut->Request.RouteID )) )
            {
                pOut->TargetID                = getID();
                pOut->EventCode = BWORD;

                if( parse.isKeyValid("noqueue") )
                {
                    pOut->EventCode |= DTRAN;
                    //  pOut->EventCode &= ~QUEUED;
                }

                pOut->Buffer.BSt.Address      = getAddress();            // The DLC address of the MCT.
                pOut->Buffer.BSt.DeviceType   = getType();
                pOut->Buffer.BSt.SSpec        = 0;  //  ACH FIX what-huh?

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().entries())
                {
                    for(size_t offset = offset; offset < parse.getActionItems().entries(); offset++)
                    {
                        RWCString actn = parse.getActionItems()[offset];
                        RWCString desc = getDescription(parse);

                        vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(pOut->Request.CommandStr), Route->getName(), nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.TrxID, pOut->Request.UserID, pOut->Request.SOE, RWOrdered());
                // Start the control request on its route(s)
                if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
                {
                    resultString = "ERROR " + CtiNumStr(nRet) + " performing command on route " + Route->getName().data() + "\n" + FormatError(nRet);
                    pRet->setResultString(resultString);
                    pRet->setStatus( nRet );
                }
                else
                {
                    delete pRet;
                    pRet = 0;
                }
            }
            else if( getRouteManager() == 0 )       // If there is no route manager, we need porter to do the route work!
            {
                // Tell the porter side to complete the assembly of the message.
                pOut->Request.BuildIt = TRUE;
                strncpy(pOut->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

                outList.insert( pOut );       // May porter have mercy.
                pOut = 0;
            }
            else
            {
                nRet = BADROUTE;

                resultString = "ERROR: Route or Route Transmitter not available for device " + getName();

                pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                      RWCString(pOut->Request.CommandStr),
                                                      resultString,
                                                      nRet,
                                                      pOut->Request.RouteID,
                                                      pOut->Request.MacroOffset,
                                                      pOut->Request.Attempt,
                                                      pOut->Request.TrxID,
                                                      pOut->Request.UserID,
                                                      pOut->Request.SOE,
                                                      RWOrdered());
            }

            if(pRet)
            {
                retList.insert( pRet );
            }

            if( pOut )
            {
                delete pOut;
            }
        }
    }


    return nRet;
}


INT CtiDeviceGroupMCT::executeControl( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    bool found = false;
    INT   nRet = NoError;

    INT function;

    if( parse.getFlags() & CMD_FLAG_CTL_SHED )
    {
        if( parse.isKeyValid("shed") )
        {
            int shed_function_base   = 0,
                shed_function_relays = 0,
                shed_function,
                shed_duration;

            shed_duration = parse.getiValue("shed");

            if( shed_duration > 0 )
            {
                if( shed_duration <= 450 )
                {
                    shed_function_base = MCTGroup_Shed_Base_07m;
                }
                else if( shed_duration <= 900 )
                {
                    shed_function_base = MCTGroup_Shed_Base_15m;
                }
                else if( shed_duration <= 1800 )
                {
                    shed_function_base = MCTGroup_Shed_Base_30m;
                }
                else
                {
                    shed_function_base = MCTGroup_Shed_Base_60m;
                }

                //  this will obviously need to be changed if we ever need to control more than 4 relays
                if( (shed_function_relays = (_lmGroupMCT.getRelays() & 0x0f)) != 0 )
                {
                    shed_function = shed_function_base | (shed_function_relays & 0x0f);

                    function = Emetcon::Control_Shed;

                    OutMessage->Buffer.BSt.Function = shed_function;
                    OutMessage->Buffer.BSt.Length   = 0;
                    OutMessage->Buffer.BSt.IO       = Emetcon::IO_Write;

                    found = true;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "No relays selected for control in MCT load group \"" << getName() << "\", cannot shed" << endl;
                    }
                }
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_CTL_RESTORE )
    {
        function = Emetcon::Control_Restore;

        OutMessage->Buffer.BSt.Function = MCTGroup_Restore;
        OutMessage->Buffer.BSt.Length   = 0;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Write;

        found = true;
    }
/*
    else if(parse.getFlags() & CMD_FLAG_CTL_OPEN)
    {
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_CLOSE)
    {
    }
*/
    //  check that we found a function and that we have an address to send it to
    if( !found )
    {
        nRet = NoMethod;
    }
    else if( !getAddress() )
    {
        nRet = ADDRESSERROR;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

        outList.append( OutMessage );
        OutMessage = NULL;
    }


    return nRet;
}
