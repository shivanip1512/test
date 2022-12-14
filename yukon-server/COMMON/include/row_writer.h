#pragma once

#include "ctitime.h"
#include "dlldefs.h"
#include "loggable.h"

#include <boost/optional.hpp>

#include <string>

namespace boost {
namespace posix_time {
    class ptime;  //  forward declaration, no need to include ptime here
}
}

namespace Cti {

class RowWriter
{
private:
    void*  operator new   (size_t){return NULL;};
    void*  operator new[] (size_t){return NULL;};
    void   operator delete   (void *){};
    void   operator delete[] (void *){};

public:

    ~RowWriter(){};
    virtual bool execute() = 0;

    enum SpecialValues
    {
        Null
    };

    virtual RowWriter &operator<<(const SpecialValues operand) = 0;
    virtual RowWriter &operator<<(const bool operand) = 0;
    virtual RowWriter &operator<<(const short operand) = 0;
    virtual RowWriter &operator<<(const unsigned short operand) = 0;
    virtual RowWriter &operator<<(const long operand) = 0;
    virtual RowWriter &operator<<(const INT operand) = 0;
    virtual RowWriter &operator<<(const UINT operand) = 0;
    virtual RowWriter &operator<<(const unsigned long operand) = 0;
    virtual RowWriter &operator<<(const long long operand) = 0;
    virtual RowWriter &operator<<(const double operand) = 0;
    virtual RowWriter &operator<<(const float operand) = 0;
    virtual RowWriter &operator<<(const CtiTime &operand) = 0;
    virtual RowWriter &operator<<(const boost::posix_time::ptime &operand) = 0;
    virtual RowWriter &operator<<(const std::string &operand) = 0;
    virtual RowWriter &operator<<(const char *operand) = 0;

    template<typename T>
    RowWriter &operator<<(const boost::optional<T> &operand)
    {
        if (operand)
        {
            return *this << *operand;
        }

        return *this << Null;
    }
};

struct RowSource : Loggable
{
    virtual void fillRowWriter(RowWriter&) const = 0;
};

}// namespace cti
