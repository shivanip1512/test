#pragma once

#include "types.h"
#include <xutility>
#include <limits>

namespace Cti {
namespace Simulator {

struct EmetconWord
{
    enum
    {
        BitLength_Bch = 6
    };

    unsigned bch;

    enum WordTypes
    {
        WordType_A,
        WordType_B,
        WordType_C,
        WordType_D1,
        WordType_D2,
        WordType_D3,
        WordType_E,
        WordType_G,
        WordType_Invalid

    } type;

    static bool restoreWords(const bytes &buf, words_t &words);

    template <unsigned>
    static EmetconWord *restore(const bytes &buf);

    static unsigned extract_bits(const bytes &buf, unsigned bit_offset, unsigned length);
    static void     append_bits(long long &destination, unsigned bits, unsigned value);
    static void     write_bits (long long source, const unsigned bits, byte_appender output);
    static unsigned bch_calc   (long long source, const unsigned bits);

    struct serializer : public std::_Outit
    {
        byte_appender *output;

        serializer(byte_appender &output_) : output(&output_) {};

        serializer &operator *()    {  return *this;  };
        serializer &operator++()    {  return *this;  };
        serializer &operator++(int) {  return *this;  };

        serializer &operator =(const EmetconWord &word)
        {
            word.serialize(*output);

            return *this;
        };

        serializer &operator =(const word_t &word)
        {
            word && word->serialize(*output);

            return *this;
        };
    };

    virtual bool serialize(byte_appender output) const = 0;
};


template<unsigned type_code, unsigned bit_length>
struct EmetconWordDescriptor
{
    enum { TypeCode = type_code, BitLength = bit_length, Length = (bit_length + 7) / 8 };
};


struct EmetconWordA : public EmetconWord, public EmetconWordDescriptor<0x08, 30>
{
    EmetconWordA() :
        repeater_fixed(0),
        repeater_variable(0),
        group_address(0),
        shed_time(0),
        function_code(0)
    {};

    enum ShedTimes
    {
        ShedTime_0450,
        ShedTime_0900,
        ShedTime_1800,
        ShedTime_3600
    };

    unsigned repeater_fixed;
    unsigned repeater_variable;
    unsigned group_address;
    unsigned shed_time;
    unsigned function_code;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;
};


struct EmetconWordB : public EmetconWord, public EmetconWordDescriptor<0x0a, 52>
{
    EmetconWordB();
    EmetconWordB(unsigned repeater_fixed_,
                 unsigned repeater_variable_,
                 unsigned dlc_address_,
                 unsigned words_to_follow_,
                 unsigned function_code_,
                 bool function_,
                 bool write_,
                 unsigned bch_ = std::numeric_limits<unsigned>::max());

    bool write;
    bool function;

    unsigned repeater_fixed;
    unsigned repeater_variable;
    unsigned dlc_address;
    unsigned words_to_follow;
    unsigned function_code;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;
};

struct EmetconWordC : public EmetconWord, public EmetconWordDescriptor<0x0c, 52>
{
    EmetconWordC(const bytes &data_,
                 bool flag=false,
                 unsigned bch_ = std::numeric_limits<unsigned>::max());

    enum { PayloadLength = 5 };

    bytes data;
    bool flag;

    static unsigned words_needed(unsigned payload_length);

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;

    bytes getPayload() const;
};

struct EmetconWordD : public EmetconWord, public EmetconWordDescriptor<0x0d, 52>
{
    static unsigned words_needed(unsigned payload_length);
};

template<unsigned payload_length=0>
struct EmetconWordDPayload
{
    EmetconWordDPayload()
    {
        memset(data, 0, sizeof(data));
    };

    enum { PayloadLength = payload_length };

    unsigned char data[payload_length];
};

struct EmetconWordD1 : public EmetconWordD, public EmetconWordDPayload<3>
{
    EmetconWordD1() :
        repeater_variable(0),
        echo_address(0),
        power_fail(false),
        alarm(false)
    {};

    EmetconWordD1(unsigned repeater_variable,
                  unsigned echo_address,
                  unsigned char data0,
                  unsigned char data1,
                  unsigned char data2,
                  bool power_fail,
                  bool alarm,
                  unsigned bch = std::numeric_limits<unsigned>::max());

    unsigned repeater_variable;
    unsigned echo_address;
    bool power_fail;
    bool alarm;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;
};

struct EmetconWordD2 : public EmetconWordD, public EmetconWordDPayload<5>
{
    EmetconWordD2() :
        time_sync_loss(false),
        spare(false)
    {};

    EmetconWordD2(unsigned char data0,
                  unsigned char data1,
                  unsigned char data2,
                  unsigned char data3,
                  unsigned char data4,
                  bool time_sync_loss,
                  bool spare,
                  unsigned bch = std::numeric_limits<unsigned>::max());

    bool time_sync_loss;
    bool spare;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;
};

struct EmetconWordD3 : public EmetconWordD, public EmetconWordDPayload<5>
{
    EmetconWordD3() :
        spare1(false),
        spare2(false)
    {};

    EmetconWordD3(unsigned char data0,
                  unsigned char data1,
                  unsigned char data2,
                  unsigned char data3,
                  unsigned char data4,
                  bool spare1,
                  bool spare2,
                  unsigned bch = std::numeric_limits<unsigned>::max());

    bool spare1;
    bool spare2;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;
};

struct EmetconWordE : public EmetconWord, public EmetconWordDescriptor<0x0e, 52>
{
    EmetconWordE() :
        repeater_variable(0),
        echo_address(0),
        power_fail(false),
        alarm(false)
    {
        memset(diagnostic_data, 0, sizeof(diagnostic_data));
        bch = std::numeric_limits<unsigned>::max();
    };

    unsigned repeater_variable;
    unsigned echo_address;

    union
    {
        struct
        {
            unsigned char incoming_bch_error        : 1;
            unsigned char incoming_no_response      : 1;
            unsigned char listen_ahead_bch_error    : 1;
            unsigned char listen_ahead_no_response  : 1;
            unsigned char weak_signal               : 1;
            unsigned char repeater_code_mismatch    : 1;
        }
        errors;

        unsigned char diagnostic_data[3];
    };

    bool power_fail;
    bool alarm;

    static const EmetconWord *restore(const bytes &word_buf);

    virtual bool serialize(byte_appender output) const;

    void insertData(unsigned char buf[], int size);
    void getBytes(unsigned char buf[], int size);
};

}
}

