#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"

#include "rfn_uom.h"

#include <boost/range/adaptor/indexed.hpp>

using Cti::Devices::Rfn::UomModifier1;
using Cti::Devices::Rfn::UomModifier2;

template <size_t Size>
using string_lists = std::array<std::initializer_list<std::string>, Size>;

BOOST_AUTO_TEST_SUITE(test_rfn_uom)

BOOST_AUTO_TEST_CASE(test_mod1_extension_bits)
{
    const UomModifier1 mod1_unset { 0x7fff };

    BOOST_CHECK_EQUAL(false, mod1_unset.getExtensionBit());

    const UomModifier1 mod1_set { 0x8000 };

    BOOST_CHECK_EQUAL(true, mod1_set.getExtensionBit());
}

BOOST_AUTO_TEST_CASE(test_mod1_of)
{
    const auto mod1_unset = UomModifier1::of( 0x7f, 0xff );

    BOOST_CHECK_EQUAL(false, mod1_unset.getExtensionBit());

    const auto mod1_set = UomModifier1::of( 0x80, 00 );

    BOOST_CHECK_EQUAL(true, mod1_set.getExtensionBit());
}

BOOST_AUTO_TEST_CASE(test_mod1_strings_empty)
{
    const UomModifier1 mod1_all_clear { 0x0000 };

    BOOST_CHECK(mod1_all_clear.getModifierStrings().empty());
}

BOOST_AUTO_TEST_CASE(test_mod1_strings_each_category)
{
    //  Set a modifier in each category
    constexpr unsigned short raw = 0b0'1'1'1'001'001'001'001;

    const auto expected = {
        "Daily Min", "Harmonic", "Phase B", "Primary", "Quadrant 3"
    };

    const UomModifier1 mod { raw };
    BOOST_CHECK_EQUAL_RANGES(expected, mod.getModifierStrings());
}

BOOST_AUTO_TEST_CASE(test_mod1_strings_2bit_values)
{
    //  Primary/secondary, phase, fundamental/harmonic, and TOU H/I are all 2 bits, so test them all together.
    constexpr unsigned short increment = 0b0'000'01'01'0000'01'01;

    const string_lists<4> expected { {
        { },  //  empty
        { "Fundamental", "Phase A", "Primary",   "TOU Rate H" },
        { "Harmonic",    "Phase B", "Secondary", "TOU Rate I" },
        {                "Phase C"                            },
    } };

    for( const auto iv : expected | boost::adaptors::indexed() )
    {
        const unsigned short raw = iv.index() * increment;

        BOOST_TEST_CONTEXT(iv.index())
        {
            const UomModifier1 mod { raw };
            BOOST_CHECK_EQUAL_RANGES(iv.value(), mod.getModifierStrings());
        }
    }
}

BOOST_AUTO_TEST_CASE(test_mod1_strings_quadrants)
{
    //  Just the quadrant bits
    constexpr unsigned short increment = 0b0'000'00'00'0001'00'00;

    const string_lists<16> expected { {
        {},  //  empty
        { "Quadrant 1" },
        {               "Quadrant 2" },
        { "Quadrant 1", "Quadrant 2" },
        {                             "Quadrant 3" },
        { "Quadrant 1",               "Quadrant 3" },
        {               "Quadrant 2", "Quadrant 3" },
        { "Quadrant 1", "Quadrant 2", "Quadrant 3" },
        {                                           "Quadrant 4" },
        { "Quadrant 1",                             "Quadrant 4" },
        {               "Quadrant 2",               "Quadrant 4" },
        { "Quadrant 1", "Quadrant 2",               "Quadrant 4" },
        {                             "Quadrant 3", "Quadrant 4" },
        { "Quadrant 1",               "Quadrant 3", "Quadrant 4" },
        {               "Quadrant 2", "Quadrant 3", "Quadrant 4" },
        { "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4" },
    } };

    for( const auto iv : expected | boost::adaptors::indexed() )
    {
        const unsigned short raw = iv.index() * increment;
        
        BOOST_TEST_CONTEXT(iv.index()) 
        {
            const UomModifier1 mod { raw };
            BOOST_CHECK_EQUAL_RANGES(iv.value(), mod.getModifierStrings());
        }
    }
}

BOOST_AUTO_TEST_CASE(test_mod1_strings_range)
{
    //  Just the range bits
    constexpr unsigned short increment = 0b0'001'00'00'0000'00'00;

    const string_lists<8> expected { {
        { },  //  empty
        { "Net Flow" },
        { "Min" },
        { "Avg" },
        { "Max" },
        { "Max Net Flow" },
        { "Daily Max" },
        { "Daily Min" },
    } };

    for( const auto iv : expected | boost::adaptors::indexed() )
    {
        const unsigned short raw = iv.index() * increment;

        BOOST_TEST_CONTEXT(iv.index())
        {
            const UomModifier1 mod { raw };
            BOOST_CHECK_EQUAL_RANGES(iv.value(), mod.getModifierStrings());
        }
    }
}

BOOST_AUTO_TEST_CASE(test_mod2_extension_bits)
{
    const UomModifier2 mod2_unset{ 0x7fff };

    BOOST_CHECK_EQUAL(false, mod2_unset.getExtensionBit());

    const UomModifier2 mod2_set{ 0x8000 };

    BOOST_CHECK_EQUAL(true, mod2_set.getExtensionBit());
}

BOOST_AUTO_TEST_CASE(test_mod2_of)
{
    const auto mod2_unset = UomModifier2::of( 0x7f, 0xff );

    BOOST_CHECK_EQUAL(false, mod2_unset.getExtensionBit());

    const auto mod2_set = UomModifier2::of( 0x80, 0x00 );

    BOOST_CHECK_EQUAL(true, mod2_set.getExtensionBit());
}

BOOST_AUTO_TEST_CASE(test_mod2_coincident)
{
    const UomModifier2 mod2_unset { 0b0'1'1'1'000'111'111'111 };

    BOOST_CHECK_EQUAL(0, mod2_unset.getCoincidentOffset());

    const UomModifier2 mod2_coin1 { 0b0'1'1'1'001'111'111'111 };

    BOOST_CHECK_EQUAL(1, mod2_coin1.getCoincidentOffset());

    const UomModifier2 mod2_coin7 { 0b0'1'1'1'111'111'111'111 };

    BOOST_CHECK_EQUAL(7, mod2_coin7.getCoincidentOffset());
}

BOOST_AUTO_TEST_CASE(test_mod2_strings_empty)
{
    const UomModifier2 mod2_all_clear { 0 };

    BOOST_CHECK(mod2_all_clear.getModifierStrings().empty());
    BOOST_CHECK_EQUAL(0, mod2_all_clear.getCoincidentOffset());
}

BOOST_AUTO_TEST_CASE(test_mod2_strings_each_category)
{
    //  Set a modifier in each category
    constexpr unsigned short raw = 0b0'1'1'1'001'001'001'001;

    const auto expected = {
        "Continuous Cumulative", "Cumulative", "Kilo", "Phase A->B", "Previous", "TOU Rate A"
    };

    const UomModifier2 mod { raw };
    BOOST_CHECK_EQUAL_RANGES(expected, mod.getModifierStrings());
    BOOST_CHECK_EQUAL(1, mod.getCoincidentOffset());
}

BOOST_AUTO_TEST_CASE(test_mod2_strings_3bit_values)
{
    //  Segmentation, rate, and coincident value are all 3 bits, so test them all together.
    //    Note that coincident does not show up as a string, but as getCoincidentOffset(), so it is not represented here.
    constexpr unsigned short increment = 0b0'0'0'0'001'000'001'001;

    const string_lists<8> expected { {
        { },  //  empty
        { "Phase A->B",            "TOU Rate A" },
        { "Phase B->C",            "TOU Rate B" },
        { "Phase C->A",            "TOU Rate C" },
        { "Phase Neutral->Ground", "TOU Rate D" },
        { "Phase A->Neutral",      "TOU Rate E" },
        { "Phase B->Neutral",      "TOU Rate F" },
        { "Phase C->Neutral",      "TOU Rate G" },
    } };

    for( const auto iv : expected | boost::adaptors::indexed() )
    {
        const unsigned short raw = iv.index() * increment;

        BOOST_TEST_CONTEXT(iv.index())
        {
            const UomModifier2 mod { raw };
            BOOST_CHECK_EQUAL_RANGES(iv.value(), mod.getModifierStrings());
        }
    }
}

BOOST_AUTO_TEST_CASE(test_mod2_strings_1bit_values)
{
    //  Cumulative, continuous cumulative, and previous are all 1 bit, so test them all together.
    constexpr unsigned short raw = 0b0'1'1'1'000'000'000'000;

    const auto expected = {
        "Continuous Cumulative", "Cumulative", "Previous",
    };

    const UomModifier2 mod { raw };
    BOOST_CHECK_EQUAL_RANGES(expected, mod.getModifierStrings());
}

BOOST_AUTO_TEST_CASE(test_mod2_scaling_factor)
{
    constexpr unsigned short increment = 0b1'000'000;

    const auto expected = {
        1.,
        1e3,
        1e6,
        1e9,
        0.,
        1e-1,
        1e-6,
        1e-3
    };

    for( const auto iv : expected | boost::adaptors::indexed() )
    {
        const unsigned short raw = iv.index() * increment;

        BOOST_TEST_CONTEXT(iv.index())
        {
            const UomModifier2 mod { static_cast<unsigned short>(iv.index() * increment) };
            BOOST_CHECK_CLOSE(iv.value(), mod.getScalingFactor(), 0.01);
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
