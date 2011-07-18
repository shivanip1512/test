!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

TARGET=vc90.pdb

ALL:	$(TARGET)

copy:	$(TARGET)

vc90.pdb: $(PRECOMPILED)\vc90.pdb $(PRECOMPILED)\precompiled.pch
	@copy $(PRECOMPILED)\vc90.pdb .

clean:
	@-del $(TARGET)
