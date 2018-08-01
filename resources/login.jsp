<!DOCTYPE HTML>
<html>
 <head>
  <meta charset="utf-8">
  <%
  	String result = request.getParameter("result");
  	String status = ( result==null ? "" : (result) );
  %>
  <title>Java Tomcat test server - Login <%= ("| "+status)%></title>
  <style><%@include file="/login.css"%></style>
</head>


<body>

  <div class="login-page">
    <div class="form">

      <form action="/project/">
        <b>Login</b>
        <input  type="text"     name="login"    placeholder="login" /><br>
        <input  type="password" name="password" placeholder="password" /><br>
        <button type="submit"   style="border-radius: 3px;">Login</button>
          <!-- Если авторизация не успешна - сообщим -->
        <p style="color: red"><%= status %></p>
      </form>
    </div>

   </div>

</body>

</html>