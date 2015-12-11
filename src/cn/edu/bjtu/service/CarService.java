package cn.edu.bjtu.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import cn.edu.bjtu.bean.search.CarSearchBean;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.vo.Carinfo;
import cn.edu.bjtu.vo.Carteam;
import cn.edu.bjtu.vo.Linetransport;

import com.alibaba.fastjson.JSONArray;

public interface CarService {
	
	public Carinfo getCarInfo(String carid);

	public boolean insertNewCar(Carinfo car,HttpServletRequest request);
	public boolean updateNewCar(Carinfo car,HttpServletRequest request);
	
	public boolean deleteCar(String id);
	
	/**
	 * ��ȡ��Դ��ɸѡcar
	 * @param carbean
	 * @param pageUtil
	 * @param session
	 * @return
	 */
	public JSONArray getSelectedCarNew(CarSearchBean carbean,PageUtil pageUtil,HttpSession session);
	
	/**
	 * ��ȡ��Դ��-����ɸѡ��¼������
	 * @param carBean
	 * @return
	 */
	public Integer getSelectedCarTotalRows(CarSearchBean carBean);
	
	/**
	 * ��ȡ��˾���������б�
	 * @Title: getCompanyCarteamList 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: JSONArray 
	 * @throws: �쳣 
	 * @author: chendonghao 
	 * @date: 2015��6��29�� ����5:35:42
	 */
	public List<Carteam> getCompanyCarteamList(HttpSession session);
	
	/**
	 * �ҵ���Ϣ-������Ϣ-�ܼ�¼����
	 * @Title: getUserCarResourceTotalRows 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: Integer 
	 * @throws: �쳣 
	 * @author: chendonghao 
	 * @date: 2015��7��3�� ����11:12:59
	 */
	public Integer getUserCarResourceTotalRows(HttpSession session);
	
	/**
	 * �ҵ���Ϣ-������Ϣ
	 * @Title: getUserCarResource 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: JSONArray 
	 * @throws: �쳣 
	 * @author: chendonghao 
	 * @date: 2015��7��3�� ����11:13:42
	 */
	public JSONArray getUserCarResource(HttpSession session,PageUtil pageUtil);
	
	/**
	 * ��ȡ��˾�������� 
	 * @param carrierId
	 * @return
	 */
	public String getCompanyCarAjax(String carrierId);
	
}