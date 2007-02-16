/***************************************************************************
 *
 * tpmset.cc - template definitions for RWTPtrMultiSet.
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
 ****************************************************************************
 *
 * $Log$
 * Revision 1.1  2007/02/16 16:54:13  jdayton
 * Adding current RogueWave (yes, DST patched) files so that the build process can continue to move away from requiring mapped network drive locations for its needed files...
 *
 * Revision 7.5  1996/09/05 15:20:49  griswolf
 * Scopus #4418: Fix problem with diff type and size of size_t.
 *
 * Revision 7.4  1996/08/09 19:24:55  hart
 * HP (aCC compiler) port
 *
 * Revision 7.3  1996/02/12 21:03:06  kevinj
 * Switched stream and class args in rw*Guts.
 *
 * Revision 7.2  1996/01/25 01:51:32  kevinj
 * Improved ETP macros.
 *
 * Revision 7.1  1996/01/13 22:28:42  kevinj
 * Created.
 *
 *
 ****************************************************************************/

#include "rw/epersist.h"

RWDEFINE_PERSISTABLE_TEMPLATE_2(RWTPtrMultiSet)

template <class T, class C, class S>
void rwSaveGuts(S& str, const RWTPtrMultiSet<T,C>& c)
{
#ifdef RW_FIX_XSIZE_T
  str.putSizeT(c.entries());
#else
  str << c.entries();
#endif
  typename RWTPtrMultiSet<T,C>::const_iterator it = c.begin();

  while( it != c.end() ) {
    str << **it;
    it++;
  }
}

template <class T, class C, class S>
void rwRestoreGuts(S& str, RWTPtrMultiSet<T,C>& c)
{
  typedef typename RWTPtrMultiSet<T,C>::size_type size_type;
  size_type count;
#ifdef RW_FIX_XSIZE_T
  str.getSizeT(count);
#else
  str >> count;
#endif
  c.clear();
  for (size_type i=0; i < count; ++i) {
    T* p;
    str >> p;
    c.insert(p);
  }
}

