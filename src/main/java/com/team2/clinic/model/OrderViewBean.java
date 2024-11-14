package com.team2.clinic.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity @Table(name="OrderContent") @Component
@Getter @Setter
public class OrderViewBean {

//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id  @Column(name="ordernum")
	private String orderNumber;

	@Column(name = "orderdate") @Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date orderDate;

	@Column(name = "empname")
    private String doctorName;

	//金流 ============================================
	@Column(name = "pay_status")
	private int payStatus;

	@Column(name = "paytool")
    private String payTool;

	@Column(name = "paydate")  @Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date payDate;

	//患者 ============================================
	@ManyToOne
	@JoinColumn(name = "pt_id")
    private OrderPatientBean patient;

	//療程 ============================================
	@ManyToOne
	@JoinColumn(name = "treatment_id")
    private OrderTreatmentBean treatment;

	@Column(name = "th_status")
    private int threapyStatus;

	public OrderViewBean() {
	}

	//Constructor
	public OrderViewBean(String doctorName, Date orderDate, String orderNumber, OrderPatientBean patient, Date payDate, int payStatus, String payTool, int threapyStatus, OrderTreatmentBean treatmentItem) {
		this.doctorName = doctorName;
		this.orderDate = orderDate;
		this.orderNumber = orderNumber;
		this.patient = patient;
		this.payDate = payDate;
		this.payStatus = payStatus;
		this.payTool = payTool;
		this.threapyStatus = threapyStatus;
		this.treatment = treatmentItem;
	}
}
