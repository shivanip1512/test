#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "cmd_rfn.h"

#include <boost/tuple/tuple_comparison.hpp>

using Cti::Devices::Commands::RfnCommand;
using boost::assign::list_of;

struct test_RfnCommand : RfnCommand
{
    using RfnCommand::getBytesFromTlvs;
    using RfnCommand::getTlvsFromBytes;

    typedef RfnCommand::TypeLengthValue TypeLengthValue;
    typedef RfnCommand::LongTlvList LongTlvList;
};

typedef test_RfnCommand::TypeLengthValue TypeLengthValue;

namespace std {

ostream &operator<<(ostream &o, const vector<unsigned char> &bytes);

ostream &operator<<(ostream &o, const ::TypeLengthValue &tlv)
{
    o << "{ 0x" << hex << (unsigned)tlv.type << dec << " (" << tlv.value << " ) }";

    return o;
}

ostream &operator<<(ostream &o, const vector<::TypeLengthValue> &tlvs)
{
    o << "{ " << endl;

    for each( const ::TypeLengthValue &tlv in tlvs )
    {
        o << "    " << tlv << endl;
    }

    o << "}";

    return o;
}

bool operator==(const ::TypeLengthValue &lhs, const ::TypeLengthValue &rhs)
{
    return
        boost::tie(lhs.type, lhs.value) ==
        boost::tie(rhs.type, rhs.value);
}

}

BOOST_AUTO_TEST_SUITE( test_cmd_rfn )

BOOST_AUTO_TEST_CASE(test_getBytesFromTlvs_empty)
{
    const RfnCommand::Bytes expected = list_of
        ( 0x00 );

    const std::vector<TypeLengthValue> tlvs;

    const RfnCommand::Bytes results =
        test_RfnCommand::getBytesFromTlvs(tlvs);

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getBytesFromTlvs_one_entry)
{
    const std::vector<unsigned> types = list_of
        ( 0x00 )
        ( 0x01 )
        ( 0x17 )
        ( 0xff );

    const std::vector<RfnCommand::Bytes> values = list_of<RfnCommand::Bytes>
        ( )
        ( list_of(0x00) )
        ( list_of(0x01) )
        ( list_of(0xff) )
        ( list_of(0x01)(0x02)(0x03) )
        ( list_of(0xfe)(0xdc)(0xba)(0x98)(0x76) );

    std::vector<RfnCommand::Bytes> results;

    for each(const unsigned type in types)
    {
        for each(const std::vector<unsigned char> &value in values)
        {
            results.push_back(
                test_RfnCommand::getBytesFromTlvs(
                    list_of
                        (TypeLengthValue(type, value))));
        }
    }

    const std::vector<RfnCommand::Bytes> expected = boost::assign::list_of
        (list_of(0x01)(0x00)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x01))
        (list_of(0x01)(0x00)(0x01)(0xff))
        (list_of(0x01)(0x00)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x01)(0x00))
        (list_of(0x01)(0x01)(0x01)(0x00))
        (list_of(0x01)(0x01)(0x01)(0x01))
        (list_of(0x01)(0x01)(0x01)(0xff))
        (list_of(0x01)(0x01)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x01)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x17)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x01))
        (list_of(0x01)(0x17)(0x01)(0xff))
        (list_of(0x01)(0x17)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x17)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0xff)(0x00))
        (list_of(0x01)(0xff)(0x01)(0x00))
        (list_of(0x01)(0xff)(0x01)(0x01))
        (list_of(0x01)(0xff)(0x01)(0xff))
        (list_of(0x01)(0xff)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0xff)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getBytesFromTlvs_two_entries)
{
    const std::vector<TypeLengthValue> tlvs = list_of
        ( TypeLengthValue( 0x77 ) )
        ( TypeLengthValue( 0x38, list_of(0x00) ) )
        ( TypeLengthValue( 0x14, list_of(0x01)(0x02)(0x03) ) )
        ( TypeLengthValue( 0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76) ) );

    std::vector<RfnCommand::Bytes> results;

    for each(const TypeLengthValue tlv1 in tlvs)
    {
        for each(const TypeLengthValue tlv2 in tlvs)
        {
            results.push_back(
                test_RfnCommand::getBytesFromTlvs(
                    list_of
                        (tlv1)
                        (tlv2)));
        }
    }

    const std::vector<RfnCommand::Bytes> expected = boost::assign::list_of
        (list_of(0x02)(0x77)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x77)(0x00)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x77)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x77)(0x00))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getBytesFromTlvs_up_to_four_entries)
{
    const std::vector<TypeLengthValue> tlvs = list_of
        ( TypeLengthValue( 0x77 ) )
        ( TypeLengthValue( 0x38, list_of(0x00) ) )
        ( TypeLengthValue( 0x14, list_of(0x01)(0x02)(0x03) ) )
        ( TypeLengthValue( 0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76) ) );

    std::vector<RfnCommand::Bytes> results;

    std::vector<TypeLengthValue> source_tlvs;

    for each(const TypeLengthValue tlv in tlvs)
    {
        source_tlvs.push_back(tlv);

        results.push_back(
            test_RfnCommand::getBytesFromTlvs(source_tlvs));
    }

    const std::vector<RfnCommand::Bytes> expected = boost::assign::list_of
        (list_of(0x01)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x03)(0x77)(0x00)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x04)(0x77)(0x00)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getBytesFromTlvs_up_to_four_entries_various_tlv_field_length_size)
{
    const std::vector<TypeLengthValue> tlvs = list_of
        ( TypeLengthValue( 0x77 ) )
        ( TypeLengthValue::makeLongTlv( 0x38, list_of(0x00) ) )
        ( TypeLengthValue( 0x14, list_of(0x01)(0x02)(0x03) ) )
        ( TypeLengthValue::makeLongTlv( 0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76) ) );

    std::vector<RfnCommand::Bytes> results;

    std::vector<TypeLengthValue> source_tlvs;

    for each(const TypeLengthValue tlv in tlvs)
    {
        source_tlvs.push_back(tlv);

        results.push_back(
            test_RfnCommand::getBytesFromTlvs(source_tlvs));
    }

    const std::vector<RfnCommand::Bytes> expected = boost::assign::list_of
        (list_of(0x01)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x03)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x04)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getTlvsFromBytes)
{
    const std::vector<RfnCommand::Bytes> bytes = boost::assign::list_of
        (list_of(0x00))
        (list_of(0x01)(0x00)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x01))
        (list_of(0x01)(0x00)(0x01)(0xff))
        (list_of(0x01)(0x00)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x01)(0x00))
        (list_of(0x01)(0x01)(0x01)(0x00))
        (list_of(0x01)(0x01)(0x01)(0x01))
        (list_of(0x01)(0x01)(0x01)(0xff))
        (list_of(0x01)(0x01)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x01)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x17)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x01))
        (list_of(0x01)(0x17)(0x01)(0xff))
        (list_of(0x01)(0x17)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x17)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0xff)(0x00))
        (list_of(0x01)(0xff)(0x01)(0x00))
        (list_of(0x01)(0xff)(0x01)(0x01))
        (list_of(0x01)(0xff)(0x01)(0xff))
        (list_of(0x01)(0xff)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0xff)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x77)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x77)(0x00)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x38)(0x01)(0x00)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x77)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x77)(0x00))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x38)(0x01)(0x00))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x01)(0x00))
        (list_of(0x03)(0x77)(0x00)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x04)(0x77)(0x00)(0x38)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    std::vector<std::vector<TypeLengthValue>> results;

    try
    {
        for each( RfnCommand::Bytes input in bytes )
        {
            results.push_back(
               test_RfnCommand::getTlvsFromBytes(
                  input));
        }
    }
    catch ( test_RfnCommand::CommandException &ce )
    {
        BOOST_FAIL(ce.what());
    }

    const std::vector<std::vector<TypeLengthValue>> expected = list_of<std::vector<TypeLengthValue>>
        ()
        (list_of(TypeLengthValue(0x00)))
        (list_of(TypeLengthValue(0x00, list_of(0x00))))
        (list_of(TypeLengthValue(0x00, list_of(0x01))))
        (list_of(TypeLengthValue(0x00, list_of(0xff))))
        (list_of(TypeLengthValue(0x00, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x00, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x01)))
        (list_of(TypeLengthValue(0x01, list_of(0x00))))
        (list_of(TypeLengthValue(0x01, list_of(0x01))))
        (list_of(TypeLengthValue(0x01, list_of(0xff))))
        (list_of(TypeLengthValue(0x01, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x01, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x17)))
        (list_of(TypeLengthValue(0x17, list_of(0x00))))
        (list_of(TypeLengthValue(0x17, list_of(0x01))))
        (list_of(TypeLengthValue(0x17, list_of(0xff))))
        (list_of(TypeLengthValue(0x17, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x17, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0xff)))
        (list_of(TypeLengthValue(0xff, list_of(0x00))))
        (list_of(TypeLengthValue(0xff, list_of(0x01))))
        (list_of(TypeLengthValue(0xff, list_of(0xff))))
        (list_of(TypeLengthValue(0xff, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0xff, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_getTlvsFromBytes_various_length_field_size)
{
    /*
     * TLV type - length field size expected:
     * 0x00 - 1
     * 0x01 - 2 -> Long
     * 0x17 - 1
     * 0xff - 2 -> Long
     * 0x77 - 1
     * 0x38 - 2 -> Long
     * 0x14 - 1
     * 0x89 - 2 -> Long
     *
     */

    const test_RfnCommand::LongTlvList longTlvs = boost::assign::list_of
        (0x01)
        (0xff)
        (0x38)
        (0x89);

    const std::vector<RfnCommand::Bytes> bytes = boost::assign::list_of
        (list_of(0x00))
        (list_of(0x01)(0x00)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x00))
        (list_of(0x01)(0x00)(0x01)(0x01))
        (list_of(0x01)(0x00)(0x01)(0xff))
        (list_of(0x01)(0x00)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x01)(0x00)(0x00))
        (list_of(0x01)(0x01)(0x00)(0x01)(0x00))
        (list_of(0x01)(0x01)(0x00)(0x01)(0x01))
        (list_of(0x01)(0x01)(0x00)(0x01)(0xff))
        (list_of(0x01)(0x01)(0x00)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x01)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x17)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x00))
        (list_of(0x01)(0x17)(0x01)(0x01))
        (list_of(0x01)(0x17)(0x01)(0xff))
        (list_of(0x01)(0x17)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0x17)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0xff)(0x00)(0x00))
        (list_of(0x01)(0xff)(0x00)(0x01)(0x00))
        (list_of(0x01)(0xff)(0x00)(0x01)(0x01))
        (list_of(0x01)(0xff)(0x00)(0x01)(0xff))
        (list_of(0x01)(0xff)(0x00)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x01)(0xff)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x77)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x77)(0x00)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x38)(0x00)(0x01)(0x00)(0x77)(0x00))
        (list_of(0x02)(0x38)(0x00)(0x01)(0x00)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x02)(0x38)(0x00)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x38)(0x00)(0x01)(0x00)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x77)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x02)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x77)(0x00))
        (list_of(0x02)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x02)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x02)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        (list_of(0x01)(0x77)(0x00))
        (list_of(0x02)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00))
        (list_of(0x03)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03))
        (list_of(0x04)(0x77)(0x00)(0x38)(0x00)(0x01)(0x00)(0x14)(0x03)(0x01)(0x02)(0x03)(0x89)(0x00)(0x05)(0xfe)(0xdc)(0xba)(0x98)(0x76))
        ;

    std::vector<std::vector<TypeLengthValue>> results;

    try
    {
        for each( RfnCommand::Bytes input in bytes )
        {
            results.push_back(
               test_RfnCommand::getTlvsFromBytes( 
                  input, longTlvs ));
        }
    }
    catch ( test_RfnCommand::CommandException &ce )
    {
        BOOST_FAIL(ce.what());
    }

    const std::vector<std::vector<TypeLengthValue>> expected = list_of<std::vector<TypeLengthValue>>
        ()
        (list_of(TypeLengthValue(0x00)))
        (list_of(TypeLengthValue(0x00, list_of(0x00))))
        (list_of(TypeLengthValue(0x00, list_of(0x01))))
        (list_of(TypeLengthValue(0x00, list_of(0xff))))
        (list_of(TypeLengthValue(0x00, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x00, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue::makeLongTlv(0x01)))
        (list_of(TypeLengthValue::makeLongTlv(0x01, list_of(0x00))))
        (list_of(TypeLengthValue::makeLongTlv(0x01, list_of(0x01))))
        (list_of(TypeLengthValue::makeLongTlv(0x01, list_of(0xff))))
        (list_of(TypeLengthValue::makeLongTlv(0x01, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue::makeLongTlv(0x01, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x17)))
        (list_of(TypeLengthValue(0x17, list_of(0x00))))
        (list_of(TypeLengthValue(0x17, list_of(0x01))))
        (list_of(TypeLengthValue(0x17, list_of(0xff))))
        (list_of(TypeLengthValue(0x17, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x17, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue::makeLongTlv(0xff)))
        (list_of(TypeLengthValue::makeLongTlv(0xff, list_of(0x00))))
        (list_of(TypeLengthValue::makeLongTlv(0xff, list_of(0x01))))
        (list_of(TypeLengthValue::makeLongTlv(0xff, list_of(0xff))))
        (list_of(TypeLengthValue::makeLongTlv(0xff, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue::makeLongTlv(0xff, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00))))
        (list_of(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00))))
        (list_of(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76)))(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        (list_of(TypeLengthValue(0x77)))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03))))
        (list_of(TypeLengthValue(0x77))(TypeLengthValue::makeLongTlv(0x38, list_of(0x00)))(TypeLengthValue(0x14, list_of(0x01)(0x02)(0x03)))(TypeLengthValue::makeLongTlv(0x89, list_of(0xfe)(0xdc)(0xba)(0x98)(0x76))))
        ;

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(),  results.end());
}


BOOST_AUTO_TEST_SUITE_END()
