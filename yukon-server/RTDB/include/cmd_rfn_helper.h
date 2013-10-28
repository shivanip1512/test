#pragma once

#include "cmd_rfn.h"
#include <string>
#include <sstream>

#include <boost/scoped_ptr.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

/**
 * Verify condition and save error description if condition is false
 */
class Condition
{
public:

    Condition( bool valid_, int errorCode_  )
        :   valid(valid_),
            errorCode(errorCode_)
    {
        if( ! valid )
        {
            description.reset( new std::ostringstream );
        }
    }

    template <typename T>
    Condition& operator<<(T & item)
    {
        return insert<T &>(item);
    }

    template <typename T>
    Condition& operator<<(const T & item)
    {
        return insert<const T &>(item);
    }

    // insert unsigned char has unsigned
    Condition& operator<<(unsigned char val)
    {
        return insert<unsigned>(val);
    }

    operator bool() const
    {
        return valid;
    }

    int getErrorCode() const
    {
        return errorCode;
    }

    std::string getErrorDescription() const
    {
        if( ! description )
        {
            return std::string();
        }

        return description->str();
    }

private:

    const bool valid;
    const int  errorCode;

    boost::scoped_ptr<std::ostringstream> description;

    template <typename T>
    inline Condition& insert( T item )
    {
        if( description )
        {
            *description << item;
        }

        return *this;
    }
};

/**
 * Throw RfnCommand::CommandException if the condition is false
 *
 * Usage Example :
 *
 * validate( Condition( parameter == validParameter, BADPARAM )
 *           << "parameter is invalid (" << parameter << ")" );
 */
inline void validate( const Condition & condition )
{
    if( ! condition )
    {
        throw RfnCommand::CommandException( condition.getErrorCode(),
                                            condition.getErrorDescription() );
    }
}


}
}
}

