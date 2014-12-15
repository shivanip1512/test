yukon.namespace('yukon.da.voltagePointsEdit');

/**
 *
 * @module yukon.da.voltagePointsEdit
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.voltagePointsEdit = (function () {

    var mod = {

            init : function () {
                $(".js-voltage-points input:checkbox").change(function() {
                    $(this).closest("tr").find("input:text").toggleDisabled();
                });

                /**
                 * If the user checked the Override Strategy checkbox.. then entered invalid limit values,
                 * then hit save. Since the Override Strategy value is not yet in the database the resulting
                 * error page would show this checkbox as unchecked. This code iterates through these and 
                 * "checks" them for the user.
                 */
                $("input.lowerLimit.error, input.upperLimit.error").each(function() {
                    this.disabled = false;
                    $(this).closest("tr").find("input:checkbox").attr("checked", true);
                });
            }
        };

    return mod;
}());

$(function () { yukon.da.voltagePointsEdit.init(); });