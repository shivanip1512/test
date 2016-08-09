#include "precompiled.h"

#include "logger.h"
#include "fdrutility.h"
#include "std_helper.h"

#include <boost/algorithm/string/split.hpp>
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
                  is_char{';'} );

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
    return mapFind( _properties, key );
}

}
}

