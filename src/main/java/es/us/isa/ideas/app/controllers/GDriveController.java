package es.us.isa.ideas.app.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;

import es.us.isa.ideas.app.security.GoogleAuthorizationService;
import es.us.isa.ideas.app.security.LoginService;
import es.us.isa.ideas.app.services.GDriveService;
import es.us.isa.ideas.app.util.MyAuthorizationCodeInstalledApp;
import es.us.isa.ideas.repo.gdrive.DriveQuickstart;

@Controller
@RequestMapping("/gdrive")
public class GDriveController extends AbstractController{
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	final String CLIENT_SECRET="6eLd-nE7u3kOiYbN8bPDZpPC";
	static InputStream in = DriveQuickstart.class.getResourceAsStream("/credentials.json");


	private GoogleAuthorizationCodeFlow flow;
	private static final String CALLBACK ="/oauth";
	
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
	@GetMapping("/upload")
	public ModelAndView uploadFile() throws GeneralSecurityException,IOException {
		ModelAndView res=new ModelAndView("gdrive/status");
		gdriveService.uploadFile("text.txt",LoginService.getPrincipal().getUsername());
		res.addObject("mensaje","ha subido el archivo ijo de su reputisima amdre estoy Mamadisimo");
		return res;
	}
	
}
