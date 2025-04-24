<?php
// STEP 1: Define MySQL credentials and target database
$servername = "localhost";
$dbUsername = "root";
$dbPassword = "";
$dbName     = "comsDB";

// STEP 2: Connect to MySQL server (without selecting DB yet)
$conn = new mysqli($servername, $dbUsername, $dbPassword);

// STEP 3: Validate connection
if ($conn->connect_error) {
    die("Connection to MySQL failed: " . $conn->connect_error);
}

// STEP 4: Create the database if it doesn't exist
$createDBSQL = "CREATE DATABASE IF NOT EXISTS `$dbName`";
if (!$conn->query($createDBSQL)) {
    die("Error creating database `$dbName`: " . $conn->error);
}

// STEP 5: Select the database
$conn->select_db($dbName);

// STEP 6: Create the `users` table if it doesn't exist
$createUsersTableSQL = "
CREATE TABLE IF NOT EXISTS users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";
if (!$conn->query($createUsersTableSQL)) {
    die("Failed to create users table: " . $conn->error);
}

// STEP 7: Insert dummy accounts if they don't exist
$dummyPassword = password_hash("user123", PASSWORD_DEFAULT);
$dummyUsers = ["user1", "user2"];

foreach ($dummyUsers as $username) {
    $checkUserSQL = "SELECT * FROM users WHERE username = '$username'";
    $result = $conn->query($checkUserSQL);
    
    if ($result->num_rows === 0) {
        $insertSQL = "INSERT INTO users (username, password) VALUES ('$username', '$dummyPassword')";
        if (!$conn->query($insertSQL)) {
            die("Failed to insert dummy user `$username`: " . $conn->error);
        }
    }
}

// ✅ Setup complete
echo "Database and users table setup complete.";
?>