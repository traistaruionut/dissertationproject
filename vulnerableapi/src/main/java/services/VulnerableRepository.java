package services;

import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class VulnerableRepository {
    Connection connection;

    String findAllUsers = "select * from user";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public VulnerableRepository() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://172.18.0.2:3306/db_example", "springuser", "pass");
            System.out.println("HERE");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> findAll() {
        Statement stmt = null;
        List<User> usersList = new ArrayList<>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(findAllUsers);
            while(rs.next()){
                User user = new User();
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return usersList;
    }

    public void insertUser(String name, String email){
        String insertUserQuery = "INSERT INTO user (name,email) VALUES ('" + name + "','" +email + "');";
        log.debug(insertUserQuery);
        try {
            Statement statement = connection.createStatement();
            statement.execute(insertUserQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserByName(String name){
        String selectQuery = "select name,email from user where name='"+name+"';";
        log.debug(selectQuery);
        String result = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);
            while(rs.next()){
                result+=rs.getString(1)+" "+rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
