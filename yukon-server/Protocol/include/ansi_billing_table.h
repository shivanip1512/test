/*---------------------------------------------------------------------------------*
*
* File:   ansi_billing_table
*
* Class:
* Date:   10/28/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:44 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __ANSI_BILLING_TABLE_H__
#define __ANSI_BILLING_TABLE_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

#define BCD unsigned char

#pragma pack( push, 1)

   struct BILLING_TABLE
   {
      //some sizes to help us on the scanner side
      int      billing_table_size;
      int      size_demand_select;
      int      size_tot_data_block;

      //from tbl 21
      int      nbr_summations;
      int      nbr_demands;
      int      nbr_coincidents;
      int      nbr_occurances;

      //from tbl 11
      int      nbr_uom_entries;
      int      nbr_demand_ctrl_entries;
      int      data_ctrl_length;
      int      nbr_data_ctrl_entries;
      int      nbr_constants_entries;

      //from tbl 00
      int      tm_format;
      int      int_format;
      int      ni_format1;
      int      ni_format2;
   };


#pragma pack( pop )

class IM_EX_PROT CtiAnsiBillingTable
{
protected:

   BILLING_TABLE    *_ansiBillingTable;

private:

public:

   int getTableSize( void );

   CtiAnsiBillingTable( int demSelSize, int totDataBlkSize, int numSums, int numDemds, int numCoins, int numOccurs, int uom, int demandCntl,
                        int dataCntlLen, int dataCntl, int constants, int tFormat, int iFormat, int ni1Format, int ni2Format );

   virtual ~CtiAnsiBillingTable();

   CtiAnsiBillingTable& operator=(const CtiAnsiBillingTable& aRef);
};

#endif // #ifndef __ANSI_BILLING_TABLE_H__
