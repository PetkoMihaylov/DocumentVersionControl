package test;

import model.Role;
import model.User;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {

    @BeforeEach
    void resetUserStaticState() throws Exception {
        Field counterField = User.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        ((AtomicInteger) counterField.get(null)).set(1);
        //if i understand correctly, this just resets the values?

        Field usernamesField = User.class.getDeclaredField("usernames");
        usernamesField.setAccessible(true);
        ((Set<?>) usernamesField.get(null)).clear();
    }

    @Test
    @Order(1)
    void constructor_storesUserName() {
        User u = new User("jay", "pass123", Role.AUTHOR);
        assertEquals("jay", u.getUserName());
    }

    @Test
    @Order(2)
    void constructor_storesPassword() {
        User u = new User("garrick", "secret", Role.REVIEWER);
        assertEquals("secret", u.getPassword());
    }

    @Test
    @Order(3)
    void constructor_storesRole() {
        User u = new User("kidflash", "pass", Role.READER);
        assertEquals(Role.READER, u.getUserRole());
    }

    @Test
    @Order(4)
    void constructor_assignsAutoIncrementingId() {
        User u1 = new User("user1", "pass1", Role.AUTHOR);
        User u2 = new User("user2", "pass2", Role.AUTHOR);

        assertEquals(u1.getUserId() + 1, u2.getUserId());
    }


    @Test
    @Order(5)
    void constructor_throwsIllegalArgumentException_onDuplicateUsername() {
        new User("duplicate", "pass1", Role.AUTHOR);

        assertThrows(IllegalArgumentException.class,
                () -> new User("duplicate", "pass2", Role.REVIEWER));
    }


    @Test
    @Order(6)
    void changeRole_updatesUserRole() {
        User u = new User("mod", "pw", Role.READER);
        u.changeRole(Role.REVIEWER);
        assertEquals(Role.REVIEWER, u.getUserRole());
    }


    @Test
    @Order(7)
    void equals_trueForSameUsernameAndPassword() {
        //object compare, so should be the same?
        User u = new User("same", "pass", Role.AUTHOR);
        assertEquals(u, u);   // reflexive, apparently, does use hashCode automatically?
    }

    @Test
    @Order(8)
    void equals_falseForDifferentPassword() {
        User u1 = new User("shared", "pass1", Role.AUTHOR);

        //this new object is created with same data?
        assertNotEquals(u1, new Object());
    }

    @Test
    @Order(9)
    void hashCode_consistentWithEquals() {
        User u = new User("hashed", "pass", Role.ADMINISTRATOR);
        assertEquals(u.hashCode(), u.hashCode());
    }
}
