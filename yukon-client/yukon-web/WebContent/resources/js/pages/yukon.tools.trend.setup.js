yukon.namespace('yukon.tools.trend.setup');

/**
 * Module that handles the behavior on the setup Trend.
 * @module yukon.tools.trend.setup
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.trend.setup = (function() {

    'use strict';

    var _initialized = false,
    
    _trendSetupDialogClass = "js-trend-setup-dialog-class",
    
    _initColorPicker = function (dialog) {
        var colorArray = dialog.find(".js-color-item").map(function () {return $(this).val();}).get(),
            colorArrayLength = colorArray.length,
            firstRowOfColors = colorArray.splice(0, colorArrayLength/2);
        
        dialog.find(".js-color-picker").spectrum({
            showPaletteOnly: true,
            hideAfterPaletteSelect:true,
            palette: [
                colorArray, firstRowOfColors
            ],
            preferredFormat: "hex",
            move: function(color){
                dialog.find(".js-color-input").val(color.toHexString());
            },
            show: function(color) {
                //Display color name instead of color hex value as a tooltip text
                var colors = $(document).find(".sp-thumb-el:visible");
                colors.each(function(index) {
                    var hexValue = $(this).prop('title'),
                        colorName = dialog.find("input[value='" + hexValue + "']").data("colorName");
                    $(this).prop('title', colorName);
                });
            }
        });
    },
    
    _initPointSetupPopup = function (dialog) {
        yukon.ui.initDateTimePickers().ancestorInit(dialog);
        _initColorPicker(dialog);
    },
    
    _updateSetupTable = function (dialog, isMarkerRow, rowUpdated) {
        yukon.ui.busy(dialog.closest(".ui-dialog").find(".js-primary-action"));
        var form = dialog.find("form"),
               displayTable = $("#js-point-setup-table"),
               templateTable = $(".js-point-setup-template-table");
        
        if (isMarkerRow) {
            displayTable = $("#js-marker-setup-table");
            templateTable = $(".js-marker-setup-template-table");
        }
        
        form.ajaxSubmit({
            success: function(data, status, xhr, $form) {
                dialog.dialog('close');
                var clonnedRow = templateTable.find(".js-template-row").clone();
                clonnedRow.removeClass("js-template-row");
                clonnedRow.find(".js-label span").html(yukon.escapeXml(data.trendSeries.label));
                clonnedRow.find(".js-color span").text(data.color);
                clonnedRow.find(".js-color div.small-rectangle").css("background-color", data.colorHexValue);
                clonnedRow.find(".js-axis").html(data.axis);
                clonnedRow.find(".js-multiplier").html(data.trendSeries.multiplier);
                clonnedRow.find(".js-row-data").val(JSON.stringify(data.trendSeries));
                if (!isMarkerRow) {
                    clonnedRow.find(".js-point-name").html(yukon.escapeXml(data.pointName));
                    clonnedRow.find(".js-device-name").html(yukon.escapeXml(data.deviceName));
                    if (data.hasOwnProperty("dateStr")) {
                        clonnedRow.find(".js-type").html(data.dateStr);
                    } else {
                        clonnedRow.find(".js-type").html(data.graphType);
                    }
                    clonnedRow.find(".js-style").html(data.style);
                }
                
                if (rowUpdated !== null) {
                    rowUpdated.empty();
                    rowUpdated.html(clonnedRow.html());
                } else {
                    displayTable.append(clonnedRow);
                }
                yukon.ui.reindexInputs(displayTable);
            },
            error: function (xhr, status, error, $form) {
                dialog.html(xhr.responseText);
                if (isMarkerRow) {
                    _initColorPicker(dialog);
                } else {
                    _initPointSetupPopup(dialog);
                }
                yukon.ui.unbusy(dialog.closest(".ui-dialog").find(".js-primary-action"));
            }
        });
    },

    mod = {

        /** Initialize this module. */
        init: function() {

            if (_initialized)
                return;
            
            $(document).on("yukon:trend:setup:pointSelection:complete", function (ev, items, picker) {
                $(".js-device-name-span").html(yukon.escapeXml(items[0].deviceName));
                var label = items[0].pointName + " / " + items[0].deviceName;
                $(".js-point-label-input").val(label.substring(0, 40));
            });
            
            $(document).on("yukon:trend:setup:addPoint", function () {
                _updateSetupTable($("#js-add-point-dialog:visible"), false, null);
            });
            
            $(document).on("yukon:trend:setup:updatePoint", function(event) {
                _updateSetupTable($(".js-edit-point-dialog:visible"), false, $(event.target));
            });
            
            $(document).on("yukon:trend:setup:updateMarker", function(event) {
                _updateSetupTable($(".js-edit-marker-dialog:visible"), true, $(event.target));
            });

            $(document).on("yukon:trend:setup:addMarker", function() {
                _updateSetupTable($("#js-add-marker-dialog:visible"), true, null);
            });
            
            $(document).on("yukon:trend:setup:markerPopupLoaded", function(event) {
                _initColorPicker($(event.target));
            });
            
            $(document).on("yukon:trend:setup:pointPopupLoaded", function (event) {
                _initPointSetupPopup($(event.target));
            });
            
            $(document).on("change", ".js-graph-type", function () {
                var isDateTypeGraphSelected = $(this).val() === $(".js-date-type-enum-value").val();
                var uniqueIdentifier = $(".js-unique-identifier").val();
                $(".js-date-picker-row").toggleClass("dn", !isDateTypeGraphSelected);
                if (isDateTypeGraphSelected) {
                    var date = moment().tz(yg.timezone).format(yg.formats.date.date);
                    $(this).closest("form").find("#js-date-picker_" + uniqueIdentifier).val(date);
                }
            });

            $(document).on("click", ".js-remove", function (event) {
                $(this).closest("tr").remove();
                yukon.ui.reindexInputs($(event.target).closest('table'));
            });
            
            $(document).on("click", ".js-edit-point", function () {
                var row = $(this).closest("tr"),
                       isMarker = $(this).hasClass("js-marker"),
                       pointSetupData = row.find(".js-row-data").val(),
                       dialogTitle,
                       cssClass,
                       url,
                       helpText,
                       okEvent,
                       loadEvent,
                       form = $("<form/>"),
                       hiddenInput = $("<input/>").attr({
                           type: "hidden",
                           name: "trendSeries"
                       }); 
                hiddenInput.val(pointSetupData);
                form.append(hiddenInput);
                row.uniqueId();
                
                if (isMarker) {
                    dialogTitle = yg.text.edit + " " + $(this).closest("tr").find(".js-label span").text();
                    url = "/tools/trend/renderEditSetupPopup?";
                    cssClass = "js-edit-marker-dialog";
                    okEvent = 'yukon:trend:setup:updateMarker';
                    loadEvent = 'yukon:trend:setup:markerPopupLoaded';
                    helpText = $('.js-marker-help').val();
                } else {
                    dialogTitle = yg.text.edit + " " + $(this).closest("tr").find(".js-point-name").text();
                    url = "/tools/trend/renderEditSetupPopup?";
                    cssClass = "js-edit-point-dialog";
                    okEvent = 'yukon:trend:setup:updatePoint';
                    loadEvent = 'yukon:trend:setup:pointPopupLoaded';
                }        
    
                var dialogDivJson = {
                    "data-title": dialogTitle,
                    "data-dialog": '',
                    "class": cssClass + " " + _trendSetupDialogClass,
                    "data-event": okEvent,
                    "data-load-event": loadEvent,
                    "data-help-text": helpText,
                    "data-width": "500",
                    "data-target": '#' + row.attr('id'),
                    "data-url": yukon.url(url + form.serialize())
                }
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));

            });
            
            $(document).on('click', '.js-add-point', function () {
                var isMarker = false,
                       numberOfPoints = $("#js-point-setup-table tr").length - 1,
                       url =  yukon.url("/tools/trend/renderSetupPopup") + "?isMarker=" + isMarker + "&numberOfRows=" + numberOfPoints,
                       dialogDivJson = {
                        "data-title": $(".js-add-point-title").val(),
                        "data-dialog": '',
                        "id": "js-add-point-dialog",
                        "class": _trendSetupDialogClass,
                        "data-event": "yukon:trend:setup:addPoint",
                        "data-load-event": "yukon:trend:setup:pointPopupLoaded",
                        "data-url": url
                    }
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });
            
            $(document).on('click', '.js-add-marker', function () {
                var isMarker = true,
                       numberOfPoints = $("#js-marker-setup-table tr").length - 1,
                       url =  yukon.url("/tools/trend/renderSetupPopup") + "?isMarker=" + isMarker + "&numberOfRows=" + numberOfPoints,
                       dialogDivJson = {
                            "data-title": $(".js-add-marker-title").val(),
                            "data-dialog": '',
                            "id": "js-add-marker-dialog",
                            "class": _trendSetupDialogClass,
                            "data-event": "yukon:trend:setup:addMarker",
                            "data-load-event": "yukon:trend:setup:markerPopupLoaded",
                            "data-help-text": $('.js-marker-help').val(),
                            "data-width": "500",
                            "data-url": url
                    };
                
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });
            
            $(document).on("click", ".js-save-trend", function () {
                yukon.ui.reindexInputs($("#js-point-setup-table"));
                var startIndex = Number($("#js-point-setup-table tbody tr").length);
                $("#js-marker-setup-table .js-row-data").each(function (index, element) {
                    var name = $(element).attr('name'),
                           newIndex = startIndex + Number(index);
                    $(element).attr('name', name.replace(/\[(\d+|\?)\]/, '[' + newIndex + ']'));
                });
                $("#js-trend-setup-form").submit();
            });

            yukon.ui.highlightErrorTabs();
            
            $(document).on("dialogclose", function(event) {
                if ($(event.target).hasClass(_trendSetupDialogClass)) {
                    var dialog = $(event.target);
                    dialog.dialog('destroy');
                    dialog.empty();
                    dialog.remove();
                }
            });
            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.tools.trend.setup.init();
});