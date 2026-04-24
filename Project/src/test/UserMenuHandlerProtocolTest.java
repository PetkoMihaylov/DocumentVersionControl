package test;

import menu.UserMenuHandler;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserMenuHandlerProtocolTest {

    private Scanner scannerOf(String... lines) {
        String input = String.join("\n", lines) + "\n";
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    // Helper: build a PrintStream backed by a ByteArrayOutputStream we can inspect.
    //if I understand correctly.
    private ByteArrayOutputStream byteArrayOutputStream;
    private PrintStream printStreamOf() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        return new PrintStream(byteArrayOutputStream);
    }


    @Test
    @Order(1)
    void readRequest_returnsCommandLine_forSingleLineRequest() {
        Scanner sc = scannerOf("CREATE_DOCUMENT", "END");
        String result = UserMenuHandler.readRequest(sc);
        assertEquals("CREATE_DOCUMENT", result.trim());
    }

    @Test
    @Order(2)
    void readRequest_joinsMultipleLinesWithNewline() {
        Scanner sc = scannerOf("CREATE_DOCUMENT", "Title", "Description", "END");
        String result = UserMenuHandler.readRequest(sc);
        String[] parts = result.split("\n");

        assertEquals("CREATE_DOCUMENT", parts[0]);
        assertEquals("Title", parts[1]);
        assertEquals("Description", parts[2]);
    }

    @Test
    @Order(3)
    void readRequest_returnsEmptyString_whenFirstLineIsEnd() {
        Scanner sc = scannerOf("END");
        String result = UserMenuHandler.readRequest(sc);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(4)
    void readRequest_doesNotIncludeTheEndSentinelInResult() {
        Scanner sc = scannerOf("COMMAND", "arg1", "END");
        String result = UserMenuHandler.readRequest(sc);
        assertFalse(result.contains("END"));
    }


    @Test
    @Order(5)
    void sendResponse_writesSingleLineFollowedByEnd() {
        PrintStream out = printStreamOf();
        UserMenuHandler.sendResponse(out, "Document created!");

        String output = byteArrayOutputStream.toString();
        String[] lines = output.split(System.lineSeparator());

        assertEquals("Document created!", lines[0]);
        assertEquals("END",               lines[1]);
    }

    @Test
    @Order(6)
    void sendResponse_writesMultipleLinesEachOnItsOwnLine() {
        PrintStream out = printStreamOf();
        UserMenuHandler.sendResponse(out, "Line A", "Line B", "Line C");

        String output = byteArrayOutputStream.toString();
        String[] lines = output.split(System.lineSeparator());

        assertEquals("Line A", lines[0]);
        assertEquals("Line B", lines[1]);
        assertEquals("Line C", lines[2]);
        assertEquals("END",    lines[3]);
    }

    @Test
    @Order(7)
    void sendResponse_alwaysTerminatesWithEnd() {
        PrintStream out = printStreamOf();
        //lines must end with "END"
        UserMenuHandler.sendResponse(out);

        String output = byteArrayOutputStream.toString().trim();
        assertEquals("END", output);
    }
}
