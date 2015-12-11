package cn.edu.bjtu.vo;

// Generated 2015-1-31 22:44:34 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Attention generated by hbm2java
 */
@Component
public class Track implements java.io.Serializable {

	private String id;
	private String orderNum;
	private String carNum;
	private Double locLongitude;
	private Double locLatitude;

	public Track() {
	}

	public Track(String id) {
		this.id = id;
	}

	public Track(String id, String orderNum, String carNum,
			Double locLongitude, Double locLatitude) {
		this.id = id;
		this.orderNum = orderNum;
		this.carNum = carNum;
		this.locLongitude = locLongitude;
		this.locLatitude = locLatitude;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNum() {
		return orderNum;
	}


	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}


	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public Double getLocLongitude() {
		return this.locLongitude;
	}

	public void setLocLongitude(Double locLongitude) {
		this.locLongitude = locLongitude;
	}

	public Double getLocLatitude() {
		return this.locLatitude;
	}

	public void setLocLatitude(Double locLatitude) {
		this.locLatitude = locLatitude;
	}

}
