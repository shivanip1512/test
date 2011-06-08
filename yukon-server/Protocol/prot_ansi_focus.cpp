
#include "yukon.h"


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

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_focus::CtiProtocolANSI_focus( void )
: CtiProtocolANSI()
{
        
}

CtiProtocolANSI_focus::~CtiProtocolANSI_focus( void )
{
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
    setCurrentAnsiWantsTableValues(Cti::Protocols::Ansi::Focus_SetLpReadControl,0,1,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (Cti::Protocols::Ansi::Focus_SetLpReadControl, 0, 1, ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);

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
    lpSetupRecord[0] = readBlockOffset.ch[0];
    lpSetupRecord[1] = readBlockOffset.ch[1]; 
    lpSetupRecord[2] = nbrReadBlocks.ch[0];
    lpSetupRecord[3] = nbrReadBlocks.ch[1]; 

    getApplicationLayer().populateParmPtr(lpSetupRecord, 4) ;


    getApplicationLayer().setProcDataSize( 4 );

    return -1;


}
