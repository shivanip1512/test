/**
 * Singleton that manages the password updating
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ui');
yukon.namespace('yukon.ui.passwords');

yukon.ui.passwords = (function () {
    var mod;

    mod = {
        init: function (args) {
            $.extend(this, args);
            $('.f-check_password').keyup(function(event){
                $('button.unlock').attr('disabled', 'true');
                $('#loginBackingBean').ajaxSubmit({
                    url: yukon.url('/login/checkPassword'),
                    type: 'POST',
                    dataType: 'json',
                    success: function(data){
                        mod.indicatePassFail(data);
                        return false;
                    },
                    error: function(err){
                        mod.indicatePassFail($.parseJSON(err.responseText));
                        return false;
                    }
                });
                return false;
            });

            $('input:password:not(.f-current)').keyup(function(e){
                var confirm = $('input.confirm[type=password]').val(),
                    password = $('input.new[type=password]').val(),
                    meetsRequirements = $('.description ul:first > li > .icon-cross');
                $('.no_match').toggleClass('vh', confirm === password);
                if (meetsRequirements.length == 0 && 
                    confirm === password && 
                    password.length >= mod.minLength) {
                    $('button.unlock').prop('disabled', false);
                } else {
                    $('button.unlock').prop('disabled', true);
                }
            });
        },

        indicatePassFail: function(data) {
            $('.password_manager .icon-accept, .password_manager .icon-cross, .password_manager .icon-blank').removeClass('icon-accept icon-cross icon-blank');
            var errors = ['policy_errors', 'rule_errors'],
                validations = ['policy_validations', 'rule_validations'],
                i,
                j;
            for (i=0; i < validations.length; i++) {
                if (data[validations[i]]) {
                    for (j=0; j <data[validations[i]].length; j++) {
                        $('.' + data[validations[i]][j] + ' .icon').removeClass('icon-cross').addClass('icon-accept');
                    }
                }
            }

            for (i = 0; i < errors.length; i++) {
                if (data[errors[i]]) {
                    for (j = 0; j < data[errors[i]].length; j++) {
                        $('.' + data[errors[i]][j] + ' .icon').removeClass('icon-accept').addClass('icon-cross');
                    }
                }
            }
        },

        minLength : 8
    };
    return mod;
})();