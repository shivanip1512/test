#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrtextfilebase.h
*
*    DATE: 04/14/2003
*
*    AUTHOR: David Sutton
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextfilebase.cpp-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2007/08/30 17:03:39 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Base class for all yukon based text file interfaces (not including ftp ones unless
*            I get around to re-writing them
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrtextfilebase.h,v $
      Revision 1.4  2007/08/30 17:03:39  tspar
      YUK-4318

      Fixed the way we read from files to be more efficient, and changed code flow to allow for more unit testing.

      Changed cparms to more show an intuitive difference between them.
      FDR_TEXTIMPORT_IMPORT_PATH is now
      FDR_TEXTIMPORT_DEFAULT_POINTIMPORT_PATH

      Changed the processe function that was causing a large memory leak over runtime.
      -Changed the code so it doesn't leak anymore, worked a long time trying to pin down the actual reason before just re-working the code.

      Changed the delete cparm to no longer default to true since we have two options, delete or rename.
      How it works now:
      1)specifying rename and delete will default to rename.
      2)not specifying either will default to delete.
      3)specifying one or the other will work as expected.

      Revision 1.3  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2.18.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.2  2003/04/22 20:44:47  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/
#ifndef __FDRTEXTFILEBASE_H__
#define __FDRTEXTFILEBASE_H__

#include "dlldefs.h"
#include "fdrinterface.h"
#include "TextFileInterfaceParts.h"

class IM_EX_FDRBASE CtiFDRTextFileBase : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

public:
    // constructors and destructors
    CtiFDRTextFileBase(std::string &interfaceType); 

    virtual ~CtiFDRTextFileBase();

    virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
    virtual int processMessageFromForeignSystem (CHAR *data);

    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    std::string & getFileName();
    std::string  getFileName() const;
    CtiFDRTextFileBase &setFileName (std::string aName);

    std::string & getDriveAndPath();
    std::string  getDriveAndPath() const;
    CtiFDRTextFileBase &setDriveAndPath (std::string aDriveAndPath);

    int getInterval() const;
    CtiFDRTextFileBase &setInterval (int aInterval);

    long getLinkStatusID( void ) const;
    CtiFDRTextFileBase &setLinkStatusID(const long aPointID);
    void sendLinkState (int aState);

    virtual bool loadTranslationLists(void)=0;

private:
    CtiFDRTextFileInterfaceParts    _textFileParts;
    long                            _linkStatusID;
};

#endif  //  #ifndef __FDRTEXTFILEBASE_H__



