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
    _iconLayerIndex = 3,
    _relayLayerIndex = 4,
    _gatewayLayerIndex = 5,
    
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    _loadDevices = function(color, devices) {
        var _icons = [];
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in devices.features) {
            var feature = devices.features[x],
                scale = 1,
                zIndex = _iconLayerIndex,
                pao = feature.properties.paoIdentifier,
                src_projection = devices.crs.properties.name,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
                icon = new ol.Feature({ device: feature, pao: pao });
            
            icon.setStyle(style);

            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc();
            
            //make larger if relay
            if (pao.paoType === 'RFN_RELAY') {
                scale = 1.2;
                zIndex = _relayLayerIndex;
            }
            
            currentStyle.setImage(new ol.style.Icon({ src: src, color: color, anchor:  [0.5, 1.0], scale: scale }));
            
            icon.setStyle(currentStyle);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
        
            _icons.push(icon);
            source.addFeature(icon);
        }

        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: _icons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(zIndex);
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
    },
    
    // MOVE TO COMMON???
    _updateZoom = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        var features = source.getFeatures();
        if (features != null && features.length > 1) {
            _map.getView().fit(source.getExtent(), _map.getSize());
            if (_map.getView().getZoom() > 16){
                _map.getView().setZoom(16);
            }
        } else {
            _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
            _map.getView().setZoom(13);
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(".js-chosen").chosen({width: "200px", max_selected_options: 5});
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
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
            });
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { return feature; });
                if (feature) {
                    var 
                    geometry = feature.getGeometry(),
                    coord = geometry.getCoordinates(),
                    properties = feature.getProperties();
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
            
            /** MOVE TO COMMON??? Change map tiles layer on tile button group clicks. */
            $('#map-tiles button').click(function (ev) {
                $(this).siblings().removeClass('on');
                $(this).addClass('on');
                for (var i in _tiles) {
                    var layer = $(this).data('layer');
                    _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                }
                
            });
            
            $(document).on('click', '.js-filter-map', function (ev) {
                var form = $('#filter-form');
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/filter'),
                    type: 'get',
                    data: form.serialize()
                }).done( function(data) {
                    var map = data.map.mappedDevices;
                    Object.keys(map).forEach(function(key) {
                        var value = map[key];
                        _loadDevices(key, value);
                    });
                    $('#legend').empty();
                    for (var i = 0; i < data.map.legend.length; i++) {
                        var legendItem = data.map.legend[i];
                        $('#legend').append('<div class=small-circle style=margin-left:5px;margin-bottom:5px;background:' + legendItem.hexColor + '><span style=margin-left:10px;>' + legendItem.text + '</span></div>');
                        $('#legend').removeClass('dn');
                    }
                });
            });
                        
            //MOVE TO COMMON??
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
                    $('#comprehensive-map-container').css('padding-bottom', '140px');
                    $('#comprehensive-map-container').css('padding-top', '0px');
                }
                //close any popups
                $('#marker-info').hide();
                _updateZoom();
                _map.updateSize();
            });
            
            //MOVE TO COMMON??
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
            
            //MOVE TO COMMON?
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