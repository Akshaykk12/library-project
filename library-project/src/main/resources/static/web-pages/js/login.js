document.getElementById("loginForm").addEventListener("submit", function (e) {
  e.preventDefault();
  verify();
});

function verify() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  fetch(`${BASE_URL}/auth/signin`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ userName: email, password })
  })
    .then(response => {
      // Handle different status codes explicitly
      if (response.status === 200) {
        return response.json();
      } else if (response.status === 404) {
        throw new Error("404 Not Found - Users data not available.");
      } else if (response.status === 401) {
        throw new Error("401 Unauthorized - Invalid token or access denied.");
      } else if (response.status === 500) {
        throw new Error("500 Internal Server Error.");
      } else {
        throw new Error(`Unexpected status code: ${response.status}`);
      }
    })
    .then(data => {
  console.log("Login response data:", data);

  if (!data || !data.token) {
    throw new Error("Token Not Found");
  }

  localStorage.setItem("token", data.token);

  // Decode the token if userType is not in the response directly
  let userType = data.userType;
  if (!userType) {
    const decoded = decodeJWT(data.token);
    console.log("Decoded token:", decoded);
    userType = decoded.usertype;
  }

  console.log("Resolved User Type:", userType);

  if (userType && userType.toUpperCase() === "ADMIN") {
    window.location.href = "../html/admin/Admin.html";
  } else{
    window.location.href = "../html/user/User.html";
  }
})


    .catch(error => {
      console.error(error);
      document.getElementById("error").textContent = error.message || "Something went wrong. Please try again.";
    });
}
