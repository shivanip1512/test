/*
 * file rwutil.h
 *  
 * Author: Jian Liu 
 * Date: 07/6/2005 10:14:32 
 * 
 * 
 * This class is the bridge between the new classes and exsiting RW classes.
 *
 * Although this file should only contain the bridge methods, it is not
 * So it can not simply be removed after RW is replaced.
 * please keep the non-rw related methods
 */

#ifndef __RWUTIL_H__
#define __RWUTIL_H__
#include "yukon.h"

#include <set>
#include <vector>
#include <string.h>
#include <rw/vstream.h>
#include <rw/db/reader.h>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/db/datetime.h>
#include <algorithm>
#include "ctitime.h"
#include "boost_time.h"
#include <boost/tokenizer.hpp>
#include <boost/shared_ptr.hpp>

/*
 * for RWDBDateTime
 */
inline RWDBDateTime toRWDBDT(const CtiTime& ct)
{
    RWTime temptime(ct.toRwSeconds());
    RWDBDateTime rwdb(temptime);
    return rwdb;
}

inline date to_boost_date(const RWDate& rw_date){
    return date(rw_date.year(), rw_date.month(), rw_date.dayOfMonth());
}

inline ptime to_boost_ptime(const RWTime& rw_time){
    struct tm tm;
    rw_time.extract(&tm);
    time_t tt = mktime(&tm);
    return boost::posix_time::from_time_t(tt);
}

inline ptime to_boost_ptime(const RWDBDateTime& rwdb_datetime)
{
    struct tm tm;
    rwdb_datetime.extract(&tm);
    time_t tt = mktime(&tm);
    return boost::posix_time::from_time_t(tt);
}

/* DataBase functions
 *
 */

inline RWDBReader& operator>>(RWDBReader& rdr, std::string& s)
{
    RWCString rw_str;
    rdr >> rw_str;
    s = (const char*) rw_str.data();
    return rdr ;
}

inline RWDBReader&   operator>>(RWDBReader&   rdr, ptime& p)
{
    RWTime t;
    rdr >> t;
    p = to_boost_ptime(t);
    return rdr;
}

inline RWDBInserter& operator<<(RWDBInserter& ins, const std::string &s)
{
    return ins << RWCString(s.c_str());
}

inline RWDBInserter& operator<<(RWDBInserter& ins, const ptime& p)
{
    RWDBDateTime dbdt(RWTime(ptime_to_utc_seconds(p) + rwEpoch));
    return ins << dbdt;
}
inline RWDBInserter& operator<<(RWDBInserter& ins, const CtiTime &ct)
{
    RWDBDateTime rwdb = toRWDBDT(ct);
    return ins << rwdb;
}
inline RWDBReader&   operator>>(RWDBReader&   rdr, CtiTime& ct)
{
    RWDBDateTime rwdb;
    rdr >> rwdb;
    struct tm ctime;
    rwdb.extract(&ctime);
    ct = CtiTime(&ctime);
    return rdr;
}


/*RWfile
 *
 */
inline RWFile& operator>>(RWFile &f, std::string &str){
    RWCString s;
    f >> s;
	str = s.data();
    return f;

}





/*
 * RW stream operators.  seralize bool
 */
inline RWvostream& operator<<(RWvostream &strm, bool b)
{
    strm << (int) b;
    return strm;
}

inline RWvistream& operator>>(RWvistream &strm, bool& b)
{
    int i;
    strm >> i;
    b = (bool) i;
    return strm;
}    



/*
 * RW stream operators.  serialize stl containers
 */

/* --Overload to put Vectors on stream for saveguts in capcontrol. used when Replacing RWOrdered */
template <class T> 
RWvostream& operator<< ( RWvostream& strm, std::vector<T> v )
{
    std::vector<T>::iterator iter;
    strm << v.size();
    for(iter = v.begin();iter != v.end();iter++)
        strm << *iter;
    return strm;
}
template <class T> 
RWvostream& operator<< ( RWvostream& strm, std::vector<T>* v )
{
    std::vector<T>::iterator iter;
    strm << v->size();
    for(iter = v->begin();iter != v->end();iter++)
        strm << *iter;
    return strm;
}
//Needed to types of this, one for pointer vector's and one for normal vectors
template <class T>
RWvistream& operator >> ( RWvistream &strm, std::vector<T>* v )
{
    int num_elements;
    T elem;
    strm >> num_elements;

    for(int iter = 0; iter < num_elements; iter++)
    {
        strm >> elem;
        v->push_back(elem);
    }
    return strm;
}

template <class T>
RWvistream& operator >> ( RWvistream &strm, std::vector<T> &v )
{
    int num_elements;
    T elem;
    strm >> num_elements;

    for(int iter = 0; iter < num_elements; iter++)
    {
        strm >> elem;
        v.push_back(elem);
    }
    return strm;
}

template <class T> 
RWvistream& operator>> ( RWvistream& strm, boost::shared_ptr<T> ptr )
{
    strm >> *ptr.get();
    return strm;
}
template <class T> 
RWvostream& operator<< ( RWvostream& strm, boost::shared_ptr<T> ptr )
{
    strm << *(ptr.get());
    return strm;
}

inline RWvostream& operator<<(RWvostream& strm, const CtiTime &ct)
{
    if(!ct.isValid())
    {
        RWTime rwt((unsigned long)0);
        return strm << rwt;
    } else
    {
        RWTime rwt(ct.toRwSeconds());
        return strm << rwt;
    }
}

inline RWvistream&  operator>>(RWvistream& strm, CtiTime &ct)
{
    RWTime rwt;
    strm >> rwt;
    if(!rwt.isValid())
    {
        ct = CtiTime(CtiTime::not_a_time);
        return strm;
    }
    struct tm ctime;
    rwt.extract(&ctime);
    ct = CtiTime(&ctime);
    return strm;
}

inline RWvostream& operator<<(RWvostream& strm, const std::string& s)
{
    return strm << RWCString((const char*) s.c_str());
}

inline RWvistream&  operator>>(RWvistream& strm, std::string& s)
{
    RWCString rw_str;
    strm >> rw_str;
    s = (const char*) rw_str.data();
    return strm;
}

//Conversion
inline RWCString string2RWCString(std::string str){
    return RWCString(str.c_str());
}

inline string RW2String( RWCString str ){
	return string(str.data());
}
typedef boost::tokenizer<boost::char_separator<char> > Boost_char_tokenizer;
typedef boost::tokenizer<boost::char_separator<char> > Tokenizer;
typedef boost::char_separator<char> Separator;

/*
* end wraper
*/



#endif
