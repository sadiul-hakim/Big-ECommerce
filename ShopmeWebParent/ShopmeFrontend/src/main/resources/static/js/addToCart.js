document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".add_to_cart_btn").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const url = this.dataset.url;

            fetch(url, {
                method: "GET"
            })
                .then(response => {
                    if (!response.ok) throw new Error("Failed to add product");
                    return response.json();
                })
                .then(msg => {
                    showToast(msg);
                })
                .catch(err => {
                    showToast("Error: " + err.message);
                });
        });
    });
});

function showToast(message) {

    // Bootstrap 5 toast example
    const toastContainer = document.getElementById("toastContainer");
    const toastEl = document.createElement("div");
    toastEl.className = "toast align-items-center text-bg-primary border-0";
    toastEl.role = "alert";
    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message.message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    toastContainer.appendChild(toastEl);
    new bootstrap.Toast(toastEl).show();
}