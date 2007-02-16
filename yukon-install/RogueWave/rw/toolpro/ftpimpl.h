#ifndef _RWIFTPIMPL_H_
#define _RWIFTPIMPL_H_
/***************************************************************************
 *
 * ftpimpl.h
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
class RWIFtpFSM;
 
/*
 * RWIFtpClientImpl
 *
 * RWIFtpClientImpl is an implementation class of its interface counterpart
 * RWIFtpClient. The main functionality of RWIFtpClientImpl is to handle
 * all FTP commands dispatched from RWIFtpClient.
 *
 * RWIFtpClientImpl also provides lock mechanisms for its interface to
 * have synchronization control in a multi-thread environment.
 *
 */

class RWINETExport RWIFtpClientImpl {

  public:

    RWIFtpClientImpl(void);
    // Default constructor

    ~RWIFtpClientImpl(void);
    // destructor

    void
    setArg(const RWCString& arg = "");
    // sets the command argument

    void
    setArg(int arg);
    // sets the command argument

    void
    lock(void);
    // sets a thread lock on the body

    void
    unlock(void);
    // clears the thread lock on the body

    void
    wait(void);
    // synchronizes threads.
        
    void
    connect(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the connect action

    void
    user(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the user action

    void
    pass(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the pass action

    void
    pwd(RWIOUEscrow<RWIFtpPwdReply> iouEscrow);
    // performs the pwd action

    void
    retr(RWIOUEscrow<RWIFtpDataReply> iouEscrow);
    // opens a read data connection

    void
    stor(RWIOUEscrow<RWIFtpDataReply> iouEscrow);
    // opens a write data connection

    void
    stou(RWIOUEscrow<RWIFtpDataReply> iouEscrow);
    // opens a write data connection

    void
    list(RWIOUEscrow<RWIFtpDataReply> iouEscrow);
    // opens a listing data connection

    void
    nlst(RWIOUEscrow<RWIFtpDataReply> iouEscrow);
    // opens a listing data connection

    void
    dataClose(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // closes the read data connection

    void
    dataAbort(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs an abort data connection command,
    // using in-band data.

    void
    dataUrgentAbort(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs an abort data connection command.
    // using out-of-band data.
    
    void
    rnfr(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs first half of rename

    void
    rnto(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs second half or rename

    void
    quit(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the quit action

    void
    noop(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the noop action

    void
    site(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the site action

    void
    help(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the help action

    void
    syst(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the syst action

    void
    rein(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs the rein action

    void
    exec(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a generic protocol action
    
  private:

    RWCString                     arg_;
    RWIFtpFSM                     *sm_;

    RWMutex                       apiMutex_;
    RWMutex                       threadMutex_;
    RWBarrier                     tSync_;
};

#endif
