package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Setting the default start path for the view.
 */
@Controller
public class IndexController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(IndexController.class);

    /**
     * Index controller, for redirecting towards the workflow site.
     * @return The redirect toward the workflow site.
     */
    @RequestMapping("/")
    public RedirectView getIndex() {
        return new RedirectView("ginnungagap",true);
    }

    /**
     * Index controller, for redirecting towards the workflow site.
     * @return The redirect toward the workflow site.
     */
    @RequestMapping("/ginnungagap")
    public String getGinnungagap(Model model) {
        log.warn("Ginnungagap model: " + model.asMap().toString());

        return "ginnungagap";
    }
}
