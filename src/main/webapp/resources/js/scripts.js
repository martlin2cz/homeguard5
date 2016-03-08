function makeRowClickExpanding(tableWidgetVar) {
	console.debug('Making  ' + tableWidgetVar + " with click-to-expand rows");

	var id = PF(tableWidgetVar).jqId;
	var rows = $(id + " tr");

	rows.each(function(i, elem) {
		if (i == 0) {
			return;
		}

		// console.debug(i + "->" + elem);
		var index = i - 1;

		elem.onclick = function() {
			$(".ui-row-toggler")[index].click();
		};

		elem.classList.add("togglable-row");
	});

	// console.debug('done ' + tableWidgetVar);
}

function makeRowClickExpandingOnLoad(tableWidgetVar) {
	$(function() {
		makeRowClickExpanding(tableWidgetVar);
	});
}
