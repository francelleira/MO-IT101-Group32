package motorph;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class MotorPH {
    
    // File Paths
    static final String EMPLOYEE_FILE = "MotorPH_Employee_Details.csv";
    static final String ATTENDANCE_FILE = "Attendance_Record.csv";
    
    // Employee details CSV file column indexes
    static final int COL_EMPLOYEE_NUMBER = 0; // employee number data is stored at index 0
    static final int COL_LAST_NAME = 1; // last name data is stored at index 1
    static final int COL_FIRST_NAME = 2; // first name data is stored at index 2
    static final int COL_BIRTHDAY = 3; // birthday data is stored at index 3
    static final int COL_HOURLY_RATE = 18; // hourly rate data is stored at index 18
    
    // Attendance record CSV file column indexes
    static final int ATTENDANCE_EMPLOYEE_NUMBER = 0; // employee number data is stored at index 0
    static final int ATTENDANCE_DATE = 3; // attendance date data is stored at index 3
    static final int ATTENDANCE_LOGIN = 4; // login data is stored at index 4
    static final int ATTENDANCE_LOGOUT = 5; // logout data is stored at index 5
    
    // Data Storage
    static ArrayList<String[]> employeeData = new ArrayList<String[]>();
    static ArrayList<String[]> attendanceData = new ArrayList<String[]>();
    
    // Cutoff start dates
    static String[] cutoffStart = {
        "06/01/2024", "06/16/2024",
        "07/01/2024", "07/16/2024",
        "08/01/2024", "08/16/2024",
        "09/01/2024", "09/16/2024",
        "10/01/2024", "10/16/2024",
        "11/01/2024", "11/16/2024",
        "12/01/2024", "12/16/2024"
    };
    
    // Cutoff end dates
    static String[] cutoffEnd = {
        "06/15/2024", "06/30/2024",
        "07/15/2024", "07/31/2024",
        "08/15/2024", "08/31/2024",
        "09/15/2024", "09/30/2024",
        "10/15/2024", "10/31/2024",
        "11/15/2024", "11/30/2024",
        "12/15/2024", "12/31/2024"
    };
    
    // This method acts as a divider between pages
    public static void printDivider() {
        System.out.println("\n------------------------------\n");
    }  

    // This method reads and load the employee details CSV file
    public static void loadEmployeeData() {
        employeeData.clear();
        
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            
            br.readLine(); // Read the first line (header) and skip it
            
            //Loop through the remaining lines of the file and add the data gathered to the data storage
            while ((line = br.readLine()) != null) {
                String[] employeeFileData = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                employeeData.add(employeeFileData);
            }
            
        } catch (IOException e) {
            System.out.println("Error reading employee file");
        }
    }
    
    // This method reads and load the attendance CSV file
    public static void loadAttendanceData() {
        attendanceData.clear();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            
            br.readLine(); // Read the first line (header) and skip it
            
            //Loop through the remaining lines of the file and add the data gathered to the data storage
            while((line = br.readLine()) != null) {
                String[] attendanceFileData = line.split(",");
                attendanceData.add(attendanceFileData);
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file");
        }
    }
    
    // This method finds and returns the employee data
    public static String[] findEmployee(String employeeNumber) {
        for (int i = 0; i < employeeData.size(); i++) {
            String[] employeeFileData = employeeData.get(i);
            
            if (employeeFileData[COL_EMPLOYEE_NUMBER].trim().equals(employeeNumber)) {
                return employeeFileData;
            }
        }
        
        return null;
    }
    
    // This method is used to confirm if the employee is included in the MotorPH employee data
    public static boolean employeeExists(String employeeNumber) {
        return findEmployee(employeeNumber) != null;
    }
    
    // This method displays the employee details consisting of their employee number, name, and birthday
    public static boolean displayEmployeeDetails(String employeeNumber) {
        String[] employeeFileData = findEmployee(employeeNumber);
        
        if (employeeFileData == null) {
            return false;
        }
        
        String fullName = employeeFileData[COL_FIRST_NAME].trim() + " " + employeeFileData[COL_LAST_NAME].trim();
        
        System.out.println("=== Employee Details ===\n");
        System.out.println("Employee Number: " + employeeFileData[COL_EMPLOYEE_NUMBER].trim());
        System.out.println("Eployee Name: " + fullName);
        System.out.println("Birthday: " + employeeFileData[COL_BIRTHDAY].trim());
        System.out.println();
        
        return true;
    }
    
    // This method gets the hourly rate of the employees from the employee data
    public static double getHourlyRate(String employeeNumber) {
        String[] employeeFileData = findEmployee(employeeNumber);
        
        if (employeeFileData == null) {
            return 0;
        }
        
        String rateString = employeeFileData[COL_HOURLY_RATE].replace("\"", "").trim();
        return Double.parseDouble(rateString);
    }
    
    // This method reads the employee data and returns all employee numbers
    public static ArrayList<String> getAllEmployeeNumbers() {
        ArrayList<String> employeeNumbers = new ArrayList<String>();

        for (int i = 0; i < employeeData.size(); i++) {
            String[] employeeFileData = employeeData.get(i);
            employeeNumbers.add(employeeFileData[COL_EMPLOYEE_NUMBER].trim());
        }

        return employeeNumbers;
    }
    
    // This method calculates the total hours worked by one employee within a specific cutoff date range
    static double calculateHours(String employeeNumber, String startDate, String endDate) throws Exception {
        
        double totalHours = 0; // stores accumulated hours worked
        
        // Date format in the CSV file
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        // Time format in the CSV file
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        
        // Convert cutoff start and end dates to LocalDate
        LocalDate start = LocalDate.parse(startDate, dateFormat);
        LocalDate end = LocalDate.parse(endDate, dateFormat);
        
        for (int i = 0; i < attendanceData.size(); i++) {
            String[] attendanceFileData = attendanceData.get(i);

            String employee = attendanceFileData[ATTENDANCE_EMPLOYEE_NUMBER].trim();
            LocalDate date = LocalDate.parse(attendanceFileData[ATTENDANCE_DATE].trim(), dateFormat);

            // Check if record belongs to an employee and if date is within the cutoff
            if (employee.equals(employeeNumber) && !date.isBefore(start) && !date.isAfter(end)) {
                LocalTime login = LocalTime.parse(attendanceFileData[ATTENDANCE_LOGIN].trim(), timeFormat);
                LocalTime logout = LocalTime.parse(attendanceFileData[ATTENDANCE_LOGOUT].trim(), timeFormat);

                // Define employee work schedule
                LocalTime workStart = LocalTime.of(8, 0);
                LocalTime workEnd = LocalTime.of(17, 0);
                LocalTime graceLimit = LocalTime.of(8, 10);

                // If employee logged in within grace period, attendance will be recorded as 8:00 AM
                if (!login.isAfter(graceLimit)) {
                    login = workStart;
                }

                // Ignore overtime after 5:00 PM
                if (logout.isAfter(workEnd)) {
                    logout = workEnd;
                }

                double hoursWorked = Duration.between(login, logout).toMinutes() / 60.0;

                // Subtract 1 hour lunch break
                hoursWorked = hoursWorked - 1.0;

                // Prevent negative value
                if (hoursWorked < 0) {
                    hoursWorked = 0;
                }

                totalHours += hoursWorked;
            }
        }
        
        return totalHours;
    }
    
    // This method calculates the gross salary by multiplying the hourly rate and total number of hours worked
    static double calculateGrossSalary(String employeeNumber, double hoursWorked) throws Exception {
        
        // Get the employee hourly rate from the employee data
        double hourlyRate = getHourlyRate(employeeNumber);
        
        // Gross salary = hours worked * hourly rate
        return hoursWorked * hourlyRate;
    }
    
    // This method computes the SSS contribution based on the employee's monthly gross salary
    public static double computeSSS(double monthlyGross) {
        
        if (monthlyGross < 3250) {
            return 135.0;
        } else if (monthlyGross >= 3250.0 && monthlyGross <= 3749.99) {
            return 157.5;
        } else if (monthlyGross >= 3750.0 && monthlyGross <= 4249.99) {
            return 180.0;
        } else if (monthlyGross >= 4250.0 && monthlyGross <= 4749.99) {
            return 202.5;
        } else if (monthlyGross >= 4750.0 && monthlyGross <= 5249.99) {
            return 225.0;
        } else if (monthlyGross >= 5250.0 && monthlyGross <= 5749.99) {
            return 247.5;
        } else if (monthlyGross >= 5750.0 && monthlyGross <= 6249.99) {
            return 270.0;
        } else if (monthlyGross >= 6250.0 && monthlyGross <= 6749.99) {
            return 292.5;
        } else if (monthlyGross >= 6750.0 && monthlyGross <= 7249.99) {
            return 315.0;
        } else if (monthlyGross >= 7250.0 && monthlyGross <= 7749.99) {
            return 337.5;
        } else if (monthlyGross >= 7750.0 && monthlyGross <= 8249.99) {
            return 360.0;
        } else if (monthlyGross >= 8250.0 && monthlyGross <= 8749.99) {
            return 382.5;
        } else if (monthlyGross >= 8750.0 && monthlyGross <= 9249.99) {
            return 405.0;
        } else if (monthlyGross >= 9250.0 && monthlyGross <= 9749.99) {
            return 427.5;
        } else if (monthlyGross >= 9750.0 && monthlyGross <= 10249.99) {
            return 450.0;
        } else if (monthlyGross >= 10250.0 && monthlyGross <= 10749.99) {
            return 472.5;
        } else if (monthlyGross >= 10750.0 && monthlyGross <= 11249.99) {
            return 495.0;
        } else if (monthlyGross >= 11250.0 && monthlyGross <= 11749.99) {
            return 517.5;
        } else if (monthlyGross >= 11750.0 && monthlyGross <= 12249.99) {
            return 540.0;
        } else if (monthlyGross >= 12250.0 && monthlyGross <= 12749.99) {
            return 562.5;
        } else if (monthlyGross >= 12750.0 && monthlyGross <= 13249.99) {
            return 585.0;
        } else if (monthlyGross >= 13250.0 && monthlyGross <= 13749.99) {
            return 607.5;
        } else if (monthlyGross >= 13750.0 && monthlyGross <= 14249.99) {
            return 630.0;
        } else if (monthlyGross >= 14250.0 && monthlyGross <= 14749.99) {
            return 652.5;
        } else if (monthlyGross >= 14750.0 && monthlyGross <= 15249.99) {
            return 675.0;
        } else if (monthlyGross >= 15250.0 && monthlyGross <= 15749.99) {
            return 697.5;
        } else if (monthlyGross >= 15750.0 && monthlyGross <= 16249.99) {
            return 720.0;
        } else if (monthlyGross >= 16250.0 && monthlyGross <= 16749.99) {
            return 742.5;
        } else if (monthlyGross >= 16750.0 && monthlyGross <= 17249.99) {
            return 765.0;
        } else if (monthlyGross >= 17250.0 && monthlyGross <= 17749.99) {
            return 787.5;
        } else if (monthlyGross >= 17750.0 && monthlyGross <= 18249.99) {
            return 810.0;
        } else if (monthlyGross >= 18250.0 && monthlyGross <= 18749.99) {
            return 832.5;
        } else if (monthlyGross >= 18750.0 && monthlyGross <= 19249.99) {
            return 855.0;
        } else if (monthlyGross >= 19250.0 && monthlyGross <= 19749.99) {
            return 877.5;
        } else if (monthlyGross >= 19750.0 && monthlyGross <= 20249.99) {
            return 900.0;
        } else if (monthlyGross >= 20250.0 && monthlyGross <= 20749.99) {
            return 922.5;
        } else if (monthlyGross >= 20750.0 && monthlyGross <= 21249.99) {
            return 945.0;
        } else if (monthlyGross >= 21250.0 && monthlyGross <= 21749.99) {
            return 967.5;
        } else if (monthlyGross >= 21750.0 && monthlyGross <= 22249.99) {
            return 990.0;
        } else if (monthlyGross >= 22250.0 && monthlyGross <= 22749.99) {
            return 1012.5;
        } else if (monthlyGross >= 22750.0 && monthlyGross <= 23249.99) {
            return 1035.0;
        } else if (monthlyGross >= 23250.0 && monthlyGross <= 23749.99) {
            return 1057.5;
        } else if (monthlyGross >= 23750.0 && monthlyGross <= 24249.99) {
            return 1080.0;
        } else if (monthlyGross >= 24250.0 && monthlyGross <= 24749.99) {
            return 1102.5;
        } else {
            return 1125.0;
        }
    }
    
    // This method computes the PhilHealth contribution based on the employee's monthly gross salary
    public static double computePhilHealth(double monthlyGross) {
        
        double premium = monthlyGross * 0.03;
        
        if (premium > 1800) {
            premium = 1800.0;
        }
        
        double employeeShare = premium / 2;
        
        return employeeShare;
    }
    
    // This method computes the Pag-IBIG contribution based on the employee's monthly gross salary
    public static double computePagIBIG(double monthlyGross) {
        
        double contribution;
        
        if (monthlyGross >= 1000 && monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else {
            contribution = monthlyGross * 0.02;
        }
        
        if (contribution > 100) {
            contribution = 100;
        }
        
        return contribution;
    }
    
    // This method computes the employee's taxable income
    // Taxable income = monthly gross salary - mandatory deductions
    public static double computeTaxableIncome(double monthlyGross, double sss, double philHealth, double pagIBIG) {
        return monthlyGross - (sss + philHealth + pagIBIG);
    }
    
    // This method computes the employee's withholding tax based on the taxable income
    public static double computeTax(double taxableIncome) {
        
        // If taxable income is below 20,832, there is no withholding tax
        if (taxableIncome <= 20832) {
            return 0;
        }
        
        // If taxable income is above 20,833 but not more than 33,333
        // Tax = 20% of the excess over 20,833
        else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.20;
        }
        
        // If taxable income is above 33,333 but not more than 66,667
        // Tax = 2,500 + 25% of the excess over 33,333
        else if (taxableIncome <= 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        }
        
        // If taxable income is above 66,667 but not more than 166,667
        // Tax = 10,833  + 30% of the excess over 66,667
        else if (taxableIncome <= 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        }
        
        // If taxable income is above 166,667 but not more than 666,667
        // Tax = 40,833.33  + 32% of the excess over 166,667
        else if (taxableIncome <= 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        }
        
        // If taxable income is above 666,667
        // Tax = 200,833.33  + 35% of the excess over 666,667
        
        else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }
    
    // This method displays employee details and processes payroll for one employee
    public static void processPayrollForEmployee(String employeeNumber) throws Exception {

        // Check if employee exists first
        if (!employeeExists(employeeNumber)) {
            System.out.println("Employee number does not exist.");
            return;
        }
        
        System.out.println("Processing payroll for employee: " + employeeNumber);
        System.out.println();

        // Display employee details
        displayEmployeeDetails(employeeNumber);

        // Process cutoffs two at a time
        for (int i = 0; i < cutoffStart.length; i += 2) {

            // Calculate total hours worked for the 2 cutoffs every month
            double firstCutoffHours = calculateHours(employeeNumber, cutoffStart[i], cutoffEnd[i]);
            double secondCutoffHours = calculateHours(employeeNumber, cutoffStart[i + 1], cutoffEnd[i + 1]);

            // Calculate gross salary for the two cutoffs every month
            double firstCutoffGross = calculateGrossSalary(employeeNumber, firstCutoffHours);
            double secondCutoffGross = calculateGrossSalary(employeeNumber, secondCutoffHours);

            // Add both cutoff gross salaries to get the monthly gross salary
            double monthlyGross = firstCutoffGross + secondCutoffGross;

            // Compute deductions based on monthly gross salary
            double sss = computeSSS(monthlyGross);
            double philHealth = computePhilHealth(monthlyGross);
            double pagIBIG = computePagIBIG(monthlyGross);
            double taxableIncome = computeTaxableIncome(monthlyGross, sss, philHealth, pagIBIG);
            double tax = computeTax(taxableIncome);

            // Compute total deductions
            double totalDeductions = sss + philHealth + pagIBIG + tax;

            // Compute the net salary for the second cutoff
            double secondCutoffNetSalary = secondCutoffGross - totalDeductions;

            // Display payroll results
            System.out.println("Cutoff Date: " + cutoffStart[i] + " to " + cutoffEnd[i]);
            System.out.println("Total Hours Worked: " + firstCutoffHours);
            System.out.println("Gross Salary: " + firstCutoffGross);
            System.out.println("Net Salary: " + firstCutoffGross); // First cutoff net salary does not have any deductions

            System.out.println("\nCutoff Date: " + cutoffStart[i + 1] + " to " + cutoffEnd[i + 1]);
            System.out.println("Total Hours Worked: " + secondCutoffHours);
            System.out.println("Gross Salary: " + secondCutoffGross);

            System.out.println("Each Deduction:");
            System.out.println("  SSS: " + sss);
            System.out.println("  PhilHealth: " + philHealth);
            System.out.println("  Pag-IBIG: " + pagIBIG);
            System.out.println("  Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + secondCutoffNetSalary);

            printDivider();
        }
    }
    
    // EMPLOYEE LOGGED IN MENU
    public static void employeeMenu(Scanner scanner) {
        System.out.println("Employee logged in\n");
        
        // Display options
        System.out.println("[1] Enter your employee number: ");
        System.out.println("[2] Exit the program");
        
        // Takes user input for the next action
        System.out.print("\nSelect option (1/2): ");
        String choice = scanner.nextLine();
        
        printDivider();
        
        // Takes user input for their employee number to log in
        if (choice.equals("1")) {
            System.out.print("Enter your employee number: ");
            String employeeNumber = scanner.nextLine();
            
            printDivider();
            
            if (!displayEmployeeDetails(employeeNumber)) {
                System.out.println("Employee number does not exist.");
            }
        // User chose to exit the program
        } else if (choice.equals("2")) {
            System.out.println("Program closed.");
            System.exit(0);
        // User input is invalid
        } else {
            System.out.println("Invalid choice.");
            System.exit(0);
        }
    }
    
    // PAYROLL STAFF LOGGED IN MENU
    public static void payrollStaffMenu(Scanner scanner) throws Exception {
        System.out.println("Payroll staff logged in\n");
        
        // Display options
        System.out.println("[1] Process Payroll");
        System.out.println("[2] Exit the program");
        
        // Takes user input for the next action
        System.out.print("\nSelect option (1/2): ");
        String choice = scanner.nextLine();
        
        printDivider();
        
        // User chose to process payroll
        if (choice.equals("1")) {
            processPayrollMenu(scanner);
        // User chose to exit the program
        } else if (choice.equals("2")) {
            System.out.println("Program closed.");
            System.exit(0);
        // User input is invalid
        } else {
            System.out.println("Invalid choice.");
            System.exit(0);
        }
    }
    
    // PROCESS PAYROLL MENU
    public static void processPayrollMenu(Scanner scanner) throws Exception {
        System.out.println("=== Process Payroll ===\n");
        
        // Display options
        System.out.println("[1] One employee");
        System.out.println("[2] All employees");
        System.out.println("[3] Exit the program");
        
        // Takes user input for the next action
        System.out.print("\nSelect option (1/2/3): ");
        String choice = scanner.nextLine();
        
        // User chose to process payroll for one employee
        if (choice.equals("1")) {
            System.out.print("\nEnter the employee number: ");
            String employeeNumber = scanner.nextLine();
            
            printDivider();
            processPayrollForEmployee(employeeNumber);
        
        // User chose to process payroll for all employees
        } else if (choice.equals("2")) {
            ArrayList<String> allEmployees = getAllEmployeeNumbers();
            
            printDivider();
            
            for (int i = 0; i < allEmployees.size(); i++) {
                String employeeNumber = allEmployees.get(i);
                processPayrollForEmployee(employeeNumber);
            }
        
        // User chose to exit the program
        } else if (choice.equals("3")) {
            printDivider();
            System.out.println("Program closed.");
            System.exit(0);
            
        // User input is invalid
        } else {
            printDivider();
            System.out.println("Invalid choice.");
            System.exit(0);
        }
    }
    
    // MAIN METHOD
    public static void main(String[] args) throws Exception {
        
        Scanner scanner = new Scanner(System.in);
        
        //Read the CSV files
        loadEmployeeData();
        loadAttendanceData();
        
        System.out.println("==== MotorPH Payroll Portal ====\n");
        
        // Log in screen
        // Takes user input for their username and password
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();   
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        
        printDivider();
        
        // Employee is logged in
        if (username.equals("employee") && password.equals("12345")) {
            employeeMenu(scanner);
        
        // Payroll staff is logged in
        } else if (username.equals("payroll_staff") && password.equals("12345")) {
            payrollStaffMenu(scanner);
        
        // User input is invalid
        } else {
            System.out.println("Incorrect username and/or password");
            System.exit(0);
        }
        
        scanner.close();
    }
}