#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/05/06 19:20:34 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include <utility>
using namespace std;

#include <rw\cstring.h>
#include "numstr.h"

#include "devicetypes.h"
#include "device.h"
#include "dev_mct.h"
#include "dev_mct31x.h"  //  for IED scanning capability
#include "logger.h"
#include "mgr_point.h"
#include "pt_numeric.h"
#include "pt_accum.h"
#include "porter.h"
#include "utility.h"
#include "yukon.h"

set< CtiDLCCommandStore > CtiDeviceMCT::_commandStore;


CtiDeviceMCT::CtiDeviceMCT() :
    _magicNumber(0),
    _lpIntervalSent(0)
    {  }

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

INT CtiDeviceMCT::getSSpec() const
{
    return 0;
}

int CtiDeviceMCT::sspecIsValid( int sspec )
{
    int valid = FALSE;

    switch( getType() )
    {
        case TYPEMCT210:
        case TYPEMCT213:
            if( sspec == 95 )
                valid = TRUE;
            break;

        case TYPEMCT212:
        case TYPEMCT224:
        case TYPEMCT226:
            if( sspec == 74 )
                valid = TRUE;
            break;

        case TYPEMCT240:
            if( sspec == 93 || sspec == 121 )
                valid = TRUE;
            break;

        case TYPEMCT242:
        case TYPEMCT248:
            if( sspec == 121 )
                valid = TRUE;
            break;

        case TYPEMCT250:
            if( sspec == 111 )
                valid = TRUE;
            break;

        case TYPEMCT310:
        case TYPEMCT310ID:
            if( sspec == 153 )
                valid = TRUE;
            break;

        case TYPEMCT310IL:
        case TYPEMCT318L:
            if( sspec == 1007 )
                valid = TRUE;
            break;

        case TYPEMCT318:
        case TYPEMCT360:
        case TYPEMCT370:
            if( sspec == 218 )
                valid = TRUE;
            break;

        default:
            break;
    }

    return valid;
}


RWCString CtiDeviceMCT::sspecIsFrom( int sspec )
{
    RWCString whois;

    switch( sspec )
    {
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
            whois = "MCT 31xL";
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
RWCString CtiDeviceMCT::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}


LONG CtiDeviceMCT::getDemandInterval() const
{
    LONG retval = MCT_DEMANDINTERVAL_DEFAULT;

    if( getLastIntervalDemandRate() )
        retval = getLastIntervalDemandRate();

    return retval;
}


bool CtiDeviceMCT::clearedForScan(int scantype)
{
    bool status = false;

    switch(scantype)
    {
        case ScanRateGeneral:
        {
            status = ( !isScanPending() );
            break;
        }
        case ScanRateIntegrity:
        {
            status = ( !isScanPending() );
            break;
        }
        case ScanRateAccum:
        {
            status = true; // CGP 032101  (!isScanFreezePending()  && !isScanResetting());
            break;
        }
        case ScanRateLoadProfile:
        {
           status = true;
           break;
        }
    }

    status = validatePendingStatus(status, scantype);

    return status;
}

void CtiDeviceMCT::resetForScan(int scantype)
{
    // OK, it is five minutes past the time I expected to have scanned this bad boy..
    switch(scantype)
    {
        case ScanRateGeneral:
        case ScanRateIntegrity:
        case ScanRateAccum:
        {
            if(isScanFreezePending())
            {
                resetScanFreezePending();
                setScanFreezeFailed();
            }

            if(isScanPending())
            {
                resetScanPending();
            }

            if(isScanResetting())
            {
                resetScanResetting();
                setScanResetFailed();
            }
            break;
        }
    }
}


RWTime CtiDeviceMCT::adjustNextScanTime(const INT scanType)
{
    RWTime Now;
    RWTime When(YUKONEOT);    // This is never!
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


void CtiDeviceMCT::sendLPInterval( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->RouteID   = getRouteID();
//    OutMessage->Priority  = MAXPRIORITY - 1;
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;     // Helps us figure it out later!
    OutMessage->Retry     = 3;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strcpy(OutMessage->Request.CommandStr, "putconfig emetcon interval lp");

    outList.insert(OutMessage);
    OutMessage = NULL;

    _lpIntervalSent = TRUE;
}


int CtiDeviceMCT::checkLoadProfileQuality( unsigned long &pulses, PointQuality_t &quality, int &badData )
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
            badData = TRUE;
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
   cs._cmd     = CtiProtocolEmetcon::GetConfig_Model;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_ModelAddr,
                            (int)MCT_ModelLen );   // Decode happens in the children please...
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Command_Loop;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_ModelAddr, 1 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_Install;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_ModelAddr,
                            (int)MCT_SspecLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::GetConfig_Raw;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( 0, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_Raw;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( 0, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Control_Close;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_FuncWriteClose, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Control_Open;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_FuncWriteOpen, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Control_Conn;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_FuncWriteClose, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Control_Disc;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_FuncWriteOpen, 0 );
   _commandStore.insert( cs );

   cs._cmd     = (CtiProtocolEmetcon::Control_ARMS);     // Just here to make this OK.
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( 0, 0 );
   _commandStore.insert( cs );

   cs._cmd     = (CtiProtocolEmetcon::Control_ARML);
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( 0, 0 );
   _commandStore.insert( cs );

   //  putconfig_tsync is in MCT2XX and MCT310 because the 2XX requires an ARMC
   //    also, the getconfig time location is different for 2XX and 3XX, so that's in each's base as well
   cs._cmd     = CtiProtocolEmetcon::GetConfig_TSync;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_TSyncAddr,
                            (int)MCT_TSyncLen );
   _commandStore.insert( cs );

   return failed;
}


INT CtiDeviceMCT::ExecuteRequest( CtiRequestMsg              *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  RWTPtrSlist< CtiMessage >  &vgList,
                                  RWTPtrSlist< CtiMessage >  &retList,
                                  RWTPtrSlist< OUTMESS >     &outList )
{
    INT        nRet  = NoError;
    CtiRoute  *Route = NULL;
    RWCString  resultString;
    long routeID;
    char Temp[80];

    bool found = false;


    switch( parse.getCommand( ) )
    {
        case LoopbackRequest:
        {
            nRet = executeLoopback( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case ScanRequest:
        {
            nRet = executeScan( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case GetValueRequest:
        {
            nRet = executeGetValue( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case PutValueRequest:
        {
            nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case ControlRequest:
        {
            nRet = executeControl( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case GetStatusRequest:
        {
            nRet = executeGetStatus( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case PutStatusRequest:
        {
            nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << RWTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert( new CtiReturnMsg(getID( ),
                                         RWCString(OutMessage->Request.CommandStr),
                                         resultString,
                                         nRet,
                                         OutMessage->Request.RouteID,
                                         OutMessage->Request.MacroOffset,
                                         OutMessage->Request.Attempt,
                                         OutMessage->Request.TrxID,
                                         OutMessage->Request.UserID,
                                         OutMessage->Request.SOE,
                                         RWOrdered( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            outList.append( OutMessage );
            OutMessage = NULL;
        }
        /*
         ***************************** PASS OFF TO ROUTE BEYOND THIS POINT ****************************************
         */

        for(int i = outList.entries() ; i > 0; i-- )
        {
            OUTMESS *pOut = outList.get();

            if( pReq->RouteId() )
                routeID = pReq->RouteId();
            else
                routeID = getRouteID();

            pOut->RouteID         = routeID;
            pOut->Request.RouteID = routeID;

            if( pReq->getMessagePriority() )
                pOut->Priority = pReq->getMessagePriority();
            else
                pOut->Priority = MAXPRIORITY - 4;

            if( (Route = CtiDeviceBase::getRoute( pOut->RouteID )) != NULL )
            {
                pOut->TargetID                = getID();
                pOut->EventCode = BWORD | RESULT | WAIT;

                if( parse.isKeyValid("noqueue") )
                {
                    pOut->EventCode |= DTRAN;
                    //  pOut->EventCode &= ~QUEUED;
                }

                pOut->Buffer.BSt.Address      = getAddress();            // The DLC address of the MCT.
                pOut->Buffer.BSt.DeviceType   = getType();
                pOut->Buffer.BSt.SSpec        = getSSpec();

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().entries())
                {
                    for(size_t offset = offset; offset < parse.getActionItems().entries(); offset++)
                    {
                        RWCString actn = parse.getActionItems()[offset];
                        RWCString desc = getDescription(parse);

                        vgList.insert(new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                CtiReturnMsg* pRet = new CtiReturnMsg(getID(), RWCString(pOut->Request.CommandStr), Route->getName(), nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.TrxID, pOut->Request.UserID, pOut->Request.SOE, RWOrdered());
                // Start the control request on its route(s)
                if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
                {
                    resultString = "ERROR " + CtiNumStr(nRet) + " performing command on route " + Route->getName().data() + "\n" + FormatError(nRet);
                    pRet->setResultString(resultString);
                    pRet->setStatus( nRet );
                    retList.insert( pRet );
                }
                else
                {
                    delete pRet;
                }
            }
            else
            {
                nRet = NoRouteGroupDevice;

                resultString = "ERROR: Route or Route Transmitter not available for group device " + getName();

                CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                                      RWCString(pOut->Request.CommandStr),
                                                      resultString,
                                                      nRet,
                                                      pOut->Request.RouteID,
                                                      pOut->Request.MacroOffset,
                                                      pOut->Request.Attempt,
                                                      pOut->Request.TrxID,
                                                      pOut->Request.UserID,
                                                      pOut->Request.SOE,
                                                      RWOrdered());

                retList.insert( pRet );
            }

            if( pOut )
            {
                delete pOut;
            }
        }
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
                              RWTPtrSlist< CtiMessage > &vgList,
                              RWTPtrSlist< CtiMessage > &retList,
                              RWTPtrSlist< OUTMESS > &outList,
                              INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        if(getOperation(CtiProtocolEmetcon::Scan_General, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->RouteID   = getRouteID();
            OutMessage->Priority  = ScanPriority;
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_General;     // Helps us figure it out later!
            OutMessage->Retry     = 3;
            OutMessage->Request.MacroOffset = selectInitialMacroRouteOffset();

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strcpy(OutMessage->Request.CommandStr, pReq->CommandString());

            outList.insert(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;

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
                                RWTPtrSlist< CtiMessage > &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist< OUTMESS > &outList,
                                INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Demand/IEDScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(CtiProtocolEmetcon::Scan_Integrity, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            //  should we scan the IED for demand instead?
            if(getType() == TYPEMCT360 || getType() == TYPEMCT370)
                if( ((CtiDeviceMCT31X *)this)->getIEDPort().getRealTimeScanFlag() )
                    getOperation(CtiProtocolEmetcon::GetValue_IEDDemand, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->RouteID   = getRouteID();
            OutMessage->Priority  = ScanPriority;
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_Integrity;     // Helps us figure it out later!;
            OutMessage->Retry     = 3;
            OutMessage->Request.MacroOffset = selectInitialMacroRouteOffset();

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
            outList.insert(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            status = NoMethod;

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS > &outList,
                                  INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** AccumulatorScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(CtiProtocolEmetcon::Scan_Accum, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->RouteID   = getRouteID();
            OutMessage->Priority  = ScanPriority;
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_Accum;
            OutMessage->Retry     = 3;
            OutMessage->Request.MacroOffset = selectInitialMacroRouteOffset();

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strcpy(OutMessage->Request.CommandStr, pReq->CommandString());

            outList.insert(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS > &outList,
                                  INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** LoadProfileScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(getOperation(CtiProtocolEmetcon::Scan_LoadProfile, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->RouteID   = getRouteID();
            OutMessage->Priority  = ScanPriority;
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_LoadProfile;
            OutMessage->Retry     = 3;
            OutMessage->Request.MacroOffset = selectInitialMacroRouteOffset();

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strcpy(OutMessage->Request.CommandStr, pReq->CommandString());

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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;

            status = NoMethod;
        }
    }

    return status;
}


INT CtiDeviceMCT::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch( InMessage->Sequence )
    {
        case CtiProtocolEmetcon::Control_ARMS:
        case CtiProtocolEmetcon::Control_ARML:
        case CtiProtocolEmetcon::Control_Open:
        case CtiProtocolEmetcon::Control_Close:
        {
            break;
        }

        case CtiProtocolEmetcon::GetConfig_Time:
        case CtiProtocolEmetcon::GetConfig_TSync:
        case CtiProtocolEmetcon::GetConfig_Raw:
        case CtiProtocolEmetcon::GetConfig_DemandInterval:
        case CtiProtocolEmetcon::GetConfig_LoadProfileInterval:
        case CtiProtocolEmetcon::GetConfig_Multiplier:
        case CtiProtocolEmetcon::GetConfig_Multiplier2:
        case CtiProtocolEmetcon::GetConfig_Multiplier3:
        {
            status = decodeGetConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        case CtiProtocolEmetcon::GetConfig_Model:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint GetConfig_Model **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }

        case CtiProtocolEmetcon::GetValue_PFCount:
        {
            status = decodeGetValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::Command_Loop:
        {
            status = decodeLoopback(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::PutConfig_Install:
        case CtiProtocolEmetcon::PutConfig_Raw:
        case CtiProtocolEmetcon::PutConfig_TSync:
        case CtiProtocolEmetcon::PutConfig_DemandInterval:
        case CtiProtocolEmetcon::PutConfig_LoadProfileInterval:
        case CtiProtocolEmetcon::PutConfig_IEDClass:
        case CtiProtocolEmetcon::PutConfig_IEDScan:
        {
            status = decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::PutValue_KYZ:
        case CtiProtocolEmetcon::PutValue_KYZ2:
        case CtiProtocolEmetcon::PutValue_KYZ3:
        case CtiProtocolEmetcon::PutValue_ResetPFCount:
        case CtiProtocolEmetcon::PutValue_IEDReset:
        {
            status = decodePutValue(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::PutStatus_Reset:
        {
            status = decodePutStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            status = NoMethod;
            break;
        }
    }

    return status;
}


INT CtiDeviceMCT::ErrorDecode(INMESS *InMessage, RWTime& Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = new CtiReturnMsg(getID(),
                                              RWCString(InMessage->Return.CommandStr),
                                              RWCString(),
                                              InMessage->EventCode & 0x7fff,
                                              InMessage->Return.RouteID,
                                              InMessage->Return.MacroOffset,
                                              InMessage->Return.Attempt,
                                              InMessage->Return.TrxID,
                                              InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    if( pPIL != NULL )
    {
        if( parse.getCommand() == ScanRequest )  //  we only plug values for failed scans
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                case ScanRateStatus:
                    //  implemented as the same scan
                    switch( getType() )
                    {
                        case TYPEMCT310:
                        case TYPEMCT310ID:
                        case TYPEMCT310IL:
                        case TYPEMCT318:
                        case TYPEMCT318L:
                        case TYPEMCT360:
                        case TYPEMCT370:
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 8, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 7, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 6, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 5, StatusPointType );

                        case TYPEMCT250:
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 4, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 3, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 2, StatusPointType );
                            insertPointFail( InMessage, pPIL, ScanRateStatus, 1, StatusPointType );
                        default:
                            ;
                    }

                    resetForScan(ScanRateGeneral);

                    break;

                case ScanRateAccum:
                    switch( getType() )
                    {
                        case TYPEMCT318:
                        case TYPEMCT318L:
                        case TYPEMCT360:
                        case TYPEMCT370:
                            insertPointFail( InMessage, pPIL, ScanRateAccum, 3, PulseAccumulatorPointType );
                            insertPointFail( InMessage, pPIL, ScanRateAccum, 2, PulseAccumulatorPointType );
                        default:
                            insertPointFail( InMessage, pPIL, ScanRateAccum, 1, PulseAccumulatorPointType );
                    }

                    resetForScan(ScanRateAccum);

                    break;

                case ScanRateIntegrity:
                    switch( getType() )
                    {
                        case TYPEMCT318:
                        case TYPEMCT318L:
                        case TYPEMCT360:
                        case TYPEMCT370:
                            insertPointFail( InMessage, pPIL, ScanRateIntegrity, 3, DemandAccumulatorPointType );
                            insertPointFail( InMessage, pPIL, ScanRateIntegrity, 2, DemandAccumulatorPointType );
                        default:
                            insertPointFail( InMessage, pPIL, ScanRateIntegrity, 1, DemandAccumulatorPointType );
                    }

                    resetForScan(ScanRateIntegrity);

                    break;

                default:
                    break;
            }

            if( commPoint = getDevicePointOffsetTypeEqual(2000, StatusPointType) )
            {
                commFailed = new CtiPointDataMsg(commPoint->getPointID(), 0.0, NormalQuality, StatusPointType);

                pPIL->PointData().insert(commFailed);
                commFailed = NULL;
            }
        }

        // send the whole mess to dispatch
        if (pPIL->PointData().entries() > 0)
        {
            retList.insert( pPIL );
        }
        else
        {
            delete pPIL;
        }
        pPIL = NULL;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}


int CtiDeviceMCT::insertPointFail( INMESS *InMessage, CtiReturnMsg *pPIL, int scanType, int pOffset, CtiPointType_t pType )
{
    int failed = FALSE;
    CtiPointBase *pPoint;

    CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    pPoint = getDevicePointOffsetTypeEqual( pOffset, pType);

    if( pMsg != NULL && pPoint != NULL )
    {
        pMsg->insert( -1 );          // This is the dispatch token and is unimplemented at this time
        pMsg->insert( OP_POINTID );  // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert( pPoint->getPointID() );
        pMsg->insert( scanType );
        pMsg->insert( InMessage->EventCode );  // The error number from dsm2.h or yukon.h which was reported.

        pPIL->PointData().insert(pMsg);
    }
    else
    {
        failed = TRUE;
    }

    return failed;
}



INT CtiDeviceMCT::executeLoopback(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  RWTPtrSlist< CtiMessage >      &vgList,
                                  RWTPtrSlist< CtiMessage >      &retList,
                                  RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    INT  function;
    int  i;

    OUTMESS *tmpOut;

    function = CtiProtocolEmetcon::Command_Loop;
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());

        for( i = 0; i < parse.getiValue("count"); i++ )
        {
            tmpOut = new OUTMESS(*OutMessage);

            if( tmpOut != NULL )
                outList.append(tmpOut);
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
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    RWCString tester;

    INT            function;
    unsigned short stub;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
        case ScanRateStatus:
        case ScanRateGeneral:
        {
            function = CtiProtocolEmetcon::Scan_General;
            found = getOperation(CtiProtocolEmetcon::Scan_General, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            break;
        }
        case ScanRateAccum:
        {
            function = CtiProtocolEmetcon::Scan_Accum;
            found = getOperation(CtiProtocolEmetcon::Scan_Accum, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            break;
        }
        case ScanRateIntegrity:
        {
            function = CtiProtocolEmetcon::Scan_Integrity;
            found = getOperation(CtiProtocolEmetcon::Scan_Integrity, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            break;
        }
        case ScanRateLoadProfile:
        {
            function = CtiProtocolEmetcon::Scan_LoadProfile;
            //  don't overwrite the outmess stuff - it's been filled in in scanner
            found = getOperation(CtiProtocolEmetcon::Scan_LoadProfile, stub, stub, stub);
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceMCT::executeGetValue( CtiRequestMsg              *pReq,
                                   CtiCommandParser           &parse,
                                   OUTMESS                   *&OutMessage,
                                   RWTPtrSlist< CtiMessage >  &vgList,
                                   RWTPtrSlist< CtiMessage >  &retList,
                                   RWTPtrSlist< OUTMESS >     &outList )
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    if( parse.getFlags() & CMD_FLAG_FROZEN )  //  Read the frozen values...
    {
        function = CtiProtocolEmetcon::GetValue_Frozen;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token IED in it!
    {
        if( getType() == TYPEMCT360  ||  //  only these types can have an IED attached
            getType() == TYPEMCT370 )
        {
            if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                function = CtiProtocolEmetcon::GetValue_IEDDemand;
                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
            }
            else  //  GV_IEDKwh, GV_IEDKvarh, GV_IEDKvah
            {
                if(      parse.getFlags() & CMD_FLAG_GV_KWH   )  function = CtiProtocolEmetcon::GetValue_IEDKwh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVARH )  function = CtiProtocolEmetcon::GetValue_IEDKvarh;
                else if( parse.getFlags() & CMD_FLAG_GV_KVAH  )  function = CtiProtocolEmetcon::GetValue_IEDKvah;
                else  /*  default request  */                    function = CtiProtocolEmetcon::GetValue_IEDKwh;

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

                if( (parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVARH) &&
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
        function = CtiProtocolEmetcon::GetValue_PFCount;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else                                            // This parse hits the default (non-ied) registers.
    {
        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            function = CtiProtocolEmetcon::GetValue_Demand;
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
        }
        else  //  if( parse.getFlags() & CMD_FLAG_GV_KWH )
        {
            function = CtiProtocolEmetcon::GetValue_Default;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            if( getType() == TYPEMCT318 || getType() == TYPEMCT318L || getType() == TYPEMCT360 || getType() == TYPEMCT370 )
            {
                //  if pulse input 3 isn't defined
                if( !getDevicePointOffsetTypeEqual(3, PulseAccumulatorPointType ) )
                {
                    OutMessage->Buffer.BSt.Length -= 3;

                    //  if pulse input 2 isn't defined
                    if( !getDevicePointOffsetTypeEqual(2, PulseAccumulatorPointType ) )
                    {
                        OutMessage->Buffer.BSt.Length -= 3;
                    }
                }
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }


    return nRet;
}

INT CtiDeviceMCT::executePutValue(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  RWTPtrSlist< CtiMessage >      &vgList,
                                  RWTPtrSlist< CtiMessage >      &retList,
                                  RWTPtrSlist< OUTMESS >         &outList)
{
    INT    nRet = NoError,
           i;
    long   rawPulses;
    double dial;

    INT function;

    bool found = false;

    if(parse.getFlags() & CMD_FLAG_PV_DIAL)
    {
        switch(parse.getiValue("offset"))
        {
            default:  //  we should always have offset set to 1-3, but just in case...
            case 1:
                function = CtiProtocolEmetcon::PutValue_KYZ;
                break;
            case 2:
                function = CtiProtocolEmetcon::PutValue_KYZ2;
                break;
            case 3:
                function = CtiProtocolEmetcon::PutValue_KYZ3;
                break;
        }

        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            dial = 0;
        }
        else
        {
            dial = parse.getdValue("dial");
        }

        CtiPointBase *tmpPoint = getDevicePointOffsetTypeEqual(parse.getiValue("offset"), PulseAccumulatorPointType);

        if( tmpPoint && tmpPoint->isA() == PulseAccumulatorPointType)
        {
            rawPulses = (int)(dial / (((CtiPointAccumulator *)tmpPoint)->getMultiplier()));
        }
        else
        {
            rawPulses = (int)dial;
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        //  copy the reading into the output buffer, MSB style
        for(i = 0; i < 3; i++)
        {
            OutMessage->Buffer.BSt.Message[i]   = (rawPulses >> ((2-i)*8)) & 0xFF;
            OutMessage->Buffer.BSt.Message[i+3] = (rawPulses >> ((2-i)*8)) & 0xFF;
            OutMessage->Buffer.BSt.Message[i+6] = (rawPulses >> ((2-i)*8)) & 0xFF;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_PV_PWR)
    {
        //  currently only know how to reset powerfail
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            function = CtiProtocolEmetcon::PutValue_ResetPFCount;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            //  set the outgoing bytes to 0
            for(int i = 0; i < OutMessage->Buffer.BSt.Length; i++)
                OutMessage->Buffer.BSt.Message[i] = 0;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_PV_IED)     // This parse has the token IED in it!
    {
        //  currently only know how to reset IEDs
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            function = CtiProtocolEmetcon::PutValue_IEDReset;

            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            if( getType() == TYPEMCT360 ||
                getType() == TYPEMCT370 )
            {
                switch( ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType() )
                {
                    case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_AlphaResetAddr;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_AlphaResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 60;  //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[1] = 1;   //  Demand Reset  function code for the Alpha
                        break;

                    case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_LGS4ResetAddr;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_LGS4ResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 3;     //  MCT's LG command identifier
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x2B;  //  Demand Reset function code for the LG S4
                        break;

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        break;
                }
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }


    return nRet;
}

INT CtiDeviceMCT::executeGetStatus(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&OutMessage,
                                  RWTPtrSlist< CtiMessage >      &vgList,
                                  RWTPtrSlist< CtiMessage >      &retList,
                                  RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    if(parse.getFlags() & CMD_FLAG_GS_DISCONNECT)          // Read the disconnect status
    {
        function = CtiProtocolEmetcon::GetStatus_Disconnect;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_INTERNAL)
    {
        function = CtiProtocolEmetcon::GetStatus_Internal;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_LOADSURVEY)
    {
        function = CtiProtocolEmetcon::GetStatus_LoadProfile;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_GS_IED)
    {
        if(parse.getFlags() & CMD_FLAG_GS_LINK)
        {
            function = CtiProtocolEmetcon::GetStatus_IEDLink;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else //  if(parse.getFlags() & CMD_FLAG_GS_EXTERNAL) - default command
    {
        function = CtiProtocolEmetcon::GetStatus_External;
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceMCT::executePutStatus(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;
    int   intervallength;
    RWCString temp;
    RWTime NowTime;
    RWDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...
    OUTMESS *tmpOutMess;

    INT function;

    if(parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = CtiProtocolEmetcon::PutStatus_Reset;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Message[0] = 0;
        OutMessage->Buffer.BSt.Message[1] = 0;
        OutMessage->Buffer.BSt.Message[2] = 0;
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
       OutMessage->RouteID   = getRouteID();
//       OutMessage->Priority  = MAXPRIORITY - 4;
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 3;


       if( OutMessage->Buffer.BSt.Function == 0x06 )  //  easiest way to tell it's an MCT3xx
       {
           tmpOutMess = new OUTMESS(*OutMessage);

           if( tmpOutMess != NULL )
           {
               //  reset power fail
               tmpOutMess->Buffer.BSt.Function = 0x50;
               tmpOutMess->Buffer.BSt.Length   =    0;

               outList.append(tmpOutMess);
           }
           else
           {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unable to allocate OUTMESS for MCT3XX powerfail reset" << endl;
           }

           tmpOutMess = new OUTMESS(*OutMessage);

           if( tmpOutMess != NULL )
           {
               //  reset encoder error
               tmpOutMess->Buffer.BSt.Function = 0x58;
               tmpOutMess->Buffer.BSt.Length   =    0;

               outList.append(tmpOutMess);
           }
           else
           {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unable to allocate OUTMESS for MCT3XX encoder error reset" << endl;
           }
       }

       strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceMCT::executeGetConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    RWCString temp;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    if(parse.isKeyValid("model"))
    {
        function = CtiProtocolEmetcon::GetConfig_Model;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("ied"))
    {
        if(parse.isKeyValid("time"))
        {
            function = CtiProtocolEmetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else if( parse.isKeyValid("scan"))
        {
            function = CtiProtocolEmetcon::GetConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else if(parse.isKeyValid("options"))
    {
        function = CtiProtocolEmetcon::GetConfig_Options;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("time"))
    {
        if(parse.isKeyValid("sync"))
        {
            function = CtiProtocolEmetcon::GetConfig_TSync;
        }
        else
        {
            function = CtiProtocolEmetcon::GetConfig_Time;
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("multiplier"))
    {
        function = CtiProtocolEmetcon::GetConfig_Multiplier;

        if( parse.isKeyValid("multchannel") )
        {
            switch( parse.getiValue("multchannel") )
            {
                case 1:
                    function = CtiProtocolEmetcon::GetConfig_Multiplier;
                    break;
                case 2:
                    function = CtiProtocolEmetcon::GetConfig_Multiplier2;
                    break;
                case 3:
                    function = CtiProtocolEmetcon::GetConfig_Multiplier3;
                    break;
            }
        }

        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.isKeyValid("interval"))
    {
        temp = parse.getsValue("interval");

        if( temp == "lp" )
        {
            function = CtiProtocolEmetcon::GetConfig_LoadProfileInterval;
        }
        else if( temp == "li" )
        {
            function = CtiProtocolEmetcon::GetConfig_DemandInterval;
        }
        else
        {
            function = CtiProtocolEmetcon::DLCCmd_Invalid;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( function != CtiProtocolEmetcon::DLCCmd_Invalid )
        {
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    //  get raw memory locations
    else if(parse.isKeyValid("rawloc"))
    {
        int rawloc, rawlen;

        rawloc = parse.getiValue("rawloc");

        if( parse.isKeyValid("rawlen") )
        {
            //  if a length was specified
            rawlen = parse.getiValue("rawlen");
        }
        else
        {
            //  no read length specified...  default to 13 bytes for a non-function read
            rawlen = 13;
        }

        //  13 is max data return from an MCT
        if( rawlen > 13 )
            rawlen = 13;

        function = CtiProtocolEmetcon::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( parse.isKeyValid("rawfunc") )
            OutMessage->Buffer.BSt.IO |= IO_FCT_MASK;

        OutMessage->Buffer.BSt.Function = rawloc;
        OutMessage->Buffer.BSt.Length = rawlen;
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceMCT::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   function;
    INT   nRet = NoError;
    int   intervallength;
    RWCString temp;
    RWTime NowTime;
    RWDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = new CtiReturnMsg(getID( ),
                                            RWCString(OutMessage->Request.CommandStr),
                                            RWCString(),
                                            nRet,
                                            OutMessage->Request.RouteID,
                                            OutMessage->Request.MacroOffset,
                                            OutMessage->Request.Attempt,
                                            OutMessage->Request.TrxID,
                                            OutMessage->Request.UserID,
                                            OutMessage->Request.SOE,
                                            RWOrdered( ));


    if( parse.isKeyValid("install") )
    {
        //  does a read of 2 bytes or so
        function = CtiProtocolEmetcon::PutConfig_Install;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("ied") )
    {
        if( parse.isKeyValid("scan") )
        {
            int scantime, scandelay;

            function = CtiProtocolEmetcon::PutConfig_IEDScan;
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

            function = CtiProtocolEmetcon::PutConfig_IEDClass;
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

            if( classnum > 255 )
                classnum = 0;
            if( classoffset > 65535 ) //  fix?
                classoffset = 0;

            if( ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType() == CtiTableDeviceMCTIEDPort::AlphaPowerPlus )
            {
                if( classnum == 0 )
                    classnum = 72;  //  default to class 72 for an Alpha

                if( classnum == 72 && classoffset == 0 )  //  do not allow 72 to have a 0 offset
                    classoffset = 2;
            }

            OutMessage->Buffer.BSt.Message[0] = 0;  //  128 len in MCT
            OutMessage->Buffer.BSt.Message[1] = (classoffset >> 8) & 0xff;
            OutMessage->Buffer.BSt.Message[2] =  classoffset       & 0xff;
            OutMessage->Buffer.BSt.Message[3] = classnum;
        }
    }
    else if(parse.isKeyValid("interval"))
    {
        temp = parse.getsValue("interval");
        if( temp == "lp" )
        {
            function = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
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
                        retList.insert( errRet );
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
                if( getType() == TYPEMCT212 ||
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
                                retList.insert( errRet );
                                errRet = NULL;
                            }
                    }
                }
                else
                {
                    intervallength *= 4;  //  all else are in multiples of 15 seconds
                }

                if( intervallength )
                {
                    function = CtiProtocolEmetcon::PutConfig_DemandInterval;
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
                    retList.insert( errRet );
                    errRet = NULL;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else if(parse.isKeyValid("timesync"))
    {
        function = CtiProtocolEmetcon::PutConfig_TSync;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        unsigned char ticper12hr, ticper5min, ticper15sec;

        //  compute how many of each tic type have passed
        ticper12hr   = (NowDate.weekDay() % 7) * 2;  //  2 tics every day  (mod 7 because RW says Sunday is 7, we want it 0)
        ticper12hr  +=  NowTime.hour() / 12;         //  1 tic every 12 hours

        ticper5min   = (NowTime.hour() % 12) * 12;   //  12 tics per hour
        ticper5min  +=  NowTime.minute() / 5;        //  1 tic every 5 minutes

        ticper15sec  = (NowTime.minute() % 5) * 4;   //  4 tics per minute
        ticper15sec +=  NowTime.second() / 15;       //  1 tic every 15 seconds

        //  invert the counters to be tics REMAINING, not PASSED
        OutMessage->Buffer.BSt.Message[0] =  20 - ticper15sec;
        OutMessage->Buffer.BSt.Message[1] = 144 - ticper5min;
        OutMessage->Buffer.BSt.Message[2] =  14 - ticper12hr;
        OutMessage->Buffer.BSt.Message[3] = 94;  //  DLCFreq1
        OutMessage->Buffer.BSt.Message[4] = 37;  //  DLCFreq2
    }
    else if(parse.isKeyValid("multiplier"))
    {
        unsigned long multbytes;


        multbytes  = (unsigned long)(parse.getdValue("multiplier") * 100.0);
        multbytes -= multbytes % 10;

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
                retList.insert( errRet );
                errRet = NULL;
            }
        }

        OutMessage->Buffer.BSt.Message[0] = (multbytes >> 8) & 0xFF;  //  bits 15-8
        OutMessage->Buffer.BSt.Message[1] =  multbytes       & 0xFF;  //  bits  7-0

        switch( parse.getiValue("multoffset") )
        {
            default:
            case 1:
                function = CtiProtocolEmetcon::PutConfig_Multiplier;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                break;

            case 2:
                function = CtiProtocolEmetcon::PutConfig_Multiplier2;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                break;

            case 3:
                function = CtiProtocolEmetcon::PutConfig_Multiplier3;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                break;
        }
    }
    else if(parse.isKeyValid("rawloc"))
    {
        function = CtiProtocolEmetcon::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        temp = parse.getsValue("rawdata");

        //  trim string to be 15 bytes long
        if( temp.length() > 15 )
            temp.remove( 15 );

        OutMessage->Buffer.BSt.Length = temp.length();
        for( int i = 0; i < temp.length(); i++ )
        {
            OutMessage->Buffer.BSt.Message[i] = temp(i);
        }

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO |= IO_FCT_MASK;
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
       OutMessage->RouteID   = getRouteID();
//       OutMessage->Priority  = MAXPRIORITY - 4;
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 3;

       strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceMCT::executeControl(CtiRequestMsg                  *pReq,
                                 CtiCommandParser               &parse,
                                 OUTMESS                        *&OutMessage,
                                 RWTPtrSlist< CtiMessage >      &vgList,
                                 RWTPtrSlist< CtiMessage >      &retList,
                                 RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];
    OUTMESS *pDCST = NULL;

    INT function;

    if(parse.getFlags() & CMD_FLAG_CTL_OPEN)
    {
        function = CtiProtocolEmetcon::Control_Open;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_CLOSE)
    {
        function = CtiProtocolEmetcon::Control_Close;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_CONNECT)
    {
        function = CtiProtocolEmetcon::Control_Conn;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & CMD_FLAG_CTL_DISCONNECT)
    {
        function = CtiProtocolEmetcon::Control_Disc;
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
        OutMessage->RouteID   = getRouteID();
//        OutMessage->Priority  = MAXPRIORITY - 4;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());

        outList.append( OutMessage );
        OutMessage = NULL;
    }


    return nRet;
}


INT CtiDeviceMCT::decodeLoopback(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    RWCString resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / Loopback successful";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodeGetValue(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;
    CtiPointBase         *pPoint;

    double Value;
    RWCString resultStr;


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        switch( InMessage->Sequence )
        {
            case CtiProtocolEmetcon::GetValue_PFCount:
            {
                int pfCount, i;

                pfCount = 0;

                for(i = 0; i < 2; i++)
                {
                    pfCount = (pfCount << 8) + InMessage->Buffer.DSt.Message[i];
                }

                resultStr = getName() + " / powerfail count = " + CtiNumStr(pfCount);

                if( (pPoint = getDevicePointOffsetTypeEqual( 20, PulseAccumulatorPointType )) != NULL )
                {
                    Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pfCount);

                    pData = new CtiPointDataMsg(pPoint->getPointID(), (double)Value, NormalQuality, PulseAccumulatorPointType);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }

                ReturnMsg->setResultString( resultStr );
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodeGetConfig(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;
    CtiCommandParser parse(InMessage->Return.CommandStr);

    int min, sec, channel;
    unsigned long multnum;  //  multiplier numerator - the value returned is multiplier * 1000
    double mult;  //  where the final multiplier value will be computed

    RWCString resultStr;


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        switch( InMessage->Sequence )
        {
            case CtiProtocolEmetcon::GetConfig_DemandInterval:
            {
                //  see MCT22X ResultDecode for an additional MCT22X step

                sec = DSt->Message[0] * 15;

                min = sec / 60;
                sec = sec % 60;

                resultStr = getName() + " / Demand Interval: " + CtiNumStr( min ) + " min";
                if( sec )
                    resultStr += ", " + CtiNumStr( sec ) + " sec";

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case CtiProtocolEmetcon::GetConfig_LoadProfileInterval:
            {
                min = DSt->Message[0] * 5;

                resultStr = getName() + " / Load Profile Interval: " + CtiNumStr( min ) + " min";

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case CtiProtocolEmetcon::GetConfig_Multiplier:
            case CtiProtocolEmetcon::GetConfig_Multiplier2:
            case CtiProtocolEmetcon::GetConfig_Multiplier3:
            {
                resultStr  = getName() + " / ";

                if( parse.isKeyValid("multchannel") )
                {
                    channel = parse.getiValue("multchannel");
                    resultStr += "channel " + CtiNumStr( channel );
                }


                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "message[0] = " << (int)DSt->Message[0] << endl;
                    dout << "message[1] = " << (int)DSt->Message[1] << endl;
                }

                multnum   = (int)DSt->Message[0];
                multnum <<= 8;
                multnum  |= (int)DSt->Message[1];

                if( multnum == 1000 )
                {
                    resultStr += " multiplier: pulses\n";
                }
                else
                {
                    mult = (double)multnum;
                    mult /= 100.0;
                    resultStr += " multiplier: " + CtiNumStr::CtiNumStr(mult,3) + "\n";
                }

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case CtiProtocolEmetcon::GetConfig_Time:
            case CtiProtocolEmetcon::GetConfig_TSync:
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

                if( InMessage->Sequence == CtiProtocolEmetcon::GetConfig_Time )
                {
                    resultStr = getName() + " / time:  ";
                }
                else if( InMessage->Sequence == CtiProtocolEmetcon::GetConfig_TSync )
                {
                    resultStr = getName() + " / time sync:  ";
                }

                resultStr += RWCString(days[day]) + " " + CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2);

                ReturnMsg->setResultString( resultStr );

                break;
            }

            case CtiProtocolEmetcon::GetConfig_Raw:
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

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodePutValue(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    RWCString resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / command complete";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodePutStatus(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    RWCString resultString;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    INT ErrReturn  = InMessage->EventCode & 0x3fff;


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString = getName( ) + " / command complete";
        ReturnMsg->setResultString( resultString );

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT::decodePutConfig(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    RWCString resultString;
    OUTMESS *OutTemplate;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiRequestMsg *pReq;

    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        switch( InMessage->Sequence )
        {
            case CtiProtocolEmetcon::PutConfig_Install:
            {
                int sspec = DSt->Message[0] + (DSt->Message[4] << 8);

                if( sspecIsValid( sspec ) )
                {
                    if( getType( ) == TYPEMCT310   ||
                        getType( ) == TYPEMCT310ID ||
                        getType( ) == TYPEMCT310IL )
                    {
                        if( !(DSt->Message[2] & 0x01) )
                        {
                            resultString = getName() + " / Error:  Metering channel 1 not enabled" + "\n";
                        }
                    }

                    OutTemplate = new(OUTMESS);

                    InEchoToOut( InMessage, OutTemplate );

                    //  reset the meter
                    pReq = new CtiRequestMsg(InMessage->TargetID, "putstatus reset", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);
                    pReq->setConnectionHandle( InMessage->Return.Connection );

                    CtiDeviceBase::ExecuteRequest(pReq, vgList, retList, outList, OutTemplate);
                    delete pReq;

                    //  put the load profile interval if it's a lp device
                    if( getType( ) == TYPEMCT310IL || getType( ) == TYPEMCT318L )
                    {
                        pReq = new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon interval lp", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);
                        pReq->setConnectionHandle( InMessage->Return.Connection );

                        CtiDeviceBase::ExecuteRequest(pReq, vgList, retList, outList, OutTemplate);
                        delete pReq;
                    }

                    //  put the demand interval
                    pReq = new CtiRequestMsg(InMessage->TargetID, "putconfig emetcon interval li", InMessage->Return.UserID, InMessage->Return.TrxID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt);
                    pReq->setConnectionHandle( InMessage->Return.Connection );

                    CtiDeviceBase::ExecuteRequest(pReq, vgList, retList, outList, OutTemplate);
                    delete pReq;

                    if( OutTemplate != NULL )
                    {
                        delete OutTemplate;
                    }

                    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

                    resultString += getName( ) + " / sspec verified as \'" + sspecIsFrom( sspec ) + "\'.";
                    ReturnMsg->setResultString( resultString );
                }
                else
                {
                    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

                    resultString = getName( ) + " / sspec \'" + CtiNumStr(sspec) + "\' not valid - looks like an \'" + sspecIsFrom( sspec ) + "\'." + "\n" +
                                   getName( ) + " / install command aborted";
                    ReturnMsg->setResultString( resultString );
                }

                break;
            }

            default:
            {
                ReturnMsg->setUserMessageId(InMessage->Return.UserID);

                resultString = getName( ) + " / command complete";
                ReturnMsg->setResultString( resultString );
                break;
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
    }

    return status;
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
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    return((DOUBLE) INVALID);

         case TYPEMCT310:        // In basic,.. SELECT 310 TO 370
         case TYPEMCT310ID:
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
                  dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

   CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )    // It's prego!
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;           // Copy over the found funcLen pair!
      length = cs._funcLen.second;           // Copy over the found funcLen pair!
      io = cs._io;
      found = true;
   }

   return found;
}


INT CtiDeviceMCT::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoError;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default load profile logic handler - request deleted" << endl;
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


bool CtiDeviceMCT::processAdditionalRoutes( INMESS *InMessage ) const
{
    bool bret = false;

    if(InMessage->Return.MacroOffset != 0)
    {
        CtiRoute *Route = 0;

        if( (Route = CtiDeviceBase::getRoute( InMessage->Return.RouteID )) != NULL )    // This is "this's" route
        {
            bret = Route->processAdditionalRoutes(InMessage);
        }
    }
    return bret;
}

ULONG CtiDeviceMCT::selectInitialMacroRouteOffset() const
{
    return 1L;
}

