#include "boost/bind.hpp"
#include <vector>

namespace Cti {

template<typename test_fixture>
class TestRunner
{
    typedef typename test_fixture::test_case_type test_case_type;
    typedef typename test_fixture::result_type    result_type;

    typedef std::vector<result_type> result_vector;

    result_vector expected;
    result_vector results;

public:

    TestRunner(test_case_type *itr, test_case_type *end)
    {
        std::transform(itr, end, back_inserter(expected), bind(&test_case_type::expected, _1));
        std::transform(itr, end, back_inserter(results),  test_fixture());
    }

    typedef typename result_vector::const_iterator result_itr;

    result_itr expected_begin() const {
        return expected.begin();
    }
    result_itr expected_end()   const {
        return expected.end();
    }
    result_itr results_begin()  const {
        return results.begin();
    }
    result_itr results_end()    const {
        return results.end();
    }
};

struct byte_buffer
{
    typedef std::vector<unsigned char> vec;
    vec v;

    byte_buffer &operator, (unsigned char c) {  v.push_back(c);  return *this;  };
    byte_buffer &operator<<(unsigned char c) {  v.push_back(c);  return *this;  };

    vec::size_type size() const {  return v.size();  }
    vec::const_pointer data() const {  return &v.front();  }

    template<typename T>
    const T *data_as() const {  return reinterpret_cast<const T *>(&v.front());  }
    template<typename T>
    vec::size_type size_as() const {  return v.size() / sizeof(T);  }

    vec::const_iterator begin() const {  return v.begin();  }
    vec::const_iterator end()   const {  return v.end();  }

    template<typename T>
    void copy_to(T *t) const {  std::copy(v.begin(), v.end(), t);  }
};

}

