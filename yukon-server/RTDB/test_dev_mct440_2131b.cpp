#include <boost/test/unit_test.hpp>

#include "dev_mct440_2131b.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;

using std::string;
using std::vector;

struct test_Mct440_2131BDevice : Cti::Devices::Mct440_2131BDevice
{
    test_Mct440_2131BDevice(int type, const string &name)
    {
        setType(type);
        _name = name;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;

    using Mct440_213xBDevice::decodeGetValueInstantLineData;

    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }
};

struct test_Mct440_2131B : test_Mct440_2131BDevice
{
    test_Mct440_2131B() : test_Mct440_2131BDevice(TYPEMCT440_2131B, "Test MCT-440-2131B") {}
};

namespace std {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& out, const test_Mct440_2131BDevice::ReadDescriptor &rd);
    std::ostream& operator<<(std::ostream& out, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct440_2131BDevice::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct440_2131BDevice::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

BOOST_AUTO_TEST_CASE(test_mct440_2131b_decodeGetValueInstantLineData)
{
    INMESS                         InMessage;
    CtiTime                        t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList  vgList;
    CtiDeviceBase::CtiMessageList  retList;
    CtiDeviceBase::OutMessageList  outList; // not use


    unsigned char test_data[4] = {0x1,0x2,0x3,0x64};

    memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_InstantLineData;

    test_Mct440_2131B test_dev;

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueInstantLineData(InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 3);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // phase A Line Volage

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 16);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // phase A Line Current

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 515);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // phase A Power Factor

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 1.0);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // phase B Line Volage
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 80);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);  // phase B Line Current
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 1543);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);  // phase B Power Factor
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 8);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[6]);  // phase C Line Volage
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 144);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[7]);  // phase C Line Current
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 2571);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[8]);  // phase C Power Factor
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 12);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }

	delete_container(retList);
}

