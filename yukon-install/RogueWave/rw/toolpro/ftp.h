#ifndef _RWIFTP_H_
#define _RWIFTP_H_
/***************************************************************************
 *
 * ftp.h
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
#include <rw/toolpro/ftpimpl.h>

#include <rw/thr/ioureslt.h>

/*
 * RWIFtpClient
 *
 * RWIFtpClient provides low-level access to the FTP client-side
 * protocol. In most cases, method names parallel the names of
 * protocol actions. The RWIFtpClient class maintains a finite
 * state machine to inforce correct FTP protocol action ordering.
 * In the case of mis-ordered method invocation, an
 * RWIProtocolClientErr exception will be thrown.
 *
 * All client methods return IOUResult's redeemable for a
 * particular type of RWIFtpReply. RWIFtpReply and its subclasses
 * contain an encapsulation of the standard FTP protocol reply
 * messages. Specific subclasses of RWIFtpReply return additional
 * information specific to that type of protocol reply.
 *
 * RWIFtpClient object are lightweight. RWIFtpClient is implemented
 * using the interface-implementation pattern. The RWIFtpClient is
 * really a handle to an implementation that performs the protocol
 * interaction.
 *
 */

class RWINETExport RWIFtpClient {

  public:

    RWIFtpClient(void);
    // Default constructor

    RWIOUResult<RWIFtpReply>
    connect(const RWCString& host, int port=21);
    // provides connect method, host is expected to
    // be an IP address or host domain name.
    // A successful reply is normally in the 2XX family
    // signifing that a login sequence should be
    // attempted with the 'USER/PASS' methods.
    
    RWIOUResult<RWIFtpReply>
    user(const RWCString& user);
    // Performs the 'USER' (User Name) protocol action.
    // This is the first half of the login sequence,
    // the second half is the 'PASS' protocol action.
    // A successful reply is normally in the 3XX family
    // signifying that a 'PASS' action should be attempted.

    RWIOUResult<RWIFtpReply>
    pass(const RWCString& pass);
    // Performs the 'PASS' (Password) protocol action.
    // This is the second half of the login sequence,
    // it must be preceeded with a 'USER' protocol action
    // or a command sequence exception will be thrown.
    // A successful reply is normally in the 2XX family.
    
    RWIOUResult<RWIFtpReply>
    cwd(const RWCString& dir);
    // Performs the 'CWD' (Change Working Directory) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed successfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    cdup(void);
    // Performs the 'CDUP' (Change To Parent Directory) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed successfully.
    // A successful reply is normally in the 2XX family.
    
    RWIOUResult<RWIFtpPwdReply>
    pwd(void);
    // Performs the 'PWD' (Present Working Directory) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpDataReply>
    retr(const RWCString& fspec, int port=0);
    // Performs the 'RETR' (Return File) protocol action.
    // The fspec value specifies the file on the server side
    // to send. See below for notes on the port parameter.
    // A successful reply is normally in the 1XX family
    // signifying that a data channel has been opened for reading.
    // A call to the 'dataClose', 'dataAbort', or 'dataUrgentAbort'
    // method must follow the 'RETR' protocol action.

    // ----------------------------------------------------------------
    // Notes on the port parameter:
    //   (used in RETR, STOR, STOU, LIST, NLIST)
    //
    // The port value controls what port will be used
    // for the transfer as well as the data connection direction.
    // If the value of port is 0 (the default) then RWIFtpClient
    // will select a port for the connection from SERVER to CLIENT using
    // the protocol PORT command.  If the value of port is greater then
    // zero then that port will be used for the data connection from
    // the SERVER to CLIENT using the 'PORT' protocol command.  If the
    // value of port is -1 then a connection from CLIENT to SERVER is
    // establish using the address and port specified in result from
    // a PASV protocol command. 
    // -----------------------------------------------------------------

    RWIOUResult<RWIFtpDataReply>
    stor(const RWCString& fspec, int port=0);
    // Performs the 'STOR' (Store File) protocol action.
    // The fspec parameter is the name to store the file as on
    // the server side.  See above for notes on the port parameter.
    // A successful reply is normally in the 1XX family
    // signifying that a data channel has been opened for writting.
    // A call to the 'dataClose', 'dataAbort', or 'dataUrgentAbort'
    // method must follow the 'STOR' protocol action.

    RWIOUResult<RWIFtpDataReply>
    stou(const RWCString& fileName, int port=0);
    // Performs the 'STOU' (Store File Unique) protocol action.
    // See notes above for information on the port parameter.
    // A successful reply is normally in the 2XX family and the
    // generated file name should be found in the text part of the
    // reply.
    // A call to the 'dataClose', 'dataAbort', or 'dataUrgentAbort'
    // method must follow the 'STOU' protocol action.

    RWIOUResult<RWIFtpDataReply>
    list(const RWCString& path = "", int port=0);
    // Performs the 'LIST' (List Directory) protocol action.
    // The path parameter is the pathname that specifies a directory
    // or a file. A null argument of path implies the current working
    // or default directory.
    // See notes above for information on the port parameter.
    // A successful reply is normally in the 1XX family
    // signifying that a data channel has been opened for reading.
    // A call to the 'dataClose', 'dataAbort', or 'dataUrgentAbort'
    // method must follow the 'LIST' protocol action.

    RWIOUResult<RWIFtpDataReply>
    nlst(const RWCString& path = "", int port=0);
    // Performs the 'NLST' (Name List) protocol action.
    // The path parameter is the pathname that specifies a directory.
    // A null argument of path implies the current directory.
    // See notes above for information on the port parameter.   
    // A successful reply is normally in the 1XX family
    // signifying that a data channel has been opened for reading.
    // A call to the 'dataClose', 'dataAbort', or 'dataUrgentAbort'
    // method must follow the 'NLST' protocol action.

    RWIOUResult<RWIFtpReply>
    dataClose(void);
    // Performs a close of the data channel socket.
    // See notes above for information on the port parameter.       
    // This action is valid only after a successful 'RETR',
    // 'STOR', 'STOU', 'LIST' or 'NLST' protocol action.
    // A successful reply is normally in the 2XX family.    

    RWIOUResult<RWIFtpReply>
    dataAbort(void);
    // Performs the 'ABOR' (Abort) protocol action.
    // This action is valid after a successful 'RETR', 'STOR',
    // 'STOU', 'LIST', or 'NLST' protocol action.  This action
    // notifies the server that a data transfer abort is requested.
    // This version of the command is send as normal (in-band) data.
    // This method takes the place of 'dataClose'.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    dataUrgentAbort(void);
    // Performs the 'ABOR' (Abort) protocol action.
    // This action is valid after a successful 'RETR', 'STOR',
    // 'STOU', 'LIST' or 'NLST' protocol action.  This action
    // notifies the server that a data transfer abort is requested.
    // This version of the command is send as out-of-band data.
    //
    // Note: This command is somewhat dangerous because some servers
    //       will abort the entire session if they receive this action
    //       after they have finished sending all their data.  Try using
    //       the in-band version first.
    //
    // This method takes the place of 'dataClose'.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    type(const RWCString& t);
    // Performs a 'TYPE' (Transfer Type) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    quit(void);
    // Performs a 'QUIT' (Quit, disconnect) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    mkd(const RWCString& fspec);
    // Performs a 'MKD' (Make Directory) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    rmd(const RWCString& fspec);
    // Performs a 'RMD' (Remove Directory) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    dele(const RWCString& fspec);
    // Performs a 'DELE' (Delete File) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    rnfr(const RWCString& fspec);
    // Performs a 'RNFR' (Rename From) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in 3XX family 
    // signifying that a RNTO action should be attempted. 

    RWIOUResult<RWIFtpReply>
    rnto(const RWCString& fspec);
    // Performs a 'RNTO' (Rename To) protocol action.
    // This action is valid after the 'RNFR' protocol action
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family

    RWIOUResult<RWIFtpReply>
    noop(void);
    // Performs a 'NOOP' (No Operation) protocol action.
    // This action is valid in two situations: 1) after the
    // control-connection has been established successfully
    // with an FTP server. 2) after the USER/PASS login
    // negotiation has been completed successfully.
    // A successful reply is normally in the 2XX family.
    
    RWIOUResult<RWIFtpReply>
    site(const RWCString& specificSiteInfo = "");
    // Performs the 'SITE' (Site Information) protocol action.
    // This action is valid after either a control connection with
    // an FTP server has been established, or the 'USER/PASS' login
    // negotiation has been completed sucessfully.
    // The action with no argument (the default) provides general
    // site information, while the command with correct, server-
    // understandable specificSiteInfo provides specific information
    // on a specific site topic.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    syst(void);
    // Performs the 'SYST' (System Information) protocol action.
    // This action is valid after either a control connection with
    // an FTP server has been established, or the 'USER/PASS' login
    // negotiation has been completed sucessfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    help(const RWCString& specificCmd = "");
    // Performs the 'HELP' (Help) protocol action.
    // This action is valid after either a control connection with
    // an FTP server has been established, or the 'USER/PASS' login
    // negotiation has been completed sucessfully.
    // The action with no argument (the default) provides general
    // help information, while the command with a particular FTP
    // command (e.g. "help list") gives help information on that
    // command.
    // A successful reply is normally in the 2XX family and contains
    // the protocol actions understood by the server.

    RWIOUResult<RWIFtpReply>
    rein(void);
    // Performs a 'REIN' (Re-initialize) protocol action.
    // This action is valid after the 'USER/PASS' login negotiation
    // has been completed succesfully.
    // A successful reply is normally in the 2XX family.

    RWIOUResult<RWIFtpReply>
    exec(const RWCString& cmdarg);
    // Performs a generic protocol command.  Returns an RWIFtpReply.
    // This method can be used to access non-standard ftp protocol
    // actions in the server.  Actions executed with the 'exec' method
    // are assumed to be atomic in the ftp client state machine.
    
  private:

    RWNetHandle<RWIFtpClientImpl> impl_;
    // Implementation Handle
};

#endif
