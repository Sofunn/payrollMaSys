package Module.Payroll;

import Config.JDBC;
import Entity.Employee;
import Entity.PayrollClass;
import Entity.Formula;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Payroll {
    public static List<Employee> retrieveAllEmployee(){
        List<Employee> employees = new ArrayList<>();
        Connection conn;

        try{
            String sql = "SELECT `employee_id`, `last_name`, `first_name`, `middle_name`, `pay_rate`, `department`, `employment_status` " +
                    "FROM `payrollmsdb`.`employees`";
            conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                BigDecimal payRate = rs.getBigDecimal("pay_rate");
                String department = rs.getString("department");
                String employmentStatus = rs.getString("employment_status");

                Employee emp = new Employee(employeeId, lastName, firstName, middleName, payRate, department, employmentStatus);
                employees.add(emp);
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    public static PayrollClass createPayroll(Employee emp, Date period_start, Date period_end){

        Connection conn;
        try{
            String sql = "INSERT INTO payrollmsdb.payroll (employee_id, period_start, period_end) VALUES (?, ?, ?)";

            conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,emp.getEmployee_id());
            stmt.setDate(2, period_start);
            stmt.setDate(3, period_end);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            return new PayrollClass(emp.getEmployee_id(), period_start,period_end, emp.getPay_rate(), emp.getLast_name() + " " + emp.getFirst_name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PayrollClass> retrieveAllPayrolls() {
        List<PayrollClass> payrollList = new ArrayList<>();
        Connection conn;

        try {
            // Fetch the latest payroll record for each employee
            String sql = "SELECT p.employee_id, p.period_start, p.period_end, " +
                    "MAX(p.days_present) AS days_present, " +
                    "MAX(p.overtime_hours) AS overtime_hours, " +
                    "MAX(p.nd_hours) AS nd_hours, " +
                    "MAX(p.sholiday_hours) AS sholiday_hours, " +
                    "MAX(p.lholiday_hours) AS lholiday_hours, " +
                    "MAX(p.late_minutes) AS late_minutes, " +
                    "e.first_name, e.last_name, e.pay_rate " +
                    "FROM payrollmsdb.payroll p " +
                    "JOIN payrollmsdb.employees e ON p.employee_id = e.employee_id " +
                    "WHERE p.period_start = (SELECT MAX(period_start) FROM payrollmsdb.payroll WHERE employee_id = p.employee_id) " +
                    "GROUP BY p.employee_id, p.period_start, p.period_end, e.first_name, e.last_name, e.pay_rate";

            conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String employeeName = rs.getString("last_name") + " " + rs.getString("first_name");
                BigDecimal payRate = rs.getBigDecimal("pay_rate");

                PayrollClass payroll = new PayrollClass(
                        rs.getInt("employee_id"),
                        rs.getDate("period_start"),
                        rs.getDate("period_end"),
                        rs.getDouble("days_present"),
                        rs.getDouble("overtime_hours"),
                        rs.getDouble("nd_hours"),
                        rs.getDouble("sholiday_hours"),
                        rs.getDouble("lholiday_hours"),
                        rs.getDouble("late_minutes"),
                        payRate,
                        employeeName
                );

                System.out.println("Adding payroll: " + payroll.getEmployee_id() + ", " + payroll.getPeriod_start() + ", " + payroll.getPeriod_end());
                payrollList.add(payroll);
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return payrollList;
    }




    public static List<PayrollClass> generatePayrollForAllEmployees( Date period_start, Date period_end) {
        List<PayrollClass> payrolls = new ArrayList<>();
        Connection conn = null;

        try {
            conn = JDBC.getConnection();
            conn.setAutoCommit(false); // Use transaction to ensure consistency

            // Step 1: Retrieve all employees
            String fetchSql = "SELECT `employee_id`, `last_name`, `first_name`, `pay_rate`, `department` FROM `payrollmsdb`.`employees`";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
            ResultSet rs = fetchStmt.executeQuery();

            // Step 2: Prepare payroll insert statement
            String insertSql = "INSERT INTO payrollmsdb.payroll (employee_id, period_start, period_end) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);

            // Step 3: Iterate and insert payroll for each employee
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                BigDecimal payRate = rs.getBigDecimal("pay_rate");
                String department = rs.getString("department");

                // Create and collect PayrollClass object
                Employee emp = new Employee(employeeId, lastName, firstName, payRate, department);
                PayrollClass payroll = new PayrollClass(employeeId, period_start, period_end, payRate, firstName + " " + lastName);
                payrolls.add(payroll);

                // Insert into payroll table
                insertStmt.setInt(1, employeeId);
                insertStmt.setDate(2, period_start);
                insertStmt.setDate(3, period_end);
                insertStmt.addBatch(); // Batch for performance
            }

            insertStmt.executeBatch(); // Execute all insertions
            conn.commit(); // Commit transaction

            // Cleanup
            fetchStmt.close();
            insertStmt.close();
            conn.close();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back if anything fails
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        }

        return payrolls;
    }


    public static void updatePayroll(PayrollClass Payroll){

        Connection conn;
        try{
            String sql = "UPDATE payrollmsdb.payroll SET " +
                    "days_present = ?, " +
                    "overtime_hours = ?, " +
                    "nd_hours = ?, " +
                    "sholiday_hours = ?, " +
                    "lholiday_hours = ?, " +
                    "late_minutes = ?, " +
                    "wage = ? " +
                    "WHERE employee_id = ?";

            conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, Payroll.getDays_present());
            stmt.setDouble(2, Payroll.getOvertime_hours());
            stmt.setDouble(3, Payroll.getNd_hours());
            stmt.setDouble(4, Payroll.getSholiday_hours());
            stmt.setDouble(5, Payroll.getLholiday_hours());
            stmt.setDouble(6, Payroll.getLate_minutes());
            stmt.setBigDecimal(7, Payroll.getPayrate());
            stmt.setInt(8, Payroll.getEmployee_id());

            stmt.executeUpdate();
            stmt.close();

            //Calculation

            Payroll.setLate_amount(Formula.computeLateAmount(Payroll.getPayrate(), Payroll.getLate_minutes()));
            Payroll.setWage(Formula.computeWage(Payroll.getPayrate(),Payroll.getLate_amount(),Payroll.getDays_present()));
            Payroll.setOvertime_amount(Formula.computeOvertimeAmount(Payroll.getPayrate(), Payroll.getOvertime_hours()));
            Payroll.setNd_amount(Formula.computeNightDifferentialAmount(Payroll.getPayrate(), Payroll.getNd_hours()));
            Payroll.setSholiday_amount(Formula.computeSpecialHolidayAmount(Payroll.getPayrate(), Payroll.getSholiday_hours()));
            Payroll.setLholiday_amount(Formula.computeLegalHolidayAmount(Payroll.getPayrate(), Payroll.getLholiday_hours()));
            Payroll.setGross_pay(Formula.computeTotalGrossAmount(Payroll.getWage(),Payroll.getOvertime_amount(), Payroll.getNd_amount(), Payroll.getSholiday_amount(), Payroll.getLholiday_amount()));
            Payroll.setTotal_deduction(Formula.computeTotalDeductionAmount(Payroll.getSss_deduction(), Payroll.getPhilhealth_deduction(),Payroll.getPagibig_deduction(), Payroll.getEfund_deduction(), Payroll.getOther_deduction()));
            Payroll.setNet_pay(Formula.computeNetPay(Payroll.getGross_pay(), Payroll.getTotal_deduction(), Payroll.getAllowance_adjustment(), Payroll.getSalary_adjustment(), Payroll.getOther_compensations()));

            String sql1 = "UPDATE payrollmsdb.payroll SET " +
                    "overtime_amount = ?, " +
                    "nd_amount = ?, " +
                    "sholiday_amount = ?, " +
                    "lholiday_amount = ?, " +
                    "late_amount = ?, " +
                    "wage = ?, " +
                    "philhealth_deduction = ?, " +
                    "sss_deduction = ?, " +
                    "pagibig_deduction = ?, " +
                    "efund_deduction = ?, " +
                    "other_deduction = ?, " +
                    "salary_adjustment = ?, " +
                    "allowance_adjustment = ?, " +
                    "other_compensations = ?, " +
                    "total_deduction = ?, " +
                    "gross_pay = ?, " +
                    "net_pay = ? " +
                    "WHERE employee_id = ?";


            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setBigDecimal(1, Payroll.getOvertime_amount());
            stmt1.setBigDecimal(2, Payroll.getNd_amount());
            stmt1.setBigDecimal(3, Payroll.getSholiday_amount());
            stmt1.setBigDecimal(4, Payroll.getLholiday_amount());
            stmt1.setBigDecimal(5, Payroll.getLate_amount());
            stmt1.setBigDecimal(6, Payroll.getWage());
            stmt1.setBigDecimal(7, Payroll.getPhilhealth_deduction());
            stmt1.setBigDecimal(8, Payroll.getSss_deduction());
            stmt1.setBigDecimal(9, Payroll.getPagibig_deduction());
            stmt1.setBigDecimal(10, Payroll.getEfund_deduction());
            stmt1.setBigDecimal(11, Payroll.getOther_deduction());
            stmt1.setBigDecimal(12, Payroll.getSalary_adjustment());
            stmt1.setBigDecimal(13, Payroll.getAllowance_adjustment());
            stmt1.setBigDecimal(14, Payroll.getOther_compensations());
            stmt1.setBigDecimal(15, Payroll.getTotal_deduction());
            stmt1.setBigDecimal(16, Payroll.getGross_pay());
            stmt1.setBigDecimal(17, Payroll.getNet_pay());
            stmt1.setInt(18, Payroll.getEmployee_id());


            stmt1.executeUpdate();
            stmt1.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void viewPayroll(PayrollClass payroll) {
        String sql = "SELECT `payroll`.`payroll_id`,\n" +
                "    `payroll`.`employee_id`,\n" +
                "    `payroll`.`period_start`,\n" +
                "    `payroll`.`period_end`,\n" +
                "    `payroll`.`days_present`,\n" +
                "    `payroll`.`overtime_hours`,\n" +
                "    `payroll`.`nd_hours`,\n" +
                "    `payroll`.`sholiday_hours`,\n" +
                "    `payroll`.`lholiday_hours`,\n" +
                "    `payroll`.`late_minutes`,\n" +
                "    `payroll`.`overtime_amount`,\n" +
                "    `payroll`.`nd_amount`,\n" +
                "    `payroll`.`sholiday_amount`,\n" +
                "    `payroll`.`lholiday_amount`,\n" +
                "    `payroll`.`late_amount`,\n" +
                "    `payroll`.`wage`,\n" +
                "    `payroll`.`philhealth_deduction`,\n" +
                "    `payroll`.`sss_deduction`,\n" +
                "    `payroll`.`pagibig_deduction`,\n" +
                "    `payroll`.`efund_deduction`,\n" +
                "    `payroll`.`other_deduction`,\n" +
                "    `payroll`.`salary_adjustment`,\n" +
                "    `payroll`.`allowance_adjustment`,\n" +
                "    `payroll`.`other_compensations`,\n" +
                "    `payroll`.`total_deduction`,\n" +
                "    `payroll`.`gross_pay`,\n" +
                "    `payroll`.`net_pay`\n" +
                "FROM `payrollmsdb`.`payroll` \n" +
                "WHERE employee_id = ?;";

        Connection conn;

        try {
            conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, payroll.getEmployee_id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payroll.setPayroll_id(rs.getInt("payroll_id"));
                    payroll.setEmployee_id(rs.getInt("employee_id"));
                    payroll.setPeriod_start(rs.getDate("period_start"));
                    payroll.setPeriod_end(rs.getDate("period_end"));
                    payroll.setDays_present(rs.getInt("days_present"));
                    payroll.setOvertime_hours(rs.getDouble("overtime_hours"));
                    payroll.setNd_hours(rs.getDouble("nd_hours"));
                    payroll.setSholiday_hours(rs.getDouble("sholiday_hours"));
                    payroll.setLholiday_hours(rs.getDouble("lholiday_hours"));
                    payroll.setLate_minutes(rs.getInt("late_minutes"));
                    payroll.setOvertime_amount(rs.getBigDecimal("overtime_amount"));
                    payroll.setNd_amount(rs.getBigDecimal("nd_amount"));
                    payroll.setSholiday_amount(rs.getBigDecimal("sholiday_amount"));
                    payroll.setLholiday_amount(rs.getBigDecimal("lholiday_amount"));
                    payroll.setLate_amount(rs.getBigDecimal("late_amount"));
                    payroll.setWage(rs.getBigDecimal("wage"));
                    payroll.setPhilhealth_deduction(rs.getBigDecimal("philhealth_deduction"));
                    payroll.setSss_deduction(rs.getBigDecimal("sss_deduction"));
                    payroll.setPagibig_deduction(rs.getBigDecimal("pagibig_deduction"));
                    payroll.setEfund_deduction(rs.getBigDecimal("efund_deduction"));
                    payroll.setOther_deduction(rs.getBigDecimal("other_deduction"));
                    payroll.setSalary_adjustment(rs.getBigDecimal("salary_adjustment"));
                    payroll.setAllowance_adjustment(rs.getBigDecimal("allowance_adjustment"));
                    payroll.setOther_compensations(rs.getBigDecimal("other_compensations"));
                    payroll.setTotal_deduction(rs.getBigDecimal("total_deduction"));
                    payroll.setGross_pay(rs.getBigDecimal("gross_pay"));
                    payroll.setNet_pay(rs.getBigDecimal("net_pay"));

                    // Print all values
                    System.out.println("Payroll ID: " + payroll.getPayroll_id());
                    System.out.println("Employee ID: " + payroll.getEmployee_id());
                    System.out.println("Period Start: " + payroll.getPeriod_start());
                    System.out.println("Period End: " + payroll.getPeriod_end());
                    System.out.println("Days Present: " + payroll.getDays_present());
                    System.out.println("Overtime Hours: " + payroll.getOvertime_hours());
                    System.out.println("Night Differential Hours: " + payroll.getNd_hours());
                    System.out.println("Special Holiday Hours: " + payroll.getSholiday_hours());
                    System.out.println("Legal Holiday Hours: " + payroll.getLholiday_hours());
                    System.out.println("Late Minutes: " + payroll.getLate_minutes());

                    System.out.println("Overtime Amount: " + payroll.getOvertime_amount());
                    System.out.println("Night Differential Amount: " + payroll.getNd_amount());
                    System.out.println("Special Holiday Amount: " + payroll.getSholiday_amount());
                    System.out.println("Legal Holiday Amount: " + payroll.getLholiday_amount());
                    System.out.println("Late Amount: " + payroll.getLate_amount());

                    System.out.println("Wage: " + payroll.getWage());
                    System.out.println("PhilHealth Deduction: " + payroll.getPhilhealth_deduction());
                    System.out.println("SSS Deduction: " + payroll.getSss_deduction());
                    System.out.println("Pag-IBIG Deduction: " + payroll.getPagibig_deduction());
                    System.out.println("Efund Deduction: " + payroll.getEfund_deduction());
                    System.out.println("Other Deduction: " + payroll.getOther_deduction());

                    System.out.println("Salary Adjustment: " + payroll.getSalary_adjustment());
                    System.out.println("Allowance Adjustment: " + payroll.getAllowance_adjustment());
                    System.out.println("Other Compensations: " + payroll.getOther_compensations());

                    System.out.println("Total Deduction: " + payroll.getTotal_deduction());
                    System.out.println("Gross Pay: " + payroll.getGross_pay());
                    System.out.println("Net Pay: " + payroll.getNet_pay());

                } else {
                    System.out.println("No payroll record found for employee_id: " + payroll.getEmployee_id());
                }

                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<Integer, PayrollClass> generatePayrollMap(Date period_start,  Date period_end) {
        List<Employee> emplist = retrieveAllEmployee();
        Map<Integer, PayrollClass> payrollMap = new HashMap<>();

        for (Employee employee : emplist) {
            PayrollClass payroll = createPayroll(employee, period_start, period_end);
            payrollMap.put(employee.getEmployee_id(), payroll);
        }

        return payrollMap;
    }

    public static void loadTimecards(Date periodStart, Date periodEnd) {
        Connection conn;
        try {
            // Retrieve all employees
            List<Employee> employees = retrieveAllEmployee();

            conn = JDBC.getConnection();

            for (Employee employee : employees) {
                int employeeId = employee.getEmployee_id();

                // Step 1: Retrieve total hours clocked from the timecard database
                String timecardSql = "SELECT SUM(hours_clocked) AS total_hours " +
                        "FROM payrollmsdb.timecard " +
                        "WHERE employee_id = ? AND date BETWEEN ? AND ?";
                PreparedStatement timecardStmt = conn.prepareStatement(timecardSql);
                timecardStmt.setInt(1, employeeId);
                timecardStmt.setDate(2, periodStart);
                timecardStmt.setDate(3, periodEnd);

                ResultSet rs = timecardStmt.executeQuery();
                double totalHours = 0.0;
                if (rs.next()) {
                    totalHours = rs.getDouble("total_hours");
                }

                rs.close();
                timecardStmt.close();

                // Step 2: Compute days present using Formula.computeDaysPresent
                double daysPresent = Formula.computeDaysPresent(totalHours);

                // Step 3: Update days_present in the payroll database
                String updateSql = "UPDATE payrollmsdb.payroll SET days_present = ? " +
                        "WHERE employee_id = ? AND period_start = ? AND period_end = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDouble(1, daysPresent);
                updateStmt.setInt(2, employeeId);
                updateStmt.setDate(3, periodStart);
                updateStmt.setDate(4, periodEnd);

                updateStmt.executeUpdate();
                updateStmt.close();
            }

            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }






    public static void main (String[] args){
        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());


        //Button to create payroll period
        Map<Integer, PayrollClass> payrollMap = generatePayrollMap(sqlDate,sqlDate);


        double days_present = 20.0;
        double overtime_hours = 10.5;
        double nd_hours = 6.0;
        double sholiday_hours = 2.5;
        double lholiday_hours = 1.0;
        double late_minutes = 15.0;
        BigDecimal pay_rate = new BigDecimal("2000");

        payrollMap.get(182).setPayrate(pay_rate);
        payrollMap.get(182).setLate_minutes(15.0);
        payrollMap.get(182).setDays_present(days_present);
        payrollMap.get(182).setOvertime_hours(overtime_hours);
        payrollMap.get(182).setNd_hours(nd_hours);
        payrollMap.get(182).setSholiday_hours(sholiday_hours);
        payrollMap.get(182).setLholiday_hours(lholiday_hours);
        payrollMap.get(182).setLate_minutes(late_minutes);


        updatePayroll(payrollMap.get(182));

        viewPayroll(payrollMap.get(182));






    }

}
