#include "precompiled.h"

#include "dsm2err.h"
#include "yukon.h"
#include "constants.h"
#include "numstr.h"

#include <boost/assign/list_of.hpp>

//  keep these definitions local to this file
namespace {

struct error_info {
    ErrorTypes type;
    std::string description;

    error_info(ErrorTypes type_, std::string desc_) :
        type(type_), description(desc_)
    {}
};

typedef std::map<YukonError_t, error_info> ErrorLookup;

static const ErrorLookup CtiErrors = boost::assign::map_list_of
    (ClientErrors::None,                        error_info(ERRTYPENONE,     "Normal (Success) Return"))
    (ClientErrors::Abnormal,                    error_info(ERRTYPESYSTEM,   "Not Normal (Unsuccessful) Return"))
    (ClientErrors::NoTransmitterForRoute,       error_info(ERRTYPESYSTEM,   "Route has no associated transmitter"))
    (ClientErrors::BadType,                     error_info(ERRTYPEPROTOCOL, "Bad Message Type"))
    (ClientErrors::DLength,                     error_info(ERRTYPEPROTOCOL, "D Word Wrong length"))
    (ClientErrors::BadId,                       error_info(ERRTYPESYSTEM,   "Bad ID Specification"))
    (ClientErrors::BadRange,                    error_info(ERRTYPESYSTEM,   "Parameter out of Range"))
    (ClientErrors::MissingParameter,            error_info(ERRTYPESYSTEM,   "Missing Parameter"))
    (ClientErrors::Syntax,                      error_info(ERRTYPESYSTEM,   "Syntax Error"))
    (ClientErrors::BadState,                    error_info(ERRTYPESYSTEM,   "Bad State Specification"))
    (ClientErrors::BadParity,                   error_info(ERRTYPECOMM,     "Parity Error"))
    (ClientErrors::BadCcu,                      error_info(ERRTYPECOMM,     "Bad CCU Specification"))
    (ClientErrors::Word1Nack,                   error_info(ERRTYPEPROTOCOL, "Word 1 NACK"))
    (ClientErrors::Word2Nack,                   error_info(ERRTYPEPROTOCOL, "Word 2 NACK"))
    (ClientErrors::Word3Nack,                   error_info(ERRTYPEPROTOCOL, "Word 3 NACK"))
    (ClientErrors::Word1NackPadded,             error_info(ERRTYPEPROTOCOL, "Word 1 NACK Padded"))
    (ClientErrors::Word2NackPadded,             error_info(ERRTYPEPROTOCOL, "Word 2 NACK Padded"))
    (ClientErrors::Word3NackPadded,             error_info(ERRTYPEPROTOCOL, "Word 3 NACK Padded"))
    (ClientErrors::BadParameter,                error_info(ERRTYPESYSTEM,   "Bad Parameter"))
    (ClientErrors::BadRoute,                    error_info(ERRTYPESYSTEM,   "Bad Route Specification"))
    (ClientErrors::BadBusSpecification,         error_info(ERRTYPESYSTEM,   "Bad Bus Specification"))
    (ClientErrors::Read,                        error_info(ERRTYPESYSTEM,   "Read Error"))
    (ClientErrors::ReadTimeout,                 error_info(ERRTYPECOMM,     "Timeout Reading from Port"))
    (ClientErrors::BadSequence,                 error_info(ERRTYPECOMM,     "Sequence Reject Frame Received... Sequencing Adjusted"))
    (ClientErrors::Framing,                     error_info(ERRTYPECOMM,     "Framing Error"))
    (ClientErrors::BadCrc,                      error_info(ERRTYPECOMM,     "Bad CRC on Message"))
    (ClientErrors::BadLength,                   error_info(ERRTYPESYSTEM,   "Bad Length Specification"))
    (ClientErrors::BadHdlcUaFrame,              error_info(ERRTYPECOMM,     "Bad HDLC UA Frame"))
    (ClientErrors::Unknown,                     error_info(ERRTYPESYSTEM,   "Unknown Error"))
    (ClientErrors::ReqackFlagSet,               error_info(ERRTYPECOMM,     "REQACK Flag set-- Frame Unexecutable"))
    (ClientErrors::RouteNotFound,               error_info(ERRTYPESYSTEM,   "Route Not Found"))
    (ClientErrors::IdNotFound,                  error_info(ERRTYPESYSTEM,   "ID Not Found"))
    (ClientErrors::TypeNotFound,                error_info(ERRTYPESYSTEM,   "Function and/or Type Not Found"))
    (ClientErrors::EWordReceived,               error_info(ERRTYPEPROTOCOL, "E-Word Received in Returned Message"))
    (ClientErrors::SystemRelated,               error_info(ERRTYPESYSTEM,   "OS or System Error"))
    (ClientErrors::BadPort,                     error_info(ERRTYPESYSTEM,   "Bad Port Specification"))
    (ClientErrors::QueueRead,                   error_info(ERRTYPESYSTEM,   "Error Reading Queue"))
    (ClientErrors::QueueWrite,                  error_info(ERRTYPESYSTEM,   "Error Writing Queue"))
    (ClientErrors::MemoryAccess,                error_info(ERRTYPESYSTEM,   "Error Allocating or Manipulating Memory"))
    (ClientErrors::NoDcd,                       error_info(ERRTYPECOMM,     "No DCD on Return Message"))
    (ClientErrors::PortRead,                    error_info(ERRTYPECOMM,     "Error Reading from Port"))
    (ClientErrors::PortWrite,                   error_info(ERRTYPECOMM,     "Error Writing to Port"))
    (ClientErrors::QueueExec,                   error_info(ERRTYPEPROTOCOL, "Error Executing CCU Queue Entry"))
    (ClientErrors::DlcTimeout,                  error_info(ERRTYPEPROTOCOL, "DLC Read Timeout on CCU Queue Entry"))
    (ClientErrors::NoAttempt,                   error_info(ERRTYPESYSTEM,   "No Attempt Made on CCU Queue Entry"))
    (ClientErrors::RouteFailed,                 error_info(ERRTYPEPROTOCOL, "Route Failed on CCU Queue Entry"))
    (ClientErrors::TransmitterFailed,           error_info(ERRTYPEPROTOCOL, "Transponder Communication Failed on CCU Queue Entry"))
    (ClientErrors::RemoteInhibited,             error_info(ERRTYPESYSTEM,   "Communications Attempted With Inhibited Remote"))
    (ClientErrors::CcuQueueFlushed,             error_info(ERRTYPESYSTEM,   "CCU Queue was Flushed... Entries Lost in Drain"))
    (ClientErrors::PipeBroken,                  error_info(ERRTYPESYSTEM,   "Pipe Connect was Broken"))
    (ClientErrors::PipeNotOpened,               error_info(ERRTYPESYSTEM,   "Pipe Not Opened"))
    (ClientErrors::PortInhibited,               error_info(ERRTYPESYSTEM,   "Communications Attempted Over Inhibited Port"))
    (ClientErrors::AccumulatorsNotSupported,    error_info(ERRTYPESYSTEM,   "Device Does Not Support Accumulators"))
    (ClientErrors::DeviceInhibited,             error_info(ERRTYPESYSTEM,   "Operation Attempted on Inhibited Device"))
    (ClientErrors::DialupFailed,                error_info(ERRTYPECOMM,     "Error Dialing Up Remote"))
    (ClientErrors::WrongAddress,                error_info(ERRTYPECOMM,     "Wrong Unique Address Received"))
    (ClientErrors::TcpConnect,                  error_info(ERRTYPECOMM,     "Error Connecting to TCP socket"))
    (ClientErrors::TcpWrite,                    error_info(ERRTYPECOMM,     "Error Writing to TCP socket"))
    (ClientErrors::TcpRead,                     error_info(ERRTYPECOMM,     "Error Reading from TCP socket"))
    (ClientErrors::Address,                     error_info(ERRTYPESYSTEM,   "Address Does Not Match Expected Value"))
    (ClientErrors::IedBufferBad,                error_info(ERRTYPESYSTEM,   "Bad Data Buffer for IED"))
    (ClientErrors::MissingConfig,               error_info(ERRTYPESYSTEM,   "Missing Required Configuration Entry"))
    (ClientErrors::BadSocket,                   error_info(ERRTYPESYSTEM,   "Bad Nexus Specification"))
    (ClientErrors::SocketWrite,                 error_info(ERRTYPESYSTEM,   "Error Writing to Nexus"))
    (ClientErrors::BadBch,                      error_info(ERRTYPEPROTOCOL, "Bad BCH"))
    (ClientErrors::Memory,                      error_info(ERRTYPESYSTEM,   "Memory Error"))
    (ClientErrors::NoMethod,                    error_info(ERRTYPESYSTEM,   "No Method"))
    (ClientErrors::NoMethodForGeneralScan,      error_info(ERRTYPESYSTEM,   "No General Scan Method"))
    (ClientErrors::NoMethodForIntegrityScan,    error_info(ERRTYPESYSTEM,   "No Integrity Scan Method"))
    (ClientErrors::NoMethodForAccumulatorScan,  error_info(ERRTYPESYSTEM,   "No Accum Scan Method"))
    (ClientErrors::NoMethodForProcessResult,    error_info(ERRTYPESYSTEM,   "No Process Result Method"))
    (ClientErrors::NoMethodForExecuteRequest,   error_info(ERRTYPESYSTEM,   "No Exec. Req. Method"))
    (ClientErrors::NoMethodForResultDecode,     error_info(ERRTYPESYSTEM,   "No Result Decode Method"))
    (ClientErrors::NoMethodForErrorDecode,      error_info(ERRTYPESYSTEM,   "No ErrorDecode Method"))
    (ClientErrors::NoMethodForHandshake,        error_info(ERRTYPESYSTEM,   "No Handshake Method"))
    (ClientErrors::NoMethodForGenerateCmd,      error_info(ERRTYPESYSTEM,   "No Generate Command Method"))
    (ClientErrors::NoMethodForDecodeResponse,   error_info(ERRTYPESYSTEM,   "No DecodeResponse Method"))
    (ClientErrors::NoMethodForDataCopy,         error_info(ERRTYPESYSTEM,   "No Data Copy Method"))
    (ClientErrors::NoConfigData,                error_info(ERRTYPESYSTEM,   "No Config Data Found"))
    (ClientErrors::NoRouteGroupDevice,          error_info(ERRTYPESYSTEM,   "No Route for Group Dev."))
    (ClientErrors::NoRoutesInMacro,             error_info(ERRTYPESYSTEM,   "No Routes for Macro Rte"))
    (ClientErrors::RouteOffsetOutOfRange,       error_info(ERRTYPESYSTEM,   "Macro Offset does not exist in Macro Rte"))
    (ClientErrors::SubRouteIsMacro,             error_info(ERRTYPESYSTEM,   "Macro Offset refers to a macro sub-rte"))
    (ClientErrors::ControlInhibitedOnDevice,    error_info(ERRTYPESYSTEM,   "Device is control disabled"))
    (ClientErrors::ControlInhibitedOnPoint,     error_info(ERRTYPESYSTEM,   "Point is control disabled"))
    (ClientErrors::RequestExpired,              error_info(ERRTYPESYSTEM,   "Requested operation expired due to time"))
    (ClientErrors::DialupConnectPort,           error_info(ERRTYPECOMM,     "Dialup connection failed. Port in error"))
    (ClientErrors::DialupConnectDevice,         error_info(ERRTYPESYSTEM,   "Dialup connection failed. Device in error"))
    (ClientErrors::PortSimulated,               error_info(ERRTYPEPROTOCOL, "Port is simulated, no inbound data available"))
    (ClientErrors::PortEchoedResponse,          error_info(ERRTYPECOMM,     "Port echoed request bytes"))
    (ClientErrors::RetrySubmitted,              error_info(ERRTYPESYSTEM,   "Retry Resubmitted"))
    (ClientErrors::ScanDeviceInhibited,         error_info(ERRTYPESYSTEM,   "Scanned device is inhibited"))
    (ClientErrors::ScanGlobalAddress,           error_info(ERRTYPESYSTEM,   "Illegal scan of global device"))
    (ClientErrors::ScanWindowClosed,            error_info(ERRTYPESYSTEM,   "Device window is closed"))
    (ClientErrors::PageRS,                      error_info(ERRTYPESYSTEM,   "Invalid transaction, typ. bad pager id or password"))
    (ClientErrors::PageNAK,                     error_info(ERRTYPESYSTEM,   "TAP Repeat Requested, but retries exhausted"))
    (ClientErrors::PageNoResponse,              error_info(ERRTYPESYSTEM,   "No response from TAP terminal"))
    (ClientErrors::WctpHttpResponse,            error_info(ERRTYPESYSTEM,   "Invalid or unsuccessful HTTP response"))
    (ClientErrors::WctpXmlParser,               error_info(ERRTYPESYSTEM,   "XML parser initialization failed"))
    (ClientErrors::WctpResponse,                error_info(ERRTYPESYSTEM,   "Invalid WCTP response format"))
    (ClientErrors::WctpTimeout,                 error_info(ERRTYPESYSTEM,   "Time out when receiving WCTP response"))
    (ClientErrors::Wctp300Series,               error_info(ERRTYPESYSTEM,   "Protocol Error 300 Series"))
    (ClientErrors::Wctp400Series,               error_info(ERRTYPESYSTEM,   "Protocol Error 400 Series"))
    (ClientErrors::Wctp500Series,               error_info(ERRTYPESYSTEM,   "Protocol Error 500 Series"))
    (ClientErrors::Wctp600Series,               error_info(ERRTYPESYSTEM,   "Protocol Error 600 Series"))
    (ClientErrors::QueuePurged,                 error_info(ERRTYPESYSTEM,   "Queue purged to limit memory usage"))
    (ClientErrors::RequestCancelled,            error_info(ERRTYPESYSTEM,   "Request was cancelled"))
    (ClientErrors::InvalidTimestamp,            error_info(ERRTYPESYSTEM,   "Invalid time returned OR time outside of requested range."))
    (ClientErrors::InvalidChannel,              error_info(ERRTYPESYSTEM,   "Invalid channel returned by daily read."))
    (ClientErrors::MacsTimeout,                 error_info(ERRTYPESYSTEM,   "MACS timed out on this message"))
    (ClientErrors::InvalidFrozenReadingParity,  error_info(ERRTYPEPROTOCOL, "The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device."))
    (ClientErrors::InvalidFrozenPeakTimestamp,  error_info(ERRTYPEPROTOCOL, "The frozen peak timestamp is outside of the expected range."))
    (ClientErrors::InvalidFreezeCounter,        error_info(ERRTYPEPROTOCOL, "The freeze counter is less than the expected value."))
    (ClientErrors::InvalidData,                 error_info(ERRTYPEPROTOCOL, "Invalid data was was received for one or more data points."))
    (ClientErrors::FreezeNotRecorded,           error_info(ERRTYPEPROTOCOL, "There is no record of the last freeze sent to this device."))
    (ClientErrors::InvalidRequest,              error_info(ERRTYPESYSTEM,   "Invalid/Incomplete Request"))
    (ClientErrors::InvalidSSPEC,                error_info(ERRTYPESYSTEM,   "Insufficient SSPEC/Firmware Revision"))
    (ClientErrors::VerifySSPEC,                 error_info(ERRTYPESYSTEM,   "Verify SSPEC/Firmware Revision"))
    (ClientErrors::UnsupportedDevice,           error_info(ERRTYPESYSTEM,   "Device Not Supported"))
    (ClientErrors::PortNotInitialized,          error_info(ERRTYPECOMM,     "Port not initialized"))
    (ClientErrors::CommandAlreadyInProgress,    error_info(ERRTYPESYSTEM,   "Command already in progress"))
    (ClientErrors::DeviceNotConnected,          error_info(ERRTYPESYSTEM,   "Device is not connected"))
    (ClientErrors::NoDisconnect,                error_info(ERRTYPESYSTEM,   "No disconnect configured on this device"))
    (ClientErrors::TransmitterOverheating,      error_info(ERRTYPEPROTOCOL, "Transmitter is overheating"))
    (ClientErrors::NeedsChannelConfig,          error_info(ERRTYPESYSTEM,   "Command needs channel config to continue."))
    (ClientErrors::DnsLookupFailed,             error_info(ERRTYPESYSTEM,   "Failed to resolve an IP for the given DNS name."))
    (ClientErrors::PointLookupFailed,           error_info(ERRTYPESYSTEM,   "Failed to find a point for the given device."))
    (ClientErrors::NeedsDateRangeReset,         error_info(ERRTYPESYSTEM,   "The device's date range must be reset to continue."))
    (ClientErrors::DataMissing,                 error_info(ERRTYPESYSTEM,   "Not enough data received from the device."))
    (ClientErrors::InvalidConfigData,           error_info(ERRTYPESYSTEM,   "Configuration data is invalid."))
    (ClientErrors::E2eUnknownAddress,           error_info(ERRTYPESYSTEM,   "Device address is unknown."))
    (ClientErrors::E2eNetworkUnavailable,       error_info(ERRTYPESYSTEM,   "Network is unavailable."))
    (ClientErrors::E2eRequestPacketTooLarge,    error_info(ERRTYPESYSTEM,   "Request packet is too large."))
    (ClientErrors::E2eProtocolUnsupported,      error_info(ERRTYPESYSTEM,   "Protocol is unsupported."))
    (ClientErrors::E2eInvalidNetworkServerId,   error_info(ERRTYPESYSTEM,   "Network server ID is invalid."))
    (ClientErrors::E2eInvalidApplicationServiceId,  error_info(ERRTYPESYSTEM, "Application service ID is invalid."))
    (ClientErrors::E2eNetworkLoadControl,       error_info(ERRTYPESYSTEM,   "Network traffic limiting."))
    (ClientErrors::E2eRequestTimeout,           error_info(ERRTYPESYSTEM,   "Did not receive a response from the device."))
    (ClientErrors::NetworkManagerTimeout,       error_info(ERRTYPESYSTEM,   "Did not receive a response from Network Manager."))
    (ClientErrors::NoPointsOnDevice,            error_info(ERRTYPESYSTEM,   "The device has no points attached."))
    ;
}


//  Returns the error's description
IM_EX_CTIBASE std::string GetErrorString(YukonError_t errorNumber)
{
    ErrorLookup::const_iterator itr = CtiErrors.find(errorNumber);

    if( itr != CtiErrors.end() )
    {
        return itr->second.description;
    }

    return "Unknown Error Code (" + CtiNumStr(errorNumber) + ")";
}


//  Returns the error's type
IM_EX_CTIBASE ErrorTypes GetErrorType(YukonError_t errorNumber)
{
    ErrorLookup::const_iterator itr = CtiErrors.find(errorNumber);

    if( itr != CtiErrors.end() )
    {
        return itr->second.type;
    }

    return ERRTYPESYSTEM;
}

