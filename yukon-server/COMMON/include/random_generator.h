#pragma once

#include <type_traits>
#include <random>

namespace Cti {

template<typename T>
class RandomGenerator
{
    std::mt19937_64 _generator { std::random_device()() };

    using Distribution =
            typename std::conditional<
                    std::is_floating_point<T>::value,
                            std::uniform_real_distribution<T>,
                            std::uniform_int_distribution<T>>::type;

    Distribution _distribution;

public:

    RandomGenerator(T max)
        :   _distribution(0, max)
    {
    }

    operator T()
    {
        return _distribution(_generator);
    }
};

}
