if(typeof(Yukon) === "undefined")
	Yukon = {};
if(typeof(Yukon.ui) === "undefined")
	Yukon.ui = {};

Yukon.ui.passwordManager = {
	minLength: 8,
	URL: "",
	
	init: function(args){
		jQuery.extend(this, args);
		jQuery(".f_check_password").keyup(function(event){

			jQuery("button.unlock").attr("disabled", "true");
			jQuery("#loginBackingBean").ajaxSubmit({
				url: '/login/checkPassword',
                type: "POST",
				dataType: 'json',
				success: function(data){
					Yukon.ui.passwordManager.indicatePassFail(data);
					return false;
				},
				error: function(err){
					Yukon.ui.passwordManager.indicatePassFail(jQuery.parseJSON(err.responseText));
					return false;
				}
			});
			return false;
		});
		
		jQuery("input:password:not(.f_current)").keyup(function(e){
			var confirm = jQuery("input.confirm[type=password]").val();
			var password = jQuery("input.new[type=password]").val();
			var meetsRequirements = jQuery(".description ul:first > li > .icon-cross");
			jQuery(".no_match").toggleClass("vh", confirm === password);
			if(meetsRequirements.length == 0 && 
				confirm === password && 
				password.length >= Yukon.ui.passwordManager.minLength){
				jQuery("button.unlock").removeAttr("disabled");
			}else{
				jQuery("button.unlock").attr("disabled", "true");
			}
		});
	},
	
	indicatePassFail: function(data){
        jQuery(".password_manager .icon-accept, .password_manager .icon-cross, .password_manager .icon-blank").removeClass("icon-accept").removeClass("icon-cross").removeClass("icon-blank");
		var errors = ['policy_errors', 'rule_errors'];
		var validations = ['policy_validations', 'rule_validations'];
		for(var i=0; i<validations.length; i++){
			if(data[validations[i]]){
				for(var j=0; j<data[validations[i]].length; j++){
					jQuery("." + data[validations[i]][j] + " .icon").removeClass("icon-cross").addClass("icon-accept");
				}
			}
		}
		
		for(var i=0; i<errors.length; i++){
            if(data[errors[i]]){
                for(var j=0; j<data[errors[i]].length; j++){
                    jQuery("." + data[errors[i]][j] + " .icon").removeClass("icon-accept").addClass("icon-cross");
                }
            }
        }
	}
};