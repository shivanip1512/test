#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "Attribute.h"
#include <chrono>

#include "test_main.hpp"

namespace std {
    ostream &operator<<(ostream& os, const chrono::minutes& m)
    {
        return os << m.count() << "min";
    }
}

std::ostream &operator<<(std::ostream& os, const Attribute &a)
{
    return os << a.getName();
}
