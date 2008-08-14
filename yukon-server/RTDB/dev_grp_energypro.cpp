/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_energypro
*
* Date:   6/19/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2008/08/14 15:57:39 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>

#include "cmdparse.h"
#include "dev_grp_energypro.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"

CtiDeviceGroupEnergyPro::CtiDeviceGroupEnergyPro()
{
}

CtiDeviceGroupEnergyPro::CtiDeviceGroupEnergyPro(const CtiDeviceGroupEnergyPro& aRef)
{
    *this = aRef;
}

CtiDeviceGroupEnergyPro::~CtiDeviceGroupEnergyPro()
{
}

CtiDeviceGroupEnergyPro& CtiDeviceGroupEnergyPro::operator=(const CtiDeviceGroupEnergyPro& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _expresscomGroup = aRef.getExpresscomGroup();
    }

    return *this;
}


CtiTableExpresscomLoadGroup CtiDeviceGroupEnergyPro::getExpresscomGroup() const
{
    return _expresscomGroup;
}
CtiTableExpresscomLoadGroup& CtiDeviceGroupEnergyPro::getExpresscomGroup()
{
    return _expresscomGroup;
}
CtiDeviceGroupEnergyPro& CtiDeviceGroupEnergyPro::setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef)
{
    _expresscomGroup = aRef;
    return *this;
}

LONG CtiDeviceGroupEnergyPro::getRouteID()
{
    return _expresscomGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceGroupEnergyPro::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;

    {
        tmpStr = "Group: " + getName() + " Load:";

        for(int i = 0; i < 8; i++)
        {
            if(_expresscomGroup.getLoadMask() & (mask << i))
            {
                tmpStr += " l" + CtiNumStr(i+1);
            }
        }
    }

    return tmpStr;
}

void CtiDeviceGroupEnergyPro::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableExpresscomLoadGroup::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("ENERGYPRO GROUP") && selector.where() );
}

void CtiDeviceGroupEnergyPro::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _expresscomGroup.DecodeDatabaseReader(rdr);
}

INT CtiDeviceGroupEnergyPro::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    string resultString;

    OutMessage->DeviceID = getID();
    OutMessage->TargetID = getID();
    OutMessage->MessageFlags |= MessageFlag_RouteToPorterGatewayThread;  // Suggest the execution path to porter.

    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */
    parse.setValue("type", ProtocolEnergyProType);
    parse.setValue("serial", 0);

    outList.push_back( OutMessage );
    OutMessage = 0;

    return nRet;
}

string CtiDeviceGroupEnergyPro::getPutConfigAssignment(UINT modifier)
{
    string assign = string("xcom assign") +
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

bool CtiDeviceGroupEnergyPro::checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &retList )
{
    bool status = false;

    string issue;

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
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), issue, NORMAL, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());
        pRet->setExpectMore( FALSE );

        retList.push_back( pRet );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << issue << endl;
        }
    }

    return status;
}

INT CtiDeviceGroupEnergyPro::ProcessResult(INMESS* InMessage, CtiTime& now, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = 0;

    retList.push_back( CTIDBG_new CtiReturnMsg(getID(), string(InMessage->Return.CommandStr), string((char*)InMessage->Buffer.GWRSt.MsgData),  status, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.GrpMsgID, InMessage->Return.UserID, InMessage->Return.SOE, CtiMultiMsg_vec()));


    return status;
}

