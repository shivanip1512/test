
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_expresscom
*
* Date:   10/4/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2004/12/01 20:12:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>

#include "cmdparse.h"
#include "dev_grp_expresscom.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"

CtiDeviceGroupExpresscom::CtiDeviceGroupExpresscom()
{
}

CtiDeviceGroupExpresscom::CtiDeviceGroupExpresscom(const CtiDeviceGroupExpresscom& aRef)
{
    *this = aRef;
}

CtiDeviceGroupExpresscom::~CtiDeviceGroupExpresscom()
{
}

CtiDeviceGroupExpresscom& CtiDeviceGroupExpresscom::operator=(const CtiDeviceGroupExpresscom& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _expresscomGroup = aRef.getExpresscomGroup();
    }

    return *this;
}


CtiTableExpresscomLoadGroup CtiDeviceGroupExpresscom::getExpresscomGroup() const
{
    return _expresscomGroup;
}
CtiTableExpresscomLoadGroup& CtiDeviceGroupExpresscom::getExpresscomGroup()
{
    return _expresscomGroup;
}
CtiDeviceGroupExpresscom& CtiDeviceGroupExpresscom::setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef)
{
    _expresscomGroup = aRef;
    return *this;
}

LONG CtiDeviceGroupExpresscom::getRouteID()
{
    return _expresscomGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceGroupExpresscom::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    RWCString tmpStr;

    {
        tmpStr = "Group: " + getName() + " Relay:";

        for(int i = 0; i < 8; i++)
        {
            if(_expresscomGroup.getLoadMask() & (mask << i))
            {
                tmpStr += " r" + CtiNumStr(i+1);
            }
        }
    }

    return tmpStr;
}

void CtiDeviceGroupExpresscom::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableExpresscomLoadGroup::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("EXPRESSCOM GROUP") && selector.where() );
}

void CtiDeviceGroupExpresscom::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _expresscomGroup.DecodeDatabaseReader(rdr);
}

INT CtiDeviceGroupExpresscom::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */
    if(parse.getiValue("type") != ProtocolExpresscomType)
    {
        parse.setValue("type", ProtocolExpresscomType);
        parse.parse();  // reparse for xcom specific data items....  This is required in case we got here from a group macro.
    }

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();

        checkForEmptyParseAddressing( parse, OutMessage, retList );

        int serial = 0;
        int spid = 0;
        int geo = 0;
        int substation = 0;
        int feeder = 0;
        int zip = 0;
        int uda = 0;
        int program = 0;
        int splinter = 0;

        serial = (int)(getExpresscomGroup().getSerial());

        if(serial == 0)
        {
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSpid)          spid = (int)(getExpresscomGroup().getServiceProvider());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atGeo)           geo = (int)(getExpresscomGroup().getGeo());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSubstation)    substation = (int)(getExpresscomGroup().getSubstation());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atFeeder)        feeder = (int)(getExpresscomGroup().getFeeder());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atZip)           zip = (int)(getExpresscomGroup().getZip());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atUser)          uda = (int)(getExpresscomGroup().getUda());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atProgram)       program = (int)(getExpresscomGroup().getProgram());
            if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSplinter)      splinter = (int)(getExpresscomGroup().getSplinter());

            parse.setValue("xc_spid", spid);
            parse.setValue("xc_geo", geo);
            parse.setValue("xc_sub", substation);
            parse.setValue("xc_feeder", feeder);
            parse.setValue("xc_zip", zip);
            parse.setValue("xc_uda", uda);
            parse.setValue("xc_program", program);
            parse.setValue("xc_splinter", splinter);
        }
        else
        {
            parse.setValue("xc_serial", serial);
        }

        if(parse.getCommand() == ControlRequest && serial <= 0 && program == 0 && splinter == 0 )
        {
            if((getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atLoad) &&
               (getExpresscomGroup().getLoadMask() != 0))
            {
                parse.setValue("relaymask", (int)(getExpresscomGroup().getLoadMask()));
            }
            else
            {
                // This is bad!  We would control every single load based upon geo addressing...
                nRet = BADPARAM;

                resultString = "\nERROR: " + getName() + " Group addressing control commands to all loads is prohibited\n" + \
                               " The group must specify program, splinter or load level addressing";
                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
                retList.insert( pRet );

                if(OutMessage)
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << resultString << endl;
                }

                return nRet;
            }
        }
        else
        {
            if((getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atLoad) && (program != 0 || splinter != 0) )
            {
                parse.setValue("relaymask", (int)(getExpresscomGroup().getLoadMask()));
            }
            else
            {
                parse.setValue("relaymask", 0);
            }
        }

        /*
         * OK, these are the items we are about to set out to perform..  Any additional signals will
         * be added into the list upon completion of the Execute!
         */
        if(parse.getActionItems().entries())
        {
            for(size_t offset = 0 ; offset < parse.getActionItems().entries(); offset++)
            {
                RWCString actn = parse.getActionItems()[offset];
                RWCString desc = getDescription(parse);

                _lastCommand = actn;    // This might just suck!  I guess I am expecting only one (today) and building for the future..?

                CtiPointStatus *pControlStatus = (CtiPointStatus*)getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                LONG pid = ( (pControlStatus != 0) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                vgList.insert(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
            }
        }

        /*
         *  Form up the reply here since the ExecuteRequest function will consume the
         *  OutMessage.
         */
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), Route->getName(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3) + " performing command on route " + Route->getName();
            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.insert( pRet );
        }
        else
        {
            if(parse.getCommand() == ControlRequest)
                reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, getLastCommand() );

            delete pRet;
        }
    }
    else
    {
        nRet = NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
        retList.insert( pRet );

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << resultString << endl;
        }
    }

    return nRet;
}

RWCString CtiDeviceGroupExpresscom::getPutConfigAssignment(UINT level)
{
    RWCString assign = RWCString("xcom assign") +
                       " S" + CtiNumStr(_expresscomGroup.getServiceProvider()) +
                       " G" + CtiNumStr(_expresscomGroup.getGeo()) +
                       " B" + CtiNumStr(_expresscomGroup.getSubstation()) +
                       " F" + CtiNumStr(_expresscomGroup.getFeeder()) +
                       " Z" + CtiNumStr(_expresscomGroup.getZip()) +
                       " U" + CtiNumStr(_expresscomGroup.getUda()) +
                       " P" + CtiNumStr(_expresscomGroup.getProgram()) +
                       " R" + CtiNumStr(_expresscomGroup.getSplinter()) + " Load ";

    for(int i = 0; i < 15; i++)
    {
        if(_expresscomGroup.getLoadMask() & (0x01 << i))
        {
            assign += CtiNumStr(i+1) + " ";
        }
    }

    return  assign;
}

bool CtiDeviceGroupExpresscom::checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &retList )
{
    bool status = false;

    RWCString issue;

    if(parse.isKeyValid("xc_serial"))
    {
        issue = "Unique addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_spid"))
    {
        issue = "SPID addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_geo"))
    {
        issue = "Geo addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_sub"))
    {
        issue = "Substation addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_feeder"))
    {
        issue = "Feeder addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_zip"))
    {
        issue = "Zip addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_uda"))
    {
        issue = "Uda addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_program"))
    {
        issue = "Program addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
    else if(parse.isKeyValid("xc_splinter"))
    {
        issue = "Splinter addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
#if 0 // 030803 CGP We don't really care about this.
    else if(parse.isKeyValid("relaymask"))
    {
        issue = "Load addressing sent to group " + getName() + ".  Group addressing overrides.";
        status = true;
    }
#endif

    if(status)
    {
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), issue, NORMAL, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
        pRet->setExpectMore( FALSE );

        retList.insert( pRet );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << issue << endl;
        }
    }

    return status;
}


