package com.cwbook.mcs.dao;

import com.cwbook.commons.DefaultModel;
import com.cwbook.mcs.ApDataPage;
import com.tradevan.commons.collection.DataList;
import com.tradevan.taurus.xdao.QueryParameter;
import com.tradevan.taurus.xdao.SqlWhere;
import com.tradevan.taurus.xdao.XdaoException;
import com.tradevan.taurus.xdao.XdaoSession;
import com.tradevan.taurus.xdao.util.XdaoUtil;


/**
 * Title: CouponQueryDao<br>
 * Description: <br>
 * Company: Tradevan Co.<br>
 * 
 * @author Bailey Cheng
 * @since 1.0.0
 */
public class CouponQueryDao implements TableConst {
	private static DefaultModel model = new DefaultModel("");
	
	/**
	 * 購物折價卷查詢
	 * @param userId
	 * @return
	 * @throws XdaoException
	 */
	public static ApDataPage findMemberCouponDataPage(String userId) throws XdaoException{
		XdaoSession session = model.getXdaoSession();
		QueryParameter param = session.getQueryParameter();
		StringBuffer sbTmp = new StringBuffer();
		sbTmp.append("ROW_NUMBER() OVER (ORDER BY subsql.BEGINDATE DESC, subsql.COUPONACT_SERNO) AS rownum,  subsql.COUPONACT_SERNO, subsql.ACTNAME,  subsql.SUMAMT, subsql.BEGINDATE, subsql.ENDDATE ");
		param.select(sbTmp.toString());
		
		sbTmp.delete(0, sbTmp.length());
		sbTmp.append("(SELECT c.COUPONACT_SERNO, " +
				" a.ACTNAME, " +
				" a.DISTTYPE, " +
				" SUM(AMOUNT) SUMAMT, " +
				" c.BEGINDATE, " +
				" CASE a.DISTTYPE WHEN '4' THEN c.ENDDATE ELSE a.ENDDATE END ENDDATE " +
				" FROM COUPON c " +
				" INNER JOIN " +
				" COUPONACT a " +
				" ON c.COUPONACT_SERNO = a.SERNO " +
				" WHERE c.USERID = '"+XdaoUtil.escape(userId) +"' " +
				" AND EXISTS (SELECT ci.USESTATUS FROM COUPON ci WHERE ci.COUPONACT_SERNO = a.SERNO AND ci.USESTATUS = 'N') " +
				" GROUP BY c.COUPONACT_SERNO, a.ACTNAME, a.DISTTYPE, c.BEGINDATE, a.DISTTYPE, c.ENDDATE, a.ENDDATE " +
				" ) subsql " +
				"  WHERE GETDATE() < CASE subsql.DISTTYPE WHEN '4' THEN subsql.ENDDATE+1 ELSE subsql.ENDDATE+1 END ");
		
		param.from(sbTmp.toString());
		param.setKeepTypeField("BEGINDATE, ENDDATE");
        ApDataPage dataPage = new ApDataPage(session, param, CouponQueryDao.class, "findMemberCouponDataPage");
        dataPage.setPageSize(10);
        return dataPage;
	}
	
	/**
	 * 折價卷明細
	 * @param couponActSerNo
	 * @return
	 * @throws XdaoException
	 */
	public static DataList findMemberCouponDetails(String couponActSerNo, String userId, String beginDate) throws XdaoException{
		XdaoSession session = model.getXdaoSession();
		try {
			DefaultModel.setModule(session, CouponQueryDao.class,
					"findMemberCouponDetails");
			String sql ="SELECT c.COUPONID, "                  //-- 折價卷代碼
					    +"c.AMOUNT, "
						+"CASE WHEN USETIME IS NOT NULL THEN '已使用' "
						+"ELSE '未使用' "
						+"END NOWSTATUS, "
						+"c.ORDERNO "      
						+"FROM COUPON c "
						+"INNER JOIN "
						+"COUPONACT a "
						+"ON c.COUPONACT_SERNO = a.SERNO "
						+"WHERE c.COUPONACT_SERNO = '"+XdaoUtil.escape(couponActSerNo)+"' "
						+"AND c.USERID = '"+XdaoUtil.escape(userId)+"' "
						+"AND c.BEGINDATE = CAST('"+XdaoUtil.escape(beginDate)+"' AS DATETIME) ";
			Object[] values = { couponActSerNo};
		    QueryParameter param = session.getQueryParameter();
			param.setMaxRow(-1);
			DataList list = session.executeQuery(sql,param);
			return list;
		} finally {
			DefaultModel.resetModule(session);
		}
	}
	
	/**
	 * 已失效購物折價卷查詢
	 * @param userId
	 * @return
	 * @throws XdaoException
	 */
	public static ApDataPage findExMemberCouponDataPage(String userId) throws XdaoException{
		XdaoSession session = model.getXdaoSession();
		QueryParameter param = session.getQueryParameter();
		StringBuffer sbTmp = new StringBuffer();
		
		sbTmp.append("ROW_NUMBER() OVER (ORDER BY subsql.BEGINDATE DESC, subsql.COUPONACT_SERNO) AS rownum, subsql.COUPONACT_SERNO, subsql.ACTNAME, subsql.SUMAMT, subsql.BEGINDATE, subsql.ENDDATE ");
		param.select(sbTmp.toString());
		
		sbTmp.delete(0, sbTmp.length());
		sbTmp.append("(SELECT c.COUPONACT_SERNO, "
					+"a.ACTNAME, "
					+"a.DISTTYPE, "
					+"SUM(c.AMOUNT) SUMAMT, "
					+"c.BEGINDATE, "
					+"CASE a.DISTTYPE WHEN '4' THEN c.ENDDATE ELSE a.ENDDATE END ENDDATE "
					+"FROM COUPON c "
					+"INNER JOIN "
					+"COUPONACT a "
					+"ON c.COUPONACT_SERNO = a.SERNO "
					+"WHERE c.USERID = '"+XdaoUtil.escape(userId)+"' "
					+"AND GETDATE() > CASE a.DISTTYPE WHEN '4' THEN c.ENDDATE+1 ELSE a.ENDDATE+1 END "
					+"GROUP BY c.COUPONACT_SERNO, a.ACTNAME, a.DISTTYPE, c.BEGINDATE, a.DISTTYPE, c.ENDDATE, a.ENDDATE "
					+"UNION ALL "
					+"SELECT c.COUPONACT_SERNO, "
					+"a.ACTNAME, "
					+"a.DISTTYPE, "
					+"SUM(c.AMOUNT) SUMAMT, "
					+"c.BEGINDATE, "
					+"CASE a.DISTTYPE WHEN '4' THEN c.ENDDATE ELSE a.ENDDATE END ENDDATE "
					+"FROM COUPON c "
					+"INNER JOIN "
					+"COUPONACT a "
					+"ON c.COUPONACT_SERNO = a.SERNO "
					+"WHERE c.USERID = '"+XdaoUtil.escape(userId)+"' "
					+"AND NOT EXISTS (SELECT ci.USESTATUS FROM COUPON ci WHERE ci.COUPONACT_SERNO = a.SERNO AND ci.USESTATUS = 'N') "
					+"AND GETDATE() < CASE a.DISTTYPE WHEN '4' THEN c.ENDDATE+1 ELSE a.ENDDATE+1 END "
					+"GROUP BY c.COUPONACT_SERNO, a.ACTNAME, a.DISTTYPE, c.BEGINDATE, a.DISTTYPE, c.ENDDATE, a.ENDDATE "
        			+") subsql ");
		param.from(sbTmp.toString());
		param.setKeepTypeField("BEGINDATE, ENDDATE");
        ApDataPage dataPage = new ApDataPage(session, param, CouponQueryDao.class, "findExMemberCouponDataPage");
        dataPage.setPageSize(10);
        return dataPage;
	}
}
