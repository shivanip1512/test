
/*-----------------------------------------------------------------------------*
*
* File:   dev_mct4xx
*
* Date:   10/5/2005
*
* Author: Jess M. Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct4xx-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/10/17 16:58:10 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\re.h>
#undef mask_                // Stupid RogueWave re.h

#include "dev_mct4xx.h"


using Cti::Protocol::Emetcon;

const char *CtiDeviceMCT4xx::PutConfigPart_all           = "all";
const char *CtiDeviceMCT4xx::PutConfigPart_tou           = "tou";
const char *CtiDeviceMCT4xx::PutConfigPart_dst           = "dst";
const char *CtiDeviceMCT4xx::PutConfigPart_vthreshold    = "vthreshold";
const char *CtiDeviceMCT4xx::PutConfigPart_demand_lp     = "demand_lp";
const char *CtiDeviceMCT4xx::PutConfigPart_configuration = "configuration";
const char *CtiDeviceMCT4xx::PutConfigPart_addressing    = "addressing";

const CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::_config_parts = initConfigParts();

CtiDeviceMCT4xx::CtiDeviceMCT4xx()
{
}

CtiDeviceMCT4xx::CtiDeviceMCT4xx(const CtiDeviceMCT4xx& aRef)
{
    *this = aRef;
}

CtiDeviceMCT4xx::~CtiDeviceMCT4xx()
{
}

CtiDeviceMCT4xx &CtiDeviceMCT4xx::operator=(const CtiDeviceMCT4xx &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        LockGuard guard(monitor());            // Protect this device!
    }
    return *this;
}

CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::initConfigParts()
{
    ConfigPartsList tempList;
    tempList.push_back(PutConfigPart_tou);
    tempList.push_back(PutConfigPart_dst);
    tempList.push_back(PutConfigPart_vthreshold);
    tempList.push_back(PutConfigPart_demand_lp);
    tempList.push_back(PutConfigPart_configuration);
    tempList.push_back(PutConfigPart_addressing);

    return tempList;
}

INT CtiDeviceMCT4xx::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   RWCString(OutMessage->Request.CommandStr),
                                                   RWCString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   RWOrdered( ));


    if( parse.isKeyValid("install") )
    {
        if( parse.getsValue("installvalue") == PutConfigPart_all )
        {
            for(CtiDeviceMCT4xx::ConfigPartsList::const_iterator tempItr = _config_parts.begin();tempItr != _config_parts.end();tempItr++)
            {
                CtiRequestMsg *tempReq = CTIDBG_new CtiRequestMsg(*pReq);
    
                OUTMESS *OutTemplate = CTIDBG_new OUTMESS(*OutMessage);
    
                if( tempReq != NULL && *tempItr != PutConfigPart_all)//_all == infinite loop == unhappy program == very unhappy jess
                {
                    RWCString tempString = tempReq->CommandString();
                    RWCString replaceString = " ";
                    replaceString += *tempItr; //FIX_ME Consider not keeping the old string but just creating a new, internal string.
                    replaceString += " ";

                    tempString.replace(" all($| )",replaceString);
                    tempReq->setCommandString(tempString);
    
                    CtiCommandParser parse(tempReq->CommandString());
                    ExecuteRequest(pReq, parse, OutTemplate, vgList, retList, outList);
    
                    if(OutTemplate != NULL)
                    {
                        delete OutTemplate;
                    }
    
                    delete tempReq;
                }
            }
        }
        else if( parse.getsValue("installvalue") == PutConfigPart_tou )
        {

        }
        else if( parse.getsValue("installvalue") == PutConfigPart_dst )
        {
            executePutConfigDst(pReq,parse,OutMessage,vgList,retList,outList);
        }
        else if( parse.getsValue("installvalue") == PutConfigPart_vthreshold )
        {
            executePutConfigVThreshold(pReq,parse,OutMessage,vgList,retList,outList);
        }
        else if( parse.getsValue("installvalue") == PutConfigPart_demand_lp )
        {
           // executePutConfigDemandLP(pReq,parse,OutMessage,vgList,retList,outList);

        }
        else if( parse.getsValue("installvalue") == PutConfigPart_configuration )
        {

        }
        else if( parse.getsValue("installvalue") == PutConfigPart_addressing )
        {

        }
        else
        {
            nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
        }
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }
    

    return nRet;

}

int CtiDeviceMCT4xx::executePutConfigDst(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigVThreshold(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList)
{
    return NoMethod;
}


void CtiDeviceMCT4xx::setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr config)
{
    _deviceConfig = config;
}
