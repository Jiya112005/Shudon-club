const scrollContainer = document.querySelector(".rgames")
console.log(scrollContainer);

const leftScroll = document.getElementById("leftbtn")
leftScroll.addEventListener("click",function(e){
    // e.preventDefault();
    scrollContainer.style.scrollBehavior = "smooth";
    scrollContainer.scrollLeft-=950;
}) 
const rightScroll = document.getElementById("rightbtn")
rightScroll.addEventListener("click",function(e){
    scrollContainer.style.scrollBehavior = "smooth";
    scrollContainer.scrollLeft+=950
    
})

scrollContainer.addEventListener("wheel",(e)=>{
    e.preventDefault();
    scrollContainer.scrollLeft +=e.deltaY;
})