import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PairwiseTestingTest {

    private BookingEmailService bookingEmailService;
    private File fixedAttachment;
    private File userAttachment;

    @BeforeEach
    public void setUp() {
        Email emailService = new Email();
        bookingEmailService = new BookingEmailService(emailService);
        fixedAttachment = new File("path/to/your/fixed/attachment");
        userAttachment = null;  // Or specify a user attachment if needed
    }

    @Test
    public void testCase1() {
        runTest(true, true, "Kamer 1", true);
    }

    @Test
    public void testCase2() {
        runTest(true, false, "Kamer 2", false);
    }

    @Test
    public void testCase3() {
        runTest(false, true, "Kamer 3", false);
    }

    @Test
    public void testCase4() {
        runTest(false, false, "Kamer 1", true);
    }

    @Test
    public void testCase5() {
        runTest(true, true, "Kamer 2", false);
    }

    @Test
    public void testCase6() {
        runTest(true, false, "Kamer 3", true);
    }

    @Test
    public void testCase7() {
        runTest(false, true, "Kamer 1", false);
    }

    @Test
    public void testCase8() {
        runTest(false, false, "Kamer 2", true);
    }

    @Test
    public void testCase9() {
        runTest(true, true, "Kamer 3", true);
    }

    private void runTest(boolean isFormFilled, boolean isEmailCorrect, String selectedRoom, boolean meetsRequirements) {
        String[] recipients = isEmailCorrect ? new String[]{"test@example.com"} : new String[]{"incorrect"};
        LocalDate date = LocalDate.now();
        Booking booking = new Booking(selectedRoom, date, "1234", "5678", new Price(new BigDecimal("100.00")));

        if (!isFormFilled) {
            System.out.println("Form is not filled, email cannot be sent.");
            return;
        }

        if (!isEmailCorrect) {
            System.out.println("Email is incorrect, email cannot be sent.");
            return;
        }

        if (selectedRoom == null || selectedRoom.isEmpty() || !meetsRequirements) {
            System.out.println("Room selection is invalid or does not meet requirements, email cannot be sent.");
            return;
        }

        try {
            MimeMessage emailMessage = bookingEmailService.getEmailService().draftEmail(recipients, booking, fixedAttachment, userAttachment);
            bookingEmailService.getEmailService().sendEmail(emailMessage);
            System.out.println("Email sent successfully for: " + selectedRoom);
            assertTrue(true, "Email should be sent successfully.");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            fail("Email sending failed.");
        }
    }
}
