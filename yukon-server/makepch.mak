!include $(COMPILEBASE)\global.inc

TARGET=vc120.pdb

ALL:	$(TARGET)

copy:	$(TARGET)

vc120.pdb: $(PRECOMPILED)\vc120.pdb $(PRECOMPILED)\precompiled.pch
	@copy $(PRECOMPILED)\vc120.pdb .

clean:
	@-del $(TARGET)
