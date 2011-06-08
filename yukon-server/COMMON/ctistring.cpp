#include "yukon.h"
#include "ctistring.h"
#include "utility.h"

using std::string;

int CtiString::compareTo(const string&s) const{
    return string::compare(s);
}

int CtiString::compareTo(const char* str) const{
    return string::compare(str);
}

bool CtiString::contains(const char* str, caseCompare cc) const{
    if(cc == CtiString::exact){
        if(find(str) == string::npos){
            return false;
        } else {
            return true;
        }
    } else {
        return findStringIgnoreCase(c_str(), string(str));
    }
}

bool CtiString::contains(const string& s, caseCompare cc) const{
    return find(s)!=string::npos;
}

string CtiString::match(const boost::regex& re, size_t start) const{
    boost::smatch what;
    string t = substr(start);
    if(boost::regex_search(t, what, re))
    {
        return string(what[0]);
    } else {
        return string("");
    }
}


string CtiString::match(const CtiString& s, size_t start) const{
    boost::regex e1;
    e1.assign((string)s);
    return match(e1, start);
}


string CtiString::match(const char* re, size_t start) const{
    boost::regex e1;
    e1.assign(re);
    return match(e1, start);
}


string& CtiString::replace(const boost::regex& re, char* s, scopeType scope){
    string s1 = c_str();
    if(scope == CtiString::one){
        s1 = boost::regex_replace(s1, re, s, boost::match_default | boost::format_all | boost::format_first_only);
    } else {
        s1 = boost::regex_replace(s1, re, s, boost::match_default | boost::format_all);
    }
    erase();
    append(s1);
    return *this;
}

string& CtiString::replace(const boost::regex& re, string str, scopeType scope){
    return replace(re, (char *)str.c_str(), scope);
}

string& CtiString::replace(size_t beg, size_t len, const char* str){
    return string::replace(beg, len, str);
}




string& CtiString::replace(char* e, char* s, scopeType scope){
    boost::regex re(e);
    return replace(re, s, scope);
}



string CtiString::strip(stripType scope, char c) {
    if(scope == CtiString::leading){
        return trim_left(string(c_str()), char2string(c));
    } else if(scope == CtiString::trailing){
        return trim_right(string(c_str()), char2string(c));
    } else {
        return trim(string(c_str()), char2string(c));
    }

}


void CtiString::toLower(){
    std::transform(begin(), end(), begin(), ::tolower);
}
void CtiString::toUpper(){
    std::transform(begin(), end(), begin(), ::toupper);
}


size_t CtiString::index(boost::regex& re, size_t* ext, size_t i) {
    boost::smatch what;
    string t = substr(i);
    if(boost::regex_search(t, what, re))
    {
        *ext = string(what[0]).size();
        return i + what.position();
    } else {
        *ext = 0;
        return string::npos;
    }

}

size_t CtiString::index(const char* e, size_t* ext, size_t i) {
    boost::regex re;
    re.assign(e);
    return index(re, ext, i);

}

//This is only intended to accept 1 character.
//The code is defensive against multiple but not perfect.
void CtiString::padFront(size_t minLength, const char *character) {
    for( int i = size(); size() < minLength; i++ )
    {
        insert(0, character);
    }
}

//This is only intended to accept 1 character.
//The code is defensive against multiple but not perfect.
void CtiString::padEnd(size_t minLength, const char *character) {
    for( int i = size(); size() < minLength; i++ )
    {
        insert(size(), character);
    }
}


CtiString operator+(const CtiString &rs1, const CtiString &rs){
    CtiString t = rs1;
    t.append(rs.c_str());
    return t;
}


CtiString& CtiString::operator=( const CtiString& rs){
    erase();
    append(rs.c_str());
    return *this;
}


CtiString& CtiString::operator = ( const string& s){
    erase();
    append(s.c_str());
    return *this;
}


CtiString& CtiString::operator = ( const char* s){
    erase();
    append(s);
    return *this;
}

