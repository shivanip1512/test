#include "precompiled.h"

#include "logger.h"
#include "numstr.h"
#include "dev_mct440_213xb.h"
#include "config_device.h"
#include "date_utility.h"
#include "utility.h"
#include "tbl_ptdispatch.h"
#include "pt_status.h"
#include "config_data_mct.h"
#include "portglob.h"
#include "dllyukon.h"
#include "eventlog_mct440_213xb.h"
#include "cmd_mct440_holidays.h"

#include <boost/assign/list_of.hpp>

#include <stack>


using namespace std;
using namespace Cti::Devices::Commands;
using namespace Cti::Config;
using namespace Cti::Protocols;


#define TOU_SCHEDULE_NBR                4
#define TOU_SCHEDULE_TIME_NBR           9
#define TOU_SCHEDULE_RATE_NBR          10

#define OUTAGE_NBR_MIN                  1
#define OUTAGE_NBR_MAX                 10

#define DEFAULT_INSTALL_READ_DELAY     20   // TODO set default delay

#define COUNT_OF(array)                (sizeof((array))/sizeof((array)[0]))

namespace Cti {
namespace Devices {


const Mct440_213xBDevice::FunctionReadValueMappings Mct440_213xBDevice::_readValueMaps      = Mct440_213xBDevice::initReadValueMaps();
const Mct440_213xBDevice::CommandSet                Mct440_213xBDevice::_commandStore       = Mct440_213xBDevice::initCommandStore();
const Mct440_213xBDevice::ConfigPartsList           Mct440_213xBDevice::_config_parts       = Mct440_213xBDevice::initConfigParts();
const std::set<UINT>                                Mct440_213xBDevice::_excludedCommands   = Mct440_213xBDevice::initExcludedCommands();
const CtiDate                                       Mct440_213xBDevice::holidayBaseDate     = CtiDate(1,1,2010);


// this is for TOU putconfig ease
struct ratechange_t
{
    int schedule;
    int time;
    int rate;

    bool operator<(const ratechange_t &rhs) const
    {
        bool retval = false;

        if( schedule < rhs.schedule || (schedule == rhs.schedule && time < rhs.time) )
        {
            retval = true;
        }

        return retval;
    }
};


/*
*********************************************************************************************************
*                                          initReadValueMaps()
*
* Description : Initialize readValueMaps member of the Mct440_213xBDevice class.
*
* Argument(s) : None.
*
* Return(s)   : ReadValueMapping
*
* Caller(s)   : At initialization of the class
*
* Note(s)     : None.
*********************************************************************************************************
*/
Mct440_213xBDevice::FunctionReadValueMappings Mct440_213xBDevice::initReadValueMaps()
{
    struct function_read_value
    {
        unsigned function;
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        // 0x000 – Firmware Revision and SSPEC
        { 0x000,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision             } },
        { 0x000,  1, { 2, CtiTableDynamicPaoInfo::Key_MCT_SSpec                     } },

        // 0x005 – Status and Event Flags and Masks
        { 0x005,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1           } },
        { 0x005,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2           } },
        { 0x005,  7, { 2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask            } },

        // 0x00F – Display Parameters
        { 0x00f,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters         } },

        // 0x013 – Group Addresses
        { 0x013,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_AddressBronze             } },
        { 0x013,  1, { 2, CtiTableDynamicPaoInfo::Key_MCT_AddressLead               } },
        { 0x013,  3, { 2, CtiTableDynamicPaoInfo::Key_MCT_AddressCollection         } },
        { 0x013,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID  } },

        // 0x019 – Transformer Ratio
        { 0x019,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio          } },

        // 0x01A – Intervals
        { 0x01a,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_DemandInterval            } },
        { 0x01a,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval       } },

        // 0x01E – Thresholds
        { 0x01e,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_PhaseLossPercent          } },
        { 0x01e,  1, { 2, CtiTableDynamicPaoInfo::Key_MCT_PhaseLossSeconds          } },

        // 0x022 – Minimum Outage Cycles
        { 0x022,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles              } },

        // 0x036 – Time Adjustment Tolerance
        { 0x036,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance       } },

        // 0x03F – MCT Time
        { 0x03f,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset            } },

        // 0x04F – Day of Scheduled Freeze
        { 0x04f,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay        } },

        // 0x050 to 0x059 – Read Meter Parameters Change Register (FIXME)

        // 0x0F9 – Read Transmit Power (FIXME)

        // 0x0D0 – Holiday 1 - 6
        { 0x0d0,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday1                  } },
        { 0x0d0,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday2                  } },
        { 0x0d0,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday3                  } },
        { 0x0d0,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday4                  } },
        { 0x0d0,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday5                  } },
        { 0x0d0, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday6                  } },

        // 0x0D1 – Holiday 7 - 12
        { 0x0d1,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday7                  } },
        { 0x0d1,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday8                  } },
        { 0x0d1,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday9                  } },
        { 0x0d1,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday10                 } },
        { 0x0d1,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday11                 } },
        { 0x0d1, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday12                 } },

        // 0x0D2 – Holiday 13 - 15
        { 0x0d2,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday13                 } },
        { 0x0d2,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday14                 } },
        { 0x0d2,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday15                 } },

        // 0x101 - Config byte
        { 0x101,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1           } },
        { 0x101,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2           } },
        { 0x101,  3, { 2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask            } },
        { 0x101,  5, { 2, CtiTableDynamicPaoInfo::Key_MCT_Configuration             } },

        // 0x19D – Long Load Profile Status
        { 0x19d,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len            } },
        { 0x19d,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len            } },
        { 0x19d,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len            } },
        { 0x19d,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len            } },

        // 0x1AD – TOU Day Schedule
        { 0x1ad,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable                  } },
        { 0x1ad, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset            } },

        // 0x1FE – Disconnect Status (FIXME)

    };

    FunctionReadValueMappings fr;

    for each( function_read_value frv in values )
    {
        fr[frv.function].insert(make_pair(frv.offset, frv.value));
    }

    return fr;
}


/*
*********************************************************************************************************
*                                          initCommandStore()
*
* Description : Initialize command store member of the Mct440_213xBDevice class.
*
* Argument(s) : None.
*
* Return(s)   : CommandSet
*
* Caller(s)   : At initialization of the class
*
* Note(s)     : None.
*********************************************************************************************************
*/
Mct440_213xBDevice::CommandSet Mct440_213xBDevice::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::GetValue_InstantLineData,       EmetconProtocol::IO_Function_Read,  0x9F,   12));

    cs.insert(CommandStore(EmetconProtocol::GetValue_TOUkWhReverse,         EmetconProtocol::IO_Function_Read,  0xE2,   13));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenTOUkWhReverse,   EmetconProtocol::IO_Function_Read,  0xE3,   13));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_PhaseLossThreshold,   EmetconProtocol::IO_Read,           0x1E,    3));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_PhaseLossThreshold,   EmetconProtocol::IO_Write,          0x1E,    3));

    cs.insert(CommandStore(EmetconProtocol::GetStatus_Freeze,               EmetconProtocol::IO_Read,           0x26,    5));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Options,              EmetconProtocol::IO_Function_Read,  0x01,    7));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Options,              EmetconProtocol::IO_Function_Write, 0x01,    6));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Intervals,            EmetconProtocol::IO_Read,           0x1A,    2));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Intervals,            EmetconProtocol::IO_Function_Write, 0x03,    2));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_SetTOUHolidayRate,    EmetconProtocol::IO_Write,          0xA4,    0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_ClearTOUHolidayRate,  EmetconProtocol::IO_Write,          0xA5,    0));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_AlarmMask,            EmetconProtocol::IO_Function_Read,  0x01,    5));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_AlarmMask,            EmetconProtocol::IO_Function_Write, 0x01,    4));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Addressing,           EmetconProtocol::IO_Read,           0x13,    6));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Addressing,           EmetconProtocol::IO_Write,          0x13,    6));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_TimeAdjustTolerance,  EmetconProtocol::IO_Read,           0x36,    1));

    return cs;
}


/*
*********************************************************************************************************
*                                         initConfigParts()
*
* Description : Initialize config parts supported for the MCT440_213xB device
*
* Argument(s) : None.
*
* Return(s)   : ConfigPartsList
*
* Caller(s)   : At initialization of the class
*
* Note(s)     : None.
*********************************************************************************************************
*/
Mct440_213xBDevice::ConfigPartsList Mct440_213xBDevice::initConfigParts()
{
    Mct440_213xBDevice::ConfigPartsList tempList;

    tempList.push_back(Mct4xxDevice::PutConfigPart_tou);
    tempList.push_back(Mct4xxDevice::PutConfigPart_dst);
    tempList.push_back(Mct4xxDevice::PutConfigPart_timezone);
    tempList.push_back(Mct4xxDevice::PutConfigPart_time_adjust_tolerance);
    tempList.push_back(Mct4xxDevice::PutConfigPart_addressing);
    tempList.push_back(Mct4xxDevice::PutConfigPart_phaseloss);

    return tempList;
}


/*
*********************************************************************************************************
*                                        initExcludedCommands()
*
* Description : Initialize list of excluded commands
*
* Argument(s) : None.
*
* Return(s)   : std::set<UINT>
*
* Caller(s)   : At initialization of the class
*
* Note(s)     : None.
*********************************************************************************************************
*/
std::set<UINT> Mct440_213xBDevice::initExcludedCommands()
{
    std::set<UINT> cs;

    cs.insert(EmetconProtocol::PutConfig_VThreshold);
    cs.insert(EmetconProtocol::PutConfig_Disconnect);
    cs.insert(EmetconProtocol::GetConfig_Thresholds);
    cs.insert(EmetconProtocol::GetValue_PeakDemand);
    cs.insert(EmetconProtocol::GetValue_FrozenPeakDemand);
    cs.insert(EmetconProtocol::GetValue_Voltage);
    cs.insert(EmetconProtocol::GetValue_FrozenVoltage);

    return cs;
}



Mct440_213xBDevice::ConfigPartsList Mct440_213xBDevice::getPartsList()
{
    return _config_parts;
}



const Mct440_213xBDevice::FunctionReadValueMappings *Mct440_213xBDevice::getReadValueMaps() const
{
    return &_readValueMaps;
}



const std::set<UINT> *Mct440_213xBDevice::getExcludedCommands() const
{
    return &_excludedCommands;
}



bool Mct440_213xBDevice::isCommandExcluded(const UINT &cmd) const
{
    const std::set<UINT>         *p_cs = getExcludedCommands();
    std::set<UINT>::const_iterator itr = p_cs->find(cmd);

    if( itr != p_cs->end() )
    {
        return true;
    }

    return false;
}



bool Mct440_213xBDevice::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )        /* check for commands added for mct440_213xb            */
    {
        return true;
    }

    if( isCommandExcluded(cmd) )                                /* check for commands no longer supported               */
    {
        return false;
    }

    return Inherited::getOperation(cmd, bst);
}



bool Mct440_213xBDevice::isSupported(const Mct410Device::Features feature) const
{
    if (feature == Feature_OutageUnits)
    {
        return true;
    }
    if (feature == Feature_DisconnectCollar)
    {
        return false;
    }

    return Mct410Device::isSupported(feature);
}


/*
*********************************************************************************************************
*                                          executeGetValue()
*
* Description : Redefines TOU / TOU frozen kWh, instant line data and outage command parsing. Other
*               commands uses inheritance from mct420.
*
* Note(s)     :
*********************************************************************************************************
*/
YukonError_t Mct440_213xBDevice::executeGetValue(CtiRequestMsg     *pReq,
                                                 CtiCommandParser  &parse,
                                                 OUTMESS           *&OutMessage,
                                                 CtiMessageList    &vgList,
                                                 CtiMessageList    &retList,
                                                 OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;
    bool found    = false;
    int  function = -1;

                                                                /* ---------- GETVALUE COMMAND NOT SUPPORTED ---------- */
    if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
        (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
        insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                           "Bad kwh command specification: rate parameter not supported");

        nRet = ExecutionComplete;
    }

                                                                /* ------------ TOU KWH (FORWARD / REVERSE) ----------- */
    else if( (parse.getFlags() & CMD_FLAG_GV_KWH) &&
             (parse.getFlags() & CMD_FLAG_GV_TOU) )
    {
        if (parse.getFlags() & CMD_FLAG_GV_REVERSE)
        {
            if( parse.getFlags() & CMD_FLAG_FROZEN )
            {
                function = EmetconProtocol::GetValue_FrozenTOUkWhReverse;
                found    = getOperation(function, OutMessage->Buffer.BSt);
            }
            else
            {
                function = EmetconProtocol::GetValue_TOUkWhReverse;
                found    = getOperation(function, OutMessage->Buffer.BSt);
            }
        }
        else
        {
            if( parse.getFlags() & CMD_FLAG_FROZEN )
            {
                function = EmetconProtocol::GetValue_FrozenTOUkWh;
                found    = getOperation(function, OutMessage->Buffer.BSt);
            }
            else
            {
                function = EmetconProtocol::GetValue_TOUkWh;
                found    = getOperation(function, OutMessage->Buffer.BSt);
            }
        }
    }

                                                                /* ----------------- INSTANT LINE DATA ---------------- */
    else if( parse.isKeyValid("instantlinedata") )
    {
        function = EmetconProtocol::GetValue_InstantLineData;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        // adjust message length request according to the number of phase
        OutMessage->Buffer.BSt.Length = getPhaseCount() * 4;
    }

                                                                /* ----------------------- OUTAGE --------------------- */
    else if( parse.isKeyValid("outage") )
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
        {
            //  we need to set it to the requested interval
            std::unique_ptr<CtiOutMessage> sspec_om(CTIDBG_new CtiOutMessage(*OutMessage));

            if( !getOperation(EmetconProtocol::GetConfig_Model, sspec_om->Buffer.BSt) )
            {
                CTILOG_ERROR(dout, "Operation GetConfig_Model not found for device "<< getName());

                return ClientErrors::NoMethod;
            }

            sspec_om->Sequence      = EmetconProtocol::GetConfig_Model;
            sspec_om->MessageFlags |= MessageFlag_ExpectMore;

            // make this return message disappear so it doesn't confuse the client
            sspec_om->Request.Connection.reset();

            outList.push_back(sspec_om.release());
        }

        const int outagenum = parse.getiValue("outage");

        if( outagenum < OUTAGE_NBR_MIN || outagenum > OUTAGE_NBR_MAX )
        {
            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Bad outage specification - Acceptable values: " + CtiNumStr(OUTAGE_NBR_MIN) + " - " + CtiNumStr(OUTAGE_NBR_MAX));

            nRet = ExecutionComplete;
        }
        else
        {
            function = EmetconProtocol::GetValue_Outage;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            /**
             *  outagenum 1-2  --> offset of 0
             *  outagenum 3-4  --> offset of 1
             *  outagenum 5-6  --> offset of 2
             *  outagenum 7-8  --> offset of 3
             *  outagenum 9-10 --> offset of 4
             *
             **/

            OutMessage->Buffer.BSt.Function += ((outagenum - 1) / 2);
        }
    }

                                                                /* ---------------- DAILY READ RECENT ----------------- */
    else if( parse.isKeyValid("daily_read") )
    {
        bool existing_request = false;

        //  if a request is already in progress and we're not submitting a continuation/retry
        if( ! _daily_read_info.request.in_progress.compare_exchange_strong(existing_request, true) &&
            _daily_read_info.request.user_id != pReq->UserMessageId() )
        {
            string temp = getName() + " / Daily read request already in progress\n";

            temp += "Channel " + CtiNumStr(_daily_read_info.request.channel) + ", ";

            if( _daily_read_info.request.type == daily_read_info_t::Request_MultiDay )
            {
                temp += printDate(_daily_read_info.request.begin + 1) + " - " +
                        printDate(_daily_read_info.request.end) + "\n";
            }
            else
            {
                temp += printDate(_daily_read_info.request.begin);
            }

            insertReturnMsg(ClientErrors::CommandAlreadyInProgress, OutMessage, retList, temp);

            nRet = ExecutionComplete;
        }
        else
        {
            int channel = parse.getiValue("channel", 1);

            const CtiDate Today     = CtiDate(),
                          Yesterday = Today - 1;


            // If the date is not specified, we use yesterday (last full day)
            CtiDate date_begin = Yesterday;

            //  grab the beginning date
            if( parse.isKeyValid("daily_read_date_begin") )
            {
                date_begin = parseDateString(parse.getsValue("daily_read_date_begin"));
            }

            if( channel < 1 || channel > 3 )
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid channel for daily read request; must be 1-3 (" + CtiNumStr(channel) + ")");

                nRet = ExecutionComplete;
            }
            else if( date_begin > Yesterday )  //  must begin on or before yesterday
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid date for daily read request; must be before today (" + parse.getsValue("daily_read_date_begin") + ")");

                nRet = ExecutionComplete;
            }
            else if( parse.isKeyValid("daily_read_detail") )
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Bad daily read detail not supported - Acceptable format: getvalue daily read mm/dd/yyyy");

                nRet = ExecutionComplete;
            }
            else if( parse.isKeyValid("daily_read_date_end") ||
                     parse.isKeyValid("daily_reads") )
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Bad multi-day read not supported - Acceptable format: getvalue daily read mm/dd/yyyy");

                nRet = ExecutionComplete;
            }
            else if( channel != 1 )
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid channel for recent daily read request; only valid for channel 1 (" + CtiNumStr(channel)  + ")");

                nRet = ExecutionComplete;
            }
            else if( date_begin < Today - 8 )  //  must be no more than 8 days ago
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid date for recent daily read request; must be less than 8 days ago (" + parse.getsValue("daily_read_date_begin") + ")");

                nRet = ExecutionComplete;
            }
            else
            {
                unsigned long day_offset = Yesterday.daysFrom1970() - date_begin.daysFrom1970();

                OutMessage->Buffer.BSt.Function = FuncRead_Channel1SingleDayBasePos + day_offset;
                OutMessage->Buffer.BSt.Length   = FuncRead_Channel1SingleDayLen;

                _daily_read_info.request.type    = daily_read_info_t::Request_Recent;
                _daily_read_info.request.channel = 1;
                _daily_read_info.request.begin   = date_begin;

                found = true;
            }

            if( !found )
            {
                _daily_read_info.request.in_progress = false;
            }
            else
            {
                function                  = EmetconProtocol::GetValue_DailyRead;
                OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;
            }
        }
    }

                                                                /* -------------- INHERITED FROM MCT-420 -------------- */
    else
    {
        nRet = Inherited::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}



YukonError_t Mct440_213xBDevice::executeGetStatus(CtiRequestMsg    *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS           *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    // ------------------- EVENT LOG --------------------- //

    if( parse.isKeyValid("eventlog") )
    {
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        OutMessage->Port     = getPortID();
        OutMessage->Remote   = getAddress();
        OutMessage->TimeOut  = 2;
        OutMessage->Retry    = 2;

        OutMessage->Request.RouteID = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Read;
        OutMessage->Sequence      = EmetconProtocol::GetStatus_EventLog;

        for( int offset = 0; offset <= Memory_EventLogMaxOffset; offset++ )
        {
            OutMessage->Buffer.BSt.Function = Memory_EventLogBasePos + offset;
            OutMessage->Buffer.BSt.Length   = Memory_EventLogLen;

            outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
        }

        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle(), outList.size());

        delete OutMessage;  //  we didn't use it, we made our own
        OutMessage = 0;

        nRet = ClientErrors::None;
    }

    // -------------- INHERITED FROM MCT-420 -------------- //

    else
    {
        nRet = Inherited::executeGetStatus(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


bool Mct440_213xBDevice::sspecValid(const unsigned sspec, const unsigned rev) const
{
    return sspec == Mct440_213xBDevice::Sspec;
}



YukonError_t Mct440_213xBDevice::ModelDecode(const INMESS    &InMessage,
                                             const CtiTime    TimeNow,
                                             CtiMessageList  &vgList,
                                             CtiMessageList  &retList,
                                             OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;

    switch(InMessage.Sequence)
    {
        case EmetconProtocol::GetValue_InstantLineData:
            status = decodeGetValueInstantLineData(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::Scan_Accum:
        case EmetconProtocol::GetValue_KWH:
        case EmetconProtocol::GetValue_FrozenKWH:
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetValue_TOUkWh:
        case EmetconProtocol::GetValue_FrozenTOUkWh:
        case EmetconProtocol::GetValue_TOUkWhReverse:
        case EmetconProtocol::GetValue_FrozenTOUkWhReverse:
            status = decodeGetValueTOUkWh(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetValue_DailyRead:
            if( _daily_read_info.request.type == daily_read_info_t::Request_Recent )
            {
                status = decodeGetValueDailyReadRecent(InMessage, TimeNow, vgList, retList, outList);
            }
            else
            {
                status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
            }
            break;

        case EmetconProtocol::GetConfig_TOU:
            status = decodeGetConfigTOU(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_PhaseLossThreshold:
            status = decodeGetConfigPhaseLossThreshold(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_AlarmMask:
            status = decodeGetConfigAlarmMask(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_Addressing:
            status = decodeGetConfigAddressing(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_TimeAdjustTolerance:
            status = decodeGetConfigTimeAdjustTolerance(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_Options:
            status = decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetStatus_EventLog:
            status = decodeGetStatusEventLog(InMessage, TimeNow, vgList, retList, outList);
            break;

        default:
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
            if( status )
            {
                CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
            }
    }

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetValueInstantLineData(const INMESS    &InMessage,
                                                               const CtiTime    TimeNow,
                                                               CtiMessageList  &vgList,
                                                               CtiMessageList  &retList,
                                                               OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const int phase_count = getPhaseCount();

    for( int phase_nbr = 0; phase_nbr < phase_count; phase_nbr++ )
    {
        int pointOffset;

        const unsigned char *PhaseData = DSt->Message + (phase_nbr*4);

        point_info PhaseVoltage;

                                                                /* Bits {31:20}    Voltage in units of 0.1V, range 0 to 409.5V  */
        PhaseVoltage.value       = (PhaseData[0] << 4) | (PhaseData[1] >> 4);
        PhaseVoltage.quality     = NormalQuality;

        switch( phase_nbr )
        {
            case 0: pointOffset = PointOffset_Analog_LineVoltagePhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LineVoltagePhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LineVoltagePhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg.get(),
                              PhaseVoltage,
                              "Phase Line Voltage",
                              CtiTime(),
                              0.1);

        point_info PhaseCurrent;

                                                                /* Bits {19:8} Current in units of 0.1 V, range 0 to 409.5A     */
        PhaseCurrent.value       = ((PhaseData[1] & 0xf) << 8) | (PhaseData[2]);
        PhaseCurrent.quality     = NormalQuality;

        switch( phase_nbr )
        {
            case 0: pointOffset = PointOffset_Analog_LineCurrentPhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LineCurrentPhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LineCurrentPhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg.get(),
                              PhaseCurrent,
                              "Phase Line Current",
                              CtiTime(),
                              0.1);

        point_info PowerFactor;

                                                                /* Bits {7:0} Power factor in units of 0.01, range -1.00 to 1.00 */
        int PowerFactorVal = reinterpret_cast<const char &>(PhaseData[3]);

        if( PowerFactorVal < -100 || PowerFactorVal > 100 )
        {
            PowerFactor.value       = 0.0;
            PowerFactor.quality     = (PowerFactorVal > 100) ? ExceedsHighQuality : ExceedsLowQuality;

            status = ClientErrors::InvalidData;
        }
        else
        {
            PowerFactor.value       = static_cast<double>(PowerFactorVal) / 100.0;
            PowerFactor.quality     = NormalQuality;
        }

        switch( phase_nbr )
        {
            case 0: pointOffset = PointOffset_Analog_LinePowFactPhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LinePowFactPhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LinePowFactPhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg.get(),
                              PowerFactor,
                              "Phase Line Power Factor",
                              CtiTime());
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetValueTOUkWh(const INMESS    &InMessage,
                                                      const CtiTime    TimeNow,
                                                      CtiMessageList  &vgList,
                                                      CtiMessageList  &retList,
                                                      OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned char *freeze_counter = 0;

    if( InMessage.Sequence == EmetconProtocol::GetValue_FrozenTOUkWh ||
        InMessage.Sequence == EmetconProtocol::GetValue_FrozenTOUkWhReverse)
    {
        string freeze_error;

        freeze_counter = DSt->Message + 12;

        if( status = checkFreezeLogic(TimeNow, *freeze_counter, freeze_error) )
        {
            ReturnMsg->setResultString(freeze_error);
        }
    }

    if( !status )
    {
        frozen_point_info pi;
        CtiTime    point_time = TimeNow;

        for( int rate_nbr = 0; rate_nbr < 4; rate_nbr++ )
        {
            int offset = (rate_nbr * 3);

            if( InMessage.Sequence == EmetconProtocol::GetValue_TOUkWh ||
                InMessage.Sequence == EmetconProtocol::GetValue_TOUkWhReverse)
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, 0);
            }
            else if( InMessage.Sequence == EmetconProtocol::GetValue_FrozenTOUkWh ||
                     InMessage.Sequence == EmetconProtocol::GetValue_FrozenTOUkWhReverse)
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, freeze_counter);

                if( pi.freeze_bit != getExpectedFreezeParity() )
                {
                    CTILOG_ERROR(dout, "incoming freeze parity bit ("<< pi.freeze_bit <<") does not match expected freeze bit ("<< getExpectedFreezeParity() <<") on device \""<< getName() <<"\" - not sending data");

                    pi.description  = "Freeze parity does not match (";
                    pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                    pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                    pi.quality = InvalidQuality;
                    pi.value = 0;

                    ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                    status = ClientErrors::InvalidFrozenReadingParity;
                }
                else
                {
                    point_time = getLastFreezeTimestamp(TimeNow);
                }
            }

            string point_name;
            int    point_offset = -1;

            switch (InMessage.Sequence)
            {
                case EmetconProtocol::GetValue_TOUkWh:
                    point_offset = (rate_nbr * PointOffset_RateOffset) + 1 + PointOffset_PulseAcc_TOUBaseFwd;
                    break;
                case EmetconProtocol::GetValue_FrozenTOUkWh:
                    point_name   = "Frozen ";
                    point_offset = (rate_nbr * PointOffset_RateOffset) + 1 + PointOffset_PulseAcc_TOUBaseFwdFrozen;
                    break;
                case EmetconProtocol::GetValue_TOUkWhReverse:
                    point_offset = (rate_nbr * PointOffset_RateOffset) + 1 + PointOffset_PulseAcc_TOUBaseRev;
                    break;
                case EmetconProtocol::GetValue_FrozenTOUkWhReverse:
                    point_name   = "Frozen ";
                    point_offset = (rate_nbr * PointOffset_RateOffset) + 1 + PointOffset_PulseAcc_TOUBaseRevFrozen;
                    break;
            }

            point_name += string("TOU rate ") + (char)('A' + rate_nbr) + " kWh";

            //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
            insertPointDataReport(PulseAccumulatorPointType,
                                  point_offset,
                                  ReturnMsg.get(),
                                  pi,
                                  point_name,
                                  point_time,
                                  0.1,
                                  TAG_POINT_MUST_ARCHIVE);

            //  if the quality's invalid, throw the status to abnormal if there's a point defined
            if( pi.quality == InvalidQuality && !status && getDevicePointOffsetTypeEqual(point_offset, PulseAccumulatorPointType) )
            {
                ReturnMsg->setResultString("Invalid data returned\n" + ReturnMsg->ResultString());
                status = ClientErrors::InvalidData;
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}

/*
*********************************************************************************************************
*                                   decodeGetStatusEventLog()
*
* Description : Decode event log received (read 0x50 - 0x59)
*
* Note(s)     :
*********************************************************************************************************
*/
YukonError_t Mct440_213xBDevice::decodeGetStatusEventLog(const INMESS    &InMessage,
                                                         const CtiTime    TimeNow,
                                                         CtiMessageList  &vgList,
                                                         CtiMessageList  &retList,
                                                         OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;

    const int offset = InMessage.Return.ProtocolInfo.Emetcon.Function - Memory_EventLogBasePos;

    if( offset < 0 || offset > Memory_EventLogMaxOffset )
    {
        CTILOG_ERROR(dout, "Invalid InMessage.Return.ProtocolInfo.Emetcon.Function ("<< InMessage.Return.ProtocolInfo.Emetcon.Function <<")");

        return ClientErrors::TypeNotFound;
    }

    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    const unsigned long timestamp = ( DSt.Message[0] << 24 ) |
                                    ( DSt.Message[1] << 16 ) |
                                    ( DSt.Message[2] <<  8 ) |
                                    ( DSt.Message[3] );

    const unsigned long userId    = ( DSt.Message[4] <<  8 ) |
                                    ( DSt.Message[5] );

    const unsigned long eventCode = ( DSt.Message[6] <<  8 ) |
                                    ( DSt.Message[7] );

    const unsigned long argument  = ( DSt.Message[8] <<  8 ) |
                                    ( DSt.Message[9] );

    string eventName,
           resolvedArgument;

    if( !Mct440_213xBEventLog::resolveEventCode( eventCode, argument, eventName, resolvedArgument ))
    {
        eventName = "received invalid event code " + CtiNumStr(eventCode);
    }

    string resultString = getName() + " / Parameters Change:\n" +
                          "Time: " + CtiTime(timestamp).asString() + "\n" +
                          "User ID: " + CtiNumStr(userId) + "\n" +
                          "Event: " + eventName + "\n";

    if( !resolvedArgument.empty() )
    {
        resultString += resolvedArgument + "\n";
    }

    std::unique_ptr<CtiReturnMsg> ReturnMsg( CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr ));

    ReturnMsg->setUserMessageId( InMessage.Return.UserID );
    ReturnMsg->setResultString( resultString );

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
    if( InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection ))
    {
        ReturnMsg->setExpectMore(true);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}

/*
*********************************************************************************************************
*                                       executePutConfigTOU()
*
* Description : Execute a putconfig install time of usage command: putconfig install tou [force] [verify]
*               Command will install up to 10 rates and 9 schedule time.
*
* Note(s)     : (1) Rate0 correspond to the midnight associated rate
*********************************************************************************************************
*/
YukonError_t Mct440_213xBDevice::executePutConfigTOU(CtiRequestMsg     *pReq,
                                                     CtiCommandParser  &parse,
                                                     OUTMESS          *&OutMessage,
                                                     CtiMessageList    &vgList,
                                                     CtiMessageList    &retList,
                                                     OutMessageList    &outList,
                                                     bool               readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    long value, tempTime;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CTILOG_ERROR(dout, "deviceConfig is null");

        nRet = ClientErrors::NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        long times[TOU_SCHEDULE_NBR][TOU_SCHEDULE_TIME_NBR];
        long rates[TOU_SCHEDULE_NBR][TOU_SCHEDULE_RATE_NBR];
        string rateStringValues[TOU_SCHEDULE_NBR][TOU_SCHEDULE_RATE_NBR],
               timeStringValues[TOU_SCHEDULE_NBR][TOU_SCHEDULE_TIME_NBR],
               daySchedule1, daySchedule2, daySchedule3, daySchedule4,
               dynDaySchedule1, dynDaySchedule2, dynDaySchedule3, dynDaySchedule4;

        // Unfortunatelly the arrays have a 0 offset, while the schedules times/rates are referenced with a 1 offset
        // Also note that rate "0" is the midnight rate.

        //These are all string values
        timeStringValues[0][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time1);
        timeStringValues[0][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time2);
        timeStringValues[0][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time3);
        timeStringValues[0][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time4);
        timeStringValues[0][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time5);
        timeStringValues[0][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time6);
        timeStringValues[0][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time7);
        timeStringValues[0][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time8);
        timeStringValues[0][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time9);

        timeStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time1);
        timeStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time2);
        timeStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time3);
        timeStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time4);
        timeStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time5);
        timeStringValues[1][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time6);
        timeStringValues[1][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time7);
        timeStringValues[1][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time8);
        timeStringValues[1][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time9);

        timeStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time1);
        timeStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time2);
        timeStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time3);
        timeStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time4);
        timeStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time5);
        timeStringValues[2][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time6);
        timeStringValues[2][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time7);
        timeStringValues[2][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time8);
        timeStringValues[2][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time9);

        timeStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time1);
        timeStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time2);
        timeStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time3);
        timeStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time4);
        timeStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time5);
        timeStringValues[3][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time6);
        timeStringValues[3][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time7);
        timeStringValues[3][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time8);
        timeStringValues[3][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time9);

        rateStringValues[0][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate0); // midnight rate schedule 1
        rateStringValues[0][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate1);
        rateStringValues[0][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate2);
        rateStringValues[0][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate3);
        rateStringValues[0][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate4);
        rateStringValues[0][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate5);
        rateStringValues[0][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate6);
        rateStringValues[0][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate7);
        rateStringValues[0][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate8);
        rateStringValues[0][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate9);

        rateStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate0); // midnight rate schedule 2
        rateStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate1);
        rateStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate2);
        rateStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate3);
        rateStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate4);
        rateStringValues[1][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate5);
        rateStringValues[1][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate6);
        rateStringValues[1][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate7);
        rateStringValues[1][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate8);
        rateStringValues[1][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate9);

        rateStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate0); // midnight rate schedule 3
        rateStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate1);
        rateStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate2);
        rateStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate3);
        rateStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate4);
        rateStringValues[2][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate5);
        rateStringValues[2][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate6);
        rateStringValues[2][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate7);
        rateStringValues[2][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate8);
        rateStringValues[2][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate9);

        rateStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate0); // midnight rate schedule 4
        rateStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate1);
        rateStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate2);
        rateStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate3);
        rateStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate4);
        rateStringValues[3][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate5);
        rateStringValues[3][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate6);
        rateStringValues[3][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate7);
        rateStringValues[3][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate8);
        rateStringValues[3][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate9);

        for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
        {
            for( int rate = 0; rate < TOU_SCHEDULE_RATE_NBR; rate++ )
            {
                if( rateStringValues[schedule][rate].empty() )
                {
                    CTILOG_ERROR(dout, "bad rate string stored");

                    nRet = ClientErrors::NoConfigData;
                }
            }
        }

        for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
        {
            for( int switchtime = 0; switchtime < TOU_SCHEDULE_TIME_NBR; switchtime++ )
            {
                // A time needs at least 4 digits X:XX and no more then 5 digits XX:XX
                if( timeStringValues[schedule][switchtime].length() < 4 || timeStringValues[schedule][switchtime].length() > 5 )
                {
                    CTILOG_ERROR(dout, "bad time string stored");

                    nRet = ClientErrors::NoConfigData;
                }
            }
        }

        if( nRet != ClientErrors::NoConfigData )
        {
            //Do conversions from strings to longs here.
            for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
            {
                for( int rate = 0; rate < TOU_SCHEDULE_RATE_NBR; rate++ )
                {
                    rates[schedule][rate] = rateStringValues[schedule][rate][0] - 'A';
                    if( rates[schedule][rate] < 0 || rates[schedule][rate] > 3 )
                    {
                        CTILOG_ERROR(dout, "bad rate string stored");

                        nRet = ClientErrors::NoConfigData;
                    }
                }
            }

            for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
            {
                for( int switchtime = 0; switchtime < TOU_SCHEDULE_TIME_NBR; switchtime++ )
                {
                    char sep;
                    int  hour, minute;

                    istringstream ss(timeStringValues[schedule][switchtime]);
                    ss >> hour >> sep >> minute;

                    if( hour   >= 0 && hour   < 24 &&
                        minute >= 0 && minute < 60 &&
                        sep == ':' )
                    {
                        times[schedule][switchtime] = hour*60 + minute;
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "bad time string stored");
                        nRet = ClientErrors::NoConfigData;
                    }
                }

                // Time is currently the actual minutes, we need the difference. Also the MCT has 5 minute resolution.
                for( int switchtime = (TOU_SCHEDULE_TIME_NBR-1); switchtime >= 0; switchtime-- )
                {
                    if( switchtime > 0 )
                    {
                        times[schedule][switchtime] = times[schedule][switchtime] - times[schedule][switchtime - 1];
                    }

                    times[schedule][switchtime] = times[schedule][switchtime] / 5;

                    if( times[schedule][switchtime] < 0 || times[schedule][switchtime] > 255 )
                    {
                        CTILOG_ERROR(dout, "invalid time sequencing");

                        nRet = ClientErrors::NoConfigData;
                    }
                }
            }
        }

        const long sundaySchedule   = resolveScheduleName(deviceConfig->getValueFromKey(MCTStrings::SundaySchedule  )),
                   weekdaysSchedule = resolveScheduleName(deviceConfig->getValueFromKey(MCTStrings::WeekdaysSchedule)),
                   saturdaySchedule = resolveScheduleName(deviceConfig->getValueFromKey(MCTStrings::SaturdaySchedule)),
                   holidaySchedule  = resolveScheduleName(deviceConfig->getValueFromKey(MCTStrings::HolidaySchedule ));

        if( nRet == ClientErrors::NoConfigData
            || sundaySchedule   < 0 || sundaySchedule   > 3
            || weekdaysSchedule < 0 || weekdaysSchedule > 3
            || saturdaySchedule < 0 || saturdaySchedule > 3
            || holidaySchedule  < 0 || holidaySchedule  > 3 )
        {
            CTILOG_ERROR(dout, "no or bad schedule value stored");

            nRet = ClientErrors::NoConfigData;
        }
        else
        {
            long dayTable = 0;

            dayTable |= holidaySchedule  << 14;
            dayTable |= saturdaySchedule << 12;
            dayTable |= weekdaysSchedule << 2;
            dayTable |= sundaySchedule;

            createTOUDayScheduleString(daySchedule1, times[0], rates[0]);
            createTOUDayScheduleString(daySchedule2, times[1], rates[1]);
            createTOUDayScheduleString(daySchedule3, times[2], rates[2]);
            createTOUDayScheduleString(daySchedule4, times[3], rates[3]);

            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dynDaySchedule1);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dynDaySchedule2);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dynDaySchedule3);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dynDaySchedule4);

            if( parse.isKeyValid("force")
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable) != dayTable
                || dynDaySchedule1 != daySchedule1
                || dynDaySchedule2 != daySchedule2
                || dynDaySchedule3 != daySchedule3
                || dynDaySchedule4 != daySchedule4 )
            {
                if( !parse.isKeyValid("verify") )
                {
                    long daySchedule[8];

                    for (int day = 0; day < 8; day++)
                    {
                        daySchedule[day] = (dayTable >> (day*2)) & 0x03;
                    }

                    createTOUScheduleConfig(daySchedule,
                                            times,
                                            rates,
                                            OutMessage,
                                            outList);
                }
                else
                {
                    nRet = ClientErrors::ConfigNotCurrent;
                }
            }
            else
            {
                nRet = ClientErrors::ConfigCurrent;
            }
        }
    }
    else // getconfig install
    {
        const int priority              = OutMessage->Priority;
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;

        strncpy(OutMessage->Request.CommandStr, "getconfig tou schedule 1", COMMAND_STR_SIZE );

        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Priority            = priority - 1; // decrease priority for part 2 to make sure we process it after part 1
        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        strncpy(OutMessage->Request.CommandStr, "getconfig tou schedule 3", COMMAND_STR_SIZE );

        OutMessage->Priority            = priority;
        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Priority            = priority - 1; // decrease priority for part 2 to make sure we process it after part 1
        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        strncpy(OutMessage->Request.CommandStr, "getconfig tou", COMMAND_STR_SIZE );

        OutMessage->Priority            = priority;
        OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return nRet;
}



YukonError_t Mct440_213xBDevice::executeGetConfig(CtiRequestMsg     *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS          *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    populateDlcOutMessage(*OutMessage);
    OutMessage->Request.RouteID = getRouteID();

                                                                /* --------------- INSTALL CONFIG PART  --------------- */
    if( parse.isKeyValid("install") )
    {
        nRet = Mct4xxDevice::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

                                                                /* ------------ TOU RATE SCHEDULE / STATUS ------------ */
    else if( parse.isKeyValid("tou") )
    {
        OutMessage->Sequence        = EmetconProtocol::GetConfig_TOU;
        OutMessage->Buffer.BSt.IO   = EmetconProtocol::IO_Function_Read;

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        if( parse.isKeyValid("tou_schedule") )
        {
            int schedulenum = parse.getiValue("tou_schedule");

            if( schedulenum == 1 || schedulenum == 2 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle(), outList.size());

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = ClientErrors::None;
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle(), outList.size());

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = ClientErrors::None;
            }
            else
            {
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "invalid schedule number " + CtiNumStr(schedulenum));

                nRet = ExecutionComplete;
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;

            nRet = ClientErrors::None;
        }
    }

                                                                /* --------------- PHASE LOSS THRESHOLD --------------- */
    else if( parse.isKeyValid("phaseloss_threshold") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_PhaseLossThreshold;

        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = ClientErrors::None;
        }
    }

                                                                /* --------------------- HOLIDAYS --------------------- */
    else if( parse.isKeyValid("holiday") )
    {
        auto holidayRead = std::make_unique<Commands::Mct440HolidaysCommand>();

        if( tryExecuteCommand(*OutMessage, std::move(holidayRead)) )
        {
            nRet = ClientErrors::None;
        }
    }

                                                                /* -------------------- ALARM MASK -------------------- */
    else if( parse.isKeyValid("alarm_mask") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_AlarmMask;

        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = ClientErrors::None;
        }
    }

                                                                /* --------------------- OPTIONS ---------------------- */
    else if( parse.isKeyValid("options") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Options;

        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = ClientErrors::None;
        }
    }

                                                                /* -------------- INHERITED FROM MCT-420 -------------- */
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}



long Mct440_213xBDevice::resolveScheduleName(const string & scheduleName)
{
    boost::optional<long> value = Cti::mapFind(scheduleMap, scheduleName);
    if (value)
    {
        return *value;
    }
    return -1;
}



void Mct440_213xBDevice::createTOUDayScheduleString(string &schedule, long (&times)[9], long (&rates)[10])
{
    for( int time_nbr = 0; time_nbr < COUNT_OF(times); time_nbr++ )
    {
        schedule += CtiNumStr(times[time_nbr]) + ", ";
    }

    for( int rate_nbr = 0; rate_nbr < (COUNT_OF(rates)-1); rate_nbr++ )
    {
        schedule += CtiNumStr(rates[rate_nbr]) + ", ";
    }

    schedule += CtiNumStr(rates[COUNT_OF(rates)-1]);
}



void Mct440_213xBDevice::parseTOUDayScheduleString(string &schedule, long (&times)[9], long (&rates)[10])
{
    char sep[2]; // expecting 2 characters: ", "
    istringstream ss(schedule);

    for( int time_nbr = 0; time_nbr < COUNT_OF(times); time_nbr++ )
    {
        ss >> times[time_nbr] >> sep;
    }

    for( int rate_nbr = 0; rate_nbr < (COUNT_OF(rates)-1); rate_nbr++ )
    {
        ss >> rates[rate_nbr] >> sep;
    }

    ss >> rates[COUNT_OF(rates)-1];
}



YukonError_t Mct440_213xBDevice::decodeGetValueDailyReadRecent(const INMESS    &InMessage,
                                                               const CtiTime    TimeNow,
                                                               CtiMessageList  &vgList,
                                                               CtiMessageList  &retList,
                                                               OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;

    string resultString;
    bool expectMore = false;

    const DSTRUCT * const DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    int channel = 1;
    int month   = DSt->Message[9] & 0x0f;
    int day     = DSt->Message[8];

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, channel);

    //  These two need to be available to be checked against ErrorText_OutOfRange below
    point_info pi_forward = getAccumulatorData(DSt->Message + 0, 3, 0); //FIXME : see if we should replace this with getData()
    point_info pi_reverse = getAccumulatorData(DSt->Message + 3, 3, 0); //FIXME : see if we should replace this with getData()

    if( channel != _daily_read_info.request.channel )
    {
        resultString  = getName() + " / Invalid channel returned by daily read ";
        resultString += "(" + CtiNumStr(channel) + "), expecting (" + CtiNumStr(_daily_read_info.request.channel) + ")";

        status = ClientErrors::InvalidChannel;
    }
    else if(  day    !=  _daily_read_info.request.begin.dayOfMonth() ||
              month  != (_daily_read_info.request.begin.month() - 1) )
    {
        resultString  = getName() + " / Invalid day/month returned by daily read ";
        resultString += "(" + CtiNumStr(day) + "/" + CtiNumStr(month + 1) + ", expecting " + CtiNumStr(_daily_read_info.request.begin.dayOfMonth()) + "/" + CtiNumStr(_daily_read_info.request.begin.month()) + ")";

        //  These come back as 0x7ff.. if daily reads are disabled, but also if the request really was out of range
        //  Ideally, this check would be against an enum or similar rather than a string,
        //    but unless getAccumulatorData is refactored to provide more than a text description and quality, this will have to do.
        if( pi_forward.description == ErrorText_OutOfRange &&
            pi_reverse.description == ErrorText_OutOfRange )
        {
            resultString += "\n";
            resultString += getName() + " / Daily reads might be disabled, check device configuration";
        }

        _daily_read_info.interest.date = DawnOfTime_Date;  //  reset it - it doesn't match what the MCT has

        status = ClientErrors::InvalidTimestamp;
    }
    else
    {
        point_info pi_outage_count = getData(DSt->Message + 6, 2, ValueType_OutageCount);

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_Accumulator_Powerfail,
                              ReturnMsg.get(),
                              pi_outage_count,
                              "Blink Counter",
                              CtiTime(_daily_read_info.request.begin + 1));  //  add on 24 hours - end of day

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_BaseMRead,
                              ReturnMsg.get(),
                              pi_forward,
                              "forward active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_BaseMRead + 1,
                              ReturnMsg.get(),
                              pi_reverse,
                              "reverse active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        // echo forward active kWh and reverse active kWh to pulse acc 181 and 281 if they exist

        if( getDevicePointOffsetTypeEqual(PointOffset_PulseAcc_RecentkWhForward, PulseAccumulatorPointType ))
        {
            insertPointDataReport(PulseAccumulatorPointType,
                                  PointOffset_PulseAcc_RecentkWhForward,
                                  ReturnMsg.get(),
                                  pi_forward,
                                  "recent forward active kWh",
                                  CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                                  TAG_POINT_MUST_ARCHIVE);
        }

        if( getDevicePointOffsetTypeEqual(PointOffset_PulseAcc_RecentkWhReverse, PulseAccumulatorPointType ))
        {
            insertPointDataReport(PulseAccumulatorPointType,
                                  PointOffset_PulseAcc_RecentkWhReverse,
                                  ReturnMsg.get(),
                                  pi_reverse,
                                  "recent reverse active kWh",
                                  CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                                  TAG_POINT_MUST_ARCHIVE);
        }

        _daily_read_info.request.in_progress = false;
    }

    //  this is gross
    if( !ReturnMsg->ResultString().empty() )
    {
        resultString = ReturnMsg->ResultString() + "\n" + resultString;
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList, expectMore );

    return status;
}



YukonError_t Mct440_213xBDevice::executePutConfig(CtiRequestMsg     *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS          *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    populateDlcOutMessage(*OutMessage);

    if( parse.isKeyValid("install") )
    {
        nRet = Mct4xxDevice::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("holiday_date0") )  //  require at least one date
    {
        nRet = executePutConfigHoliday(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("tou") )
    {
        nRet = executePutConfigTOUDays(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("phaseloss_percent_threshold"))
    {
        nRet = executePutConfigPhaseLossThreshold(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("alarm_mask") )
    {
        nRet = executePutConfigAlarmMask(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}



YukonError_t Mct440_213xBDevice::executePutConfigHoliday(CtiRequestMsg     *pReq,
                                                         CtiCommandParser  &parse,
                                                         OUTMESS          *&OutMessage,
                                                         CtiMessageList    &vgList,
                                                         CtiMessageList    &retList,
                                                         OutMessageList    &outList)
{
    const std::vector<std::string> parseKeys = boost::assign::list_of
        ("holiday_date0") ("holiday_date1") ("holiday_date2")
        ("holiday_date3") ("holiday_date4") ("holiday_date5")
        ("holiday_date6") ("holiday_date7") ("holiday_date8")
        ("holiday_date9") ("holiday_date10")("holiday_date11")
        ("holiday_date12")("holiday_date13")("holiday_date14");

    std::set<CtiDate> holidays;

    //  grab up to 15 potential dates
    for each( const std::string holidayKey in parseKeys )
    {
        boost::optional<std::string> holidayString = parse.findStringForKey(holidayKey);

        if( ! holidayString )
        {
            break;
        }

        int month, day, year;
        char sep1, sep2;

        istringstream ss(*holidayString);

        ss >> month >> sep1 >> day >> sep2 >> year;

        CtiDate holiday_date(day, month, year);

        holidays.insert(holiday_date);
    }

    auto holidayWrite = std::make_unique<Commands::Mct440HolidaysCommand>(CtiTime::now(), holidays);

    //  this call might be able to move out to ExecuteRequest() at some point - maybe we just return
    //    a DlcCommand object that it can execute out there
    if( ! tryExecuteCommand(*OutMessage, std::move(holidayWrite)) )
    {
        return ClientErrors::NoMethod;
    }

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::executePutConfigTOUDays(CtiRequestMsg     *pReq,
                                                         CtiCommandParser  &parse,
                                                         OUTMESS          *&OutMessage,
                                                         CtiMessageList    &vgList,
                                                         CtiMessageList    &retList,
                                                         OutMessageList    &outList)
{
    bool  found = false;
    YukonError_t nRet  = ClientErrors::NoMethod;

    if( parse.isKeyValid("tou_enable") )
    {
        OutMessage->Sequence = EmetconProtocol::PutConfig_TOUEnable;
        found                = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("tou_disable") )
    {
        OutMessage->Sequence = EmetconProtocol::PutConfig_TOUDisable;
        found                = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("tou_days") )
    {
        set< ratechange_t > ratechanges;
        long day_schedules[8] = {0};

        string schedule_name, daytable(parse.getsValue("tou_days"));

        if( parse.isKeyValid("tou_default") )
        {
            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "TOU default rate \"" + parse.getsValue("tou_default") + "\" specified is invalid for device \"" + getName() + "\"");

             return ExecutionComplete;
        }

        if( daytable.length() != 4 || daytable.find_first_not_of("1234") != string::npos )
        {
            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "day table \"" + daytable + "\" specified is invalid for device \"" + getName() + "\"");

            return ExecutionComplete;
        }

        day_schedules[0] = atoi(daytable.substr(0, 1).c_str()) - 1; // sunday
        day_schedules[1] = atoi(daytable.substr(1, 1).c_str()) - 1; // weekdays
        day_schedules[6] = atoi(daytable.substr(2, 1).c_str()) - 1; // saturday
        day_schedules[7] = atoi(daytable.substr(3, 1).c_str()) - 1; // holiday

        int schedulenum = 0;

        schedule_name.assign("tou_schedule_");
        schedule_name.append(CtiNumStr(schedulenum).zpad(2));

        while(parse.isKeyValid(schedule_name.c_str()))
        {
            int schedule_number = parse.getiValue(schedule_name.c_str());

            if( schedule_number > 0 && schedule_number <= TOU_SCHEDULE_NBR )
            {
                int changenum = 0;

                string change_name(schedule_name);
                change_name.append("_");
                change_name.append(CtiNumStr(changenum).zpad(2));

                while(parse.isKeyValid(change_name.c_str()))
                {
                    string ratechangestr = parse.getsValue(change_name.c_str());

                    istringstream ss(ratechangestr);

                    char rate_c, sep1, sep2;
                    int rate, hour, minute;
                    ss >> rate_c >> sep1 >> hour >> sep2 >> minute;

                    switch(rate_c)
                    {
                        case 'a':   rate =  0;  break;
                        case 'b':   rate =  1;  break;
                        case 'c':   rate =  2;  break;
                        case 'd':   rate =  3;  break;
                        default:    rate = -1;  break;
                    }

                    if( rate   >= 0   &&
                        hour   >= 0   && hour   <  24 &&
                        minute >= 0   && minute <  60)
                    {
                        ratechange_t ratechange;

                        ratechange.schedule = schedule_number - 1;
                        ratechange.rate     = rate;
                        ratechange.time     = hour * 3600 + minute * 60;

                        ratechanges.insert(ratechange);
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "schedule \""<< schedule_number <<"\" has invalid rate change \""<< ratechangestr <<"\"for device \""<< getName() <<"\"");
                    }

                    changenum++;
                    change_name.assign(schedule_name);
                    change_name.append("_");
                    change_name.append(CtiNumStr(changenum).zpad(2));
                }
            }
            else
            {
                CTILOG_ERROR(dout, "schedule \""<< schedule_number <<"\" specified is out of range for device \""<< getName() <<"\"");
            }

            schedulenum++;
            schedule_name.assign("tou_schedule_");
            schedule_name.append(CtiNumStr(schedulenum).zpad(2));
        }

        //  There's much more intelligence and safeguarding that could be added to the below,
        //  but it's a temporary fix, to be handled soon by the proper MCT Configs,
        //  so I don't think it's worth it at the moment to add all of the smarts.
        //  We'll handle a good string, and kick out on anything else.

        long times[TOU_SCHEDULE_NBR][TOU_SCHEDULE_TIME_NBR];
        long rates[TOU_SCHEDULE_NBR][TOU_SCHEDULE_RATE_NBR];

        for( int schedule_nbr = 0; schedule_nbr < TOU_SCHEDULE_NBR; schedule_nbr++ )
        {
            std::fill(times[schedule_nbr], times[schedule_nbr] + TOU_SCHEDULE_TIME_NBR, 255);
            std::fill(rates[schedule_nbr], rates[schedule_nbr] + TOU_SCHEDULE_RATE_NBR,   0);
        }

        int current_schedule = -1;
        int offset           =  0;
        int time_offset      =  0;

        std::set< ratechange_t >::iterator itr;

        for( itr = ratechanges.begin(); itr != ratechanges.end(); itr++ )
        {
            const ratechange_t &rc = *itr;

            if( rc.schedule != current_schedule )
            {
                offset           = 0;
                time_offset      = 0;
                current_schedule = rc.schedule;
            }
            else
            {
                offset++;
            }

            if( offset > TOU_SCHEDULE_TIME_NBR || rc.schedule < 0 || rc.schedule > (TOU_SCHEDULE_NBR-1) )
            {
                CTILOG_ERROR(dout, "invalid schedule");

                continue;
            }

            if( offset == 0 && rc.time == 0 )
            {
                // this is a special case, because we can't access
                // durations[rc.schedule][offset-1] yet - offset isn't 1 yet

                rates[rc.schedule][0] = rc.rate;
            }
            else
            {
                if( offset == 0 )
                {
                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Invalid rate change time for device \"" + getName() + "\"; first rate change time for schedule (" + CtiNumStr(rc.schedule) + ") is not midnight");

                    return ExecutionComplete;
                }

                times[rc.schedule][offset - 1] = (rc.time - time_offset) / 300;
                rates[rc.schedule][offset]     = rc.rate;

                if( (offset + 1) <= (TOU_SCHEDULE_RATE_NBR-1) )
                {
                    // this is to work around the 255 * 5 min limitation for switches - this way it doesn't
                    // jump back to the default rate if only a midnight rate is specified
                    rates[rc.schedule][offset + 1] = rc.rate;
                }

                time_offset = rc.time - (rc.time % 300);  //  make sure we don't miss the 5-minute marks
            }
        }

        OutMessage->Sequence = EmetconProtocol::PutConfig_TOU;

        createTOUScheduleConfig(day_schedules,
                                times,
                                rates,
                                OutMessage,
                                outList);

        delete OutMessage;  //  we didn't use it, we made our own
        OutMessage = 0;

        found = true;
    }

    if( found )
    {
        nRet = ClientErrors::None;
    }

    return nRet;
}



string Mct440_213xBDevice::describeStatusAndEvents(const unsigned char *buf)
{
    if( !buf )
    {
        return string("null buffer error");
    }

    string descriptor;

    // Point offset 10 - Event Flag (byte 1)
    descriptor += (buf[1] & 0x01)?"Power Fail occurred\n":"";
    // 0x02 - 0x80 is not used

    // Point offset 20 - Event Flag (byte 0)
    // 0x10 - 0x80 aren't used yet

    // Starts at offset 30 - NOTE that this is byte 0 (Status)
    descriptor += (buf[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
    // 0x20 is not used
    descriptor += (buf[0] & 0x04)?"DST active\n":"DST inactive\n";
    descriptor += (buf[0] & 0x08)?"Holiday active\n":"Holiday inactive\n";
    // 0x10 is not used
    descriptor += (buf[0] & 0x20)?"Clock error\n":"";
    // 0x40 - 0x80 is not used

    return descriptor;
}



YukonError_t Mct440_213xBDevice::decodeGetStatusInternal( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    string resultString;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    resultString  = getName() + " / Internal Status:\n";

    //  point offsets 10-39
    resultString += describeStatusAndEvents(DSt.Message);

    // point offset 40 - Meter alarm (byte 1)
    // 0x01 - 0x40 is not used
    resultString += (DSt.Message[3] & 0x01)?"Current without voltage\n":"";

    // point offset 50 - Meter alarm (byte 0)
    resultString += (DSt.Message[4] & 0x80)?"Load side voltage detected\n":"";
    resultString += (DSt.Message[4] & 0x40)?"Low battery\n":"";
    resultString += (DSt.Message[4] & 0x20)?"Voltage out of limit\n":"";
    resultString += (DSt.Message[4] & 0x10)?"Metal box cover removal\n":"";
    resultString += (DSt.Message[4] & 0x08)?"Reverse Energy\n":"";
    resultString += (DSt.Message[4] & 0x04)?"Terminal block cover removal\n":"";
    resultString += (DSt.Message[4] & 0x02)?"Internal error\n":"";
    resultString += (DSt.Message[4] & 0x01)?"Out of voltage\n":"";

    ReturnMsg->setResultString(resultString);

    for( int byteoffset = 0; byteoffset < 5; byteoffset++ )
    {
        int pointoffset;
        boost::shared_ptr<CtiPointStatus> point;
        std::unique_ptr<CtiPointDataMsg> p_data;
        string pointResult;

        if( byteoffset == 0 )  pointoffset = 30;
        if( byteoffset == 1 )  pointoffset = 10;
        if( byteoffset == 2 )  pointoffset = 20;
        if( byteoffset == 3 )  pointoffset = 40;
        if( byteoffset == 4 )  pointoffset = 50;

        for( int bitoffset = 0; bitoffset < 8; bitoffset++ )
        {
            //  Don't send the powerfail status again - it's being sent by dev_mct in ResultDecode()
            if( (pointoffset + bitoffset != 10) && (point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( pointoffset + bitoffset, StatusPointType ))) )
            {
                double value = (DSt.Message[byteoffset] >> bitoffset) & 0x01;

                pointResult = getName() + " / " + point->getName() + ": " + ResolveStateName((point)->getStateGroupID(), value);

                p_data.reset(CTIDBG_new CtiPointDataMsg(point->getPointID(), value, NormalQuality, StatusPointType, pointResult));

                ReturnMsg->PointData().push_back(p_data.release());
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigPhaseLossThreshold(const INMESS   &InMessage,
                                                                   const CtiTime   TimeNow,
                                                                   CtiMessageList &vgList,
                                                                   CtiMessageList &retList,
                                                                   OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultString;

    int phase_loss_percent =  DSt->Message[0],
        phase_loss_seconds = (DSt->Message[1]) << 8 | DSt->Message[2];

    resultString  = getName() + " / Phase loss threshold: " + CtiNumStr(phase_loss_percent) + string(" %");
    resultString += " duration: " + CtiNumStr(phase_loss_seconds) + string(" seconds\n");

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::executePutConfigPhaseLossThreshold(CtiRequestMsg     *pReq,
                                                                    CtiCommandParser  &parse,
                                                                    OUTMESS          *&OutMessage,
                                                                    CtiMessageList    &vgList,
                                                                    CtiMessageList    &retList,
                                                                    OutMessageList    &outList)
{
    OutMessage->Sequence = EmetconProtocol::PutConfig_PhaseLossThreshold;
    if( !getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
    {
        return ClientErrors::NoMethod;
    }

    int phaseloss_percent = parse.getiValue("phaseloss_percent_threshold", -1);
    string durationstr    = parse.getsValue("phaseloss_duration_threshold");

    istringstream ss(durationstr);

    int hour, minute, second;
    char sep1, sep2;
    ss >> hour >> sep1 >> minute >> sep2 >> second;

    int phaseloss_seconds = -1;

    if( hour   >= 0   && hour   <  24 &&
        minute >= 0   && minute <  60 &&
        second >= 0   && second <  60 )                         /* check for valid range of time units                  */
    {
        phaseloss_seconds = hour * 3600 + minute * 60 + second;
    }

    if( phaseloss_percent >= 0 && phaseloss_percent <= 100 &&
        phaseloss_seconds >= 0 && phaseloss_seconds <= 0xFFFF ) /* check valid ranges                                   */
    {
        OutMessage->Buffer.BSt.Message[0] = phaseloss_percent;
        OutMessage->Buffer.BSt.Message[1] = (phaseloss_seconds >> 8);
        OutMessage->Buffer.BSt.Message[2] = phaseloss_seconds & 0xff;

        return ClientErrors::None;
    }
    else
    {
        insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                           "Invalid phase loss thresholds for device \"" + getName() + "\" - Acceptable values: phase loss percent range: 0 - 100 ; phase loss max duration: 18:12:15 - ");

        return ExecutionComplete;
    }
}



YukonError_t Mct440_213xBDevice::executePutConfigAlarmMask(CtiRequestMsg     *pReq,
                                                           CtiCommandParser  &parse,
                                                           OUTMESS          *&OutMessage,
                                                           CtiMessageList    &vgList,
                                                           CtiMessageList    &retList,
                                                           OutMessageList    &outList)
{
    OutMessage->Sequence = EmetconProtocol::PutConfig_Options;

    if( !getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
    {
        return ClientErrors::NoMethod;
    }

                                                                /* ----------------- EVENT FLAGS MASK ----------------- */
    const int eventMask = parse.getiValue("alarm_mask", 0);

    OutMessage->Buffer.BSt.Message[0] = (eventMask >> 8) & 0xFF;
    OutMessage->Buffer.BSt.Message[1] = eventMask & 0xFF;

                                                                /* ----------- METER ALARM MASK (OPTIONAL) ------------ */
    int meterAlarmMask;

    if( parse.isKeyValid("alarm_mask_meter") )
    {
        meterAlarmMask = parse.getiValue("alarm_mask_meter", 0);
    }
    else if( CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask) )
    {
        meterAlarmMask = (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask);
    }
    else
    {
        insertReturnMsg(ClientErrors::MissingParameter, OutMessage, retList,
                           "parameter \"alarm_mask_meter\" is not specified for device \"" + getName() + "\"");

        return ExecutionComplete;
    }

    OutMessage->Buffer.BSt.Message[2] = ((meterAlarmMask >> 8) & 0xFF);
    OutMessage->Buffer.BSt.Message[3] = (meterAlarmMask & 0xFF);

                                                                /* ----------- MCT CONFIGURATION (OPTIONAL) ----------- */
    int configuration;

    if( parse.isKeyValid("config_byte") )
    {
        configuration = parse.getiValue("config_byte", 0);
    }
    else if( CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
    {
        configuration = (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);
    }
    else
    {
        insertReturnMsg(ClientErrors::MissingParameter, OutMessage, retList,
                           "parameter \"config_byte\" is not specified for device \"" + getName() + "\"");

        return ExecutionComplete;
    }

    OutMessage->Buffer.BSt.Message[5] = ((configuration>> 8) & 0xFF);
    OutMessage->Buffer.BSt.Message[6] = (configuration & 0xFF);

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigTOU(const INMESS    &InMessage,
                                                    const CtiTime    TimeNow,
                                                    CtiMessageList  &vgList,
                                                    CtiMessageList  &retList,
                                                    OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    string resultString;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
    if( InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection ))
    {
        ReturnMsg->setExpectMore(true);
    }

    int schedulenum = parse.getiValue("tou_schedule");

    if( schedulenum > 0 && schedulenum <= 4 )
    {
        long times[2][5];
        long rates[2][6];
        schedulenum -= (schedulenum - 1) % 2;

                                                                /* get expected functions                               */
        const int function_part1 = (schedulenum < 2) ? FuncRead_TOUSwitchSchedule12Pos      : FuncRead_TOUSwitchSchedule34Pos;
        const int function_part2 = (schedulenum < 2) ? FuncRead_TOUSwitchSchedule12Part2Pos : FuncRead_TOUSwitchSchedule34Part2Pos;

                                                                /* check that function must is part 1 or part 2         */
        if( InMessage.Return.ProtocolInfo.Emetcon.Function != function_part1 &&
            InMessage.Return.ProtocolInfo.Emetcon.Function != function_part2 )
        {
            CTILOG_ERROR(dout, "Invalid InMessage.Return.ProtocolInfo.Emetcon.Function ("<< InMessage.Return.ProtocolInfo.Emetcon.Function <<")");

            return ClientErrors::TypeNotFound;
        }

        const bool first_part = ( InMessage.Return.ProtocolInfo.Emetcon.Function == function_part1 );

        resultString = getName() + " / Received TOU schedule " + CtiNumStr(schedulenum) + " and " + CtiNumStr(schedulenum + 1)
                                 + (( first_part ) ? " switch time 1-5" : " switch time 6-9") + "\n";

        for( int offset = 0; offset < 2; offset++ )
        {
            int rates_raw, byte_offset, switch_count;

            if( first_part )
            {
                if( offset == 0 )
                {
                    rates_raw   = ((InMessage.Buffer.DSt.Message[5] & 0x0f) << 8) | InMessage.Buffer.DSt.Message[6];
                    byte_offset = 0;
                }
                else
                {
                    rates_raw   = ((InMessage.Buffer.DSt.Message[5] & 0xf0) << 4) | InMessage.Buffer.DSt.Message[12];
                    byte_offset = 7;
                }

                rates[offset][0] = (rates_raw) & 0x03;          /* Midnight                                             */
                rates[offset][1] = (rates_raw >> 2)  & 0x03;    /* Switch 1                                             */
                rates[offset][2] = (rates_raw >> 4)  & 0x03;    /* Switch 2                                             */
                rates[offset][3] = (rates_raw >> 6)  & 0x03;    /* Switch 3                                             */
                rates[offset][4] = (rates_raw >> 8)  & 0x03;    /* Switch 4                                             */
                rates[offset][5] = (rates_raw >> 10) & 0x03;    /* Switch 5                                             */

                switch_count = 5;
            }
            else
            {
                if( offset == 0 )
                {
                    rates_raw   = InMessage.Buffer.DSt.Message[4];
                    byte_offset = 0;
                }
                else
                {
                    rates_raw   = InMessage.Buffer.DSt.Message[9];
                    byte_offset = 5;
                }

                rates[offset][0] = (rates_raw) & 0x03;          /* switch 6                                             */
                rates[offset][1] = (rates_raw >> 2) & 0x03;     /* switch 7                                             */
                rates[offset][2] = (rates_raw >> 4) & 0x03;     /* switch 8                                             */
                rates[offset][3] = (rates_raw >> 6) & 0x03;     /* switch 9                                             */

                switch_count = 4;
            }

            for( int switchtime = 0; switchtime < switch_count; switchtime++ )
            {
                times[offset][switchtime] = InMessage.Buffer.DSt.Message[byte_offset + switchtime];
            }
        }

        PaoInfoKeys key[2];

        if( schedulenum < 2 )
        {
            key[0] = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1;
            key[1] = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2;
        }
        else
        {
            key[0] = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3;
            key[1] = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4;
        }

        for( int schedule_nbr = 0; schedule_nbr < 2; schedule_nbr++ )
        {
            string dynDaySchedule;
                                                                /* Retrieve daily schedule dynamic info                 */
            const bool ScheduleValid = CtiDeviceBase::getDynamicInfo(key[schedule_nbr], dynDaySchedule);

            long times_schedule[TOU_SCHEDULE_TIME_NBR],
                 rates_schedule[TOU_SCHEDULE_RATE_NBR];

            if( ScheduleValid )
            {
                                                                /* parse dynamic info to get times and rate values      */
                parseTOUDayScheduleString(dynDaySchedule, times_schedule, rates_schedule);
            }
            else
            {
                                                                /* if the dynamic info doesn't exist, fill with -1      */
                std::fill( times_schedule, times_schedule + COUNT_OF(times_schedule), -1 );
                std::fill( rates_schedule, rates_schedule + COUNT_OF(rates_schedule), -1 );
            }

            if( first_part )                                    /* switch 1 - 5 and midnight                            */
            {
                std::copy(times[schedule_nbr], times[schedule_nbr] + 5, times_schedule);
                std::copy(rates[schedule_nbr], rates[schedule_nbr] + 6, rates_schedule);
            }
            else                                                /* switch 6 - 9                                         */
            {
                std::copy(times[schedule_nbr], times[schedule_nbr] + 4, times_schedule + 5);
                std::copy(rates[schedule_nbr], rates[schedule_nbr] + 4, rates_schedule + 6);
            }

            string daySchedule;
                                                                /* create new strings for switch 1 - 9 + midnight       */
            createTOUDayScheduleString(daySchedule, times_schedule, rates_schedule);

                                                                /* Set daily schedule dynamic info                      */
            CtiDeviceBase::setDynamicInfo(key[schedule_nbr], daySchedule);

            if( !ReturnMsg->ExpectMore() )
            {
                resultString += getName() + " / TOU Schedule " + CtiNumStr(schedulenum + schedule_nbr) + ":\n";

                int current_rate = rates_schedule[0];
                resultString += string("00:00: ") + (char)('A' + current_rate) + "\n";

                int previous_rate, time_offset = 0;
                for( int switchtime = 0; switchtime < TOU_SCHEDULE_TIME_NBR; switchtime++ )
                {
                    time_offset += times_schedule[switchtime] * 300;

                    const int hour   = time_offset / 3600;
                    const int minute = (time_offset / 60) % 60;

                    previous_rate = current_rate;
                    current_rate  = rates_schedule[switchtime+1];

                    if( hour <= 23
                        && current_rate >= 0 && current_rate <= 3
                        && current_rate != previous_rate )
                    {
                        resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                    }
                }

                resultString += "- end of day -\n\n";
            }
        }
    }
    else
    {
        resultString = getName() + " / TOU Status:\n\n";

        unsigned long timestamp = InMessage.Buffer.DSt.Message[6] << 24 |
                                  InMessage.Buffer.DSt.Message[7] << 16 |
                                  InMessage.Buffer.DSt.Message[8] <<  8 |
                                  InMessage.Buffer.DSt.Message[9];

        resultString += "Current time: " + CtiTime(timestamp).asString() + "\n";

        int tz_offset = (char)InMessage.Buffer.DSt.Message[10] * 15;

        resultString += "Time zone offset: " + CtiNumStr((float)tz_offset / 60.0, 1) + " hours ( " + CtiNumStr(tz_offset) + " minutes)\n";

        if( InMessage.Buffer.DSt.Message[3] & 0x80 )
        {
            resultString += "Critical peak active\n";
        }
        if( InMessage.Buffer.DSt.Message[4] & 0x80 )
        {
            resultString += "Holiday active\n";
        }
        if( InMessage.Buffer.DSt.Message[4] & 0x40 )
        {
            resultString += "DST active\n";
        }

        resultString += "Current rate: " + string(1, (char)('A' + (InMessage.Buffer.DSt.Message[3] & 0x7f))) + "\n";

        resultString += "Current schedule: " + CtiNumStr((int)(InMessage.Buffer.DSt.Message[4] & 0x03) + 1) + "\n";


        resultString += "Current switch time: ";
        if( InMessage.Buffer.DSt.Message[5] == 0xff )
        {
            resultString += "not active\n";
        }
        else
        {
            resultString += CtiNumStr((int)InMessage.Buffer.DSt.Message[5]) + "\n";
        }
/*
        resultString += "Default rate: ";
        if( InMessage.Buffer.DSt.Message[2] == 0xff )
        {
            resultString += "No TOU active\n";
        }
        else
        {
            resultString += string(1, (char)('A' + InMessage.Buffer.DSt.Message[2])) + "\n";
        }
*/
        resultString += "Day table: \n";

        const char *(daynames[4]) = {"Sunday", "Monday-Friday", "Saturday", "Holiday"};

        long schedules[4];

        schedules[0] = ((InMessage.Buffer.DSt.Message[1] >> 0) & 0x03) + 1;  // Sunday
        schedules[1] = ((InMessage.Buffer.DSt.Message[1] >> 2) & 0x03) + 1;  // Monday-Friday
        schedules[2] = ((InMessage.Buffer.DSt.Message[0] >> 4) & 0x03) + 1;  // Saturday
        schedules[3] = ((InMessage.Buffer.DSt.Message[0] >> 6) & 0x03) + 1;  // Holiday

        for( int day_nbr = 0; day_nbr < 4; day_nbr++ )
        {
            resultString += "Schedule " + CtiNumStr(schedules[day_nbr]) + " - " + daynames[day_nbr] + "\n";
        }
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigModel(const INMESS   &InMessage,
                                                      const CtiTime   TimeNow,
                                                      CtiMessageList &vgList,
                                                      CtiMessageList &retList,
                                                      OutMessageList &outList)
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    const unsigned revision = DSt.Message[0];
    const unsigned sspec    = DSt.Message[1] << 8 |
                              DSt.Message[2];
    string descriptor;

    descriptor =  getName() + " / Model information:\n";
    descriptor += "Software Specification " + CtiNumStr(sspec) + " rev ";

    //  convert 10 to 1.0, 24 to 2.4
    descriptor += CtiNumStr(((double)revision) / 10.0, 1);
    descriptor += "\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList, InMessage.MessageFlags & MessageFlag_ExpectMore );

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::decodeGetStatusFreeze(const INMESS    &InMessage,
                                                       const CtiTime    TimeNow,
                                                       CtiMessageList  &vgList,
                                                       CtiMessageList  &retList,
                                                       OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultString = getName() + " / Freeze status:\n";

    unsigned long tmpTime = DSt->Message[0] << 24 |
                            DSt->Message[1] << 16 |
                            DSt->Message[2] <<  8 |
                            DSt->Message[3];

    updateFreezeInfo(DSt->Message[4], tmpTime);

    CtiTime lastFreeze(tmpTime);
    if( lastFreeze.isValid() )
    {
        resultString += "Last freeze timestamp: " + lastFreeze.asString() + "\n";
    }
    else
    {
        resultString += "Last freeze timestamp: (no freeze recorded)\n";
    }

    resultString += "Freeze counter: " + CtiNumStr(getCurrentFreezeCounter()) + "\n";
    resultString += "Next freeze expected: freeze ";
    resultString += ((getCurrentFreezeCounter() % 2)?("two"):("one"));
    resultString += "\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigIntervals(const INMESS    &InMessage,
                                                          const CtiTime    TimeNow,
                                                          CtiMessageList  &vgList,
                                                          CtiMessageList  &retList,
                                                          OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultString;

    resultString  = getName() + " / Demand Interval:       " + CtiNumStr(DSt->Message[0]) + string(" minutes\n");
    resultString += getName() + " / Load Profile Interval: " + CtiNumStr(DSt->Message[1]) + string(" minutes\n");

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::executePutStatus(CtiRequestMsg     *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS          *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    populateDlcOutMessage(*OutMessage);

    if( parse.isKeyValid("set_tou_holiday_rate") )
    {
        OutMessage->Sequence = EmetconProtocol::PutStatus_SetTOUHolidayRate;
        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = ClientErrors::None;
        }

    }
    else if( parse.isKeyValid("clear_tou_holiday_rate") )
    {
        OutMessage->Sequence = EmetconProtocol::PutStatus_ClearTOUHolidayRate;
        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = ClientErrors::None;
        }
    }
    else
    {
        nRet = Inherited::executePutStatus(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigAlarmMask(const INMESS    &InMessage,
                                                          const CtiTime    TimeNow,
                                                          CtiMessageList  &vgList,
                                                          CtiMessageList  &retList,
                                                          OutMessageList  &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultString;

    resultString  = getName() + " / Event Flags Mask:\n";
    resultString += string("Power fail ")                   + ((DSt->Message[1] & 0x01) ? "enabled" : "disabled") + "\n\n";

    resultString += getName() + " / Meter Alarm Mask:\n";
    resultString += string("Current without voltage ")      + ((DSt->Message[3] & 0x01) ? "enabled" : "disabled") + "\n";
    resultString += string("Load side voltage detected ")   + ((DSt->Message[4] & 0x80) ? "enabled" : "disabled") + "\n";
    resultString += string("Low battery ")                  + ((DSt->Message[4] & 0x40) ? "enabled" : "disabled") + "\n";
    resultString += string("Voltage out of limits ")        + ((DSt->Message[4] & 0x20) ? "enabled" : "disabled") + "\n";
    resultString += string("Metal box cover removal ")      + ((DSt->Message[4] & 0x10) ? "enabled" : "disabled") + "\n";
    resultString += string("Reverse energy ")               + ((DSt->Message[4] & 0x08) ? "enabled" : "disabled") + "\n";
    resultString += string("Terminal block cover removal ") + ((DSt->Message[4] & 0x04) ? "enabled" : "disabled") + "\n";
    resultString += string("Internal error ")               + ((DSt->Message[4] & 0x02) ? "enabled" : "disabled") + "\n";
    resultString += string("Out of voltage ")               + ((DSt->Message[4] & 0x01) ? "enabled" : "disabled") + "\n\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



void Mct440_213xBDevice::createTOUScheduleConfig(const long     (&daySchedule)[8],
                                                 const long     (&times)[4][9],
                                                 const long     (&rates)[4][10],
                                                 OUTMESS        *&OutMessage,
                                                 OutMessageList  &outList)
{

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 1 ---------- */
    unique_ptr<OUTMESS> TOU_OutMessage( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule1Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule1Len;

    // write the day table
    for( int offset = 0; offset < 8; offset++ )
    {
        int byte      = 1 - (offset / 4);
        int bitoffset = (2 * offset) % 8;

        TOU_OutMessage->Buffer.BSt.Message[byte] |= (daySchedule[offset] & 0x03) << bitoffset;
    }

    // write the durations for schedules 1 and 2 (switch 1 to 5)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 2] = times[0][offset];
        TOU_OutMessage->Buffer.BSt.Message[offset + 9] = times[1][offset];
    }

    // write the rates for schedules 1 and 2 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[7]  = ((rates[1][5] & 0x03) << 6) | // switch rate 5 schedule 2
                                             ((rates[1][4] & 0x03) << 4) | // switch rate 4 schedule 2
                                             ((rates[0][5] & 0x03) << 2) | // switch rate 5 schedule 1
                                             ((rates[0][4] & 0x03));       // switch rate 4 schedule 1

    TOU_OutMessage->Buffer.BSt.Message[8]  = ((rates[0][3] & 0x03) << 6) | // switch rate 3 schedule 1
                                             ((rates[0][2] & 0x03) << 4) | // switch rate 2 schedule 1
                                             ((rates[0][1] & 0x03) << 2) | // switch rate 1 schedule 1
                                             ((rates[0][0] & 0x03));       // midnight rate schedule 1

    TOU_OutMessage->Buffer.BSt.Message[14] = ((rates[1][3] & 0x03) << 6) | // switch rate 3 schedule 2
                                             ((rates[1][2] & 0x03) << 4) | // switch rate 2 schedule 2
                                             ((rates[1][1] & 0x03) << 2) | // switch rate 1 schedule 2
                                             ((rates[1][0] & 0x03));       // midnight rate schedule 2

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 2 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule2Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule2Len;

    // write the durations for schedules 3 and 4 (switch 1 to 5)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[2][offset];
        TOU_OutMessage->Buffer.BSt.Message[offset + 7] = times[3][offset];
    }

    // write the rates for schedule 3 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[5]  = ((rates[2][5] & 0x03) << 2) | // switch rate 5 schedule 3
                                             ((rates[2][4] & 0x03) << 0);  // switch rate 4 schedule 3

    TOU_OutMessage->Buffer.BSt.Message[6]  = ((rates[2][3] & 0x03) << 6) | // switch rate 3 schedule 3
                                             ((rates[2][2] & 0x03) << 4) | // switch rate 2 schedule 3
                                             ((rates[2][1] & 0x03) << 2) | // switch rate 1 schedule 3
                                             ((rates[2][0] & 0x03));       // midnight rate schedule 3

    // write the rates for schedule 4 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[12] = ((rates[3][5] & 0x03) << 2) | // switch rate 5 schedule 4
                                             ((rates[3][4] & 0x03));       // switch rate 4 schedule 4

    TOU_OutMessage->Buffer.BSt.Message[13] = ((rates[3][3] & 0x03) << 6) | // switch rate 3 schedule 4
                                             ((rates[3][2] & 0x03) << 4) | // switch rate 2 schedule 4
                                             ((rates[3][1] & 0x03) << 2) | // switch rate 1 schedule 4
                                             ((rates[3][0] & 0x03));       // midnight rate schedule 4

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 3 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule3Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule3Len;

    // write the durations for schedules 1 and 2 (switch 6 to 9)
    for( int offset = 0; offset < 4; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[0][offset + 5];
        TOU_OutMessage->Buffer.BSt.Message[offset + 5] = times[1][offset + 5];
    }

    // write the rates for schedule 1 (switch 6 to 9)
    TOU_OutMessage->Buffer.BSt.Message[4]  = ((rates[0][9] & 0x03) << 6) | // switch rate 9 schedule 1
                                             ((rates[0][8] & 0x03) << 4) | // switch rate 8 schedule 1
                                             ((rates[0][7] & 0x03) << 2) | // switch rate 7 schedule 1
                                             ((rates[0][6] & 0x03));       // switch rate 6 schedule 1

    // write the rates for schedule 2 (switch 6 to 9)
    TOU_OutMessage->Buffer.BSt.Message[9]  = ((rates[1][9] & 0x03) << 6) | // switch rate 9 schedule 2
                                             ((rates[1][8] & 0x03) << 4) | // switch rate 8 schedule 2
                                             ((rates[1][7] & 0x03) << 2) | // switch rate 7 schedule 2
                                             ((rates[1][6] & 0x03));       // switch rate 6 schedule 2

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 4 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule4Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule4Len;
    TOU_OutMessage->Priority           -= 1; // decrease priority of the last schedule to make sure we process it last

    // write the durations for schedules 3 and 4 (switch 6 to 9)
    for( int offset = 0; offset < 4; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[2][offset + 5];
        TOU_OutMessage->Buffer.BSt.Message[offset + 5] = times[3][offset + 5];
    }

    // write the rates for schedule 4 (switch 6 to 9)
    TOU_OutMessage->Buffer.BSt.Message[4]  = ((rates[2][9] & 0x03) << 6) | // switch rate 9 schedule 3
                                             ((rates[2][8] & 0x03) << 4) | // switch rate 8 schedule 3
                                             ((rates[2][7] & 0x03) << 2) | // switch rate 7 schedule 3
                                             ((rates[2][6] & 0x03));       // switch rate 6 schedule 3

    // write the rates for schedule 4 (switch 6 to 9)
    TOU_OutMessage->Buffer.BSt.Message[9]  = ((rates[3][9] & 0x03) << 6) | // switch rate 9 schedule 4
                                             ((rates[3][8] & 0x03) << 4) | // switch rate 8 schedule 4
                                             ((rates[3][7] & 0x03) << 2) | // switch rate 7 schedule 4
                                             ((rates[3][6] & 0x03));       // switch rate 6 schedule 4

    outList.push_back( TOU_OutMessage.release() );
}



YukonError_t Mct440_213xBDevice::decodeGetValueKWH(const INMESS   &InMessage,
                                                   const CtiTime   TimeNow,
                                                   CtiMessageList &vgList,
                                                   CtiMessageList &retList,
                                                   OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Accumulator Decode for \""<< getName() <<"\"");
    }

    if( InMessage.Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    const unsigned char *p_freeze_counter = 0;

    if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
    {
        string freeze_error_str;

        p_freeze_counter = DSt->Message + 3;

        if( status = checkFreezeLogic(TimeNow, *p_freeze_counter, freeze_error_str) )
        {
            ReturnMsg->setResultString(freeze_error_str);
        }
    }

    if( !status )
    {
        int channels = ChannelCount;

        //  cheaper than looking for parse.getFlags() & CMD_FLAG_GV_KWH
        if( stringContainsIgnoreCase(InMessage.Return.CommandStr, " kwh") )
        {
            channels = 1;
        }

        frozen_point_info pi;
        CtiTime    point_time = TimeNow;

        for( int chan_nbr = 0; chan_nbr < channels; chan_nbr++ )
        {
            int offset = (chan_nbr * 3);

            if( !chan_nbr || getDevicePointOffsetTypeEqual(chan_nbr + 1, PulseAccumulatorPointType) )
            {
                if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::Scan_Accum ||
                    InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_KWH )
                {
                    pi = getAccumulatorData(DSt->Message + offset, 3, 0);
                }
                else if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
                {
                    if( chan_nbr ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                    pi = getAccumulatorData(DSt->Message + offset, 3, p_freeze_counter);

                    if( pi.freeze_bit != getExpectedFreezeParity() )
                    {
                        CTILOG_ERROR(dout, "incoming freeze parity bit ("<< pi.freeze_bit <<") does not match expected freeze bit ("<< getExpectedFreezeParity() <<") on device \""<< getName() <<"\" - not sending data");

                        pi.description  = "Freeze parity does not match (";
                        pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                        pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                        pi.quality = InvalidQuality;
                        pi.value = 0;

                        ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                        status = ClientErrors::InvalidFrozenReadingParity;
                    }
                    else
                    {
                        point_time = getLastFreezeTimestamp(TimeNow);
                    }
                }

                string point_name;
                int    point_offset = -1;

                switch( InMessage.Sequence )
                {
                    case Cti::Protocols::EmetconProtocol::GetValue_KWH:
                         point_offset = chan_nbr + PointOffset_PulseAcc_BaseMRead;
                         break;
                    case Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH:
                         point_offset = chan_nbr + PointOffset_PulseAcc_BaseMReadFrozen;
                         point_name  += "Frozen ";
                         break;
                }

                switch( chan_nbr )
                {
                    case 0: point_name += "kWh";                       break;
                    case 1: point_name += "Reverse kWh";               break;
                    case 2: point_name += "Reactive Inductive Energy"; break;
                }

                //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
                insertPointDataReport(PulseAccumulatorPointType,
                                      point_offset,
                                      ReturnMsg.get(),
                                      pi,
                                      point_name,
                                      point_time,
                                      0.1,
                                      TAG_POINT_MUST_ARCHIVE);

                //  if the quality's invalid, throw the status to abnormal if it's the first channel OR there's a point defined
                if( pi.quality == InvalidQuality && !status && (!chan_nbr || getDevicePointOffsetTypeEqual(chan_nbr + 1, PulseAccumulatorPointType)) )
                {
                    ReturnMsg->setResultString("Invalid data returned for channel " + CtiNumStr(chan_nbr + 1) + "\n" + ReturnMsg->ResultString());
                    status = ClientErrors::InvalidData;
                }
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetStatusDisconnect(const INMESS   &InMessage,
                                                           const CtiTime   TimeNow,
                                                           CtiMessageList &vgList,
                                                           CtiMessageList &retList,
                                                           OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int state = 0;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string stateName;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    if( DSt->Message[0] & 0x01 )
    {
        state     = Mct410Device::StateGroup_DisconnectedConfirmed;
        stateName = "Disconnected confirmed";
    }
    else
    {
        state     = Mct410Device::StateGroup_Connected;
        stateName = "Connected confirmed";
    }

    point_info pi_disconnect;
    pi_disconnect.value   = state;
    pi_disconnect.quality = NormalQuality;

    string pointName = getName() + " / Disconnect Status: " + stateName;

    insertPointDataReport(StatusPointType,
                          1,
                          ReturnMsg.get(),
                          pi_disconnect,
                          pointName,
                          CtiTime(),
                          1.0,
                          TAG_POINT_MUST_ARCHIVE);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigDisconnect(const INMESS   &InMessage,
                                                           const CtiTime   TimeNow,
                                                           CtiMessageList &vgList,
                                                           CtiMessageList &retList,
                                                           OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int state = 0;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string resultStr, stateName;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    if( DSt->Message[0] & 0x01 )
    {
        state     = Mct410Device::StateGroup_DisconnectedConfirmed;
        stateName = "Disconnected confirmed";
    }
    else
    {
        state     = Mct410Device::StateGroup_Connected;
        stateName = "Connected confirmed";
    }

    point_info pi_disconnect;
    pi_disconnect.value   = state;
    pi_disconnect.quality = NormalQuality;

    string pointName = getName() + " / Disconnect Status: " + stateName;

    insertPointDataReport(StatusPointType,
                          1,
                          ReturnMsg.get(),
                          pi_disconnect,
                          pointName,
                          CtiTime(),
                          1.0,
                          TAG_POINT_MUST_ARCHIVE);

    resultStr  = getName() + " / Disconnect Info:\n";

    resultStr += decodeDisconnectStatus(*DSt);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



string Mct440_213xBDevice::decodeDisconnectStatus(const DSTRUCT &DSt) const
{
    string resultStr;

    resultStr += ( DSt.Message[0] & 0x20 ) ? "Disconnect state uncertain\n"   : "";
    resultStr += ( DSt.Message[0] & 0x40 ) ? "Load side voltage detected\n"   : "";
    resultStr += ( DSt.Message[0] & 0x08 ) ? "Control output status closed\n" : "Control output status open\n";
    resultStr += ( DSt.Message[0] & 0x04 ) ? "Disconnect sensor closed\n"     : "Disconnect sensor open\n";

    if( (DSt.Message[0] & 0x20) == 0 )                                 /* check if Disconnect State is Uncertain               */
    {
        resultStr += ( DSt.Message[0] & 0x02 ) ? "Disconnect locked open\n"   : "Disconnect not locked\n";
    }

    return resultStr;
}


unsigned Mct440_213xBDevice::getDisconnectReadDelay() const
{
    return 15;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigAddressing(const INMESS  &InMessage,
                                                           const CtiTime   TimeNow,
                                                           CtiMessageList &vgList,
                                                           CtiMessageList &retList,
                                                           OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    long bronze     =  DSt->Message[0];
    long lead       = (DSt->Message[1] << 8) | DSt->Message[2];
    long collection = (DSt->Message[3] << 8) | DSt->Message[4];
    long spid       =  DSt->Message[5];

    string resultStr = getName() + " / Group Addresses:\n";

    resultStr += "Bronze Meter Address: "     + (CtiNumStr(bronze    ).hex().zpad(2)).toString() + "\n";
    resultStr += "Lead Meter Address: "       + (CtiNumStr(lead      ).hex().zpad(4)).toString() + "\n";
    resultStr += "Meter Collection Address: " + (CtiNumStr(collection).hex().zpad(4)).toString() + "\n";
    resultStr += "Service Provider ID: "      + (CtiNumStr(spid      ).hex().zpad(2)).toString() + "\n\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigTimeAdjustTolerance(const INMESS   &InMessage,
                                                                    const CtiTime   TimeNow,
                                                                    CtiMessageList &vgList,
                                                                    CtiMessageList &retList,
                                                                    OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultStr = getName() + " / Time Adjustment Tolerance: " + CtiNumStr(DSt->Message[0]) + " seconds\n\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    return status;
}



YukonError_t Mct440_213xBDevice::decodeGetConfigOptions(const INMESS   &InMessage,
                                                        const CtiTime   TimeNow,
                                                        CtiMessageList &vgList,
                                                        CtiMessageList &retList,
                                                        OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    string resultStr = getName() + " / MCT Configuration:\n";

    resultStr += (DSt->Message[6] & 0x20) ? "Role enabled\n" : "Role disabled\n";
    resultStr += (DSt->Message[6] & 0x02) ? "LED test enabled\n" : "LED test disabled\n";
    resultStr += (DSt->Message[6] & 0x01) ? "DST enabled\n" : "DST disabled\n";

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

    if( InstallDstPending.is_pending )
    {
        InstallDstPending.is_pending = false;

        string putconfig_cmd = string("putconfig install ") + Mct4xxDevice::PutConfigPart_dst;

        if(InstallDstPending.force)
        {
             putconfig_cmd += " force"; // add force parameter
        }

        // create putconfig install request
        std::unique_ptr<CtiRequestMsg> newReq(CTIDBG_new CtiRequestMsg(getID(),
                                                                     putconfig_cmd,
                                                                     InMessage.Return.UserID,
                                                                     InMessage.Return.GrpMsgID,
                                                                     InMessage.Return.RouteID,
                                                                     MacroOffset::none,  //  PIL will recalculate this;  if we include it, we will potentially be bypassing the initial macro routes
                                                                     0,
                                                                     InMessage.Return.OptionsField,  //  make sure to copy any request ID in OptionsField
                                                                     InMessage.Priority));

        newReq->setConnectionHandle(InMessage.Return.Connection);

        retList.push_back(newReq.release()); // re-add request to do a putconfig
    }

    return status;
}



YukonError_t Mct440_213xBDevice::executePutConfigInstallPhaseLoss(CtiRequestMsg     *pReq,
                                                                  CtiCommandParser  &parse,
                                                                  OUTMESS          *&OutMessage,
                                                                  CtiMessageList    &vgList,
                                                                  CtiMessageList    &retList,
                                                                  OutMessageList    &outList,
                                                                  bool               readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CTILOG_ERROR(dout, "deviceConfig not found");

        return ClientErrors::NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        const int phaseloss_percent = deviceConfig->getLongValueFromKey(MCTStrings::PhaseLossThreshold);
        const int phaseloss_seconds = deviceConfig->getLongValueFromKey(MCTStrings::PhaseLossDuration);

        if( phaseloss_percent < 0 || phaseloss_percent > 100 ||
            phaseloss_seconds < 0 || phaseloss_seconds > 0xFFFF )
        {
            CTILOG_ERROR(dout, "no or bad phaseloss value stored");

            return ClientErrors::NoConfigData;
        }

        if( parse.isKeyValid("force")
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PhaseLossPercent) != phaseloss_percent
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PhaseLossSeconds) != phaseloss_seconds )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_PhaseLossThreshold, OutMessage->Buffer.BSt) )
                {
                    CTILOG_ERROR(dout, "Operation PutConfig_PhaseLossThreshold not found");

                    return ClientErrors::NoConfigData;
                }

                OutMessage->Buffer.BSt.Message[0] = phaseloss_percent;
                OutMessage->Buffer.BSt.Message[1] = (phaseloss_seconds >> 8);
                OutMessage->Buffer.BSt.Message[2] = phaseloss_seconds & 0xff;

                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }
        else
        {
            return ClientErrors::ConfigCurrent;
        }
    }
    else
    {
        if( !getOperation(EmetconProtocol::GetConfig_PhaseLossThreshold, OutMessage->Buffer.BSt) )
        {
            CTILOG_ERROR(dout, "Operation PutConfig_PhaseLossThreshold not found");

            return ClientErrors::NoConfigData;
        }

        OutMessage->Sequence = EmetconProtocol::GetConfig_PhaseLossThreshold;

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::executePutConfigInstallAddressing(CtiRequestMsg     *pReq,
                                                                   CtiCommandParser  &parse,
                                                                   OUTMESS          *&OutMessage,
                                                                   CtiMessageList    &vgList,
                                                                   CtiMessageList    &retList,
                                                                   OutMessageList    &outList,
                                                                   bool               readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CTILOG_ERROR(dout, "deviceConfig not found");

        return ClientErrors::NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        const int bronze     = deviceConfig->getLongValueFromKey(MCTStrings::Bronze);
        const int lead       = deviceConfig->getLongValueFromKey(MCTStrings::Lead);
        const int collection = deviceConfig->getLongValueFromKey(MCTStrings::Collection);
        const int spid       = deviceConfig->getLongValueFromKey(MCTStrings::ServiceProviderID);

        if( bronze      < 0x0 || bronze     > 0xFF   ||         /* check that bronze address is on 8-bit                */
            lead        < 0x0 || lead       > 0x0FFF ||         /* check that lead address is on 12-bit                 */
            collection  < 0x0 || collection > 0x0FFF ||         /* check that collection address is on 12-bit           */
            spid        < 0x0 || spid       > 0xFF )            /* check that service provider id is on 8-bit           */
        {
            CTILOG_ERROR(dout, "no or bad address value stored");

            return ClientErrors::NoConfigData;
        }

        if( parse.isKeyValid("force")
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze)            != bronze
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressLead)              != lead
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressCollection)        != collection
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID) != spid )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_Addressing, OutMessage->Buffer.BSt) )
                {
                    CTILOG_ERROR(dout, "Operation PutConfig_Addressing not found");

                    return ClientErrors::NoConfigData;
                }

                OutMessage->Buffer.BSt.Message[0] = bronze;
                OutMessage->Buffer.BSt.Message[1] = (lead >> 8);
                OutMessage->Buffer.BSt.Message[2] = lead & 0xff;
                OutMessage->Buffer.BSt.Message[3] = (collection >> 8);
                OutMessage->Buffer.BSt.Message[4] = collection & 0xff;
                OutMessage->Buffer.BSt.Message[5] = spid;

                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }
        else
        {
            return ClientErrors::ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_Addressing, OutMessage->Buffer.BSt) )
        {
            CTILOG_ERROR(dout, "Operation GetConfig_Addressing not found");

            return ClientErrors::NoConfigData;
        }

        OutMessage->Sequence = EmetconProtocol::GetConfig_Addressing;

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::executePutConfigInstallDST(CtiRequestMsg     *pReq,
                                                            CtiCommandParser  &parse,
                                                            OUTMESS          *&OutMessage,
                                                            CtiMessageList    &vgList,
                                                            CtiMessageList    &retList,
                                                            OutMessageList    &outList,
                                                            bool               readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CTILOG_ERROR(dout, "deviceConfig not found");

        return ClientErrors::NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        const boost::optional<bool> enable_dst = deviceConfig->findValue<bool>(MCTStrings::EnableDst);

        if( ! enable_dst )
        {
            return ClientErrors::NoConfigData;
        }

        const long dyn_configuration = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);
        const bool dyn_enable_dst    = ((dyn_configuration & 0x1) != 0x0);

        if( parse.isKeyValid("force")
            || *enable_dst != dyn_enable_dst
            || !CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1)
                    && CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2)
                    && CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask)
                    && CtiDeviceBase::hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) ) // Check if all dynamic value is available
                {
                    if( !getOperation(EmetconProtocol::PutConfig_Options, OutMessage->Buffer.BSt) )
                    {
                        CTILOG_ERROR(dout, "Operation EmetconProtocol::PutConfig_Options not found");

                        return ClientErrors::NoConfigData;
                    }

                    long configuration = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);

                    const bool config_enable_dst = ((configuration & 0x1) != 0x0);
                    if( *enable_dst != config_enable_dst )
                    {
                        configuration ^= 0x1; // flip the bit
                    }

                    const long eventFlagsMask1 = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1),
                               eventFlagsMask2 = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2),
                               meterAlarmMask  = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask);

                    OutMessage->Buffer.BSt.Message[0] = (eventFlagsMask1 & 0xFF);
                    OutMessage->Buffer.BSt.Message[1] = (eventFlagsMask2 & 0xFF);

                    OutMessage->Buffer.BSt.Message[2] = ((meterAlarmMask >> 8) & 0xFF);
                    OutMessage->Buffer.BSt.Message[3] = (meterAlarmMask & 0xFF);

                    OutMessage->Buffer.BSt.Message[4] = ((configuration >> 8) & 0xFF);
                    OutMessage->Buffer.BSt.Message[5] = (configuration & 0xFF);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                }
                else // if dynamic data is not available
                {
                    if( !getOperation(EmetconProtocol::GetConfig_Options, OutMessage->Buffer.BSt) )
                    {
                        CTILOG_ERROR(dout, "Operation GetStatus_Internal not found");

                        return ClientErrors::NoConfigData;
                    }

                    OutMessage->Sequence = EmetconProtocol::GetConfig_Options;

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    // set put config install dst pending (will be done once GetConfig_Options)
                    InstallDstPending.is_pending = true;
                    InstallDstPending.force      = parse.isKeyValid("force");
                }
            }
            else
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }
        else
        {
            return ClientErrors::ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_Options, OutMessage->Buffer.BSt) )
        {
            CTILOG_ERROR(dout, "Operation GetStatus_Internal not found");

            return ClientErrors::NoConfigData;
        }

        OutMessage->Sequence = EmetconProtocol::GetConfig_Options;

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::executePutConfigTimeAdjustTolerance(CtiRequestMsg     *pReq,
                                                                     CtiCommandParser  &parse,
                                                                     OUTMESS          *&OutMessage,
                                                                     CtiMessageList    &vgList,
                                                                     CtiMessageList    &retList,
                                                                     OutMessageList    &outList,
                                                                     bool               readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CTILOG_ERROR(dout, "deviceConfig not found");

        return ClientErrors::NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        long timeAdjustTolerance = deviceConfig->getLongValueFromKey(MCTStrings::TimeAdjustTolerance);

        if( timeAdjustTolerance < 0 || timeAdjustTolerance > 0xFF )
        {
            CTILOG_ERROR(dout, "no or bad timeAdjustTolerance value stored");

            return ClientErrors::NoConfigData;
        }

        if( parse.isKeyValid("force") ||
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance) != timeAdjustTolerance )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
                {
                    CTILOG_ERROR(dout, "Operation PutConfig_TimeAdjustTolerance not found");

                    return ClientErrors::NoConfigData;
                }

                //the bstruct IO is set above by getOperation()
                OutMessage->Buffer.BSt.Message[0] = timeAdjustTolerance;
                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }
        else
        {
            return ClientErrors::ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
        {
            CTILOG_ERROR(dout, "Operation GetConfig_TimeAdjustTolerance not found");

            return ClientErrors::NoConfigData;
        }

        OutMessage->Sequence = EmetconProtocol::GetConfig_TimeAdjustTolerance;

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return ClientErrors::None;
}



YukonError_t Mct440_213xBDevice::decodePutConfig(const INMESS   &InMessage,
                                                 const CtiTime   TimeNow,
                                                 CtiMessageList &vgList,
                                                 CtiMessageList &retList,
                                                 OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

                                                                /* ---------------- PUTCONFIG INSTALL ----------------- */
    if( InMessage.Sequence == EmetconProtocol::PutConfig_Install )
    {
        string resultString;

        std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));
        ReturnMsg->setUserMessageId(InMessage.Return.UserID);

        CtiCommandParser parse(InMessage.Return.CommandStr);

        string config_part = parse.getsValue("installvalue");

        Mct440_213xBDevice::ConfigPartsList supported_parts = getPartsList();

        // check if config part is supported by this device
        if( std::find(supported_parts.begin(), supported_parts.end(), config_part) == supported_parts.end() )
        {
            CTILOG_ERROR(dout, "unsupported config part \"" << config_part << "\"");

            return ClientErrors::InvalidRequest;
        }

        // make sure we dont receive a read value
        if( InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Read ||
            InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read )
        {
            CTILOG_ERROR(dout, "Invalid InMessage IO  received for config part \"" << config_part << "\"");

            return ClientErrors::InvalidRequest;
        }

        // note that at the moment only putconfig install will ever have a group message count.
        decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
        if( InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection ))
        {
            ReturnMsg->setExpectMore(true);
        }

        bool do_readback = true;

        // check for exception not to do a read back
        if( config_part == Mct4xxDevice::PutConfigPart_tou &&
            InMessage.Return.ProtocolInfo.Emetcon.Function != FuncWrite_TOUSchedule4Pos )
        {
            do_readback = false; // putconfig install tou read only done for schedule 4
        }

        if( do_readback )
        {
            resultString = getName() + " / Config \"" + config_part + "\" sent\n";

            string getconfig_cmd = string("getconfig install ") + config_part;

            std::unique_ptr<CtiRequestMsg> newReq(CTIDBG_new CtiRequestMsg(getID(),
                                                                         getconfig_cmd,
                                                                         InMessage.Return.UserID,
                                                                         InMessage.Return.GrpMsgID,
                                                                         InMessage.Return.RouteID,
                                                                         MacroOffset::none,  //  PIL will recalculate this;  if we include it, we will potentially be bypassing the initial macro routes
                                                                         0,
                                                                         InMessage.Return.OptionsField,  //  make sure to copy any request ID in OptionsField
                                                                         InMessage.Priority));

            newReq->setConnectionHandle(InMessage.Return.Connection);

            // the master can overwrite the default delay
            const unsigned long delay = gConfigParms.getValueAsULong("PORTER_MCT440_INSTALL_READ_DELAY", DEFAULT_INSTALL_READ_DELAY);

            // set it to execute in the future
            newReq->setMessageTime(CtiTime::now().seconds() + delay);

            resultString += getName() + " / delaying " + CtiNumStr(delay)
                         + " seconds for config \"" + config_part + "\" install read (until "
                         + newReq->getMessageTime().asString() + ")";

            retList.push_back(newReq.release());
        }

        ReturnMsg->setUserMessageId(InMessage.Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler(InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList);
    }

                                                                /* -------------- INHERITED FROM MCT-420 -------------- */
    else
    {
        status = Inherited::decodePutConfig(InMessage,TimeNow,vgList,retList,outList);
    }

    return status;
}


}
}
