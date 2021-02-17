#include "precompiled.h"

#include "dev_mct420.h"
#include "config_device.h"
#include "config_data_mct.h"

#include "dev_mct420_commands.h"
#include "devicetypes.h"

#include <boost/assign/list_of.hpp>

#include <iostream>

using namespace Cti::Devices::Commands;
using namespace Cti::Protocols;
using namespace Cti::Config;
using std::string;
using std::endl;
using std::list;
using std::vector;
using std::make_pair;

namespace Cti::Devices {

        const Mct420Device::CommandSet       Mct420Device::_commandStore = {
            {CommandStore(EmetconProtocol::GetConfig_Multiplier, EmetconProtocol::IO_Function_Read, 0xf3, 2)},
            {CommandStore(EmetconProtocol::GetConfig_MeterParameters, EmetconProtocol::IO_Function_Read, 0xf3, 2)},
            {CommandStore(EmetconProtocol::GetConfig_DailyReadInterest, EmetconProtocol::IO_Function_Read, 0x1e, 11)},
            {CommandStore(EmetconProtocol::GetConfig_Options, EmetconProtocol::IO_Function_Read, 0x01, 6)},
            {CommandStore(EmetconProtocol::PutConfig_Channel2NetMetering, EmetconProtocol::IO_Write, 0x85, 0) }
        };

        const Mct420Device::FunctionReadValueMappings Mct420Device::_readValueMaps = {
            {0x000,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision)},
                    {1, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_SSpec)}
                }
            },
            {0x04f,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay)}
                }
            },
            {0x005,
                {
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1)},
                    {6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2)},
                    {7, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask)}
                }
            },
            {0x00f,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters)}
                }
            },
            {0x013,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_AddressBronze)},
                    {1, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_AddressLead)},
                    {3, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_AddressCollection)},
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID)}
                }
            },
            {0x019,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio)}
                }
            },
            {0x01a,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DemandInterval)},
                    {1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval)},
                    {2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval)},
                    {3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval)}
                }
            },
            {0x01e,
                {
                    {0, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold)},
                    {2, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold)},
                    {4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles)}
                }
            },
            {0x022,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles)}
                }
            },
            {0x036,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance)}
                }
            },
            {0x03f,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset)}
                }
            },
            {0x0d0,
                {
                    {0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday1)},
                    {4, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2)},
                    {8, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)}
                }
            },
            {0x0d4,
                {
                    {0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2)},
                    {4, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)}
                }
            },
            {0x0d8,
                {
                    {0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)}
                }
            },
            {0x101,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Configuration)},
                    {1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1)},
                    {2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2)},
                    {3, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask)},
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Options)}
                }
            },
            {0x19d,
                {
                    {4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len)},
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len)},
                    {6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len)},
                    {7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len)}
                }
            },
            {0x1ad,
                {
                    {0, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_DayTable)},
                    {2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate)},
                    {10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset)}
                }
            },
            {0x1f3,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters)},
                    {1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio)}
                }
            },
            {0x1f6,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01)},
                    {1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02)},
                    {2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03)},
                    {3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04)},
                    {4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05)},
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06)},
                    {6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07)},
                    {7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08)},
                    {8, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09)},
                    {9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10)},
                    {10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11)},
                    {11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12)},
                    {12, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13)}
                }
            },
            {0x1f7,
                {
                    {0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14)},
                    {1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15)},
                    {2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16)},
                    {3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17)},
                    {4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18)},
                    {5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19)},
                    {6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20)},
                    {7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21)},
                    {8, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22)},
                    {9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23)},
                    {10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24)},
                    {11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25)},
                    {12, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26)}
                }
            },
            { 0x1fe,
                {
                    //{ 5, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold)}  //  stored by Mct410DisconnectConfigurationCommand
                    {7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay)},
                    {9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes)},
                    {10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes)},
                    {11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Configuration)}
                }
            }
        };


const Mct420Device::FlagSet Mct420Device::_eventFlags = boost::assign::list_of
    (make_pair(0x0001, "Zero usage"))
    (make_pair(0x0002, "Disconnect error"))
    (make_pair(0x0004, "Meter reading corrupted"))
    (make_pair(0x0008, "Reverse power"))
    // 0x0010-0x0080 unused
    (make_pair(0x0100, "Power fail event"))
    (make_pair(0x0200, "Under voltage event"))
    (make_pair(0x0400, "Over voltage event"))
    (make_pair(0x0800, "RTC lost"))
    (make_pair(0x1000, "RTC adjusted"))
    (make_pair(0x2000, "Holiday flag"))
    (make_pair(0x4000, "DST change"))
    (make_pair(0x8000, "Tamper flag"));


const Mct420Device::FlagSet Mct420Device::_meterAlarmFlags = boost::assign::list_of
    (make_pair(0x0001, "Unprogrammed"))
    (make_pair(0x0002, "Configuration error"))
    (make_pair(0x0004, "Self check error"))
    (make_pair(0x0008, "RAM failure"))
    // 0x0010 unsupported
    (make_pair(0x0020, "Non-volatile memory failure"))
    // 0x0040 unsupported
    (make_pair(0x0080, "Measurement error"))
    // 0x0100-0x0400 and 0x100-0x800 unsupported
    (make_pair(0x0800, "Power failure"));


const Mct420Device::ReadDescriptor Mct420Device::getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const
{
    unsigned read = function;

    switch( io )
    {
        case EmetconProtocol::IO_Function_Read:
        {
            read += 0x100;
            //  fall through
        }
        case EmetconProtocol::IO_Read:
        {
            if( const FunctionReadValueMappings *rm = getReadValueMaps() )
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


bool Mct420Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getType() == TYPEMCT420CL )
    {
        switch( cmd )
        {
            //  The MCT-420CL does not support the disconnect collar
            case EmetconProtocol::Control_Connect:
            case EmetconProtocol::Control_Disconnect:
                return false;
        }
    }

    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


const Mct420Device::FunctionReadValueMappings *Mct420Device::getReadValueMaps(void) const
{
    return &_readValueMaps;
}


Mct420Device::ConfigPartsList Mct420Device::getPartsList()
{
    ConfigPartsList partsList;

    if( isSupported(Feature_Display) )
    {
        partsList.push_back(PutConfigPart_display);
    }

    partsList.push_back(PutConfigPart_meter_parameters);

    if( isSupported(Feature_Disconnect) )
    {
        partsList.push_back(PutConfigPart_disconnect);
    }

    partsList.push_back(PutConfigPart_freeze_day);
    partsList.push_back(PutConfigPart_timezone);

    return partsList;
}


unsigned Mct420Device::getUsageReportDelay(const unsigned interval_length, const unsigned days) const
{
    const int fixed_delay    = gConfigParms.getValueAsInt("PORTER_MCT_PEAK_REPORT_DELAY", 10);

    //  Calculates at least 36 days of usage no matter how many days are requested
    const unsigned intervals = std::max(days, 36U) * intervalsPerDay(interval_length);

    const unsigned variable_delay = (intervals * 10 + 999) / 1000;  //  10 ms per interval, rounded up to the next second

    return fixed_delay + variable_delay;
}


bool Mct420Device::isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const
{
    const unsigned long intervals_since_epoch = TimeNow.seconds() / interval_len;

    //  truncating on purpose
    const unsigned char expected_table_pointer = intervals_since_epoch / 6 - 1;

    return table_pointer == expected_table_pointer;
}


YukonError_t Mct420Device::executePutConfig( CtiRequestMsg     *pReq,
                                             CtiCommandParser  &parse,
                                             OUTMESS          *&OutMessage,
                                             CtiMessageList    &vgList,
                                             CtiMessageList    &retList,
                                             OutMessageList    &outList )
{
    //  Load all the other stuff that is needed
    OutMessage->TargetID  = getID();
    OutMessage->Retry     = 2;

    OutMessage->Request.RouteID   = getRouteID();
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    //  Disallow the MCT-410 version of "putconfig meter parameters" - see re_mct410_meter_parameters in cmdparse.cpp
    if( parse.isKeyValid("display_resolution") &&
        parse.isKeyValid("display_test_duration") )
    {
        return ClientErrors::NoMethod;
    }
    if( parse.isKeyValid("lcd_cycle_time") )
    {
        OutMessage->Sequence = EmetconProtocol::PutConfig_Parameters;

        // These two are here for sure.
        const unsigned cycleTime = parse.getiValue("lcd_cycle_time");
        bool disconnectDisplayDisabled = parse.isKeyValid("disconnect_display_disabled");

        // Optionals
        boost::optional<std::string> displayDigitsStr;
        boost::optional<unsigned char> paoInfoValue;
        boost::optional<unsigned> transformerRatio;

        if( parse.isKeyValid("transformer_ratio") )
        {
            transformerRatio = parse.getiValue("transformer_ratio");
        }

        if( ! isSupported(Feature_LcdDisplayDigitConfiguration) )
        {
            if( parse.isKeyValid("lcd display digits") )
            {
                insertReturnMsg(ClientErrors::InvalidSSPEC, OutMessage, retList,
                                   "LCD display digits not supported for this device's SSPEC");

                return ExecutionComplete;
            }

            auto meterParameterConfiguration =
                    std::make_unique<Mct420MeterParametersCommand>(
                            cycleTime,
                            disconnectDisplayDisabled,
                            transformerRatio);

            return tryExecuteCommand(*OutMessage, std::move(meterParameterConfiguration))
                       ? ClientErrors::None
                       : ClientErrors::NoMethod;
        }
        else
        {
            if( parse.isKeyValid("lcd display digits") )
            {
                displayDigitsStr = parse.getsValue("lcd display digits");
            }
            else if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters) )
            {
                //  mask out just the three bits
                paoInfoValue = 0x70 & getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters);
            }

            auto meterParameterConfiguration =
                    std::make_unique<Mct420MeterParametersDisplayDigitsCommand>(
                            cycleTime,
                            disconnectDisplayDisabled,
                            transformerRatio,
                            displayDigitsStr,
                            paoInfoValue);

            return tryExecuteCommand(*OutMessage, std::move(meterParameterConfiguration))
                       ? ClientErrors::None
                       : ClientErrors::NoMethod;
        }
    }
    else if( parse.isKeyValid("channel_2_configuration") )
    {
        OutMessage->Sequence = EmetconProtocol::PutConfig_Channel2NetMetering;
        bool found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

        string optionsString = parse.getsValue("channel_2_configuration_setting");

        if( optionsString == "none" )
        {
            // We need to increment the function to disable net metering.
            OutMessage->Buffer.BSt.Function += 1;
        }

        if( !found )
        {
            return ClientErrors::NoMethod;
        }

        return ClientErrors::None;
    }
    else
    {
        return Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }
}


YukonError_t Mct420Device::executePutConfigDisplay(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    vector<unsigned char> display_metrics;

    if( ! readsOnly )
    {
        using dpi = CtiTableDynamicPaoInfo;

        static const std::map<PaoInfoKeys, std::string> dynamicConfigKeys {
            { dpi::Key_MCT_LcdMetric01, MCTStrings::displayItem01 },
            { dpi::Key_MCT_LcdMetric02, MCTStrings::displayItem02 },
            { dpi::Key_MCT_LcdMetric03, MCTStrings::displayItem03 },
            { dpi::Key_MCT_LcdMetric04, MCTStrings::displayItem04 },
            { dpi::Key_MCT_LcdMetric05, MCTStrings::displayItem05 },
            { dpi::Key_MCT_LcdMetric06, MCTStrings::displayItem06 },
            { dpi::Key_MCT_LcdMetric07, MCTStrings::displayItem07 },
            { dpi::Key_MCT_LcdMetric08, MCTStrings::displayItem08 },
            { dpi::Key_MCT_LcdMetric09, MCTStrings::displayItem09 },
            { dpi::Key_MCT_LcdMetric10, MCTStrings::displayItem10 },
            { dpi::Key_MCT_LcdMetric11, MCTStrings::displayItem11 },
            { dpi::Key_MCT_LcdMetric12, MCTStrings::displayItem12 },
            { dpi::Key_MCT_LcdMetric13, MCTStrings::displayItem13 },
            { dpi::Key_MCT_LcdMetric14, MCTStrings::displayItem14 },
            { dpi::Key_MCT_LcdMetric15, MCTStrings::displayItem15 },
            { dpi::Key_MCT_LcdMetric16, MCTStrings::displayItem16 },
            { dpi::Key_MCT_LcdMetric17, MCTStrings::displayItem17 },
            { dpi::Key_MCT_LcdMetric18, MCTStrings::displayItem18 },
            { dpi::Key_MCT_LcdMetric19, MCTStrings::displayItem19 },
            { dpi::Key_MCT_LcdMetric20, MCTStrings::displayItem20 },
            { dpi::Key_MCT_LcdMetric21, MCTStrings::displayItem21 },
            { dpi::Key_MCT_LcdMetric22, MCTStrings::displayItem22 },
            { dpi::Key_MCT_LcdMetric23, MCTStrings::displayItem23 },
            { dpi::Key_MCT_LcdMetric24, MCTStrings::displayItem24 },
            { dpi::Key_MCT_LcdMetric25, MCTStrings::displayItem25 },
            { dpi::Key_MCT_LcdMetric26, MCTStrings::displayItem26 } };

        vector<unsigned char> paoinfo_metrics;

        // Look through the deviceConfig for each possible dynamicConfigKeys 
        for( const auto paoConfigKey : dynamicConfigKeys )
        {
            // pao_key=dpi::Key_MCT_LcdMetricXX, config_key=MCTStrings::displayItemXX
            const PaoInfoKeys pao_key    = paoConfigKey.first;
            const std::string config_key = paoConfigKey.second;

            const auto config_value = deviceConfig->findValue<uint8_t>(config_key, Cti::Config::displayItemMap);

            if ( ! config_value )
            {
                // Oops, not found.  
                CTILOG_ERROR(dout, "Device \"" << getName() << "\" - invalid value (" << deviceConfig->getValueFromKey(config_key) << ") for config key \"" << config_key << "\"");
                return ClientErrors::NoConfigData;
            }

            display_metrics.push_back(*config_value);

            long pao_value = 0xff;

            //  will not touch pao_value if it's not found
            getDynamicInfo(pao_key, pao_value);

            paoinfo_metrics.push_back(pao_value);
        }

        if( display_metrics.size() == paoinfo_metrics.size() && std::equal(display_metrics.begin(), display_metrics.end(), paoinfo_metrics.begin()) )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }
    }

    auto lcdConfiguration = std::make_unique<Mct420LcdConfigurationCommand>(display_metrics, readsOnly);

    auto om = std::make_unique<OUTMESS>(*OutMessage);

    if( ! tryExecuteCommand(*om, std::move(lcdConfiguration)) )
    {
        return ClientErrors::NoMethod;
    }

    outList.push_back(om.release());

    return ClientErrors::None;
}

YukonError_t Mct420Device::executePutConfigMeterParameters(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
    if( !deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    DlcCommandPtr meterParameterCommand;

    if( ! readsOnly )
    {
        long paoInfo_displayParams, paoInfo_cycleTime;
        bool paoInfo_displayDisabled;
        boost::optional<unsigned char> display_digits, paoInfo_displayDigits;
        boost::optional<unsigned> transformerRatio;

        bool displayDigitsSupported = isSupported(Feature_LcdDisplayDigitConfiguration);

        const boost::optional<long>
            cycleTime = deviceConfig->findValue<long>(MCTStrings::LcdCycleTime);

        if( ! cycleTime )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - invalid value ("
                    << deviceConfig->getValueFromKey(MCTStrings::LcdCycleTime) <<") for config key \""
                    << MCTStrings::LcdCycleTime <<"\""
                    );

            return ClientErrors::NoConfigData;
        }

        if( displayDigitsSupported )
        {
            const boost::optional<long>
                numDigits = deviceConfig->findValue<long>(MCTStrings::DisplayDigits);

            if( ! numDigits )
            {
                if( getMCTDebugLevel(DebugLevel_Configs) )
                {
                    CTILOG_DEBUG(dout, "Device \""<< getName() <<"\" - invalid value ("
                            << deviceConfig->getValueFromKey(MCTStrings::DisplayDigits) <<") for config key \""
                            << MCTStrings::DisplayDigits <<"\""
                            );
                }

                return ClientErrors::NoConfigData;
            }

            /*
             * Lookups for keys other than 4, 5, or 6 will return a default value of 0, which
             * is exactly what we want for the default value anyway.
             */
            std::map<long, unsigned> digitLookup = boost::assign::map_list_of (4, 0x20)(5, 0x00)(6, 0x10);

            display_digits = digitLookup[*numDigits];
        }
        else
        {
            display_digits = 0x00;
        }

        const boost::optional<bool> disconnectDisplayDisabled = deviceConfig->findValue<bool>(MCTStrings::DisconnectDisplayDisabled);

        if( ! disconnectDisplayDisabled )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - invalid value ("
                 << deviceConfig->getValueFromKey(MCTStrings::DisconnectDisplayDisabled) <<") for config key \""
                 << MCTStrings::DisconnectDisplayDisabled << "\""
                 );

            return ClientErrors::NoConfigData;
        }

        // Grab the transformer ratio value if possible.
        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio) )
        {
            transformerRatio = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio);
        }

        // Get DynamicPaoInfo for verification.
        getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters, paoInfo_displayParams);

        paoInfo_displayDisabled = paoInfo_displayParams & 0x80;
        paoInfo_displayDigits   = paoInfo_displayParams & 0x70;
        paoInfo_cycleTime       = paoInfo_displayParams & 0x0f;

        if( paoInfo_displayDisabled == disconnectDisplayDisabled && paoInfo_cycleTime == cycleTime )
        {
            if( displayDigitsSupported )
            {
                if( paoInfo_displayDigits == display_digits )
                {
                    if( ! parse.isKeyValid("force") )
                    {
                        return ClientErrors::ConfigCurrent;
                    }
                }
                else if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }
            }
            else if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }

        meterParameterCommand =
                std::make_unique<Mct420MeterParametersDisplayDigitsCommand>(
                        *cycleTime,
                        *disconnectDisplayDisabled,
                        transformerRatio,
                        boost::none,
                        display_digits);
    }
    else
    {
        // Read command.
        meterParameterCommand =
                std::make_unique<Mct420MeterParametersDisplayDigitsCommand>();
    }

    auto om = std::make_unique<OUTMESS>(*OutMessage);

    if( ! tryExecuteCommand(*om, std::move(meterParameterCommand)) )
    {
        return ClientErrors::NoMethod;
    }

    outList.push_back(om.release());

    return ClientErrors::None;
}

YukonError_t Mct420Device::executeGetConfig( CtiRequestMsg     *pReq,
                                             CtiCommandParser  &parse,
                                             OUTMESS          *&OutMessage,
                                             CtiMessageList    &vgList,
                                             CtiMessageList    &retList,
                                             OutMessageList    &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;
    bool found = false;

    //  Explicitly disallow this command for the MCT-420
    if( parse.isKeyValid("centron_parameters") )
    {
        return ClientErrors::NoMethod;
    }
    else if( parse.isKeyValid("configuration") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Options;
        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else
    {
        return Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}


DlcBaseDevice::DlcCommandPtr Mct420Device::makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const
{
    return std::make_unique<Mct420HourlyReadCommand>(date_begin, date_end, channel);
}


/**
 * Calls the corresponding decode method for the function stored
 * in InMessage.Sequence. All EmetconProtocol commands
 * supported by the MCT-420 that aren't supported by a parent of
 * the MCT-420 must be represented in this function. Virtual
 * decode calls will be made from the parent ModelDecode calls,
 * so any decode that is supported by a parent class should be
 * omitted from this function.
 */
YukonError_t Mct420Device::ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    switch(InMessage.Sequence)
    {
        case EmetconProtocol::GetConfig_Options:
        {
            return decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
        }

        case EmetconProtocol::PutConfig_Channel2NetMetering:
        {
            return decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    const YukonError_t status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

    if( status )
    {
        CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
    }

    return status;
}

YukonError_t Mct420Device::decodePutConfig( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;
    string resultString;

    switch ( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_Channel2NetMetering:
        {
            status = decodePutConfigChannel2NetMetering(InMessage, TimeNow, vgList, retList, outList);
            resultString = getName( ) + " / channel 2 configuration sent";
            break;
        }

        default:
        {
            return Inherited::decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    // Handle the return message.
    {
        std::unique_ptr<CtiReturnMsg> ReturnMsg(new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

        ReturnMsg->setUserMessageId( InMessage.Return.UserID );
        ReturnMsg->setResultString ( resultString );

        decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

        if( InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection)!=0 )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );
    }

    return status;
}


YukonError_t Mct420Device::decodePutConfigChannel2NetMetering( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    // Execute a read to get the configuration data to be stored in dynamic pao info.
    {
        std::unique_ptr<CtiRequestMsg> pReq(
            new CtiRequestMsg(
                InMessage.TargetID,
                "getconfig configuration",
                InMessage.Return.UserID,
                InMessage.Return.GrpMsgID,
                InMessage.Return.RouteID,
                selectInitialMacroRouteOffset(InMessage.Return.RouteID),
                InMessage.Return.Attempt));

        if( strstr(InMessage.Return.CommandStr, "noqueue") )
        {
            pReq->setCommandString(pReq->CommandString() + " noqueue");
        }

        CtiCommandParser parse(pReq->CommandString());

        beginExecuteRequest(pReq.release(), parse, vgList, retList, outList);
    }

    return ClientErrors::None;
}


string Mct420Device::decodeDisconnectStatus(const DSTRUCT &DSt) const
{
    string resultStr;

    //  and adds load side voltage detection as well
    if( DSt.Message[0] & 0x40 )
    {
        resultStr += "Load side voltage detected\n";
    }

    //  The MCT-420 supports all of the MCT-410's statuses
    resultStr += Mct410Device::decodeDisconnectStatus(DSt);

    return resultStr;
}


YukonError_t Mct420Device::decodeGetConfigMeterParameters(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    string resultString = getName() + " / Meter Parameters:\n";

    resultString += "Disconnect display ";
    resultString += (DSt->Message[0] & 0x80) ? "disabled\n" : "enabled\n";

    if( isSupported(Feature_LcdDisplayDigitConfiguration) )
    {
        switch( DSt->Message[0] & 0x70 )
        {
            case 0x00:  resultString += "Display digits: 5x1\n";  break;
            case 0x10:  resultString += "Display digits: 6x1\n";  break;
            case 0x20:  resultString += "Display digits: 4x1\n";  break;
            default:    resultString += "Display digits: unknown\n";  break;
        }
    }

    resultString += "LCD cycle time: ";

    if( const unsigned lcd_cycle_time = DSt->Message[0] & 0x0f )
    {
        resultString += CtiNumStr(lcd_cycle_time) + " seconds\n";
    }
    else
    {
        resultString += "8 seconds (meter default)\n";
    }

    resultString += "Transformer ratio: " + CtiNumStr(DSt->Message[1]) + "\n";

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg, vgList, retList );

    return ClientErrors::None;
}


YukonError_t Mct420Device::decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    const unsigned revision = DSt.Message[0];
    const unsigned sspec    = DSt.Message[1] << 8 |
                              DSt.Message[2];
    string descriptor;

    descriptor += getName() + " / Model information:\n";
    descriptor += "Software Specification " + CtiNumStr(sspec) + " rev ";

    //  convert 10 to 1.0, 24 to 2.4
    descriptor += CtiNumStr(((double)revision) / 10.0, 1);

    descriptor += "\n";

    descriptor += getName() + " / Physical meter configuration:\n";
    descriptor += "Base meter: ";

    switch( sspec % 10 )
    {
        case 0x00:  descriptor += "Itron Centron C2SX";  break;
        default:    descriptor += "(unknown)";
    }

    descriptor += "\n";

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg, vgList, retList, InMessage.MessageFlags & MessageFlag_ExpectMore );

    return ClientErrors::None;
}

YukonError_t Mct420Device::decodeGetConfigOptions( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    const unsigned configuration  = DSt.Message[0];
    const unsigned eventMask      = DSt.Message[1] << 8 | DSt.Message[2];
    const unsigned meterAlarmMask = DSt.Message[3] << 8 | DSt.Message[4];
    const unsigned options        = DSt.Message[5];

    string descriptor;

    descriptor += getName() + " / Configuration information:\n";

    // Configuration description
    descriptor += configuration & 0x01 ? "DST enabled\n"
                                       : "DST disabled\n";
    descriptor += configuration & 0x02 ? "LED test enabled\n"
                                       : "LED test disabled\n";
    descriptor += configuration & 0x04 ? "Reconnect button not required\n"
                                       : "Reconnect button required\n";
    descriptor += configuration & 0x08 ? "Demand limit mode enabled\n"
                                       : "Demand limit mode disabled\n";
    descriptor += configuration & 0x10 ? "Disconnect cycling mode enabled\n"
                                       : "Disconnect cycling mode disabled\n";
    descriptor += configuration & 0x20 ? "Repeater role enabled\n"
                                       : "Repeater role disabled\n";
    descriptor += configuration & 0x40 ? "Disconnect collar is MCT-410d Rev E (or later)\n"
                                       : "Disconnect collar is not MCT-410d Rev E (or later)\n";
    descriptor += configuration & 0x80 ? "Daily reporting enabled\n"
                                       : "Daily reporting disabled\n";

    // Event mask descriptions
    descriptor += getName() + " / Event mask information:\n";

    for each (const Mct420Device::FlagMask &flag in _eventFlags)
    {
        descriptor += flag.second + " event mask " + string(eventMask & flag.first ? "en" : "dis") + "abled\n";
    }

    // Meter alarm mask descriptions
    descriptor += getName() + " / Meter alarm mask information:\n";

    for each (const Mct420Device::FlagMask &flag in _meterAlarmFlags)
    {
        descriptor += flag.second + " meter alarm mask " + string(eventMask & flag.first ? "en" : "dis") + "abled\n";
    }

    // Options description
    descriptor += getName() + " / Channel configuration:\n";

    if( (options & 0x1c) == 0x1c )
    {
        descriptor += "Channel 2: Net metering mode enabled\n";
    }
    else
    {
        descriptor += "Channel 2: No meter attached\n";
    }

    if( !(options & 0x60) )
    {
        descriptor += "Channel 3: No meter attached\n";
    }

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg, vgList, retList, InMessage.MessageFlags & MessageFlag_ExpectMore );

    return ClientErrors::None;
}


YukonError_t Mct420Device::decodeGetConfigDailyReadInterest(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(
        new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned interest_day     =   DSt.Message[9];
    const unsigned interest_month   =  (DSt.Message[10] & 0x0f) + 1;
    const unsigned interest_channel = ((DSt.Message[10] & 0x30) >> 4) + 1;

    string resultString;

    resultString  = getName() + " / Daily read interest channel: " + CtiNumStr(interest_channel) + "\n";
    resultString += getName() + " / Daily read interest month: "   + CtiNumStr(interest_month) + "\n";
    resultString += getName() + " / Daily read interest day: "     + CtiNumStr(interest_day) + "\n";

    tryVerifyDailyReadInterestDate(interest_day, interest_month, TimeNow);

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList );

    return ClientErrors::None;
}


Mct420Device::frozen_point_info Mct420Device::decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter)
{
    return Mct4xxDevice::decodePulseAccumulator(buf, len, freeze_counter);
}


Mct420Device::frozen_point_info Mct420Device::getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    frozen_point_info pi = Mct420Device::decodePulseAccumulator(buf, len, freeze_counter);

    if( freeze_counter )
    {
        //  The MCT-410's logic assumes that the reading was stored by the previous freeze counter.
        pi.freeze_bit = ! (*freeze_counter & 0x01);
    }

    return pi;
}


Mct420Device::frozen_point_info Mct420Device::getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    frozen_point_info pi = Mct410Device::getData(buf, len, ValueType_DynamicDemand);

    if( freeze_counter )
    {
        //  The MCT-410's logic assumes that the reading was stored by the previous freeze counter.
        pi.freeze_bit = ! (*freeze_counter & 0x01);
    }

    return pi;
}


bool Mct420Device::sspecValid(const unsigned sspec, const unsigned rev) const
{
    //  next-gen SSPEC is 10290-10299, split per meter type
    return (sspec / 10 * 10) == Mct420Device::Sspec;
}


bool Mct420Device::isSupported(const Mct420Device::Features feature) const
{
    switch( feature )
    {
        case Feature_LcdDisplayDigitConfiguration:
        {
            return sspecAtLeast(SspecRev_LcdDisplayDigitConfiguration);
        }
        case Feature_Display:
        {
            switch( getDeviceType() )
            {
                case TYPEMCT420CD:
                case TYPEMCT420CL:
                {
                    return true;
                }
                default:
                {
                    return false;
                }
            }
        }
    }

    return false;
}


bool Mct420Device::isSupported(const Mct410Device::Features feature) const
{
    switch( feature )
    {
        case Feature_OutageUnits:
        case Feature_HourlyKwh:
        {
            return true;
        }
        case Feature_DisconnectCollar:
        {
            //  this is the only MCT-420 that supports the collar
            return getType() == TYPEMCT420FL;
        }
        case Feature_Disconnect:
        {
            switch( getType() )
            {
                case TYPEMCT420CD:
                case TYPEMCT420FL:
                case TYPEMCT420FD:
                {
                    return true;
                }
                default:
                {
                    return false;
                }
            }
        }
    }

    return Mct410Device::isSupported(feature);
}


}