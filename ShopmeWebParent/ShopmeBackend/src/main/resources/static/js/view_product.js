const main_image = document.getElementById("main_image");
const side_images = document.getElementsByClassName("extra_image");

// Change image logic
for (let img of side_images) {
    img.onclick=()=>{
        let temp = main_image.src;
        main_image.src=img.src;
        img.src=temp;
    }
}