#ifndef __RWUTIL_H__
#define __RWUTIL_H__

#include <set>
#include <string>
#include <rw/cstring.h>
#include <rw/vstream.h>
#include <rw/db/reader.h>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/db/datetime.h>

#include "boost_time.h"

#include "dlldefs.h"

using std::set;
using std::string;

/*
 * RW Date/Time - boost
 */
IM_EX_CTIBASE date to_boost_date(const RWDate &rw_date);
IM_EX_CTIBASE ptime to_boost_ptime(const RWTime &rw_time);
IM_EX_CTIBASE ptime to_boost_ptime(const RWDBDateTime &rwdb_datetime);

/*
 * RW virtual stream - std::string operators
 */
IM_EX_CTIBASE RWvostream &operator<<(RWvostream &strm, const string &s);
IM_EX_CTIBASE RWvistream &operator>>(RWvistream &strm,       string &s);

/*
 * RW Database - std::string operators
 */
IM_EX_CTIBASE RWDBInserter &operator<<(RWDBInserter &ins, const string &s);
IM_EX_CTIBASE RWDBReader   &operator>>(RWDBReader   &rdr,       string &s);

/*
 * RW Database - boost ptime operators
 */
IM_EX_CTIBASE RWDBInserter &operator<<(RWDBInserter &ins, const ptime &p);
IM_EX_CTIBASE RWDBReader   &operator>>(RWDBReader   &rdr,       ptime &p);

#endif
