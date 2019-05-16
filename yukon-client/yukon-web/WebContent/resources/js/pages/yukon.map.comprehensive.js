yukon.namespace('yukon.map.comprehensive');
 
/**
 * Module to handle the Comprehensive Map page
 * 
 * @module yukon.map.comprehensive
 * @requires yukon
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.comprehensive = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //dark blue
    _routeColor = "#0000CC",
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _iconLayerIndex = 3,
    _relayLayerIndex = 4,
    _gatewayLayerIndex = 5,
    
    _primaryRoutePreviousPoints,
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _deviceFocusIconLayer,
    _largerScale = 1.3,
    _deviceScale = 0.8,
    _relayScale = 0.9,
    _gatewayScale = 1,
    
    _highlightDevice,
    _highlightOldStyle,
        
    /** @type {Object.<number, {ol.Feature}>} - Map of pao id to feature for all device icons. */
    _icons = [], 
    
    _destProjection = yukon.mapping.getDestProjection(),
    _srcProjection = yukon.mapping.getSrcProjection(),
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    _loadDevices = function(color, devices) {
        var layerIcons = [];
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in devices.features) {
            var feature = devices.features[x],
                zIndex = _iconLayerIndex,
                pao = feature.properties.paoIdentifier,
                src_projection = devices.crs.properties.name,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
                icon = new ol.Feature({ device: feature, pao: pao });
            
            icon.setId(feature.id);
            icon.setStyle(style);

            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc();
            
            currentStyle.setImage(new ol.style.Icon({ src: src, color: color, anchor:  [0.5, 1.0] }));
            
            icon.setStyle(currentStyle);
            
            _setScaleForDevice(icon);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
        
            layerIcons.push(icon);
            _icons.push(icon);
            source.addFeature(icon);
        }
        
        yukon.mapping.updateZoom(_map);
        
    },
    
    _findFocusDevice = function(deviceId, makeLarger) {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
            feature = source.getFeatureById(deviceId);
            if (feature && makeLarger) {
                _makeDeviceIconLarger(feature);
            }
            return feature;
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
            var normalStyle = _deviceFocusCurrentIcon.getStyle();
            normalStyle.getImage().setScale(1);
            _deviceFocusCurrentIcon.setStyle(normalStyle);
        }
        yukon.mapping.hideNeighborsLegend();
    },
    
    _setScaleForDevice = function(feature) {
        var currentStyle = feature.getStyle().clone(),
            scale = _deviceScale,
            pao = feature.get('pao');
        //make larger if relay
        if (pao.paoType === 'RFN_RELAY') {
            scale = _relayScale;
        } else if (pao.paoType === 'RFN_GATEWAY' || pao.paoType === 'GWY800') {
            scale = _gatewayScale;
        }
        currentStyle.getImage().setScale(scale);
        feature.setStyle(currentStyle);
    }
    
    //remove neighbors and route information from all icons and set back to initial scale
    _setIconsBack = function() {
        for (var i in _icons) {
            var icon = _icons[i];
            icon.unset("neighbor");
            icon.unset("routeInfo");
            _setScaleForDevice(icon);
        }
    },
    
    _makeDeviceIconLarger = function(icon) {
        var largerStyle = icon.getStyle().clone();
        largerStyle.getImage().setScale(_largerScale);
        icon.setStyle(largerStyle);
    },
    
    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            routeColor = '#808080',
            routeLineWidth = 2.5;

        _primaryRoutePreviousPoints = null;
        _removeDeviceFocusLayers();
        _setIconsBack();
        _deviceFocusCurrentIcon = focusDevice;

        for (var x in routeInfo) {
            var route = routeInfo[x],
                feature = route.location.features[0],
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ routeInfo: route });
            icon.setId(feature.id);
            
            //check if device already exists on map...the first device will always be the original device so make the icon larger
            var deviceFound = _findFocusDevice(feature.properties.paoIdentifier.paoId, x == 0);
            if (deviceFound) {
                icon = deviceFound;
                icon.set("routeInfo", route);
                icon.unset("neighbor");
            } else {
                icon.setStyle(style);
                icon.set("pao", feature.properties.paoIdentifier);
                _setScaleForDevice(icon);
                if (x == 0) {
                    _makeDeviceIconLarger(icon);
                }
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
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
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            clonedFocusDevice = focusDevice.clone();
            
        clonedFocusDevice.setStyle(focusDevice.getStyle().clone());
        clonedFocusDevice.unset("routeInfo");
        clonedFocusDevice.unset("neighbor");
            
        _removeDeviceFocusLayers();
        _setIconsBack();
        _deviceFocusCurrentIcon = clonedFocusDevice;
        _deviceFocusIcons.push(clonedFocusDevice);
        source.addFeature(clonedFocusDevice);

        for (var x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ neighbor: neighbor });
            icon.setId(feature.id);

            //check if neighbor already exists on map
            var neighborFound = _findFocusDevice(feature.properties.paoIdentifier.paoId, false);
            if (neighborFound) {
                icon = neighborFound;
                icon.set("neighbor", neighbor);
                icon.unset("routeInfo");
            } else {
                icon.setStyle(style);
                icon.set("pao", feature.properties.paoIdentifier);
                _setScaleForDevice(icon);

                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
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
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(".js-chosen").chosen({width: "400px", max_selected_options: 5});
            $(".js-chosen").bind("chosen:maxselected", function () {
                var gatewayError = $('#tooManyGatewaysError').val();
                yukon.ui.alertError(gatewayError);
            }); 
            
            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen({source: 'comprehensive-map-container'}),
                    new ol.control.Zoom() 
                ],
                layers: _tiles,
                target: 'comprehensive-map',
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], _srcProjection, _destProjection), zoom: 4 })
            });
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { return feature; });
                if (feature && feature.get('pao') != null) {
                    yukon.mapping.displayMappingPopup(feature, _overlay);
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
            
            /** Change mouse cursor when over marker.  There HAS to be a css way to do this! */
            $(_map.getViewport()).on('mousemove', function(e) {
                var pixel = _map.getEventPixel(e.originalEvent),
                    hit = _map.forEachFeatureAtPixel(pixel, function(feature, layer) { return true; });
                $('#' + _map.getTarget()).css('cursor', hit ? 'pointer' : 'default');
            });
            
            $('.js-chosen').on('change', function(evt, params) {
                var values = $(".js-chosen").chosen().val();
                $('.js-filter-map').prop('disabled', values.length == 0);
            });
            
            $(document).on('click', '.js-filter-map', function (ev) {
                var form = $('#filter-form');
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/filter'),
                    type: 'get',
                    data: form.serialize()
                }).done( function(data) {
                    //clear any existing features
                    var source = _map.getLayers().getArray()[_tiles.length].getSource();
                    if (_icons.length > 0) {
                        _icons.forEach(function(icon) {
                            if (source.getFeatureById(icon.get('pao').paoId)) {
                                source.removeFeature(icon); 
                            }
                        });
                        _icons = [];
                    }
                    _removeDeviceFocusLayers();
                    var map = data.map.mappedDevices;
                    Object.keys(map).forEach(function(key) {
                        var value = map[key];
                        _loadDevices(key, value);
                    });
                    $('#legend').empty();
                    for (var i = 0; i < data.map.legend.length; i++) {
                        var legendItem = data.map.legend[i];
                        $('#legend').append('<span class=small-circle style=margin-left:5px;margin-bottom:5px;background:' + legendItem.hexColor + '></span><span style=margin-left:5px;>' + legendItem.text + '</span>');
                    }
                    $('#legend').removeClass('dn');
                    yukon.ui.unbusy('.js-filter-map');
                });
            });
            
            $("#findDevice").keyup(function(event) {
                if (event.keyCode === 13) {
                    var searchText = $('#findDevice').val();
                    //change last found device back
                    if (_highlightDevice) {
                        _highlightDevice.setStyle(_highlightOldStyle);
                    }
                    var searchError = $('.js-no-results-found');
                    searchError.addClass('dn');
                    if (searchText) {
                        $.ajax({
                            url: yukon.url('/stars/comprehensiveMap/search?searchText=' + searchText),
                            type: 'get',
                        }).done( function(data) {
                            if (data.paoId) {
                                var source = _map.getLayers().getArray()[_tiles.length].getSource(),
                                    feature = source.getFeatureById(data.paoId);
                                if (feature) {
                                    _highlightDevice = feature;
                                    _highlightOldStyle = feature.getStyle();
                                    var largerStyle = feature.getStyle().clone(),
                                        circleStyle = new ol.style.Style({
                                        image: new ol.style.Circle({
                                            radius: 8,
                                            fill: new ol.style.Fill({color: _routeColor}),
                                            stroke: new ol.style.Stroke({color: 'black', width: 2}) 
                                        })
                                    });
                                    largerStyle.getImage().setScale(_largerScale);
                                    feature.setStyle([circleStyle, largerStyle]);
                                    _map.getView().setCenter(feature.getGeometry().getCoordinates());
                                    _map.getView().setZoom(16);
                                } else {
                                    searchError.removeClass('dn');
                                }
                            } else {
                                searchError.removeClass('dn');
                            }
                        });
                    }
                }
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
            
            /** Add an elevation layer to the map **/
            $(document).on('click', '.js-elevation-layer', function() {
                var checked = $(this).hasClass('on');
                if (checked) {
                    $(this).removeClass('on');
                    _map.removeLayer(_elevationLayer);
                } else {
                    _elevationLayer = new ol.layer.VectorTile({
                        declutter: true,
                        opacity: 0.6,
                        source: new ol.source.VectorTile({
                            format: new ol.format.MVT(),
                            url: yg.map_devices_elevation_url
                        }),
                    })
                    $(this).addClass('on');
                    _map.addLayer(_elevationLayer);
                }
            });
            
            /** Remove the coordinates for the device when the user clicks OK on the confirmation popup. **/
            $(document).on('yukon:tools:map:delete-coordinates', function(event) {
                var paoId = $('#remove-pin').data("device-id"),
                    removeUrl = yukon.url('/tools/map/device/' + paoId);
                
                $.ajax({
                    url: removeUrl,
                    type: 'POST',
                    data: {_method: 'DELETE'},
                    success: function(results) {
                        $('#confirm-delete').dialog('destroy');
                        yukon.ui.removeAlerts();
                        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
                            feature = source.getFeatureById(paoId);
                        if (feature) {
                            source.removeFeature(feature);
                        }
                        var successMsg = $('#coordinatesDeletedMsg').val();
                        yukon.ui.alertSuccess(successMsg);
                    },
                    error: function(xhr, status, error) {
                        var errorMsg = xhr.responseJSON.message;
                        yukon.ui.alertError(errorMsg);
                    }
                });
                
            });
                        
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                // we if are doing an exit from the full screen, close any open pop-ups
                if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                    $(".ui-dialog-content").dialog("close");
                    if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                        $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                    }
                    //adjust height back
                    $('#comprehensive-map-container').css('padding-bottom', '0px');
                    $('#comprehensive-map-container').css('padding-top', '0px');
                } else {
                    //adjust height for mapping buttons
                    $('#comprehensive-map-container').css('padding-bottom', '220px');
                    $('#comprehensive-map-container').css('padding-top', '0px');
                }
                //close any popups
                $('#marker-info').hide();
                yukon.mapping.updateZoom(_map);
                _map.updateSize();
            });
            
            $("body").on("dialogopen", function (event, ui) {
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
            
            _initialized = true;
        },
    };
 
    return mod;
})();
 
$(function () { yukon.map.comprehensive.init(); });