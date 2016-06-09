#pragma once

#include "Controllable.h"


namespace Cti
{
    class RowReader;
}

class StrategyManager;



class Conductor : public Controllable
{
public:

    Conductor( StrategyManager * strategyManager = nullptr );
    Conductor( Cti::RowReader & rdr, StrategyManager * strategyManager );

    virtual ~Conductor() = default;


protected:

    Conductor( const Conductor & condutor ) = default;
    Conductor & operator=( const Conductor & rhs ) = delete;


private:

    void restoreStaticData( Cti::RowReader & rdr );

};

