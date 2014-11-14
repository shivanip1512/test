<%@ page contentType="text/javascript" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<!-- Current as of version 4.2.0 -->

<cti:msgScope paths="jqGrid" >
;(function($){
/**
 * jqGrid English Translation
 * Tony Tomov tony@trirand.com
 * http://trirand.com/blog/ 
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
**/
$.jgrid = {
	defaults : {
		recordtext: "<cti:msg key="yukon.web.jqGrid.defaults.recordText"/>",
		emptyrecords: "<cti:msg key="yukon.web.jqGrid.defaults.emptyRecords"/>",
		loadtext: "<cti:msg key="yukon.web.jqGrid.defaults.loadText"/>",
		pgtext : "<cti:msg key="yukon.web.jqGrid.defaults.pgText"/>"
	},
	search : {
		caption: "<cti:msg key="yukon.web.jqGrid.search.caption"/>",
		Find: "<cti:msg key="yukon.web.jqGrid.search.find"/>",
		Reset: "<cti:msg key="yukon.web.jqGrid.search.reset"/>",
		odata : <cti:msg key="yukon.web.jqGrid.search.odata"/>,
		groupOps: <cti:msg key="yukon.web.jqGrid.search.groupOps"/>,
		matchText: "<cti:msg key="yukon.web.jqGrid.search.matchText"/>",
		rulesText: "<cti:msg key="yukon.web.jqGrid.search.rulesText"/>"
	},
	edit : {
		addCaption: "<cti:msg key="yukon.web.jqGrid.edit.addCaption"/>",
		editCaption: "<cti:msg key="yukon.web.jqGrid.edit.editCaption"/>",
		bSubmit: "<cti:msg key="yukon.web.jqGrid.edit.bSubmit"/>",
		bCancel: "<cti:msg key="yukon.web.jqGrid.edit.bCancel"/>",
		bClose: "<cti:msg key="yukon.web.jqGrid.edit.bClose"/>",
		saveData: "<cti:msg key="yukon.web.jqGrid.edit.saveData"/>",
		bYes : "<cti:msg key="yukon.web.jqGrid.edit.bYes"/>",
		bNo : "<cti:msg key="yukon.web.jqGrid.edit.bNo"/>",
		bExit : "<cti:msg key="yukon.web.jqGrid.edit.bExit"/>",
		msg: {
			required:"<cti:msg key="yukon.web.jqGrid.edit.msg.required"/>",
			number:"<cti:msg key="yukon.web.jqGrid.edit.msg.number"/>",
			minValue:"<cti:msg key="yukon.web.jqGrid.edit.msg.minValue"/>",
			maxValue:"<cti:msg key="yukon.web.jqGrid.edit.msg.maxValue"/>",
			email: "<cti:msg key="yukon.web.jqGrid.edit.msg.email"/>",
			integer: "<cti:msg key="yukon.web.jqGrid.edit.msg.integer"/>",
			date: "<cti:msg key="yukon.web.jqGrid.edit.msg.date"/>",
			url: "<cti:msg key="yukon.web.jqGrid.edit.msg.url"/>",
			nodefined : "<cti:msg key="yukon.web.jqGrid.edit.msg.nodefined"/>",
			novalue : "<cti:msg key="yukon.web.jqGrid.edit.msg.novalue"/>",
			customarray : "<cti:msg key="yukon.web.jqGrid.edit.msg.customarray"/>",
			customfcheck : "<cti:msg key="yukon.web.jqGrid.edit.msg.customcheck"/>"
			
		}
	},
	view : {
		caption: "<cti:msg key="yukon.web.jqGrid.view.caption"/>",
		bClose: "<cti:msg key="yukon.web.jqGrid.view.bClose"/>"
	},
	del : {
		caption: "<cti:msg key="yukon.web.jqGrid.del.caption"/>",
		msg: "<cti:msg key="yukon.web.jqGrid.del.msg"/>",
		bSubmit: "<cti:msg key="yukon.web.jqGrid.del.bSubmit"/>",
		bCancel: "<cti:msg key="yukon.web.jqGrid.del.bCancel"/>"
	},
	nav : {
		edittext: "<cti:msg key="yukon.web.jqGrid.nav.editText"/>",
		edittitle: "<cti:msg key="yukon.web.jqGrid.nav.editTitle"/>",
		addtext:"<cti:msg key="yukon.web.jqGrid.nav.addText"/>",
		addtitle: "<cti:msg key="yukon.web.jqGrid.nav.addTitle"/>",
		deltext: "<cti:msg key="yukon.web.jqGrid.nav.deleteText"/>",
		deltitle: "<cti:msg key="yukon.web.jqGrid.nav.deleteTitle"/>",
		searchtext: "<cti:msg key="yukon.web.jqGrid.nav.searchText"/>",
		searchtitle: "<cti:msg key="yukon.web.jqGrid.nav.searchTitle"/>",
		refreshtext: "<cti:msg key="yukon.web.jqGrid.nav.refreshText"/>",
		refreshtitle: "<cti:msg key="yukon.web.jqGrid.nav.refreshTitle"/>",
		alertcap: "<cti:msg key="yukon.web.jqGrid.nav.alertCaption"/>",
		alerttext: "<cti:msg key="yukon.web.jqGrid.nav.alertText"/>",
		viewtext: "<cti:msg key="yukon.web.jqGrid.nav.viewText"/>",
		viewtitle: "<cti:msg key="yukon.web.jqGrid.nav.viewTitle"/>"
	},
	col : {
		caption: "<cti:msg key="yukon.web.jqGrid.col.caption"/>",
		bSubmit: "<cti:msg key="yukon.web.jqGrid.col.bSubmit"/>",
		bCancel: "<cti:msg key="yukon.web.jqGrid.col.bCancel"/>"
	},
	errors : {
		errcap : "<cti:msg key="yukon.web.jqGrid.errors.errorCaption"/>",
		nourl : "<cti:msg key="yukon.web.jqGrid.errors.noUrl"/>",
		norecords: "<cti:msg key="yukon.web.jqGrid.errors.noRecords"/>",
		model : "<cti:msg key="yukon.web.jqGrid.errors.model"/>"
	},
	formatter : {
		integer : {thousandsSeparator: "<cti:msg key="yukon.web.jqGrid.formatter.integer.thousandsSeparator"/>", 
					defaultValue: '<cti:msg key="yukon.web.jqGrid.formatter.integer.defaultValue"/>'},
		number : {decimalSeparator:"<cti:msg key="yukon.web.jqGrid.formatter.number.decimalSeparator"/>", 
					thousandsSeparator: "<cti:msg key="yukon.web.jqGrid.formatter.number.thousandsSeparator"/>", 
					decimalPlaces: <cti:msg key="yukon.web.jqGrid.formatter.number.decimalPlaces"/>, 
					defaultValue: '<cti:msg key="yukon.web.jqGrid.formatter.number.defaultValue"/>'},
		currency : {decimalSeparator:"<cti:msg key="yukon.web.jqGrid.formatter.currency.decimalSeparator"/>", 
					thousandsSeparator: "<cti:msg key="yukon.web.jqGrid.formatter.currency.thousandsSeparator"/>", 
					decimalPlaces: <cti:msg key="yukon.web.jqGrid.formatter.currency.decimalPlaces"/>, 
					prefix: "<cti:msg key="yukon.web.jqGrid.formatter.currency.prefix"/>", 
					suffix:"<cti:msg key="yukon.web.jqGrid.formatter.currency.suffix"/>", 
					defaultValue: '<cti:msg key="yukon.web.jqGrid.formatter.currency.defaultValue"/>'},
		date : {
			dayNames: <cti:msg key="yukon.web.jqGrid.formatter.date.dayNames"/>,
			monthNames: <cti:msg key="yukon.web.jqGrid.formatter.date.monthNames"/>,
			AmPm : <cti:msg key="yukon.web.jqGrid.formatter.date.amPm"/>,
			S: function (j) {return j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th'},
			srcformat: '<cti:msg key="yukon.web.jqGrid.formatter.date.sourceFormat"/>',
			newformat: '<cti:msg key="yukon.web.jqGrid.formatter.date.newFormat"/>',
			masks : {
				ISO8601Long:"<cti:msg key="yukon.web.jqGrid.formatter.date.masks.ISO8601Long"/>",
				ISO8601Short:"<cti:msg key="yukon.web.jqGrid.formatter.date.masks.ISO8601Short"/>",
				ShortDate: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.shortDate"/>",
				LongDate: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.longDate"/>",
				FullDateTime: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.fullDateTime"/>",
				MonthDay: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.monthDay"/>",
				ShortTime: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.shortTime"/>",
				LongTime: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.longTime"/>",
				SortableDateTime: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.sortableDateTime"/>",
				UniversalSortableDateTime: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.universalSortableDateTime"/>",
				YearMonth: "<cti:msg key="yukon.web.jqGrid.formatter.date.masks.yearMonth"/>"
			},
			reformatAfterEdit : false
		},
		baseLinkUrl: '',
		showAction: '',
		target: '',
		checkbox : {disabled:true},
		idName : 'id'
	}
};
})(jQuery);
</cti:msgScope>