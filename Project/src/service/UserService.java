package service;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    public static Map<Integer, User> users = new HashMap<>();

//    public UserService() {
//    }

    public Map<Integer, User> getUsers() {
        return users;
    }
    public void setUsers(Map<Integer, User> setUsers) {
        users = setUsers;
    }
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void removeUser(User user) {
        users.remove(user.getUserId());
    }


    public User getUserById(int id) {
        return users.get(id);
    }
}
