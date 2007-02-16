#if !defined(__RWTHRPRODINFO_H__)
#define __RWTHRPRODINFO_H__
/*****************************************************************************
 *
 * prodinfo.h
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

prodinfo.h - Class declarations for:

   RWProductInfo - Base class for product-specific information classes.
                   Used to retrieve information about the current library.
         
See Also:

   prodinfo.cpp  - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWCSTRING_H__)
#     include <rw/cstring.h>
#  endif

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTPSLIST_H__)
#     include <rw/tpslist.h>
#  endif

class RWTHRExport RWProductInfo {
   
   RW_THR_DECLARE_TRACEABLE

   protected:

      RWTPtrSlist<RWProductInfo> dependencies_;

   public:

      virtual
      ~RWProductInfo(void) 
         RWTHRTHROWSNONE;

      RWCString
      productInfo(void) const
         RWTHRTHROWSANY;
            
      virtual
      RWCString 
      productVendorName(void) const 
         RWTHRTHROWSANY=0;
   
      virtual
      RWCString 
      productName(void) const
         RWTHRTHROWSANY=0;
   
      // Library or Executable
      virtual
      RWCString
      productType(void) const
         RWTHRTHROWSANY=0; 
      
      RWCString 
      productVersion(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      majorVersion(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      minorVersion(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      maintenanceVersion(void) const
         RWTHRTHROWSANY=0;
      
      virtual
      RWCString 
      productVersionLabel(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      productCopyright(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      buildTimeStamp(void) const
         RWTHRTHROWSANY=0;

      RWCString 
      productVariant(void) const
         RWTHRTHROWSANY;

      RWCString 
      targetPlatform(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString
      targetOSVendorName(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString
      targetOSName(void) const
         RWTHRTHROWSANY=0;
      
      virtual
      RWCString
      targetOSVersion(void) const
         RWTHRTHROWSANY=0;

      RWCString 
      targetCompiler(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      targetCompilerVendorName(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      targetCompilerName(void) const
         RWTHRTHROWSANY=0;

      virtual
      RWCString 
      targetCompilerVersion(void) const
         RWTHRTHROWSANY=0;
   
      RWCString
      configuration(void) const
         RWTHRTHROWSANY;

      // Debug or Release Version
      virtual
      RWCString 
      releaseType(void) const
         RWTHRTHROWSANY=0; 

      // N/A, Unsafe, Safe(Level 1), MT-Safe(Level 2)
      virtual
      RWCString 
      threadSafety(void) const
         RWTHRTHROWSANY=0; 

      // Compiler options, special releases (test, trace, evaluation) etc.
      virtual
      RWCString 
      additionalInfo(void) const
         RWTHRTHROWSANY=0; 

      // For future use in encoding variant information
      virtual
      RWCString 
      variantDescriptor(void) const
         RWTHRTHROWSANY=0; 

      // Product Info for products used by this product
      RWCString
      dependencies(void) const
         RWTHRTHROWSANY;

      // Number of products used with product info 
      size_t
      numberOfDependencies(void) const
         RWTHRTHROWSANY;

      // Get Product Info for products used by this product
      const RWProductInfo*
      dependency(size_t index) const
         RWTHRTHROWSANY;

      // Required for RWTPtrSlist
      inline
      int
      operator==(const RWProductInfo& second) const
         RWTHRTHROWSNONE;

      // Required for RWTPtrSlist (stdlib1 version)
      inline 
      int
      operator<(const RWProductInfo& second) const
         RWTHRTHROWSANY;
      
      // Required for RWTPtrSlist (stdlib2 version)
      inline 
      int
      operator>(const RWProductInfo& second) const
         RWTHRTHROWSANY;

   protected:
      
      RWProductInfo(void)
         RWTHRTHROWSNONE;

      void
      adoptDependency(RWProductInfo* usesInfo)
         RWTHRTHROWSANY;

};

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWTPtrSlist<RWProductInfo>;
#  if defined(RW_NO_STL)
template class RWTHRIExport RWTPtrSlink<RWProductInfo>;
template class RWTHRIExport RWTIsvSlist<RWTPtrSlink<RWProductInfo> >;
#  endif
#endif

/*****************************************************************************/

inline
RWProductInfo::RWProductInfo(void) 
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWProductInfo,RWProductInfo(void));
}

inline
int
RWProductInfo::operator==(const RWProductInfo& second) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWProductInfo,operator==(void) const:int);
   return (this == &second);
}

inline
int
RWProductInfo::operator<(const RWProductInfo& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWProductInfo,operator<(void) const:int);
   return (productName() < second.productName());
}

inline
int
RWProductInfo::operator>(const RWProductInfo& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWProductInfo,operator>(void) const:int);
   return (productName() > second.productName());
}

#endif // __RWTHRPRODINFO_H__

