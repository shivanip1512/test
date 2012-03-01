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

#pragma once
#include "yukon.h"

#include <set>
#include <vector>
#include <string.h>
#include <rw/vstream.h>
#include "database_connection.h"
#include "database_reader.h"
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <algorithm>
#include "ctitime.h"
#include "boost_time.h"
#include <boost/tokenizer.hpp>
#include <boost/shared_ptr.hpp>
#include "boostutil.h"

/*
 *  RW stream operators.  seralize bool
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

/* --Overload to put Vectors on stream for saveguts in capcontrol. used when Replacing RWOrdered*
*  Note that this cannot do a vector copy, RWCollection objects must never be put onto the stream
*  using temporary objects. RW does pointer comparison to know if an object is already on the stream
*  and if the object was on the stack, it is easy for another object to use the same pointer address.*/
template <class T>
RWvostream& operator<< ( RWvostream& strm, const std::vector<T> &v )
{
    std::vector<T>::const_iterator iter;
    strm << v.size();
    for(iter = v.begin();iter != v.end();iter++)
        strm << *iter;
    return strm;
}

template <class T>
RWvostream& operator<< ( RWvostream& strm, const std::vector<T>* v )
{
    std::vector<T>::const_iterator iter;
    strm << v->size();
    for(iter = v->begin();iter != v->end();iter++)
        strm << *iter;
    return strm;
}

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

template <class K, class T>
RWvistream& operator >> ( RWvistream &strm, std::map<K,T> &m )
{
    K keyElem;
    T elem;

    int num_elements;
    strm >> num_elements;

    for(int iter = 0; iter < num_elements; iter++)
    {
        strm >> keyElem;
        strm >> elem;

        m[keyElem] = elem;
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
inline RWCString string2RWCString(const std::string &str){
    return RWCString(str.c_str());
}

inline std::string RW2String( const RWCString &str ){
        return std::string(str.data());
}
typedef boost::tokenizer<boost::char_separator<char> > Boost_char_tokenizer;
typedef boost::tokenizer<boost::char_separator<char> > Tokenizer;
typedef boost::char_separator<char> Separator;

/*
* end wraper
*/

