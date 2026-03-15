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
    
    // This method checks if the employee number in the user input is part of the MotorPH employee data
    public static boolean employeeExists(String empNum) {
        
        try {
            String filePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\MotorPH\\src\\motorph\\MotorPH_Employee_Details.csv";
            //Open the CSV file for reading
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            
            String line;
            
            br.readLine(); // Read the first line (header) and skip it
            
            //Loop through the remaining lines of the file
            while ((line = br.readLine()) != null) {
                
                //Split the line into columns using comma as delimiter
                String[] data = line.split(",");
                
                //data[0] contains the employee number column
                if (data[0].equals(empNum)) {
                    br.close(); //close the file reader
                    return true; // Return true if the employee number matches the user input   
                }
            }
            
            br.close(); // Close the file reader after finishing the loop
        }
        catch (IOException e) {
            System.out.println("Error reading this file");
        }
        return false; // Return false if no matching employee number was found
    }
    
    // This method displays the employee details consisting of their employee number, name, and birthday
    public static boolean displayEmployeeDetails(String empNum) {

        try {
            String filePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\MotorPH\\src\\motorph\\MotorPH_Employee_Details.csv";
            //Open the CSV file for reading
            BufferedReader br = new BufferedReader(new FileReader(filePath));// Open the CSV file

            String line;

            // Skip the header row
            br.readLine();

            // Loop through each row of the file
            while ((line = br.readLine()) != null) {

                // Split the row into columns
                String[] data = line.split(",");

                // data[0] = employee number
                // data[1] = last name
                // data[2] = first name
                // data[3] = birthday
                if (data[0].equals(empNum)) {

                    // Combine first name and last name
                    String fullName = data[2] + " " + data[1];

                    // Display employee details
                    System.out.println("=== Employee Details ===");
                    System.out.println("Employee Number: " + data[0]);
                    System.out.println("Name: " + fullName);
                    System.out.println("Birthday: " + data[3]);

                    br.close();

                    return true;
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }

        // If the employee number was not found
        return false;
    }
    
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
    
    // This method calculates the total hours worked by one employee within a specific cutoff date range
    static double calculateHours(String empNum, String startDate, String endDate) throws Exception {
        
        double totalHours = 0; // stores accumulated hours worked
        
        String attendanceFilePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\MotorPH\\src\\motorph\\Attendance_Record.csv";
        
        // Open the attendance CSV file
        BufferedReader br = new BufferedReader(new FileReader(attendanceFilePath));
        String line;
        
        // Date format in the CSV file
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        // Time format in the CSV file
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        
        // Convert cutoff start and end dates to LocalDate
        LocalDate start = LocalDate.parse(startDate, dateFormat);
        LocalDate end = LocalDate.parse(endDate, dateFormat);
        
        br.readLine(); // skip the header row
        
        // Read each line in the attendance record
        while ((line = br.readLine()) != null) {
            
            String[] data = line.split(","); // split CSV columns
            
            String employee = data[0]; // employee number column
            LocalDate date = LocalDate.parse(data[3], dateFormat); // date column
            
            // Check if record belongs to an employee and if date is within the cutoff
            if (employee.equals(empNum) && !date.isBefore(start) && !date.isAfter(end)) {
                
                // Parse login and logout time
                LocalTime login = LocalTime.parse(data[4], timeFormat);
                LocalTime logout = LocalTime.parse(data[5], timeFormat);
                
                // Define employee work schedule
                LocalTime workStart = LocalTime.of(8, 0);
                LocalTime workEnd = LocalTime.of(17, 0);
                
                // Apply 10-minute grace period
                // Salary deductions will only be applied if they log in from 8:11 AM onwards
                if (login.isAfter(LocalTime.of(8, 10))) {
                    // employee is late and keep their login time
                } else {
                    //employee logged in within grace period; not considered late
                    login = workStart;
                }
                
                // Do not include extra hours beyond 5:00 PM
                if (logout.isAfter(workEnd)) {
                    logout = workEnd;
                }
                
                // Compute the total time between login and logout
                double hoursWorked = Duration.between(login, logout).toMinutes() / 60.0;
                
                // Subtract the one hour lunch break
                hoursWorked -= 1.0;
                
                // Add total hours
                totalHours += hoursWorked;
            }
        }
        
        br.close();
        
        return totalHours;
    }
    
    // This method finds the hourly rate of an employee from the employee CSV file
    static double getHourlyRate(String empNum) throws Exception {
        
        String filePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\MotorPH\\src\\motorph\\MotorPH_Employee_Details.csv";
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        
        br.readLine();
        
        while ((line = br.readLine()) != null) {
            
            // Split CSV line while ignoring commas inside quotation marks
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            if (data[0].equals(empNum)) {
                
                // Get the hourly rate from column S of the CSV file
                String rateString = data[18];
                
                // Remove quotation marks from the string
                rateString = rateString.replace("\"", "").trim();
                
                // Convert the cleaned string into a double
                double hourlyRate = Double.parseDouble(rateString);
                
                br.close();
                
                return hourlyRate;
            }
        }
        br.close();
        return 0; // if employee is not found
    }
    
    // This method calculates the gross salary using hours worked and hourly rate
    static double calculateGrossSalary(String empNum, double hoursWorked) throws Exception {
        
        // Get the employee hourly rate
        double hourlyRate = getHourlyRate(empNum);
        
        // Compute gross salary
        double grossSalary = hoursWorked * hourlyRate;
        
        return grossSalary;
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
        
        double taxableIncome = monthlyGross - (sss + philHealth + pagIBIG);
        
        return taxableIncome;
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
    
    // This method reads the employee CSV file and returns all employee numbers
    public static ArrayList<String> getAllEmployeeNumbers() {

        ArrayList<String> employeeNumbers = new ArrayList<>();

        try {
            String filePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\MotorPH\\src\\motorph\\MotorPH_Employee_Details.csv";
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line;

            br.readLine(); // skip header row

            while ((line = br.readLine()) != null) {

                // Split CSV line while ignoring commas inside quotation marks
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Get employee number from column A
                String empNum = data[0].trim();

                // Add employee number to the list
                employeeNumbers.add(empNum);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }

        return employeeNumbers;
    }
    
    // This method displays employee details and processes payroll for one employee
    public static void processPayrollForEmployee(String empNum) throws Exception {

        // Check if employee exists first
        if (!employeeExists(empNum)) {
            System.out.println("\nEmployee number not found: " + empNum);
            return;
        }

        // Display employee details
        if (!displayEmployeeDetails(empNum)) {
            System.out.println("\nEmployee number not found: " + empNum);
            return;
        }

        // Process cutoffs two at a time
        for (int i = 0; i < cutoffStart.length; i += 2) {

            // Calculate total hours for the first cutoff
            double firstCutoffHours = calculateHours(empNum, cutoffStart[i], cutoffEnd[i]);

            // Calculate total hours for the second cutoff
            double secondCutoffHours = calculateHours(empNum, cutoffStart[i + 1], cutoffEnd[i + 1]);

            // Calculate gross salary for the first cutoff
            double firstCutoffGross = calculateGrossSalary(empNum, firstCutoffHours);

            // Calculate gross salary for the second cutoff
            double secondCutoffGross = calculateGrossSalary(empNum, secondCutoffHours);

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
            System.out.println("\nCutoff Date: " + cutoffStart[i] + " to " + cutoffEnd[i]);
            System.out.println("Total Hours Worked: " + firstCutoffHours);
            System.out.println("Gross Salary: " + firstCutoffGross);
            System.out.println("Net Salary: " + firstCutoffGross);

            System.out.println("\nCutoff Date: " + cutoffStart[i + 1] + " to " + cutoffEnd[i + 1]);
            System.out.println("Total Hours Worked: " + secondCutoffHours);
            System.out.println("Gross Salary: " + secondCutoffGross);

            System.out.println("Each Deduction:");
            System.out.println(" SSS: " + sss);
            System.out.println(" PhilHealth: " + philHealth);
            System.out.println(" Pag-IBIG: " + pagIBIG);
            System.out.println(" Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + secondCutoffNetSalary);

            System.out.println("\n------------------------------");
        }
    }

    public static void main(String[] args) throws Exception {
        
        System.out.println("==== MotorPH Payroll Portal ====\n");
        
        // Log in screen
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        
        // Employee logged in
        if (username.equals("employee") && password.equals("12345")) {
            System.out.println("\n-------------------------\n");            
            System.out.println("Employee logged in\n");
            
            // Display options
            System.out.println("[1]Enter your employee number");
            System.out.println("[2]Exit the program");
            
            // Takes user input for options
            System.out.print("\nSelect option (1/2): ");
            String choice = scanner.nextLine();
            
            System.out.println("\n-------------------------\n");
            
            // User chose to enter their employee number
            if (choice.equals("1")) {
                
                // Asks the user input for the employee number
                System.out.print("Enter your employee number: ");
                String empNum = scanner.nextLine();
                
                System.out.println("\n-------------------------\n");
                
                if (employeeExists(empNum)) {
                    if (!displayEmployeeDetails(empNum)) {
                        System.out.println("\nEmployee number not found.");
                    }
                } else {
                    System.out.println("\nEmployee number not found");
                }
            } else if (choice.equals("2")) {
                System.out.println("Program closed");
                System.exit(0);
            } else {
                System.out.println("\nInvalid choice");
            }
        // Payroll staff logged in
        } else if (username.equals("payroll_staff") && password.equals("12345")) {

            System.out.println("\n-------------------------\n");
            System.out.println("Payroll staff logged in");
            
            // Display options
            System.out.println("\n[1]Process Payroll");
            System.out.println("[2]Exit the program");
            
            System.out.print("\nSelect Option (1/2): ");
            String choice2 = scanner.nextLine();
            
            System.out.println("\n-------------------------");
            
            // User chose to process payroll
            if (choice2.equals("1")) {
                System.out.println("\n=== Process Payroll ===\n");
                
                // Display Options
                System.out.println("[1]One employee");
                System.out.println("[2]All employees");
                System.out.println("[3]Exit the program");
                
                System.out.print("\nSelect Option (1/2/3): ");
                String choice3 = scanner.nextLine();
                
                // User wants to process payroll for a certain employee
                if (choice3.equals("1")) {
                    System.out.print("\nEnter employee number: ");
                    String empNum = scanner.nextLine();
                    
                    processPayrollForEmployee(empNum);
                    
                // User wants process payroll for all employee    
                } else if (choice3.equals("2")) {
                    
                    // Get all employee numbers from the CSV file
                    ArrayList<String> allEmployees = getAllEmployeeNumbers();

                    // Loop through each employee number
                    for (String empNum : allEmployees) {

                        System.out.println("\n========================================");
                        System.out.println("Processing payroll for employee: " + empNum);
                        System.out.println("========================================\n");

                        // Process payroll for the current employee
                        processPayrollForEmployee(empNum);
                    }
                    
                // User wants to exit the program
                } else if (choice3.equals("3")) {
                    System.out.println("\nProgram closed.");
                    System.exit(0);
                
                // The user input is invalid
                } else {
                    System.out.println("\nInvalid choice. Closing the program");
                    System.exit(0);
                }
                    
            // User wants to exit the program
            } else if (choice2.equals("2")) {
                    System.out.println("\nProgram closed.");
                    System.exit(0);
            }
        
        // User input is invalid
        } else {
            System.out.println("\nIncorrect username and/or password");
            System.exit(0);
        }
        
        scanner.close();
    }
    
}
