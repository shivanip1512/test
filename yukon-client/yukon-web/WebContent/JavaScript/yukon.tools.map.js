yukon.namespace('yukon.tools.map');

/**
 * Singleton that manages the Device Mapping feature
 * 
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.tools.map = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    _destProjection = 'EPSG:3857', /** The projection code of our map tiles. */
    _icons = {}, /** Map {pao id, {ol.Feature}} of all device icons. */
    _visibility = {}, /** Map {pao id, boolean} to keep track of device icon visibility. */
    _map = {}, /** The openlayers map object. */
    _styles = { /** A cache of styles to avoid creating lots of objects using lots of memory. */
        'electric': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-electric.png'), anchor: [0.5, 1.0] }) }),
        'water': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-water.png'), anchor: [0.5, 1.0] }) }),
        'gas': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-gas.png'), anchor: [0.5, 1.0] }) }),
        'generic': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png'), anchor: [0.5, 1.0] }) })
    },
    _tiles = [
        new ol.layer.Tile({ name: 'mqosm', source: new ol.source.MapQuest({ layer: 'osm' }) }),
        new ol.layer.Tile({ name: 'mqsat', source: new ol.source.MapQuest({ layer: 'sat' }), visible: false }),
        new ol.layer.Group({
            name: 'hybrid',
            layers: [
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'sat'}) }),
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'hyb'}) })
            ],
            visible: false
        })
    ],
    
    /** 
     * Returns the first layer with name provided.
     * @param {string} name - Name of layer.
     */
    _getLayer = function(name) {
        return _map.getLayers().getArray().filter(function(layer) {
            return layer.get('name') === name;
        })[0];
    },
    
    /** 
     * Gets pao locations for the device collection in geojson format and adds
     * an icon feature for each to a new vector layer for the map.
     * @param {string} dest_projection - The project that the icon vector layer will be in. 
     */
    _loadIcons = function() {
        $.getJSON(decodeURI($('#locations').val())).done(function(fc) {
            
            var 
            icons = [],
            src_projection = fc.crs.properties.name, // Yukon currently storing coords as ESPG:4326 (WGS84)
            start = new Date().getTime();
            
            for (var i in fc.features) {
                var
                feature = fc.features[i],
                pao = feature.properties.paoIdentifier,
                icon = new ol.Feature({pao: pao});
                
                icon.setStyle(_styles['generic']);
                if (src_projection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    icon.setGeometry(new ol.geom.Point(ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection)));
                }
                
                _icons[pao.paoId] = icon;
                _visibility[pao.paoId] = true;
                icons.push(icon);
            }
            _getLayer('icons').getSource().addFeatures(icons);
            
            _map.getView().fitExtent(_getLayer('icons').getSource().getExtent(), _map.getSize());
            
            debug.log('loading icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
        });
    },
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            /* Setup map */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen(), 
                    new ol.control.ScaleLine({units: ol.control.ScaleLineUnits.IMPERIAL, target: 'scale-line'}), 
                    new ol.control.Zoom(), 
                    new ol.control.MousePosition({
                        coordinateFormat: ol.coordinate.createStringXY(6),
                        projection: 'EPSG:4326',
                        target: 'mouse-position',
                        undefinedHTML: '&nbsp;'
                    })
                ],
                layers: _tiles,
                target: 'map',
                view: new ol.View2D({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
            });
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
            _loadIcons();
            
            /* Display popup on click */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { return feature; });
                if (feature) {
                    var 
                    geometry = feature.getGeometry(),
                    coord = geometry.getCoordinates(),
                    url = 'map/device/' + feature.get('pao').paoId + '/info';
                    $('#marker-info').load(url, function() {
                        $('#marker-info').show();
                        _overlay.setPosition(coord);
                    });
                } else {
                    $('#marker-info').hide();
                }
            });
            
            /* Init attribute select and handle change events. */
            $('#attribute-select').chosen({width: '100%'}).on('change', function(ev) {
                $('#filter-states').empty();
                $('#waiting-for-states').show();
                $('#no-states-for-attribute').hide();
                if ($('#attribute-select').val() !== '-1') {
                    $.getJSON(decodeURI($('#state-group-base-url').val()) + '&attribute=' + $('#attribute-select').val())
                    .done(function(groups) {
                        var group, state, row, select;
                        
                        $('#waiting-for-states').hide();
                        $('#no-states-for-attribute').toggle(groups.length === 0);
                        
                        for (var i in groups) {
                            group = groups[i];
                            row = $('#state-group-template').clone().removeAttr('id');
                            row.find('input:hidden').val(group.stateGroupID).attr('name', 'groups[' + i + '].id');
                            select = row.find('select').attr('name', 'groups[' + i + '].state');
                            for (var ii in group.statesList) {
                                state = group.statesList[ii];
                                select.append('<option value="' + state.liteID + '">' + state.stateText + '</option>');
                            }
                            $('#filter-states').append(row);
                            row.removeClass('dn');
                        }
                    });
                }
            });
            
            /* Handle submitting of filtering form. */
            $(document).on('yukon.map.filter', function(ev) {
                
                $('#map-popup').dialog('close');
                $('#no-filter-btn').show();
                $('#filter-btn').addClass('left');
                $('#filter-btn .b-label').text($('#filtered-msg').val() 
                        + ' ' + $('#attribute-select option:selected').text());
                
                var start = new Date().getTime();
                
                $('#filter-form').ajaxSubmit({
                    dataType: 'json',
                    success: function(results) {
                        
                        debug.log('point data request: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        var 
                        source = _getLayer('icons').getSource(),
                        toAdd = [], toRemove = [];
                        
                        for (var paoId in results) {
                            var show = results[paoId];
                            var visible = _visibility[paoId];
                            if (show && !visible) {
                                toAdd.push(_icons[paoId]);
                                _visibility[paoId] = true;
                            } else if (!show && visible) {
                                toRemove.push(_icons[paoId]);
                                _visibility[paoId] = false;
                            }
                        }
                        
                        debug.log('building add/remove arrays: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        source.addFeatures(toAdd);
                        
                        debug.log('adding icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        for (var i = 0; i < toRemove.length; i++) {
                            source.removeFeature(toRemove[i]);
                        }
                        
                        debug.log('removing icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                    }, 
                    error: function(xhr, status, error, $form) {
                        debug.log('error with ajax filter form submission: ' + error);
                    }
                });
                
            });
            
            /* Handle map tile buttons */
            $('#map-tiles button').click(function(ev) {
                $(this).siblings().removeClass('on');
                $(this).addClass('on');
                for (var i in _tiles) {
                    var layer = $(this).data('layer');
                    _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                }
            });
            
            /* Handle submitting of filtering form. */
            $('#no-filter-btn').click(function(ev) {
                $('#no-filter-btn').hide();
                $('#filter-btn').removeClass('left');
                $('#filter-btn .b-label').text($('#unfiltered-msg').val());
                
                var toAdd = [], start = new Date().getTime();
                for (var paoId in _visibility) {
                    if (!_visibility[paoId]) {
                        toAdd.push(_icons[paoId]);
                        _visibility[paoId] = true;
                    }
                }
                _getLayer('icons').getSource().addFeatures(toAdd);
                
                debug.log('removing icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                start = new Date().getTime();
            });
            
            /* Change mouse cursor when over marker.  There HAS to be a css way to do this! */
            $(_map.getViewport()).on('mousemove', function(e) {
                var pixel = _map.getEventPixel(e.originalEvent),
                    hit = _map.forEachFeatureAtPixel(pixel, function(feature, layer) { return true; });
                $('#' + _map.getTarget()).css('cursor', hit ? 'pointer' : 'default');
            });
            
            _initialized = true;
        },

        getMap: function() { return _map; },
        getFeatures: function() { return _icons; },
        
        getFeatureByPaoId: function(paoId) {
            return _getLayer('icons').getSource().getFeatures().filter(function(feature) {
                return feature.get('pao').paoId === paoId;
            })[0];
        },
        
        /** Test method to change an icon style */
        changeIconTest: function() {
            _getLayer('icons').getSource().getFeatures()[0].setStyle(_styles['gas']);
        },
        
        /** Test method to hide icons */
        hideIconsTest: function() {
            
            var 
            featureArray = _getLayer('icons').getSource().getFeatures().slice(0),
            hide = [],
            start = new Date();
            for (var i = 0; i < featureArray.length; i++) {
                hide.push(featureArray[i]);
                _getLayer('icons').getSource().removeFeature(featureArray[i]);
            }
            debug.log('removing ' + featureArray.length + ' features from "icons" layer took '+ ((new Date().getTime() - start.getTime()) * .001) + ' seconds');
            start = new Date();
            _getLayer('filter').getSource().addFeatures(hide);
            debug.log('adding ' + featureArray.length + ' features to "filter" layer took '+ ((new Date().getTime() - start.getTime()) * .001) + ' seconds');
        },
        
        /** Test method to show icons */
        showIconsTest: function() {
            
            var 
            featureArray = _getLayer('filter').getSource().getFeatures().slice(0),
            add = [],
            start = new Date();
            for (var i = 0; i < featureArray.length; i++) {
                add.push(featureArray[i]);
                _getLayer('filter').getSource().removeFeature(featureArray[i]);
            }
            debug.log('removing ' + featureArray.length + ' features from "filter" layer took '+ ((new Date().getTime() - start.getTime()) * .001) + ' seconds');
            start = new Date();
            _getLayer('icons').getSource().addFeatures(add);
            debug.log('adding ' + featureArray.length + ' features to "icons" layer took '+ ((new Date().getTime() - start.getTime()) * .001) + ' seconds');
        }
        
    };
    
    return _mod;
})();

$(function() { yukon.tools.map.init(); });