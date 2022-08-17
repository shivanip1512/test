#include <boost/test/auto_unit_test.hpp>

#include "dev_tap.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using Cti::Test::byte_str;

struct test_TapPagingTerminal : Cti::Devices::TapPagingTerminal
{
    using TapPagingTerminal::incrementPagePrefix;
};

BOOST_AUTO_TEST_SUITE( test_dev_tap )

BOOST_AUTO_TEST_CASE(test_dev_tap_incrementPagePrefix)
{
    test_TapPagingTerminal tap;

    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'b');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'c');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'd');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'b');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'c');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'd');
    BOOST_CHECK_EQUAL(tap.incrementPagePrefix(), 'a');
}

BOOST_AUTO_TEST_CASE(test_dev_tap_early_hangup)
{
    Cti::Devices::TapPagingTerminal tap;

    using TapRow    = Cti::Test::StringRow<21>;
    using TapReader = Cti::Test::TestReader<TapRow> ;

    TapRow keys = {
        "paobjectid",   "category",         "paoclass",         "paoname",          "type",
        "disableflag",  "deviceid",         "alarminhibit",     "controlinhibit",   "portid",
        "phonenumber",  "minconnecttime",   "maxconnecttime",   "linesettings",     "baudrate",
        "password",     "slaveaddress",     "pagernumber",      "sender",           "securitycode",
        "postpath"};
    TapRow values = {
        "96",           "DEVICE",           "TRANSMITTER",      "USA Mobility",     "TAP TERMINAL",
        "N",            "96",               "N",                "N",                "7",
        "18002506325",  "0",                "10",               "7E1",              "0",
        "(none)",       "Master",           "7632140090",       "(none)",           "(none)",
        "(none)"};

    std::vector<TapRow> rows { values };

    TapReader reader(keys, rows);

    if( reader() )
    {
        tap.DecodeDatabaseReader(reader);
    }

    /*
    from YUK-13880:

    11/13/2014 11:01:03 USA Mobility SENT: "<CR>"
    11/13/2014 11:01:05 USA Mobility RECV: "<CR><LF> ID="
    11/13/2014 11:01:05 USA Mobility SENT: "<ESC>PG1<CR>"
    11/13/2014 11:01:06 USA Mobility RECV: "<CR><ACK><CR><CR><ESC>[p<CR>"
    11/13/2014 11:01:06 USA Mobility SENT: "<STX>7632140090<CR>he0921484b2g<CR><ETX>553<CR>"
    11/13/2014 11:01:07 USA Mobility RECV: "<CR><ACK><CR>"
    11/13/2014 11:01:07 USA Mobility SENT: "<EOT><CR>"
    Com 4 modem hangup on 18002506325
    */

    OUTMESS OutMessage;
    const std::string message = "he0921484b2g";
    std::copy(
            message.begin(),
            message.end(),
            OutMessage.Buffer.TAPSt.Message);
    OutMessage.Buffer.TAPSt.Length = message.length();

    tap.allocateDataBins(&OutMessage);
    tap.setInitialState(0);

    CtiXfer transfer;
    BYTE    inBuffer[512], outBuffer[256];
    ULONG   bytesReceived = 0;
    CtiDeviceBase::CtiMessageList traceList;

    VerificationSequenceGen(true, 99);
    
    transfer.setInBuffer ( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );

    struct Transaction
    {
        Cti::Test::byte_str outbound;
        Cti::Test::byte_str inbound;
    };

    auto fillXfer = [](const Transaction trx, CtiXfer &transfer, const int pos) -> YukonError_t {

        auto outBuf = transfer.getOutBuffer();

        auto outExp = trx.outbound.begin();

        if( transfer.getOutCount() != trx.outbound.size() )
        {
            BOOST_ERROR(
                    "Outbound size mismatch at line " << pos << ", "
                    << transfer.getOutCount() << " != " << trx.outbound.size());
        }

        for( int i = transfer.getOutCount(); i && outExp != trx.outbound.end(); i-- )
        {
            if( *outBuf != *outExp )
            {
                BOOST_ERROR(
                        "Outbound mismatch at line " << pos << ", "
                        << static_cast<unsigned>(*outBuf) << " != " << static_cast<unsigned>(*outExp));
            }
            outBuf++;
            outExp++;
        }

        YukonError_t result = ClientErrors::None;

        auto inBuf = transfer.getInBuffer();
        auto inbound = trx.inbound.begin();

        /*
        if( transfer.getInCountExpected() != trx.inbound.size() )
        {
            BOOST_ERROR(
                    "Inbound size mismatch at line " << pos << ", "
                    << transfer.getInCountExpected() << " != " << trx.inbound.size());
        }
        */

        int inActual = 0;

        for( int i = transfer.getInCountExpected(); i && inbound != trx.inbound.end(); i-- )
        {
            *inBuf++ = *inbound++;
            inActual++;
        }

        transfer.setInCountActual(inActual);

        if( transfer.getInCountActual() < transfer.getInCountExpected() )
        {
            return ClientErrors::ReadTimeout;
        }

        return ClientErrors::None;
    };

    //  InitializeHandshake
    {
        std::vector<Transaction> transactions = {
            //  <CR>            <CR><LF> ID=
            {"0d",              "0d 0a 20 49 44 3d"},
            //  <ESC>PG1<CR>    <CR>
            {"1b 50 47 31 0d",  "0d"},
            //                  <ACK>
            {"",                "06"},
            //                  <CR>
            {"",                "0d"},
            //                  <CR>
            {"",                "0d"},
            //                  <ESC>
            {"",                "1b"},
            //                  [p<CR>
            {"",                "5b 70 0d"}};

        YukonError_t   status = ClientErrors::None;

        for( auto trx : transactions )
        {
            BOOST_REQUIRE( status == ClientErrors::None );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateHandshakeAbort );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateHandshakeComplete );

            status = tap.generateCommandHandshake (transfer, traceList);

            status = fillXfer(trx, transfer, __LINE__);

            status = tap.decodeResponseHandshake (transfer, status, traceList);
        }

        BOOST_REQUIRE_EQUAL( status, ClientErrors::None );
        BOOST_REQUIRE_EQUAL( tap.getCurrentState(), CtiDeviceIED::StateHandshakeComplete );
    }

    //  PerformRequestedCmd
    {
        std::vector<Transaction> transactions = {
            //  <STX>7632140090<CR>he0921484b2g<CR><ETX>553<CR>
            {"02 37 36 33 32 31 34 30 30 39 30 0d"
             " 68 65 30 39 32 31 34 38 34 62 32 67 0d 03"
             " 35 35 33 0d",
                            //  <CR>
                                "0d"},
            //                  <ACK>
            {"",                "06"},
            //                  <CR>
            {"",                "0d"}};

        YukonError_t   status = ClientErrors::None;

        for( auto trx : transactions )
        {
            BOOST_REQUIRE( status == ClientErrors::None );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateScanAbort );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateScanComplete );

            status = tap.generateCommand ( transfer , traceList);

            status = fillXfer(trx, transfer, __LINE__);

            status = tap.decodeResponse (transfer, status, traceList);

        }

        BOOST_REQUIRE_EQUAL( status, ClientErrors::None );
        BOOST_REQUIRE_EQUAL( tap.getCurrentState(), CtiDeviceIED::StateScanComplete );
    }

    tap.setLogOnNeeded(true);

    //  TerminateHandshake
    {
        YukonError_t   status = ClientErrors::None;

        std::vector<Transaction> transactions = {
            // <EOT><CR>
            {"04 0d",           ""}};

        for( auto trx : transactions )
        {
            BOOST_REQUIRE( status == ClientErrors::None );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateAbort );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateComplete );
            BOOST_REQUIRE( tap.getCurrentState() != CtiDeviceIED::StateCompleteNoHUP );

            status = tap.generateCommandDisconnect(transfer, traceList);

            status = fillXfer(trx, transfer, __LINE__);

            status = tap.decodeResponseDisconnect (transfer, status, traceList);
        }

        BOOST_CHECK_EQUAL( status, ClientErrors::None );
        BOOST_CHECK_EQUAL( tap.getCurrentState(), CtiDeviceIED::StateComplete );
    }

    tap.freeDataBins();
}

BOOST_AUTO_TEST_SUITE_END()

