#include "precompiled.h"

#include <time.h>
#include <utility>
#include <list>
#include <limits>

#include "numstr.h"

#include "devicetypes.h"
#include "dev_mct.h"
#include "dev_mct210.h"
#include "dev_mct31x.h"  //  for IED scanning capability
#include "dev_mct410.h"
#include "dev_mct470.h"
#include "dev_mct_lmt2.h"
#include "logger.h"
#include "msg_cmd.h"
#include "pt_numeric.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "porter.h"
#include "utility.h"
#include "dllyukon.h"
#include "cparms.h"
#include "ctidate.h"
#include "desolvers.h"

#include "cmd_dlc.h"

using std::string;
using std::endl;
using std::list;
using std::pair;

using namespace Cti::Protocols;

using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

const MctDevice::CommandSet MctDevice::_commandStore = MctDevice::initCommandStore();

MctDevice::MctDevice() :
    _lpIntervalSent(false),
    _configType(ConfigInvalid),
    _peakMode(PeakModeInvalid),
    _disconnectAddress(0),
    _freeze_counter (std::numeric_limits<int>::min()),
    _freeze_expected(std::numeric_limits<int>::min())
{
    _lastReadDataPurgeTime = _lastReadDataPurgeTime.now();
    for( int i = 0; i < MCTConfig_ChannelCount; i++ )
    {
        _mpkh[i] = -1.0;
        _wireConfig[i] = WireConfigInvalid;
    }

    resetMCTScansPending();
}

bool MctDevice::getMCTDebugLevel(int mask)
{
    static time_t lastaccess;  //  initialized to 0 the first time through, as per static rules
    static int mct_debuglevel;

    if( lastaccess + 300 < ::time(0) )
    {
        mct_debuglevel = gConfigParms.getValueAsInt("MCT_DEBUGLEVEL");
        lastaccess = ::time(0);
    }

    return mask & mct_debuglevel;
}


bool MctDevice::sspecIsValid( int sspec )
{
    //  note that the LMT-2 sspec relies only on the lower byte, so anything with
    //    36 (0x24) as the lower-order byte will need to be watched...
    //  36, 292, 548, 804, 1060, 1316, 1572, 1828, 2084, 2340, 2596, 2852, 3108, 3364, 3620, 3876, ...

    bool valid = false;

    using std::make_pair;
    using std::set;

    set< pair<int, int> > mct_sspec;
    pair<int, int> reported;

    mct_sspec.insert(make_pair(TYPELMT2,        36));

    mct_sspec.insert(make_pair(TYPEMCT210,      95));

    mct_sspec.insert(make_pair(TYPEMCT213,      95));

    mct_sspec.insert(make_pair(TYPEMCT212,      74));

    mct_sspec.insert(make_pair(TYPEMCT224,      74));

    mct_sspec.insert(make_pair(TYPEMCT226,      74));

    mct_sspec.insert(make_pair(TYPEMCT240,      74));
    mct_sspec.insert(make_pair(TYPEMCT240,     121));

    mct_sspec.insert(make_pair(TYPEMCT242,     121));

    mct_sspec.insert(make_pair(TYPEMCT248,     121));

    mct_sspec.insert(make_pair(TYPEMCT250,     111));

    mct_sspec.insert(make_pair(TYPEMCT310,     153));
    mct_sspec.insert(make_pair(TYPEMCT310,    1007));  //  new Grand Unification sspec

    mct_sspec.insert(make_pair(TYPEMCT310ID,   153));
    mct_sspec.insert(make_pair(TYPEMCT310ID,  1007));  //  new Grand Unification sspec

    mct_sspec.insert(make_pair(TYPEMCT310IL,  1007));

    mct_sspec.insert(make_pair(TYPEMCT310IDL, 1007));

    mct_sspec.insert(make_pair(TYPEMCT318L,   1007));

    mct_sspec.insert(make_pair(TYPEMCT318,     218));
    mct_sspec.insert(make_pair(TYPEMCT318,    1007));  //  new Grand Unification sspec

    mct_sspec.insert(make_pair(TYPEMCT360,     218));
    mct_sspec.insert(make_pair(TYPEMCT360,    1007));  //  new Grand Unification sspec
    mct_sspec.insert(make_pair(TYPEMCT360,    1008));  //  these two are the S4 and
    mct_sspec.insert(make_pair(TYPEMCT360,    1009));  //    the KV...  but i don't know which is which

    mct_sspec.insert(make_pair(TYPEMCT370,     218));
    mct_sspec.insert(make_pair(TYPEMCT370,    1007));  //  ditto of the above for the 360
    mct_sspec.insert(make_pair(TYPEMCT370,    1008));  //
    mct_sspec.insert(make_pair(TYPEMCT370,    1009));  //

    reported = make_pair(getType(), sspec);

    if( mct_sspec.find(reported) != mct_sspec.end() )
    {
        valid = true;
    }

    return valid;
}


string MctDevice::sspecIsFrom( int sspec )
{
    string whois;

    switch( sspec )
    {
        case 36:    whois = "LMT-2";    break;

        case 95:    whois = "MCT 21x";  break;

        case 74:    whois = "MCT 22x";  break;

        case 93:
        case 121:   whois = "MCT 24x";  break;

        case 111:   whois = "MCT 250";  break;

        case 153:   whois = "MCT 310";  break;

        case 1007:  whois = "MCT 3xx/3xxL";     break;

        case 218:   whois = "MCT 318/360/370";  break;

        default:    whois = "Unknown";  break;
    }

    return whois;
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string MctDevice::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}

//  utility function for boost::assign until we get initialization lists
MctDevice::value_descriptor MctDevice::make_value_descriptor(unsigned offset, CtiTableDynamicPaoInfo::PaoInfoKeys key)
{
    value_descriptor v = {offset, key};

    return v;
}

const MctDevice::ValueMapping *MctDevice::getMemoryMap(void) const
{
    return 0;
}

const MctDevice::FunctionReadValueMappings *MctDevice::getFunctionReadValueMaps(void) const
{
    return 0;
}


const MctDevice::ReadDescriptor MctDevice::getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const
{
    if( io == EmetconProtocol::IO_Read )
    {
        if( const ValueMapping *memoryMap = getMemoryMap() )
        {
            return getDescriptorFromMapping(*memoryMap, function, readLength);
        }
    }
    else if( io == EmetconProtocol::IO_Function_Read )
    {
        if( const FunctionReadValueMappings *fr = getFunctionReadValueMaps() )
        {
            FunctionReadValueMappings::const_iterator itr = fr->find(function);

            if( itr != fr->end() )
            {
                return getDescriptorFromMapping(itr->second, 0, readLength);
            }
        }
    }

    return ReadDescriptor();
}


const MctDevice::ReadDescriptor MctDevice::getDescriptorFromMapping(const ValueMapping &vm, const unsigned function, const unsigned readLength) const
{
    ReadDescriptor rd;

    ValueMapping::const_iterator
        itr = vm.lower_bound(function),
        end = vm.lower_bound(function + readLength);

    while( itr != end )
    {
        value_locator kvl = { itr->first - function, itr->second.length, itr->second.key };

        rd.push_back(kvl);

        ++itr;
    }

    return rd;
}

void MctDevice::extractDynamicPaoInfo(const INMESS &InMessage)
{
    const unsigned char io    = InMessage.Return.ProtocolInfo.Emetcon.IO;
    const unsigned function   = InMessage.Return.ProtocolInfo.Emetcon.Function;
    const unsigned readLength = InMessage.Buffer.DSt.Length;
    const unsigned char *message = InMessage.Buffer.DSt.Message;

    const ReadDescriptor descriptor = getDescriptorForRead(io, function, readLength);

    for each( const value_locator kvl in descriptor )
    {
        if( (kvl.offset + kvl.length) <= readLength )
        {
            decodeReadDataForKey(
                kvl.key,
                message + kvl.offset,
                message + kvl.offset + kvl.length);
        }
    }
}


void MctDevice::decodeReadDataForKey(const CtiTableDynamicPaoInfo::PaoInfoKeys key, const unsigned char *begin, const unsigned char *end)
{
    if( end <= begin )
    {
        CTILOG_ERROR(dout, "unexpected end <= begin for device  \"" << getName() << "\" (begin - end = " << static_cast<int>(begin - end) << ")");

        return;
    }

    switch( key )
    {
        case CtiTableDynamicPaoInfo::Key_MCT_SSpec:
        {
            //  Special processing for old-style 5-byte SSPEC read: Lo Rv xx xx Hi
            if( (end - begin) == 5 )
            {
                long sspec = begin[0];
                sspec     |= begin[4] << 8;

                setDynamicInfo(key, sspec);

                return;  //  All done with the special processing
            }
            else
            {
                break;  //  Break out to the normal processing below
            }
        }
        case CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay:
        {
            const long day = begin[0];

            //  only update the config timestamp if the value changes
            if( getDynamicInfo(key) != day )
            {
                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp, CtiTime::now());
            }

            //  Always re-set the value so users can report based on the UpdateTime field
            setDynamicInfo(key, day);

            return;
        }
    }

    unsigned long value = 0;

    for( const unsigned char *itr = begin; itr < end; ++itr )
    {
        value <<= 8;
        value |= *itr;
    }

    setDynamicInfo(key, value);
}


LONG MctDevice::getDemandInterval()
{
    const long lastIntervalDemand = getLastIntervalDemandRate();

    if( lastIntervalDemand && lastIntervalDemand != LONG_MAX )
    {
        return lastIntervalDemand;
    }

    return DemandInterval_Default;
}


void MctDevice::resetMCTScansPending( void )
{
    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateAccum, false);
}

CtiTime MctDevice::adjustNextScanTime(const INT scanType)
{
    CtiTime Now;
    CtiTime When(YUKONEOT);    // This is never!
    long   scanRate;
    unsigned long nextLPTime;

    scanRate = getScanRate(scanType);

    if( scanRate != 0 )    // If it is zero we return the future!
    {
        if( scanRate < 60 )
        {
            /* Do not allow DLC device to scan faster than 60 seconds */
            When = setNextInterval(Now.seconds() + 1, 60);
        }
        else
        {
            When = setNextInterval(Now.seconds() + 1, scanRate);
        }

        /* Some devices we need to wait till half past the minute */
        switch( getType() )
        {
        case TYPELMT2:
        case TYPEMCT212:
        case TYPEMCT224:
        case TYPEMCT226:
            {
            When += 30L;
            }
        }
    }

    setNextScan(scanType, When);

    return When;
}

unsigned long MctDevice::calcNextLPScanTime( void )
{
    return (_nextLPScanTime = YUKONEOT);  //  never for a non-load profile device...  overridden by 24x (+250), 310L, 318L
}

unsigned long MctDevice::getNextLPScanTime( void )
{
    return _nextLPScanTime;
}


void MctDevice::sendLPInterval( OUTMESS *&OutMessage, OutMessageList &outList )
{
    populateDlcOutMessage(*OutMessage);
    OutMessage->Sequence  = EmetconProtocol::PutConfig_LoadProfileInterval;     // Helps us figure it out later!

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon interval lp", COMMAND_STR_SIZE );

    outList.push_back(OutMessage);
    OutMessage = NULL;

    _lpIntervalSent = true;
}


int MctDevice::checkDemandQuality( unsigned long &pulses, PointQuality_t &quality, bool &badData )
{
    unsigned long qualityBits;
    int retVal;

    retVal = 0;  //  no error

    qualityBits = pulses & 0xc000;
    pulses      = pulses & 0x3fff;

    if( pulses > 16320 )  //  error code from device
    {
        if( pulses == 16382 )
            quality = OverflowQuality;
        else if( pulses == 16383 )
            quality = DeviceFillerQuality;
        else
            quality = UnknownQuality;

        pulses = 0;
        retVal = -1;  //  bad data
    }
    else
    {
        if( (qualityBits & 0x4000) &&
            (qualityBits & 0x8000) )
        {
            quality = OverflowQuality;
            pulses = 0;
            badData = true;
            retVal = -1;  //  bad data
        }
        else if( qualityBits & 0x4000 )
        {
            quality = PartialIntervalQuality;
        }
        else if( qualityBits & 0x8000 )
        {
            quality = PowerfailQuality;
        }
        else
        {
            quality = NormalQuality;
        }
    }

    return retVal;
}


//
//  My apologies to those who follow.
//
MctDevice::CommandSet MctDevice::initCommandStore()
{
    CommandSet cs;

    //  initialize any pan-MCT operations
    cs.insert(CommandStore(EmetconProtocol::Command_Loop,        EmetconProtocol::IO_Read, Memory_ModelPos, 1));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Model,     EmetconProtocol::IO_Read, Memory_ModelPos, Memory_ModelLen));  //  Decode happens in the children please...

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Install,   EmetconProtocol::IO_Read, Memory_ModelPos, Memory_ModelLen));  //  This basically does a getconfig model so
                                                                                                                //    we know what devicetype we're installing

    cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddressEnable,  EmetconProtocol::IO_Write, Command_GroupAddressEnable,  0));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddressInhibit, EmetconProtocol::IO_Write, Command_GroupAddressInhibit, 0));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw,       EmetconProtocol::IO_Read,           0,                  0));  //  this will be filled in by executeGetConfig

    cs.insert(CommandStore(EmetconProtocol::Control_Shed,        EmetconProtocol::IO_Write,          0,                  0));  //  this will be filled in by executeControl
    cs.insert(CommandStore(EmetconProtocol::Control_Restore,     EmetconProtocol::IO_Write,          Command_Restore,    0));

    cs.insert(CommandStore(EmetconProtocol::Control_Connect,     EmetconProtocol::IO_Write | Q_ARML, Command_Connect,    0));
    cs.insert(CommandStore(EmetconProtocol::Control_Disconnect,  EmetconProtocol::IO_Write | Q_ARML, Command_Disconnect, 0));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_ARMC,      EmetconProtocol::IO_Write,          Command_ARMC,       0));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_ARML,      EmetconProtocol::IO_Write,          Command_ARML,       0));

    //  putconfig_tsync is in MCT2XX and MCT310 because the 2XX requires an ARMC
    //    also, the getconfig time location is different for 2XX and 3XX, so that's in each's base as well
    cs.insert(CommandStore(EmetconProtocol::GetConfig_TSync,     EmetconProtocol::IO_Read, Memory_TSyncPos, Memory_TSyncLen));

    return cs;
}


/*****************************************************************************************
 *  The general scan for a mct type device is performed and collects DEMAND accumulators
 *  from the device, as well as status info, if the device can supply it in the same read.
 *****************************************************************************************/
YukonError_t MctDevice::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(DebugLevel_Scanrates) )
        {
            CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
        }


        if(getOperation(EmetconProtocol::Scan_General, OutMessage->Buffer.BSt))
        {
            populateDlcOutMessage(*OutMessage);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_General;     // Helps us figure it out later!
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.RetryMacroOffset = MacroOffset::none;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            outList.push_back(OutMessage);
            OutMessage = NULL;

            setScanFlag(ScanRateGeneral, true);  //resetScanFlag(ScanPending);
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;

            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName() <<" (Device Type "<< desolveDeviceType(getType()) <<")");

            status = ClientErrors::NoMethod;
        }
    }

    return status;
}

/*****************************************************************************************
 *  The integrity scan for a mct type device is performed and collects status data
 *  from the device.  This is valid esp for DLC devices which require separate reads to
 *  collect status information
 *****************************************************************************************/
YukonError_t MctDevice::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(DebugLevel_Scanrates) )
        {
            CTILOG_DEBUG(dout, "Demand/IEDScan for \""<< getName() <<"\"");
        }

        if(getOperation(EmetconProtocol::Scan_Integrity, OutMessage->Buffer.BSt))
        {
            populateDlcOutMessage(*OutMessage);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_Integrity;     // Helps us figure it out later!;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.RetryMacroOffset = MacroOffset::none;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
            outList.push_back(OutMessage);
            OutMessage = NULL;

            setScanFlag(ScanRateIntegrity, true);  //resetScanFlag(ScanPending);
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;

            status = ClientErrors::NoMethod;

            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName());
        }
    }

    return status;
}

/*****************************************************************************************
 *  The accumulator scan for a mct type device is performed and collects pulse data
 *  from the device, as well as status info, if the device can supply it in the same read.
 *****************************************************************************************/
YukonError_t MctDevice::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(DebugLevel_Scanrates) )
        {
            CTILOG_DEBUG(dout, "AccumulatorScan for \"" << getName() << "\"");
        }

        if(getOperation(EmetconProtocol::Scan_Accum, OutMessage->Buffer.BSt))
        {
            populateDlcOutMessage(*OutMessage);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_Accum;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.RetryMacroOffset = MacroOffset::none;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            outList.push_back(OutMessage);
            OutMessage = NULL;

            setScanFlag(ScanRateAccum, true);  //resetScanFlag(ScanPending);
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;

            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName());

            status = ClientErrors::NoMethod;
        }
    }

    return status;
}


/*****************************************************************************************
 *  The load profile scan for a mct type device is dependent on the load profile scan rate,
 *  and gathers load profile whenever 6 intervals have passed
 *****************************************************************************************/
YukonError_t MctDevice::LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(DebugLevel_Scanrates) )
        {
            CTILOG_DEBUG(dout, "LoadProfileScan for \""<< getName() <<"\"");
        }

        if(getOperation(EmetconProtocol::Scan_LoadProfile, OutMessage->Buffer.BSt))
        {
            populateDlcOutMessage(*OutMessage);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_LoadProfile;
            OutMessage->Retry     = 0;  // override            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.RetryMacroOffset = MacroOffset::none;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            calcAndInsertLPRequests(OutMessage, outList);

            if( OutMessage != NULL )
            {
                //  outMessage will be copied by calcAndInsert..., so we will delete it here
                delete OutMessage;
                OutMessage = NULL;
            }
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;

            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName());

            status = ClientErrors::NoMethod;
        }
    }

    return status;
}


YukonError_t MctDevice::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    //  extract the DynamicPaoInfo first so we have it during the decode
    extractDynamicPaoInfo(InMessage);

    YukonError_t status = ModelDecode(InMessage, TimeNow, vgList, retList, outList);

    if( status == ClientErrors::NoMethodForResultDecode )
    {
        status = Parent::ResultDecode(InMessage, TimeNow, vgList, retList, outList);
    }

    if( status == ClientErrors::NoMethodForResultDecode )
    {
        CTILOG_ERROR(dout, "No result decode method, IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
    }

    if( InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Read ||
        InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read )
    {
        std::auto_ptr<CtiReturnMsg> retMsg(new CtiReturnMsg(getID()));

        point_info pi;

        pi.value   = InMessage.Buffer.DSt.Power;
        pi.quality = NormalQuality;
        insertPointDataReport(StatusPointType, PointOffset_Status_Powerfail, retMsg.get(), pi, "", TimeNow);

        pi.value   = InMessage.Buffer.DSt.Alarm;
        pi.quality = NormalQuality;
        insertPointDataReport(StatusPointType, PointOffset_Status_GeneralAlarm, retMsg.get(), pi, "", TimeNow);

        if( ! retMsg->PointData().empty() )
        {
            vgList.push_back(retMsg.release());
        }
    }

    return status;
}


YukonError_t MctDevice::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_ARMC:
        case EmetconProtocol::PutConfig_ARML:
        {
            break;
        }

        case EmetconProtocol::Control_Latch:
        case EmetconProtocol::Control_Shed:
        case EmetconProtocol::Control_Restore:
        {
            status = decodeControl(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        case EmetconProtocol::Control_Connect:
        case EmetconProtocol::Control_Disconnect:
        {
            status = decodeControlDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::GetConfig_Time:
        case EmetconProtocol::GetConfig_TSync:
        case EmetconProtocol::GetConfig_Holiday:
        case EmetconProtocol::GetConfig_Raw:
        case EmetconProtocol::GetConfig_DemandInterval:
        case EmetconProtocol::GetConfig_LoadProfileInterval:
        case EmetconProtocol::GetConfig_Multiplier:
        case EmetconProtocol::GetConfig_Multiplier2:
        case EmetconProtocol::GetConfig_Multiplier3:
        case EmetconProtocol::GetConfig_Multiplier4:
        case EmetconProtocol::GetConfig_GroupAddress:
        {
            status = decodeGetConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        case EmetconProtocol::GetConfig_Model:
        {
            CTILOG_ERROR(dout, "unhandled GetConfig_Model");

            break;
        }

        case EmetconProtocol::GetValue_PFCount:
        {
            status = decodeGetValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::Command_Loop:
        {
            status = decodeLoopback(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::PutConfig_Install:
        case EmetconProtocol::PutConfig_Parameters:
        case EmetconProtocol::PutConfig_Multiplier:
        case EmetconProtocol::PutConfig_Multiplier2:
        case EmetconProtocol::PutConfig_Multiplier3:
        case EmetconProtocol::PutConfig_GroupAddressEnable:
        case EmetconProtocol::PutConfig_GroupAddressInhibit:
        case EmetconProtocol::PutConfig_Raw:
        case EmetconProtocol::PutConfig_TSync:
        case EmetconProtocol::PutConfig_Intervals:
        case EmetconProtocol::PutConfig_DemandInterval:
        case EmetconProtocol::PutConfig_LoadProfileInterval:
        case EmetconProtocol::PutConfig_OutageThreshold:
        case EmetconProtocol::PutConfig_ChannelSetup:
        case EmetconProtocol::PutConfig_IEDClass:
        case EmetconProtocol::PutConfig_IEDScan:
        case EmetconProtocol::PutConfig_IEDDNPAddress:
        case EmetconProtocol::PutConfig_GroupAddress_Bronze:
        case EmetconProtocol::PutConfig_GroupAddress_GoldSilver:
        case EmetconProtocol::PutConfig_GroupAddress_Lead:
        case EmetconProtocol::PutConfig_UniqueAddress:
        case EmetconProtocol::PutConfig_LoadProfileInterest:
        case EmetconProtocol::PutConfig_Disconnect:
        case EmetconProtocol::PutConfig_TOU:
        case EmetconProtocol::PutConfig_FreezeDay:
        case EmetconProtocol::PutConfig_TimeZoneOffset:
        case EmetconProtocol::PutConfig_Holiday:
        case EmetconProtocol::PutConfig_TOUEnable:
        case EmetconProtocol::PutConfig_TOUDisable:
        case EmetconProtocol::PutConfig_DailyReadInterest:
        case EmetconProtocol::PutConfig_Options:
        case EmetconProtocol::PutConfig_PhaseDetectClear:
        case EmetconProtocol::PutConfig_PhaseDetect:
        case EmetconProtocol::PutConfig_AlarmMask:
        case EmetconProtocol::PutConfig_AutoReconnect:
        case EmetconProtocol::PutConfig_PhaseLossThreshold:
        {
            status = decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::PutValue_KYZ:
        case EmetconProtocol::PutValue_KYZ2:
        case EmetconProtocol::PutValue_KYZ3:
        case EmetconProtocol::PutValue_ResetPFCount:
        case EmetconProtocol::PutValue_IEDReset:
        case EmetconProtocol::PutValue_TOUReset:
        {
            status = decodePutValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::PutStatus_Reset:
        case EmetconProtocol::PutStatus_ResetAlarms:
        case EmetconProtocol::PutStatus_ResetOverride:
        case EmetconProtocol::PutStatus_PeakOn:
        case EmetconProtocol::PutStatus_PeakOff:
        case EmetconProtocol::PutStatus_FreezeOne:
        case EmetconProtocol::PutStatus_FreezeTwo:
        case EmetconProtocol::PutStatus_FreezeVoltageOne:
        case EmetconProtocol::PutStatus_FreezeVoltageTwo:
        case EmetconProtocol::PutStatus_SetTOUHolidayRate:
        case EmetconProtocol::PutStatus_ClearTOUHolidayRate:
        {
            status = decodePutStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = ClientErrors::NoMethodForResultDecode;
            break;
        }
    }

    return status;
}

YukonError_t MctDevice::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t retCode = ClientErrors::None;

    CtiCommandParser  parse(InMessage.Return.CommandStr);
    CtiReturnMsg     *retMsg = new CtiReturnMsg(getID(),
                                                string(InMessage.Return.CommandStr),
                                                string(),
                                                InMessage.ErrorCode,
                                                InMessage.Return.RouteID,
                                                InMessage.Return.RetryMacroOffset,
                                                InMessage.Return.Attempt,
                                                InMessage.Return.GrpMsgID,
                                                InMessage.Return.UserID);

    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    if( parse.getCommand() == ScanRequest )  //  we only plug values for failed scans
    {
        switch( parse.getiValue("scantype") )
        {
            case ScanRateGeneral:
            case ScanRateStatus:
            {
                //  implemented as the same scan
                switch( getType() )
                {
                    case TYPEMCT310:
                    case TYPEMCT310ID:
                    case TYPEMCT310IDL:
                    case TYPEMCT310IL:
                    case TYPEMCT318:
                    case TYPEMCT318L:
                    case TYPEMCT360:
                    case TYPEMCT370:
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 8, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 7, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 6, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 5, StatusPointType );

                    case TYPEMCT250:
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 4, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 3, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 2, StatusPointType );
                        insertPointFail( InMessage, retMsg, ScanRateStatus, 1, StatusPointType );

                    default:
                        ;
                }

                resetForScan(ScanRateGeneral);

                break;
            }

            case ScanRateAccum:
            {
                switch( getType() )
                {
                    case TYPEMCT360:
                    case TYPEMCT370:
                    case TYPEMCT318:
                    case TYPEMCT318L:
                        insertPointFail( InMessage, retMsg, ScanRateAccum, 3, PulseAccumulatorPointType );
                        insertPointFail( InMessage, retMsg, ScanRateAccum, 2, PulseAccumulatorPointType );
                    default:
                        insertPointFail( InMessage, retMsg, ScanRateAccum, 1, PulseAccumulatorPointType );
                }

                resetForScan(ScanRateAccum);

                break;
            }

            case ScanRateIntegrity:
            {
                switch( getType() )
                {
                    case TYPEMCT360:
                    case TYPEMCT370:
                        //  insert the pointfails for the demand/KVAR/KVA points
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 10, AnalogPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 20, AnalogPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 30, AnalogPointType );

                        //  insert the pointfails for the voltage points
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, Mct31xDevice::MCT360_IED_VoltsPhaseA_PointOffset, AnalogPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, Mct31xDevice::MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, Mct31xDevice::MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, Mct31xDevice::MCT360_IED_VoltsNeutralCurrent_PointOffset, AnalogPointType );

                    case TYPEMCT318:
                    case TYPEMCT318L:
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 3, DemandAccumulatorPointType );
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 2, DemandAccumulatorPointType );
                    default:
                        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 1, DemandAccumulatorPointType );
                }

                resetForScan(ScanRateIntegrity);

                break;
            }

            case ScanRateLoadProfile:
            {
                if( isMct410(getType()) || isMct420(getType()) || isMct440(getType()) )
                {
                    int channel = parse.getiValue("loadprofile_channel", 0);

                    if( channel )
                    {
                        insertPointFail( InMessage, retMsg, ScanRateLoadProfile, channel + PointOffset_LoadProfileOffset, DemandAccumulatorPointType );
                    }
                }
                else
                {
                    for( int i = 0; i < CtiTableDeviceLoadProfile::MaxCollectedChannel; i++ )
                    {
                        if( getLoadProfile()->isChannelValid(i) )
                        {
                            insertPointFail( InMessage, retMsg, ScanRateLoadProfile, (i + 1) + PointOffset_LoadProfileOffset, DemandAccumulatorPointType );
                        }
                    }
                }

                break;
            }

            default:
            {
                break;
            }
        }
    }

    // send the whole mess to dispatch
    if( retMsg->PointData().size() > 0 )
    {
        retList.push_back(retMsg);
    }
    else
    {
        delete retMsg;
    }

    //  set it to null, it's been sent off
    retMsg = NULL;

    return retCode;
}


int MctDevice::insertPointFail( const INMESS &InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType )
{
    int failed = FALSE;
    CtiPointSPtr pPoint;

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    pPoint = getDevicePointOffsetTypeEqual( pOffset, pType);

    if( pMsg != NULL && pPoint )
    {
        pMsg->insert( -1 );          // This is the dispatch token and is unimplemented at this time
        pMsg->insert( CtiCommandMsg::OP_POINTID );  // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert( pPoint->getPointID() );
        pMsg->insert( scanType );
        pMsg->insert( InMessage.ErrorCode );

        pPIL->PointData().push_back(pMsg);
        pMsg = NULL;
    }
    else
    {
        failed = TRUE;
    }

    if(pMsg)
    {
        delete pMsg;
    }

    return failed;
}



YukonError_t MctDevice::executeLoopback(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;
    YukonError_t nRet  = ClientErrors::None;
    INT  function;
    int  i;

    OUTMESS *tmpOut;

    function = EmetconProtocol::Command_Loop;
    found = getOperation(function, OutMessage->Buffer.BSt);

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        for( i = 0; i < parse.getiValue("count"); i++ )
        {
            tmpOut = CTIDBG_new OUTMESS(*OutMessage);

            if( tmpOut != NULL )
                outList.push_back(tmpOut);
        }

        if( OutMessage != NULL )
        {
            delete OutMessage;
            OutMessage = NULL;
        }
    }


    return nRet;
}


YukonError_t MctDevice::executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;
    YukonError_t nRet  = ClientErrors::None;
    string tester;

    INT            function;
    unsigned short stub;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
        case ScanRateStatus:
        case ScanRateGeneral:
        {
            function = EmetconProtocol::Scan_General;
            found = getOperation(EmetconProtocol::Scan_General, OutMessage->Buffer.BSt);
            break;
        }
        case ScanRateAccum:
        {
            function = EmetconProtocol::Scan_Accum;
            found = getOperation(EmetconProtocol::Scan_Accum, OutMessage->Buffer.BSt);
            break;
        }
        case ScanRateIntegrity:
        {
            function = EmetconProtocol::Scan_Integrity;
            found = getOperation(EmetconProtocol::Scan_Integrity, OutMessage->Buffer.BSt);

            //  should we scan the IED for demand instead?
            if(getType() == TYPEMCT360 || getType() == TYPEMCT370)
            {
                //  if we're supposed to be scanning the IED, change it to the appropriate request
                if( ((Mct31xDevice *)this)->getIEDPort().getRealTimeScanFlag() )
                     getOperation(EmetconProtocol::GetValue_IEDDemand, OutMessage->Buffer.BSt);
            }

            break;
        }
        case ScanRateLoadProfile:
        {
            //  outmess needs to be filled in by another function, just check if it's there
            function = EmetconProtocol::Scan_LoadProfile;
            found = getOperation(EmetconProtocol::Scan_LoadProfile, OutMessage->Buffer.BSt);

            if( found )
            {
                //  make sure to define this function for all load profile devices!
                if( !calcLPRequestLocation(parse, OutMessage) )
                {
                    found = false;
                }
            }

            break;
        }
        default:
        {
            break;
        }
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t MctDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR Temp[80];

    INT function;

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( getType() == TYPEMCT360  ||  //  only these types can have an IED attached
            getType() == TYPEMCT370 )
        {
            if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                function = EmetconProtocol::GetValue_IEDDemand;
                found = getOperation( function, OutMessage->Buffer.BSt);
            }
            else  //  GV_IEDKwh, GV_IEDKvarh, GV_IEDKvah
            {
                if(      parse.getFlags() & CMD_FLAG_GV_KWH   )  function = EmetconProtocol::GetValue_IEDKwh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVARH )  function = EmetconProtocol::GetValue_IEDKvarh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVAH  )  function = EmetconProtocol::GetValue_IEDKvah;
                else  /*  default request  */                    function = EmetconProtocol::GetValue_IEDKwh;

                found = getOperation( function, OutMessage->Buffer.BSt);

                //  if( parse.getFlags() & CMD_FLAG_GV_RATEA )  OutMessage->Buffer.BSt.Function += 0;
                if(      parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
                else if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
                else if( parse.getFlags() & CMD_FLAG_GV_RATET )
                {
                    if(      parse.getFlags() & CMD_FLAG_GV_KWH   )  OutMessage->Buffer.BSt.Function += 4;
                    else if( parse.getFlags() & CMD_FLAG_GV_KVARH )  OutMessage->Buffer.BSt.Function -= 1;
                    else if( parse.getFlags() & CMD_FLAG_GV_KVAH  )  OutMessage->Buffer.BSt.Function -= 1;
                }

                if( (parse.getFlags() & CMD_FLAG_GV_KVAH || parse.getFlags() & CMD_FLAG_GV_KVARH) &&
                    (parse.getFlags() & CMD_FLAG_GV_RATED) )
                {
                    //  memory map don't allow no KVA/KVAR rate D gettin' 'round here  (apologies to Thacher Hurd)
                    found = false;
                }
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_PFCOUNT)
    {
        function = EmetconProtocol::GetValue_PFCount;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
    {
        function = EmetconProtocol::GetValue_Demand;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( getType() == TYPEMCT318 || getType() == TYPEMCT318L ||
            getType() == TYPEMCT360 || getType() == TYPEMCT370 ||
            isMct410(getType()) ||
            isMct420(getType()) ||
            isMct440(getType()) )
        {
            //  if pulse input 3 isn't defined
            if( !getDevicePointOffsetTypeEqual(3, DemandAccumulatorPointType ) )
            {
                OutMessage->Buffer.BSt.Length -= 2;

                //  if pulse input 2 isn't defined
                if( !getDevicePointOffsetTypeEqual(2, DemandAccumulatorPointType ) )
                {
                    OutMessage->Buffer.BSt.Length -= 2;
                }
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK ||
             parse.getFlags() & CMD_FLAG_GV_MINMAX )
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
        {
            function = EmetconProtocol::GetValue_FrozenPeakDemand;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            function = EmetconProtocol::GetValue_PeakDemand;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }

        //  ACH:  minimize request length someday, like below
    }
    else if( parse.getFlags() & CMD_FLAG_GV_VOLTAGE )
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
        {
            function = EmetconProtocol::GetValue_FrozenVoltage;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            function = EmetconProtocol::GetValue_Voltage;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_KWH ||
             parse.getFlags() & CMD_FLAG_GV_USAGE )
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
        {
            function = EmetconProtocol::GetValue_FrozenKWH;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            function = EmetconProtocol::GetValue_KWH;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }

        int channels = 0;  //  so we'll bypass the point-cropping code if /channels/ doesn't get set by the following:

        if( getType() == TYPEMCT310 || getType() == TYPEMCT310ID || getType() == TYPEMCT310IDL || getType() == TYPEMCT310IL ||
            getType() == TYPEMCT318 || getType() == TYPEMCT318L  || getType() == TYPEMCT360    || getType() == TYPEMCT370 )
        {
            channels = Mct31xDevice::ChannelCount;
        }
        else if( isMct410(getType()) || isMct420(getType()) || isMct440(getType()) )
        {
            channels = Mct410Device::ChannelCount;
        }
        else if( getType() == TYPEMCT470 || isMct430(getType()) )
        {
            channels = Mct470Device::ChannelCount;
        }

        //  "getvalue kwh" is the short-form request for the MCT-410;  "getvalue usage" is the long form.
        //    I don't like this type-specific code in the base class...  but I also don't want to add a virtual function for just this.
        if( (isMct410(getType()) || isMct420(getType()) || isMct440(getType())) && parse.getFlags() & CMD_FLAG_GV_KWH )
        {
            OutMessage->Buffer.BSt.Length -= 6;
        }
        else
        {
            for( int i = channels; i > 1; i-- )
            {
                if( getDevicePointOffsetTypeEqual(i, PulseAccumulatorPointType ) )
                {
                    break;
                }

                OutMessage->Buffer.BSt.Length -= 3;
            }
        }

        // If we have requested 3 or less bytes for a "getvalue kwh" read and our CPARM tells us so - pad
        // the length to 4 so we get back 2 Dwords instead of 1.  The hope is that this will help detect
        // crosstalk.
        if( OutMessage->Buffer.BSt.Length <= 3  &&
            parse.getFlags() & CMD_FLAG_GV_KWH  &&
            gConfigParms.isTrue("PORTER_MCT_PAD_KWH_READ") )
        {
            OutMessage->Buffer.BSt.Length = 4;
        }
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }


    return nRet;
}

YukonError_t MctDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    long   rawPulses;
    double reading;

    INT function = -1;

    bool found = false;

    if( parse.isKeyValid("kyz") )
    {
        switch( parse.getiValue("kyz_offset") )
        {
            case 1:     function = EmetconProtocol::PutValue_KYZ;   break;
            case 2:     function = EmetconProtocol::PutValue_KYZ2;  break;
            case 3:     function = EmetconProtocol::PutValue_KYZ3;  break;
        }

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if(parse.isKeyValid("reset") || !parse.isKeyValid("kyz_reading"))
            {
                reading = 0;
            }
            else
            {
                reading = parse.getdValue("kyz_reading");
            }

            CtiPointSPtr tmpPoint = getDevicePointOffsetTypeEqual(parse.getiValue("kyz_offset"), PulseAccumulatorPointType);

            if( tmpPoint && tmpPoint->getType() == PulseAccumulatorPointType)
            {
                rawPulses = (int)(reading / boost::static_pointer_cast<CtiPointAccumulator>(tmpPoint)->getMultiplier());
            }
            else
            {
                rawPulses = (int)reading;
            }


            //  copy the reading into the output buffer, MSB style
            for(int i = 0; i < 3; i++)
            {
                OutMessage->Buffer.BSt.Message[i]   = (rawPulses >> ((2-i)*8)) & 0xFF;
                OutMessage->Buffer.BSt.Message[i+3] = (rawPulses >> ((2-i)*8)) & 0xFF;
                OutMessage->Buffer.BSt.Message[i+6] = (rawPulses >> ((2-i)*8)) & 0xFF;
            }
        }
    }
    else if( parse.isKeyValid("power") && parse.isKeyValid("reset") )
    {
        function = EmetconProtocol::PutValue_ResetPFCount;
        found = getOperation(function, OutMessage->Buffer.BSt);

        //  set the outgoing bytes to 0
        for(int i = 0; i < OutMessage->Buffer.BSt.Length; i++)
        {
            OutMessage->Buffer.BSt.Message[i] = 0;
        }
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }


    return nRet;
}

YukonError_t MctDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    CHAR Temp[80];

    INT function;

    if(parse.getFlags() & CMD_FLAG_GS_DISCONNECT)          // Read the disconnect status
    {
        function = EmetconProtocol::GetStatus_Disconnect;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_INTERNAL)
    {
        function = EmetconProtocol::GetStatus_Internal;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_LOADPROFILE)
    {
        function = EmetconProtocol::GetStatus_LoadProfile;

        found = getOperation(function, OutMessage->Buffer.BSt);

        if( found && parse.isKeyValid("loadprofile_offset") )
        {
            if( getType() == TYPEMCT470 || isMct430(getType()) )
            {
                if( parse.getiValue("loadprofile_offset") == 1 ||
                    parse.getiValue("loadprofile_offset") == 2 )
                {
                    OutMessage->Buffer.BSt.Function = Mct470Device::FuncRead_LPStatusCh1Ch2Pos;
                }
                else if( parse.getiValue("loadprofile_offset") == 3 ||
                         parse.getiValue("loadprofile_offset") == 4 )
                {
                    OutMessage->Buffer.BSt.Function = Mct470Device::FuncRead_LPStatusCh3Ch4Pos;
                }
                else
                {
                    found = false;
                }
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GS_IED)
    {
        if(parse.getFlags() & CMD_FLAG_GS_LINK)
        {
            function = EmetconProtocol::GetStatus_IEDLink;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else if(parse.isKeyValid("ied_dnp"))
        {
            function = EmetconProtocol::GetStatus_IEDDNP;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    else //  if(parse.getFlags() & CMD_FLAG_GS_EXTERNAL) - default command
    {
        function = EmetconProtocol::GetStatus_External;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t MctDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool  found = false;
    YukonError_t nRet = ClientErrors::None;
    BSTRUCT &BSt = OutMessage->Buffer.BSt;

    INT function = -1;

    if( parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = EmetconProtocol::PutStatus_Reset;
        found = getOperation(function, BSt);

        unsigned char *begin = BSt.Message;
        unsigned char *end   = BSt.Message + std::min<USHORT>(BSt.Length, BSTRUCT::MessageLength_Max);

        std::fill(begin, end,  0);
    }
    else if( parse.getFlags() & CMD_FLAG_PS_RESETOVERRIDE )
    {
        function = EmetconProtocol::PutStatus_ResetOverride;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("freeze") )
    {
        if( parse.isKeyValid("voltage") )
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = EmetconProtocol::PutStatus_FreezeVoltageOne;
                found = getOperation(function, OutMessage->Buffer.BSt);

                //  set expected voltage freeze here
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = EmetconProtocol::PutStatus_FreezeVoltageTwo;
                found = getOperation(function, OutMessage->Buffer.BSt);

                //  set expected voltage freeze here
            }
        }
        else
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = EmetconProtocol::PutStatus_FreezeOne;
                found = getOperation(function, OutMessage->Buffer.BSt);

                setExpectedFreeze(1);
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = EmetconProtocol::PutStatus_FreezeTwo;
                found = getOperation(function, OutMessage->Buffer.BSt);

                setExpectedFreeze(2);
            }
        }
    }
    else if( parse.isKeyValid("peak") )
    {
        if( parse.getiValue("peak") == TRUE )
        {
            function = EmetconProtocol::PutStatus_PeakOn;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            function = EmetconProtocol::PutStatus_PeakOff;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
    }


    if(!found)
    {
       nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
       OutMessage->Sequence  = function;     // Helps us figure it out later!

       OutMessage->Request.RouteID   = getRouteID();

       //  fix/ach this, it's ugly
       if( OutMessage->Buffer.BSt.Function == 0x06 )  //  easiest way to tell it's an MCT3xx
       {
           OUTMESS *tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

           //  reset power fail
           tmpOutMess->Buffer.BSt.Function = 0x50;
           tmpOutMess->Buffer.BSt.Length   =    0;

           outList.push_back(tmpOutMess);

           tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

           //  reset encoder error
           tmpOutMess->Buffer.BSt.Function = 0x58;
           tmpOutMess->Buffer.BSt.Length   =    0;

           outList.push_back(tmpOutMess);
       }

       strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t MctDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;
    YukonError_t nRet = ClientErrors::None;
    string temp;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    if(parse.isKeyValid("model"))
    {
        function = EmetconProtocol::GetConfig_Model;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("ied"))
    {
        if(parse.isKeyValid("time"))
        {
            function = EmetconProtocol::GetConfig_IEDTime;
            found    = getOperation(function, OutMessage->Buffer.BSt);
        }
        else if( parse.isKeyValid("scan"))
        {
            function = EmetconProtocol::GetConfig_IEDScan;
            found    = getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    else if( parse.isKeyValid("channels") )
    {
        function = EmetconProtocol::GetConfig_ChannelSetup;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("options"))
    {
        function = EmetconProtocol::GetConfig_Options;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("address_group"))
    {
        function = EmetconProtocol::GetConfig_GroupAddress;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("time"))
    {
        if(parse.isKeyValid("sync"))
        {
            function = EmetconProtocol::GetConfig_TSync;
        }
        else
        {
            function = EmetconProtocol::GetConfig_Time;
        }

        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("multiplier"))
    {
        if( parse.isKeyValid("multchannel") )
        {
            switch( parse.getiValue("multchannel") )
            {
                case 1:     function = EmetconProtocol::GetConfig_Multiplier;   break;
                case 2:     function = EmetconProtocol::GetConfig_Multiplier2;  break;
                case 3:     function = EmetconProtocol::GetConfig_Multiplier3;  break;
                case 4:     function = EmetconProtocol::GetConfig_Multiplier4;  break;
                default:    function = -1;
            }
        }
        else
        {
            function = EmetconProtocol::GetConfig_Multiplier;
        }

        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("interval"))
    {
        temp = parse.getsValue("interval");

        if( temp == "intervals" )
        {
            function = EmetconProtocol::GetConfig_Intervals;
        }
        else if( temp == "lp" )
        {
            function = EmetconProtocol::GetConfig_LoadProfileInterval;
        }
        else if( temp == "li" )
        {
            function = EmetconProtocol::GetConfig_DemandInterval;
        }
        else
        {
            function = EmetconProtocol::DLCCmd_Invalid;

            CTILOG_ERROR(dout, "invalid interval type \""<< temp << "\"");
        }

        if( function != EmetconProtocol::DLCCmd_Invalid )
        {
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    //  needs to be moved to the 4xx base class when the inheritance gets reworked
    else if(parse.isKeyValid("holiday"))
    {
        function = EmetconProtocol::GetConfig_Holiday;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    //  get raw memory locations
    else if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;
        }

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        OutMessage->Buffer.BSt.Length   = std::min(parse.getiValue("rawlen", 13), 13);  //  default (and maximum) is 13 bytes
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;     // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}

YukonError_t MctDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool  found = false;
    INT   function;
    YukonError_t nRet = ClientErrors::None;
    int   intervallength;
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.RetryMacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.GrpMsgID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("install") )
    {
        //  does a read of 2 bytes or so
        function = EmetconProtocol::PutConfig_Install;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("armc") )
    {
        function = EmetconProtocol::PutConfig_ARMC;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("arml") )
    {
        function = EmetconProtocol::PutConfig_ARML;
        found    = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("onoffpeak") )
    {
        function = EmetconProtocol::PutConfig_OnOffPeak;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Message[0] = 0xf8 & ~0x04;  //  make sure the 0x04 bit is NOT set
    }
    else if( parse.isKeyValid("minmax") )
    {
        function = EmetconProtocol::PutConfig_MinMax;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Message[0] = 0xf8 |  0x04;  //  make sure the 0x04 bit is set
    }
    else if( parse.isKeyValid("groupaddress_enable") )
    {
        if( parse.getiValue("groupaddress_enable") == 0 )
        {
            function = EmetconProtocol::PutConfig_GroupAddressInhibit;
        }
        else
        {
            function = EmetconProtocol::PutConfig_GroupAddressEnable;
        }

        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("address") )
    {
        if( parse.isKeyValid("uniqueaddress") )
        {
            int uadd;

            function = EmetconProtocol::PutConfig_UniqueAddress;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            uadd = parse.getiValue("uniqueaddress");

            if( uadd > 0x3fffff || uadd < 0 )
            {
                found = false;

                if( errRet )
                {
                    temp = "Invalid address \"" + CtiNumStr(uadd) + string("\" for device \"") + getName() + "\", not sending";
                    errRet->setResultString(temp);
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back(errRet);
                    errRet = NULL;
                }
            }
            else if( getAddress() != TestAddress1 && getAddress() != TestAddress2 )
            {
                found = false;

                if( errRet )
                {
                    temp = "Device must be set to one of the test addresses, not sending";
                    errRet->setResultString(temp);
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back(errRet);
                    errRet = NULL;
                }
            }
            else
            {
                OutMessage->Buffer.BSt.Message[0] = ( uadd >> 16) & 0x0000ff;
                OutMessage->Buffer.BSt.Message[1] = ( uadd >>  8) & 0x0000ff;
                OutMessage->Buffer.BSt.Message[2] = ( uadd      ) & 0x0000ff;
                OutMessage->Buffer.BSt.Message[3] = (~uadd >> 16) & 0x0000ff;
                OutMessage->Buffer.BSt.Message[4] = (~uadd >>  8) & 0x0000ff;
                OutMessage->Buffer.BSt.Message[5] = (~uadd      ) & 0x0000ff;
            }
        }
        else if( parse.isKeyValid("groupaddress_gold") && parse.isKeyValid("groupaddress_silver") )
        {
            int gold, silver;

            function = EmetconProtocol::PutConfig_GroupAddress_GoldSilver;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            gold   = parse.getiValue("groupaddress_gold");
            silver = parse.getiValue("groupaddress_silver");

            if( gold   >= 1 && gold   < 5 &&
                silver >= 1 && silver < 61 )
            {
                //  zero-based in the meter
                gold--;
                silver--;

                OutMessage->Buffer.BSt.Message[0] = (gold << 6) | silver;
            }
            else
            {
                found = false;

                if( errRet )
                {
                    temp = "Bad address specification - Acceptable values:  Gold: [0-3], Silver [0-59]";
                    errRet->setResultString( temp );
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else if( parse.isKeyValid("groupaddress_bronze") )
        {
            int bronze;

            function = EmetconProtocol::PutConfig_GroupAddress_Bronze;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            bronze = parse.getiValue("groupaddress_bronze");

            if( bronze >= 1 && bronze < 257 )
            {
                //  zero-based in the meter
                bronze--;

                OutMessage->Buffer.BSt.Message[0] = bronze;
            }
            else
            {
                found = false;

                if( errRet )
                {
                    temp = "Bad address specification - Acceptable values:  Bronze: [0-255]";
                    errRet->setResultString( temp );
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else if( parse.isKeyValid("groupaddress_lead_meter") && parse.isKeyValid("groupaddress_lead_load") )
        {
            int lead_load, lead_meter;

            function = EmetconProtocol::PutConfig_GroupAddress_Lead;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            lead_load  = parse.getiValue("groupaddress_lead_load");
            lead_meter = parse.getiValue("groupaddress_lead_meter");

            if( lead_load  >= 1 && lead_load  < 4097 &&
                lead_meter >= 1 && lead_meter < 4097 )
            {
                //  zero-based in the meter
                lead_load--;
                lead_meter--;

                OutMessage->Buffer.BSt.Message[0] =   lead_load  & 0x00ff;
                OutMessage->Buffer.BSt.Message[1] =   lead_meter & 0x00ff;
                OutMessage->Buffer.BSt.Message[2] = ((lead_load  & 0x0f00) >> 4) |
                                                    ((lead_meter & 0x0f00) >> 8);
            }
            else
            {
                found = false;

                if( errRet )
                {
                    temp = "Bad address specification - Acceptable values:  Bronze: [0-255]";
                    errRet->setResultString( temp );
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
    }
    else if( parse.isKeyValid("ied") )
    {
        if( parse.isKeyValid("scan") )
        {
            int scantime, scandelay;

            function = EmetconProtocol::PutConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt);

            scantime  = parse.getiValue("scan");
            scandelay = parse.getiValue("scandelay");

            if( scantime > 3780 )  //  250 * 15 + 30 -- 250 seems a reasonable 1-byte max
                scantime = 300;  //  they are crazy - set to default

            if( scantime < 30 )  //  minimum is 30
                scantime = 30;

            scantime -= 30;
            scantime /= 15;

            if( scandelay > 3780 )  //  252 * 15 - max?
                scandelay = 120;    //  set to default

            scandelay /= 15;

            //  dsm/2 defaults - for future reference
            /*
                OutMessage->Buffer.BSt.Message[0] = 18;  //  set to scan every 300 seconds
                OutMessage->Buffer.BSt.Message[1] = 8;   //  set delay to 120 seconds
             */

            OutMessage->Buffer.BSt.Message[0] = scantime  & 0xff;  //  shouldn't be > 255, but i'll press the point
            OutMessage->Buffer.BSt.Message[1] = scandelay & 0xff;  //  ditto
        }
        else if( parse.isKeyValid("class") )
        {
            int classnum, classoffset;
            int iedtype = ((Mct31xDevice *)this)->getIEDPort().getIEDType();

            function = EmetconProtocol::PutConfig_IEDClass;
            found = getOperation(function, OutMessage->Buffer.BSt);

            classnum    = parse.getiValue("class");
            classoffset = parse.getiValue("classoffset");

            //  dsm/2 defaults
            /*
            if (RequestParam[0] == '\0') {
                DataBuffer[0] = 0;   //  len default to 0 which is 128 in MCT
                DataBuffer[1] = 0;
                DataBuffer[2] = 2;   //  Offset is 2 for frozen data
                DataBuffer[3] = 72;  //  set to read rules class
            }
             */

            switch( iedtype )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    if( classnum > 0xff )  //  fix?
                        classnum = 0;

                    if( classoffset > 0xffff )
                        classoffset = 0;

                    if( iedtype == CtiTableDeviceMCTIEDPort::AlphaPowerPlus )
                    {
                        if( classnum == 0 )
                            classnum = 72;  //  default to class 72 for an Alpha

                        if( classnum == 72 && classoffset == 0 )  //  do not allow 72 to have a 0 offset
                            classoffset = 2;
                    }

                    OutMessage->Buffer.BSt.Message[0] = 0;  //  128 len in MCT
                    OutMessage->Buffer.BSt.Message[1] = (classoffset >> 8) & 0xff;
                    OutMessage->Buffer.BSt.Message[2] =  classoffset       & 0xff;
                    OutMessage->Buffer.BSt.Message[3] =  classnum;

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    //  note that this is different from the above
                    OutMessage->Buffer.BSt.Length = 6;

                    if( classoffset > 0xffffff )  //  fix?
                        classoffset = 0;

                    if( classnum > 0xffff )
                        classnum = 0;

                    OutMessage->Buffer.BSt.Message[0] = 0;  //  128 len in MCT
                    OutMessage->Buffer.BSt.Message[1] = (classoffset & 0xff0000) >> 16;
                    OutMessage->Buffer.BSt.Message[2] = (classoffset & 0x00ff00) >>  8;
                    OutMessage->Buffer.BSt.Message[3] = (classoffset & 0x0000ff);
                    OutMessage->Buffer.BSt.Message[4] = (classnum & 0xff00) >> 8 ;
                    OutMessage->Buffer.BSt.Message[5] = (classnum & 0x00ff);

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "Unknown IED type " << iedtype << " for device \"" << getName() << "\", aborting command");

                    found = false;
                }
            }
        }
    }
    else if(parse.isKeyValid("interval"))
    {
        temp = parse.getsValue("interval");

        if( temp == "intervals" )
        {
            function = EmetconProtocol::PutConfig_Intervals;
            found = getOperation(function, OutMessage->Buffer.BSt);

            if( isMct410(getType()) || isMct420(getType()) || isMct440(getType()) )
            {
                OutMessage->Buffer.BSt.Message[0] = getLoadProfile()->getLastIntervalDemandRate() / 60;
                OutMessage->Buffer.BSt.Message[1] = getLoadProfile()->getLoadProfileDemandRate()  / 60;
                OutMessage->Buffer.BSt.Message[2] = getLoadProfile()->getVoltageDemandInterval()  / 15;
                OutMessage->Buffer.BSt.Message[3] = getLoadProfile()->getVoltageProfileRate()     / 60;
            }
            else
            {
                OutMessage->Buffer.BSt.Message[0] = getLoadProfile()->getLastIntervalDemandRate() / 60;
                OutMessage->Buffer.BSt.Message[1] = getLoadProfile()->getLoadProfileDemandRate()  / 60;
                OutMessage->Buffer.BSt.Message[2] = getLoadProfile()->getLoadProfileDemandRate()  / 60;
            }
        }
        else if( temp == "lp" )
        {
            function = EmetconProtocol::PutConfig_LoadProfileInterval;
            found = getOperation(function, OutMessage->Buffer.BSt);
            switch( getLoadProfile()->getLoadProfileDemandRate() / 60 )
            {
                case 5:
                    break;

                case 15:
                    OutMessage->Buffer.BSt.Function += 1;
                    break;

                case 30:
                    OutMessage->Buffer.BSt.Function += 2;
                    break;

                case 60:
                    OutMessage->Buffer.BSt.Function += 3;
                    break;

                default:
                    //  incorrect intervallength specified on the command line
                    found = false;
                    if( errRet )
                    {
                        temp = "Invalid Load Profile interval length - must be 5, 15, 30, or 60 min";
                        errRet->setResultString( temp );
                        errRet->setStatus(ClientErrors::NoMethod);
                        retList.push_back( errRet );
                        errRet = NULL;
                    }
                    break;
            }
        }
        else if( temp == "li" )
        {
            intervallength = getDemandInterval() / 60;

            if( intervallength >=  1 &&
                intervallength <= 60 )
            {
                //  This code may be added back someday, but it's pointless for now - we didn't even construct the appropriate commands
                //    yet.  The MCT22x's demand interval/readings are for internal use only, and not directly readable from the outside.
                //    So we have to make do with the 5-minute-subtraction dealy in the getvalue demand.
/*                if( getType() == TYPEMCT212 ||
                    getType() == TYPEMCT224 ||
                    getType() == TYPEMCT226 )
                {
                    switch( intervallength )
                    {
                        case 5:
                        case 15:
                        case 30:
                        case 60:
                            intervallength /= 5;  //  22x are in multiples of 5 mins
                            break;

                        default:
                            found = false;
                            intervallength = 0;
                            if( errRet )
                            {
                                temp = "Invalid Demand interval length - must be 5, 15, 30, or 60 min";
                                errRet->setResultString( temp );
                                errRet->setStatus(NoMethod);
                                retList.push_back( errRet );
                                errRet = NULL;
                            }
                    }
                }
                else*/
                if( !isMct410(getType()) && !isMct420(getType()) && !isMct440(getType()) )
                {
                    intervallength *= 4;  //  all else are in multiples of 15 seconds
                }

                if( intervallength )
                {
                    function = EmetconProtocol::PutConfig_DemandInterval;
                    found = getOperation(function, OutMessage->Buffer.BSt);
                    OutMessage->Buffer.BSt.Message[0] = intervallength;
                }
            }
            else
            {
                found = false;
                if( errRet )
                {
                    temp = "Invalid Demand interval length - must be between 1 and 60 min";
                    errRet->setResultString( temp );
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Invalid Demand Interval \""<< temp <<"\"");
        }
    }
    else if(parse.isKeyValid("timesync"))
    {
        function = EmetconProtocol::PutConfig_TSync;
        found = getOperation(function, OutMessage->Buffer.BSt);

        // the message is filled in by RefreshMCTTimeSync() in porttime.cpp
    }
    else if(parse.isKeyValid("multiplier"))
    {
        unsigned long multbytes;

        multbytes  = (unsigned long)(parse.getdValue("multiplier") * 100.0);

        if( multbytes == 100 )
        {
            multbytes = 1000;  //  change it into the "pulses" value
        }
        else if( multbytes >= 1000 )
        {
            if( errRet )
            {
                temp = "Multiplier too large - must be less than 10";
                errRet->setResultString(temp);
                errRet->setStatus(ClientErrors::NoMethod);
                retList.push_back(errRet);
                errRet = NULL;
            }
        }

        OutMessage->Buffer.BSt.Message[0] = (multbytes >> 8) & 0xFF;  //  bits 15-8
        OutMessage->Buffer.BSt.Message[1] =  multbytes       & 0xFF;  //  bits  7-0

        switch( parse.getiValue("multoffset") )
        {
            default:
            case 1:
                function = EmetconProtocol::PutConfig_Multiplier;
                found = getOperation(function, OutMessage->Buffer.BSt);
                break;

            case 2:
                function = EmetconProtocol::PutConfig_Multiplier2;
                found = getOperation(function, OutMessage->Buffer.BSt);
                break;

            case 3:
                function = EmetconProtocol::PutConfig_Multiplier3;
                found = getOperation(function, OutMessage->Buffer.BSt);
                break;
        }
    }
    else if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        temp = parse.getsValue("rawdata");

        //  trim string to be 15 bytes long
        if( temp.length() > 15 )
        {
            temp.erase( 15, 1 );
        }

        OutMessage->Buffer.BSt.Length = temp.length();
        for( int i = 0; i < temp.length(); i++ )
        {
            OutMessage->Buffer.BSt.Message[i] = temp[i];
        }

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;
        }
    }

    if( errRet )
    {
        delete errRet;
        errRet = NULL;
    }

    if(!found)
    {
       nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
       OutMessage->Sequence  = function;     // Helps us figure it out later!

       OutMessage->Request.RouteID   = getRouteID();
       strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


YukonError_t MctDevice::executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;

    YukonError_t nRet = ClientErrors::None;

    INT function;

    if(parse.getFlags() & CMD_FLAG_CTL_SHED)
    {
        int shed_duration, relay_mask;

        shed_duration = parse.getiValue("shed");
        relay_mask    = parse.getiValue("relaymask") & 0x0f;

        function = EmetconProtocol::Control_Shed;

        if(getOperation(function, OutMessage->Buffer.BSt))
        {
            //  if at least one of relays a-d (1-4) are selected
            //  this needs better error-handling - better printouts
            if( relay_mask )
            {
                if( shed_duration > 0 )
                {
                    int shed_function;

                    if(      shed_duration <=  450 )    shed_function = Command_Shed_07m;
                    else if( shed_duration <=  900 )    shed_function = Command_Shed_15m;
                    else if( shed_duration <= 1800 )    shed_function = Command_Shed_30m;
                    else                                shed_function = Command_Shed_60m;

                    OutMessage->Buffer.BSt.Function = shed_function | relay_mask;

                    found = true;
                }
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_RESTORE)
    {
        function = EmetconProtocol::Control_Restore;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & (CMD_FLAG_CTL_CONNECT | CMD_FLAG_CTL_CLOSE) )
    {
        function = EmetconProtocol::Control_Connect;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( disconnectRequiresCollar() )
        {
            OutMessage->MessageFlags |= MessageFlag_AddMctDisconnectSilence;
        }
    }
    else if( parse.getFlags() & (CMD_FLAG_CTL_DISCONNECT | CMD_FLAG_CTL_OPEN) )
    {
        function = EmetconProtocol::Control_Disconnect;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( disconnectRequiresCollar() )
        {
            OutMessage->MessageFlags |= MessageFlag_AddMctDisconnectSilence;

            //  do not allow the disconnect command to be sent to a meter that has no disconnect address
            if( !_disconnectAddress )
            {
                std::auto_ptr<CtiReturnMsg> ReturnMsg(
                        new CtiReturnMsg(getID(), OutMessage->Request.CommandStr));

                ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                ReturnMsg->setResultString(getName() + " / Disconnect command cannot be sent to an empty (zero) address");

                // Error is handled here, put it on the ret list and get out of here!
                // Note this bypasses the later setting to error, ect...
                retMsgHandler( OutMessage->Request.CommandStr, ClientErrors::NoDisconnect, ReturnMsg.release(), vgList, retList, false );

                delete OutMessage;
                OutMessage = NULL;

                return ClientErrors::None;
            }
        }
    }
    else if(parse.isKeyValid("latch_relays"))
    {
        string relays = parse.getsValue("latch_relays");

        function = EmetconProtocol::Control_Latch;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        //  binary logic - 00, 01, 10, 11
        //    latch_relays may also contain "none", but that's the default case and doesn't need to be explicitly handled
        if( relays.find("(a)") != string::npos )  OutMessage->Buffer.BSt.Function += 1;
        if( relays.find("(b)") != string::npos )  OutMessage->Buffer.BSt.Function += 2;
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        outList.push_back( OutMessage );

        OutMessage = NULL;
    }

    return nRet;
}


YukonError_t MctDevice::decodeLoopback(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    resultString = getName( ) + " / successful ping";
    ReturnMsg->setResultString( resultString );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t MctDevice::decodeGetValue(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg  *pData     = NULL;
    CtiPointSPtr      pPoint;

    double Value;
    string resultStr;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::GetValue_PFCount:
        {
            int pfCount, i;

            pfCount = 0;

            for(i = 0; i < 2; i++)
            {
                pfCount = (pfCount << 8) + DSt.Message[i];
            }

            if( (pPoint = getDevicePointOffsetTypeEqual( PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType )) )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pfCount);

                string pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                         boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), (double)Value, NormalQuality, PulseAccumulatorPointType, pointString);
                if(pData != NULL)
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultStr = getName() + " / Blink Counter = " + CtiNumStr(pfCount);

                ReturnMsg->setResultString( resultStr );
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t MctDevice::decodeGetConfig(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt   = InMessage.Buffer.DSt;
    CtiCommandParser parse(InMessage.Return.CommandStr);

    int min, sec, channel;
    unsigned long multnum;  //  multiplier numerator - the value returned is multiplier * 1000
    double mult;  //  where the final multiplier value will be computed

    string resultStr;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    switch( InMessage.Sequence )
    {
        //  needs to be moved to the 4xx base class when the inheritance gets reworked
        case EmetconProtocol::GetConfig_Holiday:
        {
            unsigned long timestamp;
            CtiTime holiday;
            string result;

            result  = getName() + " / Holiday schedule:\n";

            timestamp = DSt.Message[0] << 24 |
                        DSt.Message[1] << 16 |
                        DSt.Message[2] << 8  |
                        DSt.Message[3];

            holiday = CtiTime(timestamp);
            result += "Holiday 1: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

            timestamp = DSt.Message[4] << 24 |
                        DSt.Message[5] << 16 |
                        DSt.Message[6] << 8  |
                        DSt.Message[7];

            holiday = CtiTime(timestamp);
            result += "Holiday 2: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

            timestamp = DSt.Message[8]  << 24 |
                        DSt.Message[9]  << 16 |
                        DSt.Message[10] << 8  |
                        DSt.Message[11];

            holiday = CtiTime(timestamp);
            result += "Holiday 3: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

            ReturnMsg->setResultString( result );

            break;
        }
        case EmetconProtocol::GetConfig_GroupAddress:
        {
            long gold, silver, bronze, lead_load, lead_meter;

            bronze     = DSt.Message[0];
            lead_load  = ((DSt.Message[3] & 0xf0) << 4) | DSt.Message[1];
            lead_meter = ((DSt.Message[3] & 0x0f) << 8) | DSt.Message[2];
            gold       = (DSt.Message[4] & 0xc0) >> 6;
            silver     = (DSt.Message[4] & 0x3f);

            resultStr  = getName() + " / Group Addresses:\n";
            resultStr += "Gold:       " + CtiNumStr(gold + 1).spad(5) + string("\n");
            resultStr += "Silver:     " + CtiNumStr(silver + 1).spad(5) + string("\n");
            resultStr += "Bronze:     " + CtiNumStr(bronze + 1).spad(5) + string("\n");
            resultStr += "Lead Meter: " + CtiNumStr(lead_meter + 1).spad(5) + string("\n");
            resultStr += "Lead Load:  " + CtiNumStr(lead_load + 1).spad(5) + string("\n");

            ReturnMsg->setResultString( resultStr );

            break;
        }

        case EmetconProtocol::GetConfig_DemandInterval:
        {
            //  see MCT22X ResultDecode for an additional MCT22X step

            sec = DSt.Message[0] * 15;

            min = sec / 60;
            sec = sec % 60;

            resultStr = getName() + " / Demand Interval: " + CtiNumStr( min ) + " min";
            if( sec )
                resultStr += ", " + CtiNumStr( sec ) + string(" sec");

            ReturnMsg->setResultString( resultStr );

            break;
        }

        case EmetconProtocol::GetConfig_LoadProfileInterval:
        {
            min = DSt.Message[0] * 5;

            resultStr = getName() + " / Load Profile Interval: " + CtiNumStr( min ) + " min";

            ReturnMsg->setResultString( resultStr );

            break;
        }

        case EmetconProtocol::GetConfig_Multiplier:
        case EmetconProtocol::GetConfig_Multiplier2:
        case EmetconProtocol::GetConfig_Multiplier3:
        case EmetconProtocol::GetConfig_Multiplier4:
        {
            resultStr  = getName() + " / ";

            if( parse.isKeyValid("multchannel") )
            {
                channel = parse.getiValue("multchannel");
                resultStr += "channel " + CtiNumStr( channel );
            }


            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout,
                        endl <<"message[0] = "<< (int)DSt.Message[0] <<
                        endl <<"message[1] = "<< (int)DSt.Message[1]
                        );
            }

            multnum   = (int)DSt.Message[0];
            multnum <<= 8;
            multnum  |= (int)DSt.Message[1];

            if( multnum == 1000 )
            {
                resultStr += " multiplier: 1.000 (pulses)\n";
            }
            else
            {
                mult = (double)multnum;
                mult /= 100.0;
                resultStr += " multiplier: " + CtiNumStr::CtiNumStr(mult, 3) + string("\n");
            }

            ReturnMsg->setResultString( resultStr );

            break;
        }

        case EmetconProtocol::GetConfig_Time:
        case EmetconProtocol::GetConfig_TSync:
        {
            char days[8][4] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat","???"};
            unsigned char ticper12hr, ticper5min, ticper15sec;
            int day, hour, minute;

            //  time values are kept by 3 decrementing counters.
            //    Message[2] decrements once every 12 hours, and starts from 14 (7 days left, denoting Sunday midnight).
            //    Message[1] decrements once every 5 minutes, and starts from 144 (12 hours left).
            //    Message[0] decrements once every 15 seconds, and starts from 20 (5 minutes left).

            ticper12hr  = 14  - DSt.Message[2];  //  invert counter to be how many units have PASSED,
            ticper5min  = 144 - DSt.Message[1];  //    NOT how many are LEFT.
            ticper15sec = 20  - DSt.Message[0];  //

            day = (ticper12hr * 12) / 24;       //  find how many days have passed
            if( day > 7 )
                day = 7;

            hour  = (ticper5min * 5) / 60;      //  find out how many hours have passed
            hour += (ticper12hr * 12) % 24;     //    add on 12 hours if in PM (ticper12hr = 0 - Sunday AM, 1 - Sunday PM, etc)

            minute  = (ticper5min * 5) % 60;    //  find out how many minutes have passed
            minute += (ticper15sec * 15) / 60;  //    add on the 15 second timer - divide by 4 to get minutes

            if( InMessage.Sequence == EmetconProtocol::GetConfig_Time )
            {
                resultStr = getName() + " / time:  ";
            }
            else if( InMessage.Sequence == EmetconProtocol::GetConfig_TSync )
            {
                resultStr = getName() + " / time sync:  ";
            }

            resultStr += string(days[day]) + " " + CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2);

            ReturnMsg->setResultString( resultStr );

            break;
        }

        case EmetconProtocol::GetConfig_Raw:
        {
            int rawloc, rawlen;

            rawloc = parse.getiValue("rawloc");

            if( parse.isKeyValid("rawlen") )
            {
                rawlen = std::min(parse.getiValue("rawlen"), 13);
            }
            else
            {
                rawlen = DSt.Length;
            }

            if( parse.isKeyValid("rawfunc") )
            {
                for( int i = 0; i < rawlen; i++ )
                {
                    resultStr += getName( ) +
                                    " / FR " + CtiNumStr(parse.getiValue("rawloc")).xhex().zpad(2) +
                                    " byte " + CtiNumStr(i).zpad(2) +
                                    " : " + CtiNumStr((int)DSt.Message[i]).xhex().zpad(2) + "\n";
                }
            }
            else
            {
                for( int i = 0; i < rawlen; i++ )
                {
                    resultStr += getName( ) +
                                    " / byte " + CtiNumStr(i+rawloc).xhex().zpad(2) +
                                    " : " + CtiNumStr((int)DSt.Message[i]).xhex().zpad(2) + "\n";
                }
            }

            ReturnMsg->setResultString( resultStr );

            break;
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t MctDevice::decodeGetStatusDisconnect(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt   = InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;
    CtiPointSPtr     pPoint;

    double    Value;
    string resultStr, defaultStateName;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    Value = STATE_CLOSED;

    switch( getType() )
    {
        case TYPEMCT213:
        {
            switch( DSt.Message[0] & 0xc0 )
            {
                case Mct210Device::MCT210_StatusConnected:     Value = STATE_CLOSED;  defaultStateName = "Connected";      break;
                case Mct210Device::MCT210_StatusDisconnected:  Value = STATE_OPENED;  defaultStateName = "Disconnected";   break;
                default:  Value = -1;
            }

            break;
        }
        case TYPEMCT310ID:
        case TYPEMCT310IDL:
        {
            switch( DSt.Message[0] & 0xc0 )
            {
                case Mct310Device::MCT310_StatusConnected:           Value = STATE_CLOSED;         defaultStateName = "Connected";             break;
                case Mct310Device::MCT310_StatusConnectArmed:        Value = STATE_INDETERMINATE;  defaultStateName = "Connect armed";         break;
                case Mct310Device::MCT310_StatusConnectInProgress:   Value = STATE_INDETERMINATE;  defaultStateName = "Connect in progress";   break;
                case Mct310Device::MCT310_StatusDisconnected:        Value = STATE_OPENED;         defaultStateName = "Disconnected";          break;
            }

            break;
        }
        case TYPEMCT410CL:
        case TYPEMCT410FL:
        case TYPEMCT410GL:
        case TYPEMCT410IL:
        //case TYPEMCT420CL:  //  the MCT-420CL does not support the disconnect collar
        case TYPEMCT420CD:
        case TYPEMCT420FL:
        case TYPEMCT420FD:
        {
            switch( DSt.Message[0] & 0x03 )
            {
                case Mct410Device::RawStatus_Connected:
                {
                    Value = Mct410Device::StateGroup_Connected;                   defaultStateName = "Connected";                 break;
                }
                case Mct410Device::RawStatus_ConnectArmed:
                {
                    Value = Mct410Device::StateGroup_ConnectArmed;                defaultStateName = "Connect armed";             break;
                }
                case Mct410Device::RawStatus_DisconnectedUnconfirmed:
                {
                    Value = Mct410Device::StateGroup_DisconnectedUnconfirmed;     defaultStateName = "Unconfirmed disconnected";  break;
                }
                case Mct410Device::RawStatus_DisconnectedConfirmed:
                {
                    Value = Mct410Device::StateGroup_DisconnectedConfirmed;       defaultStateName = "Confirmed disconnected";    break;
                }
                default:
                {
                    Value = -1;
                    defaultStateName = "Invalid raw value from 410";
                }
            }

            break;
        }
        default:
        {
            Value = STATE_INDETERMINATE;
            defaultStateName = "Not a disconnect meter";
        }
    }

    pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

    if(pPoint)
    {
        //  This isn't useful when the status to be returned is anything but "connected" or "disconnected" - we need "Connect armed" instead, so this
        //    will not work too well.
        string stateName; /* = ResolveStateName(pPoint->getStateGroupID(), Value);

        if( stateName.empty() )*/
        {
            stateName = defaultStateName;
        }

        resultStr = getName() + " / " + pPoint->getName() + ":" + stateName;

        //  Send this value to requestor via retList.

        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, resultStr, TAG_POINT_MUST_ARCHIVE);

        if(pData != NULL)
        {
            ReturnMsg->PointData().push_back(pData);
            pData = NULL;
        }
    }
    else
    {
        resultStr = getName() + " / Disconnect Status: " + defaultStateName;
        ReturnMsg->setResultString(resultStr);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t MctDevice::decodeControl(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    std::auto_ptr<CtiReturnMsg> ReturnMsg(
       new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( getName( ) + " / control sent" );

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList );

    return ClientErrors::None;
}


YukonError_t MctDevice::decodeControlDisconnect(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    std::auto_ptr<CtiReturnMsg> ReturnMsg(
       new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    std::string resultString = getName( ) + " / control sent";

    std::string getstatusDisconnect_commandString =
        strstr(InMessage.Return.CommandStr, " noqueue")
        ? "getstatus disconnect noqueue"
        : "getstatus disconnect";

    std::auto_ptr<CtiRequestMsg> newReq(
        new CtiRequestMsg(
                getID(),
                getstatusDisconnect_commandString,
                InMessage.Return.UserID,
                InMessage.Return.GrpMsgID,
                getRouteID(),
                MacroOffset::none,
                0,
                InMessage.Return.OptionsField,
                InMessage.Priority));

    newReq->setConnectionHandle(InMessage.Return.Connection);

    if( const unsigned delay = getDisconnectReadDelay() )
    {
        const CtiTime readTime = TimeNow + delay;

        resultString += "\nWaiting " + CtiNumStr(delay) + " seconds to read status (until " + readTime.asString() + ")";

        newReq->setMessageTime(readTime);
    }

    retList.push_back(newReq.release());

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList, true );

    return ClientErrors::None;
}


unsigned MctDevice::getDisconnectReadDelay() const
{
    return 0;
}


bool MctDevice::disconnectRequiresCollar() const
{
    return false;
}


YukonError_t MctDevice::decodePutValue(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    std::auto_ptr<CtiReturnMsg> ReturnMsg(
        new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    ReturnMsg->setResultString(
       getName( ) + " / command complete");

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList );

    return ClientErrors::None;
}


YukonError_t MctDevice::decodePutStatus(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    resultString = getName( ) + " / command complete";
    ReturnMsg->setResultString( resultString );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t MctDevice::decodePutConfig(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    ULONG pfCount = 0;
    string resultString;
    OUTMESS *OutTemplate;
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiRequestMsg *pReq;

    bool expectMore = false;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_Parameters:                 resultString = getName() + " / Meter parameters sent";  break;

        case EmetconProtocol::PutConfig_Multiplier:
        case EmetconProtocol::PutConfig_Multiplier2:
        case EmetconProtocol::PutConfig_Multiplier3:                resultString = getName() + " / Multiplier sent"; break;

        case EmetconProtocol::PutConfig_GroupAddress_Bronze:
        case EmetconProtocol::PutConfig_GroupAddress_GoldSilver:
        case EmetconProtocol::PutConfig_GroupAddress_Lead:
        case EmetconProtocol::PutConfig_UniqueAddress:              resultString = getName() + " / Address reconfiguration sent";   break;

        case EmetconProtocol::PutConfig_GroupAddressEnable:         resultString = getName() + " / Group addressing enable sent";   break;
        case EmetconProtocol::PutConfig_GroupAddressInhibit:        resultString = getName() + " / Group addressing inhibit sent";  break;

        case EmetconProtocol::PutConfig_LoadProfileInterest:        resultString = getName() + " / Load profile period of interest sent";   break;
        case EmetconProtocol::PutConfig_DailyReadInterest:          resultString = getName() + " / Daily read period of interest sent";     break;

        case EmetconProtocol::PutConfig_Raw:                        resultString = getName() + " / Raw bytes sent";         break;
        case EmetconProtocol::PutConfig_TSync:                      resultString = getName() + " / Time sync sent";         break;
        case EmetconProtocol::PutConfig_Intervals:                  resultString = getName() + " / Intervals sent";         break;
        case EmetconProtocol::PutConfig_DemandInterval:             resultString = getName() + " / Demand interval sent";   break;
        case EmetconProtocol::PutConfig_LoadProfileInterval:        resultString = getName() + " / Load profile interval sent"; break;
        case EmetconProtocol::PutConfig_OutageThreshold:            resultString = getName() + " / Outage threshold sent";  break;
        case EmetconProtocol::PutConfig_ChannelSetup:               resultString = getName() + " / Channel config sent";    break;
        case EmetconProtocol::PutConfig_IEDClass:                   resultString = getName() + " / IED class info sent";    break;
        case EmetconProtocol::PutConfig_IEDScan:                    resultString = getName() + " / IED scan rate sent";     break;
        case EmetconProtocol::PutConfig_Disconnect:                 resultString = getName() + " / Disconnect config sent"; break;
        case EmetconProtocol::PutConfig_TOU:                        resultString = getName() + " / TOU config sent";        break;
        case EmetconProtocol::PutConfig_TimeZoneOffset:             resultString = getName() + " / Time zone sent";         break;
        case EmetconProtocol::PutConfig_Holiday:                    resultString = getName() + " / Holiday dates sent";     break;
        case EmetconProtocol::PutConfig_TOUEnable:                  resultString = getName() + " / TOU enable sent";        break;
        case EmetconProtocol::PutConfig_TOUDisable:                 resultString = getName() + " / TOU disable sent";       break;

        case EmetconProtocol::PutConfig_AlarmMask:                  resultString = getName() + " / Alarm Event Mask sent";           break;
        case EmetconProtocol::PutConfig_Options:                    resultString = getName() + " / options sent";           break;
        case EmetconProtocol::PutConfig_PhaseDetectClear:           resultString = getName() + " / Phase Detect flag clear sent";       break;
        case EmetconProtocol::PutConfig_PhaseDetect:                resultString = getName() + " / Phase Detect test settings sent";    break;
        case EmetconProtocol::PutConfig_AutoReconnect:              resultString = getName() + " / Autoreconnect settings sent";    break;

        case EmetconProtocol::PutConfig_PhaseLossThreshold:         resultString = getName() + " / Phase loss settings sent";       break;

        case EmetconProtocol::PutConfig_Install:
        {
            int sspec;
            bool sspecValid;

            //  LMT-2 sspec - it only has 1 sspec byte...
            //    make sure any additional sspec rev numbers do not have 36 as their least-significant byte,
            //    or this will have to change.
            //
            //  36, 292, 548, 804, 1060, 1316, 1572, 1828, 2084, 2340, 2596, 2852, 3108, 3364, 3620, 3876, ...

            if( DSt.Message[0] == 36 )
            {
                sspec = DSt.Message[0];
            }
            else
            {
                sspec = DSt.Message[0] + (DSt.Message[4] << 8);
            }

            //  if it's an invalid sspec or if the option bits aren't set properly
            if( !sspecIsValid(sspec) )
            {
                resultString = getName( ) + " / sspec \'" + CtiNumStr(sspec) + "\' not valid - looks like an \'" + sspecIsFrom( sspec ) + "\'." + "\n" +
                               getName( ) + " / install command aborted";
            }
            else if( (getType() == TYPEMCT310ID || getType() == TYPEMCT310IDL) && (sspec == 1007 || sspec == 153) && !(DSt.Message[2] & 0x40) )
            {
                //  if the disconnect option bit is not set
                resultString = getName( ) + " / option bits not valid - looks like a 310I";
            }
            else
            {
                if( getType( ) == TYPEMCT310    ||
                    getType( ) == TYPEMCT310ID  ||
                    getType( ) == TYPEMCT310IDL ||
                    getType( ) == TYPEMCT310IL )
                {
                    if( !(DSt.Message[2] & 0x01) )
                    {
                        resultString = getName() + " / Error:  Metering channel 1 not enabled" + "\n";
                    }
                }

                OutTemplate = new OUTMESS;

                InEchoToOut( InMessage, *OutTemplate );

                //  reset the meter
                pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putstatus reset", InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                if( pReq != NULL )
                {
                    if( strstr(InMessage.Return.CommandStr, "noqueue") )
                    {
                        pReq->setCommandString(pReq->CommandString() + " noqueue");
                    }

                    CtiCommandParser parse(pReq->CommandString());
                    beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);

                    delete pReq;
                }

                //  enable group addressing
                pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon group enable", InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                if( pReq != NULL )
                {
                    if( strstr(InMessage.Return.CommandStr, "noqueue") )
                    {
                        pReq->setCommandString(pReq->CommandString() + " noqueue");
                    }

                    CtiCommandParser parse(pReq->CommandString());
                    beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);

                    delete pReq;
                }

                //  put the load profile interval if it's a lp device
                if( isLoadProfile(getType()) )
                {
                    pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon interval lp", InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                    if( pReq != NULL )
                    {
                        if( strstr(InMessage.Return.CommandStr, "noqueue") )
                        {
                            pReq->setCommandString(pReq->CommandString() + " noqueue");
                        }

                        CtiCommandParser parse(pReq->CommandString());
                        beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);

                        delete pReq;
                    }
                }

                //  put the demand interval
                if( hasVariableDemandRate(getType(), sspec) )
                {
                    pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon interval li", InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                    if( pReq != NULL )
                    {
                        if( strstr(InMessage.Return.CommandStr, "noqueue") )
                        {
                            pReq->setCommandString(pReq->CommandString() + " noqueue");
                        }

                        CtiCommandParser parse(pReq->CommandString());
                        beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);
                        delete pReq;
                    }
                }

                //  We've already checked for the validity of this config in the setConfigData call, so it's safe to just forge ahead
                if( _configType == Config2XX )
                {
                    if( _mpkh[0] > 0 )
                    {
                        pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon mult kyz 1 " + CtiNumStr(_mpkh[0]), InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                        if( pReq != NULL )
                        {
                            if( strstr(InMessage.Return.CommandStr, "noqueue") )
                            {
                                pReq->setCommandString(pReq->CommandString() + " noqueue");
                            }

                            CtiCommandParser parse(pReq->CommandString());
                            beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);
                            delete pReq;
                        }

                        resultString += getName() + " / Sent config to MCT\n";
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "can't send MPKH \""<< _mpkh[0] <<"\" to meter \""<< getName() <<"\"");
                    }
                }
                else if( _configType == Config3XX )
                {
                    unsigned char config_byte = 0xc0;
                    int num_channels = 3;

                    if( getType() == TYPEMCT310    ||
                        getType() == TYPEMCT310ID  ||
                        getType() == TYPEMCT310IDL ||
                        getType() == TYPEMCT310IL )
                    {
                        num_channels = 1;
                    }

                    if( _peakMode == PeakModeOnPeakOffPeak )    config_byte |= 0x04;

                    //  negative logic so we default to three-wire
                    if( _wireConfig[0] != WireConfigTwoWire )   config_byte |= 0x08;
                    if( _wireConfig[1] != WireConfigTwoWire )   config_byte |= 0x10;
                    if( _wireConfig[2] != WireConfigTwoWire )   config_byte |= 0x20;

                    pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon raw start=0x03 " + CtiNumStr(config_byte).xhex().zpad(2), InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                    if( pReq != NULL )
                    {
                        if( strstr(InMessage.Return.CommandStr, "noqueue") )
                        {
                            pReq->setCommandString(pReq->CommandString() + " noqueue");
                        }

                        CtiCommandParser parse(pReq->CommandString());
                        beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);
                        delete pReq;
                    }

                    for( int i = 0; i < num_channels; i++ )
                    {
                        if( _mpkh[i] > 0 )
                        {
                            pReq = CTIDBG_new CtiRequestMsg(InMessage.TargetID, "putconfig emetcon mult kyz " + CtiNumStr(i+1) + string(" ") + CtiNumStr(_mpkh[i]), InMessage.Return.UserID, InMessage.Return.GrpMsgID, InMessage.Return.RouteID, selectInitialMacroRouteOffset(InMessage.Return.RouteID), InMessage.Return.Attempt);

                            if( pReq != NULL )
                            {
                                if( strstr(InMessage.Return.CommandStr, "noqueue") )
                                {
                                    pReq->setCommandString(pReq->CommandString() + " noqueue");
                                }

                                CtiCommandParser parse(pReq->CommandString());
                                beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, OutTemplate);
                                delete pReq;
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "can't send MPKH \"" << _mpkh[i] << "\" to channel " << i+1 << " on meter \"" << getName() << "\"");
                        }
                    }

                    resultString += getName() + " / Sent config to MCT \"" + getName() + "\"\n";
                }

                if( OutTemplate != NULL )
                {
                    delete OutTemplate;
                }

                resultString += getName( ) + " / sspec verified as \'" + sspecIsFrom( sspec ) + "\'.";
            }

            break;
        }

        default:
        {
            resultString = getName( ) + " / command complete";

            break;
        }
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( resultString );

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

    if( InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection)!=0 )
    {
        ReturnMsg->setExpectMore(true);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


bool MctDevice::isLoadProfile( int type )
{
    bool retVal = false;

    switch( type )
    {
        case TYPEMCT310IL:
        case TYPEMCT310IDL:
        case TYPEMCT318L:
        case TYPELMT2:
        case TYPEMCT240:
        case TYPEMCT248:
        case TYPEMCT250:
        {
            retVal = true;

            break;
        }
    }

    return retVal;
}


bool MctDevice::isMct410(int type)
{
    switch(type)
    {
        case TYPEMCT410CL:
        case TYPEMCT410FL:
        case TYPEMCT410GL:
        case TYPEMCT410IL:
        {
            return true;
        }
    }

    return false;
}

bool MctDevice::isMct420(int type)
{
    switch(type)
    {
        case TYPEMCT420CL:
        case TYPEMCT420CD:
        case TYPEMCT420FL:
        case TYPEMCT420FD:
        {
            return true;
        }
    }

    return false;
}

bool MctDevice::isMct430(int type)
{
    switch(type)
    {
        case TYPEMCT430A:
        case TYPEMCT430A3:
        case TYPEMCT430S4:
        case TYPEMCT430SL:
        {
            return true;
        }
    }

    return false;
}

bool MctDevice::isMct440(int type)
{
    switch(type)
    {
        case TYPEMCT440_2131B:
        case TYPEMCT440_2132B:
        case TYPEMCT440_2133B:
        {
            return true;
        }
    }

    return false;
}

bool MctDevice::isMct470(int type)
{
    return type == TYPEMCT470;
}

bool MctDevice::hasVariableDemandRate( int type, int sspec )
{
    bool retVal = true;

    switch( type )
    {
        case TYPELMT2:
        case TYPEMCT210:
        case TYPEMCT212:
        case TYPEMCT213:
        case TYPEMCT224:
        case TYPEMCT226:
        {
            retVal = false;

            break;
        }

        case TYPEMCT240:
        {
            if( sspec == 74 )
            {
                retVal = false;
            }
        }
    }

    return retVal;
}


bool MctDevice::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
   return getOperationFromStore(_commandStore, cmd, bst);
}


bool MctDevice::getOperationFromStore( const CommandSet &store, const UINT &cmd, BSTRUCT &bst )
{
   CommandSet::const_iterator itr = store.find(CommandStore(cmd));

   if( itr != store.end() )     // It's prego!
   {
      bst.Function  = itr->function;     // Copy over the found funcLen pair!
      bst.Length    = itr->length;      // Copy over the found funcLen pair!
      bst.IO        = itr->io;

      return true;
   }

   return false;
}


void MctDevice::calcAndInsertLPRequests(OUTMESS *&OutMessage, OutMessageList &outList)
{
    CTILOG_ERROR(dout, "Default load profile logic handler - request deleted");

    if(OutMessage != NULL)
    {
        delete OutMessage;
        OutMessage = NULL;
    }
}


bool MctDevice::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    CTILOG_ERROR(dout, "Default load profile request location handler - request deleted");

    return false;
}


void MctDevice::setConfigData( const string &configName, int configType, const string &configMode, const int mctwire[MCTConfig_ChannelCount], const double mpkh[MCTConfig_ChannelCount] )
{
    switch( getType() )
    {
        case TYPELMT2:
        case TYPEMCT210:
        case TYPEMCT212:
        case TYPEMCT213:
        case TYPEMCT224:
        case TYPEMCT226:
        case TYPEMCT240:
        case TYPEMCT242:
        case TYPEMCT248:
        case TYPEMCT250:
        {
            if( configType == Config2XX )
            {
                _configType = Config2XX;
            }
            else
            {
                CTILOG_ERROR(dout, "invalid config type \""<< configType <<"\" for device \""<< getName() <<"\"");
            }

            break;
        }
        case TYPEMCT310:
        case TYPEMCT310ID:
        case TYPEMCT310IDL:
        case TYPEMCT318:
        case TYPEMCT310IL:
        case TYPEMCT318L:
        case TYPEMCT360:
        case TYPEMCT370:
        {
            if( configType == Config3XX )
            {
                _configType = Config3XX;
            }
            else
            {
                CTILOG_ERROR(dout, "invalid config type \""<< configType <<"\" for device \""<< getName() <<"\"");
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "invalid device type \""<< getType() <<"\" for device \""<< getName() <<"\"");

            _configType = ConfigInvalid;

            break;
        }
    }

    if( !findStringIgnoreCase(configMode,"peakoffpeak") )    _peakMode = PeakModeOnPeakOffPeak;
    else if( !findStringIgnoreCase(configMode,"minmax") )    _peakMode = PeakModeMinMax;
    else
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout, "invalid peak mode string \""<< configMode <<"\" - defaulting to minmax");
        }

        _peakMode = PeakModeInvalid;
    }

    for( int i = 0; i < MCTConfig_ChannelCount; i++ )
    {
        _mpkh[i] = mpkh[i];

        if     ( mctwire[i] == WireConfigThreeWire )    _wireConfig[i] = WireConfigThreeWire;
        else if( mctwire[i] == WireConfigTwoWire )      _wireConfig[i] = WireConfigTwoWire;
        else
        {
            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout, "invalid wire config \"" << mctwire[i] << " for channel " << i+1 << " - defaulting to three-wire");
            }

            _wireConfig[i] = WireConfigInvalid;
        }
    }
}


int MctDevice::getNextFreeze( void ) const
{
    //  default to 1 - not ideal, but the best we can do with the analyzeWhiteRabbits() function call
    int retval = 1;

    if( _freeze_expected != std::numeric_limits<int>::min() )
    {
        retval = abs(_freeze_expected % 2) + 1;

        CTILOG_WARN(dout, "_freeze_counter is not set, sending _freeze_expected + 1 (" << retval << ")");
    }
    else if( _freeze_counter >= 0 )
    {
        retval = (_freeze_counter % 2) + 1;
    }
    else
    {
        CTILOG_WARN(dout, "_freeze_counter is not set, sending 1");
    }

    return retval;
}


int MctDevice::getCurrentFreezeCounter( void ) const
{
    return _freeze_counter;
}

int MctDevice::getExpectedFreezeCounter( void ) const
{
    return _freeze_expected;
}

bool MctDevice::getExpectedFreezeParity( void ) const
{
    return !(_freeze_expected % 2);
}

CtiTime MctDevice::getLastFreezeTimestamp(const CtiTime &TimeNow)
{
    CtiTime last_freeze      = getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp);
    CtiTime scheduled_freeze = getLastScheduledFreezeTimestamp(TimeNow);

    if( scheduled_freeze.isValid() && scheduled_freeze > last_freeze )
    {
        last_freeze = scheduled_freeze;

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, last_freeze);
    }

    return last_freeze;
}


CtiTime MctDevice::getLastScheduledFreezeTimestamp(const CtiTime &TimeNow)
{
    long freeze_day = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay);

    CtiTime last_scheduled_freeze(CtiTime::not_a_time);  //  not_a_time resolves to 0 seconds, ensuring it'll be less than anything else AND won't be valid

    //  we have a scheduled freeze we need to account for
    if( freeze_day > 0 )
    {
        //  we will calculate the previous freeze day based on the decode time
        CtiTime scheduled_freeze = findLastScheduledFreeze(TimeNow, freeze_day);

        if( scheduled_freeze > getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp) )
        {
            last_scheduled_freeze = scheduled_freeze;
        }
    }

    return last_scheduled_freeze;
}


/**
 * Returns the time that the last scheduled freeze occurred.
 * Scheduled freezes occur at the end of the specified day, so freeze_day 1 will return midnight on the 2nd.
 * Freeze days 31 and greater will always have a freeze time at midnight on the 1st.
 *
 * @param TimeNow
 * @param freeze_day
 *
 * @return
 */
CtiTime MctDevice::findLastScheduledFreeze(const CtiTime &TimeNow, unsigned freeze_day)
{
    if( freeze_day == 0 )
    {
        //  freeze time will never be after the freeze config date
        return CtiDate(1, 1, 1970);
    }

    //  we will eventually need to add device timezone smarts here instead of assuming server-timezone midnight
    CtiDate last_freeze = TimeNow.date();

    //  if the freeze hasn't happened yet this month, move back to the end of last month
    //    if freeze_day >= 31, this will always happen
    if( freeze_day >= last_freeze.dayOfMonth() )
    {
        last_freeze -= last_freeze.dayOfMonth();
    }

    //  if the freeze happened earlier this month, back up
    //    if freeze_day >= 31, this block will always be bypassed, leaving us at the end of the month
    if( freeze_day < last_freeze.dayOfMonth() )
    {
        last_freeze -= last_freeze.dayOfMonth();  //  back up one month...
        last_freeze += freeze_day;                    //  ... and add the freeze_day back on
    }

    //  freeze happens at the end of the day/beginning of the next day
    return CtiTime(last_freeze + 1);
}


void MctDevice::updateFreezeInfo( int freeze_counter, unsigned long freeze_timestamp )
{
    _freeze_counter  = freeze_counter;
    _freeze_expected = freeze_counter;

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, freeze_timestamp);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter,  _freeze_counter);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected, _freeze_expected);
}


void MctDevice::setExpectedFreeze( int next_freeze )
{
    //  0-based
    next_freeze--;

    if( next_freeze == 0 || next_freeze == 1 )
    {
        if( _freeze_counter == std::numeric_limits<int>::min() && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter) )
        {
            _freeze_counter = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter);
        }

        if( _freeze_expected == std::numeric_limits<int>::min() && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected) )
        {
            _freeze_expected = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected);
        }

        if( _freeze_expected >= 0 )
        {
            if( (_freeze_expected % 2) == next_freeze )
            {
                _freeze_expected = (_freeze_expected + 1) & 0xff;

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, (unsigned long)CtiTime::now().seconds());
                setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected, _freeze_expected);
            }
        }
        else if( _freeze_counter >= 0 )
        {
            if( (_freeze_counter % 2) == next_freeze )
            {
                _freeze_expected = (_freeze_counter + 1) & 0xff;

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, (unsigned long) CtiTime::now().seconds());
                setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected, _freeze_expected);
            }
        }
    }
}


YukonError_t MctDevice::checkFreezeLogic(const CtiTime &TimeNow, int incoming_counter, string &error_string )
{
    YukonError_t status = ClientErrors::None;

    if( _freeze_expected == std::numeric_limits<int>::min() && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected) )
    {
        _freeze_expected = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected);
    }

    _freeze_counter = incoming_counter;
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter, _freeze_counter);

    //  if this was a scheduled freeze, it's valid no matter what
    if( getLastFreezeTimestamp(TimeNow) == getLastScheduledFreezeTimestamp(TimeNow) )
    {
        //  set this so any calls to getExpectedFreezeParity() work properly
        _freeze_expected = _freeze_counter;
    }
    else
    {
        if( !getLastFreezeTimestamp(TimeNow).isValid() || _freeze_expected == std::numeric_limits<int>::min() )
        {
            CTILOG_ERROR(dout, "no freeze timestamp recorded for device \"" << getName() << "\"");

            error_string  = "No freeze has been recorded for this device";
            error_string += " - current freeze counter (" + CtiNumStr(_freeze_counter) + "), device expecting a \"freeze ";
            error_string += ((_freeze_counter % 2)?("two"):("one")) + string("\"");

            status = ClientErrors::FreezeNotRecorded;
        }
        else if( _freeze_counter != _freeze_expected )
        {
            int tmp_expected = abs(_freeze_expected);

            CTILOG_ERROR(dout, "incoming freeze counter ("<< _freeze_counter <<") does not match expected value ("<< tmp_expected <<") on device \"" << getName() <<"\"");

            error_string  = "Invalid freeze counter (" + CtiNumStr(_freeze_counter) + ", expected ";
            error_string += CtiNumStr(tmp_expected);
            error_string += "), send device a \"freeze ";
            error_string += (_freeze_counter % 2)?("two"):("one");
            error_string += "\" to resynchronize";
            status = ClientErrors::InvalidFreezeCounter;

            _freeze_expected = tmp_expected * -1;

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeExpected, _freeze_expected);
        }
    }

    return status;
}

}
}

