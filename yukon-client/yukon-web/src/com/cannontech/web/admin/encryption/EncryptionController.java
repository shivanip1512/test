package com.cannontech.web.admin.encryption;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.RSAKeyfileService;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
@RequestMapping("/encryption/*")
public class EncryptionController {
    @Autowired private EncryptedRouteDao encryptedRouteDao;
    @Autowired private RSAKeyfileService rsaKeyfileService;
    @Autowired private DateFormattingService dateFormattingService;

    private static final int KEYNAME_MAX_LENGTH = 50;
    private static final int KEYHEX_DIGITS_LENGTH = 32;
    private static final String HEX_STRING_PATTERN = "^[0-9A-Fa-f]*$";
    
    private Logger log = YukonLogManager.getLogger(EncryptionController.class);

    private final static String baseKey = "yukon.web.modules.adminSetup.encryption";

    private static class FileImportBindingBean {
        private String name = null;
        private MultipartFile file = null;
        public void setName(String name) { this.name = name; }
        public String getName() { return name; }
        public void setFile(MultipartFile file) { this.file = file; }
        public MultipartFile getFile() { return file; }
    }

    private Validator addKeyValidator =
        new SimpleValidator<EncryptionKey>(EncryptionKey.class) {
        @Override
        public void doValidation(EncryptionKey encryptionKey, Errors errors) {

            if (StringUtils.isNotEmpty(encryptionKey.getValue())) {
                if (encryptionKey.getValue().length() != KEYHEX_DIGITS_LENGTH) {
                    Integer [] length = {KEYHEX_DIGITS_LENGTH,encryptionKey.getValue().length()};
                    errors.rejectValue("value", baseKey + ".errorMsg.exactLength",length,"");
                }

                Pattern lengthPattern = Pattern.compile(HEX_STRING_PATTERN);
                YukonValidationUtils.regexCheck(errors, "value", encryptionKey.getValue(),
                                                lengthPattern, baseKey + ".errorMsg.hexidecimal");
            } else {
                errors.rejectValue("value", baseKey + ".errorMsg.empty");
            }
            if (StringUtils.isNotEmpty(encryptionKey.getName())) {
                for (EncryptionKey e: encryptedRouteDao.getEncryptionKeys()) {
                    if (e.getName().equals(encryptionKey.getName())) {
                        errors.rejectValue("name", baseKey + ".errorMsg.previouslyUsedKey");
                        break;
                    }
                }
                if (encryptionKey.getName().length() > KEYNAME_MAX_LENGTH) {
                    Integer [] length = {KEYNAME_MAX_LENGTH,encryptionKey.getName().length()};
                    errors.rejectValue("name", baseKey + ".errorMsg.length",length,"");
                }
            } else {
                errors.rejectValue("name", baseKey + ".errorMsg.empty");
            }
        }
    };

    private Validator importFileValidator =
        new SimpleValidator<FileImportBindingBean>(FileImportBindingBean.class) {
        @Override
        public void doValidation(FileImportBindingBean fileImportBindingBean, Errors errors) {

            if (fileImportBindingBean.getName().length() > KEYNAME_MAX_LENGTH) {
                Object [] args = {KEYNAME_MAX_LENGTH};
                errors.rejectValue("name",baseKey + ".errorMsg.nameLength",args,"");
            } else if (StringUtils.isEmpty(fileImportBindingBean.getName())) {
                errors.rejectValue("name",baseKey + ".errorMsg.empty");
            } else { 
                for (EncryptionKey e: encryptedRouteDao.getEncryptionKeys()) {
                    if (e.getName().equals(fileImportBindingBean.getName())) {
                        errors.rejectValue("name",baseKey + ".errorMsg.previouslyUsedKey");
                        break;
                    }
                }
            }

            try {
                if (fileImportBindingBean.getFile() == null 
                        || StringUtils.isBlank(fileImportBindingBean.getFile().getOriginalFilename())) {
                    errors.rejectValue("file",baseKey + ".fileUploadError.noFile");
                } else if(fileImportBindingBean.getFile().getInputStream().available() <= 0) {
                    errors.rejectValue("file",baseKey + ".fileUploadError.emptyFile");
                } 
            } catch (IOException e) {
                errors.rejectValue("file",baseKey + ".fileUploadError.noFile");
            }
        }
    };

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model, 
                       FlashScope flashScope, YukonUserContext userContext) {
        model.addAttribute("showDialog", "");
        return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(), flashScope, userContext);
    }

    public String view(HttpServletRequest request, ModelMap model, 
                       EncryptedRoute encryptedRoute, EncryptionKey encryptionKey, 
                       FileImportBindingBean fileImportBindingBean, FlashScope flashScope,
                       YukonUserContext userContext) {

        model.addAttribute("encryptedRoute", encryptedRoute);
        model.addAttribute("encryptedRoutes", encryptedRouteDao.getAllEncryptedRoutes());

        model.addAttribute("encryptionKey", encryptionKey);
        List<EncryptionKey> encryptionKeys = encryptedRouteDao.getEncryptionKeys();
        
        boolean invalidKeyFound = false;
        try {
            char [] sharedPasskey = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(sharedPasskey);
            // Check validity of each key. Making sure it decrypts with no exceptions
            boolean isValid;
            for (EncryptionKey key : encryptionKeys) {
                try {
                    isValid = aes.isAuthentic(Hex.decodeHex(key.getValue().toCharArray()));
                } catch (DecoderException e) {
                    isValid = false;
                }
                key.setIsValid(isValid);
                invalidKeyFound = invalidKeyFound || !isValid;
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

        model.addAttribute("encryptionKeys",encryptionKeys);

        model.addAttribute("fileImportBindingBean", fileImportBindingBean);

        return "encryption/view.jsp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "saveNewKey")
    public String saveNewKey(HttpServletRequest request, ModelMap model,
                             EncryptionKey encryptionKey, BindingResult bindingResult,
                             FlashScope flashScope, YukonUserContext userContext) {

        addKeyValidator.validate(encryptionKey, bindingResult);

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            model.addAttribute("showDialog", "addKey");

            return view(request, model, new EncryptedRoute(), encryptionKey, new FileImportBindingBean(), flashScope, userContext);
        }

        try {
            char[] password = CryptoUtils.getSharedPasskey();

            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            byte [] keyBytes = Hex.decodeHex(encryptionKey.getValue().toCharArray());
            String encryptedValue = new String(Hex.encodeHex(encrypter.encrypt(keyBytes)));

            encryptedRouteDao.saveNewEncyptionKey(encryptionKey.getName(),encryptedValue);
        } catch (CryptoException e) {
            log.warn("caught exception in saveNewKey", e);
        } catch (Exception e) {
            log.warn("caught exception in saveNewKey", e);
        } 

        return "redirect:view";
    }

    @RequestMapping("remove")
    public String remove(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                         BindingResult bindingResult, FlashScope flashScope,
                         YukonUserContext userContext) {

        List<EncryptedRoute> routes = encryptedRouteDao.getAllEncryptedRoutes();
        for (EncryptedRoute route : routes) {
            if (route.getPaobjectId().equals(encryptedRoute.getPaobjectId())) {
                if (!route.isEncrypted()) {
                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".removeError.notAdded"));
                    return view(request, model, new EncryptedRoute(), new EncryptionKey(),new FileImportBindingBean() ,flashScope , userContext);
                } else if(!encryptedRoute.getEncryptionKeyId().equals(route.getEncryptionKeyId())) {
                    // Trying to remove a route which showed it was added using a key. But actually added using different key.
                    // So we want to inform them they are not disabling the key they thought.
                    Object [] args = {encryptedRoute.getEncryptionKeyName(),route.getEncryptionKeyName()};
                    flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".removeError.staleData",args));
                    return view(request, model, new EncryptedRoute(), new EncryptionKey(),new FileImportBindingBean(),flashScope , userContext);
                }
            }
        }

        encryptedRouteDao.deleteEncryptedRoute(encryptedRoute);
        return "redirect:view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "add")
    public String getRedirect(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                              BindingResult bindingResult, FlashScope flashScope) {
        return "redirect:view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "add")
    public String add(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                      BindingResult bindingResult, FlashScope flashScope,
                      YukonUserContext userContext) {
        List<EncryptedRoute> routes = encryptedRouteDao.getAllEncryptedRoutes();
        for (EncryptedRoute route : routes) {
            if (route.isEncrypted() && route.getPaobjectId().equals(encryptedRoute.getPaobjectId())) {
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".addError.alreadyAdded", route.getEncryptionKeyName()));
                return view(request, model, new EncryptedRoute(), new EncryptionKey(), new FileImportBindingBean(), flashScope, userContext);
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
            return view(request, model, new EncryptedRoute(), new EncryptionKey(),new FileImportBindingBean(), flashScope, userContext);
        }

        encryptedRouteDao.addEncryptedRoute(encryptedRoute);
        return "redirect:view";
    }


    @RequestMapping(method = RequestMethod.POST, value = "deleteKey")
    public String deleteKey(HttpServletRequest request, ModelMap model, 
                            FlashScope flashScope, Integer encryptionKeyId,
                            YukonUserContext userContext) {

        // Check to make sure key isn't being used before we delete it
        for (EncryptedRoute e : encryptedRouteDao.getAllEncryptedRoutes()) {
            if (e.getEncryptionKeyId()!= null && e.getEncryptionKeyId().equals(encryptionKeyId)) {
                Object[] args = {e.getEncryptionKeyName(),e.getPaoName()};
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".errorMsg.keyInUse",args));
                return view(request, model, new EncryptedRoute(), new EncryptionKey(),new FileImportBindingBean(), flashScope, userContext);
            }
        }

        encryptedRouteDao.deleteEncyptionKey(encryptionKeyId);

        return "redirect:view";
    }

    @RequestMapping(value = "getPublicKey")
    @ResponseBody 
    public String getPublicKey(HttpServletRequest request, ModelMap model, FlashScope flashScope, YukonUserContext userContext) 
                                       throws CryptoException, ServletRequestBindingException {

        boolean generateNewKey = ServletRequestUtils.getBooleanParameter(request, "generateNewKey");

        if(generateNewKey) {
            rsaKeyfileService.generateNewKeyPair();
        }

        JSONObject publicKeyJsonObj = new JSONObject();
        publicKeyJsonObj.put("isExpired", rsaKeyfileService.isKeyPairExpired());
        publicKeyJsonObj.put("doesExist", rsaKeyfileService.doesKeyPairExist());
        String expiration = dateFormattingService.format(rsaKeyfileService.getExpiration(),
                                                         DateFormattingService.DateFormatEnum.DATE, userContext);
        expiration += " " +  dateFormattingService.format(rsaKeyfileService.getExpiration(),
                                                    DateFormattingService.DateFormatEnum.TIME, userContext);
        publicKeyJsonObj.put("expiration", expiration);
        String publicKey = null;
        if (rsaKeyfileService.doesKeyPairExist()) {
            publicKey = new String(Hex.encodeHex(rsaKeyfileService.getPublicKey().getEncoded()));
        }
        publicKeyJsonObj.put("publicKey",publicKey);

        return publicKeyJsonObj.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "importKeyFile")
    public String importKeyFile(HttpServletRequest request, ModelMap model, FileImportBindingBean fileImportBindingBean,
                                BindingResult bindingResult, FlashScope flashScope, 
                                YukonUserContext userContext) {

        if (!ServletFileUpload.isMultipartContent(request)) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.noFile"));
            model.addAttribute("showDialog", "importKey");
            return view(request, model, new EncryptedRoute(), new EncryptionKey(), fileImportBindingBean,flashScope, userContext);     
        } else {

            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile dataFile = mRequest.getFile(StringUtils.defaultIfEmpty(null, "keyFile"));
            fileImportBindingBean.setFile(dataFile);

            importFileValidator.validate(fileImportBindingBean, bindingResult);

            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

                model.addAttribute("showDialog", "importKey");

                return view(request, model, new EncryptedRoute(), new EncryptionKey() ,fileImportBindingBean, flashScope, userContext);
            } else {
                boolean success = handleUploadedFile(fileImportBindingBean, flashScope);
                if (!success) {
                    model.addAttribute("showDialog", "importKey");
                    return view(request, model, new EncryptedRoute(), new EncryptionKey(),fileImportBindingBean, flashScope, userContext); 
                }
                return "redirect:view";
            }
        }
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
                encryptedRouteDao.saveNewEncyptionKey(fileImportBindingBean.getName(),encryptedValue);
                flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".fileUploadSuccess", fileImportBindingBean.getName()));
                success = true;
            } catch (IOException e) {
                //Unable to upload File
                log.warn("caught exception in handleUploadedFile", e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
            } catch (CryptoException e) {
                // unable to decrypt file
                log.warn("caught exception in handleUploadedFile", e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unableToDecryptFile"));
            } catch (Exception e) {
                log.warn("Caught Excpetion handling upload file.",e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".fileUploadError.unknownError"));
            }
        }
        return success;
    }
}
