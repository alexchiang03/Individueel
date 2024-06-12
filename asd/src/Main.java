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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.scene.control.TextArea;

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
    private DatePicker datePicker;
    private TextField lockerCodeField;
    private TextField doorCodeField;
    private TextField priceField;
    private File userAttachment;
    private BookingEmailService bookingEmailService;
    private TextArea logArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Email emailService = new Email();
        bookingEmailService = new BookingEmailService(emailService);

        primaryStage.setTitle("Email Booking App");

        GridPane grid = createGridPane();
        configureUIComponents(grid, primaryStage);

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8, 8, 8, 8));
        grid.setVgap(6);
        grid.setHgap(8);
        grid.setStyle("-fx-background-color: #F0F0F0; -fx-font-size: 20px; -fx-font-family: Arial;");
        return grid;
    }

    private void configureUIComponents(GridPane grid, Stage primaryStage) {
        recipientsField = createTextField("Email Ontvangers:", 0, grid);
        roomComboBox = createComboBox("Kies een kamer:", 1, grid);
        datePicker = createDatePicker("Datum:", 2, grid);
        lockerCodeField = createTextField("Locker Code:", 3, grid);
        doorCodeField = createTextField("Deur Code:", 4, grid);
        priceField = createTextField("Prijs:", 5, grid);

        Button chooseFileButton = createButton("Kies bestand voor bijlage", 6, grid);
        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            userAttachment = fileChooser.showOpenDialog(primaryStage);
        });

        Button sendButton = createButton("Verzend E-mail", 7, grid);
        sendButton.setOnAction(e -> sendEmail());

        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setPrefWidth(600);
        GridPane.setConstraints(logArea, 1, 8);
        grid.getChildren().add(logArea);

        EmailObserver observer = new EmailObserver(logArea);
        bookingEmailService.getEmailService().registerObserver(observer);
    }

    private TextField createTextField(String labelText, int rowIndex, GridPane grid) {
        Label label = new Label(labelText);
        GridPane.setConstraints(label, 0, rowIndex);
        TextField textField = new TextField();
        textField.setPrefWidth(400);
        GridPane.setConstraints(textField, 1, rowIndex);
        grid.getChildren().addAll(label, textField);
        return textField;
    }

    private DatePicker createDatePicker(String labelText, int rowIndex, GridPane grid) {
        Label label = new Label(labelText);
        GridPane.setConstraints(label, 0, rowIndex);
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(400);
        GridPane.setConstraints(datePicker, 1, rowIndex);
        grid.getChildren().addAll(label, datePicker);
        return datePicker;
    }

    private ComboBox<String> createComboBox(String labelText, int rowIndex, GridPane grid) {
        Label label = new Label(labelText);
        GridPane.setConstraints(label, 0, rowIndex);
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Kamer 1", "Kamer 2", "Kamer 3", "Kamer 4", "Kamer 5", "Kamer 6", "Kamer 7", "Kamer 8", "Kamer 9");
        comboBox.setPrefWidth(400);
        GridPane.setConstraints(comboBox, 1, rowIndex);
        grid.getChildren().addAll(label, comboBox);
        return comboBox;
    }

    private Button createButton(String buttonText, int rowIndex, GridPane grid) {
        Button button = new Button(buttonText);
        button.setPrefWidth(400);
        GridPane.setConstraints(button, 1, rowIndex);
        grid.getChildren().add(button);
        return button;
    }

    private void sendEmail() {
        try {
            String[] recipients = recipientsField.getText().split(",");
            LocalDate date = datePicker.getValue();
            Booking booking = new Booking(
                    roomComboBox.getValue(), date,
                    lockerCodeField.getText(), doorCodeField.getText(), new Price(new BigDecimal(priceField.getText()))
            );
            File fixedAttachment = new File("path/to/your/fixed/attachment");

            bookingEmailService.sendBookingEmail(recipients, booking, fixedAttachment, userAttachment);

            System.out.println("Email verzonden naar: " + Arrays.toString(recipients));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// Email klasse hier (in een aparte file zou beter zijn)
class Email implements Subject {
    private Session newSession;
    private MimeMessage mimeMessage;
    private final List<Observer> observers = new ArrayList<>();

    public void sendEmail(MimeMessage mimeMessage) throws MessagingException {
        String fromUser = "alexchiang1994@gmail.com"; // Your email
        String fromUserPassword = "cqvk enax tlrh ozcd"; // Your app password
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully");

        notifyObservers("Email sent to: " + Arrays.toString(mimeMessage.getAllRecipients()));
    }

    public MimeMessage draftEmail(String[] recipients, Booking booking, File fixedAttachment, File userAttachment) throws MessagingException, IOException {
        mimeMessage = new MimeMessage(newSession);
        addRecipients(recipients);
        mimeMessage.setSubject("Booking Confirmation for " + booking.getRoom());

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(createBodyPart(booking));

        addAttachment(multipart, fixedAttachment);
        addAttachment(multipart, userAttachment);

        mimeMessage.setContent(multipart);
        return mimeMessage;
    }

    private void addRecipients(String[] recipients) throws MessagingException {
        for (String emailRecipient : recipients) {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
        }
    }

    private MimeBodyPart createBodyPart(Booking booking) throws MessagingException {
        String emailBody = "<div style=\"text-align: center;\">"
                + "<h1>Booking Details</h1>"
                + "<p>Room: " + booking.getRoom() + "</p>"
                + "<p>Date: " + DateUtil.formatDate(booking.getDate()) + "</p>"
                + "<p>Locker Code: " + booking.getLockerCode() + "</p>"
                + "<p>Door Code: " + booking.getDoorCode() + "</p>"
                + "<p>Price: " + booking.getPrice() + "</p></div>";

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody, "text/html");
        return bodyPart;
    }

    private void addAttachment(MimeMultipart multipart, File attachment) throws MessagingException, IOException {
        if (attachment != null && attachment.exists()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(attachment);
            multipart.addBodyPart(attachmentBodyPart);
        }
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
                return new PasswordAuthentication("alexchiang1994@gmail.com", "cqvk enax tlrh ozcd"); // Your credentials
            }
        });
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// Booking klasse hier (in een aparte file zou beter zijn)
class Booking {
    private String room;
    private LocalDate date;
    private String lockerCode;
    private String doorCode;
    private Price price;

    public Booking(String room, LocalDate date, String lockerCode, String doorCode, Price price) {
        this.room = room;
        this.date = date;
        this.lockerCode = lockerCode;
        this.doorCode = doorCode;
        this.price = price;
    }

    // Getters
    public String getRoom() { return room; }
    public LocalDate getDate() { return date; }
    public String getLockerCode() { return lockerCode; }
    public String getDoorCode() { return doorCode; }
    public Price getPrice() { return price; }
}

// BookingEmailService klasse hier (in een aparte file zou beter zijn)
class BookingEmailService {
    private final Email emailService;

    public BookingEmailService(Email emailService) {
        this.emailService = emailService;
    }

    public void sendBookingEmail(String[] recipients, Booking booking, File fixedAttachment, File userAttachment) throws MessagingException, IOException {
        emailService.setupServerProperties();
        MimeMessage emailMessage = emailService.draftEmail(recipients, booking, fixedAttachment, userAttachment);
        emailService.sendEmail(emailMessage);
    }

    public Email getEmailService() {
        return emailService;
    }
}

// EmailObserver klasse hier (in een aparte file zou beter zijn)
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

// DateUtil klasse hier (in een aparte file zou beter zijn)
class DateUtil {
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }
}

// Price klasse hier (in een aparte file zou beter zijn)
class Price {
    private final BigDecimal amount;

    public Price(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "&euro;" + amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
