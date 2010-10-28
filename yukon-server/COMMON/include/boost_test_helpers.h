#include "boost/bind.hpp"

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

}

