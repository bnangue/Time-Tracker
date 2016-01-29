<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

  $username=$_POST["username"];
  $email=$_POST["email"];   
   $password=$_POST["password"];
   
   $statement= mysqli_prepare($con, "INSERT INTO Users(username , email , password) VALUES(?,?,?)");
   mysqli_stmt_bind_param( $statement, "sss",$username,$email,$password);
    mysqli_stmt_execute($statement);
	
   mysqli_stmt_close($statement);
 
    
   
   mysqli_close($con);

?>