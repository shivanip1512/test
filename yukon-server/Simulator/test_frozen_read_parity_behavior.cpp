#define BOOST_AUTO_TEST_MAIN "Test FrozenReadParityBehavior"

#include "yukon.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "FrozenReadParityBehavior.h"
#include "types.h"
#include "logger.h"
#include "SimulatorLogger.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_frozen_read_parity_behavior)
{
    SimulatorLogger logger(dout);
    BehaviorCollection<MctBehavior> behaviorCollection;
    std::auto_ptr<MctBehavior> d(new FrozenReadParityBehavior());
    d->setChance(1000);

    behaviorCollection.push_back(d);

    {
        bytes message, reference;
    
        /**
         * Pushing data onto each vector. For messages returning from 
         * function read 0x91, the FrozenReadParityBehavior should 
         * modify the LSB of the third byte of the message.
         */
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);

        reference.push_back(0x04);
        reference.push_back(0x08);
        reference.push_back(0x0E); // This is where the change should occur!
        reference.push_back(0x10);
        reference.push_back(0x17);
        reference.push_back(0x2A);

        MctMessageContext contextBegin = { message, 0x91, true };
        MctMessageContext contextEnd   = { reference, 0x91, true };

        behaviorCollection.processMessage(contextBegin, logger);
    
        // Check to see that the FrozenReadParityBehavior correctly 
        // processed the message and put the information back
        // in the intended order to match contextEnd.
        BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(), 
                                      contextEnd.data.begin(), contextEnd.data.end());
        BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
        BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
    }

    {
        bytes message, reference;
    
        /**
         * Pushing data onto each vector. For messages returning from 
         * function read 0x94, the FrozenReadParityBehavior should 
         * modify the LSB of the ninth byte of the message. 
         */
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);
        message.push_back(0x31);
        message.push_back(0x42);
        message.push_back(0x57);
        message.push_back(0x80);

        reference.push_back(0x04);
        reference.push_back(0x08);
        reference.push_back(0x0F);
        reference.push_back(0x10);
        reference.push_back(0x17);
        reference.push_back(0x2A);
        reference.push_back(0x31);
        reference.push_back(0x42); 
        reference.push_back(0x56); // This is where the change should occur!
        reference.push_back(0x80);

        MctMessageContext contextBegin = { message, 0x94, true };
        MctMessageContext contextEnd   = { reference, 0x94, true };

        behaviorCollection.processMessage(contextBegin, logger);
    
        // Check to see that the FrozenReadParityBehavior correctly 
        // processed the message and put the information back
        // in the intended order to match contextEnd.
        BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(), 
                                      contextEnd.data.begin(), contextEnd.data.end());
        BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
        BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
    }

    {
        bytes message, reference;
    
        /**
         * Pushing data onto each vector. For messages returning from 
         * anything other than function reads 0x91 and 0x94, the 
         * FrozenReadParityBehavior should leave the message untouched. 
         */ 
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);

        reference.push_back(0x04);
        reference.push_back(0x08);
        reference.push_back(0x0F);
        reference.push_back(0x10);
        reference.push_back(0x17);
        reference.push_back(0x2A);

        {
            MctMessageContext contextBegin = { message, 0x91, false };   // Function 0x91, but not a function read!
            MctMessageContext contextEnd   = { reference, 0x91, false };
    
            behaviorCollection.processMessage(contextBegin, logger);
        
            // Check to see that the FrozenReadParityBehavior correctly 
            // processed the message and put the information back
            // in the intended order to match contextEnd.
            BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(), 
                                          contextEnd.data.begin(), contextEnd.data.end());
            BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
            BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
        }

        {
            MctMessageContext contextBegin = { message, 0x94, false };   // Function 0x94, still not a function read!
            MctMessageContext contextEnd   = { reference, 0x94, false };  
            
            behaviorCollection.processMessage(contextBegin, logger);
        
            // Check to see that the FrozenReadParityBehavior correctly 
            // processed the message and put the information back
            // in the intended order to match contextEnd.
            BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(), 
                                          contextEnd.data.begin(), contextEnd.data.end());
            BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
            BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);                      
        }

        {
            MctMessageContext contextBegin = { message, 0x93, true };   // Function read, but not a valid function!
            MctMessageContext contextEnd   = { reference, 0x93, true };
    
            behaviorCollection.processMessage(contextBegin, logger);
        
            // Check to see that the FrozenReadParityBehavior correctly 
            // processed the message and put the information back
            // in the intended order to match contextEnd.
            BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(), 
                                          contextEnd.data.begin(), contextEnd.data.end());
            BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
            BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
        }
    }
}
