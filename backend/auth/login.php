<?php

// STEP 1: Start session
session_start();

// STEP 2: Include the database connection file
include('db.php'); // Assumes db.php is already included as shown in the previous step

// STEP 3: Check if username and password are submitted via POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // STEP 4: Clean user inputs to avoid SQL injection
    $username = mysqli_real_escape_string($conn, $_POST['username']);
    $password = $_POST['password'];

    // STEP 5: Check if the username exists in the database
    $sql = "SELECT * FROM users WHERE username = '$username'";
    $result = $conn->query($sql);

    // STEP 6: Validate user credentials
    if ($result->num_rows == 1) {
        $user = $result->fetch_assoc();

        // Verify the password
        if (password_verify($password, $user['password'])) {
            // STEP 7: Password matches, start the session and store user info
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['username'] = $user['username'];

            // Redirect to chat interface
            header("Location: ../chat.html");
            exit();
        } else {
            // Invalid password
            $errorMessage = "Invalid password. Please try again.";
        }
    } else {
        // Invalid username
        $errorMessage = "Username does not exist. Please try again.";
    }
}

// STEP 8: Display error message if there's any
if (isset($errorMessage)) {
    echo "<p class='error'>$errorMessage</p>";
}
?>

<!-- HTML Form for login -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="login-container">
        <h2>Login to your account</h2>
        <form method="POST" action="login.php">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>

            <button type="submit">Login</button>
        </form>

        <p>Don't have an account? <a href="signup.html">Sign up</a></p>
    </div>
</body>
</html>