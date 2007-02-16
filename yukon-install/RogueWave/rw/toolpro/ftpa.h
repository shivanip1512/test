#ifndef _RWIFTPAGENT_
#define _RWIFTPAGENT_
/***************************************************************************
 *
 * ftpa.h
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

#include <rw/toolpro/sockport.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/agent.h>
#include <rw/toolpro/ftpai.h>

#include <rw/thr/ioureslt.h>

/*
 * RWIFtpAgent
 *
 * RWIFtpAgent is an easy-to-use Inter.Net.h++ class that does
 * basic FTP file and directory access. It handles more of the
 * details of the FTP protocol than the RWIFtpClient class.
 * However, it doesn't provide the flexibility of the RWIFtpClient
 * class.
 *
 * RWIFtpAgent performs actions in a transaction-based model
 * rather than the connection-based model of the RWIFtpClient.
 * The constructor stores connection information. The methods
 * perform interaction with the server by connecting, perform-
 * ing the requested action, and disconnecting with the server.
 * Multiple transactions may be performed before an RWIFtpAgent
 * object gets destroyed. Finally, the destructor cleans up
 * resources.
 *
 * An RWIFtpAgent object is lightweight. It is implemented
 * using interface-implementation pattern. The RWIFtpAgent
 * itself is really a handle to an implementation that performs
 * the protocol interaction.
 *
 */

class RWINETExport RWIFtpAgent : public RWIAgent {

public:
  
  enum TransferMode   { 
#ifndef RW_AVOID_PREPPOCESSOR_PROBLEMS
    ASCII          = 0, 
    BINARY         = 1, 
    LAST_XFER_MODE = 2,
#endif
    tmode_ascii    = 0, 
    tmode_binary   = 1, 
    tmode_latest   = 2
  };
  // Enum that specifies what type of tranfer we are requesting.
  // If the TransferMode is set to be tmode_latest, it will use 
  // the xfer mode (either ascii or binary) that was previously set.
  
  enum ConnectMode {
#ifndef RW_AVOID_PREPPOCESSOR_PROBLEMS
    ACTIVE  = 0, 
    PASSIVE = 1, 
#endif
    cmode_active  = 0, 
    cmode_passive = 1 
  };
  // Enum used to inform the server the type of data connection we
  // would like to use when building the data socket connection.
  // cmode_active states that we, as an active client, will be
  // making the data socket connection to the server. cmode_passive
  // states that we, as a passive client, will listen for a
  // connection from the server.
  
  RWIFtpAgent(void);
  // Default Constructor, builds an invalid object.  Use the
  // assignment operator or copy constructor to initialize.
  
  RWIFtpAgent(const RWCString& host, 
	      const RWCString& user, 
	      const RWCString& password
	      );
  // Constructs a valid object ready to use.  Host is domain name of
  // the ftp server of interest.  User is the user name to use when
  // logging in.  Password is the password to use.  For anonymous
  // ftp, use the user name "anonymous" and send a valid email
  // address for the password.
  
  RWIOUResult<RWSocketPortal>
  get(const RWCString& filepath,
      ConnectMode connMode = RWIFtpAgent::cmode_passive,
      TransferMode transMode = RWIFtpAgent::tmode_latest
      );
  // Gets a named file from the server.  Filepath is the complete
  // path to the file. TransMode tells the server the type of data
  // transfer we are interested, either ascii or binary. The
  // tmode_latest (the default) setting uses the mode that was
  // previously set, either ascii or binary. In the case of the
  // initial transaction of a login session, the tmode_latest
  // means ascii. ConnMode selects who makes the data connection,
  // cmode_passive tells the server to connect to the client, cmode_active
  // tells the server that the client will connect to it.
  // A call to the dataClose method must be made after the get
  // method is used.
  
  RWIOUResult<RWSocketPortal>
  put(const RWCString& filepath,
      ConnectMode connMode = RWIFtpAgent::cmode_passive,
      TransferMode transMode = RWIFtpAgent::tmode_latest
      );
  // Creates a named file on the server and opens a data connection to
  // the file. TransMode tells the server the type of data connection
  // we are interested, either ascii or binary. The tmode_latest (the
  // default) setting uses the mode that was previously set, either
  // ascii or binary. In the case of the initial transaction of a
  // login session, the tmode_latest means ascii. ConnMode selects who
  // makes the data connection, cmode_passive tells the server to
  // connect to the client, cmode_active tells the server that the
  // client will connect to it. A call to the dataClose method must be
  // made after the put method is used.
    
  RWIOUResult<RWSocketPortal>
  dir(const RWCString& path = "",
      ConnectMode connMode = RWIFtpAgent::cmode_passive
      );
  // Opens a data connection to the named directory. If the path is
  // null (the default), the current directory is presumed. ConnMode
  // selects who makes the data connection, cmode_passive tells the
  // server to connect to the client, cmode_active tells the server
  // that the client will connect to it. A call to the dataClose
  // method must be made after the get method is used.

  RWIOUResult<RWBoolean>
  dataClose(void);
  // Closes the data connection that was opened in the put, get, or
  // dir methods. A new put, get, or dir transaction may be
  // performed after a call to dataClose. The return value indicates
  // close success or failure.

  RWIOUResult<RWIFtpReply>
  mkdir(const RWCString& dirName);
  // Performs a "make directory" transaction, under the current
  // directory.
  
  RWIOUResult<RWIFtpReply>
  rmdir(const RWCString& dirName);
  // Performs a "remove directory" transaction, presuming dirName is
  // an existing directory under the current directory. 
  
  RWIOUResult<RWIFtpReply>
  cd(const RWCString& dirPath);
  // Performs a "change directory" transaction. If dirPath is "..",
  // the current directory is changed to the directory above (i.e.
  // the parent directory).
  
   RWIOUResult<RWIFtpPwdReply>
   pwd(void);
  // Performs a "current directory" transaction.

  RWIOUResult<RWIFtpReply>
  del(const RWCString& filePath);
  // Performs a "delete file" transaction.
   
  RWIOUResult<RWIFtpReply>
  rename(const RWCString& fileFrom, const RWCString& fileTo);
  // Performs a "rename file" transaction, under the current
  // directory.
    
private:
  RWIFtpAgentImpl*
  ftpImpl(void) const;
  // returns the underlying implementation pointer
  
  RWCString
    transferMode(TransferMode transMode) const;
  // returns transMode as string
  
  RWCString
  connectMode(ConnectMode connMode) const;
  // returns connMode as string
};

#endif
