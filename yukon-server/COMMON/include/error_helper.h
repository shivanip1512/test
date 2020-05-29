#pragma once

#include "yukon.h"

#include "Exceptions.h"

#include <string>
#include <sstream>
#include <variant>

namespace Cti {

template <typename Value>
class ErrorOr
{
public:
    ErrorOr(Value v) noexcept
        : _state{ v }
    {}

    ErrorOr(YukonError_t e) noexcept
        : _state{ e }
    {}

    YukonError_t error() const noexcept
    {
        const auto err = std::get_if<YukonError_t>(&_state);

        return err
            ? *err
            : ClientErrors::None;
    }

    operator bool() const noexcept
    {
        return std::holds_alternative<Value>(_state);
    }

    const Value* value() const noexcept
    {
        return std::get_if<Value>(&_state);
    }

    operator const Value*() const noexcept
    {
        return std::get_if<Value>(&_state);
    }

    const Value* operator->() const noexcept
    {
        return std::get_if<Value>(&_state);
    }

    Value value_or(Value default_value) const
    {
        const auto val = value();

        return val
            ? *val
            : default_value;
    }

    template <typename Mapper, typename Output=std::result_of_t<Mapper(Value)>>
    ErrorOr<Output> map(Mapper m) const
    {
        if( const auto err = error() )
        {
            return err;
        }
        //  Dereference pointer to allow Mapper to take by reference
        return m(*value());
    }

    template <typename Output, typename FlatMapper>
    ErrorOr<Output> flatMap(FlatMapper m) const
    {
        if( const auto err = error() )
        {
            return err;
        }
        //  Dereference pointer to allow FlatMapper to take by reference
        return m(*value());
    }

    template <typename Consumer, typename ErrorHandler>
    YukonError_t consumer(Consumer c, ErrorHandler eh)
    {
        if( const auto err = error() )
        {
            eh(err);

            return err;
        }

        //  Dereference pointer to allow Consumer to take by reference
        c(*value());

        return ClientErrors::None;
    }

private:
    const std::variant<Value, YukonError_t> _state;
};

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

