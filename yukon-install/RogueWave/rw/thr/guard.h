#if !defined(__RWTHRGUARD_H__)
#  define __RWTHRGUARD_H__
/*****************************************************************************
 *
 * guard.h
 *
 * $Id$
 *
 * Copyright (c) 1996-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 ****************************************************************************/

/*****************************************************************************

guard.h - Class declarations for:

   RWGuardBase<Resource>
      Base class for RWLockGuardBase and RWUnlockGuard.

   RWLockGuardBase<Resource>
      Base class for RWLockGuard and RWTryLockGuard.

   RWLockGuard<Resource>
      Guard class (acquire,release).
   
   RWTryLockGuard<Resource>
      Try-guard class (tryAcquire,release).

   RWUnlockGuard<Resource>
      Unlock guard class (release,acquire).


   RWReadGuardBase<Resource>
      Base class for RWReadLockGuardBase and RWReadUnlockGuard.

   RWReadLockGuardBase<Resource>
      Base class for RWReadLockGuard and RWTryReadLockGuard.

   RWReadLockGuard<Resource>
      Guard for read access (acquireRead, releaseRead).

   RWTryReadLockGuard<Resource>
      Guard for try-read access (tryAcquireRead, releaseRead).

   RWReadUnlockGuard<Resource>
      Guard for read access (releaseRead,acquireRead).


   RWWriteGuardBase<Resource>
      Base class for RWWriteLockGuardBase and RWWriteUnlockGuard.

   RWWriteLockGuardBase<Resource>
      Base class for RWWriteLockGuard and RWTryWriteLockGuard.

   RWWriteLockGuard<Resource>
      Guard for write access (acquireWrite, releaseWrite).

   RWTryWriteLockGuard<Resource>
      Guard for try-write access (tryAcquireWrite, releaseWrite).

   RWWriteUnlockGuard<Resource>
      Guard for write access (releaseWrite,acquireWrite).

See Also:

   guard.cc  - Out-of-line function definitions for classes.

*****************************************************************************/


#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

template <class Resource>
class RWTHRTExport RWGuardBase {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   protected:

      const Resource& resource_;
      RWBoolean acquired_;

   public:

      // Acquire the resource held by the guard (after release).
      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      acquire(void)
         RWTHRTHROWSANY;

      // Is the resource currently acquired?
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSNONE;

      // Release the resource held by the guard (after acquisition).
      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      release(void)
         RWTHRTHROWSANY;

   protected:

      // Construct a guard base instance
      RWGuardBase(Resource& resource)
         RWTHRTHROWSANY;

      // Dummy copy-constructor required by derived class copy-constructors
      RWGuardBase(const RWGuardBase<Resource>& second);

#     if defined(RW_THR_SUN_BUG_4227111)
         virtual ~RWGuardBase() {}
#     endif

      // Get a reference to the resource currently held by the guard
      Resource&
      resource(void) const
         RWTHRTHROWSNONE;
};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWLockGuardBase :
   public RWGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Release the resource only if acquired
      ~RWLockGuardBase(void)
         RWTHRTHROWSNONE;

   protected:

      // Construct and lock guard base instance
      RWLockGuardBase(Resource& resource)
         RWTHRTHROWSNONE;

      // Dummy copy-constructor required by derived class copy-constructors
      RWLockGuardBase(const RWLockGuardBase<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWLockGuard :
   public RWLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef  Resource  ResourceType;

   public:

      // Acquire the resource
      RWLockGuard(Resource& resource)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment
      
      RWLockGuard(const RWLockGuard<Resource>& second);

      RWLockGuard<Resource>&
      operator=(const RWLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWTryLockGuard:
   public RWLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Try to acquire the resource
      RWTryLockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Try to re-acquire the resource (after release).
      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment
      
      RWTryLockGuard(const RWTryLockGuard<Resource>& second);

      RWTryLockGuard<Resource>&
      operator=(const RWTryLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWUnlockGuard :
   public RWGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Acquire the resource
      RWUnlockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Reacquire the resource
      ~RWUnlockGuard(void)
         RWTHRTHROWSNONE;

   protected:

      // Disallow copy construction and assignment
      
      RWUnlockGuard(const RWUnlockGuard<Resource>& second);

      RWUnlockGuard<Resource>&
      operator=(const RWUnlockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWReadGuardBase {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   protected:

      const Resource& resource_;
      RWBoolean acquired_;

   public:

      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      acquire(void)
         RWTHRTHROWSANY;

      // Is the resource currently acquired for read?
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSNONE;

      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      release(void)
         RWTHRTHROWSANY;

   protected:

      // Construct a read guard base instance
      RWReadGuardBase(Resource& resource)
         RWTHRTHROWSANY;

      // Dummy copy-constructor required by derived class copy-constructors
      RWReadGuardBase(const RWReadGuardBase<Resource>& second);

      // Get a reference to the resource currently held by the guard
      Resource&
      resource(void) const
         RWTHRTHROWSNONE;

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWReadLockGuardBase :
   public RWReadGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Release the resource from read access, but only if acquired
      ~RWReadLockGuardBase(void)
         RWTHRTHROWSNONE;

   protected:

      // Construct a read lock guard base instance
      RWReadLockGuardBase(Resource& resource)
         RWTHRTHROWSANY;

      // Dummy copy-constructor required by derived class copy-constructors
      RWReadLockGuardBase(const RWReadLockGuardBase<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWReadLockGuard :
   public RWReadLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Acquire read access on a resource
      RWReadLockGuard(Resource& resource)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment

      RWReadLockGuard(const RWReadLockGuard<Resource>& second);

      RWReadLockGuard<Resource>&
      operator=(const RWReadLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWTryReadLockGuard :
   public RWReadLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef  Resource                ResourceType;

   public:

      // Try to acquire read access on the resource
      RWTryReadLockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Try to acquire read access on the resource (after release).
      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment

      RWTryReadLockGuard(const RWTryReadLockGuard<Resource>& second);

      RWTryReadLockGuard<Resource>&
      operator=(const RWTryReadLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWReadUnlockGuard :
   public RWReadGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Release read access on the resource
      RWReadUnlockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Reacquire read access on the resoruce
      ~RWReadUnlockGuard(void)
         RWTHRTHROWSNONE;

   protected:

      // Disallow copy construction and assignment

      RWReadUnlockGuard(const RWReadUnlockGuard<Resource>& second);

      RWReadUnlockGuard<Resource>&
      operator=(const RWReadUnlockGuard<Resource>& second);

};

/*****************************************************************************/
template <class Resource>
class RWTHRTExport RWWriteGuardBase {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   protected:

      const Resource& resource_;
      RWBoolean acquired_;

   public:

      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      acquire(void)
         RWTHRTHROWSANY;

      // Is the resource currently acquired for write?
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSNONE;

      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      void
      release(void)
         RWTHRTHROWSANY;

   protected:

      // Construct a write guard base instance
      RWWriteGuardBase(Resource& resource)
         RWTHRTHROWSANY;

      // Dummy copy-constructor required by derived class copy-constructors
      RWWriteGuardBase(const RWWriteGuardBase<Resource>& second);

      // Get a reference to the resource currently held by the guard
      Resource&
      resource(void) const
         RWTHRTHROWSNONE;

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWWriteLockGuardBase :
   public RWWriteGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Release the resource from write access, but only if acquired
      ~RWWriteLockGuardBase(void)
         RWTHRTHROWSNONE;

   protected:

      // Construct a write lock guard base instance
      RWWriteLockGuardBase(Resource& resource)
         RWTHRTHROWSANY;

      // Dummy copy-constructor required by derived class copy-constructors
      RWWriteLockGuardBase(const RWWriteLockGuardBase<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWWriteLockGuard :
   public RWWriteLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Acquire write access on a resource
      RWWriteLockGuard(Resource& resource)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment

      RWWriteLockGuard(const RWWriteLockGuard<Resource>& second);

      RWWriteLockGuard<Resource>&
      operator=(const RWWriteLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWTryWriteLockGuard :
   public RWWriteLockGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef  Resource                ResourceType;

   public:

      // Try to acquire write access on the resource
      RWTryWriteLockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Try to acquire write access on the resource (after release).
      // Use this member instead of directly manipulating the resource
      // so that the guard can still automatically release or acquire
      // the resource at destruction, as is appropriate.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWSANY;

   protected:

      // Disallow copy construction and assignment

      RWTryWriteLockGuard(const RWTryWriteLockGuard<Resource>& second);

      RWTryWriteLockGuard<Resource>&
      operator=(const RWTryWriteLockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
class RWTHRTExport RWWriteUnlockGuard :
   public RWWriteGuardBase<Resource> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Resource        ResourceType;

   public:

      // Release write access on the resource
      RWWriteUnlockGuard(Resource& resource)
         RWTHRTHROWSANY;

      // Reacquire write access on the resoruce
      ~RWWriteUnlockGuard(void)
         RWTHRTHROWSNONE;

   protected:

      // Disallow copy construction and assignment

      RWWriteUnlockGuard(const RWWriteUnlockGuard<Resource>& second);

      RWWriteUnlockGuard<Resource>&
      operator=(const RWWriteUnlockGuard<Resource>& second);

};

/*****************************************************************************/

template <class Resource>
inline
RWGuardBase<Resource>::RWGuardBase(Resource& resource)
   RWTHRTHROWSANY
   : 
      resource_(resource)
{
   RWTHRTRACEMFT1(RWGuardBase,Resource,RWGuardBase(Resource&));
}

template <class Resource>
inline
void
RWGuardBase<Resource>::acquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWGuardBase,Resource,acquire(void):void);
   
   if (acquired_ == FALSE) {
      resource().acquire();
      acquired_ = TRUE;
   }
}

template <class Resource>
inline
RWBoolean
RWGuardBase<Resource>::isAcquired(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWGuardBase,Resource,isAcquired(void) const:RWBoolean);
   return acquired_;
}

template <class Resource>
inline
void
RWGuardBase<Resource>::release(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWGuardBase,Resource,release(void):void);

   if (acquired_ == TRUE) {
      resource().release();
      acquired_ = FALSE;
   }
}

template <class Resource>
inline
RWGuardBase<Resource>::RWGuardBase(const RWGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      resource_(second.resource_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
Resource&
RWGuardBase<Resource>::resource(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWGuardBase,Resource,resource(void):Resource&);
   return RW_THR_CONST_CAST(Resource&, resource_);
}

/*****************************************************************************/

template <class Resource>
inline
RWLockGuardBase<Resource>::RWLockGuardBase(Resource& resource)
   RWTHRTHROWSNONE
   : 
      RWGuardBase<Resource>(resource)
{
   RWTHRTRACEMFT1(RWLockGuardBase,Resource,RWLockGuardBase(Resource&));
   // Don't do anything here - RWLockGuard and RWTryLockGuard 
   // constructors will do the right thing...
}

template <class Resource>
inline
RWLockGuardBase<Resource>::~RWLockGuardBase(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWLockGuardBase,Resource,~RWLockGuardBase(void));

   if (this->acquired_ == TRUE)
      (this->resource()).release();
}

template <class Resource>
inline
RWLockGuardBase<Resource>::RWLockGuardBase(const RWLockGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      RWGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

/*****************************************************************************/

template <class Resource>
inline
RWLockGuard<Resource>::RWLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWLockGuard<Resource>,RWLockGuard(Resource&));

   this->acquired_ = FALSE;
   (this->resource()).acquire();
   this->acquired_ = TRUE;
}

// Implement protected functions...

template <class Resource>
inline
RWLockGuard<Resource>::RWLockGuard(const RWLockGuard<Resource>& second)
   RWTHRTHROWSANY
   :
      RWLockGuardBase<Resource>(second)
{                                                       
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWLockGuard<Resource>&
RWLockGuard<Resource>::operator=(const RWLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWUnlockGuard<Resource>::RWUnlockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWGuardBase<Resource>(res)
{
   RWTHRTRACEMFT1(RWUnlockGuard,Resource,RWUnlockGuard(Resource&));

   this->acquired_ = TRUE;
   (this->resource()).release();
   this->acquired_ = FALSE;
}

template <class Resource>
inline
RWUnlockGuard<Resource>::~RWUnlockGuard(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWUnlockGuard,Resource,~RWUnlockGuard(void));

   if (this->acquired_ == FALSE)
      (this->resource()).acquire();
}

// Implement protected functions...

template <class Resource>
inline
RWUnlockGuard<Resource>::RWUnlockGuard(const RWUnlockGuard<Resource>& second)
   RWTHRTHROWSANY
   :
      RWGuardBase<Resource>(second)
{                                                       
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWUnlockGuard<Resource>&
RWUnlockGuard<Resource>::operator=(const RWUnlockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWTryLockGuard<Resource>::RWTryLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWTryLockGuard<Resource>,RWTryLockGuard(Resource&));
   this->acquired_ = FALSE;
   this->acquired_ = (this->resource()).tryAcquire();
}

template <class Resource>
inline
RWBoolean
RWTryLockGuard<Resource>::tryAcquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWTryLockGuard,Resource,tryAcquire(void):RWBoolean);

   if (this->acquired_ == FALSE)
      this->acquired_ = (this->resource()).tryAcquire();
   return this->acquired_;
}

// Implement protected functions...

template <class Resource>
inline
RWTryLockGuard<Resource>::RWTryLockGuard(const RWTryLockGuard<Resource>& second)
   : 
      RWLockGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWTryLockGuard<Resource>&
RWTryLockGuard<Resource>::operator=(const RWTryLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWReadGuardBase<Resource>::RWReadGuardBase(Resource& resource)
   RWTHRTHROWSANY
   : 
      resource_(resource)
{
   RWTHRTRACEMFT1(RWReadGuardBase,Resource,RWReadGuardBase(Resource&));
}

template <class Resource>
inline
void
RWReadGuardBase<Resource>::acquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWReadGuardBase,Resource,acquire(void):void);

   if (acquired_ == FALSE) {
      resource().acquireRead();
      acquired_ = TRUE;
   }
}

template <class Resource>
inline
RWBoolean
RWReadGuardBase<Resource>::isAcquired(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWReadGuardBase,Resource,isAcquired(void) const:RWBoolean);
   return acquired_;
}

template <class Resource>
inline
void
RWReadGuardBase<Resource>::release(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWReadGuardBase,Resource,release(void):void);

   if (acquired_ == TRUE) {
      resource().release();
      acquired_ = FALSE;
   }
}

template <class Resource>
inline
Resource&
RWReadGuardBase<Resource>::resource(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWReadGuardBase,Resource,resource(void):Resource&);
   return RW_THR_CONST_CAST(Resource&, resource_);
}

// Implement protected functions...

template <class Resource>
inline
RWReadGuardBase<Resource>::RWReadGuardBase(const RWReadGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      resource_(second.resource_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

/*****************************************************************************/

template <class Resource>
inline
RWReadLockGuardBase<Resource>::RWReadLockGuardBase(Resource& resource)
   RWTHRTHROWSANY
   : 
      RWReadGuardBase<Resource>(resource)
{
   RWTHRTRACEMFT1(RWReadLockGuardBase,Resource,RWReadLockGuardBase(Resource&));
   // Don't do anything here - RWReadLockGuard and RWTryReadLockGuard 
   // constructors will do the right thing...
}

template <class Resource>
inline
RWReadLockGuardBase<Resource>::~RWReadLockGuardBase(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWReadLockGuardBase,Resource,~RWReadLockGuardBase(void));

   if (this->acquired_ == TRUE)
      (this->resource()).release();
}

// Implement protected functions...

template <class Resource>
inline
RWReadLockGuardBase<Resource>::RWReadLockGuardBase(const RWReadLockGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      RWReadGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

/*****************************************************************************/

template <class Resource>
inline
RWReadLockGuard<Resource>::RWReadLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWReadLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWReadLockGuard<Resource>,RWReadLockGuard(Resource&));
   this->acquired_ = FALSE;
   (this->resource()).acquireRead();
   this->acquired_ = TRUE;
}

// Implement protected functions...

template <class Resource>
inline
RWReadLockGuard<Resource>::RWReadLockGuard(const RWReadLockGuard<Resource>& second)
   :
      RWReadLockGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWReadLockGuard<Resource>&
RWReadLockGuard<Resource>::operator=(const RWReadLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWReadUnlockGuard<Resource>::RWReadUnlockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWReadGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWReadUnlockGuard<Resource>,RWReadUnlockGuard(Resource&));
   this->acquired_ = TRUE;
   (this->resource()).release();
   this->acquired_ = FALSE;
}

template <class Resource>
inline
RWReadUnlockGuard<Resource>::~RWReadUnlockGuard(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWReadUnlockGuard,Resource,~RWReadUnlockGuard(void));

   if (this->acquired_ == FALSE)
      (this->resource()).acquireRead();
}

// Implement protected functions...

template <class Resource>
inline
RWReadUnlockGuard<Resource>::RWReadUnlockGuard(const RWReadUnlockGuard<Resource>& second)
   :
      RWReadGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWReadUnlockGuard<Resource>&
RWReadUnlockGuard<Resource>::operator=(const RWReadUnlockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWTryReadLockGuard<Resource>::RWTryReadLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWReadLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWTryReadLockGuard<Resource>,RWTryReadLockGuard(Resource&));
   this->acquired_ = FALSE;
   this->acquired_ = (this->resource()).tryAcquireRead();
}

template <class Resource>
inline
RWBoolean
RWTryReadLockGuard<Resource>::tryAcquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWTryReadLockGuard,Resource,tryAcquire(void):RWBoolean);

   if (this->acquired_ == FALSE)
      this->acquired_ = (this->resource()).tryAcquireRead();
   return this->acquired_;
}

// Implement protected functions...

template <class Resource>
inline
RWTryReadLockGuard<Resource>::RWTryReadLockGuard(const RWTryReadLockGuard<Resource>& second)
   :
      RWReadLockGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWTryReadLockGuard<Resource>&
RWTryReadLockGuard<Resource>::operator=(const RWTryReadLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/


template <class Resource>
inline
RWWriteGuardBase<Resource>::RWWriteGuardBase(Resource& resource)
   RWTHRTHROWSANY
   : 
      resource_(resource)
{
   RWTHRTRACEMFT1(RWWriteGuardBase,Resource,RWWriteGuardBase(Resource&));
}

template <class Resource>
inline
void
RWWriteGuardBase<Resource>::acquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWWriteGuardBase,Resource,acquire(void):void);

   if (acquired_ == FALSE) {
      resource().acquireWrite();
      acquired_ = TRUE;
   }
}

template <class Resource>
inline
RWBoolean
RWWriteGuardBase<Resource>::isAcquired(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWWriteGuardBase,Resource,isAcquired(void) const:RWBoolean);
   return acquired_;
}

template <class Resource>
inline
void
RWWriteGuardBase<Resource>::release(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWWriteGuardBase,Resource,release(void):void);

   if (acquired_ == TRUE) {
      resource().release();
      acquired_ = FALSE;
   }
}

template <class Resource>
inline
Resource&
RWWriteGuardBase<Resource>::resource(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWWriteGuardBase,Resource,resource(void):Resource&);
   return RW_THR_CONST_CAST(Resource&, resource_);
}

// Implement protected functions...

template <class Resource>
inline
RWWriteGuardBase<Resource>::RWWriteGuardBase(const RWWriteGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      resource_(second.resource_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

/*****************************************************************************/

template <class Resource>
inline
RWWriteLockGuardBase<Resource>::RWWriteLockGuardBase(Resource& resource)
   RWTHRTHROWSANY
   : 
      RWWriteGuardBase<Resource>(resource)
{
   RWTHRTRACEMFT1(RWWriteLockGuardBase,Resource,RWWriteLockGuardBase(Resource&));
   // Don't do anything here - RWWriteLockGuard and RWTryWriteLockGuard 
   // constructors will do the right thing...
}

template <class Resource>
inline
RWWriteLockGuardBase<Resource>::~RWWriteLockGuardBase(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWWriteLockGuardBase,Resource,~RWWriteLockGuardBase(void));

   if (this->acquired_ == TRUE)
      (this->resource()).release();
}

// Implement protected functions...

template <class Resource>
inline
RWWriteLockGuardBase<Resource>::RWWriteLockGuardBase(const RWWriteLockGuardBase<Resource>& second)
   RWTHRTHROWSANY
   : 
      RWWriteGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

/*****************************************************************************/

template <class Resource>
inline
RWWriteLockGuard<Resource>::RWWriteLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWWriteLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWWriteLockGuard<Resource>,RWWriteLockGuard(Resource&));
   this->acquired_ = FALSE;
   (this->resource()).acquireWrite();
   this->acquired_ = TRUE;
}

// Implement protected functions...

template <class Resource>
inline
RWWriteLockGuard<Resource>::RWWriteLockGuard(const RWWriteLockGuard<Resource>& second)
   :
      RWWriteLockGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWWriteLockGuard<Resource>&
RWWriteLockGuard<Resource>::operator=(const RWWriteLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWWriteUnlockGuard<Resource>::RWWriteUnlockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWWriteGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWWriteUnlockGuard<Resource>,RWWriteUnlockGuard(Resource&));
   this->acquired_ = TRUE;
   (this->resource()).release();
   this->acquired_ = FALSE;
}

template <class Resource>
inline
RWWriteUnlockGuard<Resource>::~RWWriteUnlockGuard(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWWriteUnlockGuard,Resource,~RWWriteUnlockGuard(void));

   if (this->acquired_ == FALSE)
      (this->resource()).acquireWrite();
}

// Implement protected functions...

template <class Resource>
inline
RWWriteUnlockGuard<Resource>::RWWriteUnlockGuard(const RWWriteUnlockGuard<Resource>& second)
   :
      RWWriteGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWWriteUnlockGuard<Resource>&
RWWriteUnlockGuard<Resource>::operator=(const RWWriteUnlockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

template <class Resource>
inline
RWTryWriteLockGuard<Resource>::RWTryWriteLockGuard(Resource& res)
   RWTHRTHROWSANY
   : 
      RWWriteLockGuardBase<Resource>(res)
{
   RWTHRTRACEMF(RWTryWriteLockGuard<Resource>,RWTryWriteLockGuard(Resource&));
   this->acquired_ = FALSE;
   this->acquired_ = (this->resource()).tryAcquireWrite();
}

template <class Resource>
inline
RWBoolean
RWTryWriteLockGuard<Resource>::tryAcquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWTryWriteLockGuard,Resource,tryAcquire(void):RWBoolean);

   if (this->acquired_ == FALSE)
      this->acquired_ = (this->resource()).tryAcquireWrite();
   return this->acquired_;
}

// Implement protected functions...

template <class Resource>
inline
RWTryWriteLockGuard<Resource>::RWTryWriteLockGuard(const RWTryWriteLockGuard<Resource>& second)
   :
      RWWriteLockGuardBase<Resource>(second)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
}

template <class Resource>
inline
RWTryWriteLockGuard<Resource>&
RWTryWriteLockGuard<Resource>::operator=(const RWTryWriteLockGuard<Resource>&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation
   return *this;
}

/*****************************************************************************/

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/guard.cc>
#  endif

#endif // __RWTHRGUARD_H__
