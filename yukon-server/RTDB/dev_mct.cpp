/*-----------------------------------------------------------------------------*
*
* File:   dev_mct
*
* Date:   12/21/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct.cpp-arc  $
* REVISION     :  $Revision: 1.87 $
* DATE         :  $Date: 2006/06/23 18:05:59 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <time.h>
#include <utility>
#include <list>

#include "numstr.h"

#include "devicetypes.h"
#include "device.h"
#include "dev_mct.h"
#include "dev_mct210.h"
#include "dev_mct31x.h"  //  for IED scanning capability
#include "dev_mct410.h"
#include "dev_mct470.h"
#include "dev_mct_lmt2.h"
#include "logger.h"
#include "mgr_point.h"
#include "msg_cmd.h"
#include "pt_numeric.h"
#include "pt_accum.h"
#include "porter.h"
#include "utility.h"
#include "dllyukon.h"
#include "cparms.h"
#include "yukon.h"
#include "ctidate.h"

using std::list;
using Cti::Protocol::Emetcon;


#define STATUS1_BIT_MCT3XX    0x80
#define STATUS2_BIT_MCT3XX    0x40
#define STATUS3_BIT_MCT3XX    0x20
#define STATUS4_BIT_MCT3XX    0x10
#define STATUS5_BIT_MCT3XX    0x01
#define STATUS6_BIT_MCT3XX    0x02
#define STATUS7_BIT_MCT3XX    0x08
#define STATUS8_BIT_MCT3XX    0x04
#define STATUS1_BIT           0x40
#define STATUS2_BIT           0x80
#define STATUS3_BIT           0x02
#define STATUS4_BIT           0x04
#define OVERFLOW_BIT          0x01
#define L_PWRFAIL_BIT         0x02
#define S_PWRFAIL_BIT         0x04
#define OVERFLOW310_BIT       0x04
#define L_PWRFAIL310_BIT      0x08
#define S_PWRFAIL310_BIT      0x10
#define TAMPER_BIT            0x08



using std::make_pair;
using std::set;

set< CtiDLCCommandStore > CtiDeviceMCT::_commandStore;


CtiDeviceMCT::CtiDeviceMCT() :
    _lpIntervalSent(false),
    _configType(ConfigInvalid),
    _peakMode(PeakModeInvalid),
    _disconnectAddress(0),
    _freeze_counter(-1),
    _expected_freeze(-1)
{
    _lastReadDataPurgeTime = _lastReadDataPurgeTime.now();
    for( int i = 0; i < MCTConfig_ChannelCount; i++ )
    {
        _mpkh[i] = -1.0;
        _wireConfig[i] = WireConfigInvalid;
    }

    resetMCTScansPending();
}

CtiDeviceMCT::CtiDeviceMCT(const CtiDeviceMCT& aRef)
{
    *this = aRef;
}

CtiDeviceMCT::~CtiDeviceMCT()  {  }

CtiDeviceMCT &CtiDeviceMCT::operator=(const CtiDeviceMCT &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        LockGuard guard(monitor());            // Protect this device!
    }
    return *this;
}

bool CtiDeviceMCT::getMCTDebugLevel(int mask)
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

INT CtiDeviceMCT::getSSpec() const
{
    return 0;
}

bool CtiDeviceMCT::sspecIsValid( int sspec )
{
    //  note that the LMT-2 sspec relies only on the lower byte, so anything with
    //    36 (0x24) as the lower-order byte will need to be watched...
    //  36, 292, 548, 804, 1060, 1316, 1572, 1828, 2084, 2340, 2596, 2852, 3108, 3364, 3620, 3876, ...

    bool valid = false;

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

    //mct_sspec.insert(make_pair(TYPEMCT410,   XXXX));  //  add this later when it's necessary

    reported = make_pair(getType(), sspec);

    if( mct_sspec.find(reported) != mct_sspec.end() )
    {
        valid = true;
    }

    return valid;
}


string CtiDeviceMCT::sspecIsFrom( int sspec )
{
    string whois;

    switch( sspec )
    {
        case 36:
            whois = "LMT-2";
            break;

        case 95:
            whois = "MCT 21x";
            break;

        case 74:
            whois = "MCT 22x";
            break;

        case 93:
        case 121:
            whois = "MCT 24x";
            break;

        case 111:
            whois = "MCT 250";
            break;

        case 153:
            whois = "MCT 310";
            break;

        case 1007:
            whois = "MCT 3xx/3xxL";
            break;

        case 218:
            whois = "MCT 318/360/370";
            break;

        default:
            whois = "Unknown";
            break;
    }

    return whois;
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceMCT::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}

void CtiDeviceMCT::getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
}

void CtiDeviceMCT::getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
}

LONG CtiDeviceMCT::getDemandInterval() const
{
    LONG retval = MCT_DemandIntervalDefault;

    if( getLastIntervalDemandRate() )
        retval = getLastIntervalDemandRate();

    return retval;
}


void CtiDeviceMCT::resetMCTScansPending( void )
{
    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateAccum, false);
}

CtiTime CtiDeviceMCT::adjustNextScanTime(const INT scanType)
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

unsigned long CtiDeviceMCT::calcNextLPScanTime( void )
{
    return (_nextLPScanTime = YUKONEOT);  //  never for a non-load profile device...  overridden by 24x (+250), 310L, 318L
}

unsigned long CtiDeviceMCT::getNextLPScanTime( void )
{
    return _nextLPScanTime;
}


void CtiDeviceMCT::sendLPInterval( OUTMESS *&OutMessage, list< OUTMESS* > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    // 082002 CGP // OutMessage->RouteID   = getRouteID();
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = Emetcon::PutConfig_LoadProfileInterval;     // Helps us figure it out later!
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon interval lp", COMMAND_STR_SIZE );

    outList.push_back(OutMessage);
    OutMessage = NULL;

    _lpIntervalSent = true;
}


int CtiDeviceMCT::checkDemandQuality( unsigned long &pulses, PointQuality_t &quality, bool &badData )
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
bool CtiDeviceMCT::initCommandStore()
{
    bool failed = false;
    CtiDLCCommandStore cs;

    //  initialize any pan-MCT operations
    cs._cmd     = Emetcon::GetConfig_Model;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT_ModelPos,
                             (int)MCT_ModelLen );   // Decode happens in the children please...
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Command_Loop;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT_ModelPos, 1 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_Install;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT_ModelPos,
                             (int)MCT_SspecLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_GroupAddrEnable;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT_Command_GroupAddrEnable, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_GroupAddrInhibit;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT_Command_GroupAddrInhibit, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Raw;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( 0, 0 );  //  this will be filled in by executeGetConfig
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Shed;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( 0, 0 );  //  this will be filled in by executeControl
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Restore;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT_Restore, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Close;
    cs._io      = Emetcon::IO_Write | Q_ARML;
    cs._funcLen = make_pair( (int)MCT_Command_Close, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Open;
    cs._io      = Emetcon::IO_Write | Q_ARML;
    cs._funcLen = make_pair( (int)MCT_Command_Open, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Conn;
    cs._io      = Emetcon::IO_Write | Q_ARML;
    cs._funcLen = make_pair( (int)MCT_Command_Close, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Control_Disc;
    cs._io      = Emetcon::IO_Write | Q_ARML;
    cs._funcLen = make_pair( (int)MCT_Command_Open, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_ARMC;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT_Command_ARMC, 0);
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_ARML;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT_Command_ARML, 0);
    _commandStore.insert( cs );

    //  putconfig_tsync is in MCT2XX and MCT310 because the 2XX requires an ARMC
    //    also, the getconfig time location is different for 2XX and 3XX, so that's in each's base as well
    cs._cmd     = Emetcon::GetConfig_TSync;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT_TSyncPos,
                             (int)MCT_TSyncLen );
    _commandStore.insert( cs );

    return failed;
}


INT CtiDeviceMCT::ExecuteRequest( CtiRequestMsg              *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  list< CtiMessage* >  &vgList,
                                  list< CtiMessage* >  &retList,
                                  list< OUTMESS* >     &outList )
{
    int nRet = NoError;
    list< OUTMESS* > tmpOutList;

    switch( parse.getCommand( ) )
    {
        case LoopbackRequest:
        {
            nRet = executeLoopback( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case ScanRequest:
        {
            nRet = executeScan( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetValueRequest:
        {
            nRet = executeGetValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutValueRequest:
        {
            nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case ControlRequest:
        {
            nRet = executeControl( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetStatusRequest:
        {
            nRet = executeGetStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutStatusRequest:
        {
            nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        string resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << CtiTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            tmpOutList.push_back( OutMessage );
            OutMessage = NULL;
        }

        executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, true);
    }

    return nRet;
}

/*****************************************************************************************
 *  The general scan for a mct type device is performed and collects DEMAND accumulators
 *  from the device, as well as status info, if the device can supply it in the same read.
 *****************************************************************************************/
INT CtiDeviceMCT::GeneralScan(CtiRequestMsg *pReq,
                              CtiCommandParser &parse,
                              OUTMESS *&OutMessage,
                              list< CtiMessage* > &vgList,
                              list< CtiMessage* > &retList,
                              list< OUTMESS* > &outList,
                              INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(MCTDebug_Scanrates) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        if(getOperation(Emetcon::Scan_General, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = Emetcon::Scan_General;     // Helps us figure it out later!
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.MacroOffset = 0; // 20020730 CGP // selectInitialMacroRouteOffset(getRouteID());

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

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Command lookup failed **** " << getName() << ".  Device Type " << desolveDeviceType(getType()) << endl;

            status = NoMethod;
        }
    }

    return status;
}

/*****************************************************************************************
 *  The integrity scan for a mct type device is performed and collects status data
 *  from the device.  This is valid esp for DLC devices which require separate reads to
 *  collect status information
 *****************************************************************************************/
INT CtiDeviceMCT::IntegrityScan(CtiRequestMsg *pReq,
                                CtiCommandParser &parse,
                                OUTMESS *&OutMessage,
                                list< CtiMessage* > &vgList,
                                list< CtiMessage* > &retList,
                                list< OUTMESS* > &outList,
                                INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(MCTDebug_Scanrates) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Demand/IEDScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(Emetcon::Scan_Integrity, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = Emetcon::Scan_Integrity;     // Helps us figure it out later!;
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.MacroOffset = 0; // 20020730 CGP // selectInitialMacroRouteOffset(getRouteID());

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

            status = NoMethod;

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;
        }
    }

    return status;
}

/*****************************************************************************************
 *  The accumulator scan for a mct type device is performed and collects pulse data
 *  from the device, as well as status info, if the device can supply it in the same read.
 *****************************************************************************************/
INT CtiDeviceMCT::AccumulatorScan(CtiRequestMsg *pReq,
                                  CtiCommandParser &parse,
                                  OUTMESS *&OutMessage,
                                  list< CtiMessage* > &vgList,
                                  list< CtiMessage* > &retList,
                                  list< OUTMESS* > &outList,
                                  INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(MCTDebug_Scanrates) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** AccumulatorScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(Emetcon::Scan_Accum, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = Emetcon::Scan_Accum;
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.MacroOffset = 0; // 20020730 CGP // selectInitialMacroRouteOffset(getRouteID());

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

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;

            status = NoMethod;
        }
    }

    return status;
}


/*****************************************************************************************
 *  The load profile scan for a mct type device is dependent on the load profile scan rate,
 *  and gathers load profile whenever 6 intervals have passed
 *****************************************************************************************/
INT CtiDeviceMCT::LoadProfileScan(CtiRequestMsg *pReq,
                                  CtiCommandParser &parse,
                                  OUTMESS *&OutMessage,
                                  list< CtiMessage* > &vgList,
                                  list< CtiMessage* > &retList,
                                  list< OUTMESS* > &outList,
                                  INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if( getMCTDebugLevel(MCTDebug_Scanrates) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** LoadProfileScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(Emetcon::Scan_LoadProfile, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = Emetcon::Scan_LoadProfile;
            OutMessage->Retry     = 0;  // 20020906 CGP
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.MacroOffset = 0; // 20020730 CGP // selectInitialMacroRouteOffset(getRouteID());

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

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;

            status = NoMethod;
        }
    }

    return status;
}


INT CtiDeviceMCT::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    status = ModelDecode(InMessage, TimeNow, vgList, retList, outList);

    if( status )
    {
    }

    return status;
}


INT CtiDeviceMCT::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch( InMessage->Sequence )
    {
        case Emetcon::Control_Latch:
        case Emetcon::Control_Open:
        case Emetcon::Control_Close:
        case Emetcon::Control_Shed:
        case Emetcon::Control_Restore:
        case Emetcon::PutConfig_ARMC:
        case Emetcon::PutConfig_ARML:
        {
            break;
        }

        case Emetcon::GetConfig_Time:
        case Emetcon::GetConfig_TSync:
        case Emetcon::GetConfig_Raw:
        case Emetcon::GetConfig_DemandInterval:
        case Emetcon::GetConfig_LoadProfileInterval:
        case Emetcon::GetConfig_Multiplier:
        case Emetcon::GetConfig_Multiplier2:
        case Emetcon::GetConfig_Multiplier3:
        case Emetcon::GetConfig_Multiplier4:
        case Emetcon::GetConfig_GroupAddress:
        {
            status = decodeGetConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        case Emetcon::GetConfig_Model:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled GetConfig_Model **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }

        case Emetcon::GetValue_PFCount:
        {
            status = decodeGetValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Command_Loop:
        {
            status = decodeLoopback(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::PutConfig_Install:
        case Emetcon::PutConfig_Multiplier:
        case Emetcon::PutConfig_Multiplier2:
        case Emetcon::PutConfig_Multiplier3:
        case Emetcon::PutConfig_GroupAddrEnable:
        case Emetcon::PutConfig_GroupAddrInhibit:
        case Emetcon::PutConfig_Raw:
        case Emetcon::PutConfig_TSync:
        case Emetcon::PutConfig_Intervals:
        case Emetcon::PutConfig_DemandInterval:
        case Emetcon::PutConfig_LoadProfileInterval:
        case Emetcon::PutConfig_IEDClass:
        case Emetcon::PutConfig_IEDScan:
        case Emetcon::PutConfig_GroupAddr_Bronze:
        case Emetcon::PutConfig_GroupAddr_GoldSilver:
        case Emetcon::PutConfig_GroupAddr_Lead:
        case Emetcon::PutConfig_UniqueAddr:
        case Emetcon::PutConfig_LoadProfileInterest:
        case Emetcon::PutConfig_Disconnect:
        case Emetcon::PutConfig_LoadProfileReportPeriod:
        case Emetcon::PutConfig_TOU:
        {
            status = decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::PutValue_KYZ:
        case Emetcon::PutValue_KYZ2:
        case Emetcon::PutValue_KYZ3:
        case Emetcon::PutValue_ResetPFCount:
        case Emetcon::PutValue_IEDReset:
        {
            status = decodePutValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::PutStatus_Reset:
        case Emetcon::PutStatus_ResetOverride:
        case Emetcon::PutStatus_PeakOn:
        case Emetcon::PutStatus_PeakOff:
        case Emetcon::PutStatus_FreezeOne:
        case Emetcon::PutStatus_FreezeTwo:
        case Emetcon::PutStatus_FreezeVoltageOne:
        case Emetcon::PutStatus_FreezeVoltageTwo:
        {
            status = decodePutStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            status = NoMethod;
            break;
        }
    }

    return status;
}

//  Note that the outmessage may/will be modified on exit!
bool CtiDeviceMCT::recordMessageRead(OUTMESS *OutMessage)
{
    bool message_inserted = false;

    if( (_lastReadDataPurgeTime.now().seconds() - _lastReadDataPurgeTime.seconds()) >= (2 * 60 * 60) )  //  2 hour span
    {
        //  Clears out every data member that is over 1 hour old, operates at most every 2 hours.
        _lastReadDataPurgeTime = _lastReadDataPurgeTime.now();

        MessageReadDataSet_t::iterator itr = _expectedReadData.begin();
        while( itr != _expectedReadData.end() )
        {
            if( (_lastReadDataPurgeTime.seconds() - itr->insertTime.seconds()) >= (1 * 60 * 60) )
            {
                itr = _expectedReadData.erase(itr);
            }
            else
            {
                itr++;
            }
        }
    }

    //  we only record reads
    if( OutMessage->Buffer.BSt.IO == Emetcon::IO_Function_Read ||
        OutMessage->Buffer.BSt.IO == Emetcon::IO_Read )
    {
        MessageReadData newData;
        newData.oldSequence = OutMessage->Sequence;

        if( _expectedReadData.empty() )
        {
            newData.newSequence = SequenceCountBegin;
        }
        else
        {
            newData.newSequence = _lastSequenceNumber+1;
        }

        _lastSequenceNumber = newData.newSequence;

        if( _lastSequenceNumber >= SequenceCountEnd )
        {
            //  sloppy, but it has a range of 10k, no one cares
            _lastSequenceNumber = SequenceCountBegin;
        }

        newData.insertTime = CtiTime::now();
        newData.ioType     = OutMessage->Buffer.BSt.IO;
        newData.location   = OutMessage->Buffer.BSt.Function;
        newData.length     = OutMessage->Buffer.BSt.Length;

        if( _expectedReadData.insert(newData).second )
        {
            //  the insert was successful
            OutMessage->Sequence = newData.newSequence;

            message_inserted = true;
        }
    }

    return message_inserted;
}

bool CtiDeviceMCT::recordMultiMessageRead(list< OUTMESS* > &outList)
{
    bool retVal = false;
    OUTMESS *outMessage;

    std::list< OUTMESS* >::iterator itr = outList.begin();
    while (itr != outList.end() )
    {
        outMessage = *itr;
        if(outMessage->Buffer.BSt.IO == Emetcon::IO_Function_Read || outMessage->Buffer.BSt.IO == Emetcon::IO_Read)
        {
            recordMessageRead(outMessage);
            retVal = true;
        }
        ++itr;
    }
    return retVal;
}

bool CtiDeviceMCT::restoreMessageRead(INMESS *InMessage, int &ioType, int &location)
{
    bool retVal = false;
    if(InMessage->Sequence < SequenceCountBegin || InMessage->Sequence > SequenceCountEnd)
    {
        //retVal = false;
    }
    else
    {
        //This thing should be in my list!
        MessageReadData temp;
        temp.newSequence = InMessage->Sequence;
        MessageReadDataSet_t::const_iterator iter = _expectedReadData.find(temp);
        if(iter == _expectedReadData.end())
        {
            //retVal = false
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** Sequence is in proper range but is not in the set" << __FILE__ << " (" << __LINE__ << ")" << endl;
            //If your getting this, either the range needs to be adjusted, or out messages are lasting longer then 2 hours before
            //being sent and returned.
        }
        else
        {
            //yay, it works
            ioType = iter->ioType;
            location = iter->location;
            InMessage->Sequence = iter->oldSequence;
            InMessage->Buffer.DSt.Length = iter->length;
            _expectedReadData.erase(temp);
            retVal = true;
        }
    }
    return retVal;
}

INT CtiDeviceMCT::ErrorDecode(INMESS *InMessage, CtiTime& Now, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                string(InMessage->Return.CommandStr),
                                                string(),
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.TrxID,
                                                InMessage->Return.UserID);
    int i;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    int ioType, location;
    restoreMessageRead(InMessage, ioType, location);//used to remove this message from the list.

    if( retMsg != NULL )
    {
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
                        case TYPEMCT470:
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
                            insertPointFail( InMessage, retMsg, ScanRateIntegrity, CtiDeviceMCT31X::MCT360_IED_VoltsPhaseA_PointOffset, AnalogPointType );
                            insertPointFail( InMessage, retMsg, ScanRateIntegrity, CtiDeviceMCT31X::MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType );
                            insertPointFail( InMessage, retMsg, ScanRateIntegrity, CtiDeviceMCT31X::MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType );
                            insertPointFail( InMessage, retMsg, ScanRateIntegrity, CtiDeviceMCT31X::MCT360_IED_VoltsNeutralCurrent_PointOffset, AnalogPointType );

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
                    switch( getType() )
                    {
                        case TYPEMCT470:
                        case TYPEMCT410:
                        {
                            int channel = parse.getiValue("loadprofile_channel", 0);

                            if( channel )
                            {
                                insertPointFail( InMessage, retMsg, ScanRateLoadProfile, channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );
                            }

                            break;
                        }

                        default:
                        {
                            for( i = 0; i < CtiTableDeviceLoadProfile::MaxCollectedChannel; i++ )
                            {
                                if( getLoadProfile().isChannelValid(i) )
                                {
                                    insertPointFail( InMessage, retMsg, ScanRateLoadProfile, (i + 1) + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );
                                }
                            }

                            break;
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
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - null retMsg() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}


int CtiDeviceMCT::insertPointFail( INMESS *InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType )
{
    int failed = FALSE;
    CtiPointSPtr pPoint;

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    pPoint = getDevicePointOffsetTypeEqual( pOffset, pType);

    if( pMsg != NULL && pPoint )
    {
        pMsg->insert( -1 );          // This is the dispatch token and is unimplemented at this time
        pMsg->insert( OP_POINTID );  // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert( pPoint->getPointID() );
        pMsg->insert( scanType );
        pMsg->insert( InMessage->EventCode );  // The error number from dsm2.h or yukon.h which was reported.

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



INT CtiDeviceMCT::executeLoopback(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  list< CtiMessage* >      &vgList,
                                  list< CtiMessage* >      &retList,
                                  list< OUTMESS* >         &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    INT  function;
    int  i;

    OUTMESS *tmpOut;

    function = Emetcon::Command_Loop;
    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;
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


INT CtiDeviceMCT::executeScan(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    string tester;

    INT            function;
    unsigned short stub;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
        case ScanRateStatus:
        case ScanRateGeneral:
        {
            function = Emetcon::Scan_General;
            found = getOperation(Emetcon::Scan_General, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            break;
        }
        case ScanRateAccum:
        {
            function = Emetcon::Scan_Accum;
            found = getOperation(Emetcon::Scan_Accum, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            break;
        }
        case ScanRateIntegrity:
        {
            function = Emetcon::Scan_Integrity;
            found = getOperation(Emetcon::Scan_Integrity, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            //  should we scan the IED for demand instead?
            if(getType() == TYPEMCT360 || getType() == TYPEMCT370)
            {
                //  if we're supposed to be scanning the IED, change it to the appropriate request
                if( ((CtiDeviceMCT31X *)this)->getIEDPort().getRealTimeScanFlag() )
                     getOperation(Emetcon::GetValue_IEDDemand, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }

            break;
        }
        case ScanRateLoadProfile:
        {
            //  outmess needs to be filled in by another function, just check if it's there
            function = Emetcon::Scan_LoadProfile;
            found = getOperation(Emetcon::Scan_LoadProfile, stub, stub, stub);

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
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;
        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCT::executeGetValue( CtiRequestMsg              *pReq,
                                   CtiCommandParser           &parse,
                                   OUTMESS                   *&OutMessage,
                                   list< CtiMessage* >  &vgList,
                                   list< CtiMessage* >  &retList,
                                   list< OUTMESS* >     &outList )
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( getType() == TYPEMCT360  ||  //  only these types can have an IED attached
            getType() == TYPEMCT370 )
        {
            if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                function = Emetcon::GetValue_IEDDemand;
                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
            }
            else  //  GV_IEDKwh, GV_IEDKvarh, GV_IEDKvah
            {
                if(      parse.getFlags() & CMD_FLAG_GV_KWH   )  function = Emetcon::GetValue_IEDKwh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVARH )  function = Emetcon::GetValue_IEDKvarh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVAH  )  function = Emetcon::GetValue_IEDKvah;
                else  /*  default request  */                    function = Emetcon::GetValue_IEDKwh;

                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

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
        function = Emetcon::GetValue_PFCount;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
    {
        function = Emetcon::GetValue_Demand;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT318 || getType() == TYPEMCT318L ||
            getType() == TYPEMCT360 || getType() == TYPEMCT370 )
        {
            //  if pulse input 3 isn't defined
            if( !getDevicePointOffsetTypeEqual(3, DemandAccumulatorPointType ) )
            {
                OutMessage->Buffer.BSt.Length -= 3;

                //  if pulse input 2 isn't defined
                if( !getDevicePointOffsetTypeEqual(2, DemandAccumulatorPointType ) )
                {
                    OutMessage->Buffer.BSt.Length -= 3;
                }
            }
        }
        else if( getType() == TYPEMCT410 )
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
            function = Emetcon::GetValue_FrozenPeakDemand;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else
        {
            function = Emetcon::GetValue_PeakDemand;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }

        //  ACH:  minimize request length someday, like below
    }
    else if( parse.getFlags() & CMD_FLAG_GV_VOLTAGE )
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
        {
            function = Emetcon::GetValue_FrozenVoltage;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else
        {
            function = Emetcon::GetValue_Voltage;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else  //  if( parse.getFlags() & CMD_FLAG_GV_KWH ) - default to a KWH read
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
        {
            function = Emetcon::GetValue_FrozenKWH;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else
        {
            function = Emetcon::GetValue_Default;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }

        int channels = 0;  //  so we'll bypass the point-cropping code if /channels/ doesn't get set by the following:

        if( getType() == TYPEMCT310 || getType() == TYPEMCT310ID || getType() == TYPEMCT310IDL || getType() == TYPEMCT310IL ||
            getType() == TYPEMCT318 || getType() == TYPEMCT318L  || getType() == TYPEMCT360    || getType() == TYPEMCT370 )
        {
            channels = CtiDeviceMCT31X::MCT31X_ChannelCount;
        }
        else if( getType() == TYPEMCT470 )
        {
            channels = CtiDeviceMCT470::MCT470_ChannelCount;
        }
        else if( getType() == TYPEMCT410 )
        {
            channels = CtiDeviceMCT410::MCT410_ChannelCount;
        }

        for( int i = channels; i > 1; i-- )
        {
            if( !getDevicePointOffsetTypeEqual(i, PulseAccumulatorPointType ) )
            {
                OutMessage->Buffer.BSt.Length -= 3;
            }
            else
            {
                break;
            }

            channels--;
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }


    return nRet;
}

INT CtiDeviceMCT::executePutValue(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  list< CtiMessage* >      &vgList,
                                  list< CtiMessage* >      &retList,
                                  list< OUTMESS* >         &outList)
{
    INT    nRet = NoError,
           i;
    long   rawPulses;
    double dial;

    INT function = -1;

    bool found = false;

    if(parse.getFlags() & CMD_FLAG_PV_DIAL)
    {
        switch(parse.getiValue("kyz_offset"))
        {
            case 1:     function = Emetcon::PutValue_KYZ;   break;
            case 2:     function = Emetcon::PutValue_KYZ2;  break;
            case 3:     function = Emetcon::PutValue_KYZ3;  break;
        }

        if( found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO) )
        {
            if(parse.getFlags() & CMD_FLAG_PV_RESET || !parse.isKeyValid("dial"))
            {
                dial = 0;
            }
            else
            {
                dial = parse.getdValue("dial");
            }

            CtiPointSPtr tmpPoint = getDevicePointOffsetTypeEqual(parse.getiValue("kyz_offset"), PulseAccumulatorPointType);

            if( tmpPoint && tmpPoint->isA() == PulseAccumulatorPointType)
            {
                rawPulses = (int)(dial / boost::static_pointer_cast<CtiPointAccumulator>(tmpPoint)->getMultiplier());
            }
            else
            {
                rawPulses = (int)dial;
            }


            //  copy the reading into the output buffer, MSB style
            for(i = 0; i < 3; i++)
            {
                OutMessage->Buffer.BSt.Message[i]   = (rawPulses >> ((2-i)*8)) & 0xFF;
                OutMessage->Buffer.BSt.Message[i+3] = (rawPulses >> ((2-i)*8)) & 0xFF;
                OutMessage->Buffer.BSt.Message[i+6] = (rawPulses >> ((2-i)*8)) & 0xFF;
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_PV_PWR)
    {
        //  currently only know how to reset powerfail
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            function = Emetcon::PutValue_ResetPFCount;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            //  set the outgoing bytes to 0
            for(int i = 0; i < OutMessage->Buffer.BSt.Length; i++)
            {
                OutMessage->Buffer.BSt.Message[i] = 0;
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_PV_IED)     // This parse has the token IED in it!
    {
        //  currently only know how to reset IEDs
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            int iedtype = ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType();

            function = Emetcon::PutValue_IEDReset;

            if( getType() == TYPEMCT360 || getType() == TYPEMCT370 )
            {
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                switch( iedtype )
                {
                    case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_AlphaResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_AlphaResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 60;  //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[1] = 1;   //  Demand Reset  function code for the Alpha
                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_LGS4ResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_LGS4ResetLen;
                        OutMessage->Buffer.BSt.Message[0] = CtiDeviceMCT31X::MCT360_LGS4ResetID;
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x2B;  //  Demand Reset function code for the LG S4
                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_GEKVResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_GEKVResetLen;
                        OutMessage->Buffer.BSt.Message[0] = CtiDeviceMCT31X::MCT360_GEKVResetID;
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x00;  //  sequence, standard proc, and uppoer bits of proc are 0
                        OutMessage->Buffer.BSt.Message[3] = 0x09;  //  procedure 9
                        OutMessage->Buffer.BSt.Message[4] = 0x01;  //  parameter length 1
                        OutMessage->Buffer.BSt.Message[5] = 0x01;  //  demand reset bit set
                        break;
                    }

                    default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Invalid IED type " << iedtype << " on device \'" << getName() << "\' **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        break;
                    }
                }
            }
            else if( getType() == TYPEMCT470 )
            {
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                if( parse.getCommandStr().find(" alpha") != string::npos )
                {
                    OutMessage->Buffer.BSt.Function   = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommand;
                    OutMessage->Buffer.BSt.Length     = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommandLen;
                    OutMessage->Buffer.BSt.Message[0] = 0xff;  //  SPID
                    OutMessage->Buffer.BSt.Message[1] = 3;     //  meter type: Alpha Power Plus
                    OutMessage->Buffer.BSt.Message[2] = 0;     //  meter num:  0
                    OutMessage->Buffer.BSt.Message[3] = 0x01;  //  function:   Alpha reset
                }
                else if( parse.getCommandStr().find(" s4") != string::npos )
                {
                    OutMessage->Buffer.BSt.Function   = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommand;
                    OutMessage->Buffer.BSt.Length     = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommandLen;
                    OutMessage->Buffer.BSt.Message[0] = 0xff;  //  SPID
                    OutMessage->Buffer.BSt.Message[1] = 1;     //  meter type: S4
                    OutMessage->Buffer.BSt.Message[2] = 0;     //  meter num:  0
                    OutMessage->Buffer.BSt.Message[3] = 0x2b;  //  function:   S4 reset
                }
                else
                {
                    found = false;
                }

                /*
                CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    //  this seems to be valid, since it's pretty much just copied from above - but it needs
                    //    to be tested before it's dropped back in

                    OutMessage->Buffer.BSt.Function   = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommandData;
                    OutMessage->Buffer.BSt.Length     = CtiDeviceMCT470::MCT470_FuncWrite_IEDCommandDataBaseLen;
                    OutMessage->Buffer.BSt.Message[0] = 0xff;  //  SPID
                    OutMessage->Buffer.BSt.Message[1] = 4;     //  meter type: GE kV
                    OutMessage->Buffer.BSt.Message[2] = 1;     //  meter num:  1?
                    OutMessage->Buffer.BSt.Message[3] = 0x09;  //  command 9?
                    OutMessage->Buffer.BSt.Message[4] = 0x01;  //  data length: 1
                    OutMessage->Buffer.BSt.Message[5] = 0x01;  //  demand reset bit set
                }
                */
            }
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }


    return nRet;
}

INT CtiDeviceMCT::executeGetStatus(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  list< CtiMessage* >      &vgList,
                                  list< CtiMessage* >      &retList,
                                  list< OUTMESS* >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    if(parse.getFlags() & CMD_FLAG_GS_DISCONNECT)          // Read the disconnect status
    {
        function = Emetcon::GetStatus_Disconnect;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_INTERNAL)
    {
        function = Emetcon::GetStatus_Internal;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_LOADPROFILE)
    {
        function = Emetcon::GetStatus_LoadProfile;

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( found && parse.isKeyValid("loadprofile_offset") )
        {
            if( getType() == TYPEMCT470 )
            {
                if( parse.getiValue("loadprofile_offset") == 1 ||
                    parse.getiValue("loadprofile_offset") == 2 )
                {
                    OutMessage->Buffer.BSt.Function = CtiDeviceMCT470::MCT470_FuncRead_LPStatusCh1Ch2Pos;
                }
                else if( parse.getiValue("loadprofile_offset") == 3 ||
                         parse.getiValue("loadprofile_offset") == 4 )
                {
                    OutMessage->Buffer.BSt.Function = CtiDeviceMCT470::MCT470_FuncRead_LPStatusCh3Ch4Pos;
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
            function = Emetcon::GetStatus_IEDLink;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else //  if(parse.getFlags() & CMD_FLAG_GS_EXTERNAL) - default command
    {
        function = Emetcon::GetStatus_External;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCT::executePutStatus(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;
    int   intervallength;
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...
    OUTMESS *tmpOutMess;

    INT function = -1;

    if( parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = Emetcon::PutStatus_Reset;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() != TYPEMCT410 && getType() != TYPEMCT470 )
        {
            OutMessage->Buffer.BSt.Message[0] = 0;
            OutMessage->Buffer.BSt.Message[1] = 0;
            OutMessage->Buffer.BSt.Message[2] = 0;
        }
    }
    else if( parse.getFlags() & CMD_FLAG_PS_RESETOVERRIDE )
    {
        function = Emetcon::PutStatus_ResetOverride;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("freeze") )
    {
        if( parse.isKeyValid("voltage") )
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = Emetcon::PutStatus_FreezeVoltageOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                //  set expected voltage freeze here
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = Emetcon::PutStatus_FreezeVoltageTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                //  set expected voltage freeze here
            }
        }
        else
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = Emetcon::PutStatus_FreezeOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                setExpectedFreeze(parse.getiValue("freeze"));
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = Emetcon::PutStatus_FreezeTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                setExpectedFreeze(parse.getiValue("freeze"));
            }
        }
    }
    else if( parse.isKeyValid("peak") )
    {
        if( parse.getiValue("peak") == TRUE )
        {
            function = Emetcon::PutStatus_PeakOn;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else
        {
            function = Emetcon::PutStatus_PeakOff;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }


    if(!found)
    {
       nRet = NoMethod;
    }
    else
    {
       // Load all the other stuff that is needed
       OutMessage->DeviceID  = getID();
       OutMessage->TargetID  = getID();
       OutMessage->Port      = getPortID();
       OutMessage->Remote    = getAddress();
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 2;

       OutMessage->Request.RouteID   = getRouteID();

       //  fix/ach this, it's ugly
       if( OutMessage->Buffer.BSt.Function == 0x06 )  //  easiest way to tell it's an MCT3xx
       {
           tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

           if( tmpOutMess != NULL )
           {
               //  reset power fail
               tmpOutMess->Buffer.BSt.Function = 0x50;
               tmpOutMess->Buffer.BSt.Length   =    0;

               outList.push_back(tmpOutMess);
           }
           else
           {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unable to allocate OUTMESS for MCT3XX powerfail reset" << endl;
           }

           tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

           if( tmpOutMess != NULL )
           {
               //  reset encoder error
               tmpOutMess->Buffer.BSt.Function = 0x58;
               tmpOutMess->Buffer.BSt.Length   =    0;

               outList.push_back(tmpOutMess);
           }
           else
           {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unable to allocate OUTMESS for MCT3XX encoder error reset" << endl;
           }
       }

       strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCT::executeGetConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    string temp;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    if(parse.isKeyValid("model"))
    {
        function = Emetcon::GetConfig_Model;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("ied"))
    {
        if(parse.isKeyValid("time"))
        {
            function = Emetcon::GetConfig_IEDTime;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else if( parse.isKeyValid("scan"))
        {
            function = Emetcon::GetConfig_IEDScan;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else if( parse.isKeyValid("channels") )
    {
        function = Emetcon::GetConfig_ChannelSetup;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("options"))
    {
        function = Emetcon::GetConfig_Options;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("disconnect"))
    {
        function = Emetcon::GetConfig_Disconnect;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("address_group"))
    {
        function = Emetcon::GetConfig_GroupAddress;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("time"))
    {
        if(parse.isKeyValid("sync"))
        {
            function = Emetcon::GetConfig_TSync;
        }
        else
        {
            function = Emetcon::GetConfig_Time;
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("multiplier"))
    {
        if( getType() != TYPEMCT470 && parse.isKeyValid("multchannel") )
        {
            switch( parse.getiValue("multchannel") )
            {
                case 1:     function = Emetcon::GetConfig_Multiplier;   break;
                case 2:     function = Emetcon::GetConfig_Multiplier2;  break;
                case 3:     function = Emetcon::GetConfig_Multiplier3;  break;
                case 4:     function = Emetcon::GetConfig_Multiplier4;  break;
                default:    function = -1;
            }
        }
        else
        {
            function = Emetcon::GetConfig_Multiplier;
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT470 && parse.isKeyValid("multchannel") )
        {
            if( parse.getiValue("multchannel") >= 1 &&
                parse.getiValue("multchannel") <= CtiDeviceMCT470::MCT470_ChannelCount )
            {
                OutMessage->Buffer.BSt.Function += (parse.getiValue("multchannel") - 1) * CtiDeviceMCT470::MCT470_Memory_ChannelOffset;
            }
            else
            {
                found = false;
            }
        }
    }
    else if(parse.isKeyValid("interval"))
    {
        temp = parse.getsValue("interval");

        if( temp == "intervals" )
        {
            function = Emetcon::GetConfig_Intervals;
        }
        else if( temp == "lp" )
        {
            function = Emetcon::GetConfig_LoadProfileInterval;
        }
        else if( temp == "li" )
        {
            function = Emetcon::GetConfig_DemandInterval;
        }
        else
        {
            function = Emetcon::DLCCmd_Invalid;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid interval type **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( function != Emetcon::DLCCmd_Invalid )
        {
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    //  needs to be moved to the 4xx base class when the inheritance gets reworked
    else if(parse.isKeyValid("holiday"))
    {
        function = Emetcon::GetConfig_Holiday;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    //  get raw memory locations
    else if(parse.isKeyValid("rawloc"))
    {
        int rawloc, rawlen;

        rawloc = parse.getiValue("rawloc");
        rawlen = 13;  //  default to 13 bytes

        if( parse.isKeyValid("rawlen") )
        {
            //  if a length was specified
            rawlen = parse.getiValue("rawlen");

            //  13 is max data return from an MCT
            if( rawlen > 13 )
            {
                rawlen = 13;
            }
        }

        function = Emetcon::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
        }

        OutMessage->Buffer.BSt.Function = rawloc;
        OutMessage->Buffer.BSt.Length   = rawlen;
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCT::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   function;
    INT   nRet = NoError;
    int   intervallength;
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));


    if( parse.isKeyValid("install") )
    {
        //  does a read of 2 bytes or so
        function = Emetcon::PutConfig_Install;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("armc") )
    {
        function = Emetcon::PutConfig_ARMC;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("arml") )
    {
        function = Emetcon::PutConfig_ARML;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("onoffpeak") )
    {
        function = Emetcon::PutConfig_OnOffPeak;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Message[0] = 0xf8 & ~0x04;  //  make sure the 0x04 bit is NOT set
    }
    else if( parse.isKeyValid("minmax") )
    {
        function = Emetcon::PutConfig_MinMax;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Message[0] = 0xf8 |  0x04;  //  make sure the 0x04 bit is set
    }
    else if( parse.isKeyValid("disconnect") )
    {
        function = Emetcon::PutConfig_Disconnect;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT410 )
        {
            long tmpaddr = _disconnectAddress & 0x3fffff;  //  make sure it's only 22 bits
            int  demand_threshold = 0,  //  default to no load limit
                 connect_delay    = 5;  //  default to a 5 minute connect delay

            if( parse.isKeyValid("disconnect demand threshold") &&
                parse.isKeyValid("disconnect load limit connect delay") )
            {
                demand_threshold = parse.getiValue("disconnect demand threshold");
                connect_delay    = parse.getiValue("disconnect load limit connect delay");

                if( demand_threshold < 0 || demand_threshold > 0xffff ||
                    connect_delay    < 0 || connect_delay    > 0x00ff )
                {
                    found = false;

                    if( errRet )
                    {
                        errRet->setResultString("Invalid disconnect parameters (" + CtiNumStr(demand_threshold) + ", " + CtiNumStr(connect_delay) + ")");
                        errRet->setStatus(NoMethod);
                        retList.push_back(errRet);

                        errRet = NULL;
                    }
                }
            }

            OutMessage->Buffer.BSt.Message[0] = (tmpaddr >> 16) & 0xff;
            OutMessage->Buffer.BSt.Message[1] = (tmpaddr >>  8) & 0xff;
            OutMessage->Buffer.BSt.Message[2] =  tmpaddr        & 0xff;

            OutMessage->Buffer.BSt.Message[3] = (demand_threshold >> 8) & 0xff;
            OutMessage->Buffer.BSt.Message[4] =  demand_threshold       & 0xff;

            OutMessage->Buffer.BSt.Message[5] = connect_delay & 0xff;
        }
    }
    else if( parse.isKeyValid("groupaddress_enable") )
    {
        if( parse.getiValue("groupaddress_enable") == 0 )
        {
            function = Emetcon::PutConfig_GroupAddrInhibit;
        }
        else
        {
            function = Emetcon::PutConfig_GroupAddrEnable;
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("address") )
    {
        if( parse.isKeyValid("uniqueaddress") )
        {
            int uadd;

            function = Emetcon::PutConfig_UniqueAddr;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            uadd = parse.getiValue("uniqueaddress");

            if( uadd > 0x3fffff || uadd < 0 )
            {
                found = false;

                if( errRet )
                {
                    temp = "Invalid address \"" + CtiNumStr(uadd) + string("\" for device \"") + getName() + "\", not sending";
                    errRet->setResultString(temp);
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);
                    errRet = NULL;
                }
            }
            else if( getAddress() != MCT_TestAddress1 && getAddress() != MCT_TestAddress2 )
            {
                found = false;

                if( errRet )
                {
                    temp = "Device must be set to one of the test addresses, not sending";
                    errRet->setResultString(temp);
                    errRet->setStatus(NoMethod);
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

            function = Emetcon::PutConfig_GroupAddr_GoldSilver;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else if( parse.isKeyValid("groupaddress_bronze") )
        {
            int bronze;

            function = Emetcon::PutConfig_GroupAddr_Bronze;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else if( parse.isKeyValid("groupaddress_lead_meter") && parse.isKeyValid("groupaddress_lead_load") )
        {
            int lead_load, lead_meter;

            function = Emetcon::PutConfig_GroupAddr_Lead;
            found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
                    errRet->setStatus(NoMethod);
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

            function = Emetcon::PutConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
            int iedtype = ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType();

            function = Emetcon::PutConfig_IEDClass;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Unknown IED type " << iedtype << " for device \'" << getName() << "\', aborting command **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

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
            function = Emetcon::PutConfig_Intervals;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            if( getType() == TYPEMCT410 )
            {
                OutMessage->Buffer.BSt.Message[0] = getLoadProfile().getLastIntervalDemandRate() / 60;
                OutMessage->Buffer.BSt.Message[1] = getLoadProfile().getLoadProfileDemandRate()  / 60;
                OutMessage->Buffer.BSt.Message[2] = getLoadProfile().getVoltageDemandInterval()  / 15;
                OutMessage->Buffer.BSt.Message[3] = getLoadProfile().getVoltageProfileRate()     / 60;
            }
            else
            {
                OutMessage->Buffer.BSt.Message[0] = getLoadProfile().getLastIntervalDemandRate() / 60;
                OutMessage->Buffer.BSt.Message[1] = getLoadProfile().getLoadProfileDemandRate()  / 60;
                OutMessage->Buffer.BSt.Message[2] = getLoadProfile().getLoadProfileDemandRate()  / 60;
            }
        }
        else if( temp == "lp" )
        {
            function = Emetcon::PutConfig_LoadProfileInterval;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            switch( getLoadProfile().getLoadProfileDemandRate() / 60 )
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
                        errRet->setStatus(NoMethod);
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
                if( getType() != TYPEMCT410 )
                {
                    intervallength *= 4;  //  all else are in multiples of 15 seconds
                }

                if( intervallength )
                {
                    function = Emetcon::PutConfig_DemandInterval;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else if(parse.isKeyValid("timesync"))
    {
        function = Emetcon::PutConfig_TSync;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        unsigned char ticper12hr, ticper5min, ticper15sec;

        if( getType() == TYPEMCT410 || getType() == TYPEMCT470 )
        {
            unsigned long time = NowTime.seconds();

            OutMessage->Buffer.BSt.Message[0] = 0xff;  //  global SPID
            OutMessage->Buffer.BSt.Message[1] = (time >> 24) & 0x000000ff;
            OutMessage->Buffer.BSt.Message[2] = (time >> 16) & 0x000000ff;
            OutMessage->Buffer.BSt.Message[3] = (time >>  8) & 0x000000ff;
            OutMessage->Buffer.BSt.Message[4] =  time        & 0x000000ff;
            OutMessage->Buffer.BSt.Message[5] = NowTime.isDST();
        }
        else
        {
            //  compute how many of each tic type have passed
            ticper12hr   = (NowDate.weekDay() % 7) * 2;  //  2 tics every day  (mod 7 because RW says Sunday is 7, we want it 0)
            ticper12hr  +=  NowTime.hour() / 12;         //  1 tic every 12 hours

            ticper5min   = (NowTime.hour() % 12) * 12;   //  12 tics per hour
            ticper5min  +=  NowTime.minute() / 5;        //  1 tic every 5 minutes

            ticper15sec  = (NowTime.minute() % 5) * 4;   //  4 tics per minute
            ticper15sec +=  NowTime.second() / 15;       //  1 tic every 15 seconds


            /*
            //  Figure out how many 15 second intervals left in this 5 minutes
            EmetFTime = 20 - ((TimeSt.tm_min % 5) * 60 + TimeSt.tm_sec) / 15;

            //  figure out how many AM/PM's left in the week
            Hour = TimeSt.tm_hour;
            EmetDay = (7 - TimeSt.tm_wday) * 2;
            if(Hour > 11)
            {
                EmetDay--;
                Hour -= 12;
            }

            //  figure out how many 5 minute periods left in this 12 hours
            EmetHTime = 144 - ((Hour * 12) + (TimeSt.tm_min / 5));
            */


            //  this seems wrong - I need to be rippling the changes down...
            //    the beginning of the week isn't 14*12 hours + 144*5 min + 20*15 sec...
            //    the numbers should be 13 + 143 + 20...  ?
            //  invert the counters to be tics REMAINING, not PASSED
            OutMessage->Buffer.BSt.Message[0] =  20 - ticper15sec;
            OutMessage->Buffer.BSt.Message[1] = 144 - ticper5min;
            OutMessage->Buffer.BSt.Message[2] =  14 - ticper12hr;
            OutMessage->Buffer.BSt.Message[3] = 94;  //  DLCFreq1
            OutMessage->Buffer.BSt.Message[4] = 37;  //  DLCFreq2
        }
    }
    else if(parse.isKeyValid("multiplier"))
    {
        unsigned long multbytes;

        if( getType() == TYPEMCT470 )
        {
            double multiplier = parse.getdValue("multiplier");
            int numerator, denominator;

            function = Emetcon::PutConfig_Multiplier;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            if( found )
            {
                //  this is broken - you can't accumulate 50,000 pulses before acknowledging 40,000 pulses
                //    so this is a faulty algorithm

                /*

                If Mp is greater than Kh, then the accumulator will accrue the difference, Mp - Kh, whenever a pulse is received.
                If Mp = Kh + 1, then the accumulator will be able to hold Kh - 1 as a maximum before the pulse is moved to the reading.
                However, on that next pulse, the meter will add Mp to Kh - 1, so the maximum in the accumulator can be stated as:

                Mp + Kh - 1

                The maximum range of the accumulator is:

                65535

                So the relationship between Mp and Kh can at most be:

                Mp + Kh - 1 = 65535
                Mp + Kh     = 65536

                So I choose to fix the upper bound for the denominator at 10,000 and allow the numerator to range from 1-50,000.


                   Mp    Kh
                     1/10000 =     0.0001
                     9/10000 =     0.0009
                    99/10000 =     0.0099
                   999/10000 =     0.0999
                  9999/10000 =     0.9999
                 49999/10000 =     4.9999
                 49999/ 1000 =    49.999
                 49999/  100 =   499.99
                 49999/   10 =  4999.9
                 49999/    1 = 49999.

                */


                if( multiplier > 50000 )
                {
                    temp = "Multiplier too large - must be less than 50000";
                    errRet->setResultString( temp );
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
                else if( multiplier < 0.0001 )
                {
                    temp = "Multiplier too small - must be at least 0.0001";
                    errRet->setResultString( temp );
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }

                denominator = 10000;

                //  ex:  multiplier = 4.097, denominator = 10000
                //         result = 40,970 <--  suitable numerator
                //       multiplier = 689,   denominator = 10000
                //         result = 6,890,000  <--  unsuitable numerator, divide denominator by 10
                //       multiplier = 689,   denominator =  1000
                //         result = 689,000    <--  unsuitable numerator, divide denominator by 10
                //       multiplier = 689,   denominator =   100
                //         result = 68,900     <--  unsuitable numerator, divide denominator by 10
                //       multiplier = 689,   denominator =    10
                //         result = 6,890      <--  suitable numerator

                while( (multiplier * denominator) > 50000 )
                {
                    denominator /= 10;
                }

                numerator = (int)(multiplier * denominator);

                OutMessage->Buffer.BSt.Message[0] = (numerator   >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[1] =  numerator         & 0xff;

                OutMessage->Buffer.BSt.Message[2] = (denominator >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[3] =  denominator       & 0xff;

                OutMessage->Buffer.BSt.Function += (parse.getiValue("multoffset") - 1) * CtiDeviceMCT470::MCT470_Memory_ChannelOffset;
            }
        }
        else
        {
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
                    errRet->setResultString( temp );
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }

            OutMessage->Buffer.BSt.Message[0] = (multbytes >> 8) & 0xFF;  //  bits 15-8
            OutMessage->Buffer.BSt.Message[1] =  multbytes       & 0xFF;  //  bits  7-0

            switch( parse.getiValue("multoffset") )
            {
                default:
                case 1:
                    function = Emetcon::PutConfig_Multiplier;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                    break;

                case 2:
                    function = Emetcon::PutConfig_Multiplier2;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                    break;

                case 3:
                    function = Emetcon::PutConfig_Multiplier3;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                    break;
            }
        }
    }
    else if(parse.isKeyValid("rawloc"))
    {
        function = Emetcon::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

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
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Write;
        }
    }

    if( errRet )
    {
        delete errRet;
    }

    if(!found)
    {
       nRet = NoMethod;
    }
    else
    {
       // Load all the other stuff that is needed
       OutMessage->DeviceID  = getID();
       OutMessage->TargetID  = getID();
       OutMessage->Port      = getPortID();
       OutMessage->Remote    = getAddress();
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 2;

       OutMessage->Request.RouteID   = getRouteID();
       strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCT::executeControl(CtiRequestMsg                  *pReq,
                                 CtiCommandParser               &parse,
                                 OUTMESS                        *&OutMessage,
                                 list< CtiMessage* >      &vgList,
                                 list< CtiMessage* >      &retList,
                                 list< OUTMESS* >         &outList)
{
    bool found = false;
    bool dead_air = false;

    INT   nRet = NoError;

    INT function;

    if(parse.getFlags() & CMD_FLAG_CTL_SHED)
    {
        int shed_duration, shed_function_base, shed_function, relay_mask;

        shed_duration = parse.getiValue("shed");
        relay_mask    = parse.getiValue("relaymask");

        function = Emetcon::Control_Shed;

        if(getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            if( shed_duration > 0 )
            {
                if( shed_duration <= 450 )
                {
                    shed_function_base = MCT_Shed_Base_07m;
                }
                else if( shed_duration <= 900 )
                {
                    shed_function_base = MCT_Shed_Base_15m;
                }
                else if( shed_duration <= 1800 )
                {
                    shed_function_base = MCT_Shed_Base_30m;
                }
                else
                {
                    shed_function_base = MCT_Shed_Base_60m;
                }

                //  if at least one of relays a-d (1-4) are selected
                if( (relay_mask & 0x0f) > 0x00 )
                {
                    shed_function = shed_function_base | (relay_mask & 0x0f);

                    OutMessage->Buffer.BSt.Function = shed_function;

                    found = true;
                }
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_RESTORE)
    {
        function = Emetcon::Control_Restore;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_OPEN)
    {
        function = Emetcon::Control_Open;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT410 )
        {
            //  the 410 requires some dead time to transmit to its disconnect base
            dead_air = true;

            //  do not allow the disconnect command to be sent to a meter that has no disconnect address
            if( !_disconnectAddress )
            {
                CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                if( ReturnMsg )
                {
                    ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                    ReturnMsg->setResultString(getName() + " / Disconnect command cannot be sent to an empty (zero) address");

                    retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                }

                found = false;
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_CLOSE)
    {
        function = Emetcon::Control_Close;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT410 )
        {
            //  the 410 requires some dead time to transmit to its disconnect base
            dead_air = true;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_CONNECT)
    {
        function = Emetcon::Control_Conn;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT410 )
        {
            //  the 410 requires some dead time to transmit to its disconnect base
            dead_air = true;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_DISCONNECT)
    {
        function = Emetcon::Control_Disc;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( getType() == TYPEMCT410 )
        {
            //  the 410 requires some dead time to transmit to its disconnect base
            dead_air = true;

            //  do not allow the disconnect command to be sent to a meter that has no disconnect address
            if( !_disconnectAddress )
            {
                CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                if( ReturnMsg )
                {
                    ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                    ReturnMsg->setResultString(getName() + " / Disconnect command cannot be sent to an empty (zero) address");

                    retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                }

                found = false;
            }
        }
    }
    else if(parse.isKeyValid("latch_relays"))
    {
        string relays = parse.getsValue("latch_relays");

        function = Emetcon::Control_Latch;
        found    = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        //  binary logic - 00, 01, 10, 11
        //    latch_relays may also contain "none", but that's the default case and doesn't need to be explicitly handled
        if( relays.find("(a)") != string::npos )  OutMessage->Buffer.BSt.Function += 1;
        if( relays.find("(b)") != string::npos )  OutMessage->Buffer.BSt.Function += 2;
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        if( dead_air )
        {
            OutMessage->MessageFlags |= MessageFlag_AddSilence;
        }

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        outList.push_back( OutMessage );

        OutMessage = NULL;
    }


    return nRet;
}


INT CtiDeviceMCT::decodeLoopback(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / successful ping";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodeGetValue(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;
    CtiPointSPtr         pPoint;

    double Value;
    string resultStr;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        switch( InMessage->Sequence )
        {
            case Emetcon::GetValue_PFCount:
            {
                int pfCount, i;

                pfCount = 0;

                for(i = 0; i < 2; i++)
                {
                    pfCount = (pfCount << 8) + InMessage->Buffer.DSt.Message[i];
                }

                resultStr = getName() + " / Blink Counter = " + CtiNumStr(pfCount);

                if( (pPoint = getDevicePointOffsetTypeEqual( MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType )) )
                {
                    Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pfCount);

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), (double)Value, NormalQuality, PulseAccumulatorPointType);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().push_back(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }

                ReturnMsg->setResultString( resultStr );
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodeGetConfig(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;
    CtiCommandParser parse(InMessage->Return.CommandStr);

    int min, sec, channel;
    unsigned long multnum;  //  multiplier numerator - the value returned is multiplier * 1000
    double mult;  //  where the final multiplier value will be computed

    string resultStr;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        switch( InMessage->Sequence )
        {
            //  needs to be moved to the 4xx base class when the inheritance gets reworked
            case Emetcon::GetConfig_Holiday:
            {
                unsigned long seconds;
                CtiTime holiday;
                string result;

                result  = getName() + " / Holiday schedule:\n";

                seconds = DSt->Message[0] << 24 |
                          DSt->Message[1] << 16 |
                          DSt->Message[2] << 8  |
                          DSt->Message[3];

                holiday = CtiTime(seconds + rwEpoch);
                result += "Holiday 1: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

                seconds = DSt->Message[4] << 24 |
                          DSt->Message[5] << 16 |
                          DSt->Message[6] << 8  |
                          DSt->Message[7];

                holiday = CtiTime(seconds + rwEpoch);
                result += "Holiday 2: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

                seconds = DSt->Message[8]  << 24 |
                          DSt->Message[9]  << 16 |
                          DSt->Message[10] << 8  |
                          DSt->Message[11];

                holiday = CtiTime(seconds + rwEpoch);
                result += "Holiday 3: " + (holiday.isValid()?holiday.asString():"(invalid)") + "\n";

                ReturnMsg->setResultString( result );

                break;
            }
            case Emetcon::GetConfig_GroupAddress:
            {
                long gold, silver, bronze, lead_load, lead_meter;

                bronze     = DSt->Message[0];
                lead_load  = ((DSt->Message[3] & 0xf0) << 4) | DSt->Message[1];
                lead_meter = ((DSt->Message[3] & 0x0f) << 8) | DSt->Message[2];
                gold       = (DSt->Message[4] & 0xc0) >> 6;
                silver     = (DSt->Message[4] & 0x3f);

                resultStr  = getName() + " / Group Addresses:\n";
                resultStr += "Gold:       " + CtiNumStr(gold + 1).spad(5) + string("\n");
                resultStr += "Silver:     " + CtiNumStr(silver + 1).spad(5) + string("\n");
                resultStr += "Bronze:     " + CtiNumStr(bronze + 1).spad(5) + string("\n");
                resultStr += "Lead Meter: " + CtiNumStr(lead_meter + 1).spad(5) + string("\n");
                resultStr += "Lead Load:  " + CtiNumStr(lead_load + 1).spad(5) + string("\n");

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case Emetcon::GetConfig_DemandInterval:
            {
                //  see MCT22X ResultDecode for an additional MCT22X step

                sec = DSt->Message[0] * 15;

                min = sec / 60;
                sec = sec % 60;

                resultStr = getName() + " / Demand Interval: " + CtiNumStr( min ) + " min";
                if( sec )
                    resultStr += ", " + CtiNumStr( sec ) + string(" sec");

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case Emetcon::GetConfig_LoadProfileInterval:
            {
                min = DSt->Message[0] * 5;

                resultStr = getName() + " / Load Profile Interval: " + CtiNumStr( min ) + " min";

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case Emetcon::GetConfig_Multiplier:
            case Emetcon::GetConfig_Multiplier2:
            case Emetcon::GetConfig_Multiplier3:
            case Emetcon::GetConfig_Multiplier4:
            {
                resultStr  = getName() + " / ";

                if( parse.isKeyValid("multchannel") )
                {
                    channel = parse.getiValue("multchannel");
                    resultStr += "channel " + CtiNumStr( channel );
                }


                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "message[0] = " << (int)DSt->Message[0] << endl;
                    dout << "message[1] = " << (int)DSt->Message[1] << endl;
                }

                multnum   = (int)DSt->Message[0];
                multnum <<= 8;
                multnum  |= (int)DSt->Message[1];

                if( getType() == TYPEMCT470 )
                {
                    int k;

                    k   = (int)DSt->Message[2];
                    k <<= 8;
                    k  |= (int)DSt->Message[3];

                    resultStr = "Mp: " + CtiNumStr((int)multnum) + string(", Kh: ") + CtiNumStr((int)k) +
                                ", metering ratio: " + CtiNumStr((double)multnum/(double)k, 3) + "\n";
                }
                else
                {
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
                }

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case Emetcon::GetConfig_Time:
            case Emetcon::GetConfig_TSync:
            {
                char days[8][4] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat","???"};
                unsigned char ticper12hr, ticper5min, ticper15sec;
                int day, hour, minute;

                //  time values are kept by 3 decrementing counters.
                //    Message[2] decrements once every 12 hours, and starts from 14 (7 days left, denoting Sunday midnight).
                //    Message[1] decrements once every 5 minutes, and starts from 144 (12 hours left).
                //    Message[0] decrements once every 15 seconds, and starts from 20 (5 minutes left).

                ticper12hr  = 14  - DSt->Message[2];  //  invert counter to be how many units have PASSED,
                ticper5min  = 144 - DSt->Message[1];  //    NOT how many are LEFT.
                ticper15sec = 20  - DSt->Message[0];  //

                day = (ticper12hr * 12) / 24;       //  find how many days have passed
                if( day > 7 )
                    day = 7;

                hour  = (ticper5min * 5) / 60;      //  find out how many hours have passed
                hour += (ticper12hr * 12) % 24;     //    add on 12 hours if in PM (ticper12hr = 0 - Sunday AM, 1 - Sunday PM, etc)

                minute  = (ticper5min * 5) % 60;    //  find out how many minutes have passed
                minute += (ticper15sec * 15) / 60;  //    add on the 15 second timer - divide by 4 to get minutes

                if( InMessage->Sequence == Emetcon::GetConfig_Time )
                {
                    resultStr = getName() + " / time:  ";
                }
                else if( InMessage->Sequence == Emetcon::GetConfig_TSync )
                {
                    resultStr = getName() + " / time sync:  ";
                }

                resultStr += string(days[day]) + " " + CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2);

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case Emetcon::GetConfig_Raw:
            {
                int rawloc, rawlen;

                rawloc = parse.getiValue("rawloc");

                if( parse.isKeyValid("rawlen") )
                {
                    rawlen = parse.getiValue("rawlen");
                }
                else
                {
                    rawlen = InMessage->Buffer.DSt.Length;
                }

                for( int i = 0; i < rawlen; i++ )
                {
                    resultStr += getName( ) + " / raw:  " +
                              CtiNumStr(i+rawloc).xhex().zpad(2) + " : " + CtiNumStr((int)InMessage->Buffer.DSt.Message[i]).xhex().zpad(2) + "\n";
                }

                ReturnMsg->setResultString( resultStr );

                break;
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodeGetStatusDisconnect(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;
    CtiPointSPtr    pPoint;

    double    Value;
    string resultStr, defaultStateName;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        Value = CLOSED;

        switch( getType() )
        {
            case TYPEMCT213:
            {
                switch( DSt->Message[0] & 0xc0 )
                {
                    case CtiDeviceMCT210::MCT210_StatusConnected:     Value = CLOSED;  defaultStateName = "Connected";      break;
                    case CtiDeviceMCT210::MCT210_StatusDisconnected:  Value = OPENED;  defaultStateName = "Disconnected";   break;
                    default:  Value = -1;
                }

                break;
            }
            case TYPEMCT310ID:
            case TYPEMCT310IDL:
            {
                switch( DSt->Message[0] & 0xc0 )
                {
                    case CtiDeviceMCT310::MCT310_StatusConnected:           Value = CLOSED;         defaultStateName = "Connected";             break;
                    case CtiDeviceMCT310::MCT310_StatusConnectArmed:        Value = INDETERMINATE;  defaultStateName = "Connect armed";         break;
                    case CtiDeviceMCT310::MCT310_StatusConnectInProgress:   Value = INDETERMINATE;  defaultStateName = "Connect in progress";   break;
                    case CtiDeviceMCT310::MCT310_StatusDisconnected:        Value = OPENED;         defaultStateName = "Disconnected";          break;
                }

                break;
            }
            case TYPEMCT410:
            {
                switch( DSt->Message[0] & 0x03 )
                {
                    case CtiDeviceMCT410::MCT410_RawStatus_Connected:
                    {
                        Value = CtiDeviceMCT410::MCT410_StateGroup_Connected;                   defaultStateName = "Connected";                 break;
                    }
                    case CtiDeviceMCT410::MCT410_RawStatus_ConnectArmed:
                    {
                        Value = CtiDeviceMCT410::MCT410_StateGroup_ConnectArmed;                defaultStateName = "Connect armed";             break;
                    }
                    case CtiDeviceMCT410::MCT410_RawStatus_DisconnectedUnconfirmed:
                    {
                        Value = CtiDeviceMCT410::MCT410_StateGroup_DisconnectedUnconfirmed;     defaultStateName = "Unconfirmed disconnected";  break;
                    }
                    case CtiDeviceMCT410::MCT410_RawStatus_DisconnectedConfirmed:
                    {
                        Value = CtiDeviceMCT410::MCT410_StateGroup_DisconnectedConfirmed;       defaultStateName = "Confirmed disconnected";    break;
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
                Value = INDETERMINATE;
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

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}



INT CtiDeviceMCT::decodePutValue(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / command complete";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodePutStatus(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / command complete";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodePutConfig(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;
    OUTMESS *OutTemplate;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiRequestMsg *pReq;

    bool expectMore = false;

    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        switch( InMessage->Sequence )
        {
            case Emetcon::PutConfig_Install:
            {
                int sspec;
                bool sspecValid;

                //  LMT-2 sspec - it only has 1 sspec byte...
                //    make sure any additional sspec rev numbers do not have 36 as their least-significant byte,
                //    or this will have to change.
                //
                //  36, 292, 548, 804, 1060, 1316, 1572, 1828, 2084, 2340, 2596, 2852, 3108, 3364, 3620, 3876, ...

                if( DSt->Message[0] == 36 )
                {
                    sspec = DSt->Message[0];
                }
                else
                {
                    sspec = DSt->Message[0] + (DSt->Message[4] << 8);
                }

                //  if it's an invalid sspec or if the option bits aren't set properly
                if( !sspecIsValid(sspec) )
                {
                    resultString = getName( ) + " / sspec \'" + CtiNumStr(sspec) + "\' not valid - looks like an \'" + sspecIsFrom( sspec ) + "\'." + "\n" +
                                   getName( ) + " / install command aborted";
                }
                else if( (getType() == TYPEMCT310ID || getType() == TYPEMCT310IDL) && (sspec == 1007 || sspec == 153) && !(DSt->Message[2] & 0x40) )
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
                        if( !(DSt->Message[2] & 0x01) )
                        {
                            resultString = getName() + " / Error:  Metering channel 1 not enabled" + "\n";
                        }
                    }

                    OutTemplate = CTIDBG_new OUTMESS;

                    InEchoToOut( InMessage, OutTemplate );

                    //  reset the meter
                    pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putstatus reset", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                    if( pReq != NULL )
                    {
                        if( strstr(InMessage->Return.CommandStr, "noqueue") )
                        {
                            pReq->setCommandString(pReq->CommandString() + " noqueue");
                        }

                        CtiCommandParser parse(pReq->CommandString());
                        CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);

                        delete pReq;
                    }

                    //  enable group addressing
                    pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon group enable", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                    if( pReq != NULL )
                    {
                        if( strstr(InMessage->Return.CommandStr, "noqueue") )
                        {
                            pReq->setCommandString(pReq->CommandString() + " noqueue");
                        }

                        CtiCommandParser parse(pReq->CommandString());
                        CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);

                        delete pReq;
                    }

                    //  put the load profile interval if it's a lp device
                    if( isLoadProfile(getType()) )
                    {
                        pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon interval lp", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                        if( pReq != NULL )
                        {
                            if( strstr(InMessage->Return.CommandStr, "noqueue") )
                            {
                                pReq->setCommandString(pReq->CommandString() + " noqueue");
                            }

                            CtiCommandParser parse(pReq->CommandString());
                            CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);

                            delete pReq;
                        }
                    }

                    //  put the demand interval
                    if( hasVariableDemandRate(getType(), sspec) )
                    {
                        pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon interval li", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                        if( pReq != NULL )
                        {
                            if( strstr(InMessage->Return.CommandStr, "noqueue") )
                            {
                                pReq->setCommandString(pReq->CommandString() + " noqueue");
                            }

                            CtiCommandParser parse(pReq->CommandString());
                            CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);
                            delete pReq;
                        }
                    }

                    //  We've already checked for the validity of this config in the setConfigData call, so it's safe to just forge ahead
                    if( _configType == Config2XX )
                    {
                        if( _mpkh[0] > 0 )
                        {
                            pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon mult kyz 1 " + CtiNumStr(_mpkh[0]), InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                            if( pReq != NULL )
                            {
                                if( strstr(InMessage->Return.CommandStr, "noqueue") )
                                {
                                    pReq->setCommandString(pReq->CommandString() + " noqueue");
                                }

                                CtiCommandParser parse(pReq->CommandString());
                                CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);
                                delete pReq;
                            }

                            resultString += getName() + " / Sent config \"" + _configName + "\" to MCT\n";
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - can't send MPKH \"" << _mpkh[0] << "\" to meter \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
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

                        pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon raw start=0x03 " + CtiNumStr(config_byte).xhex().zpad(2), InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                        if( pReq != NULL )
                        {
                            if( strstr(InMessage->Return.CommandStr, "noqueue") )
                            {
                                pReq->setCommandString(pReq->CommandString() + " noqueue");
                            }

                            CtiCommandParser parse(pReq->CommandString());
                            CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);
                            delete pReq;
                        }

                        for( int i = 0; i < num_channels; i++ )
                        {
                            if( _mpkh[i] > 0 )
                            {
                                pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon mult kyz " + CtiNumStr(i+1) + string(" ") + CtiNumStr(_mpkh[i]), InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);

                                if( pReq != NULL )
                                {
                                    if( strstr(InMessage->Return.CommandStr, "noqueue") )
                                    {
                                        pReq->setCommandString(pReq->CommandString() + " noqueue");
                                    }

                                    CtiCommandParser parse(pReq->CommandString());
                                    CtiDeviceBase::ExecuteRequest(pReq, parse, vgList, retList, outList, OutTemplate);
                                    delete pReq;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - can't send MPKH \"" << _mpkh[i] << "\" to channel " << i+1 << " on meter \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }

                        resultString += getName() + " / Sent config \"" + _configName + "\" to MCT \"" + getName() + "\"\n";
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

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( resultString );

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        if( InMessage->MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection)!=0 )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


bool CtiDeviceMCT::isLoadProfile( int type )
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


bool CtiDeviceMCT::hasVariableDemandRate( int type, int sspec )
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


DOUBLE CtiDeviceMCT::translateStatusValue (INT PointOffset, INT PointType, INT DeviceType, PUSHORT DataValueArray)
{
    /* key off the point offset */
    switch(PointOffset)
    {
        case 1:
        {
            /* first physical point */
            switch(DeviceType)
            {
                case TYPELMT2:
                case TYPEDCT501:
                case TYPEMCT250:
                    /* only this point is a legal 3-state 2 of the types */
                    if(PointType == THREESTATEPOINT)
                    {
                        /* its a three state status */
                        if(DataValueArray[4] & STATUS1_BIT && DataValueArray[4] & STATUS2_BIT)
                        {
                            return((DOUBLE) STATEFOUR);
                        }
                        else if(DataValueArray[4] & STATUS1_BIT)
                        {
                            return((DOUBLE) CLOSED);
                        }
                        else if(DataValueArray[4] & STATUS2_BIT)
                        {
                            return((DOUBLE) OPENED);
                        }
                        else
                        {
                            return((DOUBLE) INDETERMINATE);
                        }
                    }
                    else
                    {
                        if(DataValueArray[4] & STATUS1_BIT)
                        {
                            return((DOUBLE) CLOSED);
                        }
                        else
                        {
                            return((DOUBLE) OPENED);
                        }
                    }
                case TYPEMCT248:
                    if(DataValueArray[7] == 66)
                    {
                        return((DOUBLE) CLOSED);
                    }

                    if(DataValueArray[7] == 65)
                    {
                        return((DOUBLE) OPENED);
                    }

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    return((DOUBLE) INVALID);

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS1_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
               return((DOUBLE) INVALID);
            }
         }

         break;
      }
   case 2:
      {
         /* second physical point */
         switch(DeviceType)
         {
         case TYPELMT2:
         case TYPEDCT501:
         case TYPEMCT250:
            if(DataValueArray[4] & STATUS2_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS2_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }


         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);

         }

         break;
      }
   case 3:
      {
         /* third physical point */
         switch(DeviceType)
         {
         case TYPEMCT250:
            if(PointType == THREESTATEPOINT)
            {
               /* its a three state status */
               if(DataValueArray[6] & STATUS3_BIT && DataValueArray[6] & STATUS4_BIT)
               {
                  return((DOUBLE) STATEFOUR);
               }
               else if(DataValueArray[6] & STATUS3_BIT)
               {
                  return((DOUBLE) CLOSED);
               }
               else if(DataValueArray[6] & STATUS4_BIT)
               {
                  return((DOUBLE) OPENED);
               }
               else
               {
                  return((DOUBLE) INDETERMINATE);
               }
            }

            if(DataValueArray[6] & STATUS3_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS3_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 4:
      {
         /* fourth physical point */
         switch(DeviceType)
         {
         case TYPEMCT250:
            if(DataValueArray[6] & STATUS4_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS4_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 5:
      {
         /* fifth physical point */
         switch(DeviceType)
         {

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS5_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 6:
      {
         /* sixth physical point */
         switch(DeviceType)
         {

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS6_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 7:
      {
         /* seventh physical point */
         switch(DeviceType)
         {

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS7_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 8:
      {
         /* eighth physical point */
         switch(DeviceType)
         {

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
         case TYPEMCT310IDL:
         case TYPEMCT318:
         case TYPEMCT310IL:
         case TYPEMCT318L:
         case TYPEMCT360:
         case TYPEMCT370:
            if(DataValueArray[0] & STATUS8_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 9:
      {
         /* time sync flag */
         switch(DeviceType)
         {
         case TYPELMT2:
         case TYPEDCT501:
         case TYPEMCT212:
         case TYPEMCT224:
         case TYPEMCT226:
         case TYPEMCT240:
         case TYPEMCT242:
         case TYPEMCT250:
            if(DataValueArray[0])
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 10:
      {
         /* Long Power Fail Flag */
         switch(DeviceType)
         {
         case TYPELMT2:
         case TYPEDCT501:
         case TYPEMCT212:
         case TYPEMCT224:
         case TYPEMCT226:
         case TYPEMCT240:
         case TYPEMCT242:
         case TYPEMCT250:
            if(DataValueArray[4] & L_PWRFAIL_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         case TYPEMCT310:
            if(DataValueArray[4] & L_PWRFAIL310_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         default:
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 11:
      {
         /* Short Power Fail Flag */
         if(DeviceType == TYPEMCT310)
         {
            /* special bit for mct310 */
            if(DataValueArray[4] & S_PWRFAIL310_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);
         }
         else
         {
            if(DataValueArray[4] & S_PWRFAIL_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);
         }

         break;
      }
   case 12:
      {
         /* reading overflow Flag */
         if(DeviceType == TYPEMCT310)
         {
            /* special bit for mct310 */
            if(DataValueArray[4] & OVERFLOW310_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);
         }
         else
         {
            if(DataValueArray[4] & OVERFLOW_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);
         }

         break;
      }
   case 13:
   case 15:
      {
         /* load survey or Tou active Flag */
         switch(DeviceType)
         {
         case TYPELMT2:
         case TYPEDCT501:
         case TYPEMCT240:
         case TYPEMCT242:
         case TYPEMCT250:
            /* load survey halt flag */
            if(DataValueArray[3])
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 14:
      {
         /* Tamper or override/local-remote flag */
         switch(DeviceType)
         {
         case TYPEDCT501:
         case TYPELMT2:
            if(DataValueArray[2])
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         case TYPEMCT210:
         case TYPEMCT212:
         case TYPEMCT213:
         case TYPEMCT224:
         case TYPEMCT226:
         case TYPEMCT240:
         case TYPEMCT242:
         case TYPEMCT248:
         case TYPEMCT250:
            /* This is the tamper flag */
            if(DataValueArray[4] & TAMPER_BIT)
            {
               return((DOUBLE) CLOSED);
            }

            return((DOUBLE) OPENED);

         default:
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return((DOUBLE) INVALID);
         }

         break;
      }
   case 16:
      {
         /* tou halt flag */
         switch(DeviceType)
         {
         case TYPELMT2:
         case TYPEDCT501:
            if(DataValueArray[1])
            {
               return((DOUBLE)  CLOSED);
            }
            else
            {
               return((DOUBLE)  OPENED);
            }

            break;


         break;
      }
      default:
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return((DOUBLE)  INVALID);
      }
   }

   return(NORMAL);
}


INT CtiDeviceMCT::extractStatusData(INMESS *InMessage, INT type, USHORT *StatusData)
{
   INT i;
   INT status = NORMAL;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   /* extract status data by device type */
   switch(type)
   {
   case TYPEMCT210:
   case TYPEMCT310:
      /* only 2 bytes for these guys */
      StatusData[4] = MAKESHORT(DSt->Message[0], 0);
      StatusData[5] = MAKESHORT(DSt->Message[1], 0);
      break;

   case TYPEMCT318:
   case TYPEMCT318L:
   case TYPEMCT360:
   case TYPEMCT370:
      /* only 1 byte of status data */
      StatusData[0] = MAKESHORT (DSt->Message[0], 0);
      break;

   case TYPEMCT248:
      /* 248'S must be change around alittle so it looks like the
      other devices because we must read more data to get the
      mct248 relay status */

      for(i = 0; i < 6; i++)
      {
         /* put the data in order for translating */
         StatusData[i] = MAKESHORT(DSt->Message[i + 7], 0);
      }

      /* make the cap status the last byte */
      StatusData[7] = MAKESHORT(DSt->Message[0], 0);
      break;

   default:
      for(i = 0; i < 7; i++)
      {
         /* put the data in order for translating */
         StatusData[i] = MAKESHORT(DSt->Message[i], 0);

      }
      StatusData[7] = 0;

   }   /* end of device type switch */

   return status;
}

// static method
INT CtiDeviceMCT::verifyAlphaBuffer(DSTRUCT *DSt)
{
   int x;
   int status = NORMAL;

   /* this indicates that the data we have back is the
    * Alpha Meter Demand Data and we need to make sure
    * that the data buffer is good (if each byte is the
    * same and not 0 its bad).
    */

   if(DSt->Message[1] != 0)
   {
      /* check to make sure that all values are not the same */
      for(x = 2; x < DSt->Length; x++)
      {
         if(DSt->Message[x - 1] != DSt->Message[x])
         {
            break;   /* values are different buffer is good */
         }
      }

      if(x == DSt->Length)
      {
         status = ALPHABUFFERERROR;  /* went through the whole loop and all is was the same */
      }
   }

   return status;
}

bool CtiDeviceMCT::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
   bool found = false;

   if(_commandStore.empty())  // Must initialize!
   {
      initCommandStore();
   }

   DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )     // It's prego!
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;     // Copy over the found funcLen pair!
      length = cs._funcLen.second;      // Copy over the found funcLen pair!
      io = cs._io;
      found = true;
   }

   return found;
}


INT CtiDeviceMCT::calcAndInsertLPRequests(OUTMESS *&OutMessage, list< OUTMESS* > &outList)
{
    INT nRet = NoError;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default load profile logic handler - request deleted" << endl;
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


bool CtiDeviceMCT::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default load profile request location handler - request deleted" << endl;
    }

    return false;
}


void CtiDeviceMCT::setConfigData( const string &configName, int configType, const string &configMode, const int mctwire[MCTConfig_ChannelCount], const double mpkh[MCTConfig_ChannelCount] )
{
    _configName = configName;

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
        case TYPEMCT260:
        {
            if( configType == Config2XX )
            {
                _configType = Config2XX;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - invalid config type \"" << configType << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - invalid config type \"" << configType << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }

        //case TYPEDCT501
        //case TYPEMCT410
        //case TYPE_REPEATER900
        //case TYPE_REPEATER800
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _configType = ConfigInvalid;

            break;
        }
    }

    if( !findStringIgnoreCase(configMode,"peakoffpeak") )    _peakMode = PeakModeOnPeakOffPeak;
    else if( !findStringIgnoreCase(configMode,"minmax") )    _peakMode = PeakModeMinMax;
    else
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid peak mode string \"" + configMode + "\" - defaulting to minmax **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - invalid wire config \"" << mctwire[i] << " for channel " << i+1 << " - defaulting to three-wire **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _wireConfig[i] = WireConfigInvalid;
        }
    }
}


int CtiDeviceMCT::getNextFreeze( void ) const
{
    return ((_expected_freeze + 1) % 2) + 1;
}


void CtiDeviceMCT::setExpectedFreeze( int next_freeze )
{
    //  this function should be called even when a manual freeze is sent

    if( next_freeze == 1 || next_freeze == 2 )
    {
        _expected_freeze = next_freeze - 1;

        //  if it's uninitialized,
        if( (_freeze_counter < 0) && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter) )
        {
            _freeze_counter = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter);
        }

        if( (_freeze_counter > 0) &&
            (_freeze_counter % 2) != _expected_freeze )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - _freeze_counter = " << _freeze_counter << ", next_freeze = " << next_freeze << " in setExpectedFreeze() for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, CtiTime::now().seconds());
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_ExpectedFreeze, (long)_expected_freeze);
    }
}
