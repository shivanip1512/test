#pragma once

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
