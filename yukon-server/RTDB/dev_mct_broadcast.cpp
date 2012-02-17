#include "precompiled.h"

#include "dev_mct_broadcast.h"
#include "logger.h"
#include "numstr.h"
#include "porter.h"

#include "dev_mct.h"     //  for freeze commands
#include "dev_mct31x.h"  //  for IED scanning capability
#include "dev_mct4xx.h"
#include "ctidate.h"
#include "ctitime.h"

using Cti::Protocols::EmetconProtocol;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const MctBroadcastDevice::CommandSet MctBroadcastDevice::_commandStore = MctBroadcastDevice::initCommandStore();


MctBroadcastDevice::MctBroadcastDevice() :
_last_freeze(0)
{
}


MctBroadcastDevice::~MctBroadcastDevice()
{
}

string MctBroadcastDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.address, "
                                     "RTS.routeid "
                                   "FROM Device DV, DeviceCarrierSettings CS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceRoutes RTS ON YP.paobjectid = RTS.deviceid "
                                   "WHERE upper (YP.type) = 'MCT BROADCAST' AND YP.paobjectid = CS.deviceid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void MctBroadcastDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
}



INT MctBroadcastDevice::executePutConfig(CtiRequestMsg                  *pReq,
                                            CtiCommandParser               &parse,
                                            OUTMESS                        *&OutMessage,
                                            list< CtiMessage* >      &vgList,
                                            list< CtiMessage* >      &retList,
                                            list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   function = 0;
    INT   nRet = NoError;
    int   intervallength;
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ), string(OutMessage->Request.CommandStr), string(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec( ));

    if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::PutConfig_Raw;

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");

        temp = parse.getsValue("rawdata");
        if( temp.length() > 15 )
        {
            //  trim string to be 15 bytes long
            temp.erase( 15 );
        }

        OutMessage->Buffer.BSt.Length = temp.length();
        for( int i = 0; i < temp.length(); i++ )
        {
            OutMessage->Buffer.BSt.Message[i] = temp[i];
        }

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;
        }
        else
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Write;
        }

        found = true;
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}

INT MctBroadcastDevice::executePutStatus(CtiRequestMsg                  *pReq,
                                            CtiCommandParser               &parse,
                                            OUTMESS                        *&OutMessage,
                                            list< CtiMessage* >      &vgList,
                                            list< CtiMessage* >      &retList,
                                            list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;
    int   intervallength;
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    INT function;

    OutMessage->Buffer.BSt.Message[0] = 0;
    OutMessage->Buffer.BSt.Message[1] = 0;
    OutMessage->Buffer.BSt.Message[2] = 0;

    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    OutMessage->Request.RouteID = getRouteID();

    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = EmetconProtocol::PutStatus_Reset;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("freeze") )
    {
        int next_freeze = parse.getiValue("freeze");

        if( parse.isKeyValid("voltage") )
        {
            if( next_freeze == 1 )
            {
                function = EmetconProtocol::PutStatus_FreezeVoltageOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
            if( next_freeze == 2 )
            {
                function = EmetconProtocol::PutStatus_FreezeVoltageTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
        }
        else
        {
            if( next_freeze == 1 )
            {
                function = EmetconProtocol::PutStatus_FreezeOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
            if( next_freeze == 2 )
            {
                function = EmetconProtocol::PutStatus_FreezeTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
        }

        if( found )
        {
            //  create one for the MCT 400 series, too
            OUTMESS *MCT400OutMessage = CTIDBG_new OUTMESS(*OutMessage);

            //  these are all "command" type messages - a zero-length write, so it's safe to muck about here
            MCT400OutMessage->Sequence = MCT400OutMessage->Buffer.BSt.Function;

            MCT400OutMessage->Buffer.BSt.Length     = 2;
            MCT400OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
            //  this is a little tricky - watch as we carefully swap it out...
            MCT400OutMessage->Buffer.BSt.Message[1] = MCT400OutMessage->Buffer.BSt.Function;
            //  ...  right before we stomp over the original location
            MCT400OutMessage->Buffer.BSt.Function   = Mct4xxDevice::FuncWrite_Command;

            if( stringContainsIgnoreCase(parse.getCommandStr()," all") )
            {
                //  the MCT 400 message is in ADDITION to the normal command
                outList.push_back(MCT400OutMessage);
            }
            else
            {
                //  the MCT 400 message REPLACES the normal command (kinda backward, but it works)
                delete OutMessage;
                OutMessage = MCT400OutMessage;
            }

            MCT400OutMessage = 0;
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        OutMessage->Sequence = function;     // Helps us figure it out later!
    }

    return nRet;
}


INT MctBroadcastDevice::executePutValue(CtiRequestMsg                  *pReq,
                                           CtiCommandParser               &parse,
                                           OUTMESS                        *&OutMessage,
                                           list< CtiMessage* >      &vgList,
                                           list< CtiMessage* >      &retList,
                                           list< OUTMESS* >         &outList)
{
    INT    nRet = NoError,
           i;
    long   rawPulses;
    double dial;

    string command_string(OutMessage->Request.CommandStr);

    INT function;

    bool found = false;


    if( parse.isKeyValid("power") )
    {
        if( parse.isKeyValid("reset") )
        {
            if( command_string.find(" 400") != string::npos )
            {
                OutMessage->Buffer.BSt.Function = Mct4xxDevice::Command_PowerfailReset;
                OutMessage->Buffer.BSt.Length = 0;
                OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Write;

                found = true;
            }
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }

    return nRet;
}

//
//  My apologies to those who follow.
//
MctBroadcastDevice::CommandSet MctBroadcastDevice::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::PutStatus_Reset,            EmetconProtocol::IO_Write, MCTBCAST_ResetPF, MCTBCAST_ResetPFLen));

    //  Do these need an ARMS for the 200- and 300-series meters?
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeOne,        EmetconProtocol::IO_Write, MctDevice::Command_FreezeOne, 0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeTwo,        EmetconProtocol::IO_Write, MctDevice::Command_FreezeTwo, 0));

    cs.insert(CommandStore(EmetconProtocol::PutValue_IEDReset,          EmetconProtocol::IO_Function_Write, 0, 0));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeVoltageOne, EmetconProtocol::IO_Write, Mct4xxDevice::Command_FreezeVoltageOne, 0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeVoltageTwo, EmetconProtocol::IO_Write, Mct4xxDevice::Command_FreezeVoltageTwo, 0));

    return cs;
}

bool MctBroadcastDevice::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )    // It's prego!
    {
        function = itr->function;
        length   = itr->length;
        io       = itr->io;

        found = true;
    }

    return found;
}

INT MctBroadcastDevice::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

LONG MctBroadcastDevice::getAddress() const
{
    return CarrierSettings.getAddress() + MCTBCAST_LeadMeterOffset;
}

}
}

