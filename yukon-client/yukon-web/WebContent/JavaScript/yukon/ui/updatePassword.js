/**
 * Singleton that manages the password updating
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});

yukon.ui = (function (uiMod) {
    return uiMod;
})(yukon.ui || {});

yukon.ui.passwordManager = (function () {
    var passwordManagerMod;

    passwordManagerMod = {
        init: function (args) {
            jQuery.extend(this, args);
            jQuery(".f-check_password").keyup(function(event){
                jQuery("button.unlock").attr("disabled", "true");
                jQuery("#loginBackingBean").ajaxSubmit({
                    url: '/login/checkPassword',
                    type: "POST",
                    dataType: 'json',
                    success: function(data){
                        passwordManagerMod.indicatePassFail(data);
                        return false;
                    },
                    error: function(err){
                        passwordManagerMod.indicatePassFail(jQuery.parseJSON(err.responseText));
                        return false;
                    }
                });
                return false;
            });

            jQuery("input:password:not(.f-current)").keyup(function(e){
                var confirm = jQuery("input.confirm[type=password]").val(),
                    password = jQuery("input.new[type=password]").val(),
                    meetsRequirements = jQuery(".description ul:first > li > .icon-cross");
                jQuery(".no_match").toggleClass("vh", confirm === password);
                if (meetsRequirements.length == 0 && 
                    confirm === password && 
                    password.length >= passwordManagerMod.minLength) {
                    jQuery("button.unlock").removeAttr("disabled");
                } else {
                    jQuery("button.unlock").attr("disabled", "true");
                }
            });
        },

        indicatePassFail: function(data) {
            jQuery(".password_manager .icon-accept, .password_manager .icon-cross, .password_manager .icon-blank").removeClass("icon-accept").removeClass("icon-cross").removeClass("icon-blank");
            var errors = ['policy_errors', 'rule_errors'],
                validations = ['policy_validations', 'rule_validations'],
                i,
                j;
            for (i=0; i < validations.length; i++) {
                if (data[validations[i]]) {
                    for (j=0; j <data[validations[i]].length; j++) {
                        jQuery("." + data[validations[i]][j] + " .icon").removeClass("icon-cross").addClass("icon-accept");
                    }
                }
            }

            for (i = 0; i < errors.length; i++) {
                if (data[errors[i]]) {
                    for (j = 0; j < data[errors[i]].length; j++) {
                        jQuery("." + data[errors[i]][j] + " .icon")
                                .removeClass("icon-accept").addClass(
                                        "icon-cross");
                    }
                }
            }
        },

        minLength : 8
    };
    return passwordManagerMod;
})();