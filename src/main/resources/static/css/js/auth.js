document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toggle-password").forEach(function (button) {
        button.addEventListener("click", function () {
            const targetId = button.dataset.target;
            const passwordInput = document.getElementById(targetId);

            if (!passwordInput) {
                console.error("Input password tidak ditemukan:", targetId);
                return;
            }

            const showPassword = passwordInput.type === "password";

            passwordInput.type = showPassword ? "text" : "password";

            button.classList.toggle("is-visible", showPassword);
            button.setAttribute("aria-pressed", String(showPassword));
            button.setAttribute(
                "aria-label",
                showPassword ? "Sembunyikan password" : "Tampilkan password"
            );
        });
    });
});
