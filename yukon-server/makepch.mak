!include $(COMPILEBASE)\global.inc

TARGET=vc140.pdb

ALL:	$(TARGET)

copy:	$(TARGET)

vc140.pdb: $(PRECOMPILED)\vc140.pdb $(PRECOMPILED)\precompiled.pch
	@copy $(PRECOMPILED)\vc140.pdb .

clean:
	@-del $(TARGET)
