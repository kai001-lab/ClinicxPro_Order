package com.team2.clinic.model;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name = "PatientInfo")
public class OrderPatientBean implements Serializable {
	@Id @Column(name = "pt_id")
	private String patientId;
	@Column(name = "pt_name")
	private String patientName;
	@Column(name = "pt_idcard")
	private String patientIdCard;
	@Column(name = "pt_birthday")
	private Date patientBirthday;
	@Column(name = "pt_gender")
	private int patientGender;
	@Column(name = "pt_phone")
	private String patientPhone;
	@Column(name = "pt_address")
	private String patientAddress;
	@Column(name = "pt_email")
	private String patientEmail;
	
	
	
	public OrderPatientBean(String patientId, String patientName, Date patientBirthday, int patientGender,
			String patientPhone, String patientAddress, String patientEmail) {
		super();
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientBirthday = patientBirthday;
		this.patientGender = patientGender;
		this.patientPhone = patientPhone;
		this.patientAddress = patientAddress;
		this.patientEmail = patientEmail;
	}

	public OrderPatientBean() {

	}
	
}
