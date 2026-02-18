package com.hotel.main;

import com.hotel.service.HotelService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        HotelService service = new HotelService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1.Register");
            System.out.println("2.Login");
            System.out.println("3.View Rooms");
            System.out.println("4.Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    service.register();
                    break;

                case 2:
                    int userId = service.login();

                    if (userId != -1) {

                        while (true) {

                            System.out.println("\n1.View Rooms");
                            System.out.println("2.Book Room");
                            System.out.println("3.View My Bookings");
                            System.out.println("4.Cancel Booking");
                            System.out.println("5.Logout");

                            int ch = sc.nextInt();
                            sc.nextLine();

                            switch (ch) {
                                case 1:
                                    service.viewRooms();
                                    break;

                                case 2:
                                    service.bookRoom(userId);
                                    break;

                                case 3:
                                    service.viewMyBookings(userId);
                                    break;

                                case 4:
                                    service.cancelBooking(userId);
                                    break;

                                case 5:
                                    break;
                            }

                            if (ch == 5)
                                break;
                        }
                    }
                    break;


                case 3:
                    service.viewRooms();
                    break;

                case 4:
                    System.exit(0);
            }
        }
    }
}
