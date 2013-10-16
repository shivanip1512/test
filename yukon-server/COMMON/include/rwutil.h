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

