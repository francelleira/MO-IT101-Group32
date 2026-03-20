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

public class MotorPH {
    
    // These file paths point to the CSV files inside the project's folder.
    static final String EMPLOYEE_FILE = "MotorPH_Employee_Details.csv";
    static final String ATTENDANCE_FILE = "Attendance_Record.csv";
    
    // These constants represent the column positions in the employee CSV file.
    static final int COL_EMP_NUM = 0;
    static final int COL_LAST_NAME = 1;
    static final int COL_FIRST_NAME = 2;
    static final int COL_BIRTHDAY = 3;
    static final int COL_HOURLY_RATE = 18;
    
    // These constants represent the column positions in the attendance CSV file.
    static final int ATT_EMP_NUM = 0;
    static final int ATT_DATE = 3;
    static final int ATT_LOGIN = 4;
    static final int ATT_LOGOUT = 5;
    
    // These ArrayLists store the CSV contents after reading and loading the files.
    static ArrayList<String[]> employeeData = new ArrayList<String[]>();
    static ArrayList<String[]> attendanceData = new ArrayList<String[]>();
    
    // This array stores the cutoff start dates for the month of June to December.
    static String[] cutoffStart = {
        "06/01/2024", "06/16/2024",
        "07/01/2024", "07/16/2024",
        "08/01/2024", "08/16/2024",
        "09/01/2024", "09/16/2024",
        "10/01/2024", "10/16/2024",
        "11/01/2024", "11/16/2024",
        "12/01/2024", "12/16/2024"
    };
    
    // This array stores the cutoff end dates for the month of June to December.
    static String[] cutoffEnd = {
        "06/15/2024", "06/30/2024",
        "07/15/2024", "07/31/2024",
        "08/15/2024", "08/31/2024",
        "09/15/2024", "09/30/2024",
        "10/15/2024", "10/31/2024",
        "11/15/2024", "11/30/2024",
        "12/15/2024", "12/31/2024"
    };
    
    // This method prints a divider between pages.
    public static void printDivider() {
        System.out.println("\n----------------------------------------\n");
    }  

    // This method reads the employee CSV file once and stores all rows in a data storage.
    public static void loadEmployeeData() {
        employeeData.clear();
        
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            
            // Read the first line (header) and skip it.
            br.readLine();
            
            //Loop through the remaining lines of the file and add the data gathered to the data storage.
            while ((line = br.readLine()) != null) {
                // This split pattern avoids breaking fields that contain commas inside quotation marks.
                String[] empData = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                employeeData.add(empData);
            }
            
        } catch (IOException e) {
            System.out.println("Error reading employee file");
        }
    }
    
    // This method reads the attendance CSV file once and stores all rows in a data storage.
    public static void loadAttendanceData() {
        attendanceData.clear();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            
            // Read the first line (header) and skip it.
            br.readLine();
            
            //Loop through the remaining lines of the file and add the data gathered to the data storage.
            while((line = br.readLine()) != null) {
                String[] attData = line.split(",");
                attendanceData.add(attData);
            }
            
        } catch (IOException e) {
            System.out.println("Error reading attendance file");
        }
    }
    
    // This method searches for one employee in the data storage using the employee number.
    public static String[] findEmployee(String empNum) {
        for (int i = 0; i < employeeData.size(); i++) {
            String[] empData = employeeData.get(i);
            
            if (empData[COL_EMP_NUM].trim().equals(empNum)) {
                return empData;
            }
        }
        
        return null;
    }
    
    // This method checks whether an employee number exists in the loaded employee data.
    public static boolean employeeExists(String empNum) {
        return findEmployee(empNum) != null;
    }
    
    // This method displays the employee details consisting of their employee number, name, and birthday.
    public static boolean displayEmployeeDetails(String empNum) {
        String[] empData = findEmployee(empNum);
        
        if (empData == null) {
            return false;
        }
        
        String fullName = empData[COL_FIRST_NAME].trim() + " " + empData[COL_LAST_NAME].trim();
        
        System.out.println("=========== Employee Details ===========\n");
        System.out.println("Employee Number: " + empData[COL_EMP_NUM].trim());
        System.out.println("Employee Name: " + fullName);
        System.out.println("Birthday: " + empData[COL_BIRTHDAY].trim());
        printDivider();
        
        return true;
    }
    
    // This method gets the hourly rate of one employee from the loaded employee data.
    public static double getHourlyRate(String empNum) {
        String[] empData = findEmployee(empNum);
        
        if (empData == null) {
            return 0;
        }
        
        String rateString = empData[COL_HOURLY_RATE].replace("\"", "").trim();
        return Double.parseDouble(rateString);
    }
    
    // This method returns all employee numbers from the loaded employee data.
    public static ArrayList<String> getAllEmployeeNumbers() {
        ArrayList<String> empNums = new ArrayList<String>();

        for (int i = 0; i < employeeData.size(); i++) {
            String[] empData = employeeData.get(i);
            empNums.add(empData[COL_EMP_NUM].trim());
        }

        return empNums;
    }
    
    // This method calculates the total works hours of one employee within a cutoff period.
    static double calculateHours(String empNum, String startDate, String endDate) throws Exception {
        
        // This variable stores accumulated hours worked.
        double totalHours = 0;
        
        // Date and time format in the CSV file
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        
        // Convert cutoff start and end dates to LocalDate
        LocalDate start = LocalDate.parse(startDate, dateFormat);
        LocalDate end = LocalDate.parse(endDate, dateFormat);
        
        for (int i = 0; i < attendanceData.size(); i++) {
            String[] attData = attendanceData.get(i);

            String rowEmpNum = attData[ATT_EMP_NUM].trim();
            LocalDate workDate = LocalDate.parse(attData[ATT_DATE].trim(), dateFormat);

            // Only process rows that belong to the correct employee and fall within the cutoff dates.
            if (rowEmpNum.equals(empNum) && !workDate.isBefore(start) && !workDate.isAfter(end)) {
                LocalTime login = LocalTime.parse(attData[ATT_LOGIN].trim(), timeFormat);
                LocalTime logout = LocalTime.parse(attData[ATT_LOGOUT].trim(), timeFormat);

                double worked = calculateDailyHours(login, logout);
                totalHours += worked;
            }
        }
        
        return totalHours;
    }
    
    // This method calculates one day's payable hours based on login and logout times.
    public static double calculateDailyHours(LocalTime login, LocalTime logout) {
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime workEnd = LocalTime.of(17, 0);
        LocalTime graceLimit = LocalTime.of(8, 10);

        // If the employee logs in within the grace period, treat it as exactly 8:00 AM.
        if (!login.isAfter(graceLimit)) {
            login = workStart;
        }

        // Any time after 5:00 PM is not counted because overtime is not included here.
        if (logout.isAfter(workEnd)) {
            logout = workEnd;
        }

        double hours = Duration.between(login, logout).toMinutes() / 60.0;

        // Deduct one hour for lunch break from the daily total.
        hours = hours - 1.0;

        // Prevent negative hours in case of invalid or incomplete time data.
        if (hours < 0) {
            hours = 0;
        }

        return hours;
    }
    
    // This method calculates the gross salary using hours worked and hourly rate.
    static double calculateGrossSalary(String empNum, double hoursWorked) throws Exception {
        
        // Get the employee hourly rate from the employee data.
        double hourlyRate = getHourlyRate(empNum);
        
        // Gross salary = hours worked * hourly rate
        return hoursWorked * hourlyRate;
    }
    
    // This method calculates the combined monthly gross salary grom two cutoffs.
    public static double calculateMonthlyGross(double firstGross, double secondGross) {
        return firstGross + secondGross;
    }
    
    // This method computes the employee's SSS contribution based on the monthly gross salary.
    // The SSS table increases in fixed steps of 500 salary and 22.5 contribution.
    public static double computeSSS(double monthlyGross) {
        
        //Minimum contribution for salaries below 3250
        if (monthlyGross < 3250) {
            return 135.0;
        }
        
        // Maximum contribution cap
        if (monthlyGross >= 24750) {
            return 1125.0;
        }
        
        // Compute which salary bracket the employee falls into
        int bracket = (int)((monthlyGross - 3250) / 500);
        
        // Base value starts at 157.5 for the first bracket
        return 157.5 + (bracket * 22.5);
    }
    
    // This method computes the employee share of PhilHealth based on their monthly gross salary.
    public static double computePhilHealth(double monthlyGross) {
        
        double premium = monthlyGross * 0.03;
        
        // Cap the total premium at 1800, then divide by 2 for the employee share.
        if (premium > 1800) {
            premium = 1800.0;
        }
        
        return premium / 2;
    }
    
    // This method computes the Pag-IBIG contribution based on the employee's monthly gross salary
    public static double computePagIBIG(double monthlyGross) {
        
        double contribution;
        
        if (monthlyGross >= 1000 && monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else {
            contribution = monthlyGross * 0.02;
        }
        
        // The employee contribution is capped at 100 pesos.
        if (contribution > 100) {
            contribution = 100;
        }
        
        return contribution;
    }
    
    // This method computes the employee's taxable income.
    // Taxable income = monthly gross salary - mandatory deductions.
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
    
    // This method computes the total deductions of one employee.
    public static double computeDeductions(double sss, double philHealth, double pagIBIG, double tax) {
        return sss + philHealth + pagIBIG + tax;
    }
    
    // This method displays the payroll details for both cutoffs in one month.
    public static void displayPayroll(String start1, String end1, String start2, String end2,
                                      double hours1, double gross1, double hours2, double gross2,
                                      double sss, double philHealth, double pagIBIG, double tax,
                                      double totalDed, double net2) {

        System.out.println("Cutoff Date: " + start1 + " to " + end1);
        System.out.println("Total Hours Worked: " + hours1);
        System.out.println("Gross Salary: " + gross1);
        System.out.println("Net Salary: " + gross1);

        System.out.println();

        System.out.println("Cutoff Date: " + start2 + " to " + end2);
        System.out.println("Total Hours Worked: " + hours2);
        System.out.println("Gross Salary: " + gross2);
        System.out.println("Each Deduction:");
        System.out.println("  SSS: " + sss);
        System.out.println("  PhilHealth: " + philHealth);
        System.out.println("  Pag-IBIG: " + pagIBIG);
        System.out.println("  Tax: " + tax);
        System.out.println("Total Deductions: " + totalDed);
        System.out.println("Net Salary: " + net2);

        printDivider();
    }
    
    // This method processes payroll for one employee across all monthly cutoffs.
    public static void processPayrollForEmployee(String empNum) throws Exception {
        
        if (!employeeExists(empNum)) {
            System.out.println("Employee number does not exist.");
            return;
        }

        displayEmployeeDetails(empNum);

        // Process cutoffs two at a time.
        // The loop moves two cutoffs at a time because one month has two payroll periods.
        for (int i = 0; i < cutoffStart.length; i += 2) {
            String start1 = cutoffStart[i];
            String end1 = cutoffEnd[i];
            String start2 = cutoffStart[i + 1];
            String end2 = cutoffEnd[i + 1];

            double hours1 = calculateHours(empNum, start1, end1);
            double hours2 = calculateHours(empNum, start2, end2);

            double gross1 = calculateGrossSalary(empNum, hours1);
            double gross2 = calculateGrossSalary(empNum, hours2);

            double monthlyGross = calculateMonthlyGross(gross1, gross2);

            double sss = computeSSS(monthlyGross);
            double philHealth = computePhilHealth(monthlyGross);
            double pagIBIG = computePagIBIG(monthlyGross);
            double taxable = computeTaxableIncome(monthlyGross, sss, philHealth, pagIBIG);
            double tax = computeTax(taxable);
            double totalDed = computeDeductions(sss, philHealth, pagIBIG, tax);
            double net2 = gross2 - totalDed;

            displayPayroll(start1, end1, start2, end2,
                    hours1, gross1, hours2, gross2,
                    sss, philHealth, pagIBIG, tax,
                    totalDed, net2);
        }
    }
    
    // This method handles the employee menu after successful login.
    public static void employeeMenu(Scanner scanner) {
        System.out.println("========== Employee logged in ==========\n");
        
        System.out.println("[1] Enter your employee number: ");
        System.out.println("[2] Exit the program");
        
        // Takes user input for the next action.
        System.out.print("\nSelect option (1/2): ");
        String choice = scanner.nextLine();
        
        printDivider();
        
        // If the user chose "1", user input will be taken to verify their employee number.
        if (choice.equals("1")) {
            System.out.print("Enter your employee number: ");
            String empNum = scanner.nextLine();
            
            printDivider();
            
            if (!displayEmployeeDetails(empNum)) {
                System.out.println("Employee number does not exist.");
            }
            
        // If the user chose "2", the program will be closed.
        } else if (choice.equals("2")) {
            System.out.println("Program closed.");
            System.exit(0);
            
        // The program informs the user if their input is invalid.
        } else {
            System.out.println("Invalid input.");
            System.exit(0);
        }
    }
    
    // This method handles the payroll staff menu after successful log in.
    public static void payrollStaffMenu(Scanner scanner) throws Exception {
        System.out.println("======== Payroll staff logged in =======\n");
        
        System.out.println("[1] Process Payroll");
        System.out.println("[2] Exit the program");
        
        // Takes user input for the next action.
        System.out.print("\nSelect option (1/2): ");
        String choice = scanner.nextLine();
        
        printDivider();
        
        // The user will be taken to the process payroll menu if they choose "1".
        if (choice.equals("1")) {
            processPayrollMenu(scanner);
            
        // The program will close if the user input is "2".
        } else if (choice.equals("2")) {
            System.out.println("Program closed.");
            System.exit(0);
            
        // The program informs the user if their input is invalid.
        } else {
            System.out.println("Invalid input.");
            System.exit(0);
        }
    }
    
    // This method handles the payroll processing options.
    public static void processPayrollMenu(Scanner scanner) throws Exception {
        System.out.println("============ Process Payroll ===========\n");
        
        // Display options for payroll processing.
        System.out.println("[1] One employee");
        System.out.println("[2] All employees");
        System.out.println("[3] Exit the program");
        
        // Takes user input for the next action.
        System.out.print("\nSelect option (1/2/3): ");
        String choice = scanner.nextLine();
        
        // If the user chose "1", the program will process and display payroll results for one employee.
        if (choice.equals("1")) {
            System.out.print("\nEnter the employee number: ");
            String empNum = scanner.nextLine();
            
            printDivider();
            processPayrollForEmployee(empNum);
        
        // If the user chose "2", the program will process and display the payroll results for all employees.
        } else if (choice.equals("2")) {
            ArrayList<String> allEmployees = getAllEmployeeNumbers();
            
            printDivider();
            
            for (int i = 0; i < allEmployees.size(); i++) {
                String empNum = allEmployees.get(i);
                processPayrollForEmployee(empNum);
            }
        
        // The program will be closed if the user choose to exit.
        } else if (choice.equals("3")) {
            printDivider();
            System.out.println("Program closed.");
            System.exit(0);
            
        // The program informs the user if their input is invalid.
        } else {
            printDivider();
            System.out.println("Invalid input.");
            System.exit(0);
        }
    }
    
    // This is the main method that starts the program.
    public static void main(String[] args) throws Exception {
        
        Scanner scanner = new Scanner(System.in);
        
        //Read the CSV files only once at the start of the program.
        loadEmployeeData();
        loadAttendanceData();
        
        System.out.println("\n======== MotorPH Payroll Portal ========\n");
        
        // Takes user input for their username and password to log in.
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        
        printDivider();
        
        // Employee is logged in successfully; the user is taken to the employee menu.
        if (username.equals("employee") && password.equals("12345")) {
            employeeMenu(scanner);
        
        // Payroll staff is logged in successfully; the user is taken to the payroll staff menu.
        } else if (username.equals("payroll_staff") && password.equals("12345")) {
            payrollStaffMenu(scanner);
        
        // The program informs the user if their login input is incorrect or invalid.
        } else {
            System.out.println("Incorrect username and/or password");
            System.exit(0);
        }
        
        scanner.close();
    }
}