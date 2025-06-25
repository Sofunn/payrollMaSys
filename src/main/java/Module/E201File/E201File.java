package Module.E201File;
import Config.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class E201File {

    public static void displayEmployeeGeneral(){
        String sql = "SELECT `employees`.`employee_id`,\n" +
                "    `employees`.`last_name`,\n" +
                "    `employees`.`first_name`,\n" +
                "    `employees`.`middle_name`,\n" +
                "    `employees`.`tin_number`,\n" +
                "    `employees`.`philhealth_number`,\n" +
                "    `employees`.`pagibig_number`,\n" +
                "    `employees`.`pay_rate`,\n" +
                "    `employees`.`employment_status`,\n" +
                "    `employees`.`department`,\n" +
                "    `employees`.`shift_start`,\n" +
                "    `employees`.`shift_end`\n" +
                "FROM `payrollmsdb`.`employees`;\n";

        Connection conn;

        try{
            conn = JDBC.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String tinNumber = rs.getString("tin_number");
                String philhealthNumber = rs.getString("philhealth_number");
                String pagibigNumber = rs.getString("pagibig_number");
                double payRate = rs.getDouble("pay_rate");
                String employmentStatus = rs.getString("employment_status");
                String department = rs.getString("department");
                Time shiftStart = rs.getTime("shift_start");
                Time shiftEnd = rs.getTime("shift_end");

                System.out.println("Employee ID: " + employeeId);
                System.out.println("Name: " + lastName + ", " + firstName + " " + middleName);
                System.out.println("TIN: " + tinNumber);
                System.out.println("PhilHealth: " + philhealthNumber);
                System.out.println("Pagibig: " + pagibigNumber);
                System.out.println("Pay Rate: " + payRate);
                System.out.println("Employment Status: " + employmentStatus);
                System.out.println("Department: " + department);
                System.out.println("Shift: " + shiftStart + " to " + shiftEnd);
                System.out.println("--------------------------------------------");
            }


            rs.close();
            stmt.close();
            conn.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object[][] getEmployeeTableData() {
        List<Object[]> dataList = new ArrayList<>();

        String query = """
    SELECT employee_id, last_name, first_name, middle_name,
           department, employment_status, shift_start, shift_end,
           pay_rate, tin_number, pagibig_number, pagibig_percentage,
           sss_number, sss_percentage, philhealth_number, philhealth_percentage,
           efund_amount, other_deductions
    FROM employees""";

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String department = rs.getString("department");
                String employmentStatus = rs.getString("employment_status");
                Time shiftStart = rs.getTime("shift_start");
                Time shiftEnd = rs.getTime("shift_end");
                double payRate = rs.getDouble("pay_rate");
                String tinNumber = rs.getString("tin_number");
                String pagibigNumber = rs.getString("pagibig_number");
                double pagibigPercentage = rs.getDouble("pagibig_percentage");
                String sssNumber = rs.getString("sss_number");
                double sssPercentage = rs.getDouble("sss_percentage");
                String philhealthNumber = rs.getString("philhealth_number");
                double philhealthPercentage = rs.getDouble("philhealth_percentage");
                double efundAmount = rs.getDouble("efund_amount");
                double otherDeductions = rs.getDouble("other_deductions");

                Object[] row = {
                        id, lastName, firstName, middleName, department, employmentStatus,
                        shiftStart, shiftEnd, payRate, tinNumber, pagibigNumber, pagibigPercentage,
                        sssNumber, sssPercentage, philhealthNumber, philhealthPercentage,
                        efundAmount, otherDeductions
                };
                dataList.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList.toArray(new Object[0][]);
    }

    public static void updateEmployeeData(String lastName, String firstName, String middleName,
                                          String department, String employmentStatus, double payRate,
                                          String tinNumber, String pagibigNumber, double pagibigPercentage,
                                          String sssNumber, double sssPercentage, String philhealthNumber,
                                          double philhealthPercentage, double efundAmount, double otherDeductions,
                                          int employeeId) {
        String query = """
        UPDATE employees
        SET last_name = ?, first_name = ?, middle_name = ?, department = ?, employment_status = ?,
            pay_rate = ?, tin_number = ?, pagibig_number = ?, pagibig_percentage = ?, sss_number = ?, 
            sss_percentage = ?, philhealth_number = ?, philhealth_percentage = ?, efund_amount = ?, 
            other_deductions = ?
        WHERE employee_id = ?
    """;

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, middleName);
            pstmt.setString(4, department);
            pstmt.setString(5, employmentStatus);
            pstmt.setDouble(6, payRate);
            pstmt.setString(7, tinNumber);
            pstmt.setString(8, pagibigNumber);
            pstmt.setDouble(9, pagibigPercentage);
            pstmt.setString(10, sssNumber);
            pstmt.setDouble(11, sssPercentage);
            pstmt.setString(12, philhealthNumber);
            pstmt.setDouble(13, philhealthPercentage);
            pstmt.setDouble(14, efundAmount);
            pstmt.setDouble(15, otherDeductions);
            pstmt.setInt(16, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee data updated successfully.");
            } else {
                System.out.println("No employee found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        displayEmployeeGeneral();
    }

}
