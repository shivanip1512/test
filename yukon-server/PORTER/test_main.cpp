#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "test_main.hpp"

//  hack to get BOOST_CHECK_EQUAL_COLLECTIONS to print unsigned char as integer
namespace std {

ostream &operator<<( ostream &os, const unsigned char &uc ) {
    return os << static_cast<unsigned>(uc);
}

}

