package com.binaryalchemist.programondo.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	private long id;
	private String country;
	private String city;
	private String occupation;
	private String birthday;
	private boolean gender;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
}
