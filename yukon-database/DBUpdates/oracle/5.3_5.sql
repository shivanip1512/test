/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 
      
/* Start YUK-10174 */ 
DELETE FROM YukonServices 
WHERE ServiceID = 18
  AND ServiceName = 'CymDISTMessageListener';
/* End YUK-10174 */

/* Start YUK-10287 */ 
INSERT INTO DeviceTypeCommand VALUES (-820, -1, 'MCT-420CLD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-821, -81, 'MCT-420CLD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-822, -3, 'MCT-420CLD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-823, -6, 'MCT-420CLD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-824, -34, 'MCT-420CLD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-825, -82, 'MCT-420CLD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-826, -18, 'MCT-420CLD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-827, -19, 'MCT-420CLD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-828, -83, 'MCT-420CLD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-829, -84, 'MCT-420CLD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-830, -11, 'MCT-420CLD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-831, -12, 'MCT-420CLD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-832, -13, 'MCT-420CLD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-833, -106, 'MCT-420CLD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-834, -107, 'MCT-420CLD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-835, -108, 'MCT-420CLD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-836, -111, 'MCT-420CLD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-837, -114, 'MCT-420CLD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-838, -15, 'MCT-420CLD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-839, -130, 'MCT-420CLD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-840, -131, 'MCT-420CLD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-841, -132, 'MCT-420CLD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-842, -136, 'MCT-420CLD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-843, -137, 'MCT-420CLD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-844, -138, 'MCT-420CLD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-845, -139, 'MCT-420CLD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-846, -140, 'MCT-420CLD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-847, -141, 'MCT-420CLD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-848, -142, 'MCT-420CLD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-849, -154, 'MCT-420CLD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-850, -155, 'MCT-420CLD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-851, -156, 'MCT-420CLD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-852, -157, 'MCT-420CLD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-853, -169, 'MCT-420CLD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-854, -170, 'MCT-420CLD', 35, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-860, -1, 'MCT-420CL', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-861, -81, 'MCT-420CL', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-862, -3, 'MCT-420CL', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-863, -6, 'MCT-420CL', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-864, -34, 'MCT-420CL', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-865, -82, 'MCT-420CL', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-866, -18, 'MCT-420CL', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-867, -19, 'MCT-420CL', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-868, -83, 'MCT-420CL', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-869, -84, 'MCT-420CL', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-870, -106, 'MCT-420CL', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-871, -107, 'MCT-420CL', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-872, -108, 'MCT-420CL', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-873, -111, 'MCT-420CL', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-874, -114, 'MCT-420CL', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-875, -15, 'MCT-420CL', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-876, -130, 'MCT-420CL', 17, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-877, -131, 'MCT-420CL', 18, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-878, -132, 'MCT-420CL', 19, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-879, -136, 'MCT-420CL', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-880, -137, 'MCT-420CL', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-881, -138, 'MCT-420CL', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-882, -139, 'MCT-420CL', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-883, -140, 'MCT-420CL', 24, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-884, -141, 'MCT-420CL', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-885, -142, 'MCT-420CL', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-886, -154, 'MCT-420CL', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-887, -155, 'MCT-420CL', 28, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-888, -156, 'MCT-420CL', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-889, -157, 'MCT-420CL', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-890, -169, 'MCT-420CL', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-891, -170, 'MCT-420CL', 32, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-900, -1, 'MCT-420FLD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-901, -81, 'MCT-420FLD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-902, -3, 'MCT-420FLD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-903, -6, 'MCT-420FLD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-904, -34, 'MCT-420FLD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-905, -82, 'MCT-420FLD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-906, -18, 'MCT-420FLD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-907, -19, 'MCT-420FLD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-908, -83, 'MCT-420FLD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-909, -84, 'MCT-420FLD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-910, -11, 'MCT-420FLD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-911, -12, 'MCT-420FLD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-912, -13, 'MCT-420FLD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-913, -106, 'MCT-420FLD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-914, -107, 'MCT-420FLD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-915, -108, 'MCT-420FLD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-916, -111, 'MCT-420FLD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-917, -114, 'MCT-420FLD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-918, -15, 'MCT-420FLD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-919, -130, 'MCT-420FLD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-920, -131, 'MCT-420FLD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-921, -132, 'MCT-420FLD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-922, -136, 'MCT-420FLD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-923, -137, 'MCT-420FLD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-924, -138, 'MCT-420FLD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-925, -139, 'MCT-420FLD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-926, -140, 'MCT-420FLD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-927, -141, 'MCT-420FLD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-928, -142, 'MCT-420FLD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-929, -154, 'MCT-420FLD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-930, -155, 'MCT-420FLD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-931, -156, 'MCT-420FLD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-932, -157, 'MCT-420FLD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-933, -169, 'MCT-420FLD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-934, -170, 'MCT-420FLD', 35, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-940, -1, 'MCT-420FL', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-941, -81, 'MCT-420FL', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-942, -3, 'MCT-420FL', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-943, -6, 'MCT-420FL', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-944, -34, 'MCT-420FL', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-945, -82, 'MCT-420FL', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-946, -18, 'MCT-420FL', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-947, -19, 'MCT-420FL', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-948, -83, 'MCT-420FL', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-949, -84, 'MCT-420FL', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-950, -11, 'MCT-420FL', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-951, -12, 'MCT-420FL', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-952, -13, 'MCT-420FL', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-953, -106, 'MCT-420FL', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-954, -107, 'MCT-420FL', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-955, -108, 'MCT-420FL', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-956, -111, 'MCT-420FL', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-957, -114, 'MCT-420FL', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-958, -15, 'MCT-420FL', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-959, -130, 'MCT-420FL', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-960, -131, 'MCT-420FL', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-961, -132, 'MCT-420FL', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-962, -136, 'MCT-420FL', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-963, -137, 'MCT-420FL', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-964, -138, 'MCT-420FL', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-965, -139, 'MCT-420FL', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-966, -140, 'MCT-420FL', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-967, -141, 'MCT-420FL', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-968, -142, 'MCT-420FL', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-969, -154, 'MCT-420FL', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-970, -155, 'MCT-420FL', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-971, -156, 'MCT-420FL', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-972, -157, 'MCT-420FL', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-973, -169, 'MCT-420FL', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-974, -170, 'MCT-420FL', 35, 'Y', -1);
/*  End YUK-10287  */

/* Start YUK-10333 */
DELETE FROM YukonListEntry 
WHERE ListId = 1005 
AND EntryId IN (1060, 1061, 1063);
/*  End  YUK-10333 */

/* Start YUK-10393 */
INSERT INTO YukonRoleProperty VALUES (-21311,-213,'Archived Data Analysis','true','Controls access to Archived Data Analysis collection action.');
/*  End  YUK-10393 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
