#pragma warning( disable : 4786)
#ifndef __PROT_ANSI_KV2_H__
#define __PROT_ANSI_KV2_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/09/30 21:37:19 $
*    History: 
      $Log: prot_ansi_kv2.h,v $
      Revision 1.2  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 15:13:05  dsutton
      Manufacturer additions to the base ansi protocol for the kv2




*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "prot_ansi.h"

#include "ansi_kv2_mtable_zero.h"
#include "ansi_kv2_mtable_seventy.h"
//#include "kv2_ansi_table_oneten.h"

class IM_EX_PROT CtiProtocolANSI_kv2:public CtiProtocolANSI
{
    typedef CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_kv2();
        virtual ~CtiProtocolANSI_kv2();

        virtual void destroyManufacturerTables( void );
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, int aTableID );

        virtual void calculateLPDataBlockStartIndex(ULONG lastLPTime);
        virtual int calculateLPDataBlockSize(int numChans);
        virtual int calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock);
   private:

      CtiAnsiKV2ManufacturerTableZero                   *_tableZero;
      CtiAnsiKV2ManufacturerTableSeventy                *_tableSeventy;
//      CtiKV2AnsiTable_110               *_table_110;

      BYTE               *_table_110;

};


#endif // #ifndef __PROT_ANSI_KV2_H__

