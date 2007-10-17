/*
 * class CtiString
 *
 * Author: Jian Liu
 * Date: 07/23/2005 15:20:00
 *
 *
 * This class initailly is to replace the string in cmdparse.cpp. It is not part of the DLL interface.
 *
 * The class use C++ standard string class and boost regular expression library.
 *
 * Its interface and methods are the same as sTring.
 *
 * Please refer to string document to see its functionalities.
 */



#ifndef  __STRING_WRAPER_H__
#define  __STRING_WRAPER_H__

#include "rwutil.h"
#include <string.h>
#include <boost/regex.hpp>
using std::string;

class IM_EX_CTIBASE CtiString : public string
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

    CtiString(): string(){};
    CtiString(const string& s): string(s){};
    CtiString(const char* s): string(s){};


    int compareTo(const string& s) const;
    int compareTo(const char* str) const;
    bool contains(const char* str, caseCompare cc=CtiString::exact) const;
    bool contains(const string& s, caseCompare cc=CtiString::exact) const;
    string match(const boost::regex& re, size_t start=0) const;
    string match(const char* re, size_t start=0) const;
    string& replace(const boost::regex& re, char* s, scopeType scope=CtiString::one);
    string& replace(const boost::regex& re, string s, scopeType scope=CtiString::one);
    string& replace(char* e, char* s, scopeType scope=CtiString::one);
    string& replace(size_t beg, size_t len, const char* str);
    string strip(stripType scope=CtiString::both, char c = ' ');
    void toLower();
    void toUpper();

    CtiString& operator= ( const CtiString& rs);
    CtiString& operator= ( const string& s);
    CtiString& operator= ( const char* s);
    size_t index(boost::regex& re, size_t* ext, size_t i=0);
    size_t index(char* e, size_t* ext, size_t i=0);

};

IM_EX_CTIBASE CtiString operator+ (const CtiString &rs1, const CtiString &rs);


#endif
