
/**
 * Singleton that manages the Device Mapping feature
 * 
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */

yukon.namespace('yukon.collection.map');

yukon.collection.map = (function () {
    
    var
    mod = {},
    initialized = false,
    map = {},
    
    _loadIconLayer = function(dest_projection) {
        $.getJSON('/yukon/tools/map/locations?collectionType=group&group.name=%2FRF+Cart+Devices').done(function(fc) {
            
            var 
            icons = [],
            src_projection = fc.crs.properties.name;  // Yukon currently storing coords as ESPG:4326 (WGS84)
            
            for (i in fc.features) {
                var
                feature = fc.features[i],
                icon = new ol.Feature({paoIdentifier: feature.properties.paoIdentifier}),
                iconStyle = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        opacity: 1.0,
                        src: yukon.url('/WebConfig/yukon/Icons/electric_16.png')
                    })
                });
                icon.setStyle(iconStyle);
                
                if (src_projection === dest_projection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates)); 
                } else {
                    icon.setGeometry(new ol.geom.Point(ol.proj.transform(feature.geometry.coordinates, src_projection, dest_projection)));
                }
                
                icons.push(icon);
            }
            
            map.addLayer(new ol.layer.Vector({ source: new ol.source.Vector({ features: icons, projection: dest_projection }) }));
        });
    };

    mod = {
            
        init: function() {

            if (initialized) {
                return;
            }
            
            var osmLayer = new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'osm'}) });
            
            map = new ol.Map({
                controls: ol.control.defaults().extend([new ol.control.FullScreen()]),
                target: 'map',
                layers: [osmLayer],
                view: new ol.View2D({
                    center: [0, 0],
                    zoom: 9
                })
            });
            
            var dest_projection = mod.getMap().getView().getProjection().getCode();
            map.getView().setCenter(ol.proj.transform([-93.557708, 45.254846], 'EPSG:4326', dest_projection));
            _loadIconLayer(dest_projection);
            
            initialized = true;
        },

        getMap: function () {
            return map;
        },
        
        changeIcon: function () {
            
            var 
            dest_projection = mod.getMap().getView().getProjection().getCode(),
            iconFeature = new ol.Feature({
                geometry: new ol.geom.Point(ol.proj.transform([-93.557708, 45.254846], 'EPSG:4326', dest_projection))
            }),
            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [0.5, 0.5],
                    opacity: 1.0,
                    src: yukon.url('/WebConfig/yukon/Icons/gas_16.png')
                }))
            });

            iconFeature.setStyle(iconStyle);
            var source = mod.getMap().getLayers().getAt(1).getSource();
            source.removeFeature(source.getFeatures()[0]);
            source.addFeature(iconFeature);
        }
    };
    
    return mod;
})();

$(function() { yukon.collection.map.init(); });