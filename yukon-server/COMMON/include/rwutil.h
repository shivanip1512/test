#ifndef __RWUTIL_H__
#define __RWUTIL_H__

#include <string>
#include <rw/cstring.h>
#include <rw/vstream.h>
#include <rw/db/reader.h>

#include "dlldefs.h"

/*
 * RW virtual stream - std::string operators
 */
IM_EX_CTIBASE RWvostream& operator<<(RWvostream& strm, const std::string& s);
IM_EX_CTIBASE RWvistream&  operator>>(RWvistream& strm, std::string& s);

/*
 * RW Database - std::string operators
 */
IM_EX_CTIBASE RWDBReader&  operator>>(RWDBReader& rdr, std::string& s);

#endif
