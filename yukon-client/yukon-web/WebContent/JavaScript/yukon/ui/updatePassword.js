if(typeof(Yukon) === "undefined")
	Yukon = {};
if(typeof(Yukon.ui) === "undefined")
	Yukon.ui = {}

Yukon.ui.passwordManager = {
	minLength: 8,
	URL: "",
	
	init: function(args){
		jQuery.extend(this, args);
		jQuery(".f_check_password").keyup(function(e){
			
			jQuery("button.unlock").attr("disabled", "true");
			jQuery("#loginBackingBean").ajaxSubmit({
				url: 'checkPassword',
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
		});
		
		jQuery("input:password").keyup(function(e){
			var confirm = jQuery("input.confirm[type=password]").val();
			var password = jQuery("input.new[type=password]").val();
			var meetsRequirements = jQuery(".description ul:first > li.fail");
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
		jQuery(".password_manager .fail, .password_manager .pass").removeClass("fail").removeClass("passs");
		var errors = ['policy_errors', 'rule_errors'];
		var validations = ['policy_validations', 'rule_validations'];
		
		for(var i=0; i<errors.length; i++){
			if(data[errors[i]]){
				for(var j=0; j<data[errors[i]].length; j++){
					jQuery("." + data[errors[i]][j]).addClass("fail");
				}
			}
		}
		
		for(var i=0; i<validations.length; i++){
			if(data[validations[i]]){
				for(var j=0; j<data[validations[i]].length; j++){
					jQuery("." + data[validations[i]][j]).addClass("pass");
				}
			}
		}
	}
};