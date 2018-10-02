#include "precompiled.h"

#include "NestLoadShapingOptions.h"
#include "logger.h"
#include "std_helper.h"



namespace NestLoadShaping
{

PreparationOption::Cache    PreparationOption::_lookup;

PreparationOption::PreparationOption( const std::string & name )
    :   _name( name ),
        _value( _lookup.size() )
{
    _lookup[ _name ] = this;
}

const PreparationOption & PreparationOption::Lookup( const std::string & name )
{
    if ( auto option = Cti::mapFind( _lookup, name ) )
    {
        return **option;
    }

    CTILOG_ERROR( dout, "Invalid Load Shaping Preparation Option: " << name << ". Defaulting to: " << Standard.getName() );

    return Standard;
}

const PreparationOption
    PreparationOption::Standard { "STANDARD"    },
    PreparationOption::None     { "NONE"        },
    PreparationOption::Ramping  { "RAMPING"     };


///


PeakOption::Cache    PeakOption::_lookup;

PeakOption::PeakOption( const std::string & name )
    :   _name( name ),
        _value( _lookup.size() )
{
    _lookup[ _name ] = this;
}

const PeakOption & PeakOption::Lookup( const std::string & name )
{
    if ( auto option = Cti::mapFind( _lookup, name ) )
    {
        return **option;
    }

    CTILOG_ERROR( dout, "Invalid Load Shaping Peak Option: " << name << ". Defaulting to: " << Standard.getName() );

    return Standard;
}

const PeakOption
    PeakOption::Standard  { "STANDARD"    },
    PeakOption::Uniform   { "UNIFORM"     },
    PeakOption::Symmetric { "SYMMETRIC"   };


///


PostOption::Cache    PostOption::_lookup;

PostOption::PostOption( const std::string & name )
    :   _name( name ),
        _value( _lookup.size() )
{
    _lookup[ _name ] = this;
}

const PostOption & PostOption::Lookup( const std::string & name )
{
    if ( auto option = Cti::mapFind( _lookup, name ) )
    {
        return **option;
    }

    CTILOG_ERROR( dout, "Invalid Load Shaping Peak Option: " << name << ". Defaulting to: " << Standard.getName() );

    return Standard;
}

const PostOption
    PostOption::Standard { "STANDARD"    },
    PostOption::Ramping  { "RAMPING"     };

}

