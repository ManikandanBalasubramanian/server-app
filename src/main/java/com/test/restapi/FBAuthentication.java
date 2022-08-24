package com.test.restapi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

@Path("/")
public class FBAuthentication {

  private static final Logger LOGGER = Logger.getLogger(FBAuthentication.class.getName());

  @Context HttpServletRequest request;
  HttpServletResponse response;

  static {
    LOGGER.info("Firebase Initialization SuccessFul");
    try (FileInputStream serviceAccount =
        new FileInputStream("secretKey.json")) {
      final FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @POST
  @Path("sessionLogin")
  @Consumes("application/json")
  public Response createSessionCookie(String tokenStr) {
    String idToken = new JSONObject(tokenStr).getString("idToken");

    long expiresIn = TimeUnit.DAYS.toMillis(5);
    SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

    try {
      String sessionCookie = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
      NewCookie cookie =
          new NewCookie("session", sessionCookie, "/", "", "comment", (int) expiresIn, false);

      return Response.ok().cookie(cookie).build();
      
    } catch (FirebaseAuthException e) {
      LOGGER.severe("Error creating cookie");
      return Response.status(Status.UNAUTHORIZED)
          .entity("Failed to create a session cookie")
          .build();
    }
  }

  @GET
  @Path("sessionLogout")
  public Response clearSessionCookie(@CookieParam("session") Cookie cookie) {

    if (cookie == null) {
      return Response.temporaryRedirect(URI.create("/login.html")).build();
    }

    final int maxAge = 0;
    NewCookie newCookie = new NewCookie(cookie, null, maxAge, true);
    return Response.temporaryRedirect(URI.create("/login.html")).cookie(newCookie).build();
  }

  @GET
  @Path("home")
  public Response verifySessionCookie(@CookieParam("session") Cookie cookie) {
    if (cookie == null) {
      return Response.temporaryRedirect(URI.create("/login.html")).build();
    }

    String sessionCookie = cookie.getValue();
    try {
      final boolean checkRevoked = true;
      FirebaseToken decodedToken =
          FirebaseAuth.getInstance().verifySessionCookie(sessionCookie, checkRevoked);
      return serveContentForUser(decodedToken);
    } catch (FirebaseAuthException e) {
      return Response.temporaryRedirect(URI.create("/login.html")).build();
    }
  }

  public Response serveContentForUser(FirebaseToken decodedToken) {
    return null;
  }
}
