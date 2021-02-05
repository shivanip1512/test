#include "precompiled.h"

#include "tbl_port_timing.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortTimings::CtiTablePortTimings()
{
   for(int i = 0; i < LAST_DELAY; i++)
   {
      _delays[ i ] = 0;
   }
}

CtiTablePortTimings::~CtiTablePortTimings()
{
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

void CtiTablePortTimings::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   rdr["pretxwait"]        >> _delays[PRE_RTS_DELAY];
   rdr["rtstotxwait"]      >> _delays[RTS_TO_DATA_OUT_DELAY];
   rdr["posttxwait"]       >> _delays[DATA_OUT_TO_RTS_DOWN_DELAY];
   rdr["receivedatawait"]  >> _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY];
   rdr["PostCommWait"]     >> _delays[POST_REMOTE_DELAY];
   rdr["extratimeout"]     >> _delays[EXTRA_DELAY];

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       Cti::FormattedList itemList;

       itemList.add("pre_rts_delay")        << _delays[PRE_RTS_DELAY];
       itemList.add("rts_to_data_out_dly")  << _delays[RTS_TO_DATA_OUT_DELAY];
       itemList.add("data_out_to_rts_dwn")  << _delays[DATA_OUT_TO_RTS_DOWN_DELAY];
       itemList.add("data_out_inflush_dly") << _delays[DATA_OUT_TO_INBUFFER_FLUSH_DELAY];
       itemList.add("post_comm_wait")       << _delays[POST_REMOTE_DELAY];
       itemList.add("extra_delay")          << _delays[EXTRA_DELAY];

       CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName() <<
               itemList
               );
   }
}

string CtiTablePortTimings::getTableName()
{
   return "PortTiming";
}
