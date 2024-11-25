package com.dlvb.redischat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for managing the chat-related pages in the application.
 * @author Matushkin Anton
 */
@Controller
public class ChatController {

    /**
     * @return homepage template name.
     */
    @GetMapping
    public String homePage() {
        return "index";
    }

    /**
     * @return chat page template name.
     */
    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }

}
