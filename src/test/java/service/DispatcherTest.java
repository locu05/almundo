package service;

import model.Llamada;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

/**
 * Created by Locu on 07/05/2017.
 */
public class DispatcherTest {

    Dispatcher dispatcher;

    @Test(expected = Exception.class)
    public void dispatcherMoreThan10EmployeesTest() throws Exception {
        dispatcher = new Dispatcher(11,0,0);
    }

    @Test
    public void dispatcherWithOneOperatorOneCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithOneOperatorOneCallTest ---------->");
        dispatcher = new Dispatcher(1,0,0);
        Llamada call = new Llamada();
        dispatcher.dispatchCall(call);
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithOneOperatorOneCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithTwoOperatorTenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithTwoOperatorTenCallTest ---------->");
        dispatcher = new Dispatcher(2,0,0);
        for(int i = 0 ; i < 10 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithTwoOperatorTenCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithTwoOperatorOneSupervisorTenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithTwoOperatorOneSupervisorTenCallTest ---------->");
        dispatcher = new Dispatcher(2,1,0);
        for(int i = 0 ; i < 10 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithTwoOperatorOneSupervisorTenCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithTwoOperatorOneSupervisorOneDirectorTenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithTwoOperatorOneSupervisorOneDirectorTenCallTest ---------->");
        dispatcher = new Dispatcher(2,1,1);
        for(int i = 0 ; i < 10 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithTwoOperatorOneSupervisorOneDirectorTenCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithEightOperatorsOneSupervisorOneDirectorTenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithEightOperatorsOneSupervisorOneDirectorTenCallTest ---------->");
        dispatcher = new Dispatcher(8,1,1);
        for(int i = 0 ; i < 10 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithEightOperatorsOneSupervisorOneDirectorTenCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithEightOperatorsOneSupervisorOneDirectorElevenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithEightOperatorsOneSupervisorOneDirectorTenCallTest ---------->");
        dispatcher = new Dispatcher(8,1,1);
        for(int i = 0 ; i < 11 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithEightOperatorsOneSupervisorOneDirectorTenCallTest");
        System.out.println("");
    }

    @Test
    public void dispatcherWithOneOperatorElevenCallTest() throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("dispatcherWithOneOperatorOneCallTest ---------->");
        dispatcher = new Dispatcher(1,0,0);
        for(int i = 0 ; i < 11 ; i++) {
            Llamada call = new Llamada();
            dispatcher.dispatchCall(call);
        }
        waitDispatcher();
        System.out.println("------------------------------------------------");
        System.out.println("<-----------dispatcherWithOneOperatorOneCallTest");
        System.out.println("");
    }

    private void waitDispatcher() throws InterruptedException {
        Map<String, ThreadPoolTaskExecutor> executors = dispatcher.getExecutors();
        for(String key : executors.keySet()) {
           while(executors.get(key).getActiveCount() > 0) {
               Thread.sleep(1000);
           }
        }
        dispatcher.stop();
    }
}
