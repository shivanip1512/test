#include "yukon.h"
#include "mc_script.h"

/*-----------------------------------------------------------------------------*
*
* File:   mc_script
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_script.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


RWDEFINE_COLLECTABLE( CtiMCScript, MSG_MC_SCRIPT )

string CtiMCScript::_path("..\\scripts");
const unsigned CtiMCScript::_io_buf_size = 8192;

CtiMCScript::CtiMCScript()
{
}

CtiMCScript::~CtiMCScript()
{
}

const string& CtiMCScript::getScriptName() const
{
    return _name;
}

CtiMCScript& CtiMCScript::setScriptName(const string& name)
{
    _name = name;
    return *this;
}

const string& CtiMCScript::getContents() const
{
    return _contents;
}

CtiMCScript& CtiMCScript::setContents(const string& contents)
{
    _contents = contents;
    return *this;
}

/*----------------------------------------------------------------------------
  readContents

  Reads the contents of the script file from disk.
  Will read file:
  getScriptPath()\getScriptName() to locate the file.
----------------------------------------------------------------------------*/
bool CtiMCScript::readContents()
{
    bool result = false;
    char buf[_io_buf_size];
    FILE* fi;
    int num;
    string filename;

    filename.append(getScriptPath())
            .append("\\")
            .append(getScriptName());

    // Clear out old script contents
    _contents = "";
    
    // Append each block read into _contents string
    if( (fi = fopen( (const char*) filename.data(), "rb")) != NULL )
    {
        do
        {
            num = fread( buf, sizeof(char), _io_buf_size, fi );
            _contents.append( buf, num );

        } while( result && num == _io_buf_size );

        result = (_contents.length() > 0);
    }

    if( fi != NULL )
        fclose(fi);

    return result;
}

/*----------------------------------------------------------------------------
  writeContents

  Writes out the contents of the script file to disk.
  Uses getScriptPath()\getScriptName()
----------------------------------------------------------------------------*/
bool CtiMCScript::writeContents()
{
    bool result = false;
    FILE* fo;
    unsigned num;
    string filename;

    filename.append(getScriptPath())
            .append("\\")
            .append(getScriptName());

    if( (fo = fopen( (const char*) filename.data(), "wb")) != NULL )
    {
        num = fwrite(_contents.data(), sizeof(char), _contents.length(), fo );
        result = ( num == _contents.length() );
    }

    if( fo != NULL )
        fclose(fo);

    return result;
}

CtiMessage* CtiMCScript::replicateMessage() const
{
    CtiMCScript* copy = new CtiMCScript();
    *copy = *this;
    return copy;
}

void CtiMCScript::restoreGuts(RWvistream &strm)
{
    RWCString temp_str;

    CtiMessage::restoreGuts(strm);
    strm    >> temp_str;
    _name = (const char*) temp_str.data();

    strm    >> temp_str;
    _contents = (const char*) temp_str.data();
}

void CtiMCScript::saveGuts(RWvostream &strm) const
{
    CtiMessage::saveGuts(strm);
    strm    <<  RWCString( (const char*) _name.data() )
            <<  RWCString( (const char*) _contents.data() );
}

const string& CtiMCScript::getScriptPath()
{
    return _path;
}

void CtiMCScript::setScriptPath(const string& path)
{
    _path = path;
}

