#include <string>

/*
 * A few functions to trim your c++ strings
 */

template<class size_type>
inline std::basic_string<size_type>&
trim_left( std::basic_string<size_type>& str )
{
   return( str = str.substr( str.find_first_not_of( ' ' ) ) );

}

template<class size_type>
inline std::basic_string<size_type>&
trim_right( std::basic_string<size_type>& str )
{
   return( str = str.substr( 0, str.find_last_not_of( ' ' ) + 1 ) );

}

template<class size_type>
inline std::basic_string<size_type>&
trim( std::basic_string<size_type>& str )
{
   return( trim_left( trim_right( str ) ) );

}
