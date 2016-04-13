<?php //v1.0.20160410

$perform_authentication=false;

if ($perform_authentication) {
	$valid_passwords = array ("gate" => "gate1");
	$valid_users = array_keys($valid_passwords);
	$user = $_SERVER['PHP_AUTH_USER'];
	$pass = $_SERVER['PHP_AUTH_PW'];
	$validated = (in_array($user, $valid_users)) && ($pass == $valid_passwords[$user]);
	if (!$validated) {
		header('WWW-Authenticate: Basic realm="Generic HTTP Device"');
		header('HTTP/1.0 401 Unauthorized');
		if (isset($_POST['Test'])) {
			echo "Test=Failed : ";
		}
		if (isset($_POST['Led1'])) {
			echo "Led1=Failed : ";
		}
		if (isset($_POST['Led2'])) {
			echo "Led2=Failed : ";
		}
		die ("Authentication Required!");
	}
}
// If code arrives here, this would be a valid user.

//BUILD ARRAY VALUES
date_default_timezone_set('America/Chicago');
//CHECK IF GD IS INSTALLED


if (isset($_POST['Led1on'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 1:1");
	echo $rpi;
    die();
}
if (isset($_POST['Led1off'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 1:0");
	echo $rpi;
  	die();
}
if (isset($_POST['Led2on'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 2:1");
	echo $rpi;
  die();
}
if (isset($_POST['Led2off'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 2:0");
	echo $rpi;
     die();
}
if (isset($_POST['Led3on'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 3:1");
	echo $rpi;
    die();
}
if (isset($_POST['Led3off'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 3:0");
	echo $rpi;
  	die();
}
if (isset($_POST['Led4on'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 4:1");
	echo $rpi;
  die();
}
if (isset($_POST['Led4off'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 4:0");
	echo $rpi;
     die();
}
if (isset($_POST['Led5on'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 5:1");
	echo $rpi;
  die();
}
if (isset($_POST['Led5off'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 5:0");
	echo $rpi;
     die();
}
if (isset($_POST['refresh'])) {
	$rpi = exec("/usr/bin/python /var/www/adcommand.py 9");
	echo $rpi;
     die();
}

?>

<html>
<head>
<meta charset="UTF-8" />
<meta name=viewport content='width=700'>
</style>
</head>

<body>
<div class="center">
<pre>
</pre>
  <p>Post commands </p>
 <p>LedxOn - Turns LED/Relay On </p>
  <p>LedxOff - Turns LED/Relay Off </p>
  <p>refresh - Send Status command </p>

  <p>Where X is relay 1 to 5  </p


  
</body>
</html>