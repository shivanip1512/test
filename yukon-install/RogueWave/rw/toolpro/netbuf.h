#ifndef __RWNET_BUF_H__
#define __RWNET_BUF_H__
/***************************************************************************
 *
 * netbuf.h
 *
 * $Id$
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
/*
 * RWNetBuf: a Net.h++ buffer
 *
 * A Net.h++ buffer is a buffer of bytes of data plus flags
 * indicating something about the state of the buffer.  Not
 * to be confused with a TLI netbuf, which is something completely
 * different.
 * 
 * The buffer is implemented using an RWCString to hold the bytes.
 * There is no reason it has to be done this way.
 */

#include <rw/toolpro/netdefs.h>
#include <rw/cstring.h>

class RWNETExport RWNetBuf {
public:
  // The State enum indicates the state of the buffer.  Complex state
  // is indicated by or'ing together any of the following values.
  enum State {   
    normal=1,      // Nothing special
    eof=0          // End of file
  };
    
public:
  RWNetBuf();
  RWNetBuf(const RWCString& buf, State state=normal);
  // Construct a buffer 

  operator RWBoolean() const;  // Returns true if normal state
  operator RWCString() const;

private:
  RWCString buf_;
  State     state_;
};

#endif
