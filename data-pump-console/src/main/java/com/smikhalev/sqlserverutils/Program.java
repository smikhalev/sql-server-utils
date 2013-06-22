package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class Program {

    @Autowired
    private ConnectionProvider connectionProvider;

    @Autowired
    private Facade facade;

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
        catch (ApplicationException e) {
            System.out.println("Exception:" + e.getMessage() + e.getStackTrace());
        }
    }

    private static void processCommand(Program program, String command, String filePath) {
        switch (command){
            case "import":
                System.out.println("Import started.");
                program.facade.doImport(filePath);
                System.out.println("Import finished.");
                break;
            case "export":
                System.out.println("Export started.");
                program.facade.doExport(filePath);
                System.out.println("Export finished.");
                break;
            case "clear":
                System.out.println("Clear started.");
                program.facade.doClear();
                System.out.println("Clear finished.");
                break;
            default:
                System.out.println("Command is not supported.");
        }
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

    public void setFacade(Facade facade) {
        this.facade = facade;
    }
}
