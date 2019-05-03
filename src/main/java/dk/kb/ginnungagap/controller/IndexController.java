package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.GinnungagapConstants;
import dk.kb.ginnungagap.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    /** The ginnugagap index path.*/
    protected static final String PATH = "ginnungagap";

    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The constants.*/
    @Autowired
    protected GinnungagapConstants constants;
    
    /**
     * Index controller, for redirecting towards the workflow site.
     * @return The redirect toward the workflow site.
     */
    @RequestMapping("/")
    public RedirectView getIndex() {
        return new RedirectView(PATH,true);
    }

    /**
     * Index controller, for redirecting towards the workflow site.
     * @param model The model for the view.
     * @return The redirect toward the workflow site.
     */
    @RequestMapping("/" + PATH)
    public String getGinnungagap(Model model) {
        model.addAttribute("cumulusConf", conf.getViewableCumulusConfiguration());
        model.addAttribute("localConf", conf.getLocalConfiguration());
        model.addAttribute("bitmagConf", conf.getBitmagConf());
        model.addAttribute("transformationConf", conf.getTransformationConf());
        model.addAttribute("mailConf", conf.getMailConfiguration());
        model.addAttribute("version", constants.getBuildVersion());

        return PATH;
    }
}
