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

#define TOU_SCHEDULE_NBR            4
#define TOU_SCHEDULE_TIME_NBR      10
#define TOU_SCHEDULE_RATE_NBR      11

#define OUTAGE_NBR_MIN              1
#define OUTAGE_NBR_MAX             10

#define STR_EXPAND(x)              #x
#define STR(x)                     STR_EXPAND(x)

#define NBR_SECONDS_PER_DAY        86400
#define UTC_TIMESTAMP_JAN_1_2010   0x4B3D3B00


namespace Cti {
namespace Devices {


const Mct440_213xBDevice::FunctionReadValueMappings Mct440_213xBDevice::_readValueMaps = Mct440_213xBDevice::initReadValueMaps();
const Mct440_213xBDevice::CommandSet                Mct440_213xBDevice::_commandStore  = Mct440_213xBDevice::initCommandStore();


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

        // 0x0D0 – Holiday 1 - 7
        { 0x0d0,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday1                  } },
        { 0x0d0,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday2                  } },
        { 0x0d0,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday3                  } },
        { 0x0d0,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday4                  } },
        { 0x0d0,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday5                  } },
        { 0x0d0, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday6                  } },
        { 0x0d0, 12, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday7                  } },

        // 0x0D1 – Holiday 8 - 14
        { 0x0d1,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday8                  } },
        { 0x0d1,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday9                  } },
        { 0x0d1,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday10                 } },
        { 0x0d1,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday11                 } },
        { 0x0d1,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday12                 } },
        { 0x0d1, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday13                 } },
        { 0x0d1, 12, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday14                 } },

        // 0x0D2 – Holiday 15 - 21
        { 0x0d2,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday15                 } },
        { 0x0d2,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday16                 } },
        { 0x0d2,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday17                 } },
        { 0x0d2,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday18                 } },
        { 0x0d2,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday19                 } },
        { 0x0d2, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday20                 } },
        { 0x0d2, 12, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday21                 } },

        // 0x0D3 – Holiday 22 - 28
        { 0x0d3,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday22                 } },
        { 0x0d3,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday23                 } },
        { 0x0d3,  4, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday24                 } },
        { 0x0d3,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday25                 } },
        { 0x0d3,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday26                 } },
        { 0x0d3, 10, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday27                 } },
        { 0x0d3, 12, { 2, CtiTableDynamicPaoInfo::Key_MCT_Holiday28                 } },

        // 0x19D – Long Load Profile Status
        { 0x19d,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len            } },
        { 0x19d,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len            } },
        { 0x19d,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len            } },
        { 0x19d,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len            } },

        // 0x1AD – TOU Day Schedule
        { 0x1ad,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable                  } },
        { 0x1ad,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate            } },
        { 0x1ad, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset            } },

        // 0x1F6 – LCD Configuration 1
        { 0x1f6,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01               } },
        { 0x1f6,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02               } },
        { 0x1f6,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03               } },
        { 0x1f6,  3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04               } },
        { 0x1f6,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05               } },
        { 0x1f6,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06               } },
        { 0x1f6,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07               } },
        { 0x1f6,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08               } },
        { 0x1f6,  8, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09               } },
        { 0x1f6,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10               } },
        { 0x1f6, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11               } },
        { 0x1f6, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12               } },
        { 0x1f6, 12, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13               } },

        // 0x1F7 – LCD Configuration 2
        { 0x1f7,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14               } },
        { 0x1f7,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15               } },
        { 0x1f7,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16               } },
        { 0x1f7,  3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17               } },
        { 0x1f7,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18               } },
        { 0x1f7,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19               } },
        { 0x1f7,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20               } },
        { 0x1f7,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21               } },
        { 0x1f7,  8, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22               } },
        { 0x1f7,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23               } },
        { 0x1f7, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24               } },
        { 0x1f7, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25               } },
        { 0x1f7, 12, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26               } },

        // 0x1FE – Disconnect Status
        { 0x1fe,  5, { 2, CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold           } },
        { 0x1fe,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay              } },
        { 0x1fe,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes         } },
        { 0x1fe, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes            } },
        { 0x1fe, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_Configuration             } },
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

//    cs.insert(CommandStore(EmetconProtocol::GetConfig_TOU,                  EmetconProtocol::IO_Function_Read,  FuncRead_TOUSwitchSchedule12Pos,       FuncRead_TOUSwitchSchedule12Len));
//    cs.insert(CommandStore(EmetconProtocol::GetConfig_TOU,                  EmetconProtocol::IO_Function_Read,  FuncRead_TOUSwitchSchedule34Pos,       FuncRead_TOUSwitchSchedule34Len));
//    cs.insert(CommandStore(EmetconProtocol::GetConfig_TOU,                  EmetconProtocol::IO_Function_Read,  FuncRead_TOUSwitchSchedule12Part2Pos,  FuncRead_TOUSwitchSchedule12Part2Len));
//    cs.insert(CommandStore(EmetconProtocol::GetConfig_TOU,                  EmetconProtocol::IO_Function_Read,  FuncRead_TOUSwitchSchedule34Part2Pos,  FuncRead_TOUSwitchSchedule34Part2Len));

    return cs;
}


/*
*********************************************************************************************************
*                                     getDescriptorForRead()
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
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    if( cmd == EmetconProtocol::PutConfig_VThreshold ||
        cmd == EmetconProtocol::GetConfig_Thresholds)           /* check for commands no longer supported               */
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

                                                                /* ------------ TOU KWH (FORWARD / REVERSE) ----------- */
    if( (parse.getFlags() & CMD_FLAG_GV_KWH) &&
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
            found = false;

            CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                           string(OutMessage->Request.CommandStr),
                                                           string(),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.MacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec( ));

            if( errRet )
            {
                errRet->setResultString("Bad outage specification - Acceptable values: "STR(OUTAGE_NBR_MIN)" - "STR(OUTAGE_NBR_MAX));
                errRet->setStatus(NoMethod);
                retList.push_back(errRet);
            }
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
        nRet = Inherited::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);

        if (nRet == NoError)
        {
            if(_daily_read_info.request.type == daily_read_info_t::Request_Recent)
            {
                OutMessage->Buffer.BSt.Length = FuncRead_Channel1SingleDayLen;
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
    case EmetconProtocol::GetConfig_TOUPart2:
        status = decodeGetConfigTOU(InMessage, TimeNow, vgList, retList, outList);
        break;

    case EmetconProtocol::GetConfig_PhaseLossThreshold:
        status = decodeGetConfigPhaseLossThreshold(InMessage, TimeNow, vgList, retList, outList);
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

    const int phase_cnt = getPhaseCount();

    for (int i = 0; i < phase_cnt; i++)
    {
        int pointOffset;

        unsigned char *PhaseData = DSt->Message + (i*4);

        point_info PhaseVoltage;

                                                                /* Bits {31:20}    Voltage in units of 0.1V, range 0 to 409.5V  */
        PhaseVoltage.value       = (PhaseData[0] << 4) | (PhaseData[1] >> 4);
        PhaseVoltage.quality     = NormalQuality;
        PhaseVoltage.freeze_bit  = false;

        switch (i) {
            case 0: pointOffset = PointOffset_PulseAcc_LineVoltagePhaseA; break;
            case 1: pointOffset = PointOffset_PulseAcc_LineVoltagePhaseB; break;
            case 2: pointOffset = PointOffset_PulseAcc_LineVoltagePhaseC; break;
        }

        insertPointDataReport(PulseAccumulatorPointType,
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

        switch (i) {
            case 0: pointOffset = PointOffset_PulseAcc_LineCurrentPhaseA; break;
            case 1: pointOffset = PointOffset_PulseAcc_LineCurrentPhaseB; break;
            case 2: pointOffset = PointOffset_PulseAcc_LineCurrentPhaseC; break;
        }

        insertPointDataReport(PulseAccumulatorPointType,
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

        switch (i) {
            case 0: pointOffset = PointOffset_PulseAcc_LinePowFactPhaseA; break;
            case 1: pointOffset = PointOffset_PulseAcc_LinePowFactPhaseB; break;
            case 2: pointOffset = PointOffset_PulseAcc_LinePowFactPhaseC; break;
        }

        insertPointDataReport(PulseAccumulatorPointType,
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
    int point_offset = 0;

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
        for( int i = 0; i < 4; i++ )
        {
            int offset = (i * 3);

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

            string point_name = string("TOU rate ") + (char)('A' + i) + " kWh";

            switch (InMessage->Sequence)
            {
                case EmetconProtocol::GetValue_TOUkWh:
                case EmetconProtocol::GetValue_FrozenTOUkWh:
                    point_offset = 1 + PointOffset_PulseAcc_TOUBaseForward + i * PointOffset_RateOffset;
                    break;
                case EmetconProtocol::GetValue_TOUkWhReverse:
                case EmetconProtocol::GetValue_FrozenTOUkWhReverse:
                    point_offset = 1 + PointOffset_PulseAcc_TOUBaseReverse + i * PointOffset_RateOffset;
                    break;
            }

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


    if(deviceConfig)
    {
        if( ! readsOnly )
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

            for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
            {
                for( int j = 0; j < TOU_SCHEDULE_RATE_NBR; j++ )
                {
                    if( rateStringValues[i][j].empty() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
            }

            for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
            {
                for( int j = 0; j < TOU_SCHEDULE_TIME_NBR; j++ )
                {
                    if( timeStringValues[i][j].length() < 4 ) //A time needs at least 4 digits X:XX
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
                for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
                {
                    for( int j = 0; j < TOU_SCHEDULE_RATE_NBR; j++ )
                    {
                        rates[i][j] = rateStringValues[i][j][0] - 'A';
                        if( rates[i][j] < 0 || rates[i][j] > 3 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            nRet = NoConfigData;
                        }
                    }
                }
                for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
                {
                    for( int j = 0; j < TOU_SCHEDULE_TIME_NBR; j++ )
                    {
                        // Im going to remove the :, get the remaining value, and do simple math on it. I think this
                        // results in less error checking needed.
                        timeStringValues[i][j].erase(timeStringValues[i][j].find(':'), 1);
                        tempTime    = strtol(timeStringValues[i][j].c_str(),NULL,10);
                        times[i][j] = ((tempTime/100) * 60) + (tempTime%100);
                    }
                }
                // Time is currently the actual minutes, we need the difference. Also the MCT has 5 minute resolution.
                for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
                {
                    for( int j = (TOU_SCHEDULE_TIME_NBR-1); j > 0; j-- )
                    {
                        times[i][j] = times[i][j]-times[i][j-1];
                        times[i][j] = times[i][j]/5;
                        if( times[i][j] < 0 || times[i][j] > 255 )
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
                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule1Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule1Len;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (char)(dayTable>>8);
                        OutMessage->Buffer.BSt.Message[1] = (char)dayTable;
                        OutMessage->Buffer.BSt.Message[2] = (char)times[0][0];
                        OutMessage->Buffer.BSt.Message[3] = (char)times[0][1];
                        OutMessage->Buffer.BSt.Message[4] = (char)times[0][2];
                        OutMessage->Buffer.BSt.Message[5] = (char)times[0][3];
                        OutMessage->Buffer.BSt.Message[6] = (char)times[0][4];
                        OutMessage->Buffer.BSt.Message[7] = (char)( ((rates[1][4]<<6)&0xC0) | ((rates[1][3]<<4)&0x30) | ((rates[0][4]<<2)&0x0C) | (rates[0][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[8] = (char)( ((rates[0][2]<<6)&0xC0) | ((rates[0][1]<<4)&0x30) | ((rates[0][0]<<2)&0x0C) | (rates[0][5]&0x03) );
                        OutMessage->Buffer.BSt.Message[9] =  (char)times[1][0];
                        OutMessage->Buffer.BSt.Message[10] = (char)times[1][1];
                        OutMessage->Buffer.BSt.Message[11] = (char)times[1][2];
                        OutMessage->Buffer.BSt.Message[12] = (char)times[1][3];
                        OutMessage->Buffer.BSt.Message[13] = (char)times[1][4];
                        OutMessage->Buffer.BSt.Message[14] = (char)( ((rates[1][2]<<6)&0xC0) | ((rates[1][1]<<4)&0x30) | ((rates[1][0]<<2)&0x0C) | (rates[1][5]&0x03) );

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule2Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule2Len;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (char)times[2][0];
                        OutMessage->Buffer.BSt.Message[1] = (char)times[2][1];
                        OutMessage->Buffer.BSt.Message[2] = (char)times[2][2];
                        OutMessage->Buffer.BSt.Message[3] = (char)times[2][3];
                        OutMessage->Buffer.BSt.Message[4] = (char)times[2][4];
                        OutMessage->Buffer.BSt.Message[5] = (char)( ((rates[2][4]<<2)&0x0C) | (rates[2][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[6] = (char)( ((rates[2][2]<<6)&0xC0) | ((rates[2][1]<<4)&0x30) | ((rates[2][0]<<2)&0x0C) | (rates[2][5]&0x03) );
                        OutMessage->Buffer.BSt.Message[7] = (char)times[3][0];
                        OutMessage->Buffer.BSt.Message[8] = (char)times[3][1];
                        OutMessage->Buffer.BSt.Message[9] = (char)times[3][2];
                        OutMessage->Buffer.BSt.Message[10] = (char)times[3][3];
                        OutMessage->Buffer.BSt.Message[11] = (char)times[3][4];
                        OutMessage->Buffer.BSt.Message[12] = (char)( ((rates[3][4]<<2)&0x0C) | (rates[3][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[13] = (char)( ((rates[3][2]<<6)&0xC0) | ((rates[3][1]<<4)&0x30) | ((rates[3][0]<<2)&0x0C) | (rates[3][5]&0x03) );
                        OutMessage->Buffer.BSt.Message[14] = (char)(defaultTOURate);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule3Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule3Len;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (char)times[0][5];
                        OutMessage->Buffer.BSt.Message[1] = (char)times[0][6];
                        OutMessage->Buffer.BSt.Message[2] = (char)times[0][7];
                        OutMessage->Buffer.BSt.Message[3] = (char)times[0][8];
                        OutMessage->Buffer.BSt.Message[4] = (char)times[0][9];
                        OutMessage->Buffer.BSt.Message[5] = (char)( ((rates[0][9]<<2)&0x0C) | (rates[0][8]&0x03) );
                        OutMessage->Buffer.BSt.Message[6] = (char)( ((rates[0][7]<<6)&0xC0) | ((rates[0][6]<<4)&0x30) | ((rates[0][5]<<2)&0x0C) | (rates[0][10]&0x03) );
                        OutMessage->Buffer.BSt.Message[7] = (char)times[1][5];
                        OutMessage->Buffer.BSt.Message[8] = (char)times[1][6];
                        OutMessage->Buffer.BSt.Message[9] = (char)times[1][7];
                        OutMessage->Buffer.BSt.Message[10] = (char)times[1][8];
                        OutMessage->Buffer.BSt.Message[11] = (char)times[1][9];
                        OutMessage->Buffer.BSt.Message[12] = (char)( ((rates[1][9]<<2)&0x0C) | (rates[1][8]&0x03) );
                        OutMessage->Buffer.BSt.Message[13] = (char)( ((rates[1][7]<<6)&0xC0) | ((rates[1][6]<<4)&0x30) | ((rates[1][5]<<2)&0x0C) | (rates[1][10]&0x03) );
                        OutMessage->Buffer.BSt.Message[14] = (char)(defaultTOURate);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule4Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule4Len;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (char)times[2][5];
                        OutMessage->Buffer.BSt.Message[1] = (char)times[2][6];
                        OutMessage->Buffer.BSt.Message[2] = (char)times[2][7];
                        OutMessage->Buffer.BSt.Message[3] = (char)times[2][8];
                        OutMessage->Buffer.BSt.Message[4] = (char)times[2][9];
                        OutMessage->Buffer.BSt.Message[5] = (char)( ((rates[2][9]<<2)&0x0C) | (rates[2][8]&0x03) );
                        OutMessage->Buffer.BSt.Message[6] = (char)( ((rates[2][7]<<6)&0xC0) | ((rates[2][6]<<4)&0x30) | ((rates[2][5]<<2)&0x0C) | (rates[2][10]&0x03) );
                        OutMessage->Buffer.BSt.Message[7] = (char)times[3][5];
                        OutMessage->Buffer.BSt.Message[8] = (char)times[3][6];
                        OutMessage->Buffer.BSt.Message[9] = (char)times[3][7];
                        OutMessage->Buffer.BSt.Message[10] = (char)times[3][8];
                        OutMessage->Buffer.BSt.Message[11] = (char)times[3][9];
                        OutMessage->Buffer.BSt.Message[12] = (char)( ((rates[3][9]<<2)&0x0C) | (rates[3][8]&0x03) );
                        OutMessage->Buffer.BSt.Message[13] = (char)( ((rates[3][7]<<6)&0xC0) | ((rates[3][6]<<4)&0x30) | ((rates[3][5]<<2)&0x0C) | (rates[3][10]&0x03) );
                        OutMessage->Buffer.BSt.Message[14] = (char)(defaultTOURate);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
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

        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == NORMAL)
        {
            // Set up the reads here
            OutMessage->Buffer.BSt.Function    = FuncRead_TOUSwitchSchedule12Pos;
            OutMessage->Buffer.BSt.Length      = FuncRead_TOUSwitchSchedule12Len;
            OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Read;

            OUTMESS *touOutMessage             = CTIDBG_new OUTMESS(*OutMessage);
            touOutMessage->Priority           -= 1; //decrease for read. Only want read after a successful write.

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 1", COMMAND_STR_SIZE );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOUPart2;
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 3", COMMAND_STR_SIZE );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOUPart2;
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou", COMMAND_STR_SIZE );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
            outList.push_back( touOutMessage );

            touOutMessage = 0;
        }
    }
    else
    {
        nRet = NoConfigData;
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


                                                                /* ------------ TOU RATE SCHEDULE / STATUS ------------ */
    if( parse.isKeyValid("tou") )
    {
        // Load all the other stuff that is needed
        // FIXME: most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID        = getID();
        OutMessage->TargetID        = getID();
        OutMessage->Port            = getPortID();
        OutMessage->Remote          = getAddress();
        OutMessage->TimeOut         = 2;
        OutMessage->Retry           = 2;
        OutMessage->Request.RouteID = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        if( parse.isKeyValid("tou_schedule") )
        {
            int schedulenum = parse.getiValue("tou_schedule");

            if( schedulenum == 1 || schedulenum == 2 )
            {
                OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;

                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
                OutMessage->Sequence            = EmetconProtocol::GetConfig_TOUPart2;

                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = NoError;
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;

                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
                OutMessage->Sequence            = EmetconProtocol::GetConfig_TOUPart2;

                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                nRet = NoError;
            }
            else
            {
                CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                               string(OutMessage->Request.CommandStr),
                                                               string(),
                                                               nRet,
                                                               OutMessage->Request.RouteID,
                                                               OutMessage->Request.MacroOffset,
                                                               OutMessage->Request.Attempt,
                                                               OutMessage->Request.GrpMsgID,
                                                               OutMessage->Request.UserID,
                                                               OutMessage->Request.SOE,
                                                               CtiMultiMsg_vec( ));

                if( errRet )
                {
                    errRet->setResultString("invalid schedule number " + CtiNumStr(schedulenum));
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);
                }
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
            OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
            OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

            nRet = NoError;
        }
    }

                                                                /* -------------------- HOLIDAYS ---------------------- */
    if( parse.isKeyValid("holiday") )
    {
        // Load all the other stuff that is needed
        // FIXME: most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID            = getID();
        OutMessage->TargetID            = getID();
        OutMessage->Port                = getPortID();
        OutMessage->Remote              = getAddress();
        OutMessage->TimeOut             = 2;
        OutMessage->Retry               = 2;
        OutMessage->Request.RouteID     = getRouteID();


        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Read;

        OutMessage->Buffer.BSt.Function = Memory_Holiday1_7Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday1_7Len;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_Holiday1_7;

        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday8_14Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday8_14Len;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_Holiday8_14;

        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday15_21Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday15_21Len;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_Holiday15_21;

        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        OutMessage->Buffer.BSt.Function = Memory_Holiday22_28Pos;
        OutMessage->Buffer.BSt.Length   = Memory_Holiday22_28Len;
        OutMessage->Sequence            = EmetconProtocol::GetConfig_Holiday22_28;

        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));

        delete OutMessage;  //  we didn't use it, we made our own
        OutMessage = 0;

        nRet = NoError;
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
    for( int i=0; i<10; i++ )
    {
        schedule += CtiNumStr(times[i]);
        schedule += ", ";
    }
    for( int i=0; i<10; i++ )
    {
        schedule += CtiNumStr(rates[i]);
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

    for( int i=0; i<10; i++ )
    {
        ss >> times[i] >> sep;
    }

    for( int i=0; i<10; i++ )
    {
        ss >> rates[i] >> sep;
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

    CtiReturnMsg    *ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
    {
        resultString = getName() + " / Daily read requires SSPEC rev 2.1 or higher, command could not automatically retrieve SSPEC; retry command or execute \"getconfig model\" to verify SSPEC";
        status = ErrorVerifySSPEC;
    }
    else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_DailyRead )
    {
        resultString = getName() + " / Daily read requires SSPEC rev 2.1 or higher; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1);
        status = ErrorInvalidSSPEC;
        InMessage->Return.MacroOffset = 0;  //  stop the retries!
    }
    else
    {
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
            if( pi_forward.description    == ErrorText_OutOfRange
                && pi_reverse.description == ErrorText_OutOfRange )
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

            InterlockedExchange(&_daily_read_info.request.in_progress, false);
        }
    }

    //  this is gross
    if( !ReturnMsg->ResultString().empty() )
    {
        resultString = ReturnMsg->ResultString() + "\n" + resultString;
    }

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

    if( parse.isKeyValid("holiday_offset") )
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
    bool found = false;
    INT  nRet  = NoMethod;
    const INT function = EmetconProtocol::PutConfig_Holiday;


    if( found = getOperation(function, OutMessage->Buffer.BSt) )
    {
        unsigned long holidays[7];
        int holiday_count  = 0;
        int holiday_offset = parse.getiValue("holiday_offset");

        OutMessage->Sequence = function;

        CtiDate start_date = CtiDate(1,1,2010);

                                                                /*  grab up to 7 potential dates                        */
        for( int i = 0; i < 7 && parse.isKeyValid("holiday_date" + CtiNumStr(i)); i++ )
        {
            int month, day, year;
            char sep1, sep2;

            istringstream ss;
            ss.str(parse.getsValue("holiday_date" + CtiNumStr(i)));

            ss >> month >> sep1 >> day >> sep2 >> year;

            CtiDate holiday_date(day, month, year);


            if( holiday_date.isValid() && holiday_date > CtiDate::now() )
            {
                                                                /* get the number of days since Jan 1 2010              */
                holidays[holiday_count++] = (holiday_date.daysFrom1970() - start_date.daysFrom1970());
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

                for( int i = 0; i < holiday_count; i++ )
                {
                    OutMessage->Buffer.BSt.Message[i*2+1] = holidays[i] >> 8;
                    OutMessage->Buffer.BSt.Message[i*2+2] = holidays[i]  & 0xff;
                }
            }
            else
            {
                found = false;

                CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                               string(OutMessage->Request.CommandStr),
                                                               string(),
                                                               nRet,
                                                               OutMessage->Request.RouteID,
                                                               OutMessage->Request.MacroOffset,
                                                               OutMessage->Request.Attempt,
                                                               OutMessage->Request.GrpMsgID,
                                                               OutMessage->Request.UserID,
                                                               OutMessage->Request.SOE,
                                                               CtiMultiMsg_vec( ));

                if( errRet )
                {
                    errRet->setResultString("Specified dates are invalid");
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);
                }
            }
        }
        else
        {
            found = false;

            CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                           string(OutMessage->Request.CommandStr),
                                                           string(),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.MacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec( ));

            if( errRet )
            {
                errRet->setResultString("Invalid holiday offset specified, valid offsets are: 1, 8, 15, 22");
                errRet->setStatus(NoMethod);
                retList.push_back(errRet);
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
    bool  found    = false;
    INT   nRet     = NoMethod;
    INT   function = -1;


    if( parse.isKeyValid("tou_enable") )
    {
        function             = EmetconProtocol::PutConfig_TOUEnable;
        found                = getOperation(function, OutMessage->Buffer.BSt);
        OutMessage->Sequence = function;
    }
    else if( parse.isKeyValid("tou_disable") )
    {
        function             = EmetconProtocol::PutConfig_TOUDisable;
        found                = getOperation(function, OutMessage->Buffer.BSt);
        OutMessage->Sequence = function;
    }
    else if( parse.isKeyValid("tou_days") )
    {
        set< ratechange_t > ratechanges;
        int default_rate, day_schedules[8];

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
            CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                           string(OutMessage->Request.CommandStr),
                                                           string(),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.MacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec( ));

            if( errRet )
            {
                errRet->setResultString("TOU default rate \"" + parse.getsValue("tou_default") + "\" specified is invalid for device \"" + getName() + "\"");
                errRet->setStatus(NoMethod);
                retList.push_back(errRet);
            }
        }
        else
        {
            if( daytable.length() < 8 || daytable.find_first_not_of("1234") != string::npos )
            {
                CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                               string(OutMessage->Request.CommandStr),
                                                               string(),
                                                               nRet,
                                                               OutMessage->Request.RouteID,
                                                               OutMessage->Request.MacroOffset,
                                                               OutMessage->Request.Attempt,
                                                               OutMessage->Request.GrpMsgID,
                                                               OutMessage->Request.UserID,
                                                               OutMessage->Request.SOE,
                                                               CtiMultiMsg_vec( ));

                if( errRet )
                {
                    errRet->setResultString("day table \"" + daytable + "\" specified is invalid for device \"" + getName() + "\"");
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);
                }
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

                OUTMESS *TOU_OutMessage1 = CTIDBG_new OUTMESS(*OutMessage), // Configure TOU Schedule Part 1
                        *TOU_OutMessage2 = CTIDBG_new OUTMESS(*OutMessage), // Configure TOU Schedule Part 2
                        *TOU_OutMessage3 = CTIDBG_new OUTMESS(*OutMessage), // Configure TOU Schedule Part 3
                        *TOU_OutMessage4 = CTIDBG_new OUTMESS(*OutMessage); // Configure TOU Schedule Part 4

                TOU_OutMessage1->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;
                TOU_OutMessage2->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;
                TOU_OutMessage3->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;
                TOU_OutMessage4->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;

                TOU_OutMessage1->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;
                TOU_OutMessage2->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;
                TOU_OutMessage3->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;
                TOU_OutMessage4->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;

                TOU_OutMessage1->Buffer.BSt.Function = FuncWrite_TOUSchedule1Pos;
                TOU_OutMessage1->Buffer.BSt.Length   = FuncWrite_TOUSchedule1Len;

                TOU_OutMessage2->Buffer.BSt.Function = FuncWrite_TOUSchedule2Pos;
                TOU_OutMessage2->Buffer.BSt.Length   = FuncWrite_TOUSchedule2Len;

                TOU_OutMessage3->Buffer.BSt.Function = FuncWrite_TOUSchedule3Pos;
                TOU_OutMessage3->Buffer.BSt.Length   = FuncWrite_TOUSchedule3Len;

                TOU_OutMessage4->Buffer.BSt.Function = FuncWrite_TOUSchedule4Pos;
                TOU_OutMessage4->Buffer.BSt.Length   = FuncWrite_TOUSchedule4Len;


                std::set< ratechange_t >::iterator itr;

                //  There's much more intelligence and safeguarding that could be added to the below,
                //  but it's a temporary fix, to be handled soon by the proper MCT Configs,
                //  so I don't think it's worth it at the moment to add all of the smarts.
                //  We'll handle a good string, and kick out on anything else.

                int durations[TOU_SCHEDULE_NBR][TOU_SCHEDULE_TIME_NBR];
                int rates[TOU_SCHEDULE_NBR][TOU_SCHEDULE_RATE_NBR];

                for( int i = 0; i < TOU_SCHEDULE_NBR; i++ )
                {
                    for( int j = 0; j < TOU_SCHEDULE_RATE_NBR; j++ )
                    {
                        if( j < TOU_SCHEDULE_TIME_NBR )
                        {
                            durations[i][j] = 255;
                        }

                        rates[i][j] = default_rate;
                    }
                }

                int current_schedule = -1;
                int offset           =  0;
                int time_offset      =  0;

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

                        durations[rc.schedule][offset - 1] = (rc.time - time_offset) / 300;
                        rates[rc.schedule][offset]         = rc.rate;

                        if( (offset + 1) <= (TOU_SCHEDULE_RATE_NBR-1) )
                        {
                            //  this is to work around the 255 * 5 min limitation for switches - this way it doesn't
                            //    jump back to the default rate if only a midnight rate is specified
                            rates[rc.schedule][offset + 1] = rc.rate;
                        }

                        time_offset = rc.time - (rc.time % 300);  //  make sure we don't miss the 5-minute marks
                    }
                }


                for( offset = 0; offset < 8; offset++ )
                {
                    //  write the day table

                    int byte = 1 - (offset / 4);
                    int bitoffset = (2 * offset) % 8;

                    TOU_OutMessage1->Buffer.BSt.Message[byte] |= (day_schedules[offset] & 0x03) << bitoffset;
                }

                for( offset = 0; offset < 5; offset++ )
                {
                    //  write the durations for schedules 1 and 2 (time 1 to 5)
                    TOU_OutMessage1->Buffer.BSt.Message[offset + 2] = durations[0][offset];
                    TOU_OutMessage1->Buffer.BSt.Message[offset + 9] = durations[1][offset];

                    //  write the durations for schedules 3 and 4 (time 1 to 5)
                    TOU_OutMessage2->Buffer.BSt.Message[offset + 0] = durations[2][offset];
                    TOU_OutMessage2->Buffer.BSt.Message[offset + 7] = durations[3][offset];

                    //  write the durations for schedules 1 and 2 (time 6 to 10)
                    TOU_OutMessage3->Buffer.BSt.Message[offset + 0] = durations[0][offset+5];
                    TOU_OutMessage3->Buffer.BSt.Message[offset + 7] = durations[1][offset+5];

                    //  write the durations for schedules 3 and 4 (time 6 to 10)
                    TOU_OutMessage4->Buffer.BSt.Message[offset + 0] = durations[2][offset+5];
                    TOU_OutMessage4->Buffer.BSt.Message[offset + 7] = durations[3][offset+5];
                }


                //  write the rates for schedules 1 and 2 (switch 1 to 5 + midnight)
                TOU_OutMessage1->Buffer.BSt.Message[7]  = ((rates[1][5] & 0x03)  << 6) |
                                                          ((rates[1][4] & 0x03)  << 4) |
                                                          ((rates[0][5] & 0x03)  << 2) |
                                                          ((rates[0][4] & 0x03)  << 0);

                TOU_OutMessage1->Buffer.BSt.Message[8]  = ((rates[0][3] & 0x03)  << 6) |
                                                          ((rates[0][2] & 0x03)  << 4) |
                                                          ((rates[0][1] & 0x03)  << 2) |
                                                          ((rates[0][0] & 0x03)  << 0);

                TOU_OutMessage1->Buffer.BSt.Message[14] = ((rates[1][3] & 0x03)  << 6) |
                                                          ((rates[1][2] & 0x03)  << 4) |
                                                          ((rates[1][1] & 0x03)  << 2) |
                                                          ((rates[1][0] & 0x03)  << 0);

                //  write the rates for schedule 3 (switch 1 to 5 + midnight)
                TOU_OutMessage2->Buffer.BSt.Message[5]  = ((rates[2][5] & 0x03)  << 2) |
                                                          ((rates[2][4] & 0x03)  << 0);

                TOU_OutMessage2->Buffer.BSt.Message[6]  = ((rates[2][3] & 0x03)  << 6) |
                                                          ((rates[2][2] & 0x03)  << 4) |
                                                          ((rates[2][1] & 0x03)  << 2) |
                                                          ((rates[2][0] & 0x03)  << 0);

                //  write the rates for schedule 4 (switch 1 to 5 + midnight)
                TOU_OutMessage2->Buffer.BSt.Message[12] = ((rates[3][5] & 0x03)  << 2) |
                                                          ((rates[3][4] & 0x03)  << 0);

                TOU_OutMessage2->Buffer.BSt.Message[13] = ((rates[3][3] & 0x03)  << 6) |
                                                          ((rates[3][2] & 0x03)  << 4) |
                                                          ((rates[3][1] & 0x03)  << 2) |
                                                          ((rates[3][0] & 0x03)  << 0);

                // write default rate
                TOU_OutMessage2->Buffer.BSt.Message[14] = default_rate;

                //  write the rates for schedule 1 (switch 6 to 10)
                TOU_OutMessage3->Buffer.BSt.Message[5]  = ((rates[0][10] & 0x03)  << 2) |
                                                          ((rates[0][9] & 0x03)  << 0);

                TOU_OutMessage3->Buffer.BSt.Message[6]  = ((rates[0][8] & 0x03)  << 6) |
                                                          ((rates[0][7] & 0x03)  << 4) |
                                                          ((rates[0][6] & 0x03)  << 2);

                //  write the rates for schedule 2 (switch 6 to 10)
                TOU_OutMessage3->Buffer.BSt.Message[12] = ((rates[1][10] & 0x03)  << 2) |
                                                          ((rates[1][9] & 0x03)  << 0);

                TOU_OutMessage3->Buffer.BSt.Message[13] = ((rates[1][8] & 0x03)  << 6) |
                                                          ((rates[1][7] & 0x03)  << 4) |
                                                          ((rates[1][6] & 0x03)  << 2);

                // write default rate
                TOU_OutMessage3->Buffer.BSt.Message[14] = default_rate;

                //  write the rates for schedule 3 (switch 6 to 10)
                TOU_OutMessage4->Buffer.BSt.Message[5]  = ((rates[2][10] & 0x03)  << 2) |
                                                          ((rates[2][9] & 0x03)  << 0);

                TOU_OutMessage4->Buffer.BSt.Message[6]  = ((rates[2][8] & 0x03)  << 6) |
                                                          ((rates[2][7] & 0x03)  << 4) |
                                                          ((rates[2][6] & 0x03)  << 2);

                //  write the rates for schedule 4 (switch 6 to 10)
                TOU_OutMessage4->Buffer.BSt.Message[12] = ((rates[3][10] & 0x03)  << 2) |
                                                          ((rates[3][9] & 0x03)  << 0);

                TOU_OutMessage4->Buffer.BSt.Message[13] = ((rates[3][8] & 0x03)  << 6) |
                                                          ((rates[3][7] & 0x03)  << 4) |
                                                          ((rates[3][6] & 0x03)  << 2);

                // write default rate
                TOU_OutMessage4->Buffer.BSt.Message[14] = default_rate;

                outList.push_back(TOU_OutMessage4);
                outList.push_back(TOU_OutMessage3);
                outList.push_back(TOU_OutMessage2);
                outList.push_back(TOU_OutMessage1);

                TOU_OutMessage1 = 0;
                TOU_OutMessage2 = 0;
                TOU_OutMessage3 = 0;
                TOU_OutMessage4 = 0;

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
    resultString += (DSt.Message[3] & 0x80)?"Current without voltage\n":"";

    // point offset 50 - Meter alarm (byte 0)
    resultString += (DSt.Message[4] & 0x01)?"Load side voltage detected\n":"";
    resultString += (DSt.Message[4] & 0x02)?"Low battery\n":"";
    resultString += (DSt.Message[4] & 0x04)?"Voltage out of limit\n":"";
    resultString += (DSt.Message[4] & 0x08)?"Metal box cover removal\n":"";
    resultString += (DSt.Message[4] & 0x10)?"Reverse Energy\n":"";
    resultString += (DSt.Message[4] & 0x20)?"Terminal block cover removal\n":"";
    resultString += (DSt.Message[4] & 0x40)?"Internal error\n":"";
    resultString += (DSt.Message[4] & 0x80)?"Out of voltage\n":"";

    ReturnMsg->setResultString(resultString);

    for( int i = 0; i < 5; i++ )
    {
        int offset;
        boost::shared_ptr<CtiPointStatus> point;
        CtiPointDataMsg *pData;
        string pointResult;

        if( i == 0 )  offset = 30;
        if( i == 1 )  offset = 10;
        if( i == 2 )  offset = 20;
        if( i == 3 )  offset = 40;
        if( i == 4 )  offset = 50;

        for( int j = 0; j < 8; j++ )
        {
            //  Don't send the powerfail status again - it's being sent by dev_mct in ResultDecode()
            if( (offset + j != 10) && (point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( offset + j, StatusPointType ))) )
            {
                double value = (DSt.Message[i] >> j) & 0x01;

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
    INT  nRet = NoMethod;
    const INT function = EmetconProtocol::PutConfig_PhaseLossThreshold;


    if( !getOperation(function, OutMessage->Buffer.BSt) )
    {
        return nRet;
    }

    OutMessage->Sequence  = function;
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

        nRet = NoError;
    }
    else
    {
        CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                       string(OutMessage->Request.CommandStr),
                                                       string(),
                                                       nRet,
                                                       OutMessage->Request.RouteID,
                                                       OutMessage->Request.MacroOffset,
                                                       OutMessage->Request.Attempt,
                                                       OutMessage->Request.GrpMsgID,
                                                       OutMessage->Request.UserID,
                                                       OutMessage->Request.SOE,
                                                       CtiMultiMsg_vec( ));
        if( errRet )
        {
            errRet->setResultString("Invalid phase loss thresholds for device \"" + getName() + "\" - Acceptable values: phase loss percent range: 0 - 100 ; phase loss max duration: 18:12:15 - ");
            errRet->setStatus(NoMethod);
            retList.push_back(errRet);
        }

        nRet = BADPARAM;
    }

    return nRet;
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

            if( InMessage->Sequence == EmetconProtocol::GetConfig_TOU )
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
            else                                                /* EmetconProtocol::GetConfig_TOUPart2                  */
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
                int hour, minute;

                time_offset += InMessage->Buffer.DSt.Message[byte_offset + switchtime] * 300;
                timeArray[offset][switchtime] = InMessage->Buffer.DSt.Message[byte_offset + switchtime];

                hour   = time_offset / 3600;
                minute = (time_offset / 60) % 60;

                current_rate = rateArray[offset][switchtime];

                if( hour <= 23 && current_rate != previous_rate )
                {
                    resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                }

                previous_rate = current_rate;
            }

            if( InMessage->Sequence == EmetconProtocol::GetConfig_TOU )
            {
                resultString += "- end of switch 1-5 - \n\n";
            }
            else
            {
                resultString += "- end of day - \n\n";
            }

        }

        string dynDayScheduleA,
               dynDayScheduleB;

        if( schedulenum < 2 )                                   /* Retrieve dynamic info for schedule 1 and 2           */
        {
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dynDayScheduleA);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dynDayScheduleB);
        }
        else                                                    /* Retrieve dynamic info for schedule 3 and 4           */
        {
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dynDayScheduleA);
            CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dynDayScheduleB);
        }

        long times_schedule[2][10],
             rates_schedule[2][11];
                                                                /* parse dynamic info to get times and rate values      */
        parseTOUDayScheduleString(dynDayScheduleA, times_schedule[0], rates_schedule[0]);
        parseTOUDayScheduleString(dynDayScheduleB, times_schedule[1], rates_schedule[1]);

                                                                /* copy the new data over the previous                  */
        if( InMessage->Sequence == EmetconProtocol::GetConfig_TOU )
        {
            for( int i=0; i<2; i++ )                            /* switch 1 - 5 and midnight                            */
            {
                memcpy(&times_schedule[i][0], timeArray[i], sizeof(long)*5);
                memcpy(&rates_schedule[i][0], rateArray[i], sizeof(long)*5);
                rates_schedule[i][10] = rateArray[i][5];
            }
        }
        else
        {
            for( int i=0; i<2; i++ )                            /* switch 6 - 10                                        */
            {
                memcpy(&times_schedule[i][5], timeArray[i], sizeof(long)*5);
                memcpy(&rates_schedule[i][5], rateArray[i], sizeof(long)*5);
            }
        }

        string dayScheduleA,
               dayScheduleB;
                                                                /* create new strings for all 10 switches               */
        createTOUDayScheduleString(dayScheduleA, times_schedule[0], rates_schedule[0]);
        createTOUDayScheduleString(dayScheduleB, times_schedule[1], rates_schedule[1]);

        if( schedulenum < 2 )                                   /* Update dynamic info for schedule 1 and 2             */
        {
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dayScheduleA);
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dayScheduleB);
        }
        else                                                    /* Update dynamic info for schedule 3 and 4             */
        {
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dayScheduleA);
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dayScheduleB);
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
/*
        resultString += "Current switch time: ";

        if( InMessage->Buffer.DSt.Message[5] == 0xff )
        {
            resultString += "not active\n";
        }
        else
        {
             resultString += CtiNumStr((int)InMessage->Buffer.DSt.Message[5]) + "\n";
        }
*/
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

        char *(daynames[8]) = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Holiday"};

        for( int i = 0; i < 8; i++ )
        {
            int dayschedule = InMessage->Buffer.DSt.Message[1 - i/4] >> ((i % 4) * 2) & 0x03;

            resultString += "Schedule " + CtiNumStr(dayschedule + 1) + " - " + daynames[i] + "\n";
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
int Mct440_213xBDevice::decodeGetConfigModel(INMESS              *InMessage,
                                             CtiTime             &TimeNow,
                                             list< CtiMessage* > &vgList,
                                             list< CtiMessage* > &retList,
                                             list< OUTMESS* >    &outList)
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
*                                           decodeGetConfig()
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
INT Mct440_213xBDevice::decodeGetConfig(INMESS          *InMessage,
                                        CtiTime         &TimeNow,
                                        CtiMessageList  &vgList,
                                        CtiMessageList  &retList,
                                        OutMessageList  &outList)
{
    INT status = NORMAL;


    switch( InMessage->Sequence )
    {
        case EmetconProtocol::GetConfig_Holiday1_7:
        case EmetconProtocol::GetConfig_Holiday8_14:
        case EmetconProtocol::GetConfig_Holiday15_21:
        case EmetconProtocol::GetConfig_Holiday22_28:
        {
            status = Mct440_213xBDevice::decodeGetConfigHoliday(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        default:
        {
            status = Inherited::decodeGetConfig(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    return status;
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

    DSTRUCT &DSt       = InMessage->Buffer.DSt;
    string result      = getName() + " / Holiday schedule:\n";
    CtiTime start_time = CtiTime(UTC_TIMESTAMP_JAN_1_2010);

    int offset;

    switch( InMessage->Sequence )
    {
        case EmetconProtocol::GetConfig_Holiday1_7:     offset = 1;  break;
        case EmetconProtocol::GetConfig_Holiday8_14:    offset = 8;  break;
        case EmetconProtocol::GetConfig_Holiday15_21:   offset = 15; break;
        case EmetconProtocol::GetConfig_Holiday22_28:   offset = 22; break;
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " InMessage->Sequence not supported " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return BADPARAM;
        }
    }

    for( int i = 0; i < 7; i++ )
    {
        unsigned long days      = (DSt.Message[i*2] << 8) | DSt.Message[(i*2)+1];

        unsigned long timestamp = (days * NBR_SECONDS_PER_DAY) + start_time.seconds();
        CtiTime holiday         = CtiTime(timestamp);
        ostringstream ss;
        ss << (i + offset);

        result += "Holiday " + ss.str() + ": " + (holiday.isValid()?holiday.asString(CtiTime::Gmt, CtiTime::OmitTimezone) + " GMT":"(invalid)") + "\n";
    }

    ReturnMsg->setResultString( result );

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return NORMAL;
}


}
}
