#ifndef _RWISMTPIMPL_H_
#define _RWISMTPIMPL_H_
/***************************************************************************
 *
 * smtpimpl.h
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
#include <rw/toolpro/nreply.h>

#include <rw/thr/barrier.h>
#include <rw/thr/iouescro.h>

// forward declaration
class RWISmtpFSM;

/*
 * RWISmtpClientImpl
 *
 * RWISmtpClientImpl is an implementation class of its interface counterpart
 * RWISmtpClient. The main functionality of RWISmtpClientImpl is to handle
 * all SMTP commands dispatched from RWISmtpClient.
 *
 * RWISmtpClientImpl also provides lock mechanisms for its interface to
 * have synchronization control in a multi-thread environment.
 *
 */

class RWINETExport RWISmtpClientImpl {

  public:

    RWISmtpClientImpl(void);
    // Default constructor.

    ~RWISmtpClientImpl(void);
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
    connect(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // connects to server.

    void
    helo(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends helo protocol message.

    void
    mail(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends mail protocol message.

    void
    rcpt(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends the rcpt protocol message.

    void
    vrfy(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends the vrfy protocol message.

    void
    expn(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends the expn protocol message.

    void
    rset(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends the rset protocol message.

    void
    dataOpen(RWIOUEscrow<RWISmtpDataReply> iouEscrow);
    // opens the users data channel to the server

    void
    dataClose(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // closes the users data channels to the server

    void
    quit(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // quits the exchange

    void
    noop(RWIOUEscrow<RWISmtpReply> iouEscrow);
    // sends noop protocol message.

  private:
  
    RWCString                     arg_;
    RWISmtpFSM                    *sm_;

    RWMutex                       apiMutex_;
    RWMutex                       threadMutex_;
    RWBarrier                     tSync_;
};

#endif
