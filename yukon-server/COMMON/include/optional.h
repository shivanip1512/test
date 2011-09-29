#pragma once

namespace Cti {

template <typename T>
class Optional
{
    T value;

    bool valid;

public:

    operator bool() const  {  return valid;  }

    Optional<T> &operator=(T rhs)  {  value = rhs;  valid = true;  return *this;  }

    Optional<T> &operator=(void *)  {  valid = false;  return *this;  }

    T &operator*()   {  return  value;  }
    T *operator->()  {  return &value;  }

    const T &operator*()  const  {  return  value;  }
    const T *operator->() const  {  return &value;  }

    static Optional<T> make_empty()  {  return Optional<T>() = 0;  }
};

template<typename T>
Optional<T> make_optional(T param)
{
    return Optional<T>() = param;
}

}
