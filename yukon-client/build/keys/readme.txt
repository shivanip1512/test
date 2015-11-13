key store and key password is cti123
the alias is "cannon" - OLD
the alias changed to "cannon4" - 20121031 - OLD
the alias changed to "cannon5" - 20151103 by SLN

The below steps were used the last time the certificate expired and needed updating (20121031). There is no guarantee
  that these will be the same instructions to following in 3 years (the next time it will expire)!
  
   
Step 1: Create a Keystore and Private Key
 - I ran this from C:\Yukon\Runtime\bin since I happened to have an installed version of Java there.
 - I generated all files to C:\Yukon\ directory, just so they were all in an easy to find location.
 - I used "cannon5" as your_alias_name this time around.
 - I used cti123 as the password 
 - Use these values to fill out questions (CN actually asks for your First and Last Name, use Company/Common Name instead):
    -- NOTE: Semantec/Verisign wants the O value to be searchable on yp.com and/or google maps
    CN=Eaton, OU=Unknown, O=Eaton, L=Minneapolis, ST=Minnesota, C=US


From command line:
   C:\Yukon\Runtime\bin>keytool -genkey -alias cannon5 -keystore C:\Yukon\jarKeystore -keyalg RSA -keysize 2048

 
Step 2: Generate A CSR
 - I incremented the name of the csr file (example jarCerReq#.csr where # is +1 from last time).
 - You can verify your CSR by editing the csr file in notepad and copying the data to here: 
     https://cryptoreport.websecurity.symantec.com/checker/views/csrCheck.jsp
  
 From command line:
   C:\Yukon\Runtime\bin>keytool -certreq -alias cannon5 -file C:\Yukon\jarCertReq5.csr -keystore C:\Yukon\jarKeystore


Step 3: Send CSR to Versign   
 - Had to create a purchase request for it.
 - Provide Sarah Porter (Hosted Systems) with the csr. She will generate a PR and submit to Verisign for ordering.

 
 ...wait for certificates from verisign....

 
Step 4: Save Certificates from Verisign
 - There are 2 certs needed for updating the keystore
  1. Intermediate Certificate (this used to be a Primary and a Secondary, however Mark from Symantec says its no longer necessary)
  2. our Certificate
 
 - Intermediate for Sun Java Code Signing can be copied from here: 
      https://knowledge.verisign.com/support/code-signing-support/index?page=content&id=AR1739
 - "our" certificate should be provided in the email back from verisign
 - Each certificate needs to be copied to a text file and saved with .cer extension.
  1. Saved as primary_int_ca.cer
  2. Saved as fromverisign5.cer
  
  NOTE: Symantec (Matt) only provides 1 intermediate certificate anymore. 
    Matt:  Certificate authorities used two intermediates for a long time.
    Matt:  This was to provide two options for roots, which gave wider compatibility.
    Matt:  One of the roots was a 1024 bit root, which is no longer in use.
    Matt:  So there is not really a need for the extra intermediate anymore.


Step 5: Import Certificates to keystore
 - Import each of the 2 certificates

Import Primary Intermediate Certificate - From command line:
   C:\Yukon\Runtime\bin>keytool -import -trustcacerts -alias primaryIntermediate -keystore C:\Yukon\jarKeystore -file C:\Yukon\primary_int_ca.cer

Import our Certificate - From command line:
   C:\Yukon\Runtime\bin>keytool -import -trustcacerts -alias cannon5 -keystore C:\Yukon\jarKeystore -file C:\Yukon\fromverisign5.cer

   
Step 6: Verify keystore has 3 entries
 - At this point, jarKeystore should have 4 entries in it: 
    Certificate [1] is your Code Signing ID (cannon5)
    Certificate [2] is the Primary Code Signing Intermediate CA
    Certificate [3] is the Root CA (the original one we used to generate the csr submitted to verisign)

From command line:
   C:\Yukon\Runtime\bin>keytool -list -v -keystore C:\Yukon\jarKeystore
   

Step 7: Commit files to subversion. See YUK-11279 for reference.
 - Change the alias (cannon4 to cannon5) in 3 places.
   yukon-client/build/build.properties
   yukon-build/build.xml - 2 instances
 - Commit the new jarKeystore to /yukon-client/build/keys/
 - Commit jarCertReq5.csr and fromverisign5.cer to /yukon-client/build/keys/ - this is just for versioning and saving.

Step 8: Need to update the Installer to use this certificate also, See YUK-12744 for reference.
 