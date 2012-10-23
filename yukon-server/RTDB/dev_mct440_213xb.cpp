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


namespace Cti {
namespace Devices {


const Mct440_213xBDevice::FunctionReadValueMappings Mct440_213xBDevice::_readValueMaps = Mct440_213xBDevice::initReadValueMaps();
const Mct440_213xBDevice::CommandSet                Mct440_213xBDevice::_commandStore  = Mct440_213xBDevice::initCommandStore();


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

        // 0x01E – Voltage Thresholds (FIXME)
        { 0x01e,  0, { 2, CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold      } },
        { 0x01e,  2, { 2, CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold     } },
        { 0x01e,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles              } },

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

        // 0x0D0 – Holiday 1
        { 0x0d0,  0, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday1                  } },
        { 0x0d0,  4, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2                  } },
        { 0x0d0,  8, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3                  } },

        // 0x0D4 – Holiday 2
        { 0x0d4,  0, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2                  } },
        { 0x0d4,  4, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3                  } },

        // 0x0D8 – Holiday 3
        { 0x0d8,  0, { 4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3                  } },

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
const Mct440_213xBDevice::ReadDescriptor Mct440_213xBDevice::getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const
{
    unsigned read = function;

    switch( io )
    {
        case EmetconProtocol::IO_Function_Read:
        {
            read += 0x100;
            // fall through
        }
        case EmetconProtocol::IO_Read:
        {
            if(const FunctionReadValueMappings *rm = &_readValueMaps)
            {
                FunctionReadValueMappings::const_iterator itr = rm->find(read);

                if( itr != rm->end() )
                {
                    return getDescriptorFromMapping(itr->second, 0, readLength);
                }
            }
        }
    }

    return ReadDescriptor();
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

    return Inherited::getOperation(cmd, bst);
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
bool Mct440_213xBDevice::isSupported(const Mct410Device::Features feature) const
{
    return Mct410Device::isSupported(feature);
}


/*
*********************************************************************************************************
*                                          executeGetValue()
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
INT Mct440_213xBDevice::executeGetValue(CtiRequestMsg     *pReq,
                                        CtiCommandParser  &parse,
                                        OUTMESS           *&OutMessage,
                                        CtiMessageList    &vgList,
                                        CtiMessageList    &retList,
                                        OutMessageList    &outList)
{
    INT nRet   = NoMethod;
    bool found = false;
    int function;


    static const string str_outage          = "outage";
    static const string str_instantlinedata = "instantlinedata";

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
    return (sspec == Mct440_213xBDevice::Sspec) || ((sspec / 10) == Mct440_213xBDevice::Sspec);
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
    unsigned char *PhaseData = NULL;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);


    for (int i = 0; i < 3; i++)
    {
        int pointOffset;

        PhaseData = DSt->Message + (i*4);

        point_info PhaseVoltage;

                                                                /* Bits {31:20}    Voltage in units of 0.1V, range 0 to 409.5V  */
        PhaseVoltage.value       = (PhaseData[0] << 4) | (PhaseData[1] >> 4);
        PhaseVoltage.quality     = NormalQuality;
        PhaseVoltage.freeze_bit  = false;

        switch (i) {
            case 0: pointOffset = PointOffset_LineVoltagePhaseA; break;
            case 1: pointOffset = PointOffset_LineVoltagePhaseB; break;
            case 2: pointOffset = PointOffset_LineVoltagePhaseC; break;
        }

        insertPointDataReport(DemandAccumulatorPointType,
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
            case 0: pointOffset = PointOffset_LineCurrentPhaseA; break;
            case 1: pointOffset = PointOffset_LineCurrentPhaseB; break;
            case 2: pointOffset = PointOffset_LineCurrentPhaseC; break;
        }

        insertPointDataReport(DemandAccumulatorPointType,
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
            case 0: pointOffset = PointOffset_LinePowFactorPhaseA; break;
            case 1: pointOffset = PointOffset_LinePowFactorPhaseB; break;
            case 2: pointOffset = PointOffset_LinePowFactorPhaseC; break;
        }

        insertPointDataReport(DemandAccumulatorPointType,
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
                    point_offset = 1 + PointOffset_TOUBase + i * PointOffset_RateOffset;
                    break;
                case EmetconProtocol::GetValue_TOUkWhReverse:
                case EmetconProtocol::GetValue_FrozenTOUkWhReverse:
                    point_offset = 1 + PointOffset_TOUBaseReverse + i * PointOffset_RateOffset;
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
* Description :
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
                        tempTime = strtol(timeStringValues[i][j].data(),NULL,10);
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
            touOutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 1", COMMAND_STR_SIZE );
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 3", COMMAND_STR_SIZE );
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 1 continued", COMMAND_STR_SIZE );
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

            touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
            touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;

            strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 3 continued", COMMAND_STR_SIZE );
            outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

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
    bool found = false;

                                                                /* ---------------------- TOU RATE -------------------- */
    if( parse.isKeyValid("tou") )
    {
        found = true;

        if( parse.isKeyValid("tou_schedule") )
        {
            int schedulenum = parse.getiValue("tou_schedule");

            if( schedulenum == 1 || schedulenum == 2 )
            {
                if( parse.isKeyValid("continued") )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Part2Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Part2Len;
                    OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                }
                else
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                    OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                }
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                if( parse.isKeyValid("continued") )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Part2Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Part2Len;
                    OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                }
                else
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                    OutMessage->Sequence            = EmetconProtocol::GetConfig_TOU;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
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
        }
    }

                                                                /* -------------- INHERITED FROM MCT-420 -------------- */
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        // FIXME: most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
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
        schedule += ', ';
    }
    for( int i=0; i<10; i++ )
    {
        schedule += CtiNumStr(rates[i]);
        schedule += ', ';
    }
    schedule += CtiNumStr(rates[10]);
}


}
}
