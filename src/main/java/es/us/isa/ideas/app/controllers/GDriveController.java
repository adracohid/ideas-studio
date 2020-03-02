package es.us.isa.ideas.app.controllers;


import java.io.IOException;
import java.io.InputStream;

import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;



import es.us.isa.ideas.app.security.GoogleAuthorizationService;
import es.us.isa.ideas.app.security.LoginService;
import es.us.isa.ideas.app.services.GDriveService;
import es.us.isa.ideas.repo.gdrive.DriveQuickstart;

@Controller
@RequestMapping("/gdrive")
public class GDriveController extends AbstractController{

	final String CLIENT_SECRET="6eLd-nE7u3kOiYbN8bPDZpPC";
	static InputStream in = DriveQuickstart.class.getResourceAsStream("/credentials.json");



	
	@Autowired
	GoogleAuthorizationService googleAuthorizationService;
	
	@Autowired
	GDriveService gdriveService;

	@GetMapping("/googlesignin")
	public void doGoogleSignIn(HttpServletResponse response) throws Exception{
		response.sendRedirect(googleAuthorizationService.authenticateUserViaGoogle(LoginService.getPrincipal().getUsername()));
	}
	
	@GetMapping("/oauth/callback")
	public ModelAndView saveAuthorizationCode(HttpServletRequest request,@RequestParam("code") String code){
		ModelAndView res=new ModelAndView("redirect:/app/editor");
		try {
		if(code !=null) {
			googleAuthorizationService.exchangeCodeForTokens(code,LoginService.getPrincipal().getUsername());
			res.addObject("mensaje", "token actualizado");
		}
		}catch(Throwable e) {
			e.printStackTrace();
			res.addObject("error","¡Ha ocurrido un error!");
		}
		return res;
	}
	
	@GetMapping("/logout")
	public ModelAndView logout(HttpServletRequest request){
		ModelAndView res=new ModelAndView("redirect:/app/editor");
		try {
			googleAuthorizationService.removeUserSession(LoginService.getPrincipal().getUsername());
		} catch (IOException e) {
			
			e.printStackTrace();
			res.addObject("error","¡Ha ocurrido un error!");

		}

		return res;
	}

}
