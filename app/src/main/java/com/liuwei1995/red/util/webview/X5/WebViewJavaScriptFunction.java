package com.liuwei1995.red.util.webview.X5;

public interface WebViewJavaScriptFunction {

	void onJsFunctionCalled(String tag);
	/**
	 *  view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
	 + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	 */
}
