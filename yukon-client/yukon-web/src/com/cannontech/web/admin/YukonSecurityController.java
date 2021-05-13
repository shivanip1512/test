package com.cannontech.web.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMWriter;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.EcobeePGPException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.service.impl.EcobeeZeusCommunicationServiceImpl;
import com.cannontech.encryption.CertificateGenerationFailedException;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.encryption.EcobeeZeusSecurityService;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.HoneywellSecurityService;
import com.cannontech.encryption.ItronSecurityService;
import com.cannontech.encryption.RSAKeyfileService;
import com.cannontech.encryption.SecurityKeyPair;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.DREncryption;
import com.cannontech.system.dao.impl.GlobalSettingDaoImpl;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class YukonSecurityController {
    
    @Autowired private EncryptedRouteDao encryptedRouteDao;
    @Autowired private RSAKeyfileService rsaKeyfileService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private HoneywellSecurityService honeywellSecurityService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EcobeeSecurityService ecobeeSecurityService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ItronSecurityService itronSecurityService;
    @Autowired private GlobalSettingDaoImpl globalSettingDaoImpl;
    @Autowired private EcobeeZeusSecurityService ecobeeZeusSecurityService;
    @Autowired private EcobeeZeusCommunicationServiceImpl ecobeeZeusCommunicationService;
    // TODO: Remove hard coded String once globalsettings is created for reportingUrl.
    private String reportingUrl = "http://eaton.com/ecobee/runtimedata";

    private static final int KEYNAME_MAX_LENGTH = 50;
    private static final int KEYHEX_DIGITS_LENGTH = 32;
    private static final String HEX_STRING_PATTERN = "^[0-9A-Fa-f]*$";
    private static final String HONEYWELL_CERTIFICATE_FILE_NAME = "cert-eaton.crt";
    private static final String RESPONSE_CONTENT_TYPE_X509_CERTIFICATE = "application/x-x509-user-cert";
    
    private Logger log = YukonLogManager.getLogger(YukonSecurityController.class);

    private final static String baseKey = "yukon.web.modules.adminSetup.security";
   
    private static class FileImportBindingBean {
        private String name = null;
        private MultipartFile file = null;
        public void setName(String name) { this.name = name; }
        public String getName() { return name; }
        public void setFile(MultipartFile file) { this.file = file; }
        public MultipartFile getFile() { return file; }
    }

    private static class HoneywellFileImportBindingBean {
        private String name = null;
        private MultipartFile file = null;

        public String getName() {
            return name;
        }

        public void setFile(MultipartFile file) {
            this.file = file;
        }

        public MultipartFile getFile() {
            return file;
        }
    }

    private Validator addKeyValidator =
        new SimpleValidator<EncryptionKey>(EncryptionKey.class) {
        @Override
        public void doValidation(EncryptionKey encryptionKey, Errors errors) {

            if (StringUtils.isNotEmpty(encryptionKey.getPrivateKey())) {
                if (encryptionKey.getPrivateKey().length() != KEYHEX_DIGITS_LENGTH) {
                    Integer[] length = { KEYHEX_DIGITS_LENGTH, encryptionKey.getPrivateKey().length() };
                    errors.rejectValue("privateKey", baseKey + ".errorMsg.exactLength", length, "");
                }

                Pattern lengthPattern = Pattern.compile(HEX_STRING_PATTERN);
                YukonValidationUtils.regexCheck(errors, "privateKey", encryptionKey.getPrivateKey(), lengthPattern,
                    baseKey + ".errorMsg.hexidecimal");
            } else {
                errors.rejectValue("privateKey", baseKey + ".errorMsg.empty");
            }
            if (StringUtils.isNotEmpty(encryptionKey.getName())) {
                for (EncryptionKey e : encryptedRouteDao.getEncryptionKeys()) {
                    if (e.getName().equals(encryptionKey.getName())) {
                        errors.rejectValue("name", baseKey + ".errorMsg.previouslyUsedKey");
                        break;
                    }
                }
                if (encryptionKey.getName().length() > KEYNAME_MAX_LENGTH) {
                    Integer[] length = { KEYNAME_MAX_LENGTH, encryptionKey.getName().length() };
                    errors.rejectValue("name", baseKey + ".errorMsg.length", length, "");
                }
            } else {
                errors.rejectValue("name", baseKey + ".errorMsg.empty");
            }
        }
    };

    private Validator importFileValidator = new SimpleValidator<FileImportBindingBean>(FileImportBindingBean.class) {
        @Override
        public void doValidation(FileImportBindingBean fileImportBindingBean, Errors errors) {
            try {
                FileUploadUtils.validateKeyUploadFileType(fileImportBindingBean.getFile());
            } catch (FileImportException e) {
                errors.rejectValue("file", e.getMessage());
            } catch (IOException e) {
                errors.rejectValue("file", baseKey + ".fileUploadError.noFile");
            }
            if (fileImportBindingBean.getName().length() > KEYNAME_MAX_LENGTH) {
                Object[] args = { KEYNAME_MAX_LENGTH };
                errors.rejectValue("name", baseKey + ".errorMsg.nameLength", args, "");
            } else if (StringUtils.isEmpty(fileImportBindingBean.getName())) {
                errors.rejectValue("name", baseKey + ".errorMsg.empty");
            } else {
                for (EncryptionKey e : encryptedRouteDao.getEncryptionKeys()) {
                    if (e.getName().equals(fileImportBindingBean.getName())) {
                        errors.rejectValue("name", baseKey + ".errorMsg.previouslyUsedKey");
                        break;
                    }
                }
            }

        }
    };

    @RequestMapping("/config/security/view")
    public String view(HttpServletRequest request, ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        model.addAttribute("showDialog", "");
        return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
            new HoneywellFileImportBindingBean(), flashScope, userContext);
    }

    private String view(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
            EncryptionKey encryptionKey, FileImportBindingBean fileImportBindingBean,
            HoneywellFileImportBindingBean honeywellFileImportBindingBean, FlashScope flashScope,
            YukonUserContext userContext) {

        model.addAttribute("encryptedRoute", encryptedRoute);
        model.addAttribute("encryptedRoutes", encryptedRouteDao.getAllEncryptedRoutes());

        model.addAttribute("encryptionKey", encryptionKey);
        List<EncryptionKey> encryptionKeys = encryptedRouteDao.getEncryptionKeys();
        
        boolean invalidKeyFound = false;
        try {
            char[] sharedPasskey = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(sharedPasskey);
            // Check validity of each key. Making sure it decrypts with no exceptions
            boolean isValid;
            for (EncryptionKey key : encryptionKeys) {
                try {
                    isValid = aes.isAuthentic(Hex.decodeHex(key.getPrivateKey().toCharArray()));
                } catch (DecoderException e) {
                    isValid = false;
                }
                key.setIsValid(isValid);
                invalidKeyFound = invalidKeyFound || !isValid;
            }

            // Extract Honeywell key if Honeywell is Eanble
            boolean honeywellEnabled =
                configurationSource.getBoolean(MasterConfigBoolean.HONEYWELL_SUPPORT_ENABLED, false);
            if (honeywellEnabled) {
                encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Honeywell)
                    .ifPresent(encKey -> {
                        try {
                            String decryptedPublicKeyValue = aes.decryptHexStr(encKey.getPublicKey());
                            model.addAttribute("honeywellPublicKey", decryptedPublicKeyValue);
                        } catch (CryptoException | DecoderException e) {
                            flashScope.setError(new YukonMessageSourceResolvable(
                                baseKey + ".honeywellKeyDecryptionFailed", e.getMessage()));
                        }
                    });
            }

        } catch (Exception e) {
            for (EncryptionKey key : encryptionKeys) {
                key.setIsValid(false);
            }
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".sharedKeyFileCorrupt", e.getMessage()));
            model.addAttribute("blockingError", true);
        }

        if (invalidKeyFound) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".invalidKeyFound"));
            model.addAttribute("blockingError", true);
        }

        try {
            Instant ecobeeKeyCreationTime = ecobeeSecurityService.getEcobeeKeyPairCreationTime();
            String ecobeeDateGenerated = dateFormattingService.format(ecobeeKeyCreationTime,
                DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            model.put("ecobeeKeyGeneratedDateTime", ecobeeDateGenerated);
        } catch (NoSuchElementException e) {
            log.debug("Ecobee PGP Key Creation time is not available, may be it is not generated yet.");
        }
        
        try {
            ZeusEncryptionKey ecobeeZeusEncryptionKey = ecobeeZeusSecurityService.getZeusEncryptionKey();
            String ecobeeZeusDateGenerated = dateFormattingService.format(ecobeeZeusEncryptionKey.getTimestamp(),
                    DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            model.put("ecobeeKeyZeusGeneratedDateTime", ecobeeZeusDateGenerated);
        } catch (NoSuchElementException e) {
            log.debug("Ecobee Zeus Key Creation time is not available, may be it is not generated yet.");
        } catch (Exception e) {
            log.error("Error while retrieving Ecobee Zeus encryption keys. ", e);
        }

        try {
            Instant itronKeyCreationTime = itronSecurityService.getItronKeyPairCreationTime();
            String itronDateGenerated = dateFormattingService.format(itronKeyCreationTime,
                DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            model.put("itronKeyGeneratedDateTime", itronDateGenerated);
        } catch (NoSuchElementException e) {
            log.debug("Itron Key Creation time is not available, may be it is not generated yet.");
        }
        model.addAttribute("encryptionKeys", encryptionKeys);

        model.addAttribute("fileImportBindingBean", fileImportBindingBean);
        model.addAttribute("honeywellFileImportBindingBean", honeywellFileImportBindingBean);

        return "security/view.jsp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/config/security/saveNewKey")
    public String saveNewKey(HttpServletRequest request, ModelMap model, EncryptionKey encryptionKey,
            BindingResult bindingResult, FlashScope flashScope, YukonUserContext userContext) throws IOException,
            CryptoException, JDOMException, DecoderException {
        addKeyValidator.validate(encryptionKey, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("showDialog", "addKey");
            return view(request, model, new EncryptedRoute(), encryptionKey, new FileImportBindingBean(),
                new HoneywellFileImportBindingBean(), flashScope, userContext);
        }

        char[] password = CryptoUtils.getSharedPasskey();

        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        byte[] keyBytes = Hex.decodeHex(encryptionKey.getPrivateKey().toCharArray());
        String encryptedValue = new String(Hex.encodeHex(encrypter.encrypt(keyBytes)));

        encryptedRouteDao.saveNewEncryptionKey(encryptionKey.getName(), encryptedValue, null,
            EncryptionKeyType.ExpresscomOneWay, Instant.now());

        return "redirect:view";
    }

    @RequestMapping("/config/security/remove")
    public String remove(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
            BindingResult bindingResult, FlashScope flashScope, YukonUserContext userContext) {

        List<EncryptedRoute> routes = encryptedRouteDao.getAllEncryptedRoutes();
        for (EncryptedRoute route : routes) {
            if (route.getPaobjectId().equals(encryptedRoute.getPaobjectId())) {
                if (!route.isEncrypted()) {
                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".removeError.notAdded"));
                    return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
                        new HoneywellFileImportBindingBean(), flashScope, userContext);
                } else if (!encryptedRoute.getEncryptionKeyId().equals(route.getEncryptionKeyId())) {
                    // Trying to remove a route which showed it was added using a key. But actually added
                    // using different key.
                    // So we want to inform them they are not disabling the key they thought.
                    Object[] args = { encryptedRoute.getEncryptionKeyName(), route.getEncryptionKeyName() };
                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".removeError.staleData", args));
                    return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
                        new HoneywellFileImportBindingBean(), flashScope, userContext);
                }
            }
        }

        encryptedRouteDao.deleteEncryptedRoute(encryptedRoute);
        return "redirect:view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/config/security/add")
    public String getRedirect(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
            BindingResult bindingResult, FlashScope flashScope) {
        return "redirect:view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/config/security/add")
    public String add(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
            BindingResult bindingResult, FlashScope flashScope, YukonUserContext userContext) {
        List<EncryptedRoute> routes = encryptedRouteDao.getAllEncryptedRoutes();
        for (EncryptedRoute route : routes) {
            if (route.isEncrypted() && route.getPaobjectId().equals(encryptedRoute.getPaobjectId())) {
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".addError.alreadyAdded",
                    route.getEncryptionKeyName()));
                return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
                    new HoneywellFileImportBindingBean(), flashScope, userContext);
            }
        }
        List<EncryptionKey> keys = encryptedRouteDao.getEncryptionKeys();
        boolean keyExists = false;
        for (EncryptionKey key : keys) {
            if (key.getEncryptionKeyId().equals(encryptedRoute.getEncryptionKeyId())) {
                keyExists = true;
                break;
            }
        }

        if (!keyExists) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".addError.invalidKey"));
            return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
                new HoneywellFileImportBindingBean(), flashScope, userContext);
        }

        encryptedRouteDao.addEncryptedRoute(encryptedRoute);
        return "redirect:view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/config/security/deleteKey")
    public String deleteKey(HttpServletRequest request, ModelMap model, FlashScope flashScope, Integer encryptionKeyId,
            YukonUserContext userContext) {
        // Check to make sure key isn't being used before we delete it
        for (EncryptedRoute e : encryptedRouteDao.getAllEncryptedRoutes()) {
            if (e.getEncryptionKeyId() != null && e.getEncryptionKeyId().equals(encryptionKeyId)) {
                Object[] args = { e.getEncryptionKeyName(), e.getPaoName() };
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".errorMsg.keyInUse", args));
                return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(),
                    new HoneywellFileImportBindingBean(), flashScope, userContext);
            }
        }

        encryptedRouteDao.deleteEncryptionKey(encryptionKeyId);

        return "redirect:view";
    }

    @RequestMapping(value = "/config/security/getPublicKey")
    @ResponseBody
    public Map<String, Object> getPublicKey(HttpServletRequest request, boolean generateNewKey,
            YukonUserContext userContext) throws CryptoException {
        if (generateNewKey) {
            rsaKeyfileService.generateNewKeyPair();
        }

        Map<String, Object> publicKeyJsonObj = Maps.newHashMapWithExpectedSize(4);
        publicKeyJsonObj.put("isExpired", rsaKeyfileService.isKeyPairExpired());
        publicKeyJsonObj.put("doesExist", rsaKeyfileService.doesKeyPairExist());
        String expiration =
            dateFormattingService.format(rsaKeyfileService.getExpiration(), DateFormattingService.DateFormatEnum.DATE,
                userContext);
        expiration +=
            " "
                + dateFormattingService.format(rsaKeyfileService.getExpiration(),
                    DateFormattingService.DateFormatEnum.TIME, userContext);
        publicKeyJsonObj.put("expiration", expiration);
        String publicKey = null;
        if (rsaKeyfileService.doesKeyPairExist()) {
            publicKey = new String(Hex.encodeHex(rsaKeyfileService.getPublicKey().getEncoded()));
        }
        publicKeyJsonObj.put("publicKey", publicKey);

        return publicKeyJsonObj;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/config/security/getHoneywellPublicKey")
    @ResponseBody
    public Map<String, Object> getHoneywellPublicKey(YukonUserContext userContext) throws CryptoException {
        Map<String, Object> json = new HashMap<>();

        try {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            // GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024, new SecureRandom());

            java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            byte publicKeyEncoded[] = publicKey.getEncoded();
            byte[] publicKeyEncoded64 = Base64.encodeBase64(publicKeyEncoded);
            String publicStringKey = new String(publicKeyEncoded64);

            String encryptedpublicKeyValue = new String(Hex.encodeHex(encrypter.encrypt(publicStringKey.getBytes())));

            PrivateKey privateKey = keyPair.getPrivate();

            byte privateKeyEncoded[] = privateKey.getEncoded();
            byte[] privateKeyEncoded64 = Base64.encodeBase64(privateKeyEncoded);
            String privateStringKey = new String(privateKeyEncoded64);

            String encryptedPrivateKeyValue = new String(Hex.encodeHex(encrypter.encrypt(privateStringKey.getBytes())));

            encryptedRouteDao.saveOrUpdateEncryptionKey(encryptedPrivateKeyValue, encryptedpublicKeyValue, EncryptionKeyType.Honeywell, Instant.now());
            systemEventLogService.newPublicKeyGenerated(userContext.getYukonUser(), DREncryption.HONEYWELL);
            json.put("honeywellPublicKey", publicStringKey);

        } catch (Exception ex) {
            log.error(ex);
            json.put("hasError", true);
        }

        return json;

    }

    @RequestMapping(method = RequestMethod.POST, value = "/config/security/importKeyFile")
    public String importKeyFile(HttpServletRequest request, ModelMap model, FileImportBindingBean fileImportBindingBean,
                                BindingResult bindingResult, FlashScope flashScope, 
                                YukonUserContext userContext, HttpServletResponse resp) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            model.addAttribute("showDialog", "importKey");
            return view(request, model, new EncryptedRoute(), new EncryptionKey(), fileImportBindingBean, new HoneywellFileImportBindingBean(), flashScope, userContext);     
        }

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = mRequest.getFile(StringUtils.defaultIfEmpty(null, "keyFile"));
        fileImportBindingBean.setFile(dataFile);

        importFileValidator.validate(fileImportBindingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "security/importKey.jsp";
        }

        boolean success = handleUploadedFile(fileImportBindingBean, flashScope);
        if (!success) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "security/importKey.jsp";
        }

        return "redirect:view";
    }

    private boolean handleUploadedFile(FileImportBindingBean fileImportBindingBean, FlashScope flashScope) {
        boolean success = false;
        if (!rsaKeyfileService.doesKeyPairExist()) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".publicKey.noKeyExists"));
        } else if (rsaKeyfileService.isKeyPairExpired()) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".publicKey.keyExpired"));
        } else {
            try {                    
                File file = WebFileUtils.convertToTempFile(fileImportBindingBean.getFile(), "keyFile", "");
                byte[] bytes = rsaKeyfileService.decryptAndExtractData(file);

                char[] sharedPassword = CryptoUtils.getSharedPasskey();
                byte[] encryptedData = new AESPasswordBasedCrypto(sharedPassword).encrypt(bytes);
                String encryptedValue = new String(Hex.encodeHex(encryptedData));
                encryptedRouteDao.saveNewEncryptionKey(fileImportBindingBean.getName(), encryptedValue, null,
                    EncryptionKeyType.ExpresscomOneWay, Instant.now());
                flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".fileUploadSuccess",
                    fileImportBindingBean.getName()));
                success = true;
            } catch (IOException e) {
                log.error("File not found or other IO error", e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
            } catch (CryptoException e) {
                log.error("Unable to decrypt file", e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unableToDecryptFile"));
            } catch (JDOMException e) {
                log.error("Unable to properly read file", e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
            }
        }
        return success;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/config/security/importHoneywellKeyFile")
    public String importHoneywellKeyFile(HttpServletRequest request, ModelMap model,
            HoneywellFileImportBindingBean honeywellFileImportBindingBean, BindingResult bindingResult,
            FlashScope flashScope, YukonUserContext userContext) {

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = mRequest.getFile(StringUtils.defaultIfEmpty(null, "keyFile"));
        try {
            FileUploadUtils.validateKeyUploadFileType(dataFile);
        } catch (FileImportException e) {
            log.info("Import for Honeywell Key file failed.");
            systemEventLogService.keyFileImportFailed(userContext.getYukonUser(), DREncryption.HONEYWELL);
            flashScope.setError(new YukonMessageSourceResolvable(e.getMessage()));
            return "redirect:view";
        } catch (IOException e) {
            log.info("Import for Honeywell Key file failed.", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
            return "redirect:view";
        }
        honeywellFileImportBindingBean.setFile(dataFile);
        boolean success = handleHoneywellUploadedFile(honeywellFileImportBindingBean, flashScope);
        if (!success) {
            log.info("Import for Honeywell Key file failed.");
            systemEventLogService.keyFileImportFailed(userContext.getYukonUser(), DREncryption.HONEYWELL);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
        } else {
            systemEventLogService.importedKeyFile(userContext.getYukonUser(), DREncryption.HONEYWELL);
        }
        return "redirect:view";
    }

    private boolean handleHoneywellUploadedFile(HoneywellFileImportBindingBean fileImportBindingBean,
            FlashScope flashScope) {
        boolean success = false;

        try {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            byte[] privatekeyBytes = fileImportBindingBean.getFile().getBytes();

            SecurityKeyPair keyPair = rsaKeyfileService.getKeyPair(new String(privatekeyBytes));

            String encryptedPublicKey = new String(Hex.encodeHex(encrypter.encrypt(keyPair.getPublicKey().getBytes())));
            String encryptedPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(keyPair.getPrivateKey().getBytes())));

            encryptedRouteDao.saveOrUpdateEncryptionKey(encryptedPrivateKey, encryptedPublicKey, EncryptionKeyType.Honeywell, Instant.now());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".fileUploadSuccess", "Honeywell"));
            success = true;
        } catch (IOException e) {
            log.error("File not found or other IO error", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
        } catch (CryptoException e) {
            log.error("Unable to decrypt file", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unableToDecryptFile"));
        } catch (JDOMException e) {
            log.error("Unable to properly read file", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
        } catch (GeneralSecurityException e) {
            log.error("General security error", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
        }
        return success;
    }
    
    @RequestMapping(value = "/config/security/generateHoneywellCertificate", method = RequestMethod.POST)
    public String generateHoneywellCertificate(HttpServletResponse response, FlashScope flashScope,
            YukonUserContext userContext) {
        try {
            X509Certificate certificate = honeywellSecurityService.generateHoneywellCertificate();
            // create .crt file
            StringBuilder crtFilePath = new StringBuilder();
            crtFilePath.append(System.getenv("YUKON_BASE"));
            crtFilePath.append("\\Server\\Config\\Keys\\" + HONEYWELL_CERTIFICATE_FILE_NAME);
            String fileName = crtFilePath.toString();
            PEMWriter fileWriter = new PEMWriter(new FileWriter(fileName));
            fileWriter.writeObject(certificate);
            fileWriter.flush();
            fileWriter.close();

            // download the file
            OutputStream output = response.getOutputStream();
            InputStream input = new FileInputStream(new File(fileName));
            // set up the response
            response.setContentType(RESPONSE_CONTENT_TYPE_X509_CERTIFICATE);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + HONEYWELL_CERTIFICATE_FILE_NAME
                + "\"");

            // pull data from the file and push it to the browser
            IOUtils.copy(input, output);
            systemEventLogService.certificateGenerated(userContext.getYukonUser(), DREncryption.HONEYWELL);
            return null;
        } catch (IOException | CertificateGenerationFailedException exception) {
            systemEventLogService.certificateGenerationFailed(userContext.getYukonUser(), DREncryption.HONEYWELL);
            log.error("Error in creating .crt file ", exception);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".honeywellCertificate.downloadfailed"));
            return "redirect:view";
        }
    }
    
    @GetMapping(value = "/config/security/downloadEcobeeKey")
    @CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
    public void downloadEcobeeKey(HttpServletResponse response) {
        try (OutputStream stream = response.getOutputStream()) {
            String publicKey = ecobeeSecurityService.getEcobeePGPPublicKey();
            response.setContentType("text/plain");
            response.setHeader("Content-Type", "application/force-download");
            String fileName = ServletUtil.makeWindowsSafeFileName("ecobeePublicKey.txt");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            stream.write(publicKey.getBytes());
        } catch (Exception e) {
            log.error("Exception getting the ecobee Public Key", e);
        }
    }
    

    @GetMapping(value = "/config/security/generateEcobeeKey")
    @CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
    public @ResponseBody Map<String, Object> generateEcobeeKey(YukonUserContext userContext, FlashScope flashScope)
            throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        try {
            Instant keyCreationTime = ecobeeSecurityService.generateEcobeePGPKeyPair();
            String dateGenerated = dateFormattingService.format(keyCreationTime,
                    DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            json.put("ecobeeKeyGeneratedDateTime", dateGenerated);
        } catch (EcobeePGPException epe) {
            log.error("Exception while generating the PGP Public and Private Key ", epe);
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String errMsg = accessor.getMessage(baseKey + ".ecobeeKeyPair.generationFailed");
            json.put("ecobeeKeyGeneratedDateTime", errMsg);
        }
        return json;
    }

    @GetMapping(value = "/config/security/generateEcobeeZeusKey")
    @CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
    public @ResponseBody Map<String, Object> generateEcobeeZeusKey(YukonUserContext userContext, FlashScope flashScope)
            throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            ZeusEncryptionKey zeusEncryptionKey = ecobeeZeusSecurityService.generateZeusEncryptionKey();
            String dateGenerated = dateFormattingService.format(zeusEncryptionKey.getTimestamp(),
                    DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            json.put("ecobeeKeyZeusGeneratedDateTime", dateGenerated);
        } catch (RuntimeException e) {
            log.error("Exception while generating ecobee Public and Private Key ", e);
            String errMsg = accessor.getMessage(baseKey + ".ecobeeZeusKeyPair.generationFailed");
            json.put("ecobeeKeyZeusGeneratedDateTime", errMsg);
        }
        return json;
    }

    @GetMapping(value = "/config/security/registerEcobeeZeusKey")
    @CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
    public @ResponseBody Map<String, Object> registerEcobeeZeusKey(YukonUserContext userContext, FlashScope flashScope)
            throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        try {
            String privateKey = ecobeeZeusSecurityService.getZeusEncryptionKey().getPrivateKey();
            ecobeeZeusCommunicationService.createPushApiConfiguration(reportingUrl, privateKey);

            DateTime todayDate = new DateTime();
            String registeredDateTime = dateFormattingService.format(todayDate,
                    DateFormattingService.DateFormatEnum.DATEHM_12, userContext);

            String successMsg = accessor.getMessage(baseKey + ".ecobeeZeusKeyRegistered");
            json.put("ecobeeKeyZeusRegisteredDateTime", successMsg + registeredDateTime);
            json.put("success", true);
        } catch (Exception e) {
            log.error("Exception while generating ecobee Public and Private Key ", e);
            DateTime todayDate = new DateTime();
            String registeredDateTime = dateFormattingService.format(todayDate,
                    DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            String errMsg = accessor.getMessage(baseKey + ".ecobeeZeusKeyNotRegistered");
            json.put("ecobeeKeyZeusRegisteredDateTime", errMsg + registeredDateTime);
            json.put("success", false);
        }
        return json;
    }

    @GetMapping(value = "/config/security/checkRegistrationEcobeeZeusKey")
    @CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
    public @ResponseBody Map<String, Object> checkRegistrationEcobeeZeusKey(YukonUserContext userContext, FlashScope flashScope)
            throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        boolean isSuccess = false;
        String message = null;
        try {
            ZeusShowPushConfig showPushConfig = ecobeeZeusCommunicationService.showPushApiConfiguration();
            String privateKeySha1 = DigestUtils.sha1Hex(ecobeeZeusSecurityService.getZeusEncryptionKey().getPrivateKey());
            boolean checkUrl = showPushConfig.getReportingUrl().equals(reportingUrl);
            boolean checkPrivateKeySha1 = showPushConfig.getPrivateKey().equals(privateKeySha1);

            if (checkUrl && checkPrivateKeySha1) {
                isSuccess = true;
                message = accessor.getMessage(baseKey + ".checkRegistrationEcobeeZeusKey.success");
            } else if (!checkUrl) {
                message = accessor.getMessage(baseKey + ".checkRegistrationEcobeeZeusURL.error");
            } else if (!checkPrivateKeySha1) {
                message = accessor.getMessage(baseKey + ".checkRegistrationEcobeeZeusPublicKey.error");
            } else {
                message = accessor.getMessage(baseKey + ".checkRegistrationEcobeeZeusURLAndPublicKey.error");
            }

        } catch (Exception e) {
            log.error("Exception while checking registration with ecobee zeus", e);
            message = accessor.getMessage(baseKey + ".checkRegistrationEcobeeZeusKey.failed");
        }
        json.put("success", isSuccess);
        json.put("checkRegistrationMsg", message);
        return json;
    }

    @GetMapping(value = "/config/security/viewEcobeeZeusPublicKey")
    public @ResponseBody Map<String, Object> viewEcobeeZeusPublicKey(YukonUserContext userContext, FlashScope flashScope)
            throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            String privateKey = ecobeeZeusSecurityService.getZeusEncryptionKey().getPrivateKey();
            json.put("privateKey", privateKey);
            json.put("success", true);
        } catch (Exception e) {
            log.error("Exception getting the ecobee zeus Public Key", e);
            String message = accessor.getMessage(baseKey + ".viewEcobeeZeusKey.failed");
            json.put("privateKey", message);
            json.put("success", false);
        }
        return json;
    }

    @GetMapping(value = "/config/security/downloadItronKey")
    public void downloadItronKey(HttpServletResponse response) {
        try (OutputStream stream = response.getOutputStream()) {
            String publicKey = itronSecurityService.getItronPublicSshRsaKey();
            response.setContentType("text/plain");
            response.setHeader("Content-Type", "application/force-download");
            String fileName = ServletUtil.makeWindowsSafeFileName("itronPublicKey.txt");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            stream.write(publicKey.getBytes());
        } catch (Exception e) {
            log.error("Exception getting the itron Public Key", e);
        }
    }
    

    @GetMapping(value = "/config/security/generateItronKey")
    public @ResponseBody Map<String, Object> generateItronKey(YukonUserContext userContext, FlashScope flashScope) throws CryptoException {
        Map<String, Object> json = new HashMap<>();
        // The comment is optional, I think it will just stay as null
        String comment = null;
        try {
            Instant keyCreationTime = itronSecurityService.generateItronSshRsaPublicPrivateKeys(comment);
            String dateGenerated = dateFormattingService.format(keyCreationTime,
                DateFormattingService.DateFormatEnum.DATEHM_12, userContext);
            json.put("itronKeyGeneratedDateTime", dateGenerated);
        } catch (RuntimeException e) {
            log.error("Exception while generating Itron Public and Private Key ", e);
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String errMsg = accessor.getMessage(baseKey + ".itronKeyPair.generationFailed");
            json.put("itronKeyGeneratedDateTime", errMsg);
        } catch (Exception e) {
            log.warn("caught exception in generateItronKey", e);
        }
        return json;
    }

}