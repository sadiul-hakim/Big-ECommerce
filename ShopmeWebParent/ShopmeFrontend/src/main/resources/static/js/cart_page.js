document.addEventListener("DOMContentLoaded", () => {

    const totalItemsPrice = document.getElementById("totalItemsPrice");
    const paymentTotal = document.getElementById("paymentTotal");

    function validateInput(e) {
        let input = e.target;
        let value = parseInt(input.value, 10);

        if (isNaN(value)) {
            input.value = 1; // fallback if someone clears it or types non-numeric
            return;
        }

        if (value < 1) input.value = 1;
        if (value > 99) input.value = 99;
    }

    async function updateQuantity(e, value, replace, updateQuantityField) {
        const li = e.target.closest("li");
        const cartItemId = li.dataset.cartId;

        const res = await fetch(`${pathContext}cart/update-quantity?cartItemId=${cartItemId}&quantity=${value}&replace=${replace}`);
        const data = await res.json();

        if (data.quantity <= 0) {
            li.remove();
        } else {
            if (updateQuantityField) {
                li.querySelector(".quantity_field").value = data.quantity;
            }
            li.querySelector(".item-price").textContent = data.totalPrice;
            li.querySelector(".item-actual-price").textContent = data.actualPrice;
        }
        totalItemsPrice.textContent = data.totalItemsPrice;
        paymentTotal.textContent = data.paymentTotal;
    }

    document.querySelectorAll(".quantity_field").forEach(field => {
        field.oninput = async (e) => {
            validateInput(e);
            let value = e.target.value;
            await updateQuantity(e, value, true, false);
        }
    });

    // Increment buttons
    document.querySelectorAll(".increment-btn").forEach(btn => {
        btn.addEventListener("click", async (e) => {
            await updateQuantity(e, 1, false, true);
        });
    });

    // Decrement buttons
    document.querySelectorAll(".decrement-btn").forEach(btn => {
        btn.addEventListener("click", async (e) => {
            await updateQuantity(e, -1, false, true);
        });
    });

    // DELETE button
    document.querySelectorAll(".delete-btn").forEach(btn => {
        btn.addEventListener("click", async (e) => {
            const cartItemId = btn.dataset.cartId;
            const li = btn.closest("li");

            const res = await fetch(`${pathContext}cart/delete-item?cartItemId=${cartItemId}`);
            const data = await res.json();

            if (res.ok) {
                li.remove();
                showToast(data.message)
            } else {
                showToast(data.message || "Failed to delete item");
            }
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
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    toastContainer.appendChild(toastEl);
    new bootstrap.Toast(toastEl).show();
}