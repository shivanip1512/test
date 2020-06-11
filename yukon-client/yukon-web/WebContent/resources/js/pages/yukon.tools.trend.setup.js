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
    
    _initColorPicker = function (dialog) {
        var colorArray = dialog.find(".js-color-item").map(function () {return $(this).val();}).get(),
               colorArrayLength = colorArray.length,
               firstRowOfColors = colorArray.splice(0, colorArrayLength/2);
        
        dialog.find(".js-color-picker").spectrum({
            showPaletteOnly: true,
            showPalette:true,
            hideAfterPaletteSelect:true,
            palette: [
                colorArray, firstRowOfColors
            ],
            preferredFormat: "name",
            move: function(color){
                debugger;
                dialog.find(".js-color-input").val(color.toName().toUpperCase());
            }
        });
    },
    
    _initPointSetupPopup = function (dialog) {
        yukon.ui.initDateTimePickers().ancestorInit(dialog);
        _initColorPicker(dialog);
    },
    
    _addRowToPointSetupTable = function (data) {
        var clonnedRow = $(".js-point-setup-template-table").find(".js-template-row").clone();
        clonnedRow.removeClass("js-template-row");
        clonnedRow.find(".js-point-name").html(yukon.escapeXml(data.pointName));
        clonnedRow.find(".js-device-name span").html(yukon.escapeXml(data.deviceName));
        clonnedRow.find(".js-label").html(yukon.escapeXml(data.trendSeries.label));
        clonnedRow.find(".js-color span").text(data.color);
        clonnedRow.find(".js-color div.small-rectangle").css("background-color", data.color);
        clonnedRow.find(".js-axis").html(data.axis);
        if (data.hasOwnProperty("dateStr")) {
            clonnedRow.find(".js-type").html(data.dateStr);
        } else {
            clonnedRow.find(".js-type").html(data.graphType);
        }
        clonnedRow.find(".js-style").html(data.style);
        clonnedRow.find(".js-multiplier").html(data.trendSeries.multiplier);
        clonnedRow.find(".js-row-data").val(JSON.stringify(data.trendSeries));
        $('#js-point-setup-table').append(clonnedRow);
        yukon.ui.reindexInputs(clonnedRow.closest('table'));
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
            
            $(document).on("yukon:trend:setup:addPoint", function (event) {
                $(event.target).find(".js-point-setup-form").ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $("#js-add-point-dialog").dialog('close');
                        _addRowToPointSetupTable(data);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#js-add-point-dialog').html(xhr.responseText);
                        _initPointSetupPopup($('#js-add-point-dialog'));
                    }
                });
            });
            
            $(document).on("change", ".js-graph-type", function () {
                $(".js-date-picker-row").toggleClass("dn", !($(this).val() === $(".js-date-type-enum-value").val()));
            });
            
            $(document).on("yukon:trend:setup:pointPopupLoaded", function (event) {
                _initPointSetupPopup($(event.target));
            });
            
            $(document).on("click", ".js-remove-point", function (event) {
                $(this).closest("tr").remove();
                yukon.ui.reindexInputs("#js-point-setup-table");
            });
            
            $(document).on("click", ".js-edit-point", function (event) {
                var row = $(this).closest("tr"),
                       pointSetupData = row.find(".js-row-data").val(),
                       dailogTitle = yg.text.edit + " " + $(this).closest("tr").find(".js-point-name").text(),
                       form = $("<form/>"),
                       hiddenInput = $("<input/>").attr({
                           type: "hidden",
                           name: "trendSeries"
                       }),
                       dialogPopup = $("<div class='js-edit-point-dialog'/>");
                hiddenInput.val(pointSetupData);
                form.append(hiddenInput);
                
                dialogPopup.load(yukon.url("/tools/trend/renderEditPointPopup?" + form.serialize()), function () {
                    dialogPopup.dialog({
                        title: dailogTitle,
                        width: 'auto',
                        height: 'auto',
                        classes: {
                            "ui-dialog": 'ov'
                        },
                        modal: true,
                        open: function () {
                            _initPointSetupPopup($(".js-edit-point-dialog"));
                        },
                        close: function () {
                            $(".js-edit-point-dialog").empty();
                            $(".js-edit-point-dialog").dialog('destroy').remove();
                        },
                        buttons: yukon.ui.buttons({event: 'yukon:trend:setup:updatePoint', target: row})
                    });
                });
            });
            
            $(document).on("yukon:trend:setup:updatePoint", function(event) {
                $(".js-edit-point-dialog").find(".js-point-setup-form").ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $(".js-edit-point-dialog").dialog('close');
                        $(event.target).remove();
                        _addRowToPointSetupTable(data);
                    },
                    error: function(xhr, status, error, $form) {
                        $(".js-edit-point-dialog").html(xhr.responseText);
                        _initPointSetupPopup($(".js-edit-point-dialog"));
                    }
                });
            });
            
            yukon.ui.highlightErrorTabs();
            
            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.tools.trend.setup.init();
});