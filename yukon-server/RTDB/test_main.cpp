#define BOOST_TEST_MAIN

#include "dev_mct.h"
#include "cmd_rfn.h"

#include <boost/tuple/tuple.hpp>

#include <boost/test/unit_test.hpp>

#include "test_main.hpp"

using Cti::Devices::Commands::RfnCommand;

struct test_MctDevice : Cti::Devices::MctDevice
{
    using Cti::Devices::MctDevice::ReadDescriptor;
    using Cti::Devices::MctDevice::value_locator;
};

namespace std {

//  Override to get BOOST_CHECK_EQUAL_COLLECTIONS to print unsigned char as integer
ostream &operator<<( ostream &os, const unsigned char &uc )
{
    return os << static_cast<unsigned>(uc);
}

ostream &operator<<(ostream &o, const std::vector<unsigned char> &bytes)
{
    o << "{";

    const unsigned char oldfill = o.fill('0');
    const ios::fmtflags oldformat = o.setf(ios_base::hex, ios::basefield);

    int idx = 0;

    for( unsigned char b : bytes )
    {
        if( idx++ )
        {
            o << ", ";
        }

        o << "0x" << setw(2) << (unsigned)b;
    }

    o.setf(oldformat, ios::basefield);
    o.fill(oldfill);

    o << "}";

    return o;
}

ostream &operator<<(ostream &o, const std::vector<bool> &v)
{
    o << "{";

    int idx = 0;

    for( bool b : v )
    {
        if( idx++ )
        {
            o << ", ";
        }

        o << b;
    }

    o << "}";

    return o;
}

ostream& operator<<( ostream& out, const vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd)
{
    out << "{" << rd.size();
    for each(const boost::tuples::tuple<unsigned, unsigned, int> &d in rd)
    {
        out << ",(" << d.get<0>() << ":" << d.get<1>() << "=" << d.get<2>() << ")";
    }
    out << "}";
    return out;
}

//  For test_dev_mct410, 420, 470
ostream& operator<<( ostream& out, const test_MctDevice::ReadDescriptor &rd)
{
    //  The commented lines produce a tuple_list_of(xx,yy,zz) that can be pasted into the test cases

    out << "{" << rd.size();
    //out << "(tuple_list_of";
    for each(const test_MctDevice::ReadDescriptor::value_type &d in rd)
    {
        out << ",(" << d.offset << ":" << d.length << "=" << d.key << ")";
        //out << "(" << d.offset << "," << d.length << "," << d.key << ")";
    }
    out << "}";
    //out << ")";
    return out;
}

// for test_cmd_rfn_*
ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex )
{
    return os << "{(" << ex.error_code << "):" << ex.error_description << "}";
}

//  For test_dev_mct410, 420, 470
bool operator==(const test_MctDevice::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs)
{
    return
        lhs.offset == rhs.get<0>() &&
        lhs.length == rhs.get<1>() &&
        lhs.key    == rhs.get<2>();
}

bool operator==(const boost::tuples::tuple<unsigned, unsigned, int> &lhs, const test_MctDevice::value_locator &rhs)
{
    return
        lhs.get<0>() == rhs.offset &&
        lhs.get<1>() == rhs.length &&
        lhs.get<2>() == rhs.key;
}

}


namespace boost {
namespace test_tools {

//  For test_dev_mct410, 420, 470
bool operator!=(const test_MctDevice::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs)
{
    if( lhs.size() != rhs.size() )
    {
        return true;
    }

    return ! std::equal(lhs.begin(), lhs.end(), rhs.begin());
}

bool operator!=(const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &lhs, const test_MctDevice::ReadDescriptor &rhs)
{
    if( lhs.size() != rhs.size() )
    {
        return true;
    }

    return ! std::equal(lhs.begin(), lhs.end(), rhs.begin());
}

// for test_cmd_rfn_*
bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs )
{
    return lhs.error_code != rhs.error_code || lhs.error_description != rhs.error_description;
}

}
}

