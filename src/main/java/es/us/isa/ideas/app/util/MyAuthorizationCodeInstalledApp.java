package es.us.isa.ideas.app.util;

import java.io.IOException;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

public class MyAuthorizationCodeInstalledApp extends AuthorizationCodeInstalledApp {

	public MyAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
		super(flow, receiver);
		// TODO Auto-generated constructor stub
	}

	public String getBrowsingURL() {
		return browsingURL;
	}

	

	private String browsingURL; 
	
	protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
		browsingURL=authorizationUrl.build();
	}
	
	public Credential authorize(String userId) throws IOException {
	    try {
	      Credential credential = getFlow().loadCredential(userId);
	      if (credential != null
	          && (credential.getRefreshToken() != null || 
	              credential.getExpiresInSeconds() == null || 
	              credential.getExpiresInSeconds() > 60)) {
	        return credential;
	      }
	      // open in browser
	      String redirectUri = getReceiver().getRedirectUri();
	      AuthorizationCodeRequestUrl authorizationUrl =
	          getFlow().newAuthorizationUrl().setRedirectUri(redirectUri);
	      onAuthorization(authorizationUrl);	      
	      return null;
	    } finally {
	      getReceiver().stop();
	    }
	  }
	
	public Credential continueAuthorization(String redirectUri,String userId) throws IOException  {
		try {
	      // receive authorization code and exchange it for an access token
	      String code =getReceiver().waitForCode();
	      TokenResponse response = getFlow().newTokenRequest(code).setRedirectUri(redirectUri).execute();
	      // store credential and return it
	      return getFlow().createAndStoreCredential(response, userId);
		} finally {
		      getReceiver().stop();
		}
	}
}
