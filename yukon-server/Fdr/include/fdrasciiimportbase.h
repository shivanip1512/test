#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrasciiimportbase.h
*
*    DATE: 03/06/2002
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic class for any interface importing an ascii file
*
*    DESCRIPTION: 
*
*    Copyright (C) 2002 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRASCIIIMPORTBASE_H__
#define __FDRASCIIIMPORTBASE_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
#include "dlldefs.h"
#include "fdrinterface.h"

class IM_EX_FDRBASE CtiFDRAsciiImportBase : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRAsciiImportBase(std::string &interfaceType); 
    
        virtual ~CtiFDRAsciiImportBase();
    
        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage )=0;
        virtual int processMessageFromForeignSystem (CHAR *data)=0;
    
        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );
    
    protected:
    
        RWThreadFunction    iThreadReadFromFile;
        void threadFunctionReadFromFile( void );
    
        std::string & getFileName();
        std::string  getFileName() const;
        CtiFDRAsciiImportBase &setFileName (std::string aName);
    
        std::string & getDriveAndPath();
        std::string  getDriveAndPath() const;
        CtiFDRAsciiImportBase &setDriveAndPath (std::string aDriveAndPath);

        int getImportInterval() const;
        CtiFDRAsciiImportBase &setImportInterval (int aInterval);

        bool shouldDeleteFileAfterImport() const;
        CtiFDRAsciiImportBase &setDeleteFileAfterImport (bool aFlag);

        long getLinkStatusID( void ) const;
        CtiFDRAsciiImportBase &setLinkStatusID(const long aPointID);
        void sendLinkState (int aState);

        virtual bool loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send=false);

        virtual bool validateAndDecodeLine( std::string &input, CtiMessage **aRetMsg) = 0;
    
    private:
        std::string      iFileName;
        std::string      iDriveAndPath;
        int            iImportInterval;
        long           iLinkStatusID;
        bool           iDeleteFileAfterImportFlag;
};

#endif  //  #ifndef __FDRASCIIIMPORTBASE_H__


