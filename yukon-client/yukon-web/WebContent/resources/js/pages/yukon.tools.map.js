yukon.namespace('yukon.tools.map');

/**
 * Singleton that manages the device collection mapping feature.
 * 
 * @module yukon.tools.map
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.tools.map = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _primaryRoutePreviousPoints,
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _deviceFocusIconLayer,
    _largerScale = 1.3,
    _violationColor = '#ec971f',
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _iconLayerIndex = 3,
    
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
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    /** Hashmap of colors and paoIds for tinting the icons */
    _mappingColors,
        
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
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
        
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
    
    _getLocationUrl = function() {
        var monitorId = $('#monitorId').val(),
            violationsOnly = $('#violationsSelect').val(),
            locationsUrl = $('#locations').val();
        if (monitorId) {
            locationsUrl = $('#monitorLocations').val() + "?violationsOnly=" + violationsOnly;
        }
        return locationsUrl;
    },
    
    _updateStyleIfInViolation = function(data, pao, icon) {
        //check if it's in violation
        var monitorId = $('#monitorId').val();
        if (monitorId) {
            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc();   
            var inViolation = data.violationDevices.filter(function (device) {
                return device.deviceId === pao.paoId;
            });
            if (inViolation.length > 0) {
                currentStyle.setImage(new ol.style.Icon({ src: src, color: _violationColor, anchor:  [0.5, 1.0] }));
                icon.setStyle(currentStyle);
            } else {
                //return back to original color
                currentStyle.setImage(new ol.style.Icon({ src: src, anchor:  [0.5, 1.0] }));
                icon.setStyle(currentStyle);
            }
        }
    },
    
    _updateStyleIfFoundInMappingColors = function(paoId, icon) {
        if (_mappingColors) {
            var color;
            Object.keys(_mappingColors).map(function (key) {        
                var ids = _mappingColors[key];
                if (ids.includes(parseInt(paoId, 10))) {
                    color = key;
                }
            });
            if (color) {
                var currentStyle = icon.getStyle().clone(),
                    image = currentStyle.getImage(),
                    src = image.getSrc();
                currentStyle.setImage(new ol.style.Icon({ src: src, color: color, anchor:  [0.5, 1.0] }));
                icon.setStyle(currentStyle);
            }
        }
    },
    
    /** 
     * Gets pao locations for the device collection in geojson format and adds
     * an icon feature for each to a new vector layer for the map.
     */
    _loadIcons = function() {
        $('.js-status-loading').show();
        var monitorId = $('#monitorId').val();
        $.getJSON(decodeURI(_getLocationUrl())).done(function(data) {
            
            var 
            icon, icons = [],
            source = _getLayer('icons').getSource(),
            fc = monitorId ? data.locations : data,
            mappingData = $('#mappingColors');
                        
            if (mappingData.length) {
                _mappingColors = yukon.fromJson(mappingData);
            }
            
            debug.time('Loading Icons');
            
            for (var i in fc.features) {
                var feature = fc.features[i],
                    pao = feature.properties.paoIdentifier;
                icon = _createFeature(fc.features[i], fc.crs.properties.name);
                _updateStyleIfInViolation(data, pao, icon);
                _updateStyleIfFoundInMappingColors(pao.paoId, icon);
                icons.push(icon);
            }
            source.addFeatures(icons);
            
            _updateZoom();

            $('.js-status-loading').hide();
            debug.timeEnd('Loading Icons');
            
            if ($('#map').is('[data-dynamic]')) {
                _update();
            }
        });
    },
    
    /** Method to update device list with recursive setTimeout. */
    _update = function(once) {
        var monitorId = $('#monitorId').val();
        $.getJSON(decodeURI(_getLocationUrl())).done(function(data) {
            
            var toAdd = [], toRemove = [], icons = {},
                i, pao, feature, diff,
                source = _getLayer('icons').getSource(),
                fc = monitorId ? data.locations : data;
            
            // add any features we don't have
            for (i = 0; i < fc.features.length; i++) {
                feature = fc.features[i],
                pao = feature.properties.paoIdentifier;
                
                icons[pao.paoId] = feature;
                
                if (typeof _icons[pao.paoId] === 'undefined') {
                    var
                    icon = _createFeature(feature, fc.crs.properties.name);
                    _updateStyleIfInViolation(data, pao, icon);
                    toAdd.push(icon);
                } else {
                    var icon = _icons[pao.paoId];
                    _updateStyleIfInViolation(data, pao, icon);
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
            if (monitorId) {
                //update violations
                var violationsBadge = $('#violation-collection .js-violations');
                var currentViolations = parseInt(violationsBadge.text(), 10);
                if (currentViolations !== data.violationDevices.length) {
                    violationsBadge.text(data.violationDevices.length);
                    violationsBadge.addClass('animated flash');
                }
            }
            else if (diff !== 0) {
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
    
    _updateZoom = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        var features = source.getFeatures();
        if (features != null) {
            if (features.length > 1) {
                _map.getView().fit(source.getExtent(), _map.getSize());
                if (_map.getView().getZoom() > 16){
                    _map.getView().setZoom(16);
                }
            } else if (features.length == 1){
                _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
                _map.getView().setZoom(9);
            }
        }
    },
    
    _removeDeviceFocusLayers = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        _deviceFocusIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _deviceFocusLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _map.removeLayer(_deviceFocusIconLayer);
        _deviceFocusIcons = [];
        _deviceFocusLines = [];
        _deviceFocusIconLayer = null;
        //set focus device back to normal style
        if (_deviceFocusCurrentIcon != null) {
            //var normalStyle = _deviceFocusCurrentIcon.getStyle().clone();
            var normalStyle = _deviceFocusCurrentIcon.getStyle();
            normalStyle.getImage().setScale(1);
            _deviceFocusCurrentIcon.setStyle(normalStyle);
        }
        yukon.mapping.hideNeighborsLegend();
    },
    
    _findFocusDevice = function(deviceId, makeLarger) {
        //check icons first
        var exists = [];
        for (var i in _icons) {
            if (_icons[i].get('pao').paoId === deviceId) {
                exists.push(_icons[i]);
                break;
            }
        }
        //check focus devices next
        if (exists.length === 0) {
            exists = _deviceFocusIcons.filter(function (device) {
                if (device.getProperties().routeInfo != null) {
                    return device.getProperties().routeInfo.device.paoIdentifier.paoId === deviceId;
                } else if (device.getProperties().neighbor != null) {
                    return device.getProperties().neighbor.device.paoIdentifier.paoId === deviceId;
                }
            });
        }
        if (exists.length > 0) {
            var focusDevice = exists[0];
            if (makeLarger) {
                var largerStyle = focusDevice.getStyle().clone();
	            largerStyle.getImage().setScale(_largerScale);
	            focusDevice.setStyle(largerStyle);
            }
            return focusDevice;
        }
    },
    
    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            routeColor = '#808080',
            routeLineWidth = 2.5;

        _primaryRoutePreviousPoints = null;
        _removeDeviceFocusLayers();
        _deviceFocusCurrentIcon = focusDevice;

        for (var x in routeInfo) {
            var route = routeInfo[x],
                feature = route.location.features[0],
                src_projection = 'EPSG:4326',
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ routeInfo: route });
            
            icon.setStyle(style);
            
            //check if device already exists on map...the first device will always be the original device so make the icon larger
            var deviceFound = _findFocusDevice(feature.properties.paoIdentifier.paoId, x == 0);
            if (deviceFound) {
            	icon = deviceFound;
            } else {
                if (src_projection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }

                _deviceFocusIcons.push(icon);
                source.addFeature(icon);
            }
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (_primaryRoutePreviousPoints != null) {
                points.push(_primaryRoutePreviousPoints);
            } else {
                points.push(focusPoints);
            }
            _primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),
                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: routeColor, width: routeLineWidth })
                })
            });
            
            layerLines.setZIndex(_primaryRouteLayerIndex);
            _deviceFocusLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: _deviceFocusIcons})});
        iconsLayer.setZIndex(_iconLayerIndex);
        _deviceFocusIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            clonedFocusDevice = focusDevice.clone();
            
           _removeDeviceFocusLayers();
           _deviceFocusCurrentIcon = clonedFocusDevice;
           _deviceFocusIcons.push(clonedFocusDevice);
           source.addFeature(clonedFocusDevice);
        
        for (var x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            src_projection = 'EPSG:4326',
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ neighbor: neighbor });
            
            //check if neighbor already exists on map
            var neighborFound = _findFocusDevice(feature.properties.paoIdentifier.paoId, false);
            if (neighborFound) {
            	icon = neighborFound;
            } else {
                icon.setStyle(style);
                
                if (src_projection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                _deviceFocusIcons.push(icon);
                source.addFeature(icon);
            }
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            points.push(focusPoints);

            var lineColor = yukon.mapping.getNeighborLineColor(neighbor.data.etxBand),
                lineThickness = yukon.mapping.getNeighborLineThickness(neighbor.data.numSamples);
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),

                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: lineColor, width: lineThickness })
                })
            });
            
            layerLines.setZIndex(_neighborsLayerIndex);
            _deviceFocusLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        
        var allIcons = [];
        allIcons.push.apply(allIcons, _deviceFocusIcons);
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        _deviceFocusIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
    },
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen({source: 'map-container'}),
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
            /** Hide any cog dropdowns on zoom or map move **/
            _map.getView().on('change:resolution', function(ev) {
               $('.dropdown-menu').css('display', 'none');
            });
            _map.on('movestart', function(ev) {
                $('.dropdown-menu').css('display', 'none');
             });
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { return feature; });
                if (feature) {
                    var 
                    geometry = feature.getGeometry(),
                    coord = geometry.getCoordinates(),
                    properties = feature.getProperties(),
                    neighbor = properties.neighbor,
                    routeInfo = properties.routeInfo;
                    if (routeInfo != null) {
                        yukon.mapping.displayCommonPopupProperties(routeInfo);
                        yukon.mapping.displayPrimaryRoutePopupProperties(routeInfo);
                        _overlay.setPosition(coord);
                    } else if (neighbor != null) {
                        yukon.mapping.displayCommonPopupProperties(neighbor);
                        yukon.mapping.displayNeighborPopupProperties(neighbor);
                        _overlay.setPosition(coord);
                    } else {
                        $('#parent-info').hide();
                        $('#neighbor-info').hide();
                        $('#route-info').hide();
                        var url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info');
                        $('#device-info').load(url, function() {
                            $('#device-info').show();
                            $('#marker-info').show();
                            _overlay.setPosition(coord);
                        });
                        //close any lingering delete dialogs to simplify handling
                        var deleteDialog = $('#confirm-delete');
                        if (deleteDialog.hasClass('ui-dialog-content')) {
                            deleteDialog.dialog('destroy');
                        }
                    }
                } else {
                    var target = ev.originalEvent.target;
                    //check if user clicked on the cog, the error hide-reveal, or notes icon
                    var cog = $(target).closest('.js-cog-menu'),
                        error = $(target).closest('.hide-reveal-container'),
                        notesIcon = $(target).closest('.js-view-all-notes');
                    if (!cog.exists() && !error.exists() && !notesIcon.exists()) {
                        $('#marker-info').hide();
                    }
                }
            });
            
            $('#violationsSelect').on('change', function(ev) {
                _update(true);
            });
            
            $('#devicesFilter').on('change', function(ev) {
                $(this).trigger('yukon.map.filter');
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
                            select.attr('id', 'state-select');
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
                        + ' ' + $('#attribute-select option:selected').text() + ' - ' + $('#state-select').find('option:selected').text());
                
                var start = new Date().getTime();
                
                $('#filter-form').ajaxSubmit({
                    dataType: 'json',
                    success: function(json) {
                        var results = json.results,
                            filteredCount = json.filteredDeviceCount,
                            filteredDevicesOnly = $('#devicesFilter').val();
                        
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
                                var currentStyle = icon.getStyle().clone(),
                                    image = currentStyle.getImage(),
                                    src = image.getSrc();  
                                if (show) {
                                    currentStyle.setImage(new ol.style.Icon({ src: src, color: _violationColor, anchor:  [0.5, 1.0] }));
                                    icon.setStyle(currentStyle);
                                } else {
                                    currentStyle.setImage(new ol.style.Icon({ src: src, anchor:  [0.5, 1.0] }));
                                    icon.setStyle(currentStyle);
                                    _updateStyleIfFoundInMappingColors(paoId, icon);
                                }
                                if (show && !visible || (filteredDevicesOnly == "false" && !visible)) {
                                    toAdd.push(icon);
                                    _visibility[paoId] = true;
                                } else if (!show && visible) {
                                    if (filteredDevicesOnly == "true") {
                                        toRemove.push(icon);
                                        _visibility[paoId] = false;
                                    }
                                }
                            }
                        }
                        
                        //update filtered count
                        var filteredBadge = $('#filtered-collection .js-filtered');
                        var currentFiltered = parseInt(filteredBadge.text(), 10);
                        if (currentFiltered !== filteredCount) {
                            filteredBadge.text(filteredCount);
                            filteredBadge.addClass('animated flash');
                        }
                        
                        $('.js-filtered-devices').removeClass('dn');
                        $('.js-color-collections').addClass('dn');
                        
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
                    var icon = _icons[paoId],
                        currentStyle = icon.getStyle().clone(),
                        image = currentStyle.getImage(),
                        src = image.getSrc();
                    currentStyle.setImage(new ol.style.Icon({ src: src, anchor:  [0.5, 1.0] }));
                    icon.setStyle(currentStyle);
                    _updateStyleIfFoundInMappingColors(paoId, icon);
                    if (!_visibility[paoId]) {
                        toAdd.push(icon);
                        _visibility[paoId] = true;
                    }
                }
                _getLayer('icons').getSource().addFeatures(toAdd);
                
                //update filtered count
                var filteredBadge = $('#filtered-collection .js-filtered');
                filteredBadge.text(0);
                filteredBadge.addClass('animated flash');
                $('.js-filtered-devices').addClass('dn');
                $('.js-color-collections').removeClass('dn');

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
                var paoId = $('#remove-pin').data("device-id"),
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
            
            /** Redirects to new device map network page **/
            $(document).on('click', '.js-device-map', function() {
                var deviceId = $(this).data('deviceId');
                window.location.href = yukon.url('/stars/mapNetwork/home') + '?deviceId=' + deviceId;
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-neighbors', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/neighbors') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.neighbors) {
                        _addNeighborDataToMap(deviceId, json.neighbors);
                        yukon.mapping.displayNeighborsLegend();
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-route', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.routeInfo) {
                        _addPrimaryRouteToMap(deviceId, json.routeInfo);
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
            });
            
            /** Remove animation classes when animation finishes. */
            $('#device-collection .js-count')
                .on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(ev) {
                $('#device-collection .js-count').removeClass('animated flash'); 
            });
            $('#violation-collection .js-violations')
                .on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(ev) {
                    $('#violation-collection .js-violations').removeClass('animated flash'); 
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
            
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {                
                // we if are doing an exit from the full screen, close any open pop-ups
                if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                    $(".ui-dialog-content").dialog("close");
                    if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                        $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                    }
                    //adjust height back
                    $('#map-container').css('padding-bottom', '0px');
                    $('#map-container').css('padding-top', '0px');
                } else {
                    //adjust height for mapping buttons
                    $('#map-container').css('padding-bottom', '120px');
                    $('#map-container').css('padding-top', '10px');
                }
                
                //close any popups
                $('#marker-info').hide();
                _updateZoom();
                _map.updateSize();
            });
            
            $("body").on("dialogopen", function (event, ui) {
                // if the user is viewing the map in fullscreen mode, append the dialog to the div that is being displayed in fullscreen mode.
                // So that the dialog displays over the fullscreen div. 
                if (document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement) {
                    $(event.target).closest('.ui-dialog').appendTo("div.ol-viewport");
                    $(event.target).closest('.ui-dialog').find('.ui-dialog-content').scroll( function (event) {
                        if ($(this).hasClass('menu-open')) {
                            $(this).removeClass('menu-open');
                        }
                        $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                    });
                    
                    // If a dialog box is opened while viewing the map in full screen map, disable double click and
                    // mouse wheel zoom interactions since they perform a zoom operation on map.
                    if(!_interactionsBlocked) {
                        _doubleClickZoomInteraction.setActive(false);
                        _mouseWheelZoomInteraction.setActive(false);
                        _interactionsBlocked = true;
                    }
                }
            });
            
            $("body").on("dialogclose", function(event, ui) {
                // If Double click and mouse wheel zoom interactions are disabled AND there is no dialog box open,
                // enable those operations again.
                if(($("body").find(".ui-dialog:visible").length === 0) && _interactionsBlocked) {
                    _doubleClickZoomInteraction.setActive(true);
                    _mouseWheelZoomInteraction.setActive(true);
                    _interactionsBlocked = false;
                }
            });
            
            var interactions = _map.getInteractions();
            for(var i=0; i < interactions.getLength(); i++) {
                var interaction = interactions.item(i);
                if (interaction instanceof ol.interaction.DoubleClickZoom) {
                    _doubleClickZoomInteraction = interaction;
                } else if (interaction instanceof ol.interaction.MouseWheelZoom) {
                    _mouseWheelZoomInteraction = interaction;
                }
            }
            
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