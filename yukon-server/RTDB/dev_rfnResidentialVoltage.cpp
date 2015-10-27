#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "config_helpers.h"
#include "dev_rfnResidentialVoltage.h"
#include "devicetypes.h"
#include "CParms.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_inserter.hpp>

#include <limits>


namespace Cti       {
namespace Devices   {

namespace   // anonymous
{

/**
 * create CtiDate object from string "mm/dd/yyyy"
 */
CtiDate getDateFromString( std::string date )
{
    int month, day, year;
    char sep1, sep2;

    std::istringstream ss(date);
    ss >> month >> sep1 >> day >> sep2 >> year;

    return CtiDate(day, month, year);
}

unsigned getSecondsFromTimeString( std::string time )
{
    int hour, minute;
    char sep;

    std::istringstream ss(time);
    ss >> hour >> sep >> minute;

    return hour * 3600 + minute * 60;
}

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


bool RfnResidentialVoltageDevice::NmCompatibilityAtLeast( const double minimumVersion )
{
    return getNmCompatibilityVersion() >= minimumVersion;
}

double RfnResidentialVoltageDevice::getNmCompatibilityVersion() const
{
    return gConfigParms.getValueAsDouble( "NM_COMPATIBILITY" );
}

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

    if ( NmCompatibilityAtLeast( 7.0 ) )
    {
        if( readOnly )
        {
            boost::assign::insert( m )
                ( ConfigPart::voltageprofile,   bindConfigMethod( &RfnResidentialVoltageDevice::executeGetConfigPermanentVoltageProfile,  this ) )
                    ;
        }
        else
        {
            boost::assign::insert( m )
                ( ConfigPart::voltageprofile,   bindConfigMethod( &RfnResidentialVoltageDevice::executePutConfigPermanentVoltageProfile,  this ) )
                    ;
        }
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
        boost::optional<unsigned> voltageAveragingInterval,
                                  voltageDataStreamingInterval;

        bool operator==( const IntervalSettings & rhs ) const
        {
            return voltageAveragingInterval == rhs.voltageAveragingInterval && voltageDataStreamingInterval == rhs.voltageDataStreamingInterval;
        }
    }
    configSettings,
    paoSettings;

    try
    {
        configSettings.voltageAveragingInterval = getConfigData<unsigned>(deviceConfig, Config::RfnStrings::voltageAveragingInterval);
        configSettings.voltageDataStreamingInterval = getConfigData<unsigned>(deviceConfig, Config::RfnStrings::voltageDataStreamingIntervalMinutes);
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return ClientErrors::InvalidConfigData;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return ClientErrors::NoConfigData;
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
            paoSettings.voltageDataStreamingInterval = static_cast<unsigned>(pao_value);
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

    rfnRequests.push_back(
            boost::make_shared<Commands::RfnVoltageProfileSetConfigurationCommand>(
                    *configSettings.voltageAveragingInterval,
                    *configSettings.voltageDataStreamingInterval));

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

/// voltage profile

YukonError_t RfnResidentialVoltageDevice::executeGetConfigPermanentVoltageProfile( CtiRequestMsg    * pReq,
                                                                                   CtiCommandParser & parse,
                                                                                   ReturnMsgList    & returnMsgs,
                                                                                   RfnCommandList   & rfnRequests )
{
    using Commands::RfnLoadProfileGetRecordingCommand;

    rfnRequests.push_back( boost::make_shared<RfnLoadProfileGetRecordingCommand>());

    return ClientErrors::None;
}

YukonError_t RfnResidentialVoltageDevice::executePutConfigPermanentVoltageProfile( CtiRequestMsg    * pReq,
                                                                                   CtiCommandParser & parse,
                                                                                   ReturnMsgList    & returnMsgs,
                                                                                   RfnCommandList   & rfnRequests )
{
    using Commands::RfnLoadProfileRecordingCommand;
    using Commands::RfnLoadProfileSetPermanentRecordingCommand;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        {
            const bool                  configEnabled  = getConfigData  <bool>( deviceConfig, Config::RfnStrings::enableDataStreaming );
            const boost::optional<bool> paoInfoEnabled = findDynamicInfo<bool>( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled );

            if( configEnabled != paoInfoEnabled || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                RfnLoadProfileRecordingCommand::RecordingOption option =
                    configEnabled
                        ? RfnLoadProfileRecordingCommand::EnableRecording
                        : RfnLoadProfileRecordingCommand::DisableRecording;

                rfnRequests.push_back( boost::make_shared<RfnLoadProfileSetPermanentRecordingCommand>( option ) );
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

YukonError_t RfnResidentialVoltageDevice::executePutConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                                          CtiCommandParser  & parse,
                                                                          ReturnMsgList     & returnMsgs,
                                                                          RfnCommandList    & rfnRequests )
{
    // putconfig voltage profile enable|disable

    using Commands::RfnLoadProfileRecordingCommand;
    using Commands::RfnLoadProfileSetTemporaryRecordingCommand;

    //
    // enable|disable recording
    //

    if( parse.isKeyValid("voltage_profile_enable") )
    {
        RfnLoadProfileRecordingCommand::RecordingOption option = (parse.getiValue("voltage_profile_enable")) ? RfnLoadProfileRecordingCommand::EnableRecording
                                                                                                             : RfnLoadProfileRecordingCommand::DisableRecording;

        rfnRequests.push_back( boost::make_shared<RfnLoadProfileSetTemporaryRecordingCommand>( option ));

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnResidentialVoltageDevice::executeGetConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                                          CtiCommandParser  & parse,
                                                                          ReturnMsgList     & returnMsgs,
                                                                          RfnCommandList    & rfnRequests )
{
    // getconfig voltage profile state

    using Commands::RfnLoadProfileGetRecordingCommand;

    if( parse.isKeyValid("voltage_profile_state") )
    {
        rfnRequests.push_back( boost::make_shared<RfnLoadProfileGetRecordingCommand>());

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnResidentialVoltageDevice::executeGetValueVoltageProfile( CtiRequestMsg     * pReq,
                                                                         CtiCommandParser  & parse,
                                                                         ReturnMsgList     & returnMsgs,
                                                                         RfnCommandList    & rfnRequests )
{
    // getvalue voltage profile 12/13/2005 12/15/2005

    using Commands::RfnLoadProfileReadPointsCommand;

    //
    // date begin
    //

    CtiDate date_begin;

    if( const boost::optional<std::string> dateStr = parse.findStringForKey("read_points_date_begin") )
    {
        date_begin = getDateFromString( *dateStr );
    }
    else
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing voltage profile start date");

        return ClientErrors::MissingParameter;
    }

    CtiTime begin = date_begin;

    if( const boost::optional<std::string> timeStr = parse.findStringForKey("read_points_time_begin") )
    {
        begin += getSecondsFromTimeString(*timeStr);
    }

    //
    // date end
    //

    CtiTime end = date_begin + 1;  //  If no end date provided, default to end of the requested day

    if( const boost::optional<std::string> dateStr = parse.findStringForKey("read_points_date_end") )
    {
        CtiDate date_end = getDateFromString(*dateStr);

        if( const boost::optional<std::string> timeStr = parse.findStringForKey("read_points_time_end") )
        {
            end = date_end;
            end += getSecondsFromTimeString(*timeStr);
        }
        else
        {
            end = date_end + 1;
        }
    }

    rfnRequests.push_back( boost::make_shared<RfnLoadProfileReadPointsCommand>( CtiTime::now(),
                                                                                begin,
                                                                                end ));

    return ClientErrors::None;
}

void RfnResidentialVoltageDevice::handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand & cmd )
{
    if ( cmd.isPermanentEnabled() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled, true );
    }
    else if ( cmd.isTemporaryEnabled() )
    {
        if ( cmd.getEndTime() ) // temp enabled with timestamp
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil,
                            *cmd.getEndTime() );
        }
        else    // old school temp enabled
        {
            // do nothing
        }
    }
    else    // disabled
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled, false );

        purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil );
    }
}

void RfnResidentialVoltageDevice::handleCommandResult(const Commands::RfnLoadProfileSetTemporaryRecordingCommand & cmd)
{
    if( cmd.recordingOption == Commands::RfnLoadProfileRecordingCommand::EnableRecording )
    {
        setDynamicInfo(
            CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil,
            CtiTime::now().addDays(14));
    }
    else
    {
        purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil);
    }
}

void RfnResidentialVoltageDevice::handleCommandResult(const Commands::RfnLoadProfileSetPermanentRecordingCommand & cmd)
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled,
                    cmd.recordingOption == Commands::RfnLoadProfileRecordingCommand::EnableRecording );
}

}   // Devices
}   // Cti

