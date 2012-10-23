package com.cannontech.web.support.logging;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.FileUtil;

/**
 * LogController acts as an abstract base
 * class for other logXxxControllers
 * Contains some common directories for locating
 * the local and remote log files.
 * @see internal getLogFile() method
 * @author dharrington
 */
public class LogController {

    //get the local log directory
    protected File localDir = new File(BootstrapUtils.getServerLogDir());
    
    /**
     * Gets the correct file from the request and returns it.  In the case that the file
     * is not in the log directory it will throw an IllegalArgumentException. 
     * 
     * @param request
     * @param root
     * @return
     * @throws IOException
     * @throws ServletRequestBindingException 
     */
    protected File getLogFile(HttpServletRequest request) throws IOException {
        
        String fileName = getFileNameParameter(request);
        File logFile = new File(localDir, fileName);
        
        logFile = logFile.getCanonicalFile();
        if (!logFile.exists() ||
            !isFileUnderRoot(localDir, logFile)) throw new IllegalArgumentException();
        
        return logFile;
    }
    
    protected String getRootlessFilePath(File file){
        if(isLogRoot(file)){
            return "/";
        } else {
            return getRootlessFilePath(file.getParentFile()) + file.getName() + "/";
        }
    }
    
    protected boolean isLogRoot(File file){
        if(file == null){
            return true;
        }
        return FileUtil.areFilesEqual(file, localDir);
    }
    
	protected String getFileNameParameter(HttpServletRequest request) {
		String fileName = ServletRequestUtils.getStringParameter(request, "file", "");
		return fileName;
	}

	/**
	 * Checks to see if the file is under root, but is not root.
	 * 
	 * @param root
	 * @param logFile
	 * @return
	 */
    private boolean isFileUnderRoot(File root, File logFile) {
		File file = logFile;
		do {
			if (file.equals(root)) {
				return true;
			}
			file = file.getParentFile();
		} while (file != null);
		
		return false;
	}

    public void setLocalDir(File localDir) {
        this.localDir = localDir;
    }
}
