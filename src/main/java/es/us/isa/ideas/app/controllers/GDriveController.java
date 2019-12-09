package es.us.isa.ideas.app.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import es.us.isa.ideas.app.security.LoginService;
import es.us.isa.ideas.app.util.GDrive;
import es.us.isa.ideas.repo.gdrive.DriveQuickstart;

@Controller
@RequestMapping("/gdrive")
public class GDriveController extends AbstractController{
	@RequestMapping(value = "/connect", method = RequestMethod.GET)
    public ModelAndView connect() {
		//ModelAndView res=new ModelAndView("gdrive/success");
		ModelAndView res=null;
		try {
			
			GDrive.driveService();
			//res=new ModelAndView("app/editor");
			res=new ModelAndView("gdrive/connect");
			res.addObject("isConnected",GDrive.isConnected(LoginService.getPrincipal().getUsername()));
		
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	@GetMapping("/isConnected")
	public Boolean isConnected() {
		return GDrive.isConnected(LoginService.getPrincipal().getUsername());
	}
	
	@RequestMapping(value = "/disconnect", method = RequestMethod.GET)
	public ModelAndView disconnect() {
		boolean b=GDrive.logout(LoginService.getPrincipal().getUsername());
		ModelAndView res=new ModelAndView("gdrive/connected");
		res.addObject("connected",GDrive.isConnected(LoginService.getPrincipal().getUsername()));
		if(!b) {
            res.addObject("message_type", "An error has ocurred");

		}
		res.addObject("disconnected",b);
		return res;
	}
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public ModelAndView status() {
		ModelAndView res=new ModelAndView("gdrive/status");
		res.addObject("isConnected",GDrive.isConnected(LoginService.getPrincipal().getUsername()));
		return res;
	}


	
}
