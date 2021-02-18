#include <boost/test/unit_test.hpp>

#include "dnp_transport.h"

#include "boostutil.h"

using std::vector;

BOOST_AUTO_TEST_SUITE( test_dnp_transport )

struct test_Transport : private Cti::Protocols::DNP::TransportLayer
{
    using Cti::Protocols::DNP::TransportLayer::packet_sequence_t;

    using Cti::Protocols::DNP::TransportLayer::isPacketSequenceValid;
    using Cti::Protocols::DNP::TransportLayer::extractPayload;
};

using Cti::Protocols::DNP::Transport::TransportPacket;

BOOST_AUTO_TEST_CASE(test_dnp_transport_isPacketSequenceValid_no_packets)
{
    test_Transport::packet_sequence_t packet_sequence;

    BOOST_CHECK_EQUAL(false, test_Transport::isPacketSequenceValid(packet_sequence));
}

BOOST_AUTO_TEST_CASE(test_dnp_transport_isPacketSequenceValid_one_packet)
{
    TransportPacket packets[] =
    {
        TransportPacket(0x01, 0, 0),
        TransportPacket(0x42, 0, 0),
        TransportPacket(0x83, 0, 0),
        TransportPacket(0xc4, 0, 0),
    };

    const int packet_count = sizeof(packets) / sizeof(packets[0]);

    const bool _ = false, X = true;

    bool expected[packet_count] =
    {
        _, _, _, X
    };

    std::vector<bool> results;

    for( int i = 0; i < packet_count; ++i )
    {
        test_Transport::packet_sequence_t packet_sequence;

        packet_sequence.insert(packets[i]);

        results.push_back(test_Transport::isPacketSequenceValid(packet_sequence));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected, expected + packet_count,
       results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_isPacketSequenceValid_two_packets)
{
    TransportPacket packets[] =
    {
        TransportPacket(0x09, 0, 0),  //   9
        TransportPacket(0x8b, 0, 0),  //  11, final
        TransportPacket(0x4a, 0, 0),  //  10, first
        TransportPacket(0xcc, 0, 0),  //  12, first and final
    };

    const int packet_count = sizeof(packets) / sizeof(packets[0]);

    const bool _ = false, X = true;

    const bool sequence_valid[packet_count * packet_count] =
    {
        //  This matrix encodes the packets and the order in which they were added to the set.
        //  It's valid to have packet 3 and packet 2 added, or packet 4 added twice.
        _, _, _, _,
        _, _, X, _,
        _, X, _, _,
        _, _, _, X,
    };

    std::vector<bool> results;

    for( int i = 0; i < packet_count; ++i )
    {
        for( int j = 0; j < packet_count; ++j )
        {
            test_Transport::packet_sequence_t packet_sequence;

            packet_sequence.insert(packets[i]);
            packet_sequence.insert(packets[j]);

            results.push_back(test_Transport::isPacketSequenceValid(packet_sequence));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       sequence_valid, sequence_valid + packet_count * packet_count,
       results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_isPacketSequenceValid_three_packets)
{
    TransportPacket packets[] =
    {
        TransportPacket(0x12, 0, 0),  //  18
        TransportPacket(0x93, 0, 0),  //  19, final
        TransportPacket(0x51, 0, 0),  //  17, first
        TransportPacket(0xd4, 0, 0),  //  20, first and final
    };

    const int packet_count = sizeof(packets) / sizeof(packets[0]);

    const bool _ = false, X = true;

    const bool sequence_valid[packet_count * packet_count * packet_count] =
    {
        //  This matrix encodes the packets and the order in which they were added to the set.
        _, _, _, _,
        _, _, X, _,
        _, X, _, _,
        _, _, _, _,

        _, _, X, _,
        _, _, _, _,
        X, _, _, _,
        _, _, _, _,

        _, X, _, _,
        X, _, _, _,
        _, _, _, _,
        _, _, _, _,

        _, _, _, _,
        _, _, _, _,
        _, _, _, _,
        _, _, _, X,
    };

    std::vector<bool> results;

    for( int i = 0; i < packet_count; ++i )
    {
        for( int j = 0; j < packet_count; ++j )
        {
            for( int k = 0; k < packet_count; ++k )
            {
                test_Transport::packet_sequence_t packet_sequence;

                packet_sequence.insert(packets[i]);
                packet_sequence.insert(packets[j]);
                packet_sequence.insert(packets[k]);

                results.push_back(test_Transport::isPacketSequenceValid(packet_sequence));
            }
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       sequence_valid, sequence_valid + packet_count * packet_count * packet_count,
       results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_isPacketSequenceValid_wraparound)
{
    //  try from 1 to 5 packets - can't go to 64 since next_permutation() is O(N!) below
    for( int packet_count = 1; packet_count <= 5; ++packet_count )
    {
        //  start the first packet at all 64 possibilities
        for( int start_index = 0; start_index < 64; ++start_index )
        {
            vector<TransportPacket> packets;

            for( int i = 0; i < packet_count; ++i )
            {
                //  make the start value
                unsigned char header = (start_index + i) & 0x3f;

                //  first
                if( i == 0 )
                {
                    header |= 0x40;
                }
                //  final
                if( i == packet_count - 1 )
                {
                    header |= 0x80;
                }

                packets.push_back(TransportPacket(header, 0, 0));
            }

            //  start out sorted
            std::sort(packets.begin(), packets.end());

            //  and go through every possible permutation of inserting the packets
            while( std::next_permutation(packets.begin(), packets.end()) )
            {
                test_Transport::packet_sequence_t packet_sequence;

                //  don't insert the first packet
                for( int i = 1; i < packet_count; ++i )
                {
                    packet_sequence.insert(packets[i]);

                    //  verify that it's continuously not valid without the first packet
                    BOOST_CHECK_EQUAL(false, test_Transport::isPacketSequenceValid(packet_sequence));
                }

                //  now insert the first packet
                packet_sequence.insert(packets[0]);

                //  and verify that it's valid
                BOOST_CHECK_EQUAL(true, test_Transport::isPacketSequenceValid(packet_sequence));
            }
        }
    }
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_extractPayload_no_packets)
{
    test_Transport::packet_sequence_t packet_sequence;

    const vector<unsigned char> expected;
    const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

    BOOST_CHECK_EQUAL(expected.size(), result.size());

    BOOST_CHECK(std::equal(expected.begin(),
                           expected.end(),
                           result.begin()));
}

BOOST_AUTO_TEST_CASE(test_dnp_transport_extractPayload_one_packet)
{
    const std::string expected_string = "This is the expected string.";
    const std::vector<unsigned char> expected(expected_string.begin(),
                                              expected_string.end());

    test_Transport::packet_sequence_t packet_sequence;

    packet_sequence.insert(TransportPacket(true, 99, (const unsigned char *)expected_string.c_str(),
                                                     expected_string.size()));

    const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

    BOOST_CHECK_EQUAL(expected.size(), result.size());

    BOOST_CHECK(std::equal(expected.begin(),
                           expected.end(),
                           result.begin()));
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_extractPayload_two_packets)
{
    const std::string expected_string = "This is the expected string.";
    const std::vector<unsigned char> expected(expected_string.begin(),
                                              expected_string.end());

    const int packet_count = 2;

    //  normal case
    {
        test_Transport::packet_sequence_t packet_sequence;

        for( int i = 0; i < packet_count; ++i )
        {
            const char *chunk_begin = expected_string.c_str() + expected_string.size() * i / packet_count;
            const char *chunk_end   = expected_string.c_str() + expected_string.size() * (i + 1) / packet_count;

            packet_sequence.insert(TransportPacket(!i, i, (const unsigned char *)chunk_begin, chunk_end - chunk_begin));
        }

        const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

        BOOST_CHECK_EQUAL(expected.size(), result.size());

        BOOST_CHECK(std::equal(expected.begin(),
                               expected.end(),
                               result.begin()));
    }

    //  wraparound case
    {
        test_Transport::packet_sequence_t packet_sequence;

        for( int i = 0; i < packet_count; ++i )
        {
            const char *chunk_begin = expected_string.c_str() + expected_string.size() * i / packet_count;
            const char *chunk_end   = expected_string.c_str() + expected_string.size() * (i + 1) / packet_count;

            packet_sequence.insert(TransportPacket(!i, (i + 63) % 64, (const unsigned char *)chunk_begin, chunk_end - chunk_begin));
        }

        const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

        BOOST_CHECK_EQUAL(expected.size(), result.size());

        BOOST_CHECK(std::equal(expected.begin(),
                               expected.end(),
                               result.begin()));
    }
}


BOOST_AUTO_TEST_CASE(test_dnp_transport_extractPayload_three_packets)
{
    const std::string expected_string = "This is the expected string.";
    const std::vector<unsigned char> expected(expected_string.begin(),
                                              expected_string.end());

    const int packet_count = 3;

    //  normal case
    {
        test_Transport::packet_sequence_t packet_sequence;

        for( int i = 0; i < packet_count; ++i )
        {
            const char *chunk_begin = expected_string.c_str() + expected_string.size() * i / packet_count;
            const char *chunk_end   = expected_string.c_str() + expected_string.size() * (i + 1) / packet_count;

            packet_sequence.insert(TransportPacket(!i, i % 64, (const unsigned char *)chunk_begin, chunk_end - chunk_begin));
        }

        const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

        BOOST_CHECK_EQUAL(expected.size(), result.size());

        BOOST_CHECK(std::equal(expected.begin(),
                               expected.end(),
                               result.begin()));
    }

    //  wraparound case
    {
        test_Transport::packet_sequence_t packet_sequence;

        for( int i = 0; i < packet_count; ++i )
        {
            const char *chunk_begin = expected_string.c_str() + expected_string.size() * i / packet_count;
            const char *chunk_end   = expected_string.c_str() + expected_string.size() * (i + 1) / packet_count;

            packet_sequence.insert(TransportPacket(!i, (i + 63) % 64, (const unsigned char *)chunk_begin, chunk_end - chunk_begin));
        }

        const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

        BOOST_CHECK_EQUAL(expected.size(), result.size());

        BOOST_CHECK(std::equal(expected.begin(),
                               expected.end(),
                               result.begin()));
    }

    //  wraparound case
    {
        test_Transport::packet_sequence_t packet_sequence;

        for( int i = 0; i < packet_count; ++i )
        {
            const char *chunk_begin = expected_string.c_str() + expected_string.size() * i / packet_count;
            const char *chunk_end   = expected_string.c_str() + expected_string.size() * (i + 1) / packet_count;

            packet_sequence.insert(TransportPacket(!i, (i + 62) % 64, (const unsigned char *)chunk_begin, chunk_end - chunk_begin));
        }

        const vector<unsigned char> result = test_Transport::extractPayload(packet_sequence);

        BOOST_CHECK_EQUAL(expected.size(), result.size());

        BOOST_CHECK(std::equal(expected.begin(),
                               expected.end(),
                               result.begin()));
    }
}


BOOST_AUTO_TEST_SUITE_END()
