#if !defined(__RWTHRVERSION_H__)
#  define __RWTHRVERSION_H__
/*****************************************************************************
 *
 * version.h
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

/******************************* Header File **********************************

version.h - Declarations for class:

   RWThreadsProductInfo - Threads.h++ product information class.
            
See Also:

   version.cpp  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHRPRODINFO_H__)
#     include <rw/thr/prodinfo.h>
#  endif

class RWTHRExport RWThreadsProductInfo : 
   public RWProductInfo {
   
   // Member Variables
   public:
   protected:
   private:

   // Member Functions
   public:

      RWThreadsProductInfo(void)
         RWTHRTHROWSANY;

      virtual
      ~RWThreadsProductInfo(void)
         RWTHRTHROWSNONE;

      virtual
      RWCString 
      productVendorName(void) const
         RWTHRTHROWSANY; // Use THR's Throw macros to maintain signature...
      
      virtual
      RWCString 
      productName(void) const
         RWTHRTHROWSANY;
      
      virtual
      RWCString
      productType(void) const
         RWTHRTHROWSANY;
      
      virtual
      RWCString 
      majorVersion(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      minorVersion(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      maintenanceVersion(void) const
         RWTHRTHROWSANY;
      
      virtual
      RWCString 
      productVersionLabel(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      productCopyright(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      buildTimeStamp(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString
      targetOSVendorName(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString
      targetOSName(void) const
         RWTHRTHROWSANY;
      
      virtual
      RWCString
      targetOSVersion(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      targetCompilerVendorName(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      targetCompilerName(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      targetCompilerVersion(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      releaseType(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      threadSafety(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      additionalInfo(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString
      threadAPI(void) const
         RWTHRTHROWSANY;

      virtual
      RWCString 
      variantDescriptor(void) const
         RWTHRTHROWSANY;

   protected:
   private:

};

#endif // __RWTHRVERSION_H__

