package com.test.restapi.response;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorResponseHandler extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
    Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
    handleGenericException(resp, throwable, statusCode);
  }

  public static void handleGenericException(
      HttpServletResponse response, Throwable throwable, int statusCode) {

    switch (statusCode) {
      case 404:
        TestResponse.sendErrorResponse("URL Not Found", statusCode, response);
        break;
      case 408:
        TestResponse.sendErrorResponse("Request timeout", statusCode, response);
        break;
      case 414:
        TestResponse.sendErrorResponse("URI too large", statusCode, response);
        break;
      case 413:
        TestResponse.sendErrorResponse("Payload too large", statusCode, response);
        break;
      case 505:
        TestResponse.sendErrorResponse("HTTP Version Not Supported", statusCode, response);
        break;
    }
  }
}
