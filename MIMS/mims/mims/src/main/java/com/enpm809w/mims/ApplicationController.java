package com.enpm809w.mims;

import java.io.UnsupportedEncodingException;
import java.util.List;


import com.enpm809w.mims.Common.Insurance;
import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.PasswordValidator.InvalidPasswordException;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Services.InsuranceDetailsService;
import com.enpm809w.mims.Services.UserDetailService;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(MimsApplication.class);
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private InsuranceDetailsService insuranceDetailsService;

    @Autowired
    private DataConstants dataConstants;
    @Autowired
    private UserDetailService userDetail;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, HttpServletRequest request) throws Exception {
        userDetail.register(user, getSiteURL(request), userRepo);
        logger.info("A new user has been registered:" + user.getEmail());
        return "register_success";
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/users")
    public String listInsurance(Model model) {
        return findPaginated(1, "insuranceName", "asc", model);
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userDetail.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

    @GetMapping("/homepage")
    public String viewSystemHomePage(Model model) {
        logger.info("Viewing user registered insurances!");
        return findPaginated(1, "insuranceName", "asc", model);
    }

    @GetMapping("/showNewInsuranceForm")
    public String showNewInsuranceForm(Model model) {
        Insurance insurance = new Insurance();
        model.addAttribute("insurance", insurance);
        return "new_insurance";
    }

    @PostMapping("/saveInsurance")
    public String saveInsurance(@ModelAttribute("Insurance") Insurance insurance, Model model) {
        // save insurance to database
        try {
            insuranceDetailsService.saveInsurance(insurance);
            logger.info("Added new insurance:"+insurance.getInsuranceName());
            return "redirect:/homepage";
        } catch (Exception e){
            logger.info("Failed to save insurance.");
            model.addAttribute("errorMessage", "Failed to save insurance.");
            return  "error";
        }
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {

        Insurance insurance = insuranceDetailsService.getInsuranceById(id);
        model.addAttribute("insurance", insurance);
        return "update_insurance";
    }

    @GetMapping("/deleteInsurance/{id}")
    public String deleteInsurance(@PathVariable(value = "id") long id) {

        Insurance insurance = insuranceDetailsService.getInsuranceById(id);
        this.insuranceDetailsService.deleteInsuranceById(id);
        logger.info("Deleted insurance:"+insurance.getInsuranceName());
        return "redirect:/homepage";
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 5;

        //Page<Insurance> page = insuranceDetailsService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Insurance> listInsurances = insuranceDetailsService.findPaginated(pageNo, pageSize, sortField, sortDir);

        model.addAttribute("currentPage", pageNo);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listInsurances", listInsurances);
        return "homepage";
    }


    @GetMapping("/changePassword")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("pageTitle", "Change Password");
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String processChangePassword(HttpServletRequest request, HttpServletResponse response,
                                        Model model, RedirectAttributes ra) throws ServletException {

        String username = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");

        if (oldPassword.equals(newPassword)) {
            model.addAttribute("errorMessage", "Your new password must be different than the old one.");
            return "error";
        }

        if (!userDetail.changeUserPassword(username, oldPassword, newPassword)){
            model.addAttribute("message", "Your old password is incorrect.");
            return "changePassword";
        } else{
            return "redirect:/";
        }

    }



}
