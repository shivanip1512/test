#pragma once

#include <vector>
#include <string>
#include <sstream>

#include <boost/shared_ptr.hpp>

namespace Cti {
namespace Simulator {

typedef std::vector<unsigned char> bytes;
typedef std::back_insert_iterator<bytes> byte_appender;

class EmetconWord;

typedef boost::shared_ptr<const EmetconWord> word_t;
typedef std::vector<word_t> words_t;

//  TODO-P2: replace with exceptions
struct error_t : public std::string
{
    enum status_t
    {
        success = true,
        error   = false
    };

    error_t(status_t status = success)
    {
        if( status != success  )
        {
            assign("(error)");
        }
    };

    error_t(const std::string &str) :
        std::string(str)
    { };

    template<class T>
    error_t(const std::string &str, T param)
    {
        std::ostringstream ostr;

        ostr << str << " (" << param << ")";

        assign(ostr.str());
    };

    //  if we have error text, we're an error (bool true)
    operator bool() const  {  return !empty();  };

    template<class T, class U>
    error_t &op(const T t, const std::string &op_str, const U u)
    {
        std::ostringstream ostr;

        ostr << " (" << t << " " << op_str << " " << u << ")";

        append(ostr.str());

        return *this;
    };

    template<class T, class U>  error_t &neq(const T t, const U u)  {  return op(t, "!=", u);  };
    template<class T, class U>  error_t &geq(const T t, const U u)  {  return op(t, ">=", u);  };
    template<class T, class U>  error_t &leq(const T t, const U u)  {  return op(t, "<=", u);  };
    template<class T, class U>  error_t &eq (const T t, const U u)  {  return op(t, "=",  u);  };
    template<class T, class U>  error_t &lt (const T t, const U u)  {  return op(t, "<",  u);  };
    template<class T, class U>  error_t &gt (const T t, const U u)  {  return op(t, ">",  u);  };
/*
    error_t &operator <<(const std::string &str);
    error_t &operator <<(int i);
*/
};

}
}

