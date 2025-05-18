
const BASE_URL = "http://localhost:8080";

function loadPage(page) {
    fetch(`${page}`)
        .then(response => response.text())
        .then(data => {
            const container = document.getElementById('main-content');
            container.innerHTML = data;

            if (typeof initMyCoursesPage === "function") {
                initMyCoursesPage();
            }

            // Extract and evaluate scripts manually
            const scripts = container.querySelectorAll('script');
            scripts.forEach(script => {
                const newScript = document.createElement('script');
                if (script.src) {
                    newScript.src = script.src;
                } else {
                    newScript.textContent = script.textContent;
                }
                document.body.appendChild(newScript);
                script.remove(); // Optional: remove the original script tag
            });
        })
        .catch(err => {
            document.getElementById('main-content').innerHTML =
                '<p class="text-danger">Failed to load content.</p>';
            console.error(err);
        });
}

function decodeJWT(token) {
    if (!token) return null;

    const parts = token.split('.');
    if (parts.length !== 3) return null; const payload = parts[1];
    // Base64URL decode
    const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));

    try {
        return JSON.parse(decodedPayload);
    } catch (e) {
        console.error("Invalid JWT payload:", e); return null;
    }
}

function getUserId() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("No token found, redirecting...");
        window.location.href = "index.html";
        return;
    }
    const decoded = decodeJWT(token);
    console.log("Decoded JWT in getUserId:", decoded);
    return decoded.userid;
}


function getUserName() {
    const token = localStorage.getItem("token");
    if (!token)
        window.location.href = "index.html";
    const decoded = decodeJWT(token);
    return decoded.sub;
}

function getUserEmail() {
    const token = localStorage.getItem("token");
    if (!token)
        window.location.href = "index.html";;
    const decoded = decodeJWT(token);
    return decoded.userEmail;
}

function getUserType() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("No token found");
        window.location.href = "index.html";
        return;
    }

    const decoded = decodeJWT(token);
    if (!decoded || !decoded.userType) {
        console.warn("Invalid or missing usertype in token");
        window.location.href = "index.html";
        return;
    }

    return decoded.userType;
}


function getAuthorization() {
    const token = localStorage.getItem("token");
    if (!token)
        window.location.href = "index.html";
    const decoded = decodeJWT(token);
    return `Bearer ${token}`;
}

function logout() {
    localStorage.removeItem("token");
    localStorage.clear();
    window.location.href = "index.html";
}