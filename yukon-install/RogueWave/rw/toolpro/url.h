#ifndef _RWURL_
#define _RWURL_
/***************************************************************************
 *
 * url.h
 *
 * $Id: url.h@#/main/11  02/11/98 09:34:17  jc (TPR0100_WIN32_19980305)
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

#include <rw/rstream.h>
#include <rw/cstring.h>
#include <rw/rwerr.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/ineterr.h>

/*
 * RWURL
 *
 * RWURL is a convenience class that encapsulates parsing and component
 * management of URL information.
 *
 * URL's are assumed to be in RFC 1738 format:
 *   <scheme>:<scheme data>
 *
 * RWURL has knowledge of some URL schemes implicitly, specifically URL's
 * supported by Inter.Net.h++ product as well as some other common URL
 * schemes. Specific URL schemes known to the class include:
 * FTP, HTTP, MAILTO, NEWS, NNTP, and FILE.
 *
 * URL schemes not known to RWURL will return FALSE from the isKnown()
 * method. URL schemes that are known but have basic syntactical errors
 * will return FALSE from the isValid() method.
 *
 */

class RWINETExport RWURL {

  public:

    RWURL(void);
    // Default Constructor.  

    RWURL(const RWCString& urlString);
    // Constructs from a const RWCString the represents the
    // URL.
    
    RWBoolean
    operator==(const RWURL& url) const;
    // Comparison operator

    RWCString scheme(void) const;
    // returns the protocol string (ie http/ftp)

    RWCString data(void) const;
    // returns the raw data
    
    RWCString host(void) const;
    // returns the host name portion of the URL

    RWCString path(void) const;
    // returns the path of the file or directory.
    
    RWCString port(void) const;
    // returns the port number specified, or empty
    // string if it does not exist.
    
    RWCString user(void) const;
    // returns the user name portion of the URL
    
    RWCString password(void) const;
    // returns the password name portion fo the URL

    RWCString mailAddress(void) const;
    // returns the mail addresss if mailto url

    RWCString newsGroup(void) const;
    // returns the newsgroup name

    RWCString newsArticle(void) const;
    // returns the news article
    
    RWCString type(void) const;
    // returns the ftp file transfer type

    RWCString search(void) const;
    // returns the http search argument.
    
    RWCString error(void) const;
    // Return the parse error string

    RWBoolean isValid(void) const;
    // Indicates that the URL type is known
    // however an error parsing was encountered.
    // A call to the data() method will return
    // the url string that was used during
    // attempted construction.  A call to the
    // error() method will return a descriptive
    // text string describing why the URL could
    // not be parsed.
    
    RWBoolean isKnown(void) const;
    // Indicates that the URL type is unknown
    // and no parsing was performed.  A call to the
    // data() method will return the url string that
    // was used during attempted construction.
   
  private:
 
    friend RW_SL_IO_STD(ostream)& operator<<
	(RW_SL_IO_STD(ostream)& str, const RWURL& url);
    // outputs an RWURL to an ostream 

  private:

    void      setInvalid(const RWCString& reason);
    // Sets the url to invalid

    void      setUnrecognized(const RWCString& reason);
    // Sets the url to unrecognized
    
    void      parse(RWCString& localURL);
    void      parseHTTP(RWCString& localURL);
    void      parseFTP(RWCString& localURL);
    void      parseMailTo(RWCString& localURL);
    void      parseFile(RWCString& localURL);
    void      parseNNTP(RWCString& localURL);
    void      parseNews(RWCString& localURL);
    void      parseTelnet(RWCString& localURL);
    void      clear(void);
    
    int       valid_;       // validity of url
    int       known_;       // whether the scheme is known
    
    RWCString host_;        // host name
    RWCString port_;        // host port
    RWCString path_;        // file path
    RWCString proto_;       // protocol to use
    RWCString user_;	    // user name
    RWCString password_;    // users password
    RWCString search_;      // http search argument
    RWCString type_;        // ftp file transfer type
    RWCString mailAddress_; // mailto: mailAddress
    RWCString reason_;      // why url is invalid
    RWCString newsArticle_; // news article
    RWCString newsGroup_;   // news group
    RWCString data_;        // raw data
};

inline RW_SL_IO_STD(ostream)& operator<<
   (RW_SL_IO_STD(ostream)& str, const RWURL& url)
    { str << url.data_; return(str); }
    
#endif
