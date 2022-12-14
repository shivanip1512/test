#pragma once


#include "dlldefs.h"
#include "utility.h"

#include <boost/optional.hpp>
#include <boost/algorithm/string/predicate.hpp>

#include <string>
#include <map>


namespace Cti
{
namespace Fdr
{

class IM_EX_FDRBASE Translation
{
public:
    DEBUG_INSTRUMENTATION;

    using PropertyKey   = std::string;
    using PropertyValue = std::string;
    using OptionalValue = boost::optional<PropertyValue>;

    struct KeyIgnoreCase   // case-insensitive key comparisons
    {
        bool operator()( const PropertyKey & lhs, const PropertyKey & rhs ) const
        {
            return boost::algorithm::ilexicographical_compare( lhs, rhs );
        }
    };

    using PropertyCollection = std::map<PropertyKey, PropertyValue, KeyIgnoreCase>;

    Translation( const std::string & input );

    OptionalValue operator[]( const PropertyKey & key ) const;

private:

    PropertyCollection  _properties;
};

}
}

