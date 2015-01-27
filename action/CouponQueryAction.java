package com.cwbook.mcs.action;

import java.util.List;

import com.cwbook.commons.dao.ApParamDao;
import com.cwbook.mcs.ApContext;
import com.cwbook.mcs.ApDataPage;
import com.cwbook.mcs.PageAction;
import com.cwbook.mcs.dao.CouponQueryDao;
import com.tradevan.taurus.xdao.XdaoException;


/**
 * Title: KnowledgeQuery<br>
 * Description: 購物折價券查詢<br>
 * Company: Tradevan Co.<br>
 * 
 * @author Bailey Cheng
 * @since 1.0.0
 */
public class CouponQueryAction extends PageAction {
	
	private static final String QUERY_COUPON_LIST = "queryCouponList";
	private static final String COUPON_DETAIL_AJAX = "couponDetail";
	
	protected ApDataPage exDataPage = null;
	protected int exCurrentPage = 1;
	protected List exDataList = null;
	
	private List couponDetailList;
	private String clickedCouponAct_SerNo;
	private String clickedBeginDate;
	private String moreMemFaqCatNo;
	/**
	 * 進入購物折價卷查詢畫面
	 */
	public String execute() throws XdaoException {
		String uid = ApContext.getUserId();
		//String uid = "test";
	    //取得折價券清單
		dataPage = CouponQueryDao.findMemberCouponDataPage(uid);
        // 設定目前頁數
        dataPage.setCurrentPage(currentPage);

        list = dataPage.getDataList().toList();
        //couponDetailList = CouponQueryDao.findMemberCouponDetails("1").toList();
	    //取得已失效折價券清單
        exDataPage = CouponQueryDao.findExMemberCouponDataPage(uid);
        
        // 設定目前頁數
        exDataPage.setCurrentPage(exCurrentPage);

        exDataList = exDataPage.getDataList().toList();
        
		moreMemFaqCatNo = ApContext.getParam("MoreCouponFAQ");
	   // 顯示queryCouponList.jsp呈現清單資料
	    return QUERY_COUPON_LIST;
	}
	

	/**
	 * 	點選展開明細
	 *	註：(可以用jQuery.load(…)的方式)
	 * @return
	 * @throws XdaoException
	 */
	public String couponDetail() throws XdaoException {
		String uid = ApContext.getUserId();
	    //取得折價券明細清單
		couponDetailList = CouponQueryDao.findMemberCouponDetails(clickedCouponAct_SerNo, uid, clickedBeginDate).toList();
	    //返回bonusDetail.jsp呈現折價券明細資料
	    return COUPON_DETAIL_AJAX;
	}


	/**
	 * @return the clickedCouponAct_SerNo
	 */
	public String getClickedCouponAct_SerNo() {
		return clickedCouponAct_SerNo;
	}


	/**
	 * set clickedCouponAct_SerNo	
	 * @param clickedCouponAct_SerNo - String
	 */
	public void setClickedCouponAct_SerNo(String clickedCouponAct_SerNo) {
		this.clickedCouponAct_SerNo = clickedCouponAct_SerNo;
	}

	
	public String getClickedBeginDate() {
		return clickedBeginDate;
	}
	public void setClickedBeginDate(String clickedBeginDate) {
		this.clickedBeginDate = clickedBeginDate;
	}


	/**
	 * @return the couponDetailList
	 */
	public List getCouponDetailList() {
		return couponDetailList;
	}


	/**
	 * set couponDetailList	
	 * @param couponDetailList - List
	 */
	public void setCouponDetailList(List couponDetailList) {
		this.couponDetailList = couponDetailList;
	}    

    /**
     * 取得分頁物件
     * @return
     */
    public ApDataPage getExDataPage() {
        return this.exDataPage;
    }
        
    /**
     * 取得分頁資料
     * @return
     * @throws Exception
     */
    public List getExDataList() throws Exception {
        if (exDataPage != null) {
            return exDataList;
        }
        return null;
    }
	
    
    /**
     * @return the exCurrentPage
     */
    public int getExCurrentPage() {
        return exCurrentPage;
    }

    /**
     * @param exCurrentPage the exCurrentPage to set
     */
    public void setExCurrentPage(int exCurrentPage) {
        this.exCurrentPage = exCurrentPage;
    }
    
	public String getMoreMemFaqCatNo() {
		return moreMemFaqCatNo;
	}

	public void setMoreMemFaqCatNo(String moreMemFaqCatNo) {
		this.moreMemFaqCatNo = moreMemFaqCatNo;
	}
}
