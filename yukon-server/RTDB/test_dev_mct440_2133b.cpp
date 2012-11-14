#include <boost/test/unit_test.hpp>

#include "dev_mct440_2133b.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container
#include "config_device.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using Cti::Protocols::EmetconProtocol;

using std::string;
using std::vector;

struct test_Mct440_2133BDevice : Cti::Devices::Mct440_2133BDevice
{
    test_Mct440_2133BDevice(int type, const string &name)
    {
        setType(type);
        _name = name;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;

    using Mct440_213xBDevice::decodeGetValueInstantLineData;

    typedef std::map<int, CtiPointSPtr>              PointOffsetMap;
    typedef std::map<CtiPointType_t, PointOffsetMap> PointTypeOffsetMap;

    PointTypeOffsetMap points;

    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type)
    {
        CtiPointSPtr point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned point_count = 0;

        for each( const std::pair<CtiPointType_t, PointOffsetMap> &p in points )
        {
            point_count += p.second.size();
        }

        switch( type )
        {
            case AnalogPointType:
            {
                Test_CtiPointAnalog *analog = new Test_CtiPointAnalog();
                analog->setPointOffset(offset);
                analog->setDeviceID(reinterpret_cast<long>(&points));
                analog->setID(point_count);
                point.reset(analog);
            }
            break;

            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            {
                Test_CtiPointAccumulator *accumulator = new Test_CtiPointAccumulator();
                accumulator->setPointOffset(offset);
                accumulator->setDeviceID(reinterpret_cast<long>(&points));
                accumulator->setID(point_count);
                point.reset(accumulator);
            }
            break;

            case StatusPointType:
            {
                Test_CtiPointStatus *status = new Test_CtiPointStatus();
                status->setPointOffset(offset);
                status->setDeviceID(reinterpret_cast<long>(&points));
                status->setID(point_count);
                point.reset(status);
            }
            break;
        }

        return point;
    }
};

struct test_Mct440_2133B : test_Mct440_2133BDevice
{
    test_Mct440_2133B() : test_Mct440_2133BDevice(TYPEMCT440_2133B, "Test MCT-440-2133B")  {}
};

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig(-1, string(), string()) {}

    using DeviceConfig::insertValue;
};

namespace std {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& out, const test_Mct440_2133BDevice::ReadDescriptor &rd);
    std::ostream& operator<<(std::ostream& out, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct440_2133BDevice::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct440_2133BDevice::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

BOOST_AUTO_TEST_CASE(test_mct440_2133b_decodeGetValueInstantLineData)
{
    INMESS                         InMessage;
    CtiTime                        t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList  vgList;
    CtiDeviceBase::CtiMessageList  retList;
    CtiDeviceBase::OutMessageList  outList; // not use


    unsigned char test_data[12] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC};

    memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_InstantLineData;

    test_Mct440_2133B test_dev;

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueInstantLineData(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 9);

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

        BOOST_CHECK_EQUAL( pdata->getValue(), 4);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // phase B Line Volage

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 80);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);  // phase B Line Current

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 1543);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);  // phase B Power Factor

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 8);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[6]);  // phase C Line Volage

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 144);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[7]);  // phase C Line Current

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 2571);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[8]);  // phase C Power Factor

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 12);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

}

