
#pragma warning( disable : 4786)
#ifndef __PROT_ANSI_SENTINEL_H__
#define __PROT_ANSI_SENTINEL_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_sentinel.h
*
* Class:
* Date:   8/27/2004
*
* Author: Julie Richter
*
*


*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "prot_ansi.h"

//#include "ansi_kv2_mtable_zero.h"
//#include "ansi_kv2_mtable_seventy.h"
//#include "kv2_ansi_table_oneten.h"


#define DAYS_SINCE_DEMAND_RESET    0x1c030008
#define DAYS_SINCE_LAST_TEST       0x94000010
#define TIME_OF_LAST_OUTAGE        0x94000004
#define TIME_OF_LAST_INTERROGATION 0x94000005
#define DAYS_ON_BATTERY            0x6c000003
#define CURRENT_BATTERY_READING    0x9400000d
#define GOOD_BATTERY_READING       0x9400000c
#define DST_CONFIGURED             0x6c000008


class IM_EX_PROT CtiProtocolANSI_sentinel:public CtiProtocolANSI
{
    typedef CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_sentinel();
        virtual ~CtiProtocolANSI_sentinel();

        virtual void destroyManufacturerTables( void );
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID );

        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);
        virtual int calculateLPDataBlockSize(int numChans);
        virtual void setAnsiDeviceType();
        virtual int snapshotData();
        virtual int batteryLifeData();
        virtual int getGoodBatteryReading();
        virtual int getCurrentBatteryReading();
        virtual int getDaysOnBatteryReading();

        virtual bool retreiveKV2PresentValue( int offset, double *value );

        int calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock);
                                
   private:

      //CtiAnsiKV2ManufacturerTableZero                   *_tableZero;
      //CtiAnsiKV2ManufacturerTableSeventy                *_tableSeventy;
//      CtiKV2AnsiTable_110               *_table_110;

     // BYTE               *_table_110;

       UINT16 _daysSinceDemandReset;
       UINT16 _daysSinceLastTest;
       UINT32 _timeOfLastOutage;
       UINT32 _timeOfLastInterrogation;
       UINT32 _daysOnBattery;
       UINT16 _currentBatteryReading;
       UINT16 _goodBatteryReading;
       UINT8  _dstConfigured;
};


#endif // #ifndef __PROT_ANSI_SENTINEL_H__

