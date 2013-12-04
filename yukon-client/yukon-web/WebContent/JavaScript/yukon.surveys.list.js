var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Surveys.List');
Yukon.Surveys.List = (function () {
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
    Yukon.Surveys.List.init();
});