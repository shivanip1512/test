/**
 * File:   livedatatypes
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive: $
 *    REVISION     :  $Revision: 1.1 $
 *    DATE         :  $Date: 2005/04/15 15:34:41 $
 */

#include <vector>
#include "pointdefs.h"
#include "cparms.h"


/**
 * LiveData Types Wrapper.
 * This class' sole purpose is to provide a namespace
 * and access hiding for the classes it contains.
 * There are no methods or variables in this class.
 */
class LiveDataTypes
{
public:
  /**
   * LiveData Types Base Class.
   * This ABC acts as an interface between the code in
   * fdrlivedata.cpp and the actual LiveData types.
   * To add a new type, create a class that extends this one,
   * ensure that the string returned by getName is added to 
   * the approriate row in the FDRInterfaceOption table, and
   * add a line to the Factory class' initialize method.
   */
  class Base
  {
  public:
    virtual std::string getName() = 0;
    virtual unsigned int getSize() = 0;
    virtual double getData(char *buffer) = 0;
    virtual bool hasTimestamp() {return false;};
    virtual time_t getTimestamp(char *buffer) {return 0;};
    virtual bool hasQuality() {return false;};
    virtual PointQuality_t getQuality(char *buffer) {return NormalQuality;};
  
  private:
    // there is no good time to delete these,
    // they will just live as long as the program runs
    ~Base() {};
  
  };

private:
  /**
   * LiveData ICCP Types Base Class.
   * This ABC class is a helper for the other ICCP
   * type classes that return a quality.
   * An extending class will implement getRawQuality
   * and let this class' definition of getQuality determine
   * the actual Yukon quality that is returned.
   */
  class IccpBase : public Base
  {
  public:
    IccpBase();
    virtual PointQuality_t getQuality(char *buffer);
  protected:
    virtual unsigned char getRawQuality(char *buffer) {return 0;};
    static void setupQualityData();
    static CtiConfigParameters  _configParameters;
    static bool _initialized;
    static std::vector<unsigned char> _qualityMasks;
    static std::vector<unsigned char> _qualityPatterns;
    static std::vector<PointQuality_t> _qualityClasses;
  
  };


  /**
   * LiveData Data_RealExtended Data Type.
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_RealExtended : public IccpBase
  {
  public:
    std::string getName() {return "Data_RealExtended";};
    unsigned int getSize() {return 12;};
    bool hasTimestamp() {return true;};
    time_t getTimestamp(char *buffer) {return *((time_t*)(buffer + 4));};
    double getData(char *buffer) {return *((float*)(buffer + 0));};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 8));};
  };

  /**
   * LiveData Data_RealQTimeTag Data Type.
   * For our purposes this class is identical to Data_RealExtended
   * (it omits the COV counter, which we ignore anyways).
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_RealQTimeTag : public Data_RealExtended
  {
  public:
    std::string getName() {return "Data_RealQTimeTag";};
  };

  /**
   * LiveData Data_RealQ Data Type.
   * 
   * Timestamp: no
   * Quality: yes
   */
  class Data_RealQ : public IccpBase
  {
  public:
    std::string getName() {return "Data_RealQ";};
    unsigned int getSize() {return 8;};
    double getData(char *buffer) {return *((float*)(buffer + 0));};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 4));};
  };

  /**
   * LiveData Data_Real Data Type.
   * 
   * Timestamp: no
   * Quality: no
   */
  class Data_Real : public IccpBase
  {
  public:
    std::string getName() {return "Data_Real";};
    unsigned int getSize() {return 4;};
    double getData(char *buffer) {return *((float*)(buffer + 0));};
  };

  /**
   * LiveData Data_StateExtended Data Type.
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_StateExtended : public IccpBase
  {
  public:
    std::string getName() {return "Data_StateExtended";};
    unsigned int getSize() {return 8;};
    bool hasTimestamp() {return true;};
    time_t getTimestamp(char *buffer) {return *((time_t*)(buffer + 0));};
    double getData(char *buffer) {return *((unsigned char*)(buffer + 4)) & 0x3;};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 4));};
  };

  /**
   * LiveData Data_State Data Type.
   * 
   * Timestamp: no
   * Quality: yes
   */
  class Data_State : public IccpBase
  {
  public:
    std::string getName() {return "Data_State";};
    unsigned int getSize() {return 1;};
    double getData(char *buffer) {return *((unsigned char*)(buffer)) & 0x3;};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 4));};
  };

  /**
   * LiveData Data_StateQTimeTag Data Type.
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_StateQTimeTag : public Data_StateExtended
  {
  public:
    std::string getName() {return "Data_StateQTimeTag";};
  };

  /**
   * LiveData Data_DiscreteExtended Data Type.
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_DiscreteExtended : public IccpBase
  {
  public:
    std::string getName() {return "Data_DiscreteExtended";};
    unsigned int getSize() {return 12;};
    bool hasTimestamp() {return true;};
    time_t getTimestamp(char *buffer) {return *((time_t*)(buffer + 4));};
    double getData(char *buffer) {return *((int*)(buffer + 0));};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 8));};
  };

  /**
   * LiveData Data_DiscreteQ Data Type.
   * 
   * Timestamp: no
   * Quality: yes
   */
  class Data_DiscreteQ : public IccpBase
  {
  public:
    std::string getName() {return "Data_DiscreteQ";};
    unsigned int getSize() {return 8;};
    double getData(char *buffer) {return *((int*)(buffer + 0));};
    bool hasQuality() {return true;};
    unsigned char getRawQuality(char *buffer) {return *((unsigned char*)(buffer + 4));};
  };

  /**
   * LiveData Data_DiscreteQTimeTag Data Type.
   * For our purposes this class is identical to Data_DiscreteExtended
   * (it omits the COV counter, which we ignore anyways).
   * 
   * Timestamp: yes
   * Quality: yes
   */
  class Data_DiscreteQTimeTag : public Data_DiscreteExtended
  {
  public:
    std::string getName() {return "Data_DiscreteQTimeTag";};
  };

  /**
   * LiveData Data_Discrete Data Type.
   * 
   * Timestamp: no
   * Quality: no
   */
  class Data_Discrete : public IccpBase
  {
  public:
    std::string getName() {return "Data_Discrete";};
    unsigned int getSize() {return 4;};
    double getData(char *buffer) {return *((int*)(buffer));};
  };

public:
  /**
   * LiveData Data Type Factory.
   * This class is used to reteive instances of the 
   * specific Data Type classes defined above.
   * A instance of each of the Data Type classes is 
   * new'd when the class is initialized, the Factory
   * will return a pointer to one of those instances
   * each time getDataType is called. The instances 
   * and the Factory itself are never deleted.
   */
  class Factory
  {
  public:
    static Factory*  getInstance();
    Base*     getDataType(std::string  typeName);
  
  protected:
    void initialize()
    {
      _lookup.push_back( new Data_RealExtended );
      _lookup.push_back( new Data_RealQ );
      _lookup.push_back( new Data_RealQTimeTag );
      _lookup.push_back( new Data_Real );
      _lookup.push_back( new Data_StateExtended );
      _lookup.push_back( new Data_StateQTimeTag );
      _lookup.push_back( new Data_State );
      _lookup.push_back( new Data_DiscreteExtended );
      _lookup.push_back( new Data_DiscreteQ );
      _lookup.push_back( new Data_DiscreteQTimeTag );
      _lookup.push_back( new Data_Discrete );
    };
    std::vector<Base*> _lookup;
  
    // I'm a singleton
    Factory();
    ~Factory();
    static Factory *myself;
  
  };

};

