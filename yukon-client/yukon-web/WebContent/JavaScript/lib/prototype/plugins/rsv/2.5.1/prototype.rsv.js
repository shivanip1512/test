var RSV=Class.create();RSV.prototype={options:{},returnHash:[],initialize:function(){var opts=Object.extend({formID:"",rules:[],displayType:"alert-all",errorFieldClass:null,errorTextIntro:"Please fix the following error(s) and resubmit:",errorJSItemBullet:"* ",errorHTMLItemBullet:"&bull; ",errorTargetElementId:"rsvErrors",onCompleteHandler:null,customErrorHandler:null},arguments[0]||{});if(!opts.formID)
{alert("The formID configuration setting is required.");return false;}
if($(opts.formID))
$(opts.formID).onsubmit=this.validate.bind(this);this.options=opts;},validate:function()
{var form=$(this.options.formID);var rules=this.options.rules;this.returnHash=[];for(var i=0;i<rules.length;i++)
{var row=rules[i].replace(/\\,/ig,"%%C%%");row=row.split(",");var satisfiesIfConditions=true;while(row[0].match("^if:"))
{var cond=row[0];cond=cond.replace("if:","");var comparison="equal";var parts=[];if(cond.search("!=")!=-1)
{parts=cond.split("!=");comparison="not_equal";}
else
parts=cond.split("=");var fieldToCheck=parts[0];var valueToCheck=parts[1];var fieldnameValue="";if(form[fieldToCheck].type==undefined)
{for(var j=0;j<form[fieldToCheck].length;j++)
{if(form[fieldToCheck][j].checked)
fieldnameValue=form[fieldToCheck][j].value;}}
else if(form[fieldToCheck].type=="checkbox")
{if(form[fieldToCheck].checked)
fieldnameValue=form[parts[0]].value;}
else
fieldnameValue=form[parts[0]].value;if(comparison=="equal"&&fieldnameValue!=valueToCheck)
{satisfiesIfConditions=false;break;}
else if(comparison=="not_equal"&&fieldnameValue==valueToCheck)
{satisfiesIfConditions=false;break;}
else
row.shift();}
if(!satisfiesIfConditions)
continue;var requirement=row[0];var fieldName=row[1];var fieldName2,fieldName3,errorMessage,lengthRequirements,date_flag;if(requirement!="function"&&form[fieldName]==undefined)
{alert("RSV Error: the field \""+fieldName+"\" doesn't exist! Please check your form and settings.");return false;}
if(requirement!="function"&&this.options.errorFieldClass)
{if(form[fieldName].type==undefined)
{for(var j=0;j<form[fieldName].length;j++)
$(form[fieldName][j]).removeClassName(this.options.errorFieldClass);}
else
$(form[fieldName]).removeClassName(this.options.errorFieldClass);}
if(row.length==6)
{fieldName2=row[2];fieldName3=row[3];date_flag=row[4];errorMessage=row[5];}
else if(row.length==5)
{fieldName2=row[2];fieldName3=row[3];errorMessage=row[4];}
else if(row.length==4)
{fieldName2=row[2];errorMessage=row[3];}
else
errorMessage=row[2];if(requirement.match("^length"))
{lengthRequirements=requirement;requirement="length";}
if(requirement.match("^range"))
{rangeRequirements=requirement;requirement="range";}
switch(requirement)
{case"required":if(form[fieldName].type==undefined)
{var oneIsChecked=false;for(var j=0;j<form[fieldName].length;j++)
{if(form[fieldName][j].checked)
oneIsChecked=true;}
if(!oneIsChecked)
{if(!this.processError(form[fieldName],errorMessage))
return false;}}
else if(form[fieldName].type=="select-multiple")
{var oneIsSelected=false;for(var k=0;k<form[fieldName].length;k++)
{if(form[fieldName][k].selected)
oneIsSelected=true;}
if(!oneIsSelected||form[fieldName].length==0)
{if(!this.processError(form[fieldName],errorMessage))
return false;}}
else if(form[fieldName].type=="checkbox")
{if(!form[fieldName].checked)
{if(!this.processError(form[fieldName],errorMessage))
return false;}}
else if(!form[fieldName].value)
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"digits_only":if(form[fieldName].value&&form[fieldName].value.match(/\D/))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"letters_only":if(form[fieldName].value&&form[fieldName].value.match(/[^a-zA-Z]/))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"is_alpha":if(form[fieldName].value&&form[fieldName].value.match(/\W/))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"custom_alpha":var conversion={"L":"[A-Z]","V":"[AEIOU]","l":"[a-z]","v":"[aeiou]","D":"[a-zA-Z]","F":"[aeiouAEIOU]","C":"[BCDFGHJKLMNPQRSTVWXYZ]","x":"[0-9]","c":"[bcdfghjklmnpqrstvwxyz]","X":"[1-9]","E":"[bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ]"};var reg_exp_str="";for(j=0;j<fieldName2.length;j++)
{if(conversion[fieldName2.charAt(j)])
reg_exp_str+=conversion[fieldName2.charAt(j)];else
reg_exp_str+=fieldName2.charAt(j);}
var reg_exp=new RegExp(reg_exp_str);if(form[fieldName].value&&reg_exp.exec(form[fieldName].value)==null)
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"reg_exp":var reg_exp_str=fieldName2.replace(/%%C%%/ig, ",");if(row.length==5)
var reg_exp=new RegExp(reg_exp_str,fieldName3);else
var reg_exp=new RegExp(reg_exp_str);if(form[fieldName].value&&reg_exp.exec(form[fieldName].value)==null)
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"length":comparison_rule="";rule_string="";if(lengthRequirements.match(/length=/))
{comparison_rule="equal";rule_string=lengthRequirements.replace("length=","");}
else if(lengthRequirements.match(/length>=/))
{comparison_rule="greater_than_or_equal";rule_string=lengthRequirements.replace("length>=","");}
else if(lengthRequirements.match(/length>/))
{comparison_rule="greater_than";rule_string=lengthRequirements.replace("length>","");}
else if(lengthRequirements.match(/length<=/))
{comparison_rule="less_than_or_equal";rule_string=lengthRequirements.replace("length<=","");}
else if(lengthRequirements.match(/length</))
{comparison_rule="less_than";rule_string=lengthRequirements.replace("length<","");}
switch(comparison_rule)
{case"greater_than_or_equal":if(!(form[fieldName].value.length>=parseInt(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"greater_than":if(!(form[fieldName].value.length>parseInt(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"less_than_or_equal":if(!(form[fieldName].value.length<=parseInt(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"less_than":if(!(form[fieldName].value.length<parseInt(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"equal":var range_or_exact_number=rule_string.match(/[^_]+/);var fieldCount=range_or_exact_number[0].split("-");if(fieldCount.length==2)
{if(form[fieldName].value.length<fieldCount[0]||form[fieldName].value.length>fieldCount[1])
{if(!this.processError(form[fieldName],errorMessage))
return false;}}
else
{if(form[fieldName].value.length!=fieldCount[0])
{if(!this.processError(form[fieldName],errorMessage))
return false;}}
break;}
break;case"valid_email":if(form[fieldName].value&&!this.isValidEmail(form[fieldName].value))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"valid_date":var isLaterDate=false;if(date_flag=="later_date")
isLaterDate=true;else if(date_flag=="any_date")
isLaterDate=false;if(!this.isValidDate(form[fieldName].value,form[fieldName2].value,form[fieldName3].value,isLaterDate))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"same_as":if(form[fieldName].value!=form[fieldName2].value)
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"range":comparison_rule="";rule_string="";if(rangeRequirements.match(/range=/))
{comparison_rule="equal";rule_string=rangeRequirements.replace("range=","");}
else if(rangeRequirements.match(/range>=/))
{comparison_rule="greater_than_or_equal";rule_string=rangeRequirements.replace("range>=","");}
else if(rangeRequirements.match(/range>/))
{comparison_rule="greater_than";rule_string=rangeRequirements.replace("range>","");}
else if(rangeRequirements.match(/range<=/))
{comparison_rule="less_than_or_equal";rule_string=rangeRequirements.replace("range<=","");}
else if(rangeRequirements.match(/range</))
{comparison_rule="less_than";rule_string=rangeRequirements.replace("range<","");}
switch(comparison_rule)
{case"greater_than_or_equal":if(!(form[fieldName].value>=Number(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"greater_than":if(!(form[fieldName].value>Number(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"less_than_or_equal":if(!(form[fieldName].value<=Number(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"less_than":if(!(form[fieldName].value<Number(rule_string)))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;case"equal":var rangeValues=rule_string.split("-");if((form[fieldName].value<Number(rangeValues[0]))||(form[fieldName].value>Number(rangeValues[1])))
{if(!this.processError(form[fieldName],errorMessage))
return false;}
break;}
break;case"function":custom_function=fieldName;eval("var result = "+custom_function+"()");if(result.constructor.toString().indexOf("Array")!=-1)
{for(var j=0;j<result.length;j++)
{if(!this.processError(result[j][0],result[j][1]))
return false;}}
break;default:alert("Unknown requirement flag in validateFields(): "+requirement);return false;}}
if(typeof this.options.customErrorHandler=='function')
{return this.options.customErrorHandler(form,this.returnHash);}
else if(this.options.displayType=="alert-all")
{var errorStr=this.options.errorTextIntro+"\n\n";for(var i=0;i<this.returnHash.length;i++)
{errorStr+=this.options.errorJSItemBullet+this.returnHash[i][1]+"\n";this.styleField(this.returnHash[i][0],i==0);}
if(this.returnHash.length>0)
{alert(errorStr);return false;}}
else if(this.options.displayType=="display-html")
{var success=this.displayHTMLErrors(form,this.returnHash);if(!success)
return false;}
if(typeof this.options.onCompleteHandler=='function')
return this.options.onCompleteHandler();else
return true;},processError:function(obj,message)
{message=message.replace(/%%C%%/ig,",");var continueProcessing=true;switch(this.options.displayType)
{case"alert-one":alert(message);this.styleField(obj,true);continueProcessing=false;break;case"alert-all":case"display-html":this.returnHash.push([obj,message]);break;}
return continueProcessing;},displayHTMLErrors:function(f,errorInfo)
{var errorHTML=this.options.errorTextIntro+"<br /><br />";for(var i=0;i<errorInfo.length;i++)
{errorHTML+=this.options.errorHTMLItemBullet+errorInfo[i][1]+"<br />";this.styleField(errorInfo[i][0],i==0);}
if(errorInfo.length>0)
{$(this.options.errorTargetElementId).style.display="block";$(this.options.errorTargetElementId).innerHTML=errorHTML;return false;}
return true;},styleField:function(field,focus)
{if(field.type==undefined)
{if(focus)
field[0].focus();for(var i=0;i<field.length;i++)
$(field[i]).addClassName(this.options.errorFieldClass);}
else
{if(this.options.errorFieldClass)
$(field).addClassName(this.options.errorFieldClass);if(focus)
field.focus();}},isValidEmail:function(str)
{var s=str.strip();var at="@";var dot=".";var lat=s.indexOf(at);var lstr=s.length;var ldot=s.indexOf(dot);if(s.indexOf(at)==-1||(s.indexOf(at)==-1||s.indexOf(at)==0||s.indexOf(at)==lstr)||(s.indexOf(dot)==-1||s.indexOf(dot)==0||s.indexOf(dot)==lstr)||(s.indexOf(at,(lat+1))!=-1)||(s.substring(lat-1,lat)==dot||s.substring(lat+1,lat+2)==dot)||(s.indexOf(dot,(lat+2))==-1)||(s.indexOf(" ")!=-1))
{return false;}
return true;},isValidDate:function(month,day,year,isLaterDate)
{var daysInMonth;if((year%4==0)&&((year%100!=0)||(year%400==0)))
daysInMonth=[31,29,31,30,31,30,31,31,30,31,30,31];else
daysInMonth=[31,28,31,30,31,30,31,31,30,31,30,31];if(!month||!day||!year)return false;if(1>month||month>12)return false;if(year<0)return false;if(1>day||day>daysInMonth[month-1])return false;if(isLaterDate)
{var today=new Date();var currMonth=today.getMonth()+1;var currDay=today.getDate();var currYear=today.getFullYear();if(String(currMonth).length==1)currMonth="0"+currMonth;if(String(currDay).length==1)currDay="0"+currDay;var currDate=String(currYear)+String(currMonth)+String(currDay);if(String(month).length==1)month="0"+month;if(String(day).length==1)day="0"+day;incomingDate=String(year)+String(month)+String(day);if(Number(currDate)>Number(incomingDate))
return false;}
return true;}}