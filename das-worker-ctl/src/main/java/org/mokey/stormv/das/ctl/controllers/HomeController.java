package org.mokey.stormv.das.ctl.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by enousei on 2/16/16.
 */
@Controller
public class HomeController {
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
