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
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2003/04/22 20:44:47 $
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
      Revision 1.2  2003/04/22 20:44:47  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/
#ifndef __FDRTEXTFILEBASE_H__
#define __FDRTEXTFILEBASE_H__

#include <windows.h>    
#include "dlldefs.h"
#include "fdrinterface.h"
#include "TextFileInterfaceParts.h"

class IM_EX_FDRBASE CtiFDRTextFileBase : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

public:
    // constructors and destructors
    CtiFDRTextFileBase(RWCString &interfaceType); 

    virtual ~CtiFDRTextFileBase();

    virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
    virtual int processMessageFromForeignSystem (CHAR *data);

    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    RWCString & getFileName();
    RWCString  getFileName() const;
    CtiFDRTextFileBase &setFileName (RWCString aName);

    RWCString & getDriveAndPath();
    RWCString  getDriveAndPath() const;
    CtiFDRTextFileBase &setDriveAndPath (RWCString aDriveAndPath);

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



