#include "precompiled.h"

#include "devicetypes.h"
#include "dev_repeater.h"
#include "logger.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

using namespace Cti::Protocols;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const Repeater900Device::CommandSet Repeater900Device::_commandStore = Repeater900Device::initCommandStore();


Repeater900Device::Repeater900Device()
{ }


Repeater900Device::CommandSet Repeater900Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_General,       EmetconProtocol::IO_Read,   ModelPos,       1));
    cs.insert(CommandStore(EmetconProtocol::Command_Loop,       EmetconProtocol::IO_Read,   ModelPos,       1));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Role,     EmetconProtocol::IO_Write,  RoleBasePos,    RoleLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Role,     EmetconProtocol::IO_Read,   RoleBasePos,    RoleLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Model,    EmetconProtocol::IO_Read,   ModelPos,       ModelLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw,      EmetconProtocol::IO_Write,         0,       0));    // filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw,      EmetconProtocol::IO_Read,          0,       0));    // ...ditto

    return cs;
}


bool Repeater900Device::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        function = itr->function;
        length   = itr->length;
        io       = itr->io;

        found = true;
    }

    return found;
}


YukonError_t Repeater900Device::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
        {
            CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
        }


        if(getOperation(EmetconProtocol::Command_Loop, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            populateDlcOutMessage(*OutMessage);
            OutMessage->Retry     = 3;  //  override

            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_General;     // Helps us figure it out later!

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            OutMessage->Request.RouteID = getRouteID();
            strncpy(OutMessage->Request.CommandStr, "loop", COMMAND_STR_SIZE);

            outList.push_back(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName());

            status = ClientErrors::NoMethod;
        }
    }

    return status;
}


YukonError_t Repeater900Device::executeLoopback(CtiRequestMsg        *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList)
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR Temp[80];

    INT function;

    function = EmetconProtocol::Command_Loop;
    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Retry     = 3;  //  override
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Request.RouteID = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t Repeater900Device::executeGetConfig(CtiRequestMsg        *pReq,
                                                 CtiCommandParser  &parse,
                                                 OUTMESS          *&OutMessage,
                                                 CtiMessageList    &vgList,
                                                 CtiMessageList    &retList,
                                                 OutMessageList    &outList)
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR Temp[80];

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    //  get repeater role info
    if(parse.isKeyValid("rolenum"))
    {
        //  correct for 1-based numbering (C++ and repeater's memory want 0-based)
        int rolenum = parse.getiValue("rolenum") - 1;

        function = EmetconProtocol::GetConfig_Role;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        //  add on offset if it's role 2-24, else default to role 1 (no offset)
        if( rolenum > 0 && rolenum < 24 )
        {
            rolenum -= rolenum % 6;  //  start from the closest multiple of 6
            OutMessage->Buffer.BSt.Function += rolenum * RoleLen;
        }

        //  get 6 roles
        OutMessage->Buffer.BSt.Length *= 6;
    }
    else if(parse.isKeyValid("model"))
    {
        function = EmetconProtocol::GetConfig_Model;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;
        }
        OutMessage->Buffer.BSt.Length = std::min(parse.getiValue("rawlen", 13), 13);    //  default (and maximum) is 13 bytes
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Retry     = 3;  //  override
        OutMessage->Sequence  = function;     // Helps us figure it out later!

        OutMessage->Request.RouteID = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t Repeater900Device::executePutConfig(CtiRequestMsg        *pReq,
                                                 CtiCommandParser  &parse,
                                                 OUTMESS          *&OutMessage,
                                                 CtiMessageList    &vgList,
                                                 CtiMessageList    &retList,
                                                 OutMessageList    &outList)
{
    int   i;
    bool  found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR  Temp[80];
    string temp2;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    //  get repeater role info
    if(parse.isKeyValid("rolenum"))
    {
       //  correct for 1-based numbering
       int rolenum = parse.getiValue("rolenum") - 1;

       //  check that the offset is valid - otherwise we should fail
       if( rolenum >= 0 && rolenum < 24 )
       {
           function = EmetconProtocol::PutConfig_Role;
           found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

           //  add on offset if it's role 2-24, else default to role 1 (no offset)
           if( rolenum > 0 && rolenum < 24 )
           {
               OutMessage->Buffer.BSt.Function += rolenum * RoleLen;
           }

           Temp[0]  =  parse.getiValue("rolefixed") & 0x1F;
           Temp[0] |=  parse.getiValue("roleout") << 5;

           Temp[1]  = (parse.getiValue("rolerpt") << 1) & 0x1F;
           Temp[1] |=  parse.getiValue("rolein") << 5;
           Temp[1] |=  0x01;  //  set lowest bit to 1 instead of 0, per spec

           OutMessage->Buffer.BSt.Message[0] = Temp[0];
           OutMessage->Buffer.BSt.Message[1] = Temp[1];
       }
       else
       {
           nRet = ClientErrors::BadParameter;
       }
    }
    else if(parse.isKeyValid("multi_rolenum"))
    {
       function = EmetconProtocol::PutConfig_Role;
       found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

       int fixbits;
       int varbits_out;
       int varbits_in;
       int stagestf;

       int j;
       int msgcnt = ((parse.getiValue("multi_rolecount") + 5) / 6);     // This is the number of OutMessages to send.
       int firstrole = parse.getiValue("multi_rolenum") - 1;            // The first role to send.
       int rolenum   = parse.getiValue("multi_rolenum") - 1;            // The first role to send.

       string strFixed  = parse.getsValue("multi_rolefixed");
       string strVarOut = parse.getsValue("multi_roleout");
       string strVarIn  = parse.getsValue("multi_rolein");
       string strStages = parse.getsValue("multi_rolerpt");

       string strTemp;

       //boost::tokenizer<>::iterator beg=tok.begin();

       boost::tokenizer<> fixtok(strFixed);
       boost::tokenizer<> vouttok(strVarOut);
       boost::tokenizer<> vintok(strVarIn);
       boost::tokenizer<> stftok(strStages);

       boost::tokenizer<>::iterator fixitr  = fixtok.begin();
       boost::tokenizer<>::iterator voutitr = vouttok.begin();
       boost::tokenizer<>::iterator vinitr  = vintok.begin();
       boost::tokenizer<>::iterator stfitr  = stftok.begin();

       for(j = 0; j < msgcnt; j++)
       {
           rolenum = firstrole + (j * 6);       // This is where we begin.

           OUTMESS *pOutMessage = CTIDBG_new OUTMESS(*OutMessage);  // Copy construct based upon OutMessage.

           // Ok, multi_role always fills the message (6 roles or bust), unless we are filling the high role(ers)
           if(rolenum > 18)
           {
               pOutMessage->Buffer.BSt.Length = pOutMessage->Buffer.BSt.Length * (24 - rolenum);
           }
           else
           {
               pOutMessage->Buffer.BSt.Length = pOutMessage->Buffer.BSt.Length * 6;
           }

           //  add on offset if it's role 2-24, else default to role 1 (no offset)
           if( rolenum > 0 && rolenum < 24 )
           {
               pOutMessage->Buffer.BSt.Function += rolenum * RoleLen;
           }

           for(i = 0; i < pOutMessage->Buffer.BSt.Length; i = i + 2)       // This is the number of defined roles.
           {
               strTemp = "";

               if( fixitr != fixtok.end() )
               {
                   strTemp = *fixitr;
                   fixitr++;
               }
               fixbits = !strTemp.empty() ? atoi(strTemp.c_str()) : 31;

               if( voutitr != vouttok.end() )
               {
                   strTemp = *voutitr;
                   voutitr++;
               }
               varbits_out = !strTemp.empty() ? atoi(strTemp.c_str()) : 7;

               if( vinitr != vintok.end() )
               {
                   strTemp = *vinitr;
                   vinitr++;
               }
               varbits_in = !strTemp.empty() ? atoi(strTemp.c_str()) : 7;

               if( stfitr != stftok.end() )
               {
                   strTemp = *stfitr;
                   stfitr++;
               }
               stagestf = !strTemp.empty() ? atoi(strTemp.c_str()) : 15;

               Temp[0]  = fixbits & 0x1F;
               Temp[0] |= varbits_out << 5;
               Temp[1]  = (stagestf << 1) & 0x1F;
               Temp[1] |= varbits_in << 5;
               Temp[1] |= 0x01;  //  set lowest bit to 1 instead of 0, per spec

               pOutMessage->Buffer.BSt.Message[i] = Temp[0];
               pOutMessage->Buffer.BSt.Message[i+1] = Temp[1];
           }

           populateDlcOutMessage(*OutMessage);
           pOutMessage->Retry     = 3;  //  override
           pOutMessage->Sequence  = function;     // Helps us figure it out later!

           pOutMessage->Request.RouteID = getRouteID();

           strncpy(pOutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

           outList.push_back( pOutMessage );
       }

       delete OutMessage;
       OutMessage = 0;          // Make the original go away!
    }
    else if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;
        }

        string rawData = parse.getsValue("rawdata");

        OutMessage->Buffer.BSt.Length = std::min( rawData.length(), 15u );  //  default (and maximum) is 15 bytes

        rawData.copy( (char *)OutMessage->Buffer.BSt.Message, OutMessage->Buffer.BSt.Length );
    }

    if(!found)
    {
       nRet = ClientErrors::NoMethod;
    }
    else if(OutMessage)
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Retry     = 3;  //  override
       OutMessage->Sequence  = function;     // Helps us figure it out later!

       OutMessage->Request.RouteID = getRouteID();

       strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t Repeater900Device::executeGetValue(CtiRequestMsg        *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList)
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR Temp[80];

    INT function;

    //  for the rpt 800
    if(parse.getFlags() & CMD_FLAG_GV_PFCOUNT)
    {
        function = EmetconProtocol::GetValue_PFCount;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Retry     = 3;  //  override
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t Repeater900Device::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;


    switch(InMessage.Sequence)
    {
        case (EmetconProtocol::Scan_General):
        case (EmetconProtocol::Command_Loop):
        {
            status = decodeLoopback(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::PutConfig_Role):
        {
            status = decodePutConfigRole(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_Role):
        {
            status = decodeGetConfigRole(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        case EmetconProtocol::GetConfig_Raw:
        {
            status = decodeGetConfigRaw( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::PutConfig_Raw:
        {
            status = decodePutConfigRaw( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        default:
        {
            CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
            status = ClientErrors::NoMethod;
        }
    }

    return status;
}


YukonError_t Repeater900Device::decodeLoopback(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    resetScanFlag(ScanRateGeneral);

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( getName() + " / successful ping" );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Repeater900Device::decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int  sspec;
    char revision;
    string modelStr;

    sspec  = DSt->Message[0] << 8;
    sspec |= DSt->Message[1];

    revision = DSt->Message[2] + '@';  //  '@' is just before 'A' - thus 1 represents 'A', as intended

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RPT_SSpec,         (long)sspec);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RPT_SSpecRevision, (long)DSt->Message[2]);

    modelStr = getName() + " / sspec: " + CtiNumStr(sspec) + revision;

    ReturnMsg->setResultString( modelStr );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Repeater900Device::decodeGetConfigRole(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    int   rolenum;
    const unsigned char *buf;
    string roleStr, tmpStr;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    rolenum = parse.getiValue( "rolenum" );
    rolenum -= (rolenum - 1) % 6;
    buf = InMessage.Buffer.DSt.Message;

    for( int i = 0; i < 6; i++ )
    {
        tmpStr = getName() + " / role " + CtiNumStr(i+rolenum).spad(2) + ": ";
        tmpStr += "F = " + CtiNumStr((int)(buf[(i*2)+0] & 0x1F)).spad(2)        + string(", ") +
                  "I = " + CtiNumStr((int)((buf[(i*2)+0] & 0xE0) >> 5)).spad(2) + ", " +
                  "O = " + CtiNumStr((int)((buf[(i*2)+1] & 0xE0) >> 5)).spad(2) + ", " +
                  "S = " + CtiNumStr((int)((buf[(i*2)+1] & 0x1E) >> 1)).spad(2) + "\n";

        roleStr += tmpStr;
    }

    ReturnMsg->setResultString( roleStr );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Repeater900Device::decodePutConfigRole(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    INT   j;
    ULONG pfCount = 0;
    string resultString;
    bool expectMore = false;

    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    string cmdStr = InMessage.Return.CommandStr;
    //This should look like "putconfig emetcon mrole 1 2 3 4.... : 6 3 4 5 5 .... : 12 3 4 1 4...
    //We want to grab the numbers after the leading : then remove the leading :

    if( containsString(cmdStr, ": ") )
    {
        removeRegex(cmdStr, "( [0-9]+)+ *:");
        if( containsRegex(cmdStr, "( [0-9]+)+") )
        {
            //We stripped one, another still exists. We need to re-submit this.
            CtiRequestMsg *newReq = new CtiRequestMsg( getID(),
                                                      cmdStr,
                                                      InMessage.Return.UserID,
                                                      InMessage.Return.GrpMsgID,
                                                      InMessage.Return.RouteID,
                                                      selectInitialMacroRouteOffset(InMessage.Return.RouteID),
                                                      0,
                                                      0,
                                                      InMessage.Priority);

            newReq->setMessageTime(CtiTime::now().seconds() + 5);
            newReq->setConnectionHandle((void *)InMessage.Return.Connection);
            retList.push_back(newReq);
            expectMore = true;

        }
    }

    if( expectMore )
    {
        resultString = getName() + " / command continuing";
    }
    else
    {
        resultString = getName() + " / command complete";
    }

    ReturnMsg->setResultString( resultString );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );

    return status;
}

YukonError_t Repeater900Device::decodeGetConfigRaw( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::Abnormal;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage.Return.CommandStr);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    string results;

    int rawloc = parse.getiValue("rawloc");

    int rawlen = parse.isKeyValid("rawlen")
        ? std::min(parse.getiValue("rawlen"), 13)       // max 13 bytes...
        : DSt->Length;

    if( parse.isKeyValid("rawfunc") )
    {
        for( int i = 0; i < rawlen; i++ )
        {
            results += getName()
                    + " / FR " + CtiNumStr(rawloc).xhex().zpad(2)
                    + " byte " + CtiNumStr(i).zpad(2)
                    + " : "    + CtiNumStr((int)DSt->Message[i]).xhex().zpad(2)
                    + "\n";
        }
    }
    else
    {
        for( int i = 0; i < rawlen; i++ )
        {
            results += getName()
                    + " / byte " + CtiNumStr(rawloc + i).xhex().zpad(2)
                    + " : "      + CtiNumStr((int)DSt->Message[i]).xhex().zpad(2)
                    + "\n";
        }
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Repeater900Device::decodePutConfigRaw( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::Abnormal;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    string results;

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_Raw:
        {
            results = getName() + " / Raw bytes sent";
            break;
        }
        default:
        {

        }
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

}
}
