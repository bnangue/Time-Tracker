<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

  $email=$_POST["email"];   
   $password=$_POST["password"];
   
   $statement= mysqli_prepare($con, "SELECT * FROM Users  WHERE email = ? AND password = ?");
   mysqli_stmt_bind_param($statement, "ss" , $email, $password);
    mysqli_stmt_execute($statement);
   
   mysqli_stmt_store_result($statement);
   mysqli_stmt_bind_result($statement,$UserID,$username,$email,$password,$firstname,$lastname,$onlineStatus);

   $user=array();
   while(mysqli_stmt_fetch($statement))
   {
   $user[username]=$username;
   $user[email]=$email;
   $user[password]=$password;
   $user[onlineStatus]=$onlineStatus;
   }
   
   echo json_encode($user);
      mysqli_stmt_close($statement);

   mysqli_close($con);

?>