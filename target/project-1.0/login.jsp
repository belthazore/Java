<!DOCTYPE HTML>
<html>
 <head>
  <meta charset="utf-8">
  <title>Java Tomcat test server | Login</title>
  <style><%@include file="/login.css"%></style>
</head>


<body>

  <div class="login-page">
    <div class="form">

      <form action="/project/">
        <input  type="text"     name="login"    placeholder="login" /><br>
        <input  type="password" name="password" placeholder="password" /><br>
        <button type="submit"   style="border-radius: 3px;">Login</button>
          <!-- Если авторизация не успешна - сообщим -->
        <% String badResult = request.getParameter("badResult"); %>
        <p style="color: red"><%= ( badResult==null ? "" : ("<br>"+badResult) ) %></p>
      </form>
    </div>

   </div>

</body>

</html>