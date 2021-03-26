package hr.foi.daspicko.iotmas.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class General {

    @GetMapping("viewlog")
    public String viewLogMessages() {
        return "logmessages";
    }

}
