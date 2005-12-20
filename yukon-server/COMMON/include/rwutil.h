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


/*
 * for RWDBDateTime
 */
inline RWDBDateTime toRWDBDT(const CtiTime& ct)
{
    struct tm ctime;
	ct.extract(&ctime);
    RWDBDateTime rwdb(&ctime);
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
    for(iter = v.begin;iter != v.end();iter++)
        strm << *iter;
    return strm;
}

template <class T>
RWvistream& operator >> ( RWvistream &strm, std::vector<T> v )
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
inline RWvostream& operator<<(RWvostream& strm, const CtiTime &ct)
{
    if(!ct.isValid())
    {
        RWTime rwt((unsigned long)0);
        return strm << rwt;
    } else
    {
        struct tm ctime;
        ct.extract(&ctime);
        RWTime rwt(&ctime);
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

// compare string ignore cases (not used now)

inline int stringCompareIgnoreCase(const std::string& str1, const std::string& str2){
    std::string s1 = str1;
    std::string s2 = str2;
    std::transform(str1.begin(), str1.end(), s1.begin(), ::tolower);
    std::transform(str2.begin(), str2.end(), s2.begin(), ::tolower);
    return s1.compare(s2);

}
//TS add for a string contains without case sensitivity
inline int stringContainsIgnoreCase(const std::string& str, const std::string& frag){
    std::string s1 = str;
    std::string s2 = frag;

    std::transform(str.begin(), str.end(), s1.begin(), ::tolower);
    std::transform(frag.begin(), frag.end(), s2.begin(), ::tolower);

	if (str.find(frag) == string::npos)
		return 0;
	else return 1;
}
// find the substring ignorecase
inline std::string::size_type findStringIgnoreCase(std::string str, std::string sub){
    std::transform(str.begin(), str.end(), str.begin(), ::tolower);
    std::transform(sub.begin(), sub.end(), sub.begin(), ::tolower);
    return str.find(sub)!=std::string::npos;
}

inline std::string char2string(char c){
    std::string s;
    s = c;
    return s;
}

//Conversion
inline RWCString string2RWCString(std::string str){
    return RWCString(str.c_str());
}
inline void CtiToLower( std::string& str){
	std::transform(str.begin(),str.end(),str.begin(),::tolower);
}
inline void CtiToUpper( std::string& str){
	std::transform(str.begin(),str.end(),str.begin(),::toupper);
}
inline string RW2String( RWCString str ){
	return string(str.data());
}
//String Modificaction:
inline std::string trim_right ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( str.find_last_not_of ( t ) + 1 ) ;
}

// trim_left() 
inline std::string trim_left ( std::string & source ,std::string t= " ")
{
    std::string str = source;
    return source = str.erase ( 0 , source.find_first_not_of ( t ) ) ;
}
// trim() 
inline std::string trim ( std::string & source , std::string t = " " )
{
    std::string str = source;
    return source = trim_left ( trim_right ( str , t ) , t ) ;
}


typedef boost::tokenizer<boost::char_separator<char> > Boost_char_tokenizer;




/*
* end wraper
*/



#endif
