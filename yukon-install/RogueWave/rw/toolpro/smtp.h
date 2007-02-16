#ifndef _RWISMTP_H_
#define _RWISMTP_H_
/***************************************************************************
 *
 * smtp.h
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
#include <rw/mutex.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/smtpimpl.h>

#include <rw/thr/ioureslt.h>

/*
 * RWISmtpClient
 *
 * RWISmtpClient provides low-level access to the SMTP client-side
 * protocol. In most cases, method names parallel the names of
 * protocol actions. The RWISmtpClient class maintains a finite
 * state machine to inforce correct SMTP protocol action ordering.
 * In the case of mis-ordered method invocation, an
 * RWIProtocolClientErr exception will be thrown.
 *
 * All client methods return IOUResult's redeemable for a
 * particular type of RWISmtpReply. RWISmtpReply and its subclass
 * RWISmtpDataReply contain an encapsulation of the standard SMTP
 * protocol reply messages.
 *
 * RWISmtpClient object are lightweight. RWISmtpClient is implemented
 * using the interface-implementation pattern. The RWISmtpClient is
 * really a handle to an implementation that performs the protocol
 * interaction.
 *
 */

class RWINETExport RWISmtpClient {

  public:

    RWISmtpClient(void);
    // Default constructor

    RWIOUResult<RWISmtpReply>
    connect(const RWCString& host, int port=25);
    // provides a connect method, host is expected to
    // an IP address or host domain name.

    RWIOUResult<RWISmtpReply>
    helo(const RWCString& localMachine);
    // performs helo command

    RWIOUResult<RWISmtpReply>
    mail(const RWCString& from);
    // performs the from command

    RWIOUResult<RWISmtpReply>
    rcpt(const RWCString& to);
    // performs the to command

    RWIOUResult<RWISmtpReply>
    rset(void);
    // resets the server

    RWIOUResult<RWISmtpReply>
    vrfy(const RWCString& who);
    // verifies email address

    RWIOUResult<RWISmtpReply>
    expn(const RWCString& who);
    // verifies email address, expand maillist

    RWIOUResult<RWISmtpDataReply>
    dataOpen(void);
    // returns the write socket

    RWIOUResult<RWISmtpReply>
    dataClose(void);
    // closes the body of the message

    RWIOUResult<RWISmtpReply>
    quit(void);
    // quits command

    RWIOUResult<RWISmtpReply>
    noop(void);
    // performs a protocol noop
    
  private:

    // Implementation Handle
    RWNetHandle<RWISmtpClientImpl> impl_;
};

#endif
