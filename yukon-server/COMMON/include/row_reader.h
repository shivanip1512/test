#pragma once

#include "ctitime.h"
#include "dlldefs.h"
#include <string>
#include <boost/date_time/posix_time/posix_time_types.hpp>

namespace Cti {

class RowReader
{
private:
    void*  operator new   (size_t){return NULL;};
    void*  operator new[] (size_t){return NULL;};
    void   operator delete   (void *){};
    void   operator delete[] (void *){};

public:

    ~RowReader(){};

    virtual bool isValid() = 0;
    virtual bool execute() = 0;

    // Checks if the current index is null.
    // For example: rdr["test"].isNull();
    virtual bool isNull() = 0;
    virtual bool operator()() = 0;

    virtual bool setCommandText(const std::string &command) = 0;
    virtual RowReader &operator[](const char *columnName) = 0;
    virtual RowReader &operator[](const std::string &columnName) = 0;
    virtual RowReader &operator[](int columnNumber) = 0;

protected:

    using boost_ptime = boost::posix_time::ptime;

private:

    //  Make these private so we are the only ones who can do the typecast
    virtual operator bool()            = 0;
    virtual operator short()           = 0;
    virtual operator unsigned short()  = 0;
    virtual operator long()            = 0;
    virtual operator int()             = 0;
    virtual operator unsigned()        = 0;
    virtual operator unsigned char()   = 0;
    virtual operator unsigned long()   = 0;
    virtual operator long long()       = 0;
    virtual operator double()          = 0;
    virtual operator float()           = 0;
    virtual operator CtiTime()         = 0;
    virtual operator boost_ptime()     = 0;
    virtual operator std::string()     = 0;

    virtual RowReader &extractChars(char *destination, unsigned count) = 0;

    virtual void incrementColumnIndex() = 0;

public:
    template<typename T>
    RowReader& operator>>(T& operand)
    {
        operand = as<T>();
        incrementColumnIndex();
        return *this;
    }

    template<unsigned N>
    RowReader &operator>>(char (&operand)[N])
    {
        extractChars(operand, N);
        incrementColumnIndex();
        return *this;
    }

    template<typename T>
    T as()
    {
        //  Explicitly call the conversion operator.
        //  This ensures the template will fail if operator T is not defined.
        return operator T();  
    }

    //  Check if the column's string representation is a case-insensitive match to the template character.
    //  Null column always returns false.
    template<char Ch>
    bool is()
    {
        if( isNull() )
        {
            return false;
        }

        const auto str = as<std::string>();

        return str.size() == 1 && std::tolower(str[0]) == Ch;
    }

    bool isNotNull() 
    { 
        return ! isNull(); 
    }

    //  prevent implicit bool casts
    RowReader &operator<<(const bool operand) = delete;

    // inputs for variable binding
    virtual RowReader &operator<<(const short operand) = 0;
    virtual RowReader &operator<<(const unsigned short operand) = 0;
    virtual RowReader &operator<<(const long operand) = 0;
    virtual RowReader &operator<<(const INT operand) = 0;
    virtual RowReader &operator<<(const UINT operand) = 0;
    virtual RowReader &operator<<(const unsigned long operand) = 0;
    virtual RowReader &operator<<(const long long operand) = 0;
    virtual RowReader &operator<<(const double operand) = 0;
    virtual RowReader &operator<<(const float operand) = 0;
    virtual RowReader &operator<<(const CtiTime &operand) = 0;
    virtual RowReader &operator<<(const boost::posix_time::ptime &operand) = 0;
    virtual RowReader &operator<<(const std::string &operand) = 0;
    virtual RowReader &operator<<(const char *operand) = 0;
};

}// namespace cti
