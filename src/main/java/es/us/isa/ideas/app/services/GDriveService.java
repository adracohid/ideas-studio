package es.us.isa.ideas.app.services;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.us.isa.ideas.app.security.GoogleAuthorizationService;
import es.us.isa.ideas.app.security.LoginService;

@Service
public class GDriveService {
private Logger logger=LoggerFactory.getLogger(GDriveService.class);
private Drive driveService;
public static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

@Autowired
GoogleAuthorizationService googleAuthorizationService;
/*
@PostConstruct
public void init() throws Exception{
	Credential credential=googleAuthorizationService.getCredentials(LoginService.getPrincipal().getUsername());
	driveService=new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
}
*/
public void uploadFile(String name,String username) throws IOException, GeneralSecurityException {
	File fileMetadata = new File();
	String type = null;
	Credential credential=googleAuthorizationService.getCredentials(username);
	driveService=new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	if (name.contains(".txt")) {
		type = "text/plain";
	}
	// TODO
	// Si type==null, solo se puede poner las extensiones anteriores
	fileMetadata.setName(name);
	java.io.File filePath = new java.io.File("src/main/resources/files/" + name);
	FileContent mediaContent = new FileContent(type, filePath);
	File file = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
	
}
}
