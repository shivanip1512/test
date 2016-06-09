#include "precompiled.h"

#include "Conductor.h"
#include "database_reader.h"



Conductor::Conductor( StrategyManager * strategyManager )
    :   Controllable( strategyManager )
{

}


Conductor::Conductor( Cti::RowReader & rdr, StrategyManager * strategyManager )
    :   Controllable( rdr, strategyManager )
{
    restoreStaticData( rdr );

}


void Conductor::restoreStaticData( Cti::RowReader & rdr )
{


}

