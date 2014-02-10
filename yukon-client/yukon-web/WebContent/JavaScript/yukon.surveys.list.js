var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});
yukon.namespace('yukon.Surveys.List');
yukon.Surveys.List = (function () {
    mod = {
        init: function () {
            jQuery('#addSurveyBtn').click(function () {
                jQuery('#ajaxDialog').load('editDetails', function () {
                    jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
                });
            });

            jQuery(document).on('yukonDetailsUpdated', function (event, newUrl) {
                jQuery('#ajaxDialog').dialog('close');
                window.location = newUrl;
            });
        }
    };
    return mod;
}());

jQuery(function () {
    yukon.Surveys.List.init();
});