When a web installation is done for a customer, this folder may be
used to store their customer specific logos, gifs, styleSheets, etc.

This folder should not be deleted during Yukon upgrades.  Any folder 
with a customer name is schedule to be dumped in future (2.42.15+ 3.0.4+)
revisions.

Customer specific data can not be automatically delivered with each CD,
(or rather packaged in the yukon-web.war file), the upgrade(r)/install(er)
will need to build a specific CD for a web customer and include all of the
customer specific files.  Any customer files that CTI has are stored in 
CVS project customer-config.

SN-CTI-20040310
