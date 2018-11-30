package com.chailease.tw.app.android.data;


import com.chailease.tw.app.android.data.api.LoginData;

import org.apache.commons.lang3.StringUtils;

public class UserProfile {

	private String userId;
	private String userName;
	private int deptId;
	private String deptCd;
	private String deptName;
	private int deptLvl;
	private boolean isHD;
	private int deptCompId;
	private String deptCompName;
	private String email;
	private String verifyCode;
	private int workType = 5;

	public UserProfile(LoginData.USR_PRFL usrPrfl) {
		userId = usrPrfl.USR_ID;
		userName = usrPrfl.USR_NME;
		email = usrPrfl.USR_EMAIL;
		deptCompId = Integer.parseInt(usrPrfl.DEPT_COMP_ID);
		deptCompName = usrPrfl.COMP_SHRT_NME;
		deptLvl = Integer.parseInt(usrPrfl.DEPT_LVL);
		deptId = Integer.parseInt(usrPrfl.DEPT_ID);
		deptCd = usrPrfl.DEPT_CD;
		deptName = usrPrfl.DEPT_NME;
		isHD = userId.equals(usrPrfl.DEPT_HD_USR_ID);
		if (!StringUtils.isBlank(usrPrfl.WORKTYP_ID)) {
			workType = Integer.parseInt(usrPrfl.WORKTYP_ID);
		}
	}
	public UserProfile(){
		super();
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public int getDeptId() {
		return deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public int getDeptLvl() {
		return deptLvl;
	}

	public boolean isHD() {
		return isHD;
	}

	public int getDeptCompId() {
		return deptCompId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public void setDeptLvl(int deptLvl) {
		this.deptLvl = deptLvl;
	}

	public void setHD(boolean HD) {
		isHD = HD;
	}

	public void setDeptCompId(int deptCompId) {
		this.deptCompId = deptCompId;
	}

	public String getDeptCd() {
		return deptCd;
	}
	public void setDeptCd(String deptCd) {
		this.deptCd = deptCd;
	}
	public String getDeptCompName() {
		return deptCompName;
	}
	public void setDeptCompName(String deptCompName) {
		this.deptCompName = deptCompName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public String getVerifyCode() {
		return verifyCode;
	}

	public int getWorkType() {
		return workType;
	}

	public void setWorkType(int workType) {
		this.workType = workType;
	}

	public UserProfile clone() {
		UserProfile clone = new UserProfile();
		clone.userId = this.userId;
		clone.userName = this.userName;
		clone.deptId = this.deptId;
		clone.deptCd = this.deptCd;
		clone.deptName = this.deptName;
		clone.deptLvl = this.deptLvl;
		clone.isHD = this.isHD;
		clone.deptCompId = this.deptCompId;
		clone.deptCompName = this.deptCompName;
		clone.email = this.email;
		clone.verifyCode = this.verifyCode;
		clone.workType = this.workType;
		return clone;
	}

}