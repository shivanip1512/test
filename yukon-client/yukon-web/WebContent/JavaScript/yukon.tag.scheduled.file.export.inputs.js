yukon.namespace('yukon.tag.scheduledFileExportInputs');

yukon.tag.scheduledFileExportInputs = (function () {
    var 
    _toggleField = function(checkBoxId, changeItemId) {
        if ($(checkBoxId).is(":checked")) {
            $(changeItemId).prop("disabled", false).closest("tr").show(250);
        } else {
            $(changeItemId).prop("disabled", true).closest("tr").hide();
        }
    },

    _toggleTimestampPatternField = function() {
        _toggleField("#appendDateToFileName", "#timestampPatternField");
    },

    _toggleFileExtensionField = function() {
        _toggleField("#overrideFileExtension", "#exportFileExtension");
    },

    _toggleExportPathField = function() {
        _toggleField("#includeExportCopy", "#exportPath");
    },

    _nameChanged = function() {
        if ($("#sameAsSchedName").is(":checked")) {
            $("#exportFileName").val($("#scheduleName").val());
        }
    },

    _lastDisplayName = false,
    _sameAsNameClicked = function() {
         if ($("#sameAsSchedName").is(":checked")) {
             _lastDisplayName = $("#exportFileName").val();
             $("#exportFileName").val($("#scheduleName").val());
             $("#exportFileName").prop("disabled", true);
         } else {
            if (_lastDisplayName) {
                 $("#exportFileName").val(_lastDisplayName);
             }
             $("#exportFileName").prop("disabled", false);
         }
     },

     _intializeAllFields = function () {
         _toggleTimestampPatternField();
         _toggleFileExtensionField();
         _toggleExportPathField();
         _sameAsNameClicked();
     },

    mod = {
        initializeFields: function() {
            _intializeAllFields();
        },

         init: function() {
             $(document).on('click', "#appendDateToFileName", _toggleTimestampPatternField);
             $(document).on('click', "#overrideFileExtension", _toggleFileExtensionField);
             $(document).on('click', "#includeExportCopy", _toggleExportPathField);
             $(document).on('keyup', "#scheduleName", _nameChanged);
             $(document).on('change', "#scheduleName", _nameChanged);
             $(document).on('click', "#sameAsSchedName", _sameAsNameClicked);
             
             _intializeAllFields();
             }
         };

    return mod;
}());

$(function () {
    yukon.tag.scheduledFileExportInputs.init();
});