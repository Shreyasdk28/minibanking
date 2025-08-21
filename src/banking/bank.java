package banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class bank {

    public static void main(String[] args) throws IOException {  // follow naming convention
        try (BufferedReader sc = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.println("\n ->||    Welcome to InBank    ||<- \n");
                System.out.println("1) Create Account");
                System.out.println("2) Login Account");
                System.out.println("5) Exit");

                System.out.print("\n    Enter Input: ");

                int ch;
                try {
                    ch = Integer.parseInt(sc.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("❌ Enter a valid number!");
                    continue;
                }

                switch (ch) {
                    case 1 -> {
                        try {
                            System.out.print("Enter Unique UserName: ");
                            String name = sc.readLine();

                            System.out.print("Enter New Password (numeric): ");
                            int passCode = Integer.parseInt(sc.readLine());

                            if (bankmanagement.createAccount(name, passCode)) {
                                System.out.println("✅ Account Created Successfully!\n");
                            } else {
                                System.out.println("❌ Account Creation Failed!\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Password must be a number!\n");
                        }
                    }

                    case 2 -> {
                        try {
                            System.out.print("Enter UserName: ");
                            String name = sc.readLine();

                            System.out.print("Enter Password (numeric): ");
                            int passCode = Integer.parseInt(sc.readLine());

                            if (bankmanagement.loginAccount(name, passCode)) {
                                System.out.println("✅ Logout Successfully!\n");
                            } else {
                                System.out.println("❌ Login Failed!\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Password must be a number!\n");
                        }
                    }

                    case 5 -> {
                        System.out.println("Exited Successfully!\n\nThank You :)");
                        return; // exit program
                    }

                    default -> System.out.println("❌ Invalid Entry!\n");
                }
            }
        }
    }
}
