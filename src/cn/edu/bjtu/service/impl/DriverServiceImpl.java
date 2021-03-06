package cn.edu.bjtu.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.edu.bjtu.dao.CarDao;
import cn.edu.bjtu.dao.DriverDao;
import cn.edu.bjtu.service.DriverService;
import cn.edu.bjtu.util.Constant;
import cn.edu.bjtu.util.IdCreator;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.util.UploadFile;
import cn.edu.bjtu.vo.Carinfo;
import cn.edu.bjtu.vo.Driverinfo;
import cn.edu.bjtu.vo.Linetransport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
@Transactional
public class DriverServiceImpl implements DriverService{

	@Autowired
	DriverDao driverDao;
	@Autowired
	Driverinfo driverinfo;
	@Autowired
	CarDao carDao;
	
	@Override
	/**
	 * 通过driverid找到司机信息
	 */
	public Driverinfo getDriverInfo(String driverId) {
		
		return driverDao.getDriverInfo(driverId);
	}
	
	@Override
	public Driverinfo getDriverByPhone(String phone) {
		// TODO 自动生成的方法存根
	
		return driverDao.getDriverInfoByPhone(phone);
	}
	
	@Override
	/**
	 * 通过carid找到driverinfo
	 */
	public Driverinfo getDriverByCarId(String carId) {
		
		String driverId = carDao.get(Carinfo.class, carId).getDriverId();

		return driverDao.getDriverInfo(driverId);
	}

	@Override
	/**
	 * 获取所有的司机姓名，更新车辆页面使用
	 */
	public List getAllDriverName(String carrierId) {
		
		return driverDao.getAllDriverName(carrierId);
	}

	@Override
	public List getAllDriver(String carrierId) {
		
		return driverDao.getAllDriver(carrierId);
	}
	
	
	@Override
	// 未实现
	public String getDriverIdByName(String driverName) {
		//FIXME
		return "";
	}

	@Override
	/**
	 * 新增司机
	 */
	public boolean insertNewDriver(Driverinfo driver,HttpServletRequest request,MultipartFile file){
		String carrierId = (String) request.getSession().getAttribute(Constant.USER_ID);
		//保存文件
		String fileLocation=UploadFile.uploadFile(file, carrierId, "driver");
		driver.setId(IdCreator.createDriverId());
		driver.setCarrierId(carrierId);
		driver.setRelDate(new Date());
		//设置文件位置 
		driver.setIdscans(fileLocation);
		driverDao.save(driver);// 保存实体
		return true;
	}

	@Override
	public boolean updateNewDriver(Driverinfo driver,HttpServletRequest request,MultipartFile file){
		String carrierId = (String) request.getSession().getAttribute(Constant.USER_ID);
		//保存文件
		String fileLocation=UploadFile.uploadFile(file, carrierId, "driver");

		Driverinfo driverInstance = driverDao.get(Driverinfo.class,driver.getId());
		driverInstance.setDriverName(driver.getDriverName());
		driverInstance.setSex(driver.getSex());
		driverInstance.setIDCard(driver.getIDCard());
		driverInstance.setLicenceNum(driver.getLicenceNum());
		driverInstance.setLicenceRate(driver.getLicenceRate());
		driverInstance.setLicenceTime(driver.getLicenceTime());
		driverInstance.setPhone(driver.getPhone());
		
		//设置文件位置 
		driverInstance.setIdscans(fileLocation);

		//更新
		driverDao.update(driverInstance);
		return true;
	}
	
	
	private static Date stringToDate(String str) {  
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        Date date = null;  
        try {  
            // Fri Feb 24 00:00:00 CST 2012  
            date = format.parse(str);   
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        // 2012-02-24  
        date = java.sql.Date.valueOf(str);  
                                              
        return date;  
} 
	
	@Override
	/**
	 * 删除司机
	 * @param id
	 * @return
	 */
	public boolean deleteDriver(String id) {
		driverinfo = getDriverInfo(id);// 根据id查找到车辆信息
		driverDao.delete(driverinfo);
		return true;
	}
	/**
	 * 我的信息-司机信息
	 */
	@Override
	public JSONArray getUserDriverResource(HttpSession session,PageUtil pageUtil) {
		String carrierId=(String)session.getAttribute(Constant.USER_ID);
		String hql="from Driverinfo t where t.carrierId=:carrierId order by t.relDate desc ";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("carrierId", carrierId);
		int page=pageUtil.getCurrentPage()==0?1:pageUtil.getCurrentPage();
		int display=pageUtil.getDisplay()==0?10:pageUtil.getDisplay();
		List<Driverinfo> driverList=driverDao.find(hql, params,page,display);
		
		JSONArray jsonArray=new JSONArray();
		for(Driverinfo driver:driverList){
			JSONObject jsonObject=(JSONObject)JSONObject.toJSON(driver);
			jsonArray.add(jsonObject);
		}
		
		return jsonArray;
		
	}

	/**
	 * 我的信息-司机信息-总记录条数
	 */
	@Override
	public Integer getUserDriverResourceTotalRows(HttpSession session) {
		String carrierId=(String)session.getAttribute(Constant.USER_ID);
		String hql="select count(*) from Driverinfo t where t.carrierId=:carrierId";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("carrierId", carrierId);
		
		Long count=driverDao.count(hql, params);
		return count.intValue();
		
	}

	
	
}
