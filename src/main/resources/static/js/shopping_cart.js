$(document).ready(function(){
    $(".linkMinus").on("click", function(e){
        e.preventDefault();
        decreaseQuantity($(this));
    })

    $(".linkPlus").on("click", function(e){
        e.preventDefault();
        increaseQuantity($(this));
    })
});

function decreaseQuantity(link){
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if(newQuantity  > 0){
        quantityInput.val(newQuantity);

        updateQuantity(productId, newQuantity);
    }else{
        showWarningModal("Minimum allowed Quantity is 1");
    }
}

function increaseQuantity(link){
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;

    if(newQuantity <= 5){
        quantityInput.val(newQuantity);

        updateQuantity(productId, newQuantity);
    }else{
        showWarningModal("Maximum allowed Quantity is 5");
    }
}

function updateQuantity(productId, quantity) {
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr){ // csrfHeaderName and csrfValue are provided in shopping_cart.html
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(updatedSubtotal){
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function(){
        showErrorModal("Error!!! Could not update product quantity.");
    })
}

// This is for every Product
function updateSubtotal(updatedSubtotal, productId) {
    formatedSubtotal = $.number(updatedSubtotal, 2);
    $("#subTotal" + productId).text(formatedSubtotal);
}

// This is for Estemated subtotal
function updateTotal() {
    console.log("called");
    total = 0.0;

    $(".subTotal").each(function(index, element) {
        total += parseFloat(element.innerHTML.replaceAll(",",""));
        console.log(total);
    });

    formtedTotal = $.number(total, 2);    
    $("#total").text(formtedTotal);
}