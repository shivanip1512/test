
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ansi_billing_table
*
* Date:   10/28/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:38:39 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ansi_billing_table.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable::CtiAnsiBillingTable( int demSelSize, int totDataBlkSize, int numSums, int numDemds, int numCoins,
                                          int numOccurs, int uom, int demandCntl, int dataCntlLen, int dataCntl, int constants,
                                          int tFormat, int iFormat, int ni1Format, int ni2Format )
{
   _ansiBillingTable = new BILLING_TABLE;

   _ansiBillingTable->billing_table_size = (sizeof( int ) * 16);
   _ansiBillingTable->size_demand_select = demSelSize;
   _ansiBillingTable->size_tot_data_block = totDataBlkSize;
   _ansiBillingTable->nbr_summations = numSums;
   _ansiBillingTable->nbr_demands = numDemds;
   _ansiBillingTable->nbr_coincidents = numCoins;
   _ansiBillingTable->nbr_occurances = numOccurs;
   _ansiBillingTable->nbr_uom_entries = uom;
   _ansiBillingTable->nbr_demand_ctrl_entries = demandCntl;
   _ansiBillingTable->data_ctrl_length = dataCntlLen;
   _ansiBillingTable->nbr_data_ctrl_entries = dataCntl;
   _ansiBillingTable->nbr_constants_entries = constants;
   _ansiBillingTable->tm_format = tFormat;
   _ansiBillingTable->int_format = iFormat;
   _ansiBillingTable->ni_format1 = ni1Format;
   _ansiBillingTable->ni_format2 = ni2Format;
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable::~CtiAnsiBillingTable()
{
   delete _ansiBillingTable;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable& CtiAnsiBillingTable::operator=(const CtiAnsiBillingTable& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getTableSize( void )
{
   return _ansiBillingTable->billing_table_size;
}
