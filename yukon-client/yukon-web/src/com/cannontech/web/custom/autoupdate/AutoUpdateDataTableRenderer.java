/**
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cannontech.web.custom.autoupdate;

import java.io.IOException;

import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.ext.HtmlTableRenderer;
import org.apache.myfaces.util.StateUtils;

import com.cannontech.web.custom.ajax.AjaxRenderer;

/**
 * @author J&ouml;rg Artaker
 * @author Thomas Huber
 * @version $Revision$ $Date$
 *          <p/>
 *          $Log$
 *          Revision 1.1  2006/05/04 18:15:08  tmack
 *          Adding a couple customer components copied directly out of the MyFaces Sandbox.
 *
 */
public class AutoUpdateDataTableRenderer extends HtmlTableRenderer implements AjaxRenderer {

    /**
     * Encodes any stand-alone javascript functions that are needed.  Uses either the extension filter, or a
     * user-supplied location for the javascript files.
     *
     * @param context FacesContext
     * @param component UIComponent
     * @throws java.io.IOException
     */
    private void encodeJavascript(FacesContext context, UIComponent component) throws IOException {
        // for the time being, assume that prototype.js has been included by the standard page framework
    }

    /**
     * @param context FacesContext
     * @param component UIComponent
     * @throws java.io.IOException
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(context, component, AutoUpdateDataTable.class);
        AutoUpdateDataTable autoUpdateDataTable = (AutoUpdateDataTable) component;

        this.encodeJavascript(context, component);
        super.encodeEnd(context, component);

        ResponseWriter out = context.getResponseWriter();

        String viewId = context.getViewRoot().getViewId();
        ViewHandler viewHandler = context.getApplication().getViewHandler();
        String actionURL = viewHandler.getActionURL(context, viewId);

        out.startElement(HTML.SCRIPT_ELEM, component);
        out.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);

        StringBuffer script = new StringBuffer();
        script.append("\n");
        script.append("new Ajax.PeriodicalUpdater('");
        script.append(component.getClientId(context)).append(":tbody_element");
        script.append("','");
        script.append(context.getExternalContext().encodeActionURL(actionURL
            + "?affectedAjaxComponent=" + component.getClientId(context)));
        script.append("', {\n frequency: ").append(autoUpdateDataTable.getFrequency());
        script.append(",\n decay: 1.007");
        
        if (context.getApplication().getStateManager().isSavingStateInClient(context)) {
            script
                .append(" , parameters: '&jsf_tree_64='+encodeURIComponent(document.getElementById('jsf_tree_64').value)+'&jsf_state_64='+encodeURIComponent(document.getElementById('jsf_state_64').value)+'&jsf_viewid='+encodeURIComponent(document.getElementById('jsf_viewid').value)");
        }
        script.append("    })");
        script.append("\n");

        out.writeText(script.toString(), null);

        out.endElement(HTML.SCRIPT_ELEM);
    }

    /**
     * @param facesContext FacesContext
     * @param component UIComponent
     */
    public void decode(FacesContext facesContext, UIComponent component) {
        super.decode(facesContext, component);
    }

    /**
     * @param context FacesContext
     * @param component UIComponent
     * @throws java.io.IOException
     */
    public void encodeAjax(FacesContext context, UIComponent component) throws IOException {
        encodeInnerHtml(context, component);
        if (context.getApplication().getStateManager().isSavingStateInClient(context)) {
            StateManager stateManager = context.getApplication().getStateManager();
            StateManager.SerializedView serializedView = stateManager.saveSerializedView(context);
            Object compStates = serializedView.getState();

            StringBuffer buf = new StringBuffer();

            buf.append("jsf_state=");
            buf.append(StateUtils.encode64(compStates));
            buf.append("jsf_state_end");

            context.getResponseWriter().write(buf.toString());
        }
    }
}
