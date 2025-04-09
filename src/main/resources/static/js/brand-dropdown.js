document.addEventListener("DOMContentLoaded", function() {
    const brandDropdown = document.getElementById("brandDropdown");
    const brandList = document.getElementById("brandList");

    // Kiểm tra sự tồn tại của phần tử trước khi thêm sự kiện
    if (brandDropdown) {
        brandDropdown.addEventListener("click", function(event) {
            event.stopPropagation(); // Ngăn sự kiện click lan truyền đến document

            if (brandList.style.display === "none" || brandList.style.display === "") {
                fetch("/api/brands")
                    .then(response => response.json())
                    .then(data => {
                        let brandsHtml = '<ul>';
                        data.forEach(brand => {
                            brandsHtml += `<li><a class="dropdown-item" href="/products/brand/${brand.id}">${brand.name}</a></li>`;
                        });
                        brandsHtml += '</ul>';
                        brandList.innerHTML = brandsHtml;
                        brandList.style.display = "block";
                    })
                    .catch(error => console.error('Error fetching brands:', error));
            } else {
                brandList.style.display = "none";
            }
        });
    }

    if (brandList) {
        document.addEventListener("click", function(event) {
            if (!brandDropdown.contains(event.target)) {
                brandList.style.display = "none";
            }
        });
    }
});
