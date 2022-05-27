package com.cannontech.development.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.ProgramConstraint;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.DailyDefaultState;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMGearDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.development.model.DemandResponseSetup;
import com.cannontech.development.service.DemandResponseSetupService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.ApplianceCategoryService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class DemandResponseSetupServiceImpl implements DemandResponseSetupService {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Logger log = YukonLogManager.getLogger(DemandResponseSetupServiceImpl.class);
    @Autowired GlobalSettingDao settingDao;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private ApplianceCategoryService applianceCategoryService;
    @Autowired private StarsInventoryBaseService starsInventoryBaseService;
    @Autowired private EnrollmentDao enrollmentDao;
    private RestTemplate restTemplate;

    @Override
    public boolean isRunning() {
        return lock.isLocked();
    }
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
    }
    
    private String addYukon(){
        String url = webserverUrlResolver.getUrl("");
        if ((url.contains("localhost") || url.contains("127.0.0.1"))) {
            return "/yukon";
        } else {
            return "";
        }
    }
    
    private class Summary {
        List<String> notes = new ArrayList<>();
    }
    
    private void clean(DemandResponseSetup setup) {
        List<LiteYukonPAObject> programs = findPaoByTemplate(dbCache.getAllLMPrograms(), setup);
        List<LiteYukonPAObject> loadGroups = findPaoByTemplate(dbCache.getAllLMGroups(), setup);
        List<LiteYukonPAObject> areas = findPaoByTemplate(dbCache.getAllLMControlAreas(), setup);
        List<LiteYukonPAObject> scenarios = findPaoByTemplate(dbCache.getAllLMScenarios(), setup);
        
        List<Integer> groupIds = loadGroups.stream()
                .map(pao -> pao.getLiteID())
                .collect(Collectors.toList());
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIds);
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(setup.getUserContext().getYukonUser());
        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        inventoryIds.forEach(inventoryId ->{
            LiteInventoryBase inv = inventoryBaseDao.getByInventoryId(inventoryId);
            starsInventoryBaseService.removeDeviceFromAccount(inv, lsec, setup.getUserContext().getYukonUser());     
        });
        
        
        programs.forEach(p -> {
            try {
                AssignedProgram program = assignedProgramDao.getByDeviceId(p.getLiteID());
                log.info("Unassigning program:{}", program.getProgramName());
                applianceCategoryService.unassignProgram(program.getApplianceCategoryId(), program.getAssignedProgramId(),
                        setup.getUserContext());
                log.info("Unassigned program:{}", program.getProgramName());
            } catch (Exception e) {
                log.error("Can't unassign {}", p.getPaoName(), e);
            }
        });
        
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.setContentType(MediaType.APPLICATION_JSON);
        newheaders.set("Authorization", "Bearer " + setup.getToken());
        HttpEntity<Object> request = new HttpEntity<>(newheaders);
        programs.forEach(p -> delete(request, p, "loadPrograms"));
        loadGroups.forEach(p -> delete(request, p, "loadGroups"));
        scenarios.forEach(p -> delete(request, p, "controlScenarios"));
        areas.forEach(p -> delete(request, p, "controlAreas"));
     }

    private void delete(HttpEntity<Object> request, LiteYukonPAObject p, String type) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/"+type+"/" + p.getLiteID());
        restTemplate.exchange(getUrl(url), HttpMethod.DELETE, request, Object.class);
        log.info("Deleted {}", p.getPaoName());
    }

    private List<LiteYukonPAObject> findPaoByTemplate(List<LiteYukonPAObject> list, DemandResponseSetup setup) {
        return list.stream()
                .filter(p -> p.getPaoName().contains(setup.getTemplateName())).collect(Collectors.toList());
    }
  
    @Override
    public void executeSetup(DemandResponseSetup drSetup) {
        if(drSetup.isClean()) {
            clean(drSetup);
        } else {
            setup(drSetup);
        }
    }

    private void setup(DemandResponseSetup drSetup) {
        Summary notes = new Summary();
        notes.notes.add("Setup:" + drSetup);
        log.info("Setup started program:{} settings:{}", dbCache.getAllPaosMap().get(drSetup.getProgramId()), drSetup);
        if (lock.tryLock()) {
            try {
                List<LoadProgram> createdPrograms = createProgramsAndLoadGroups(drSetup, notes);
                createControlAreas(drSetup, createdPrograms, notes);
                createScenarios(drSetup, createdPrograms, notes);

                assignProgramToEnergyCompany(createdPrograms, drSetup, notes);

                // 1 account - 3 devices
                List<Pair<CustomerAccount, List<LiteYukonPAObject>>> accountsToDevices = getDevicesPerAccount(drSetup);
                enrollDevices(createdPrograms, accountsToDevices, drSetup.getUserContext(), notes);
                log.info("Setup completed program:{} settings:{}", dbCache.getAllPaosMap().get(drSetup.getProgramId()), drSetup);
                log.info("Summary:\n" + String.join("\n", notes.notes));
            } catch (UnexpectedRollbackException e) {
                log.error("Setup failed program:{} settings:{}", dbCache.getAllPaosMap().get(drSetup.getProgramId()), drSetup, e);
                log.info("Summary:\n" + String.join("\n", notes.notes));
            } catch (Exception ex) {
                log.error("Setup failed program:{} settings:{}", dbCache.getAllPaosMap().get(drSetup.getProgramId()), drSetup, ex);
                log.info("Summary:\n" + String.join("\n", notes.notes));
            }finally {
                lock.unlock();
            }

        }
    }

    private void assignProgramToEnergyCompany(List<LoadProgram> createdPrograms, DemandResponseSetup drSetup, Summary notes) {
        AssignedProgram pr = assignedProgramDao.getByDeviceId(drSetup.getProgramId());
        AtomicInteger counter = new AtomicInteger(
                assignedProgramDao.getHighestProgramOrder(pr.getApplianceCategoryId()));
        
        List<String> programNames = new ArrayList<>();
        createdPrograms.forEach(p -> {
            AssignedProgram program = new AssignedProgram();
           // program.setProgramOrder(counter.incrementAndGet());
            program.setProgramId(p.getProgramId());
            program.setProgramName(p.getName());
            program.setDisplayName(p.getName());
            program.setShortName(p.getName());
            program.setApplianceCategoryId(pr.getApplianceCategoryId());
            program.getWebConfiguration();
            program.setDescription(p.getName());
            applianceCategoryService.assignProgram(program, drSetup.getUserContext());
            programNames.add(p.getName());
        });
        
        if(log.isDebugEnabled()) {
            notes.notes.add("Assigned to Energy Company programs:"+ programNames.size() + " " + programNames);
        } else {
            notes.notes.add("Assigned to Energy Company programs:"+ programNames.size());
        }
    }
    
    private void enrollDevices(List<LoadProgram> createdPrograms,
            List<Pair<CustomerAccount, List<LiteYukonPAObject>>> accountsToDevices, YukonUserContext userContext, Summary notes) {

        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        List<EnrollmentHelper> enrollments = createEnrollments(createdPrograms, accountsToDevices,
                userContext, energyCompany);

        List<String> enrolledDevicesInfo = new ArrayList<>();
        enrollments.stream().forEach(e -> {
            try {
                enrollmentHelperService.doEnrollment(e, EnrollmentEnum.ENROLL, userContext.getYukonUser());
                log.info("Enrolled Account:{} Device Serial:{}", e.getAccountNumber(), e.getSerialNumber());
                enrolledDevicesInfo.add("Account:" + e.getAccountNumber() + " Serial:" + e.getSerialNumber());
            } catch (Exception ex) {
                log.error("Unable to enroll Account:{} Serial:{}", e.getAccountNumber(), e.getSerialNumber(), ex);
            }
        });

        if (log.isDebugEnabled()) {
            notes.notes.add("Enrolled devices:" + enrolledDevicesInfo.size() + " " + enrolledDevicesInfo);
        } else {
            notes.notes.add("Enrolled devices:" + enrolledDevicesInfo.size());
        }
    }

    private List<EnrollmentHelper> createEnrollments(List<LoadProgram> createdPrograms,
            List<Pair<CustomerAccount, List<LiteYukonPAObject>>> accountsToDevices, YukonUserContext userContext,
            LiteStarsEnergyCompany energyCompany) {
        List<EnrollmentHelper> enrollments = new ArrayList<>();
        for (int i = 0; i < accountsToDevices.size(); i++) {
            try {
                LoadProgram program = createdPrograms.get(i);
                CustomerAccount account = accountsToDevices.get(i).getKey();
                List<LiteYukonPAObject> threeDevices = accountsToDevices.get(i).getValue();
                String groupName = program.getAssignedGroups().get(0).getGroupName();
                threeDevices.forEach(device -> {
                    LiteInventoryBase liteInventoryBase = inventoryBaseDao
                            .getHardwareByDeviceId(device.getLiteID());
                    try {
                        hardwareUiService.addDeviceToAccount(liteInventoryBase, account.getAccountId(), false,
                                energyCompany, userContext.getYukonUser());
                    } catch (Exception e) {
                        //try unenroll before enrolling
                        hardwareUiService.addDeviceToAccount(liteInventoryBase, account.getAccountId(), true,
                                energyCompany, userContext.getYukonUser());
                    }
                    log.info("Added device:{} to account:{}", device.getPaoName(), account.getAccountNumber());
                    enrollments.add(new EnrollmentHelper(account.getAccountNumber(),
                            groupName, program.getName(),
                            liteInventoryBase.getManufacturerSerialNumber()));
                });
            } catch (Exception e) {
                log.error(e);
                continue;
            }
        }
        return enrollments;
    }

    private List<Pair<CustomerAccount, List<LiteYukonPAObject>>> getDevicesPerAccount(DemandResponseSetup drSetup) {
        int maxAccounts = (int) Math.ceil((double)drSetup.getDevices() / 3);
        List<CustomerAccount> accounts = customerAccountDao.getAll()
                .stream()
                .filter(a -> a.getAccountId() > 0)
                .limit(maxAccounts)
                .collect(Collectors.toList());
        List<LiteYukonPAObject> devices = dbCache.getAllDevices().stream()
                .filter(device -> drSetup.getTypes().contains(device.getPaoType()))
                .limit(drSetup.getDevices())
                .collect(Collectors.toList());
        
        List<List<LiteYukonPAObject>> devicesPerAccount = Lists.partition(Lists.newArrayList(devices), 3);
        List<Pair<CustomerAccount, List<LiteYukonPAObject>>> accountsToDevices = new ArrayList<>();
        for (int i = 0; i < devicesPerAccount.size(); i++) {
            try {
                accountsToDevices.add(Pair.of(accounts.get(i), devicesPerAccount.get(i)));
            } catch (Exception e) {
                // ignore no accounts to enroll
                log.error(e);
                break;
            }
        }
        return accountsToDevices;
    }

    private void createControlAreas(DemandResponseSetup drSetup, List<LoadProgram> createdPrograms, Summary notes) {
        List<List<LoadProgram>> programs = partionPrograms(drSetup.getControlAreas(), createdPrograms);
        AtomicInteger counter = new AtomicInteger(1);
        List<ControlArea> areas = programs.stream().map(
                p -> createControlArea(drSetup.getTemplateName(), drSetup.getToken(), counter.getAndIncrement(), p)).collect(Collectors.toList());
        if(log.isDebugEnabled()) {
            List<String> areasToPrograms = areas.stream()
                    .map(a -> a.getName() + ":"
                            + a.getProgramAssignment().stream().map(ps -> ps.getProgramName()).collect(Collectors.toList()))
                    .collect(Collectors.toList());
            notes.notes.add("Created areas:"+ areas.size() + " " + areasToPrograms);
        } else {
            notes.notes.add("Created areas:"+ areas.size());
        }
    }
    
    private ControlArea createControlArea(String template, String token, int i, List<LoadProgram> programs) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/controlAreas");
        ControlArea area = new ControlArea();
        area.setName(template + " Control Area " + i);
        area.setAllTriggersActiveFlag(false);
        area.setControlInterval(0);
        area.setMinResponseTime(0);
        area.setDailyDefaultState(DailyDefaultState.None);
        AtomicInteger counter = new AtomicInteger(1);
        List<ControlAreaProgramAssignment> assignment = programs.stream()
                .map(p -> getProgramAssignment(counter, p)).collect(Collectors.toList());
        area.setProgramAssignment(assignment);

        HttpEntity<ControlArea> requestEntity = getRequestWithAuthHeaders(area, token);
        ResponseEntity<ControlArea> response = restTemplate.exchange(getUrl(url), HttpMethod.POST, requestEntity,
                ControlArea.class);
        log.info("Created:{} Programs Assigned:{}", response.getBody().getName(),
                response.getBody().getProgramAssignment().size());
        return area;
    }

    private ControlAreaProgramAssignment getProgramAssignment(AtomicInteger counter, LoadProgram p) {
        ControlAreaProgramAssignment a = new ControlAreaProgramAssignment();
        a.setProgramId(p.getProgramId());
        a.setProgramName(p.getName());
        a.setStartPriority(counter.get());
        a.setStopPriority(counter.getAndIncrement());
        return a;
    }
    
    private List<List<LoadProgram>> partionPrograms(int number, List<LoadProgram> createdPrograms) {
        List<List<LoadProgram>> programs = Lists.partition(Lists.newArrayList(createdPrograms),
                (int) Math.ceil((double) createdPrograms.size() / number));
        return programs;
    }
    
    private void createScenarios(DemandResponseSetup drSetup, List<LoadProgram> createdPrograms, Summary notes) {
        List<List<LoadProgram>> programs = partionPrograms(drSetup.getScenarios(), createdPrograms);
        AtomicInteger counter = new AtomicInteger(1);
        List<ControlScenario> scenarios = programs.stream().map(
                p -> createScenario(drSetup.getTemplateName(), drSetup.getToken(), counter.getAndIncrement(), p)).collect(Collectors.toList());
        
        if(log.isDebugEnabled()) {
            List<String> areasToPrograms = scenarios.stream()
                    .map(a -> a.getName() + ":"
                            + a.getAllPrograms().stream().map(ps -> dbCache.getAllPaosMap().get(ps.getProgramId()).getPaoName())
                                    .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            notes.notes.add("Created scenario:"+ scenarios.size() + " " + areasToPrograms);
        } else {
            notes.notes.add("Created scenario:"+ scenarios.size());
        }
    }

    private ControlScenario createScenario(String template, String token, int i, List<LoadProgram> programs) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/controlScenarios");
        ControlScenario scenario = new ControlScenario();
        scenario.setName(template + " Scenario " + i);
        List<ProgramDetails> programDetails = programs.stream()
                .map(p -> getProgramDetails(p)).collect(Collectors.toList());
        scenario.setAllPrograms(programDetails);
        HttpEntity<ControlScenario> requestEntity = getRequestWithAuthHeaders(scenario, token);
        ResponseEntity<ControlScenario> response = restTemplate.exchange(getUrl(url), HttpMethod.POST, requestEntity,
                ControlScenario.class);
        log.info("Created:{} Programs:{}", response.getBody().getName(), response.getBody().getAllPrograms().size());
        return response.getBody(); 
    }
    
    
    private List<LoadProgram> createProgramsAndLoadGroups(DemandResponseSetup drSetup, Summary notes) {
        List<LoadProgram> createdPrograms = new ArrayList<>();
        List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(drSetup.getProgramId());

        List<String> programNames = new ArrayList<>();
        List<String> loadGroupNames = new ArrayList<>();
        
        IntStream.range(1, drSetup.getPrograms() + 1).forEach(i -> {
            LoadGroupBase loadGroup = copyLoadGroup(drSetup.getTemplateName(), drSetup.getToken(),
                    loadGroups.get(0).getLoadGroupId(), i);
            LoadProgram program = copyProgram(drSetup.getTemplateName(), drSetup.getToken(), drSetup.getProgramId(), i);
            updateProgram(drSetup.getToken(), program, loadGroup);
            
            programNames.add(program.getName());
            loadGroupNames.add(loadGroup.getName());

            createdPrograms.add(program);
        });
        
        if(log.isDebugEnabled()) {
            notes.notes.add("Created programs:"+ programNames.size() + " " + programNames);
            notes.notes.add("Created load groups:"+ loadGroupNames.size() + " " + loadGroupNames);
        } else {
            notes.notes.add("Created programs:"+ programNames.size());
            notes.notes.add("Created load groups:"+ loadGroupNames.size());
        }
        
        return createdPrograms;
    }
    
    private LoadGroupBase copyLoadGroup(String template, String token, int loadGroupId, int i) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/loadGroups/" + loadGroupId + "/copy");
        LMCopy copy = new LMCopy();
        copy.setName(template + " Load Group " + i);
        
        HttpEntity<LMCopy> requestEntity = getRequestWithAuthHeaders(copy, token);
        ResponseEntity<LoadGroupBase> response = restTemplate.exchange(getUrl(url), HttpMethod.POST, requestEntity,
                LoadGroupBase.class);
        LoadGroupBase group = response.getBody();
        group.setId(deviceDao.getDeviceIdByName(copy.getName()));
        log.info("Created:{}", group.getName());
        return group ;
    }
    
    private LoadProgram copyProgram(String template, String token, int programId, int i) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/loadPrograms/" + programId + "/copy");
        ProgramConstraint pr = new ProgramConstraint();
        pr.setConstraintId(10);
        LoadProgramCopy copy = new LoadProgramCopy();
        copy.setConstraint(pr);
        copy.setName(template + " Program " + i);
        copy.setCopyMemberControl(false);
        copy.setOperationalState(OperationalState.Automatic);
        
        HttpEntity<LoadProgramCopy> requestEntity = getRequestWithAuthHeaders(copy, token);
        ResponseEntity<LoadProgram> response = restTemplate.exchange(getUrl(url), HttpMethod.POST, requestEntity,
                LoadProgram.class);
        
        LoadProgram program = response.getBody();
        program.setProgramId(deviceDao.getDeviceIdByName(copy.getName()));
        log.info("Created:{}", program.getName());
        return program;
    }
    
    private void updateProgram(String token, LoadProgram program, LoadGroupBase loadGroup) {
        String url = webserverUrlResolver.getUrl(addYukon() + "/api/dr/loadPrograms/" + program.getProgramId());
        program.setAssignedGroups(new ArrayList<>());
        ProgramGroup group = new ProgramGroup();
        group.setGroupId(loadGroup.getId());
        group.setGroupName(loadGroup.getName());
        group.setGroupOrder(1);
        group.setType(loadGroup.getType());
        program.getAssignedGroups().add(group);

        HttpEntity<LoadProgram> requestEntity = getRequestWithAuthHeaders(program, token);
        ResponseEntity<LoadProgram> response = restTemplate.exchange(getUrl(url), HttpMethod.PUT, requestEntity,
                LoadProgram.class);

        log.info("Assigned:{} to:{}", loadGroup.getName(), response.getBody().getName());

    }

    private <T> HttpEntity<T> getRequestWithAuthHeaders(T requestObject, String token) {
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.setContentType(MediaType.APPLICATION_JSON);
        newheaders.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(requestObject, newheaders);
    }

    private URI getUrl(String url) {
        URI uri = UriComponentsBuilder.fromUriString(url)
                .build()
                .toUri();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setOutputStreaming(false);
        if (useProxy(url)) {
            YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                factory.setProxy(httpProxy.getJavaHttpProxy());
            });
        }
        restTemplate.setRequestFactory(factory);
        return uri;
    }

    private boolean useProxy(String stringUrl) {
        if ((stringUrl.contains("localhost") || stringUrl.contains("127.0.0.1"))) {
            return false;
        }
        return true;
    }
    
    private ProgramDetails getProgramDetails(LoadProgram program) {
        ProgramDetails programDetails = new ProgramDetails();
        programDetails.setProgramId(program.getProgramId());
        programDetails.setStartOffsetInMinutes(0);
        programDetails.setStopOffsetInMinutes(0);
        List<LMGearDto> gears = program.getGears()
                .stream().map(p -> new LMGearDto(p.getGearNumber(), p.getGearName()))
                .collect(Collectors.toList());
        programDetails.setGears(gears);
        return programDetails;
    }
}