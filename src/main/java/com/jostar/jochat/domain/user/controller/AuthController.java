package com.jostar.jochat.domain.user.controller;

import com.jostar.jochat.domain.user.dto.SignupRequest;
import com.jostar.jochat.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest("", "", ""));
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        authService.signup(signupRequest);
        return "redirect:/login";
    }
}