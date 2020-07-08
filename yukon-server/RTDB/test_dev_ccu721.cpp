#include <boost/test/unit_test.hpp>

#include "dev_ccu721.h"
#include "prot_emetcon.h"

#include "cti_asmc.h"

#include "boost_test_helpers.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_dev_ccu721 )

struct Test_Ccu721Device : Cti::Devices::Ccu721Device
{
    typedef Ccu721Device Inherited;

    typedef Inherited::byte_buffer_t byte_buffer_t;

    using Ccu721Device::writeBWord;
    using Ccu721Device::decodeEWord;
    
    void setAddress(uint8_t address) { _klondike.setAddresses(address, 127); }
};

BOOST_AUTO_TEST_CASE(test_ccu721_bword)
{
    BSTRUCT BSt;

    BSt.Address = 12345;
    BSt.DlcRoute.Amp = 1;
    BSt.DlcRoute.Bus = 1;
    BSt.DlcRoute.RepFixed = 1;
    BSt.DlcRoute.RepVar   = 1;
    BSt.DlcRoute.Stages   = 1;
    BSt.Function = 1;
    BSt.IO = Cti::Protocols::EmetconProtocol::IO_Write;
    BSt.Length = 15;
    BSt.Message[ 0] = 0x12;
    BSt.Message[ 1] = 0x23;
    BSt.Message[ 2] = 0x34;
    BSt.Message[ 3] = 0x45;
    BSt.Message[ 4] = 0x56;
    BSt.Message[ 5] = 0x67;
    BSt.Message[ 6] = 0x78;
    BSt.Message[ 7] = 0x89;
    BSt.Message[ 8] = 0x9a;
    BSt.Message[ 9] = 0xab;
    BSt.Message[10] = 0xbc;
    BSt.Message[11] = 0xcd;
    BSt.Message[12] = 0xde;
    BSt.Message[13] = 0xef;
    BSt.Message[14] = 0xf0;

    {
        Test_Ccu721Device::byte_buffer_t buf, expected;

        Test_Ccu721Device::writeBWord(buf, BSt);

        char *result = "\xa2\x10\x0c\x0e\x70\x10\x00"
                       "\xc1\x22\x33\x44\x55\x62\xb0"
                       "\xc6\x77\x88\x99\xaa\xb1\x40"
                       "\xcb\xcc\xdd\xee\xff\x01\xb0";

        expected.assign(reinterpret_cast<unsigned char *>(result),
                        reinterpret_cast<unsigned char *>(result) + 7 * 4);

        BOOST_CHECK_EQUAL_COLLECTIONS(
           expected.begin(), expected.end(),
           buf.begin(), buf.end());
    }

    BSt.Length = 0;

    {
        Test_Ccu721Device::byte_buffer_t buf, expected;

        Test_Ccu721Device::writeBWord(buf, BSt);

        char *result = "\xa2\x10\x0c\x0e\x40\x13\x50";

        expected.assign(reinterpret_cast<unsigned char *>(result),
                        reinterpret_cast<unsigned char *>(result) + 7);

        BOOST_CHECK_EQUAL_COLLECTIONS(
           expected.begin(), expected.end(),
           buf.begin(), buf.end());
    }
}


BOOST_AUTO_TEST_CASE(test_ccu721_decode_eword)
{
    {
        const char *e_word = "\xee\x00\x00\x20\x00\x02\xb0";
        ESTRUCT ESt;

        BOOST_CHECK_EQUAL(ClientErrors::Word1NackPadded, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7, &ESt));
    }

    {
        const char *e_word = "\xee\x00\x00\x20\x00\x02\xc0";
        ESTRUCT ESt;

        BOOST_CHECK_EQUAL(ClientErrors::BadBch, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7, &ESt));
    }

    {
        const char *e_word = "\xfe\x00\x00\x20\x00\x02\x60";
        ESTRUCT ESt;

        BOOST_CHECK_EQUAL(ClientErrors::BadWordType, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7, &ESt));
    }

    {
        const char *e_word = "\xed\x11\x23\x45\x00\x03\xc0";
        ESTRUCT ESt;

        BOOST_CHECK_EQUAL(ClientErrors::EWordReceived, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7, &ESt));
    }
}


bool findAllMessages(void *, void *)
{
    return true;
}


bool findRequestId(void *request_id, void *om)
{
    return om && (unsigned long)request_id == ((OUTMESS *)om)->Request.GrpMsgID;
}


BOOST_AUTO_TEST_CASE(test_ccu721_queue_handler_find_all)
{
    Test_Ccu721Device test_ccu721;

    OUTMESS om;

    om.Request.GrpMsgID = 112358;
    om.TargetID = 17;  //  so that the CCU knows it's targeted at an MCT

    //  verify the queues start out empty
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 0);

    BOOST_CHECK(!test_ccu721.hasWaitingWork());
    BOOST_CHECK(!test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    unsigned device_queue_count;
    OUTMESS *om_queued = &om;

    YukonError_t retval = test_ccu721.queueOutMessageToDevice(om_queued, &device_queue_count);

    BOOST_CHECK_EQUAL(retval, ClientErrors::QueuedToDevice);
    BOOST_CHECK_EQUAL(device_queue_count, 1);
    BOOST_CHECK( ! om_queued);

    //  verify it got in there with the correct requestid
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 1);

    BOOST_CHECK(test_ccu721.hasWaitingWork());
    BOOST_CHECK(test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    //  pull out all OMs
    auto entries = test_ccu721.retrieveQueueEntries(findAllMessages, NULL);

    //  verify the OM is no longer in there
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 0);

    BOOST_CHECK(test_ccu721.hasWaitingWork());  //  as of now, the klondike portion doesn't get erased, just the CCU's record of it
    BOOST_CHECK(!test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    //  and that we got the right OM back
    BOOST_CHECK_EQUAL(entries.size(), 1);
    BOOST_CHECK_EQUAL(entries.front(), &om);
}


BOOST_AUTO_TEST_CASE(test_ccu721_queue_handler_find_requestid)
{
    Test_Ccu721Device test_ccu721;

    OUTMESS om;

    om.Request.GrpMsgID = 112358;
    om.TargetID = 17;  //  so that the CCU knows it's targeted at an MCT

    //  verify the queues start out empty
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 0);

    BOOST_CHECK(!test_ccu721.hasWaitingWork());
    BOOST_CHECK(!test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    unsigned device_queue_count;
    OUTMESS *om_queued = &om;

    YukonError_t retval = test_ccu721.queueOutMessageToDevice(om_queued, &device_queue_count);

    BOOST_CHECK_EQUAL(retval, ClientErrors::QueuedToDevice);
    BOOST_CHECK_EQUAL(device_queue_count, 1);
    BOOST_CHECK( ! om_queued);

    //  verify it got in there with the correct requestid
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 1);

    BOOST_CHECK(test_ccu721.hasWaitingWork());
    BOOST_CHECK(test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    std::vector<OUTMESS*> entries;

    //  try to grab a bogus requestID
    entries = test_ccu721.retrieveQueueEntries(findRequestId, (void *)111111);

    //  verify our OM is still in there
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 1);

    BOOST_CHECK(test_ccu721.hasWaitingWork());
    BOOST_CHECK(test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    BOOST_CHECK_EQUAL(entries.size(), 0);

    //  grab the right requestID
    entries = test_ccu721.retrieveQueueEntries(findRequestId, (void *)112358);

    //  verify the OM is no longer in there
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(0), 0);
    BOOST_CHECK_EQUAL(test_ccu721.getRequestCount(112358), 0);

    BOOST_CHECK(test_ccu721.hasWaitingWork());  //  as of now, the klondike portion doesn't get erased, just the CCU's record of it
    BOOST_CHECK(!test_ccu721.hasQueuedWork());
    BOOST_CHECK(!test_ccu721.hasRemoteWork());

    //  and that we got the right OM back
    BOOST_CHECK_EQUAL(entries.size(), 1);
    BOOST_CHECK_EQUAL(entries.front(), &om);
}


BOOST_AUTO_TEST_CASE(test_ccu721_dtran_reject)
{
    Test_Ccu721Device test_ccu721;

    test_ccu721.setAddress(3);

    OUTMESS om;

    om.EventCode = DTRAN | BWORD;
    om.Priority = 7;

    om.Buffer.BSt.Address = 2906827;
    om.Buffer.BSt.DlcRoute.RepVar = 7;
    om.Buffer.BSt.DlcRoute.RepFixed = 31;
    om.Buffer.BSt.DlcRoute.Stages = 0;
    om.Buffer.BSt.DlcRoute.Bus = 0;
    om.Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Read;
    om.Buffer.BSt.Function = 0x90;
    om.Buffer.BSt.Length = 3;

    test_ccu721.recvCommRequest(&om);

    CtiXfer xfer;

    //  Generate an IDLC reset
    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 5);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 0);

    Cti::Test::byte_str idlc_reset =
        "7e 07 1f 39 aa";

    BOOST_CHECK_EQUAL_COLLECTIONS(
        idlc_reset.begin(), idlc_reset.end(),
        xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_REQUIRE_EQUAL(test_ccu721.isTransactionComplete(), false);

    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 0);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 5);

    Cti::Test::byte_str idlc_reset_ack =
        "7e 06 73 8b 1a";

    std::copy(
        idlc_reset_ack.bytes.begin(),
        idlc_reset_ack.bytes.end(),
        xfer.getInBuffer());
    xfer.setInCountActual(idlc_reset_ack.bytes.size());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK_EQUAL(test_ccu721.isTransactionComplete(), false);

    //  Generate the Check Status
    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 7);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 0);

    Cti::Test::byte_str check_status =
        "7e 07 10 01 11 ba 36";

    BOOST_CHECK_EQUAL_COLLECTIONS(
        check_status.begin(), check_status.end(),
        xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_REQUIRE_EQUAL(test_ccu721.isTransactionComplete(), false);

    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 0);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 5);

    Cti::Test::byte_str check_status_ack =
        "7e 06 11 07"
        " 81"  //  response command
        " 11"  //  requested command
        " 00 00"   //  status bytes
        " 08"      //  slots available
        " 90 75"   //  expected sequence
        " 2b 01";  //  crc

    std::copy(
        check_status_ack.bytes.begin(),
        check_status_ack.bytes.end(),
        xfer.getInBuffer());
    xfer.setInCountActual(check_status_ack.bytes.size());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK_EQUAL(test_ccu721.isTransactionComplete(), false);

    //  Generate the DTRAN request
    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 19);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 0);

    Cti::Test::byte_str out_expected =
        "7e 07 30 0d"
        " 01"
        " 90 75"
        " 01"
        " 00 07"
        " af fb 16 b2 d9 0c d0"
        " 97 61";

    BOOST_CHECK_EQUAL_COLLECTIONS(
        out_expected.begin(), out_expected.end(), 
        xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK_EQUAL(test_ccu721.isTransactionComplete(), false);

    test_ccu721.generate(xfer);

    BOOST_CHECK_EQUAL(xfer.getOutCount(), 0);
    BOOST_CHECK_EQUAL(xfer.getInCountExpected(), 5);

    Cti::Test::byte_str inbound =
        "7e 06 32 07 c1 01 40 00 02 92 75 4b 24";

    std::copy(
        inbound.bytes.begin(),
        inbound.bytes.end(),
        xfer.getInBuffer());
    xfer.setInCountActual(inbound.bytes.size());

    BOOST_CHECK_EQUAL(test_ccu721.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK_EQUAL(test_ccu721.isTransactionComplete(), true);

    INMESS inmess;

    test_ccu721.sendCommResult(inmess);

    BOOST_CHECK_EQUAL(inmess.ErrorCode, ClientErrors::BadSequence);
}

BOOST_AUTO_TEST_SUITE_END()
