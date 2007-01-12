package com.amdswireless.messages.tx;

import java.util.ArrayList;

public class TxLoadShedMsg extends TxMsg {
    private static final long serialVersionUID = 1L;
    private ArrayList<LoadCommand> loadCommand = new ArrayList<LoadCommand>();

    public TxLoadShedMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x0E);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
            System.err.println("Error initializing TxLoadShedMsg:  "+ex );
        }
    }


    public String getMsgText() {
        super.updateMsg((short)0, 29 );
        return super.getMsgText();
    }


    public class LoadCommand {
        private int loadId;
        private int loadCommand;
        private int loadReserved = 0;

        public LoadCommand( int l, int c ) {
            loadId = l;
            loadCommand = c;
            loadReserved = 0;
        }

        public int getLoadId() {
            return this.loadId;
        }

        public int getLoadCommand() {
            return this.loadCommand;
        }

        public int getLoadReserved() {
            return loadReserved;
        }
    }


    public ArrayList<LoadCommand> getLc() {
        return loadCommand;
    }


    public void setLc(ArrayList<LoadCommand> lc) {
        this.loadCommand = lc;
    }

    public void addLoadCommand(int loadId, int loadCmd) {
        loadCommand.add(new LoadCommand(loadId, loadCmd));
    }

}
