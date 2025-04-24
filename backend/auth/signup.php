<?php
// STEP 1: Start session
session_start();

// STEP 2: Include the database connection file
include('db.php');

// STEP 3: Check if the signup form is submitted
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // STEP 4: Clean user inputs to prevent SQL injection
    $username = mysqli_real_escape_string($conn, $_POST['username']);
    $password = $_POST['password'];
    $confirmPassword = $_POST['confirm_password'];

    // STEP 5: Check if passwords match
    if ($password !== $confirmPassword) {
        $errorMessage = "Passwords do not match.";
    } else {
        // STEP 6: Check if the username already exists in the database
        $sql = "SELECT * FROM users WHERE username = '$username'";
        $result = $conn->query($sql);

        if ($result->num_rows > 0) {
            $errorMessage = "Username already taken.";
        } else {
            // STEP 7: Hash the password for security
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

            // STEP 8: Insert the new user into the database
            $insertSql = "INSERT INTO users (username, password) VALUES ('$username', '$hashedPassword')";

            if ($conn->query($insertSql) === TRUE) {
                // STEP 9: Registration successful, redirect to login page
                header("Location: login.php");
                exit();
            } else {
                $errorMessage = "Error: Could not create account.";
            }
        }
    }
}

// STEP 10: Display error message if there's any
if (isset($errorMessage)) {
    echo "<p class='error'>$errorMessage</p>";
}
?>

<!-- HTML Form for signup -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="signup-container">
        <h2>Create a new account</h2>
        <form method="POST" action="signup.php">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>

            <label for="confirm_password">Confirm Password:</label>
            <input type="password" id="confirm_password" name="confirm_password" required>

            <button type="submit">Sign Up</button>
        </form>

        <p>Already have an account? <a href="login.php">Login</a></p>
    </div>
</body>
</html>