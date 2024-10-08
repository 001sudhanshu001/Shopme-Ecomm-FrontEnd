var dropDownCountry;
var dataListState;
var fieldState;

$(document).ready(function() {
    dropDownCountry = $("#country");
    dataListState =  $("#listStates"); // 1.Option Dependent Dropdown $("#listStates")
   // dataListState =  $("#stateList"); // 2.Option Dependent Dropdown $("#stateList")
    fieldState = $("#state");

   dataListState.on("click", function() {
       loadStatesForCountry();
       fieldState.val("").focus();
   });

    $('#buttonCancel').click(function() {
        window.location = moduleURL;
    });
});
    function loadStatesForCountry(){
        selectedCountry = $("#country option:selected");
        countryId = selectedCountry.val();
        url = contextPath + "settings/list_states_by_country/" + countryId;

        $.get(url, function(responseJSON) {
            dataListState.empty();

            $.each(responseJSON, function(index, state) {
                $("<option>").val(state.name).text(state.name).appendTo(dataListState);
            });

        }).fail(function() {
            alert('failed to connect to the server!');
        });
    }

    function checkPasswordMatch(confirmPassword){
        if(confirmPassword.value != $("#password").val()){
            confirmPassword.setCustomValidity("Password do no match!")
        }else{
            confirmPassword.setCustomValidity("");
        }
    }

//        function checkEmailUnique(form){
//                url = contextPath + "customers/check_unique_email";
//                customerEmail = $("#email").val();
//                csrfValue = $("input[name='_csrf']").val();
//
//                params = {email: customerEmail,_csrf: csrfValue};
//
//                $.post(url, params, function(response){
//                    if(response == "OK"){
//                        form.submit();
//                    }else if(response == "Duplicated"){
//                        showWarningModal("There is another Customer having the email " + customerEmail + ". Please use another Email");
//                    }else{
//                        showErrorModal("Unknown response from server");
//                    }
//                }).fail(function() {
//                     showModalDialog("Error","Could not connect to server");
//                });
//                return false;
//        }

    function showModalDialog(title, message) {
        $("#modalTitle").text(title);
        $("#modalBody").text(message);
        $("#modalDialog").modal();
    }

    function showErrorModal(message){
        showModalDialog("Error", message);
    }
    function showWarningModal(message){
       showModalDialog("Warning", message);
    }
