#pragma once

#include "dlldefs.h"
#include "fdrinterface.h"
#include "TextFileInterfaceParts.h"

class IM_EX_FDRBASE CtiFDRTextFileBase : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

public:
    DEBUG_INSTRUMENTATION;

    // constructors and destructors
    CtiFDRTextFileBase(std::string &interfaceType); 

    virtual ~CtiFDRTextFileBase();

    void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
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
