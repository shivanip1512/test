package com.cannontech.analysis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.report.JFreeReport;
import org.jfree.report.PageDefinition;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.modules.output.pageable.pdf.PDFReportUtil;

import com.cannontech.analysis.report.CapBankRecentMaxDailyOpsReport;
import com.cannontech.analysis.report.CapBankReport;
import com.cannontech.analysis.report.CapControlCurrentStatusReport;
import com.cannontech.analysis.report.CapControlEventLogReport;
import com.cannontech.analysis.report.CapControlNewActivityReport;
import com.cannontech.analysis.report.CarrierDBReport;
import com.cannontech.analysis.report.DailyPeaksReport;
import com.cannontech.analysis.report.ECActivityDetailReport;
import com.cannontech.analysis.report.ECActivityLogReport;
import com.cannontech.analysis.report.HECO_CustomerMonthlyBillingSettlementReport;
import com.cannontech.analysis.report.HECO_DSMISReport;
import com.cannontech.analysis.report.HECO_LMEventSummaryReport;
import com.cannontech.analysis.report.HECO_MonthlyBillingSettlementReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.LPDataSummaryReport;
import com.cannontech.analysis.report.LPSetupDBReport;
import com.cannontech.analysis.report.LoadControlVerificationReport;
import com.cannontech.analysis.report.MeterOutageCountReport;
import com.cannontech.analysis.report.MeterReadReport;
import com.cannontech.analysis.report.PointDataIntervalReport;
import com.cannontech.analysis.report.PointDataSummaryReport;
import com.cannontech.analysis.report.ProgramDetailReport;
import com.cannontech.analysis.report.RepeaterRoleCollisionReport;
import com.cannontech.analysis.report.RouteDBReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.ScanRateSetupDBReport;
import com.cannontech.analysis.report.ScheduledMeterReadReport;
import com.cannontech.analysis.report.StarsAMRDetailReport;
import com.cannontech.analysis.report.StarsLMDetailReport;
import com.cannontech.analysis.report.StarsLMSummaryReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ActivityDetailModel;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.CapBankListModel;
import com.cannontech.analysis.tablemodel.CapBankRecentMaxDailyOpsModel;
import com.cannontech.analysis.tablemodel.CapControlCurrentStatusModel;
import com.cannontech.analysis.tablemodel.CapControlEventLogModel;
import com.cannontech.analysis.tablemodel.CapControlNewActivityModel;
import com.cannontech.analysis.tablemodel.CarrierDBModel;
import com.cannontech.analysis.tablemodel.DailyPeaksModel;
import com.cannontech.analysis.tablemodel.HECO_CustomerMonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.HECO_DSMISModel;
import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;
import com.cannontech.analysis.tablemodel.HECO_MonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LPDataSummaryModel;
import com.cannontech.analysis.tablemodel.LPSetupDBModel;
import com.cannontech.analysis.tablemodel.LoadControlVerificationModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MeterOutageCountModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.PointDataIntervalModel;
import com.cannontech.analysis.tablemodel.PointDataSummaryModel;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.analysis.tablemodel.RepeaterRoleCollisionModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteDBModel;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.ScanRateSetupDBModel;
import com.cannontech.analysis.tablemodel.ScheduledMeterReadModel;
import com.cannontech.analysis.tablemodel.StarsAMRDetailModel;
import com.cannontech.analysis.tablemodel.StarsLMDetailModel;
import com.cannontech.analysis.tablemodel.StarsLMSummaryModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.yukon.IDatabaseCache;
import com.keypoint.PngEncoder;

public class ReportFuncs {
    
    private static IDatabaseCache cache = YukonSpringHook.getBean(IDatabaseCache.class);
    private static PaoAuthorizationService paoAuthService = YukonSpringHook.getBean(PaoAuthorizationService.class);
    private static StrategyDao strategyDao = YukonSpringHook.getBean(StrategyDao.class);
    private static DeviceGroupUiService deviceGroupUiService = YukonSpringHook.getBean(DeviceGroupUiService.class);
    
    public static YukonReportBase createYukonReport(ReportModelBase<?> model) {
        YukonReportBase returnVal = null;
        if( model instanceof StatisticModel)
            returnVal = new StatisticReport();
        else if( model instanceof SystemLogModel)
            returnVal = new SystemLogReport();
        else if( model instanceof LMControlLogModel)
            returnVal = new SystemLogReport((LMControlLogModel)model);
        else if( model instanceof LoadGroupModel)
            returnVal = new LGAccountingReport();
        else if( model instanceof DailyPeaksModel)
            returnVal = new DailyPeaksReport();
        else if( model instanceof MeterReadModel)
            returnVal = new MeterReadReport();
        else if(model instanceof RouteDBModel)  //extends CarrierDBModel, so check this first.
            returnVal = new RouteDBReport();        
        else if( model instanceof CarrierDBModel)
            returnVal = new CarrierDBReport();
        else if( model instanceof MeterOutageCountModel)
            returnVal = new MeterOutageCountReport();
        else if( model instanceof ActivityModel)
            returnVal = new ECActivityLogReport();
        else if( model instanceof RouteMacroModel)
            returnVal = new RouteMacroReport();
        else if( model instanceof ScanRateSetupDBModel)
            returnVal = new ScanRateSetupDBReport();
        else if( model instanceof LPSetupDBModel)
            returnVal = new LPSetupDBReport();
        else if( model instanceof LPDataSummaryModel)
            returnVal = new LPDataSummaryReport();
        else if( model instanceof ActivityDetailModel)
            returnVal = new ECActivityDetailReport();
        else if( model instanceof ProgramDetailModel)
            returnVal = new ProgramDetailReport();
        else if( model instanceof WorkOrderModel)
            returnVal = new WorkOrder();
        else if( model instanceof StarsLMSummaryModel)
            returnVal = new StarsLMSummaryReport();
        else if( model instanceof StarsLMDetailModel)
            returnVal = new StarsLMDetailReport();
//      else if( model instanceof StarsAMRSummaryModel) TODO
//          returnVal = new StarsAMRSummaryReport();
        else if( model instanceof StarsAMRDetailModel)
            returnVal = new StarsAMRDetailReport();
        else if( model instanceof PointDataIntervalModel)
            returnVal = new PointDataIntervalReport();
        else if( model instanceof PointDataSummaryModel)
            returnVal = new PointDataSummaryReport();
        else if( model instanceof CapBankListModel)
            returnVal = new CapBankReport();
        else if( model instanceof CapControlNewActivityModel)
            returnVal = new CapControlNewActivityReport();
        else if( model instanceof CapControlCurrentStatusModel)
            returnVal = new CapControlCurrentStatusReport();
        else if( model instanceof CapControlEventLogModel)
            returnVal = new CapControlEventLogReport();         
        else if( model instanceof LoadControlVerificationModel)
            returnVal = new LoadControlVerificationReport();
        else if( model instanceof HECO_LMEventSummaryModel)
            returnVal = new HECO_LMEventSummaryReport();
        else if( model instanceof HECO_MonthlyBillingSettlementModel)
            returnVal = new HECO_MonthlyBillingSettlementReport();
        else if( model instanceof HECO_CustomerMonthlyBillingSettlementModel)
            returnVal = new HECO_CustomerMonthlyBillingSettlementReport();
        else if( model instanceof HECO_DSMISModel)
            returnVal = new HECO_DSMISReport();             
        else if( model instanceof CapBankRecentMaxDailyOpsModel)
            returnVal = new CapBankRecentMaxDailyOpsReport();
        else if( model instanceof ScheduledMeterReadModel)
            returnVal = new ScheduledMeterReadReport();
        else if( model instanceof RepeaterRoleCollisionModel)
            returnVal = new RepeaterRoleCollisionReport();
        else
            return null;

        returnVal.setModel(model);
        return returnVal;
    }
    
    public static void outputYukonReport(JFreeReport report, String ext, OutputStream out) throws IOException
    {
        if (ext.equalsIgnoreCase("pdf"))
            PDFReportUtil.createPDF(report, out);

        else if (ext.equalsIgnoreCase("csv"))
            ((ReportModelBase<?>)report.getData()).buildByteStream(out);
    }
    
    /**
      * Create the empty image for the given page size.
      *
      * @param pf the page format that defines the image bounds.
      * @return the generated image.
      */
    public static BufferedImage createImage(final PageDefinition pd)
    {
        final double width = pd.getWidth();
        final double height = pd.getHeight();
        //write the report to the temp file
        final BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_BYTE_INDEXED);
        return bi;
    }

    public static void encodePNG(java.io.OutputStream out, Image image) throws java.io.IOException
    {
        final PngEncoder encoder = new PngEncoder(image, true, 0, 9);
        final byte[] data = encoder.pngEncode();
        out.write(data);
    }

    public static void generatePreview(YukonReportBase rmReport) throws FunctionInitializeException, ReportProcessingException {
        rmReport.getModel().collectData();
    
        //Create the report
        JFreeReport report = rmReport.createReport();
        report.setData(rmReport.getModel());
    
        final PreviewDialog dialog = new PreviewDialog(report);
        // Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
        dialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dialog.setVisible(false);
                dialog.dispose();
                System.exit(0);
            };
        });
    
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    public static List<? extends Object> getObjectsByModelType(ReportFilter filter, int userId) {
        
        if( filter.equals(ReportFilter.DEVICE)){
            return cache.getAllDevices();

        } else if( filter.equals(ReportFilter.PORT)){
            return cache.getAllPorts();

        } else if( filter.equals(ReportFilter.GROUPS)){
            List<? extends DeviceGroup> allGroups = deviceGroupUiService.getGroups(new NonHiddenDeviceGroupPredicate());
            List<String> mappingList = new MappingList<DeviceGroup, String>(allGroups, new ObjectMapper<DeviceGroup, String>() {
                @Override
                public String map(DeviceGroup from) {
                    return from.getFullName();
                }
            });
            return mappingList;
        } else if( filter.equals(ReportFilter.ROUTE)){
            List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();
            return allRoutes;
        } else if( filter.equals(ReportFilter.LMCONTROLAREA)){
        	
        	// don't load anything, the pickers handle available control area loading
        	return new ArrayList<LiteYukonPAObject>();
        	
        } else if( filter.equals(ReportFilter.LMGROUP)) {
        	
        	// don't load anything, the pickers handle available group loading
        	return new ArrayList<LiteYukonPAObject>();
        	
        } else if( filter.equals(ReportFilter.TRANSMITTER)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> trans = new ArrayList<LiteYukonPAObject>();
            
            if( allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    if (lPao.getPaoType().getPaoClass() == PaoClass.TRANSMITTER)
                        trans.add(lPao);
                }
            }
            return trans;
            
        } else if( filter.equals(ReportFilter.RECEIVER)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> receivers = new ArrayList<LiteYukonPAObject>();

            if( allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    if (lPao.getPaoType().isRtu() || lPao.getPaoType() == PaoType.SERIES_5_LMI)
                        receivers.add(lPao);
                }
            }
            return receivers;
            
        } else if( filter.equals(ReportFilter.RTU)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> rtus = new ArrayList<LiteYukonPAObject>();
            
            if( allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    
                    if (lPao.getPaoType().isRtu() || lPao.getPaoType() == PaoType.DAVISWEATHER) {
                        rtus.add(lPao);
                    }
                }
            }
            return rtus;
            
        } else if( filter.equals(ReportFilter.CAPCONTROLSUBBUS)) {
            List<LiteYukonPAObject> capControlSubBuses = cache.getAllCapControlSubBuses();
            return capControlSubBuses;
        } else if( filter.equals(ReportFilter.CAPCONTROLSUBSTATION)) {
            List<LiteYukonPAObject> capControlSubstations = cache.getAllCapControlSubStations();
            return capControlSubstations;
        } else if( filter.equals(ReportFilter.CAPCONTROLFEEDER)) {
            List<LiteYukonPAObject> capControlFeeders = cache.getAllCapControlFeeders();
            return capControlFeeders;
        } else if( filter.equals(ReportFilter.CAPBANK)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> caps = new ArrayList<LiteYukonPAObject>();
            
            if( allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    if(lPao.getPaoType() == PaoType.CAPBANK) {                        
                        caps.add(lPao);
                    }
                }
            }
            return caps;
            
        } else if (filter.equals(ReportFilter.AREA)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> areas = new ArrayList<LiteYukonPAObject>();
            
            if (allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    if (lPao.getPaoType() == PaoType.CAP_CONTROL_AREA) {
                        areas.add(lPao);
                    }
                }
            }
            return areas;
        }
        else if( filter.equals(ReportFilter.SCHEDULE)) {
            List <LiteYukonPAObject> allPaos = cache.getAllYukonPAObjects();
            List <LiteYukonPAObject> schedules = new ArrayList<LiteYukonPAObject>();

            if( allPaos != null) {
                for (LiteYukonPAObject lPao : allPaos) {
                    if(lPao.getPaoType() == PaoType.SCRIPT )                     
                        schedules.add(lPao);
                }
            }
            return schedules;   

        }
        else if( filter.equals(ReportFilter.PROGRAM) || filter.equals(ReportFilter.PROGRAM_SINGLE_SELECT)) {
        	
        	// don't load anything, the pickers handle available program loading
        	return new ArrayList<LiteYukonPAObject>();
        	
        } else if (filter.equals(ReportFilter.STRATEGY)) {
            
        	List<LiteCapControlStrategy> strategyList = strategyDao.getAllLiteStrategies();
        	
        	return strategyList;
        }
        else {
            return new ArrayList<Object>(0);    //and empty list of nothing objects. 
        }
    }
    
    public static List<LiteYukonPAObject> getRestrictedPrograms(LiteYukonUser user){
        List<LiteYukonPAObject> programs = cache.getAllLMPrograms();
        return paoAuthService.filterAuthorized(user, programs, Permission.LM_VISIBLE);
    }
    
    public static List<LiteYukonPAObject> getRestrictedLMGroups(LiteYukonUser user){
        List<LiteYukonPAObject> groups = cache.getAllLMGroups();
        List<LiteYukonPAObject> filtered = paoAuthService.filterAuthorized(user, groups, Permission.LM_VISIBLE); 
        return filtered;
    }
    
    /**
     * Returns a subset of the ProgramLoadGroup List that the user is allowed to view.
     * If none, an empty list is returned.
     * @param programAndGroupList
     * @param restrictedPrograms
     * @return
     */
    public static List<ProgramLoadGroup> filterProgramsByPermission(List<ProgramLoadGroup> programAndGroupList, 
                                                                    List<LiteYukonPAObject> restrictedPrograms){
        if(!restrictedPrograms.isEmpty()) {
            List<ProgramLoadGroup> filterProgramList = new ArrayList<ProgramLoadGroup>();
            for(ProgramLoadGroup programLoadGroup : programAndGroupList) {
                for(YukonPao restrictedProgram : restrictedPrograms){
                    if(restrictedProgram.getPaoIdentifier().getPaoId() == programLoadGroup.getPaobjectId()){
                        filterProgramList.add(programLoadGroup);
                        break;
                    }
                }
            }
            return filterProgramList;
        } else {
            return programAndGroupList;
        }
    }
}