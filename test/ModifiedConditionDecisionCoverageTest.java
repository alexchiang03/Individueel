import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingEmailTest {

    private boolean isFormulierIngevuld(String recipients, String room, LocalDate date, String lockerCode, String doorCode, BigDecimal price) {
        return recipients != null && !recipients.isEmpty() &&
                room != null && !room.isEmpty() &&
                date != null &&
                lockerCode != null && !lockerCode.isEmpty() &&
                doorCode != null && !doorCode.isEmpty() &&
                price != null;
    }

    private boolean isJuisteEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isJuisteKamerGeselecteerd(String room, List<String> availableRooms) {
        return availableRooms.contains(room);
    }

    private boolean canSendEmail(String recipients, String room, LocalDate date, String lockerCode, String doorCode, BigDecimal price, List<String> availableRooms) {
        boolean formulierIngevuld = isFormulierIngevuld(recipients, room, date, lockerCode, doorCode, price);
        boolean juisteEmail = isJuisteEmail(recipients);
        boolean juisteKamerGeselecteerd = isJuisteKamerGeselecteerd(room, availableRooms);
        return formulierIngevuld && juisteEmail && juisteKamerGeselecteerd;
    }

    @Test
    void testCase1() {
        List<String> availableRooms = Arrays.asList("Kamer 1", "Kamer 2", "Kamer 3");
        assertFalse(canSendEmail("", "", null, "", "", null, availableRooms));
    }

    @Test
    void testCase2() {
        List<String> availableRooms = Arrays.asList("Kamer 1", "Kamer 2", "Kamer 3");
        assertFalse(canSendEmail("example@example.com", "", LocalDate.now(), "1234", "5678", new BigDecimal("100"), availableRooms));
    }

    @Test
    void testCase3() {
        List<String> availableRooms = Arrays.asList("Kamer 1", "Kamer 2", "Kamer 3");
        assertFalse(canSendEmail("example@example.com", "Kamer 1", LocalDate.now(), "1234", "", new BigDecimal("100"), availableRooms));
    }

    @Test
    void testCase4() {
        List<String> availableRooms = Arrays.asList("Kamer 1", "Kamer 2", "Kamer 3");
        assertFalse(canSendEmail("example@example.com", "Kamer 4", LocalDate.now(), "1234", "5678", new BigDecimal("100"), availableRooms));
    }

    @Test
    void testCase5() {
        List<String> availableRooms = Arrays.asList("Kamer 1", "Kamer 2", "Kamer 3");
        assertTrue(canSendEmail("example@example.com", "Kamer 1", LocalDate.now(), "1234", "5678", new BigDecimal("100"), availableRooms));
    }
}
