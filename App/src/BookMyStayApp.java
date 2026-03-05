/**
 * Book My Stay App
 *
 * This class represents the entry point of the Hotel Booking Management System.
 * It demonstrates how a Java application starts execution and prints
 * a welcome message to the console.
 *
 * @author YourName
 * @version 1.0
 */
public class BookMyStayApp {

    /**
     * Main method – entry point of the application.
     * The JVM starts program execution from this method.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // Application name and version
        String appName = "Book My Stay - Hotel Booking System";
        String version = "v1.0";

        // Print welcome message
        System.out.println("======================================");
        System.out.println(" Welcome to " + appName);
        System.out.println(" Version: " + version);
        System.out.println("======================================");
        System.out.println("Application started successfully.");
        System.out.println("Thank you for using Book My Stay!");
    }
}