#pragma once

#include "random_generator.h"
#include "types.h"

namespace Cti {
namespace Simulator {

class Logger;

class Behavior
{
public:

    Behavior(double probability)
        :   _probability(probability)
    {
    }

protected:

    bool behaviorOccurs()
    {
        return _chance < _probability;
    }

private:

    double _probability;
    RandomGenerator<double> _chance { 100.0 };
};

}
}
