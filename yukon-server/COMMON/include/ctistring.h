#pragma once

#include <string.h>
#include <boost/regex.hpp>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiString : public std::string
{

public:
    enum caseCompare{
        exact,
        ignoreCase
    };
    enum scopeType {
        one,
        all
    };
    enum stripType {
        trailing,
        leading,
        both
    };

    CtiString(): std::string(){};
    CtiString(const std::string& s): std::string(s){};
    CtiString(const char* s): std::string(s){};


    int compareTo(const std::string& s) const;
    int compareTo(const char* str) const;
    bool contains(const char* str, caseCompare cc=CtiString::exact) const;
    bool contains(const std::string& s, caseCompare cc=CtiString::exact) const;
    std::string match(const boost::regex& re, size_t start=0) const;
    std::string match(const char* re, size_t start=0) const;
    std::string match(const CtiString& s, size_t start = 0) const;
    std::string& replace(const boost::regex& re, char* s, scopeType scope=CtiString::one);
    std::string& replace(const boost::regex& re, std::string s, scopeType scope=CtiString::one);
    std::string& replace(char* e, char* s, scopeType scope=CtiString::one);
    std::string& replace(size_t beg, size_t len, const char* str);
    std::string strip(stripType scope=CtiString::both, char c = ' ');
    void toLower();
    void toUpper();
    void padFront(size_t minLength, const char *character);
    void padEnd(size_t minLength, const char *character);

    CtiString& operator= ( const CtiString& rs);
    CtiString& operator= ( const std::string& s);
    CtiString& operator= ( const char* s);
    size_t index(boost::regex& re, size_t* ext, size_t i=0);
    size_t index(const char* e, size_t* ext, size_t i=0);

};

IM_EX_CTIBASE CtiString operator+ (const CtiString &rs1, const CtiString &rs);

