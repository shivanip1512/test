#include "atomic.h"

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

using boost::assign::list_of;

BOOST_AUTO_TEST_SUITE( test_string_formatter )

template<typename T>
struct TestAtomic
{
    const std::vector<T> _values;

    static std::vector<T> initues()
    {
        std::vector<T> values = list_of
                (0)
                (1)
                (-1)
                (std::numeric_limits<T>::min())
                (std::numeric_limits<T>::max())
                (std::numeric_limits<T>::min()+1)
                (std::numeric_limits<T>::max()-1)
                (std::numeric_limits<T>::min()-1)
                (std::numeric_limits<T>::max()+1);

        return values;
    }

    TestAtomic() :
        _values(initues())
    {}

    void run()
    {
        test_init();
        test_assign();
        test_add();
        test_substract();
        test_and();
        test_or();
        test_xor();
        test_pre_increment();
        test_pre_decrement();
        test_post_increment();
        test_post_decrement();
    }

    void test_init()
    {
        std::vector<T> act, exp;

        for each(const T val in _values)
        {
            Cti::Atomic<T> a(val);
            act.push_back(a);
            exp.push_back(val);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_assign()
    {
        std::vector<T> act, exp;

        Cti::Atomic<T> a;
        for each(const T val in _values)
        {
            a = val;
            act.push_back(a);
            exp.push_back(val);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_exchange()
    {
        std::vector<T> act, exp;

        for each(const T val in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                act.push_back(a.exchange(val));
                exp.push_back(e);
                e = val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_add()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                a += val; e += val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_substract()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                a -= val; e -= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_and()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                a &= val; e &= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_or()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                a |= val; e |= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_xor()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            for each(const T val in _values)
            {
                a ^= val; e ^= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_pre_increment()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            act.push_back(a);
            exp.push_back(e);
            act.push_back(++a);
            exp.push_back(++e);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_pre_decrement()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            act.push_back(a);
            exp.push_back(e);
            act.push_back(--a);
            exp.push_back(--e);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_post_increment()
    {
        std::vector<T> act, exp;

        for each(const T init in _values)
        {
            Cti::Atomic<T> a = init; T e = init;
            act.push_back(a++);
            exp.push_back(e++);
            act.push_back(a);
            exp.push_back(e);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_post_decrement()
    {
        std::vector<T> act, exp;
        {
            for each(const T init in _values)
            {
                Cti::Atomic<T> a = init; T e = init;
                act.push_back(a--);
                exp.push_back(e--);
                act.push_back(a);
                exp.push_back(e);
            }
        }
        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }
};

template<>
struct TestAtomic<bool>
{
    const std::vector<bool> _values;

    static std::vector<bool> initues()
    {
        std::vector<bool> values = list_of
                (true)
                (false);

        return values;
    }

    TestAtomic() :
        _values(initues())
    {}

    void run()
    {
        test_init();
        test_assign();
        test_exchange();
        test_and();
        test_or();
        test_xor();
    }

    void test_init()
    {
        std::vector<bool> act, exp;

        for each(const bool val in _values)
        {
            const Cti::Atomic<bool> a(val);
            act.push_back(a);
            exp.push_back(val);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_assign()
    {
        std::vector<bool> act, exp;

        Cti::Atomic<bool> a;
        for each(const bool val in _values)
        {
            a = val;
            act.push_back(a);
            exp.push_back(val);
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_exchange()
    {
        std::vector<bool> act, exp;

        for each(const bool init in _values)
        {
            Cti::Atomic<bool> a = init; bool e = init;
            for each(const bool val in _values)
            {
                act.push_back(a.exchange(val));
                exp.push_back(e);
                e = val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_and()
    {
        std::vector<bool> act, exp;

        for each(const bool init in _values)
        {
            Cti::Atomic<bool> a = init; bool e = init;
            for each(const bool val in _values)
            {
                a &= val; e &= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_or()
    {
        std::vector<bool> act, exp;

        for each(const bool init in _values)
        {
            Cti::Atomic<bool> a = init; bool e = init;
            for each(const bool val in _values)
            {
                a |= val; e |= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }

    void test_xor()
    {
        std::vector<bool> act, exp;

        for each(const bool init in _values)
        {
            Cti::Atomic<bool> a = init; bool e = init;
            for each(const bool val in _values)
            {
                a ^= val; e ^= val;
                act.push_back(a);
                exp.push_back(e);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                act.begin(), act.end(),
                exp.begin(), exp.end());
    }
};

template<typename T>
struct TestAtomic<T*>
{
    void run()
    {
        test_init();
        test_assign();
        test_exchange();
        test_add();
        test_substract();
        test_pre_increment();
        test_pre_decrement();
        test_post_increment();
        test_post_decrement();
    }

    void test_init()
    {
        T val;
        const Cti::Atomic<T*> a(&val);

        T* const act = a;
        T* const exp = &val;
        BOOST_CHECK_EQUAL(act, exp);
    }

    void test_assign()
    {
        T val;
        Cti::Atomic<T*> a;
        a = &val;

        T* const act = a;
        T* const exp = &val;
        BOOST_CHECK_EQUAL(act, exp);
    }

    void test_exchange()
    {
        T val1, val2;
        Cti::Atomic<T*> a = &val1;

        {
            T* const act = a.exchange(&val2);
            T* const exp = &val1;
            BOOST_CHECK_EQUAL(act, exp);
        }
        {
            T* const act = a;
            T* const exp = &val2;
            BOOST_CHECK_EQUAL(act, exp);
        }
    }

    void test_add()
    {
        T val[10];
        Cti::Atomic<T*> a = &val[0];
        a += 10;

        T* const act = a;
        T* const exp = &val[10];
        BOOST_CHECK_EQUAL(act, exp);
    }

    void test_substract()
    {
        T val[10];
        Cti::Atomic<T*> a = &val[10];
        a -= 10;

        T* const act = a;
        T* const exp = &val[0];
        BOOST_CHECK_EQUAL(act, exp);
    }

    void test_pre_increment()
    {
        T val[2];
        Cti::Atomic<T*> a = &val[0];
        T* e = &val[0];

        {
            T* const act = a;
            T* const exp = e;
            BOOST_CHECK_EQUAL(act, exp);
        }
        {
            T* const act = ++a;
            T* const exp = ++e;
            BOOST_CHECK_EQUAL(act, exp);
        }
    }

    void test_pre_decrement()
    {
        T val[2];
        Cti::Atomic<T*> a = &val[1];
        T* e = &val[1];

        {
            T* const act = a;
            T* const exp = e;
            BOOST_CHECK_EQUAL(act, exp);
        }
        {
            T* const act = --a;
            T* const exp = --e;
            BOOST_CHECK_EQUAL(act, exp);
        }
    }

    void test_post_increment()
    {
        T val[2];
        Cti::Atomic<T*> a = &val[0];
        T* e = &val[0];

        {
            T* const act = a++;
            T* const exp = e++;
            BOOST_CHECK_EQUAL(act, exp);
        }
        {
            T* const act = a;
            T* const exp = e;
            BOOST_CHECK_EQUAL(act, exp);
        }
    }

    void test_post_decrement()
    {
        T val[2];
        Cti::Atomic<T*> a = &val[1];
        T* e = &val[1];

        {
            T* const act = a--;
            T* const exp = e--;
            BOOST_CHECK_EQUAL(act, exp);
        }
        {
            T* const act = a;
            T* const exp = e;
            BOOST_CHECK_EQUAL(act, exp);
        }
    }
};

BOOST_AUTO_TEST_CASE(test_long)
{
    TestAtomic<long>().run();
}

BOOST_AUTO_TEST_CASE(test_ulong)
{
    TestAtomic<unsigned long>().run();
}

BOOST_AUTO_TEST_CASE(test_int)
{
    TestAtomic<int>().run();
}

BOOST_AUTO_TEST_CASE(test_uint)
{
    TestAtomic<unsigned int>().run();
}

BOOST_AUTO_TEST_CASE(test_short)
{
    TestAtomic<short>().run();
}

BOOST_AUTO_TEST_CASE(test_ushort)
{
    TestAtomic<unsigned short>().run();
}

BOOST_AUTO_TEST_CASE(test_char)
{
    TestAtomic<char>().run();
}

BOOST_AUTO_TEST_CASE(test_uchar)
{
    TestAtomic<unsigned char>().run();
}

BOOST_AUTO_TEST_CASE(test_bool)
{
    TestAtomic<bool>().run();
}

BOOST_AUTO_TEST_CASE(test_pointer_int)
{
    TestAtomic<int*>().run();
}

BOOST_AUTO_TEST_CASE(test_pointer_long)
{
    TestAtomic<long*>().run();
}

BOOST_AUTO_TEST_CASE(test_pointer_char)
{
    TestAtomic<char*>().run();
}

BOOST_AUTO_TEST_CASE(test_pointer_short)
{
    TestAtomic<short*>().run();
}

BOOST_AUTO_TEST_CASE(test_pointer_custom)
{
    struct BigClass
    {
    	std::string A, B;
    	long array[10];
    };
    
    TestAtomic<BigClass*>().run();
}

BOOST_AUTO_TEST_SUITE_END()
