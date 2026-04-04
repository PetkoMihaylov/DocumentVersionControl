package model;


public class Reader extends User {

    public Reader(String userName, String password) {
        super(userName, password);
    }

    @Override
    public UserType getUserType() {
        return UserType.READER;
    }

    @Override
    public String toString() {
        return "Reader{}:" + super.toString();
    }
}

