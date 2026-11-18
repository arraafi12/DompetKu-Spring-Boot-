document.addEventListener("DOMContentLoaded", function () {
    const toggleButtons = document.querySelectorAll(".toggle-password");

    toggleButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            const targetId = button.getAttribute("data-target");
            const passwordInput = document.getElementById(targetId);

            if (!passwordInput) {
                console.error("Input password tidak ditemukan:", targetId);
                return;
            }

            const showPassword = passwordInput.type === "password";

            passwordInput.type = showPassword ? "text" : "password";

            button.innerHTML = showPassword
                ? '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3l18 18M10.6 10.6a2 2 0 002.8 2.8M9.9 5.2A11 11 0 0121 12a11 11 0 01-3.2 4.2M6.2 6.2A11 11 0 003 12a11 11 0 0018 0"/></svg>'
                : '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>';

            button.setAttribute(
                "aria-label",
                showPassword ? "Sembunyikan password" : "Tampilkan password"
            );
        });
    });
});
