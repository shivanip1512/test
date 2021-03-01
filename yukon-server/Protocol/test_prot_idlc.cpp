#include <boost/test/unit_test.hpp>

#include "prot_idlc.h"
#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_prot_idlc )

struct Test_IDLC : Cti::Protocols::IDLC
{
    Test_IDLC()
    {
        // Zeroing the sequence numbers allows the call
        //  to generate() to take the right path to the bug
        clearSequenceNumbers( test_tag );
    }
};

BOOST_AUTO_TEST_CASE( test_idlc_long_message_length )
{
    Test_IDLC   idlc;
    CtiXfer     xfer;

    // A message, that combined with the 6 byte IDLC header, exceeds the 255 bytes in length
    std::vector<unsigned char>  buffer( 251, 'a' );

    // Prep the protocol for sending
    idlc.send( buffer );

    // The call to generate() fills the xfer object.  Internally it computes the CRC for the message
    //  which is where the bug was happening.  The length parameter passed to the CRC function was
    //  overflowing and causing the function to access invalid memory, resulting in an exception.
    idlc.generate( xfer );

    // We are now longer than 255 bytes
    BOOST_CHECK_EQUAL( 257, xfer.getOutCount() );

    // Validate our CRC of 0xa4c7
    BOOST_CHECK_EQUAL( 0xa4, *(xfer.getOutBuffer() + 255) );
    BOOST_CHECK_EQUAL( 0xc7, *(xfer.getOutBuffer() + 256) );
}

BOOST_AUTO_TEST_SUITE_END()
