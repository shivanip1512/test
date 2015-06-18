#include "precompiled.h"

#include "logger.h"
#include "fdrutility.h"

#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/predicate.hpp>
#include <boost/algorithm/string/classification.hpp>


namespace Cti
{
namespace Fdr
{

Translation::Translation( const std::string & input )
{
    std::vector<std::string>    tokenList;

    boost::split( tokenList,
                  input,
                  boost::algorithm::is_any_of(";") );

    for ( const auto & token : tokenList )
    {
        if ( ! token.empty() )
        {
            const auto index = token.find_first_of(':');

            if ( 0 < index && index < token.size() - 1 )
            {
                _properties.emplace( PropertyKey( token, 0, index ),
                                     PropertyValue( token, index + 1 ) );
            }
            else
            {
                CTILOG_ERROR( dout, "Skipping malformed translation entry: [" << token << "]" );
            }
        }
    }
}

Translation::OptionalValue Translation::operator[]( const Translation::PropertyKey & key ) const
{
    for ( const auto & property : _properties )
    {
        if ( boost::algorithm::iequals( property.first, key ) )     // case-insensitive matching
        {
            return property.second;
        }
    }

    return boost::none;
}

}
}

