#include "precompiled.h"


/** include files **/

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "TextFileInterfaceParts.h"

using std::string;

// Constructors, Destructor, and Operators
CtiFDRTextFileInterfaceParts::CtiFDRTextFileInterfaceParts(string &aFileName, string &aPath, int aInterval)
:    _fileName(aFileName),
    _driveAndPath (aPath),
    _interval(aInterval)
{

}


CtiFDRTextFileInterfaceParts::~CtiFDRTextFileInterfaceParts()
{
}

int CtiFDRTextFileInterfaceParts::getInterval() const
{
    return _interval;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setInterval (int aInterval)
{
    _interval = aInterval;
    return *this;
}

string & CtiFDRTextFileInterfaceParts::getFileName()
{
    return _fileName;
}

string  CtiFDRTextFileInterfaceParts::getFileName() const
{
    return _fileName;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setFileName (string aFile)
{
    _fileName = aFile;
    return *this;
}

string & CtiFDRTextFileInterfaceParts::getDriveAndPath()
{
    return _driveAndPath;
}

string  CtiFDRTextFileInterfaceParts::getDriveAndPath() const
{
    return _driveAndPath;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setDriveAndPath (string aPath)
{
    _driveAndPath = aPath;
    return *this;
}



