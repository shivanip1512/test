yukon.namespace('yukon.tools.map');

/**
 * Singleton that manages the device collection mapping feature.
 * 
 * @module yukon.tools.map
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.tools.map = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** @type {number} - The setTimeout reference for periodic updating of device collection. */
    _updater = -1, 
    
    /** @type {number} - The ms interval to wait before updating the device collection. */
    _updateInterval = 4000,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    /** @type {Object.<number, {ol.Feature}>} - Map of pao id to feature for all device icons. */
    _icons = {}, 
    
    /** @type {Object.<number, boolean>} - Map of pao id to boolean to keep track of device icon visibility. */
    _visibility = {},
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {}, 
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec.png'), anchor: [0.5, 1.0] }) }),
        'METER_WATER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water.png'), anchor: [0.5, 1.0] }) }),
        'METER_GAS': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas.png'), anchor: [0.5, 1.0] }) }),
        'TRANSMITTER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter.png'), anchor: [0.5, 1.0] }) }),
        'GENERIC_GREY': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png'), anchor: [0.5, 1.0] }) }),
        'GENERIC_RED': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker.png'), anchor: [0.5, 1.0] }) })
    },
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
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
     * Creates an {ol.Feature} icon from the geojson feature object.
     * @param {Object} feature - the geojson feature to convert.
     * @param {string} src_projection - The projection system of the tile layer.
     * @returns {ol.Feature} icon - The openlayers feature icon.
     */
    _createFeature = function(feature, src_projection) {
        var pao = feature.properties.paoIdentifier,
            icon = new ol.Feature({pao: pao}),
            style = _styles[feature.properties.icon] || _styles['GENERIC_RED'];
        
        icon.setStyle(style);
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        _icons[pao.paoId] = icon;
        _visibility[pao.paoId] = true;
        
        return icon;
    },
    
    /** 
     * Gets pao locations for the device collection in geojson format and adds
     * an icon feature for each to a new vector layer for the map.
     */
    _loadIcons = function() {
        $('.js-status-loading').show();
        $.getJSON(decodeURI($('#locations').val())).done(function(fc) {
            
            var 
            icon, icons = [],
            source = _getLayer('icons').getSource();
            
            debug.time('Loading Icons');
            
            for (var i in fc.features) {
                icon = _createFeature(fc.features[i], fc.crs.properties.name);
                icons.push(icon);
            }
            source.addFeatures(icons);
            
            if (icons.length > 1) {
                _map.getView().fitExtent(source.getExtent(), _map.getSize());
            } else if (icons.length === 1) {
                _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
                _map.getView().setZoom(9);
            }
            $('.js-status-loading').hide();
            debug.timeEnd('Loading Icons');
            
            if ($('#map').is('[data-dynamic]')) {
                _update();
            }
        });
    },
    
    /** Method to update device list with recursive setTimeout. */
    _update = function(once) {
        $.getJSON(decodeURI($('#locations').val())).done(function(fc) {
            
            var toAdd = [], toRemove = [], icons = {},
                i, pao, feature, diff,
                source = _getLayer('icons').getSource();
            
            // add any features we don't have
            for (i = 0; i < fc.features.length; i++) {
                feature = fc.features[i],
                pao = feature.properties.paoIdentifier;
                
                icons[pao.paoId] = feature;
                
                if (typeof _icons[pao.paoId] === 'undefined') {
                    var
                    icon = _createFeature(feature, fc.crs.properties.name);
                    toAdd.push(icon);
                }
            }
            source.addFeatures(toAdd);
            debug.log('added ' + toAdd.length + ' features');
            
            // remove any features we don't want
            for (i in _icons) {
                feature = _icons[i];
                if (typeof icons[feature.get('pao').paoId] === 'undefined') {
                    toRemove.push(feature);
                }
            }
            for (i = 0; i < toRemove.length; i++) {
                feature = toRemove[i];
                source.removeFeature(feature);
                delete _icons[feature.get('pao').paoId];
                delete _visibility[feature.get('pao').paoId];
            }
            debug.log('removed ' + toRemove.length + ' features');
            
            diff = toAdd.length - toRemove.length;
            if (diff !== 0) {
                var count = parseInt($('#device-collection .js-count').text(), 10);
                $('#device-collection .js-count').text(count + diff);
                $('#device-collection .js-count').addClass('animated flash');
            }
            
        }).fail(function(xhr, status, error) {
            debug.log('update failed:' + status + ': ' + error);
        }).always(function() {
            if (!once) {
                _updater = setTimeout(_update, _updateInterval);
            }
        });
    },
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen(), 
                    new ol.control.ScaleLine({units: 'us', target: 'scale-line'}), 
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
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
            });
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
            _loadIcons();
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { return feature; });
                if (feature) {
                    var 
                    geometry = feature.getGeometry(),
                    coord = geometry.getCoordinates(),
                    url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info');
                    $('#marker-info').load(url, function() {
                        $('#marker-info').show();
                        _overlay.setPosition(coord);
                    });
                    //close any lingering delete dialogs to simplify handling
                    $('#confirm-delete').dialog('destroy');
                } else {
                    $('#marker-info').hide();
                }
            });
            
            /** Initilize the attribute select and handle change events. */
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
            
            /** Submit the filtering form when 'yukon.map.filter' event is fired, then process results. */
            $(document).on('yukon.map.filter', function(ev) {
                
                $('#map-popup').dialog('close');
                $('#no-filter-btn').show();
                $('.js-status-retrieving').show();
                $('#filter-btn').addClass('left');
                $('#filter-btn .b-label').text($('#filtered-msg').val() 
                        + ' ' + $('#attribute-select option:selected').text());
                
                var start = new Date().getTime();
                
                $('#filter-form').ajaxSubmit({
                    dataType: 'json',
                    success: function(results) {
                        
                        $('.js-status-retrieving').hide();
                        $('.js-status-filtering').show();
                        
                        debug.log('point data request: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        var 
                        source = _getLayer('icons').getSource(),
                        toAdd = [], toRemove = [],
                        visible, show, paoId, icon;
                        
                        for (paoId in results) {
                            icon = _icons[paoId];
                            if (icon) { // Ignore any paos we aren't tracking. i.e. They don't have a location.
                                show = results[paoId];
                                visible = _visibility[paoId];
                                if (show && !visible) {
                                    toAdd.push(icon);
                                    _visibility[paoId] = true;
                                } else if (!show && visible) {
                                    toRemove.push(icon);
                                    _visibility[paoId] = false;
                                }
                            }
                        }
                        
                        debug.log('building add/remove arrays: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        source.addFeatures(toAdd);
                        
                        debug.log('adding icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        start = new Date().getTime();
                        
                        var toRemoveLength = toRemove.length;
                        for (var i = 0; i < toRemoveLength; i++) {
                            source.removeFeature(toRemove[i]);
                        }
                        
                        debug.log('removing icons: '+ ((new Date().getTime() - start) * .001) + ' seconds');
                        $('.js-status-filtering').hide();
                    }, 
                    error: function(xhr, status, error, $form) {
                        debug.log('error with ajax filter form submission: ' + error);
                        $('.js-status-retrieving').hide();
                    }
                });
                
            });
            
            /** Change map tiles layer on tile button group clicks. */
            $('#map-tiles button').click(function(ev) {
                $(this).siblings().removeClass('on');
                $(this).addClass('on');
                for (var i in _tiles) {
                    var layer = $(this).data('layer');
                    _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                }
                
            });
            
            /** Remove filtering when no filter button clicked. */
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
            
            /** Change mouse cursor when over marker.  There HAS to be a css way to do this! */
            $(_map.getViewport()).on('mousemove', function(e) {
                var pixel = _map.getEventPixel(e.originalEvent),
                    hit = _map.forEachFeatureAtPixel(pixel, function(feature, layer) { return true; });
                $('#' + _map.getTarget()).css('cursor', hit ? 'pointer' : 'default');
            });
            
            /** Remove the coordinates for the device when the user clicks OK on the confirmation popup. **/
            $(document).on('yukon:tools:map:delete-coordinates', function(event) {
                var paoId = $('#remove-pin').data("pao"),
                    removeUrl = yukon.url('/tools/map/device/' + paoId);
                
                $.ajax({
                    url: removeUrl,
                    type: 'POST',
                    data: {_method: 'DELETE'}, //Spring request type hackery
                    success: function(results) {
                        $('#confirm-delete').dialog('destroy');
                        yukon.ui.removeAlerts();
                        _update(true); //refresh map
                    },
                    error: function(xhr, status, error) {
                        var errorMsg = xhr.responseJSON.message;
                        yukon.ui.alertError(errorMsg);
                    }
                });
                
            });
            
            /** Destroy the coordinate deletion confirmation popup when cancelled **/
            $(document).on('click', '.cancel-delete', function(event) {
                $('#confirm-delete').dialog('destroy');
            });
            
            /** Remove animation classes when animation finishes. */
            $('#device-collection .js-count')
                .on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(ev) {
                $('#device-collection .js-count').removeClass('animated flash'); 
            });
            
            /** Pause/Resume updating on updater button clicks. */
            $('#map-updater .button').on('click', function(ev) {
                var pause = $('#map-updater .yes').is('.on');
                if (pause) {
                    clearTimeout(_updater);
                } else {
                    _updater = setTimeout(_update, _updateInterval);
                }
                $('#map-updater .button').toggleClass('on');
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