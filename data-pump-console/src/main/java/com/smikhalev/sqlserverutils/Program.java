package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.cleardata.CleanManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.ConnectionProvider;
import com.smikhalev.sqlserverutils.exportdata.ExportManager;
import com.smikhalev.sqlserverutils.importdata.ImportManager;
import org.springframework.beans.factory.annotation.Autowired;

public class Program {

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
        if (args.length < 2) {
            System.out.println("There is not enough arguments.");
            System.out.println("dpump <command> <connection-string>");
            return;
        }

        Program program = injectDependencies();
        substituteDefaultConnectionString(args[1], program);

        try {
            String filePath = "c:\\sqlserverutils.data";
            String command = args[0];
            processCommand(program, command, filePath);
        }
        catch (ApplicationException | InterruptedException e) {
            System.out.println("Exception:" + e.getMessage() + e.getStackTrace());
        }
    }

    private static void processCommand(Program program, String command, String filePath) throws InterruptedException {
        switch (command){
            case "import":
                System.out.println("Import started.");
                program.importManager.doImport(filePath);
                System.out.println("Import finished.");
                break;
            case "export":
                System.out.println("Export started.");

                startStatusThread(program.exportManager);
                program.exportManager.doExport(filePath);

                break;
            case "clear":
                System.out.println("Clear started.");
                program.cleanManager.doClear();
                System.out.println("Clear finished.");
                break;
            default:
                System.out.println("Command is not supported.");
        }
    }

    public static void startStatusThread(final ExportManager exportManager) {
        Runnable statusThreadRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessResult result = exportManager.getCurrentStatus();
                    int sleepTimes = 0;
                    while(!result.isFinished()) {
                        Thread.sleep(STATUS_THREAD_SLEEP_TIME);
                        sleepTimes++;
                        result = exportManager.getCurrentStatus();

                        System.out.println(sleepTimes + ")" + result.getMessage());
                    }

                } catch (InterruptedException e) {
                    throw new ApplicationException(e.getMessage(), e.getCause());
                }
            }
        };

        Thread statusThread = new Thread(statusThreadRunnable);

        statusThread.start();
    }

    private static void substituteDefaultConnectionString(String arg, Program program) {
        String connectionString = arg;
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
