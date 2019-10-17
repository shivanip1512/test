#pragma once

#include <boost/bimap.hpp>

#include <initializer_list>

namespace Cti {

template <typename L, typename R>
boost::bimap<L, R> make_bimap(std::initializer_list<typename boost::bimap<L, R>::value_type> list)
{
    return boost::bimap<L, R>(list.begin(), list.end());
}

}