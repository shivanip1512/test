#include <boost/test/auto_unit_test.hpp>

#include "trx_711.h"
#include "portdecl.h"
#include "cparms.h"

BOOST_AUTO_TEST_SUITE( test_gen_reply )

int callGenReply(BYTE reply[], int size, CtiTransmitter711Info &pInfo)
{
    int address = 0, cmd = 0x00;

    YukonError_t status = GenReply(&reply[0],
                                   size,
                                   &pInfo.RemoteSequence.Request,
                                   &pInfo.RemoteSequence.Reply,
                                   address,
                                   cmd,
                                   &pInfo.SequencingBroken);

    // Neither of these should have been changed.
    BOOST_CHECK_EQUAL(address, 0);
    BOOST_CHECK_EQUAL(cmd, 0);

    return status;
}

BOOST_AUTO_TEST_CASE(test_gen_reply_sequencing_cases)
{
    CtiTransmitter711Info pInfo(TYPE_CCU711);

    BYTE reply[] = {0x7e, 0x00, 0x30, 0x0e, 0x00, /* Seq# = 0, Next seq# = 1 */
                    0x00, 0x00, 0x00, 0x00, 0x00, /* NSADJ bit _not_ flipped */
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x0a, 0xcb};

    int status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = false, SequencingBroken = false, SequencesMatch = true

       We expect the request and reply numbers to each become 1,
       sequencing broken to remain false, and a return of NORMAL.    */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, false);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 1);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 1);
    BOOST_CHECK_EQUAL(status, ClientErrors::None);

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = false, SequencingBroken = false, SequencesMatch = false

       We expect a FRAMEERR return. SequencingBroken should remain false. */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, false);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 1);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 1);
    BOOST_CHECK_EQUAL(status, ClientErrors::Framing);

    /* Pretend a timeout occurred. Increment the sequencing values manually
       and flip the SequencingBroken value to true.                     */
    pInfo.adjustSequencingForTimeout();

    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, true);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 3);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 3);

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = false, SequencingBroken = true, SequencesMatch = false

       We expect a FRAMEERR return. SequencingBroken should remain true.
       Request value should be accepted from the reply as 1.            */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, true);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 1);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 3);
    BOOST_CHECK_EQUAL(status, ClientErrors::Framing);

    reply[2] = 0x56; // set the reply number to equal 3 so our sequences match!
    reply[18] = 0x5a;
    reply[19] = 0x2a; // Fix the crc too.

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = false, SequencingBroken = true, SequencesMatch = true

       We expect a FRAMEERR return. Sequencing broken should remain true.
       Request value should be accepted from the reply as 2 this time. */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, true);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 2);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 3);
    BOOST_CHECK_EQUAL(status, ClientErrors::Framing);

    // We don't want sequencing to match here, so let's switch our reply
    // number to 4 to make that happen. We also need to enable NSADJ here.
    pInfo.RemoteSequence.Reply++;
    reply[6] |= STAT_NSADJ;
    reply[18] = 0x82;
    reply[19] = 0x3f; // Fix the crc too.

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = true, SequencingBroken = true, SequencesMatch = false

       We expect a FRAMEERR to occur here as well.                    */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, true);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 2);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 4);
    BOOST_CHECK_EQUAL(status, ClientErrors::Framing);

    // We want sequencing to match again, so decrement our reply number.
    pInfo.RemoteSequence.Reply--;

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = true, SequencingBroken = true, SequencesMatch = true

       Everything should be back on track. We expect a return of NORMAL
       and our sequencing should no longer be broken. Our reply number
       should be incremented in the GenReply function back to 4.       */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, false);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 2);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 4);
    BOOST_CHECK_EQUAL(status, ClientErrors::None);

    // Change the sequencing in the reply for the next test and
    // update the CRC.
    reply[2] = 0xde;
    reply[18] = 0x53;
    reply[19] = 0x4b;

    // Our sequences need to match, so set our reply to 7 to make that happen.
    pInfo.RemoteSequence.Reply = 7;

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = true, SequencingBroken = false, SequencesMatch = true

       Expect a return of NORMAL here. GenReply should have accepted
       what the reply message gave us as a reply number, then incremented
       it. Since the incoming reply number was 7, 7+1 mod 8 = 0. Our request
       number should have also been changed to 6 from the first 3 bits of
       reply[2].                                                      */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, false);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 6);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 0);
    BOOST_CHECK_EQUAL(status, ClientErrors::None);

    // Change our reply to something else.
    pInfo.RemoteSequence.Reply = 3;

    status = callGenReply(reply, sizeof(reply), pInfo);

    /* NSADJ = true, SequencingBroken = false, SequencesMatch = false

       Expect a return of normal and GenReply should have accepted what the
       reply message gave us as a reply number, then incremented it. We
       have a reply number of 3, the reply message says 7. Since these don't
       match, we expect GenReply to take the 7, then increment it to 0 again. */
    BOOST_CHECK_EQUAL(pInfo.SequencingBroken, false);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Request, 6);
    BOOST_CHECK_EQUAL(pInfo.RemoteSequence.Reply, 0);
    BOOST_CHECK_EQUAL(status, ClientErrors::None);
}

BOOST_AUTO_TEST_SUITE_END()
