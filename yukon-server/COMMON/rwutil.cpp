#include "rwutil.h"

RWvostream& operator<<(RWvostream& strm, const std::string& s)
{
    return strm << RWCString((const char*) s.data());
}

RWvistream&  operator>>(RWvistream& strm, std::string& s)
{
    RWCString rw_str;
    strm >> rw_str;
    s = (const char*) rw_str.data();
    return strm;
}

RWDBReader& operator>>(RWDBReader& rdr, std::string& s)
{
    RWCString rw_str;
    rdr >> rw_str;
    s = (const char*) rw_str.data();
    return rdr ;
}

