$(document).ready(function(){
    $(".linkMinus").on("click", function(e){
        e.preventDefault();
        decreaseQuantity($(this));
    })

    $(".linkPlus").on("click", function(e){
        e.preventDefault();
        increaseQuantity($(this));
    })

    $(".linkRemove").on("click", function(e){
        e.preventDefault();
        removeProduct($(this));
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
    console.log("UPDATED CALLED");
    total = 0.0;
    productCount = 0;

    $(".subTotal").each(function(index, element) {
        productCount++;
        total += parseFloat(element.innerHTML.replaceAll(",",""));
        console.log(total);
    });

    if(productCount < 1){
        console.log("Product Count" + productCount);
      //  location.reload();
        showEmptyShoppingCart();
    }else{
        formatedTotal = $.number(total, 2);
        $("#total").text(formatedTotal);
    }

    
}

function removeProduct(link) {
    url = link.attr("href");
    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function(xhr){ // csrfHeaderName and csrfValue are provided in shopping_cart.html
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(response){
        //location.reload(); Instead of relaoding we will remove that dive only by using JS
        rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumebers();

        showModalDialog("Shopping Cart", response);

    }).fail(function(){
        showErrorModal("Error!!! Could not remove Product.");
    })

}

function removeProductHTML(rowNumber){
    $("#row" + rowNumber).remove();
    $("#blankline" + rowNumber).remove();
}

function updateCountNumebers() {
    $(".divCount").each(function(index, element){
        element.innerHTML = "" + (index + 1); // because index starts from 0
    });
}

function showEmptyShoppingCart() {
	$("#sectionTotal").hide();
	$("#sectionEmptyCartMessage").removeClass("d-none");
}