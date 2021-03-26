package hr.foi.daspicko.iotmas.controllers.rest;

import com.google.gson.JsonObject;
import hr.foi.daspicko.iotmas.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(value = "/api/v1/email")
public class EmailController {

    @Value("${spring.mail.username}")
    private String from;

    private EmailServiceImpl emailService;

    @Autowired
    public void setEmailService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON)
    private String sendEmail(@RequestParam("to") String to, @RequestParam("body") String body) {
        final String subject = "Service for device is needed";

        emailService.sendSimpleMessage(to, from, subject, body);

        JsonObject response = new JsonObject();
        response.addProperty("to", to);
        response.addProperty("from", from);
        response.addProperty("subject", subject);
        response.addProperty("body", body);

        return response.toString();
    }
}
