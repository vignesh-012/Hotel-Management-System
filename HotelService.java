package com.hotel.service;

import com.hotel.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class HotelService {

    Scanner sc = new Scanner(System.in);
 // VIEW MY BOOKINGS
    public void viewMyBookings(int userId) throws Exception {

        Connection con = DBConnection.getConnection();

        String sql = "SELECT b.booking_id, r.type, b.check_in, b.check_out, b.total_amount, b.status " +
                     "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
                     "WHERE b.user_id = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println(
                    "Booking ID: " + rs.getInt("booking_id") +
                    " | Room: " + rs.getString("type") +
                    " | Check-in: " + rs.getDate("check_in") +
                    " | Check-out: " + rs.getDate("check_out") +
                    " | Amount: " + rs.getDouble("total_amount") +
                    " | Status: " + rs.getString("status")
            );
        }

        con.close();
    }


    // REGISTER
    public void register() throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "INSERT INTO users(name,email,password,role) VALUES (?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setString(4, "CUSTOMER");

        ps.executeUpdate();
        System.out.println("Registered Successfully!");

        con.close();
    }

    // LOGIN
    public int login() throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Login Successful!");
            return rs.getInt("id");
        } else {
            System.out.println("Invalid Credentials");
            return -1;
        }
    }
 // CANCEL BOOKING
    public void cancelBooking(int userId) throws Exception {

        viewMyBookings(userId);

        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = sc.nextInt();
        sc.nextLine();

        Connection con = DBConnection.getConnection();

        String sql = "UPDATE bookings SET status='CANCELLED' WHERE booking_id=? AND user_id=?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, bookingId);
        ps.setInt(2, userId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Booking Cancelled Successfully!");
        } else {
            System.out.println("Invalid Booking ID!");
        }

        con.close();
    }

    // VIEW ROOMS
    public void viewRooms() throws Exception {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM rooms");

        while (rs.next()) {
            System.out.println(
                    rs.getInt("room_id") + " | " +
                    rs.getString("type") + " | " +
                    rs.getDouble("price")
            );
        }

        con.close();
    }

    // BOOK ROOM
    public void bookRoom(int userId) throws Exception {

        Connection con = DBConnection.getConnection();

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Check-in (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(sc.nextLine());

        System.out.print("Check-out (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(sc.nextLine());

        if (!checkOut.isAfter(checkIn)) {
            System.out.println("Invalid Date Range!");
            return;
        }

        String checkSQL =
                "SELECT COUNT(*) FROM bookings " +
                "WHERE room_id=? AND status='BOOKED' " +
                "AND NOT (check_out <= ? OR check_in >= ?)";

        PreparedStatement ps = con.prepareStatement(checkSQL);
        ps.setInt(1, roomId);
        ps.setDate(2, Date.valueOf(checkIn));
        ps.setDate(3, Date.valueOf(checkOut));

        ResultSet rs = ps.executeQuery();
        rs.next();

        if (rs.getInt(1) > 0) {
            System.out.println("Room Not Available!");
            return;
        }

        PreparedStatement ps2 =
                con.prepareStatement("SELECT price FROM rooms WHERE room_id=?");
        ps2.setInt(1, roomId);
        ResultSet rs2 = ps2.executeQuery();
        rs2.next();

        double price = rs2.getDouble("price");
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = days * price;

        String insertSQL =
                "INSERT INTO bookings(user_id,room_id,check_in,check_out,total_amount,status) " +
                "VALUES (?,?,?,?,?,?)";

        PreparedStatement ps3 = con.prepareStatement(insertSQL);
        ps3.setInt(1, userId);
        ps3.setInt(2, roomId);
        ps3.setDate(3, Date.valueOf(checkIn));
        ps3.setDate(4, Date.valueOf(checkOut));
        ps3.setDouble(5, total);
        ps3.setString(6, "BOOKED");

        ps3.executeUpdate();

        System.out.println("Room Booked Successfully! Total = " + total);

        con.close();
    }
}
