#pragma once

#include "dlldefs.h"
#include "fdr.h"
#include "loggable.h"

#include <string>

class CtiFDRPoint;

class IM_EX_FDRBASE CtiFDRDestination : public Cti::Loggable
{
    public:
        CtiFDRDestination () {}; // this is only defined so this class can be used in an std::map
        CtiFDRDestination( const long parentPoint, const std::string &translation, const std::string &destination = std::string() );
        virtual ~CtiFDRDestination();
        CtiFDRDestination& operator=( const CtiFDRDestination &other );

        std::string & getTranslation(void);
        std::string  getTranslation(void) const;
        CtiFDRDestination& setTranslation (std::string aTranslation);

        std::string getTranslationValue(std::string propertyName) const;

        std::string & getDestination(void);
        std::string  getDestination(void) const;
        CtiFDRDestination& setDestination (std::string aDestination);

        long getParentPointId( void ) const;
        CtiFDRDestination & setParentPointId( const long parentPointId );

        bool operator<(const CtiFDRDestination& other) const;
        bool operator==(const CtiFDRDestination& other) const;

        std::string toString() const;

    private:
        // private data
        std::string      iTranslation;
        std::string      iDestination;
        long             iParentPointId;
};

IM_EX_FDRBASE std::ostream& operator<< (std::ostream& os, const CtiFDRDestination& dest);
