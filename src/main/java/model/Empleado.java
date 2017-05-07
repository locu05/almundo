package model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Locu on 07/05/2017.
 */
public class Empleado implements Runnable{

    private Object finishCall;

    public Empleado(Object finishCall) {
        this.finishCall = finishCall;
    }

    public void run() {
        System.out.println("Atendiendo llamada con empleado de tipo: " + this.getClass());
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10000 + 1));
            synchronized (finishCall) {
                finishCall.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
