#include "yukon.h"


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
    //_table_110=NULL;
    _daysSinceDemandReset = 0;   
    _daysSinceLastTest = 0;      
    _timeOfLastOutage = 0;       
    _timeOfLastInterrogation = 0;
    _daysOnBattery = 0;          
    _currentBatteryReading = 0;  
    _goodBatteryReading = 0;     
    _dstConfigured = 0;          

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
   
  /* if( _table_110 != NULL )
   {
      delete _table_110;
      _table_110 = NULL;
   } */
}

void CtiProtocolANSI_sentinel::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
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
            */ 
    case 1:
        {
            break;
        }

    case 2:
      {
          memcpy((void *)&_daysSinceDemandReset, data, sizeof(unsigned char) * 2);
          data += 2;
          memcpy((void *)&_daysSinceLastTest, data, sizeof(unsigned char) * 2);
          data += 2;
          memcpy((void *)&_timeOfLastOutage, data, sizeof(unsigned char) * 4);
          data += 4;
          memcpy((void *)&_timeOfLastInterrogation, data, sizeof(unsigned char) * 4);
          data += 4;
          memcpy((void *)&_daysOnBattery, data, sizeof(unsigned char) * 4);
          data += 4;
          memcpy((void *)&_currentBatteryReading, data, sizeof(unsigned char) * 2);
          data += 2;
          memcpy((void *)&_goodBatteryReading, data, sizeof(unsigned char) * 2);
          data += 2;
          memcpy((void *)&_dstConfigured, data, sizeof(unsigned char));
          data += 1;

          RWTime tempTime1 = RWTime(_timeOfLastOutage + RWTime(RWDate(1,1,2000)).seconds());
          RWTime tempTime2 = RWTime(_timeOfLastInterrogation + RWTime(RWDate(1,1,2000)).seconds());


          if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
          {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << endl<<"=======================  Clock Related Data =========================" << endl;
                   dout << "      Days Since Demand Reset      " << _daysSinceDemandReset << endl;
                   dout << "      Days Since Last Test         " <<_daysSinceLastTest << endl;
                   dout << "      Time Of Last Outage          " << tempTime1 << endl;
                   dout << "      Time Of Last Interrogation   " << tempTime2 << endl;
                   dout << "      Days On Battery              " << _daysOnBattery << endl;
                   dout << "      Current Battery Reading      " << _currentBatteryReading << endl;
                   dout << "      Good Battery Reading         " << _goodBatteryReading << endl;
                   dout << "      DST Configured               " << _dstConfigured << endl << endl;
          }

          break;
      }

        /*case 70:
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

bool CtiProtocolANSI_sentinel::retreiveKV2PresentValue( int offset, double *value )
{
    return false;
}

int CtiProtocolANSI_sentinel::batteryLifeData()
{
    //setWriteProcedureInProgress(true);

    setCurrentAnsiWantsTableValues(2049,0,1,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (2049, 0, 1, ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);

    //Bogus - not used for this...just populating with dummy zeros.
    REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 0;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 0;   

    getApplicationLayer().setProcBfld( reqData.proc );
    
    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );
    
    BYTE clockRelatedData[34];

    //READ #3 in Sentinel Developers Guide
    clockRelatedData[0] = 0x00;   //Mode
    clockRelatedData[1] = 0x08;    //Count 

    BYTEULONG temp;
    temp.ul = DAYS_SINCE_DEMAND_RESET;
    clockRelatedData[2] = temp.ch[0];
    clockRelatedData[3] = temp.ch[1];
    clockRelatedData[4] = temp.ch[2];
    clockRelatedData[5] = temp.ch[3];

    temp.ul = DAYS_SINCE_LAST_TEST;
    clockRelatedData[6] = temp.ch[0];
    clockRelatedData[7] = temp.ch[1];
    clockRelatedData[8] = temp.ch[2];
    clockRelatedData[9] = temp.ch[3];

    temp.ul = TIME_OF_LAST_OUTAGE;
    clockRelatedData[10] = temp.ch[0];
    clockRelatedData[11] = temp.ch[1];
    clockRelatedData[12] = temp.ch[2];
    clockRelatedData[13] = temp.ch[3];


    temp.ul = TIME_OF_LAST_INTERROGATION;
    clockRelatedData[14] = temp.ch[0];
    clockRelatedData[15] = temp.ch[1];
    clockRelatedData[16] = temp.ch[2];
    clockRelatedData[17] = temp.ch[3];


    temp.ul = DAYS_ON_BATTERY;
    clockRelatedData[18] = temp.ch[0];
    clockRelatedData[19] = temp.ch[1];
    clockRelatedData[20] = temp.ch[2];
    clockRelatedData[21] = temp.ch[3];


    temp.ul = CURRENT_BATTERY_READING;
    clockRelatedData[22] = temp.ch[0];
    clockRelatedData[23] = temp.ch[1];
    clockRelatedData[24] = temp.ch[2];
    clockRelatedData[25] = temp.ch[3];

    temp.ul = GOOD_BATTERY_READING;
    clockRelatedData[26] = temp.ch[0];
    clockRelatedData[27] = temp.ch[1];
    clockRelatedData[28] = temp.ch[2];
    clockRelatedData[29] = temp.ch[3];


    temp.ul = DST_CONFIGURED;
    clockRelatedData[30] = temp.ch[0];
    clockRelatedData[31] = temp.ch[1];
    clockRelatedData[32] = temp.ch[2];
    clockRelatedData[33] = temp.ch[3]; 

    getApplicationLayer().populateParmPtr(clockRelatedData, 34) ;


    getApplicationLayer().setProcDataSize( 34 );

    return -1;

}

int CtiProtocolANSI_sentinel::getGoodBatteryReading()
{
    return  (int)_goodBatteryReading;
}

int CtiProtocolANSI_sentinel::getCurrentBatteryReading()
{
    return (int)_currentBatteryReading;
}

int CtiProtocolANSI_sentinel::getDaysOnBatteryReading()
{
    return (int)_daysOnBattery;
}





