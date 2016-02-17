/* [ ---- Gebo Admin Panel - common ---- ] */

$(function() {

	gebo_sidebar.scrollbar();

	//* to top
	$().UItoTop({inDelay:200,outDelay:200,scrollSpeed: 500});
});


gebo_sidebar = {
	scrollbar: function() {
		$('.app_container').slimScroll({
			position: 'left',
			height: $(window).height()-90,
			alwaysVisible: false,
			opacity: '0.2'
		});
	}
};