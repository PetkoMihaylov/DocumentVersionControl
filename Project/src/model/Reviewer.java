package model;

public class Reviewer extends User
{
    public Reviewer(String userName, String password)
    {
        super(userName, password);
    }

    @Override
    public UserType getUserType()
    {
        return UserType.REVIEWER;
    }

    @Override
    public String toString()
    {
        return "Reviewer{} " + super.toString();
    }
}
