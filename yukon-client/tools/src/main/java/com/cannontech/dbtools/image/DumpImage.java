/*
 * Created on Aug 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbtools.image;

import java.io.FileInputStream;

/**
 * @author aaron
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DumpImage {

	public static void main(String[] args) throws Exception {
		String filename = args[0];
			
		byte[] buf = new byte[4096];
		FileInputStream fin = new FileInputStream(filename);
	
		int r;
		while((r = fin.read(buf)) != -1) {
			for(int i = 0; i < r; i++) {
				int n = 0x000000FF & (int) buf[i];
				String num = Integer.toHexString(n);
				if(num.length() == 1) 
					num = "0" + num;
					
				System.out.print(num);
			}
		}
		
		fin.close();
	}
}
