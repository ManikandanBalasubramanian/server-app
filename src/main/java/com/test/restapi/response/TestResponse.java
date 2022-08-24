package com.test.restapi.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestResponse {

  public static final Logger LOGGER = Logger.getLogger(TestResponse.class.getName());

  public static final String STATUS = "status"; // No I18N
  public static final String MESSAGE = "message"; // No I18N
  public static final String ERROR = "error"; // No I18N

  public static final String SUCCESS = "success"; // No I18N
  public static final String FAILED = "failed"; // No I18N

  /** Private constructor to avoid object creation. */
  private TestResponse() {}

  /**
   * Use to send response when there is successful execution of the request.<br>
   * Creates a Http response with 200 status code along with the given data.
   *
   * @param message {@link JSONObject} which contains the result of the request.
   * @param httpServletResponse Servlet to which the response will be written
   */
  public static void sendSuccessResponse(
      JSONObject message, HttpServletResponse httpServletResponse) {
    httpServletResponse.setStatus(200);
    writeResponse(getSuccessResponseJson(message), httpServletResponse);
  }

  public static JSONObject getSuccessResponseJson(JSONObject message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, SUCCESS);
    response.put(MESSAGE, message);
    return response;
  }

  public static JSONObject getSuccessResponseJson(JSONArray message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, SUCCESS);
    response.put(MESSAGE, message);
    return response;
  }

  public static Response getSuccessResponse(JSONArray message) {
    // Remove Access-Controle-Allow-Origin
    return Response.status(Status.OK)
        .entity(getSuccessResponseJson(message).toString())
        .header("Access-Control-Allow-Origin", "*")
        .build(); // NO I18N
  }

  public static Response getSuccessResponse(JSONObject message) {
    // Remove Access-Controle-Allow-Origin
    return Response.status(Status.OK)
        .entity(getSuccessResponseJson(message).toString())
        .header("Access-Control-Allow-Origin", "*")
        .build(); // NO I18N
  }

  public static JSONObject getErrorResponseJson(String message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, FAILED);
    response.put(ERROR, message);
    return response;
  }

  /**
   * Use to send error response when there is Runtime Exceptions such as {@link
   * ArrayIndexOutOfBoundsException}, {@link RuntimeException},..<br>
   * It constructs Internal Server Error message with Http status code 500.
   *
   * @param httpServletResponse Servlet to which the response will be written
   */
  public static void sendErrorResponse(HttpServletResponse httpServletResponse) {
    JSONObject response = new JSONObject();
    response.put(STATUS, FAILED);
    response.put(ERROR, "Internal Server Error");
    httpServletResponse.setStatus(500);
    writeResponse(response, httpServletResponse);
  }

  /**
   * Use to send custom error response with status code. To handle genericExceptions
   *
   * @param httpServletResponse Servlet to which the response will be written
   */
  public static void sendErrorResponse(
      String message, int statusCode, HttpServletResponse httpServletResponse) {
    JSONObject response = new JSONObject();
    response.put(STATUS, FAILED);
    response.put(ERROR, message);
    httpServletResponse.setStatus(statusCode);
    writeResponse(response, httpServletResponse);
  }

  /**
   * Writes {@link JSONObject} into the servlet response using the {@link
   * JSONObject#write(java.io.Writer)} for efficiency.
   *
   * @param message JSONObject to be written into the servlet response
   * @param servletResponse Servlet to which the response will be written
   */
  private static void writeResponse(JSONObject message, HttpServletResponse servletResponse) {
    try {
      servletResponse.setContentType("application/json");
      servletResponse.setCharacterEncoding("UTF-8");
      message.write(servletResponse.getWriter());
    } catch (JSONException | IOException e) {
      LOGGER.log(Level.SEVERE, "Unable write response for request", e);
    }
  }

  public static void sendFileResponse(String filePath, HttpServletResponse response)
      throws IOException {
    final File fileObj = new File(filePath);
    byte[] bytes = new byte[1024];
    response.setContentType("application/octet-stream"); // No I18N
    response.setStatus(200);

    try (FileInputStream inStream = new FileInputStream(fileObj);
        ServletOutputStream outStream = response.getOutputStream(); ) {
      int inbyte = 0;
      while ((inbyte = inStream.read(bytes)) != -1) {
        outStream.write(bytes, 0, inbyte);
      }
      outStream.flush();
    }
  }
}
