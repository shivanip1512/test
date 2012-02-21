#include <boost/test/unit_test.hpp>

#include "fdrtelegyr.h"

BOOST_AUTO_TEST_SUITE( test_fdrTelegyr )

class  TestFdrTelegyr :  public CtiFDRTelegyr {
    public:
        TestFdrTelegyr(){};
        bool testProcessAnalog (APICLI_GET_MEA aPoint, int groupid, int group_type, int index)
        {
            return processAnalog(aPoint,groupid,group_type,index);
        }
        //Disabling the ability to send to dispatch.
        bool sendMessageToDispatch (CtiMessage *aMessage){ return true; };
        bool queueMessageToDispatch (CtiMessage *aMessage){ return true; };

        void setControlCenter(CtiTelegyrControlCenter& controlCenter)
        {
            _controlCenter = controlCenter;
        }
};

BOOST_AUTO_TEST_CASE( test_one )
{
    int pointId = 42;
    int groupId = 1;
    int groupType = 0;
    int index = 0;
    TestFdrTelegyr telegyr;

    //Initialize the interface to have a point in a group.
    CtiTelegyrControlCenter controlCenter;
    CtiTelegyrGroup group1;
    CtiFDRPoint fdrPoint;

    fdrPoint.setPointID(pointId);
    group1.getPointList().push_back(fdrPoint);
    group1.setGroupID(1);
    //group1.set
    controlCenter.addToGroupList(group1);
    telegyr.setControlCenter(controlCenter);

    //Call processAnalog with passed in data to send a non updated message.
    // APICLI_GET_MEA aPoint, int groupid, int group_type, int index


    APICLI_GET_MEA aPoint;
    aPoint.mea_value.mea4_value = 4.0;
    aPoint.mea_valid = API_VALID;
    aPoint.sys_dependent_info.long_value = 2;

    bool ret = telegyr.testProcessAnalog(aPoint,groupId,groupType,index);
    BOOST_CHECK_EQUAL(ret,true);
}

BOOST_AUTO_TEST_SUITE_END()
