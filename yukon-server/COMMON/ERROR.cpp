#include "precompiled.h"

#include "dsm2err.h"
#include "yukon.h"
#include "constants.h"

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

typedef std::map<int, error_info> ErrorLookup;

static const ErrorLookup CtiErrors = boost::assign::map_list_of<int, error_info>
    (NoError,       error_info(ERRTYPENONE,     "Normal (Success) Return"))
    (Error_Abnormal,error_info(ERRTYPESYSTEM,   "Not Normal (Unsuccessful) Return"))
    (BADTYPE,       error_info(ERRTYPEPROTOCOL, "Bad Message Type"))
    (DLENGTH,       error_info(ERRTYPEPROTOCOL, "D Word Wrong length"))
    (BADID,         error_info(ERRTYPESYSTEM,   "Bad ID Specification"))
    (BADRANGE,      error_info(ERRTYPESYSTEM,   "Parameter out of Range"))
    (MISPARAM,      error_info(ERRTYPESYSTEM,   "Missing Parameter"))
    (SYNTAX,        error_info(ERRTYPESYSTEM,   "Syntax Error"))
    (BADSTATE,      error_info(ERRTYPESYSTEM,   "Bad State Specification"))
    (BADPARITY,     error_info(ERRTYPECOMM,     "Parity Error"))
    (BADCCU,        error_info(ERRTYPECOMM,     "Bad CCU Specification"))
    (NACK1,         error_info(ERRTYPEPROTOCOL, "Word 1 NACK"))
    (NACK2,         error_info(ERRTYPEPROTOCOL, "Word 2 NACK"))
    (NACK3,         error_info(ERRTYPEPROTOCOL, "Word 3 NACK"))
    (NACKPAD1,      error_info(ERRTYPEPROTOCOL, "Word 1 NACK Padded"))
    (NACKPAD2,      error_info(ERRTYPEPROTOCOL, "Word 2 NACK Padded"))
    (NACKPAD3,      error_info(ERRTYPEPROTOCOL, "Word 3 NACK Padded"))
    (BADPARAM,      error_info(ERRTYPESYSTEM,   "Bad Parameter"))
    (BADROUTE,      error_info(ERRTYPESYSTEM,   "Bad Route Specification"))
    (BADBUSS,       error_info(ERRTYPESYSTEM,   "Bad Bus Specification"))
    (READERR,       error_info(ERRTYPESYSTEM,   "Read Error"))
    (READTIMEOUT,   error_info(ERRTYPECOMM,     "Timeout Reading from Port"))
    (BADSEQUENCE,   error_info(ERRTYPECOMM,     "Sequence Reject Frame Received... Sequencing Adjusted"))
    (FRAMEERR,      error_info(ERRTYPECOMM,     "Framing Error"))
    (BADCRC,        error_info(ERRTYPECOMM,     "Bad CRC on Message"))
    (BADLENGTH,     error_info(ERRTYPESYSTEM,   "Bad Length Specification"))
    (BADUA,         error_info(ERRTYPECOMM,     "Bad HDLC UA Frame"))
    (ERRUNKNOWN,    error_info(ERRTYPESYSTEM,   "Unknown Error"))
    (REQACK,        error_info(ERRTYPECOMM,     "REQACK Flag set-- Frame Unexecutable"))
    (RTNF,          error_info(ERRTYPESYSTEM,   "Route Not Found"))
    (IDNF,          error_info(ERRTYPESYSTEM,   "ID Not Found"))
    (TYNF,          error_info(ERRTYPESYSTEM,   "Function and/or Type Not Found"))
    (EWORDRCV,      error_info(ERRTYPEPROTOCOL, "E-Word Received in Returned Message"))
    (SYSTEM,        error_info(ERRTYPESYSTEM,   "OS or System Error"))
    (BADPORT,       error_info(ERRTYPESYSTEM,   "Bad Port Specification"))
    (QUEUE_READ,    error_info(ERRTYPESYSTEM,   "Error Reading Queue"))
    (QUEUE_WRITE,   error_info(ERRTYPESYSTEM,   "Error Writing Queue"))
    (MEMORY,        error_info(ERRTYPESYSTEM,   "Error Allocating or Manipulating Memory"))
    (NODCD,         error_info(ERRTYPECOMM,     "No DCD on Return Message"))
    (PORTREAD,      error_info(ERRTYPECOMM,     "Error Reading from Port"))
    (PORTWRITE,     error_info(ERRTYPECOMM,     "Error Writing to Port"))
    (QUEUEEXEC,     error_info(ERRTYPEPROTOCOL, "Error Executing CCU Queue Entry"))
    (DLCTIMEOUT,    error_info(ERRTYPEPROTOCOL, "DLC Read Timeout on CCU Queue Entry"))
    (NOATTEMPT,     error_info(ERRTYPESYSTEM,   "No Attempt Made on CCU Queue Entry"))
    (ROUTEFAILED,   error_info(ERRTYPEPROTOCOL, "Route Failed on CCU Queue Entry"))
    (TRANSFAILED,   error_info(ERRTYPEPROTOCOL, "Transponder Communication Failed on CCU Queue Entry"))
    (REMOTEINHIBITED,   error_info(ERRTYPESYSTEM,   "Communications Attempted With Inhibited Remote"))
    (QUEUEFLUSHED,      error_info(ERRTYPESYSTEM,   "CCU Queue was Flushed... Entries Lost in Drain"))
    (PIPEWASBROKEN,     error_info(ERRTYPESYSTEM,   "Pipe Connect was Broken"))
    (PIPEOPEN,          error_info(ERRTYPESYSTEM,   "Pipe Not Opened"))
    (PORTINHIBITED,     error_info(ERRTYPESYSTEM,   "Communications Attempted Over Inhibited Port"))
    (ACCUMSNOTSUPPORTED,    error_info(ERRTYPESYSTEM,   "Device Does Not Support Accumulators"))
    (DEVICEINHIBITED,   error_info(ERRTYPESYSTEM,   "Operation Attempted on Inhibited Device"))
    (DIALUPERROR,       error_info(ERRTYPECOMM,     "Error Dialing Up Remote"))
    (WRONGADDRESS,      error_info(ERRTYPECOMM,     "Wrong Unique Address Received"))
    (TCPCONNECTERROR,   error_info(ERRTYPECOMM,     "Error Connecting to TCP socket"))
    (TCPWRITEERROR,     error_info(ERRTYPECOMM,     "Error Writing to TCP socket"))
    (TCPREADERROR,      error_info(ERRTYPECOMM,     "Error Reading from TCP socket"))
    (ADDRESSERROR,      error_info(ERRTYPESYSTEM,   "Address Does Not Match Expected Value"))
    (ALPHABUFFERERROR,  error_info(ERRTYPESYSTEM,   "Bad Data Buffer for IED"))
    (MISCONFIG,         error_info(ERRTYPESYSTEM,   "Missing Required Configuration Entry"))
    (BADSOCK,           error_info(ERRTYPESYSTEM,   "Bad Nexus Specification"))
    (SOCKWRITE,         error_info(ERRTYPESYSTEM,   "Error Writing to Nexus"))
    (BADBCH,            error_info(ERRTYPEPROTOCOL, "Bad BCH"))
    (MemoryError,                   error_info(ERRTYPESYSTEM,   "Memory Error"))
    (NoMethod,                      error_info(ERRTYPESYSTEM,   "No Method"))
    (NoGeneralScanMethod,           error_info(ERRTYPESYSTEM,   "No General Scan Method"))
    (NoIntegrityScanMethod,         error_info(ERRTYPESYSTEM,   "No Integrity Scan Method"))
    (NoAccumulatorScanMethod,       error_info(ERRTYPESYSTEM,   "No Accum Scan Method"))
    (NoProcessResultMethod,         error_info(ERRTYPESYSTEM,   "No Process Result Method"))
    (NoExecuteRequestMethod,        error_info(ERRTYPESYSTEM,   "No Exec. Req. Method"))
    (NoResultDecodeMethod,          error_info(ERRTYPESYSTEM,   "No Result Decode Method"))
    (NoErrorDecodeMethod,           error_info(ERRTYPESYSTEM,   "No ErrorDecode Method"))
    (NoHandShakeMethod,             error_info(ERRTYPESYSTEM,   "No Handshake Method"))
    (NoGenerateCmdMethod,           error_info(ERRTYPESYSTEM,   "No Generate Command Method"))
    (NoDecodeResponseMethod,        error_info(ERRTYPESYSTEM,   "No DecodeResponse Method"))
    (NoDataCopyMethod,              error_info(ERRTYPESYSTEM,   "No Data Copy Method"))
    (NoConfigData,                  error_info(ERRTYPESYSTEM,   "No Config Data Found"))
    (NoRouteGroupDevice,            error_info(ERRTYPESYSTEM,   "No Route for Group Dev."))
    (NoRoutesInMacro,               error_info(ERRTYPESYSTEM,   "No Routes for Macro Rte"))
    (RouteOffsetOutOfRange,         error_info(ERRTYPESYSTEM,   "Macro Offset does not exist in Macro Rte"))
    (SubRouteIsMacro,               error_info(ERRTYPESYSTEM,   "Macro Offset refers to a macro sub-rte"))
    (ControlInhibitedOnDevice,      error_info(ERRTYPESYSTEM,   "Device is control disabled"))
    (ControlInhibitedOnPoint,       error_info(ERRTYPESYSTEM,   "Point is control disabled"))
    (ErrRequestExpired,             error_info(ERRTYPESYSTEM,   "Requested operation expired due to time"))
    (ErrPortDialupConnect_Port,     error_info(ERRTYPECOMM,     "Dialup connection failed. Port in error"))
    (ErrPortDialupConnect_Device,   error_info(ERRTYPESYSTEM,   "Dialup connection failed. Device in error"))
    (ErrPortSimulated,              error_info(ERRTYPEPROTOCOL, "Port is simulated, no inbound data available"))
    (ErrPortEchoResponse,           error_info(ERRTYPECOMM,     "Port echoed request bytes"))
    (RETRY_SUBMITTED,               error_info(ERRTYPESYSTEM,   "Retry Resubmitted"))
    (SCAN_ERROR_DEVICE_INHIBITED,   error_info(ERRTYPESYSTEM,   "Scanned device is inhibited"))
    (SCAN_ERROR_GLOBAL_ADDRESS,     error_info(ERRTYPESYSTEM,   "Illegal scan of global device"))
    (SCAN_ERROR_DEVICE_WINDOW_CLOSED,error_info(ERRTYPESYSTEM,  "Device window is closed"))
    (ErrorPageRS,                   error_info(ERRTYPESYSTEM,   "Invalid transaction, typ. bad pager id or password"))
    (ErrorPageNAK,                  error_info(ERRTYPESYSTEM,   "TAP Repeat Requested, but retries exhausted"))
    (ErrorPageNoResponse,           error_info(ERRTYPESYSTEM,   "No response from TAP terminal"))
    (UnknownError,                  error_info(ERRTYPESYSTEM,   "Unknown Error"))
    (ErrorHttpResponse,             error_info(ERRTYPESYSTEM,   "Invalid or unsuccessful HTTP response"))
    (ErrorXMLParser,                error_info(ERRTYPESYSTEM,   "XML parser initialization failed"))
    (ErrorWctpResponse,             error_info(ERRTYPESYSTEM,   "Invalid WCTP response format"))
    (ErrorWctpTimeout,              error_info(ERRTYPESYSTEM,   "Time out when receiving WCTP response"))
    (ErrorWctp300Series,            error_info(ERRTYPESYSTEM,   "Protocol Error 300 Series"))
    (ErrorWctp400Series,            error_info(ERRTYPESYSTEM,   "Protocol Error 400 Series"))
    (ErrorWctp500Series,            error_info(ERRTYPESYSTEM,   "Protocol Error 500 Series"))
    (ErrorWctp600Series,            error_info(ERRTYPESYSTEM,   "Protocol Error 600 Series"))
    (ErrorQueuePurged,              error_info(ERRTYPESYSTEM,   "Queue purged to limit memory usage"))
    (ErrorRequestCancelled,         error_info(ERRTYPESYSTEM,   "Request was cancelled"))
    (ErrorInvalidTimestamp,         error_info(ERRTYPESYSTEM,   "Invalid time returned OR time outside of requested range."))
    (ErrorInvalidChannel,           error_info(ERRTYPESYSTEM,   "Invalid channel returned by daily read."))
    (ErrorMACSTimeout,              error_info(ERRTYPESYSTEM,   "MACS timed out on this message"))
    (ErrorInvalidFrozenReadingParity,error_info(ERRTYPEPROTOCOL, "The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device."))
    (ErrorInvalidFrozenPeakTimestamp,error_info(ERRTYPEPROTOCOL, "The frozen peak timestamp is outside of the expected range."))
    (ErrorInvalidFreezeCounter,     error_info(ERRTYPEPROTOCOL, "The freeze counter is less than the expected value."))
    (ErrorInvalidData,              error_info(ERRTYPEPROTOCOL, "Invalid data was was received for one or more data points."))
    (ErrorFreezeNotRecorded,        error_info(ERRTYPEPROTOCOL, "There is no record of the last freeze sent to this device."))
    (ErrorInvalidRequest,           error_info(ERRTYPESYSTEM,   "Invalid/Incomplete Request"))
    (ErrorInvalidSSPEC,             error_info(ERRTYPESYSTEM,   "Insufficient SSPEC/Firmware Revision"))
    (ErrorVerifySSPEC,              error_info(ERRTYPESYSTEM,   "Verify SSPEC/Firmware Revision"))
    (ErrorUnsupportedDevice,        error_info(ERRTYPESYSTEM,   "Device Not Supported"))
    (ErrorPortNotInitialized,       error_info(ERRTYPECOMM,     "Port not initialized"))
    (ErrorCommandAlreadyInProgress, error_info(ERRTYPESYSTEM,   "Command already in progress"))
    (ErrorDeviceNotConnected,       error_info(ERRTYPESYSTEM,   "Device is not connected"))
    (ErrorNoDisconnect,             error_info(ERRTYPESYSTEM,   "No disconnect configured on this device"))
    (ErrorTransmitterOverheating,   error_info(ERRTYPEPROTOCOL, "Transmitter is overheating"))
    (ErrorNeedsChannelConfig,       error_info(ERRTYPESYSTEM,   "Command needs channel config to continue."))
    (ErrorInvalidStartDate,         error_info(ERRTYPESYSTEM,   "Command requires a valid date."))
    (ErrorDnsLookupFailed,          error_info(ERRTYPESYSTEM,   "Failed to resolve an IP for the given DNS name."))
    (ErrorPointLookupFailed,        error_info(ERRTYPESYSTEM,   "Failed to find a point for the given device."))
    (ErrorNeedsDateRangeReset,      error_info(ERRTYPESYSTEM,   "The device's date range must be reset to continue."))
    (ErrorDataMissing,              error_info(ERRTYPESYSTEM,   "Not enough data received from the device."))
    (ErrorInvalidConfigData,        error_info(ERRTYPESYSTEM,   "Configuration data is invalid."))
    (ErrorUnknownAddress,             error_info(ERRTYPESYSTEM, "Device address is unknown."))
    (ErrorNetworkUnavailable,         error_info(ERRTYPESYSTEM, "Network is unavailable."))
    (ErrorRequestPacketTooLarge,      error_info(ERRTYPESYSTEM, "Request packet is too large."))
    (ErrorProtocolUnsupported,        error_info(ERRTYPESYSTEM, "Protocol is unsupported."))
    (ErrorInvalidNetworkServerId,     error_info(ERRTYPESYSTEM, "Network server ID is invalid."))
    (ErrorInvalidApplicationServiceId,error_info(ERRTYPESYSTEM, "Application service ID is invalid."))
    (ErrorNetworkLoadControl,         error_info(ERRTYPESYSTEM, "Network traffic limiting."))
    (ErrorRequestTimeout,             error_info(ERRTYPESYSTEM, "Did not receive a response from the device."))
    (ErrorNetworkManagerTimeout,      error_info(ERRTYPESYSTEM, "Did not receive a response from Network Manager."))
    (ErrorNoPointsOnDevice,           error_info(ERRTYPESYSTEM, "The device has no points attached."))
    ;
}


//  Returns the error's description
IM_EX_CTIBASE std::string GetErrorString(int errorNumber)
{
    ErrorLookup::const_iterator itr = CtiErrors.find(errorNumber);

    if( itr != CtiErrors.end() )
    {
        return itr->second.description;
    }

    return "Unknown Error";
}


//  Returns the error's type
IM_EX_CTIBASE ErrorTypes GetErrorType(int errorNumber)
{
    ErrorLookup::const_iterator itr = CtiErrors.find(errorNumber);

    if( itr != CtiErrors.end() )
    {
        return itr->second.type;
    }

    return ERRTYPESYSTEM;
}

