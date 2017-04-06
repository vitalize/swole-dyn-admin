function rqlAdd(){
    var typeSelector = document.getElementById('RQL_ITEM_TYPE');
    var selectedType = typeSelector.options[typeSelector.selectedIndex].text;

    var actionSelector = document.getElementById('RQL_ACTION_TYPE');
    var selectedAction = actionSelector.options[actionSelector.selectedIndex].text;


    //add,remove,update all have an id="" attribute...but query does not
    var idAttr = selectedAction == 'query-items' ? '' : ' id=\"\"';

    var rql = '<' + selectedAction + ' item-descriptor=\"' + selectedType + '\"' + idAttr + '>';
    rql += '</' + selectedAction + '>';

    document.getElementsByName('xmltext')[0].value += (rql + "\n");
}


function rqlClear() {
    document.getElementsByName('xmltext')[0].value = "";
}