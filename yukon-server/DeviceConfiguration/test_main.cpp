#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "PointAttribute.h"
#include <chrono>

namespace std {
    ostream &operator<<(ostream& os, const chrono::minutes& m)
    {
        return os << m.count() << "minutes";
    }
}

std::ostream &operator<<(std::ostream& os, const Attribute &a)
{
    return os << a.getName();
}
