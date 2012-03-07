#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_sentinel
*
* Date:   8/27/2004
*
* Author: Julie Richter
*


*-----------------------------------------------------------------------------*/


#include "guard.h"
#include "logger.h"
#include "prot_ansi_sentinel.h"
#include "ctidate.h"

using std::endl;
using namespace Cti::Protocols::Ansi;

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
    CtiProtocolANSI_sentinel::destroyManufacturerTables();  // virtual function call: specify which one we are calling
}


void CtiProtocolANSI_sentinel::destroyManufacturerTables( void )
{

}

void CtiProtocolANSI_sentinel::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
{

    switch( aTableID )
    {

    case Sentinel_BatteryLifeResponse:
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

          CtiTime tempTime1 = CtiTime(_timeOfLastOutage + CtiTime(CtiDate(1,1,2000)).seconds());
          CtiTime tempTime2 = CtiTime(_timeOfLastInterrogation + CtiTime(CtiDate(1,1,2000)).seconds());


          if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
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

    default:
            break;

    }
}

int CtiProtocolANSI_sentinel::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{

    setCurrentAnsiWantsTableValues(ProcedureInitiate,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (ProcedureInitiate, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);

    Cti::Protocols::Ansi::REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 22;
    reqData.proc.std_vs_mfg_flag = 1;
    reqData.proc.selector = 3;


    getApplicationLayer().setProcBfld( reqData.proc );

    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


    reqData.u.pm22.time = lastLPTime - CtiTime(CtiDate(1,1,2000)).seconds();

    getApplicationLayer().populateParmPtr((BYTE *) &reqData.u.pm22.time, 4) ;

    getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) + 4 );

    return -1;

}


void CtiProtocolANSI_sentinel::setAnsiDeviceType()
{
    // 2 = sentinel
    getApplicationLayer().setAnsiDeviceType(CtiANSIApplication::sentinel);
    return;
}

bool CtiProtocolANSI_sentinel::batteryLifeData()
{
    setCurrentAnsiWantsTableValues(Sentinel_BatteryLifeRequest,0,1,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (Sentinel_BatteryLifeRequest, 0, 1, ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_WRITE);

    //Bogus - not used for this...just populating with dummy zeros.
    Cti::Protocols::Ansi::REQ_DATA_RCD reqData;
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

    return true;

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


void CtiProtocolANSI_sentinel::updateMfgBytesExpected()
{
    switch( (getCurrentTableId()) )
    {
        
        case Sentinel_BatteryLifeResponse:
        {
            setCurrentAnsiWantsTableValues(Sentinel_BatteryLifeResponse,0,20,ANSI_TABLE_TYPE_MANUFACTURER, ANSI_OPERATION_READ);
            break;
        }
        

        default:
            break;
    }
}



