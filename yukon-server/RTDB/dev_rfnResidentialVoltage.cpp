#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "config_helpers.h"
#include "dev_rfnResidentialVoltage.h"
#include "devicetypes.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_inserter.hpp>

#include <limits>


namespace Cti       {
namespace Devices   {

namespace   // anonymous
{

Commands::RfnOvUvConfigurationCommand::MeterID getMeterIdForDeviceType( const int deviceType )
{
    static const std::map<int, Commands::RfnOvUvConfigurationCommand::MeterID> DeviceTypeToMeterId
    {
        { TYPE_RFN410CL,  Commands::RfnOvUvConfigurationCommand::CentronC1SX },
        { TYPE_RFN420CL,  Commands::RfnOvUvConfigurationCommand::CentronC2SX },
        { TYPE_RFN420CD,  Commands::RfnOvUvConfigurationCommand::CentronC2SX },
        { TYPE_RFN410FX,  Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN410FD,  Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN420FX,  Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN420FD,  Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN420FRX, Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN420FRD, Commands::RfnOvUvConfigurationCommand::LGFocusAX },
        { TYPE_RFN420FL,  Commands::RfnOvUvConfigurationCommand::LGFocusAL }
    };

    if ( auto meterId = mapFind( DeviceTypeToMeterId, deviceType ) )
    {
        return *meterId;
    }

    return Commands::RfnOvUvConfigurationCommand::Unspecified;
}

}   // anonymous


RfnMeterDevice::ConfigMap RfnResidentialVoltageDevice::getConfigMethods(bool readOnly)
{
    ConfigMap m = RfnResidentialDevice::getConfigMethods( readOnly );

    if( readOnly )
    {
        boost::assign::insert( m )
            ( ConfigPart::voltageaveraging, bindConfigMethod( &RfnResidentialVoltageDevice::executeGetConfigVoltageAveragingInterval, this ) )
            ( ConfigPart::ovuv,             bindConfigMethod( &RfnResidentialVoltageDevice::executeGetConfigOvUv,                     this ) )
                ;
    }
    else
    {
        boost::assign::insert( m )
            ( ConfigPart::voltageaveraging, bindConfigMethod( &RfnResidentialVoltageDevice::executePutConfigVoltageAveragingInterval, this ) )
            ( ConfigPart::ovuv,             bindConfigMethod( &RfnResidentialVoltageDevice::executePutConfigOvUv,                     this ) )
                ;
    }

    return m;
}


/// Voltage Averaging Interval

YukonError_t RfnResidentialVoltageDevice::executeGetConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                                                    CtiCommandParser  & parse,
                                                                                    ReturnMsgList     & returnMsgs,
                                                                                    RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnVoltageProfileGetConfigurationCommand>() );

    return ClientErrors::None;
}

YukonError_t RfnResidentialVoltageDevice::executePutConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                                                    CtiCommandParser  & parse,
                                                                                    ReturnMsgList     & returnMsgs,
                                                                                    RfnCommandList    & rfnRequests )
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    struct IntervalSettings
    {
        boost::optional<unsigned>   voltageAveragingInterval,
                                    loadProfileInterval;

        bool operator==( const IntervalSettings & rhs ) const
        {
            return voltageAveragingInterval == rhs.voltageAveragingInterval && loadProfileInterval == rhs.loadProfileInterval;
        }
    }
    configSettings,
    paoSettings;

    {
        const std::string             configKey( Config::RfnStrings::voltageAveragingInterval );
        const boost::optional<long>   configValue = deviceConfig->findValue<long>( configKey );

        if ( ! configValue  )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing value for config key \""<< configKey <<"\"");
            return ClientErrors::NoConfigData;
        }

        if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned>::max() )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\"");
            return ClientErrors::InvalidConfigData;
        }

        configSettings.voltageAveragingInterval = static_cast<unsigned>( *configValue );
    }

    {
        const std::string           configKey( Config::RfnStrings::profileInterval );
        const boost::optional<long> configValue = deviceConfig->findValue<long>( configKey );

        if ( ! configValue  )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing value for config key \""<< configKey <<"\"");
            return ClientErrors::NoConfigData;
        }

        if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned>::max() )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\"");
            return ClientErrors::InvalidConfigData;
        }

        configSettings.loadProfileInterval = static_cast<unsigned>( *configValue );
    }

    {
        double pao_value;

        if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageAveragingInterval, pao_value ) )
        {
            paoSettings.voltageAveragingInterval = pao_value;
        }
    }

    {
        long pao_value;

        if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, pao_value ) )
        {
            paoSettings.loadProfileInterval = static_cast<unsigned>( pao_value );
        }
    }

    if( configSettings == paoSettings )
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

    rfnRequests.push_back( boost::make_shared<Commands::RfnVoltageProfileSetConfigurationCommand>( *configSettings.voltageAveragingInterval,
                                                                                                   *configSettings.loadProfileInterval ) );
    return ClientErrors::None;
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnVoltageProfileGetConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageAveragingInterval, *cmd.getVoltageAveragingInterval() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval,      *cmd.getLoadProfileInterval() );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnVoltageProfileSetConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageAveragingInterval, cmd.voltageAveragingInterval );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval,      cmd.loadProfileInterval );
}

/// OvUv

YukonError_t RfnResidentialVoltageDevice::executeGetConfigOvUv( CtiRequestMsg    * pReq,
                                                                CtiCommandParser & parse,
                                                                ReturnMsgList    & returnMsgs,
                                                                RfnCommandList   & rfnRequests )
{
    // get the meter ID

    Commands::RfnOvUvConfigurationCommand::MeterID  meterID = getMeterIdForDeviceType(getType());

    rfnRequests.push_back( boost::make_shared<Commands::RfnGetOvUvAlarmConfigurationCommand>( meterID, Commands::RfnOvUvConfigurationCommand::OverVoltage ) );
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetOvUvAlarmConfigurationCommand>( meterID, Commands::RfnOvUvConfigurationCommand::UnderVoltage ) );

    return ClientErrors::None;
}

YukonError_t RfnResidentialVoltageDevice::executePutConfigOvUv( CtiRequestMsg    * pReq,
                                                                CtiCommandParser & parse,
                                                                ReturnMsgList    & returnMsgs,
                                                                RfnCommandList   & rfnRequests )
{
    using Commands::RfnOvUvConfigurationCommand;
    using Commands::RfnSetOvUvAlarmProcessingStateCommand;
    using Commands::RfnSetOvUvNewAlarmReportIntervalCommand;
    using Commands::RfnSetOvUvAlarmRepeatIntervalCommand;
    using Commands::RfnSetOvUvAlarmRepeatCountCommand;
    using Commands::RfnSetOvUvSetOverVoltageThresholdCommand;
    using Commands::RfnSetOvUvSetUnderVoltageThresholdCommand;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        {
            const bool configOvUvEnabled               = getConfigData   <bool> ( deviceConfig, Config::RfnStrings::OvUvEnabled );
            const boost::optional<bool> paoOvUvEnabled = findDynamicInfo <bool> ( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled );

            if( configOvUvEnabled != paoOvUvEnabled || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                RfnSetOvUvAlarmProcessingStateCommand::AlarmStates state = configOvUvEnabled
                      ? RfnSetOvUvAlarmProcessingStateCommand::EnableOvUv
                      : RfnSetOvUvAlarmProcessingStateCommand::DisableOvUv;

                rfnRequests.push_back( boost::make_shared<RfnSetOvUvAlarmProcessingStateCommand>( state ) );
            }
        }

        {
            const unsigned configOvUvReportingInterval               = getConfigData   <unsigned> ( deviceConfig, Config::RfnStrings::OvUvAlarmReportingInterval );
            const boost::optional<unsigned> paoOvUvReportingInterval = findDynamicInfo <unsigned> ( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval );

            if( configOvUvReportingInterval != paoOvUvReportingInterval || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back( boost::make_shared<RfnSetOvUvNewAlarmReportIntervalCommand>( configOvUvReportingInterval ) );
            }
        }

        {
             const unsigned configOvUvRepeatInterval               = getConfigData   <unsigned> ( deviceConfig, Config::RfnStrings::OvUvAlarmRepeatInterval );
             const boost::optional<unsigned> paoOvUvRepeatInterval = findDynamicInfo <unsigned> ( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval );

             if( configOvUvRepeatInterval != paoOvUvRepeatInterval || parse.isKeyValid("force") )
             {
                 if( parse.isKeyValid("verify") )
                 {
                     return ClientErrors::ConfigNotCurrent;
                 }

                 rfnRequests.push_back( boost::make_shared<RfnSetOvUvAlarmRepeatIntervalCommand>( configOvUvRepeatInterval ) );
             }
        }

        {
            const unsigned configOvUvRepeatCount               = getConfigData   <unsigned> ( deviceConfig, Config::RfnStrings::OvUvRepeatCount );
            const boost::optional<unsigned> paoOvUvRepeatCount = findDynamicInfo <unsigned> ( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount );

            if( configOvUvRepeatCount != paoOvUvRepeatCount || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back( boost::make_shared<RfnSetOvUvAlarmRepeatCountCommand>( configOvUvRepeatCount ) );
            }
        }

        // get the meter ID
        const RfnOvUvConfigurationCommand::MeterID  meterID = getMeterIdForDeviceType(getType());

        {
            const double configOvThreshold               = getConfigData   <double> ( deviceConfig, Config::RfnStrings::OvThreshold );
            const boost::optional<double> paoOvThreshold = findDynamicInfo <double> ( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold );

            if( configOvThreshold != paoOvThreshold || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back( boost::make_shared<RfnSetOvUvSetOverVoltageThresholdCommand>( meterID, configOvThreshold ) );
            }
        }

        {
            const double configUvThreshold               = getConfigData   <double> ( deviceConfig, Config::RfnStrings::UvThreshold );
            const boost::optional<double> paoUvThreshold = findDynamicInfo <double> ( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold );

            if( configUvThreshold != paoUvThreshold || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back( boost::make_shared<RfnSetOvUvSetUnderVoltageThresholdCommand>( meterID, configUvThreshold ) );
            }
        }

        if( ! parse.isKeyValid("force") && rfnRequests.size() == 0 )
        {
            return ClientErrors::ConfigCurrent;
        }

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::InvalidConfigData;
    }
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnGetOvUvAlarmConfigurationCommand & cmd )
{
    Commands::RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration   config = cmd.getAlarmConfiguration();

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,   config.ovuvEnabled );

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, config.ovuvAlarmReportingInterval );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,    config.ovuvAlarmRepeatInterval );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,            config.ovuvAlarmRepeatCount );

    if ( config.ovThreshold )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, *config.ovThreshold );
    }
    else
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold, *config.uvThreshold );
    }
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvAlarmProcessingStateCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,
                    cmd.alarmState == Commands::RfnSetOvUvAlarmProcessingStateCommand::EnableOvUv );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatCountCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount, cmd.repeatCount );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvAlarmRepeatIntervalCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval, cmd.repeatInterval );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvNewAlarmReportIntervalCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, cmd.reportingInterval );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvSetOverVoltageThresholdCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, cmd.ovThresholdValue );
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnSetOvUvSetUnderVoltageThresholdCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold, cmd.uvThresholdValue );
}


}   // Devices
}   // Cti

