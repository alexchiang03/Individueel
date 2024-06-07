// app password: cqvk enax tlrh ozcd
//regex kan gebruiken

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Email {
    private Session newSession;
    private MimeMessage mimeMessage;

    public void sendEmail(MimeMessage mimeMessage) throws MessagingException {
        // Email gegevens
        String fromUser = "alexchiang1994@gmail.com"; // Vul je e-mail in
        String fromUserPassword = "cqvk enax tlrh ozcd"; // Vul je app-wachtwoord in
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully");
    }

    public MimeMessage draftEmail(String[] recipients, String room, String description, String date, String lockerCode, String doorCode, String price, File attachment) throws AddressException, MessagingException, IOException {
        String emailSubject = "Booking Confirmation for " + room;
        String emailBody = "<h1>Booking Details</h1>"
                + "<p>Room: " + room + "</p>"
                + "<p>Description: " + description + "</p>"
                + "<p>Date: " + date + "</p>"
                + "<p>Locker Code: " + lockerCode + "</p>"
                + "<p>Door Code: " + doorCode + "</p>"
                + "<p>Price: " + price + "</p>";

        mimeMessage = new MimeMessage(newSession);

        for (String emailRecipient : recipients) {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
        }
        mimeMessage.setSubject(emailSubject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody, "text/html");

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        // Voeg bijlage toe als deze is opgegeven
        if (attachment != null && attachment.exists()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(attachment);
            multipart.addBodyPart(attachmentBodyPart);
        }

        mimeMessage.setContent(multipart);
        return mimeMessage;
    }

    public void setupServerProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        newSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@gmail.com", "your-app-password"); // Vul je gegevens in
            }
        });
    }
}


class Attachment {
    // request and response. url submit
}

class GUI {
    private JFrame frame;
    private JTextField recipientsField;
    private JComboBox<String> roomComboBox;
    private JTextField dateField;
    private JTextField lockerCodeField;
    private JTextField doorCodeField;
    private JTextField priceField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Email Booking App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000); // Instellen van de grootte van het venster

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Ontvangers
        JLabel recipientsLabel = new JLabel("Email Ontvangers:");
        recipientsField = new JTextField(20);
        panel.add(recipientsLabel);
        panel.add(recipientsField);

        // Kamerkeuze
        JLabel roomLabel = new JLabel("Kies een kamer:");
        String[] rooms = {"Kamer 1", "Kamer 2", "Kamer 3", "Kamer 4", "Kamer 5", "Kamer 6", "Kamer 7", "Kamer 8", "Kamer 9"};
        roomComboBox = new JComboBox<>(rooms);
        panel.add(roomLabel);
        panel.add(roomComboBox);

        // Datum selectie
        JLabel dateLabel = new JLabel("Datum:");
        dateField = new JTextField(10);
        panel.add(dateLabel);
        panel.add(dateField);

        // Lockercode
        JLabel lockerLabel = new JLabel("Locker Code:");
        lockerCodeField = new JTextField(10);
        panel.add(lockerLabel);
        panel.add(lockerCodeField);

        // Deurcode
        JLabel doorLabel = new JLabel("Deur Code:");
        doorCodeField = new JTextField(10);
        panel.add(doorLabel);
        panel.add(doorCodeField);

        // Prijs
        JLabel priceLabel = new JLabel("Prijs:");
        priceField = new JTextField(10);
        panel.add(priceLabel);
        panel.add(priceField);

        // Verzendknop
        JButton sendButton = new JButton("Verzend E-mail:");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendEmail();
            }
        });
        panel.add(sendButton);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void sendEmail() {
        try {
            String[] recipients = recipientsField.getText().split(",");
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            String selectedDate = dateField.getText();
            String lockerCode = lockerCodeField.getText();
            String doorCode = doorCodeField.getText();
            String price = priceField.getText();
            File attachment = new File("path/to/your/attachment/file"); // Voeg hier het pad naar je bestand toe

            Email email = new Email();
            email.setupServerProperties();
            MimeMessage message = email.draftEmail(recipients, selectedRoom, "Fixed Description", selectedDate, lockerCode, doorCode, price, attachment);
            email.sendEmail(message);

            System.out.println("Email verzonden naar: " + Arrays.toString(recipients));
            System.out.println("Geselecteerde Kamer: " + selectedRoom);
            System.out.println("Geselecteerde Datum: " + selectedDate);
            System.out.println("Locker Code: " + lockerCode);
            System.out.println("Deur Code: " + doorCode);
            System.out.println("Prijs van de Kamer: " + price);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class invoice {
    // voor na de portfolio opdrachten.
}


public class Main {
    Session newSession = null;

    public static void main(String[] args) throws AddressException, MessagingException {
        //Setup mail server properties
        Email email = new Email();
        email.setupServerProperties();

    }
}
