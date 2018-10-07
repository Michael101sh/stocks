package stocksPortfoliosSrc;
 
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import stocksPortfoliosAPI.IO;
import stocksPortfoliosAPI.Operations; 
 
/**
 * The class CLIRunner - run all of the threads of clients.
 * @author Michael Shachar. */
public class CLIRunner implements Runnable {
    private static final int LIMIT = 10000;
    private static String basePath = "";
    private static int operation = -1;
    private static String registerFilesNames = "";
    private static String newPortfolioFilesNames = "";
    private static String updatePortfolioFilesNames = "";
    private static String idsFileName = "";
    private static ArrayList<String> registerPaths = null;
    private static ArrayList<String> newPortfolioPaths = null;
    private static ArrayList<String> updatePortfolioPaths = null;
    private static ArrayList<Integer> idsForUpdate = null;
    private static ArrayList<Integer> idsForValue = null;
     
    /**
     * @param args - should contain the base path for 
     * 	the resources of the system. */
    public static void main(String[] args) throws Exception {
       if (args.length != 0) {
          basePath = args[0];
//            basePath =  System.getProperty("basePath");
            System.out.println("Please enter opertaion number: \n"
                + "1 for register - register new customer \n"
                + "2 for update - update stocks in existing customer \n"
                + "3 for replace - replace the portfolio of existing"
                + " customer \n"
                + "4 for value - calculating the portoflio value of "
                + "existing customer");
            Scanner scanner = null;
            IO io = new StocksIO();
            int length = 0;
            int threadsAmount = 0;
            String path = null;
            scanner = new Scanner(System.in);
            operation = scanner.nextInt();
            while (operation != 1 && operation != 2 && 
                    operation != 3 && operation != 4) {
                System.out.println("You chose a wrong operation"
                        + "number, try again.");
                operation = scanner.nextInt();              
            }
            scanner.nextLine(); // move to next input
            if (operation == 1) { // register operation
                System.out.println("Please enter the name of the file "
                        + "of register files names");
                registerFilesNames = scanner.nextLine();
                path = basePath + registerFilesNames;
                File registerPath = new File(path);
                while (!registerPath.exists()) {
                    System.out.println("You chose a wrong file name,"
                            + " try again");
                    registerFilesNames = scanner.nextLine();
                    path = basePath + registerFilesNames;
                    registerPath = new File(path);
                }
                registerPaths = new ArrayList<String>
                    (io.readPaths(path));
                if (registerPaths == null) {
					 System.exit(1);
                }
                length = registerPaths.size();
                if (length < LIMIT) {
                    threadsAmount = length;
                } else {
                    threadsAmount = LIMIT;
                }
            } else if (operation == 2 || operation == 3) {                 
                System.out.println("Please enter the name of the "
                            + "file of the ids indexes of "
                            + "customers which you want to update/replace"
                            + " their portfolio");
                idsFileName = scanner.nextLine();
                path = basePath + idsFileName;
                File idsUpdateFile = new File(path);
                while (!idsUpdateFile.exists()) {
                    System.out.println("You chose a wrong file name,"
                            + " try again");
                    idsFileName = scanner.nextLine();
                    path = basePath + idsFileName;
                    idsUpdateFile = new File(path);
                }
                idsForUpdate = new ArrayList<Integer>
                    (io.readIDsIndexes(path));
                if (operation == 2) { // update operation                   
                    System.out.println("Please enter the name of the file"
                            + " of update portfolio files names");
                    updatePortfolioFilesNames = scanner.nextLine();
                    path = basePath + updatePortfolioFilesNames;
                    File updatePortfolioPath = new File(path);
                    while (!updatePortfolioPath.exists()) {
                        System.out.println("You chose a wrong file name,"
                                + " try again");
                        updatePortfolioFilesNames = scanner.nextLine();
                        path = basePath + updatePortfolioFilesNames;
                        updatePortfolioPath = new File(path);
                    }
                    updatePortfolioPaths = new ArrayList<String>
                        (io.readPaths(path));
                    if (updatePortfolioPaths == null) {
   					 	System.exit(1);
                    }
                    length = updatePortfolioPaths.size();
                    if (length < LIMIT) {
                        threadsAmount = length;
                    } else {
                        threadsAmount = LIMIT;
                    }
                } else if (operation == 3) {  // replace operation                      
                	 System.out.println("Please enter the name of the "
                             + "file of new portfolio files names");
                    newPortfolioFilesNames = scanner.nextLine();
                    path = basePath + newPortfolioFilesNames;
                    File newPortfolioPath = new File(path);
                    while (!newPortfolioPath.exists()){
                        System.out.println("You chose a wrong file name,"
                                + " try again");
                        newPortfolioFilesNames = scanner.nextLine();
                        path = basePath + newPortfolioFilesNames;
                        newPortfolioPath = new File(path);
                    }
                    newPortfolioPaths = new ArrayList<String>
                        (io.readPaths(path));
                    if (newPortfolioPaths == null) {
   					 	System.exit(1);
                    }
                    length = newPortfolioPaths.size();
                    if (length < LIMIT) {
                        threadsAmount = length;
                    } else {
                        threadsAmount = LIMIT;
                    }
                }
            } else { // value operation
                    System.out.println("Please enter the name of the "
                            + "file of the ids indexes which you "
                            + "want to get their portfolio value");
                    idsFileName = scanner.nextLine();
                    path = basePath + idsFileName;
                    File idsFile = new File(path);
                    while (!idsFile.exists()) {
                        System.out.println("You chose a wrong file name,"
                                + " try again");
                        idsFileName = scanner.nextLine();
                        path = basePath + idsFileName;
                        idsFile = new File(path);
                    }
                    idsForValue = new ArrayList<Integer>
                        (io.readIDsIndexes(path));
                    length = idsForValue.size();
                    if (length < LIMIT) {
                        threadsAmount = length;
                    } else {
                        threadsAmount = LIMIT;
                    }
                }
                for (int i = 0; i < threadsAmount; i++) {
                    Thread thread = new Thread(new CLIRunner());
                    thread.setName(String.valueOf(i));
                    thread.start();
                }
            if(scanner != null) {
                scanner.close();
            }
        } else {
             System.err.println("Please supply the base url");
        }
    }
     
    /**
     * Defines the behavior of thread running.
     * Each thread will ask to do a request from the API -
     *  register, update, replace or value. */
    public void run() {
        Thread currentThread = Thread.currentThread();
        int thrNumber = Integer.parseInt(currentThread.getName());
        Operations oprs = new Client();
        if (operation == 1) { 
        	// register operation
            String registerFile = registerPaths.get(thrNumber);
            String registerPath = basePath + registerFile;
            oprs.register(registerPath);
        } else if (operation == 2) {
        	// update opertaion
            String path = null;
            String updatePortFile = updatePortfolioPaths.get(thrNumber);
            path = basePath + updatePortFile;
            int idIndex = idsForUpdate.get(thrNumber);
            oprs.update(idIndex, path);        
        } else if (operation == 3) { 
         // replace operation
            String path = null;
            String newPortFile = newPortfolioPaths.get(thrNumber);
            path = basePath + newPortFile;
            int idIndex = idsForUpdate.get(thrNumber);
            oprs.replace(idIndex, path);    
        } else { 
        	// operation = 4, value operation
            int idIndex = idsForValue.get(thrNumber);
            oprs.value(idIndex);
        }
    } 
}