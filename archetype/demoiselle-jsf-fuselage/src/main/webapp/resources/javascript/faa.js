/**
 * 
 */

function openWindow(URL) {
	window
			.open(
					URL,
					'_blank',
					'width=800, height=600, top=100, left=100, scrollbars=yes, status=no, toolbar=no, location=no, directories=no, menubar=no, resizable=no, fullscreen=no');
}

function openReport(reportPath, reportId) {
	openWindow("/atius/" + reportPath + "/report?id=" + reportId);
}

/**
 * saveAndCloseButtons methods
 * 
 * @author J. Riba Cruz - jribacruz@gmail.com
 */
function id(_id) {
	return ('#' + _id.replace(':', '\\:'));
}

function getDefaultBar(componentId) {
	return jQuery(id(componentId + ":default-bar"));
}

function getSuccessBar(componentId) {
	return jQuery(id(componentId + ":success-bar"));
}

function getSavingBar(componentId) {
	return jQuery(id(componentId + ":saving-bar"));
}

function getErrorBar(componentId) {
	return jQuery(id(componentId + ":error-bar"));
}

function startSave(dialog, componentId) {
	getDefaultBar(componentId).hide();
	getSuccessBar(componentId).hide();
	getSavingBar(componentId).show();
	blockDialogInputs(dialog);
}

function blockDialogInputs(dialog) {
	jQuery('#' + dialog + ' .dialog-body input:not(.readonly)').attr(
			'readonly', 'readonly');
	jQuery('#' + dialog + ' .dialog-body textarea:not(.readonly)').attr(
			'readonly', 'readonly');
	jQuery('#' + dialog + ' .dialog-body select:not(.readonly)').attr(
			'readonly', 'readonly');
	jQuery('#' + dialog + ' .dialog-body button:not(.readonly)').attr(
			'disabled', 'disabled');
}

function unBlockDialogInputs(dialog) {
	jQuery('#' + dialog + ' .dialog-body input:not(.readonly)').removeAttr(
			'readonly');
	jQuery('#' + dialog + ' .dialog-body textarea:not(.readonly)').removeAttr(
			'readonly');
	jQuery('#' + dialog + ' .dialog-body select:not(.readonly)').removeAttr(
			'readonly');
	jQuery('#' + dialog + ' .dialog-body button:not(.readonly)').removeAttr(
			'disabled');
}

function showNotificationClass(componentId, qtipStyleClass) {
	mId = id(componentId + ':messages');
	lId = id(componentId + ':message-link');

	jQuery(lId).qtip({
		content : {
			text : jQuery(mId)
		},
		events : {
			show : function(event, api) {
				jQuery(this).qtip('api').set('content.text', jQuery(mId));
				jQuery(mId).show();
			}
		},
		hide : {
			event : 'mouseleave'
		},
		position : {
			my : 'bottom right',
			at : 'top center'
		},
		style : {
			classes : qtipStyleClass + ' ui-tooltip-shadow ui-tooltip-rounded'
		}
	});
	jQuery(lId).qtip('api').show();
}

function showNotification(componentId) {
	getErrorBar(componentId).show();
	showNotificationClass(componentId, 'ui-tooltip-green');
}

function hideNotification(componentId) {
	if (jQuery(id(componentId + ':message-link')).qtip('api') !== null
			&& jQuery(id(componentId + ':message-link')).qtip('api') !== undefined)
		jQuery(id(componentId + ':message-link')).qtip('api').hide();
}

function hideDialog(dialogObj) {
	if (dialogObj !== undefined)
		dialogObj.hide();
}

function completeSave(xhr, status, args, dialog, componentId) {
	if (args.validationFailed || args.appValidationFailed) {

		getDefaultBar(componentId).show();
		getErrorBar(componentId).show();

		unBlockDialogInputs(dialog);
		showNotificationClass(componentId, 'ui-tooltip-red');

		getSuccessBar(componentId).hide();
		getSavingBar(componentId).hide();

	} else {
		getDefaultBar(componentId).hide();
		getErrorBar(componentId).hide();
		getSavingBar(componentId).hide();
		getSuccessBar(componentId).show();
		hideNotification(componentId);
	}
}
