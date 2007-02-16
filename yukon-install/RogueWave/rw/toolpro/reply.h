#ifndef _RWIREPLY_H_
#define _RWIREPLY_H_
/***************************************************************************
 *
 * reply.h
 *
 * $Id: reply.h@#/main/7  02/11/98 09:33:56  jc (TPR0100_WIN32_19980305)
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

#include <rw/cstring.h>
#include <rw/tpslist.h>

#include <rw/toolpro/sockport.h>
#include <rw/toolpro/portstrm.h>
#include <rw/toolpro/inetaddr.h>

#include <rw/toolpro/inetdefs.h>

/*
 * RWIReply
 *
 * RWIReply is the abstract base class for all protocol replys. It
 * provides some common methods that all replies share.
 *
 */

class RWINETExport RWIReply {
  
  public:
    
    RWIReply(void);
    // Default constructor

    virtual void
    clearAndDestroy(void);
    // Resets the object to the state as after the default
    // construction
    
    virtual RWBoolean
    isComplete(void) const;
    // Returns whether the reply is complete.

    virtual RWBoolean
    isValid(void) const;
    // Returns whether the reply is valid.

  protected:

    virtual void readFromPortal(const RWSocketPortal& portal) = 0;
    // builds from socket data, it purl virtual function must be
    // overidden by a derived class.
  
    RWBoolean valid_;
    RWBoolean complete_;

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
      (RW_SL_IO_STD(ostream)& str, const RWIReply& r);
    // Outputs an RWIReply to an ostream
};
    
#endif
