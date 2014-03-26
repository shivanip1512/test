yukon.namespace('yukon.tag.scheduledFileExportInputs');

yukon.tag.scheduledFileExportInputs = (function () {
    var 
    _toggleField = function(checkBoxId, changeItemId) {
        if ($(checkBoxId).is(":checked")) {
            $(changeItemId).removeAttr("disabled").closest("tr").show(250);
        } else {
            $(changeItemId).attr("disabled","disabled").closest("tr").hide();
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
             $("#exportFileName").attr("disabled","disabled");
         } else {
            if (_lastDisplayName) {
                 $("#exportFileName").val(_lastDisplayName);
             }
             $("#exportFileName").removeAttr("disabled");
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