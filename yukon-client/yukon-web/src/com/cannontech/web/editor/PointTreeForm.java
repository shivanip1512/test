package com.cannontech.web.editor;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.capcontrol.CCSessionInfo;
import com.cannontech.web.editor.point.PointForm;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFTreeUtils;

public class PointTreeForm {

    private YukonPAObject device = null;
    private String statusMessage = null;
    private HtmlTree pointTree = null;
    private TreeNode pointList = null;
    private int deviceType = 0;
    
    private final String pointTreeName = "devicePointTree";

    /**
     * Accepts a paoID and creates the DBPersistent from it, and then retrieves the
     * data from the DB
     */
    public PointTreeForm(int paoId) {
        super();
        init(paoId);
    }

    public void init(int paoId) {
        LiteYukonPAObject litePAO = null;
        try {
            litePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(paoId);
        } catch(NotFoundException nfe) {
            // might be coming from a schedule or someother crazy nonpao
        }
        if(litePAO != null) {
            device = PAOFactory.createPAObject(litePAO);
            setPao(device);
        
            retrieveDB();
        }
    }

    public PointTreeForm() {}

    public YukonPAObject getPao() {

        return device;
    }

    public void setPao(YukonPAObject device) {
        this.device = device;
    }

    public void retrieveDB() {

        if (getPao() == null)
            return;

        try {
            setPao(Transaction.createTransaction(Transaction.RETRIEVE,getPao()).execute());

        } catch (TransactionException te) {
            CTILogger.error("Unable to retrieve db object", te);
            return;
        }

    }
    
    public void resetPointList() {
        pointList = null;
    }

    public TreeNode getPointList() {
        if (pointList == null) {
            pointList = new TreeNodeBase("root", "Points", false);
            TreeNode analog = new TreeNodeBase("pointtype", "Analog", false);
            TreeNode status = new TreeNodeBase("pointtype", "Status", false);
            TreeNode accum = new TreeNodeBase("pointtype","Accumulator", false);
            if (device != null) {
                int deviceId = device.getPAObjectID();
                List<LitePoint> litePoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceId);
        
                TreeSet<LitePoint> statusSet = new TreeSet<LitePoint>();
                TreeSet<LitePoint> analogSet = new TreeSet<LitePoint>();
                TreeSet<LitePoint> accumSet = new TreeSet<LitePoint>();

                for (LitePoint litePoint : litePoints) {
                    int pointType = litePoint.getPointType();
                    if (pointType == PointTypes.ANALOG_POINT || pointType == PointTypes.CALCULATED_POINT) {
                        analogSet.add(litePoint);
                    } else if (pointType == PointTypes.STATUS_POINT || pointType == PointTypes.CALCULATED_STATUS_POINT) {
                        statusSet.add(litePoint);
                    } else if (pointType == PointTypes.PULSE_ACCUMULATOR_POINT || pointType == PointTypes.DEMAND_ACCUMULATOR_POINT){
                        accumSet.add(litePoint);
                    }
                }
        
                analog = JSFTreeUtils.createTreeFromPointList(analogSet, analog);
                status = JSFTreeUtils.createTreeFromPointList(statusSet, status);
                accum = JSFTreeUtils.createTreeFromPointList(accumSet, accum);
                
                pointList.getChildren().add(status);
                pointList.getChildren().add(analog);
                pointList.getChildren().add(accum);
                
                JSFTreeUtils.splitTree(pointList, 100, "sublevels");
            }
        }
        restoreState(getPointTree());
        return pointList;
    }

    public String getSelectedPointFormatString() {
        return "Add Point";
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String msgStr) {
        this.statusMessage = msgStr;
    }

    public HtmlTree getPointTree() {
        return pointTree;
    }

    public void setPointTree(HtmlTree pointTree) {
        this.pointTree = pointTree;

    }

    public void pointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        //save the state of the point tree before changing the page
        saveState(getPointTree());
        try {

            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            String path = "/capcontrol/points/";
            String pointId = JSFParamUtil.getJSFReqParam("ptID");

            String location = contextPath + path + pointId;

            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CapControlForm form = (CapControlForm) session.getAttribute("capControlForm");
            form.setTab(ae);
            CBCNavigationUtil.bookmarkLocationAndRedirect(location, session);
            //go to the next page
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:pointClick. " + e.getMessage());
        } finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("point_click", fm);
            }
        }
    }
    
    public void addPointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        //save the state of the point tree before changing the page
        saveState(getPointTree());        
        try {
            String val = JSFParamUtil.getJSFReqParam("parentId");
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CapControlForm form = (CapControlForm) session.getAttribute("capControlForm");
            PointForm pointForm = (PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );
            pointForm.resetForm();
            form.setTab(ae);
            String location = "pointWizBase.jsf?parentId=" + val;
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);            
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:addPointClick. " + e.getMessage());
        } finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("add_point_click", fm);
            }
        }
    }

    public void deletePointClick (ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        //save the state of the point tree before changing the page
        saveState(getPointTree());        
        try {
            Integer paoID = getPao().getPAObjectID();
            List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(paoID.intValue());
            String attribVal  = "";
            for (LitePoint point : points) {
                attribVal += "value=" + point.getLiteID() + "&";
            }
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CapControlForm form = (CapControlForm) session.getAttribute("capControlForm");
            form.setTab(ae);
            String location = "deleteBasePoint.jsf?" + attribVal;
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);            
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);
            FacesContext.getCurrentInstance().responseComplete();
        } 
        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:deletePointClick. " + e.getMessage());
        } finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("delete_point_click", fm);
            }
        }
    }
   
    public static void insertPointsIntoDB(DBPersistent pointVector) {
        FacesMessage fcsMessage = new FacesMessage();
         try {
             PointUtil.insertIntoDB(pointVector);
         } catch (PersistenceException e) {
             fcsMessage.setDetail("ERROR creating points for CBC:CBCControlEditor" + e.getMessage());
         } finally {
             if (fcsMessage.getDetail() != null) {
                 FacesContext.getCurrentInstance().addMessage("cti_db_add", fcsMessage);                
             }
         }
    }

    public void setPointList(TreeNode pointList) {
        this.pointList = pointList;
    }

    private void saveState (HtmlTree tree) {
        if (tree != null) {
            Object treeState = tree.saveState(FacesContext.getCurrentInstance());
            CCSessionInfo ccSession = (CCSessionInfo) JSFParamUtil.getJSFVar("ccSession");
            ccSession.setTreeState(pointTreeName, treeState);
        }
    }

    private void restoreState (HtmlTree tree) {
        if (tree != null) {
            CCSessionInfo ccSession = (CCSessionInfo) JSFParamUtil.getJSFVar("ccSession");          
            if (ccSession.getTreeState(pointTreeName) != null ) {
                this.pointTree.restoreState(FacesContext.getCurrentInstance(), ccSession.getTreeState(pointTreeName));
            }
        }
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
