/***************************************************************************
 *
 * $Id$
 * Template definitions for RWTPtrMemTable<T,C>
 *    This memory table is parameterized on T, the type of object
 *    which is a 'row' of the table, and C, the collection class
 *    which implements the table. 
 * See the class reference for restrictions on T and C
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************/

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBTptrMemTable constructors, destructor, operator=
//                                                                     
//////////////////////////////////////////////////////////////////////////
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable(size_t max)
    : RWDBTMemTableBase(max)
{;}
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable(const RWDBTable& t, size_t max)
    : RWDBTMemTableBase(t.status(), max)
{
    RWDBReader aReader = t.reader();
    populate( aReader );
}
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable (const RWDBTable& t,
                                         const RWDBConnection& aConn,
                                         size_t max)
    : RWDBTMemTableBase(t.status(), max)
{
    RWDBReader aReader = t.reader( aConn );
    populate( aReader );
}
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable (const RWDBSelectorBase& selector,
                                         size_t max)
    : RWDBTMemTableBase(selector.status(), max)
{
    RWDBReader aReader = selector.reader();
    populate( aReader );
}
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable (const RWDBSelectorBase& selector,
                                         const RWDBConnection& aConn,
                                         size_t max)
    : RWDBTMemTableBase(selector.status(), max)
{
    RWDBReader aReader = selector.reader( aConn );
    populate( aReader );
}
template <class T, class C>
RWDBTPtrMemTable<T,C>::RWDBTPtrMemTable (RWDBReader& r, size_t max)
    : RWDBTMemTableBase(r.status(), max)
{
    populate (r);
}
template <class T, class C>
RWDBTPtrMemTable<T,C>::~RWDBTPtrMemTable()
{;}


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBTPtrMemTable<T,C>:: entries()
//                                                                     
//////////////////////////////////////////////////////////////////////////
template <class T, class C>
size_t
RWDBTPtrMemTable<T,C>::entries()
{
    return C::entries();
}

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBTPtrMemTable<T,C>:: operator[]
//                                                                     
//////////////////////////////////////////////////////////////////////////
template <class T, class C>
T*
RWDBTPtrMemTable<T,C>::operator[] (size_t i)
{
    return C::operator[](i);
}

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBTPtrMemTable<T,C>::readRow()
//                                                                     
//////////////////////////////////////////////////////////////////////////
template <class T, class C>
RWBoolean
RWDBTPtrMemTable<T,C>::readRow (RWDBReader& aReader)
{
    if (maxAllowedSize_ != 0 && entries() >= maxAllowedSize_) {
        return FALSE;
    }
    if (!aReader()) {
        return FALSE;
    }
    T* aTPtr = new T;
    aReader >> *aTPtr;
    insert(aTPtr);
    return TRUE;
}
