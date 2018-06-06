#pragma once

#include "yukon.h"

#include <string>
#include <sstream>

namespace Cti {

/**
 * Verify condition and save error description if condition is false
 */
class Condition
{
public:

    Condition( bool valid_, YukonError_t errorCode_  )
        :   valid(valid_),
            errorCode(errorCode_)
    {
        if( ! valid )
        {
            description = std::make_unique<StreamBuffer>();
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

    // insert unsigned char as unsigned
    Condition& operator<<(unsigned char val)
    {
        return insert<unsigned>(val);
    }

    operator bool() const
    {
        return valid;
    }

    YukonError_t getErrorCode() const
    {
        return errorCode;
    }

    std::string getErrorDescription() const
    {
        if( ! description )
        {
            return {};
        }

        return *description;
    }

private:

    const bool valid;
    const YukonError_t errorCode;

    std::unique_ptr<StreamBuffer> description;

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
 * Throw Command::CommandException if the condition is false
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
        throw YukonErrorException { 
                    condition.getErrorCode(),
                    condition.getErrorDescription() };
    }
}


}

