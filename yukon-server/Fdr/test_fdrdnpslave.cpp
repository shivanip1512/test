#include <boost/test/unit_test.hpp>

#include "fdrdnpslave.h"
#include "desolvers.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_fdrdnpslave )

using Cti::Test::byte_str;

using std::vector;

struct Test_FdrDnpSlave : Cti::Fdr::DnpSlave
{
    using DnpSlave::translateSinglePoint;
    using DnpSlave::processMessageFromForeignSystem;

    std::vector<std::unique_ptr<CtiMessage>> dispatchMessages, pointMessages;

    std::unique_ptr<CtiRequestMsg> lastRequestMsg;

    std::string returnString;
    LitePoint point;

    YukonError_t writePorterConnection(CtiRequestMsg *msg, const Cti::Timing::Chrono duration) override
    {
        //  Requests to device IDs ending in 99 will time out
        if( (msg->DeviceId() % 100) == 99 )
        {
            lastRequestMsg.reset(nullptr);

            delete msg;
        }
        else
        {
            lastRequestMsg.reset(msg);
        }

        return ClientErrors::None;
    }

    std::unique_ptr<CtiReturnMsg> readPorterConnection(const Cti::Timing::Chrono duration) override
    {
        if( ! lastRequestMsg )
        {
            return nullptr;
        }

        auto returnMsg =
            std::make_unique<CtiReturnMsg>(
                    lastRequestMsg->DeviceId(),
                    lastRequestMsg->CommandString(),
                    returnString);

        returnMsg->setExpectMore(false);
        returnMsg->setUserMessageId(lastRequestMsg->UserMessageId());

        return returnMsg;
    }

    LitePoint lookupPointById(long pointId) override
    {
        return point;
    }

    bool queueMessageToDispatch(CtiMessage *aMessage) override
    {
        pointMessages.emplace_back(aMessage);

        return true;
    }

    bool sendMessageToDispatch (CtiMessage *aMessage) override
    {
        dispatchMessages.emplace_back(aMessage);

        return true;
    };

    bool isDnpDirectDeviceId(const long deviceid) const override
    {
        return deviceid > 100;
    }
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
            "05 64 05 c0 1e 00 02 00 16 67");

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

BOOST_AUTO_TEST_CASE( test_unsolicited_disable )
{
    Test_FdrDnpSlave dnpSlave;

    const byte_str request(
        "05 64 11 c4 1e 00 02 00 a1 de "
        "c0 ca 14 3c 02 06 3c 03 06 3c 04 06 14 0f");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 0a 44 02 00 1e 00 cd c7 "
        "c0 ca 81 00 01 44 e3");  //  IIN has Unsupported Function Code set

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_unsolicited_enable )
{
    Test_FdrDnpSlave dnpSlave;

    const byte_str request(
        "05 64 11 c4 1e 00 02 00 a1 de "
        "c0 ca 15 3c 02 06 3c 03 06 3c 04 06 76 cc");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 0a 44 02 00 1e 00 cd c7 "
        "c0 ca 81 00 01 44 e3");  //  IIN has Unsupported Function Code set

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_delay_measurement )
{
    Test_FdrDnpSlave dnpSlave;

    const byte_str request(
        "05 64 08 c4 1e 00 02 00 5b 89 "
        "c0 ca 17 b3 d3");  //  delay measurement

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 11 44 02 00 1e 00 80 b6 "
        "c0 ca 81 00 00 34 02 00 00 00 00 00 d3 2c");  //  0 millisecond delay

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_write_time )
{
    Test_FdrDnpSlave dnpSlave;

    const byte_str request(
        "05 64 12 c4 01 00 e8 03 2d a4 "
        "c0 c1 02 32 01 07 01 e8 a8 7e dd 61 01 70 ee");  //  write time

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 0a 44 e8 03 01 00 db ee "
        "c0 c1 81 00 00 74 2a");  //  success, no IIN bits set

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}

BOOST_AUTO_TEST_CASE( test_start_application )
{
    Test_FdrDnpSlave dnpSlave;

    const byte_str request(
        "05 64 11 c4 1e 00 02 00 a1 de "
        "c0 ca 11 5a 01 5b 01 03 00 43 4c 36 a7 0c");  //  start application "CL6"

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 0a 44 02 00 1e 00 cd c7 "
        "c0 ca 81 00 01 44 e3");  //  IIN has Unsupported Function Code set

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}



BOOST_AUTO_TEST_CASE( test_scan_request )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *sendManager = new CtiFDRManager("DNP slave send test");
    CtiFDRManager *recvManager = new CtiFDRManager("DNP slave recv test");

    CtiFDRPointList sendPointList;
    CtiFDRPointList recvPointList;

    sendPointList.setPointList(sendManager);
    recvPointList.setPointList(recvManager);

    dnpSlave.getSendToList().deletePointList();
    dnpSlave.setSendToList(sendPointList);
    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(recvPointList);

    //  CtiFDRPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    sendPointList.setPointList(nullptr);
    recvPointList.setPointList(nullptr);

    const CtiTime now;

    using FDV = vector<CtiFDRDestination>;

    auto translateFdrPoint = 
        [&, now](int pointId, int paoId, int offset, CtiPointType_t type, PointQuality_t quality, double value, bool sendList, std::string translation) 
        {
            auto fdrPoint = boost::make_shared<CtiFDRPoint>();

            fdrPoint->setPointID(pointId);
            fdrPoint->setPaoID(paoId);
            fdrPoint->setOffset(offset);
            fdrPoint->setPointType(type);
            fdrPoint->setValue(value);
            fdrPoint->setLastTimeStamp(now);
            fdrPoint->setQuality(quality);

            fdrPoint->setDestinationList( FDV { { fdrPoint->getPointID(), translation, "Test Destination" } } );

            (sendList ? sendManager : recvManager)->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

            dnpSlave.translateSinglePoint(fdrPoint, sendList);
        };

    //  Pulse Accumulator offset 17, point ID 42
    translateFdrPoint(42, 52, 17, PulseAccumulatorPointType, NormalQuality,   19, true, "MasterId:2;SlaveId:30;POINTTYPE:PulseAccumulator;Offset:1");
    translateFdrPoint(43, 53, 12, StatusPointType,           NormalQuality,    0, true, "MasterId:2;SlaveId:30;POINTTYPE:Status;Offset:2");
    translateFdrPoint(44, 54, 13, StatusPointType,           NormalQuality,    1, true, "MasterId:2;SlaveId:30;POINTTYPE:CalcStatus;Offset:3");
    //  InvalidQuality for DNP "offline" status
    translateFdrPoint(45, 55, 11, AnalogPointType,           InvalidQuality, 319, true, "MasterId:2;SlaveId:30;POINTTYPE:Analog;Offset:4");
    translateFdrPoint(46, 56, 10, AnalogPointType,           NormalQuality,  320, true, "MasterId:2;SlaveId:30;POINTTYPE:CalcAnalog;Offset:5");

    //  Receive mappings sent as Binary and Analog Outputs
    translateFdrPoint(47, 53, 16, StatusPointType, NormalQuality,   0, false, "MasterId:2;SlaveId:30;POINTTYPE:Status;Offset:6");
    translateFdrPoint(48, 56, 15, AnalogPointType, NormalQuality, 320, false, "MasterId:2;SlaveId:30;POINTTYPE:Analog;Offset:7");

    const byte_str request(
            "05 64 17 c4 1e 00 02 00 78 b5 "
            "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
            "01 06 75 e1");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 3a 44 02 00 1e 00 8e 4e "
            "c0 ca 81 00 00 1e 01 00 03 04 00 3f 01 00 00 01 51 da "
            "40 01 00 00 01 02 00 01 02 01 81 14 01 00 00 00 1b a6 "
            "01 13 00 00 00 28 01 00 06 06 01 40 01 00 00 0a 4d 11 "
            "02 00 05 05 01 71 6c");

    /*
    05 64   - header bytes
    3a  - length 58
    44  - DLC remote, DL request, FCB 0, FCB invalid?, user data
    02 00   - destination 2
    1e 00   - source 30

    c0  - transport sequence 0, first, final

    ca  - application sequence 0, first, final, sequence 10
    81  - application response
    00 00   - no IIN set

    1e 01 00 03 04  - 32-bit analog with flags, 1 byte start and stop index
        - index 3
    00  - offline
    3f 01 00 00 - value 13f = 319
        - index 4
    01  - online
    40 01 00 00 - value 140 = 320

    01 02 00 01 02  - binary input with status, 1 byte start and stop index
        - index 1
    01  - online, state 0
        - index 2
    81  - online, state 1

    14 01 00 00 00  - 32-bit counter, 1 byte start and stop index
        - index 0
    01  - online
    13 00 00 00 - value 13 = 19

    28 01 00 06 06  - 32 bit analog output with status, 1 byte start and stop index
        - index 6
    01  - online
    40 01 00 00 - value 140 = 320

    0a 02 00 05 05  - binary output with status, 1 byte start and stop index
        - index 5
    01  - online, state 0
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

    dnpSlave.getSendToList().deletePointList();
    dnpSlave.getSendToList().deletePointList();
    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    //  Pulse Accumulator offset 17, point ID 42
    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(42);
        fdrPoint->setPaoID(52);
        fdrPoint->setOffset(17);
        fdrPoint->setPointType(PulseAccumulatorPointType);
        fdrPoint->setValue(19);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:2;SlaveId:30;POINTTYPE:PulseAccumulator;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 14 c4 1e 00 02 00 28 26 "
            "c0 ca 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 93 96");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 14 44 02 00 1e 00 09 4e "
            "c0 ca 81 00 00 14 01 00 00 00 00 13 00 00 00 f8 da");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_scan_request_multiple_packet )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getSendToList().deletePointList();
    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

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
                    fdrPoint->getPointID(),
                    "MasterId:2;SlaveId:30;"
                        "POINTTYPE:" + desolvePointType(pointtype) + ";"
                        "Offset:" + std::to_string(pointoffset), "Test Destination");

            vector<CtiFDRDestination> destinationList;

            destinationList.push_back(pointDestination);

            fdrPoint->setDestinationList(destinationList);

            fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

            dnpSlave.translateSinglePoint(fdrPoint, true);
        }
    }

    const byte_str request(
            "05 64 17 c4 1e 00 02 00 78 b5 "
            "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
            "01 06 75 e1");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 2);

    auto msgItr = connection.messages.begin();

    {
        const byte_str expected(
                "05 64 ff 44 02 00 1e 00 f9 09 "
                "40 ca 81 00 00 1e 01 00 00 27 00 01 00 00 00 00 18 25 "
                "02 00 00 00 00 03 00 00 00 00 04 00 00 00 00 05 3d fd "
                "00 00 00 00 06 00 00 00 00 07 00 00 00 00 08 00 9c b1 "
                "00 00 00 09 00 00 00 00 0a 00 00 00 00 0b 00 00 52 b8 "
                "00 00 0c 00 00 00 00 0d 00 00 00 00 0e 00 00 00 42 39 "
                "00 0f 00 00 00 00 10 00 00 00 00 11 00 00 00 00 96 29 "
                "12 00 00 00 00 13 00 00 00 00 14 00 00 00 00 15 53 b2 "
                "00 00 00 00 16 00 00 00 00 17 00 00 00 00 18 00 55 70 "
                "00 00 00 19 00 00 00 00 1a 00 00 00 00 1b 00 00 a6 84 "
                "00 00 1c 00 00 00 00 1d 00 00 00 00 1e 00 00 00 20 8e "
                "00 1f 00 00 00 00 20 00 00 00 00 21 00 00 00 00 46 15 "
                "22 00 00 00 00 23 00 00 00 00 24 00 00 00 00 25 e1 63 "
                "00 00 00 00 26 00 00 00 00 27 00 00 00 00 28 00 77 7f "
                "00 00 01 02 00 00 27 80 00 80 00 80 00 80 00 80 fb fa "
                "00 80 00 80 00 80 00 80 00 80 00 80 00 80 00 80 d0 34 "
                "00 80 00 80 00 80 00 80 00 80 d6 9a");

        BOOST_CHECK_EQUAL_RANGES(expected, *msgItr);
    }
    msgItr++;
    {
        const byte_str expected(
                "05 64 d8 44 02 00 1e 00 45 26 "
                "81 00 80 00 80 00 14 01 00 00 27 00 01 00 00 00 08 51 "
                "00 02 00 00 00 00 03 00 00 00 00 04 00 00 00 00 08 a7 "
                "05 00 00 00 00 06 00 00 00 00 07 00 00 00 00 08 a9 9f "
                "00 00 00 00 09 00 00 00 00 0a 00 00 00 00 0b 00 2f db "
                "00 00 00 0c 00 00 00 00 0d 00 00 00 00 0e 00 00 da cb "
                "00 00 0f 00 00 00 00 10 00 00 00 00 11 00 00 00 d7 b0 "
                "00 12 00 00 00 00 13 00 00 00 00 14 00 00 00 00 f2 b1 "
                "15 00 00 00 00 16 00 00 00 00 17 00 00 00 00 18 c7 d0 "
                "00 00 00 00 19 00 00 00 00 1a 00 00 00 00 1b 00 e6 1a "
                "00 00 00 1c 00 00 00 00 1d 00 00 00 00 1e 00 00 2e f7 "
                "00 00 1f 00 00 00 00 20 00 00 00 00 21 00 00 00 1e 55 "
                "00 22 00 00 00 00 23 00 00 00 00 24 00 00 00 00 fc 8a "
                "25 00 00 00 00 26 00 00 00 00 27 00 00 00 00 28 75 01 "
                "00 00 00 ff ff");

        BOOST_CHECK_EQUAL_RANGES(expected, *msgItr);
    }
}

BOOST_AUTO_TEST_CASE( test_scan_request_maximum_packet )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getSendToList().deletePointList();
    dnpSlave.setSendToList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    unsigned pointid = 37;

    for( auto pointtype : { PulseAccumulatorPointType, DemandAccumulatorPointType, StatusPointType, StatusOutputPointType, AnalogPointType, AnalogOutputPointType } )
    {
        for( int pointoffset = 1; pointoffset <= 900; ++pointoffset, ++pointid )
        //  Pulse Accumulator offset 17, point ID 42
        {
            //Initialize the interface to have a point in a group.
            CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

            fdrPoint->setPointID(pointid);
            fdrPoint->setPaoID(52);
            fdrPoint->setOffset(pointoffset * 25 / 24);
            fdrPoint->setPointType(PulseAccumulatorPointType);
            fdrPoint->setValue(
                    (pointtype == StatusPointType || pointtype == StatusOutputPointType)
                        ? pointoffset % 2
                        : pointoffset);

            CtiFDRDestination pointDestination(
                    fdrPoint->getPointID(),
                    "MasterId:2;SlaveId:30;"
                        "POINTTYPE:" + desolvePointType(pointtype) + ";"
                        "Offset:" + std::to_string(pointoffset), "Test Destination");

            vector<CtiFDRDestination> destinationList;

            destinationList.push_back(pointDestination);

            fdrPoint->setDestinationList(destinationList);

            fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

            dnpSlave.translateSinglePoint(fdrPoint, true);
        }
    }

    const byte_str request(
        "05 64 17 c4 1e 00 02 00 78 b5 "
        "c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 "
        "01 06 75 e1");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 58);
}


/**
* Verify behavior of various "control close" messages sent to a point.
* Correctly-formatted requests should generate a pointdata message.
*/
BOOST_AUTO_TEST_CASE( test_control_close_receive )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);  //  <=100, not a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(false);  //  Not receive-for-control, just receive

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Close, NUL operation (0x40)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   1);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Close, pulse on (0x41)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   1);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Close, pulse off (0x42)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   1);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Pulse on, no trip/close (0x01)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   1);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Latch on, no trip/close (0x03)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   1);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }
}


/**
* Verify behavior of various "control close" messages sent to a
* non-DNP device.
* Correctly-formatted requests should generate a Dispatch
* control message.
*/
BOOST_AUTO_TEST_CASE( test_control_close_dispatch )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);  //  <=100, not a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Close, NUL operation (0x40)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Close, pulse on (0x41)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Close, pulse off (0x42)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Pulse on, no trip/close (0x01)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Latch on, no trip/close (0x03)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  1);  //  control state
    }
}


/**
 * Verify behavior of various "control close" messages sent to a
 * DNP device.
 * Correctly-formatted requests should generate a Porter request
 * message with the correct command string, and handle a DNP
 * control status in the response message.
 */
BOOST_AUTO_TEST_CASE( test_control_close_porter )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);  //  >100, a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setControlOffset(17);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);
    dnpSlave.point.setControlType(ControlType_Normal);
    dnpSlave.point.setStateZeroControl("control open");
    dnpSlave.point.setStateOneControl("control close");
    dnpSlave.point.setCloseTime1(1000);
    dnpSlave.point.setCloseTime2(1000);

    //  Close, pulse on (0x41)
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 41 01 e8 03 00 00 00 00 2e 18 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 41 01 e8 03 00 00 c5 65 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 17");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Close, pulse off (0x42)
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
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Close, NUL operation (0x01), command string missing "direct"
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 01 01 e8 03 00 00 00 00 4a a9 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 01 01 e8 03 00 00 b8 cb "
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setStateOneControl("control close direct");

    //  Close, NUL operation (0x01), command string includes "direct"
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 01 01 e8 03 00 00 00 00 4a a9 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 01 01 e8 03 00 00 b8 cb "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close direct offset 17");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    dnpSlave.point.setStateOneControl("control close");  //  change back to vanilla "control close" for the latch cases

    //  Latch on, no trip/close (0x03), does not match point control type
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
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setControlType(ControlType_Latch);

    //  Latch on, no trip/close (0x03), matches point control type
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

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 17");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }
}


/**
 * Verify behavior of various control close messages sent to a
 * DNP device configured to do SBO controls. Correctly-formatted
 * requests should generate a Porter request message with the
 * correct command string containing "sbo_selectonly" or
 * "sbo_operate", and handle a DNP control status in the
 * response message.
 */
BOOST_AUTO_TEST_CASE( test_control_close_sbo_porter )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);  //  >100, a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setControlOffset(72);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);
    dnpSlave.point.setControlType(ControlType_SBOPulse);
    dnpSlave.point.setStateZeroControl("control open");
    dnpSlave.point.setStateOneControl("control close");
    dnpSlave.point.setCloseTime1(1000);
    dnpSlave.point.setCloseTime2(1000);

    //  Close, pulse on (0x41), direct control
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
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Close, pulse on (0x41), select
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 41 01 e8 03 00 00 00 00 0e 9e "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 41 01 e8 03 00 00 c5 65 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 72 sbo_selectonly");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Close, pulse on (0x41), operate
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 04 0c 01 17 01 00 41 01 e8 03 00 00 00 00 de 26 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 41 01 e8 03 00 00 c5 65 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 72 sbo_operate");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Latch on, no trip/close (0x03), direct operate, does not match point control type
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
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Latch on, no trip/close (0x03), select, does not match point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 03 01 00 00 00 00 00 00 e4 b2 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 03 01 00 00 00 00 6d 3b "
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setControlType(ControlType_SBOLatch);

    //  Latch on, no trip/close (0x03), direct operate, matches point control type
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
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Latch on, no trip/close (0x03), select, matches point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 03 01 00 00 00 00 00 00 e4 b2 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 03 01 00 00 00 00 6d 3b "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 72 sbo_selectonly");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    //  Latch on, no trip/close (0x03), operate, matches point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 04 0c 01 17 01 00 03 01 00 00 00 00 00 00 34 0a "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 03 01 00 00 00 00 6d 3b "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control close offset 72 sbo_operate");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }
}


/**
* Verify behavior of various "control open" messages sent to a point.
* Correctly-formatted requests should generate a pointdata message.
*/
BOOST_AUTO_TEST_CASE( test_control_open_receive )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);  //  <=100, not a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(false);  //  not receive-for-control, just receive

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Trip, NUL operation (0x80)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   0);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Trip, pulse on (0x81)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   0);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Pulse off, no trip/close (0x02)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   0);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }

    dnpSlave.pointMessages.clear();

    //  Latch off, no trip/close (0x04)
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

        BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
        BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

        auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getId(),      43);
        BOOST_CHECK_EQUAL(msg->getQuality(), 5);
        BOOST_CHECK_EQUAL(msg->getValue(),   0);
        BOOST_CHECK_EQUAL(msg->getType(),    StatusPointType);
    }
}


/**
* Verify behavior of various "control open" messages sent to a
* non-DNP device.
* Correctly-formatted requests should generate a Dispatch
* control message.
*/
BOOST_AUTO_TEST_CASE( test_control_open_dispatch )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);  //  <=100, not a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    //  Trip, NUL operation (0x80)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Trip, pulse on (0x81)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Pulse off, no trip/close (0x02)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }

    dnpSlave.dispatchMessages.clear();

    //  Latch off, no trip/close (0x04)
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

        const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

        BOOST_REQUIRE(msg);

        BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::ControlRequest);

        CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
        BOOST_REQUIRE_EQUAL(opArgs.size(), 4);
        BOOST_CHECK_EQUAL(opArgs[0], -1);
        BOOST_CHECK_EQUAL(opArgs[1],  0);
        BOOST_CHECK_EQUAL(opArgs[2], 43);  //  point id
        BOOST_CHECK_EQUAL(opArgs[3],  0);  //  control state
    }
}


/**
 * Verify behavior of various "control open" messages sent to a
 * DNP device.
 * Correctly-formatted requests should generate a Porter request
 * message with the correct command string, and handle a DNP
 * control status in the response message.
 */
BOOST_AUTO_TEST_CASE( test_control_open_porter )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);  //  >100, a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setControlOffset(19);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);
    dnpSlave.point.setControlType(ControlType_Normal);
    dnpSlave.point.setStateZeroControl("control open");
    dnpSlave.point.setStateOneControl("control close");
    dnpSlave.point.setCloseTime1(1000);
    dnpSlave.point.setCloseTime2(1000);

    //  Trip, pulse on (0x81)
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 81 01 e8 03 00 00 00 00 fb 86 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 81 01 e8 03 00 00 3b da "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 19");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Trip, pulse off (0x82)
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 82 01 e8 03 00 00 00 00 cd bc "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 82 01 e8 03 00 00 6b 49 "
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Trip, NUL operation (0x80)
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 80 01 e8 03 00 00 00 00 e9 90 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 80 01 e8 03 00 00 dc 6f "
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setStateOneControl("control open");  //  change back to vanilla "control close" for the latch cases

    //  Latch off, no trip/close (0x04), does not match point control type
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
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setControlType(ControlType_Latch);

    //  Latch off, no trip/close (0x04), matches point control type
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

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 19");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }
}


/**
 * Verify behavior of various control open/trip messages sent to
 * a DNP device configured to do SBO controls.
 * Correctly-formatted requests should generate a Porter request
 * message with the correct command string containing
 * "sbo_selectonly" or "sbo_operate", and handle a DNP control
 * status in the response message.
 */
BOOST_AUTO_TEST_CASE( test_control_open_sbo_porter )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);  //  >100, a DNP deviceid (see Test_FdrDnpSlave::isDnpDirectDeviceId)
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setControlOffset(13);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);
    dnpSlave.point.setControlType(ControlType_SBOPulse);
    dnpSlave.point.setStateZeroControl("control open");
    dnpSlave.point.setStateOneControl("control close");
    dnpSlave.point.setCloseTime1(1000);
    dnpSlave.point.setCloseTime2(1000);

    //  Trip, pulse on (0x81), direct control
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 05 0c 01 17 01 00 81 01 e8 03 00 00 00 00 fb 86 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 81 01 e8 03 00 00 3b da "
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Trip, pulse on (0x81), select
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 81 01 e8 03 00 00 00 00 db 00 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 81 01 e8 03 00 00 3b da "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 13 sbo_selectonly");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Trip, pulse on (0x81), operate
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 04 0c 01 17 01 00 81 01 e8 03 00 00 00 00 0b b8 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 81 01 e8 03 00 00 3b da "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 13 sbo_operate");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    dnpSlave.lastRequestMsg.reset();

    //  Latch off, no trip/close (0x04), direct operate, does not match point control type
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
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Latch off, no trip/close (0x04), select, does not match point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 04 01 00 00 00 00 00 00 9a d0 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 04 01 00 00 00 00 53 e5 "
                "00 00 00 00 03 1d a5");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    dnpSlave.point.setControlType(ControlType_SBOLatch);

    //  Latch off, no trip/close (0x04), direct operate, matches point control type
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
                "00 00 00 00 04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());
        BOOST_CHECK( ! dnpSlave.lastRequestMsg);
    }

    //  Latch off, no trip/close (0x04), select, matches point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 03 0c 01 17 01 00 04 01 00 00 00 00 00 00 9a d0 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 04 01 00 00 00 00 53 e5 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 13 sbo_selectonly");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }

    //  Latch off, no trip/close (0x04), operate, matches point control type
    {
        const byte_str request(
                "05 64 18 c4 f6 01 e8 03 36 79 "
                "c0 c1 04 0c 01 17 01 00 04 01 00 00 00 00 00 00 4a 68 "
                "00 00 00 ff ff");

        Test_ServerConnection connection;

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 1a 44 e8 03 f6 01 20 bb "
                "c0 c1 81 00 00 0c 01 17 01 00 04 01 00 00 00 00 53 e5 "
                "00 00 00 00 00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK(dnpSlave.dispatchMessages.empty());

        BOOST_REQUIRE(dnpSlave.lastRequestMsg);
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "control open offset 13 sbo_operate");
        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->DeviceId(), 153);
    }
}


BOOST_AUTO_TEST_CASE( test_control_request_shortIndexShortQuantity )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 1a c4 f6 01 e8 03 81 5f "
            "c0 c1 05 0c 01 28 01 00 00 00 41 01 00 00 00 00 7d d2 "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1c 44 e8 03 f6 01 f9 d0 "
            "c0 c1 81 00 00 0c 01 28 01 00 00 00 41 01 00 00 85 b9 "
            "00 00 00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_visualTD_MCT )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Status;Offset:2", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 1a c4 0b 00 e8 03 77 03 "
            "c3 c2 05 0c 01 28 01 00 01 00 41 01 00 00 00 00 d3 a0 "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1c 44 e8 03 0b 00 b2 89 "
            "c0 c2 81 00 00 0c 01 28 01 00 01 00 41 01 00 00 4a be "
            "00 00 00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_visualTD_CBC )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 1a c4 0b 00 e8 03 77 03 "
            "c4 c3 05 0c 01 28 01 00 00 00 41 01 00 00 00 00 04 75 "
            "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 1c 44 e8 03 0b 00 b2 89 "
            "c0 c3 81 00 00 0c 01 28 01 00 00 00 41 01 00 00 b5 65 "
            "00 00 00 00 00 00 00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_request_controlDisabled )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(false);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

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

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:502;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, true);
    }

    const byte_str request(
            "05 64 0e c4 f6 01 e8 03 36 79 "
            "c0 c1 05 01 01 17 01 00 00 8e 4e");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    BOOST_CHECK(connection.messages.empty());
}


BOOST_AUTO_TEST_CASE(test_control_porter_timeout)
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(199);  //  a DNP device that will time out its Porter request
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(StatusPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Status;Offset:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
        "05 64 1a c4 0b 00 e8 03 77 03 "
        "c4 c3 05 0c 01 28 01 00 00 00 41 01 00 00 00 00 04 75 "
        "00 00 00 00 00 ff ff");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 1c 44 e8 03 0b 00 b2 89 "
        "c0 c3 81 00 00 0c 01 28 01 00 00 00 41 01 00 00 b5 65 "
        "00 00 00 00 00 00 04 87 26");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());
}


BOOST_AUTO_TEST_CASE( test_control_noPoints )
{
    Test_FdrDnpSlave dnpSlave;

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


BOOST_AUTO_TEST_CASE( test_analog_output_receive )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(false);  //  no controls will be sent

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Analog;Offset:1;Multiplier:2", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
        "05 64 14 c4 0b 00 e8 03 72 f2 "
        "c4 c3 05 29 01 28 01 00 00 00 01 02 03 04 00 81 7a");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
        "05 64 16 44 e8 03 0b 00 d9 35 "
        "c0 c3 81 00 00 29 01 28 01 00 00 00 01 02 03 04 44 27 "
        "00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

    BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 0);
    BOOST_REQUIRE_EQUAL(dnpSlave.pointMessages.size(), 1);

    auto msg = dynamic_cast<const CtiPointDataMsg *>(dnpSlave.pointMessages.front().get());

    BOOST_REQUIRE(msg);

    BOOST_CHECK_EQUAL(msg->getId(),      43);
    BOOST_CHECK_EQUAL(msg->getQuality(), 5);
    BOOST_CHECK_EQUAL(msg->getValue(),   134611970);
    BOOST_CHECK_EQUAL(msg->getType(),    AnalogPointType);
}


BOOST_AUTO_TEST_CASE( test_analog_output_dispatch )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(53);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Analog;Offset:1:Multiplier:3", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    const byte_str request(
            "05 64 14 c4 0b 00 e8 03 72 f2 "
            "c4 c3 05 29 01 28 01 00 00 00 01 02 03 04 00 81 7a");

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

    const byte_str expected(
            "05 64 16 44 e8 03 0b 00 d9 35 "
            "c0 c3 81 00 00 29 01 28 01 00 00 00 01 02 03 04 44 27 "
            "00 ff ff");

    BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
    BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

    BOOST_REQUIRE_EQUAL(dnpSlave.dispatchMessages.size(), 1);

    const CtiCommandMsg *msg = dynamic_cast<const CtiCommandMsg *>(dnpSlave.dispatchMessages.front().get());

    BOOST_REQUIRE(msg);

    BOOST_CHECK_EQUAL(msg->getOperation(),  CtiCommandMsg::AnalogOutput);

    CtiCommandMsg::OpArgList opArgs = msg->getOpArgList();
    BOOST_REQUIRE_EQUAL(opArgs.size(), 2);
    BOOST_CHECK_EQUAL(opArgs[0], 43);        //  point id
    BOOST_CHECK_EQUAL(opArgs[1], 67305985);  //  analog value
}


BOOST_AUTO_TEST_CASE( test_analog_output_porter_controloffset )
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);
        fdrPoint->setOffset(12);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Analog;Offset:1;Multiplier:1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setControlOffset(3);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);

    //  Success
    {
        const byte_str request(
                "05 64 14 c4 0b 00 e8 03 72 f2 "
                "c4 c3 05 29 01 28 01 00 00 00 01 02 03 04 00 81 7a");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 16 44 e8 03 0b 00 d9 35 "
                "c0 c3 81 00 00 29 01 28 01 00 00 00 01 02 03 04 44 27 "
                "00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 67305985 select pointid 43");  //  aka 0x04030201
    }

    //  Failure from device
    {
        const byte_str request(
                "05 64 14 c4 0b 00 e8 03 72 f2 "
                "c4 c3 05 29 01 28 01 00 00 00 01 02 03 04 00 81 7a");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (4): Request not accepted because a control operation is not supported for this point.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
                "05 64 16 44 e8 03 0b 00 d9 35 "
                "c0 c3 81 00 00 29 01 28 01 00 00 00 01 02 03 04 44 27 "
                "04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 67305985 select pointid 43");  //  aka 0x04030201
    }
}


BOOST_AUTO_TEST_CASE(test_analog_output_porter_analogoutput)
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);
        fdrPoint->setOffset(10017);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Analog;Offset:17;Multiplier:4.1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setPointOffset(10019);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);

    //  Success
    {
        const byte_str request(
            "05 64 14 c4 0b 00 e8 03 72 f2 "
            "c4 c3 05 29 01 28 01 00 10 00 01 02 03 04 00 45 b9");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
            "05 64 16 44 e8 03 0b 00 d9 35 "
            "c0 c3 81 00 00 29 01 28 01 00 10 00 01 02 03 04 85 5f "
            "00 ff ff");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 275954538 select pointid 43");  //  aka floor(0x04030201 * 4.1)
    }

    //  Failure from device
    {
        const byte_str request(
            "05 64 14 c4 0b 00 e8 03 72 f2 "
            "c4 c3 05 29 01 28 01 00 10 00 01 02 03 04 00 45 b9");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (4): Request not accepted because a control operation is not supported for this point.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
            "05 64 16 44 e8 03 0b 00 d9 35 "
            "c0 c3 81 00 00 29 01 28 01 00 10 00 01 02 03 04 85 5f "
            "04 87 26");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 275954538 select pointid 43");  //  aka floor(0x04030201 * 4.1)
    }
}


BOOST_AUTO_TEST_CASE(test_analog_output_porter_analogoutput_double)
{
    Test_FdrDnpSlave dnpSlave;

    CtiFDRManager *fdrManager = new CtiFDRManager("DNP slave, but this is just a test");

    CtiFDRPointList fdrPointList;

    fdrPointList.setPointList(fdrManager);

    dnpSlave.getReceiveFromList().deletePointList();
    dnpSlave.setReceiveFromList(fdrPointList);

    //  fdrPointList's destructor will try to delete the point list, but it is being used by dnpSlave - so null it out
    fdrPointList.setPointList(nullptr);

    {
        //Initialize the interface to have a point in a group.
        CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

        fdrPoint->setPointID(43);
        fdrPoint->setPaoID(153);
        fdrPoint->setOffset(10017);
        fdrPoint->setPointType(AnalogPointType);
        fdrPoint->setValue(0);
        fdrPoint->setControllable(true);

        CtiFDRDestination pointDestination(fdrPoint->getPointID(), "MasterId:1000;SlaveId:11;POINTTYPE:Analog;Offset:17;Multiplier:4.1", "Test Destination");

        vector<CtiFDRDestination> destinationList;

        destinationList.push_back(pointDestination);

        fdrPoint->setDestinationList(destinationList);

        fdrManager->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

        dnpSlave.translateSinglePoint(fdrPoint, false);
    }

    dnpSlave.point.setPointOffset(10019);
    dnpSlave.point.setPaoId(153);
    dnpSlave.point.setPointId(43);

    //  Success
    {
        const byte_str request(
            "05 64 18 c4 0b 00 e8 03 c0 25 "
            "c4 c3 05 29 04 28 01 00 10 00 4a d8 12 4d fb 21 ff f7 "
            "09 40 00 ac 80");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (0): Request accepted, initiated, or queued.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
            "05 64 1a 44 e8 03 0b 00 6b e2 "
            "c0 c3 81 00 00 29 04 28 01 00 10 00 4a d8 12 4d 8a a0 "
            "fb 21 09 40 00 31 92");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 12.880530 select pointid 43");  //  aka 3.1415926 * 4.1, to 6 digits
    }

    //  Failure from device
    {
        const byte_str request(
            "05 64 18 c4 0b 00 e8 03 c0 25 "
            "c4 c3 05 29 04 28 01 00 10 00 4a d8 12 4d fb 21 ff f7 "
            "09 40 00 ac 80");

        Test_ServerConnection connection;

        dnpSlave.returnString = "Jimmy / Control result (4): Request not accepted because a control operation is not supported for this point.";

        dnpSlave.processMessageFromForeignSystem(connection, request.char_data(), request.size());

        const byte_str expected(
            "05 64 1a 44 e8 03 0b 00 6b e2 "
            "c0 c3 81 00 00 29 04 28 01 00 10 00 4a d8 12 4d 8a a0 "
            "fb 21 09 40 04 49 4b");

        BOOST_REQUIRE_EQUAL(connection.messages.size(), 1);
        BOOST_CHECK_EQUAL_RANGES(expected, connection.messages.front());

        BOOST_CHECK_EQUAL(dnpSlave.lastRequestMsg->CommandString(), "putvalue analog value 12.880530 select pointid 43");  //  aka 3.1415926 * 4.1, to 6 digits
    }
}


BOOST_AUTO_TEST_SUITE_END()

