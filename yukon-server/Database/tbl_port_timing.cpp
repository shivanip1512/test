#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_timing
*
* Date:   8/28/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_timing.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_port_timing.h"
#include "logger.h"

CtiTablePortTimings::CtiTablePortTimings()
{
   for(int i = 0; i < LAST_DELAY; i++)
   {
      _delays[ i ] = 0;
   }
}

CtiTablePortTimings::CtiTablePortTimings(const CtiTablePortTimings& aRef)
{
   *this = aRef;
}

CtiTablePortTimings::~CtiTablePortTimings()
{
}

CtiTablePortTimings& CtiTablePortTimings::operator=(const CtiTablePortTimings& aRef)
{
   if(this != &aRef)
   {
      for(int i = 0; i < LAST_DELAY; i++)
      {
         _delays[ i ] = aRef.getDelay(i);
      }
   }
   return *this;
}

CtiTablePortTimings&  CtiTablePortTimings::setDelay(int Offset, int D)
{
   _delays[Offset] = D;
   return *this;
}

ULONG  CtiTablePortTimings::getDelay(int Offset) const
{
   return _delays[Offset];
}

void CtiTablePortTimings::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable portTbl = db.table(getTableName() );

   selector <<
      portTbl["pretxwait"] <<
      portTbl["rtstotxwait"] <<
      portTbl["posttxwait"] <<
      portTbl["receivedatawait"] <<
      portTbl["extratimeout"];

   selector.from(portTbl);

   selector.where( selector.where() && keyTable["paobjectid"] == portTbl["portid"] );
}

void CtiTablePortTimings::DecodeDatabaseReader(RWDBReader &rdr)
{
   ULONG    uTemp;

   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["pretxwait"]        >> _delays[PRE_RTS_DELAY];
      if(getDebugLevel() & 0x00000800) dout << " pre_rts_delay        : " << _delays[PRE_RTS_DELAY] << endl;
      rdr["rtstotxwait"]      >> _delays[RTS_TO_DATA_OUT_DELAY];
      if(getDebugLevel() & 0x00000800) dout << " rts_to_data_out_dly  : " << _delays[RTS_TO_DATA_OUT_DELAY] << endl;
      rdr["posttxwait"]       >> _delays[DATA_OUT_TO_RTS_DOWN_DELAY];
      if(getDebugLevel() & 0x00000800) dout << " data_out_to_rts_dwn  : " << _delays[DATA_OUT_TO_RTS_DOWN_DELAY] << endl;
      rdr["receivedatawait"]  >> _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY];
      if(getDebugLevel() & 0x00000800) dout << " data_out_inflush_dly : " << _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY] << endl;
      rdr["extratimeout"]     >> _delays[EXTRA_DELAY];
      if(getDebugLevel() & 0x00000800) dout << " extra_delay          : " << _delays[EXTRA_DELAY] << endl;
   }
}

RWCString CtiTablePortTimings::getTableName()
{
   return "PortTiming";
}
