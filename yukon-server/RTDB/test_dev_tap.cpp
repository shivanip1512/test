#include <boost/test/auto_unit_test.hpp>

#include "dev_tap.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using Cti::Test::byte_str;

struct test_TapPagingTerminal : Cti::Devices::TapPagingTerminal
{
    using TapPagingTerminal::incrementPagePrefix;
};

BOOST_AUTO_TEST_SUITE( test_dev_tap )

BOOST_AUTO_TEST_CASE(test_dev_tap_incrementPagePrefix)
{
    test_TapPagingTerminal tap;

    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'b');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'c');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'd');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'b');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'c');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'd');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
}
/*
BOOST_AUTO_TEST_CASE(test_dev_tap_early_hangup)
{
    Cti::Devices::TapPagingTerminal tap;
    /*
YukonError_t TapPagingTerminal::decodeResponseDisconnect (CtiXfer &xfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    if( commReturnValue == ClientErrors::ReadTimeout )
    {
        //  Remote end hung up on us - success!
        setCurrentState(StateComplete);

        return ClientErrors::None;
    }
    else if( commReturnValue )
    {
        CTILOG_ERROR(dout, getName() <<" status "<< status);

        return commReturnValue;
    }

    // Communications must have been successful
    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
    }

    setPreviousState( getCurrentState() );    // Leave a breadcrumb for those who follow to get us back here if needed


//-----

    tap.allocateDataBins(OutMessage);
    tap.setInitialState(0);

    if( (status = InitializeHandshake (Port, Device, traceList)) == ClientErrors::None )

//-----

YukonError_t InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, list< CtiMessage* > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    YukonError_t   status = ClientErrors::None;
    ULONG          bytesReceived (0);

    CtiDeviceIED *aIEDDevice = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    while( status == ClientErrors::None &&
           !((aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeAbort) ||
             (aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeComplete)))
    {
        status = aIEDDevice->generateCommandHandshake (transfer, traceList);
        status = aPortRecord->outInMess(transfer, dev, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, dev, status);
        }

        if( deviceCanSurviveThisStatus(status) )
        {
            status = aIEDDevice->decodeResponseHandshake (transfer, status, traceList);
        }

        DisplayTraceList(aPortRecord, traceList, true);
    }


    // check our return
    if(status == ClientErrors::None && aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeAbort)
    {
        status = ClientErrors::Abnormal;
    }

    return status;
}
//-----
    {
        status = PerformRequestedCmd (Port, Device, NULL, NULL, traceList);
//-----
            YukonError_t   status = ClientErrors::None;
    CtiXfer        transfer;
    BYTE           inBuffer[2048];
    BYTE           outBuffer[2048];
    ULONG          bytesReceived=0;

    INT            infLoopPrevention = 0;

    #ifdef _DEBUG
    memset(inBuffer, 0, sizeof(inBuffer));
    #endif

    CtiDeviceIED *aIED = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    try
    {
        do
        {
            status = aIED->generateCommand ( transfer , traceList);
            status = aPortRecord->outInMess( transfer, dev, traceList );
            if( transfer.doTrace( status ) )
            {
                aPortRecord->traceXfer(transfer, traceList, dev, status);
            }

            if( deviceCanSurviveThisStatus(status) )
            {
                status = aIED->decodeResponse (transfer, status, traceList);
            }

            // check if we are sending load profile to scanner
            if(!status && aIED->getCurrentState() == CtiDeviceIED::StateScanReturnLoadProfile)
            {
                if( aInMessage )
                {
                    status = ReturnLoadProfileData ( aPortRecord, dev, *aInMessage, aOutMessage, traceList);
                }
            }

            if(aOutMessage) processCommStatus(status, aOutMessage->DeviceID, aOutMessage->TargetID, aOutMessage->Retry > 0, dev);

            if( status )
            {
                CTILOG_ERROR(dout, aIED->getName() <<" status = "<< status <<" "<< GetErrorString( status ));
            }

            if(!(++infLoopPrevention % INF_LOOP_COUNT))  // If we go INF_LOOP_COUNT loops we're considering this infinite...
            {
                CTILOG_WARN(dout, "Possible infinite loop on device "<< aIED->getName() <<" - breaking loop, forcing abort state.");

                status = ClientErrors::Abnormal;
            }

            DisplayTraceList(aPortRecord, traceList, true);

        } while( status == ClientErrors::None &&
                 !((aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
                   (aIED->getCurrentState() == CtiDeviceIED::StateScanComplete)));

        if( (status == ClientErrors::None && aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
            (PorterDebugLevel & PORTER_DEBUG_VERBOSE && status) )
        {
            CTILOG_WARN(dout, aIED->getName() <<" status was returned as "<< status <<".  This may be a ied state or an actual error status. - State set to abort");

            status = ClientErrors::Abnormal;
        }

        DisplayTraceList(aPortRecord, traceList, true);

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Port "<< aPortRecord->getName());
    }

    return status;

//-----

        YukonError_t dcstat = TerminateHandshake (Port, Device, traceList);
//-----

YukonError_t TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, list< CtiMessage* > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    YukonError_t   status = ClientErrors::None;
    ULONG          bytesReceived (0);

    CtiDeviceIED *aIEDDevice = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    do
    {
        status = aIEDDevice->generateCommandDisconnect(transfer, traceList);
        status = aPortRecord->outInMess(transfer, dev, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, dev, status);
        }

        if( deviceCanSurviveThisStatus(status) )
        {
            status = aIEDDevice->decodeResponseDisconnect (transfer, status, traceList);
        }

    } while( status == ClientErrors::None &&
             !((aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateCompleteNoHUP)));

    // check our return
    if(status == ClientErrors::None)
    {
        if( aPortRecord->isDialup() )
        {
            if(aIEDDevice->getCurrentState() == CtiDeviceIED::StateCompleteNoHUP)
            {
                aPortRecord->setShouldDisconnect( FALSE );
            }
            else
            {
                aPortRecord->setShouldDisconnect( TRUE );
            }
        }
        else if( aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete   // The device did the disconnect.
              || aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort)
        {
            switch(aIEDDevice->getType())
            {
            case TYPE_TAPTERM:
                {
                    // Since an EOT was done on this port by this device, all devices on this port need to logon the next loop.
                    // Sweep the port and tag them so.
                    vector<CtiDeviceManager::ptr_type> devices;

                    DeviceManager.getDevicesByPortID(aPortRecord->getPortID(), devices);

                    for_each(devices.begin(), devices.end(), TAPNeedsLogon());

                    break;
                }
            }
        }
    }

    return status;
}

        if(status == ClientErrors::None)
            status = dcstat;
    }

    tap.freeDataBins();
}
        */

BOOST_AUTO_TEST_SUITE_END()

