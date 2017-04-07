var RQL_ACTION_TYPE_QUERY_ITEMS = "query-items";

function rqlAdd(){
    var typeSelector = document.getElementById('RQL_ITEM_TYPE');
    var selectedType = typeSelector.options[typeSelector.selectedIndex].text;



    var selectedAction = $("#RQL_ACTION_TYPE").val();

    //add,remove,update all have an id="" attribute...but query does not
    var idAttr = selectedAction == RQL_ACTION_TYPE_QUERY_ITEMS ? '' : ' id=\"\"';

    var rql = '<' + selectedAction + ' item-descriptor=\"' + selectedType + '\"' + idAttr + '>';

    if(selectedAction == RQL_ACTION_TYPE_QUERY_ITEMS){
        rql += "ALL";

        if($('#RQL_RANGE_ENABLED').prop('checked')) {
            rql += " RANGE " + $("#RQL_RANGE_SKIP").val() + "+" + $("#RQL_RANGE_SHOW").val();
        }

    }

    rql += '</' + selectedAction + '>';

    document.getElementsByName('xmltext')[0].value += (rql + "\n");
}


function rqlClear() {
    document.getElementsByName('xmltext')[0].value = "";
}


$(document).ready(function() {
    var data = [];
    for(var n in itemDescriptors){
        data.push({ text : n})
    }

    $("#RQL_ITEM_TYPE").select2({
        data : data
    });


    $("#RQL_ACTION_TYPE").select2();

    $("#RQL_ACTION_TYPE").change(
        function(){
            var val = $(this).val();

            var rangeToolbar = $("#RQL_RANGE_TOOLBAR");

            if(val == RQL_ACTION_TYPE_QUERY_ITEMS){
                rangeToolbar.show();
            } else {
                rangeToolbar.hide();
            }
        }
    );


});