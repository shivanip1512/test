
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_sentinel
*
* Date:   8/27/2004
*
* Author: Julie Richter
*


*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_ansi_sentinel.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_sentinel::CtiProtocolANSI_sentinel( void )
: CtiProtocolANSI()
{
    //_tableZero=NULL;
   // _tableSeventy=NULL;
    _table_110=NULL;
}

CtiProtocolANSI_sentinel::~CtiProtocolANSI_sentinel( void )
{
    destroyManufacturerTables();
}


void CtiProtocolANSI_sentinel::destroyManufacturerTables( void )
{
   /*if( _tableZero != NULL )
   {
      delete _tableZero;
      _tableZero = NULL;
   }

   if( _tableSeventy != NULL )
   {
      delete _tableSeventy;
      _tableSeventy = NULL;
   } */
   
   if( _table_110 != NULL )
   {
      delete _table_110;
      _table_110 = NULL;
   }
}

void CtiProtocolANSI_sentinel::convertToManufacturerTable( BYTE *data, BYTE numBytes, int aTableID )
{

    switch( aTableID - 0x0800)
    {
        /*case 0:
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
        */case 110:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << RWTime() << " Creating Sentinel table 110" << endl;
                }

//                _table_110 = new CtiKV2AnsiTable_110( data );
                break;
            }
        default:
            break;
      
    }
}

int CtiProtocolANSI_sentinel::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{
    //setWriteProcedureInProgress(true);

    setCurrentAnsiWantsTableValues(7,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (7, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);

    REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 22;
    reqData.proc.std_vs_mfg_flag = 1;
    reqData.proc.selector = 3;   


    getApplicationLayer().setProcBfld( reqData.proc );
    
    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


    reqData.u.pm22.time = lastLPTime - RWTime(RWDate(1,1,2000)).seconds();

    //UINT32 tempTime =  RWTime().seconds() - 60 - RWTime(RWDate(1,1,2000)).seconds() - /*(26 * 3600 * 24) - */10800;
    getApplicationLayer().populateParmPtr((BYTE *) &reqData.u.pm22.time, 4) ;
    //getApplicationLayer().populateParmPtr((BYTE *) &tempTime, 4) ;


    getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) + 4 );

    return -1;

}


int CtiProtocolANSI_sentinel::calculateLPDataBlockSize(int numChans)
{
    return (264 * numChans) + 260;
}

int CtiProtocolANSI_sentinel::calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock)
{
    return 4+ (8*numChans) + (numIntvlsLastDataBlock * ((2 * numChans) + 2));
}

void CtiProtocolANSI_sentinel::setAnsiDeviceType()
{
    // 2 = sentinel
    getApplicationLayer().setAnsiDeviceType(2);
    return;
}
int CtiProtocolANSI_sentinel::snapshotData()
{
    return 1;
}

