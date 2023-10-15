$(document).ready(function(){
    $("#buttonAdd2Cart").on("click", function(e){
        addToCart();
    });
});


function addToCart(){
    quantity = $("#quantity" + productId).val(); //productId is set in product_details.html, this id is in quantity_contorl.html fragment
    url = contextPath + "cart/add/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr){ // csrfHeaderName and csrfValue are provided in product_details.html
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(response){
        showModalDialog("Shopping Cart", response);
    }).fail(function(){
        showErrorModal("Error!!! Could not add product to shopping cart.");
    })
}