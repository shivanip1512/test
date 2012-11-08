key store and key password is cti123
the alias is "cannon" - OLD
the alias changed to "cannon4" - 20121031

The below steps were used the last time the certificate expired and needed updating (20121031). There is no guarantee
  that these will be the same instructions to following in 3 years (the next time it will expire)!
  
   
Step 1: Create a Keystore and Private Key
 - I ran this from C:\Yukon\Runtime\bin since I happened to have an installed version of Java there.
 - I generated all files to C:\Yukon\ directory, just so they were all in an easy to find location.
 - I used "cannon4" as your_alias_name this time around.
 - I used cti123 as the password 
 - Use these values to fill out questions:
    CN=Cannon Technologies, OU=Unknown, O=Cannon Technologies, L=Minneapolis, ST=Minnesota, C=US
 
From command line:
   C:\Yukon\Runtime\bin>keytool -genkey -alias cannon4 -keystore C:\Yukon\jarKeystore -keysize 2048

 
Step 2: Generate A CSR
 - I incremented the name of the csr file (example jarCerReq#.csr where # is +1 from last time).
 - You can verify your CRS by editing the csr file in notepad and copying the data to here: 
     https://ssl-tools.verisign.com/checker
  
 From command line:
   C:\Yukon\Runtime\bin>keytool -certreq -alias cannon4 -file C:\Yukon\jarCertReq4.csr -keystore C:\Yukon\jarKeystore


Step 3: Send CSR to Versign   
 - Had to create a purchase request for it.
 - Provide TJ (or IT) with the csr. He will generate the PR and submit to Verisign for ordering.
 
 
 ...wait for certificates from verisign....
 
Step 4: Save Certificates from Verisign
 - There are 3 certs needed for updating the keystore
  1. Primary Intermediate Certificate
  2. Secondary Intermediate Certificate
  3. our Certificate
 
 - Primary and Secondary for Sun Java Code Signing can be copied from here: 
      https://knowledge.verisign.com/support/code-signing-support/index?page=content&id=AR1739
 - "our" certificate should be provided in the email back from verisign
 - Each certificate needs to be copied to a text file and saved with .cer extension.
  1. Saved as primary_int_ca.cer
  2. Saved as secondary_int_ca.cer
  3. Saved as fromverisign4.cer


Step 5: Import Certificates to keystore
 - Import each of the 3 certificates

Import Primary Intermediate Certificate - From command line:
   C:\Yukon\Runtime\bin>keytool -import -trustcacerts -alias primaryIntermediate -keystore C:\Yukon\jarKeystore -file C:\Yukon\primary_int_ca.cer

Import Secondary Intermediate Certificate - From command line:
   C:\Yukon\Runtime\bin>keytool -import -trustcacerts -alias secondaryIntermediate -keystore C:\Yukon\jarKeystore -file C:\Yukon\secondary_int_ca.cer

Import our Certificate - From command line:
   C:\Yukon\Runtime\bin>keytool -import -trustcacerts -alias cannon4 -keystore C:\Yukon\jarKeystore -file C:\Yukon\fromverisign4.cer

   
Step 6: Verify keystore has 4 entries
 - At this point, jarKeystore should have 4 entries in it: 
    Certificate [1] is your Code Signing ID (cannon4)
    Certificate [2] is the Secondary Code Signing Intermediate CA
    Certificate [3] is the Primary Code Signing Intermediate CA
    Certificate [4] is the Root CA (the original one we used to generate the csr submitted to verisign)

From command line:
   C:\Yukon\Runtime\bin>keytool -list -v -keystore C:\Yukon\jarKeystore
   

Step 7: Commit files to subversion. See YUK-11279 for reference.
 - Change the alias in 2 places.
   yukon-client/build/build.properties
   yukon-build/build.xml
 - Commit the new jarKeystore to /yukon-client/build/keys/
 - Commit jarCertReq4.csr and fromverisign4.cer to /yukon-client/build/keys/ - this is just for versioning and saving.


 