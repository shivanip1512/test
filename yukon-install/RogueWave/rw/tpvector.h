#ifndef __RWTPVECTOR_H__
#define __RWTPVECTOR_H__

/*
 * RWTPtrVector: Parameterized vector of pointers to T
 *
 * $Id$
 *
 * Copyright (c) 1989-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 * Commercial Computer Software � Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 ***************************************************************************
 *
 * Stores a *pointer* to the item in the vector.  Basically, a typesafe
 * interface to RWPtrVector.
 *
 ***************************************************************************
 *
 * $Log$
 * Revision 1.1  2007/02/16 16:54:08  jdayton
 * Adding current RogueWave (yes, DST patched) files so that the build process can continue to move away from requiring mapped network drive locations for its needed files...
 *
 * Revision 7.11  1996/07/18 04:13:33  hart
 * ObjectStore changes
 *
 * Revision 7.10  1996/05/07 19:58:28  kyle
 * Added export pragma for Mac code fragments
 *
 * Revision 7.9  1996/03/18 16:04:45  jims
 * Port to cfront template mechanism
 *
 * Revision 7.8  1996/03/16 15:29:23  jims
 * Port non-stdlib persistence to DEC and Watcom
 *
 * Revision 7.7  1996/02/18 01:48:47  griswolf
 * Replace tabs with spaces, per Rogue Wave standard.
 *
 * Revision 7.6  1996/01/27 18:33:18  kevinj
 * Simple External Template Persistence (ETP).
 *
 * Revision 7.5  1995/12/11 22:30:41  jims
 * Remove 'Sca' from interior class names
 * Add 'rw_' to global helper classes
 * Change 'muterator' to 'filterator'
 *
 * Revision 7.4  1995/11/13 12:38:25  jims
 * return by const ref instead of by value
 *
 * Revision 7.3  1995/09/05 20:55:48  jims
 * Use new copyright macro
 *
 * Revision 7.2  1995/04/18  08:36:27  jims
 * Scopus TAN ID# 82: Use RWTExport to use templates with Tools DLL under
 * MSVC 2.x
 *
 * Revision 7.1  1994/10/16  03:07:10  josh
 * Merged 6.1 and 7.0 development trees
 *
 * Revision 6.2  1994/07/12  19:58:19  vriezen
 * Update Copyright notice
 *
 * Revision 6.1  1994/04/15  19:48:06  vriezen
 * Move all files to 6.1
 *
 * Revision 2.11  1994/01/03  23:33:28  jims
 * ObjectStore version: Add #include <ostore/ostore.hh>
 *
 * Revision 2.10  1993/12/31  00:56:30  jims
 * ObjectStore version: add get_os_typespec() static member function
 *
 * Revision 2.9  1993/12/10  19:44:18  jims
 * ObjectStore version: Add explicit copy constructors to avoid problem
 * with the ones generated by the OSCC compiler
 *
 * Revision 2.8  1993/09/10  02:56:53  keffer
 * Switched RCS idents to avoid spurious diffs
 *
 * Revision 2.7  1993/08/03  00:44:15  keffer
 * Changed return type of data().
 *
 * Revision 2.6  1993/04/09  19:35:45  keffer
 * Indexing is now done using size_t
 *
 * Revision 2.5  1993/03/24  01:29:51  keffer
 * Indexing operations now used unsigned
 *
 * Revision 2.4  1993/02/17  18:27:29  keffer
 * Now based on class RWPtrVector.
 *
 * Revision 2.3  1993/02/11  02:37:29  keffer
 * HP compiler does not understand access adjustment with templates.
 *
 * Revision 2.2  1993/01/28  02:39:24  keffer
 * Ported to cfront V3.0
 *
 *    Rev 1.0   19 Mar 1992 10:33:10   KEFFER
 * Initial revision.
 */

#ifndef __RWPVECTOR_H__
# include "rw/pvector.h"
#endif

#ifdef RW_PRAGMA_EXPORT
#pragma export on
#endif

template<class T> class RWTExport RWTPtrVector : private RWPtrVector
{

public:

  RWTPtrVector() {;}
  RWTPtrVector(size_t n) : RWPtrVector(n)       {;}
  RWTPtrVector(size_t n, T* p) : RWPtrVector(n, p) {;}

  RWTPtrVector<T>&      operator=(const RWTPtrVector<T>& v)
        {return (RWTPtrVector<T>&)RWPtrVector::operator=(v);}
  RWTPtrVector<T>&      operator=(T* p)
        {return (RWTPtrVector<T>&)RWPtrVector::operator=(p);}

  T*&           operator()(size_t n)
    {return (T*&)RWPtrVector::operator()(n);}
  T*const&      operator()(size_t n) const
    {return (T*const&) RWPtrVector::operator()(n);}
  T*&           operator[](size_t n)
    {return (T*&)RWPtrVector::operator[](n);}
  T*const&      operator[](size_t n) const
    {return (T*const&) RWPtrVector::operator[](n);}

  T* const *    data() const          {return (T* const *)RWPtrVector::data();}
  size_t        length() const        {return RWPtrVector::length();          }
  void          reshape(size_t N)     {RWPtrVector::reshape(N);               }
  void          resize(size_t N)      {RWPtrVector::resize(N);                }

  RW_T_TYPESPEC  /* This macro usually expands to nothing */

};

#include "rw/edefs.h"
RWDECLARE_PERSISTABLE_TEMPLATE(RWTPtrVector)

#ifdef RW_NO_TEMPLINST_ON_BASE
class RWExport RWpistream; class RWExport RWpostream;
class RWExport RWbistream; class RWExport RWbostream; 
class RWExport RWeistream; class RWExport RWeostream;
RWDECLARE_PERSIST_TEMPLATE_IO(RWTPtrVector,RWpistream,RWpostream)
RWDECLARE_PERSIST_TEMPLATE_IO(RWTPtrVector,RWbistream,RWbostream)
RWDECLARE_PERSIST_TEMPLATE_IO(RWTPtrVector,RWeistream,RWeostream)
#endif     

template <class T, class S>
void rwSaveGuts(S& str, const RWTPtrVector<T>& c);

template <class T, class S>
void rwRestoreGuts(S& str, RWTPtrVector<T>& c);


#if defined(_AIX) && !defined(RW_COMPILE_INSTANTIATE)
#pragma implementation ("tpvector.cc")
#endif

#ifdef RW_COMPILE_INSTANTIATE
# include "rw/tpvector.cc"
#endif

#ifdef RW_PRAGMA_EXPORT
#pragma export off
#endif

#endif /* __RWTPVECTOR_H__ */
