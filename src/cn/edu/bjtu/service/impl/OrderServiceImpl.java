package cn.edu.bjtu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.bjtu.bean.page.OrderBean;
import cn.edu.bjtu.dao.AddressDao;
import cn.edu.bjtu.dao.CompanyDao;
import cn.edu.bjtu.dao.OrderDao;
import cn.edu.bjtu.service.OrderService;
import cn.edu.bjtu.util.Constant;
import cn.edu.bjtu.util.IdCreator;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.vo.Address;
import cn.edu.bjtu.vo.Carrierinfo;
import cn.edu.bjtu.vo.OrderCarrierView;
import cn.edu.bjtu.vo.Orderform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author RussWest0
 *
 */
@Transactional
@Repository
public class OrderServiceImpl implements OrderService {

	@Resource
	OrderDao orderDao;
	@Resource
	Orderform orderform;
	@Autowired
	CompanyDao companyDao;
	@Autowired
	AddressDao addressDao;

	@Override
	public OrderCarrierView getSendOrderDetail(String id) {
		
		return orderDao.getSendOrderDetail(id);
	}

	@Override
	public Orderform getRecieveOrderDetail(String id) {
		
		return orderDao.getRecieveOrderDetail(id);
	}


	@Override
	public Orderform getOrderByOrderNum(String orderNum) {
		return orderDao.getOrderByOrderNum(orderNum);
	}

	@Override
	public OrderCarrierView getOrderByOrderId(String orderId) {
		
		return orderDao.getOrderByOrderId(orderId);
	}

	@Override
	public boolean acceptOrder(String orderId) {
		
		return orderDao.acceptOrder(orderId);
	}

	@Override
	/**
	 * ����Ԥ���˷�
	 */
	public float getExpectedMoney(String orderId) {
		
		return orderDao.getExpectedMoney(orderId);
	}

	/**
	 * ǩ���ϴ�����
	 */
	@Override
	public boolean signBill(String orderId, float actualPrice,
			String explainReason,String fileLocation) {
		Orderform order = orderDao.get(Orderform.class, orderId);
		order.setState("��ȷ��");//�޸� ����״̬
		order.setActualPrice(actualPrice);
		order.setExplainReason(explainReason);
		order.setAcceptPicture(fileLocation);
		
		orderDao.update(order);
		return true;
		
	}

	@Override
	public Orderform getOrderInfo(String orderId) {
		
		return orderDao.getOrderInfo(orderId);
	}

	@Override
	public boolean confirmCargo(String orderId) {
		
		return orderDao.confirmCargo(orderId);
	}

	@Override
	public boolean cancel(String cancelReason, String orderId) {
		
		return orderDao.cancel(cancelReason, orderId);
	}

	

	/**
	 * ���˷�ǩ���ϴ���ĸ���
	 */
	@Override
	public boolean updateSignBill(String orderId,
			float actualPrice, String explainReason,String fileLocation) {
		
		Orderform order=orderDao.get(Orderform.class, orderId);
		order.setActualPrice(actualPrice);
		order.setExplainReason(explainReason);
		order.setAcceptPicture(fileLocation);
		
		orderDao.update(order);
		
		return true;
		//return orderDao.signBill(orderId, actualPrice, explainReason,path,fileName);
	}

	/**
	 * ���¶���
	 */
	@Override
	public boolean updateOrder(HttpSession session, OrderBean orderBean) {
		String userId=(String)session.getAttribute(Constant.USER_ID);
		Orderform orderInstance=orderDao.get(Orderform.class,orderBean.getId());

		orderInstance.setClientName(orderBean.getClientName());
		orderInstance.setIsLinkToClientWayBill(orderBean.getIsLinkToClientWayBill());
		orderInstance.setClientWayBillNum(orderBean.getClientWayBillNum());
		orderInstance.setContractId(orderBean.getContractId());
		orderInstance.setHasCarrierContract(orderBean.getHasCarrierContract());
		
		orderInstance.setGoodsName(orderBean.getGoodsName());
		orderInstance.setGoodsVolume(orderBean.getGoodsVolume());
		orderInstance.setGoodsWeight(orderBean.getGoodsWeight());
		orderInstance.setDeclaredPrice(orderBean.getDeclaredPrice());
		orderInstance.setInsurance(orderBean.getInsurance());
		orderInstance.setExpectedPrice(orderBean.getExpectedPrice());
		
		orderInstance.setDeliveryAddr(orderBean.getDeliveryAddr());
		orderInstance.setDeliveryName(orderBean.getDeliveryName());
		orderInstance.setDeliveryPhone(orderBean.getDeliveryPhone());
		orderInstance.setRecieverAddr(orderBean.getRecieverAddr());
		orderInstance.setRecieverName(orderBean.getRecieverName());
		orderInstance.setRecieverPhone(orderBean.getRecieverPhone());
		
		orderInstance.setRemarks(orderBean.getRemarks());
		
		orderDao.update(orderInstance);
		
		//�����Ҫ�����泣���շ�����ַ
		saveAddress(orderBean, userId);
		
		return true;
		
	}

	
	/**
	 * �½�����
	 */
	@Override
	public boolean createOrder(HttpSession session, OrderBean orderBean) {

		String userId=(String)session.getAttribute(Constant.USER_ID);
		Orderform orderInstance=new Orderform();
		BeanUtils.copyProperties(orderBean, orderInstance);
		orderInstance.setId(IdCreator.createOrderId());
		orderInstance.setOrderNum(IdCreator.createOrderNum());
		orderInstance.setState("������");
		orderInstance.setSubmitTime(new Date());
		orderInstance.setClientId(userId);
		orderInstance.setSettlementState("δ����");
		
		orderDao.save(orderInstance);
		
		saveAddress(orderBean, userId);
				
		return true;
	}
	
	//���ѡ���˱��泣�õ�ַ������г��õ�ַ����
    //��������Ϣ 
	private void saveAddress(OrderBean orderBean, String userId) {
		
		if("on".equals(orderBean.getSender_info())){
			Address address=new Address();
			address.setName(orderBean.getDeliveryName());
			address.setPhone(orderBean.getDeliveryPhone());
			address.setAddress(orderBean.getDeliveryAddr());
			address.setId(IdCreator.createAddressId());
			address.setRelDate(new Date());
			address.setClientId(userId);
			address.setKind(1);
			addressDao.save(address);
			
		}
		//�ջ�����Ϣ
		if("on".equals(orderBean.getReciever_info())){
			Address address=new Address();
			address.setName(orderBean.getRecieverName());
			address.setPhone(orderBean.getRecieverPhone());
			address.setAddress(orderBean.getRecieverAddr());
			address.setId(IdCreator.createAddressId());
			address.setRelDate(new Date());
			address.setClientId(userId);
			address.setKind(2);
			addressDao.save(address);
		}
	}

	/**
	 * �����û�������������
	 */
	@Override
	public Long getUserWaitToAcceptNum(HttpSession session) {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql="select count(*) from Orderform t "+whereSql(session,params)+" and t.state='������'";
		Long count= orderDao.count(hql, params);
		return count==null?0L:count;
		
	}

	/**
	 * �����û������ն�����
	 */
	@Override
	public Long getUserWaitToReceiveNum(HttpSession session) {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql="select count(*) from Orderform t "+whereSql(session,params)+" and t.state='���ջ�'";
		Long count =orderDao.count(hql, params);
		return count==null?0L:count;
	}

	/**
	 * �����û������㶩����
	 */
	@Override
	public Long getUserWaitToSettleNum(HttpSession session) {
		/*String hql="from Orderform t where t.state=''";
		Map<String,Object> params=new HashMap<String,Object>();
		return orderDao.count(hql+whereSql(session,params), params);*/
		return 0L;
	}

	/**
	 * �����û�����ɶ�����
	 */
	@Override
	public Long finishedNum(HttpSession session) {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql="select count(*) from Orderform t "+whereSql(session,params)+" and  t.state='�����'";
		Long count =orderDao.count(hql, params);
		return count==null?0L:count;
	}
	
	/**
	 * where sql 
	 * @param session
	 * @param params
	 * @return
	 */
	private String whereSql(HttpSession session,Map<String,Object> params){
		String userId=(String)session.getAttribute(Constant.USER_ID);
		Integer userKind=(Integer)session.getAttribute(Constant.USER_KIND);
		String wheresql=" where 1=1 ";
		if(userKind==2){//�����û�
			wheresql+=" and t.clientId=:clientId ";
			params.put("clientId", userId);
		}else if(userKind==3){//��ҵ�û�
			wheresql+=" and t.carrierId=:carrierId ";
			params.put("carrierId", userId);
		}
		
		return wheresql;
		
	}

	/*
	 * ���ύ�Ķ����ܼ�¼��(non-Javadoc)
	 * @see cn.edu.bjtu.service.OrderService#getUserSendOrderTotalRows(javax.servlet.http.HttpSession)
	 */
	@Override
	public Integer getUserSendOrderTotalRows(HttpSession session,Orderform order) {
		Map<String,Object> params=new HashMap<String,Object>();
		String userId=(String)session.getAttribute(Constant.USER_ID);
		String hql="select count(*) from Orderform t "+whereHql(order,params);
		hql+=" and t.clientId=:clientId";
		params.put("clientId", userId);
		
		Long count=orderDao.count(hql, params);
		
		return count.intValue();
	}

	/**
	 * ���ύ�Ķ���
	 */
	@Override
	public JSONArray getUserSendOrder(HttpSession session,PageUtil pageUtil,Orderform order) {
		
		Map<String,Object> params=new HashMap<String,Object>();
		String userId=(String)session.getAttribute(Constant.USER_ID);
		String hql="from Orderform t "+whereHql(order,params);
		hql+=" and t.clientId=:clientId order by t.submitTime desc";
		params.put("clientId", userId);
		
		List<Orderform> orderList=orderDao.find(hql,params);
		List<OrderBean> beanList=new ArrayList<OrderBean>();
		for(Orderform orderIns:orderList){
			OrderBean bean=new OrderBean();
			BeanUtils.copyProperties(orderIns, bean);
			Carrierinfo company=companyDao.get(Carrierinfo.class, orderIns.getCarrierId());
			
			bean.setCompanyName(company.getCompanyName());
			beanList.add(bean);
		}
		
		JSONArray jsonArray=new JSONArray();
		for(OrderBean orderBean:beanList){
			JSONObject jsonObject=(JSONObject)JSONObject.toJSON(orderBean);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	/**
	 * where hql ������������
	 * @param order
	 * @param params
	 * @return
	 */
	private String whereHql(Orderform order,Map<String,Object> params){
		String hql=" where 1=1 ";
		if(order!= null){
			if(!"".equals(order.getOrderNum()) && order.getOrderNum() !=null){
				hql+=" and t.orderNum like '%"+order.getOrderNum()+"%'";
			}
		}
		return hql;
	}
	
	/**
	 * ���յ��Ķ���
	 */
	@Override
	public JSONArray getUserRecieveOrder(HttpSession session,PageUtil pageUtil,Orderform order) {
		Map<String,Object> params=new HashMap<String,Object>();
		String userId=(String)session.getAttribute(Constant.USER_ID);
		String hql="from Orderform t "+whereHql(order,params);
		hql+=" and t.carrierId=:carrierId order by t.submitTime desc";
		params.put("carrierId", userId);
		
		List<Orderform> orderList=orderDao.find(hql,params);
		List<OrderBean> beanList=new ArrayList<OrderBean>();
		for(Orderform orderIns:orderList){
			OrderBean bean=new OrderBean();
			BeanUtils.copyProperties(orderIns, bean);
			Carrierinfo company=companyDao.get(Carrierinfo.class, orderIns.getCarrierId());
			
			bean.setCompanyName(company.getCompanyName());
			beanList.add(bean);
		}
		
		JSONArray jsonArray=new JSONArray();
		for(OrderBean orderBean:beanList){
			JSONObject jsonObject=(JSONObject)JSONObject.toJSON(orderBean);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	/**
	 * ���յ��Ķ���-�ܼ�¼��
	 */
	@Override
	public Integer getUserRecieveOrderTotalRows(HttpSession session,Orderform order) {
		Map<String,Object> params=new HashMap<String,Object>();
		String userId=(String)session.getAttribute(Constant.USER_ID);
		String hql="select count(*) from Orderform t "+whereHql(order,params);
		hql+=" and t.carrierId=:carrierId";
		params.put("carrierId", userId);
		
		Long count=orderDao.count(hql, params);
		
		return count.intValue();
	}
	
	
	
	

}