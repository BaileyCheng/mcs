package com.cwbook.mcs.bean.query;

import java.io.Serializable;

/**
 * Title: CartQueryBean<br>
 * Description: <br>
 * Company: Tradevan Co.<br>
 * 
 * @author Bailey Cheng
 * @since 1.0.0 
 */
public class CartQueryBean implements Serializable {
	
	 private static final long serialVersionUID = 1L;
	 
	 private String coupon;
	 private String countryCode;
	 private String shipping_area;
	 private String shipping_option;
	 private String payType_str;
	 private String mail_type;
	 private String receipt_type;
	 private String receipt_invdonate;
	 
	 
}
