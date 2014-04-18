yukon.namespace('yukon.collection.map');

/**
 * Singleton that manages the Device Mapping feature
 * 
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.collection.map = (function() {
    
    var
    _initialized = false,
    _map = {}, /** The openlayers map object. */
    _featureLookup = {}, /** A map of pao id to feature colleciton array index. */
    _featureCollection = {}, /** The collection of devices as geojson objects (FeatureCollection). */
    _styles = { /** A cache of styles to avoid creating lots of objects using lots of memory. */
        'electric': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-electric.png') }) }),
        'water': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-water.png') }) }),
        'gas': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-gas.png') }) }),
        'generic': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png') }) })
    },
    
    /** 
     * Gets pao locations for the device collection in geojson format and adds
     * an icon feature for each to a new vector layer for the map.
     * @param {string} dest_projection - The project that the icon vector layer will be in. 
     */
    _loadIconLayer = function(dest_projection) {
        $.getJSON(decodeURI($('#locations').val())).done(function(fc) {
            _featureCollection = fc;
            var 
            icons = [],
            src_projection = fc.crs.properties.name,  // Yukon currently storing coords as ESPG:4326 (WGS84)
            start = new Date().getTime();
            
            for (var i in fc.features) {
                var
                feature = fc.features[i],
                icon = new ol.Feature({paoIdentifier: feature.properties.paoIdentifier});
                
                icon.setStyle(_styles['electric']);
                if (src_projection === dest_projection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates)); 
                } else {
                    icon.setGeometry(new ol.geom.Point(ol.proj.transform(feature.geometry.coordinates, src_projection, dest_projection)));
                }
                
                icons.push(icon);
            }
            
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ features: icons, projection: dest_projection }) }));
            _map.getView().fitExtent(_getLayer('icons').getSource().getExtent(), _map.getSize());
            
            _buildFeatureLookup();
            debug.log('loading icons took '+ ((new Date().getTime() - start) * .001) + ' seconds');
        });
    },
    
    /** 
     * Add feature index to paoid lookup to avoid looping over entire list to find a feature.
     * Have to do this in a second loop after the fact since the order a
     * feature was added is different than it's index in the array that 
     * .getFeatures() returns.
     */
    _buildFeatureLookup = function() {
        _featureLookup = {}; // Reset the map.
        var featureArray = _getLayer('icons').getSource().getFeatures();
        for (var i in featureArray) {
            feature = featureArray[i]; 
            _featureLookup[feature.get('paoIdentifier').paoId] = i;
        }
    },
    
    /** 
     * Returns the first layer with name provided.
     * @param {string} name - Name of layer.
     */
    _getLayer = function(name) {
        return _map.getLayers().getArray().filter(function(layer) {
            return layer.get('name') === name;
        })[0];
    },
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            var osmLayer = new ol.layer.Tile({ name: 'osm', source: new ol.source.MapQuest({layer: 'osm'}) });
            
            _map = new ol.Map({
                controls: [
                    new ol.control.FullScreen(), 
                    new ol.control.ScaleLine({units: ol.control.ScaleLineUnits.IMPERIAL}), 
                    new ol.control.Zoom(), 
                    new ol.control.MousePosition({
                        coordinateFormat: ol.coordinate.createStringXY(6),
                        projection: 'EPSG:4326',
                        className:  "custom-mouse-position",
                        target: document.getElementById('mouse-position'),
                        undefinedHTML: '&nbsp;'
                    })
                ],
                layers: [osmLayer],
                target: 'map',
                view: new ol.View2D({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
            });
            
            var dest_projection = _map.getView().getProjection().getCode();
            _loadIconLayer(dest_projection);
            
            _initialized = true;
        },

        getMap: function() { return _map; },
        
        getFeatureLookup: function() { return _featureLookup; },
        
        getFeatureByPaoId: function(paoId) {
            return _getLayer('icons').getSource().getFeatures()[_featureLookup[paoId]];
        },
        
        /** Test method to change an icon style */
        changeIconTest: function() {
            _getLayer('icons').getSource().getFeatures()[0].setStyle(_styles['gas']);
        },
        
        /** Test method to remove icons */
        hideIconsTest: function() {
            
            var source = _getLayer('icons').getSource();
            for (var i = 0; i < 6; i++) {
                
                var featureArray = source.getFeatures(),
                    start = new Date(),
                    half = featureArray.length / 2;
                
                for (var ii = 0; ii < half; ii++) {
                    source.removeFeature(featureArray[ii]);
                    // try skipped features when infinate loop is fixed https://groups.google.com/forum/#!topic/ol3-dev/LjHVWy3AHO0
                    // _map.getSkippedFeatures().push(featureArray[ii]);
                }
                debug.log('removing ' + half + ' features took '+ ((new Date().getTime() - start.getTime()) * .001) + ' seconds');
            }
            _buildFeatureLookup();
        }
        
    };
    
    return _mod;
})();

$(function() { yukon.collection.map.init(); });