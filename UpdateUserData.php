<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
  $username=$_POST["username"];
  $email=$_POST["email"];   
   $password=$_POST["password"];
  $firstname=$_POST["firstname"];
  $lastname=$_POST["lastname"];
    $currentUsername=$_POST["currentUsername"];

     $currentPassword=$_POST["currentPassword"];

  
   
   $statement= "UPDATE Users SET username ='$username',email ='$email',password ='$password',firstname ='$firstname',lastname ='$lastname' WHERE username='$currentUsername' AND password ='$currentPassword'";
   if(mysqli_query($con, $statement)){
    echo "User data successfully updated";
   }else{ 
        echo "Error updating user data" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>