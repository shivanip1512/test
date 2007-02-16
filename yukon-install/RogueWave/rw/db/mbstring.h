#ifndef __RWDB_MBSTRING_H__
#define __RWDB_MBSTRING_H__

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
 **************************************************************************
 *
 * Definition of class RWDBMBString
 *
 * This class is simple wrapper around RWCString.  Because databases 
 * diffentiate between multibyte strings and standard ASCII strings, 
 * DBTools.h++ must do the same.  Tools.h++ does not differentiate.  
 * It stores both type of strings in the class RWCString.  This class 
 * allows DBTools.h++ to treat multibyte string differently even though it 
 * ends up storing them in an instance of RWCString. 
 *
 **************************************************************************/


#include <rw/cstring.h>

class RWDBExport RWDBMBString : public RWCString
{
public:
  RWDBMBString () { ; }
  RWDBMBString(RWSize_T ic) : RWCString(ic) { ; }  // Suggested capacity
  RWDBMBString(const RWCString& S) : RWCString(S) { ; } // Copy constructor
  RWDBMBString(const RWDBMBString& S) : RWCString(S) { ; } // Copy constructor
  RWDBMBString(const char * a) : RWCString(a) { ; }            // Copy to embedded null
  RWDBMBString(const char * a, size_t N) : RWCString(a, N) { ; }  // Copy past any embedded nulls
  RWDBMBString(char c) : RWCString(c) { ; } 

#ifndef RW_NO_OVERLOAD_UCHAR
  RWDBMBString(unsigned char c) : RWCString(c) { ; }
#endif
#ifndef RW_NO_OVERLOAD_SCHAR
  RWDBMBString(signed char c) : RWCString(c) { ; }
#endif

  RWDBMBString(char c, size_t N) : RWCString(c, N) { ; }
  
  RWDBMBString(const RWCSubString& SS) : RWCString(SS) { ; }

  ~RWDBMBString() { ; }

  RWDBMBString&    operator=(const char* s) { RWCString::operator=(s);  return *this; }
  RWDBMBString&    operator=(const RWCString& s) { RWCString::operator=(s); return *this; }
  RWDBMBString&    operator=(const RWDBMBString& s) { RWCString::operator=(s); return *this; }

private:
    
};

#endif


