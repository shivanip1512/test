/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_kv2
*
* Date:   2/7/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_ansi_kv2.cpp-arc  $
<<<<<<< prot_ansi_kv2.cpp
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/03/14 21:44:16 $
*    History: 
=======
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/03/14 21:44:16 $
*    History:
>>>>>>> 1.7
      $Log: prot_ansi_kv2.cpp,v $
      Revision 1.8  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.7  2005/02/17 19:02:58  mfisher
      Removed space before CVS comment header, moved #include "yukon.h" after CVS header

      Revision 1.6  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.4  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.3  2004/12/10 21:58:40  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.2  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 15:13:05  dsutton
      Manufacturer additions to the base ansi protocol for the kv2


*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_ansi_kv2.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_kv2::CtiProtocolANSI_kv2( void )
: CtiProtocolANSI()
{
    _tableZero=NULL;
    _tableSeventy=NULL;
    _tableOneHundredTen=NULL;
}

CtiProtocolANSI_kv2::~CtiProtocolANSI_kv2( void )
{
    destroyManufacturerTables();
}


void CtiProtocolANSI_kv2::destroyManufacturerTables( void )
{
   if( _tableZero != NULL )
   {
      delete _tableZero;
      _tableZero = NULL;
   }

   if( _tableSeventy != NULL )
   {
      delete _tableSeventy;
      _tableSeventy = NULL;
   }
   if( _tableOneHundredTen != NULL )
   {
      delete _tableOneHundredTen;
      _tableOneHundredTen = NULL;
   }


}

void CtiProtocolANSI_kv2::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
{

    switch( aTableID - 0x0800)
    {
        case 0:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << RWTime() << " Creating KV2 table 0" << endl;
                }
                _tableZero = new CtiAnsiKV2ManufacturerTableZero( data );
                _tableZero->printResult();
                break;
            }
        case 70:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << RWTime() << " Creating KV2 table 70" << endl;
                }

                _tableSeventy = new CtiAnsiKV2ManufacturerTableSeventy( data );
                _tableSeventy->printResult();
                break;
            }
        case 110:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << RWTime() << " Creating KV2 table 110" << endl;
                }

                _tableOneHundredTen = new CtiAnsiKV2ManufacturerTableOnehundredten( data );
                _tableOneHundredTen->printResult();
                break;
            }
        default:
            break;

    }
}


int CtiProtocolANSI_kv2::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{
    int nbrIntervals = 0;
    int nbrValidInts = getNbrValidIntvls();
    int nbrIntsPerBlock = getNbrIntervalsPerBlock();
    int nbrBlksSet = getNbrValidBlks();
    RWTime currentTime;
    

    currentTime.now();
    nbrIntervals =  (abs(currentTime.seconds() - lastLPTime)/60) / getMaxIntervalTime();
    if (nbrIntervals > (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts))
    {
        nbrIntervals = (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts); 
    }

    if (nbrIntervals <= nbrValidInts)
    {
        // lastBlockIndex;
        return getLastBlockIndex();
    }
    else if ((nbrIntervals - nbrValidInts) > nbrIntsPerBlock)
    {
        // lastBlockIndex - ((nbrIntervals - nbrValidInts) / nbrIntsPerBlock);
        return getLastBlockIndex() - ((nbrIntervals - nbrValidInts) / nbrIntsPerBlock);
    }
    else //(nbrIntervals - nbrValidInts) <= nbrIntsPerBlock
    {
        // lastBlockIndex -  1;
        return getLastBlockIndex() - 1;
    }


    /*setPreviewTable64InProgress(true);

    setCurrentAnsiWantsTableValues(64,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_READ);
    getApplicationLayer().initializeTableRequest (64, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_READ);
      */
    
}

int CtiProtocolANSI_kv2::calculateLPDataBlockSize(int numChans)
{
    return getSizeOfLPDataBlock(1);
}


int CtiProtocolANSI_kv2::calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock)
{
    return 4+ (8*numChans) + (numIntvlsLastDataBlock * ((2 * numChans) +2));
}

void CtiProtocolANSI_kv2::setAnsiDeviceType()
{
    getApplicationLayer().setAnsiDeviceType(1);
    return;
}

int CtiProtocolANSI_kv2::snapshotData()
{
    //setWriteProcedureInProgress(true);

    setCurrentAnsiWantsTableValues(7,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (7, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);

    REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 84;
    reqData.proc.std_vs_mfg_flag = 1;
    reqData.proc.selector = 0;   


    getApplicationLayer().setProcBfld( reqData.proc );
    
    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


    //getApplicationLayer().populateParmPtr(0, 1) ;


    getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) );

    return -1;

}
int CtiProtocolANSI_kv2::batteryLifeData()
{
    return 1;
}

bool CtiProtocolANSI_kv2::retreiveKV2PresentValue( int offset, double *value )
{
    bool retVal = false;
    switch(offset)
    {
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        {
            *value = (_tableOneHundredTen->getVlnFundOnly()[0])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:   
        {
            *value = (_tableOneHundredTen->getVlnFundOnly()[1])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE: 
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:   
        {
            *value = (_tableOneHundredTen->getVlnFundOnly()[2])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT: 
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:   
        {
            *value = (_tableOneHundredTen->getCurrFundOnly()[0])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT: 
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:  
        {
            *value = (_tableOneHundredTen->getCurrFundOnly()[1])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT: 
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:   
        {
            *value = (_tableOneHundredTen->getCurrFundOnly()[2])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT: 
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:   
        {
            *value = (_tableOneHundredTen->getImputedNeutralCurr())/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        default:
        {
            value = NULL;
            break;
        }
    }
    return retVal;
}


int CtiProtocolANSI_kv2::getGoodBatteryReading()
{
    return  -1;
}

int CtiProtocolANSI_kv2::getCurrentBatteryReading()
{
    return -1;
}
int CtiProtocolANSI_kv2::getDaysOnBatteryReading()
{
    return -1;
}
