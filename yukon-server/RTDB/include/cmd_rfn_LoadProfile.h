#pragma once

#include "cmd_rfn_Individual.h"
#include "ctidate.h"


namespace Cti        {
namespace Devices    {
namespace Commands   {


/**
 * Load Profile Command Base Class
 */
class IM_EX_DEVDB RfnLoadProfileCommand : public RfnTwoWayCommand
{
protected:

    enum
    {
        SecondsPerMinute = 60
    };

    enum CommandCode
    {
        CommandCode_Request                     = 0x68,
        CommandCode_Response                    = 0x69
    };

    enum Operation
    {
        Operation_SetConfiguration                    = 0x00,
        Operation_GetConfiguration                    = 0x01,
        Operation_DisableLoadProfileRecording         = 0x02,
        Operation_EnableTemporaryLoadProfileRecording = 0x03,
        Operation_GetLoadProfileRecordingState        = 0x04,
        Operation_GetLoadProfilePoints                = 0x05,
        Operation_EnablePermanentLoadProfileRecording = 0x06
    };

    enum TlvType
    {
        TlvType_VoltageProfileConfiguration         = 0x01,
        TlvType_LoadProfileState                    = 0x02,
        TlvType_GetProfilePointsResponse            = 0x03,
        TlvType_GetProfilePointsRequest             = 0x04,
        TlvType_PermanentLoadProfileRecordingState  = 0x05,
        TlvType_TemporaryLoadProfileRecordingState  = 0x06
    };

    RfnLoadProfileCommand( const Operation operation );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getCommandData();

    typedef std::vector<TypeLengthValue> TlvList;

    virtual TlvList getTlvs();

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now,
                                           const RfnResponsePayload & response );

    TlvList getTlvsFromPayload( const RfnResponsePayload & response );

    static LongTlvList longTlvs;

public:

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response ) = 0;
};


/**
 * Voltage Profile Configuration Base Class
 */
class IM_EX_DEVDB RfnVoltageProfileConfigurationCommand : public RfnLoadProfileCommand
{
public:

    struct Read
    {
        virtual boost::optional<unsigned> getVoltageAveragingInterval() const = 0;
        virtual boost::optional<unsigned> getLoadProfileInterval()      const = 0;
    };

    enum
    {
        SecondsPerIncrement = 15
    };

protected:

    RfnVoltageProfileConfigurationCommand( const Operation operation );
};


/**
 * Voltage Profile Get Configuration
 */
class IM_EX_DEVDB RfnVoltageProfileGetConfigurationCommand : public RfnVoltageProfileConfigurationCommand, public RfnVoltageProfileConfigurationCommand::Read,
       InvokerFor<RfnVoltageProfileGetConfigurationCommand>
{
public:

    RfnVoltageProfileGetConfigurationCommand();

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    boost::optional<unsigned> getVoltageAveragingInterval() const override;
    boost::optional<unsigned> getLoadProfileInterval()      const override;

private:

    boost::optional<unsigned> _voltageAveragingInterval,
                              _loadProfileInterval;
};


/**
 * Voltage Profile Set Configuration
 */
class IM_EX_DEVDB RfnVoltageProfileSetConfigurationCommand : public RfnVoltageProfileConfigurationCommand,
       InvokerFor<RfnVoltageProfileSetConfigurationCommand>
{
public:

    RfnVoltageProfileSetConfigurationCommand( const unsigned voltage_averaging_interval_seconds,
                                              const unsigned load_profile_interval_minutes );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    const unsigned voltageAveragingInterval,    // seconds
                   loadProfileInterval;         // minutes

protected:

    virtual TlvList getTlvs();
};


/**
 * Recording State Base Class
 */
class IM_EX_DEVDB RfnLoadProfileRecordingCommand : public RfnLoadProfileCommand
{
public:

    enum RecordingOption
    {
        DisableRecording,
        EnableRecording
    };

protected:

    RfnLoadProfileRecordingCommand( const Operation operation );
};


/**
 * Load Profile Recording Get State
 */
class IM_EX_DEVDB RfnLoadProfileGetRecordingCommand : public RfnLoadProfileRecordingCommand,
       InvokerFor<RfnLoadProfileGetRecordingCommand>
{
public:

    RfnLoadProfileGetRecordingCommand();

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    boost::optional<RecordingOption> getRecordingOption() const;
    boost::optional<CtiTime>         getEndTime() const;

    bool isPermanentEnabled() const;
    bool isTemporaryEnabled() const;

private:

    boost::optional<RecordingOption> _option;
    boost::optional<CtiTime>         _endTime;

    bool _isPermanentEnabled,
         _isTemporaryEnabled;
};


/**
 * Load Profile Recording Set Temporary Recording State
 */
class IM_EX_DEVDB RfnLoadProfileSetTemporaryRecordingCommand : public RfnLoadProfileRecordingCommand,
       InvokerFor<RfnLoadProfileSetTemporaryRecordingCommand>
{
public:

    RfnLoadProfileSetTemporaryRecordingCommand( const RecordingOption option );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    const RecordingOption recordingOption;
};


/**
 * Load Profile Recording Set Permanent Recording State
 */
class IM_EX_DEVDB RfnLoadProfileSetPermanentRecordingCommand : public RfnLoadProfileRecordingCommand,
       InvokerFor<RfnLoadProfileSetPermanentRecordingCommand>
{
public:

    RfnLoadProfileSetPermanentRecordingCommand( const RecordingOption option );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    const RecordingOption recordingOption;
};


/**
 * Load Profile Read Points
 */
class IM_EX_DEVDB RfnLoadProfileReadPointsCommand : public RfnLoadProfileCommand, NoResultHandler
{
public:

    RfnLoadProfileReadPointsCommand( const CtiTime now,
                                     const CtiTime begin,
                                     const CtiTime end );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

protected:

    virtual TlvList getTlvs();

private:

    const CtiTime _begin;
    const CtiTime _end;

    UomModifier1  _uomModifier1;
    UomModifier2  _uomModifier2;
    unsigned      _profileInterval;

    unsigned decodePointsReportHeader( RfnCommandResult & result,
                                       const Bytes & lpPointDescriptor,
                                       unsigned & pointRecordCount );

    unsigned decodePointRecord( RfnCommandResult & result,
                                const Bytes & lpPointDescriptor,
                                const unsigned offset );
};


}
}
}

