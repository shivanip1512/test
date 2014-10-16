
#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_focus
*
* Date:   8/27/2004
*
* Author: Julie Richter
*


*-----------------------------------------------------------------------------*/


#include "guard.h"
#include "logger.h"
#include "prot_ansi_focus.h"
#include "ctidate.h"
#include "pointdefs.h"

using std::endl;
using namespace Cti::Protocols::Ansi;
//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_focus::CtiProtocolANSI_focus( void )
: CtiProtocolANSI()
{
   _table04 = NULL;
   _table13 = NULL;
   _table24 = NULL;
}

CtiProtocolANSI_focus::~CtiProtocolANSI_focus( void )
{
    if( _table04 != NULL )
    {
        delete _table04;
        _table04 = NULL;
    }
    if( _table13 != NULL )
    {
        delete _table13;
        _table13 = NULL;
    }
    if( _table24 != NULL )
    {
        delete _table24;
        _table24 = NULL;
    }
}

void CtiProtocolANSI_focus::setAnsiDeviceType()
{
    // 3 = focus
    getApplicationLayer().setAnsiDeviceType(CtiANSIApplication::focus);
    return;
}

int CtiProtocolANSI_focus::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{
    BYTEUSHORT readBlockOffset;
    readBlockOffset.sh = 0; //0  We will always go to 0.  Which will be the most current lp data.
    
    //dataBlockDecreasingOrder
    int nbrIntervals =  (int)(abs((long)(CtiTime().seconds() - getlastLoadProfileTime()))/60) / getMaxIntervalTime();
    if( nbrIntervals >  getNbrValidBlks() * getNbrIntervalsPerBlock())
    {
        nbrIntervals = getNbrValidBlks() * getNbrIntervalsPerBlock();
    }
    setLoadProfileFullBlocks(nbrIntervals / getNbrIntervalsPerBlock() );
    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
    {
        CTILOG_DEBUG(dout, getApplicationLayer().getAnsiDeviceName() <<":"<<
                endl <<" ** Last Load Profile Time  "<< CtiTime(getlastLoadProfileTime()) <<
                endl <<" ** Nbr of Requested Intervals  "<< nbrIntervals
                );
    }
    setCurrentAnsiWantsTableValues(Focus_SetLpReadControl,0,1,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (Focus_SetLpReadControl, 0, 1, ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);

    //Bogus - not used for this...just populating with dummy zeros.
    Cti::Protocols::Ansi::REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 0;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 0;

    getApplicationLayer().setProcBfld( reqData.proc );

    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );

    BYTE lpSetupRecord[4] = {
        //READ #3.1.2.8.34 MFG Table 34 - Set Load Profile Read Control in
        //Focux AX C12.19 Implementation Guide
        readBlockOffset.ch[1],
        readBlockOffset.ch[0],
        0,
        0 };

    getApplicationLayer().populateParmPtr(lpSetupRecord, 4) ;
    getApplicationLayer().setProcDataSize( 4 );

    return -1;


}


void CtiProtocolANSI_focus::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
{
    switch( aTableID )
    {
        case Focus_InstantaneousMeasurements:
        {
            CTILOG_INFO(dout, "Creating Focus Mfg Table 4");

            _table04 = new CtiAnsiFocusMfgTable04( data, getDataOrder() );
            _table04->printResult();
            break;
        }
            
        case FocusAX_InstantaneousMeasurements:
        {
            CTILOG_INFO(dout, "Creating Focus AX Mfg Table 13");

            _table13 = new CtiAnsiFocusMfgTable13( data, getFirmwareVersion(), getFirmwareRevision(), getDataOrder()  );
            _table13->printResult();
            break;
        }
        case FocusAX_ConstantsMeasurements:
        {
            CTILOG_INFO(dout, "Creating Focus AX Mfg Table 24");

            _table24 = new CtiAnsiFocusMfgTable24( data, getDataOrder()  );
            _table24->printResult();
            break;
        }


        default:
            break;
    }
}

void CtiProtocolANSI_focus::updateMfgBytesExpected()
{
    switch( (getCurrentTableId()) )
    {
        case Focus_InstantaneousMeasurements:
        {
            setCurrentAnsiWantsTableValues(Focus_InstantaneousMeasurements,0,8,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        case FocusAX_InstantaneousMeasurements:
        {
            setCurrentAnsiWantsTableValues(FocusAX_InstantaneousMeasurements,0,53,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        case FocusAX_ConstantsMeasurements:
        {
            setCurrentAnsiWantsTableValues(FocusAX_ConstantsMeasurements,0,15,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        default:
            break;
    }
} 

bool CtiProtocolANSI_focus::retrieveMfgPresentValue( int offset, double *value )
{
    bool retVal = false;
    if( _table04 != NULL )
    {
        retVal = retrieveFocusKwPresentValue(offset,value );
    }
    if( _table13 != NULL )
    {
        retVal = retrieveFocusAXPresentValue(offset, value );
    }
    return retVal;
}


bool CtiProtocolANSI_focus::retrieveFocusKwPresentValue( int offset, double *value )
{

    switch(offset)
    {
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        {
            *value = _table04->getInstantVoltage(0) / 8; 
            return true;
        }
        default:
        {
            return false;
        }
    }
}


bool CtiProtocolANSI_focus::retrieveFocusAXPresentValue( int offset, double *value )
{
    switch(offset)
    {
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        {
            *value = _table13->getPhaseVoltage(A);
            return true;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        {
            *value = _table13->getPhaseVoltage(B);
            return true;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        {
            *value = _table13->getPhaseVoltage(C);
            return true;
        }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        {
            *value = _table13->getPhaseCurrent(A);
            return true;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        {
            *value = _table13->getPhaseCurrent(B);
            return true;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        {
            *value = _table13->getPhaseCurrent(C);
            return true;
        }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        {
            *value = _table13->getNeutralCurrent();
            return true;
        }
        default:
        {
            return false;
        }
    }
}

float CtiProtocolANSI_focus::getMfgConstants(  )
{
    if( _table24 != NULL )
    {
        return (1000 / _table24->getVhIhFhConstantsRcd() );
    }
    return 1.0;
}


bool CtiProtocolANSI_focus::doesSegmentationMatch(int index, AnsiSegmentation segmentation)
{
    switch(index)
    {
        case VhPhaseA:
        case IhPhaseA:
        {
            return segmentation == PhaseA;
        }
        case VhPhaseB:
        case IhPhaseB:
        {
            return segmentation == PhaseB;
        }
        case VhPhaseC:
        case IhPhaseC:
        {
            return segmentation == PhaseC;
        }
        default:
        {
            return true;
        }
    }
}
