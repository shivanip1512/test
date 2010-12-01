#pragma once

#include <string>
#include "dlldefs.h"
#include "fdr.h"
#include "rwutil.h"

class CtiFDRPoint;

class IM_EX_FDRBASE CtiFDRDestination
{
    public:
        CtiFDRDestination () {}; // this is only defined so this class can be used in an std::map
        CtiFDRDestination (CtiFDRPoint* parentPoint, const std::string &translation, const std::string &destination = std::string());
        virtual ~CtiFDRDestination();
        CtiFDRDestination& operator=( const CtiFDRDestination &other );


        std::string & getTranslation(void);
        std::string  getTranslation(void) const;
        CtiFDRDestination& setTranslation (std::string aTranslation);

        std::string getTranslationValue(std::string propertyName) const;

        std::string & getDestination(void);
        std::string  getDestination(void) const;
        CtiFDRDestination& setDestination (std::string aDestination);

        CtiFDRPoint* getParentPoint(void) const;
        CtiFDRDestination& setParentPoint (CtiFDRPoint* parentPoint);

        bool operator<(const CtiFDRDestination& other) const;
        bool operator==(const CtiFDRDestination& other) const;

    private:
        // private data
        std::string      iTranslation;
        std::string      iDestination;
        CtiFDRPoint*     iParentPoint;

};

IM_EX_FDRBASE std::ostream& operator<< (std::ostream& os, const CtiFDRDestination& dest);
