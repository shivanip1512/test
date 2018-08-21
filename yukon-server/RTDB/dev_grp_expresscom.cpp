/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_expresscom
*
* Date:   10/4/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.34.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:40 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

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
#include "devicetypes.h"

using std::string;
using std::endl;
using std::list;

CtiDeviceGroupExpresscom::CtiDeviceGroupExpresscom()
{
}

CtiTableExpresscomLoadGroup& CtiDeviceGroupExpresscom::getExpresscomGroup()
{
    return _expresscomGroup;
}

const CtiTableExpresscomLoadGroup& CtiDeviceGroupExpresscom::getExpresscomGroup() const
{
    return _expresscomGroup;
}

LONG CtiDeviceGroupExpresscom::getRouteID()
{
    return _expresscomGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceGroupExpresscom::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;

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

string CtiDeviceGroupExpresscom::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, ECV.routeid, ECV.serialnumber, "
                                     "ECV.serviceaddress, ECV.geoaddress, ECV.substationaddress, ECV.feederaddress, "
                                     "ECV.zipcodeaddress, ECV.udaddress, ECV.programaddress, ECV.splinteraddress, "
                                     "ECV.addressusage, ECV.relayusage, ECV.protocolpriority "
                                   "FROM YukonPAObject YP, Device DV, ExpressComAddress_View ECV "
                                   "WHERE upper (YP.type) = 'EXPRESSCOM GROUP' AND YP.paobjectid = ECV.lmgroupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupExpresscom::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader" )
    }

    _expresscomGroup.DecodeDatabaseReader(rdr);
}


YukonError_t CtiDeviceGroupExpresscom::extractGroupAddressing(CtiRequestMsg * &pReq, CtiCommandParser &parse, OUTMESS * &OutMessage, CtiMessageList &vgList, CtiMessageList &retList, string &resultString)
{
    YukonError_t nRet = ClientErrors::None;

    checkForEmptyParseAddressing( parse, OutMessage, retList );

    int serial     = 0;
    int spid       = 0;
    int geo        = 0;
    int substation = 0;
    int feeder     = 0;
    int zip        = 0;
    int uda        = 0;
    int program    = 0;
    int splinter   = 0;

    serial = (int)(getExpresscomGroup().getSerial());

    if(serial == 0)
    {
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSpid)          spid        = (int)(getExpresscomGroup().getServiceProvider());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atGeo)           geo         = (int)(getExpresscomGroup().getGeo());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSubstation)    substation  = (int)(getExpresscomGroup().getSubstation());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atFeeder)        feeder      = (int)(getExpresscomGroup().getFeeder());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atZip)           zip         = (int)(getExpresscomGroup().getZip());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atUser)          uda         = (int)(getExpresscomGroup().getUda());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atProgram)       program     = (int)(getExpresscomGroup().getProgram());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSplinter)      splinter    = (int)(getExpresscomGroup().getSplinter());

        // Only want to set these parse entries if the values are not zero.  Zeros screw up
        // address validation code.  Exception is "xc_feeder" which CAN be zero.

        parse.setValue("xc_feeder",   feeder);
        if(spid        != 0) parse.setValue("xc_spid",     spid);
        if(geo         != 0) parse.setValue("xc_geo",      geo);
        if(substation  != 0) parse.setValue("xc_sub",      substation);
        if(zip         != 0) parse.setValue("xc_zip",      zip);
        if(uda         != 0) parse.setValue("xc_uda",      uda);
        if(program     != 0) parse.setValue("xc_program",  program);
        if(splinter    != 0) parse.setValue("xc_splinter", splinter);
    }
    else
    {
        parse.setValue("xc_serial", serial);
    }

    if(getExpresscomGroup().getPriority() < 3 && getExpresscomGroup().getPriority() >=0 && !parse.isKeyValid("xcpriority"))
    {
        parse.setValue("xcpriority", getExpresscomGroup().getPriority());
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
            nRet = ClientErrors::BadParameter;

            resultString = "\nERROR: " + getName() + " Group addressing control commands to all loads is prohibited\n" + \
                           " The group must specify program, splinter or load level addressing";
            CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                         string(OutMessage->Request.CommandStr),
                                                         resultString,
                                                         nRet,
                                                         OutMessage->Request.RouteID,
                                                         OutMessage->Request.RetryMacroOffset,
                                                         OutMessage->Request.Attempt,
                                                         OutMessage->Request.GrpMsgID,
                                                         OutMessage->Request.UserID,
                                                         OutMessage->Request.SOE,
                                                         CtiMultiMsg_vec());

            retList.push_back( pRet );

            if(OutMessage)
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            CTILOG_ERROR(dout, resultString)

            return nRet;
        }
    }
    else
    {
        if((getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atLoad) && (serial != 0))
        {
            parse.setValue("relaymask", (int)(getExpresscomGroup().getLoadMask()));
        }
        else
        {
            parse.setValue("relaymask", 0);
        }
    }

    if( parse.getControlled() )
    {
        OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt(GROUP_CONTROL_EXPIRATION, 1200);
    }

    reportActionItemsToDispatch(pReq, parse, vgList);

    return nRet;
}


void CtiDeviceGroupExpresscom::reportAndLogControlStart(CtiCommandParser &parse, CtiMessageList &vgList, const string &commandString)
{
    if (parse.getCommand() == ControlRequest)
    {
        int priority = parse.getiValue("xcpriority",3);
        if (priority < 0 || priority > 3)
        {
            CTILOG_ERROR(dout, "Priority is invalid: "<< priority);

            priority = 3; //set it to the default priority if there is a problem.
        }
        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()), priority );
    }

    string addressing = getAddressingAsString();

    CTILOG_INFO(slog, getName() <<": Preparing command for transmission"<<
            endl <<"Group ID "<< getID() <<", Addressing: "<< addressing <<
            endl <<"Command: "<< commandString
            );
}


YukonError_t CtiDeviceGroupExpresscom::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

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

        if( nRet = extractGroupAddressing(pReq, parse, OutMessage, vgList, retList, resultString) )
        {
            return nRet;
        }

        /*
         *  Form up the reply here since the ExecuteRequest function will consume the
         *  OutMessage.
         */
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     Route->getName(),
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3) + string(" performing command on route ") + Route->getName();
            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.push_back( pRet );
        }
        else
        {
            reportAndLogControlStart(parse, vgList, pRet->CommandString());

            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     resultString,
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());

        retList.push_back( pRet );

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        CTILOG_ERROR(dout, resultString);
    }

    return nRet;
}

string CtiDeviceGroupExpresscom::getPutConfigAssignment(UINT modifier)
{
    /*
     *  This putconfig assign should give us addressability only for the address components which are assigned to be controlled by the group.
     *  Any additional addressing should not be affected.  This is what we would want for any STARS web configurations.
     *
     *  The command modifier "
     */
    bool forcefullconfig = (modifier & CtiDeviceBase::PutConfigAssignForce);

    string assign = string("xcom assign");

    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSpid)           assign += " S" + CtiNumStr(_expresscomGroup.getServiceProvider());
    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atGeo)            assign += " G" + CtiNumStr(_expresscomGroup.getGeo());
    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSubstation)     assign += " B" + CtiNumStr(_expresscomGroup.getSubstation());
    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atFeeder)         assign += " F" + CtiNumStr(_expresscomGroup.getFeeder());
    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atZip)            assign += " Z" + CtiNumStr(_expresscomGroup.getZip());
    if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atUser)           assign += " U" + CtiNumStr(_expresscomGroup.getUda());

    if(_expresscomGroup.getLoadMask() &&
       ((forcefullconfig ||
         getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atProgram) ||
        (getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSplinter)) )  // We have enough to do some load addressing
    {
        if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atProgram)        assign += " P" + CtiNumStr(_expresscomGroup.getProgram());
        if(forcefullconfig || getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSplinter)       assign += " R" + CtiNumStr(_expresscomGroup.getSplinter());

        assign += " Load ";

        for(int i = 0; i < 15; i++)
        {
            if(_expresscomGroup.getLoadMask() & (0x01 << i))
            {
                assign += CtiNumStr(i+1) + " ";
            }
        }
    }

    return  assign;
}

string CtiDeviceGroupExpresscom::getAddressingAsString()
{
    string addressing;

    if(getExpresscomGroup().getSerial() != 0)
    {
        addressing += " Serial "   + CtiNumStr(_expresscomGroup.getSerial());
    }
    else
    {
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSpid)           addressing += " SPID "     + CtiNumStr(_expresscomGroup.getServiceProvider());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atGeo)            addressing += " GEO "      + CtiNumStr(_expresscomGroup.getGeo());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSubstation)     addressing += " Sub "      + CtiNumStr(_expresscomGroup.getSubstation());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atFeeder)         addressing += " Feeder "   + CtiNumStr(_expresscomGroup.getFeeder());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atZip)            addressing += " ZIP "      + CtiNumStr(_expresscomGroup.getZip());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atUser)           addressing += " User "     + CtiNumStr(_expresscomGroup.getUda());

        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atProgram)        addressing += " Program "  + CtiNumStr(_expresscomGroup.getProgram());
        if(getExpresscomGroup().getAddressUsage() & CtiProtocolExpresscom::atSplinter)       addressing += " Splinter " + CtiNumStr(_expresscomGroup.getSplinter());
    }

    if(_expresscomGroup.getLoadMask())
    {
        addressing += " Load ";

        for(int i = 0; i < 15; i++)
        {
            if(_expresscomGroup.getLoadMask() & (0x01 << i))
            {
                addressing += CtiNumStr(i+1) + " ";
            }
        }
    }

    return addressing;
}

bool CtiDeviceGroupExpresscom::checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &retList )
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
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     issue,
                                                     ClientErrors::None,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());
        pRet->setExpectMore(false);

        retList.push_back( pRet );

        CTILOG_WARN(dout, issue)
    }

    return status;
}

CtiDeviceGroupBase::ADDRESSING_COMPARE_RESULT CtiDeviceGroupExpresscom::compareAddressing(CtiDeviceGroupBaseSPtr otherGroup)
{
    ADDRESSING_COMPARE_RESULT retVal = NO_RELATIONSHIP;

    // Inherited classes can use this exact same get type check.
    // Currently this says the addressing for RFN can never match the base expresscom type.
    if( otherGroup  && getType() == otherGroup->getType() )
    {
        CtiDeviceGroupExpresscom *expGroup = (CtiDeviceGroupExpresscom*)otherGroup.get();
        if( _expresscomGroup.getAddressUsage() == expGroup->_expresscomGroup.getAddressUsage() )
        {
            //They have identical address levels, check every level they have to ensure they are the same.
            retVal = compareAddressValues(_expresscomGroup.getAddressUsage(), expGroup, ADDRESSING_EQUIVALENT);
        }
        else if ( ( _expresscomGroup.getAddressUsage() & expGroup->_expresscomGroup.getAddressUsage()) == _expresscomGroup.getAddressUsage() )
        {
            //This means that *this is potentially the parent of the operand
            retVal = compareAddressValues(_expresscomGroup.getAddressUsage(), expGroup, THIS_IS_PARENT);
        }
        else if( (_expresscomGroup.getAddressUsage() & expGroup->_expresscomGroup.getAddressUsage()) == expGroup->_expresscomGroup.getAddressUsage() )
        {
            //The operand is potentially the parent of *this;
            retVal = compareAddressValues(expGroup->_expresscomGroup.getAddressUsage(), expGroup, OPERAND_IS_PARENT);
        }

        switch (retVal)
        {
        case NO_RELATIONSHIP:
            CTILOG_DEBUG(dout, _expresscomGroup.getId() << " unrelated to " << expGroup->_expresscomGroup.getId());
            break;
        case THIS_IS_PARENT:
            CTILOG_DEBUG(dout, _expresscomGroup.getId() << " is parent of " << expGroup->_expresscomGroup.getId());
            break;
        case OPERAND_IS_PARENT:
            CTILOG_DEBUG(dout, _expresscomGroup.getId() << " is child of " << expGroup->_expresscomGroup.getId());
            break;
        case ADDRESSING_EQUIVALENT:
            CTILOG_DEBUG(dout, _expresscomGroup.getId() << " is equivalent of " << expGroup->_expresscomGroup.getId());
            break;
        }
    }

    return retVal;
}

// Function to report control start for this group and ALL CHILD groups
void CtiDeviceGroupExpresscom::reportControlStart(int isshed, int shedtime, int reductionratio, CtiMessageList  &vgList, string cmd, int controlPriority )
{
    reportChildControlStart(isshed, shedtime, reductionratio, vgList, cmd, controlPriority);
    if( isAParent() )
    {
        //We need multiple copies!
        for( WPtrGroupMap::iterator iter = _children.begin(); iter != _children.end(); iter++ )
        {
            CtiDeviceGroupBaseSPtr sptr = iter->second.lock();
            // Inherited classes can use this exact same get type check.
            if( sptr && getType() == sptr->getType() )
            {
                CtiDeviceGroupExpresscom *grpPtr = (CtiDeviceGroupExpresscom*)sptr.get();
                grpPtr->reportChildControlStart(isshed, shedtime, reductionratio, vgList, cmd, controlPriority);
            }
        }
    }
}

// Function to report control start for ONLY this group.
void CtiDeviceGroupExpresscom::reportChildControlStart(int isshed, int shedtime, int reductionratio, CtiMessageList  &vgList, string cmd, int controlPriority)
{
    Inherited::reportControlStart(isshed, shedtime, reductionratio, vgList, cmd, controlPriority);
}

/**
    This method compares the contents of the address values.
    If the address is specified (in addressing) it then compares the values for that type 
    of address.  If they match, the search continutes to the next address type.

    If the address does not match in any address type, it's marked as NO_RELATIONSHIP.
    
    If all the addresses match, then we pass back the presumptive relationship from compareAddressing()

    Feeder addressing is special in that we determine if the group is a member of a subset of the parent.

*/
CtiDeviceGroupBase::ADDRESSING_COMPARE_RESULT CtiDeviceGroupExpresscom::compareAddressValues(USHORT addressing, CtiDeviceGroupExpresscom *expGroup, CtiDeviceGroupBase::ADDRESSING_COMPARE_RESULT presumption)
{
    if( expGroup != NULL )
    {
        int serial = (int)(getExpresscomGroup().getSerial());

        // However horrible this is, this is what is currently expected as seen above in executeRequest
        if(serial != 0)
        {
            if (getExpresscomGroup().getSerial() != expGroup->getExpresscomGroup().getSerial())
            {
                return NO_RELATIONSHIP;
            }
        }
        else
        {
            if (addressing & CtiProtocolExpresscom::atSpid)
            {
                if (getExpresscomGroup().getServiceProvider() != expGroup->getExpresscomGroup().getServiceProvider())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atGeo)
            {
                if (getExpresscomGroup().getGeo() != expGroup->getExpresscomGroup().getGeo())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atSubstation)
            {
                if (getExpresscomGroup().getSubstation() != expGroup->getExpresscomGroup().getSubstation())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atZip)
            {
                if (getExpresscomGroup().getZip() != expGroup->getExpresscomGroup().getZip())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atUser)
            {
                if (getExpresscomGroup().getUda() != expGroup->getExpresscomGroup().getUda())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atProgram)
            {
                if (getExpresscomGroup().getProgram() != expGroup->getExpresscomGroup().getProgram())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atSplinter)
            {
                if (getExpresscomGroup().getSplinter() != expGroup->getExpresscomGroup().getSplinter())
                {
                    return NO_RELATIONSHIP;
                }
            }

            if (addressing & CtiProtocolExpresscom::atLoad)
            {
                if (getExpresscomGroup().getLoadMask() != expGroup->getExpresscomGroup().getLoadMask())
                {
                    return NO_RELATIONSHIP;
                }
            }

            /* Determine if this one is a subset of the other based on the feeder bits. */
            if (addressing & CtiProtocolExpresscom::atFeeder)
            {
                if (getExpresscomGroup().getFeeder() != expGroup->getExpresscomGroup().getFeeder())
                {
                    if ((getExpresscomGroup().getFeeder() & expGroup->getExpresscomGroup().getFeeder()) == expGroup->getExpresscomGroup().getFeeder())
                    {
                        return THIS_IS_PARENT;
                    }
                    else if (getExpresscomGroup().getFeeder() == (expGroup->getExpresscomGroup().getFeeder() & getExpresscomGroup().getFeeder()))
                    {
                        return OPERAND_IS_PARENT;
                    }
                    /* If neither is a subset of the other, these are disjoint */
                    return NO_RELATIONSHIP;
                }
            }
        }
    }

    return presumption;
}

bool CtiDeviceGroupExpresscom::isAParent()
{
    return _children.size() > 0;
}

void CtiDeviceGroupExpresscom::addChild(CtiDeviceGroupBaseSPtr child)
{
    if( child )
    {
        _children.insert(WPtrGroupMap::value_type(child->getID(), child));
    }
}

void CtiDeviceGroupExpresscom::removeChild(long child)
{
    _children.erase(child);
}

void CtiDeviceGroupExpresscom::clearChildren()
{
    _children.clear();
}
