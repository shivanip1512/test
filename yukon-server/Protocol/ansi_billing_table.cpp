#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   ansi_billing_table
*
* Date:   10/28/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_billing_table.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:55 $
*    History: 
      $Log: ansi_billing_table.cpp,v $
      Revision 1.4  2005/02/10 23:23:55  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

      
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ansi_billing_table.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable::CtiAnsiBillingTable( int demSelSize, int totDataBlkSize, int numSums, int numDemds, int numCoins,
                                          int numOccurs, int uom, int demandCntl, int dataCntlLen, int dataCntl, int constants,
                                          int tFormat, int iFormat, int ni1Format, int ni2Format, int nbrtiers, bool dreset,
                                          bool timedata, bool cumdemand, bool contcumdemand )
{
   _billingTable.billing_table_size = (sizeof( int ) * 17) + (sizeof( bool ) * 4);
   _billingTable.size_demand_select = demSelSize;
   _billingTable.size_tot_data_block = totDataBlkSize;
   _billingTable.nbr_summations = numSums;
   _billingTable.nbr_demands = numDemds;
   _billingTable.nbr_coincidents = numCoins;
   _billingTable.nbr_occurances = numOccurs;
   _billingTable.nbr_uom_entries = uom;
   _billingTable.nbr_demand_ctrl_entries = demandCntl;
   _billingTable.data_ctrl_length = dataCntlLen;
   _billingTable.nbr_data_ctrl_entries = dataCntl;
   _billingTable.nbr_constants_entries = constants;
   _billingTable.tm_format = tFormat;
   _billingTable.int_format = iFormat;
   _billingTable.ni_format1 = ni1Format;
   _billingTable.ni_format2 = ni2Format;

   _billingTable.nbr_tiers = nbrtiers;
   _billingTable.demand_reset_flag = dreset;
   _billingTable.time_data_flag = timedata;
   _billingTable.cum_demand_flag = cumdemand;
   _billingTable.cont_cum_demand_flag = contcumdemand;

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable::CtiAnsiBillingTable( BYTE *blob )
{
   memcpy( &_billingTable, blob, 17 * sizeof( int ) + sizeof( bool ) * 4);
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::copyDataOut( BYTE *dest )
{
   int   total;

   memcpy( dest, &(_billingTable.billing_table_size), 17 * sizeof( int ) + sizeof( bool ) * 4);

   return( 17*sizeof( int ) + sizeof( bool )*4 );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiBillingTable::~CtiAnsiBillingTable()
{
//   delete _billingTable;
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
   return _billingTable.billing_table_size;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getDemandSelectSize( void )
{
   return _billingTable.size_demand_select;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getTotDataBlockSize( void )
{
   return _billingTable.size_tot_data_block;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumSummations( void )
{
   return _billingTable.nbr_summations;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumDemands( void )
{
   return _billingTable.nbr_demands;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumCoins( void )
{
   return _billingTable.nbr_coincidents;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumOccurs( void )
{
   return _billingTable.nbr_occurances;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumUOMEntries( void )
{
   return _billingTable.nbr_uom_entries;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumDemandCntlEntries( void )
{
   return _billingTable.nbr_demand_ctrl_entries;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getDataCntlLength( void )
{
   return _billingTable.data_ctrl_length;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumDataCntlEntries( void )
{
   return _billingTable.nbr_data_ctrl_entries;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getConstantEntries( void )
{
   return _billingTable.nbr_constants_entries;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getTmFormat( void )
{
   return _billingTable.tm_format;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getIntFormat( void )
{
   return _billingTable.int_format;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNiFormat1( void )
{
   return _billingTable.ni_format1;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNiFormat2( void )
{
   return _billingTable.ni_format2;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiBillingTable::getNumTiers( void )
{
   return _billingTable.nbr_tiers;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiBillingTable::getDemandResetFlag( void )
{
   return _billingTable.demand_reset_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiBillingTable::getTimeDateFlag( void )
{
   return _billingTable.time_data_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiBillingTable::getCumDemandFlag( void )
{
   return _billingTable.cum_demand_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiBillingTable::getContCumDemandFlag( void )
{
   return _billingTable.cont_cum_demand_flag;
}


