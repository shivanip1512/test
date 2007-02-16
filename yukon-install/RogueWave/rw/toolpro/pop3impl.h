#ifndef _RWIPOP3IMPL_H_
#define _RWIPOP3IMPL_H_
/***************************************************************************
 *
 * pop3impl.h
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

#include <rw/cstring.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/preply.h>

#include <rw/thr/barrier.h>
#include <rw/thr/iouescro.h>

// forward declaration
class RWIPop3FSM;

/*
 * RWIPop3ClientImpl
 *
 * RWIPop3ClientImpl is an implementation class of its interface counterpart
 * RWIPop3Client. The main functionality of RWIPop3ClientImpl is to handle
 * all POP3 commands dispatched from RWIPop3Client.
 *
 * RWIPop3ClientImpl also provides lock mechanisms for its interface to
 * have synchronization control in a multi-thread environment.
 *
 */

class RWINETExport RWIPop3ClientImpl {

  public:

    RWIPop3ClientImpl(void);
    // Default constructor.

    ~RWIPop3ClientImpl(void);
    // destructor

    void
    setArg(const RWCString& arg = "");
    // sets the command argument.

    void
    lock(void);
    // locks access to the body
    
    void
    unlock(void);
    // unlocks access to the body

    void
    wait(void);
    // synchronizes threads
    
    void
    connect(RWIOUEscrow<RWIPop3ConnReply> iouEscrow);
    // connects to server.

    void
    user(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // sends user

    void
    pass(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // sends password.

    void
    stat(RWIOUEscrow<RWIPop3StatReply> iouEscrow);
    // sends the stat protocol message.

    void
    list(RWIOUEscrow<RWIPop3DataReply> iouEscrow);
    // performs list protocol action

    void
    retr(RWIOUEscrow<RWIPop3DataReply> iouEscrow);
    // returns a mail message on the server.

    void
    dele(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // deletes a mail message on the server.

    void
    noop(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // performs a protocol noop.

    void
    quit(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // quits the exchange

    void
    top(RWIOUEscrow<RWIPop3DataReply> iouEscrow);
    // opens a data connection to the mail message.
    // Returns n number of lines of the text item.

    void
    uidl(RWIOUEscrow<RWIPop3DataReply> iouEscrow);
    // sends the uidl command

    void
    rset(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // sends the rset command
    
  private:
  
    RWCString                     arg_;
    RWIPop3FSM                    *sm_;

    RWMutex                       apiMutex_;
    RWMutex                       threadMutex_;
    RWBarrier                     tSync_;
};

#endif
