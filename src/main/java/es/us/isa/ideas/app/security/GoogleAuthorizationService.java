package es.us.isa.ideas.app.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
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

@Service
public class GoogleAuthorizationService {
private Logger logger=LoggerFactory.getLogger(GoogleAuthorizationService.class);
private FileDataStoreFactory dataStoreFactory;
private GoogleAuthorizationCodeFlow flow;
private static final String CREDENTIALS_FILE_PATH ="/credentials.json";
private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
public static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//public static 	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
//private static final String TOKENS_DIRECTORY_PATH = "ideas-repo/" + LoginService.getPrincipal().getUsername();
private static final String TOKENS_DIRECTORY_PATH = "ideas-repo/user";
/*
@PostConstruct
public void init() throws Exception{
	//InputStreamReader reader=new InputStreamReader("classpath:credentials.json".get)
	InputStream reader=GoogleAuthorizationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
	if (reader == null) {
		throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
	}
	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(reader));
	flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
			SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))).build();
	

}
*/
public boolean isUserAuthenticated(String username) throws IOException{
	Credential credential=getCredentials(username);
	if(credential !=null) {
		boolean isTokenValid=credential.refreshToken();
		logger.debug("isTokenValid, "+isTokenValid);
		return isTokenValid;
	}
	return false;
}
public Credential getCredentials(String username) throws IOException{
	InputStream reader=GoogleAuthorizationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
	if (reader == null) {
		throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
	}
	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(reader));
	flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
			SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("ideas-repo/"+username))).build();
	
	if(username!=null) {
	return flow.loadCredential(username);
	}else {
		return null;
	}
}

public String authenticateUserViaGoogle(String username) throws Exception{
	InputStream reader=GoogleAuthorizationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
	if (reader == null) {
		throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
	}
	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(reader));
	flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
			SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("ideas-repo/"+username))).build();
	GoogleAuthorizationCodeRequestUrl url=flow.newAuthorizationUrl();
	String redirectUrl=url.setRedirectUri("http://localhost:8080/gdrive/oauth/callback").setAccessType("offline").build();
	logger.debug("redirectUrl, "+redirectUrl);
	return redirectUrl;
}

public void exchangeCodeForTokens(String code, String username) throws Exception{
	GoogleTokenResponse tokenResponse=flow.newTokenRequest(code).setRedirectUri("http://localhost:8080/gdrive/oauth/callback").execute();
	//flow.createAndStoreCredential(tokenResponse, "user");
	flow.createAndStoreCredential(tokenResponse,username);
}

public void removeUserSession(String username) throws IOException{
	//FileDataStoreFactory dataStoreFactory=new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH));
	FileDataStoreFactory dataStoreFactory=new FileDataStoreFactory(new java.io.File("ideas-repo/"+username));
	dataStoreFactory.getDataStore("StoredCredential").clear();
}
}
