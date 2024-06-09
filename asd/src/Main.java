import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// Subject interface
interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(String message);
}

// Observer interface
interface Observer {
    void update(String message);
}

public class Main extends Application {
    private TextField recipientsField;
    private ComboBox<String> roomComboBox;
    private TextField dateField;
    private TextField lockerCodeField;
    private TextField doorCodeField;
    private TextField priceField;
    private File userAttachment;
    private Email emailService;
    private TextArea logArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        emailService = new Email();
        emailService.setupServerProperties();

        primaryStage.setTitle("Email Booking App");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8, 8, 8, 8));
        grid.setVgap(6);
        grid.setHgap(8);

        // Achtergrond kleuren instellen op lichtgrijze tint
        grid.setStyle("-fx-background-color: #F0F0F0; -fx-font-size: 20px; -fx-font-family: Arial;");

        // Ontvangers
        Label recipientsLabel = new Label("Email Ontvangers:");
        GridPane.setConstraints(recipientsLabel, 0, 0);
        recipientsField = new TextField();
        recipientsField.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(recipientsField, 1, 0);

        // Kamerkeuze
        Label roomLabel = new Label("Kies een kamer:");
        GridPane.setConstraints(roomLabel, 0, 1);
        roomComboBox = new ComboBox<>();
        roomComboBox.getItems().addAll("Kamer 1", "Kamer 2", "Kamer 3", "Kamer 4", "Kamer 5", "Kamer 6", "Kamer 7", "Kamer 8", "Kamer 9");
        roomComboBox.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(roomComboBox, 1, 1);

        // Datum selectie
        Label dateLabel = new Label("Datum:");
        GridPane.setConstraints(dateLabel, 0, 2);
        dateField = new TextField();
        dateField.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(dateField, 1, 2);

        // Lockercode
        Label lockerLabel = new Label("Locker Code:");
        GridPane.setConstraints(lockerLabel, 0, 3);
        lockerCodeField = new TextField();
        lockerCodeField.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(lockerCodeField, 1, 3);

        // Deurcode
        Label doorLabel = new Label("Deur Code:");
        GridPane.setConstraints(doorLabel, 0, 4);
        doorCodeField = new TextField();
        doorCodeField.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(doorCodeField, 1, 4);

        // Prijs
        Label priceLabel = new Label("Prijs:");
        GridPane.setConstraints(priceLabel, 0, 5);
        priceField = new TextField();
        priceField.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(priceField, 1, 5);

        // Verzendknop
        Button sendButton = new Button("Verzend E-mail");
        sendButton.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(sendButton, 1, 7);
        sendButton.setOnAction(e -> sendEmail());

        // File chooser knop
        Button chooseFileButton = new Button("Kies bestand voor bijlage");
        chooseFileButton.setPrefWidth(400); // Set preferred width
        GridPane.setConstraints(chooseFileButton, 1, 6);
        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            userAttachment = fileChooser.showOpenDialog(primaryStage);
        });

        // Logweergave
        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setPrefWidth(400);
        GridPane.setConstraints(logArea, 1, 8);

        // Registreer de observer
        EmailObserver observer = new EmailObserver(logArea);
        emailService.registerObserver(observer);

        // Alle labels, knoppen, textvelden etc. krijgen hetzelfde stijl
        grid.getChildren().forEach(node -> {
            if (node instanceof Control) {
                node.setStyle("-fx-font-size: 20px; -fx-font-family: Arial;");
            }
        });

        grid.getChildren().addAll(recipientsLabel, recipientsField, roomLabel, roomComboBox, dateLabel, dateField, lockerLabel, lockerCodeField, doorLabel, doorCodeField, priceLabel, priceField, chooseFileButton, sendButton, logArea);

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendEmail() {
        try {
            String[] recipients = recipientsField.getText().split(",");
            String selectedRoom = roomComboBox.getValue();
            String selectedDate = dateField.getText();
            String lockerCode = lockerCodeField.getText();
            String doorCode = doorCodeField.getText();
            String price = priceField.getText();
            File fixedAttachment = new File("path/to/your/fixed/attachment"); // Voeg hier het pad naar je vaste bestand toe

            // Verander hier descriptie voor gebruik
            MimeMessage message = emailService.draftEmail(recipients, selectedRoom, "Fixed Description", selectedDate, lockerCode, doorCode, price, fixedAttachment, userAttachment);
            emailService.sendEmail(message);

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

// Email klasse hier (in een aparte file zou beter zijn)
class Email implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private Session newSession;
    private MimeMessage mimeMessage;

    public void sendEmail(MimeMessage mimeMessage) throws MessagingException {
        String fromUser = "alexchiang1994@gmail.com"; // Vul je e-mail in
        String fromUserPassword = "cqvk enax tlrh ozcd"; // Vul je app-wachtwoord in
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully");

        notifyObservers("Email sent to: " + Arrays.toString(mimeMessage.getAllRecipients()));
    }

    public MimeMessage draftEmail(String[] recipients, String room, String description, String date, String lockerCode, String doorCode, String price, File fixedAttachment, File userAttachment) throws MessagingException, IOException {
        String emailSubject = "Booking Confirmation for " + room;
        String emailBody = "<div style=\"text-align: center;\">"; //centreren van de tekst
        emailBody += "<h1>Booking Details</h1>"
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

        if (fixedAttachment != null && fixedAttachment.exists()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(fixedAttachment);
            multipart.addBodyPart(attachmentBodyPart);
        }

        if (userAttachment != null && userAttachment.exists()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(userAttachment);
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
                return new PasswordAuthentication("alexchiang1994@gmail.com", "cqvk enax tlrh ozcd"); // Vul je gegevens in
            }
        });
    }

    // Observer methods
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// EmailObserver class
class EmailObserver implements Observer {
    private final TextArea logArea;

    public EmailObserver(TextArea logArea) {
        this.logArea = logArea;
    }

    @Override
    public void update(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }
}
