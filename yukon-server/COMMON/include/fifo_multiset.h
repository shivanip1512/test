#ifndef __FIFO_MULTISET_H__
#define __FIFO_MULTISET_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <set>

template <class Element, class Compare=std::less<Element> >
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


template <class Element, class Compare=std::less<Element> >
class fifo_multiset : public std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> >
{
    //  we should not ever exhaust this count;
    //    10 requests every second would take 13+ years to hit 4 billion, and that's only
    //    if we never stopped talking.
    unsigned count;

    typedef std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> > Inherited;

public:

    class iterator : public Inherited::iterator
    {
        /*
            The syntax for this iterator is a little opaque.
            The big key is that set_itr is an iterator that points to a pair<Element, unsigned>.
            The /unsigned/ element defines the insert ordering, and the /Element/ is the actual element
            that's stored.
            When iterating over the set, we only care about the element, not the insert ordering.
            So, to hide the unsigned element and to make it appear as if the iterator is pointing directly
            at the element, fifo_multiset::iterator overloads operator* and operator-> to always reference
            the pair::first element.
        */
    public:

        typedef Inherited::iterator set_itr;

        iterator(set_itr itr) : set_itr(itr) { }
        iterator() : set_itr() { }

        //  bool() returns the implicit bool conversion of the Element - for pointers, this is a NULL check
        operator bool()        {  return (*(static_cast<set_itr *>(this)))->first;  }

        Element &operator*()  const  {  return (*(static_cast<const set_itr *>(this)))->first;  }
        //  operator->() requires an address as a return value in order to work properly
        Element *operator->() const  {  return &((*(static_cast<const set_itr *>(this)))->first);  }

        iterator &operator++()     {  ++(*(static_cast<set_itr *>(this)));  return *this; }
        iterator  operator++(int)  {  return (*(static_cast<set_itr *>(this)))++; }
    };

    class const_iterator : public Inherited::iterator
    {
    public:

        typedef Inherited::iterator set_itr;

        const_iterator(set_itr itr) : set_itr(itr) { }
        const_iterator() : set_itr() { }

        operator bool() const              {  return (*(static_cast<const set_itr *>(this)))->first;  }
        const Element &operator*()  const  {  return (*(static_cast<const set_itr *>(this)))->first;  }

        const Element *operator->() const  {  return &((*(static_cast<const set_itr *>(this)))->first);  }

        const_iterator &operator++()     {  ++(*(static_cast<set_itr *>(this)));  return *this; }
        const_iterator  operator++(int)  {  return (*(static_cast<set_itr *>(this)))++; }
    };

    iterator begin()  {  return iterator(Inherited::begin());  }
    iterator end()    {  return iterator(Inherited::end());    }

    const_iterator begin() const  {  return const_iterator(Inherited::begin());  }
    const_iterator end()   const  {  return const_iterator(Inherited::end());    }

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

