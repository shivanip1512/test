
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
    BYTEUSHORT nbrReadBlocks;
    nbrReadBlocks.sh = 0; // zero to (nbrValidBlocks - 1)

    //dataBlockDecreasingOrder
    int nbrIntervals =  (int)(abs((long)(CtiTime().seconds() - getlastLoadProfileTime()))/60) / getMaxIntervalTime();
    if( nbrIntervals <=  getNbrValidIntvls() )
    {
        nbrReadBlocks.sh = 0;
    }
    else if( nbrIntervals > getNbrValidIntvls() &&
             nbrIntervals < getNbrIntervalsPerBlock() * getNbrValidBlks() )
    {
        nbrReadBlocks.sh = ((nbrIntervals - getNbrValidIntvls() ) / getNbrIntervalsPerBlock()) + 1;
    }
    else
    {
        nbrReadBlocks.sh = getNbrValidBlks() - 1;
    }
    setLoadProfileFullBlocks(nbrIntervals / getNbrIntervalsPerBlock() );
    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** Last Load Profile Time  " <<CtiTime(getlastLoadProfileTime()) << endl;
        dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** Nbr of Requested Intervals  " <<nbrIntervals << endl;
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

    BYTE lpSetupRecord[4];

    //READ #3.1.2.8.34 MFG Table 34 - SEt Load Profile Read Control in
    //Focux AX C12.19 Implementation Guide
    lpSetupRecord[0] = readBlockOffset.ch[1];
    lpSetupRecord[1] = readBlockOffset.ch[0];
    lpSetupRecord[2] = 0; //nbrReadBlocks.ch[1];
    lpSetupRecord[3] = 0; //nbrReadBlocks.ch[0];

    getApplicationLayer().populateParmPtr(lpSetupRecord, 4) ;


    getApplicationLayer().setProcDataSize( 4 );

    return -1;


}


void CtiProtocolANSI_focus::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
{
    switch( aTableID )
    {
         case Focus_InstantaneouMeasurements:
        {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " Creating Focus Mfg Table 4" << endl;
            }
            _table04 = new CtiAnsiFocusMfgTable04( data, getDataOrder() );
            _table04->printResult();
            break;
        }
            
        case FocusAX_InstantaneouMeasurements:
        {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << CtiTime() << " Creating Focus Mfg Table 13" << endl;
            }
            _table13 = new CtiAnsiFocusMfgTable13( data, getFirmwareVersion(), getFirmwareRevision(), getDataOrder()  );
            _table13->printResult();
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
        case Focus_InstantaneouMeasurements:
        {
            setCurrentAnsiWantsTableValues(Focus_InstantaneouMeasurements,0,8,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        case FocusAX_InstantaneouMeasurements:
        {
            setCurrentAnsiWantsTableValues(FocusAX_InstantaneouMeasurements,0,53,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        default:
            break;
    }
} 

bool CtiProtocolANSI_focus::retreiveMfgPresentValue( int offset, double *value )
{
    bool retVal = false;
    if( _table04 != NULL )
    {
        retVal = retreiveFocusKwPresentValue(offset,value );
    }
    if( _table13 != NULL )
    {
        retVal = retreiveFocusAXPresentValue(offset, value );
    }
    return retVal;
}


bool CtiProtocolANSI_focus::retreiveFocusKwPresentValue( int offset, double *value )
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


bool CtiProtocolANSI_focus::retreiveFocusAXPresentValue( int offset, double *value )
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
