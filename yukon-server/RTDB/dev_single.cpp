#include "precompiled.h"

#include <vector>

#include "cparms.h"
#include "dev_single.h"
#include "logger.h"
#include "porter.h"
#include "numstr.h"
#include "tbl_ptdispatch.h"
#include "utility.h"
#include "ctidate.h"
#include "ctitime.h"
#include "scanner_debug.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "dllyukon.h"
#include "exceptions.h"
#include "prot_base.h"
#include "desolvers.h"
#include "config_exceptions.h"
#include "MetricIdLookup.h"

using namespace std;


using namespace Cti;  //  in preparation for moving devices to their own namespace


CtiTime CtiDeviceSingle::adjustNextScanTime( const INT scanType )
{
    CtiTime    Now;
    CtiTime    When( YUKONEOT );
    int       loop_count = 0;
    const int MaxLoopCount = 100;

    if( getScanRate( scanType ) > 0 )
    {
        if( !( getNextScan( scanType ).seconds() % getScanRate( scanType ) ) )
        {   // Aligned to the correct boundary.
            do
            {
                setNextScan( scanType, getNextScan( scanType ) + getScanRate( scanType ) );
            }
            while(getNextScan(scanType) <= Now && ++loop_count < MaxLoopCount);

            if( loop_count >= MaxLoopCount )
            {
                setNextScan( scanType, firstScan( Now + 120, scanType ) );

                CTILOG_WARN( dout, "infinite loop averted in adjustNextScanTime" );
            }
        }
        else
        {
            setNextScan( scanType, firstScan( Now, scanType ) );
        }

        When = getNextScan( scanType );
    }
    else if( getScanRate( scanType ) == 0 )
    {
        When = When.now();
    }

    return When;
}


CtiTime CtiDeviceSingle::firstScan( const CtiTime &When, INT rate )
{
    CtiTime first;

    if( getScanRate( rate ) > 3600 )
    {
        CtiTime hourstart = CtiTime( When.seconds() - ( When.seconds() % 3600 ) ); // align to the hour.
        first = CtiTime( hourstart.seconds() - ( ( hourstart.hour() * 3600 ) % getScanRate( rate ) ) + getScanRate( rate ) );
    }
    else if( getScanRate( rate ) > 0 )    // Prevent a divide by zero with this check...
    {
        first = CtiTime( When.seconds() - ( When.seconds() % getScanRate( rate ) ) + getScanRate( rate ) );
    }
    else if( getScanRate( rate ) == 0 )
    {
        first = When.now();
    }
    else
    {
        first = CtiTime( YUKONEOT );
    }

    while( first <= When )
    {
        first += getScanRate( rate );
    }

    return first;
}

void CtiDeviceSingle::validateScanTimes( bool force )
{
    CtiTime Now;

    CtiLockGuard<CtiMutex> guard( _classMutex );

    for( int rate = 0; rate < ScanRateInvalid; rate++ )
    {
        /*
         *  Make sure we have not gone tardy.. nextScheduledScan is used to make sure we are within 1 (or 2)
         *  scan intervals of the expected next time...
         */

        LONG scanrate = getScanRate( rate );

        if( scanrate >= 0 && isWindowOpen() )
        {
            bool scanChanged = hasRateOrClockChanged( rate, Now );

            if( force == true || scanChanged )
            {
                setNextScan( rate, firstScan( Now, rate ) );

                if( rate == ScanRateAccum && scanChanged )
                {
                    setLastFreezeNumber( 0L );
                    setPrevFreezeNumber( 0L );
                    setLastFreezeTime( CtiTime() );
                    setPrevFreezeTime( CtiTime() );
                }
            }
            else if( isScanFlagSet( rate ) || isScanFlagSet( ScanFreezePending ) )
            {
                // if we're pending, do the calculation anyway
                setNextScan( rate, firstScan( Now, rate ) );
            }
        }
    }
}

YukonError_t CtiDeviceSingle::initiateAccumulatorScan( OutMessageList &outList, INT ScanPriority )
{
    YukonError_t nRet = ClientErrors::None;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    CtiMessageList vgList;
    CtiMessageList retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg( getID(),
        "scan accumulator",
        0,                        // This is a number used to track the message on the outbound request.
        0,                        // Client can track this request with this number
        getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
        MacroOffset::none,        // 
        1 );                      // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if( OutMessage != NULL )
    {
        if( isInhibited() )
        {
            adjustNextScanTime( ScanRateAccum );
            nRet = ClientErrors::DeviceInhibited;
        }
        else if( !isWindowOpen() )
        {
            adjustNextScanTime( ScanRateAccum );
            nRet = ClientErrors::ScanWindowClosed;
        }
        else if( clearedForScan( ScanRateAccum ) )
        {
            resetScanFlag( ScanForced );            // Reset this guy since we're doing it

            if( isDeviceAddressGlobal() )
            {
                // CAN NOT scan a global address.
                setScanRate( ScanRateAccum, YUKONEOT );    // set him to the end of time!
                getNextScan( ScanRateAccum ) = CtiTime( YUKONEOT );
                return ClientErrors::ScanGlobalAddress; // Cannot scan a global address.
            }

            strncpy( OutMessage->Request.CommandStr, "scan accumulator", sizeof( OutMessage->Request.CommandStr ) );
            OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

            // Do the device's AccumulatorScan!
            nRet = AccumulatorScan( pReq, parse, OutMessage, vgList, retList, outList, ScanPriority );
            OutMessage = NULL;      // Memory may be forgotten

            if( nRet && nRet != ClientErrors::AccumulatorsNotSupported )
            {
                setScanFlag( ScanFreezeFailed );
            }
        }
        else
        {
            if( SCANNER_DEBUG_ACCUMSCAN & gConfigParms.getValueAsULong( "SCANNER_DEBUGLEVEL", 0, 16 ) )
            {
                CTILOG_DEBUG( dout, "Accumulator Scan aborted due to scan in progress, device \"" << getName() << "\"" );
            }

            if( getScanRate( ScanRateAccum ) < 60 )
            {
                setNextScan( ScanRateAccum, firstScan( Now, ScanRateAccum ) );
            }
            else            /* Check to be sure we have not gone tardy... */
                if( Now.seconds() - getNextScan( ScanRateAccum ).seconds() > 300 )
                {
                    resetForScan( ScanRateAccum );
                }
        }
    }
    else
    {
        nRet = ClientErrors::MemoryAccess;
    }

    /*
     *  Calculate the next time we see this guy...
     *
     *  Since an accumulator scan is defined in our system to bring back a full dump
     *  of the device, we us the two scan times to do our work.
     */

    if( nRet != ClientErrors::AccumulatorsNotSupported )
    {
        if( !isScanFlagSet( ScanForced ) )
        {
            if( getNextScan( ScanRateGeneral ) <= getNextScan( ScanRateAccum ) &&
                !( getScanRate( ScanRateAccum ) % getScanRate( ScanRateGeneral ) ) )
            {
                adjustNextScanTime( ScanRateGeneral );
            }

            adjustNextScanTime( ScanRateAccum );
        }
    }
    else
    {
        setNextScan( ScanRateAccum, CtiTime( YUKONEOT ) );
    }

    if( OutMessage != NULL )
    {
        delete OutMessage;
    }

    if( pReq != NULL )
    {
        delete pReq;
    }

    return nRet;
}


YukonError_t CtiDeviceSingle::initiateIntegrityScan( OutMessageList &outList, INT ScanPriority )
{
    YukonError_t nRet = ClientErrors::None;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    CtiMessageList vgList;
    CtiMessageList retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg( getID(),
        "scan integrity",
        0,                        // This is a number used to track the message on the outbound request.
        0,                        // Client can track this request with this number
        getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
        MacroOffset::none,        // 
        1 );                      // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if( OutMessage != NULL )
    {
        strncpy( OutMessage->Request.CommandStr, "scan integrity", sizeof( OutMessage->Request.CommandStr ) );
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if( isInhibited() )
        {
            CTILOG_WARN( dout, "Integrity Scan aborted due to inhibited device: " << getName() );

            nRet = ClientErrors::ScanDeviceInhibited;
        }
        else if( !isWindowOpen() )
        {
            nRet = ClientErrors::ScanWindowClosed;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan( ScanRateIntegrity ) )
            {
                resetScanFlag( ScanForced );            // Reset this guy since we're doing it

                if( isDeviceAddressGlobal() )
                {
                    // CAN NOT scan a global address.
                    setScanRate( ScanRateIntegrity, YUKONEOT );    // set him to the end of time!
                    setNextScan( ScanRateIntegrity, CtiTime( YUKONEOT ) );

                    return ClientErrors::ScanGlobalAddress; // Cannot scan a global address.
                }

                // Do the devices integrity scan!
                nRet = IntegrityScan( pReq, parse, OutMessage, vgList, retList, outList, ScanPriority );
                OutMessage = NULL;      // Memory may be forgotten

                if( nRet )
                {
                    CTILOG_ERROR( dout, "Integrity Scan error " << nRet );
                }
                else
                {
                    setScanFlag( ScanRateIntegrity );
                }

            }
            else
            {
                if( SCANNER_DEBUG_INTEGRITYSCAN & gConfigParms.getValueAsULong( "SCANNER_DEBUGLEVEL", 0, 16 ) )
                {
                    CTILOG_DEBUG( dout, "Integrity Scan aborted due to scan in progress, device \"" << getName() << "\"" );
                }

                if( getScanRate( ScanRateIntegrity ) < 60 )
                {
                    setNextScan( ScanRateIntegrity, firstScan( Now, ScanRateIntegrity ) );
                }
                else if( Now.seconds() - getNextScan( ScanRateIntegrity ).seconds() > 300 )
                {
                    resetForScan( ScanRateIntegrity );
                }
            }
        }
    }
    else
    {
        nRet = ClientErrors::MemoryAccess;
    }

    adjustNextScanTime( ScanRateIntegrity );


    if( OutMessage != NULL )
    {
        delete OutMessage;
    }

    if( pReq != NULL )
    {
        delete pReq;
    }

    return nRet;
}


YukonError_t CtiDeviceSingle::initiateGeneralScan( OutMessageList &outList, INT ScanPriority )
{
    YukonError_t nRet = ClientErrors::None;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;

    CtiMessageList vgList;
    CtiMessageList retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg( getID(),
        "scan general",
        0,                        // This is a number used to track the message on the outbound request.
        0,                        // Client can track this request with this number
        getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
        MacroOffset::none,        // 
        1 );                      // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );


    if( OutMessage != NULL )
    {
        strncpy( OutMessage->Request.CommandStr, "scan general", sizeof( OutMessage->Request.CommandStr ) );
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if( getNextScan( ScanRateGeneral ).seconds() == 0 )
        {
            setNextScan( ScanRateGeneral, firstScan( Now, ScanRateGeneral ) );
        }
        else
        {
            if( isInhibited() )
            {
                /*
                 *  this guy is out of service, so send the last values to VanGogh as a plug?
                 */

                /*
                 *  As a wild hair perhaps the plugging could best be done at VanGogh via a single
                 *  message from Scanner here.. The alternative is a multi-point update
                 *  being blasted out from here to be dispersed by VanGogh as he sees fit.
                 *
                 *  FIX FIX FIX 082899 CGP
                 */

                adjustNextScanTime( ScanRateGeneral );
                nRet = ClientErrors::ScanDeviceInhibited;
            }
            else if( !isWindowOpen() )
            {
                adjustNextScanTime( ScanRateGeneral );
                nRet = ClientErrors::ScanWindowClosed;
            }
            else // if(isInhibited()) ... so it isn't inhibited
            {
                if( clearedForScan( ScanRateGeneral ) )
                {
                    resetScanFlag( ScanForced );            // Reset this guy since we're doing it

                    if( isDeviceAddressGlobal() )
                    {
                        // CANNOT scan a global address.
                        setScanRate( ScanRateGeneral, YUKONEOT );    // set him to the end of time!
                        setNextScan( ScanRateGeneral, CtiTime( YUKONEOT ) );

                        return ClientErrors::ScanGlobalAddress; // Cannot scan a global address.
                    }

                    // Do the devices general scan!
                    nRet = GeneralScan( pReq, parse, OutMessage, vgList, retList, outList, ScanPriority );

                    if( !vgList.empty() || !retList.empty() )
                    {
                        delete_container( vgList );
                        delete_container( retList );
                        vgList.clear();
                        retList.clear();
                    }

                    OutMessage = NULL;      // Memory may be forgotten

                    if( !nRet )
                    {
                        setScanFlag( ScanRateGeneral );
                    }
                    else     // Error occurred
                    {
                        CTILOG_ERROR( dout, "Error " << CtiError::GetErrorString( nRet ) << " sending general scan to " << getName() );

                        // Report the comm error and plug any points!
                        // FIX FIX FIX CGP 082999
                        setNextScan( ScanRateGeneral, firstScan( Now, ScanRateGeneral ) );
                    }
                }
                else
                {
                    if( SCANNER_DEBUG_GENERALSCAN & gConfigParms.getValueAsULong( "SCANNER_DEBUGLEVEL", 0, 16 ) )
                    {
                        CTILOG_DEBUG( dout, "General Scan aborted due to scan in progress, device \"" << getName() << "\"" );
                    }

                    if( getScanRate( ScanRateGeneral ) < 60 )
                    {
                        setNextScan( ScanRateGeneral, firstScan( Now, ScanRateGeneral ) );
                    }
                    else if( Now.seconds() - getNextScan( ScanRateGeneral ).seconds() > 300 ) /* Check to be sure we have not gone tardy... */
                    {
                        resetForScan( ScanRateGeneral );
                    }
                }
            }
        }
    }
    else
    {
        nRet = ClientErrors::MemoryAccess;
    }

    /*
     *  Calculate the next time we see this guy...
     *  But only if we are about to do something now (as indicated by OutMessages in the list!).
     */
    if( !isScanFlagSet( ScanForced ) && outList.size() > 0 )
    {
        adjustNextScanTime( ScanRateGeneral );
    }

    if( OutMessage != NULL )
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    if( pReq != NULL )
    {
        delete pReq;
    }

    return nRet;
}


YukonError_t CtiDeviceSingle::initiateLoadProfileScan( OutMessageList &outList, INT ScanPriority )
{
    YukonError_t nRet = ClientErrors::None;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each load profile device prior to the
     *  actual device specific code called by that device type.
     */

    CtiMessageList vgList;
    CtiMessageList retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg( getID(),
        "scan loadprofile",
        0,                        // This is a number used to track the message on the outbound request.
        0,                        // Client can track this request with this number
        getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
        MacroOffset::none,        // 
        1 );                      // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if( OutMessage != NULL )
    {
        strncpy( OutMessage->Request.CommandStr, "scan loadprofile", sizeof( OutMessage->Request.CommandStr ) );
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if( isInhibited() )
        {
            CTILOG_WARN( dout, "Load Profile Scan aborted due to inhibited device: " << getName() );

            nRet = ClientErrors::ScanDeviceInhibited;
        }
        else if( !isWindowOpen() )
        {
            nRet = ClientErrors::ScanWindowClosed;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan( ScanRateLoadProfile ) )
            {
                resetScanFlag( ScanForced );            // Reset this guy since we're doing it

                if( isDeviceAddressGlobal() )
                {
                    // CAN NOT scan a global address.
                    setScanRate( ScanRateLoadProfile, YUKONEOT );    // set him to the end of time!
                    setNextScan( ScanRateLoadProfile, CtiTime( YUKONEOT ) );

                    return ClientErrors::ScanGlobalAddress; // Cannot scan a global address.
                }

                // Do the devices load profile scan!
                nRet = LoadProfileScan( pReq, parse, OutMessage, vgList, retList, outList, ScanPriority );
                OutMessage = NULL;      // Memory may be forgotten

                if( nRet )
                {
                    CTILOG_ERROR( dout, "Load Profile Scan error " << nRet );
                }
            }
            else
            {
                CTILOG_WARN( dout, "Load Profile Scan aborted due to scan in progress" );
            }
        }
    }
    else
    {
        nRet = ClientErrors::MemoryAccess;
    }

    adjustNextScanTime( ScanRateLoadProfile );


    if( OutMessage != NULL )
    {
        delete OutMessage;
    }

    if( pReq != NULL )
    {
        delete pReq;
    }

    return nRet;
}


inline Protocols::Interface *CtiDeviceSingle::getProtocol()
{
    return 0;
}


YukonError_t CtiDeviceSingle::generate( CtiXfer &xfer )
{
    YukonError_t retval = ClientErrors::Abnormal;
    Protocols::Interface *prot = getProtocol();

    if( prot )  retval = prot->generate( xfer );

    return retval;
}

YukonError_t CtiDeviceSingle::decode( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retval = ClientErrors::Abnormal;
    Protocols::Interface *prot = getProtocol();

    try
    {
        if( prot )  retval = prot->decode( xfer, status );
    }
    catch( MissingConfigException &e )
    {
        CTILOG_ERROR(dout, "Device configuration missing for device \"" << getName() << "\"");

        retval = ClientErrors::MissingConfig;
    }
    catch( const Cti::Devices::InvalidConfigDataException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Invalid device configuration for device \"" << getName() << "\"");

        retval = ClientErrors::InvalidConfigData;
    }

    return retval;
}

YukonError_t CtiDeviceSingle::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retval = ClientErrors::Abnormal;
    Protocols::Interface *prot = getProtocol();

    if( prot )  retval = prot->recvCommRequest( OutMessage );

    return retval;
}

YukonError_t CtiDeviceSingle::sendCommResult( INMESS &InMessage )
{
    YukonError_t retval = ClientErrors::Abnormal;
    Protocols::Interface *prot = getProtocol();

    if( prot )  retval = prot->sendCommResult( InMessage );

    return retval;
}

bool CtiDeviceSingle::isTransactionComplete( void )
{
    bool retval = true;
    Protocols::Interface *prot = getProtocol();

    if( prot )  retval = prot->isTransactionComplete();

    return retval;
}


void CtiDeviceSingle::sendDispatchResults( CtiConnection &vg_connection ) { }
void CtiDeviceSingle::getVerificationObjects( queue< CtiVerificationBase * > &work_queue ) { }
auto CtiDeviceSingle::getQueuedResults() -> vector<queued_result_t> {  return {};  }


std::string CtiDeviceSingle::eWordReport( const ESTRUCT &ESt, Cti::Optional<repeater_info> repeater_details ) const
{
    ostringstream report;

    if( repeater_details )
    {
        report << "Route: "
            << repeater_details->route_name << ", "
            << "id " << repeater_details->route_id << endl;

        report << "Repeater: "
            << repeater_details->repeater_name << ", "
            << "id " << repeater_details->repeater_id << endl;

        report << "Repeater position: "
            << repeater_details->route_position << " / "
            << repeater_details->total_stages << " stages" << endl;
    }
    else
    {
        report << "-- Couldn't determine repeater route information --" << endl;
    }

    report << "E word variable bits: " << ESt.repeater_variable << endl;
    report << "E word echo address: " << ESt.echo_address << endl;

    if( ESt.alarm )       report << "E word alarm bit set" << endl;
    if( ESt.power_fail )  report << "E word power fail bit set" << endl;

    vector<std::string> diagnostics;

    if( ESt.diagnostics.incoming_bch_error )        diagnostics.push_back( "incoming BCH error" );
    if( ESt.diagnostics.incoming_no_response )      diagnostics.push_back( "incoming no response" );
    if( ESt.diagnostics.listen_ahead_bch_error )    diagnostics.push_back( "listen-ahead BCH error" );
    if( ESt.diagnostics.listen_ahead_no_response )  diagnostics.push_back( "listen-ahead no response" );
    if( ESt.diagnostics.repeater_code_mismatch )    diagnostics.push_back( "repeater code mismatch" );
    if( ESt.diagnostics.weak_signal )               diagnostics.push_back( "weak signal" );

    if( !diagnostics.empty() )
    {
        report << "E word diagnostics: ";
        report << Cti::join(diagnostics , "," );
        report << endl;
    }

    return report.str();
}


YukonError_t CtiDeviceSingle::ProcessResult( const INMESS   &InMessage,
    const CtiTime   TimeNow,
    CtiMessageList &vgList,
    CtiMessageList &retList,
    OutMessageList &outList )
{
    YukonError_t nRet = InMessage.ErrorCode;
    YukonError_t status = ClientErrors::None;
    bool  bLastFail = false;

    if( !nRet )
    {
        nRet = ResultDecode( InMessage, TimeNow, vgList, retList, outList );
    }

    if( nRet )
    {
        const std::string CmdStr =
            InMessage.Return.CommandStr[0]
            ? boost::algorithm::to_lower_copy( std::string( InMessage.Return.CommandStr ) )
            : "Unknown";

        if( processAdditionalRoutes( InMessage, nRet ) )
        {
            OUTMESS *OutTemplate = new OUTMESS;

            InEchoToOut( InMessage, *OutTemplate );

            CtiRequestMsg pReq( InMessage.TargetID,
                string( InMessage.Return.CommandStr ),
                InMessage.Return.UserID,
                InMessage.Return.GrpMsgID,
                InMessage.Return.RouteID,
                InMessage.Return.RetryMacroOffset,
                InMessage.Return.Attempt,
                InMessage.Return.OptionsField,
                InMessage.Priority );

            pReq.setConnectionHandle( InMessage.Return.Connection );

            {
                std::ostringstream msg;

                msg << "Macro offset " << *InMessage.Return.RetryMacroOffset - 1 << " failed. Attempting next offset."
                    << "\nError " << nRet << ": " << CtiError::GetErrorString( nRet );

                if( nRet == ClientErrors::EWordReceived && InMessage.Buffer.RepeaterError.ESt )
                {
                    msg << "\n" << eWordReport( *( InMessage.Buffer.RepeaterError.ESt ), InMessage.Buffer.RepeaterError.Details );
                }

                std::unique_ptr<CtiReturnMsg> Ret(
                    new CtiReturnMsg(
                    getID(),
                    InMessage.Return,
                    msg.str(),
                    nRet ) );

                Ret->setExpectMore( true );           // Help MACS know this is intermediate.

                retList.push_back( Ret.release() );
            }

            size_t cnt = outList.size();

            if( status = beginExecuteRequestFromTemplate( &pReq, CtiCommandParser( pReq.CommandString() ), vgList, retList, outList, OutTemplate ) )
            {
                CTILOG_ERROR( dout, "beginExecuteRequestFromTemplate return error = " << status << ": " << CtiError::GetErrorString( status ) );
            }

            if( cnt == outList.size() && status )
            {
                bLastFail = true;
            }
            else
            {
                // if blastfail is not set, we need to decrement the message we are retrying here.
                decrementGroupMessageCount( InMessage.Return.UserID, InMessage.Return.Connection );
            }

            if( OutTemplate != NULL )
            {
                delete OutTemplate;
                OutTemplate = 0;
            }
        }
        else if( CmdStr.find( "scan" ) != string::npos && hasLongScanRate( CmdStr ) )
        {
            CTILOG_WARN( dout, "Retrying long scanrate scan (cmdstr = " << CmdStr << ")" );
        }
        else
        {
            bLastFail = true;
        }

        if( bLastFail )
        {
            /* something went wrong so start by printing error */
            if( InMessage.ErrorCode != ClientErrors::PortSimulated )
            {
                CTILOG_ERROR( dout, "Error (" << InMessage.ErrorCode << ") to Remote: " << getName() << ": " << CtiError::GetErrorString( nRet ) );
            }

            CtiReturnMsg *Ret = CTIDBG_new CtiReturnMsg( getID(),
                CmdStr,
                CtiError::GetErrorString( nRet ),
                nRet,
                InMessage.Return.RouteID,
                InMessage.Return.RetryMacroOffset,
                InMessage.Return.Attempt,
                InMessage.Return.GrpMsgID,
                InMessage.Return.UserID,
                InMessage.Return.SOE,
                CtiMultiMsg_vec() );

            if( nRet == ClientErrors::EWordReceived && InMessage.Buffer.RepeaterError.ESt )
            {
                string msg = Ret->ResultString();

                msg += "\n";
                msg += eWordReport( *( InMessage.Buffer.RepeaterError.ESt ), InMessage.Buffer.RepeaterError.Details );

                Ret->setResultString( msg );
            }

            decrementGroupMessageCount( InMessage.Return.UserID, InMessage.Return.Connection );
            if( getGroupMessageCount( InMessage.Return.UserID, InMessage.Return.Connection ) > 0 )
            {
                Ret->setExpectMore( true );
            }

            retList.push_back( Ret );

            const unsigned outList_size = outList.size();

            SubmitRetry( InMessage, TimeNow, vgList, retList, outList );

            if( outList.size() != outList_size )
            {
                //  They generated another request, so we're not done.
                //  Set all of the ReturnMessages to expectMore = true
                for each( CtiMessage *msg in retList )
                {
                    if( msg && msg->isA() == MSG_PCRETURN )
                    {
                        ( (CtiReturnMsg *) msg )->setExpectMore( true );
                    }
                }
            }
            else
            {
                //  No retry generated, send an error result instead
                ErrorDecode( InMessage, TimeNow, retList );
            }
        }
    }

    return nRet;
}


YukonError_t CtiDeviceSingle::SubmitRetry( const INMESS   &InMessage,
    const CtiTime   TimeNow,
    CtiMessageList &vgList,
    CtiMessageList &retList,
    OutMessageList &outList )
{
    //  default to no retries
    return ClientErrors::None;
}

INT CtiDeviceSingle::doDeviceInit( void )
{
    INT nRet = 0;
    INT i;

    // RefreshDevicePoints();                         // Get attached points now.

    CtiTime   Now;

    for( i = 0; i < ScanRateInvalid; i++ )
    {
        setNextScan( i, firstScan( Now, i ) );                 // Set them up...
    }

    return nRet;
}

BOOL CtiDeviceSingle::isScanFlagSet( int scantype ) const
{
    BOOL val = FALSE;

    try
    {
        if( scantype != -1 )
        {
            if( _pending_map.find( scantype ) != _pending_map.end() )
            {
                val = _pending_map[scantype];                       // The specific pending will be checked if asked for...
            }
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
    }
    return val;
}
BOOL     CtiDeviceSingle::setScanFlag( int scantype, BOOL b )
{
    BOOL val = b;

    try
    {
        if( scantype != -1 )
        {
            _pending_map[scantype] = b; // The specific pending is also set.
        }
        else
        {
            if( b == FALSE )
            {
                _pending_map.clear();   // If we were nonspecific and asked for a clear, we must blank all pending flags.
            }
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
    }

    return val;
}
BOOL CtiDeviceSingle::resetScanFlag( int scantype )
{
    return setScanFlag( scantype, FALSE );
}

LONG  CtiDeviceSingle::getLastFreezeNumber() const
{
    return getScanData()->getLastFreezeNumber();
}
LONG& CtiDeviceSingle::getLastFreezeNumber()
{
    return getScanData().getLastFreezeNumber();
}
CtiDeviceSingle& CtiDeviceSingle::setLastFreezeNumber( const LONG aLastFreezeNumber )
{
    getScanData().setLastFreezeNumber( aLastFreezeNumber );
    return *this;
}

LONG  CtiDeviceSingle::getPrevFreezeNumber() const
{
    return getScanData()->getPrevFreezeNumber();
}
LONG& CtiDeviceSingle::getPrevFreezeNumber()
{
    return getScanData().getPrevFreezeNumber();
}
CtiDeviceSingle& CtiDeviceSingle::setPrevFreezeNumber( const LONG aPrevFreezeNumber )
{
    getScanData().setPrevFreezeNumber( aPrevFreezeNumber );
    return *this;
}

CtiTime  CtiDeviceSingle::getLastFreezeTime() const
{
    return getScanData()->getLastFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setLastFreezeTime( const CtiTime& aLastFreezeTime )
{
    getScanData().setLastFreezeTime( aLastFreezeTime );
    return *this;
}

CtiTime  CtiDeviceSingle::getPrevFreezeTime() const
{
    return getScanData()->getPrevFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setPrevFreezeTime( const CtiTime& aPrevFreezeTime )
{
    getScanData().setPrevFreezeTime( aPrevFreezeTime );
    return *this;
}

CtiTime  CtiDeviceSingle::getLastLPTime()
{
    static bool bDone = false;

    CtiTime rt;

    rt += ( 24 * 60 * 60 );  //  default to tomorrow, i.e., no LP data returned

    if( useScanFlags() )
    {
        if( !bDone )
        {
            CtiTime dispatchTime = peekDispatchTime();

            if( getScanData().getLastLPTime() > dispatchTime )
            {
                CTILOG_INFO( dout, "Moving Last LP Time (" << getScanData().getLastLPTime() << ") back to " << dispatchTime );

                setLastLPTime( dispatchTime );
            }

            bDone = true;
        }
        rt = getScanData().getLastLPTime();
    }
    else
    {
        CTILOG_ERROR( dout, "getLastLPTime() called, but scanFlags not set - returning now + 86400" );
    }
    return rt;
}
CtiDeviceSingle& CtiDeviceSingle::setLastLPTime( const CtiTime& aTime )
{
    if( useScanFlags() )
    {
        CtiLockGuard<CtiMutex> guard( _classMutex );
        getScanData().setLastLPTime( aTime );
    }
    return *this;
}


CtiTime CtiDeviceSingle::getNextScan( INT a )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );
    return getScanData().getNextScan( a );
}
CtiDeviceSingle& CtiDeviceSingle::setNextScan( INT a, const CtiTime &b )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    scheduleSignaledAlternateScan( a );

    CtiTime Now, When;

    if( !isWindowOpen( Now, When ) )
    {
        CTILOG_WARN( dout, getName() << " scan window opens at " << When << " -  Scanning suspended" );

        getScanData().setNextScan( a, When );
    }
    else
        getScanData().setNextScan( a, b );

    return *this;
}

CtiTime CtiDeviceSingle::nextRemoteScan() const
{
    CtiTime nt = YUKONEOT;

    if( useScanFlags() )
    {
        nt = getScanData()->nextNearestTime( ScanRateLoadProfile );

        if( getDebugLevel() & 0x00100000 )
        {
            if( nt < CtiTime( YUKONEOT ) )
            {
                CTILOG_DEBUG( dout, getName() << "'s next scan is to occur at " << nt );
            }
            else
            {
                CTILOG_DEBUG( dout, getName() << " is pending completion" );
            }
        }
    }

    return nt;
}

void CtiDeviceSingle::invalidateScanRates()
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    for( int i = 0; i < ScanRateInvalid; i++ )
    {
        if( _scanRateTbl[i] != NULL )
        {
            _scanRateTbl[i]->setUpdated( FALSE );
        }
    }

    return;
}

void CtiDeviceSingle::deleteNonUpdatedScanRates()
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    for( int i = 0; i < ScanRateInvalid; i++ )
    {
        if( _scanRateTbl[i] != NULL )
        {
            if( _scanRateTbl[i]->getUpdated() == FALSE )
            {
                CTILOG_INFO( dout, getName() << " deleting scanrate. Scantype " << i );

                delete _scanRateTbl[i];
                _scanRateTbl[i] = NULL;

                setNextScan( i, CtiTime( YUKONEOT ) );
            }
        }
    }

    return;
}

bool CtiDeviceSingle::clearedForScan( const CtiScanRate_t scantype )
{
    bool status = false;

    switch( scantype )
    {
    case ScanRateGeneral:
    {
        status = !isScanFlagSet( scantype );
        break;
    }
    case ScanRateIntegrity:
    {
        status = !isScanFlagSet( scantype );
        break;
    }
    case ScanRateAccum:
    {
        status = !isScanFlagSet( scantype );
        break;
    }
    case ScanRateLoadProfile:
    {
        status = true;
        break;
    }
    }

    return validateClearedForScan( status, scantype );
}

void CtiDeviceSingle::resetForScan( const CtiScanRate_t scantype )
{
    resetScanFlag( scantype );        // Clears ALL scan flags!
}

CtiDeviceSingle::CtiDeviceSingle() :
_useScanFlags( 0 )
{
    int i;
    _lastExpirationCheckTime = _lastExpirationCheckTime.now();
    CtiLockGuard<CtiMutex> guard( _classMutex );

    for( i = 0; i < ScanRateInvalid; i++ )
    {
        _scanRateTbl[i] = NULL;
    }
}

CtiDeviceSingle::~CtiDeviceSingle()
{
    int i;

    CtiLockGuard<CtiMutex> guard( _classMutex );

    for( i = 0; i < ScanRateInvalid; i++ )
    {
        if( _scanRateTbl[i] != NULL )
        {
            delete _scanRateTbl[i];
            _scanRateTbl[i] = NULL;
        }
    }

    if( _scanData.isDirty() )
    {
        if( !_scanData.Update() )
        {
            if( !_scanData.Insert() )
            {
                CTILOG_ERROR( dout, "Unable to insert or update scandata for device " << getName() );
            }
        }
    }
}

BOOL CtiDeviceSingle::isRateValid( const INT i ) const
{

    BOOL status = FALSE;

    if( _scanRateTbl[i] != NULL )
    {
        status = TRUE;
    }

    return status;
}

LONG CtiDeviceSingle::getScanRate( int rate ) const
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    if( rate >= 0 && rate < ScanRateInvalid && _scanRateTbl[rate] != NULL )
    {
        bool bScanIsScheduled;
        if( isAlternateRateActive( bScanIsScheduled, CtiTime(), rate ) )
        {
            // FirstScanInSignaledAlternateRate scan GOES NOW, once scheduled we report the normal rate.
            INT altrate = bScanIsScheduled ? _scanRateTbl[rate]->getAlternateRate() : 1L;

            return altrate;
        }
        else
        {
            return _scanRateTbl[rate]->getScanRate();
        }
    }
    else
    {
        return -1L;
    }
}

void CtiDeviceSingle::setScanRate( int a, LONG b )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );
    if( _scanRateTbl[a] != NULL )
    {
        _scanRateTbl[a]->setScanRate( b );
    }
}

BOOL CtiDeviceSingle::isWindowOpen( CtiTime &aNow, CtiTime &opensAt, CtiDeviceWindow_t windowType ) const
{
    BOOL status = TRUE;


    try
    {
        // loop the vector
        for( int x = 0; x < _windowVector.size(); x++ )
        {
            if( _windowVector[x].getType() == windowType )
            {
                CtiTime lastMidnight = CtiTime( CtiDate() );
                CtiTime open( lastMidnight.seconds() + _windowVector[x].getOpen() );
                CtiTime close( open.seconds() + _windowVector[x].getDuration() );

                if( open == close )
                {
                    CTILOG_ERROR( dout, "DB Config Error: " << getName() << " has a zero time scan window defined" );
                }
                else if( ( aNow < open ) || ( aNow > close ) )
                {
                    opensAt = open + 86400L;    // The next Window open time (open is today's open).
                    status = FALSE;
                }
            }
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );

        status = FALSE;
    }

    return status;
}


void CtiDeviceSingle::checkSignaledAlternateRateForExpiration()
{
    BOOL status = FALSE;

    for( int x = 0; x <_windowVector.size(); x++ )
    {
        if( _windowVector[x].getType() == DeviceWindowSignaledAlternateRate )
        {
            CtiTime lastMidnight = CtiTime( CtiDate() );
            CtiTime open( lastMidnight.seconds() + _windowVector[x].getOpen() );
            CtiTime close( open.seconds() + _windowVector[x].getDuration() );
            CtiTime now;

            // this was a scan once and remove
            if( ( open <= now ) && ( ( _windowVector[x].getDuration() == 0 ) || ( now > close ) ) )
            {
                status = TRUE;
            }

            if( status && _windowVector[x].verifyWindowMatch() )
            {
                _windowVector.erase( _windowVector.begin() + x );
                break;
            }
        }
    }
}

// this does a little more than it probably should but for now it will have to do DLS
BOOL CtiDeviceSingle::isAlternateRateActive( bool &bScanIsScheduled, CtiTime &aNow, int rate ) const
{
    BOOL status = FALSE;

    bScanIsScheduled = false;

    // loop the vector
    for( int x = 0; x <_windowVector.size(); x++ )
    {
        CtiTime lastMidnight = CtiTime( CtiDate() );
        CtiTime open( lastMidnight.seconds() + _windowVector[x].getOpen() );
        CtiTime close( open.seconds() + _windowVector[x].getDuration() );

        if( _windowVector[x].getType() == DeviceWindowAlternateRate )
        {
            if( ( open <= aNow ) && ( close > aNow ) )
            {
                status = TRUE;
                break;
            }
        }
        else if( _windowVector[x].getType() == DeviceWindowSignaledAlternateRate )
        {
            /*********************************
            * we have an alternate rate from the outside somewhere
            ***********************************/
            if( ( open <= aNow ) && ( ( close > aNow ) || ( _windowVector[x].getDuration() == 0 ) ) )
            {
                _windowVector[x].addSignaledRateActive( rate );
                bScanIsScheduled = _windowVector[x].isSignaledRateScheduled( rate );

                status = TRUE;
                break;
            }
        }
    }
    return status;
}

CtiTime CtiDeviceSingle::getNextWindowOpen() const
{
    CtiTime now;
    CtiTime lastMidnight = CtiTime( CtiDate() );
    CtiTime windowOpens = CtiTime( YUKONEOT );

    try
    {
        // loop the vector
        for( int x = 0; x < _windowVector.size(); x++ )
        {
            CtiTime open( lastMidnight.seconds() + _windowVector[x].getOpen() );

            if( now <= open && open < windowOpens )
            {
                windowOpens = open;
            }
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
    }

    return windowOpens;
}

void CtiDeviceSingle::applySignaledRateChange( LONG aOpen, LONG aDuration )
{
    bool found = false;

    CtiTime now;
    CtiTime lastMidnight = CtiTime( CtiDate() );

    if( aOpen == -1 || aDuration == 0 || ( lastMidnight.seconds() + aOpen + aDuration > now.seconds() ) )
    {
        for( int x = 0; x < _windowVector.size(); x++ )
        {
            if( _windowVector[x].getType() == DeviceWindowSignaledAlternateRate )
            {
                found = true;

                if( aOpen == -1 )
                {
                    _windowVector[x].setOpen( now.seconds() - lastMidnight.seconds() );
                    _windowVector[x].setAlternateOpen( now.seconds() - lastMidnight.seconds() );
                }
                else
                {
                    _windowVector[x].setOpen( aOpen );
                    _windowVector[x].setAlternateOpen( aOpen );
                }

                _windowVector[x].setDuration( aDuration );
                _windowVector[x].setAlternateDuration( aDuration );
            }
        }

        if( !found )
        {
            CtiTableDeviceWindow newWindow;
            newWindow.setID( getID() );
            newWindow.setType( DeviceWindowSignaledAlternateRate );
            newWindow.setDuration( aDuration );
            newWindow.setAlternateDuration( aDuration );

            if( aOpen == -1 )
            {
                newWindow.setOpen( now.seconds() - lastMidnight.seconds() );
                newWindow.setAlternateOpen( now.seconds() - lastMidnight.seconds() );
            }
            else
            {
                newWindow.setOpen( aOpen );
                newWindow.setAlternateOpen( aOpen );
            }
            _windowVector.push_back( newWindow );
        }

        if( aOpen == -1 )
        {
            CTILOG_INFO( dout, getName() << " changing to alternate scan rate starting now for " << aDuration << " seconds" );
        }
        else
        {
            CTILOG_INFO( dout, getName() << " changing to alternate scan rate starting " << CtiTime( lastMidnight.seconds() + aOpen ) << " for " << aDuration << " seconds" );
        }
    }
    else
    {
        CTILOG_WARN( dout, "Signaled alternate scan rate stop time has already passed for " << getName() << " " << CtiTime( lastMidnight.seconds() + aOpen + aDuration ) );
    }
}

void CtiDeviceSingle::DecodeDatabaseReader( Cti::RowReader &rdr )
{

    Inherited::DecodeDatabaseReader( rdr );       // get the base class handled
    _scanData.setDeviceID( getID() );

    CtiLockGuard<CtiMutex> guard( _classMutex );
    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG( dout, "Decoding DB reader" );
    }
}

void CtiDeviceSingle::DecodeScanRateDatabaseReader( Cti::RowReader &rdr )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    string scantypeStr;

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG( dout, "Decoding DB reader" );
    }

    if( !rdr["scantype"].isNull() )
    {
        rdr["scantype"] >> scantypeStr;
        INT i = resolveScanType( scantypeStr );

        if( i < ScanRateInvalid )
        {
            if( _scanRateTbl[i] == NULL )
            {
                _scanRateTbl[i] = CTIDBG_new CtiTableDeviceScanRate;
            }

            if( _scanRateTbl[i] )
                _scanRateTbl[i]->DecodeDatabaseReader( rdr );
        }
    }
}
void CtiDeviceSingle::DecodeDeviceWindowDatabaseReader( Cti::RowReader &rdr )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG( dout, "Decoding device windows" );
    }

    if( !rdr["type"].isNull() )
    {
        CtiTableDeviceWindow newWindow;
        newWindow.DecodeDatabaseReader( rdr );

        /*************************
        * if open and close were equal, duration of the window will be 0
        *
        * sounds like a config problem to me, make the window always open
        **************************
        */
        if( newWindow.getDuration() == 0 )
        {
            CTILOG_ERROR( dout, getName() << "'s device window is invalid, open and close times are equal" );
        }
        else
        {
            removeWindowType( newWindow.getType() );
            _windowVector.push_back( newWindow );
        }
    }
}


std::string CtiDeviceSingle::toString() const
{
    Cti::FormattedList itemList;
    itemList << "CtiDeviceSingle";

    {
        CtiLockGuard<CtiMutex> guard( _classMutex );
        for( int i = 0; i < ScanRateInvalid; i++ )
        {
            if( _scanRateTbl[i] )
            {
                itemList << "Device \"" << getName() << "\" Scan Rate Record " << i << ":" << _scanRateTbl[i];
            }
        }
    }

    return ( Inherited::toString() += itemList.toString() );
}

BOOL CtiDeviceSingle::useScanFlags() const
{
    return _useScanFlags;
}

CtiTableDeviceScanData& CtiDeviceSingle::getScanData()
{
    setUseScanFlags( TRUE );
    validateScanData();

    CtiLockGuard<CtiMutex> guard( _classMutex );
    return _scanData;
}

const CtiTableDeviceScanData* CtiDeviceSingle::getScanData() const
{
    return &_scanData;
}

BOOL     CtiDeviceSingle::setUseScanFlags( BOOL b )
{
    CtiLockGuard<CtiMutex> guard( _classMutex );
    return _useScanFlags = b;
}
BOOL     CtiDeviceSingle::resetUseScanFlags( BOOL b )
{
    return setUseScanFlags( b );
}

bool CtiDeviceSingle::isScanDataValid() const
{
    return true;
}

INT CtiDeviceSingle::validateScanData()
{
    INT status = ClientErrors::None;

    CtiLockGuard<CtiMutex> guard( _classMutex );

    if( !isScanFlagSet( ScanDataValid ) )
    {
        const int deviceId = getID();

        _scanData.setDeviceID( getID() );
        setScanFlag( ScanDataValid, true );
        if( !_scanData.Restore() )
        {
            if( !_scanData.Insert() )
            {
                CTILOG_ERROR( dout, "_scanData insert into DB failed for device \"" << getName() << "\" (device Id: " << getID() << ")" );
            }
        }
    }
    return status;
}

/*
 *  Look for the oldest point in the archive which is greater than the default date...
 */
CtiTime CtiDeviceSingle::peekDispatchTime() const
{
    CtiTime dispatchTime;

    vector<CtiPointSPtr> points;

    getDevicePoints( points );

    vector<CtiPointSPtr>::iterator itr;

    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        CtiTablePointDispatch ptdefault( 0 );
        CtiTablePointDispatch pd;
        CtiPointSPtr pPoint;

        pPoint = *itr;

        if( pPoint )
        {
            pd.setPointID( pPoint->getPointID() );

            if( pd.Restore() )
            {
                if( pd.getTimeStamp() > ptdefault.getTimeStamp() && pd.getTimeStamp() < dispatchTime )
                {
                    dispatchTime = pd.getTimeStamp();
                }
            }
        }
    }

    if( isDebugLudicrous() )
    {
        CTILOG_DEBUG( dout, "Oldest dispatch write time is " << dispatchTime << " for device \"" << getName() << "\" (device Id: " << getID() << ")" );
    }

    return dispatchTime;
}

bool CtiDeviceSingle::processAdditionalRoutes( const INMESS &InMessage, int nRet ) const
{
    return false;
}


bool CtiDeviceSingle::validateClearedForScan( bool clearedForScan, const CtiScanRate_t scantype )
{
    const CtiTime Now;

    if( !clearedForScan )     // In this case we need to make sure we have not gone tardy on this scan type
    {
        if( gScanForceDevices.count( getID() ) )
        {
            clearedForScan = true;
        }
        else
        {
            const auto lastCommTime = getScanData().getLastCommunicationTime( scantype );
            const auto tardyInterval = getTardyInterval( scantype );
            if( lastCommTime + tardyInterval < Now )
            {
                CTILOG_WARN( dout, getName() << "'s pending flags (" << scantype << ") reset due to timeout on prior scan (" << lastCommTime << " + " << tardyInterval << " < " << Now << ")" );

                resetForScan( scantype );
                getScanData().setLastCommunicationTime( scantype, Now );
                clearedForScan = true;
            }
        }
    }
    else            // We are about to submit a scan of some form for the device.  Mark out the time!
    {
        getScanData().setLastCommunicationTime( scantype, Now );
    }

    return clearedForScan;
}


long CtiDeviceSingle::getTardyInterval( int scantype ) const
{
    const long minTardyInterval = gConfigParms.getValueAsInt( "SCANNER_MAX_TARDY_TIME", 60 );

    long tardyInterval = getScanRate( scantype );

    if( tardyInterval < minTardyInterval )
    {
        tardyInterval = minTardyInterval;
    }
    else if( scantype == ScanRateGeneral ||
        scantype == ScanRateIntegrity )
    {
        tardyInterval = tardyInterval * 2 + 1;
    }
    else if( scantype == ScanRateAccum )
    {
        tardyInterval = tardyInterval + tardyInterval / 2;
    }

    return min( tardyInterval, 7200L );
}

bool CtiDeviceSingle::hasLongScanRate( const string &cmd ) const
{
    if( useScanFlags() )
    {
        const int scanratetype = desolveScanRateType( cmd );

        if( getScanRate( scanratetype ) > 3600 )
        {
            return true;
        }
    }

    return false;
}


bool CtiDeviceSingle::hasRateOrClockChanged( int rate, CtiTime &Now )
{
    CtiTime previousScan;
    bool bstatus = false;

    LONG scanrate = getScanRate( rate );

    /*
     *  This is a computation of the "expected" previous scan time based upon the current scan rate.
     *  It should give an indication if the scan rate or clock has been altered in a manner that is significant.
     *  The Now < previousScan will ensure that we scan at the smaller of the current or the previous rate
     *  and then proceed to align to the current rate.
     */
    if( rate == ScanRateGeneral )
    {
        previousScan = getNextScan( rate ) - scanrate - scanrate;
    }
    else
    {
        previousScan = getNextScan( rate ) - scanrate;
    }

    if( Now < previousScan )
    {
        bstatus = true;
    }

    return bstatus;
}

BOOL CtiDeviceSingle::scheduleSignaledAlternateScan( int rate ) const
{
    BOOL status = FALSE;
    CtiTime now;
    CtiTime lastMidnight = CtiTime( CtiDate() );

    // loop the vector
    for( int x = 0; x <_windowVector.size(); x++ )
    {
        if( _windowVector[x].getType() == DeviceWindowSignaledAlternateRate )
        {
            CtiTime open( lastMidnight.seconds() + _windowVector[x].getOpen() );
            CtiTime close( open.seconds() + _windowVector[x].getDuration() );

            /*********************************
            * we have an alternate rate from the outside somewhere
            ***********************************/
            if( ( open <= now ) && ( ( close > now ) || ( _windowVector[x].getDuration() == 0 ) ) )
            {
                _windowVector[x].addSignaledRateSent( rate );
                status = TRUE;
                break;
            }
        }
    }
    return status;
}

/*
 * This method allows the command string to re-discover the scan type that was asked for.
 */
CtiScanRate_t CtiDeviceSingle::desolveScanRateType( const string &cmd )
{
    // First decide what scan rate we are.
    auto scanratetype = ScanRateInvalid;

    if( findStringIgnoreCase( cmd, " general" ) || findStringIgnoreCase( cmd, " status" ) )
    {
        scanratetype = ScanRateGeneral;
    }
    else if( findStringIgnoreCase( cmd, " integrity" ) )
    {
        scanratetype = ScanRateIntegrity;
    }
    else if( findStringIgnoreCase( cmd, " accumulator" ) )
    {
        scanratetype = ScanRateAccum;
    }
    else if( findStringIgnoreCase( cmd, " loadprofile" ) )
    {
        //  not applicable
        scanratetype = ScanRateLoadProfile;
    }

    return scanratetype;
}

bool CtiDeviceSingle::removeWindowType( int window_type )
{
    bool found = false;
    bool detect = false;
    bool again = true;

    while( again )
    {
        detect = false;
        for( int x = 0; x < _windowVector.size(); x++ )
        {
            if( window_type < 0 || _windowVector[x].getType() == window_type )    // if window_type < 0, we remove all, this is the default argument.
            {
                _windowVector.erase( _windowVector.begin() + x );
                found = true;
                detect = true;
                break;
            }
        }

        if( !detect ) again = false;
    }

    return found;
}

int CtiDeviceSingle::getGroupMessageCount( long userID, Cti::ConnectionHandle client )
{
    int retVal = 0;
    channelWithID temp;
    temp.channel = client.getConnectionId();
    temp.identifier = userID;
    MessageCount_t::iterator itr = _messageCount.find( temp );

    if( itr != _messageCount.end() )
    {
        retVal = itr->second;
    }
    return retVal;
}

void CtiDeviceSingle::incrementGroupMessageCount( long userID, Cti::ConnectionHandle client, int entries /*=1*/ )
{
    channelWithID temp;
    temp.channel = client.getConnectionId();
    temp.identifier = userID;
    temp.creationTime = temp.creationTime.now();

    if( ( _lastExpirationCheckTime.now().seconds() - _lastExpirationCheckTime.seconds() ) >= 2 * 60 * 60 )//2 hour span
    {
        //Clears out every data member that is over 1 hour old, operates at most every 2 hours.
        _lastExpirationCheckTime = _lastExpirationCheckTime.now();

        for( MessageCount_t::iterator i = _messageCount.begin(); i != _messageCount.end(); )
        {
            if( ( _lastExpirationCheckTime.seconds() - i->first.creationTime.seconds() ) >= 1 * 60 * 60 )
                i = _messageCount.erase( i );
            else
                i++;
        }
    }

    MessageCount_t::iterator itr = _messageCount.find( temp );

    if( itr != _messageCount.end() )
    {
        itr->second += entries;
    }
    else
    {
        //This object was not here before. insert it.
        _messageCount.insert( MessageCount_t::value_type( temp, entries ) );
    }
}

void CtiDeviceSingle::decrementGroupMessageCount( long userID, Cti::ConnectionHandle client, int entries /*=1*/ )
{
    int msgCount;
    channelWithID temp;
    temp.channel = client.getConnectionId();
    temp.identifier = userID;
    MessageCount_t::iterator itr = _messageCount.find( temp );

    if( itr != _messageCount.end() )
    {
        msgCount = itr->second;
        msgCount -= entries;
        if( msgCount <= 0 )
        {
            _messageCount.erase( temp );
        }
        else
        {
            itr->second = msgCount;
        }
    }
}

boost::optional<std::string> CtiDeviceSingle::getTransactionReport()
{
    return boost::none;
}

// This is here so that SNPP transactions can override this function to clear the transaction report variable 
// while doing nothing in the event that some other transaction calls this function.
void CtiDeviceSingle::clearTransactionReport() { }

unsigned CtiDeviceSingle::intervalsPerDay( unsigned intervalLength )
{
    //  TODO: Perhaps it's more appropriate to throw an exception here?
    if( intervalLength == 0 )
    {
        return 0;
    }

    return 86400 / intervalLength;
}

/*
    Use this predicate to identify globally addressable devices who are trying
    to scan at their corresponding global address.  This is a no-no.
    Currently the filter is pretty rough.  It blocks every global addressing capable
    device at every global address.
    Future improvement: Segregate each device to its exact address.
    */
bool CtiDeviceSingle::isDeviceAddressGlobal()
{
    switch( getType() )
    {
    case TYPE_CCU700:
    case TYPE_CCU710:
    case TYPE_CCU711:
    case TYPE_ILEXRTU:
    case TYPE_WELCORTU:
    case TYPE_SES92RTU:
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
    case TYPE_TCU5000:
    case TYPE_TCU5500:
    case TYPE_DAVIS:
    {
        switch( getAddress() )
        {
        case RTUGLOBAL:
        case CCUGLOBAL:

            return true;
        }
    }
    }

    return false;
}


void CtiDeviceSingle::insertPointDataReport( CtiPointType_t type, int offset, CtiReturnMsg *rm, point_info pi, const string &default_pointname, const CtiTime &timestamp, double default_multiplier, int tags )
{
    string pointname;
    CtiPointSPtr p;

    if( p = getDevicePointOffsetTypeEqual( offset, type ) )
    {
        if( p->isNumeric() )
        {
            pi.value = boost::static_pointer_cast<CtiPointNumeric>( p )->computeValueForUOM( pi.value );
        }

        if( pi.quality != InvalidQuality )
        {
            CtiPointDataMsg *pdm = CTIDBG_new CtiPointDataMsg( p->getID(), pi.value, pi.quality, p->getType(), valueReport( p, pi, timestamp ).c_str() );

            if( is_valid_time( timestamp ) )
            {
                pdm->setTime( timestamp );
            }
            else if( getDebugLevel() & DEBUGLEVEL_MGR_DEVICE )
            {
                CTILOG_DEBUG( dout, "invalid time " << timestamp << " for point " << pointname << " (point Id " << p->getID() << ")" );
            }

            pdm->setTags( tags );

            rm->PointData().push_back( pdm );

            return;
        }

        pointname = p->getName();
    }
    else
    {
        pointname = default_pointname;
        pi.value *= default_multiplier;
    }

    //  if the point doesn't exist and there's no default pointname, we don't insert a message
    if( p || !pointname.empty() )
    {
        string result_string = rm->ResultString();

        if( !result_string.empty() )  result_string += "\n";

        result_string += valueReport( pointname, pi, timestamp );

        if( !p )
        {
            result_string += " (point not in DB:";
            result_string += desolvePointType( type );
            result_string += " ";
            result_string += CtiNumStr( offset );
            result_string += ")";
        }

        rm->setResultString( result_string );
    }
}


string CtiDeviceSingle::resolveStateName( long groupId, long rawValue ) const
{
    return ResolveStateName( groupId, rawValue );
}


string CtiDeviceSingle::valueReport( const CtiPointSPtr p, const point_info &pi, const CtiTime &t ) const
{
    string report;

    report = getName() + " / " + p->getName() + " = ";

    if( pi.quality != InvalidQuality )
    {
        if( p->isNumeric() )
        {
            report += CtiNumStr( pi.value, boost::static_pointer_cast<CtiPointNumeric>( p )->getPointUnits().getDecimalPlaces() );
        }
        else if( p->isStatus() )
        {
            std::string state_name = resolveStateName( boost::static_pointer_cast<CtiPointStatus>( p )->getStateGroupID(), pi.value );

            if( state_name != "" )
            {
                report += state_name;
            }
            else
            {
                report += CtiNumStr( pi.value, 0 );
            }
        }
    }
    else
    {
        report += "(invalid data)";
    }

    if( t > getDeviceDawnOfTime() && t < YUKONEOT )
    {
        report += " @ ";
        report += t.asString();
    }

    if( !pi.description.empty() )
    {
        report += " [";
        report += pi.description;
        report += "]";
    }

    return report;
}


string CtiDeviceSingle::valueReport( const string &pointname, const point_info &pi, const CtiTime &t ) const
{
    string report;

    report = getName() + " / " + pointname.c_str() + " = ";

    if( pi.quality != InvalidQuality )
    {
        report += CtiNumStr( pi.value );
    }
    else
    {
        report += "(invalid data)";
    }

    if( t > getDeviceDawnOfTime() && t < YUKONEOT )
    {
        report += " @ ";
        report += t.asString();
    }

    if( !pi.description.empty() )
    {
        report += " [";
        report += pi.description;
        report += "]";
    }

    return report;
}

/**
* Report a configuration error to the client.
*
* @param errcode Yukon error code
* @param msg message provided by the command processor
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
YukonError_t CtiDeviceSingle::reportConfigErrorDetails(
    const YukonError_t errcode,
    std::string msg,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    returnMsgs.emplace_back(
        std::make_unique<CtiReturnMsg>(
            pReq->DeviceId(),
            pReq->CommandString(),
            msg,
            errcode,
            0,
            MacroOffset::none,
            0,
            pReq->GroupMessageId(),
            pReq->UserMessageId() ));

    return errcode;
}

/**
* Report a configuration error to the client.
*
* @param ex Invalid Configuration Data Exception
* @param msg message provided by the command processor
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
YukonError_t CtiDeviceSingle::reportConfigErrorDetails(
    const Cti::Devices::InvalidConfigDataException &ex,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    return reportConfigErrorDetails( ClientErrors::InvalidConfigData, Cti::Logging::getExceptionCause( ex ), pReq, returnMsgs );
}

/**
* Report a configuration error to the client.
*
* @param ex Missing Config DataException
* @param msg message provided by the command processor
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
YukonError_t CtiDeviceSingle::reportConfigErrorDetails(
    const Cti::Devices::MissingConfigDataException &ex,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    return reportConfigErrorDetails( ClientErrors::NoConfigData, Cti::Logging::getExceptionCause( ex ), pReq, returnMsgs );
}

/**
* Add a configuration mismatch detail to message list.
*
* @param msg the message to send.
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
void CtiDeviceSingle::reportConfigDetails(
    std::string &msg,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    CTILOG_DEBUG( dout, msg );

    returnMsgs.emplace_back(
        std::make_unique<CtiReturnMsg>(
            pReq->DeviceId(),
            pReq->CommandString(),
            msg,
            ClientErrors::ConfigNotCurrent,
            0,
            Cti::MacroOffset::none,
            0,
            pReq->GroupMessageId(),
            pReq->UserMessageId() ));
}

/**
* Report a configuration mismatch to the client.
*
* @param setting Setting title
* @param deviceSetting Device setting value
* @param configSetting Config setting value
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
void CtiDeviceSingle::reportConfigMismatchDetails(
    std::string setting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    std::string msg( "Config " + setting + " did not match." );

    reportConfigDetails( msg, pReq, returnMsgs );
}

/**
* Report a configuration mismatch to the client.
*
* @param setting Setting title
* @param deviceSetting Device setting value
* @param configSetting Config setting value
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
template <>
void CtiDeviceSingle::reportConfigMismatchDetails(
    std::string setting,
    const bool configSetting,
    const boost::optional<bool> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    std::string msg( "Config " + setting + " did not match. Config: " +
        ( configSetting ? "True" : "False" ) +
        ", Meter: " +
        ( deviceSetting ? ( deviceSetting.get() ? "True" : "False" ) : "Uninitialized" ) );

    reportConfigDetails( msg, pReq, returnMsgs );
}

/**
* Report a configuration mismatch to the client.
*
* @param setting Setting title
* @param deviceSetting Device setting value
* @param configSetting Config setting value
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
template <typename T>
void CtiDeviceSingle::reportConfigMismatchDetails(
    std::string setting,
    const T configSetting,
    const boost::optional<T> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    std::string msg( "Config " + setting + " did not match. Config: " +
        std::to_string( configSetting ) +
        ", Meter: " +
        ( deviceSetting ? std::to_string( deviceSetting.get() ) : "Uninitialized" ) );

    reportConfigDetails( msg, pReq, returnMsgs );
}

/**
* Report a configuration mismatch to the client.
*
* @param setting Setting title
* @param deviceSetting Device setting value
* @param configSetting Config setting value
* @param pReg pointer to Request Message
* @param returnMsgs list of messages to return to the client.
*/
template <>
void CtiDeviceSingle::reportConfigMismatchDetails(
    std::string setting,
    const double configSetting,
    const boost::optional<double> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs )
{
    std::string msg( "Config " + setting + " did not match. Config: " +
        CtiNumStr( configSetting, 1 ) +
        ", Meter: " +
        ( deviceSetting ? std::string( CtiNumStr( deviceSetting.get(), 1 ) ) : "Uninitialized" ) );

    reportConfigDetails( msg, pReq, returnMsgs );
}

template void IM_EX_DEVDB CtiDeviceSingle::reportConfigMismatchDetails<double>(
    std::string setting,
    const double configSetting,
    const boost::optional<double> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs );

template void IM_EX_DEVDB CtiDeviceSingle::reportConfigMismatchDetails<unsigned int>(
    std::string setting,
    const unsigned int configSetting,
    const boost::optional<unsigned int> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs );

template void IM_EX_DEVDB CtiDeviceSingle::reportConfigMismatchDetails<int>(
    std::string setting,
    const int configSetting,
    const boost::optional<int> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs );

template void IM_EX_DEVDB CtiDeviceSingle::reportConfigMismatchDetails<unsigned char>(
    std::string setting,
    const unsigned char configSetting,
    const boost::optional<unsigned char> deviceSetting,
    CtiRequestMsg *pReq,
    CtiDeviceSingle::ReturnMsgList &returnMsgs );
