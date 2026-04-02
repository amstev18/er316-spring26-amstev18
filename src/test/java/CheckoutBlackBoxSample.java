import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sample Black-Box tests for the Checkout system.
 * This class demonstrates how to write black-box tests using:
 * - Equivalence Partitioning (EP)
 * - Boundary Value Analysis (BVA)
 * - Parametrized tests across multiple implementations
 *
 * Black-box testing focuses on testing the SPECIFICATION WITHOUT
 * looking at the implementation.
 *
 * The parameterized structure allows testing all Checkout implementations
 * with the same tests to identify which implementations have bugs.
 */
public class CheckoutBlackBoxSample {

    private Checkout checkout;

    /**
     * Provides the list of Checkout classes to test.
     * Each test will run against ALL implementations.
     */
    @SuppressWarnings("unchecked")
//    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
//       return (Stream<Class<? extends Checkout>>) Stream.of(
//                Checkout0.class,
//                Checkout1.class,
//                Checkout2.class,
//                Checkout3.class
//        );
//    }

    // Uncomment when you implement the method in assign 3 and comment the above
    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
        return Stream.of(Checkout.class);
    }


    /**
     * Helper method to create Checkout instance from class using reflection.
     */
    private Checkout createCheckout(Class<? extends Checkout> clazz) throws Exception {
        Constructor<? extends Checkout> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    /**
     * SAMPLE TEST 1: Tests successful checkout of an available book
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T2: Successful checkout - available book, eligible patron")
    public void testBookAvailable(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 0.0 for success
        assertEquals(0.0, result, 0.01,
                "Expected successful checkout (0.0) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be unavailable
        assertFalse(book.isAvailable(),
                "Book should be unavailable after checkout for " + checkoutClass.getSimpleName());

        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count increased
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 2: Tests checkout with unavailable book
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T1: Unavailable book returns error code 2.0")
    public void testUnavailableBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create unavailable book
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 5);
        book.setAvailableCopies(0);  // We are pretending it has been checked out by others and is not available anymore

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 2.0 for unavailable book
        assertEquals(2.0, result, 0.01,
                "Expected error code 2.0 for unavailable book for " + checkoutClass.getSimpleName());

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list for " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 3: Tests checkout if patron is null
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T3: Patron is null, returns 3.1")
    public void testPatronNull(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        checkout.addBook(book);

        double result = checkout.checkoutBook(book, null);

        assertEquals(3.1, result, .01,
                "Expected 3.1 for null patron in " + checkoutClass.getSimpleName());
        assertEquals(1, book.getAvailableCopies(),
                "Book copies shouldn't change in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 4: Tests checkout if patron exists
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T4: Successful checkout, eligible patron")
    public void testPatronExists(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for existing patron in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 5: Tests checkout if patron is suspended
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T5: Patron is suspended, returns 3.0")
    public void testPatronSuspended(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.setAccountSuspended(true);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(3.0, result, .01,
                "Expected 3.0 for suspended patron in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 6: Tests checkout if patron is active
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T6: Successful checkout, eligible patron")
    public void testPatronActive(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for active patron in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 7: Tests checkout if book is null
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T7: Book is null, returns 2.1")
    public void testBookNull(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(null, patron);

        assertEquals(2.1, result, .01,
                "Expected 2.1 for null book in " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron should have no book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 8: Tests checkout if book exists
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T8: Successful checkout, eligible null")
    public void testBookExists(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for existing book in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 9: Tests checkout if book is reference only
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T9: Reference only book, returns 5.0")
    public void testReferenceOnly(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.REFERENCE, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(5.0, result, .01,
                "Expected 5.0 for reference book in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have reference book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 10: Tests checkout if book is not reference only
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T10: Successful checkout, eligible book")
    public void testNotReference(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for non reference book in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 11: Tests renewal if patron has book
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T:11 Successful checkout, renewal")
    public void testHasBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.addCheckedOutBook(book.getIsbn(), LocalDate.now().minusDays(5));
        checkout.addBook(book);
        checkout.registerPatron(patron);

        int copies = book.getAvailableCopies();
        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.1, result, .01,
                "Expected 0.1 for renewal in " + checkoutClass.getSimpleName());
        assertEquals(copies, book.getAvailableCopies(),
                "Copies shouldn't change in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 12: Tests renewal if patron doesn't have book
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T12: Successful checkout, eligible patron eligible book")
    public void testDoesntHaveBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for book in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 13: Tests checkout below fine limit
     * This tests the valid boundary value - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T13: Successful checkout, eligible patron")
    public void testBelowFineLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.addFine(3.00);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for fines below limit in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 14: Tests checkout at warning fine limit
     * This tests the valid boundary value - shows warning.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T14: Successful checkout, warning close to fine limit")
    public void testWarningFineLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.addFine(9.99);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for fines at 9.99 in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 15: Tests checkout at fine limit
     * This tests a max boundary value.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T15: Patron at fine limit, returns 4.1")
    public void testAtFineLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.addFine(10.00);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(4.1, result, .01,
                "Expected 4.2 for fines at limit in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 16: Tests checkout above fine limit
     * This tests an invalid boundary value.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T16: Patron above fine limit, returns 4.1")
    public void testAboveFineLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.addFine(12.00);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(4.1, result, .01,
                "Expected 4.1 for fines above limit in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 17: Tests checkout below checkout limit
     * This tests the valid boundary value - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T17: Successful checkout, eligible patron")
    public void testBelowCheckoutLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        for (int i = 0; i < 7; i++) {
            patron.addCheckedOutBook("Book" + i, LocalDate.now().plusDays(30));
        }
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(0.0, result, .01,
                "Expected 0.0 for books below limit in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 18: Tests checkout at warning checkout limit
     * This tests the valid boundary value - shows warning.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T18: Successful checkout, warning within 2 of checkout limit, return 1.1")
    public void testWarningCheckoutLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        for (int i = 0; i < 8; i++) {
            patron.addCheckedOutBook("Book" + i, LocalDate.now().plusDays(30));
        }
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(1.1, result, .01,
                "Expected 1.1 for books within 2 of limit in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 19: Tests checkout at checkout limit
     * This tests a max boundary value.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T19: At checkout limit, return 3.2")
    public void testAtCheckoutLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        for (int i = 0; i < 10; i++) {
            patron.addCheckedOutBook("Book" + i, LocalDate.now().plusDays(30));
        }
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(3.2, result, .01,
                "Expected 3.2 for books at limit in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 20: Tests checkout above checkout limit
     * This tests an invalid boundary value.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T20: Above checkout limit, return 3.2")
    public void testAboveCheckoutLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        for (int i = 0; i < 12; i++) {
            patron.addCheckedOutBook("Book" + i, LocalDate.now().plusDays(30));
        }
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(3.2, result, .01,
                "Expected 3.2 for books above limit in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 21: Tests overdue book warning
     * This tests a shows a valid equivalence partition, shows warning.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T21: Successful checkout, send warning, returns 1.0")
    public void testOverdueWarning(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.setOverdueCount(2);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(1.0, result, .01,
                "Expected 1.0 for overdue books under limit in " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 22: Tests checkout above overdue limit
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T22: At overdue limit, returns 4.0")
    public void testOverdueLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.setOverdueCount(3);
        checkout.addBook(book);
        checkout.registerPatron(patron);

        double result = checkout.checkoutBook(book, patron);

        assertEquals(4.0, result, .01,
                "Expected 4.0 for overdue books at limit in " + checkoutClass.getSimpleName());
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should not have book in " + checkoutClass.getSimpleName());
    }
}
