#ifndef _RWIDIRENT_
#define _RWIDIRENT_
/***************************************************************************
 *
 * dent.h
 *
 * $Id: dent.h@#/main/9  02/16/98 15:03:47  griswolf (TPR0100_WIN32_19980305)
 *
 * Copyright (c) 1998-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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

#include <stdio.h>

#include <rw/rwdate.h>
#include <rw/rwerr.h>
#include <rw/rwtime.h>
#include <rw/tpslist.h>
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/regexp.h>

#include <rw/toolpro/inetdefs.h>

/*
 * RWIDirEntry
 *
 * RWIDirEntry provides directory entry parsing of common directory
 * entry formats returned by common ftp servers.  Typically the
 * directory entry format is some variation on the UNIX 'ls' long
 * format.  The RWIDirEntry class is fairly robust in its ability
 * to make sense of the data presented to it. RWIDirEntry expects text
 * similar to the following:
 * 
 * FileType     Owner  Group    Size Date   Time  Name    Link
 *
 * drwxr-xr-x 3 fred   staff     512 Apr 24 16:14 pub
 * lrwxr-xr-x 1 fred   staff     512 Jan 10  1990 data -> /home/data
 * -rw-rw-rw- 1 fred   staff    1024 Apr 24 12:23 stuff

 * Often the owner and/or group field is missing.  This is handled
 * by the parse method.  Data type checks are performed on the
 * fields (ie Size is an integer, Time is XX:XX etc).  If any of
 * fields fail their format test then the object is set to the
 * invalid state and an error text message is saved.  This message
 * is available though the 'error' method.  If the directory entry
 * parse fails a copy of the original input string is available
 * through the 'data' method.
 *
 */

class RWINETExport RWIDirEntry {

  public:

    enum entryType { 
#ifndef RW_AVOID_PREPPOCESSOR_PROBLEMS
      DIRECTORY       = 0, 
      LINK            = 1, 
      FILE            = 2, 
      UNKNOWN         = 3,
#endif
      ftype_directory = 0, 
      ftype_link      = 1, 
      ftype_file      = 2, 
      ftype_unknown   = 3 
    };
    // Enumerates possible file types
        
    RWIDirEntry(void);
    // Default constructor
    // Constructs a default, invalid dir entry.
    
    RWIDirEntry(const RWCString& line);
    // Constructs a directory entry from a text buffer
    
    RWCString       data(void) const;
    // Gets a copy of the raw text line

    entryType type(void) const;
    // Gets the entry type (enum type)
    
    RWCString       name(void) const;
    // Gets the entry name
    
    int             size(void) const;
    // Gets the size of the file. Note: This value is
    // generally rounded up by the native file system
    // and should not be considered exact.
    
    RWTime          time(void) const;
    // Gets the time the file was created.  Note that
    // if the file was specified as a year rather then
    // time by the server this method will return
    // 12:00 PM.
      
    RWCString       link(void) const; 
    // If the file is actually a link then the actual
    // file path is returned.  If not an empty string
    // is returned.

    RWBoolean       isValid(void) const;
    // Returns TRUE if the directory entry was parsed successfully.
    // Note: Not all ftp servers return the directory information
    // in a consistant way.  For this reason the data() method
    // is provided to obtain a copy of the orignal text buffer.
    
    RWCString       error(void) const;
    // returns a text string that describes the problem that
    // was encountered during the directory entry parse phase.

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	(RW_SL_IO_STD(ostream)& str, const RWIDirEntry&);
    // Outputs an RWIDirEntry to an ostream
    
  private:

    void init(void);
    // Resets to the invalid state

    void parse(RWCString& line);
    // Parses the text representation

    void setInvalid(const RWCString& reason);
    // Sets the reason why it's invalid
  
    RWCString       data_;
    entryType       type_;
    RWCString       name_;
    RWCString       linkdest_;
    int             size_;
    RWTime          time_;
    int             isValid_;
    RWCString       reason_;
};
    
#endif
