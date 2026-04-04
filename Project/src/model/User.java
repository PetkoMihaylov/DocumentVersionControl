package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class User implements Serializable
{
    private static final AtomicInteger counter = new AtomicInteger(1);
    private final int id;
    private String userName;
    private String password;

    public User(String userName, String password) {
        this.id = counter.getAndIncrement();
        this.userName = userName;
        this.password = password;
    }

    public abstract UserType getUserType();

    public String getUserName() {
        return userName;
    }

    /*public String getUserNameById(){
        // a little bit confused on how this will work when I have to have an object and I can call getUserId();
    }*/

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getUserId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }

}
