#include <boost/test/auto_unit_test.hpp>

#include "dnp_object_internalindications.h"
#include "dnp_object_analoginput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_counter.h"
#include "boostutil.h"

BOOST_AUTO_TEST_SUITE( test_prot_dnp )

const Cti::Protocols::DNP::TimeCTO *NoTimeCTO = 0;

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_internalindications)
{
    using Cti::Protocols::DNP::InternalIndications;

    const void *Null = 0;

    {
        InternalIndications iin(InternalIndications::II_InternalIndications);

        BOOST_CHECK_EQUAL(80,    iin.getGroup());
        BOOST_CHECK_EQUAL( 1,    iin.getVariation());
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(0,     iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(17,    iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(1,     iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NoTimeCTO));
    }

    {
        InternalIndications iin(InternalIndications::II_InternalIndications);

        BOOST_CHECK_EQUAL(80,    iin.getGroup());
        BOOST_CHECK_EQUAL( 1,    iin.getVariation());

        unsigned char buf = 17;

        iin.setValue(true);

        BOOST_CHECK_EQUAL( 1,    iin.getSerializedLen());
        BOOST_CHECK_EQUAL( 1,    iin.serialize(&buf));

        BOOST_CHECK_EQUAL( 1,    buf);

        iin.setValue(false);

        BOOST_CHECK_EQUAL( 1,    iin.getSerializedLen());
        BOOST_CHECK_EQUAL( 1,    iin.serialize(&buf));

        BOOST_CHECK_EQUAL( 0,    buf);
    }

    {
        InternalIndications iin(-1);

        BOOST_CHECK_EQUAL( 80,   iin.getGroup());
        BOOST_CHECK_EQUAL(255,   iin.getVariation());  //  getVariation returns an unsigned char
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(  0,   iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 17,   iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(  1,   iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NoTimeCTO));
    }

    {
        InternalIndications iin(2);

        BOOST_CHECK_EQUAL(80,    iin.getGroup());
        BOOST_CHECK_EQUAL( 2,    iin.getVariation());  //  getVariation returns an unsigned char
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 0,    iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(17,    iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 1,    iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NoTimeCTO));
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_binary_input)
{
    using Cti::Protocols::DNP::BinaryInput;

    gDNPOfflineNonUpdated = true;

    {
        BinaryInput bi(BinaryInput::BI_SingleBitPacked);

        unsigned char buf = 0x00;

        BOOST_CHECK_EQUAL( bi.restoreBits(&buf, 0, 1), 1 );

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryInput bi(BinaryInput::BI_SingleBitPacked);

        unsigned char buf = 0x01;

        BOOST_CHECK_EQUAL( bi.restoreBits(&buf, 0, 1), 1 );

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryInput bi(BinaryInput::BI_WithStatus);

        unsigned char buf = 0x00;

        BOOST_CHECK_EQUAL( bi.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }
    {
        BinaryInput bi(BinaryInput::BI_WithStatus);

        unsigned char buf = 0x01;

        BOOST_CHECK_EQUAL( bi.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryInput bi(BinaryInput::BI_WithStatus);

        unsigned char buf = 0x80;

        BOOST_CHECK_EQUAL( bi.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        BinaryInput bi(BinaryInput::BI_WithStatus);

        unsigned char buf = 0x81;

        BOOST_CHECK_EQUAL( bi.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bi.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
}


BOOST_AUTO_TEST_CASE(test_prot_dnp_object_binary_output)
{
    using Cti::Protocols::DNP::BinaryOutput;

    gDNPOfflineNonUpdated = true;

    {
        BinaryOutput bo(BinaryOutput::BO_SingleBit);

        unsigned char buf = 0x00;

        BOOST_CHECK_EQUAL( bo.restoreBits(&buf, 0, 1), 1 );

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryOutput bo(BinaryOutput::BO_SingleBit);

        unsigned char buf = 0x01;

        BOOST_CHECK_EQUAL( bo.restoreBits(&buf, 0, 1), 1 );

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryOutput bo(BinaryOutput::BO_WithStatus);

        unsigned char buf = 0x00;

        BOOST_CHECK_EQUAL( bo.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        BinaryOutput bo(BinaryOutput::BO_WithStatus);

        unsigned char buf = 0x01;

        BOOST_CHECK_EQUAL( bo.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   0 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        BinaryOutput bo(BinaryOutput::BO_WithStatus);

        unsigned char buf = 0x80;

        BOOST_CHECK_EQUAL( bo.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        BinaryOutput bo(BinaryOutput::BO_WithStatus);

        unsigned char buf = 0x81;

        BOOST_CHECK_EQUAL( bo.restore(&buf, 1), 1);

        std::unique_ptr<CtiPointDataMsg> pData(bo.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   1 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_analog_input)
{
    using Cti::Protocols::DNP::AnalogInput;

    gDNPOfflineNonUpdated = true;

    const unsigned char buf[] =
        { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    const unsigned char buf_offline[] =
        { 0, 2, 3, 4, 5, 6, 7, 8, 9 };

    {
        AnalogInput ai(AnalogInput::AI_16Bit);

        BOOST_CHECK_EQUAL( ai.restore(buf, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_16Bit);

        BOOST_CHECK_EQUAL( ai.restore(buf_offline, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_16BitNoFlag);

        BOOST_CHECK_EQUAL( ai.restore(buf, 2), 2);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   513 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_32Bit);

        BOOST_CHECK_EQUAL( ai.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_32Bit);

        BOOST_CHECK_EQUAL( ai.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_32BitNoFlag);

        BOOST_CHECK_EQUAL( ai.restore(buf, 4), 4);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   67305985 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_DoubleFloat);

        BOOST_CHECK_EQUAL( ai.restore(buf, 9), 9);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   3.7258e-265, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogInput ai(AnalogInput::AI_DoubleFloat);

        BOOST_CHECK_EQUAL( ai.restore(buf_offline, 9), 9);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   3.7258e-265, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogInput ai(AnalogInput::AI_SingleFloat);

        BOOST_CHECK_EQUAL( ai.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   6.2072e-36, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogInput ai(AnalogInput::AI_SingleFloat);

        BOOST_CHECK_EQUAL( ai.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ai.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   6.2072e-36, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_analog_output_status)
{
    using Cti::Protocols::DNP::AnalogOutputStatus;

    gDNPOfflineNonUpdated = true;

    const unsigned char buf[] =
        { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    const unsigned char buf_offline[] =
        { 0, 2, 3, 4, 5, 6, 7, 8, 9 };

    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_16Bit);

        BOOST_CHECK_EQUAL( ao.restore(buf, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_16Bit);

        BOOST_CHECK_EQUAL( ao.restore(buf_offline, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_32Bit);

        BOOST_CHECK_EQUAL( ao.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_32Bit);

        BOOST_CHECK_EQUAL( ao.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_DoubleFloat);

        BOOST_CHECK_EQUAL( ao.restore(buf, 9), 9);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   3.7258e-265, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_DoubleFloat);

        BOOST_CHECK_EQUAL( ao.restore(buf_offline, 9), 9);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   3.7258e-265, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_SingleFloat);

        BOOST_CHECK_EQUAL( ao.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   6.2072e-36, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        AnalogOutputStatus ao(AnalogOutputStatus::AOS_SingleFloat);

        BOOST_CHECK_EQUAL( ao.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(ao.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_CLOSE( pData->getValue(),   6.2072e-36, 0.001 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_counter)
{
    using Cti::Protocols::DNP::Counter;

    gDNPOfflineNonUpdated = true;

    const unsigned char buf[] =
        { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    const unsigned char buf_offline[] =
        { 0, 2, 3, 4, 5, 6, 7, 8, 9 };

    {
        Counter c(Counter::C_Binary16Bit);

        BOOST_CHECK_EQUAL( c.restore(buf, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        Counter c(Counter::C_Binary16Bit);

        BOOST_CHECK_EQUAL( c.restore(buf_offline, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        Counter c(Counter::C_Binary16BitNoFlag);

        BOOST_CHECK_EQUAL( c.restore(buf, 2), 2);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   513 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        Counter c(Counter::C_Binary32Bit);

        BOOST_CHECK_EQUAL( c.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        Counter c(Counter::C_Binary32Bit);

        BOOST_CHECK_EQUAL( c.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        Counter c(Counter::C_Binary32BitNoFlag);

        BOOST_CHECK_EQUAL( c.restore(buf, 4), 4);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   67305985 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        Counter c(Counter::C_Delta16Bit);

        BOOST_CHECK_EQUAL( c.restore(buf, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        Counter c(Counter::C_Delta16Bit);

        BOOST_CHECK_EQUAL( c.restore(buf_offline, 3), 3);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   770 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        Counter c(Counter::C_Delta16BitNoFlag);

        BOOST_CHECK_EQUAL( c.restore(buf, 2), 2);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   513 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }

    {
        Counter c(Counter::C_Delta32Bit);

        BOOST_CHECK_EQUAL( c.restore(buf, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
    {
        Counter c(Counter::C_Delta32Bit);

        BOOST_CHECK_EQUAL( c.restore(buf_offline, 5), 5);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   84148994 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NonUpdatedQuality );
    }

    {
        Counter c(Counter::C_Delta32BitNoFlag);

        BOOST_CHECK_EQUAL( c.restore(buf, 4), 4);

        std::unique_ptr<CtiPointDataMsg> pData(c.getPoint(NoTimeCTO));

        BOOST_REQUIRE(pData.get());

        BOOST_CHECK_EQUAL( pData->getValue(),   67305985 );
        BOOST_CHECK_EQUAL( pData->getQuality(), NormalQuality );
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_object_time_delay)
{
    using Cti::Protocols::DNP::TimeDelay;

    const unsigned char buf[] =
    { 77, 99 };

    {
        TimeDelay td(TimeDelay::TD_Coarse);

        BOOST_CHECK_EQUAL(td.restore(buf, 2), 2);

        BOOST_CHECK(td.isValid());
        BOOST_CHECK_EQUAL(td.getSeconds(),      25421);
        BOOST_CHECK_EQUAL(td.getMilliseconds(), 0);
    }
    {
        TimeDelay td(TimeDelay::TD_Fine);

        BOOST_CHECK_EQUAL(td.restore(buf, 2), 2);

        BOOST_CHECK(td.isValid());
        BOOST_CHECK_CLOSE(td.getSeconds(), 25.421, 0.00001);
        BOOST_CHECK_EQUAL(td.getMilliseconds(), 421);
    }
}

BOOST_AUTO_TEST_SUITE_END()
