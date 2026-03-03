package Main;


public class Administrator extends User
{
    public Administrator(String userName, String password) {
        super(userName, password);
    }

    @Override
    public UserType getUserType()
    {
        return UserType.ADMINISTRATOR;
    }

    @Override
    public String toString()
    {
        return "Admin{} " + super.toString();
    }
}
