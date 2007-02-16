#ifndef __RWINCOUPLER_H__
#define __RWINCOUPLER_H__
/***************************************************************************
 *
 * coupler.h
 *
 * $Id: coupler.h@#/main/7  02/16/98 14:48:29  griswolf (TPR0100_WIN32_19980305)
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

#include <rw/rstream.h> /* get appropriate stream header */

#include <rw/cstring.h>

#include <rw/toolpro/portstrm.h>

#include <rw/toolpro/inetdefs.h>

/*
 * RWStreamCoupler
 *
 * RWStreamCoupler provides an automated mechanism for connecting
 * an input stream to an output stream. Used in conjunction with
 * portal streams (provided by Net.h++) and the Inter.Net.h++
 * product, the possibilities include coupling an ftp get to an
 * ftp put, an ftp get to a file, an ftp put to a file, and an
 * pop3 get to a file. Or, it can simply be used to automate
 * copying one file to another.
 *
 * The coupling mechanism respects cononical line termination in
 * ASCII mode, and will always use CR/LF pairs to terminate lines
 * when streaming out to a portal.
 * 
 * An optional end-of-input filter may be provided to terminate
 * the input stream prior to end-of-stream.
 *
 */

class RWINETExport RWStreamCoupler
{
  public:

    typedef RWBoolean(*Filter)(const RWCString&);
    // typedef of a Filter function that takes RWCString& as its
    // parameter. The purpose of a possible, customized Filter
    // function is to allow the termination of processing an input
    // stream prior to end-of-stream.

    enum TransferMode   {
#ifndef RW_AVOID_PREPPOCESSOR_PROBLEMS
      ASCII = 0, 
      BINARY = 1,
#endif
      mode_ascii = 0,
      mode_binary = 1
    };
    // enumerates which mode to transfer

    RWStreamCoupler(TransferMode mode = mode_ascii);
    // constructor

    RWBoolean 
    operator()(RW_SL_IO_STD(istream)& in,
	       RW_SL_IO_STD(ostream)& out);

    RWBoolean 
    operator()(RW_SL_IO_STD(istream)& in, RWPortalOStream& out);

    RWBoolean 
    operator()(RW_SL_IO_STD(istream)& in,
	       RW_SL_IO_STD(ostream)& out, Filter filter);

    RWBoolean 
    operator()(RW_SL_IO_STD(istream)& in, RWPortalOStream& out, Filter filter);
    // copies bytes from input stream to output stream, with the
    // filter, if provided.  If the output stream is a portal stream
    // and mode is ascii, then output lines are terminated with 
    // CR/LF pairs.

    void setMode(TransferMode mode);
    // sets transfer mode

  private:

    RWBoolean
    couple(RW_SL_IO_STD(istream)& istr,
	   RW_SL_IO_STD(ostream)& ostr,
	   RWBoolean bForceCRLF, Filter Filter);
    // performs the actual data copy.  If a filter is provided and
    // the transfer mode is ASCII, then each line is filtered prior
    // to passing it to the output stream. If bForceCRLF is TRUE and
    // the mode is ASCII, a CR/LF pair is used to terminate each line.
    // Otherwise, the endl stream-manipulator is used for line termination.

  private:

    TransferMode    mode_;
};

#endif
