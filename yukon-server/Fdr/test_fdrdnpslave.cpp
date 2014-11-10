#include <boost/test/unit_test.hpp>

#include "fdrdnpslave.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_fdrdnpslave )

using Cti::byte_buffer;

using std::vector;

struct Test_FdrDnpSlave : Cti::Fdr::DnpSlave
{
    using DnpSlave::translateSinglePoint;
    using DnpSlave::processMessageFromForeignSystem;
};

struct Test_ServerConnection : Cti::Fdr::ServerConnection
{
    std::vector<int> message;

    bool queueMessage(char *buf, unsigned len, int priority)
    {
        std::vector<unsigned char> tmp(buf, buf + len);

        message.assign(tmp.begin(), tmp.end());

        return true;
    }

    virtual std::string getName()     const  {  return "test server connection";  }
    virtual int getConnectionNumber() const  {  return 42;  }
    virtual int getPortNumber() { return 42;  }
};

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

    byte_buffer request;
    request << 0x05, 0x64, 0x17, 0xc4, 0x1e, 0x00, 0x02, 0x00, 0x78, 0xb5,
               0xc0, 0xca, 0x01, 0x32, 0x01, 0x06, 0x3c, 0x02, 0x06, 0x3c,
               0x03, 0x06, 0x3c, 0x04, 0x06, 0x3c, 0x9d, 0xf5, 0x01, 0x06,
               0x75, 0xe1;

    Test_ServerConnection connection;

    dnpSlave.processMessageFromForeignSystem(connection, request.data_as<char>(), request.size());

    byte_buffer response;
    response <<  0x05, 0x64, 0x2a, 0x44, 0x02, 0x00, 0x1e, 0x00, 0x4f, 0x36,
                 0xc0, 0xca, 0x81, 0x00, 0x00, 0x1e, 0x01, 0x28, 0x01, 0x00,
                 0x03, 0x00, 0x00, 0x3f, 0x01, 0x00, 0xd9, 0xf6, 0x00, 0x01,
                 0x02, 0x28, 0x01, 0x00, 0x01, 0x00, 0x00, 0x14, 0x01, 0x28,
                 0x01, 0x00, 0x00, 0x00, 0x57, 0xa8, 0x00, 0x13, 0x00, 0x00,
                 0x00, 0x99, 0x52;

    vector<int> expected(response.begin(), response.end());

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

    BOOST_CHECK_EQUAL_COLLECTIONS(
        connection.message.begin(),
        connection.message.end(),
        expected.begin(),
        expected.end());
}


BOOST_AUTO_TEST_SUITE_END()
