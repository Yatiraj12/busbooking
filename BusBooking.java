import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class BusBooking extends JFrame {
    private JButton[] seats;
    private HashMap<Integer, String> seatStatus = new HashMap<>(); // Store seat status
    private int selectedSeatNumber;

    public BusBooking() {
        setTitle("Sleeper Bus Ticket Booking");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel title = new JLabel("Sleeper Bus Seat Booking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Create seat selection panel (Bus-like layout)
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(4, 4, 10, 10)); // 4x4 grid for seat layout
        seats = new JButton[16]; // Example: 16 seats
        for (int i = 0; i < seats.length; i++) {
            seats[i] = new JButton("Seat " + (i + 1));
            seats[i].setBackground(Color.GREEN);
            seats[i].addActionListener(new SeatSelectionListener(i + 1));
            seatPanel.add(seats[i]);
        }

        add(seatPanel, BorderLayout.CENTER);

        // Status Label
        JLabel statusLabel = new JLabel("Select a seat to book", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Seat selection listener
    private class SeatSelectionListener implements ActionListener {
        private int seatNumber;

        public SeatSelectionListener(int seatNumber) {
            this.seatNumber = seatNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton selectedSeat = (JButton) e.getSource();

            // Check if the seat is available
            if (selectedSeat.getBackground() == Color.GREEN) {
                selectedSeatNumber = seatNumber;
                
                // Ask for passenger gender
                String[] genderOptions = {"Male", "Female"};
                String gender = (String) JOptionPane.showInputDialog(null, 
                        "Select Gender for Seat " + selectedSeatNumber, 
                        "Gender Selection", 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        genderOptions, 
                        genderOptions[0]);

                if (gender != null) { // Proceed if gender is selected
                    openPassengerDetailsForm(gender);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seat already booked.");
            }
        }
    }

    // Open Passenger Details Form with auto-filled timings
    private void openPassengerDetailsForm(String gender) {
        JFrame passengerDetailsFrame = new JFrame("Passenger Details");
        passengerDetailsFrame.setSize(400, 500);
        passengerDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        passengerDetailsFrame.setLayout(new GridLayout(9, 2));

        // Fields for name, age, phone, boarding point, destination point
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField phoneField = new JTextField();

        // Karnataka districts for boarding and destination points
        String[] karnatakaDistricts = {
            "Bangalore", "Mysore", "Mangalore", "Hubli", "Dharwad", "Belgaum", "Shimoga",
            "Bellary", "Udupi", "Gulbarga", "Bijapur", "Chikmagalur", "Tumkur", "Raichur"
        };
        JComboBox<String> boardingPointBox = new JComboBox<>(karnatakaDistricts);
        JComboBox<String> destinationBox = new JComboBox<>(karnatakaDistricts);

        JTextField journeyDateField = new JTextField("dd-mm-yyyy");

        // Automatically set departure and arrival times (current time + some offset for arrival)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String departureTime = sdf.format(new Date()); // Current time as departure time
        String arrivalTime = sdf.format(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000)); // Add 2 hours to the current time for arrival

        passengerDetailsFrame.add(new JLabel("Name:"));
        passengerDetailsFrame.add(nameField);
        passengerDetailsFrame.add(new JLabel("Age:"));
        passengerDetailsFrame.add(ageField);
        passengerDetailsFrame.add(new JLabel("Phone:"));
        passengerDetailsFrame.add(phoneField);
        passengerDetailsFrame.add(new JLabel("Boarding Point:"));
        passengerDetailsFrame.add(boardingPointBox);
        passengerDetailsFrame.add(new JLabel("Destination Point:"));
        passengerDetailsFrame.add(destinationBox);
        passengerDetailsFrame.add(new JLabel("Journey Date:"));
        passengerDetailsFrame.add(journeyDateField);
        passengerDetailsFrame.add(new JLabel("Departure Time:"));
        passengerDetailsFrame.add(new JLabel(departureTime)); // Display auto-filled departure time
        passengerDetailsFrame.add(new JLabel("Arrival Time:"));
        passengerDetailsFrame.add(new JLabel(arrivalTime)); // Display auto-filled arrival time

        // Button to submit passenger details and move to payment
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            String name = nameField.getText();
            String age = ageField.getText();
            String phone = phoneField.getText();
            String boardingPoint = (String) boardingPointBox.getSelectedItem();
            String destination = (String) destinationBox.getSelectedItem();
            String journeyDate = journeyDateField.getText();

            if (name.isEmpty() || age.isEmpty() || phone.isEmpty() || journeyDate.isEmpty()) {
                JOptionPane.showMessageDialog(passengerDetailsFrame, "Please fill in all the details.");
            } else {
                passengerDetailsFrame.dispose(); // Close the passenger details window
                openPaymentPage(name, age, phone, boardingPoint, destination, journeyDate, departureTime, arrivalTime);
            }
        });

        passengerDetailsFrame.add(new JLabel()); // Empty label for spacing
        passengerDetailsFrame.add(nextButton);

        passengerDetailsFrame.setVisible(true);
    }

    // Open Payment Page
    private void openPaymentPage(String name, String age, String phone, String boardingPoint, String destination, String journeyDate, String departureTime, String arrivalTime) {
        JFrame paymentFrame = new JFrame("Payment");
        paymentFrame.setSize(300, 200);
        paymentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        paymentFrame.setLayout(new GridLayout(5, 2));

        JLabel paymentLabel = new JLabel("Select payment method:");
        JButton cardPaymentButton = new JButton("Card");
        JButton upiPaymentButton = new JButton("UPI");

        cardPaymentButton.addActionListener(e -> {
            paymentFrame.dispose();
            openCardPaymentForm(name, age, phone, boardingPoint, destination, journeyDate, departureTime, arrivalTime);
        });

        upiPaymentButton.addActionListener(e -> {
            paymentFrame.dispose();
            confirmBooking(name, age, phone, boardingPoint, destination, journeyDate, departureTime, arrivalTime);
        });

        paymentFrame.add(paymentLabel);
        paymentFrame.add(cardPaymentButton);
        paymentFrame.add(new JLabel()); // Empty for layout
        paymentFrame.add(upiPaymentButton);

        paymentFrame.setVisible(true);
    }

    // Open Card Payment Form
    private void openCardPaymentForm(String name, String age, String phone, String boardingPoint, String destination, String journeyDate, String departureTime, String arrivalTime) {
        JFrame cardPaymentFrame = new JFrame("Card Payment");
        cardPaymentFrame.setSize(400, 300);
        cardPaymentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cardPaymentFrame.setLayout(new GridLayout(5, 2));

        // Card details
        JTextField cardNumberField = new JTextField();
        JTextField cardNameField = new JTextField();
        JTextField cvvField = new JTextField();
        JTextField expireDateField = new JTextField("MM/YY");

        cardPaymentFrame.add(new JLabel("Card Number:"));
        cardPaymentFrame.add(cardNumberField);
        cardPaymentFrame.add(new JLabel("Cardholder Name:"));
        cardPaymentFrame.add(cardNameField);
        cardPaymentFrame.add(new JLabel("CVV:"));
        cardPaymentFrame.add(cvvField);
        cardPaymentFrame.add(new JLabel("Expire Date:"));
        cardPaymentFrame.add(expireDateField);

        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> {
            String cardNumber = cardNumberField.getText();
            String cardName = cardNameField.getText();
            String cvv = cvvField.getText();
            String expireDate = expireDateField.getText();

            if (cardNumber.isEmpty() || cardName.isEmpty() || cvv.isEmpty() || expireDate.isEmpty()) {
                JOptionPane.showMessageDialog(cardPaymentFrame, "Please fill in all card details.");
            } else {
                cardPaymentFrame.dispose();
                confirmBooking(name, age, phone, boardingPoint, destination, journeyDate, departureTime, arrivalTime);
            }
        });

        cardPaymentFrame.add(new JLabel()); // Empty label for spacing
        cardPaymentFrame.add(payButton);

        cardPaymentFrame.setVisible(true);
    }

    // Confirm Booking and Show Ticket
    private void confirmBooking(String name, String age, String phone, String boardingPoint, String destination, String journeyDate, String departureTime, String arrivalTime) {
        // Mark seat as booked
        seats[selectedSeatNumber - 1].setBackground(Color.RED);
        seatStatus.put(selectedSeatNumber, "Booked");

        // Generate random PNR number
        String pnrNumber = "PNR" + new Random().nextInt(10000);

        // Show booking confirmation
        JFrame confirmationFrame = new JFrame("Booking Confirmed");
        confirmationFrame.setSize(400, 400);
        confirmationFrame.setLayout(new GridLayout(9, 1));

        confirmationFrame.add(new JLabel("Ticket Confirmed!", SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Seat Number: " + selectedSeatNumber, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("PNR: " + pnrNumber, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Name: " + name, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Age: " + age, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Boarding: " + boardingPoint, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Destination: " + destination, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Journey Date: " + journeyDate, SwingConstants.CENTER));
        confirmationFrame.add(new JLabel("Departure: " + departureTime + " | Arrival: " + arrivalTime, SwingConstants.CENTER));

        confirmationFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new BusBooking();
    }
}
