package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.cleardata.CleanManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.ConnectionProvider;
import com.smikhalev.sqlserverutils.exportdata.ExportManager;
import com.smikhalev.sqlserverutils.importdata.ImportManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class Program {

    public static final String IMPORT = "import";
    public static final String EXPORT = "export";
    public static final String CLEAR = "clear";

    @Autowired
    private ConnectionProvider connectionProvider;

    @Autowired
    private ImportManager importManager;

    @Autowired
    private ExportManager exportManager;

    @Autowired
    private CleanManager cleanManager;

    private static final int STATUS_THREAD_SLEEP_TIME = 1 * 1000;

    public static void main(String [] args) {
        if (!validateArgs(args))
            return;

        Program program = injectDependencies();
        substituteDefaultConnectionString(args[1], program);

        try {
            processCommand(program, args);
        }
        catch (ApplicationException | InterruptedException e) {
            System.out.println("Exception:" + e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }

    private static boolean validateArgs(String[] args) {
        if (args.length < 2 && args.length > 3) {
            return fail();
        }

        if (args.length == 2 && !args[0].equals(CLEAR)) {
            return fail();
        }

        if (args.length == 3 && !(args[0].equals(IMPORT)) || args[0].equals(EXPORT)) {
            return fail();
        }

        return true;
    }


    private static boolean fail() {
        System.out.println("There is not enough arguments.");
        System.out.println("dpump command connection-string [file]");
        return false;
    }

    private static void processCommand(Program program, String[] args) throws InterruptedException {
        switch (args[0]){
            case IMPORT:
                System.out.println("Import started.");
                startStatusThread(program.importManager);
                program.importManager.doImport(args[2]);
                break;
            case EXPORT:
                System.out.println("Export started.");
                startStatusThread(program.exportManager);
                program.exportManager.doExport(args[2]);
                break;
            case CLEAR:
                System.out.println("Clear started.");
                program.cleanManager.doClear();
                System.out.println("Clear finished.");
                break;
            default:
                System.out.println("Command is not supported.");
        }
    }

    public static void startStatusThread(final BaseWorkerManager workerManager) {
        Runnable statusThreadRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessResult result = workerManager.getCurrentStatus();
                    int sleepTimes = 0;

                    while(!result.isStarted()) {
                        Thread.sleep(STATUS_THREAD_SLEEP_TIME);
                        sleepTimes++;
                        result = workerManager.getCurrentStatus();
                        System.out.println(sleepTimes + ") Starting" );
                    }

                    while(!result.isFinished()) {
                        Thread.sleep(STATUS_THREAD_SLEEP_TIME);
                        sleepTimes++;
                        result = workerManager.getCurrentStatus();

                        System.out.println(sleepTimes + ") " + result.getMessage());
                    }
                } catch (InterruptedException e) {
                    throw new ApplicationException(e.getMessage(), e.getCause());
                }
            }
        };

        Thread statusThread = new Thread(statusThreadRunnable);
        statusThread.start();
    }

    private static void substituteDefaultConnectionString(String connectionString, Program program) {
        program.connectionProvider.setConnectionString(connectionString);
    }

    private static Program injectDependencies() {
        Program program = new Program();
        ApplicationContextLoader contextLoader = new ApplicationContextLoader();
        contextLoader.load(program, "spring-config.xml");
        return program;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void setImportManager(ImportManager importManager) {
        this.importManager = importManager;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public void setCleanManager(CleanManager cleanManager) {
        this.cleanManager = cleanManager;
    }
}
