#ifndef __FIFO_MULTISET_H__
#define __FIFO_MULTISET_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <set>

template <class Element, class Compare=less<Element> >
struct ordered_pair_less : public std::binary_function<std::pair<Element, unsigned>,
                                                       std::pair<Element, unsigned>, bool>
{
    bool operator()(const std::pair<Element, unsigned> &lhs,
                    const std::pair<Element, unsigned> &rhs) const
    {
        bool less = Compare()(lhs.first, rhs.first);

        //  if the .first elements are equal, then compare the ordering element
        if( !less && !Compare()(rhs.first, lhs.first) )
        {
            less = lhs.second < rhs.second;
        }

        return less;
    }
};


template <class Element, class Compare=less<Element> >
class fifo_multiset : public std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> >
{
    //  we cannot exhaust this count - 10 requests every second would
    //     take 13+ years to hit 4 billion.  We can't talk that fast.
    unsigned count;

    typedef std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> > Inherited;

public:

    class iterator : public Inherited::iterator
    {
    public:

        typedef Inherited::iterator set_itr;

        iterator(set_itr itr) : set_itr(itr) { }
        iterator() : set_itr() { }

        operator bool()        {  return (*(static_cast<set_itr *>(this)))->first;  /*return *this;*/  }
        Element &operator->()  {  return (*(static_cast<set_itr *>(this)))->first;  /*return *this;*/  }
        Element &operator*()   {  return (*(static_cast<set_itr *>(this)))->first;  }
    };

    class const_iterator : public Inherited::iterator
    {
    public:

        typedef Inherited::iterator set_itr;

        const_iterator(set_itr itr) : set_itr(itr) { }
        const_iterator() : set_itr() { }

        operator bool() const        {  return (*(static_cast<const set_itr *>(this)))->first;  /*return *this;*/  }
        Element &operator->() const  {  return (*(static_cast<const set_itr *>(this)))->first;  /*return *this;*/  }
        Element &operator*()  const  {  return (*(static_cast<const set_itr *>(this)))->first;  }
    };

    iterator begin()  {  return iterator((static_cast<Inherited *>(this))->begin());  }
    iterator end()    {  return iterator((static_cast<Inherited *>(this))->end());    }

    const_iterator begin() const  {  return const_iterator((static_cast<const Inherited *>(this))->begin());  }
    const_iterator end()   const  {  return const_iterator((static_cast<const Inherited *>(this))->end());    }

    Inherited::_Pairib insert(Element element)
    {
        if( empty() )  count = 0;  //  if we ever get a breather, reset the FIFO count

        return Inherited::insert(std::make_pair(element, ++count));
    }
};


template <class T>
struct ptr_priority_sort : public std::binary_function<T *, T *, bool>
{
    bool operator()(const T *lhs, const T *rhs)
    {
        return (lhs && rhs)?(lhs->Priority > rhs->Priority):(rhs);
    }
};


#endif // __FIFO_MULTISET_H__

