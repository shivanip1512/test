#include "boost/bind.hpp"
#include <vector>

namespace Cti {

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

