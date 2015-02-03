#pragma once

#include <sstream>
#include <limits>
#include <boost/ptr_container/ptr_vector.hpp>
#include <boost/scoped_ptr.hpp>
#include "ctitime.h"

#include "message_factory.h"


/*-----------------------------------------------------------------------------
    default compare function
-----------------------------------------------------------------------------*/
template<typename type_t>
struct TestCompareMember
{
    static bool Compare( const type_t& outVal,
                         const type_t& expectedVal,
                               std::string& received,
                               std::string& expected )
    {
        ostringstream oss;
        oss << outVal;
        received = oss.str();

        oss.str("");
        oss << expectedVal;
        expected = oss.str();

        return ( outVal == expectedVal );
    }
};


/*-----------------------------------------------------------------------------
    vector compare function
-----------------------------------------------------------------------------*/
template<typename type_t>
struct TestCompareMember<std::vector<type_t>>
{
    static bool Compare( const std::vector<type_t>& outVal,
                         const std::vector<type_t>& expectedVal,
                               std::string& received,
                               std::string& expected )
    {
        if( outVal.size() != expectedVal.size() )
        {
            ostringstream oss;
            oss << "vector size= " << outVal.size();
            received = oss.str();

            oss.str("");
            oss << "vector size= " << expected.size();
            expected = oss.str();

            return false;
        }
        else
        {
            bool match = true;
            received = "";
            expected = "";

            for(int index = 0 ; index < outVal.size(); index++)
            {
                std::string tmp_received, tmp_expected;

                if( !TestCompareMember<type_t>::Compare( outVal[index], expectedVal[index], tmp_received, tmp_expected ))
                {
                    match = false;

                    ostringstream oss;
                    oss << "index: " << index << " " << tmp_received << " ";
                    received += oss.str();

                    oss.str("");
                    oss << "index: " << index << " " << tmp_expected << " ";
                    expected += oss.str();
                }
            }

            return match;
        }
    }
};


/*-----------------------------------------------------------------------------
    Test case base class
-----------------------------------------------------------------------------*/
template< typename message_t >
struct TestCaseBase
{
    std::string _failures;

    int _mismatchCount;

    std::auto_ptr<message_t> _imsg,
                             _omsg;

    TestCaseBase() : _mismatchCount(0)
    {
    }

    template<typename type_t>
    void CompareMember( const std::string& name,
                        const type_t&      expectedval,
                        const type_t&      oval )
    {
        std::string received, expected;

        if( !TestCompareMember<type_t>::Compare( oval, expectedval, received, expected ))
        {
            std::string details = "received: " + received + " - expected: " + expected;

            reportMismatch( name, details );
        }
    }

    void reportMismatch ( const std::string& name,
                          const std::string& details )
    {
        ++_mismatchCount;

        _failures += "mismatch \"" + name + "\" - " + details + "\n";
    }
};


/*-----------------------------------------------------------------------------
    Test case default template undefined
-----------------------------------------------------------------------------*/
template< typename >
struct TestCase;


/*-----------------------------------------------------------------------------
    Test sequence
-----------------------------------------------------------------------------*/
template<typename testcase_t>
struct TestSequence
{
    testcase_t _testcase;

    void DoSerialization()
    {
        std::vector<unsigned char> bytes;
        const string msg_type = Cti::Messaging::Serialization::g_messageFactory.serialize( *(_testcase._imsg.get()), bytes );
        _testcase._omsg = Cti::Messaging::Serialization::g_messageFactory.deserialize( msg_type, bytes );
    }

    int Run()
    {
        _testcase.Create();
        _testcase.Populate();

        DoSerialization();

        _testcase.Compare();

        if( _testcase._mismatchCount )
        {
            std::cout << endl;
            std::cout << "!!! testcase \"" << typeid(_testcase).name() << "\" has fail" << endl;
            std::cout << _testcase._failures << endl;
        }

        return _testcase._mismatchCount;
    }
};


/*-----------------------------------------------------------------------------
    pseudo random generator
-----------------------------------------------------------------------------*/
struct RandomGeneratorImpl
{
private:
    unsigned int u, v;

public:
    RandomGeneratorImpl() :
        u(0), v(0)
    {
    }

    void reset( unsigned int seed )
    {
        u = seed;
        v = seed;
    }

    unsigned int rand()
    {
        v = 36969*(v & 65535) + (v >> 16) + 1;
        u = 18000*(u & 65535) + (u >> 16) + 1;

        unsigned int val = (v << 16) + u;

        return val;
    }
};


struct RandomGenerator
{
private:
    static RandomGeneratorImpl& generator()
    {
        static RandomGeneratorImpl generator;
        return generator;
    }

public:
    static void reset( unsigned int seed )
    {
        generator().reset( seed );
    }

    static unsigned int rand()
    {
        return generator().rand();
    }
};


/*-----------------------------------------------------------------------------
    random generators global functions
-----------------------------------------------------------------------------*/
template <typename type_t>
type_t GenerateRandom()
{
    type_t val;
    TestRandomGenerator<type_t>::Generate( val );
    return val;
}

template <typename type_t, typename range_t >
type_t GenerateRandom( const range_t min, const range_t max )
{
    type_t val;
    TestRandomGenerator<type_t>::Generate( val, min, max );
    return val;
}

template <typename type_t>
type_t& GenerateRandom( type_t& val )
{
    TestRandomGenerator<type_t>::Generate( val );
    return val;
}

template <typename type_t, typename range_t >
type_t& GenerateRandom( type_t& val, const range_t min, const range_t max )
{
    TestRandomGenerator<type_t>::Generate( val, min, max );
    return val;
}

template <typename type_t, typename len_t >
type_t& GenerateRandom( type_t& val, const len_t len )
{
    TestRandomGenerator<type_t>::Generate( val, len );
    return val;
}

template <typename type_t, size_t N >
type_t& GenerateRandom( type_t& val, const type_t (&choices) [N] )
{
    val = choices[RandomGenerator::rand() % N];
    return val;
}


/*-----------------------------------------------------------------------------
    random generators specialized template
-----------------------------------------------------------------------------*/
template <typename type_t>
struct TestRandomGenerator
{
    static void Generate( type_t& val,
                          const type_t min = std::numeric_limits<type_t>::min(),
                          const type_t max = std::numeric_limits<type_t>::max() )
    {
        long long trand = RandomGenerator::rand();
        long long tmax  = max;
        long long tmin  = min;

        // please note : this formula only work for integer under 64 bits
        assert( (tmax - tmin + 1) > 0 && "random generator cannot be use with given range" );

        val = tmin + ( trand % ( tmax - tmin + 1 ));
    }
};


template <>
struct TestRandomGenerator<double>
{
    static void Generate( double& val,
                          const double min = -std::numeric_limits<double>::max() / 3,
                          const double max =  std::numeric_limits<double>::max() / 3 )
    {
        double d_rand = (double)RandomGenerator::rand() / 0xffffffff;
        val = min + d_rand * (max - min);
    }
};


template <>
struct TestRandomGenerator<float>
{
    static void Generate( float& val,
                          const float min = -std::numeric_limits<float>::max() / 3,
                          const float max =  std::numeric_limits<float>::max() / 3 )
    {
        double d_val, d_min = min, d_max = max;

        GenerateRandom( d_val, d_min, d_max );

        val = (float)d_val;
    }
};


template <>
struct TestRandomGenerator<std::string>
{
    // generate string of fixed length
    static void Generate( std::string& str,
                          const unsigned int len )
    {
        str = "";
        for( int i = 0; i < len; ++i )
        {
            str += GenerateRandom<char>( 32, 126 ); // refer to the ascii table
        }
    }

    // generate string of fixed length specifying min and max
    static void Generate( std::string& str,
                          const unsigned int min,
                          const unsigned int max )
    {
        unsigned int len;
        GenerateRandom( len, min, max );

        Generate( str, len );
    }

    // generate string default behavior
    static void Generate( std::string& str )
    {
        Generate( str, 10, 1000 );
    }
};


template <>
struct TestRandomGenerator<CtiTime>
{
    static void Generate( CtiTime& t,
                          const CtiTime min = CtiTime( 0x259EF1E0 ), // 01/01/1990
                          const CtiTime max = CtiTime( 0x967ACA60 )) // 01/01/2050
    {
        unsigned long seconds;
        GenerateRandom( seconds, min.seconds(), max.seconds() );
        t = CtiTime( seconds );
    }
};


template <typename type_t>
struct TestRandomGenerator<std::vector<type_t>>
{
    static void Generate( std::vector<type_t>& vec,
                          const unsigned int min = 10,
                          const unsigned int max = 1000 )
    {
        unsigned int len;
        GenerateRandom( len, min, max );

        vec.clear();
        for( int i = 0; i < len; ++i )
        {
            type_t val;
            GenerateRandom( val );
            vec.push_back( val );
        }
    }
};


template <>
struct TestRandomGenerator<bool>
{
    static void Generate( bool& val )
    {
        val = RandomGenerator::rand() % 2;
    }
};


/*-----------------------------------------------------------------------------
    Testcase Item
-----------------------------------------------------------------------------*/
template< typename type_t >
struct TestCaseItem
{
    TestCase<type_t> _tc;
    std::string _failures;

    ~TestCaseItem()
    {
        // the testcase item does not take ownership
        _tc._imsg.release();
        _tc._omsg.release();
    }

    void Populate( type_t* iitem )
    {
        _tc._imsg.reset( iitem );
        _tc.Populate();
    }

    bool Compare( type_t* oitem )
    {
        _tc._omsg.reset( oitem );
        _tc.Compare();

        _failures = _tc._failures;

        return ( _tc._mismatchCount == 0 );
    }
};

