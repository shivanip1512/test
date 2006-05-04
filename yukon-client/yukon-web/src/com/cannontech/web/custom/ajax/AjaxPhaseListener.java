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
package com.cannontech.web.custom.ajax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.application.jsp.JspStateManagerImpl;
import org.apache.myfaces.custom.buffer.HtmlBufferResponseWriterWrapper;
import org.apache.myfaces.renderkit.RendererUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Martin Marinschek
 * @version $Revision$ $Date$
 *          <p/>
 *          $Log$
 *          Revision 1.1  2006/05/04 18:15:08  tmack
 *          Adding a couple customer components copied directly out of the MyFaces Sandbox.
 *
 */
public class AjaxPhaseListener implements PhaseListener
{
    private static Log log=LogFactory.getLog(AjaxPhaseListener.class);

    public static Object getValueForComponent(FacesContext facesContext, UIComponent component)
    {
        String possibleClientId = component.getClientId(facesContext);

        if(facesContext.getExternalContext().getRequestParameterMap().containsKey(possibleClientId))
        {
            return facesContext.getExternalContext().getRequestParameterMap().get(possibleClientId);
        }
        else
        {
            possibleClientId = (String)facesContext.getExternalContext().getRequestParameterMap().get(
                            "affectedAjaxComponent");

            UIViewRoot root = facesContext.getViewRoot();

            UIComponent ajaxComponent =
                    root.findComponent(possibleClientId);

            if(ajaxComponent==component)
            {
                return facesContext.getExternalContext().getRequestParameterMap().get(possibleClientId);
            }
            else
            {
                log.error("No value found for this component : "+possibleClientId);
                return null;
            }
        }
    }

    public void afterPhase(PhaseEvent event)
    {
         FacesContext context = event.getFacesContext();

        if(context.getExternalContext().getRequestParameterMap().containsKey("affectedAjaxComponent"))
        {                                 
            UIViewRoot root = context.getViewRoot();

            UIComponent ajaxComponent = root.findComponent((String)context.getExternalContext().getRequestParameterMap().get("affectedAjaxComponent"));

            if(ajaxComponent instanceof AjaxComponent)
            {                

                try
                {
                    HtmlBufferResponseWriterWrapper wrapper = HtmlBufferResponseWriterWrapper.getInstance(null);
                    context.setResponseWriter(wrapper);

                    ((AjaxComponent) ajaxComponent).encodeAjax(context);

                    StringBuffer buf = new StringBuffer(wrapper.toString());

                    buf.insert(0,Integer.toHexString(buf.length())+"\r\n");
                    buf.append("\r\n"+0+"\r\n\r\n");

                    //todo: fix this to work in PortletRequest as well
                    if(context.getExternalContext().getResponse() instanceof HttpServletResponse)
                    {
                        ((HttpServletResponse)context.getExternalContext().getResponse()).addHeader(
                                "Transfer-Encoding", "chunked");
                        PrintWriter writer = ((HttpServletResponse) context.getExternalContext().getResponse()).getWriter();
                       // buf.delete(buf.indexOf("<"), buf.indexOf(">")+1);
                        writer.print(buf.toString());
                        //System.out.println("PhaseListener: buf = " + buf.toString());
                        writer.flush();
                        writer.close();
                    }
                }
                catch (IOException e)
                {
                    log.error("Exception while rendering ajax-response",e);
                }
            }
            else
            {


                log.error("Found component is no ajaxComponent : "+RendererUtils.getPathToComponent(ajaxComponent));
            }
            
            if (!context.getApplication().getStateManager().isSavingStateInClient(context)){
                ((JspStateManagerImpl) context.getApplication().getStateManager()).saveSerializedView(context);
            }
            context.responseComplete();
        }
    }

    public void beforePhase(PhaseEvent event)
    {

    }

    /**
     * We need to hang our AJAX phase listener in the invoke application phase as it is
     * impossible to stop rendering in the render response phase.
     *
     * @return PhaseId The AJAX phase listener will be invoked after the invoke application phase.
     */
    public PhaseId getPhaseId()
    {
        //return PhaseId.ANY_PHASE;
        //return PhaseId.RESTORE_VIEW;
        return PhaseId.INVOKE_APPLICATION;
    }

}
