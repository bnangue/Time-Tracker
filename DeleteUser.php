<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
    $username=$_POST["username"];   
   
   $password=$_POST["password"];
  
   
   $statement= "DELETE FROM Users WHERE username='$username' AND password ='$password'";
   if(mysqli_query($con, $statement)){
    echo "User successfully deleted";
   }else{ 
        echo "Error deleting User" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>