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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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

   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["pretxwait"]        >> _delays[PRE_RTS_DELAY];
   rdr["rtstotxwait"]      >> _delays[RTS_TO_DATA_OUT_DELAY];
   rdr["posttxwait"]       >> _delays[DATA_OUT_TO_RTS_DOWN_DELAY];
   rdr["receivedatawait"]  >> _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY];
   rdr["extratimeout"]     >> _delays[EXTRA_DELAY];

   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << " pre_rts_delay        : " << _delays[PRE_RTS_DELAY] << endl;
      dout << " rts_to_data_out_dly  : " << _delays[RTS_TO_DATA_OUT_DELAY] << endl;
      dout << " data_out_to_rts_dwn  : " << _delays[DATA_OUT_TO_RTS_DOWN_DELAY] << endl;
      dout << " data_out_inflush_dly : " << _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY] << endl;
      dout << " extra_delay          : " << _delays[EXTRA_DELAY] << endl;
   }
}

RWCString CtiTablePortTimings::getTableName()
{
   return "PortTiming";
}
