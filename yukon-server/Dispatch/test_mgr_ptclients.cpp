#include <boost/test/unit_test.hpp>

#include "mgr_ptclients.h"
#include "tbl_pt_alarm.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "con_mgr_vg.h"
#include "connection.h"

#include "rtdb_test_helpers.h"

#include <boost/assign/list_of.hpp>
#include <cms/Destination.h>

struct Test_CtiPointClientManager : CtiPointClientManager
{
    using CtiPointClientManager::addAlarming;
    using CtiPointClientManager::removeAlarming;
    using CtiPointClientManager::generateSqlStatements;
};


using namespace std;

BOOST_AUTO_TEST_SUITE( test_mgr_ptclients )

Cti::Test::use_in_unit_tests_only test_tag;

enum
{
    begin_id   =  9,

    analog1_id,
    analog2_id,
    analog3_id,

    status1_id,
    status2_id,

    device1_id,
    device2_id,

    point1_offset = 1,
    point2_offset,
};

struct Test_CtiTablePointAlarming : CtiTablePointAlarming
{
private:
    typedef CtiTablePointAlarming Inherited;
public:
    CtiTablePointAlarming& operator=(const CtiTablePointAlarming& aRef) { Inherited::operator=(aRef); return *this; }

    CtiTablePointAlarming& setAlarmCategory(const INT offset, const UINT &aInt) { return Inherited::setAlarmCategory(offset, aInt); }
};

BOOST_AUTO_TEST_CASE(test_alarming)
{
    Test_CtiPointClientManager manager;
    BOOST_CHECK(manager.entries() == 0);

    CtiPointSPtr point_status1(Cti::Test::makeStatusPoint(device1_id, status1_id, point1_offset));

    Test_CtiTablePointAlarming alarm;
    alarm = manager.getAlarming(*point_status1);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(1) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);

    alarm.setAlarmCategory(1, 3);

    BOOST_CHECK(alarm.getAlarmCategory(1) > SignalEvent);

    manager.addAlarming(alarm);

    alarm = manager.getAlarming(*point_status1);

    BOOST_CHECK(alarm.getAlarmCategory(1) > SignalEvent);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);

    manager.removeAlarming(status1_id);

    alarm = manager.getAlarming(*point_status1);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(1) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);
}

BOOST_AUTO_TEST_CASE(test_dynamic)
{
    Test_CtiPointClientManager manager;
    BOOST_CHECK(manager.entries() == 0);

    CtiPointSPtr point_status1(Cti::Test::makeStatusPoint(device1_id, status1_id, point1_offset));

    CtiDynamicPointDispatchSPtr dynamic = manager.getDynamic(*point_status1);

    BOOST_CHECK(!dynamic);

    dynamic = CtiDynamicPointDispatchSPtr(CTIDBG_new CtiDynamicPointDispatch(status1_id));
    manager.setDynamic(status1_id, dynamic);

    CtiDynamicPointDispatchSPtr pDispatch = manager.getDynamic(*point_status1);
    BOOST_CHECK(pDispatch);

    BOOST_CHECK_EQUAL(pDispatch.get(), dynamic.get());

    manager.erase(status1_id);
    pDispatch = manager.getDynamic(*point_status1);

    BOOST_CHECK(!pDispatch);
}

BOOST_AUTO_TEST_CASE(test_generate_sql_statements)
{
    Test_CtiPointClientManager manager;
    BOOST_CHECK(manager.entries() == 0);

    // Seed this to be the same every test.
    srand(42);

    // Make sure we get over 1000 values.
    const set<long> pointIds = boost::assign::list_of
        (42).repeat_fun(1500, rand);

    const auto sqls = manager.generateSqlStatements(pointIds);

    BOOST_REQUIRE_EQUAL(sqls.size(), 2);
    {
        const std::vector<int> pointIdsExpected {
            15,42,47,60,70,112,150,
            161,175,204,243,262,287,299,305,321,362,371,380,383,384,394,400,403,442,464,481,488,561,
            576,577,580,625,639,650,659,662,679,682,714,799,809,842,846,875,898,921,963,996,1017,1033,
            1038,1064,1096,1097,1149,1173,1206,1216,1223,1282,1302,1304,1321,1349,1387,1404,1411,1415,
            1423,1445,1463,1468,1482,1521,1555,1577,1588,1592,1600,1672,1689,1717,1723,1765,1769,1794,
            1803,1809,1910,1916,1928,1939,1960,1975,1976,1991,1994,2000,2031,2047,2062,2084,2104,2113,
            2164,2174,2175,2177,2178,2179,2183,2203,2215,2251,2260,2280,2311,2316,2336,2340,2341,2355,
            2357,2381,2386,2392,2403,2408,2409,2421,2424,2430,2453,2485,2503,2508,2511,2519,2534,2538,
            2566,2590,2635,2638,2651,2657,2663,2685,2723,2727,2728,2753,2762,2778,2803,2826,2833,2847,
            2870,2910,3021,3033,3058,3074,3102,3130,3131,3138,3156,3160,3164,3182,3202,3203,3225,3240,
            3241,3274,3301,3333,3390,3445,3458,3480,3507,3527,3542,3552,3597,3603,3631,3678,3680,3689,
            3696,3715,3719,3747,3771,3776,3783,3804,3808,3834,3836,3860,3887,3894,3900,3933,3935,3937,
            3941,3944,3980,4003,4052,4059,4064,4084,4102,4113,4138,4165,4181,4192,4195,4205,4212,4219,
            4230,4308,4313,4319,4369,4375,4382,4393,4408,4416,4426,4432,4465,4491,4507,4514,4558,4593,
            4597,4624,4629,4632,4642,4663,4697,4742,4807,4808,4847,4848,4945,5005,5007,5037,5092,5130,
            5133,5242,5262,5267,5317,5339,5344,5354,5361,5384,5464,5470,5486,5487,5544,5554,5587,5611,
            5628,5670,5685,5690,5691,5751,5795,5833,5836,5844,5900,5902,5905,5979,6004,6012,6039,6049,
            6086,6091,6096,6177,6183,6194,6221,6257,6263,6290,6293,6324,6336,6350,6377,6433,6453,6465,
            6490,6508,6522,6532,6543,6552,6568,6596,6628,6635,6638,6655,6728,6742,6744,6769,6781,6800,
            6803,6840,6858,6859,6881,6900,7032,7040,7052,7056,7093,7099,7108,7115,7117,7133,7136,7173,
            7180,7185,7188,7230,7247,7256,7271,7273,7299,7300,7321,7327,7356,7417,7422,7430,7437,7506,
            7520,7525,7537,7550,7557,7582,7614,7622,7644,7654,7674,7682,7704,7723,7741,7759,7781,7797,
            7811,7819,7825,7890,7906,7961,7988,8016,8024,8034,8092,8109,8140,8141,8149,8167,8189,8202,
            8215,8238,8246,8275,8276,8343,8350,8389,8417,8464,8517,8527,8531,8534,8536,8539,8570,8635,
            8650,8702,8706,8750,8773,8777,8780,8785,8844,8864,8867,8883,8897,8907,8921,8929,8986,8999,
            9024,9034,9054,9060,9102,9117,9138,9139,9148,9190,9191,9215,9217,9237,9253,9281,9307,9362,
            9385,9391,9422,9428,9445,9448,9481,9515,9523,9571,9626,9641,9650,9679,9709,9710,9731,9738,
            9743,9754,9761,9808,9816,9830,9867,9888,9903,9907,9930,9948,10027,10044,10052,10139,10154,
            10162,10168,10274,10291,10300,10311,10363,10398,10480,10483,10495,10508,10514,10521,10523,
            10533,10603,10610,10612,10619,10653,10654,10669,10693,10721,10729,10734,10736,10748,10785,
            10795,10821,10850,10877,10887,10903,10926,11010,11024,11026,11157,11174,11177,11181,11209,
            11262,11321,11370,11385,11432,11466,11527,11612,11649,11660,11663,11674,11716,11749,11769,
            11771,11842,11859,11868,11946,12000,12036,12054,12067,12076,12087,12098,12140,12154,12188,
            12244,12247,12264,12270,12289,12290,12300,12328,12340,12437,12498,12512,12515,12517,12524,
            12584,12591,12599,12616,12627,12668,12704,12709,12749,12754,12789,12800,12801,12805,12810,
            12822,12841,12859,12879,12884,12905,12970,13003,13016,13021,13025,13027,13052,13086,13104,
            13113,13122,13156,13204,13230,13246,13252,13269,13289,13291,13292,13330,13348,13350,13368,
            13385,13388,13389,13409,13413,13429,13450,13456,13464,13471,13503,13507,13509,13531,13536,
            13542,13577,13581,13636,13638,13664,13712,13716,13731,13802,13809,13833,13840,13856,13866,
            13916,13923,14011,14065,14083,14112,14117,14123,14124,14193,14244,14272,14318,14346,14351,
            14374,14377,14391,14433,14456,14466,14472,14492,14497,14529,14568,14574,14583,14595,14621,
            14668,14714,14736,14789,14832,14844,14919,14973,15005,15011,15037,15047,15052,15068,15112,
            15122,15128,15130,15133,15161,15248,15278,15315,15351,15373,15374,15388,15397,15401,15405,
            15440,15456,15485,15545,15555,15586,15613,15673,15689,15698,15714,15715,15731,15758,15800,
            15809,15811,15838,15852,15862,15877,15880,15892,15947,15951,15968,15980,15989,15991,16005,
            16007,16016,16083,16090,16125,16143,16183,16184,16195,16201,16228,16243,16265,16277,16339,
            16357,16361,16368,16376,16409,16433,16441,16454,16458,16461,16525,16526,16563,16565,16625,
            16630,16647,16651,16654,16697,16704,16723,16749,16812,16840,16845,16846,16886,16893,16903,
            16916,16925,16970,17019,17022,17024,17039,17042,17067,17109,17126,17131,17207,17219,17281,
            17296,17300,17304,17338,17357,17400,17401,17414,17445,17460,17491,17526,17537,17539,17556,
            17612,17621,17692,17693,17694,17745,17757,17814,17844,17869,17872,17879,17891,17900,17920,
            17926,17950,17957,18015,18027,18073,18075,18137,18141,18158,18210,18212,18224,18260,18273,
            18308,18380,18454,18508,18589,18626,18648,18649,18683,18700,18717,18718,18751,18754,18768,
            18771,18784,18809,18816,18824,18833,18838,18866,18885,18935,18943,18974,18984,18985,19045,
            19072,19092,19115,19142,19182,19227,19295,19311,19345,19358,19377,19383,19485,19503,19509,
            19555,19589,19590,19643,19654,19676,19702,19735,19768,19813,19825,19834,19874,19904,19905,
            19946,20013,20019,20039,20044,20047,20097,20111,20156,20157,20158,20161,20168,20200,20253,
            20271,20299,20317,20368,20373,20390,20411,20448,20487,20518,20564,20572,20576,20643,20708 };

        BOOST_CHECK_EQUAL(sqls[0].sql,
            "SELECT DPD.pointid, DPD.timestamp, DPD.quality, DPD.value, DPD.tags, DPD.nextarchive, "
            "DPD.millis FROM DynamicPointDispatch DPD WHERE DPD.pointid IN ("
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
            ")");

        BOOST_CHECK_EQUAL(sqls[0].pointIds.size(), 950);
        BOOST_CHECK_EQUAL_RANGES(sqls[0].pointIds, pointIdsExpected);
    }

    {
        const std::vector<int> pointIdsExpected{
            20744,20768,20815,20819,
            20825,20842,20849,20871,20879,20890,20894,20946,20957,21023,21030,21054,21144,21150,21188,
            21267,21311,21322,21328,21343,21358,21384,21465,21495,21512,21531,21564,21592,21637,21653,
            21715,21728,21762,21831,21849,21858,21875,21881,21936,21939,21946,21966,21969,21996,22053,
            22061,22077,22079,22088,22123,22161,22162,22168,22169,22180,22191,22204,22235,22256,22271,
            22277,22338,22363,22389,22416,22420,22445,22464,22488,22497,22506,22516,22521,22536,22562,
            22608,22626,22637,22648,22754,22769,22797,22817,22830,22888,22943,22972,23022,23033,23063,
            23066,23070,23085,23097,23135,23136,23137,23139,23207,23225,23227,23269,23296,23310,23318,
            23336,23371,23373,23444,23447,23462,23512,23539,23581,23597,23632,23649,23666,23668,23674,
            23707,23709,23719,23750,23755,23763,23767,23771,23778,23801,23845,23858,23921,23924,23935,
            23953,23963,24022,24049,24067,24127,24135,24150,24226,24262,24321,24348,24471,24514,24524,
            24538,24561,24584,24597,24605,24641,24656,24674,24676,24687,24693,24696,24713,24745,24762,
            24768,24777,24792,24806,24857,24861,24874,24891,24913,24934,24953,24994,25017,25079,25095,
            25133,25134,25143,25190,25192,25235,25246,25278,25285,25347,25362,25381,25398,25413,25422,
            25448,25472,25495,25496,25566,25572,25600,25607,25612,25674,25685,25703,25709,25732,25789,
            25808,25833,25874,25887,25911,25912,25957,25966,25999,26006,26032,26049,26061,26087,26109,
            26207,26225,26244,26326,26336,26411,26422,26434,26487,26497,26519,26537,26567,26646,26654,
            26659,26686,26702,26717,26719,26730,26739,26745,26757,26778,26810,26886,26911,26933,26939,
            26977,27007,27035,27152,27166,27173,27202,27246,27327,27343,27392,27428,27441,27469,27507,
            27533,27536,27561,27569,27570,27588,27600,27646,27663,27665,27690,27711,27725,27737,27747,
            27748,27759,27774,27804,27813,27830,27849,27869,27877,27881,27882,27890,27896,27946,27948,
            27975,28017,28027,28065,28068,28086,28091,28095,28113,28119,28134,28154,28168,28206,28246,
            28259,28265,28268,28282,28290,28304,28330,28348,28370,28424,28437,28459,28544,28548,28566,
            28583,28633,28712,28729,28737,28745,28762,28763,28793,28857,28888,28890,28978,28997,29002,
            29015,29042,29054,29057,29088,29128,29186,29205,29210,29213,29227,29247,29269,29282,29342,
            29351,29359,29365,29382,29393,29397,29409,29414,29423,29456,29467,29486,29489,29503,29519,
            29585,29618,29629,29652,29664,29670,29686,29688,29702,29749,29764,29792,29797,29817,29836,
            29851,29875,29908,29923,29929,29931,29937,29950,29992,30047,30056,30073,30087,30108,30110,
            30172,30182,30185,30202,30217,30254,30270,30271,30291,30294,30314,30327,30339,30350,30380,
            30402,30451,30517,30528,30530,30538,30544,30547,30576,30604,30615,30658,30666,30698,30758,
            30759,30765,30818,30825,30837,30861,30870,30904,30911,30961,31003,31030,31046,31057,31125,
            31147,31189,31201,31202,31211,31228,31245,31269,31287,31295,31332,31336,31341,31349,31379,
            31434,31471,31495,31498,31535,31541,31545,31555,31601,31604,31605,31610,31616,31623,31632,
            31739,31774,31805,31819,31864,31869,31898,31933,31991,32011,32037,32039,32076,32118,32135,
            32171,32248,32257,32286,32385,32400,32409,32421,32428,32438,32442,32479,32536,32540,32551,
            32554,32597,32645,32653,32662,32663,32681,32759 };

        BOOST_CHECK_EQUAL(sqls[1].sql,
            "SELECT DPD.pointid, DPD.timestamp, DPD.quality, DPD.value, DPD.tags, DPD.nextarchive, "
            "DPD.millis FROM DynamicPointDispatch DPD WHERE DPD.pointid IN ("
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
            ")");

        BOOST_CHECK_EQUAL(sqls[1].pointIds.size(), 522);
        BOOST_CHECK_EQUAL_RANGES(sqls[1].pointIds, pointIdsExpected);
    }
}

struct Test_CtiPointClientManager2 : CtiPointClientManager
{
    using CtiPointClientManager::erase;
    using CtiPointClientManager::InsertConnectionManager;
    using CtiPointClientManager::removePointsFromConnectionManager;
    using CtiPointClientManager::pointHasConnection;
    using CtiPointClientManager::getRegistrationSet;

    typedef Cti::Test::TestReader<std::vector<std::string>> PointManagerReader;

    Test_CtiPointClientManager2()
    {
        // Create 4 test DB records.  Order is important.  Simulates:
        //    " PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, PT.pointoffset,"
        //    " PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, PT.archiveinterval,"
        //    " UNT.uomid, UNT.decimalplaces, UNT.decimaldigits,"
        //    " UM.calctype,"
        //    " ALG.multiplier, ALG.dataoffset, ALG.deadband,"
        //    " PC.controloffset, PC.controlinhibit"

        PointManagerReader reader( {
            { "pointid", "1001", "1002", "1003", "1004" },
            { "pointname", "w", "x", "y", "z" },
            { "pointtype", "Analog", "Analog", "Analog", "Analog" },
            { "paobjectid", "100", "100", "100", "100" },
            { "stategroupid", "-1", "-1", "-1", "-1" },
            { "pointoffset", "1", "2", "3", "4" },
            { "serviceflag", "N", "N", "N", "N" },
            { "alarminhibit", "N", "N", "N", "N" },
            { "pseudoflag", "R", "R", "R", "R" },
            { "archivetype", "None", "None", "None", "None" },
            { "archiveinterval", "0", "0", "0", "0" },
            { "uomid", "0", "0", "0", "0" },
            { "decimalplaces", "0", "0", "0", "0" },
            { "decimaldigits", "0", "0", "0", "0" },
            { "calctype", "0", "0", "0", "0" },
            { "multiplier", "0", "0", "0", "0" },
            { "dataoffset", "0", "0", "0", "0" },
            { "deadband", "0", "0", "0", "0" },
            { "controloffset", "0", "0", "0", "0" },
            { "controlinhibit", "0", "0", "0", "0" },
        } );

        std::set<long> pointIdsFound;

        CtiPointManager::refreshPoints( pointIdsFound, reader );
    }

};

struct Test_CtiPointConnection : CtiPointConnection
{
    using CtiPointConnection::AddConnectionManager;
    using CtiPointConnection::removeConnectionManagersFromPoint;
    using CtiPointConnection::HasConnection;
};

struct Test_CtiListenerConnection : CtiListenerConnection
{
    Test_CtiListenerConnection() :CtiListenerConnection( "test" )
    {
    }

    std::unique_ptr<cms::Destination> getClientReplyDest() const override
    {
        std::unique_ptr<cms::Destination> d;
        return d;
    }
};


BOOST_AUTO_TEST_CASE( test_erase )
{
    Test_CtiPointClientManager2 manager;
    Test_CtiPointConnection connection;

    CtiListenerConnection lc1( "test1" );
    CtiListenerConnection lc2( "test2" );

    boost::shared_ptr<CtiConnection::Que_t> testQ(new CtiConnection::Que_t());
    boost::shared_ptr<CtiConnectionManager> cm1( new CtiVanGoghConnectionManager(lc1, testQ.get()) );
    boost::shared_ptr<CtiConnectionManager> cm2( new CtiVanGoghConnectionManager(lc2, testQ.get()) );

    CtiPointRegistrationMsg aReg1( REG_ADD_POINTS );
    CtiPointRegistrationMsg aReg2( REG_ADD_POINTS );
    aReg1.insert( 1001 );
    aReg1.insert( 1002 );
    aReg2.insert( 1003 );
    aReg1.insert( 1004 );       // This point is on both connections
    aReg2.insert( 1004 );

    connection.AddConnectionManager( cm1 );
    connection.AddConnectionManager( cm2 );

    // We now have 2 cm's
    BOOST_CHECK_EQUAL( connection.getManagerList().size(), 2 );

    manager.InsertConnectionManager( cm1, aReg1 );
    manager.InsertConnectionManager( cm2, aReg2 );

    // Happy tests.  Make sure everything is as it should be.
    BOOST_CHECK_EQUAL( manager.entries(), 4);

    // Verify the _conMgrPointMap contains an entry,
    // and that our points are in that set.

    auto set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 3 );
    BOOST_CHECK( set.find( 1001 ) != set.end() );
    BOOST_CHECK( set.find( 1002 ) != set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    BOOST_CHECK( manager.getCachedPoint( 1001 ) );
    BOOST_CHECK( manager.getCachedPoint( 1002 ) );
    BOOST_CHECK( manager.getCachedPoint( 1003 ) );
    BOOST_CHECK( manager.getCachedPoint( 1004 ) );

    // Now we erase a point
    manager.erase( 1001 );

    // We removed the point
    BOOST_CHECK_EQUAL(manager.entries(), 3);

    set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) != set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    BOOST_CHECK( ! manager.getCachedPoint( 1001 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1002 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1003 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1004 ) );

    // Now we erase the last point.  Should also remove the 
    manager.erase( 1002 );

    // We removed the point
    BOOST_CHECK_EQUAL(manager.entries(), 2);

    set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 1 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    BOOST_CHECK( ! manager.getCachedPoint( 1001 ) );
    BOOST_CHECK( ! manager.getCachedPoint( 1002 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1003 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1004 ) );
}

BOOST_AUTO_TEST_CASE( test_removePointsFromConnectionManager )
{
    Test_CtiPointClientManager2 manager;
    Test_CtiPointConnection connection;

    CtiListenerConnection lc1( "test1" );
    CtiListenerConnection lc2( "test2" );

    boost::shared_ptr<CtiConnection::Que_t> testQ(new CtiConnection::Que_t());
    boost::shared_ptr<CtiConnectionManager> cm1(new CtiVanGoghConnectionManager(lc1, testQ.get()));
    boost::shared_ptr<CtiConnectionManager> cm2(new CtiVanGoghConnectionManager(lc2, testQ.get()));

    CtiPointRegistrationMsg aReg1( REG_ADD_POINTS );
    CtiPointRegistrationMsg aReg2( REG_ADD_POINTS );
    aReg1.insert( 1001 );
    aReg1.insert( 1002 );
    aReg2.insert( 1003 );
    aReg1.insert( 1004 );
    aReg2.insert( 1004 );

    connection.AddConnectionManager( cm1 );
    connection.AddConnectionManager( cm2 );

    // We now have 2 cm's
    BOOST_CHECK_EQUAL( connection.getManagerList().size(), 2 );

    manager.InsertConnectionManager( cm1, aReg1 );
    manager.InsertConnectionManager( cm2, aReg2 );

    // Happy tests.  Make sure everything is as it should be.
    BOOST_CHECK_EQUAL( manager.entries(), 4 );

    // Verify the _conMgrPointMap contains an entry,
    // and that our points are in that set.

    auto set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 3 );
    BOOST_CHECK( set.find( 1001 ) != set.end() );
    BOOST_CHECK( set.find( 1002 ) != set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    BOOST_CHECK( manager.getCachedPoint( 1001 ) );
    BOOST_CHECK( manager.getCachedPoint( 1002 ) );
    BOOST_CHECK( manager.getCachedPoint( 1003 ) );
    BOOST_CHECK( manager.getCachedPoint( 1004 ) );

    manager.removePointsFromConnectionManager( cm1 );

    // We've removed the connection, not the point so this doesn't change
    BOOST_CHECK_EQUAL( manager.entries(), 4 );

    // Verify the _conMgrPointMap contains an entry,
    // and that our points are in that set.
    set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 0 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) == set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );   // Removing cm1 shouldn't change this

    BOOST_CHECK( manager.getCachedPoint( 1001 ) );
    BOOST_CHECK( manager.getCachedPoint( 1002 ) );
    BOOST_CHECK( manager.getCachedPoint( 1003 ) );
    BOOST_CHECK( manager.getCachedPoint( 1004 ) );
}

BOOST_AUTO_TEST_CASE( test_expire )
{
    Test_CtiPointClientManager2 manager;
    Test_CtiPointConnection connection;

    CtiListenerConnection lc1( "test1" );
    CtiListenerConnection lc2( "test2" );

    boost::shared_ptr<CtiConnection::Que_t> testQ(new CtiConnection::Que_t());
    boost::shared_ptr<CtiConnectionManager> cm1(new CtiVanGoghConnectionManager(lc1, testQ.get()));
    boost::shared_ptr<CtiConnectionManager> cm2(new CtiVanGoghConnectionManager(lc2, testQ.get()));

    CtiPointRegistrationMsg aReg1( REG_ADD_POINTS );
    CtiPointRegistrationMsg aReg2( REG_ADD_POINTS );
    aReg1.insert( 1001 );
    aReg1.insert( 1002 );
    aReg2.insert( 1003 );
    aReg1.insert( 1004 );
    aReg2.insert( 1004 );

    connection.AddConnectionManager( cm1 );
    connection.AddConnectionManager( cm2 );

    // We now have 2 cm's
    BOOST_CHECK_EQUAL( connection.getManagerList().size(), 2 );

    manager.InsertConnectionManager( cm1, aReg1 );
    manager.InsertConnectionManager( cm2, aReg2 );

    // Happy tests.  Make sure everything is as it should be.
    BOOST_CHECK_EQUAL( manager.entries(), 4 );

    // Verify the _conMgrPointMap contains an entry,
    // and that our points are in that set.

    auto set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 3 );
    BOOST_CHECK( set.find( 1001 ) != set.end() );
    BOOST_CHECK( set.find( 1002 ) != set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    BOOST_CHECK( manager.getCachedPoint( 1001 ) );
    BOOST_CHECK( manager.getCachedPoint( 1002 ) );
    BOOST_CHECK( manager.getCachedPoint( 1003 ) );
    BOOST_CHECK( manager.getCachedPoint( 1004 ) );

    manager.expire( 1001 );

    // We've removed the point
    BOOST_CHECK_EQUAL( manager.entries(), 3 );

    // Verify the _conMgrPointMap contains an entry,
    // and that our points are in that set.
    set = manager.getRegistrationSet( cm1->hash( *cm1 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 3 );
    BOOST_CHECK( set.find( 1001 ) != set.end() );   // Doesn't remove the point, just the cache
    BOOST_CHECK( set.find( 1002 ) != set.end() );
    BOOST_CHECK( set.find( 1003 ) == set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );

    set = manager.getRegistrationSet( cm2->hash( *cm2 ), test_tag );
    BOOST_CHECK_EQUAL( set.size(), 2 );
    BOOST_CHECK( set.find( 1001 ) == set.end() );
    BOOST_CHECK( set.find( 1002 ) == set.end() );
    BOOST_CHECK( set.find( 1003 ) != set.end() );
    BOOST_CHECK( set.find( 1004 ) != set.end() );   // Removing cm1 shouldn't change this

    BOOST_CHECK( ! manager.getCachedPoint( 1001 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1002 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1003 ) );
    BOOST_CHECK(   manager.getCachedPoint( 1004 ) );
}

BOOST_AUTO_TEST_SUITE_END()
