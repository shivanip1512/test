#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/12/10 21:58:40 $
*    History: 
      $Log: prot_ansi_kv2.cpp,v $
      Revision 1.3  2004/12/10 21:58:40  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.2  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 15:13:05  dsutton
      Manufacturer additions to the base ansi protocol for the kv2


*-----------------------------------------------------------------------------*/

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
    _table_110=NULL;
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

   if( _table_110 != NULL )
   {
      delete _table_110;
      _table_110 = NULL;
   }
}

void CtiProtocolANSI_kv2::convertToManufacturerTable( BYTE *data, BYTE numBytes, int aTableID )
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

//                _table_110 = new CtiKV2AnsiTable_110( data );
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
    nbrIntervals =  ((currentTime.seconds() - lastLPTime)/60) / getMaxIntervalTime();
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
