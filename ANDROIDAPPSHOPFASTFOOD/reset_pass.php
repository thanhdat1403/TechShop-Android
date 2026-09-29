<?php

include "connect.php";

if (isset($_GET['key']) && isset($_GET['reset'])) {

    $email = $_GET['key'];
    $pass = $_GET['reset'];

    /*
     * Reset password page.
     *
     * Example local URL:
     * http://YOUR_LOCAL_IP:8888/ANDROIDAPPSHOPFASTFOOD/reset_pass.php
     * ?key=YOUR_EMAIL@example.com&reset=YOUR_RESET_TOKEN
     */

    $query = "SELECT email, pass FROM user WHERE email = '$email' AND pass = '$pass'";

    $data = mysqli_query($conn, $query);

    if ($data == true) {
        ?>

        <form method="post" action="submit_new.php">

            <input
                type="hidden"
                name="email"
                value="<?php echo htmlspecialchars($email); ?>"
            >

            <p>Enter New password</p>

            <input
                type="password"
                name="password"
                required
            >

            <input
                type="submit"
                name="submit_password"
                value="Submit"
            >

        </form>

        <?php
    }
}

?>