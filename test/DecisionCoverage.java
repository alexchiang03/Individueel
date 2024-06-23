import org.junit.Test;
import static org.junit.Assert.*;

public class DecisionCoverage {

    @Test
    public void testMakeBookingDecision() {
        Main main = new Main();

        // Test cases for decision coverage
        // Decision 1: Check if form is fully filled
        // True branch
        assertTrue(main.makeBookingDecision(true, "valid@email.com", "Room1"));
        // False branch
        assertFalse(main.makeBookingDecision(false, "valid@email.com", "Room1"));

        // Decision 2: Check if email is valid
        // True branch
        assertTrue(main.makeBookingDecision(true, "valid@email.com", "Room1"));
        // False branch
        assertFalse(main.makeBookingDecision(true, "invalidemail.com", "Room1"));

        // Decision 3: Check if room is available and suitable
        // True branch
        assertTrue(main.makeBookingDecision(true, "valid@email.com", "Room1"));
        // False branch
        assertFalse(main.makeBookingDecision(true, "valid@email.com", "Room2"));
    }

    // Main class to be tested
    static class Main {

        public boolean makeBookingDecision(boolean isFormFilled, String email, String room) {
            boolean isValidEmail = isValidEmail(email);
            boolean isRoomAvailable = isRoomAvailable(room);

            if (isFormFilled) {
                if (isValidEmail && isRoomAvailable) {
                    return true; // Decision point 1: True branch
                } else {
                    return false; // Decision point 1: False branch
                }
            } else {
                return false; // Decision point 2: False branch
            }
        }

        private boolean isValidEmail(String email) {
            return email != null && email.contains("@") && email.contains(".");
        }

        private boolean isRoomAvailable(String room) {
            // Simulate room availability logic
            return room.equals("Room1");
        }
    }
}
