/*
 * class CtiTokenzier
 *  
 * Author: Jian Liu 
 * Date: 07/20/2005 15:37:47 
 * 
 * 
 * This class initailly is to replace the RWCTokinizer in cmdparse.cpp. It is not part of the DLL interface.
 * The class use boost tokenizer library to perform the string tokenizing. Its interface and methods are the 
 * same as RWCTokenizer. Please refer to RWTokenizer document to see its functionalities.
 */

#ifndef _TOKENIZER_WRAPER_
#define _TOKENIZER_WRAPER_
#include <string>
#include <boost/tokenizer.hpp>
#include "dlldefs.h"

typedef boost::token_iterator_generator<boost::char_separator<char> >::type Iter;

class IM_EX_CTIBASE CtiTokenizer {
private:
    std::string str;
    bool first; // first time use this tokenizer, which means the iterator should be at the beginning of the string
    bool pend;  // end of the tokenizer, which means the iterator should be at the end of the string
    Iter tok_iter; //iterator to keep track of the current position
public:
    CtiTokenizer();
    CtiTokenizer(std::string& s);
    std::string operator()(char* d = " ");
};

#endif
