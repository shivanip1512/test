#include "precompiled.h"

#include "logger.h"
#include "numstr.h"
#include "dev_mct440_213xb.h"
#include "config_device.h"
#include "date_utility.h"
#include "utility.h"
#include "tbl_ptdispatch.h"
#include "pt_status.h"
#include "ctistring.h"
#include "config_data_mct.h"
#include "portglob.h"
#include "dllyukon.h"

#include <stack>


using namespace std;
using namespace Cti::Devices::Commands;
using namespace Cti::Config;
using Cti::Protocols::EmetconProtocol;


#define TOU_SCHEDULE_NBR                4
#define TOU_SCHEDULE_TIME_NBR          10
#define TOU_SCHEDULE_RATE_NBR          11

#define OUTAGE_NBR_MIN                  1
#define OUTAGE_NBR_MAX                 10

#define DEFAULT_INSTALL_READ_DELAY     20   // TODO set default delay


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
        { 0x005,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_StatusFlags                    } },
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

        // 0x0D2 – Holiday 13 - 18
        { 0x0d2,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday13                 } },
        { 0x0d2,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday14                 } },
        { 0x0d2,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday15                 } },
        { 0x0d2,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday16                 } },
        { 0x0d2,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday17                 } },
        { 0x0d2, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday18                 } },

        // 0x0D3 – Holiday 19 - 24
        { 0x0d3,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday19                 } },
        { 0x0d3,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday20                 } },
        { 0x0d3,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday21                 } },
        { 0x0d3,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday22                 } },
        { 0x0d3,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday23                 } },
        { 0x0d3, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday24                 } },

        // 0x0D4 – Holiday 25 - 28
        { 0x0d4,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday25                 } },
        { 0x0d4,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday26                 } },
        { 0x0d4,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday27                 } },
        { 0x0d4,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday28                 } },

        // 0x19D – Long Load Profile Status
        { 0x19d,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len            } },
        { 0x19d,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len            } },
        { 0x19d,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len            } },
        { 0x19d,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len            } },

        // 0x1AD – TOU Day Schedule
        { 0x1ad,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable                  } },
        { 0x1ad,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate            } },
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
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Options,              EmetconProtocol::IO_Function_Write, 0x01,    4));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Intervals,            EmetconProtocol::IO_Function_Write, 0x03,    2));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Intervals,            EmetconProtocol::IO_Read,           0x1A,    2));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_SetTOUHolidayRate,    EmetconProtocol::IO_Write,          0xA4,    0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_ClearTOUHolidayRate,  EmetconProtocol::IO_Write,          0xA5,    0));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_AlarmMask,            EmetconProtocol::IO_Read,           0x01,    9));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Addressing,           EmetconProtocol::IO_Read,           0x13,    6));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Addressing,           EmetconProtocol::IO_Read,           0x13,    6));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_SetDSTActive,         EmetconProtocol::IO_Write,          0xA6,    0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_SetDSTInactive,       EmetconProtocol::IO_Write,          0xA7,    0));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_TimeZoneOffset,       EmetconProtocol::IO_Read,           0x3F,    1));
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


/*
*********************************************************************************************************
*                                           getPartsList()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
Mct440_213xBDevice::ConfigPartsList Mct440_213xBDevice::getPartsList()
{
    return _config_parts;
}


/*
*********************************************************************************************************
*                                         getReadValueMaps()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
const Mct440_213xBDevice::FunctionReadValueMappings *Mct440_213xBDevice::getReadValueMaps() const
{
    return &_readValueMaps;
}


/*
*********************************************************************************************************
*                                         getExcludedCommands()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
const std::set<UINT> *Mct440_213xBDevice::getExcludedCommands() const
{
    return &_excludedCommands;
}


/*
*********************************************************************************************************
*                                         isCommandExcluded()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
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


/*
*********************************************************************************************************
*                                          getOperation()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
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


/*
*********************************************************************************************************
*                                            isSupported()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
bool Mct440_213xBDevice::isSupported(const Mct410Device::Features feature) const
{
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
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::executeGetValue(CtiRequestMsg     *pReq,
                                        CtiCommandParser  &parse,
                                        OUTMESS           *&OutMessage,
                                        CtiMessageList    &vgList,
                                        CtiMessageList    &retList,
                                        OutMessageList    &outList)
{
    INT  nRet     = NoMethod;
    bool found    = false;
    int  function = -1;


    static const string str_outage          = "outage";
    static const string str_instantlinedata = "instantlinedata";
    static const string str_daily_read      = "daily_read";

                                                                /* ---------- GETVALUE COMMAND NOT SUPPORTED ---------- */
    if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
        (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
        returnErrorMessage(BADPARAM, OutMessage, retList,
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
    else if( parse.isKeyValid(str_instantlinedata) )
    {
        function = EmetconProtocol::GetValue_InstantLineData;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        // adjust message length request according to the number of phase
        OutMessage->Buffer.BSt.Length = getPhaseCount() * 4;
    }

                                                                /* ----------------------- OUTAGE --------------------- */
    else if( parse.isKeyValid(str_outage) )
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
        {
            //  we need to set it to the requested interval
            CtiOutMessage *sspec_om = new CtiOutMessage(*OutMessage);

            if( sspec_om )
            {
                getOperation(EmetconProtocol::GetConfig_Model, sspec_om->Buffer.BSt);

                sspec_om->Sequence = EmetconProtocol::GetConfig_Model;

                sspec_om->MessageFlags |= MessageFlag_ExpectMore;

                outList.push_back(sspec_om);
                sspec_om = 0;
            }
        }

        int outagenum = parse.getiValue(str_outage);

        if( outagenum < OUTAGE_NBR_MIN || outagenum > OUTAGE_NBR_MAX )
        {
            returnErrorMessage(BADPARAM, OutMessage, retList,
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
    else if( parse.isKeyValid(str_daily_read) )
    {
        //  if a request is already in progress and we're not submitting a continuation/retry
        if( InterlockedCompareExchange( &_daily_read_info.request.in_progress, true, false) &&
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

            nRet  = ExecutionComplete;
            returnErrorMessage(ErrorCommandAlreadyInProgress, OutMessage, retList, temp);
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
                date_begin = parseDateValue(parse.getsValue("daily_read_date_begin"));
            }

            if( channel < 1 || channel > 3 )
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid channel for daily read request; must be 1-3 (" + CtiNumStr(channel) + ")");

                nRet = ExecutionComplete;
            }
            else if( date_begin > Yesterday )  //  must begin on or before yesterday
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid date for daily read request; must be before today (" + parse.getsValue("daily_read_date_begin") + ")");

                nRet = ExecutionComplete;
            }
            else if( parse.isKeyValid("daily_read_detail") )
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Bad daily read detail not supported - Acceptable format: getvalue daily read mm/dd/yyyy");

                nRet = ExecutionComplete;
            }
            else if( parse.isKeyValid("daily_read_date_end") ||
                     parse.isKeyValid("daily_reads") )
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Bad multi-day read not supported - Acceptable format: getvalue daily read mm/dd/yyyy");

                nRet = ExecutionComplete;
            }
            else if( channel != 1 )
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid channel for recent daily read request; only valid for channel 1 (" + CtiNumStr(channel)  + ")");

                nRet = ExecutionComplete;
            }
            else if( date_begin < Today - 8 )  //  must be no more than 8 days ago
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
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
                InterlockedExchange(&_daily_read_info.request.in_progress, false);
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
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


/*
*********************************************************************************************************
*                                              sspecValid()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
bool Mct440_213xBDevice::sspecValid(const unsigned sspec, const unsigned rev) const
{
    //FIXME: check for revision possibilities
    return (sspec == Mct440_213xBDevice::Sspec);
}


/*
*********************************************************************************************************
*                                             ModelDecode()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::ModelDecode(INMESS          *InMessage,
                                    CtiTime         &TimeNow,
                                    CtiMessageList  &vgList,
                                    CtiMessageList  &retList,
                                    OutMessageList  &outList)
{
    if( !InMessage )
    {
        return MEMORY;
    }

    INT status = NORMAL;

    switch(InMessage->Sequence)
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

        case EmetconProtocol::GetConfig_Holiday:
            status = decodeGetConfigHoliday(InMessage, TimeNow, vgList, retList, outList);
            break;

        case EmetconProtocol::GetConfig_AlarmMask:
            status = decodeGetConfigAlarmMask(InMessage, TimeNow, vgList, retList, outList);
            break;

        default:
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
            if (status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
    }

    return status;
}


/*
*********************************************************************************************************
*                                   decodeGetValueInstantLineData()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetValueInstantLineData(INMESS          *InMessage,
                                                      CtiTime         &TimeNow,
                                                      CtiMessageList  &vgList,
                                                      CtiMessageList  &retList,
                                                      OutMessageList  &outList)
{
    INT status = NORMAL;
    DSTRUCT *DSt = &InMessage->Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;


    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    const int phase_count = getPhaseCount();

    for( int phase_nbr = 0; phase_nbr < phase_count; phase_nbr++ )
    {
        int pointOffset;

        unsigned char *PhaseData = DSt->Message + (phase_nbr*4);

        point_info PhaseVoltage;

                                                                /* Bits {31:20}    Voltage in units of 0.1V, range 0 to 409.5V  */
        PhaseVoltage.value       = (PhaseData[0] << 4) | (PhaseData[1] >> 4);
        PhaseVoltage.quality     = NormalQuality;
        PhaseVoltage.freeze_bit  = false;

        switch( phase_nbr ) 
        {
            case 0: pointOffset = PointOffset_Analog_LineVoltagePhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LineVoltagePhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LineVoltagePhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg,
                              PhaseVoltage,
                              "Phase Line Voltage",
                              CtiTime(),
                              0.1);

        point_info PhaseCurrent;

                                                                /* Bits {19:8} Current in units of 0.1 V, range 0 to 409.5A     */
        PhaseCurrent.value       = ((PhaseData[1] & 0xf) << 8) | (PhaseData[2]);
        PhaseCurrent.quality     = NormalQuality;
        PhaseCurrent.freeze_bit  = false;

        switch( phase_nbr ) 
        {
            case 0: pointOffset = PointOffset_Analog_LineCurrentPhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LineCurrentPhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LineCurrentPhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg,
                              PhaseCurrent,
                              "Phase Line Current",
                              CtiTime(),
                              0.1);

        point_info PowerFactor;

        double PowerFactorVal = PhaseData[3];                   /* Bits {7:0} Power factor in units of 0.01, range 0.00 to 1.99 */

        if (PowerFactorVal < 200)
        {
            PowerFactor.value       = PowerFactorVal;
            PowerFactor.quality     = NormalQuality;
            PowerFactor.freeze_bit  = false;
        }
        else
        {
            PowerFactor.value       = 0;
            PowerFactor.quality     = ExceedsHighQuality;
            PowerFactor.freeze_bit  = false;

            status = ErrorInvalidData;
        }

        switch( phase_nbr ) 
        {
            case 0: pointOffset = PointOffset_Analog_LinePowFactPhaseA; break;
            case 1: pointOffset = PointOffset_Analog_LinePowFactPhaseB; break;
            case 2: pointOffset = PointOffset_Analog_LinePowFactPhaseC; break;
        }

        insertPointDataReport(AnalogPointType,
                              pointOffset,
                              ReturnMsg,
                              PowerFactor,
                              "Phase Line Power Factor",
                              CtiTime(),
                              0.01);
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                       decodeGetValueTOUkWh()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetValueTOUkWh(INMESS          *InMessage,
                                             CtiTime         &TimeNow,
                                             CtiMessageList  &vgList,
                                             CtiMessageList  &retList,
                                             OutMessageList  &outList)
{
    INT status = NORMAL;
    CtiTime point_time;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info pi, pi_freezecount;

    CtiPointSPtr   pPoint;
    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    unsigned char *freeze_counter = 0;

    if( InMessage->Sequence == EmetconProtocol::GetValue_FrozenTOUkWh ||
        InMessage->Sequence == EmetconProtocol::GetValue_FrozenTOUkWhReverse)
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
        for( int rate_nbr = 0; rate_nbr < 4; rate_nbr++ )
        {
            int offset = (rate_nbr * 3);

            if( InMessage->Sequence == EmetconProtocol::GetValue_TOUkWh ||
                InMessage->Sequence == EmetconProtocol::GetValue_TOUkWhReverse)
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, 0);

                point_time -= point_time.seconds() % 60;
            }
            else if( InMessage->Sequence == EmetconProtocol::GetValue_FrozenTOUkWh ||
                     InMessage->Sequence == EmetconProtocol::GetValue_FrozenTOUkWhReverse)
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, freeze_counter);

                if( pi.freeze_bit != getExpectedFreezeParity() )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming freeze parity bit (" << pi.freeze_bit <<
                                            ") does not match expected freeze bit (" << getExpectedFreezeParity() <<
                                            "/" << getExpectedFreezeCounter() << ") on device \"" << getName() << "\", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    pi.description  = "Freeze parity does not match (";
                    pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                    pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                    pi.quality = InvalidQuality;
                    pi.value = 0;

                    ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                    status = ErrorInvalidFrozenReadingParity;
                }
                else
                {
                    point_time  = getLastFreezeTimestamp(TimeNow);
                    point_time -= point_time.seconds() % 60;
                }
            }

            string point_name;
            int    point_offset = -1;

            switch (InMessage->Sequence)
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
                                  ReturnMsg,
                                  pi,
                                  point_name,
                                  point_time,
                                  0.1,
                                  TAG_POINT_MUST_ARCHIVE);

            //  if the quality's invalid, throw the status to abnormal if there's a point defined
            if( pi.quality == InvalidQuality && !status && getDevicePointOffsetTypeEqual(point_offset, PulseAccumulatorPointType) )
            {
                ReturnMsg->setResultString("Invalid data returned\n" + ReturnMsg->ResultString());
                status = ErrorInvalidData;
            }
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                       executePutConfigTOU()
*
* Description : Execute a putconfig install time of usage command: putconfig install tou [force] [verify]
*               Command will install up to 10 rates and 11 schedule time.
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     : (1) Rate0 correspond to the midnight associated rate
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigTOU(CtiRequestMsg     *pReq,
                                            CtiCommandParser  &parse,
                                            OUTMESS          *&OutMessage,
                                            CtiMessageList    &vgList,
                                            CtiMessageList    &retList,
                                            OutMessageList    &outList,
                                            bool               readsOnly)
{
    int nRet = NORMAL;
    long value, tempTime;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig is null **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
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
        long defaultTOURate;
        long dayTable;

        // Unfortunatelly the arrays have a 0 offset, while the schedules times/rates are referenced with a 1 offset
        // Also note that rate "0" is the midnight rate.
        string mondayScheduleStr    = deviceConfig->getValueFromKey(MCTStrings::MondaySchedule);
        string tuesdayScheduleStr   = deviceConfig->getValueFromKey(MCTStrings::TuesdaySchedule);
        string wednesdayScheduleStr = deviceConfig->getValueFromKey(MCTStrings::WednesdaySchedule);
        string thursdayScheduleStr  = deviceConfig->getValueFromKey(MCTStrings::ThursdaySchedule);
        string fridayScheduleStr    = deviceConfig->getValueFromKey(MCTStrings::FridaySchedule);
        string saturdayScheduleStr  = deviceConfig->getValueFromKey(MCTStrings::SaturdaySchedule);
        string sundayScheduleStr    = deviceConfig->getValueFromKey(MCTStrings::SundaySchedule);
        string holidayScheduleStr   = deviceConfig->getValueFromKey(MCTStrings::HolidaySchedule);

        long mondaySchedule    = resolveScheduleName(mondayScheduleStr);
        long tuesdaySchedule   = resolveScheduleName(tuesdayScheduleStr);
        long wednesdaySchedule = resolveScheduleName(wednesdayScheduleStr);
        long thursdaySchedule  = resolveScheduleName(thursdayScheduleStr);
        long fridaySchedule    = resolveScheduleName(fridayScheduleStr);
        long saturdaySchedule  = resolveScheduleName(saturdayScheduleStr);
        long sundaySchedule    = resolveScheduleName(sundayScheduleStr);
        long holidaySchedule   = resolveScheduleName(holidayScheduleStr);

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
        timeStringValues[0][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time10);

        timeStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time1);
        timeStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time2);
        timeStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time3);
        timeStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time4);
        timeStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time5);
        timeStringValues[1][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time6);
        timeStringValues[1][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time7);
        timeStringValues[1][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time8);
        timeStringValues[1][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time9);
        timeStringValues[1][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time10);

        timeStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time1);
        timeStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time2);
        timeStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time3);
        timeStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time4);
        timeStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time5);
        timeStringValues[2][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time6);
        timeStringValues[2][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time7);
        timeStringValues[2][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time8);
        timeStringValues[2][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time9);
        timeStringValues[2][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time10);

        timeStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time1);
        timeStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time2);
        timeStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time3);
        timeStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time4);
        timeStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time5);
        timeStringValues[3][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time6);
        timeStringValues[3][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time7);
        timeStringValues[3][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time8);
        timeStringValues[3][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time9);
        timeStringValues[3][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time10);

        rateStringValues[0][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate1);
        rateStringValues[0][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate2);
        rateStringValues[0][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate3);
        rateStringValues[0][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate4);
        rateStringValues[0][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate5);
        rateStringValues[0][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate6);
        rateStringValues[0][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate7);
        rateStringValues[0][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate8);
        rateStringValues[0][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate9);
        rateStringValues[0][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate10);
        rateStringValues[0][10] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate0);

        rateStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate1);
        rateStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate2);
        rateStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate3);
        rateStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate4);
        rateStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate5);
        rateStringValues[1][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate6);
        rateStringValues[1][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate7);
        rateStringValues[1][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate8);
        rateStringValues[1][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate9);
        rateStringValues[1][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate10);
        rateStringValues[1][10] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate0);

        rateStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate1);
        rateStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate2);
        rateStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate3);
        rateStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate4);
        rateStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate5);
        rateStringValues[2][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate6);
        rateStringValues[2][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate7);
        rateStringValues[2][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate8);
        rateStringValues[2][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate9);
        rateStringValues[2][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate10);
        rateStringValues[2][10] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate0);

        rateStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate1);
        rateStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate2);
        rateStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate3);
        rateStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate4);
        rateStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate5);
        rateStringValues[3][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate6);
        rateStringValues[3][6] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate7);
        rateStringValues[3][7] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate8);
        rateStringValues[3][8] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate9);
        rateStringValues[3][9] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate10);
        rateStringValues[3][10] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate0);

        string defaultTOURateString = deviceConfig->getValueFromKey(MCTStrings::DefaultTOURate);

        for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
        {
            for( int rate = 0; rate < TOU_SCHEDULE_RATE_NBR; rate++ )
            {
                if( rateStringValues[schedule][rate].empty() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
            }
        }

        for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
        {
            for( int switchtime = 0; switchtime < TOU_SCHEDULE_TIME_NBR; switchtime++ )
            {
                if( timeStringValues[schedule][switchtime].length() < 4 ) //A time needs at least 4 digits X:XX
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - bad time string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
            }
        }

        if( nRet != NoConfigData )
        {
            //Do conversions from strings to longs here.
            for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
            {
                for( int rate = 0; rate < TOU_SCHEDULE_RATE_NBR; rate++ )
                {
                    rates[schedule][rate] = rateStringValues[schedule][rate][0] - 'A';
                    if( rates[schedule][rate] < 0 || rates[schedule][rate] > 3 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
            }
            for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
            {
                for( int switchtime = 0; switchtime < TOU_SCHEDULE_TIME_NBR; switchtime++ )
                {
                    // Im going to remove the :, get the remaining value, and do simple math on it. I think this
                    // results in less error checking needed.
                    timeStringValues[schedule][switchtime].erase(timeStringValues[schedule][switchtime].find(':'), 1);
                    tempTime                    = strtol(timeStringValues[schedule][switchtime].c_str(),NULL,10);
                    times[schedule][switchtime] = ((tempTime/100) * 60) + (tempTime%100);
                }
            }
            // Time is currently the actual minutes, we need the difference. Also the MCT has 5 minute resolution.
            for( int schedule = 0; schedule < TOU_SCHEDULE_NBR; schedule++ )
            {
                for( int switchtime = (TOU_SCHEDULE_TIME_NBR-1); switchtime > 0; switchtime-- )
                {
                    times[schedule][switchtime] = times[schedule][switchtime]-times[schedule][switchtime-1];
                    times[schedule][switchtime] = times[schedule][switchtime]/5;
                    if( times[schedule][switchtime] < 0 || times[schedule][switchtime] > 255 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - time sequencing **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
            }
            if( !defaultTOURateString.empty() )
            {
                defaultTOURate = defaultTOURateString[0] - 'A';
                if( defaultTOURate < 0 || defaultTOURate > 3 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - bad default rate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no default rate stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
        }

        if( nRet == NoConfigData
            || mondaySchedule    == std::numeric_limits<long>::min()
            || tuesdaySchedule   == std::numeric_limits<long>::min()
            || fridaySchedule    == std::numeric_limits<long>::min()
            || saturdaySchedule  == std::numeric_limits<long>::min()
            || sundaySchedule    == std::numeric_limits<long>::min()
            || holidaySchedule   == std::numeric_limits<long>::min()
            || wednesdaySchedule == std::numeric_limits<long>::min()
            || thursdaySchedule  == std::numeric_limits<long>::min() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
        else
        {
            dayTable = holidaySchedule    << 14;
            dayTable |= saturdaySchedule  << 12;
            dayTable |= fridaySchedule    << 10;
            dayTable |= thursdaySchedule  << 8;
            dayTable |= wednesdaySchedule << 6;
            dayTable |= tuesdaySchedule   << 4;
            dayTable |= mondaySchedule    << 2;
            dayTable |= sundaySchedule;

            createTOUDayScheduleString(daySchedule1, times[0], rates[0]);
            createTOUDayScheduleString(daySchedule2, times[1], rates[1]);
            createTOUDayScheduleString(daySchedule3, times[2], rates[2]);
            createTOUDayScheduleString(daySchedule4, times[3], rates[3]);

            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dynDaySchedule1);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dynDaySchedule2);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dynDaySchedule3);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dynDaySchedule4);

            if (parse.isKeyValid("force")
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable) != dayTable
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate) != defaultTOURate
                || dynDaySchedule1 != daySchedule1
                || dynDaySchedule2 != daySchedule2
                || dynDaySchedule3 != daySchedule3
                || dynDaySchedule4 != daySchedule4)
            {
                if( !parse.isKeyValid("verify") )
                {
                    long daySchedule[8];

                    for (int i = 0; i < 8; i++)
                    {
                        daySchedule[i] = (dayTable >> (i*2)) & 0x03;
                    }

                    createTOUScheduleConfig(daySchedule,
                                            times,
                                            rates,
                                            defaultTOURate,
                                            OutMessage,
                                            outList);
                }
                else
                {
                    nRet = ConfigNotCurrent;
                }
            }
            else
            {
                nRet = ConfigCurrent;
            }
        }
    }
    else // getconfig install
    {
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
        OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return nRet;
}


/*
*********************************************************************************************************
*                                          executeGetConfig()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::executeGetConfig(CtiRequestMsg     *pReq,
                                         CtiCommandParser  &parse,
                                         OUTMESS          *&OutMessage,
                                         CtiMessageList    &vgList,
                                         CtiMessageList    &retList,
                                         OutMessageList    &outList )
{
    INT nRet = NoMethod;


    // Load all the other stuff that is needed
    OutMessage->DeviceID        = getID();
    OutMessage->TargetID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();
    OutMessage->TimeOut         = 2;
    OutMessage->Retry           = 2;
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

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = NoError;
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = NoError;
            }
            else
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "invalid schedule number " + CtiNumStr(schedulenum));

                nRet = ExecutionComplete;
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;

            nRet = NoError;
        }
    }

                                                                /* --------------- PHASE LOSS THRESHOLD --------------- */
    else if( parse.isKeyValid("phaseloss_threshold") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_PhaseLossThreshold;

        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = NoError;
        }
    }

                                                                /* --------------------- HOLIDAYS --------------------- */
    else if( parse.isKeyValid("holiday") )
    {
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Read;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_Holiday;

        OutMessage->Buffer.BSt.Function = Memory_Holiday1_6Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday1_6Len;
        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday7_12Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday7_12Len;
        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday13_18Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday13_18Len;
        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday19_24Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday19_24Len;
        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday25_28Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday25_28Len;
        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        delete OutMessage;  //  we didn't use it, we made our own
        OutMessage = 0;

        nRet = NoError;
    }

                                                                /* -------------------- ALARM MASK -------------------- */
    else if( parse.isKeyValid("alarm_mask") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_AlarmMask;

        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = NoError;
        }
    }

                                                                /* -------------- INHERITED FROM MCT-420 -------------- */
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


/*
*********************************************************************************************************
*                                       resolveScheduleName()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
long Mct440_213xBDevice::resolveScheduleName(const string & scheduleName)
{
    CtiString schedule = scheduleName;
    schedule.toLower();

    if (schedule == "schedule 1")
    {
        return 0;
    }
    else if( schedule == "schedule 2" )
    {
        return 1;
    }
    else if( schedule == "schedule 3" )
    {
        return 2;
    }
    else //schedule 4
    {
        return 3;
    }
}


/*
*********************************************************************************************************
*                                     createTOUDayScheduleString()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
void Mct440_213xBDevice::createTOUDayScheduleString(string &schedule, long (&times)[10], long (&rates)[11])
{
    for( int time_nbr=0; time_nbr<10; time_nbr++ )
    {
        schedule += CtiNumStr(times[time_nbr]);
        schedule += ", ";
    }

    for( int rate_nbr=0; rate_nbr<10; rate_nbr++ )
    {
        schedule += CtiNumStr(rates[rate_nbr]);
        schedule += ", ";
    }

    schedule += CtiNumStr(rates[10]);
}


/*
*********************************************************************************************************
*                                    parseTOUDayScheduleString()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
void Mct440_213xBDevice::parseTOUDayScheduleString(string &schedule, long (&times)[10], long (&rates)[11])
{
    istringstream ss;
    char sep[2];                                                /* expecting ", "                                       */

    ss.str(schedule);

    for( int time_nbr=0; time_nbr<10; time_nbr++ )
    {
        ss >> times[time_nbr] >> sep;
    }

    for( int rate_nbr=0; rate_nbr<10; rate_nbr++ )
    {
        ss >> rates[rate_nbr] >> sep;
    }

    ss >> rates[10];
}


/*
*********************************************************************************************************
*                                   decodeGetValueDailyReadRecent()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetValueDailyReadRecent(INMESS          *InMessage,
                                                      CtiTime         &TimeNow,
                                                      CtiMessageList  &vgList,
                                                      CtiMessageList  &retList,
                                                      OutMessageList  &outList)
{
    INT status = NORMAL;

    string resultString;
    bool expectMore = false;

    const DSTRUCT * const DSt  = &InMessage->Buffer.DSt;

    CtiReturnMsg  *ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

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

        status = ErrorInvalidChannel;
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

        status = ErrorInvalidTimestamp;
    }
    else
    {
        point_info pi_outage_count = getData(DSt->Message + 6, 2, ValueType_OutageCount);

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_Accumulator_Powerfail,
                              ReturnMsg,
                              pi_outage_count,
                              "Blink Counter",
                              CtiTime(_daily_read_info.request.begin + 1));  //  add on 24 hours - end of day

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_RecentkWhForward,
                              ReturnMsg,
                              pi_forward,
                              "forward active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_RecentkWhReverse,
                              ReturnMsg,
                              pi_reverse,
                              "reverse active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        // echo forward active kWh and reverse active kWh to pulse acc 1 and 2

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_BaseMRead,
                              ReturnMsg,
                              pi_forward,
                              "forward active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        insertPointDataReport(PulseAccumulatorPointType,
                              PointOffset_PulseAcc_BaseMRead + 1,
                              ReturnMsg,
                              pi_reverse,
                              "reverse active kWh",
                              CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                              TAG_POINT_MUST_ARCHIVE);

        InterlockedExchange(&_daily_read_info.request.in_progress, false);
    }

    //  this is gross
    if( !ReturnMsg->ResultString().empty() )
    {
        resultString = ReturnMsg->ResultString() + "\n" + resultString;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );

    return status;
}


/*
*********************************************************************************************************
*                                       executePutConfig()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::executePutConfig(CtiRequestMsg     *pReq,
                                         CtiCommandParser  &parse,
                                         OUTMESS          *&OutMessage,
                                         CtiMessageList    &vgList,
                                         CtiMessageList    &retList,
                                         OutMessageList    &outList)
{
    INT nRet = NoMethod;

    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    if( parse.isKeyValid("install") )
    {
        nRet = Mct4xxDevice::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("holiday_offset") )
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


/*
*********************************************************************************************************
*                                       executePutConfigHolidays()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigHoliday(CtiRequestMsg     *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList)
{
    OutMessage->Sequence = EmetconProtocol::PutConfig_Holiday;
    if( !getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
    {
        return NoMethod;
    }

    unsigned long holidays[7];
    int holiday_count  = 0;
    int holiday_offset = parse.getiValue("holiday_offset");

                                                            /*  grab up to 7 potential dates                        */
    for( int date_nbr = 0; date_nbr < 7 && parse.isKeyValid("holiday_date" + CtiNumStr(date_nbr)); date_nbr++ )
    {
        int month, day, year;
        char sep1, sep2;

        istringstream ss;
        ss.str(parse.getsValue("holiday_date" + CtiNumStr(date_nbr)));

        ss >> month >> sep1 >> day >> sep2 >> year;

        CtiDate holiday_date(day, month, year);


        if( holiday_date.isValid() && holiday_date > CtiDate::now() )
        {
                                                            /* get the number of days since Jan 1 2010              */
            holidays[holiday_count++] = (holiday_date.daysFrom1970() - holidayBaseDate.daysFrom1970());
        }
        else
        {
            holiday_count = 0;                              /* If the data is less then 2010, break                  */
            break;
        }
    }


    /*
     * check to make sure that holiday_offset is:
     * 1, 8, 15, 22
     */

    if( (holiday_offset % 7) == 1 &&
         holiday_offset      >= 1 &&
         holiday_offset      <= 22)
    {
        if( holiday_count >  0 ||
            holiday_count <= 7)
        {
            //  change to 0-based offset;  it just makes things easier
            holiday_offset--;

            OutMessage->Buffer.BSt.Length     = (holiday_count  * 2) + 1;
            OutMessage->Buffer.BSt.Message[0] =  holiday_offset;

            for( int holiday_nbr = 0; holiday_nbr < holiday_count; holiday_nbr++ )
            {
                OutMessage->Buffer.BSt.Message[holiday_nbr*2+1] = holidays[holiday_nbr] >> 8;
                OutMessage->Buffer.BSt.Message[holiday_nbr*2+2] = holidays[holiday_nbr]  & 0xff;
            }

            return NoError;
        }
        else
        {
            returnErrorMessage(BADPARAM, OutMessage, retList,
                               "Specified dates are invalid");

            return ExecutionComplete;
        }
    }
    else
    {
        returnErrorMessage(BADPARAM, OutMessage, retList,
                           "Invalid holiday offset specified, valid offsets are: 1, 8, 15, 22");

        return ExecutionComplete;
    }
}


/*
*********************************************************************************************************
*                                       executePutConfigTOUDays()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigTOUDays(CtiRequestMsg     *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList)
{
    bool  found = false;
    INT   nRet  = NoMethod;


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
        long default_rate, day_schedules[8];

        string schedule_name, change_name, daytable(parse.getsValue("tou_days"));

        switch( parse.getsValue("tou_default").data()[0] )
        {
            case 'a':   default_rate =  0;  break;
            case 'b':   default_rate =  1;  break;
            case 'c':   default_rate =  2;  break;
            case 'd':   default_rate =  3;  break;
            default:    default_rate = -1;  break;
        }

        if( default_rate < 0 )
        {
            returnErrorMessage(BADPARAM, OutMessage, retList,
                               "TOU default rate \"" + parse.getsValue("tou_default") + "\" specified is invalid for device \"" + getName() + "\"");

            return ExecutionComplete;
        }
        else
        {
            if( daytable.length() < 8 || daytable.find_first_not_of("1234") != string::npos )
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "day table \"" + daytable + "\" specified is invalid for device \"" + getName() + "\"");

                return ExecutionComplete;
            }
            else
            {
                for( int day = 0; day < 8; day++ )
                {
                    day_schedules[day] = atoi(daytable.substr(day, 1).c_str()) - 1;
                }

                int schedulenum = 0;

                schedule_name.assign("tou_schedule_");
                schedule_name.append(CtiNumStr(schedulenum).zpad(2));

                while(parse.isKeyValid(schedule_name.c_str()))
                {
                    int schedule_number = parse.getiValue(schedule_name.c_str());

                    if( schedule_number > 0 && schedule_number <= TOU_SCHEDULE_NBR )
                    {
                        int changenum = 0;

                        change_name.assign(schedule_name);
                        change_name.append("_");
                        change_name.append(CtiNumStr(changenum).zpad(2));

                        while(parse.isKeyValid(change_name.c_str()))
                        {
                            string ratechangestr = parse.getsValue(change_name.c_str());

                            istringstream ss;
                            ss.str(ratechangestr);

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
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - schedule \"" << schedule_number << "\" has invalid rate change \"" << ratechangestr << "\"for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

                            changenum++;
                            change_name.assign(schedule_name);
                            change_name.append("_");
                            change_name.append(CtiNumStr(changenum).zpad(2));
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - schedule \"" << schedule_number << "\" specified is out of range for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
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
                    for( int rate_nbr = 0; rate_nbr < TOU_SCHEDULE_RATE_NBR; rate_nbr++ )
                    {
                        if( rate_nbr < TOU_SCHEDULE_TIME_NBR )
                        {
                            times[schedule_nbr][rate_nbr] = 255;
                        }

                        rates[schedule_nbr][rate_nbr] = default_rate;
                    }
                }

                int current_schedule = -1;
                int offset           =  0;
                int time_offset      =  0;

                std::set< ratechange_t >::iterator itr;

                for( itr = ratechanges.begin(); itr != ratechanges.end(); itr++ )
                {
                    ratechange_t &rc = *itr;

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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        continue;
                    }

                    if( offset == 0 && rc.time == 0 )
                    {
                        //  this is a special case, because we can't access
                        //    durations[rc.schedule][offset-1] yet - offset isn't 1 yet

                        rates[rc.schedule][0] = rc.rate;
                    }
                    else
                    {
                        if( offset == 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - first rate change time for schedule (" << rc.schedule <<
                                                    ") is not midnight, assuming default rate (" << default_rate <<
                                                    ") for midnight until (" << rc.time << ") for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            //  rates[rc.schedule][0] was already initialized to default_rate, so just move along
                            offset++;
                        }

                        times[rc.schedule][offset - 1] = (rc.time - time_offset) / 300;
                        rates[rc.schedule][offset]     = rc.rate;

                        if( (offset + 1) <= (TOU_SCHEDULE_RATE_NBR-1) )
                        {
                            //  this is to work around the 255 * 5 min limitation for switches - this way it doesn't
                            //    jump back to the default rate if only a midnight rate is specified
                            rates[rc.schedule][offset + 1] = rc.rate;
                        }

                        time_offset = rc.time - (rc.time % 300);  //  make sure we don't miss the 5-minute marks
                    }
                }

                OutMessage->Sequence = EmetconProtocol::PutConfig_TOU;

                createTOUScheduleConfig(day_schedules,
                                        times,
                                        rates,
                                        default_rate,
                                        OutMessage,
                                        outList);

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                found = true;
            }
        }
    }

    if( found )
    {
        nRet = NoError;
    }

    return nRet;
}


/*
*********************************************************************************************************
*                                       describeStatusAndEvents()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
string Mct440_213xBDevice::describeStatusAndEvents(unsigned char *buf)
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


/*
*********************************************************************************************************
*                                       decodeGetStatusInternal()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT &DSt = InMessage->Buffer.DSt;

    string resultString;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

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
        CtiPointDataMsg *pData;
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

                if( pData = CTIDBG_new CtiPointDataMsg(point->getPointID(), value, NormalQuality, StatusPointType, pointResult) )
                {
                    ReturnMsg->PointData().push_back(pData);
                }
            }
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                  decodeGetConfigPhaseLossThreshold()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetConfigPhaseLossThreshold(INMESS         *InMessage,
                                                          CtiTime        &TimeNow,
                                                          CtiMessageList &vgList,
                                                          CtiMessageList &retList,
                                                          OutMessageList &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;

    int phase_loss_percent =  DSt->Message[0],
        phase_loss_seconds = (DSt->Message[1]) << 8 | DSt->Message[2];

    resultString  = getName() + " / Phase loss percent threshold: " + CtiNumStr(phase_loss_percent) + string(" %\n");
    resultString += getName() + " / Phase loss seconds threshold: " + CtiNumStr(phase_loss_seconds) + string(" seconds\n");

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                  executePutConfigPhaseLossThreshold()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigPhaseLossThreshold(CtiRequestMsg     *pReq,
                                                           CtiCommandParser  &parse,
                                                           OUTMESS          *&OutMessage,
                                                           CtiMessageList    &vgList,
                                                           CtiMessageList    &retList,
                                                           OutMessageList    &outList)
{
    OutMessage->Sequence = EmetconProtocol::PutConfig_PhaseLossThreshold;
    if( !getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
    {
        return NoMethod;
    }

    int phaseloss_percent = parse.getiValue("phaseloss_percent_threshold", -1);
    string durationstr    = parse.getsValue("phaseloss_duration_threshold");

    istringstream ss;
    ss.str(durationstr);

    int hour, minute, second;
    char sep1, sep2;
    ss >> hour >> sep1 >> minute >> sep2 >> second;

    int phaseloss_seconds = -1;

                                                            /* check for valid range of time units                  */
    if( hour   >= 0   && hour   <  24 &&
        minute >= 0   && minute <  60 &&
        second >= 0   && second <  60)
    {
        phaseloss_seconds = hour * 3600 + minute * 60 + second;
    }

                                                            /* check valid ranges                                   */
    if( phaseloss_percent >= 0 && phaseloss_percent <= 100 &&
        phaseloss_seconds >= 0 && phaseloss_seconds <= 0xFFFF)
    {
        OutMessage->Buffer.BSt.Message[0] = phaseloss_percent;
        OutMessage->Buffer.BSt.Message[1] = (phaseloss_seconds >> 8);
        OutMessage->Buffer.BSt.Message[2] = phaseloss_seconds & 0xff;

        return NoError;
    }
    else
    {
        returnErrorMessage(BADPARAM, OutMessage, retList,
                           "Invalid phase loss thresholds for device \"" + getName() + "\" - Acceptable values: phase loss percent range: 0 - 100 ; phase loss max duration: 18:12:15 - ");

        return ExecutionComplete;
    }
}


/*
*********************************************************************************************************
*                                      executePutConfigAlarmMask()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigAlarmMask(CtiRequestMsg     *pReq,
                                                  CtiCommandParser  &parse,
                                                  OUTMESS          *&OutMessage,
                                                  CtiMessageList    &vgList,
                                                  CtiMessageList    &retList,
                                                  OutMessageList    &outList)
{
    if( parse.isKeyValid("config_byte") )
    {
        returnErrorMessage(BADPARAM, OutMessage, retList,
                           "Device \"" + getName() + "\" does not support the \"configbyte\" parameter");

        return ExecutionComplete;
    }

    const int alarmMask = parse.getiValue("alarm_mask", 0);
    OutMessage->Buffer.BSt.Message[0] = (alarmMask & 0xFF);
    OutMessage->Buffer.BSt.Message[1] = ((alarmMask >> 8) & 0xFF);

    if( parse.isKeyValid("alarm_mask_meter") )
    {
        const int meterAlarmMask = parse.getiValue("alarm_mask_meter", 0);
        OutMessage->Buffer.BSt.Message[2] = (meterAlarmMask & 0xFF);
        OutMessage->Buffer.BSt.Message[3] = ((meterAlarmMask >> 8) & 0xFF);
    }

    if( !getOperation(EmetconProtocol::PutConfig_Options, OutMessage->Buffer.BSt) )
    {
        return NoMethod;
    }

    OutMessage->Sequence = EmetconProtocol::PutConfig_AlarmMask;

    return NoError;
}


/*
*********************************************************************************************************
*                                       decodeGetConfigTOU()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetConfigTOU(INMESS          *InMessage,
                                           CtiTime         &TimeNow,
                                           CtiMessageList  &vgList,
                                           CtiMessageList  &retList,
                                           OutMessageList  &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;
    unsigned long timestamp;
    CtiTime tmpTime;

    int schedulenum = parse.getiValue("tou_schedule");
    
    if( schedulenum > 0 && schedulenum <= 4 )
    {
        long timeArray[2][5];
        long rateArray[2][6];
        schedulenum -= (schedulenum - 1) % 2;

                                                                /* get expected functions                               */
        const int function_switch1_5  = (schedulenum < 2) ? FuncRead_TOUSwitchSchedule12Pos      : FuncRead_TOUSwitchSchedule34Pos;
        const int function_switch6_10 = (schedulenum < 2) ? FuncRead_TOUSwitchSchedule12Part2Pos : FuncRead_TOUSwitchSchedule34Part2Pos;

                                                                /* check that function must is part 1 or part 2         */
        if( InMessage->Return.ProtocolInfo.Emetcon.Function != function_switch1_5 &&
            InMessage->Return.ProtocolInfo.Emetcon.Function != function_switch6_10 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Invalid InMessage.Return.ProtocolInfo.Emetcon.Function " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return TYNF;
        }

        const bool switch1_5 = ( InMessage->Return.ProtocolInfo.Emetcon.Function == function_switch1_5 );

        for( int offset = 0; offset < 2; offset++ )
        {
            int rates, current_rate, previous_rate, byte_offset, time_offset;

            resultString += getName() + " / TOU Schedule " + CtiNumStr(schedulenum + offset) + ":\n";

            if( offset == 0 )
            {
                rates = ((InMessage->Buffer.DSt.Message[5] & 0x0f) << 8) | InMessage->Buffer.DSt.Message[6];
                byte_offset = 0;
            }
            else
            {
                rates = ((InMessage->Buffer.DSt.Message[5] & 0xf0) << 4) | InMessage->Buffer.DSt.Message[12];
                byte_offset = 7;
            }

            if( switch1_5 )
            {
                rateArray[offset][0] = (rates >> 2)  & 0x03;    /* Switch 1                                             */
                rateArray[offset][1] = (rates >> 4)  & 0x03;    /* Switch 2                                             */
                rateArray[offset][2] = (rates >> 6)  & 0x03;    /* Switch 3                                             */
                rateArray[offset][3] = (rates >> 8)  & 0x03;    /* Switch 4                                             */
                rateArray[offset][4] = (rates >> 10) & 0x03;    /* Switch 5                                             */
                rateArray[offset][5] = (rates) & 0x03;          /* Midnight                                             */

                current_rate = rateArray[offset][5];
                resultString += string("00:00: ") + (char)('A' + current_rate) + "\n";
            }
            else
            {
                rateArray[offset][0] = (rates) & 0x03;          /* switch 6                                             */
                rateArray[offset][1] = (rates >> 2) & 0x03;     /* switch 7                                             */
                rateArray[offset][2] = (rates >> 4) & 0x03;     /* switch 8                                             */
                rateArray[offset][3] = (rates >> 6) & 0x03;     /* switch 9                                             */
                rateArray[offset][4] = (rates >> 8) & 0x03;     /* switch 10                                            */

                current_rate = -1;
            }

            time_offset = 0;
            previous_rate = current_rate;
            for( int switchtime = 0; switchtime < 5; switchtime++ )
            {
                time_offset += InMessage->Buffer.DSt.Message[byte_offset + switchtime] * 300;
                timeArray[offset][switchtime] = InMessage->Buffer.DSt.Message[byte_offset + switchtime];

                const int hour   = time_offset / 3600;
                const int minute = (time_offset / 60) % 60;

                current_rate = rateArray[offset][switchtime];

                if( hour <= 23 && current_rate != previous_rate )
                {
                    resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                }

                previous_rate = current_rate;
            }

            if( switch1_5 )
            {
                resultString += "\n\n";
            }
            else
            {
                resultString += "- end of day - \n\n";
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

            long times_schedule[10],
                 rates_schedule[11];

            if( ScheduleValid )
            {
                                                                /* parse dynamic info to get times and rate values      */
                parseTOUDayScheduleString(dynDaySchedule, times_schedule, rates_schedule);
            }
            else
            {
                                                                /* if the dynamic info doesn't exist, fill with -1      */
                std::fill( times_schedule, times_schedule + (sizeof(times_schedule)/sizeof(times_schedule[0])) , -1 );
                std::fill( rates_schedule, rates_schedule + (sizeof(rates_schedule)/sizeof(rates_schedule[0])) , -1 );
            }

            if( switch1_5 )                                     /* switch 1 - 5 and midnight                            */
            {
                std::copy(timeArray[schedule_nbr], timeArray[schedule_nbr] + 5, times_schedule);
                std::copy(rateArray[schedule_nbr], rateArray[schedule_nbr] + 5, rates_schedule);
                rates_schedule[10] = rateArray[schedule_nbr][5];
            }
            else                                                /* switch 6 - 10                                        */
            {
               std::copy(timeArray[schedule_nbr], timeArray[schedule_nbr] + 5, times_schedule + 5);
               std::copy(rateArray[schedule_nbr], rateArray[schedule_nbr] + 5, rates_schedule + 5);
            }

            string daySchedule;
                                                                /* create new strings for all 10 switches               */
            createTOUDayScheduleString(daySchedule, times_schedule, rates_schedule);

                                                                /* Set daily schedule dynamic info                      */
            CtiDeviceBase::setDynamicInfo(key[schedule_nbr], daySchedule);
        }
    }
    else
    {
        resultString = getName() + " / TOU Status:\n\n";

        timestamp = InMessage->Buffer.DSt.Message[6] << 24 |
                    InMessage->Buffer.DSt.Message[7] << 16 |
                    InMessage->Buffer.DSt.Message[8] <<  8 |
                    InMessage->Buffer.DSt.Message[9];

        resultString += "Current time: " + CtiTime(timestamp).asString() + "\n";

        int tz_offset = (char)InMessage->Buffer.DSt.Message[10] * 15;

        resultString += "Time zone offset: " + CtiNumStr((float)tz_offset / 60.0, 1) + " hours ( " + CtiNumStr(tz_offset) + " minutes)\n";

        if( InMessage->Buffer.DSt.Message[3] & 0x80 )
        {
            resultString += "Critical peak active\n";
        }
        if( InMessage->Buffer.DSt.Message[4] & 0x80 )
        {
            resultString += "Holiday active\n";
        }
        if( InMessage->Buffer.DSt.Message[4] & 0x40 )
        {
            resultString += "DST active\n";
        }

        resultString += "Current rate: " + string(1, (char)('A' + (InMessage->Buffer.DSt.Message[3] & 0x7f))) + "\n";

        resultString += "Current schedule: " + CtiNumStr((int)(InMessage->Buffer.DSt.Message[4] & 0x03) + 1) + "\n";


        resultString += "Current switch time: ";
        if( InMessage->Buffer.DSt.Message[5] == 0xff )
        {
            resultString += "not active\n";
        }
        else
        {
            resultString += CtiNumStr((int)InMessage->Buffer.DSt.Message[5]) + "\n";
        }

        resultString += "Default rate: ";
        if( InMessage->Buffer.DSt.Message[2] == 0xff )
        {
            resultString += "No TOU active\n";
        }
        else
        {
            resultString += string(1, (char)('A' + InMessage->Buffer.DSt.Message[2])) + "\n";
        }

        resultString += "\nDay table: \n";

        const char *(daynames[8]) = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Holiday"};

        for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
        {
            int dayschedule = InMessage->Buffer.DSt.Message[1 - day_nbr/4] >> ((day_nbr % 4) * 2) & 0x03;

            resultString += "Schedule " + CtiNumStr(dayschedule + 1) + " - " + daynames[day_nbr] + "\n";
        }
    }

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                       decodeGetConfigModel()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::decodeGetConfigModel(INMESS         *InMessage,
                                             CtiTime        &TimeNow,
                                             CtiMessageList &vgList,
                                             CtiMessageList &retList,
                                             OutMessageList &outList)
{
    DSTRUCT &DSt = InMessage->Buffer.DSt;

    const unsigned revision = DSt.Message[0];
    const unsigned sspec    = DSt.Message[1] << 8 |
                              DSt.Message[2];
    string descriptor;

    descriptor += getName() + " / Model information:\n";
    descriptor += "Software Specification " + CtiNumStr(sspec) + " rev ";

    //  convert 10 to 1.0, 24 to 2.4
    descriptor += CtiNumStr(((double)revision) / 10.0, 1);

    descriptor += "\n";

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage->Return.CommandStr, NoError, ReturnMsg, vgList, retList, InMessage->MessageFlags & MessageFlag_ExpectMore );

    return NoError;
}


/*
*********************************************************************************************************
*                                       decodeGetConfigHoliday()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::decodeGetConfigHoliday(INMESS          *InMessage,
                                               CtiTime         &TimeNow,
                                               CtiMessageList  &vgList,
                                               CtiMessageList  &retList,
                                               OutMessageList  &outList)
{
    INT status = NORMAL;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    int holiday_offset;
    int holiday_cnt;

    switch( InMessage->Return.ProtocolInfo.Emetcon.Function )
    {
        case Memory_Holiday1_6Pos  : holiday_offset = 0;  holiday_cnt = 6; break;
        case Memory_Holiday7_12Pos : holiday_offset = 6;  holiday_cnt = 6; break;
        case Memory_Holiday13_18Pos: holiday_offset = 12; holiday_cnt = 6; break;
        case Memory_Holiday19_24Pos: holiday_offset = 18; holiday_cnt = 6; break;
        case Memory_Holiday25_28Pos: holiday_offset = 24; holiday_cnt = 4; break;
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Invalid InMessage.Return.ProtocolInfo.Emetcon.Function " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return TYNF;
        }
    }

    DSTRUCT &DSt  = InMessage->Buffer.DSt;
    string result = getName() + " / Holiday schedule:\n";

    for( int holiday_nbr = 0; holiday_nbr < holiday_cnt; holiday_nbr++ )
    {
        CtiDate holiday_date(holidayBaseDate);

        const int days = (DSt.Message[holiday_nbr*2] << 8) | DSt.Message[(holiday_nbr*2)+1];

        holiday_date += days;

        ostringstream ss;
        ss << (holiday_nbr + holiday_offset + 1);

        result += "Holiday " + ss.str() + ": " + holiday_date.asStringUSFormat() + "\n";
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(result);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return NORMAL;
}


/*
*********************************************************************************************************
*                                       decodeGetStatusFreeze()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetStatusFreeze(INMESS          *InMessage,
                                              CtiTime         &TimeNow,
                                              CtiMessageList  &vgList,
                                              CtiMessageList  &retList,
                                              OutMessageList  &outList)
{
     INT status = NORMAL;

     INT ErrReturn  = InMessage->EventCode & 0x3fff;
     DSTRUCT *DSt  = &InMessage->Buffer.DSt;

     string resultString;
     unsigned long tmpTime;
     CtiTime lpTime;

     CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
     CtiPointDataMsg      *pData = NULL;

     if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
     {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
     }

     ReturnMsg->setUserMessageId(InMessage->Return.UserID);

     resultString += getName() + " / Freeze status:\n";

     tmpTime = DSt->Message[0] << 24 |
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

     ReturnMsg->setResultString(resultString);

     retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

     return status;
}


/*
*********************************************************************************************************
*                                    decodeGetConfigIntervals()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetConfigIntervals(INMESS          *InMessage,
                                                 CtiTime         &TimeNow,
                                                 CtiMessageList  &vgList,
                                                 CtiMessageList  &retList,
                                                 OutMessageList  &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;

    resultString  = getName() + " / Demand Interval:       " + CtiNumStr(DSt->Message[0]) + string(" minutes\n");
    resultString += getName() + " / Load Profile Interval: " + CtiNumStr(DSt->Message[1]) + string(" minutes\n");

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                        executePutStatus()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::executePutStatus(CtiRequestMsg     *pReq,
                                         CtiCommandParser  &parse,
                                         OUTMESS          *&OutMessage,
                                         CtiMessageList    &vgList,
                                         CtiMessageList    &retList,
                                         OutMessageList    &outList)
{
    INT nRet = NoMethod;


    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    if( parse.isKeyValid("set_tou_holiday_rate") )
    {
        OutMessage->Sequence = EmetconProtocol::PutStatus_SetTOUHolidayRate;
        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = NoError;
        }

    }
    else if( parse.isKeyValid("clear_tou_holiday_rate") )
    {
        OutMessage->Sequence = EmetconProtocol::PutStatus_ClearTOUHolidayRate;
        if( getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            nRet = NoError;
        }
    }
    else
    {
        nRet = Inherited::executePutStatus(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


/*
*********************************************************************************************************
*                                       decodeGetConfigAlarmMask()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::decodeGetConfigAlarmMask(INMESS          *InMessage,
                                                 CtiTime         &TimeNow,
                                                 CtiMessageList  &vgList,
                                                 CtiMessageList  &retList,
                                                 OutMessageList  &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
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

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                       createTOUScheduleConfig()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
void Mct440_213xBDevice::createTOUScheduleConfig(long           (&daySchedule)[8],
                                                 long           (&times)[4][10],
                                                 long           (&rates)[4][11],
                                                 long            defaultRate,
                                                 OUTMESS        *&OutMessage,
                                                 OutMessageList  &outList)
{

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 1 ---------- */
    auto_ptr<OUTMESS> TOU_OutMessage( CTIDBG_new OUTMESS(*OutMessage) );

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

    // write the durations for schedules 1 and 2 (time 1 to 5)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 2] = times[0][offset];
        TOU_OutMessage->Buffer.BSt.Message[offset + 9] = times[1][offset];
    }

    // write the rates for schedules 1 and 2 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[7]  = ((rates[1][5]  & 0x03) << 6) |
                                             ((rates[1][4]  & 0x03) << 4) |
                                             ((rates[0][5]  & 0x03) << 2) |
                                             ((rates[0][4]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[8]  = ((rates[0][3]  & 0x03) << 6) |
                                             ((rates[0][2]  & 0x03) << 4) |
                                             ((rates[0][1]  & 0x03) << 2) |
                                             ((rates[0][0]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[14] = ((rates[1][3]  & 0x03) << 6) |
                                             ((rates[1][2]  & 0x03) << 4) |
                                             ((rates[1][1]  & 0x03) << 2) |
                                             ((rates[1][0]  & 0x03));

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 2 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule2Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule2Len;

    // write the durations for schedules 3 and 4 (time 1 to 5)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[2][offset];
        TOU_OutMessage->Buffer.BSt.Message[offset + 7] = times[3][offset];
    }

    // write the rates for schedule 3 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[5]  = ((rates[2][5]  & 0x03) << 2) |
                                             ((rates[2][4]  & 0x03) << 0);

    TOU_OutMessage->Buffer.BSt.Message[6]  = ((rates[2][3]  & 0x03) << 6) |
                                             ((rates[2][2]  & 0x03) << 4) |
                                             ((rates[2][1]  & 0x03) << 2) |
                                             ((rates[2][0]  & 0x03));

    // write the rates for schedule 4 (switch 1 to 5 + midnight)
    TOU_OutMessage->Buffer.BSt.Message[12] = ((rates[3][5]  & 0x03) << 2) |
                                             ((rates[3][4]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[13] = ((rates[3][3]  & 0x03) << 6) |
                                             ((rates[3][2]  & 0x03) << 4) |
                                             ((rates[3][1]  & 0x03) << 2) |
                                             ((rates[3][0]  & 0x03));

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 3 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule3Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule3Len;

    // write the durations for schedules 1 and 2 (time 6 to 10)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[0][offset+5];
        TOU_OutMessage->Buffer.BSt.Message[offset + 7] = times[1][offset+5];
    }

    // write the rates for schedule 1 (switch 6 to 10)
    TOU_OutMessage->Buffer.BSt.Message[5]  = ((rates[0][10] & 0x03) << 2) |
                                             ((rates[0][9]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[6]  = ((rates[0][8]  & 0x03) << 6) |
                                             ((rates[0][7]  & 0x03) << 4) |
                                             ((rates[0][6]  & 0x03) << 2);

    // write the rates for schedule 2 (switch 6 to 10)
    TOU_OutMessage->Buffer.BSt.Message[12] = ((rates[1][10] & 0x03) << 2) |
                                             ((rates[1][9]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[13] = ((rates[1][8]  & 0x03) << 6) |
                                             ((rates[1][7]  & 0x03) << 4) |
                                             ((rates[1][6]  & 0x03) << 2);

    // write default rate
    TOU_OutMessage->Buffer.BSt.Message[14] = defaultRate;

    outList.push_back( TOU_OutMessage.release() );

                                                                /* ----------- CONFIGURE TOU SCHEDULE PART 4 ---------- */
    TOU_OutMessage.reset( CTIDBG_new OUTMESS(*OutMessage) );

    TOU_OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
    TOU_OutMessage->Buffer.BSt.Function = FuncWrite_TOUSchedule4Pos;
    TOU_OutMessage->Buffer.BSt.Length   = FuncWrite_TOUSchedule4Len;
    TOU_OutMessage->Priority           -= 1;    // decrease priority of the last schedule to make sure we process it last

    // write the durations for schedules 3 and 4 (time 6 to 10)
    for( int offset = 0; offset < 5; offset++ )
    {
        TOU_OutMessage->Buffer.BSt.Message[offset + 0] = times[2][offset+5];
        TOU_OutMessage->Buffer.BSt.Message[offset + 7] = times[3][offset+5];
    }

    // write the rates for schedule 3 (switch 6 to 10)
    TOU_OutMessage->Buffer.BSt.Message[5]  = ((rates[2][10] & 0x03) << 2) |
                                             ((rates[2][9]  & 0x03) << 0);

    TOU_OutMessage->Buffer.BSt.Message[6]  = ((rates[2][8]  & 0x03) << 6) |
                                             ((rates[2][7]  & 0x03) << 4) |
                                             ((rates[2][6]  & 0x03) << 2);

    // write the rates for schedule 4 (switch 6 to 10)
    TOU_OutMessage->Buffer.BSt.Message[12] = ((rates[3][10] & 0x03) << 2) |
                                             ((rates[3][9]  & 0x03));

    TOU_OutMessage->Buffer.BSt.Message[13] = ((rates[3][8]  & 0x03) << 6) |
                                             ((rates[3][7]  & 0x03) << 4) |
                                             ((rates[3][6]  & 0x03) << 2);

    // write default rate
    TOU_OutMessage->Buffer.BSt.Message[14] = defaultRate;

    outList.push_back( TOU_OutMessage.release() );
}


/*
*********************************************************************************************************
*                                         decodeGetValueKWH()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetValueKWH(INMESS         *InMessage,
                                          CtiTime        &TimeNow,
                                          CtiMessageList &vgList,
                                          CtiMessageList &retList,
                                          OutMessageList &outList)
{
    INT status = NORMAL;
    CtiTime pointTime;
    bool valid_data = true;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info pi, pi_freezecount;

    CtiPointSPtr   pPoint;
    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( InMessage->Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    unsigned char *p_freeze_counter = 0;

    if( InMessage->Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
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
        const int channels = ChannelCount;
/*
        //  cheaper than looking for parse.getFlags() & CMD_FLAG_GV_KWH
        if( stringContainsIgnoreCase(InMessage->Return.CommandStr, " kwh") )
        {
            channels = 1;
        }
*/
        for( int chan_nbr = 0; chan_nbr < channels; chan_nbr++ )
        {
            int offset = (chan_nbr * 3);

            if( !chan_nbr || getDevicePointOffsetTypeEqual(chan_nbr + 1, PulseAccumulatorPointType) )
            {
                if( InMessage->Sequence == Cti::Protocols::EmetconProtocol::Scan_Accum ||
                    InMessage->Sequence == Cti::Protocols::EmetconProtocol::GetValue_KWH )
                {
                    pi = getAccumulatorData(DSt->Message + offset, 3, 0);

                    pointTime -= pointTime.seconds() % 60;
                }
                else if( InMessage->Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
                {
                    if( chan_nbr ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                    pi = getAccumulatorData(DSt->Message + offset, 3, p_freeze_counter);

                    if( pi.freeze_bit != getExpectedFreezeParity() )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - incoming freeze parity bit (" << pi.freeze_bit <<
                                                ") does not match expected freeze bit (" << getExpectedFreezeParity() <<
                                                "/" << getExpectedFreezeCounter() << ") on device \"" << getName() << "\", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        pi.description  = "Freeze parity does not match (";
                        pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                        pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                        pi.quality = InvalidQuality;
                        pi.value = 0;

                        ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                        status = ErrorInvalidFrozenReadingParity;
                    }
                    else
                    {
                        pointTime  = getLastFreezeTimestamp(TimeNow);
                        pointTime -= pointTime.seconds() % 60;
                    }
                }

                string point_name;
                int    point_offset = -1;

                switch( InMessage->Sequence )
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
                                      ReturnMsg,
                                      pi,
                                      point_name,
                                      pointTime,
                                      0.1,
                                      TAG_POINT_MUST_ARCHIVE);

                //  if the quality's invalid, throw the status to abnormal if it's the first channel OR there's a point defined
                if( pi.quality == InvalidQuality && !status && (!chan_nbr || getDevicePointOffsetTypeEqual(chan_nbr + 1, PulseAccumulatorPointType)) )
                {
                    ReturnMsg->setResultString("Invalid data returned for channel " + CtiNumStr(chan_nbr + 1) + "\n" + ReturnMsg->ResultString());
                    status = ErrorInvalidData;
                }
            }
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                      decodeGetConfigDisconnect()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetStatusDisconnect(INMESS         *InMessage,
                                                  CtiTime        &TimeNow,
                                                  CtiMessageList &vgList,
                                                  CtiMessageList &retList,
                                                  OutMessageList &outList)
{
    INT status = NORMAL, state = 0;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultStr, stateName;

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

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
                          ReturnMsg,
                          pi_disconnect,
                          pointName,
                          CtiTime(),
                          1.0,
                          TAG_POINT_MUST_ARCHIVE);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                      decodeGetConfigDisconnect()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodeGetConfigDisconnect(INMESS         *InMessage,
                                                  CtiTime        &TimeNow,
                                                  CtiMessageList &vgList,
                                                  CtiMessageList &retList,
                                                  OutMessageList &outList)
{
    INT status = NORMAL, state = 0;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultStr, stateName;

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

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
                          ReturnMsg,
                          pi_disconnect,
                          pointName,
                          CtiTime(),
                          1.0,
                          TAG_POINT_MUST_ARCHIVE);

    resultStr  = getName() + " / Disconnect Info:\n";

    resultStr += decodeDisconnectStatus(*DSt);

    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


/*
*********************************************************************************************************
*                                       decodeDisconnectStatus()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
string Mct440_213xBDevice::decodeDisconnectStatus(const DSTRUCT &DSt)
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


/*
*********************************************************************************************************
*                                     executePutConfigPhaseLossInstall()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigInstallPhaseLoss(CtiRequestMsg     *pReq,
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return NoConfigData;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        if( parse.isKeyValid("force")
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PhaseLossPercent) != phaseloss_percent
            || (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PhaseLossSeconds) != phaseloss_seconds )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_PhaseLossThreshold, OutMessage->Buffer.BSt) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Operation PutConfig_PhaseLossThreshold not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    return NoConfigData;
                }

                OutMessage->Buffer.BSt.Message[0] = phaseloss_percent;
                OutMessage->Buffer.BSt.Message[1] = (phaseloss_seconds >> 8);
                OutMessage->Buffer.BSt.Message[2] = phaseloss_seconds & 0xff;

                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ConfigNotCurrent;
            }
        }
        else
        {
            return ConfigCurrent;
        }
    }
    else
    {
        if( !getOperation(EmetconProtocol::GetConfig_PhaseLossThreshold, OutMessage->Buffer.BSt) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Operation PutConfig_PhaseLossThreshold not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return NORMAL;
}


/*
*********************************************************************************************************
*                                     executePutConfigInstallAddressing()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigInstallAddressing(CtiRequestMsg     *pReq,
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return NoConfigData;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Addressing not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    return NoConfigData;
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
                return ConfigNotCurrent;
            }
        }
        else
        {
            return ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_Addressing, OutMessage->Buffer.BSt) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Operation GetConfig_Addressing not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return NORMAL;
}


/*
*********************************************************************************************************
*                                     executePutConfigInstallDST()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigInstallDST(CtiRequestMsg     *pReq,
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        string enable_dst_str = deviceConfig->getValueFromKey(MCTStrings::EnableDST);
        std::transform(enable_dst_str.begin(), enable_dst_str.end(), enable_dst_str.begin(), tolower);

        const bool   enable_dst     = (enable_dst_str.compare("true") == 0);
        const int    status_flags   = (int)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_StatusFlags);
        const bool   dyn_enable_dst = ((status_flags >> 2) & 0x1 != 0x0);

        if( parse.isKeyValid("force") || enable_dst != dyn_enable_dst )
        {
            if( !parse.isKeyValid("verify") )
            {
                const int function = (enable_dst) ? EmetconProtocol::PutStatus_SetDSTActive : EmetconProtocol::PutStatus_SetDSTInactive;

                if( !getOperation(function, OutMessage->Buffer.BSt) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Operation " << ((enable_dst) ? "PutStatus_SetDSTActive" : "PutStatus_SetDSTInactive") << " not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    return NoConfigData;
                }

                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ConfigNotCurrent;
            }
        }
        else
        {
            return ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetStatus_Internal, OutMessage->Buffer.BSt) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Operation GetStatus_Internal not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return NORMAL;
}


/*
*********************************************************************************************************
*                                     executePutConfigTimezone()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigTimezone(CtiRequestMsg     *pReq,
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        char timezoneOffset = deviceConfig->getLongValueFromKey(MCTStrings::TimeZoneOffset);

        if( timezoneOffset == std::numeric_limits<long>::min() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        timezoneOffset *= 4; // The timezone offset in the mct is in 15 minute increments.

        if(parse.isKeyValid("force")
           || (char)CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset) != timezoneOffset)
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, OutMessage->Buffer.BSt) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Operation PutConfig_TimeZoneOffset not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    return NoConfigData;
                }

                //  the bstruct IO is set above by getOperation()
                OutMessage->Buffer.BSt.Message[0] = timezoneOffset;
                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ConfigNotCurrent;
            }
        }
        else
        {
            return ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_TimeZoneOffset, OutMessage->Buffer.BSt) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Operation GetConfig_TimeZoneOffset not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return NORMAL;
}


/*
*********************************************************************************************************
*                                  executePutConfigTimeAdjustTolerance()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
int Mct440_213xBDevice::executePutConfigTimeAdjustTolerance(CtiRequestMsg     *pReq,
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - deviceConfig not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return NoConfigData;
    }
                                                                /* overwrite the request command                        */
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( !readsOnly )
    {
        long timeAdjustTolerance = deviceConfig->getLongValueFromKey(MCTStrings::TimeAdjustTolerance);

        if( timeAdjustTolerance == std::numeric_limits<long>::min() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        if( parse.isKeyValid("force") ||
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance) != timeAdjustTolerance )
        {
            if( !parse.isKeyValid("verify") )
            {
                if( !getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Operation PutConfig_TimeAdjustTolerance not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    return NoConfigData;
                }

                //the bstruct IO is set above by getOperation()
                OutMessage->Buffer.BSt.Message[0] = timeAdjustTolerance;
                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                return ConfigNotCurrent;
            }
        }
        else
        {
            return ConfigCurrent;
        }
    }
    else // getconfig install
    {
        if( !getOperation(EmetconProtocol::GetConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Operation GetConfig_TimeAdjustTolerance not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return NoConfigData;
        }

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    return NORMAL;
}


/*
*********************************************************************************************************
*                                        decodePutConfig()
*
* Description :
*
* Argument(s) :
*
* Return(s)   :
*
* Caller(s)   :
*
* Note(s)     :
*********************************************************************************************************
*/
INT Mct440_213xBDevice::decodePutConfig(INMESS         *InMessage,
                                        CtiTime        &TimeNow,
                                        CtiMessageList &vgList,
                                        CtiMessageList &retList,
                                        OutMessageList &outList)
{
    INT status = NoError;
    string resultString;

    std::auto_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr));

                                                                /* ---------------- PUTCONFIG INSTALL ----------------- */
    if( InMessage->Sequence == EmetconProtocol::PutConfig_Install )
    {
        CtiCommandParser parse(InMessage->Return.CommandStr);

        string part = parse.getsValue("installvalue");

        Mct440_213xBDevice::ConfigPartsList supported_parts = getPartsList();

        // check if config part is supported by this device
        if( std::find(supported_parts.begin(), supported_parts.end(), part) == supported_parts.end() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - unsupported config part \"" << part << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return ErrorInvalidRequest;
        }

        //note that at the moment only putconfig install will ever have a group message count.
        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        if( InMessage->MessageFlags & MessageFlag_ExpectMore ||
            getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection) != 0)
        {
            ReturnMsg->setExpectMore(true);
        }

                                                                /* ------------- PUTCONFIG INSTALL READ  -------------- */
        if( InMessage->Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Read ||
            InMessage->Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read )
        {
            resultString = getName() + " / Config \"" + part + "\" data received: ";

            if( InMessage->Buffer.DSt.Length > 0 )
            {
                for( int byte_nbr = 0; byte_nbr < InMessage->Buffer.DSt.Length; byte_nbr++ )
                {
                    resultString.append((CtiNumStr(InMessage->Buffer.DSt.Message[byte_nbr]).hex().zpad(2)).toString(), 0, 2);
                }
            }
            else
            {
                resultString += "(no data)";
            }
        }

                                                                /* -------------- PUTCONFIG INSTALL WRITE ------------- */
        else // IO_Write or IO_Function_Write
        {
            bool do_readback = true;

            if( part == Mct4xxDevice::PutConfigPart_tou && InMessage->Return.ProtocolInfo.Emetcon.Function != FuncWrite_TOUSchedule4Pos )
            {
                do_readback = false; // putconfig install tou read only done for schedule 4
            }

            if( do_readback )
            {
                resultString = getName() + " / Config \"" + part + "\" date sent\n";

                string getconfig_cmd = string("getconfig install ") + part;

                CtiRequestMsg *newReq = new CtiRequestMsg(getID(),
                                                          getconfig_cmd,
                                                          InMessage->Return.UserID,
                                                          InMessage->Return.GrpMsgID,
                                                          InMessage->Return.RouteID,
                                                          0,  //  PIL will recalculate this;  if we include it, we will potentially be bypassing the initial macro routes
                                                          0,
                                                          InMessage->Return.OptionsField,
                                                          InMessage->Priority);

                newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                // the master can overwrite the default delay
                const unsigned long delay = gConfigParms.getValueAsULong("PORTER_MCT440_INSTALL_READ_DELAY", DEFAULT_INSTALL_READ_DELAY);

                // set it to execute in the future
                newReq->setMessageTime(CtiTime::now().seconds() + delay);

                resultString += getName() + " / delaying " + CtiNumStr(delay)
                             + " seconds for config \"" + part + "\" install read (until "
                             + newReq->getMessageTime().asString() + ")";

                retList.push_back(newReq);
            }
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler(InMessage->Return.CommandStr, status, ReturnMsg.release(), vgList, retList);
    }
    else
    {
        status = Inherited::decodePutConfig(InMessage,TimeNow,vgList,retList,outList);
    }

    return status;
}


}
}
