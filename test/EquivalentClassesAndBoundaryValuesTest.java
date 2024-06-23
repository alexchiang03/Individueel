import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EquivalentClassesAndBoundaryValuesTest {

    // Helper method to simulate decision making based on parameters
    private boolean canSendEmail(boolean isFormComplete, boolean isEmailValid, boolean isRoomAvailable) {
        return isFormComplete && isEmailValid && isRoomAvailable;
    }

    @Test
    public void testEquivalentieklasseG1E1K1() {
        assertTrue(canSendEmail(true, true, true), "Formulier volledig ingevuld, geldig emailadres, kamer beschikbaar en past bij de benodigheden");
    }

    @Test
    public void testEquivalentieklasseG2E1K1() {
        assertFalse(canSendEmail(false, true, true), "Formulier niet volledig ingevuld, geldig emailadres, kamer beschikbaar en past bij de benodigheden");
    }

    @Test
    public void testEquivalentieklasseG1E2K1() {
        assertFalse(canSendEmail(true, false, true), "Formulier volledig ingevuld, ongeldig emailadres, kamer beschikbaar en past bij de benodigheden");
    }

    @Test
    public void testEquivalentieklasseG1E1K2() {
        assertFalse(canSendEmail(true, true, false), "Formulier volledig ingevuld, geldig emailadres, kamer niet beschikbaar of past niet bij de benodigheden");
    }

    @Test
    public void testEquivalentieklasseG2E2K2() {
        assertFalse(canSendEmail(false, false, false), "Formulier niet volledig ingevuld, ongeldig emailadres, kamer niet beschikbaar of past niet bij de benodigheden");
    }

    @Test
    public void testRandwaardentest1() {
        assertTrue(canSendEmail(true, true, true), "Formulier volledig ingevuld, geldig emailadres, kamer net beschikbaar (bij de grenswaarde)");
    }

    @Test
    public void testRandwaardentest2() {
        assertFalse(canSendEmail(true, true, false), "Formulier volledig ingevuld, geldig emailadres, kamer net niet beschikbaar (bij de grenswaarde)");
    }
}
