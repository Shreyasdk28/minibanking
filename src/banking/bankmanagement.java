package banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class bankmanagement { // class names should be PascalCase

    private static final Connection con = connection.getConnection();
    private static String sql = "";

    // --- Create Account ---
    public static boolean createAccount(String name, int passCode) {
        if (name == null || name.isEmpty() || passCode == 0) {
            System.out.println("❌ All fields are required!");
            return false;
        }

        sql = "INSERT INTO customer (cname, balance, pass_code) VALUES (?, 1000, ?)";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, name);
            st.setInt(2, passCode);

            if (st.executeUpdate() == 1) {
                System.out.println(name + ", your account has been created. You can now login!");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Username not available!");
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
        }
        return false;
    }

    // --- Login Account ---
    public static boolean loginAccount(String name, int passCode) {
        if (name == null || name.isEmpty() || passCode == 0) {
            System.out.println("❌ All fields are required!");
            return false;
        }

        sql = "SELECT * FROM customer WHERE cname = ? AND pass_code = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, name);
            st.setInt(2, passCode);

            try (ResultSet rs = st.executeQuery();
                 BufferedReader sc = new BufferedReader(new InputStreamReader(System.in))) {

                if (rs.next()) {
                    int senderAc = rs.getInt("ac_no");

                    while (true) {
                        System.out.println("\nHello, " + rs.getString("cname"));
                        System.out.println("1) Transfer Money");
                        System.out.println("2) View Balance");
                        System.out.println("5) Logout");
                        System.out.print("Enter Choice: ");

                        int ch;
                        try {
                            ch = Integer.parseInt(sc.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Enter a valid number!");
                            continue;
                        }

                        switch (ch) {
                            case 1 -> {
                                System.out.print("Enter Receiver A/c No: ");
                                int receiverAc = Integer.parseInt(sc.readLine());

                                System.out.print("Enter Amount: ");
                                int amt = Integer.parseInt(sc.readLine());

                                if (transferMoney(senderAc, receiverAc, amt)) {
                                    System.out.println("✅ Money sent successfully!\n");
                                } else {
                                    System.out.println("❌ Transaction failed!\n");
                                }
                            }
                            case 2 -> getBalance(senderAc);
                            case 5 -> {
                                return true;
                            }
                            default -> System.out.println("❌ Enter a valid choice!\n");
                        }
                    }
                }
                return false; // login failed
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Get Balance ---
    public static void getBalance(int acNo) {
        sql = "SELECT ac_no, cname, balance FROM customer WHERE ac_no = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, acNo);
            try (ResultSet rs = st.executeQuery()) {
                System.out.println("-----------------------------------------------------------");
                System.out.printf("%12s %10s %10s\n", "Account No", "Name", "Balance");

                while (rs.next()) {
                    System.out.printf("%12d %10s %10d.00\n",
                            rs.getInt("ac_no"),
                            rs.getString("cname"),
                            rs.getInt("balance"));
                }
                System.out.println("-----------------------------------------------------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Transfer Money ---
    public static boolean transferMoney(int senderAc, int receiverAc, int amount) {
        if (receiverAc == 0 || amount <= 0) {
            System.out.println("❌ Receiver account and amount are required!");
            return false;
        }

        String debitSQL = "UPDATE customer SET balance = balance - ? WHERE ac_no = ? AND balance >= ?";
        String creditSQL = "UPDATE customer SET balance = balance + ? WHERE ac_no = ?";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement debitStmt = con.prepareStatement(debitSQL);
                 PreparedStatement creditStmt = con.prepareStatement(creditSQL)) {

                // Debit from sender
                debitStmt.setInt(1, amount);
                debitStmt.setInt(2, senderAc);
                debitStmt.setInt(3, amount);

                int rowsDebited = debitStmt.executeUpdate();
                if (rowsDebited == 0) {
                    System.out.println("❌ Insufficient Balance or Invalid Sender Account!");
                    con.rollback();
                    return false;
                }

                // Credit to receiver
                creditStmt.setInt(1, amount);
                creditStmt.setInt(2, receiverAc);

                int rowsCredited = creditStmt.executeUpdate();
                if (rowsCredited == 0) {
                    System.out.println("❌ Receiver Account Not Found!");
                    con.rollback();
                    return false;
                }

                con.commit();
                System.out.println("✅ Transaction Successful!");
                return true;
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
        return false;
    }
}
