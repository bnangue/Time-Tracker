<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
    $username=$_POST["username"];   
     
   
   $statement= "DELETE ProfilePictures,Users FROM ProfilePictures,Users WHERE Users.username=ProfilePictures.username AND Users.username='$username'";
   if(mysqli_query($con, $statement)){
    echo "User successfully deleted";
   }else{ 
        echo "Error deleting User" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>