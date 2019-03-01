package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jdom2.JDOMException;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.FileUtil;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class DeveloperController {
    private final Logger log = YukonLogManager.getLogger(DeveloperController.class);
    private static final String homeKey = "yukon.web.modules.dev.ecobeePGPKeyPair.";

    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private ProgramDao programDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonListDao listDao;
    @Autowired private EncryptedRouteDao encryptedRouteDao;

    private final Map<String, Integer> databaseFields;
    private final Map<String, String> categoryFields;

    @Autowired
    public DeveloperController(ConfigurationSource configurationSource) {
        Map<String, Integer> mutableDatabaseFields = new TreeMap<>();
        Map<String, String> mutableCategoryFields = new TreeMap<>();
        if (configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
            try {
                DBChangeMsg obj = new DBChangeMsg();
                Class<?> objClass = obj.getClass();
                Field[] fields = objClass.getFields();
                for (Field field : fields) {
                    String name = field.getName();
                    Object value = field.get(obj);
                    if (name.startsWith("CAT_")) {
                        mutableCategoryFields.put(name, (String) value);
                    } else if (name.startsWith("CHANGE_")) {
                        mutableDatabaseFields.put(name, (Integer) value);
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.warn("DBChange development tool was not setup properly. Unexpected error occurred. ", e);
            }
        }
        databaseFields = ImmutableMap.copyOf(mutableDatabaseFields);
        categoryFields = ImmutableMap.copyOf(mutableCategoryFields);
    }

    @RequestMapping(value = { "/", "/dev" })
    public String root() {
        return "development.jsp";
    }

    @RequestMapping("/download-uber-log")
    public String downloadUberLog(@RequestParam final List<UberLogComponent> logs, @RequestParam Instant start,
            @RequestParam Instant stop, HttpServletResponse response) throws MalformedURLException, IOException {

        File logDir = new File(BootstrapUtils.getServerLogDir());
        File[] logFiles =
            logDir.listFiles(FileUtil.creationDateFilter(start.minus(Duration.standardDays(1)), stop, true));
        Set<DatedLogFile> datedLogFiles = new HashSet<>();
        Set<UberLogComponent> logComponentTypes = new HashSet<>();
        for (File logFile : logFiles) {
            for (UberLogComponent uberLogCompenent : logs) {
                if (logFile.getName().toLowerCase().startsWith(uberLogCompenent.getLabel().toLowerCase())) {
                    datedLogFiles.add(new DatedLogFile(uberLogCompenent, start, stop, logFile));
                    logComponentTypes.add(uberLogCompenent);
                }
            }
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + getFilenameForLogComponents(logComponentTypes) +"\"");
        InOrderMultiLogReader logReader = new InOrderMultiLogReader(datedLogFiles);
        logReader.prime();
        logReader.writeTo(new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
        return null;
    }

    @RequestMapping("/uber-log")
    public String uberLog(ModelMap model) {
        model.addAttribute("logs", UberLogComponent.values());
        model.addAttribute("start", Instant.now().minus(Duration.standardHours(24)));
        model.addAttribute("stop", Instant.now());
        return "uberLog.jsp";
    }

    @RequestMapping("/db-change")
    public String dbChangePage(ModelMap model) {
        model.addAttribute("databaseFields", databaseFields);
        model.addAttribute("categoryFields", categoryFields);
        model.addAttribute("dbChangeTypes", DbChangeType.values());
        return "dbChange.jsp";
    }

    @RequestMapping("/do-db-change")
    @ResponseBody
    public Map<String, Object> doDbChangePage(Integer itemId, String database, String category, DbChangeType type) {
        Integer databaseValue = databaseFields.get(database);
        String categoryValue = categoryFields.get(category);
        Map<String, Object> returnJson = Maps.newHashMapWithExpectedSize(2);
        try {
            dbChangeManager.processDbChange(itemId, databaseValue, categoryValue, type);
        } catch (Exception e) {
            log.warn("Exception caught while sending manual DbChange message. ", e);
            returnJson.put("error", true);
            returnJson.put("errorMessage", "Exception Thrown: " + e.getMessage());
        }
        return returnJson;
    }

    @RequestMapping("/create-import-files")
    public String createImportFiles(ModelMap model) {
        Map<String, Object> jsonData = new HashMap<>();
        for (EnergyCompany ec : energyCompanyDao.getAllEnergyCompanies()) {
            Map<String, Object> energyCompanyData = new HashMap<>();
            energyCompanyData.put("name", ec.getName());

            List<ApplianceCategory> applianceCategories = applianceCategoryDao.getApplianceCategoriesByEcId(ec.getId());
            Map<ApplianceCategory, List<Program>> programsByAppCat =
                programDao.getByApplianceCategories(applianceCategories);
            Map<String, Object> appCats = new HashMap<>();
            for (ApplianceCategory appCat : programsByAppCat.keySet()) {
                Map<String, Object> programs = new HashMap<>();
                for (Program program : programsByAppCat.get(appCat)) {
                    List<String> loadGroupNames = new ArrayList<>();
                    for (LoadGroup loadGroup : loadGroupDao.getByStarsProgramId(program.getProgramId())) {
                        loadGroupNames.add(loadGroup.getName());
                    }
                    Map<String, Object> programData = new HashMap<>();
                    programData.put("name", program.getProgramName());
                    programData.put("loadGroups", loadGroupNames);
                    programs.put(Integer.toString(program.getProgramId()), programData);
                }
                Map<String, Object> appCatData = new HashMap<>();
                appCatData.put("name", appCat.getName());
                appCatData.put("programs", programs);
                appCats.put(Integer.toString(appCat.getApplianceCategoryId()), appCatData);
            }
            energyCompanyData.put("applianceCategories", appCats);

            List<LiteUserGroup> residentialUserGroups = ecMappingDao.getResidentialUserGroups(ec.getId());
            List<String> userGroups = new ArrayList<>();
            for (LiteUserGroup userGroup : residentialUserGroups) {
                userGroups.add(userGroup.getUserGroupName());
            }
            energyCompanyData.put("userGroups", userGroups);

            List<String> deviceTypes = new ArrayList<>();
            YukonSelectionList devTypeList =
                selectionListService.getSelectionList(ec, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
            for (YukonListEntry entry : devTypeList.getYukonListEntries()) {
                deviceTypes.add(entry.getEntryText());
            }
            energyCompanyData.put("deviceTypes", deviceTypes);
            jsonData.put(Integer.toString(ec.getId()), energyCompanyData);
        }

        model.addAttribute("energyCompanyData", jsonData);
        model.addAttribute("energyCompanies", energyCompanyDao.getAllEnergyCompanies());
        model.addAttribute("nextAccountNumber", getNextAccountNumber());
        model.addAttribute("nextSerialNumber", getNextSerialNumber());

        return "createImportFiles.jsp";
    }

    @RequestMapping("/download-hardware-file")
    public String createHardwareFile(HttpServletResponse response, int numberOfAccounts, int accountNumberStart,
            int numberOfHardware, long serialNumberStart, int appCatId, int programId, String loadGroup,
            String[] deviceTypes) throws IOException {

        ApplianceCategory appCat = applianceCategoryDao.getById(appCatId);
        Program program = programDao.getByProgramId(programId);

        response.setHeader("Content-Disposition", "attachment; filename=hardware.csv");

        Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        ImportFileCreater.writeHardwareFile(writer, numberOfAccounts, accountNumberStart, numberOfHardware,
            serialNumberStart, program.getProgramName(), loadGroup, appCat.getName(), deviceTypes);
        return null;
    }
    
    @RequestMapping("/download-account-file")
    public String createAccountFile(HttpServletResponse response, int numberOfAccounts, int accountNumberStart,
            String usergroup) throws IOException {

        response.setHeader("Content-Disposition", "attachment; filename=accounts.csv");

        Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        ImportFileCreater.writeAccountFile(writer, numberOfAccounts, accountNumberStart, usergroup);

        return null;
    }
    
    @RequestMapping("/viewDeviceGroupSimulator")
    public String viewDeviceGroupSimulator() {
        return "deviceGroupSimulator.jsp";
    }
    
    @GetMapping("/getEcobeePGPKeyPair")
    public ModelAndView ecobeePGPKeyPair(ModelMap model)
            throws CryptoException, IOException, JDOMException, DecoderException {
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee);
        PGPKeyPair keypair = new PGPKeyPair();
        if (!encryptionKey.isEmpty()) {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            String aesDecryptedPrivatekey = encrypter.decryptHexStr(encryptionKey.get().getPrivateKey());
            String aesDecryptedPublickey = encrypter.decryptHexStr(encryptionKey.get().getPublicKey());
            keypair.setPgpPrivateKey(aesDecryptedPrivatekey);
            keypair.setPgpPublicKey(aesDecryptedPublickey);
        }
        ModelAndView ecobeePGPKeys = new ModelAndView("ecobeePGPKeyPair.jsp", "pgpKeyPair", keypair);
        return ecobeePGPKeys;
    }

    @PostMapping(path = "/saveEcobeeKeyPair")
    public String saveEcobeeKeyPair(@ModelAttribute("pgpKeyPair") PGPKeyPair pgpKeyPair, HttpServletRequest request,
            FlashScope flash) throws CryptoException, IOException, JDOMException {
        Instant timestamp = Instant.now();
        if (!pgpKeyPair.getPgpPublicKey().isBlank() && !pgpKeyPair.getPgpPrivateKey().isBlank()) {
            saveEncryptionKey(pgpKeyPair.getPgpPublicKey(), pgpKeyPair.getPgpPrivateKey(), timestamp);
            flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "save.success"));
            return "redirect:getEcobeePGPKeyPair";
        }
        flash.setError(new YukonMessageSourceResolvable(homeKey + "save.failed"));
        return "ecobeePGPKeyPair.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor dateTimeEditor =
            datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATE, userContext, BlankMode.ERROR);
        binder.registerCustomEditor(Instant.class, dateTimeEditor);
    }

    private int getNextAccountNumber() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT").append(maxNumber("AccountNUmber"));
        sql.append("FROM CustomerAccount WHERE accountnumber NOT LIKE '%[^0-9]%'");

        return jdbcTemplate.queryForInt(sql) + 1;
    }

    private long getNextSerialNumber() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT").append(maxNumber("ManufacturerSerialNumber"));
        sql.append("FROM LMHardwareBase WHERE ManufacturerSerialNumber NOT LIKE '%[^0-9]%'");

        return jdbcTemplate.queryForLong(sql) + 1;
    }

    private SqlFragmentSource maxNumber(String colName) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleSql = builder.buildForAllOracleDatabases();
        oracleSql.append("MAX(CAST(");
        oracleSql.append(colName);
        oracleSql.append(" AS NUMBER(19)))");

        SqlBuilder otherSql = builder.buildOther();
        otherSql.append("MAX(CAST(");
        otherSql.append(colName);
        otherSql.append(" AS BIGINT))");

        return builder;
    }

    private String getFilenameForLogComponents(Set<UberLogComponent> logs) {
        String filename = "";
        int numberAdded = 0;
        for (UberLogComponent logType : logs) {
            filename += logType.getLabel() + "_";
            numberAdded++;
            if (filename.length() > 30) {
                break;
            }
        }
        if (numberAdded != logs.size()) {
            filename += "_plus " + (logs.size() - numberAdded) + " logs_";
        }
        filename += Instant.now().getMillis() + ".log";
        return filename;
    }

    private void saveEncryptionKey(String pgpPublicKey, String pgpPrivateKey, Instant timestamp)
            throws CryptoException, IOException, JDOMException {
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        String aesBasedCryptoPublicKey = new String(Hex.encodeHex(encrypter.encrypt(pgpPublicKey.getBytes())));
        log.debug("AES based crypto Public key [" + aesBasedCryptoPublicKey + "]");
        String aesBasedCryptoPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(pgpPrivateKey.getBytes())));
        log.debug("AES based crypto Private key [" + aesBasedCryptoPrivateKey + "]");
        encryptedRouteDao.saveOrUpdateEncryptionKey(aesBasedCryptoPrivateKey, aesBasedCryptoPublicKey,
            EncryptionKeyType.Ecobee, timestamp);
    }
    
    public static enum UberLogComponent {
        CAPCONTROL("Capcontrol"),
        DISPATCH("Dispatch"),
        DB_EDITOR("DBEditor"),
        LOAD_MANAGEMENT("Loadmanagement"),
        MACS("Macs"),
        PORTER("Porter"),
        SCANNER("Scanner"),
        SERVICE_MANAGER("ServiceManager"),
        TRENDING("Trending"),
        WEBSERVER("Webserver");

        final private String label;

        private UberLogComponent(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getDatePattern() {
            if (this == WEBSERVER || this == SERVICE_MANAGER) {
                return "MM/dd/yyyy HH:mm:ss,SSS";
            }
            return "MM/dd/yyyy HH:mm:ss.SSS";
        }
    }

    public class InOrderMultiLogReader {
        private Set<DatedLogFile> logFiles;

        public InOrderMultiLogReader(Set<DatedLogFile> logFiles) {
            this.logFiles = logFiles;
        }

        /**
         * Opens files and sets up LogReader for reading log files
         */
        public void prime() throws IOException {
            for (DatedLogFile logFile : logFiles) {
                logFile.prime();
            }
        }

        /**
         * Can only be called once
         */
        private void writeTo(Writer writer) throws IOException {
            String line;
            while ((line = readLine()) != null) {
                writer.write(line + "\n");
            }
            writer.flush();
        }

        private String readLine() throws IOException {
            if (logFiles.isEmpty()) {
                return null;
            }

            DatedLogFile oldestLog = getOldest();
            String line = oldestLog.readLine();
            if (line == null) {
                logFiles.remove(oldestLog);
                line = "EOF";
            }
            return String.format("%-15s | %s", oldestLog.getIdentifier(), line);
        }

        private DatedLogFile getOldest() {
            Instant oldestDate = new Instant();
            DatedLogFile nextLogFile = null;
            for (DatedLogFile logFile : logFiles) {
                if (logFile.getNextTime().isBefore(oldestDate)) {
                    oldestDate = logFile.getNextTime();
                    nextLogFile = logFile;
                }
            }
            return nextLogFile;
        }
    }

    public class DatedLogFile {
        private final File file;
        private final DateTimeFormatter dateFormat;
        private final UberLogComponent logComponent;
        private final Instant start;
        private final Instant stop;

        private RandomAccessFile randomAccessFile;
        private String nextline;
        private Instant nextTime;

        public DatedLogFile(UberLogComponent logComponent, Instant start, Instant stop, File file) {
            this.start = start;
            this.stop = stop;
            this.logComponent = logComponent;
            this.dateFormat = DateTimeFormat.forPattern(logComponent.getDatePattern());
            this.file = file;
        }

        public void prime() throws IOException {
            randomAccessFile = new RandomAccessFile(file, "r");
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            nextTime = new Instant(attr.creationTime().toMillis());
            readLine();// Need to read the first line to set nextline and nextTime properly
            boolean eof = false;
            while (!eof && nextTime.isBefore(start)) {
                eof = readLine() == null;
            }
        }

        public Instant getNextTime() {
            return nextTime;
        }

        /**
         * Returns null when log is exhausted
         */
        public String readLine() throws IOException {
            String thisLine = nextline;
            if (nextTime.isAfter(stop)) {
                return null;
            }
            nextline = randomAccessFile.readLine();
            nextTime = getDate(nextline, nextTime);
            return thisLine;
        }

        public String getIdentifier() {
            return logComponent.getLabel();
        }

        private Instant getDate(String line, Instant defaultDate) {
            Instant parsedDate = defaultDate;
            if (line != null) {
                try {
                    int dateFormatLength = logComponent.getDatePattern().length();
                    DateTime parseDateTime =
                        dateFormat.parseDateTime(line.substring(0, Math.min(line.length(), dateFormatLength)));
                    parsedDate = parseDateTime.toInstant();
                } catch (IllegalArgumentException e) {
                    // no date on this line
                }
            }
            return parsedDate;
        }
    }
}
