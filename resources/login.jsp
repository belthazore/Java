<!DOCTYPE HTML>
<html>
 <head>
  <meta charset="utf-8">
  <%
  	String result = request.getParameter("result");
  	String status = ( result==null ? "" : (result) );

    String loginFromUrl = request.getParameter("login");
    String passwordFromUrl = request.getParameter("password");

    String login = ( loginFromUrl==null ? "login" : loginFromUrl );
    String password = ( passwordFromUrl==null ? "password" : passwordFromUrl );
  %>
  <title>Java Tomcat test server - Login <%= ("| "+status)%></title>
  <style><%@include file="/login.css"%></style>
</head>


<body>

  <div class="login-page">
    <div class="form">

      <form action="/project/">
        <b>Login</b>
        <b>______________________________</b>
        <br><br><br>
        <input  type="text"     name="login"    placeholder="login" value= <%= login%> /><br>
        <input  type="password" name="password" placeholder="password" value= <%= password%> /><br>
        <br>
        <button type="submit"   style="border-radius: 3px;">Login</button>
          <!-- Если авторизация не успешна - сообщим -->
        <p style="color: red"><%= status %></p>
      </form>
    </div>

   </div>

</body>

</html>