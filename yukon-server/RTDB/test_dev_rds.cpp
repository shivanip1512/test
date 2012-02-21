#include <boost/test/unit_test.hpp>

#include "dev_rds.h"
#include "test_reader.h"

BOOST_AUTO_TEST_SUITE( test_dev_rds )

using namespace std;

using Cti::Devices::RDSTransmitter;

struct Test_RDSTransmitter : RDSTransmitter
{
    typedef RDSTransmitter Inherited;

    using RDSTransmitter::RDSTransmitter;
    using RDSTransmitter::resetStates;
    using RDSTransmitter::getEncoderAddress;
    using RDSTransmitter::getSiteAddress;
    using RDSTransmitter::getSequenceCount;
    using RDSTransmitter::getGroupTypeCode;
    using RDSTransmitter::getGroupsPerSecond;
    using RDSTransmitter::addSequenceCounter;
    using RDSTransmitter::addMessageAddressing;
    using RDSTransmitter::addUECPCRC;
    using RDSTransmitter::addCooperCRC;
    using RDSTransmitter::replaceReservedBytes;
    using RDSTransmitter::addStartStopBytes;

    using RDSTransmitter::MessageStore;
};

void fillRDSTransmitter(Test_RDSTransmitter &test_xmitter)
{
    {
        typedef Cti::Test::StringRow<15> RdsDeviceDatabaseRow;
        typedef Cti::Test::TestReader<RdsDeviceDatabaseRow> RDSDatabaseReader;

        RdsDeviceDatabaseRow columnNames =   {"paobjectid", "category", "paoclass", "paoname", "type", "disableflag", "deviceid", "alarminhibit", "controlinhibit", "portid","phonenumber",                             "minconnecttime", "maxconnecttime", "linesettings", "baudrate" };
        RdsDeviceDatabaseRow columnValues1 = {"1234",     "transmitter", "device", "Rds Pao 1", "RDS Transmitter", "N", "1234",     "Y",            "Y",               "1",     RDSDatabaseReader::getNullString(),          "0",              "1",              "8N1",             "8600"};

        std::vector<RdsDeviceDatabaseRow> rowVec;
        rowVec.push_back( columnValues1 );

        RDSDatabaseReader reader(columnNames, rowVec);

        if( reader() )
        {
            test_xmitter.DecodeDatabaseReader(reader);
        }
    }

    {
        typedef Cti::Test::StringRow<4> StaticPaoInfoRow;
        typedef Cti::Test::TestReader<StaticPaoInfoRow> StaticPaoInfoReader;

        StaticPaoInfoRow columnNames    = {"staticpaoinfoid", "paobjectid",        "infokey",              "value"};
        StaticPaoInfoRow staValues1     = {"1",               "1234",      "RDS_TRANSMITTER_SITE_ADDRESS",    "11"};
        StaticPaoInfoRow staValues2     = {"2",               "1234",      "RDS_TRANSMITTER_ENCODER_ADDRESS", "12"};
        StaticPaoInfoRow staValues3     = {"3",               "1234",      "RDS_TRANSMITTER_TRANSMIT_SPEED",  ".8"};
        StaticPaoInfoRow staValues4     = {"4",               "1234",      "RDS_TRANSMITTER_GROUP_TYPE",      "11A"};

        std::vector<StaticPaoInfoRow> rowVec;
        rowVec.push_back( staValues1 );
        rowVec.push_back( staValues2 );
        rowVec.push_back( staValues3 );
        rowVec.push_back( staValues4 );

        StaticPaoInfoReader reader(columnNames, rowVec);

        CtiTableStaticPaoInfo static_paoinfo;

        while( reader() )
        {
            static_paoinfo.DecodeDatabaseReader(reader);

            test_xmitter.setStaticInfo(static_paoinfo);
        }
    }
}

BOOST_AUTO_TEST_CASE(test_reader_and_getters)
{
    Test_RDSTransmitter test_xmitter;
    fillRDSTransmitter(test_xmitter);

    BOOST_CHECK_EQUAL(test_xmitter.getSiteAddress(), 11);
    BOOST_CHECK_EQUAL(test_xmitter.getEncoderAddress(), 12);
    BOOST_CHECK_EQUAL(test_xmitter.getGroupTypeCode(), 0x16);
    BOOST_CHECK_CLOSE(test_xmitter.getGroupsPerSecond(), 0.8f, 1e-5 );
}

BOOST_AUTO_TEST_CASE(test_crc)
{
    //FROM UECP RDS Forum Technical Specification SPB 490
    //Version 7.05
    //Example:
    //When applied to the string:
    //2D111234010105ABCD123F0XXXX11069212491000320066
    //The range of each of these 47 characters is 00..FF and the CRC generated and then appended will be:
    //0x9723
    Test_RDSTransmitter test_xmitter;

    Test_RDSTransmitter::MessageStore message;
    message.push_back('2');
    message.push_back('D');
    message.push_back('1');
    message.push_back('1');
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');
    message.push_back('4');
    message.push_back('0');
    message.push_back('1');
    message.push_back('0');
    message.push_back('1');
    message.push_back('0');
    message.push_back('5');
    message.push_back('A');
    message.push_back('B');
    message.push_back('C');
    message.push_back('D');
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');
    message.push_back('F');
    message.push_back('0');
    message.push_back('X');
    message.push_back('X');
    message.push_back('X');
    message.push_back('X');
    message.push_back('1');
    message.push_back('1');
    message.push_back('0');
    message.push_back('6');
    message.push_back('9');
    message.push_back('2');
    message.push_back('1');
    message.push_back('2');
    message.push_back('4');
    message.push_back('9');
    message.push_back('1');
    message.push_back('0');
    message.push_back('0');
    message.push_back('0');
    message.push_back('3');
    message.push_back('2');
    message.push_back('0');
    message.push_back('0');
    message.push_back('6');
    message.push_back('6');

    test_xmitter.addUECPCRC(message);

    BOOST_CHECK_EQUAL(message.back(), 0x23);
    message.pop_back();
    BOOST_CHECK_EQUAL(message.back(), 0x97);

    // Test Cooper's CRC. This is a snippet from hardwares crc test.
    //uint8_t pData[] = "123456789";
    //......
    //if (crc != 0x29b1)
    //{
    //    fprintf(stderr, "\nError in return value from cpsCRC16_ccitt_byte. Exp:0x29B1\tCalc:%04X\n", crc);
    //    return EXIT_FAILURE;
    //}

    // Note that this is not representative of an expected message as RDS messages will be binary, not ascii representations.
    message.clear();
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');
    message.push_back('4');
    message.push_back('5');
    message.push_back('6');
    message.push_back('7');
    message.push_back('8');
    message.push_back('9');

    test_xmitter.addCooperCRC(message);

    BOOST_CHECK_EQUAL(message.back(), 0xB1);
    message.pop_back();
    BOOST_CHECK_EQUAL(message.back(), 0x29);
}

BOOST_AUTO_TEST_CASE(test_add_addressing)
{
    Test_RDSTransmitter test_xmitter;
    fillRDSTransmitter(test_xmitter);

    Test_RDSTransmitter::MessageStore message;
    message.push_back('2');
    message.push_back('D');
    message.push_back('1');
    message.push_back('1');
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');
    message.push_back('4');
    message.push_back('0');

    test_xmitter.addMessageAddressing(message);

    // site 11, encoder 12
    BOOST_CHECK_EQUAL(message.front(), (11 >> 2));
    message.pop_front();
    BOOST_CHECK_EQUAL(message.front(), ((11 << 6) | 12) & 0xFF);

}

BOOST_AUTO_TEST_CASE(test_replace_reserved)
{
    Test_RDSTransmitter test_xmitter;
    fillRDSTransmitter(test_xmitter);

    Test_RDSTransmitter::MessageStore message;
    message.push_back(0xFF);
    message.push_back('D');
    message.push_back('1');
    message.push_back('1');
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');
    message.push_back(0xFD);
    message.push_back(0xFE);

    test_xmitter.replaceReservedBytes(message);

    BOOST_CHECK_EQUAL(message.front(), 0xFD);
    message.pop_front();
    BOOST_CHECK_EQUAL(message.front(), 0x02);

    BOOST_CHECK_EQUAL(message.back(), 0x01);
    message.pop_back();
    BOOST_CHECK_EQUAL(message.back(), 0xFD);
    message.pop_back();
    BOOST_CHECK_EQUAL(message.back(), 0x00);
    message.pop_back();
    BOOST_CHECK_EQUAL(message.back(), 0xFD);
}

BOOST_AUTO_TEST_CASE(test_start_stop_bytes)
{
    Test_RDSTransmitter test_xmitter;
    fillRDSTransmitter(test_xmitter);

    Test_RDSTransmitter::MessageStore message;
    message.push_back('D');
    message.push_back('1');
    message.push_back('1');
    message.push_back('1');
    message.push_back('2');
    message.push_back('3');

    test_xmitter.addStartStopBytes(message);

    BOOST_CHECK_EQUAL(message.front(), 0xFE);
    BOOST_CHECK_EQUAL(message.back(),  0xFF);
}

BOOST_AUTO_TEST_SUITE_END()
