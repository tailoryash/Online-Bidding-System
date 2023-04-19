package com.project.bidding.api.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long eventNo;
	private String eventTitle;
	private String description;
	private String category;
	private String imageName;
//	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private LocalTime startTime;
	private long duration;
	@OneToMany(targetEntity = Catalog.class , cascade = CascadeType.ALL)
	@JoinColumn(name="event_id",referencedColumnName = "eventNo")
	private List<Catalog> items;
//	@OneToOne(targetEntity=Seller.class)
//	@JoinColumn(name="seller_id",referencedColumnName="email")
	private String sellerId;
	
	
	
	public Auction() {
		super();
	}



	public Auction(long eventNo, String eventTitle, String description, String category, String imageName,
			LocalDate startDate, LocalTime startTime, long duration, List<Catalog> items, String sellerId) {
		super();
		this.eventNo = eventNo;
		this.eventTitle = eventTitle;
		this.description = description;
		this.category = category;
		this.imageName = imageName;
		this.startDate = startDate;
		this.startTime = startTime;
		this.duration = duration;
		this.items = items;
		this.sellerId = sellerId;
	}



	public long getEventNo() {
		return eventNo;
	}



	public void setEventNo(long eventNo) {
		this.eventNo = eventNo;
	}



	public String getEventTitle() {
		return eventTitle;
	}



	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public String getImageName() {
		return imageName;
	}



	public void setImageName(String imageName) {
		this.imageName = imageName;
	}



	public LocalDate getStartDate() {
		return startDate;
	}



	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}



	public LocalTime getStartTime() {
		return startTime;
	}



	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}



	public long getDuration() {
		return duration;
	}



	public void setDuration(long duration) {
		this.duration = duration;
	}



	public List<Catalog> getItems() {
		return items;
	}



	public void setItems(List<Catalog> items) {
		this.items = items;
	}



	public String getSellerId() {
		return sellerId;
	}



	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	
	


	

	
	 

}
