package app.controller;

import app.UserAptDetails;
import app.model.Appointment;
import app.model.Venue;
import app.repository.AppointmentRepository;
import app.repository.UserAptDetailsRepository;
import app.repository.UserRepository;
import app.model.User;
import app.exception.UserNotFoundException;

import app.security.CustomUserDetails;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(path = "users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserAptDetailsRepository userAptDetailsRepository;
    @Autowired
    AppointmentController appointmentController;
    @Autowired
    AppointmentRepository appointmentRepository;

    public UserController() {
    }

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String showRegLoginLandingPage() {
        return "reg_login_landing";
    }

    @GetMapping("/register")
    public String startRegistration(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // register attempt of user with error checking for duplicate email or ppsn
    @PostMapping("/register_attempt")
    public String registerAttempt(@ModelAttribute("user") User newUser) {
        if (getUserByEmail(newUser.getEmail())) {
            System.out.println("An account associated with this email address has already been created.");
            return "reg_login_landing";
        }else if (getUserByPPSN(newUser.getPpsn())) {
            System.out.println("An account associated with this PPS number has already been created.");
            return "reg_login_landing";
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(encodedPassword);
            userService.registerDefaultUser(newUser);
            System.out.println("User saved");
            return "success_reg";
        }
    }

    public void registerAdmin(User newUser) {
        if (getUserByEmail(newUser.getEmail())) {
            System.out.println("Admin: An account associated with this email address has already been created.");
        }else if (getUserByPPSN(newUser.getPpsn())) {
            System.out.println("Admin: An account associated with this PPS number has already been created.");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(encodedPassword);
            userService.registerAdminUser(newUser);
            System.out.println("Admin User saved");
        }
    }

    @RequestMapping("/login")
    public String login(){
        // Take username and password
        // Check against database
        // Login status= true
        // Redirect to my details page
        return "Welcome!";
    }

    @GetMapping("/listUsers")
    public String listUsers(Model model) {
        List<User> allUsers = userRepository.findAll();
        model.addAttribute("listUsers", allUsers);
        return "list_users";
    }

    @GetMapping("/myInfo")
    public String showMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String userEmail = userDetails.getUsername();
        User user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        Appointment nextApt = user.getNextApptId();
        if (nextApt == null){
            System.out.println("Next Apt: "  + nextApt);
        } else{
            System.out.println("Next Apt: "  + nextApt.toString());

        }
        model.addAttribute("apt", nextApt);
        return "my_info";
    }

    @GetMapping("/editUserInfo/{id}")
    public String editUserInfo(@PathVariable(value = "id") Long userId, Model model) {
        User user = userRepository.findByID(userId);
        model.addAttribute("user", user);
        Appointment nextApt = user.getNextApptId();
        if (nextApt == null){
            System.out.println("Next Apt: "  + nextApt);
        } else{
            System.out.println("Next Apt: "  + nextApt.toString());
        }
        model.addAttribute("apt", nextApt);
        return "edit_user_info";
    }

    @GetMapping("/confirmDose1/{user_id}")
    public String confirmDose1(@PathVariable(value = "user_id") Long userId,
                               Model model) {
        User user = userRepository.findByID(userId);

        Appointment attendedApt = user.getNextApptId();

        String oldDate = attendedApt.getDate();
//        String oldDatetime = attendedApt.getDate() + " " + attendedApt.getTime();
//            System.out.println("Old Date time " + oldDatetime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new java.util.Date();
        try {
            newDate = sdf.parse(oldDate);
        } catch (ParseException pe){
            pe.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(newDate);
        c.add(Calendar.DATE, 21); // Adding 3 weeks
        String futureDate = sdf.format(c.getTime());
        System.out.println(futureDate);
        String confirmedDose = attendedApt.getVaccine();
        String attendedDate = attendedApt.getDate();
        System.out.println("Setting user null");
        user.setNextApptId(null);
        System.out.println("Deleting Apt");
        appointmentRepository.delete(attendedApt); // Delete old appointment

        Appointment newApt = new Appointment(
                attendedApt.getVaccine(),
                "dose2",
                futureDate,
                attendedApt.getTime(),
                attendedApt.getVenue()
        );
        System.out.println("Saving New Apt");
        appointmentRepository.save(newApt); // Create new appointment 21 days in future
        System.out.println("Updating user to include new apt");
//        userRepository.updateUser(userId, newApt.getApt_id()); // Update user's appointment
        user.setNextApptId(newApt);
        System.out.println("Updating dose info");
        userRepository.updateDose1(confirmedDose, userId); // Update dose on users table
        userRepository.updateDose1Date(attendedDate, userId); // Update dose date on users table

        return "redirect:/users/editUserInfo/" + userId.toString();
    }

    @GetMapping("/confirmDose2/{user_id}")
    public String confirmDose2(@PathVariable(value = "user_id") Long userId,
                               Model model) {
        User user = userRepository.findByID(userId);
        Appointment attendedApt = user.getNextApptId();
        String confirmedDose = attendedApt.getVaccine();
        String attendedDate = attendedApt.getDate();
        System.out.println("Setting user null");
        user.setNextApptId(null);
        System.out.println("Deleting Apt");
        appointmentRepository.delete(attendedApt); // Delete old appointment
        userRepository.updateDose2(confirmedDose, userId); // Update dose on users table
        userRepository.updateDose2Date(attendedDate, userId); // Update dose date on users table
        return "redirect:/users/editUserInfo/" + userId.toString();
    }

    @RequestMapping("/logout")
    public String logout(){
        // Login status= false
        // Redirect to home page
        return "Welcome!";
    }

    // Get All users
    public List<User> getAllUsers(){
        return  userRepository.findAll();
    }

//    // altered function to only save user if email not already taken
//    @PostMapping
//    public void newUser(@Valid @RequestBody User newUser) {
//        if (getUserByEmail(newUser.getEmail()))
//            System.out.println("An account associated with this email address has already been created.");
//        else if (getUserByPPSN(newUser.getPpsn()))
//            System.out.println("An account associated with this PPS number has already been created.");
//        else
//            userRepository.save(newUser);
//    }

    // I will try to consolidate this and the email check into one method
    public Boolean getUserByEmail(String email) {
        var users = getAllUsers();

        var user =  users.stream()
                .filter(t -> email.equals(t.getEmail()))
                .findFirst()
                .orElse(null);

        if (user == null) return false;
        else return true;
    }

    public Boolean getUserByPPSN(String ppsn) {
        var users = getAllUsers();

        var user =  users.stream()
                .filter(t -> ppsn.equals(t.getPpsn()))
                .findFirst()
                .orElse(null);

        if (user == null) return false;
        else return true;
    }

//    // Get a Single User
//    @GetMapping("/{id}")
//    public User getUserById(@PathVariable(value = "id") Long userId)
//            throws UserNotFoundException {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//    }


    @GetMapping("/bookAppointment")
    public String bookingForm(Model model) {
        model.addAttribute("venue", new Venue());
        return "select_venue";
    }


    // return appointment details of a user - incomplete pending team decisions on functionality
    public void showAppointment(Long userId) throws UserNotFoundException {
        UserAptDetails userAptDetails = userAptDetailsRepository.findAptDetails(userId);
        System.out.println(userAptDetails.getApt_id() + " " + userAptDetails.getVenue());
    }

    @GetMapping("/edit")
    public String editUsers() {
        return "edit_users";
    }
}