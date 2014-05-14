package com.cannontech.web.common.resources;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.core.image.model.YukonImage;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;
import com.cannontech.web.admin.theme.model.ThemePropertyType;

@Controller
public class YukonImageController {

    @Autowired private YukonImageDao imageDao;
    @Autowired private ThemeDao themeDao;
    @Autowired private ResourceLoader resourceLoader;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final static String keyBase = "yukon.common.imagePicker.";

    private String getImageExtension(LiteYukonImage image) {
        String name = image.getImageName();
        String ext = name.substring(name.lastIndexOf('.') + 1);
        return ext;
    }

    private String getImageContentType(LiteYukonImage image) {
        String ext = getImageExtension(image);
        String contentType = "image/" + (ext.equals("jpg") ? "jpeg" : ext);
        return contentType;
    }

    private LiteYukonImage getFullImage(int id) throws IOException, SQLException {
        LiteYukonImage fullImage = imageDao.getLiteYukonImage(id);
        if (fullImage == null) {
            fullImage = getDefaultImages(id);
        }
        return fullImage;
    }

    private enum Dimensions {
        TARGET_HEIGHT_PIXELS(200),
        TARGET_WIDTH_PIXELS(200);

        private Dimensions(final int dimension) {
            this.dimension = dimension;
        }

        private int dimension;

        public int getDimension() {
            return dimension;
        }
    };

    private final Dimension DefaultDimension = new Dimension(Dimensions.TARGET_WIDTH_PIXELS.getDimension(),
            Dimensions.TARGET_HEIGHT_PIXELS.getDimension());
    
    private BufferedImage scaleImage(BufferedImage src,
        int targetWidth, int targetHeight, Object interpolationHintValue, int imageType) {
        // Setup the rendering resources to match the source image's
        BufferedImage result = new BufferedImage(targetWidth, targetHeight, imageType);
        Graphics2D resultGraphics = result.createGraphics();

        // Scale the image to the new buffer using the specified rendering hint.
        resultGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                interpolationHintValue);
        resultGraphics.drawImage(src, 0, 0, targetWidth, targetHeight, null);

        // Just to be clean, explicitly dispose our temporary graphics object
        resultGraphics.dispose();

        // Return the scaled image to the caller.
        return result;
    }

    private Dimension getScaledDimensions(BufferedImage imageSrc, Dimension defaultDimension) {
        double currentHeight = (double) imageSrc.getHeight();
        double currentWidth = (double) imageSrc.getWidth();
        double requestedHeightScaling = (defaultDimension.getHeight() / currentHeight);
        double requestedWidthScaling = (defaultDimension.getWidth() / currentWidth);
        double actualScaling = Math.min(requestedHeightScaling, requestedWidthScaling);
        int targetHeight = (int) Math.round(currentHeight * actualScaling);
        int targetWidth = (int) Math.round(currentWidth * actualScaling);
        Dimension scaledDimension = new Dimension(targetWidth, targetHeight);
        return scaledDimension;
    }

    private LiteYukonImage getDefaultImages(int id) throws IOException, SQLException {
        LiteYukonImage image = null;
        if (id == YukonImage.DEFAULT_LOGO.getId()) {
            image = imageDao.add(YukonImage.DEFAULT_LOGO.getId(), 
                            YukonImage.DEFAULT_LOGO.getCategory(), 
                            YukonImage.DEFAULT_LOGO.getName(), 
                            resourceLoader.getResource(YukonImage.DEFAULT_LOGO.getPath()));
        } else if (id == YukonImage.DEFAULT_BACKGROUND.getId()) {
            image = imageDao.add(YukonImage.DEFAULT_BACKGROUND.getId(), 
                            YukonImage.DEFAULT_BACKGROUND.getCategory(), 
                            YukonImage.DEFAULT_BACKGROUND.getName(), 
                            resourceLoader.getResource(YukonImage.DEFAULT_BACKGROUND.getPath()));
        }
        return image;
    }

    @RequestMapping(value="/images/{id}/thumb", method=RequestMethod.GET)
    public void imageThumbnail(HttpServletResponse resp, @PathVariable int id) throws IOException, SQLException {

        LiteYukonImage image = getFullImage(id);
        String contentType = getImageContentType(image);
        resp.setContentType(contentType);
        ServletOutputStream out = resp.getOutputStream();
        ByteArrayInputStream imageBytes = new ByteArrayInputStream(image.getImageValue());
        BufferedImage scalableImage = ImageIO.read(imageBytes);
        // Don't bother scaling down below a certain threshold.
        if (scalableImage.getWidth() < Dimensions.TARGET_WIDTH_PIXELS.getDimension() &&
            scalableImage.getHeight() < Dimensions.TARGET_HEIGHT_PIXELS.getDimension()) {
            out.write(image.getImageValue());
            out.close();
            return;
        }
        // Target dimensions are smaller than one or more of the original dimensions
        int imageType = scalableImage.getType();
        Dimension scaledDimension = getScaledDimensions(scalableImage, DefaultDimension);
        BufferedImage thumbImg = scaleImage(
                scalableImage,
                scaledDimension.width,
                scaledDimension.height,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC,
                imageType);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String ext = getImageExtension(image);
        ImageIO.write(thumbImg, ext, baos);
        baos.flush();
        byte[] bytesImage = baos.toByteArray();
        baos.close();
        out.write(bytesImage);
        out.close();
    }

    @RequestMapping(value="/images/{id}", method=RequestMethod.GET)
    public void image(HttpServletResponse resp, @PathVariable int id) throws IOException, SQLException {

        LiteYukonImage image = getFullImage(id);
        String contentType = getImageContentType(image);
        resp.setContentType(contentType);
        ServletOutputStream out = resp.getOutputStream();
        out.write(image.getImageValue());
        out.close();
    }

    @RequestMapping(value="/images/{id}", method=RequestMethod.DELETE)
    public @ResponseBody Map<String, Object> delete(YukonUserContext userContext, @PathVariable int id) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            List<Theme> themes = themeDao.getThemes();
            Set<Integer> nonDeletableImages = new HashSet<>();
            for (Theme theme : themes) {
                Integer backgroundId = Integer.parseInt((String) theme.getProperties().get(ThemePropertyType.LOGIN_BACKGROUND));
                Integer logoId = Integer.parseInt((String) theme.getProperties().get(ThemePropertyType.LOGO));
                nonDeletableImages.add(backgroundId);
                nonDeletableImages.add(logoId);
            }
            if (YukonImage.isStandardImageId(id)) {
                throw new IllegalArgumentException(accessor.getMessage(keyBase + "delete.default"));
            } else if (nonDeletableImages.contains(id)) {
                throw new IllegalArgumentException(accessor.getMessage(keyBase + "delete.inuse"));
            }
            
            imageDao.delete(id);
            
        } catch (Exception e) {
            Map<String, Object> message = new HashMap<>();
            message.put("success", false);
            message.put("message", e.getMessage());
            return message;
        }
        
        Map<String, Object> message = new HashMap<>();
        message.put("success", true);
        return message;
    }

    @RequestMapping(value="/images", method=RequestMethod.POST)
    public void upload(HttpServletResponse resp, HttpServletRequest req, YukonUserContext userContext, @RequestParam(defaultValue="logos") String category) throws IOException {
        
        Map<String, Object> json = new HashMap<>();
        
        try {
            rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_SUPER_USER, userContext.getYukonUser());
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (!isMultipart) {
                throw new IllegalArgumentException("not multipart file");
            }
            
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) req;
            MultipartFile file = mRequest.getFile("file");
            if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
                throw new IllegalArgumentException("Blank file.");
            }
            
            if(!file.getContentType().startsWith("image")) {
                throw new IllegalArgumentException("Only image files are valid.");
            }
            InputStream inputStream = file.getInputStream();
            LiteYukonImage image = imageDao.add(category, file.getOriginalFilename(), new InputStreamResource(inputStream));
            Map<String, Object> imageStats = new HashMap<>(); 
            imageStats.put("id", image.getImageID());
            imageStats.put("name", image.getImageName());
            imageStats.put("category", image.getImageCategory());
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String size = accessor.getMessage("yukon.common.prefixedByteValue.kibi", image.getImageValue().length * .001);
            imageStats.put("size", size);
            json.put("image", imageStats);
            
            json.put("status", "success");
        } catch (Exception e) {
            json.put("status", "error");
            json.put("message", e.getMessage());
        }
        // content type must be text or html or IE will throw up a save/open dialog
        resp.setContentType("text/plain");
        String jsonString = JsonUtils.toJson(json);
        resp.getWriter().write(jsonString);
    }

}