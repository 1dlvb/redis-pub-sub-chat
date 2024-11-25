package com.dlvb.redischat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for managing the chat-related pages in the application.
 * @author Matushkin Anton
 */
@Controller
public class ChatController {

    @GetMapping
    public String homePage() {
        return "index";
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }

}
