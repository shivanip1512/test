#include "precompiled.h"

#include "PointValueHolder.h"
#include "std_helper.h"
#include "msg_pdata.h"


void PointValueHolder::addPointValue( const int pointId,
                                      const double pointValue,
                                      const CtiTime & pointTime,
                                      const unsigned pointQuality )
{
    _valueMap[ pointId ] = { pointValue, pointQuality, pointTime };
}

bool PointValueHolder::getPointValue( const int pointId, double & value ) const
{
    if ( auto result = Cti::mapFind( _valueMap, pointId ) )
    {
        value = result->value;
        return true;
    }
    
    return false;
}

bool PointValueHolder::getPointTime( const int pointId, CtiTime & time ) const
{
    if ( auto result = Cti::mapFind( _valueMap, pointId ) )
    {
        time = result->timestamp;
        return true;
    }

    return false;
}

PointValue PointValueHolder::getCompletePointInfo( const int pointId ) const
{
    return Cti::mapFindOrDefault( _valueMap, pointId, { 0, UnintializedQuality, CtiTime() } );
}

void PointValueHolder::updatePointValue( const CtiPointDataMsg & message )
{
    addPointValue( message.getId(), message.getValue(), message.getTime(), message.getQuality() );
}

