
#ifndef __RWCOMM_PORTALSTREAMBUF_H__
#define __RWCOMM_PORTALSTREAMBUF_H__
/***************************************************************************
 *
 * portstrm.h
 *
 * $Id: portstrm.h@#/main/11  02/11/98 09:33:53  jc (TPR0100_WIN32_19980305)
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
 * RWPortalStreams: classes for iostream I/O using Portals
 *
 * The most important classes here from a user point of view are
 * RWPortalIStream and RWPortalOStream, the input and output streams
 * that use portals.
 */

#include <rw/toolpro/tprdefs.h>
#include <rw/toolpro/portal.h>
#include "rw/rstream.h"

/*
 * PortalStreambuf: A streambuf which is also a portal
 */

class RWNETExport RWPortalStreambuf : public RW_SL_IO_STD(streambuf) {
public:
  // Initialize and destroy the streambuf.
  RWPortalStreambuf();
  RWPortalStreambuf(const RWPortal& p);
  // Flush the buffer...
  ~RWPortalStreambuf();

  // Read/write using the portal once the buffer under/overflows.
  // This is where the action is.
  virtual int        overflow(int = EOF);
  virtual int        underflow();

  virtual int        sync();
  // Scopus #8299 flush the buffer but DO NOT THROW in destructor.
  virtual int        sync(int);

  // Set the communications channel that we are writing into.
  void setPortal(const RWPortal& p);

private:
  // Here is where all the overflows and underflows go!
  RWPortal portal_;
#ifndef RW_NO_IOSTD
  // makes maintaining the buffer easier
  char* base_;
#endif

  // Reset the get and put buffers
  void resetbuf();                           // old, deprecated
  void resetbuf(char* start, char* endmark); // new (for std::iostreams)
#ifndef RW_NO_IOSTD
// these inlines avoid changing code that depends on old streambuf API
  char* base() const {return this->base_;}
  int out_waiting() const { return pptr() - pbase();} // return int?
  int unbuffered() const {
    return 0 == base() || 0 == eback() || 
      (egptr() == eback() && epptr() == pbase());}
//stdlib2 defines return value of long instead of int. This ifdef may need to be enhanced as new comilers
//come out with stdlib 2 compliant standard libraries that are not Rogue Wave's
#if (defined (RW_RWV2X_STDLIB) && !defined (__TURBOC__))
  inline virtual long showmanyc() { return ( RW_SL_IO_STD(streamsize) (egptr() - gptr()) );}
#else
  inline virtual int showmanyc() { return ( RW_SL_IO_STD(streamsize) (egptr() - gptr()) );}
#endif
#endif
};

class RWNETExport RWPortalStreamBase : virtual public RW_SL_IO_STD(ios) {
public:
  // Construct a portal stream
  RWPortalStreamBase();
  RWPortalStreamBase(const RWPortal&);

  // Set the stream to use a portal to the same communications 
  // channel as p.
  void attach(const RWPortal& p)  {buf_.setPortal(p);}

  // Detach the stream from the portal.  If this is the last portal onto this
  // communications channel, then the channel will be closed.
  void detach();

protected:
  RWPortalStreambuf* getBufPointer() { return &buf_; }
private:
  // The streambuf.
  RWPortalStreambuf buf_;
};

class RWNETExport RWPortalIStream : public RWPortalStreamBase, public RW_SL_IO_STD(istream) {
public:
  // Construct a portal input stream
  RWPortalIStream();
  RWPortalIStream(const RWPortal&);
};

class RWNETExport RWPortalOStream : public RWPortalStreamBase, public RW_SL_IO_STD(ostream) {
public:
  // Construct a portal output stream
  RWPortalOStream();
  RWPortalOStream(const RWPortal&);
};
#endif 
