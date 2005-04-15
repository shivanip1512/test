#include "yukon.h"

/**
 * File:   livedatatypes
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.1 $
 * DATE         :  $Date: 2005/04/15 15:34:41 $
 */

#include "livedatatypes.h"
#include <map>
#include "logger.h"
#include "guard.h"



CtiConfigParameters  LiveDataTypes::IccpBase::_configParameters;
bool LiveDataTypes::IccpBase::_initialized;
std::vector<unsigned char> LiveDataTypes::IccpBase::_qualityMasks;
std::vector<unsigned char> LiveDataTypes::IccpBase::_qualityPatterns;
std::vector<PointQuality_t> LiveDataTypes::IccpBase::_qualityClasses;


LiveDataTypes::Factory* LiveDataTypes::Factory::myself = NULL;

/**
 * Default Constructor.
 */
LiveDataTypes::Factory::Factory()
{
  initialize();
}

/**
 * Static accessor method for Factory class.
 * This is used to enforce the singleton pattern.
 */
LiveDataTypes::Factory*  LiveDataTypes::Factory::getInstance()
{
  if (myself == NULL)
  {
    myself = new Factory();
  }
  return myself;
}

/**
 * The "Factory" method of the Factory class.
 */
LiveDataTypes::Base* LiveDataTypes::Factory::getDataType(std::string  typeName)
{
  std::vector<LiveDataTypes::Base*>::iterator myIter;
  for (myIter = _lookup.begin(); myIter != _lookup.end(); ++myIter)
  {
    if ((*myIter)->getName() == typeName)
    {
      return (*myIter);
    }
  }
  throw std::exception();
}

/**
 * Default Constructor.
 */
LiveDataTypes::IccpBase::IccpBase()
{
  if (!_initialized)
  {
    setupQualityData();
    _initialized = true;
  }
}

/**
 * Returns a Yukon quality given a pointer to a LiveData data buffer.
 * How the ICCP quality is translated to a Yukon quality is determined
 * by three CPARMS and is described in the "FDR LiveData.doc" document.
 */
PointQuality_t LiveDataTypes::IccpBase::getQuality(char *buffer)
{
  unsigned char rawQuality = getRawQuality(buffer);

  PointQuality_t result = UnknownQuality;

  int count = _qualityMasks.size();
  for (int i = 0; i < count; ++i)
  {
    if ((rawQuality & _qualityMasks[i]) == _qualityPatterns[i])
    {
      result = _qualityClasses[i];
      break;
    }
  }

  return result;

}


/**
 * Sets up the member variables that are used by IccpBase::getQuality to
 * translate the quality values.
 */
void LiveDataTypes::IccpBase::setupQualityData()
{

  _qualityMasks.clear();
  _qualityPatterns.clear();
  _qualityClasses.clear();

  // read in the strings from CPARMS
  std::string strMasks = _configParameters.getValueAsString("FDR_LIVEDATA_ICCP_QUALITY_MASKS","0x00").data();
  std::string strPatterns = _configParameters.getValueAsString("FDR_LIVEDATA_ICCP_QUALITY_PATTERNS","0x00").data();
  std::string strClasses = _configParameters.getValueAsString("FDR_LIVEDATA_ICCP_QUALITY_CLASSES","NormalQuality").data();

  // strings are delimited by a comma (and possibly some space)
  const std::string delimiters = ", ";
  char  *dummyPointer;

  std::string::size_type lastPos = 0;
  std::string::size_type pos = 0;

  // tokenize the masks, converting each to an unsigned char and storing in _qualityMasks
  pos = strMasks.find_first_of(delimiters, lastPos);
  while (std::string::npos != pos || std::string::npos != lastPos)
  {
    unsigned char temp = strtoul(strMasks.substr(lastPos, pos - lastPos).c_str(), &dummyPointer, 0);
    _qualityMasks.push_back(temp);
    lastPos = strMasks.find_first_not_of(delimiters, pos);
    pos = strMasks.find_first_of(delimiters, lastPos);
  }

  // tokenize the patterns, converting each to an unsigned char and storing in _qualityPatterns
  lastPos = 0;
  pos = strPatterns.find_first_of(delimiters, lastPos);
  while (std::string::npos != pos || std::string::npos != lastPos)
  {
    unsigned char temp = strtoul(strPatterns.substr(lastPos, pos - lastPos).c_str(), &dummyPointer, 0);
    _qualityPatterns.push_back(temp);
    lastPos = strPatterns.find_first_not_of(delimiters, pos);
    pos = strPatterns.find_first_of(delimiters, lastPos);
  }

  // setup a map with all the known quality values
  std::map<std::string,PointQuality_t> qualityLookup;
  qualityLookup["Unintialized"] = UnintializedQuality;
  qualityLookup["InitDefault"] = InitDefaultQuality;
  qualityLookup["InitLastKnown"] = InitLastKnownQuality;
  qualityLookup["NonUpdated"] = NonUpdatedQuality;
  qualityLookup["Manual"] = ManualQuality;
  qualityLookup["Normal"] = NormalQuality;
  qualityLookup["ExceedsLow"] = ExceedsLowQuality;
  qualityLookup["ExceedsHigh"] = ExceedsHighQuality;
  qualityLookup["Abnormal"] = AbnormalQuality;
  qualityLookup["Unknown"] = UnknownQuality;
  qualityLookup["Invalid"] = InvalidQuality;
  qualityLookup["PartialInterval"] = PartialIntervalQuality;
  qualityLookup["DeviceFiller"] = DeviceFillerQuality;
  qualityLookup["Questionable"] = QuestionableQuality;
  qualityLookup["Overflow"] = OverflowQuality;
  qualityLookup["Powerfail"] = PowerfailQuality;
  qualityLookup["Unreasonable"] = UnreasonableQuality;

  // tokenize the classes, converting each to an PointQuality_t enum and storing in _qualityClasses
  lastPos = 0;
  pos = strClasses.find_first_of(delimiters, lastPos);
  while (std::string::npos != pos || std::string::npos != lastPos)
  {
    std::string temp_str = strClasses.substr(lastPos, pos - lastPos).c_str();
    std::map<std::string,PointQuality_t>::iterator qualIter = qualityLookup.find(temp_str);
    PointQuality_t temp;

    if (qualIter == qualityLookup.end())
    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " FDR_LIVEDATA_ICCP_QUALITY_CLASSES has unknown quality: " << temp_str << endl;

      temp = UnknownQuality;
    }
    else
    {
      temp = (*qualIter).second;
    }
    _qualityClasses.push_back(temp);
    lastPos = strClasses.find_first_not_of(delimiters, pos);
    pos = strClasses.find_first_of(delimiters, lastPos);
  }

  // assert (_qualityMasks.size() == _qualityPatterns.size() && _qualityPatterns.size() == _qualityClasses.size())

}


