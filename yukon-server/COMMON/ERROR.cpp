#include "precompiled.h"

#include "dsm2err.h"
#include "yukon.h"
#include "constants.h"
#include "numstr.h"

#include "std_helper.h"

//  keep these definitions local to this file
namespace {

using error_info = std::tuple<ErrorTypes, const char *>;
using ei = error_info;

static const std::map<YukonError_t, error_info> CtiErrors = {
        { ClientErrors::None,                        ei{ ERRTYPENONE,     "Normal (Success) Return"                     } },
        { ClientErrors::Abnormal,                    ei{ ERRTYPESYSTEM,   "Not Normal (Unsuccessful) Return"            } },
        { ClientErrors::NoTransmitterForRoute,       ei{ ERRTYPESYSTEM,   "Route has no associated transmitter"         } },
        { ClientErrors::BadWordType,                 ei{ ERRTYPEPROTOCOL, "Bad Message Type"                            } },
        { ClientErrors::DLength,                     ei{ ERRTYPEPROTOCOL, "D Word Wrong length"                         } },
        { ClientErrors::BadId,                       ei{ ERRTYPESYSTEM,   "Bad ID Specification"                        } },
        { ClientErrors::BadRange,                    ei{ ERRTYPESYSTEM,   "Parameter out of Range"                      } },
        { ClientErrors::MissingParameter,            ei{ ERRTYPESYSTEM,   "Missing Parameter"                           } },
        { ClientErrors::Syntax,                      ei{ ERRTYPESYSTEM,   "Syntax Error"                                } },
        { ClientErrors::BadState,                    ei{ ERRTYPESYSTEM,   "Bad State Specification"                     } },
        { ClientErrors::BadParity,                   ei{ ERRTYPECOMM,     "Parity Error"                                } },
        { ClientErrors::BadCcu,                      ei{ ERRTYPECOMM,     "Bad CCU Specification"                       } },
        { ClientErrors::Word1Nack,                   ei{ ERRTYPEPROTOCOL, "Word 1 NACK"                                 } },
        { ClientErrors::Word2Nack,                   ei{ ERRTYPEPROTOCOL, "Word 2 NACK"                                 } },
        { ClientErrors::Word3Nack,                   ei{ ERRTYPEPROTOCOL, "Word 3 NACK"                                 } },
        { ClientErrors::Word1NackPadded,             ei{ ERRTYPEPROTOCOL, "Word 1 NACK Padded"                          } },
        { ClientErrors::Word2NackPadded,             ei{ ERRTYPEPROTOCOL, "Word 2 NACK Padded"                          } },
        { ClientErrors::Word3NackPadded,             ei{ ERRTYPEPROTOCOL, "Word 3 NACK Padded"                          } },
        { ClientErrors::BadParameter,                ei{ ERRTYPESYSTEM,   "Bad Parameter"                               } },
        { ClientErrors::BadRoute,                    ei{ ERRTYPESYSTEM,   "Bad Route Specification"                     } },
        { ClientErrors::BadBusSpecification,         ei{ ERRTYPESYSTEM,   "Bad Bus Specification"                       } },
        { ClientErrors::Read,                        ei{ ERRTYPESYSTEM,   "Read Error"                                  } },
        { ClientErrors::ReadTimeout,                 ei{ ERRTYPECOMM,     "Timeout Reading from Port"                   } },
        { ClientErrors::BadSequence,                 ei{ ERRTYPECOMM,     "Sequence Reject Frame Received... Sequencing Adjusted" } },
        { ClientErrors::Framing,                     ei{ ERRTYPECOMM,     "Framing Error"                               } },
        { ClientErrors::BadCrc,                      ei{ ERRTYPECOMM,     "Bad CRC on Message"                          } },
        { ClientErrors::BadLength,                   ei{ ERRTYPESYSTEM,   "Bad Length Specification"                    } },
        { ClientErrors::BadHdlcUaFrame,              ei{ ERRTYPECOMM,     "Bad HDLC UA Frame"                           } },
        { ClientErrors::Unknown,                     ei{ ERRTYPESYSTEM,   "Unknown Error"                               } },
        { ClientErrors::ReqackFlagSet,               ei{ ERRTYPECOMM,     "REQACK Flag set-- Frame Unexecutable"        } },
        { ClientErrors::RouteNotFound,               ei{ ERRTYPESYSTEM,   "Route Not Found"                             } },
        { ClientErrors::IdNotFound,                  ei{ ERRTYPESYSTEM,   "ID Not Found"                                } },
        { ClientErrors::TypeNotFound,                ei{ ERRTYPESYSTEM,   "Function and/or Type Not Found"              } },
        { ClientErrors::EWordReceived,               ei{ ERRTYPEPROTOCOL, "E-Word Received in Returned Message"         } },
        { ClientErrors::SystemRelated,               ei{ ERRTYPESYSTEM,   "OS or System Error"                          } },
        { ClientErrors::BadPort,                     ei{ ERRTYPESYSTEM,   "Bad Port Specification"                      } },
        { ClientErrors::QueueRead,                   ei{ ERRTYPESYSTEM,   "Error Reading Queue"                         } },
        { ClientErrors::QueueWrite,                  ei{ ERRTYPESYSTEM,   "Error Writing Queue"                         } },
        { ClientErrors::MemoryAccess,                ei{ ERRTYPESYSTEM,   "Error Allocating or Manipulating Memory"     } },
        { ClientErrors::NoDcd,                       ei{ ERRTYPECOMM,     "No DCD on Return Message"                    } },
        { ClientErrors::PortRead,                    ei{ ERRTYPECOMM,     "Error Reading from Port"                     } },
        { ClientErrors::PortWrite,                   ei{ ERRTYPECOMM,     "Error Writing to Port"                       } },
        { ClientErrors::QueueExec,                   ei{ ERRTYPEPROTOCOL, "Error Executing CCU Queue Entry"             } },
        { ClientErrors::DlcTimeout,                  ei{ ERRTYPEPROTOCOL, "DLC Read Timeout on CCU Queue Entry"         } },
        { ClientErrors::NoAttempt,                   ei{ ERRTYPESYSTEM,   "No Attempt Made on CCU Queue Entry"          } },
        { ClientErrors::RouteFailed,                 ei{ ERRTYPEPROTOCOL, "Route Failed on CCU Queue Entry"             } },
        { ClientErrors::TransmitterFailed,           ei{ ERRTYPEPROTOCOL, "Transponder Communication Failed on CCU Queue Entry" } },
        { ClientErrors::RemoteInhibited,             ei{ ERRTYPESYSTEM,   "Communications Attempted With Inhibited Remote" } },
        { ClientErrors::CcuQueueFlushed,             ei{ ERRTYPESYSTEM,   "CCU Queue was Flushed... Entries Lost in Drain" } },
        { ClientErrors::PipeBroken,                  ei{ ERRTYPESYSTEM,   "Pipe Connect was Broken"                     } },
        { ClientErrors::PipeNotOpened,               ei{ ERRTYPESYSTEM,   "Pipe Not Opened"                             } },
        { ClientErrors::PortInhibited,               ei{ ERRTYPESYSTEM,   "Communications Attempted Over Inhibited Port" } },
        { ClientErrors::AccumulatorsNotSupported,    ei{ ERRTYPESYSTEM,   "Device Does Not Support Accumulators"        } },
        { ClientErrors::DeviceInhibited,             ei{ ERRTYPESYSTEM,   "Operation Attempted on Inhibited Device"     } },
        { ClientErrors::DialupFailed,                ei{ ERRTYPECOMM,     "Error Dialing Up Remote"                     } },
        { ClientErrors::WrongAddress,                ei{ ERRTYPECOMM,     "Wrong Unique Address Received"               } },
        { ClientErrors::TcpConnect,                  ei{ ERRTYPECOMM,     "Error Connecting to TCP socket"              } },
        { ClientErrors::TcpWrite,                    ei{ ERRTYPECOMM,     "Error Writing to TCP socket"                 } },
        { ClientErrors::TcpRead,                     ei{ ERRTYPECOMM,     "Error Reading from TCP socket"               } },
        { ClientErrors::Address,                     ei{ ERRTYPESYSTEM,   "Address Does Not Match Expected Value"       } },
        { ClientErrors::IedBufferBad,                ei{ ERRTYPESYSTEM,   "Bad Data Buffer for IED"                     } },
        { ClientErrors::MissingConfig,               ei{ ERRTYPESYSTEM,   "Missing Required Configuration Entry"        } },
        { ClientErrors::BadSocket,                   ei{ ERRTYPESYSTEM,   "Bad Nexus Specification"                     } },
        { ClientErrors::SocketWrite,                 ei{ ERRTYPESYSTEM,   "Error Writing to Nexus"                      } },
        { ClientErrors::BadBch,                      ei{ ERRTYPEPROTOCOL, "Bad BCH"                                     } },
        { ClientErrors::Memory,                      ei{ ERRTYPESYSTEM,   "Memory Error"                                } },
        { ClientErrors::NoMethod,                    ei{ ERRTYPESYSTEM,   "No Method"                                   } },
        { ClientErrors::NoMethodForGeneralScan,      ei{ ERRTYPESYSTEM,   "No General Scan Method"                      } },
        { ClientErrors::NoMethodForIntegrityScan,    ei{ ERRTYPESYSTEM,   "No Integrity Scan Method"                    } },
        { ClientErrors::NoMethodForAccumulatorScan,  ei{ ERRTYPESYSTEM,   "No Accum Scan Method"                        } },
        { ClientErrors::NoMethodForProcessResult,    ei{ ERRTYPESYSTEM,   "No Process Result Method"                    } },
        { ClientErrors::NoMethodForExecuteRequest,   ei{ ERRTYPESYSTEM,   "No Exec. Req. Method"                        } },
        { ClientErrors::NoMethodForResultDecode,     ei{ ERRTYPESYSTEM,   "No Result Decode Method"                     } },
        { ClientErrors::NoMethodForErrorDecode,      ei{ ERRTYPESYSTEM,   "No ErrorDecode Method"                       } },
        { ClientErrors::NoMethodForHandshake,        ei{ ERRTYPESYSTEM,   "No Handshake Method"                         } },
        { ClientErrors::NoMethodForGenerateCmd,      ei{ ERRTYPESYSTEM,   "No Generate Command Method"                  } },
        { ClientErrors::NoMethodForDecodeResponse,   ei{ ERRTYPESYSTEM,   "No DecodeResponse Method"                    } },
        { ClientErrors::NoMethodForDataCopy,         ei{ ERRTYPESYSTEM,   "No Data Copy Method"                         } },
        { ClientErrors::NoConfigData,                ei{ ERRTYPESYSTEM,   "No Config Data Found"                        } },
        { ClientErrors::NoRouteGroupDevice,          ei{ ERRTYPESYSTEM,   "No Route for Group Dev."                     } },
        { ClientErrors::NoRoutesInMacro,             ei{ ERRTYPESYSTEM,   "No Routes for Macro Rte"                     } },
        { ClientErrors::RouteOffsetOutOfRange,       ei{ ERRTYPESYSTEM,   "Macro Offset does not exist in Macro Rte"    } },
        { ClientErrors::SubRouteIsMacro,             ei{ ERRTYPESYSTEM,   "Macro Offset refers to a macro sub-rte"      } },
        { ClientErrors::ControlInhibitedOnDevice,    ei{ ERRTYPESYSTEM,   "Device is control disabled"                  } },
        { ClientErrors::ControlInhibitedOnPoint,     ei{ ERRTYPESYSTEM,   "Point is control disabled"                   } },
        { ClientErrors::RequestExpired,              ei{ ERRTYPESYSTEM,   "Requested operation expired due to time"     } },
        { ClientErrors::DialupConnectPort,           ei{ ERRTYPECOMM,     "Dialup connection failed. Port in error"     } },
        { ClientErrors::DialupConnectDevice,         ei{ ERRTYPESYSTEM,   "Dialup connection failed. Device in error"   } },
        { ClientErrors::PortSimulated,               ei{ ERRTYPEPROTOCOL, "Port is simulated, no inbound data available" } },
        { ClientErrors::PortEchoedResponse,          ei{ ERRTYPECOMM,     "Port echoed request bytes"                   } },
        { ClientErrors::RetrySubmitted,              ei{ ERRTYPESYSTEM,   "Retry Resubmitted"                           } },
        { ClientErrors::ScanDeviceInhibited,         ei{ ERRTYPESYSTEM,   "Scanned device is inhibited"                 } },
        { ClientErrors::ScanGlobalAddress,           ei{ ERRTYPESYSTEM,   "Illegal scan of global device"               } },
        { ClientErrors::ScanWindowClosed,            ei{ ERRTYPESYSTEM,   "Device window is closed"                     } },
        { ClientErrors::PageRS,                      ei{ ERRTYPESYSTEM,   "Invalid transaction, typ. bad pager id or password" } },
        { ClientErrors::PageNAK,                     ei{ ERRTYPESYSTEM,   "TAP Repeat Requested, but retries exhausted" } },
        { ClientErrors::PageNoResponse,              ei{ ERRTYPESYSTEM,   "No response from TAP terminal"               } },
        { ClientErrors::WctpHttpResponse,            ei{ ERRTYPESYSTEM,   "Invalid or unsuccessful HTTP response"       } },
        { ClientErrors::WctpXmlParser,               ei{ ERRTYPESYSTEM,   "XML parser initialization failed"            } },
        { ClientErrors::WctpResponse,                ei{ ERRTYPESYSTEM,   "Invalid WCTP response format"                } },
        { ClientErrors::WctpTimeout,                 ei{ ERRTYPESYSTEM,   "Time out when receiving WCTP response"       } },
        { ClientErrors::Wctp300Series,               ei{ ERRTYPESYSTEM,   "Protocol Error 300 Series"                   } },
        { ClientErrors::Wctp400Series,               ei{ ERRTYPESYSTEM,   "Protocol Error 400 Series"                   } },
        { ClientErrors::Wctp500Series,               ei{ ERRTYPESYSTEM,   "Protocol Error 500 Series"                   } },
        { ClientErrors::Wctp600Series,               ei{ ERRTYPESYSTEM,   "Protocol Error 600 Series"                   } },
        { ClientErrors::QueuePurged,                 ei{ ERRTYPESYSTEM,   "Queue purged to limit memory usage"          } },
        { ClientErrors::RequestCancelled,            ei{ ERRTYPESYSTEM,   "Request was cancelled"                       } },
        { ClientErrors::InvalidTimestamp,            ei{ ERRTYPESYSTEM,   "Invalid time returned OR time outside of requested range." } },
        { ClientErrors::InvalidChannel,              ei{ ERRTYPESYSTEM,   "Invalid channel returned by daily read."     } },
        { ClientErrors::MacsTimeout,                 ei{ ERRTYPESYSTEM,   "MACS timed out on this message"              } },
        { ClientErrors::InvalidFrozenReadingParity,  ei{ ERRTYPEPROTOCOL, "The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device." } },
        { ClientErrors::InvalidFrozenPeakTimestamp,  ei{ ERRTYPEPROTOCOL, "The frozen peak timestamp is outside of the expected range." } },
        { ClientErrors::InvalidFreezeCounter,        ei{ ERRTYPEPROTOCOL, "The freeze counter is less than the expected value." } },
        { ClientErrors::InvalidData,                 ei{ ERRTYPEPROTOCOL, "Invalid data was was received for one or more data points." } },
        { ClientErrors::FreezeNotRecorded,           ei{ ERRTYPEPROTOCOL, "There is no record of the last freeze sent to this device." } },
        { ClientErrors::InvalidRequest,              ei{ ERRTYPESYSTEM,   "Invalid/Incomplete Request"                  } },
        { ClientErrors::InvalidSSPEC,                ei{ ERRTYPESYSTEM,   "Insufficient SSPEC/Firmware Revision"        } },
        { ClientErrors::VerifySSPEC,                 ei{ ERRTYPESYSTEM,   "Verify SSPEC/Firmware Revision"              } },
        { ClientErrors::UnsupportedDevice,           ei{ ERRTYPESYSTEM,   "Device Not Supported"                        } },
        { ClientErrors::PortNotInitialized,          ei{ ERRTYPECOMM,     "Port not initialized"                        } },
        { ClientErrors::CommandAlreadyInProgress,    ei{ ERRTYPESYSTEM,   "Command already in progress"                 } },
        { ClientErrors::DeviceNotConnected,          ei{ ERRTYPESYSTEM,   "Device is not connected"                     } },
        { ClientErrors::NoDisconnect,                ei{ ERRTYPESYSTEM,   "No disconnect configured on this device"     } },
        { ClientErrors::TransmitterOverheating,      ei{ ERRTYPEPROTOCOL, "Transmitter is overheating"                  } },
        { ClientErrors::NeedsChannelConfig,          ei{ ERRTYPESYSTEM,   "Command needs channel config to continue."   } },
        { ClientErrors::DnsLookupFailed,             ei{ ERRTYPESYSTEM,   "Failed to resolve an IP for the given DNS name." } },
        { ClientErrors::PointLookupFailed,           ei{ ERRTYPESYSTEM,   "Failed to find a point for the given device." } },
        { ClientErrors::NeedsDateRangeReset,         ei{ ERRTYPESYSTEM,   "The device's date range must be reset to continue." } },
        { ClientErrors::DataMissing,                 ei{ ERRTYPESYSTEM,   "Not enough data received from the device."   } },
        { ClientErrors::InvalidConfigData,           ei{ ERRTYPESYSTEM,   "Configuration data is invalid."              } },
        { ClientErrors::E2eUnknownAddress,           ei{ ERRTYPESYSTEM,   "Device address is unknown."                  } },
        { ClientErrors::E2eNetworkUnavailable,       ei{ ERRTYPESYSTEM,   "Network is unavailable."                     } },
        { ClientErrors::E2eRequestPacketTooLarge,    ei{ ERRTYPESYSTEM,   "Request packet is too large."                } },
        { ClientErrors::E2eProtocolUnsupported,      ei{ ERRTYPESYSTEM,   "Protocol is unsupported."                    } },
        { ClientErrors::E2eInvalidNetworkServerId,   ei{ ERRTYPESYSTEM,   "Network server ID is invalid."               } },
        { ClientErrors::E2eInvalidApplicationServiceId,  ei{ ERRTYPESYSTEM, "Application service ID is invalid."        } },
        { ClientErrors::E2eNetworkLoadControl,       ei{ ERRTYPESYSTEM,   "Network traffic limiting."                   } },
        { ClientErrors::E2eRequestTimeout,           ei{ ERRTYPESYSTEM,   "Did not receive a response from the device." } },
        { ClientErrors::NetworkManagerTimeout,       ei{ ERRTYPESYSTEM,   "Did not receive a response from Network Manager." } },
        { ClientErrors::E2eRequestPayloadTooLarge,   ei{ ERRTYPESYSTEM,   "E2E request payload too large."              } },
        { ClientErrors::E2eRequestNotAcceptable,     ei{ ERRTYPESYSTEM,   "Endpoint indicated request not acceptable."  } },
        { ClientErrors::NoPointsOnDevice,            ei{ ERRTYPESYSTEM,   "The device has no points attached."          } },
        { ClientErrors::E2eNetworkServiceFailure,    ei{ ERRTYPESYSTEM,   "Network service failure."                    } },
    };

}


//  Returns the error's description
IM_EX_CTIBASE std::string GetErrorString(YukonError_t errorNumber)
{
    if( const boost::optional<error_info> info = Cti::mapFind(CtiErrors, errorNumber) )
    {
        return std::get<1>(*info);
    }

    return "Unknown Error Code (" + CtiNumStr(errorNumber) + ")";
}


//  Returns the error's type
IM_EX_CTIBASE ErrorTypes GetErrorType(YukonError_t errorNumber)
{
    if( const boost::optional<error_info> info = Cti::mapFind(CtiErrors, errorNumber) )
    {
        return std::get<0>(*info);
    }

    return ERRTYPESYSTEM;
}

