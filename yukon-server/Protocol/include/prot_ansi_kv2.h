/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_kv2.h
*
* Class:
* Date:   2/7/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_ansi_kv2.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: prot_ansi_kv2.h,v $
      Revision 1.7  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.6  2005/12/20 17:19:58  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.5  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.4  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.3  2004/12/10 21:58:42  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.2  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 15:13:05  dsutton
      Manufacturer additions to the base ansi protocol for the kv2




*-----------------------------------------------------------------------------*/
#ifndef __PROT_ANSI_KV2_H__
#define __PROT_ANSI_KV2_H__
#pragma warning( disable : 4786)

#include <windows.h>
#include "prot_ansi.h"
#include "pointdefs.h"
#include "ansi_kv2_mtable_000.h"
#include "ansi_kv2_mtable_070.h"
#include "ansi_kv2_mtable_110.h"

class IM_EX_PROT CtiProtocolANSI_kv2:public CtiProtocolANSI
{
    typedef CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_kv2();
        virtual ~CtiProtocolANSI_kv2();

        virtual void destroyManufacturerTables( void );
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID );

        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);
        virtual int calculateLPDataBlockSize(int numChans);
        virtual int calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock);
        virtual void setAnsiDeviceType();
        virtual int snapshotData();
        virtual int batteryLifeData();
        virtual bool retreiveKV2PresentValue( int offset, double *value );
        virtual int getGoodBatteryReading();
        virtual int getCurrentBatteryReading();
        virtual int getDaysOnBatteryReading();


   private:

      CtiAnsiKV2ManufacturerTable000  *_table000;
      CtiAnsiKV2ManufacturerTable070  *_table070;
      CtiAnsiKV2ManufacturerTable110  *_table110;


};


#endif // #ifndef __PROT_ANSI_KV2_H__

