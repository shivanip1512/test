<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<div align="center">
    <table>
        <tr>
            <td colspan="2">
                <table width="100%">
                    <tr>
                        <td width="10%"></td>
                        <td
                            style="text-align: center; font-size: 25; color: rgb(192, 192, 192);"
                            width="80%">
                            Legend
                        </td>
                        <td style="text-align: right;" width="10%">
                            <a
                                style="font-size: 25; color: rgb(192, 192, 192);"
                                href="javascript:void(0);"
                                onclick="closePopupWindow();">x</a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <table>
                    <tr>
                        <td valign="top">
                            <table width="100%" border="1">
                                <tr>
                                    <td colspan="2"
                                        style="border-style: solid; border-width: 3px; font-size: 20; text-align: center; color: rgb(192, 192, 192);">
                                        Commands
                                    </td>
                                </tr>
                                <tr
                                    style="color: rgb(192, 192, 192); font-size: 15;">
                                    <th>
                                        Image
                                    </th>
                                    <th>
                                        Description
                                    </th>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(159, 187, 172); font-size: 15;">
                                        T:D::
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Disabled
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(159, 187, 172); font-size: 15;">
                                        T::V:
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Disabled OVUV
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(159, 187, 172); font-size: 15;">
                                        T:::F
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Fixed
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(159, 187, 172); font-size: 15;">
                                        T:::S
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Standalone
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(255, 175, 175); font-size: 15;">
                                        Feeder
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Name of Feeder
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(115, 79, 182); font-size: 15;">
                                        CapBank
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Name of CapBank
                                    </td>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(255, 165, 0); font-size: 15;">
                                        CapBank
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Temp Moved CapBank
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2"
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        <div>
                                            *** All command images can be clicked to send commands.
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            <table width="100%" border="1">
                                <tr>
                                    <td colspan="2"
                                        style="border-style: solid; border-width: 3px; font-size: 20; text-align: center; color: rgb(192, 192, 192);">
                                        General Information
                                    </td>
                                </tr>
                                <tr
                                    style="color: rgb(192, 192, 192); font-size: 15;">
                                    <th>
                                        Image
                                    </th>
                                    <th>
                                        Description
                                    </th>
                                </tr>
                                <tr>
                                    <td
                                        style="color: rgb(255, 0, 0); font-size: 12;">
                                        (*)
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Abnormal quality
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/X.gif'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Undefined State
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/regenerate.gif'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Regenerate
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/magnifier.gif'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Two way CBC reported point
                                        values
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/question.GIF'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        CapBank Additional Information
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/edit.gif'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Link to editor
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <img
                                            src="<cti:url value='/oneline/arrow.gif'/>" />
                                    </td>
                                    <td
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        Get me out of here. Exit
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td valign="top">
                            <table width="100%" border="1">
                                <tr>
                                    <td colspan="2"
                                        style="border-style: solid; border-width: 3px; font-size: 20; text-align: center; color: rgb(192, 192, 192);">
                                        CapBank States
                                    </td>
                                </tr>
                                <tr
                                    style="color: rgb(192, 192, 192); font-size: 15;">
                                    <th>
                                        Image
                                    </th>
                                    <th>
                                        Description
                                    </th>
                                </tr>
                                <c:forEach var="state"
                                    items="${capBankStateList}">
                                    <tr>
                                        <td>
                                            <img
                                                src="<cti:url value='/oneline/${imageNameMap[state.imageID]}'/>" />
                                        </td>
                                        <td
                                            style="color: rgb(192, 192, 192); font-size: 12;">
                                            ${state.stateText}
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td colspan="2"
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        <div>
                                            *** All state images can be clicked to change state.
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td valign="top">
                            <table width="100%" border="1">
                                <tr>
                                    <td colspan="2"
                                        style="border-style: solid; border-width: 3px; font-size: 20; text-align: center; color: rgb(192, 192, 192);">
                                        SubBus States
                                    </td>
                                </tr>
                                <tr
                                    style="color: rgb(192, 192, 192); font-size: 15;">
                                    <th>
                                        Image
                                    </th>
                                    <th>
                                        Description
                                    </th>
                                </tr>
                                <c:forEach var="state"
                                    items="${onelineStateList}">
                                    <tr>
                                        <td>
                                            <img
                                                src="<cti:url value='/oneline/${imageNameMap[state.imageID]}'/>" />
                                        </td>
                                        <td
                                            style="color: rgb(192, 192, 192); font-size: 12;">
                                            ${state.stateText}
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td colspan="2"
                                        style="color: rgb(192, 192, 192); font-size: 12;">
                                        <div>
                                            *** All state images can be clicked to change state.
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>
