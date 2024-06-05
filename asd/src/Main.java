import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

class Email {
    private Session newSession;
    private MimeMessage mimeMessage;

    public static void main(String[] args) {
        Email email = new Email();
        try {
            email.setupServerProperties();
            MimeMessage message = email.draftEmail();
            email.sendEmail(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(MimeMessage mimeMessage) throws MessagingException {
        //Email gegevens
        String fromUser = "";
        String fromUserPassword = "";
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
        System.out.println("Email send successfully");
    }

    private MimeMessage draftEmail() throws AddressException, MessagingException, IOException {
        //naar wie de mail naar toe gaat
        String[] emailRecipients = getRecipients(); // Ontvang de ontvangers
        //hoofd van de mail
        String emailSubject = "Test Mail";
        //wat in de mail staat
        String emailBody = "Test Body of my mail";
        mimeMessage = new MimeMessage(newSession);

        for (String emailRecipient : emailRecipients) {
            mimeMessage.addRecipient(Message.RecipientType.TO, new javax.mail.internet.InternetAddress(emailRecipient));
        }
        mimeMessage.setSubject(emailSubject);

        // Maak een MimeBodyPart voor de inhoud van de e-mail
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody, "text/html");

        // Attachment met padnaam (leeg voor nu)
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(""));

        // Voeg de body en attachment toe aan een MimeMultipart
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        // Stel de inhoud van de e-mail in op de MimeMultipart
        mimeMessage.setContent(multipart);
        return mimeMessage;
    }

    private String[] getRecipients() {
        // Vraag de gebruiker om ontvangers in te voeren
        Scanner scanner = new Scanner(System.in);
        System.out.println("Voer de ontvangers (gescheiden door komma's): ");
        String recipientsInput = scanner.nextLine();
        return recipientsInput.split(",");
    }

    private void setupServerProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587"); // De standaard SMTP-poort voor Gmail
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        newSession = Session.getDefaultInstance(properties, null);
    }
}


class Attachment {

}

class GUI{
    private JFrame frame;
    private JTextField recipientsField;
    private JComboBox<String> roomComboBox;
    private JTextField dateField;
    private JTextField lockerCodeField;
    private JTextField DoorCodeField;
    private JTextField priceField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.createAndShowGUI();
        });s
    }
    private void createAndShowGUI(){
        frame = new JFrame("Email Booking App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //ontvangers
        JLabel recipientsLabel = new JLabel("Email Ontvangers:");
        recipientsField = new JTextField(20);
        panel.add(recipientsLabel);
        panel.add(recipientsField);

        //kamerkeuze
        JLabel roomLabel = new JLabel("Kies een kamer:");
        String[] rooms = {"kamer 1", "Kamer 2", "Kamer 3", "Kamer 4", "Kamer 5", "Kamer 6", "Kamer 7", "Kamer 8", "Kamer 9"};
        roomComboBox = new JComboBox<>(rooms);
        panel.add(roomLabel);
        panel.add(roomComboBox);

        //datum selectie
        JLabel dateLabel = new JLabel("Datum:");
        dateField = new JTextField(10);
        panel.add(dateLabel);
        panel.add(dateField);

        //lockercode
        JLabel lockerLabel = new JLabel("Locker Code:");
        lockerCodeField = new JTextField(10);
        panel.add(lockerLabel);
        panel.add(lockerCodeField);

        //deurcode
        JLabel doorLabel = new JLabel("Deur Code:");
        DoorCodeField = new JTextField(10);
        panel.add(doorLabel);
        panel.add(DoorCodeField);

        //prijs
        JLabel priceLabel = new JLabel("Prijs:");
        priceField = new JTextField(10);
        panel.add(priceLabel);
        panel.add(priceField);

        //verzendknop
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
        try{
            String[] recipients = recipientsField.getText().split(",");
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            String selectedDate = dateField.getText();
            String lockerCode = lockerCodeField.getText();
            String doorcode = DoorCodeField.getText();
            String price = priceField.getText();

            // stuur de gegevens per email
            // Email email = new Email();
            // email.setupServerProperties();
            // MimeMessage message = email.draftEmail(recipients, selectedRoom, selectedDate, lockerCode, DoorCode, price
            // email.sendEmail(message);

            System.out.println("Email verzonden naar: " + Arrays.toString(recipients));
            System.out.println("Geselecteerde Kamer:" + selectedRoom);
            System.out.println("Geselecteerde Datum:" + selectedDate);
            System.out.println("Locker Code: " + lockerCode);
            System.out.println("Deur Code: " + doorcode);
            System.out.println("Prijs van de Kamer: " + price);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class Date{

}

class Kamer {

}

class LockerCode {

}

class DeurCode {

}

class invoice {

}


public class Main {
    Session newSession = null;

    public static void main(String[] args) throws AddressException, MessagingException {
        //Setup mail server properties
        Email email = new Email();
        email.setupServerProperties();
        email.draftEmail();
        email.sendEmail();
    }
}