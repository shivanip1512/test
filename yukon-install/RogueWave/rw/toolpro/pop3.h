#ifndef _RWIPOP3_H_
#define _RWIPOP3_H_
/***************************************************************************
 *
 * pop3.h
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
#include <rw/toolpro/pop3impl.h>

#include <rw/thr/ioureslt.h>

/*
 * RWIPop3Client
 *
 * RWIPop3Client provides low-level access to the POP3 client-side
 * protocol. In most cases, method names parallel the names of
 * protocol actions. The RWIPop3Client class maintains a finite
 * state machine to inforce correct POP3 protocol action ordering.
 * In the case of mis-ordered method invocation, an
 * RWIProtocolClientErr exception will be thrown.
 *
 * All client methods return IOUResult's redeemable for a
 * particular type of RWIPop3Reply. RWIPop3Reply and its subclasses
 * contain an encapsulation of the standard POP3 protocol reply
 * messages. Specific subclasses of RWIPop3Reply return additional
 * information specific to that type of protocol reply.
 *
 * RWIPop3Client object are lightweight. RWIPop3Client is implemented
 * using the interface-implementation pattern. The RWIPop3Client is
 * really a handle to an implementation that performs the protocol
 * interaction.
 *
 */

class RWINETExport RWIPop3Client {

  public:

    RWIPop3Client(void);
    // Default constructor
    
    RWIOUResult<RWIPop3ConnReply>
    connect(const RWCString& host, int port=110);
    // provides a connect method, host is expected to
    // an IP address or host domain name.

    RWIOUResult<RWIPop3Reply>
    user(const RWCString& user);
    // Informs server of pop mailbox name

    RWIOUResult<RWIPop3Reply>
    pass(const RWCString& password);
    // Informs the server of the password associated
    // with the mailbox name

    RWIOUResult<RWIPop3StatReply>
    stat(void);
    // Performs the status command

    RWIOUResult<RWIPop3DataReply>
    list(int message=0);
    // returns a data-read socket
    // NOTE:
    // If the list cmd is specified with no parameter, the
    // reply may look like the following line:
    // +OK ....
    // with the following data that can be read off from
    // the data socket:
    // 1 sizeOfMsg1
    // 2 sizeOfMsg2
    // ...
    // . (indicator of the data termination)
    // If the list cmd is specified with a message index
    // (e.g. list 1), the reply may be as follows:
    // +OK 1 sizeOfMsg1
    // and there is NO more data waiting in the socket to
    // read. Use RWIPop3DataReply's additionalData() method
    // to get the size info of the message (e.g., sizeOfMsg1
    // in the above example.)
    // --->
    // This is to say, you should NOT try to read data off
    // from the available socket, in the case of specifing
    // a message index in the command.

    RWIOUResult<RWIPop3DataReply>
    retr(int message);
    // hands back a data connection to the
    // requested mail message.  The message is
    // terminated with a line containing only
    // a period (".").

    RWIOUResult<RWIPop3Reply>
    dele(int message);
    // Deletes a message on the server.  The message
    // number must not be referred to again.

    RWIOUResult<RWIPop3Reply>
    noop(void);
    // performs a protocol noop

    RWIOUResult<RWIPop3DataReply>
    top(int message, int lines);
    // hands back a data connection to the
    // requested mail message.  Only the first
    // 'lines' number of lines will be returned.
    // The message is terminated with a line containing
    // a period (".")

    RWIOUResult<RWIPop3DataReply>
    uidl(int message=0);
    // returns a unique id for the numbered message.  This
    // message id is good across connections to the server.
    // NOTE:
    // If the uidl cmd is specified with no parameter, the
    // reply may look like the following line:
    // +OK ....
    // with the following data that can be read off from
    // the data socket:
    // 1 sizeOfMsg1
    // 2 sizeOfMsg2
    // ...
    // . (indicator of the data termination)
    // If the uidl cmd is specified with a message index
    // (e.g. uidl 1), the reply may be as follows:
    // +OK 1 id
    // and there is NO more data waiting in the socket to
    // read. Use RWIPop3DataReply's additionalData() method
    // to get the id of the message.
    // --->
    // This is to say, you should NOT try to read data off
    // from the available socket, in the case of specifing
    // a message index in the command.
 
    RWIOUResult<RWIPop3Reply>
    rset(void);
    // unmarks all messages that have been marked as deleted.
    // If there is no delete-marked message, rset does
    // nothing.

    RWIOUResult<RWIPop3Reply>
    quit(void);
    // quits command

  private:

    // Implementation Handle
    RWNetHandle<RWIPop3ClientImpl> impl_;

};

/*
 * function:
 *   pop3StreamFilter(const RWCString&)
 *
 * This function is used to indicate an end-of-data condition
 * on an POP3 input stream.  It can be passed as the filter argument
 * to the RWIStreamCoupler parentheses operator to assist in
 * coupling an POP3 message to an output stream.
 *
 * parameters:
 *   buffer - contains a null terminated string
 *
 * returns:
 *   If buffer == ".", then FALSE is returned.
 */

RWINETExportFunc(RWBoolean)
pop3StreamFilter(const RWCString& buffer);

#endif
