package controllers;

import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import services.VulnerableRepository;

@Controller
@RequestMapping(path="/vulnerable")
public class VulnerableController {

    @Autowired
    VulnerableRepository vulnerableRepository;

    @GetMapping(path="/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return vulnerableRepository.findAll();
    }

    @GetMapping(path="/add")
    public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email){
        vulnerableRepository.insertUser(name,email);
        return "Saved";
    }

    @GetMapping(path="/getByName")
    public @ResponseBody String getUserByName(@RequestParam String name){
        return vulnerableRepository.getUserByName(name);
    }

}
