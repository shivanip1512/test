#ifndef __RWDB_COLLECT_H__
#define __RWDB_COLLECT_H__

/**************************************************************************
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


#include <rw/db/defs.h>

#include <rw/collect.h>

/************************************************************************
 *                                                                      *
 * The macro RWDBDECLARE_COLLECTABLE_DLL should be included in the      *
 * declaration of any class that derives from RWCollectable.            *
 *                                                                      *
 ************************************************************************/

#ifndef RW_TRAILING_RWEXPORT
#define RWDBDECLARE_COLLECTABLE_DLL(className)                                 \
  public:                                                                      \
    virtual RWCollectable*      newSpecies() const;                            \
    virtual RWCollectable*      copy() const;                                  \
    virtual RWClassID           isA() const;                                   \
    static void enablePersistance ();                                          \
    static void disablePersistance ();                                         \
    friend rwdbexport  RWvistream& operator>>(RWvistream& s, className*& pCl)              \
      { pCl = (className*)RWCollectable::recursiveRestoreFrom(s); return s; }  \
    friend rwdbexport RWFile& operator>>(RWFile& f,     className*& pCl)              \
      { pCl = (className*)RWCollectable::recursiveRestoreFrom(f); return f; }
#else
#define RWDBDECLARE_COLLECTABLE_DLL(className)                                 \
  public:                                                                      \
    virtual RWCollectable*      newSpecies() const;                            \
    virtual RWCollectable*      copy() const;                                  \
    virtual RWClassID           isA() const;                                   \
    static void enablePersistance ();                                          \
    static void disablePersistance ();                                         \
    friend  RWvistream& rwdbexport operator>>(RWvistream& s, className*& pCl)              \
      { pCl = (className*)RWCollectable::recursiveRestoreFrom(s); return s; }  \
    friend  RWFile& rwdbexport operator>>(RWFile& f,     className*& pCl)              \
      { pCl = (className*)RWCollectable::recursiveRestoreFrom(f); return f; }
#endif

/************************************************************************
 *                                                                      *
 * The macro RWDEFINE_COLLECTABLE_DLL should be included in one ".cpp"  *
 * file to implement various functions for classing deriving from       *
 * RWCollectable.  It presently serves four purposes:                   *
 * 1) To provide a definition for newSpecies().                         *
 * 2) To provide a definition for copy().                               *
 * 3) To provide a definition for isA().                                *
 * 4) To define a "creator function" to be inserted into the            *
 *    one-of-a-kind global RWFactory pointed to by theFactory.          *
 *                                                                      *
 ************************************************************************/
#define RWDBDEFINE_COLLECTABLE_DLL(className, id)                              \
  /* Global function to create an instance of the class:*/                     \
  RWCollectable* rwCreateFN(className)()                                       \
    { return new className; }                                                  \
                                                                               \
  RWCollectable* className::newSpecies() const                                 \
    { return new className; }                                                  \
  RWCollectable* className::copy() const                                       \
    { return new className(*this); }                                           \
  RWClassID      className::isA() const                                        \
    { return id; }                                                             \
  void className::enablePersistance()                                          \
    { rwAddToFactory (rwCreateFN(className), id); }                        \
  void className::disablePersistance()                                         \
    { rwRemoveFromFactory (id); }


#if defined(RWDLL)

#   define RWDBDEFINE_COLLECTABLE   RWDBDEFINE_COLLECTABLE_DLL
#   define RWDBDECLARE_COLLECTABLE   RWDBDECLARE_COLLECTABLE_DLL

#else

#   define RWDBDEFINE_COLLECTABLE   RWDEFINE_COLLECTABLE
#   define RWDBDECLARE_COLLECTABLE   RWDECLARE_COLLECTABLE

#endif

#endif

