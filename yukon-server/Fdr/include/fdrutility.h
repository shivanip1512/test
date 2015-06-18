#pragma once


#include "dlldefs.h"

#include <boost/optional.hpp>

#include <string>
#include <map>


namespace Cti
{
namespace Fdr
{

class IM_EX_FDRBASE Translation
{
public:

    using PropertyKey   = std::string;
    using PropertyValue = std::string;
    using OptionalValue = boost::optional<PropertyValue>;

    using PropertyCollection = std::map<PropertyKey, PropertyValue>;

    Translation( const std::string & input );

    OptionalValue operator[]( const PropertyKey & key ) const;

private:

    PropertyCollection  _properties;
};

}
}

