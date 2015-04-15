yukon.namespace('yukon.hub');

/**
 * A simple pub/sub module. See http://davidwalsh.name/pubsub-javascript
 * 
 * @requires yukon
 */
yukon.hub = (function () {
    
    var topics = {};
    var hop = topics.hasOwnProperty;
    
    return {
        
        /** 
         * Subcribe a listener function to be notified of an event on a topic.
         * Returns an object with a 'remove' property as a function to remove the registered listener.
         * 
         * @param {string} topic - The topic to subscribe to.
         * @param {Function} listener - The listener function fired for each event on the topic.
         * @return {Object} o - Listener removal handler.
         */
        sub: function (topic, listener) {
            // Create the topic's object if not yet created
            if (!hop.call(topics, topic)) topics[topic] = [];
            
            // Add the listener to topic's listner queue
            var index = topics[topic].push(listener) -1;
            
            // Provide handle back for removal of a topic listener
            return {
                remove: function () {
                    delete topics[topic][index];
                }
            };
        },
        
        /**
         * Publish an event on the topic with optional data.
         * 
         * @param {string} topic - The topic to publish the event on.
         * @param {Object} [data] - The optional data to pass the listeners.
         */
        pub: function (topic, data) {
            // If the topic doesn't exist, or there's no listeners in queue, just leave
            if (!hop.call(topics, topic)) return;
            
            // Cycle through topics queue, fire!
            topics[topic].forEach(function (listener) {
                listener(data || {});
            });
        }
    };
})();