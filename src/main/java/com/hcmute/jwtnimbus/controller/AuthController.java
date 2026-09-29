package com.hcmute.jwtnimbus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controller render view giống phần Render in Ajax của bài giảng. */
@Controller
@RequestMapping("/")
public class AuthController {

    @GetMapping
    public String home() { return "redirect:/login"; }

    @GetMapping("login")
    public String login() { return "login"; }

    @GetMapping("register")
    public String register() { return "register"; }

    @GetMapping({"profile", "user/profile"})
    public String profile() { return "profile"; }
}
