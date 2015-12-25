package cn.edu.bjtu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.bjtu.bean.page.FinancialBean;
import cn.edu.bjtu.dao.OrderDao;
import cn.edu.bjtu.service.FinancialService;
import cn.edu.bjtu.util.Constant;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.vo.Orderform;
@Service
@Transactional
public class FinancialServiceImpl implements FinancialService{

	@Autowired
	OrderDao orderDao;
	/**
	 * 获取账户财务分析数据
	 */
	@Override
	public List<FinancialBean> getAccountFinancialInfo(FinancialBean financialBean,PageUtil pageUtil,HttpSession session) {
		String userId=(String)session.getAttribute(Constant.USER_ID);
		//按天计算 
		String sql="select * from (select date(submitTime) as date,"
				+ "sum(actualPrice) as transportFee,"
				+ "sum(insurance) as totalInsurance"
				+ " from orderform where carrierId=:userId "
				+ " group by date(submitTime) order by date(submitTime) desc ) as t";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", userId);
		List<Object[]> objList=orderDao.findBySql(sql, params);
		List<FinancialBean> beanList=new ArrayList<FinancialBean>();
		for(Object[] obj:objList){
			FinancialBean bean=new FinancialBean();
			bean.setDate((Date)obj[0]);
			bean.setTransportFee((Double)obj[1]==null?0.0:(Double)obj[1]);
			bean.setTotalInsurance((Double)obj[2]==null?0.0:(Double)obj[2]);
			bean.setTotalFee(bean.getTotalInsurance()+bean.getTransportFee());
			beanList.add(bean);
		}
		return beanList;
	}
	/**
	 * 财务指标总记录数
	 */
	@Override
	public Long getAccountFinancialInfoTotalRows(FinancialBean financialBean,PageUtil pageUtil,HttpSession session) {
		String userId=(String)session.getAttribute(Constant.USER_ID);
		//按天计算
		/**不使用这么多层的嵌套查询，报
		 * [DaTian]org.hibernate.util.JDBCExceptionReporter WARN   - SQL Error: 0, SQLState: S0022
		   [DaTian]org.hibernate.util.JDBCExceptionReporter ERROR  - Column '' not found.
		 * 的错误
		 */
		String sql="select * from (select count(*) from (select date(submitTime) as date,"
				+ "sum(actualPrice) as transportFee,"
				+ "sum(insurance) as totalInsurance"
				+ " from orderform where carrierId=:userId "
				+ " group by date(submitTime) order by date(submitTime) desc ) as t) as r";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", userId);
		Long count=orderDao.countBySql(sql, params);
		return count;
	}
	/**
	 * 查看某一天 所有订单
	 */
	@Override
	public List<Orderform> viewFinancialDetails(HttpSession session,FinancialBean financialBean) {
		String userId=(String)session.getAttribute(Constant.USER_ID);
		String hql="from Orderform t "
				+ "where t.carrierId=:carrierId "
				+ "and date(t.submitTime)=:submitTime "
				+ "order by t.submitTime desc";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("carrierId", userId);
		params.put("submitTime", financialBean.getDate());
		List<Orderform> list=orderDao.find(hql, params);
		
		return list;
		
	}
	
	
	

}
