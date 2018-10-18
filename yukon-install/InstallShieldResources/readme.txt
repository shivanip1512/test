This document outlines the procedure for updating the certificate InstallShield uses to sign the Yukon installer (Setup.exe).
InstallShield uses the same certificate we use for jar signing, and will need to be updated whenever that cert expires.

Note that most of these steps need to be performed as an administrator.

Install programs for formatting the certificate.
1. Install OpenSSL (https://slproweb.com/products/Win32OpenSSL.html)
2. Install KeyStore Explorer (http://keystore-explorer.sourceforge.net)

Extract private key file
1. Open yukon-client/build/keys/jarKeystore with Keystore Explorer. Password should be specified in the readme file in the same directory.
2. Right-click cannon6 entry. (This is the Key Pair entry, name may change in the future). 
3. Select export -> export private key.
4. Select PKCS #8
5. Uncheck encrypt. Check PEM. Specify export file name (Example: cannon6_PrivatKey.pkcs8).

Create .pfx file.
1. translate .p7b to .cer 
    Example: openssl pkcs7 -in C:\Yukon\fromverisign6.p7b -print_certs -out C:\Yukon\fromverisign6.cer
     
2. Add the openssl.exe to the path, or navigate to the location it was installed in CMD.
    In CMD: "openssl pkcs12 -export -inkey privateKeyFileName -in fromverisign6.cer - out pfxFileName.pfx"
    Example: openssl pkcs12 -export -inkey C:\Yukon\cannon6_PrivateKey.pkcs8 -in C:\Yukon\fromverisign6.cer -out C:\Yukon\com.cannontech.pfx

Use the pfx file in InstallShield
1. Move the pfx file to yukon-install/InstallshieldResources
2. If it's named com.cannontech.pfx (replacing the old file) it should just work, otherwise you'll have to modify the installer to find the new file.
3. In the InstallShield editor, navigate to the Media -> Releases view. Select Release 1, then the Signing tab.
4. Change the "Digital Certificate Setting" to point to the new file. Make sure to use the path variable <BUILD_ROOT_DIR>, rather than an absolute path.
5. No entry is needed for "Private Key File".
6. Specify the certificate password.
7. "Sign output files" = Setup.exe.
8. "Sign Files in Package" = No.
