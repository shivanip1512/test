
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

class IM_EX_PROT CtiProtocolANSI_sentinel:public CtiProtocolANSI
{
    typedef CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_sentinel();
        virtual ~CtiProtocolANSI_sentinel();

        virtual void destroyManufacturerTables( void );
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, int aTableID );

        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);
        virtual int calculateLPDataBlockSize(int numChans);
        virtual void setAnsiDeviceType();
        virtual int snapshotData();

        virtual bool retreiveKV2PresentValue( int offset, double *value );

        int calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock);

   private:

      //CtiAnsiKV2ManufacturerTableZero                   *_tableZero;
      //CtiAnsiKV2ManufacturerTableSeventy                *_tableSeventy;
//      CtiKV2AnsiTable_110               *_table_110;

      BYTE               *_table_110;

};


#endif // #ifndef __PROT_ANSI_SENTINEL_H__

