#include "precompiled.h"
#include <boost/tokenizer.hpp>
#include "ctitokenizer.h"
#include "boostutil.h"

/*
 * Constructor, initiates the date memebers
 */

CtiTokenizer::CtiTokenizer(std::string& s)
{
    str = s;
    first = true;
    pend = false;
}

/*
 * Tokenizing.
 * d is the delimiters
 */

std::string CtiTokenizer::operator()(char* d){
    boost::char_separator<char> sep(d);
    Iter tok_char;
    if( first ){
        first = false;
        tok_iter = boost::make_token_iterator<std::string>(str.begin(), str.end(), sep);
        tok_char = tok_iter;
    } else {
        tok_char = boost::make_token_iterator<std::string>(tok_iter.base(), tok_iter.end(), sep);
    }
    Iter end;
    if((tok_char != end)&&!pend){
        std::string  s = *tok_char;
        tok_iter = tok_char;
        return s;
    } else {
        pend = true;
        return std::string();
    }
}

