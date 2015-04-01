#include <boost/test/unit_test.hpp>

#include "fdrdnpslave.h"
#include "desolvers.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_fdrdnpslave )

using Cti::Test::byte_str;

using std::vector;

struct Test_FdrDnpSlave : Cti::Fdr::DnpSlave
{
    using DnpSlave::translateSinglePoint;
    using DnpSlave::processMessageFromForeignSystem;

    boost::ptr_vector<CtiMessage> dispatchMessages;

    bool sendMessageToDispatch (CtiMessage *aMessage) override
    {
        dispatchMessages.push_back(aMessage);

        return true;
    };
};

struct Test_ServerConnection : Cti::Fdr::ServerConnection
{
    std::vector<std::vector<unsigned>> messages;

    bool queueMessage(char *buf, unsigned len, int priority)
    {
        auto unsigned_buf = reinterpret_cast<const unsigned char *>(buf);

        messages.emplace_back(unsigned_buf, unsigned_buf + len);

        delete [] buf;

        return true;
    }

    virtual std::string getName()     const  {  return "test server connection";  }
    virtual int getConnectionNumber() const  {  return 42;  }
    virtual int getPortNumber() { return 42;  }
};

BOOST_AUTO_TEST_CASE( test_datalink_request )
{
    Test_FdrDnpSlave dnpSlave;

    /*
    05 64   - header bytes
    05  - length 5
    c9  - DLC master, DL request, FCB 0, FCB invalid?, link status
    02 00   - destination 2
    1e 00   - source 30
    */
    const byte_str request(
            "05 64 05 c9 1e 00 02 00 59 11");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    /*
    05 64   - header bytes
    05  - length 5
    0b  - DLC remote, DL response, link status response
    1e 00   - destination 30
    02 00   - source 2
    */
    const byte_str expected(
            "05 64 05 0b 02 00 1e 00 ce 0f");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_datalink_reset )
{
    Test_FdrDnpSlave dnpSlave;

    /*
    05 64   - header bytes
    05  - length 5
    c0  - DLC master, DL request, FCB 0, FCB invalid?, reset link
    02 00   - destination 2
    1e 00   - source 30
    */
    const byte_str request(
            "05 64 05 c0 1e 00 02 00 43 6e");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    /*
    05 64   - header bytes
    05  - length 5
    0b  - DLC remote, DL response, ACK
    1e 00   - destination 30
    02 00   - source 2
    */
    const byte_str expected(
            "05 64 05 00 02 00 1e 00 8d 3f");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_getMessageSize )
{
    Test_FdrDnpSlave dnpSlave;

    {
        const byte_str packet(
                "05 64 05 c9 1e 00 02 00 59 11");

        BOOST_CHECK_EQUAL(dnpSlave.getMessageSize(packet.char_data()), 10);
    }

    {
        const byte_str packet(  //  10 + 18 + 4
                "05 64 17 c4 1e 00 02 00 78 b5 "
                "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
                "01 06 75 e1");

        BOOST_CHECK_EQUAL(dnpSlave.getMessageSize(packet.char_data()), 32);
    }

    {
        const byte_str packet(  //  10 + 18 + 18 + 7
                "05 64 2a 44 02 00 1e 00 4f 36 "
                "c0 ca 81 00 00 1e 01 28 01 00 03 00 00 3f 01 00 d9 f6 "
                "00 01 02 28 01 00 01 00 00 14 01 28 01 00 00 00 57 a8 "
                "00 13 00 00 00 99 52");

        BOOST_CHECK_EQUAL(dnpSlave.getMessageSize(packet.char_data()), 53);
    }
}

BOOST_AUTO_TEST_CASE( test_scan_request )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    //  Pulse Accumulator offset 17, point ID 42
    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(42);
        fdrPoint->setPaoID(52);
        fdrPoint->setOffset(17);
        fdrPoint->setPointType(PulseAccumulatorPointType);
        fdrPoint->setValue(19);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:2;SlaveId:30;POINTTYPE:PulseAccumulator;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:2;SlaveId:30;POINTTYPE:Status;Offset:2", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(44);
        fdrPoint->setPaoID(54);
        fdrPoint->setOffset(11);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(319);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:2;SlaveId:30;POINTTYPE:Analog;Offset:4", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 17 c4 1e 00 02 00 78 b5 "
            "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
            "01 06 75 e1");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 2a 44 02 00 1e 00 4f 36 "
            "c0 ca 81 00 00 1e 01 28 01 00 03 00 00 3f 01 00 d9 f6 "
            "00 01 02 28 01 00 01 00 00 14 01 28 01 00 00 00 57 a8 00 13 00 00 "
            "00 99 52");

    /*
    05 64   - header bytes
    27  - length 39
    44  - DLC remote, DL request, FCB 0, FCB invalid?, user data
    02 00   - destination 2
    1e 00   - source 30

    c0  - transport sequence 0, first, final

    ca  - application sequence 0, first, final, sequence 10
    81  - application response
    0f  - class 0, 1, 2, 3, all stations
    00  - no other IIN set

    1e 01  28 01 00 - 32-bit analog with flags, 2 byte count, 2 byte index, 1 object
    00 03 - index 3
    00 - no flags
    3f 01 00 00 - value 13f = 319

    01 02  28 01 00 - binary input with status, 2 byte count, 2 byte index, 1 object
    00 01 - index 1
    00 - state 0

    14 01  28 01 00 - 32-bit counter, 2 byte count, 2 byte index, 1 object
    00 00 - index 0
    00 - no flags
    13 00 00 00 - value 19
    */

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_scan_request_class1230 )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    //  Pulse Accumulator offset 17, point ID 42
    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(42);
        fdrPoint->setPaoID(52);
        fdrPoint->setOffset(17);
        fdrPoint->setPointType(PulseAccumulatorPointType);
        fdrPoint->setValue(19);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:2;SlaveId:30;POINTTYPE:PulseAccumulator;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 14 c4 1e 00 02 00 28 26 "
            "c0 ca 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 93 96");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 16 44 02 00 1e 00 be 68 "
            "c0 ca 81 00 00 14 01 28 01 00 00 00 00 13 00 00 00 1c "
            "00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_scan_request_multiple_packet )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    unsigned pointid = 37;

    for( auto pointtype : { PulseAccumulatorPointType, StatusPointType, AnalogPointType } )
    {
        for( int pointoffset = 1; pointoffset <= 40; ++pointoffset, ++pointid )
        //  Pulse Accumulator offset 17, point ID 42
        {
            //Initialize the interface to have a point in a group.
            CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

            fdrPoint->setPointID(pointid);
            fdrPoint->setPaoID(52);
            fdrPoint->setOffset(pointoffset);
            fdrPoint->setPointType(PulseAccumulatorPointType);
            fdrPoint->setValue(
                    pointtype == StatusPointType
                        ? pointoffset % 2
                        : pointoffset);

            CtiFDRDestination pointDestination(
                    fdrPoint.get(),
                    "MasterId:2;SlaveId:30;"
                        "POINTTYPE:" + desolvePointType(pointtype) + ";"
                        "Offset:" + std::to_string(pointoffset), "Test Destination");

            vector<CtiFDRDestination> destinationList;

            destinationList.push_back(pointDestination);

            fdrPoint->setDestinationList(destinationList);

            fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

            dnpSlave.translateSinglePoint(fdrPoint, true);
        }
    }

    const byte_str request(
            "05 64 17 c4 1e 00 02 00 78 b5 "
            "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
            "01 06 75 e1");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 3);

    auto msgItr = connection.messages.begin();

    {
        const byte_str expected(
                "05 64 ff 44 02 00 1e 00 f9 09 "
                "40 ca 81 00 00 1e 01 28 28 00 00 00 00 01 00 00 d9 a9 "
                "00 01 00 00 02 00 00 00 02 00 00 03 00 00 00 03 9c 16 "
                "00 00 04 00 00 00 04 00 00 05 00 00 00 05 00 00 f7 ce "
                "06 00 00 00 06 00 00 07 00 00 00 07 00 00 08 00 3d eb "
                "00 00 08 00 00 09 00 00 00 09 00 00 0a 00 00 00 fe e4 "
                "0a 00 00 0b 00 00 00 0b 00 00 0c 00 00 00 0c 00 a3 a5 "
                "00 0d 00 00 00 0d 00 00 0e 00 00 00 0e 00 00 0f 31 62 "
                "00 00 00 0f 00 00 10 00 00 00 10 00 00 11 00 00 0a 44 "
                "00 11 00 00 12 00 00 00 12 00 00 13 00 00 00 13 f6 fc "
                "00 00 14 00 00 00 14 00 00 15 00 00 00 15 00 00 c6 5c "
                "16 00 00 00 16 00 00 17 00 00 00 17 00 00 18 00 31 cb "
                "00 00 18 00 00 19 00 00 00 19 00 00 1a 00 00 00 0f 9d "
                "1a 00 00 1b 00 00 00 1b 00 00 1c 00 00 00 1c 00 8d 4c "
                "00 1d 00 00 00 1d 00 00 1e 00 00 00 1e 00 00 1f 52 7d "
                "00 00 00 1f 00 00 20 00 00 00 20 00 00 21 00 00 9f b0 "
                "00 21 00 00 22 00 00 00 22 00 5b 4f");

        BOOST_CHECK_EQUAL_RANGES(expected, *msgItr);
    }
    msgItr++;
    {
        const byte_str expected(
                "05 64 ff 44 02 00 1e 00 f9 09 "
                "01 00 23 00 00 00 23 00 00 24 00 00 00 24 00 00 b6 c6 "
                "25 00 00 00 25 00 00 26 00 00 00 26 00 00 27 00 d3 10 "
                "00 00 27 00 00 28 00 00 00 01 02 28 28 00 00 00 a2 0b "
                "80 01 00 00 02 00 80 03 00 00 04 00 80 05 00 00 50 eb "
                "06 00 80 07 00 00 08 00 80 09 00 00 0a 00 80 0b db 2d "
                "00 00 0c 00 80 0d 00 00 0e 00 80 0f 00 00 10 00 4b fb "
                "80 11 00 00 12 00 80 13 00 00 14 00 80 15 00 00 6c b3 "
                "16 00 80 17 00 00 18 00 80 19 00 00 1a 00 80 1b dd 22 "
                "00 00 1c 00 80 1d 00 00 1e 00 80 1f 00 00 20 00 93 4c "
                "80 21 00 00 22 00 80 23 00 00 24 00 80 25 00 00 28 5b "
                "26 00 80 27 00 00 14 01 28 28 00 00 00 00 01 00 7f d9 "
                "00 00 01 00 00 02 00 00 00 02 00 00 03 00 00 00 96 58 "
                "03 00 00 04 00 00 00 04 00 00 05 00 00 00 05 00 7c d0 "
                "00 06 00 00 00 06 00 00 07 00 00 00 07 00 00 08 6f ea "
                "00 00 00 08 00 00 09 00 00 00 09 00 00 0a 00 00 43 77 "
                "00 0a 00 00 0b 00 00 00 0b 00 d0 cd");

        BOOST_CHECK_EQUAL_RANGES(expected, *msgItr);
    }
    msgItr++;
    {
        const byte_str expected(
                "05 64 cf 44 02 00 1e 00 ba 80 "
                "82 00 0c 00 00 00 0c 00 00 0d 00 00 00 0d 00 00 47 8d "
                "0e 00 00 00 0e 00 00 0f 00 00 00 0f 00 00 10 00 13 24 "
                "00 00 10 00 00 11 00 00 00 11 00 00 12 00 00 00 4b 07 "
                "12 00 00 13 00 00 00 13 00 00 14 00 00 00 14 00 1a 38 "
                "00 15 00 00 00 15 00 00 16 00 00 00 16 00 00 17 5f d4 "
                "00 00 00 17 00 00 18 00 00 00 18 00 00 19 00 00 17 4f "
                "00 19 00 00 1a 00 00 00 1a 00 00 1b 00 00 00 1b c3 89 "
                "00 00 1c 00 00 00 1c 00 00 1d 00 00 00 1d 00 00 62 b3 "
                "1e 00 00 00 1e 00 00 1f 00 00 00 1f 00 00 20 00 36 f7 "
                "00 00 20 00 00 21 00 00 00 21 00 00 22 00 00 00 58 8d "
                "22 00 00 23 00 00 00 23 00 00 24 00 00 00 24 00 11 4e "
                "00 25 00 00 00 25 00 00 26 00 00 00 26 00 00 27 fa f5 "
                "00 00 00 27 00 00 28 00 00 00 41 12");

        BOOST_CHECK_EQUAL_RANGES(expected, *msgItr);
    }
}


BOOST_AUTO_TEST_CASE( test_control_close )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Close, NUL operation
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 40 01 00 00 00 00 00 00 96 bf "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 40 01 00 00 00 00 40 06 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Close, pulse on
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 41 01 e8 03 00 00 00 00 2e 18 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 41 01 e8 03 00 00 c5 65 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Close, pulse off
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 42 01 e8 03 00 00 00 00 18 22 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 42 01 e8 03 00 00 95 f6 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Pulse on, no trip/close
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 01 01 00 00 00 00 00 00 e0 18 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 01 01 00 00 00 00 da 1d "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Latch on, no trip/close
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 03 01 00 00 00 00 00 00 c4 34 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 03 01 00 00 00 00 6d 3b "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }
}


BOOST_AUTO_TEST_CASE( test_control_open )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Trip, NUL operation
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 80 01 00 00 00 00 00 00 43 21 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 80 01 00 00 00 00 be b9 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Trip, pulse on
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 81 01 00 00 00 00 00 00 51 37 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 81 01 00 00 00 00 59 0c "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Pulse of, no trip/close
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 02 01 00 00 00 00 00 00 d6 22 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 02 01 00 00 00 00 8a 8e "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Latch off, no trip/close
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 04 01 00 00 00 00 00 00 ba 56 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 04 01 00 00 00 00 53 e5 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

        const CtiCommandMsg &msg = dynamic_cast<const CtiCommandMsg &>(dnpSlave.dispatchMessages.front());

        CtiCommandMsg::OpArgList opArgs = msg.getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }
}


BOOST_AUTO_TEST_CASE( test_control_request_shortIndexShortQuantity )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 18 c4 f6 01 e8 03 36 79 "
            "c0 c1 05 0c 01 28 00 01 00 00 41 01 00 00 00 00 de cf "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1a 44 e8 03 f6 01 20 bb "
            "c0 c1 81 00 00 0c 01 17 01 00 41 01 00 00 00 00 a7 b3 "
            "00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_visualTD_MCT )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:11;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 1a c4 0b 00 e8 03 77 03 "
            "c3 c2 05 0c 01 28 01 00 01 00 41 01 00 00 00 00 d3 a0 "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1a 44 e8 03 0b 00 6b e2 "
            "c0 c2 81 00 00 0c 01 17 01 01 41 01 00 00 00 00 ed 5e "
            "00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_visualTD_CBC )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:11;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 1a c4 0b 00 e8 03 77 03 "
            "c4 c3 05 0c 01 28 01 00 00 00 41 01 00 00 00 00 04 75 "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1a 44 e8 03 0b 00 6b e2 "
            "c0 c3 81 00 00 0c 01 17 01 00 41 01 00 00 00 00 97 6f "
            "00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_controlDisabled )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(false);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 18 c4 f6 01 e8 03 36 79 "
            "c0 c1 05 0c 01 17 01 00 41 01 00 00 00 00 00 00 84 a9 "
            "00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1a 44 e8 03 f6 01 20 bb "
            "c0 c1 81 00 00 0c 01 17 01 00 41 01 00 00 00 00 a7 b3 "
            "00 00 00 00 04 87 26");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_invalidObject )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(0);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint.get(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:0", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().insert(std::make_pair(fdrPoint->getPointID(), fdrPoint));

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 0e c4 f6 01 e8 03 36 79 "
            "c0 c1 05 01 01 17 01 00 00 8e 4e");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    BOOST_CHECK(connection.messages.empty());
}


BOOST_AUTO_TEST_CASE( test_control_noPoints )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    const byte_str request(
            "05 64 18 c4 f6 01 e8 03 36 79 "
            "c0 c1 05 0c 01 17 01 00 41 01 00 00 00 00 00 00 84 a9 "
            "00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1a 44 e8 03 f6 01 20 bb "
            "c0 c1 81 00 00 0c 01 17 01 00 41 01 00 00 00 00 a7 b3 "
            "00 00 00 00 04 87 26");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_SUITE_END()
