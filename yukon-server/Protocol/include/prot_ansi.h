
#pragma warning( disable : 4786)
#ifndef __PROT_ANSI_H__
#define __PROT_ANSI_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi
*
* Class:
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_ansi.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/04/22 21:12:54 $
*    History: 
      $Log: prot_ansi.h,v $
      Revision 1.5  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.4  2003/04/25 15:12:29  dsutton
      This is now base protocol class for every ansi type meter

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>

#include "ansi_application.h"
#include "ansi_datalink.h"
#include "ansi_billing_table.h"
#include "std_ansi_tbl_base.h"
#include "std_ansi_tbl_zero_zero.h"
#include "std_ansi_tbl_zero_one.h"
#include "std_ansi_tbl_one_zero.h"
#include "std_ansi_tbl_one_one.h"
#include "std_ansi_tbl_one_two.h"
#include "std_ansi_tbl_one_three.h"
#include "std_ansi_tbl_one_four.h"
#include "std_ansi_tbl_one_five.h"
#include "std_ansi_tbl_one_six.h"
#include "std_ansi_tbl_two_one.h"
#include "std_ansi_tbl_two_two.h"
#include "std_ansi_tbl_two_three.h"
#include "std_ansi_tbl_five_two.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#define FLOAT64

//converters
//these aren't needed anymore
typedef union
{
   UCHAR    ch[8];
   double   u64;
} BYTEFLOAT64;

typedef union
{
   UCHAR    ch[12];
} BYTEARRAY12;

typedef union
{
   UCHAR    ch[6];
} BYTEARRAY;

typedef union
{
   UCHAR    ch[6];
   double   int48;
} BYTEINT48;

typedef union
{
   UCHAR    ch[4];
   long     int32;
} BYTEINT32;


struct WANTS_HEADER
{
   unsigned long  lastLoadProfileTime;
   int            numTablesRequested;
   int            command;
};

#define ANSI_TABLE_TYPE_STANDARD         0
#define ANSI_TABLE_TYPE_MANUFACTURER     1
#define ANSI_OPERATION_READ              10
#define ANSI_OPERATION_WRITE             11


//this one's usable on both sides
struct ANSI_TABLE_WANTS
{
   int   tableID;
   int   tableOffset;
   int   bytesExpected;
   int  type;
   int  operation;
};

//=========================================================================================================================================
//tables defined by the ansi standard
//=========================================================================================================================================
#pragma pack( push, 1)
/*
struct TABLE_27_PRESENT_REGISTER_SELECTION
{
   unsigned char        present_demand_select[255];
   unsigned char        present_value_select[255];
};
*/
struct TIME
{
   union CASES
   {
      struct CASE1
      {
         BCD   hour;
         BCD   minute;
         BCD   second;
      };

      struct CASE2
      {
         unsigned char  hour;
         unsigned char  minute;
         unsigned char  second;
      };

      struct CASE3
      {
         long d_time;
      };

      struct CASE4
      {
         long d_time;
      };
   };
};

#pragma pack( pop )

class IM_EX_PROT CtiProtocolANSI
{
   public:

      CtiProtocolANSI();
      virtual ~CtiProtocolANSI();

      void reinitialize( void );
      void destroyMe( void );

      int getTotalBillingSize( void );

      bool generate( CtiXfer &xfer );
      bool decode  ( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      int recvInbound( INMESS *InMessage );
      int recvOutbound( OUTMESS  *OutMessage );

      void convertToTable( );

      void decipherInMessage( void );
      int sendCommResult( INMESS *InMessage );
        void receiveCommResult( INMESS *InMessage );
    void buildWantedTableList( BYTE *aPtr);


      CtiANSIApplication &getApplicationLayer( void );
    void updateBytesExpected( );
    int sizeOfNonIntegerFormat( int aFormat );


      void processBillingTables( void );
      void processLoadProfileTables( void );

      // pure virtual functions
    virtual void destroyManufacturerTables( void )=0;
    virtual void convertToManufacturerTable( BYTE *data, int numBytes, int aTableID )=0;

    //bool isReturnMsgMaxSize(void);
   //void fillReturnMessage(BYTE *aBuffer);


   protected:

   private:

      int                              _index;

      CtiANSIApplication               _appLayer;

      BYTE                             *_outBuff;

      ULONG                            _bytesInGot;

      ANSI_TABLE_WANTS                 *_tables;
      WANTS_HEADER                     *_header;

//      CtiAnsiBillingHeader              *_billingHeader;
      CtiAnsiBillingTable              *_billingTable;

      CtiAnsiTableZeroZero             *_tableZeroZero;
      CtiAnsiTableZeroOne              *_tableZeroOne;
      CtiAnsiTableOneZero              *_tableOneZero;
      CtiAnsiTableOneOne               *_tableOneOne;
      CtiAnsiTableOneTwo               *_tableOneTwo;
      CtiAnsiTableOneThree             *_tableOneThree;
      CtiAnsiTableOneFour              *_tableOneFour;
      CtiAnsiTableOneFive              *_tableOneFive;
      CtiAnsiTableOneSix               *_tableOneSix;
      CtiAnsiTableTwoOne               *_tableTwoOne;
      CtiAnsiTableTwoTwo               *_tableTwoTwo;
      CtiAnsiTableTwoThree             *_tableTwoThree;
      CtiAnsiTableFiveTwo              *_tableFiveTwo;
};


#endif // #ifndef __PROT_ANSI_H__
