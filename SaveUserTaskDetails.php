<?php
$con=mysqli_connect("mysql12.000webhost.com","a5659879_brice","fXu*br5q","a5659879_time");

    // Check connection
if (!$con) {
    die("Connection failed: " . mysqli_connect_error());
}
echo "Connected successfully";


  $username=$_POST["username"];
  $taskname=$_POST["taskname"];   
   $taskday=$_POST["taskday"];
    $taskmonth=$_POST["taskmonth"];
  $taskyear=$_POST["taskyear"];   
   $taskdetails=$_POST["taskdetails"];
    $tasktime=$_POST["tasktime"];
  $settasktime=$_POST["settasktime"];   
   
   
   $statement= mysqli_prepare($con, "INSERT INTO Users(username , taskname , taskday, taskmonth,
   taskyear , taskdetails , tasktime , settasktime ) VALUES(?,?,?)");
   
   mysqli_stmt_bind_param( $statement, "sss",$username,$email,$password);
    mysqli_stmt_execute($statement);
   
   mysqli_stmt_close($statement);
   
   mysqli_close($con);

?>