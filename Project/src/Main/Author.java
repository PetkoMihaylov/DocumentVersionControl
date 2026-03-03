package Main;


public class Author extends User
{
    public Author(String userName, String password) {
        super(userName, password);
    }

    @Override
    public UserType getUserType()
    {
        return UserType.AUTHOR;
    }

    @Override
    public String toString()
    {
        return "Author{}:" + super.toString();
    }
}
