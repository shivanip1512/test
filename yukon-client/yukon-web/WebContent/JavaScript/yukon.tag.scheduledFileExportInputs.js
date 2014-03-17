yukon.namespace('yukon.tag.scheduledFileExportInputs');

yukon.tag.scheduledFileExportInputs = (function () {
    var 
    _toggleField = function(checkBoxId, changeItemId) {
        if (jQuery(checkBoxId).is(":checked")) {
            jQuery(changeItemId).removeAttr("disabled").closest("tr").show(250);
        } else {
            jQuery(changeItemId).attr("disabled","disabled").closest("tr").hide();
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
        if (jQuery("#sameAsSchedName").is(":checked")) {
            jQuery("#exportFileName").val(jQuery("#scheduleName").val());
        }
    },

    _lastDisplayName = false,
    _sameAsNameClicked = function() {
         if (jQuery("#sameAsSchedName").is(":checked")) {
             _lastDisplayName = jQuery("#exportFileName").val();
             jQuery("#exportFileName").val(jQuery("#scheduleName").val());
             jQuery("#exportFileName").attr("disabled","disabled");
         } else {
            if (_lastDisplayName) {
                 jQuery("#exportFileName").val(_lastDisplayName);
             }
             jQuery("#exportFileName").removeAttr("disabled");
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
             jQuery(document).on('click', "#appendDateToFileName", _toggleTimestampPatternField);
             jQuery(document).on('click', "#overrideFileExtension", _toggleFileExtensionField);
             jQuery(document).on('click', "#includeExportCopy", _toggleExportPathField);
             jQuery(document).on('keyup', "#scheduleName", _nameChanged);
             jQuery(document).on('change', "#scheduleName", _nameChanged);
             jQuery(document).on('click', "#sameAsSchedName", _sameAsNameClicked);
             
             _intializeAllFields();
             }
         };

    return mod;
}());

jQuery(function () {
    yukon.tag.scheduledFileExportInputs.init();
});