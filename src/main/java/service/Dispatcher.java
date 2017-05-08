package service;

import model.Director;
import model.Llamada;
import model.Operador;
import model.Supervisor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class Dispatcher implements Runnable{

    private static String OPERATOR = "Operator";
    private static String SUPERVISOR = "Supervisor";
    private static String DIRECTOR = "Director";
    private static int MAX_SIZE = 10;

    private Map<String, ThreadPoolTaskExecutor> executors;
    private int operators;
    private int supervisors;
    private int directors;
    private Queue<Llamada> pendientCalls;
    private Object employeeAvailable;
    private ThreadPoolTaskExecutor dispatchThreadPool;
    private boolean isRunning;

    /**
     *
     * @param operators, the amount of operators
     * @param supervisors, the amount of supervisors
     * @param directors, the amount of directors
     * @throws Exception
     */
    public Dispatcher(int operators, int supervisors, int directors) throws Exception {
        if(operators + supervisors + directors > MAX_SIZE) {
            throw new Exception("No se puede crear un dispatcher con mas de " + MAX_SIZE + " empleados");
        }
        this.operators = operators;
        this.supervisors = supervisors;
        this.directors = directors;
        executors = new HashMap();
        pendientCalls = new ArrayBlockingQueue<Llamada>(10);
        employeeAvailable = new Object();
        isRunning = true;

        if(operators > 0)
            executors.put(OPERATOR, createThreadPoolTaskExecutor(operators));

        if(supervisors > 0)
            executors.put(SUPERVISOR, createThreadPoolTaskExecutor(supervisors));

        if(directors > 0)
            executors.put(DIRECTOR, createThreadPoolTaskExecutor(directors));

        dispatchThreadPool = new ThreadPoolTaskExecutor();
        dispatchThreadPool.initialize();
        dispatchThreadPool.execute(this);

    }

    /**
     * Create a ThreadPoolTaskExecutor with the given sizen
     * @param size
     * @return
     */
    private ThreadPoolTaskExecutor createThreadPoolTaskExecutor(int size) {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(size);
        threadPool.setMaxPoolSize(size);
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.initialize();
        return threadPool;
    }

    /**
     * Dispatch call to an Operator, Supervisor or Director
     * if they are available. Otherwise, put on hold the call if
     * there are space in the pendient call queue, if not, inform
     * that the call is not going to be handled
     * @param call
     */
    public void dispatchCall(Llamada call) {
        if(isOperatorAvailable()) {
            dispatchToOperator();
        } else if (isSupervisorAvailable()) {
            dispatchToSupervisor();
        } else if (isDirectorAvailable()) {
            dispatchToDirector();
        } else {
            if(canPutOnHold()) {
                System.out.println("No se encuentran empleados disponibles, poniendo musiquita para" +
                        "que la llamada aguarde un empleado disponible");
                pendientCalls.add(call);
            } else {
                System.out.println("El sistema no puede procesar mas de 10 llamadas simultaneas, descartando" +
                        " llamada, llame mas tarde");
            }
        }
    }

    /**
     * Wait until a Employee is available to dispatch the call
     */
    public void run() {

        while(true && isRunning) {
            synchronized (employeeAvailable) {
                try {
                    employeeAvailable.wait();
                    if(pendientCalls.peek()!=null){
                        dispatchCall(pendientCalls.poll());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stop the dispatcher
     */
    public void stop() {
        isRunning = false;
        synchronized (employeeAvailable) {
            employeeAvailable.notify();
        }
    }

    /**
     * Check if the size of the queue plus the current calls being handled
     * is less than 10
     * @return
     */
    private boolean canPutOnHold() {
        return getTotalCount() + pendientCalls.size() < 10;
    }

    /**
     * Execute a director thread
     */
    private void dispatchToDirector() {
        executors.get(DIRECTOR).execute(new Director(employeeAvailable));
    }

    /**
     * Execute a supervisor thread
     */
    private void dispatchToSupervisor() {
        executors.get(SUPERVISOR).execute(new Supervisor(employeeAvailable));
    }

    /**
     * Execute an operator thread
     */
    private void dispatchToOperator() {
        executors.get(OPERATOR).execute(new Operador(employeeAvailable));
    }

    /**
     * Check if there are a director available
     * @return
     */
    private boolean isDirectorAvailable() {
        return executors.get(DIRECTOR) != null && executors.get(DIRECTOR).getActiveCount() < directors;
    }

    /**
     * Chech if there are supervisor available
     * @return
     */
    private boolean isSupervisorAvailable() {
        return executors.get(SUPERVISOR) != null && executors.get(SUPERVISOR).getActiveCount() < supervisors;
    }

    /**
     * Check if there are an operator available
     * @return
     */
    private boolean isOperatorAvailable() {
        return executors.get(OPERATOR) != null && executors.get(OPERATOR).getActiveCount() < operators;
    }

    /**
     * Get the total count of current calls taking place
     * @return
     */
    private int getTotalCount() {
        int activeCount = 0;
        for(String key : executors.keySet()) {
            activeCount = activeCount + executors.get(key).getActiveCount();
        }
        return activeCount;
    }

    public ThreadPoolTaskExecutor getDispatchThreadPool() {
        return dispatchThreadPool;
    }

    public Map<String, ThreadPoolTaskExecutor> getExecutors() {
        return executors;
    }
}
