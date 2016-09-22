package com.cannontech.tools.dispatch;

import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.util.ClientConnectionFactory;

public class VGConnPounder {

    String host;

    int threadNum;

    int howOftenToConn;

    int howLongToConn;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 4) {
               System.out.println("Usage: dispatch_host threads how_often how_long");
               System.exit(-1);
        }

        VGConnPounder cp = new VGConnPounder();
        cp.host = args[0];
        cp.threadNum = Integer.parseInt(args[1]);
        cp.howOftenToConn = Integer.parseInt(args[2]);
        cp.howLongToConn = Integer.parseInt(args[3]);

        System.out.println("starting " + cp.threadNum + " connection threads");
        for (int i = 0; i < cp.threadNum; i++) {
            PounderThr pt = cp.new PounderThr();
            Thread t = new Thread(pt);
            t.start();

        }
    }

    class PounderThr implements Runnable {
        DispatchClientConnection conn;

        public void run() {
            try {
                System.out.println(Thread.currentThread().getId()
                        + ": Randominzing start");
                Thread.sleep((long) (Math.random() * 5000));

                while (true) {
                    System.out.println(Thread.currentThread().getId()
                            + ": Trying to connect to dispatch @" + VGConnPounder.this.host);
                    connect();

                    System.out.println(Thread.currentThread().getId()
                            + ": Will stay connected for "
                            + VGConnPounder.this.howLongToConn);
                    Thread.sleep(VGConnPounder.this.howLongToConn);
                    
                    System.out.println(Thread.currentThread().getId() + ": disconnecting");
                    conn.disconnect();

                    System.out.println(Thread.currentThread().getId()
                            + ": Will connect again in "
                            + VGConnPounder.this.howOftenToConn);
                    Thread.sleep(VGConnPounder.this.howOftenToConn);
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        void connect() {


            conn = ClientConnectionFactory.getInstance().createDispatchConn();

            com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
            reg.setAppName("VGPounder " + Thread.currentThread().getId());

            reg.setAppIsUnique(0);
            reg.setAppExpirationDelay(5000);
            PointRegistration pReg = new PointRegistration();
            pReg.setRegFlags(PointRegistration.REG_ALL_POINTS
                    | PointRegistration.REG_ALARMS);

            com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
            multi.getVector().addElement(reg);
            multi.getVector().addElement(pReg);
            conn.setRegistrationMsg(multi);
            conn.setAutoReconnect(true);

            conn.connectWithoutWait();
        }
    }
}
