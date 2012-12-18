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

    virtual RowReader &operator>>(bool &operand) = 0;
    virtual RowReader &operator>>(short &operand) = 0;
    virtual RowReader &operator>>(unsigned short &operand) = 0;
    virtual RowReader &operator>>(long &operand) = 0;
    virtual RowReader &operator>>(INT &operand) = 0;
    virtual RowReader &operator>>(UINT &operand) = 0;
    virtual RowReader &operator>>(UCHAR &operand) = 0;
    virtual RowReader &operator>>(unsigned long &operand) = 0;
    virtual RowReader &operator>>(__int64 &operand) = 0;
    virtual RowReader &operator>>(double &operand) = 0;
    virtual RowReader &operator>>(float &operand) = 0;
    virtual RowReader &operator>>(CtiTime &operand) = 0;
    virtual RowReader &operator>>(boost::posix_time::ptime &operand) = 0;
    virtual RowReader &operator>>(std::string &operand) = 0;
    template<unsigned N>
    RowReader &operator>>(char (&operand)[N])
    {
        return extractChars(operand, N);
    }
    virtual RowReader &extractChars(char *destination, unsigned count) = 0;

    // inputs for variable binding
    virtual RowReader &operator<<(const bool operand) = 0;
    virtual RowReader &operator<<(const short operand) = 0;
    virtual RowReader &operator<<(const unsigned short operand) = 0;
    virtual RowReader &operator<<(const long operand) = 0;
    virtual RowReader &operator<<(const INT operand) = 0;
    virtual RowReader &operator<<(const UINT operand) = 0;
    virtual RowReader &operator<<(const unsigned long operand) = 0;
    virtual RowReader &operator<<(const __int64 operand) = 0;
    virtual RowReader &operator<<(const double operand) = 0;
    virtual RowReader &operator<<(const float operand) = 0;
    virtual RowReader &operator<<(const CtiTime &operand) = 0;
    virtual RowReader &operator<<(const boost::posix_time::ptime &operand) = 0;
    virtual RowReader &operator<<(const std::string &operand) = 0;
    virtual RowReader &operator<<(const char *operand) = 0;
};

}// namespace cti
